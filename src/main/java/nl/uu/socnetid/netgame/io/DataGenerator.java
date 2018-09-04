package nl.uu.socnetid.netgame.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private static final int SIMS_PRE_UPC = 10;

    // amount of rounds per simulation
    private static final int ROUNDS_PRE_EPIDEMIC = 40;
    private static final int ROUNDS_EPIDEMIC = 60;

    // what files to generate
    private static final boolean GENERATE_SUMMARY = true;
    private static final boolean GENERATE_ROUND_SUMMARY = true;
    private static final boolean GENERATE_AGENT_DETAILS_LAST_ROUND_ONLY = true;
    private static final boolean GENERATE_AGENT_DETAILS = false;
    private static final boolean GENERATE_GEXF = false;

    // simulation stage
    private SimulationStage simStage;
    private boolean tiesBrokenWithInfectionPresent;

    // simulation parameters
    private DiseaseSpecs ds;

    // duration of last wave of infection
    private int roundStartInfection;
    private int roundsLastInfection;

    // CSV writers
    private FileWriter summaryCSVWriter;
    private FileWriter roundSummaryCSVWriter;
    private FileWriter agentsDetailsCSVWriter;

    // unique parameter combination
    private int upc = 0;
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
    }

    /**
     * Generates data.
     */
    public void generateData() {

        // ---------- INITIALIZATIONS ---------- //

        // network size
        int[] Ns = new int[] {5, 10, 15};             //5, 10, 15};   //, 20, 25, 50, 75, 100};

        // utility
        double[] alphas = new double[] {10.0};
        double[] betas  = new double[] {2.0, 8.0};
        double[] cs     = new double[] {9.0}; //, 11.0};

        // disease
        DiseaseType diseaseType = DiseaseType.SIR;
        int[]    taus   = new int[] {10};
        double[] ss     = new double[] {2.0, 10.0, 50.0};
        double[] gammas = new double[] {0.1};
        double[] mus    = new double[] {1.0, 1.5};

        // risk behavior
        double[] rs = new double[] {0.0, 1.0, 2.0};

        // initial network
        boolean[] startWithEmptyNetworks = new boolean[] {true, false};

        // unique parameter combinations
        int upcs = Ns.length * alphas.length * betas.length * cs.length * taus.length
                * ss.length * gammas.length * mus.length * rs.length * startWithEmptyNetworks.length;

        // COUNTERS
        // simulation in total
        int sim = 0;


        // ---------- SIMULATION ---------- //
        try {
            // initialize export directory
            String exportDir = GEXFWriter.DEFAULT_EXPORT_DIR
                            + (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";

            // create export directory if it does not exist
            File directory = new File(exportDir);
            if (!directory.exists()){
                directory.mkdirs();
            }

            // summary CSV
            if (GENERATE_SUMMARY) {
                String summaryCSVPath = exportDir + "summary.csv";
                this.summaryCSVWriter = new FileWriter(summaryCSVPath);
                CSVUtils.writeLine(summaryCSVWriter, Arrays.asList(
                        "sim",
                        "N",
                        "alpha", "beta", "c",
                        "tau", "s", "gamma", "mu",
                        "r",
                        "network at start",
                        "unique parameter combination (upc)",
                        "sim per upc",
                        "susceptibles (last round)", "infected (last round)", "recovered (last round)",
                        "ties broken with infection present",
                        "duration of infection",
                        //"network type before infection present", "network type after infection present",
                        //"1st order density (pre-infection)", "2nd order density (pre-infection)",
                        //"1st order density (post-infection)", "2nd order density (post-infection)",
                        "density (pre-infection)",
                        "average degree (pre-infection)",
                        "average clustering (pre-infection)",
                        "density (post-infection)",
                        "average degree (post-infection)",
                        "average clustering (post-infection)",
                        "filename"
                        ));
            }

            if (GENERATE_ROUND_SUMMARY) {
                String roundSummaryCSVPath = exportDir + "round-summary.csv";
                this.roundSummaryCSVWriter = new FileWriter(roundSummaryCSVPath);
                List<String> roundSummaryCSVCols = new LinkedList<String>();
                // unique simulation id
                roundSummaryCSVCols.add("uid");
                // unique parameter combination
                roundSummaryCSVCols.add("upc.id");
                // simulation data
                roundSummaryCSVCols.add("sim.id");
                roundSummaryCSVCols.add("sim.round");
                roundSummaryCSVCols.add("sim.stage");
                // network data - parameters
                roundSummaryCSVCols.add("net.param.size");
                roundSummaryCSVCols.add("net.param.av.risk.factor");
                // disease data - parameters
                roundSummaryCSVCols.add("disease.param.s");
                // network data - stats
                // network data - stability
                roundSummaryCSVCols.add("net.stats.stable");
                roundSummaryCSVCols.add("net.stats.density");
                roundSummaryCSVCols.add("net.stats.av.degree");
                roundSummaryCSVCols.add("net.stats.av.clustering");
                // disease data - statistics
                roundSummaryCSVCols.add("dis.stats.pct.sus");
                roundSummaryCSVCols.add("dis.stats.pct.inf");
                roundSummaryCSVCols.add("dis.stats.pct.rec");
                CSVUtils.writeLine(this.roundSummaryCSVWriter, roundSummaryCSVCols);
            }

            if (GENERATE_AGENT_DETAILS || GENERATE_AGENT_DETAILS_LAST_ROUND_ONLY) {
                String agentsDetailsCSVPath = exportDir + "agents-details.csv";
                this.agentsDetailsCSVWriter = new FileWriter(agentsDetailsCSVPath);
                List<String> agentsDetailsCSVCols = new LinkedList<String>();
                // unique simulation id
                agentsDetailsCSVCols.add("uid");
                // unique parameter combination
                agentsDetailsCSVCols.add("upc.id");
                // simulation data
                agentsDetailsCSVCols.add("sim.id");
                agentsDetailsCSVCols.add("sim.round");
                agentsDetailsCSVCols.add("sim.stage");
                // network data - parameters
                agentsDetailsCSVCols.add("net.param.size");
                // actor data
                agentsDetailsCSVCols.add("act.id");
                // actor - parameter risk factor
                agentsDetailsCSVCols.add("act.param.risk.factor");
                // actor - parameters network
                agentsDetailsCSVCols.add("act.param.net.alpha");
                agentsDetailsCSVCols.add("act.param.net.beta");
                agentsDetailsCSVCols.add("act.param.net.c");
                // actor - parameters disease
                agentsDetailsCSVCols.add("act.param.dis.tau");
                agentsDetailsCSVCols.add("act.param.dis.s");
                agentsDetailsCSVCols.add("act.param.dis.gamma");
                agentsDetailsCSVCols.add("act.param.dis.mu");
                // actor - properties satisfaction
                agentsDetailsCSVCols.add("act.prop.satisfied");
                // actor - properties utility
                agentsDetailsCSVCols.add("act.prop.util.overall");
                agentsDetailsCSVCols.add("act.prop.util.benefit.distance.1");
                agentsDetailsCSVCols.add("act.prop.util.benefit.distance.2");
                agentsDetailsCSVCols.add("act.prop.util.costs.distance.1");
                agentsDetailsCSVCols.add("act.prop.util.costs.disease");
                // actor - properties disease
                agentsDetailsCSVCols.add("act.prop.dis.state");
                agentsDetailsCSVCols.add("act.prop.dis.rounds.until.recovered");
                // actor - stats network
                agentsDetailsCSVCols.add("act.stats.net.degree.order.1");
                agentsDetailsCSVCols.add("act.stats.net.degree.order.2");
                agentsDetailsCSVCols.add("act.stats.net.closeness");
                // actor - stats connections
                agentsDetailsCSVCols.add("act.stats.cons.broken.active");
                agentsDetailsCSVCols.add("act.stats.cons.broken.passive");
                agentsDetailsCSVCols.add("act.stats.cons.out.accepted");
                agentsDetailsCSVCols.add("act.stats.cons.out.declined");
                agentsDetailsCSVCols.add("act.stats.cons.in.accepted");
                agentsDetailsCSVCols.add("act.stats.cons.in.declined");
                CSVUtils.writeLine(this.agentsDetailsCSVWriter, agentsDetailsCSVCols);
            }

            for (int N : Ns) {
                for (double alpha : alphas) {
                    for (double beta : betas) {
                        for (double c : cs) {
                            for (int tau : taus) {
                                for (double s : ss) {
                                    for (double gamma : gammas) {
                                        for (double mu : mus) {
                                            for (double r : rs) {
                                                for (boolean empty : startWithEmptyNetworks) {
                                                    this.startWithEmptyNetwork = empty;

                                                    logger.info("Starting to compute "
                                                            + SIMS_PRE_UPC + " simulations for parameter combination: "
                                                            + ++this.upc + " / "
                                                            + upcs);

                                                    for (this.simPerUpc = 1; this.simPerUpc <= SIMS_PRE_UPC;
                                                            this.simPerUpc++) {
                                                        sim++;

                                                        // create network
                                                        Network network = new Network();

                                                        // add noise to risk factor
                                                        double min = rs[0];
                                                        double max = rs[2];
                                                        if (r < rs[1]) {
                                                            max = rs[1];
                                                        } else if (r > rs[1]) {
                                                            min = rs[1];
                                                        }
                                                        double randR = min + Math.random() * (max - min);

                                                        // TODO: refactor into own log method
                                                        GEXFWriter gexfWriter = new GEXFWriter();
                                                        String gexfFilename = "NA";
                                                        if (GENERATE_GEXF) {
                                                            // start recording GEXF file
                                                            StringBuilder sb = new StringBuilder(exportDir);
                                                            sb.append("sim-").append(sim).append("_");
                                                            sb.append("N-").append(N).append("_");
                                                            sb.append("alpha-").append(alpha).append("_");
                                                            sb.append("beta-").append(beta).append("_");
                                                            sb.append("c-").append(c).append("_");
                                                            sb.append("tau-").append(tau).append("_");
                                                            sb.append("s-").append(s).append("_");
                                                            sb.append("gamma-").append(gamma).append("_");
                                                            sb.append("mu-").append(mu).append("_");
                                                            sb.append("r-").append(randR).append("_");
                                                            sb.append("emptyNetwork-").append(
                                                                    this.startWithEmptyNetwork
                                                                    ? "yes" : "no").append("_");
                                                            sb.append("upc-").append(this.upc).append("_");
                                                            sb.append("simUpc-").append(this.simPerUpc);
                                                            sb.append(".gexf").toString();
                                                            gexfFilename = sb.toString();
                                                            gexfWriter.startRecording(network, gexfFilename);
                                                        }

                                                        // create utility and disease specs
                                                        UtilityFunction uf = new IRTC(alpha, beta, c);
                                                        this.ds = new DiseaseSpecs(
                                                                diseaseType, tau, s, gamma, mu);

                                                        // add actors
                                                        for (int i = 0; i < N; i++) {
                                                            Actor actor = network.addActor(uf, ds, randR);
                                                            actor.addActorListener(this);
                                                        }

                                                        // create full network if required
                                                        if (!this.startWithEmptyNetwork) {
                                                            network.createFullNetwork();
                                                        }

                                                        // simulate with no disease present
                                                        this.simStage = SimulationStage.PRE_EPIDEMIC;
                                                        this.tiesBrokenWithInfectionPresent = false;
                                                        Simulation simulation = new Simulation(network);
                                                        simulation.addSimulationListener(this);

                                                        // TODO think of a better way to make this more general
                                                        simulation.simulate(ROUNDS_PRE_EPIDEMIC);
                                                        //NetworkTypes networkTypeBeforeInfection = network.getType();
                                                        double densityPre = network.getDensity();
                                                        double avDegreePre = network.getAvDegree();
                                                        double avClusteringPre = network.getAvClustering();

                                                        // infect random actor
                                                        network.infectRandomActor(ds);
                                                        this.simStage = SimulationStage.ACTIVE_EPIDEMIC;
                                                        this.roundStartInfection = simulation.getRounds();

                                                        // simulate with disease present
                                                        // TODO think of a better way to make this more general
                                                        simulation.simulate(ROUNDS_EPIDEMIC);

                                                        //NetworkTypes networkTypeAfterInfection = network.getType();
                                                        double densityPost = network.getDensity();
                                                        double avDegreePost = network.getAvDegree();
                                                        double avClusteringPost = network.getAvClustering();

                                                        // stop recording GEXF file
                                                        if (GENERATE_GEXF) {
                                                            gexfWriter.stopRecording();
                                                        }

                                                        if (GENERATE_SUMMARY) {
                                                            // add result to overview CSV
                                                            CSVUtils.writeLine(this.summaryCSVWriter, Arrays.asList(
                                                                    Integer.toString(sim),
                                                                    Integer.toString(N),
                                                                    Double.toString(alpha),
                                                                    Double.toString(beta),
                                                                    Double.toString(c),
                                                                    Integer.toString(tau),
                                                                    Double.toString(s),
                                                                    Double.toString(gamma),
                                                                    Double.toString(mu),
                                                                    Double.toString(randR),
                                                                    this.startWithEmptyNetwork ? "empty" : "full",
                                                                            Integer.toString(this.upc),
                                                                            Integer.toString(this.simPerUpc),
                                                                            Integer.toString(network.getSusceptibles().size()),
                                                                            Integer.toString(network.getInfected().size()),
                                                                            Integer.toString(network.getRecovered().size()),
                                                                            this.tiesBrokenWithInfectionPresent ? "yes" : "no",
                                                                                    Integer.toString(this.roundsLastInfection),
                                                                                    //networkTypeBeforeInfection.toString(),
                                                                                    //networkTypeAfterInfection.toString(),
                                                                                    Double.toString(densityPre),
                                                                                    Double.toString(avDegreePre),
                                                                                    Double.toString(avClusteringPre),
                                                                                    Double.toString(densityPost),
                                                                                    Double.toString(avDegreePost),
                                                                                    Double.toString(avClusteringPost),
                                                                                    gexfFilename
                                                                    ));
                                                            this.summaryCSVWriter.flush();
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
            // at last: finalize CSVs
            if (GENERATE_SUMMARY) {
                this.summaryCSVWriter.flush();
                this.summaryCSVWriter.close();
            }
            if (GENERATE_ROUND_SUMMARY) {
                this.roundSummaryCSVWriter.flush();
                this.roundSummaryCSVWriter.close();
            }
            if (GENERATE_AGENT_DETAILS) {
                this.agentsDetailsCSVWriter.flush();
                this.agentsDetailsCSVWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /**
     * TODO comment
     *
     * @param simulation
     */
    private void logRoundSummary(Simulation simulation) {
        Network network = simulation.getNetwork();

        // a single CSV row
        List<String> roundSummaryCSVCols = new LinkedList<String>();

        // unique id
        roundSummaryCSVCols.add(String.valueOf(this.upc) + "-" + String.valueOf(this.simPerUpc));

        // unique parameter combination
        roundSummaryCSVCols.add(String.valueOf(this.upc));

        // simulation data
        roundSummaryCSVCols.add(String.valueOf(this.simPerUpc));
        roundSummaryCSVCols.add(String.valueOf(simulation.getRounds()));
        roundSummaryCSVCols.add(String.valueOf(this.simStage));

        // network data - parameters
        roundSummaryCSVCols.add(String.valueOf(network.getN()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvRiskFactor()));
        // disease data - parameters
        roundSummaryCSVCols.add(String.valueOf(this.ds.getS()));

        // network data - stats
        roundSummaryCSVCols.add(String.valueOf(network.isStable()));
        roundSummaryCSVCols.add(String.valueOf(network.getDensity()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvDegree()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvClustering()));

        // disease data - statistics
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
     * TODO comment
     *
     * @param simulation
     */
    private void logAgentsDetails(Simulation simulation) {
        Network network = simulation.getNetwork();

        List<Actor> actors = new LinkedList<Actor>(network.getActors());
        Collections.sort(actors);

        for (Actor actor : actors) {

            // a single CSV row
            List<String> agentsDetailsCSVCols = new LinkedList<String>();

            // unique id
            agentsDetailsCSVCols.add(String.valueOf(this.upc) + "-" + String.valueOf(this.simPerUpc));

            // unique parameter combination
            agentsDetailsCSVCols.add(String.valueOf(this.upc));

            // simulation data
            agentsDetailsCSVCols.add(String.valueOf(this.simPerUpc));
            agentsDetailsCSVCols.add(String.valueOf(simulation.getRounds()));
            agentsDetailsCSVCols.add(String.valueOf(this.simStage));

            // network data
            // network data - parameters
            agentsDetailsCSVCols.add(String.valueOf(network.getN()));

            // actor data
            agentsDetailsCSVCols.add(actor.getId());
            // actor - parameter risk factor
            agentsDetailsCSVCols.add(String.valueOf(actor.getRiskFactor()));
            // actor - parameters network
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getAlpha()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getBeta()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtilityFunction().getC()));
            // actor - parameters disease
            agentsDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getTau()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getS()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getGamma()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getMu()));
            // actor - properties satisfaction
            agentsDetailsCSVCols.add(String.valueOf(actor.isSatisfied()));
            // actor - properties utility
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtility().getOverallUtility()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtility().getBenefitDirectConnections()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtility().getBenefitIndirectConnections()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtility().getCostsDirectConnections()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getUtility().getEffectOfDisease()));
            // actor - properties disease
            agentsDetailsCSVCols.add(actor.getDiseaseGroup().name());
            if (actor.isInfected()) {
                agentsDetailsCSVCols.add(String.valueOf(actor.getTimeUntilRecovered()));
            } else {
                agentsDetailsCSVCols.add("NA");
            }
            // actor - stats network
            agentsDetailsCSVCols.add(String.valueOf(StatsComputer.computeFirstOrderDegree(actor)));
            agentsDetailsCSVCols.add(String.valueOf(StatsComputer.computeSecondOrderDegree(actor)));
            agentsDetailsCSVCols.add(String.valueOf(StatsComputer.computeCloseness(actor)));
            // actor - stats connections
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesActive()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesPassive()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsOut()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsOut()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsIn()));
            agentsDetailsCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsIn()));

            try {
                CSVUtils.writeLine(this.agentsDetailsCSVWriter, agentsDetailsCSVCols);
                this.agentsDetailsCSVWriter.flush();
            } catch (IOException e) {
                logger.error(e);
            }
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
    @SuppressWarnings("unused")
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (GENERATE_ROUND_SUMMARY) {
            logRoundSummary(simulation);
        }
        if (GENERATE_AGENT_DETAILS) {
            logAgentsDetails(simulation);
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
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.simStage == SimulationStage.POST_EPIDEMIC) {
            this.simStage = SimulationStage.FINISHED;
            if (GENERATE_ROUND_SUMMARY) {
                logRoundSummary(simulation);
            }
            if (GENERATE_AGENT_DETAILS || GENERATE_AGENT_DETAILS_LAST_ROUND_ONLY) {
                logAgentsDetails(simulation);
            }
        }
    }

}
