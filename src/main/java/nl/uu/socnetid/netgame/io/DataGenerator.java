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

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.actors.ActorListener;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.io.network.GEXFWriter;
import nl.uu.socnetid.netgame.io.types.ActorParameters;
import nl.uu.socnetid.netgame.io.types.ActorProperties;
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
import nl.uu.socnetid.netgame.utilities.IRTC;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;


/**
 *
 * @author Hendrik Nunner
 */
public class DataGenerator implements ActorListener, SimulationListener {

    // logger
    private static final Logger logger = Logger.getLogger(DataGenerator.class);

    // simulations per unique parameter combination
    private static final int SIMS_PER_UPC = 1000;

    // network size
    private static final int[] NS = new int[] {5, 10, 15, 20, 25, 50};   //{5, 10, 15, 20, 25, 50, 75, 100};

    // utility
    private static final double[] ALPHAS = new double[] {10.0};
    private static final double[] BETAS  = new double[] {2.0, 8.0};
    private static final double[] CS     = new double[] {9.0}; //, 11.0};

    // disease
    private static final DiseaseType DISEASE_TYPE = DiseaseType.SIR;
    private static final int[]    TAUS   = new int[] {10};
    private static final double[] SS     = new double[] {2.0, 10.0, 50.0};
    private static final double[] GAMMAS = new double[] {0.1};
    private static final double[] MUS    = new double[] {1.0, 1.5};

    // risk behavior
    private static final double[] R_BOUNDS = new double[] {0.25, 1.75};

    // initial network
    private static final boolean[] START_WITH_EMPTY_NETWORKS = new boolean[] {true, false};

    // amount of rounds per simulation
    private static final int ROUNDS_PRE_EPIDEMIC = 150;
    private static final int ROUNDS_EPIDEMIC = 200;

    // share of peers an actor evaluates per round
    private static final double PHI = 0.4;

    // what kind of data to export
    private static final boolean EXPORT_SUMMARY_DATA = true;
    private static final boolean EXPORT_ROUND_SUMMARY_DATA = true;
    private static final boolean EXPORT_ACTOR_DETAIL_DATA = false;
    private static final boolean EXPORT_ACTOR_DETAIL_REDUCED_DATA = true;
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
    private double densityPre;
    private double avDegreePre;
    private double avClusteringPre;

    // export directory
    private final String exportDir;

    // GEXF export file
    private String gexfExportFile = "NA";
    private GEXFWriter gexfWriter;

    // CSV writers
    private FileWriter simulationSummaryCSVWriter;
    private FileWriter roundSummaryCSVWriter;
    private FileWriter actorDetailsCSVWriter;

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
        int upcs = NS.length * ALPHAS.length * BETAS.length * CS.length * TAUS.length
                * SS.length * GAMMAS.length * MUS.length * START_WITH_EMPTY_NETWORKS.length;

        // initialization of data export files
        initDataExportFiles();


