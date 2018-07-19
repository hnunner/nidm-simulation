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

    // simulation stage
    private SimulationStage simStage;
    private boolean tiesBrokenWithInfectionPresent;

    // duration of last wave of infection
    private int roundStartInfection;
    private int roundsLastInfection;

    // micro CSV writer
    private FileWriter microCSVWriter;

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
        int[] Ns = new int[] {10};             //5, 10, 15};   //, 20, 25, 50, 75, 100};

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

        // simulations per unique parameter combination
        int simsPerUpc = 20;

        // maximum rounds to simulate
        int maxRounds = 25;

        // unique parameter combinations
        int upcs = Ns.length * alphas.length * betas.length * cs.length * taus.length
                * ss.length * gammas.length * mus.length * rs.length * startWithEmptyNetworks.length;

        // COUNTERS
        // simulation in total
        int sim = 0;

        // initialize overview CSV
        String outputDir = GEXFWriter.DEFAULT_EXPORT_DIR
                        + (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
        String overviewCSVPath = outputDir + "summary.csv";

        // GEXF file generation?
        boolean gexfOutput = false;


        // ---------- SIMULATION ---------- //

        try {
            // create export directory if it does not exist
            File directory = new File(outputDir);
            if (!directory.exists()){
                directory.mkdirs();
            }

            FileWriter overviewCSVWriter = new FileWriter(overviewCSVPath);
            CSVUtils.writeLine(overviewCSVWriter, Arrays.asList(
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

            for (int N : Ns) {
                String microCSVPath = outputDir + "micro-" + N + ".csv";
                this.microCSVWriter = new FileWriter(microCSVPath);
                List<String> microCSVCols = new LinkedList<String>();
                // unique simulation id
                microCSVCols.add("uid");
                // unique parameter combination
                microCSVCols.add("upc.id");
                // simulation data
                microCSVCols.add("sim.id");
                microCSVCols.add("sim.round");
                microCSVCols.add("sim.stage");
                // network data - stability
                microCSVCols.add("net.stable");
                // network data - statistics
                microCSVCols.add("net.stats.density");
                microCSVCols.add("net.stats.av.degree");
                microCSVCols.add("net.stats.av.clustering");
                // disease data - statistics
                microCSVCols.add("dis.stats.cnt.sus");
                microCSVCols.add("dis.stats.cnt.inf");
                microCSVCols.add("dis.stats.cnt.rec");
                // actor data
                microCSVCols.add("act.id");
                // actor - parameter risk factor
                microCSVCols.add("act.param.risk.factor");
                // actor - parameters network
                microCSVCols.add("act.param.net.alpha");
                microCSVCols.add("act.param.net.beta");
                microCSVCols.add("act.param.net.c");
                // actor - parameters disease
                microCSVCols.add("act.param.dis.tau");
                microCSVCols.add("act.param.dis.s");
                microCSVCols.add("act.param.dis.gamma");
                microCSVCols.add("act.param.dis.mu");
                // actor - properties satisfaction
                microCSVCols.add("act.prop.satisfied");
                // actor - properties utility
                microCSVCols.add("act.prop.util.overall");
                microCSVCols.add("act.prop.util.benefit.distance.1");
                microCSVCols.add("act.prop.util.benefit.distance.2");
                microCSVCols.add("act.prop.util.costs.distance.1");
                microCSVCols.add("act.prop.util.costs.disease");
                // actor - properties disease
                microCSVCols.add("act.prop.dis.state");
                microCSVCols.add("act.prop.dis.rounds.until.recovered");
                // actor - stats network
                microCSVCols.add("act.stats.net.degree.order.1");
                microCSVCols.add("act.stats.net.degree.order.2");
                microCSVCols.add("act.stats.net.closeness");
                // actor - stats connections
                microCSVCols.add("act.stats.cons.broken.active");
                microCSVCols.add("act.stats.cons.broken.passive");
                microCSVCols.add("act.stats.cons.out.accepted");
                microCSVCols.add("act.stats.cons.out.declined");
                microCSVCols.add("act.stats.cons.in.accepted");
                microCSVCols.add("act.stats.cons.in.declined");
                CSVUtils.writeLine(microCSVWriter, microCSVCols);

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
                                                            + simsPerUpc + " simulations for parameter combination: "
                                                            + ++this.upc + " / "
                                                            + upcs);

                                                    for (this.simPerUpc = 1; this.simPerUpc <= simsPerUpc;
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


                                                        GEXFWriter gexfWriter = new GEXFWriter();
                                                        String gexfFilename = "NA";
                                                        if (gexfOutput) {
                                                            // start recording GEXF file
                                                            StringBuilder sb = new StringBuilder(outputDir);
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
                                                        DiseaseSpecs ds = new DiseaseSpecs(
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

                                                        // TODO think of a better way to make this more general (10)
                                                        simulation.simulate(25);
                                                        //NetworkTypes networkTypeBeforeInfection = network.getType();
                                                        double densityPre = network.getDensity();
                                                        double avDegreePre = network.getAvDegree();
                                                        double avClusteringPre = network.getAvClustering();

                                                        // infect random actor
                                                        network.infectRandomActor(ds);
                                                        this.simStage = SimulationStage.ACTIVE_EPIDEMIC;
                                                        this.roundStartInfection = simulation.getRounds();

                                                        // simulate with disease present
                                                        // TODO think of a better way to make this more general (40)
                                                        simulation.simulate(50);

                                                        //NetworkTypes networkTypeAfterInfection = network.getType();
                                                        double densityPost = network.getDensity();
                                                        double avDegreePost = network.getAvDegree();
                                                        double avClusteringPost = network.getAvClustering();

                                                        // stop recording GEXF file
                                                        if (gexfOutput) {
                                                            gexfWriter.stopRecording();
                                                        }

                                                        // add result to overview CSV
                                                        CSVUtils.writeLine(overviewCSVWriter, Arrays.asList(
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
                                                    }


                                                    // after each unique parameter combination: flush CSVs
                                                    this.microCSVWriter.flush();
                                                    overviewCSVWriter.flush();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                this.microCSVWriter.flush();
                this.microCSVWriter.close();
            }
            // at last: finalize overview CSV
            overviewCSVWriter.flush();
            overviewCSVWriter.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /**
     * @param simulation
     */
    private void logMicroCSV(Simulation simulation) {
        Network network = simulation.getNetwork();

        List<Actor> actors = new LinkedList<Actor>(network.getActors());
        Collections.sort(actors);

        for (Actor actor : actors) {

            // a single CSV row
            List<String> microCSVCols = new LinkedList<String>();

            // unique id
            microCSVCols.add(String.valueOf(this.upc) + "-" + String.valueOf(this.simPerUpc));

            // unique parameter combination
            microCSVCols.add(String.valueOf(this.upc));

            // simulation data
            microCSVCols.add(String.valueOf(this.simPerUpc));
            microCSVCols.add(String.valueOf(simulation.getRounds()));
            microCSVCols.add(String.valueOf(this.simStage));

            // network data
            // network data - stability
            microCSVCols.add(String.valueOf(network.isStable()));
            // network data - statistics
            microCSVCols.add(String.valueOf(network.getDensity()));
            microCSVCols.add(String.valueOf(network.getAvDegree()));
            microCSVCols.add(String.valueOf(network.getAvClustering()));

            // disease data - statistics
            microCSVCols.add(String.valueOf(network.getSusceptibles().size()));
            microCSVCols.add(String.valueOf(network.getInfected().size()));
            microCSVCols.add(String.valueOf(network.getRecovered().size()));

            // actor data
            microCSVCols.add(actor.getId());
            // actor - parameter risk factor
            microCSVCols.add(String.valueOf(actor.getRiskFactor()));
            // actor - parameters network
            microCSVCols.add(String.valueOf(actor.getUtilityFunction().getAlpha()));
            microCSVCols.add(String.valueOf(actor.getUtilityFunction().getBeta()));
            microCSVCols.add(String.valueOf(actor.getUtilityFunction().getC()));
            // actor - parameters disease
            microCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getTau()));
            microCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getS()));
            microCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getGamma()));
            microCSVCols.add(String.valueOf(actor.getDiseaseSpecs().getMu()));
            // actor - properties satisfaction
            microCSVCols.add(String.valueOf(actor.isSatisfied()));
            // actor - properties utility
            microCSVCols.add(String.valueOf(actor.getUtility().getOverallUtility()));
            microCSVCols.add(String.valueOf(actor.getUtility().getBenefitDirectConnections()));
            microCSVCols.add(String.valueOf(actor.getUtility().getBenefitIndirectConnections()));
            microCSVCols.add(String.valueOf(actor.getUtility().getCostsDirectConnections()));
            microCSVCols.add(String.valueOf(actor.getUtility().getEffectOfDisease()));
            // actor - properties disease
            microCSVCols.add(actor.getDiseaseGroup().name());
            if (actor.isInfected()) {
                microCSVCols.add(String.valueOf(actor.getTimeUntilRecovered()));
            } else {
                microCSVCols.add("NA");
            }
            // actor - stats network
            microCSVCols.add(String.valueOf(StatsComputer.computeFirstOrderDegree(actor)));
            microCSVCols.add(String.valueOf(StatsComputer.computeSecondOrderDegree(actor)));
            microCSVCols.add(String.valueOf(StatsComputer.computeCloseness(actor)));
            // actor - stats connections
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesActive()));
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getBrokenTiesPassive()));
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsOut()));
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsOut()));
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getAcceptedRequestsIn()));
            microCSVCols.add(String.valueOf(actor.getConnectionStats().getDeclinedRequestsIn()));

            try {
                CSVUtils.writeLine(this.microCSVWriter, microCSVCols);
                this.microCSVWriter.flush();
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
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        logMicroCSV(simulation);
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
            logMicroCSV(simulation);
        }
    }

}
