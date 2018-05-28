package nl.uu.socnetid.netgame.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
import nl.uu.socnetid.netgame.utilities.IRTC;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 *
 * @author Hendrik Nunner
 */
public class DataGenerator implements ActorListener, SimulationListener {

    // logger
    private static final Logger logger = Logger.getLogger(DataGenerator.class);

    // flag whether simulation has infection present
    private boolean infectionPresent;
    private boolean tiesBrokenWithInfectionPresent;

    // duration of last wave of infection
    private int roundStartInfection;
    private int roundsLastInfection;


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
        int[] Ns = new int[] {5, 10, 15, 20, 25, 50, 75, 100};

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

        // simulations per unique parameter combination
        int simsPerUpc = 100;

        // maximum rounds to simulate
        int maxRounds = 2000;

        // COUNTERS
        // simulation in total
        int sim = 0;
        // unique parameter combination
        int upc = 0;

        // initialize overview CSV
        String outputDir = GEXFWriter.DEFAULT_EXPORT_DIR
                        + (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
        String csvFile = outputDir + "summary.csv";

        // GEXF file generation?
        boolean gexfOutput = false;


        // ---------- SIMULATION ---------- //

        try {
            // create export directory if it does not exist
            File directory = new File(outputDir);
            if (!directory.exists()){
                directory.mkdirs();
            }

            FileWriter csvWriter = new FileWriter(csvFile);
            CSVUtils.writeLine(csvWriter, Arrays.asList(
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
                for (double alpha : alphas) {
                    for (double beta : betas) {
                        for (double c : cs) {
                            for (int tau : taus) {
                                for (double s : ss) {
                                    for (double gamma : gammas) {
                                        for (double mu : mus) {
                                            for (double r : rs) {
                                                for (boolean startWithEmptyNetwork : startWithEmptyNetworks) {

                                                    logger.info("Starting to compute "
                                                            + simsPerUpc + " simulations for parameter combination: "
                                                            + ++upc + " / "
                                                            + upcs);

                                                    for (int simPerUpc = 1; simPerUpc <= simsPerUpc; simPerUpc++) {
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
                                                            sb.append("sim-").append(++sim).append("_");
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
                                                                    startWithEmptyNetwork ? "yes" : "no").append("_");
                                                            sb.append("upc-").append(upc).append("_");
                                                            sb.append("simUpc-").append(simPerUpc);
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
                                                        if (!startWithEmptyNetwork) {
                                                            network.createFullNetwork();
                                                        }

                                                        // simulate with no disease present
                                                        this.infectionPresent = false;
                                                        this.tiesBrokenWithInfectionPresent = false;
                                                        Simulation simulation = new Simulation(network);
                                                        simulation.addSimulationListener(this);
                                                        simulation.simulate(maxRounds);
                                                        //NetworkTypes networkTypeBeforeInfection = network.getType();
                                                        double densityPre = network.getDensity();
                                                        double avDegreePre = network.getAvDegree();
                                                        double avClusteringPre = network.getAvClustering();

                                                        // infect random actor
                                                        network.infectRandomActor(ds);
                                                        this.infectionPresent = true;
                                                        this.roundStartInfection = simulation.getRounds();

                                                        // simulate with disease present
                                                        simulation.simulate(maxRounds);
                                                        //NetworkTypes networkTypeAfterInfection = network.getType();
                                                        double densityPost = network.getDensity();
                                                        double avDegreePost = network.getAvDegree();
                                                        double avClusteringPost = network.getAvClustering();

                                                        // stop recording GEXF file
                                                        if (gexfOutput) {
                                                            gexfWriter.stopRecording();
                                                        }

                                                        // add result to overview CSV
                                                        CSVUtils.writeLine(csvWriter, Arrays.asList(
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
                                                                startWithEmptyNetwork ? "empty" : "full",
                                                                Integer.toString(upc),
                                                                Integer.toString(simPerUpc),
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
                                                    // after each unique parameter combination: flush overview CSV
                                                    csvWriter.flush();
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
            // at last: finalize overview CSV
            csvWriter.flush();
            csvWriter.close();
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
        if (this.infectionPresent) {
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
    public void notifyRoundFinished(Simulation simulation) { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.roundsLastInfection = simulation.getRounds() - this.roundStartInfection;
    }
}
