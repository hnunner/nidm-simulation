package nl.uu.socnetid.netgame.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.netgame.agents.Agent;
import nl.uu.socnetid.netgame.agents.AgentListener;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.io.network.GEXFWriter;
import nl.uu.socnetid.netgame.io.types.AgentParameters;
import nl.uu.socnetid.netgame.io.types.AgentProperties;
import nl.uu.socnetid.netgame.io.types.DiseaseParameters;
import nl.uu.socnetid.netgame.io.types.DiseaseProperties;
import nl.uu.socnetid.netgame.io.types.NetworkParameters;
import nl.uu.socnetid.netgame.io.types.NetworkProperties;
import nl.uu.socnetid.netgame.io.types.SimulationParameters;
import nl.uu.socnetid.netgame.io.types.SimulationProperties;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.simulation.Simulation;
import nl.uu.socnetid.netgame.simulation.SimulationListener;
import nl.uu.socnetid.netgame.simulation.SimulationStage;
import nl.uu.socnetid.netgame.stats.StatsComputer;
import nl.uu.socnetid.netgame.utilities.CIDMo;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;


/**
 *
 * @author Hendrik Nunner
 */
public class DataGenerator implements AgentListener, SimulationListener {

    // logger
    private static final Logger logger = Logger.getLogger(DataGenerator.class);

    // simulations per unique parameter combination
    private static final int SIMS_PER_UPC = 100;

    // network size
    private static final int[] NS = new int[] {10, 15, 20, 25, 50};   //{5, 10, 15, 20, 25, 50, 75, 100};

    // utility
    private static final double[] ALPHAS = new double[] {10.0};
    private static final double   KAPPA  = 1.0;
    private static final double[] BETAS  = new double[] {2.0, 8.0};
    private static final double   LAMDA  = 1.0;
    private static final double[] CS     = new double[] {9.0}; //, 11.0};

    // disease
    private static final DiseaseType DISEASE_TYPE = DiseaseType.SIR;
    private static final int[]    TAUS   = new int[] {10};
    private static final double[] SS     = new double[] {2.0, 10.0, 50.0};
    private static final double[] GAMMAS = new double[] {0.1};
    private static final double[] MUS    = new double[] {1.0, 1.5};

    // risk behavior - RPi == RSigma!!!
    private static final double[] RS    = new double[] {0.5, 1.0, 1.5};

    // initial network
    private static final boolean[] START_WITH_EMPTY_NETWORKS = new boolean[] {true, false};

    // amount of rounds per simulation
    private static final int ROUNDS_PRE_EPIDEMIC = 150;
    private static final int ROUNDS_EPIDEMIC = 200;

    // share of peers an agent evaluates per round
    private static final double PHI = 0.4;

    // what kind of data to export
    private static final boolean EXPORT_SUMMARY_DATA = true;
    private static final boolean EXPORT_ROUND_SUMMARY_DATA = true;
    private static final boolean EXPORT_AGENT_DETAIL_DATA = false;
    private static final boolean EXPORT_AGENT_DETAIL_REDUCED_DATA = true;
    private static final boolean EXPORT_GEXF_DATA = false;

    // simulation stage
    private SimulationStage simStage;
    private boolean tiesBrokenWithInfectionPresent;

    // simulation parameters
    private DiseaseSpecs ds;

    // duration of last wave of infection
    private int roundStartInfection;
    private int roundsLastInfection;

    // pre-epidemic properties
    // network
    private double densityPre;
    private double avDegreePre;
    private double avDegree2Pre;
    private double avClosenessPre;
    private double avClusteringPre;
    private double avUtility;
    private double avBenefitDistance1;
    private double avBenefitDistance2;
    private double avCostsDistance1;
    private double avCostsDisease;
    // index case
    private double indexDegree1;
    private double indexDegree2;
    private double indexCloseness;
    private double indexClustering;
    private double indexUtility;
    private double indexBenefit1;
    private double indexBenefit2;
    private double indexCosts1;
    private double indexCostsDisease;

    // export directory
    private final String exportDir;

    // GEXF export file
    private String gexfExportFile = "NA";
    private GEXFWriter gexfWriter;

