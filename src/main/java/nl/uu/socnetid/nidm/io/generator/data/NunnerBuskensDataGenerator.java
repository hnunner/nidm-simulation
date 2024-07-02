/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.io.generator.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.EpidemicStructures;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensAgentDetailsWriterReduced;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensSimulationSummaryWriterReduced;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class NunnerBuskensDataGenerator extends AbstractDataGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensDataGenerator.class);

    // network
    private Network network;
    private Agent indexCase;

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<NunnerBuskensParameters> dgData;
    private NunnerBuskensSimulationSummaryWriterReduced ssWriter;
    private NunnerBuskensRoundSummaryWriter rsWriter;
    private NunnerBuskensAgentDetailsWriterReduced adWriter;
    private GEXFWriter gexfWriter;


    /**
     * Constructor.
     *
     * @param rootExportPath
     *          the root export path
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public NunnerBuskensDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<NunnerBuskensParameters>(PropertiesHandler.getInstance().getNunnerBuskensParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new NunnerBuskensSimulationSummaryWriterReduced(getExportPath() + "simulation-summary.csv",
                    this.dgData);
        }
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter = new NunnerBuskensRoundSummaryWriter(getExportPath() + "round-summary.csv", this.dgData);
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.adWriter = new NunnerBuskensAgentDetailsWriterReduced(getExportPath() + "agent-details.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        double[] b1s = this.dgData.getUtilityModelParams().isB1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB1s();
        double[] b2s = this.dgData.getUtilityModelParams().isB2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB2s();
        double[] alphas = this.dgData.getUtilityModelParams().isAlphaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getAlphas();
        double[] c1s = this.dgData.getUtilityModelParams().isC1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC1s();
        double[] c2s = this.dgData.getUtilityModelParams().isC2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC2s();
        double[] d1s = this.dgData.getUtilityModelParams().isD1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getD1s();
        double[] d2s = this.dgData.getUtilityModelParams().isD2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getD2s();
        double[] sigmas = this.dgData.getUtilityModelParams().isSigmaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getSigmas();
        double[] gammas = this.dgData.getUtilityModelParams().isGammaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getGammas();
        int[] taus = this.dgData.getUtilityModelParams().isTauRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getTaus();

        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();
        double[] psis = this.dgData.getUtilityModelParams().isPsiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPsis();
        double[] xis = this.dgData.getUtilityModelParams().isXiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getXis();
        double[] omegas = this.dgData.getUtilityModelParams().isOmegaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getOmegas();
        boolean[] selectives = this.dgData.getUtilityModelParams().isSelectiveRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getSelectives();

        // unique parameter combinations
        this.dgData.getSimStats().setUpcs(
                b1s.length *
                b2s.length *
                alphas.length *
                c1s.length *
                c2s.length *
                d1s.length *
                d2s.length *
                sigmas.length *
                gammas.length *
                taus.length *
                Ns.length *
                iotas.length *
                phis.length *
                psis.length *
                xis.length *
                omegas.length *
                selectives.length);

        // loop over all possible parameter combinations
        for (double b1 : b1s) {
            this.dgData.getUtilityModelParams().setCurrB1(b1);
            for (double b2 : b2s) {
                this.dgData.getUtilityModelParams().setCurrB2(b2);
                for (double alpha : alphas) {
                	this.dgData.getUtilityModelParams().setCurrAlpha(alpha);
                	for (double c1 : c1s) {
                		this.dgData.getUtilityModelParams().setCurrC1(c1);
                		for (double c2 : c2s) {
                			this.dgData.getUtilityModelParams().setCurrC2(c2);
                        	for (double d1 : d1s) {
                        		this.dgData.getUtilityModelParams().setCurrD1(d1);
                        		for (double d2 : d2s) {
                        			this.dgData.getUtilityModelParams().setCurrD2(d2);
		                			for (double sigma : sigmas) {
		                				this.dgData.getUtilityModelParams().setCurrSigma(sigma);
		                				for (double gamma : gammas) {
		                					this.dgData.getUtilityModelParams().setCurrGamma(gamma);
		                					for (int tau : taus) {
		                						this.dgData.getUtilityModelParams().setCurrTau(tau);
		                						for (int N : Ns) {
		                							this.dgData.getUtilityModelParams().setCurrN(N);
		                							for (boolean iota : iotas) {
		                								this.dgData.getUtilityModelParams().setCurrIota(iota);
		                								for (double phi : phis) {
		                									this.dgData.getUtilityModelParams().setCurrPhi(phi);
		                									for (double psi : psis) {
		                										this.dgData.getUtilityModelParams().setCurrPsi(psi);
		                										for (double xi : xis) {
		                											this.dgData.getUtilityModelParams().setCurrXi(xi);
		                											for (double omega : omegas) {
		                												this.dgData.getUtilityModelParams().setCurrOmega(omega);
		                												for (boolean selective : selectives) {
		                													this.dgData.getUtilityModelParams().setCurrSelective(selective);
		
		                													this.dgData.getSimStats().incUpc();
		                													// multiple simulations for same parameter combination
		                													this.dgData.getSimStats().setSimPerUpc(1);
		                													while (this.dgData.getSimStats().getSimPerUpc()
		                															<= this.dgData.getUtilityModelParams().
		                															getSimsPerParameterCombination()) {
		
		                														// simulate
		                														performSingleSimulation();
		
		                														this.dgData.getSimStats().incSimPerUpc();
		                													}
		                												}
		                											}
		                										}
		                									}
		                								}
		                							}
		                						}
		                					}
		                				}
		                			}
                        		}
                			}
                		}
                	}
                }
            }
        }

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in dgData
     */
    private void performSingleSimulation() {
    	
    	NunnerBuskensParameters ump = this.dgData.getUtilityModelParams();

        // setting parameters
        // b1
		if (ump.isB1Random()) {
            ump.setCurrB1(ThreadLocalRandom.current().nextDouble(
                    ump.getB1RandomMin(),
                    ump.getB1RandomMax()));
        }
        // b2
        if (ump.isB2Random()) {
            ump.setCurrB2(ThreadLocalRandom.current().nextDouble(
                    ump.getB2RandomMin(),
                    ump.getB2RandomMax()));
        }
        // alpha
        if (ump.isAlphaRandom()) {
            ump.setCurrAlpha(ThreadLocalRandom.current().nextDouble(
                    ump.getAlphaRandomMin(),
                    ump.getAlphaRandomMax()));
        }
        // c1
        if (ump.isC1Random()) {
            ump.setCurrC1(ThreadLocalRandom.current().nextDouble(
                    ump.getC1RandomMin(),
                    ump.getC1RandomMax()));
        }
        // c2
        if (ump.isC2Random()) {
            ump.setCurrC2(ThreadLocalRandom.current().nextDouble(
                    ump.getC2RandomMin(),
                    ump.getC2RandomMax()));
        }
        // d1
		if (ump.isD1Random()) {
            ump.setCurrD1(ThreadLocalRandom.current().nextDouble(
                    ump.getD1RandomMin(),
                    ump.getD1RandomMax()));
        }
        // d2
		if (ump.isD2Random()) {
            ump.setCurrD2(ThreadLocalRandom.current().nextDouble(
                    ump.getD2RandomMin(),
                    ump.getD2RandomMax()));
        }
        // sigma
        if (ump.isSigmaRandom()) {
            ump.setCurrSigma(ThreadLocalRandom.current().nextDouble(
                    ump.getSigmaRandomMin(),
                    ump.getSigmaRandomMax()));
        }
        // gamma
        if (ump.isGammaRandom()) {
            ump.setCurrGamma(ThreadLocalRandom.current().nextDouble(
                    ump.getGammaRandomMin(),
                    ump.getGammaRandomMax()));
        }
        // tau
        if (ump.isTauRandom()) {
            ump.setCurrTau(ThreadLocalRandom.current().nextInt(
                    ump.getTauRandomMin(),
                    ump.getTauRandomMax()));
        }
        // N
        if (ump.isNRandom()) {
            ump.setCurrN(ThreadLocalRandom.current().nextInt(
                    ump.getNRandomMin(),
                    ump.getNRandomMax()));
        }
        // iota
        if (ump.isIotaRandom()) {
            ump.setCurrIota(ThreadLocalRandom.current().nextBoolean());
        }
        // phi
        if (ump.isPhiRandom()) {
            ump.setCurrPhi(ThreadLocalRandom.current().nextDouble(
                    ump.getPhiRandomMin(),
                    ump.getPhiRandomMax()));
        }
        // psi
        if (ump.isPsiRandom()) {
            ump.setCurrPsi(ThreadLocalRandom.current().nextDouble(
                    ump.getPsiRandomMin(),
                    ump.getPsiRandomMax()));
        }
        // xi
        if (ump.isXiRandom()) {
            ump.setCurrXi(ThreadLocalRandom.current().nextDouble(
                    ump.getXiRandomMin(),
                    ump.getXiRandomMax()));
        }
        // omega
        if (ump.isOmegaRandom()) {
            ump.setCurrOmega(ThreadLocalRandom.current().nextDouble(
                    ump.getOmegaRandomMin(),
                    ump.getOmegaRandomMax()));
        }
        // selective
        if (ump.isSelectiveRandom()) {
            ump.setCurrSelective(ThreadLocalRandom.current().nextBoolean());
        }
        

        // create utility
        UtilityFunction uf = new NunnerBuskens(
                ump.getCurrB1(),
                ump.getCurrB2(),
                ump.getCurrAlpha(),
                ump.getCurrC1(),
                ump.getCurrC2(),
                ump.getCurrD1(),
                ump.getCurrD2()); 

        // create disease specs
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR,
                ump.getCurrTau(),
                ump.getCurrSigma(),
                ump.getCurrGamma(),
                0);
        
        // Create risk score according to experimental data from Nunner et al. (2023)
        List<Double> riskScores = new ArrayList<Double>(ump.getCurrN());
        double riskScoreSum = 0.0;
        while (riskScores.size() < ump.getN()) {
        	double meanRiskScore = 1.223701;
        	double sdRiskScore = 0.4625078;
        	NormalDistribution nd = new NormalDistribution(meanRiskScore, sdRiskScore);
        	
            double riskScore = -1.0;
            while (riskScore > 2.0 || riskScore < 0.447402) {
                riskScore = nd.sample();
            }
            riskScores.add(riskScore);
            riskScoreSum += riskScore;
        }
        Collections.sort(riskScores);
        ump.setCurrRMin(riskScores.get(0));
        ump.setCurrRMax(riskScores.get(riskScores.size()-1));
        Collections.shuffle(riskScores);
        ump.setRSigmaAv(riskScoreSum / riskScores.size());
        ump.setCurrRSigmas(riskScores.stream().mapToDouble(Double::doubleValue).toArray());
        ump.setRPiAv(riskScoreSum / riskScores.size());
        ump.setCurrRPis(riskScores.stream().mapToDouble(Double::doubleValue).toArray());

        for (int simIteration = 1; simIteration <= ump.getSimIterations(); simIteration++) {

            this.dgData.getSimStats().setSimIt(simIteration);

            // reset sim epidemic stats
            this.dgData.getSimStats().resetEpidemicStats();

            // create network
            this.network = new Network();

            // begin: GEXF export
            if (PropertiesHandler.getInstance().isExportGexf()) {
                this.gexfWriter = new GEXFWriter();
                this.dgData.setExportFileName(getExportPath() + this.dgData.getSimStats().getUid()
                        + "-" + simIteration + "-" + ".gexf");
                gexfWriter.startRecording(network, this.dgData.getExportFileName());
            }

            // add agents - with RPi == RSigma!!!
            for (int n = 0; n < ump.getCurrN(); n++) {
            	
            	double r = riskScores.get(n);
                
                network.addAgent(uf, ds,
                        r,
                        r,
                        ump.getCurrPhi(),
                        ump.getCurrOmega(),
                        ump.isCurrSelective(),
                        ump.getCurrPsi(),
                        ump.getCurrXi(),
                        // TODO make age optional
                        AgeStructure.getInstance().getRandomAge(),
                        false,
                        // TODO make profession optional
                        "NA",
                        false,
                        false);
            }
            this.dgData.setAgents(new LinkedList<Agent>(network.getAgents()));

            // create full network if required
            if (!ump.isCurrIota()) {
                network.createFullNetwork();
            }

            this.indexCase = this.network.getRandomNotInfectedAgent();
            this.dgData.getSimStats().setRounds(0);

            if (ump.getEpStructure() == EpidemicStructures.BOTH) {
                ump.setCurrEpStructure(EpidemicStructures.STATIC);
                this.simulatePreEpidemic();
                this.simulateEpidemic(ds, indexCase, true);
                // write agent data
                if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                        PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                            this.adWriter.writeCurrentData();
                }
                ump.setCurrEpStructure(EpidemicStructures.DYNAMIC);
                this.simulateEpidemic(ds, indexCase, false);
                this.simulatePostEpidemic();

            } else {
                ump.setCurrEpStructure(ump.getEpStructure());
                this.simulatePreEpidemic();
                this.simulateEpidemic(ds, indexCase, true);
                this.simulatePostEpidemic();
            }

            // write summary data
            if (PropertiesHandler.getInstance().isExportSummary()) {
                this.ssWriter.writeCurrentData();
            }
            // write agent data
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                        this.adWriter.writeCurrentData();
            }

            logger.debug("Finished - "
                    + "UPC: " + this.dgData.getSimStats().getUpc() + "/" + this.dgData.getSimStats().getUpcs()
                    + ", simulation: "  + this.dgData.getSimStats().getSimPerUpc() + "/"
                    + ump. getSimsPerParameterCombination()
                    + ", iteration: " + simIteration + "/"
                    + ump.getSimIterations());
        }
    }

    /**
     *
     */
    private void simulatePreEpidemic() {

        boolean isEpStatic = this.dgData.getUtilityModelParams().getCurrEpStructure() == EpidemicStructures.STATIC;

        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);

        // create simulation
        this.simulation = new Simulation(network, isEpStatic);
        this.simulation.addSimulationListener(this);

        // simulate
        this.simulation.simulateUntilStable(this.dgData.getUtilityModelParams().getZeta());
    }

    /**
     *
     */
    private void simulatePostEpidemic() {

        boolean isEpStatic = this.dgData.getUtilityModelParams().getCurrEpStructure() == EpidemicStructures.STATIC;

        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.POST_EPIDEMIC);

        // create simulation
        this.simulation = new Simulation(network, isEpStatic);
        this.simulation.addSimulationListener(this);

        // simulate
        this.simulation.simulateUntilStable(this.dgData.getUtilityModelParams().getZeta());
    }

    /**
     * @param ds
     */
    private void simulateEpidemic(DiseaseSpecs ds, Agent indexCase, boolean savePreEpidemicData) {

        this.network.resetDiseaseStates();
        indexCase.forceInfect(ds);

        if (savePreEpidemicData) {
            this.dgData.setNetStatsPre(new NetworkStatsPre(this.network, this.simulation.getRounds()));
            this.dgData.setIndexCaseStats(new AgentStatsPre(indexCase, this.simulation.getRounds()));
            HashMap<String, AgentStatsPre> agentStats = new HashMap<String, AgentStatsPre>();
            Iterator<Agent> aIt = this.network.getAgentIterator();
            while (aIt.hasNext()) {
                Agent agent = aIt.next();
                agent.setInitialIndexCaseDistance(indexCase);
                agentStats.put(agent.getId(), new AgentStatsPre(agent, this.simulation.getRounds()));
            }
            this.dgData.setAgentStatsPre(agentStats);
        }


        this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);

        switch (this.dgData.getUtilityModelParams().getCurrEpStructure()) {
            case STATIC:
                this.simulation.setEpStatic(true);
                break;

            case DYNAMIC:
                this.simulation.setEpStatic(false);
                break;

            case BOTH:
            default:
                logger.error("Unimplement epidemic structure: " + this.dgData.getUtilityModelParams().getCurrEpStructure());
        }

        this.dgData.getSimStats().setRoundStartInfection(this.simulation.getRounds());
        // simulate
        this.simulation.simulateUntilEpidemicFinished();

        // save data of last round of post-epidemic stage
        switch (this.dgData.getUtilityModelParams().getCurrEpStructure()) {
            case STATIC:
                this.dgData.setNetStatsPostStatic(new NetworkStatsPost(this.network));
                HashMap<String, AgentStatsPost> agentStatsStatic = new HashMap<String, AgentStatsPost>();
                Iterator<Agent> aItStatic = this.network.getAgentIterator();
                while (aItStatic.hasNext()) {
                    Agent agent = aItStatic.next();
                    agentStatsStatic.put(agent.getId(), new AgentStatsPost(agent));
                }
                this.dgData.setAgentStatsPostStatic(agentStatsStatic);
                break;

            case DYNAMIC:
                this.dgData.setNetStatsPostDynamic(new NetworkStatsPost(this.network));
                HashMap<String, AgentStatsPost> agentStatsDynamic = new HashMap<String, AgentStatsPost>();
                Iterator<Agent> aItDynamic = this.network.getAgentIterator();
                while (aItDynamic.hasNext()) {
                    Agent agent = aItDynamic.next();
                    agentStatsDynamic.put(agent.getId(), new AgentStatsPost(agent));
                }
                this.dgData.setAgentStatsPostDynamic(agentStatsDynamic);
                break;

            case BOTH:
            default:
                logger.error("Unimplement epidemic structure: " + this.dgData.getUtilityModelParams().getCurrEpStructure());
        }

        // end: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter.stopRecording();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#prepareAnalysis()
     */
    @Override
    protected String prepareAnalysis() {
        // preparation of R-scripts
        Path srcAnalysis = Paths.get(PropertiesHandler.getInstance().getRAnalysisNunnerBuskensTemplatePath());
        String dstAnalysisPath = getExportPath() + "nunnerbuskens.R";
        Path dstAnalysis = Paths.get(dstAnalysisPath);
        try {
            Files.copy(srcAnalysis, dstAnalysis, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e);
        }
        return dstAnalysisPath;
    }

    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            if (PropertiesHandler.getInstance().isExportSummary()) {
                this.ssWriter.flush();
                this.ssWriter.close();
            }
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                this.rsWriter.flush();
                this.rsWriter.close();
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.adWriter.flush();
                this.adWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationStarted(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationStarted(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        this.dgData.getSimStats().setRounds(dgData.getSimStats().getRounds() + 1);

        switch (this.dgData.getSimStats().getSimStage()) {
            case ACTIVE_EPIDEMIC:
                int epidemicSize = simulation.getNetwork().getInfected().size();

                switch (this.dgData.getUtilityModelParams().getCurrEpStructure()) {
                    case STATIC:
                        if (epidemicSize > this.dgData.getSimStats().getEpidemicPeakSizeStatic()) {
                            this.dgData.getSimStats().setEpidemicPeakSizeStatic(epidemicSize);
                            this.dgData.getSimStats().setEpidemicPeakStatic(simulation.getRounds());
                        }
                        break;

                    case DYNAMIC:
                        if (epidemicSize > this.dgData.getSimStats().getEpidemicPeakSizeDynamic()) {
                            this.dgData.getSimStats().setEpidemicMaxInfectionsDynamic(epidemicSize);
                            this.dgData.getSimStats().setEpidemicPeakDynamic(simulation.getRounds());
                        }
                        break;

                    case BOTH:
                    default:
                        break;
                }
                break;

            case PRE_EPIDEMIC:
            case POST_EPIDEMIC:
            case FINISHED:
            default:
                break;
        }

        if (PropertiesHandler.getInstance().isExportSummaryEachRound() || PropertiesHandler.getInstance().isExportAgentDetails()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.dgData.setIndexCaseStatsCurrent(new AgentStatsPre(this.indexCase, simulation.getRounds()));
        }
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter.writeCurrentData();
        }
        if (PropertiesHandler.getInstance().isExportAgentDetails()) {
            this.adWriter.writeCurrentData();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.dgData.getSimStats().setRoundLastInfection(simulation.getRounds());

        if (this.dgData.getUtilityModelParams().getCurrEpStructure() == EpidemicStructures.STATIC) {
            this.dgData.getSimStats().setEpidemicDurationStatic(simulation.getRounds());
        } else {
            this.dgData.getSimStats().setEpidemicDurationDynamic(simulation.getRounds());
        }

        this.dgData.getSimStats().setSimStage(SimulationStage.STOPPED_EPIDEMIC);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
//        if (this.dgData.getSimStats().getSimStage() == SimulationStage.STOPPED_EPIDEMIC) {
////            this.dgData.getSimStats().setSimStage(SimulationStage.FINISHED);
//            if (PropertiesHandler.getInstance().isExportSummaryEachRound() ||
//                    PropertiesHandler.getInstance().isExportAgentDetails() ||
//                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
//                this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
//            }
//            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
//                this.rsWriter.writeCurrentData();
//            }
//            if (PropertiesHandler.getInstance().isExportAgentDetails()) {
//                this.adWriter.writeCurrentData();
//            }
//        }
    }

}
