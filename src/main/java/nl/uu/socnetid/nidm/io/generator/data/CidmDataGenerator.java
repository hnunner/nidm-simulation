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
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentListener;
import nl.uu.socnetid.nidm.data.out.CidmParameters;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.analysis.RegressionParameterWriter;
import nl.uu.socnetid.nidm.io.csv.CidmAgentDetailsWriter;
import nl.uu.socnetid.nidm.io.csv.CidmRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.csv.CidmSimulationSummaryWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.Cidm;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class CidmDataGenerator extends AbstractDataGenerator implements AgentListener, SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(CidmDataGenerator.class);

    // stats
    private DataGeneratorData<CidmParameters> dgData;

    // network
    private Network network;
//    private int tiesBrokenWithInfectionPresent;

    // simulation
    private Simulation simulation;

    // data export
    private CidmSimulationSummaryWriter ssWriter;
    private CidmRoundSummaryWriter rsWriter;
    private CidmAgentDetailsWriter adWriter;
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
    public CidmDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "cidm";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<CidmParameters>(PropertiesHandler.getInstance().getCidmParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new CidmSimulationSummaryWriter(getExportPath() + "simulation-summary.csv", this.dgData);
        }
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter = new CidmRoundSummaryWriter(getExportPath() + "round-summary.csv", this.dgData);
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.adWriter = new CidmAgentDetailsWriter(getExportPath() + "agent-details.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        // risk perceptions the same with regards to susceptibility (pi) and diseases severity (sigma)?
        double[] rSigmas = this.dgData.getUtilityModelParams().getRSigmas();
        double[] rPis = this.dgData.getUtilityModelParams().getRPis();
        if (this.dgData.getUtilityModelParams().isRsEqual()) {
            rPis = new double[1];
        }

        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();
        double[] omegas = this.dgData.getUtilityModelParams().isOmegaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getOmegas();


        // unique parameter combinations
        int upcs = this.dgData.getUtilityModelParams().getAlphas().length *
                this.dgData.getUtilityModelParams().getKappas().length *
                this.dgData.getUtilityModelParams().getBetas().length *
                this.dgData.getUtilityModelParams().getLamdas().length *
                this.dgData.getUtilityModelParams().getCs().length *
                this.dgData.getUtilityModelParams().getMus().length *
                this.dgData.getUtilityModelParams().getSigmas().length *
                this.dgData.getUtilityModelParams().getGammas().length *
                rSigmas.length *
                rPis.length *
                Ns.length *
                iotas.length *
                phis.length *
                omegas.length *
                this.dgData.getUtilityModelParams().getTaus().length;

        // loop over all possible parameter combinations
        for (double alpha : this.dgData.getUtilityModelParams().getAlphas()) {
            this.dgData.getUtilityModelParams().setCurrAlpha(alpha);
            for (double kappa : this.dgData.getUtilityModelParams().getKappas()) {
                this.dgData.getUtilityModelParams().setCurrKappa(kappa);
                for (double beta : this.dgData.getUtilityModelParams().getBetas()) {
                    this.dgData.getUtilityModelParams().setCurrBeta(beta);
                    for (double lamda : this.dgData.getUtilityModelParams().getLamdas()) {
                        this.dgData.getUtilityModelParams().setCurrLamda(lamda);
                        for (double c : this.dgData.getUtilityModelParams().getCs()) {
                            this.dgData.getUtilityModelParams().setCurrC(c);
                            for (double mu : this.dgData.getUtilityModelParams().getMus()) {
                                this.dgData.getUtilityModelParams().setCurrMu(mu);
                                for (double sigma : this.dgData.getUtilityModelParams().getSigmas()) {
                                    this.dgData.getUtilityModelParams().setCurrSigma(sigma);
                                    for (double gamma : this.dgData.getUtilityModelParams().getGammas()) {
                                        this.dgData.getUtilityModelParams().setCurrGamma(gamma);
                                        for (double rSigma : rSigmas) {
                                            this.dgData.getUtilityModelParams().setCurrRSigma(rSigma);
                                            for (double rPi : rPis) {
                                                if (this.dgData.getUtilityModelParams().isRsEqual()) {
                                                    this.dgData.getUtilityModelParams().setCurrRPi(rSigma);
                                                } else {
                                                    this.dgData.getUtilityModelParams().setCurrRPi(rPi);
                                                }
                                                for (int N : Ns) {
                                                    this.dgData.getUtilityModelParams().setCurrN(N);
                                                    for (boolean iota : iotas) {
                                                        this.dgData.getUtilityModelParams().setCurrIota(iota);
                                                        for (double phi : phis) {
                                                            this.dgData.getUtilityModelParams().setCurrPhi(phi);
                                                            for (double omega : omegas) {
                                                                this.dgData.getUtilityModelParams().setCurrOmega(omega);
                                                                for (int tau : this.dgData.getUtilityModelParams().getTaus()) {
                                                                    this.dgData.getUtilityModelParams().setCurrTau(tau);

                                                                    this.dgData.getSimStats().incUpc();
                                                                    logger.info("Starting to compute "
                                                                            + this.dgData.getUtilityModelParams().
                                                                            getSimsPerParameterCombination()
                                                                            + " simulations for parameter combination: "
                                                                            + this.dgData.getSimStats().getUpc() + " / "
                                                                            + upcs);

                                                                    // multiple simulations for same parameter combination
                                                                    this.dgData.getSimStats().setSimPerUpc(1);
                                                                    while (this.dgData.getSimStats().getSimPerUpc()
                                                                            <= this.dgData.getUtilityModelParams().
                                                                            getSimsPerParameterCombination()) {

                                                                        // simulate
                                                                        performSingleSimulation();

                                                                        // log simulation summary
                                                                        if (PropertiesHandler.getInstance().isExportSummary()) {
                                                                            this.ssWriter.writeCurrentData();
                                                                        }

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

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in dgData
     */
    private void performSingleSimulation() {

        // create network
        this.network = new Network();

        // setting parameters
        // N
        if (this.dgData.getUtilityModelParams().isNRandom()) {
            this.dgData.getUtilityModelParams().setCurrN(ThreadLocalRandom.current().nextInt(
                    this.dgData.getUtilityModelParams().getNRandomMin(),
                    this.dgData.getUtilityModelParams().getNRandomMax()));
        }
        // iota
        if (this.dgData.getUtilityModelParams().isIotaRandom()) {
            this.dgData.getUtilityModelParams().setCurrIota(ThreadLocalRandom.current().nextBoolean());
        }
        // phi
        if (this.dgData.getUtilityModelParams().isPhiRandom()) {
            this.dgData.getUtilityModelParams().setCurrPhi(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getPhiRandomMin(),
                    this.dgData.getUtilityModelParams().getPhiRandomMax()));
        }
        // omega
        if (this.dgData.getUtilityModelParams().isOmegaRandom()) {
            this.dgData.getUtilityModelParams().setCurrOmega(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getOmegaRandomMin(),
                    this.dgData.getUtilityModelParams().getOmegaRandomMax()));
        }

        // begin: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter = new GEXFWriter();
            this.dgData.setExportFileName(getExportPath() + this.dgData.getSimStats().getUid() + ".gexf");
            gexfWriter.startRecording(network, this.dgData.getExportFileName());
        }

        // create utility
        UtilityFunction uf = new Cidm(this.dgData.getUtilityModelParams().getCurrAlpha(),
                this.dgData.getUtilityModelParams().getCurrKappa(),
                this.dgData.getUtilityModelParams().getCurrBeta(),
                this.dgData.getUtilityModelParams().getCurrLamda(),
                this.dgData.getUtilityModelParams().getCurrC());

        // add agents
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR,
                this.dgData.getUtilityModelParams().getCurrTau(),
                this.dgData.getUtilityModelParams().getCurrSigma(),
                this.dgData.getUtilityModelParams().getCurrGamma(),
                this.dgData.getUtilityModelParams().getCurrMu());
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            Agent agent = network.addAgent(uf, ds,
                    this.dgData.getUtilityModelParams().getCurrRSigma(),
                    this.dgData.getUtilityModelParams().getCurrRPi(),
                    this.dgData.getUtilityModelParams().getCurrPhi(),
                    this.dgData.getUtilityModelParams().getCurrOmega());
            agent.addAgentListener(this);
        }
        this.dgData.setAgents(new LinkedList<Agent>(network.getAgents()));

        // create full network if required
        if (!this.dgData.getUtilityModelParams().isCurrIota()) {
            this.network.createFullNetwork();
        }

        // PRE_EPIDEMIC STAGE
        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);
        // create simulation
        this.simulation = new Simulation(network);
        this.simulation.addSimulationListener(this);
        // simulate
        this.simulation.simulate(this.dgData.getUtilityModelParams().getZeta());
        // save data of last round of pre-epidemic stage
        this.dgData.setNetStatsPre(new NetworkStatsPre(this.network, this.simulation.getRounds()));
        // write agent detail data if necessary
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.adWriter.writeCurrentData();
        }

        // EPIDEMIC AND POST-EPIDEMIC STAGES
//        this.tiesBrokenWithInfectionPresent = 0;
        Agent indexCase = this.network.infectRandomAgent(ds);
        this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
        // save index case properties of pre-epidemic stage
        this.dgData.setIndexCaseStats(new AgentStatsPre(indexCase, this.simulation.getRounds()));
        this.dgData.getSimStats().setRoundStartInfection(this.simulation.getRounds());
        // simulate
        this.simulation.simulate(this.dgData.getUtilityModelParams().getEpsilon());
        // save data of last round of post-epidemic stage
        this.dgData.setNetStatsPostStatic(new NetworkStatsPost(this.network));     // TODO static is only quick dirty fix
//        this.dgData.getNetStatsPostStatic().setTiesBrokenWithInfectionPresent(this.tiesBrokenWithInfectionPresent);     // TODO static is only quick dirty fix

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
        RegressionParameterWriter rpWriter = new RegressionParameterWriter();
        return rpWriter.writeRegressionFiles(getExportPath());
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
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeAdded(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Agent agent, String attribute, Object value) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeChanged(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Agent agent, String attribute, Object oldValue, Object newValue) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeRemoved(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Agent agent, String attribute) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.nidm.agents.Agent, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Agent agent1, Agent agent2) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyConnectionRemoved(
     * nl.uu.socnetid.nidm.agents.Agent, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Agent agent, Edge edge) {
//        if (this.dgData.getSimStats().getSimStage() == SimulationStage.ACTIVE_EPIDEMIC) {
//            this.tiesBrokenWithInfectionPresent++;
//        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationStarted(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationStarted(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyRoundFinished(Agent agent) {
        this.dgData.getSimStats().setRounds(this.simulation.getRounds());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (PropertiesHandler.getInstance().isExportSummaryEachRound() || PropertiesHandler.getInstance().isExportAgentDetails()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
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
        this.dgData.getSimStats().setRoundLastInfection(simulation.getRounds() -
                this.dgData.getSimStats().getRoundStartInfection());
        this.dgData.getSimStats().setSimStage(SimulationStage.POST_EPIDEMIC);
        // TODO improve
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.adWriter.writeCurrentData();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.dgData.getSimStats().getSimStage() == SimulationStage.POST_EPIDEMIC) {
            this.dgData.getSimStats().setSimStage(SimulationStage.FINISHED);
            if (PropertiesHandler.getInstance().isExportSummaryEachRound() ||
                    PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            }
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                this.rsWriter.writeCurrentData();
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.adWriter.writeCurrentData();
            }
        }
    }

}
