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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ExperimentParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.ExperimentRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.csv.ExperimentSimulationSummaryWriter;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
import nl.uu.socnetid.nidm.stats.SimulationStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ExperimentDataGenerator_RandomParams extends AbstractDataGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(ExperimentDataGenerator_RandomParams.class);

    // network
    private Network network;
    private Agent indexCase;

    private static final int INDEX_CASE_HC = 4;
    private static final int[] RISK_SCORE_ORDER_HC_ASS = {36, 6, 57, 48, 16, 21, 38, 47, 30, 27, 58, 12, 25, 53, 31, 19, 8, 56,
            26, 20, 18, 35, 13, 15, 3, 59, 33, 23, 37, 4, 11, 2, 1, 34, 51, 32, 52, 28, 54, 7, 17, 22, 39, 10, 44, 49, 46, 9,
            29, 42, 5, 40, 14, 45, 50, 55, 43, 24, 41, 60};
    private static final int[] RISK_SCORE_ORDER_HC_RAND = {28, 55, 18, 27, 3, 58, 34, 59, 46, 45, 54, 36, 29, 25, 38, 57, 60, 13,
            39, 24, 49, 6, 8, 11, 41, 33, 14, 21, 37, 4, 50, 52, 40, 7, 23, 9, 22, 17, 20, 43, 1, 16, 31, 42, 12, 56, 5, 2, 26,
            30, 48, 15, 53, 35, 10, 47, 32, 44, 51, 19};

    private static final int INDEX_CASE_LC = 35;
    private static final int[] RISK_SCORE_ORDER_LC_ASS = {53, 3, 24, 19, 11, 29, 47, 22, 59, 5, 17, 28, 56, 13, 44, 34, 36, 57,
            55, 12, 41, 43, 1, 21, 33, 31, 10, 39, 46, 35, 30, 16, 48, 50, 14, 37, 23, 20, 32, 7, 4, 8, 27, 58, 38, 42, 6, 60,
            45, 9, 26, 49, 40, 52, 15, 51, 54, 25, 2, 18};
    private static final int[] RISK_SCORE_ORDER_LC_RAND = {20, 27, 19, 38, 22, 59, 18, 55, 25, 41, 1, 30, 60, 42, 43, 47, 33, 40,
            17, 6, 36, 16, 57, 44, 32, 51, 54, 37, 53, 35, 12, 48, 15, 21, 31, 8, 10, 24, 46, 56, 29, 45, 49, 3, 2, 4, 14, 11,
            23, 13, 5, 50, 9, 26, 58, 34, 52, 7, 39, 28};

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<ExperimentParameters> dgData;
    private ExperimentSimulationSummaryWriter ssWriter;
    private ExperimentRoundSummaryWriter rsWriter;


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
    public ExperimentDataGenerator_RandomParams(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "experiment";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        ExperimentParameters ep = new ExperimentParameters();

        ep.setN(60);
        ep.setB1(1.0);
        
        ep.setB2(0.5);
        ep.setC1(0.2);
//        ep.setC2(0.067);		// 6 ties
//        ep.setC2(0.061538462);		// 6.5 ties
//        ep.setC2(0.059701493);		// 6.7 ties
//        ep.setC2(0.05714);		// 7ties
//        ep.setC2(0.05);		// 8ties

        ep.setGamma(0.15);
        ep.setSigma(0.34);
//        ep.setSigma(5.00);


        this.dgData = new DataGeneratorData<ExperimentParameters>(ep);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        this.ssWriter = new ExperimentSimulationSummaryWriter(getExportPath() + "sim-simulation-summary.csv", this.dgData);
        this.rsWriter = new ExperimentRoundSummaryWriter(getExportPath() + "sim-round-summary.csv", this.dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        ExperimentParameters ump = this.dgData.getUtilityModelParams();
        SimulationStats simStats = this.dgData.getSimStats();

        // SENSITIVITY ANALYSIS
        ump.setTau(4);
        ump.setPhi(0.2);
        ump.setPsi(0.5);
        ump.setXi(0.3);
    	ump.setRationalityInfectedNeighbor(1.0);
    	

        StringBuilder ids = new StringBuilder("level.node <- c(");
        StringBuilder cClus = new StringBuilder("cond.clustering <- c(");
        StringBuilder cMix = new StringBuilder("cond.mixing <- c(");
        StringBuilder dStateNode = new StringBuilder("dstate.node <- c(");
        StringBuilder degrees = new StringBuilder("net.ties <- c(");
        StringBuilder propClosedTriads = new StringBuilder("net.prop.triads.closed <- c(");
        StringBuilder avPathLengths = new StringBuilder("net.avpathlength <- c(");

        for (int upc = 1; upc <= 1; upc++) {
	        simStats.setUpc(upc);
	        
	        for (int simPerUpc = 1; simPerUpc <= 1; simPerUpc++) {
		        simStats.setSimPerUpc(simPerUpc);
		        
		        switch (upc) {
		        // unchanged
		        case 1:
		        	ump.setRationality(1.0);
		        	ump.setOverestimate(1.0);
		        	ump.setC2(0.067);
		        	break;
		        	
		        // random rationality
		        case 2:
		        	ump.setRationality(ThreadLocalRandom.current().nextDouble());
		        	ump.setOverestimate(1.0);
		        	ump.setC2(0.067);
		        	break;

	        	// random overestimate
		        case 3:
		        	ump.setRationality(1.0);
		        	ump.setOverestimate(ThreadLocalRandom.current().nextDouble(1.0, 9.0));
		        	ump.setC2(0.067);
		        	break;

		        // random c2
		        case 4:
		        	ump.setRationality(1.0);
		        	ump.setOverestimate(1.0);
		        	ump.setC2(ThreadLocalRandom.current().nextDouble(0.05, 0.07));
		        	break;

		        default:
		        	throw new RuntimeException("Unknown UPC: " + upc);
		        }
		
		        List<Double> initialHomophilies = new ArrayList<Double>(ump.getSimsPerParameterCombination());
		        
	            for (int simIt = 1; simIt <= 2; simIt++) {
	            	simStats.setSimIt(simIt);
			        
		            // BETWEEN SUBJECT CONDITION: random mixing (omega = 0.0) vs. assoratitive mixing (omega = 0.8)
		            ump.setOmega(simStats.getSimIt() % 2 == 0 ? 0.0 : 0.8);
		
		            // CREATE EXPERIMENTAL SESSION WITH SAME SUBJECTS FOR CLUSTERING CONDITIONS
		            List<Double> riskScores = new ArrayList<>();
		            while (riskScores.size() < ump.getN()) {
		                // expected average risk score and standard deviation - taken from Vriens & Buskens (2021) and
		                // rescaled to range between 0.0 and 2.0: 19.7/31*2 = 1.27, 6.98/31*2 = 0.45
		//			                                double meanRiskScore = 1.27;
		//											NormalDistribution nd = new NormalDistribution(meanRiskScore, 0.45);
		            	
		                // expected average risk score and standard deviation - taken from NIDM experiment and
		                // rescaled to range between 0.0 and 2.0: 
		            	// > mean(d.users$r_score)
		            	// [1] 13.03264
		            	// > convert_risk_score_from_experiment(13.03264)
		            	// [1] 1.223701
		            	// > sd(d.users$r_score)
		            	//[1] 5.863769
		            	// > convert_risk_score_from_experiment(13.03264) - convert_risk_score_from_experiment(5.863769)
		            	// [1] -0.4625078
		            	// > 1.223701 - (2-1.223701)
		            	// [1] 0.447402
		            	double meanRiskScore = 1.223701;
						NormalDistribution nd = new NormalDistribution(meanRiskScore, 0.4625078);
		                double riskScore = -1.0;
		                while (riskScore > 2.0 || riskScore < 0.447402) {
		                    riskScore = nd.sample();
		                }
		                riskScores.add(riskScore);
		            }
		            double averageRiskScore = riskScores.stream().mapToDouble(Double::doubleValue).sum() /
		                    riskScores.size();
		            ump.setAverageRiskScore(averageRiskScore);
		            Collections.sort(riskScores);
		            ump.setCurrRMin(riskScores.get(0));
		            Collections.sort(riskScores, Collections.reverseOrder());
		            ump.setCurrRMax(riskScores.get(riskScores.size()-1));
		
		            // WITHIN SUBJECT CONDITION: high vs. low clustering
		            double[] alphas = {0.0, 0.667};
		            for (double alpha : alphas) {
		                ump.setAlpha(alpha);
		                String indexCaseId;
		                int[] riskScoreOrder;
		                if (alpha == 0.0) {
		
		                    ump.setInputNetworkFile(
		                            getClass().getClassLoader().getResource("low-clustering_experiment.dgs").getPath());
		                    indexCaseId = String.valueOf(INDEX_CASE_LC);
		                    riskScoreOrder = (
		                            ump.getOmega() == 0.0 ? RISK_SCORE_ORDER_LC_RAND : RISK_SCORE_ORDER_LC_ASS);
		                } else {
		                	
		                    ump.setInputNetworkFile(
		                            getClass().getClassLoader().getResource(
		                                    "high-clustering_experiment.dgs").getPath());
		                    indexCaseId = String.valueOf(INDEX_CASE_HC);
		                    riskScoreOrder = (
		                            ump.getOmega() == 0.0 ? RISK_SCORE_ORDER_HC_RAND : RISK_SCORE_ORDER_HC_ASS);
		                }
		
		                // import network
		                DGSReader dgsReader = new DGSReader();
		                this.network = dgsReader.readNetwork(ump.getInputNetworkFile());
		                if (this.network.getN() != ump.getN()) {
		                    logger.error("Mismatch between network size  (" + this.network.getN() +
		                            ") and intended network size (" + ump.getN() + ")!");
		                    return;
		                }
		                this.network.updateRiskScores(-1.0);
		                UtilityFunction uf = new NunnerBuskens(ump.getB1(),
		                        ump.getB2(), ump.getAlpha(), ump.getC1(), ump.getC2(), ump.getOverestimate());
		                this.network.updateUtility(uf);
		                DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR,
		                        ump.getTau(), ump.getSigma(), ump.getGamma(), 0);
		                this.network.updateDisease(ds);
		                this.network.updateAgentSelection(ump.getPhi(), ump.getPsi(), ump.getXi());
		                
		                // assign index case, risk scores, and degree of rationality
		                this.indexCase = this.network.getAgent(indexCaseId);
		                Collections.sort(riskScores, Collections.reverseOrder());
		                for (int i = 0; i < this.network.getN(); i++) {
		                    Agent agent = this.network.getAgent(String.valueOf(riskScoreOrder[i]));
							agent.updateRiskScores(riskScores.get(i));
							agent.updateDegreeOfRationality(ump.getRationality());
							agent.updateDegreeOfRationalityInfectedNeighbor(ump.getRationalityInfectedNeighbor());
							agent.updateOmega(ump.getOmega());
							
							
							// make proportion of 0.86 agents irrational (trying to have 12+ ties)
							if (ThreadLocalRandom.current().nextDouble() >= 0.8597222) {
								agent.updateUtilityFunction(new NunnerBuskens(ump.getB1(),ump.getB2(), ump.getAlpha(), ump.getC1(), 0.02, ump.getOverestimate()));
							}
		                }
		
		                // reset disease
		                this.network.resetDiseaseStates();
		                this.dgData.getSimStats().resetEpidemicStats();
		                this.indexCase.forceInfect(ds);
		
		                // initialize simulation
		                this.simulation = new Simulation(network, false);
		                this.simulation.addSimulationListener(this);
		
		                // store data before the epidemic
		                this.dgData.setAgents(new ArrayList<Agent>(this.network.getAgents()));
		                this.dgData.setNetStatsPre(new NetworkStatsPre(this.network, this.simulation.getRounds()));
		                HashMap<String, AgentStatsPre> agentStats = new HashMap<String, AgentStatsPre>();
		                Iterator<Agent> aIt = this.network.getAgentIterator();
		                while (aIt.hasNext()) {
		                    Agent agent = aIt.next();
		                    agent.setInitialIndexCaseDistance(this.indexCase);
		                    agentStats.put(agent.getId(), new AgentStatsPre(agent, this.simulation.getRounds()));
		                }
		                this.dgData.setAgentStatsPre(agentStats);
		                this.dgData.setIndexCaseStats(new AgentStatsPre(this.indexCase, this.simulation.getRounds()));
		
		                this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
		                this.dgData.getSimStats().setRoundStartInfection(this.simulation.getRounds());
		                
		                initialHomophilies.add(this.network.getAssortativityRiskPerception());
		                
		                
		                
		                
		                double avPathLength = Math.round(network.getAvPathLength() * 100.0) / 100.0;

		                
		                Iterator<Agent> agents = network.getAgentIterator();
		                while (agents.hasNext()) {
		                	Agent agent = agents.next();

		                	ids.append(agent.getId());
		                	cClus.append(ump.getAlpha() == 0.0 ? "\"A\"" : "\"B\"");
		                	cMix.append(ump.getOmega() == 0.0 ? "1" : "2");
		                	dStateNode.append(agent.isInfected() ? 2 : 1);
		                	degrees.append(agent.getDegree());
		                	LocalAgentConnectionsStats lacs = StatsComputer.computeLocalAgentConnectionsStats(agent);
		                	propClosedTriads.append(Math.round((lacs.getY() + lacs.getZ()) == 0 ? 0.0 : ((double) lacs.getZ()) / (lacs.getY() + lacs.getZ()) * 100.0) / 100.0);
		                	avPathLengths.append(avPathLength);
		                	
	                		ids.append(", ");
	                		cClus.append(", ");
	                		cMix.append(", ");
	                		dStateNode.append(", ");
	                		degrees.append(", ");
	                		propClosedTriads.append(", ");
	                		avPathLengths.append(", ");
		                }
		                
		                
		                
		                
		                // simulate
		                this.simulation.simulateUntilEpidemicFinished();
		                
		                
		                logger.info("final size: " + network.getRecovered().size());
		                
		
		                // save data of last round of post-epidemic stage
		                this.dgData.setNetStatsPostDynamic(new NetworkStatsPost(this.network));
		                HashMap<String, AgentStatsPost> agentStatsDynamic = new HashMap<String, AgentStatsPost>();
		                Iterator<Agent> aItDynamic = this.network.getAgentIterator();
		                while (aItDynamic.hasNext()) {
		                    Agent agent = aItDynamic.next();
		                    agentStatsDynamic.put(agent.getId(), new AgentStatsPost(agent));
		                }
		                this.dgData.setAgentStatsPostDynamic(agentStatsDynamic);
		
		                // write summary data
		                this.ssWriter.writeCurrentData();
		
		                logger.debug("Finished - "
		                        + "UPC: " + this.dgData.getSimStats().getUpc() + "/4"
		                        + ", simulation: "  + this.dgData.getSimStats().getSimPerUpc() + "/100"
		                        + ", iteration: " + this.dgData.getSimStats().getSimIt() + "/48");
		            }
	            }

                logger.info(ids.toString());
                logger.info(cClus.toString());
                logger.info(cMix.toString());
                logger.info(dStateNode.toString());
                logger.info(degrees.toString());
                logger.info(propClosedTriads.toString());
                logger.info(avPathLengths.toString());
                
		        logger.info("Initial homophily: " + initialHomophilies);
	        }
        }

        // finish data generation
        finalizeDataExportFiles();
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
        this.dgData.getSimStats().setRounds(simulation.getRounds());

        int epidemicSize = simulation.getNetwork().getInfected().size();
        if (epidemicSize > this.dgData.getSimStats().getEpidemicPeakSizeDynamic()) {
            this.dgData.getSimStats().setEpidemicMaxInfectionsDynamic(epidemicSize);
            this.dgData.getSimStats().setEpidemicPeakDynamic(simulation.getRounds());
        }

        this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
        this.dgData.setIndexCaseStatsCurrent(new AgentStatsPre(this.indexCase, simulation.getRounds()));
        this.rsWriter.writeCurrentData();
        //        this.adWriter.writeCurrentData();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.dgData.getSimStats().setRoundLastInfection(simulation.getRounds());
        this.dgData.getSimStats().setEpidemicDurationDynamic(simulation.getRounds());
        this.dgData.getSimStats().setSimStage(SimulationStage.POST_EPIDEMIC);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        //        if (this.dgData.getSimStats().getSimStage() == SimulationStage.POST_EPIDEMIC) {
        //            this.dgData.getSimStats().setSimStage(SimulationStage.FINISHED);
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

}
