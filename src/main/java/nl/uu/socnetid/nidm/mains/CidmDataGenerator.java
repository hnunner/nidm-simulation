package nl.uu.socnetid.nidm.mains;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentListener;
import nl.uu.socnetid.nidm.data.CidmDataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.generator.CidmAgentDetailsWriter;
import nl.uu.socnetid.nidm.io.generator.CidmRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.generator.CidmSimulationSummaryWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStats;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.Cidm;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * @author Hendrik Nunner
 */
public class CidmDataGenerator implements AgentListener, SimulationListener {

    // logger
    private static final Logger logger = Logger.getLogger(CidmDataGenerator.class);

    // stats
    private CidmDataGeneratorData dgData = new CidmDataGeneratorData();

    // network
    private Network network;
    private boolean tiesBrokenWithInfectionPresent;

    // simulation
    private Simulation simulation;

    // data export
    private static final String FULL_DATA_EXPORT_PATH = PropertiesHandler.getInstance().getDataExportPath() +
            (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
    private CidmSimulationSummaryWriter ssWriter;
    private CidmRoundSummaryWriter rsWriter;
    private CidmAgentDetailsWriter adWriter;
    private GEXFWriter gexfWriter;


    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public static void main(String[] args) throws IOException {
        CidmDataGenerator dataGenerator = new CidmDataGenerator();
        dataGenerator.generateData();
        if (PropertiesHandler.getInstance().isAnalyzeData()) {
            dataGenerator.anaylzeData();
        }
    }


    /**
     * Constructor.
     *
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CidmDataGenerator() throws IOException {
        // initializations
        // create export directory if it does not exist
        File directory = new File(FULL_DATA_EXPORT_PATH);
        if (!directory.exists()){
            directory.mkdirs();
        }

        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new CidmSimulationSummaryWriter(FULL_DATA_EXPORT_PATH + "simulation-summary.csv", this.dgData);
        }
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter = new CidmRoundSummaryWriter(FULL_DATA_EXPORT_PATH + "round-summary.csv", this.dgData);
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.adWriter = new CidmAgentDetailsWriter(FULL_DATA_EXPORT_PATH + "agent-details.csv", this.dgData);
        }
    }


    /**
     * Generates data.
     */
    public void generateData() {

        // risk perceptions the same with regards to susceptibility (pi) and diseases severity (sigma)?
        double[] rSigmas = this.dgData.getUtilityModelParams().getRSigmas();
        double[] rPis = this.dgData.getUtilityModelParams().getRPis();
        if (this.dgData.getUtilityModelParams().isRsEqual()) {
            rPis = new double[1];
        }

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
                this.dgData.getUtilityModelParams().getNs().length *
                this.dgData.getUtilityModelParams().getIotas().length *
                this.dgData.getUtilityModelParams().getPhis().length *
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
                                                for (int N : this.dgData.getUtilityModelParams().getNs()) {
                                                    this.dgData.getUtilityModelParams().setCurrN(N);
                                                    for (boolean iota : this.dgData.getUtilityModelParams().getIotas()) {
                                                        this.dgData.getUtilityModelParams().setCurrIota(iota);
                                                        for (double phi : this.dgData.getUtilityModelParams().getPhis()) {
                                                            this.dgData.getUtilityModelParams().setCurrPhi(phi);
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

                                                                    // uid = "upc-sim"
                                                                    this.dgData.getSimStats().setUid(
                                                                            String.valueOf(this.dgData.getSimStats().getUpc()) +
                                                                            "-" + String.valueOf(
                                                                                    this.dgData.getSimStats().getSimPerUpc()));

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

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in dgData
     */
    private void performSingleSimulation() {

        // create network
        this.network = new Network();

        // begin: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter = new GEXFWriter();
            this.dgData.setGexfExportFile(FULL_DATA_EXPORT_PATH + this.dgData.getSimStats().getUid() + ".gexf");
            gexfWriter.startRecording(network, this.dgData.getGexfExportFile());
        }

        // create utility
        UtilityFunction uf = new Cidm(this.dgData.getUtilityModelParams().getCurrAlpha(),
                this.dgData.getUtilityModelParams().getCurrKappa(),
                this.dgData.getUtilityModelParams().getCurrBeta(),
                this.dgData.getUtilityModelParams().getCurrLamda(),
                this.dgData.getUtilityModelParams().getCurrC());

        // add agents - with RPi == RSigma!!!
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR,
                this.dgData.getUtilityModelParams().getCurrTau(),
                this.dgData.getUtilityModelParams().getCurrSigma(),
                this.dgData.getUtilityModelParams().getCurrGamma(),
                this.dgData.getUtilityModelParams().getCurrMu());
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            Agent agent = network.addAgent(uf, ds,
                    this.dgData.getUtilityModelParams().getCurrRSigma(),
                    this.dgData.getUtilityModelParams().getCurrRPi(),
                    this.dgData.getUtilityModelParams().getCurrPhi());
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
        this.dgData.setNetStatsPre(new NetworkStats(this.network));
        this.dgData.getNetStatsPre().setTiesBrokenWithInfectionPresent(false);
        // write agent detail data if necessary
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.adWriter.writeCurrentData();
        }

        // EPIDEMIC AND POST-EPIDEMIC STAGES
        this.tiesBrokenWithInfectionPresent = false;
        Agent indexCase = this.network.infectRandomAgent(ds);
        this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
        // save index case properties of pre-epidemic stage
        this.dgData.setIndexCaseStats(new AgentStats(indexCase));
        this.dgData.getSimStats().setRoundStartInfection(this.simulation.getRounds());
        // simulate
        this.simulation.simulate(this.dgData.getUtilityModelParams().getEpsilon());
        // save data of last round of post-epidemic stage
        this.dgData.setNetStatsPost(new NetworkStats(this.network));
        this.dgData.getNetStatsPost().setTiesBrokenWithInfectionPresent(this.tiesBrokenWithInfectionPresent);

        // end: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter.stopRecording();
        }

    }


    /**
     * Analyzes data.
     */
    private void anaylzeData() {
        try {
            // invocation of R-script
            ProcessBuilder pb = new ProcessBuilder(PropertiesHandler.getInstance().getRscriptPath(),
                    PropertiesHandler.getInstance().getRAnalysisFilePath(), FULL_DATA_EXPORT_PATH);
            logger.info("Starting analysis of simulated data. "
                    + "Invoking R-script: "
                    + pb.command().toString());
            Process p = pb.start();

            // status messages of R-script
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // wait for analysis to finish (blocking)
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                logger.info("Analysis finished successfully.");
            } else {
                logger.error("Analysis finished with error code: " + exitCode);
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
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
        if (this.dgData.getSimStats().getSimStage() == SimulationStage.ACTIVE_EPIDEMIC) {
            this.tiesBrokenWithInfectionPresent = true;
        }
    }

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