        for (int N : NS) {
            for (double alpha : ALPHAS) {
                for (double beta : BETAS) {
                    for (double c : CS) {
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
                                                // random risk factor
                                                double minR = R_BOUNDS[0];
                                                double maxR = R_BOUNDS[1];
                                                double randR = minR + Math.random() * (maxR - minR);
                                                // create utility and disease specs
                                                UtilityFunction uf = new IRTC(alpha, beta, c);
                                                this.ds = new DiseaseSpecs(
                                                        DISEASE_TYPE, tau, s, gamma, mu);
                                                // add actors
                                                for (int i = 0; i < N; i++) {
                                                    Actor actor = network.addActor(uf, this.ds, randR, PHI);
                                                    actor.addActorListener(this);
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
                                                this.avClusteringPre = network.getAvClustering();
                                                // TODO improve:
                                                if (!EXPORT_ACTOR_DETAIL_DATA && EXPORT_ACTOR_DETAIL_REDUCED_DATA) {
                                                    logActorDetails(simulation);
                                                }
                                                // EPIDEMIC AND POST-EPIDEMIC STAGES
                                                network.infectRandomActor(ds);
                                                this.simStage = SimulationStage.ACTIVE_EPIDEMIC;
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
        // actor details
        if (EXPORT_ACTOR_DETAIL_DATA || EXPORT_ACTOR_DETAIL_REDUCED_DATA) {
            initActorDetailsCSV();
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
        simulationSummaryCSVCols.add(NetworkProperties.DENSITY_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.DENSITY_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_DEGREE_POST.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLUSTERING_PRE.toString());
        simulationSummaryCSVCols.add(NetworkProperties.AV_CLUSTERING_POST.toString());

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
        simulationSummaryCSVCols.add(String.valueOf(network.getAvR()));
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
        simulationSummaryCSVCols.add(Double.toString(this.densityPre));
        simulationSummaryCSVCols.add(Double.toString(network.getDensity()));
        simulationSummaryCSVCols.add(Double.toString(this.avDegreePre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvDegree()));
        simulationSummaryCSVCols.add(Double.toString(this.avClusteringPre));
        simulationSummaryCSVCols.add(Double.toString(network.getAvClustering()));

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
        roundSummaryCSVCols.add(String.valueOf(network.getAvR()));
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
     * Initializes the CSV file for actor detail data.
     */
    private void initActorDetailsCSV() {

        List<String> actorDetailsCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        actorDetailsCSVCols.add(SimulationParameters.UID.toString());
        actorDetailsCSVCols.add(SimulationParameters.UPC.toString());
        actorDetailsCSVCols.add(SimulationParameters.SIM.toString());
        // network
        actorDetailsCSVCols.add(NetworkParameters.N.toString());
        // actor
        actorDetailsCSVCols.add(ActorParameters.ID.toString());
        actorDetailsCSVCols.add(ActorParameters.R.toString());
        actorDetailsCSVCols.add(ActorParameters.ALPHA.toString());
        actorDetailsCSVCols.add(ActorParameters.BETA.toString());
        actorDetailsCSVCols.add(ActorParameters.C.toString());
        actorDetailsCSVCols.add(ActorParameters.TAU.toString());
        actorDetailsCSVCols.add(ActorParameters.S.toString());
        actorDetailsCSVCols.add(ActorParameters.GAMMA.toString());
        actorDetailsCSVCols.add(ActorParameters.MU.toString());

        // PROPERTIES
        // simulation
        actorDetailsCSVCols.add(SimulationProperties.SIM_ROUND.toString());
        actorDetailsCSVCols.add(SimulationProperties.SIM_STAGE.toString());
        // network
        actorDetailsCSVCols.add(NetworkProperties.STABLE.toString());
        actorDetailsCSVCols.add(NetworkProperties.DENSITY.toString());
        actorDetailsCSVCols.add(NetworkProperties.AV_DEGREE.toString());
        actorDetailsCSVCols.add(NetworkProperties.AV_CLUSTERING.toString());
        // actor
        actorDetailsCSVCols.add(ActorProperties.SATISFIED.toString());
        actorDetailsCSVCols.add(ActorProperties.UTIL.toString());
        actorDetailsCSVCols.add(ActorProperties.BENEFIT_DIST1.toString());
        actorDetailsCSVCols.add(ActorProperties.BENEFIT_DIST2.toString());
        actorDetailsCSVCols.add(ActorProperties.COSTS_DIST1.toString());
        actorDetailsCSVCols.add(ActorProperties.COSTS_DISEASE.toString());
        actorDetailsCSVCols.add(ActorProperties.DISEASE_STATE.toString());
        actorDetailsCSVCols.add(ActorProperties.DISEASE_ROUNDS_REMAINING.toString());
        actorDetailsCSVCols.add(ActorProperties.DEGREE1.toString());
        actorDetailsCSVCols.add(ActorProperties.DEGREE2.toString());
        actorDetailsCSVCols.add(ActorProperties.CLOSENESS.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_BROKEN_ACTIVE.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_BROKEN_PASSIVE.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_OUT_ACCEPTED.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_OUT_DECLINED.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_IN_ACCEPTED.toString());
        actorDetailsCSVCols.add(ActorProperties.CONS_IN_DECLINED.toString());

        try {
            this.actorDetailsCSVWriter = new FileWriter(this.exportDir + "actor-details.csv");
            CSVUtils.writeLine(this.actorDetailsCSVWriter, actorDetailsCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the actor detail data.
     *
     * @param simulation
     *          the simulation to log the actor details for.
     */
    private void logActorDetails(Simulation simulation) {
        Network network = simulation.getNetwork();
        List<Actor> actors = new LinkedList<Actor>(network.getActors());
        Collections.sort(actors);

        for (Actor actor : actors) {

            // a single CSV row
            List<String> actorDetailsCSVCols = new LinkedList<String>();

            // PARAMETERS
            // simulation
            actorDetailsCSVCols.add(this.uid);
            actorDetailsCSVCols.add(String.valueOf(this.upc));
            actorDetailsCSVCols.add(String.valueOf(this.simPerUpc));
            // network
            actorDetailsCSVCols.add(String.valueOf(network.getN()));
            // actor
            actorDetailsCSVCols.add(actor.getId());
            actorDetailsCSVCols.add(String.valueOf(actor.getRiskFactor()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getAlpha()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getBeta()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getC()));
            actorDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getTau()));
            actorDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getS()));
            actorDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getGamma()));
            actorDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getMu()));

            // PROPERTIES
            // simulation
            actorDetailsCSVCols.add(String.valueOf(simulation.getRounds()));
            actorDetailsCSVCols.add(String.valueOf(this.simStage));
            // network
            actorDetailsCSVCols.add(String.valueOf(network.isStable()));
            actorDetailsCSVCols.add(String.valueOf(network.getDensity()));
            actorDetailsCSVCols.add(String.valueOf(network.getAvDegree()));
            actorDetailsCSVCols.add(String.valueOf(network.getAvClustering()));
            // actor
            actorDetailsCSVCols.add(String.valueOf(actor.isSatisfied()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtility().getOverallUtility()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtility().getBenefitDirectConnections()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtility().getBenefitIndirectConnections()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtility().getCostsDirectConnections()));
            actorDetailsCSVCols.add(String.valueOf(actor.getUtility().getEffectOfDisease()));
            actorDetailsCSVCols.add(actor.getDiseaseGroup().name());
            if (actor.isInfected()) {
                actorDetailsCSVCols.add(String.valueOf(actor.getTimeUntilRecovered()));
            } else {
                actorDetailsCSVCols.add("NA");
            }
            actorDetailsCSVCols.add(String.valueOf(StatsComputer.computeFirstOrderDegree(actor)));
            actorDetailsCSVCols.add(String.valueOf(StatsComputer.computeSecondOrderDegree(actor)));
            actorDetailsCSVCols.add(String.valueOf(StatsComputer.computeCloseness(actor)));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesActive()));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesPassive()));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsOut()));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsOut()));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsIn()));
            actorDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsIn()));

            try {
                CSVUtils.writeLine(this.actorDetailsCSVWriter, actorDetailsCSVCols);
                this.actorDetailsCSVWriter.flush();
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
            if (EXPORT_ACTOR_DETAIL_DATA || EXPORT_ACTOR_DETAIL_REDUCED_DATA) {
                this.actorDetailsCSVWriter.flush();
                this.actorDetailsCSVWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeAdded(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Actor actor, String attribute, Object value) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeChanged(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Actor actor, String attribute, Object oldValue, Object newValue) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeRemoved(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Actor actor, String attribute) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.netgame.actors.Actor, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyConnectionRemoved(
     * nl.uu.socnetid.netgame.actors.Actor, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Actor actor, Edge edge) {
        if (this.simStage == SimulationStage.ACTIVE_EPIDEMIC) {
            this.tiesBrokenWithInfectionPresent = true;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (EXPORT_ROUND_SUMMARY_DATA) {
            logRoundSummaryCSV(simulation);
        }
        if (EXPORT_ACTOR_DETAIL_DATA) {
            logActorDetails(simulation);
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
        if (!EXPORT_ACTOR_DETAIL_DATA && EXPORT_ACTOR_DETAIL_REDUCED_DATA) {
            logActorDetails(simulation);
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
            if (EXPORT_ACTOR_DETAIL_DATA || EXPORT_ACTOR_DETAIL_REDUCED_DATA) {
                logActorDetails(simulation);
            }
        }
    }

}