    // CSV writers
    private FileWriter simulationSummaryCSVWriter;
    private FileWriter roundSummaryCSVWriter;
    private FileWriter agentDetailsCSVWriter;

    // unique parameter combination
    private int upc = 0;
    private String uid;
    private int simPerUpc;
    private boolean startWithEmptyNetwork;


    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.generateData();
    }


    /**
     * Constructor.
     */
    public DataGenerator() {
        super();

        // initialize export directory
        this.exportDir = GEXFWriter.DEFAULT_EXPORT_DIR
                + (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";

        // create export directory if it does not exist
        File directory = new File(this.exportDir);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    /**
     * Generates data.
     */
    public void generateData() {

        // unique parameter combinations
        int upcs = NS.length * ALPHAS.length * BETAS.length * CS.length * RS.length * TAUS.length
                * SS.length * GAMMAS.length * MUS.length * START_WITH_EMPTY_NETWORKS.length;

        // initialization of data export files
        initDataExportFiles();


        for (int N : NS) {
            for (double alpha : ALPHAS) {
                for (double beta : BETAS) {
                    for (double c : CS) {
                        for (double r : RS) {
                            for (int tau : TAUS) {
                                for (double s : SS) {
                                    for (double gamma : GAMMAS) {
                                        for (double mu : MUS) {
                                            for (boolean empty : START_WITH_EMPTY_NETWORKS) {
                                                this.startWithEmptyNetwork = empty;

                                                logger.info("Starting to compute "
                                                        + SIMS_PER_UPC + " simulations for parameter combination: "
                                                        + ++this.upc + " / "
                                                        + upcs);

                                                for (this.simPerUpc = 1; this.simPerUpc <= SIMS_PER_UPC; this.simPerUpc++) {

                                                    // INITIALIZATIONS
                                                    // uid = "upc-sim"
                                                    this.uid = String.valueOf(this.upc) + "-" + String.valueOf(this.simPerUpc);
                                                    // create network
                                                    Network network = new Network();
                                                    // begin: GEXF export
                                                    if (EXPORT_GEXF_DATA) {
                                                        this.gexfWriter = new GEXFWriter();
                                                        this.gexfExportFile = this.exportDir + this.uid + ".gexf";
                                                        gexfWriter.startRecording(network, this.gexfExportFile);
                                                    }
                                                    // create utility and disease specs
                                                    UtilityFunction uf = new CIDMo(alpha, KAPPA, beta, LAMDA, c);
                                                    this.ds = new DiseaseSpecs(
                                                            DISEASE_TYPE, tau, s, gamma, mu);
                                                    // add agents - with RPi == RSigma!!!
                                                    for (int i = 0; i < N; i++) {
                                                        Agent agent = network.addAgent(uf, this.ds, r, r, PHI);
                                                        agent.addAgentListener(this);
                                                    }
                                                    // create full network if required
                                                    if (!this.startWithEmptyNetwork) {
                                                        network.createFullNetwork();
                                                    }

                                                    // SIMULATION
                                                    // PRE_EPIDEMIC STAGE
                                                    this.simStage = SimulationStage.PRE_EPIDEMIC;
                                                    this.tiesBrokenWithInfectionPresent = false;
                                                    Simulation simulation = new Simulation(network);
                                                    simulation.addSimulationListener(this);
                                                    simulation.simulate(ROUNDS_PRE_EPIDEMIC);
                                                    // save network properties of pre-epidemic stage
                                                    this.densityPre = network.getDensity();
                                                    this.avDegreePre = network.getAvDegree();
                                                    this.avDegree2Pre = network.getAvDegree2();
                                                    this.avClosenessPre = network.getAvCloseness();
                                                    this.avClusteringPre = network.getAvClustering();
                                                    this.avUtility = network.getAvUtility();
                                                    this.avBenefitDistance1 = network.getAvBenefitDistance1();
                                                    this.avBenefitDistance2 = network.getAvBenefitDistance2();
                                                    this.avCostsDistance1 = network.getAvCostsDistance1();
                                                    this.avCostsDisease = network.getAvCostsDisease();
                                                    // TODO improve:
                                                    if (!EXPORT_AGENT_DETAIL_DATA && EXPORT_AGENT_DETAIL_REDUCED_DATA) {
                                                        logAgentDetails(simulation);
                                                    }
                                                    // EPIDEMIC AND POST-EPIDEMIC STAGES
                                                    Agent indexCase = network.infectRandomAgent(ds);
                                                    this.simStage = SimulationStage.ACTIVE_EPIDEMIC;

                                                    // save index case properties of pre-epidemic stage
                                                    this.indexDegree1 = indexCase.getDegree();
                                                    this.indexDegree2 = indexCase.getSecondOrderDegree();
                                                    this.indexCloseness = indexCase.getCloseness();
                                                    this.indexClustering = indexCase.getClustering();
                                                    this.indexUtility = indexCase.getUtility().getOverallUtility();
                                                    this.indexBenefit1 = indexCase.getUtility().getBenefitDirectConnections();
                                                    this.indexBenefit2 = indexCase.getUtility().getBenefitIndirectConnections();
                                                    this.indexCosts1 = indexCase.getUtility().getCostsDirectConnections();
                                                    this.indexCostsDisease = indexCase.getUtility().getEffectOfDisease();

                                                    this.roundStartInfection = simulation.getRounds();
                                                    simulation.simulate(ROUNDS_EPIDEMIC);
                                                    // end: GEXF export
                                                    if (EXPORT_GEXF_DATA) {
                                                        this.gexfWriter.stopRecording();
                                                    }
                                                    // log simulation summary
                                                    if (EXPORT_SUMMARY_DATA) {
                                                        logSimulationSummaryCSV(simulation);
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
        finalizeDataExportFiles();
    }


    /**
     * Initializes the files for data export.
     */
    private void initDataExportFiles() {
        // summary CSV
        if (EXPORT_SUMMARY_DATA) {
            initSimulationSummaryCSV();
        }
        // round summary CSV
        if (EXPORT_ROUND_SUMMARY_DATA) {
            initRoundSummaryCSV();
        }
        // agent details
        if (EXPORT_AGENT_DETAIL_DATA || EXPORT_AGENT_DETAIL_REDUCED_DATA) {
            initAgentDetailsCSV();
        }
    }

    /**
     * Initializes the CSV file for simulation summary data.
     */
    private void initSimulationSummaryCSV() {

        List<String> simulationSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        simulationSummaryCSVCols.add(SimulationParameters.UID.toString());
        simulationSummaryCSVCols.add(SimulationParameters.UPC.toString());
        simulationSummaryCSVCols.add(SimulationParameters.SIM.toString());
        simulationSummaryCSVCols.add(SimulationParameters.NETWORK_STRUCTURE.toString());
        simulationSummaryCSVCols.add(SimulationParameters.GEXF_EXPORT_FILE.toString());
        // network
        simulationSummaryCSVCols.add(NetworkParameters.N.toString());
        simulationSummaryCSVCols.add(NetworkParameters.ALPHA.toString());
        simulationSummaryCSVCols.add(NetworkParameters.BETA.toString());
        simulationSummaryCSVCols.add(NetworkParameters.C.toString());
        simulationSummaryCSVCols.add(NetworkParameters.R.toString());
        // disease
        simulationSummaryCSVCols.add(DiseaseParameters.S.toString());
        simulationSummaryCSVCols.add(DiseaseParameters.GAMMA.toString());
        simulationSummaryCSVCols.add(DiseaseParameters.MU.toString());
        simulationSummaryCSVCols.add(DiseaseParameters.TAU.toString());

        // PROPERTIES
        // disease
        simulationSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_SUSCEPTIBLE.toString());
        simulationSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_INFECTED.toString());
        simulationSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_RECOVERED.toString());
        simulationSummaryCSVCols.add(DiseaseProperties.DURATION.toString());
        // network
        simulationSummaryCSVCols.add(NetworkProperties.TIES_BROKEN_EPIDEMIC.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE2_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE2_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLOSENESS_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLOSENESS_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLUSTERING_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLUSTERING_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_UTIL_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_UTIL_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_BENEFIT_DIST1_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_BENEFIT_DIST1_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_BENEFIT_DIST2_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_BENEFIT_DIST2_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_COSTS_DIST1_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_COSTS_DIST1_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_COSTS_DISEASE_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_COSTS_DISEASE_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.DENSITY_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.DENSITY_POST.toString());
        // index case
        simulationSummaryCSVCols.add(AgentProperties.DEGREE1.toString());
        simulationSummaryCSVCols.add(AgentProperties.DEGREE2.toString());
        simulationSummaryCSVCols.add(AgentProperties.CLOSENESS.toString());
        simulationSummaryCSVCols.add(AgentProperties.CLUSTERING.toString());
        simulationSummaryCSVCols.add(AgentProperties.UTIL.toString());
        simulationSummaryCSVCols.add(AgentProperties.BENEFIT_DIST1.toString());
        simulationSummaryCSVCols.add(AgentProperties.BENEFIT_DIST2.toString());
        simulationSummaryCSVCols.add(AgentProperties.COSTS_DIST1.toString());
        simulationSummaryCSVCols.add(AgentProperties.COSTS_DISEASE.toString());

        // FILE SYSTEM
        try {
            this.simulationSummaryCSVWriter = new FileWriter(this.exportDir + "simulation-summary.csv");
            CSVUtils.writeLine(this.simulationSummaryCSVWriter, simulationSummaryCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the summary data of a single simulation.
     *
     * @param simulation
     *          the simulation to be logged.
     */
    private void logSimulationSummaryCSV(Simulation simulation) {

        Network network = simulation.getNetwork();
        List<String> simulationSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        simulationSummaryCSVCols.add(this.uid);
        simulationSummaryCSVCols.add(String.valueOf(this.upc));
        simulationSummaryCSVCols.add(String.valueOf(this.simPerUpc));
        simulationSummaryCSVCols.add(String.valueOf(this.startWithEmptyNetwork ? 1 : 0));
        simulationSummaryCSVCols.add(gexfExportFile);
        // network
        simulationSummaryCSVCols.add(String.valueOf(network.getN()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvAlpha()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvBeta()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvC()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvRPi()));   // RPi == RSigma!!!
        // disease
        simulationSummaryCSVCols.add(String.valueOf(this.ds.getS()));
        simulationSummaryCSVCols.add(String.valueOf(this.ds.getGamma()));
        simulationSummaryCSVCols.add(String.valueOf(this.ds.getMu()));
        simulationSummaryCSVCols.add(String.valueOf(this.ds.getTau()));

        // PROPERTIES
        // disease
        double pct = 100D / network.getN();
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getSusceptibles().size()));
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getInfected().size()));
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getRecovered().size()));
        simulationSummaryCSVCols.add(Integer.toString(this.roundsLastInfection));
        // network
        simulationSummaryCSVCols.add(String.valueOf(this.tiesBrokenWithInfectionPresent ? 1 : 0));
        simulationSummaryCSVCols.add(Double.toString(this.avDegreePre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvDegree()));
        simulationSummaryCSVCols.add(Double.toString(this.avDegree2Pre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvDegree2()));
        simulationSummaryCSVCols.add(Double.toString(this.avClosenessPre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCloseness()));
        simulationSummaryCSVCols.add(Double.toString(this.avClusteringPre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvClustering()));
        simulationSummaryCSVCols.add(Double.toString(this.avUtility));
        simulationSummaryCSVCols.add(Double.toString(network.getAvUtility()));
        simulationSummaryCSVCols.add(Double.toString(this.avBenefitDistance1));
        simulationSummaryCSVCols.add(Double.toString(network.getAvBenefitDistance1()));
        simulationSummaryCSVCols.add(Double.toString(this.avBenefitDistance2));
        simulationSummaryCSVCols.add(Double.toString(network.getAvBenefitDistance2()));
        simulationSummaryCSVCols.add(Double.toString(this.avCostsDistance1));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCostsDistance1()));
        simulationSummaryCSVCols.add(Double.toString(this.avCostsDisease));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCostsDisease()));
        simulationSummaryCSVCols.add(Double.toString(this.densityPre));
        simulationSummaryCSVCols.add(Double.toString(network.getDensity()));
        // index case
        simulationSummaryCSVCols.add(Double.toString(this.indexDegree1));
        simulationSummaryCSVCols.add(Double.toString(this.indexDegree2));
        simulationSummaryCSVCols.add(Double.toString(this.indexCloseness));
        simulationSummaryCSVCols.add(Double.toString(this.indexClustering));
        simulationSummaryCSVCols.add(Double.toString(this.indexUtility));
        simulationSummaryCSVCols.add(Double.toString(this.indexBenefit1));
        simulationSummaryCSVCols.add(Double.toString(this.indexBenefit2));
        simulationSummaryCSVCols.add(Double.toString(this.indexCosts1));
        simulationSummaryCSVCols.add(Double.toString(this.indexCostsDisease));

        // FILE SYSTEM
        try {
            CSVUtils.writeLine(this.simulationSummaryCSVWriter, simulationSummaryCSVCols);
            this.simulationSummaryCSVWriter.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Initializes the CSV file for round summary data.
     */
    private void initRoundSummaryCSV() {

        List<String> roundSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        roundSummaryCSVCols.add(SimulationParameters.UID.toString());
        roundSummaryCSVCols.add(SimulationParameters.UPC.toString());
        roundSummaryCSVCols.add(SimulationParameters.SIM.toString());
        // network
        roundSummaryCSVCols.add(NetworkParameters.N.toString());
        roundSummaryCSVCols.add(NetworkParameters.BETA.toString());
        roundSummaryCSVCols.add(NetworkParameters.R.toString());
        // disease
        roundSummaryCSVCols.add(DiseaseParameters.S.toString());
        roundSummaryCSVCols.add(DiseaseParameters.MU.toString());

        // PROPERTIES
        // simulation
        roundSummaryCSVCols.add(SimulationProperties.SIM_ROUND.toString());
        roundSummaryCSVCols.add(SimulationProperties.SIM_STAGE.toString());
        // network
        roundSummaryCSVCols.add(NetworkProperties.STABLE.toString());
        roundSummaryCSVCols.add(NetworkProperties.DENSITY.toString());
        roundSummaryCSVCols.add(NetworkProperties.AV_DEGREE.toString());
        roundSummaryCSVCols.add(NetworkProperties.AV_CLUSTERING.toString());
        // disease
        roundSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_SUSCEPTIBLE.toString());
        roundSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_INFECTED.toString());
        roundSummaryCSVCols.add(DiseaseProperties.PERCENTAGE_RECOVERED.toString());

        // FILE SYSTEM
        try {
            this.roundSummaryCSVWriter = new FileWriter(this.exportDir + "round-summary.csv");
            CSVUtils.writeLine(this.roundSummaryCSVWriter, roundSummaryCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the summary data of a single simulation round.
     *
     * @param simulation
     *          the simulation to log the round for.
     */
    private void logRoundSummaryCSV(Simulation simulation) {

        Network network = simulation.getNetwork();
        List<String> roundSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        roundSummaryCSVCols.add(this.uid);
        roundSummaryCSVCols.add(String.valueOf(this.upc));
        roundSummaryCSVCols.add(String.valueOf(this.simPerUpc));
        // network
        roundSummaryCSVCols.add(String.valueOf(network.getN()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvBeta()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvRPi()));   // RPi == RSigma!!!
        // disease
        roundSummaryCSVCols.add(String.valueOf(this.ds.getS()));
        roundSummaryCSVCols.add(String.valueOf(this.ds.getMu()));

        // PROPERTIES
        // simulation
        roundSummaryCSVCols.add(String.valueOf(simulation.getRounds()));
        roundSummaryCSVCols.add(String.valueOf(this.simStage));
        // network
        roundSummaryCSVCols.add(String.valueOf(network.isStable()));
        roundSummaryCSVCols.add(String.valueOf(network.getDensity()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvDegree()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvClustering()));
        // disease
        double pct = 100D / network.getN();
        roundSummaryCSVCols.add(String.valueOf(pct * network.getSusceptibles().size()));
        roundSummaryCSVCols.add(String.valueOf(pct * network.getInfected().size()));
        roundSummaryCSVCols.add(String.valueOf(pct * network.getRecovered().size()));

        try {
            CSVUtils.writeLine(this.roundSummaryCSVWriter, roundSummaryCSVCols);
            this.roundSummaryCSVWriter.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Initializes the CSV file for agent detail data.
     */
    private void initAgentDetailsCSV() {

        List<String> agentDetailsCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        agentDetailsCSVCols.add(SimulationParameters.UID.toString());
        agentDetailsCSVCols.add(SimulationParameters.UPC.toString());
        agentDetailsCSVCols.add(SimulationParameters.SIM.toString());
        // network
        agentDetailsCSVCols.add(NetworkParameters.N.toString());
        // agent
        agentDetailsCSVCols.add(AgentParameters.ID.toString());
        agentDetailsCSVCols.add(AgentParameters.R.toString());
        agentDetailsCSVCols.add(AgentParameters.ALPHA.toString());
        agentDetailsCSVCols.add(AgentParameters.BETA.toString());
        agentDetailsCSVCols.add(AgentParameters.C.toString());
        agentDetailsCSVCols.add(AgentParameters.TAU.toString());
        agentDetailsCSVCols.add(AgentParameters.S.toString());
        agentDetailsCSVCols.add(AgentParameters.GAMMA.toString());
        agentDetailsCSVCols.add(AgentParameters.MU.toString());

        // PROPERTIES
        // simulation
        agentDetailsCSVCols.add(SimulationProperties.SIM_ROUND.toString());
        agentDetailsCSVCols.add(SimulationProperties.SIM_STAGE.toString());
        // network
        agentDetailsCSVCols.add(NetworkProperties.STABLE.toString());
        agentDetailsCSVCols.add(NetworkProperties.DENSITY.toString());
        agentDetailsCSVCols.add(NetworkProperties.AV_DEGREE.toString());
        agentDetailsCSVCols.add(NetworkProperties.AV_CLUSTERING.toString());
        // agent
        agentDetailsCSVCols.add(AgentProperties.SATISFIED.toString());
        agentDetailsCSVCols.add(AgentProperties.UTIL.toString());
        agentDetailsCSVCols.add(AgentProperties.BENEFIT_DIST1.toString());
        agentDetailsCSVCols.add(AgentProperties.BENEFIT_DIST2.toString());
        agentDetailsCSVCols.add(AgentProperties.COSTS_DIST1.toString());
        agentDetailsCSVCols.add(AgentProperties.COSTS_DISEASE.toString());
        agentDetailsCSVCols.add(AgentProperties.DISEASE_STATE.toString());
        agentDetailsCSVCols.add(AgentProperties.DISEASE_ROUNDS_REMAINING.toString());
        agentDetailsCSVCols.add(AgentProperties.DEGREE1.toString());
        agentDetailsCSVCols.add(AgentProperties.DEGREE2.toString());
        agentDetailsCSVCols.add(AgentProperties.CLOSENESS.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_BROKEN_ACTIVE.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_BROKEN_PASSIVE.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_OUT_ACCEPTED.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_OUT_DECLINED.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_IN_ACCEPTED.toString());
        agentDetailsCSVCols.add(AgentProperties.CONS_IN_DECLINED.toString());

        try {
            this.agentDetailsCSVWriter = new FileWriter(this.exportDir + "agent-details.csv");
            CSVUtils.writeLine(this.agentDetailsCSVWriter, agentDetailsCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the agent detail data.
     *
     * @param simulation
     *          the simulation to log the agent details for.
     */
    private void logAgentDetails(Simulation simulation) {
        Network network = simulation.getNetwork();
        List<Agent> agents = new LinkedList<Agent>(network.getAgents());
        Collections.sort(agents);

        for (Agent agent : agents) {

            // a single CSV row
            List<String> agentDetailsCSVCols = new LinkedList<String>();

            // PARAMETERS
            // simulation
            agentDetailsCSVCols.add(this.uid);
            agentDetailsCSVCols.add(String.valueOf(this.upc));
            agentDetailsCSVCols.add(String.valueOf(this.simPerUpc));
            // network
            agentDetailsCSVCols.add(String.valueOf(network.getN()));
            // agent
            agentDetailsCSVCols.add(agent.getId());
            agentDetailsCSVCols.add(String.valueOf(agent.getRPi()));   // RPi == RSigma!!!
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getAlpha()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getBeta()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getC()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getTau()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getS()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getGamma()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getMu()));

            // PROPERTIES
            // simulation
            agentDetailsCSVCols.add(String.valueOf(simulation.getRounds()));
            agentDetailsCSVCols.add(String.valueOf(this.simStage));
            // network
            agentDetailsCSVCols.add(String.valueOf(network.isStable()));
            agentDetailsCSVCols.add(String.valueOf(network.getDensity()));
            agentDetailsCSVCols.add(String.valueOf(network.getAvDegree()));
            agentDetailsCSVCols.add(String.valueOf(network.getAvClustering()));
            // agent
            agentDetailsCSVCols.add(String.valueOf(agent.isSatisfied()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getOverallUtility()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getBenefitDirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getBenefitIndirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getCostsDirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getEffectOfDisease()));
            agentDetailsCSVCols.add(agent.getDiseaseGroup().name());
            if (agent.isInfected()) {
                agentDetailsCSVCols.add(String.valueOf(agent.getTimeUntilRecovered()));
            } else {
                agentDetailsCSVCols.add("NA");
            }
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeFirstOrderDegree(agent)));
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeSecondOrderDegree(agent)));
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeCloseness(agent)));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getBrokenTiesActive()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getBrokenTiesPassive()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getAcceptedRequestsOut()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getDeclinedRequestsOut()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getAcceptedRequestsIn()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getDeclinedRequestsIn()));

            try {
                CSVUtils.writeLine(this.agentDetailsCSVWriter, agentDetailsCSVCols);
                this.agentDetailsCSVWriter.flush();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            if (EXPORT_SUMMARY_DATA) {
                this.simulationSummaryCSVWriter.flush();
                this.simulationSummaryCSVWriter.close();
            }
            if (EXPORT_ROUND_SUMMARY_DATA) {
                this.roundSummaryCSVWriter.flush();
                this.roundSummaryCSVWriter.close();
            }
            if (EXPORT_AGENT_DETAIL_DATA || EXPORT_AGENT_DETAIL_REDUCED_DATA) {
                this.agentDetailsCSVWriter.flush();
                this.agentDetailsCSVWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyAttributeAdded(
     * nl.uu.socnetid.netgame.agents.Agent, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Agent agent, String attribute, Object value) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyAttributeChanged(
     * nl.uu.socnetid.netgame.agents.Agent, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Agent agent, String attribute, Object oldValue, Object newValue) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyAttributeRemoved(
     * nl.uu.socnetid.netgame.agents.Agent, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Agent agent, String attribute) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.netgame.agents.Agent, nl.uu.socnetid.netgame.agents.Agent)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Agent agent1, Agent agent2) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyConnectionRemoved(
     * nl.uu.socnetid.netgame.agents.Agent, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Agent agent, Edge edge) {
        if (this.simStage == SimulationStage.ACTIVE_EPIDEMIC) {
            this.tiesBrokenWithInfectionPresent = true;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.agents.AgentListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.agents.Agent)
     */
    @Override
    public void notifyRoundFinished(Agent agent) { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (EXPORT_ROUND_SUMMARY_DATA) {
            logRoundSummaryCSV(simulation);
        }
        if (EXPORT_AGENT_DETAIL_DATA) {
            logAgentDetails(simulation);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.roundsLastInfection = simulation.getRounds() - this.roundStartInfection;
        this.simStage = SimulationStage.POST_EPIDEMIC;
        // TODO improve
        if (!EXPORT_AGENT_DETAIL_DATA && EXPORT_AGENT_DETAIL_REDUCED_DATA) {
            logAgentDetails(simulation);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.simStage == SimulationStage.POST_EPIDEMIC) {
            this.simStage = SimulationStage.FINISHED;
            if (EXPORT_ROUND_SUMMARY_DATA) {
                logRoundSummaryCSV(simulation);
            }
            if (EXPORT_AGENT_DETAIL_DATA || EXPORT_AGENT_DETAIL_REDUCED_DATA) {
                logAgentDetails(simulation);
            }
        }
    }

}
