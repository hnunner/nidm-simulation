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
import nl.uu.socnetid.netgame.networks.NetworkTypes;
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

        // network size
        int[] Ns = new int[] {4};     //, 5, 6, 7, 8, 9, 10};

        // utility
        double[] alphas = new double[] {10.0};
        double[] betas  = new double[] {2.0, 8.0};
        double[] cs     = new double[] {9.0};

        // disease
        DiseaseType diseaseType = DiseaseType.SIR;
        int[]    taus   = new int[] {10};
        double[] deltas = new double[] {2.0, 10.0, 50.0};
        double[] gammas = new double[] {0.1};
        double[] mus    = new double[] {1.0, 1.5};

        // risk behavior
        double[] rs = new double[] {0.5, 1.0, 1.5};

        // initial network
        boolean[] startWithEmptyNetworks = new boolean[] {true, false};

        // unique parameter combinations
        int uniqueParameterCombinations = Ns.length * alphas.length * betas.length * cs.length * taus.length
                * deltas.length * gammas.length * mus.length * rs.length * startWithEmptyNetworks.length;
        int currParameterCombination = 1;

        // simulations per unique parameter combination
        int sims = 10;

        // maximum rounds to simulate
        int maxRounds = 500;


        // initialize overview CSV
        String outputDir = GEXFWriter.DEFAULT_EXPORT_DIR
                        + (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
        String csvFile = outputDir + "summary.csv";
        try {
            // create export directory if not existent
            File directory = new File(outputDir);
            if (!directory.exists()){
                directory.mkdirs();
            }

            FileWriter csvWriter = new FileWriter(csvFile);
            CSVUtils.writeLine(csvWriter, Arrays.asList(
                    "N",
                    "alpha", "beta", "c",
                    "tau", "delta", "gamma", "mu",
                    "r",
                    "network at start",
                    "sim",
                    "susceptibles (last round)", "infected (last round)", "recovered (last round)",
                    "ties broken with infection present",
                    "duration of infection",
                    "network type before infection present", "network type after infection present",
                    "filename"
                    ));

            for (int N : Ns) {
                for (double alpha : alphas) {
                    for (double beta : betas) {
                        for (double c : cs) {
                            for (int tau : taus) {
                                for (double delta : deltas) {
                                    for (double gamma : gammas) {
                                        for (double mu : mus) {
                                            for (double r : rs) {
                                                for (boolean startWithEmptyNetwork : startWithEmptyNetworks) {

                                                    logger.info("Starting to compute "
                                                            + sims + " simulations for parameter combination: "
                                                            + currParameterCombination++ + " / "
                                                            + uniqueParameterCombinations);

                                                    for (int sim = 1; sim <= sims; sim++) {
                                                        // create network
                                                        Network network = new Network();

                                                        // start recording GEXF file
                                                        GEXFWriter gexfWriter = new GEXFWriter();
                                                        StringBuilder sb = new StringBuilder(outputDir);
                                                        sb.append("N-").append(N).append("_");
                                                        sb.append("alpha-").append(alpha).append("_");
                                                        sb.append("beta-").append(beta).append("_");
                                                        sb.append("c-").append(c).append("_");
                                                        sb.append("tau-").append(tau).append("_");
                                                        sb.append("delta-").append(delta).append("_");
                                                        sb.append("gamma-").append(gamma).append("_");
                                                        sb.append("mu-").append(mu).append("_");
                                                        sb.append("r-").append(r).append("_");
                                                        sb.append("emptyNetwork-").append(
                                                                startWithEmptyNetwork ? "yes" : "no").append("_");
                                                        sb.append("sim-").append(sim);
                                                        sb.append(".gexf").toString();
                                                        String filename = sb.toString();
                                                        gexfWriter.startRecording(network, filename);

                                                        // create utility and disease specs
                                                        UtilityFunction uf = new IRTC(alpha, beta, c);
                                                        DiseaseSpecs ds = new DiseaseSpecs(
                                                                diseaseType, tau, delta, gamma, mu);

                                                        // add actors
                                                        for (int i = 0; i < N; i++) {
                                                            Actor actor = network.addActor(uf, ds, r);
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
                                                        logger.info("Starting to compute disease-free network for "
                                                                + "simulation " + sim + ".");
                                                        simulation.simulate(maxRounds);
                                                        NetworkTypes networkTypeBeforeInfection = network.getType();

                                                        // infect random actor
                                                        network.infectRandomActor(ds);
                                                        this.infectionPresent = true;
                                                        this.roundStartInfection = simulation.getRounds();

                                                        // simulate with disease present
                                                        logger.info("Starting to compute infected network for "
                                                                + "simulation " + sim + ".");
                                                        simulation.simulate(maxRounds);
                                                        NetworkTypes networkTypeAfterInfection = network.getType();

                                                        // stop recording GEXF file
                                                        gexfWriter.stopRecording();

                                                        // add result to overview CSV
                                                        CSVUtils.writeLine(csvWriter, Arrays.asList(
                                                                Integer.toString(N),
                                                                Double.toString(alpha),
                                                                Double.toString(beta),
                                                                Double.toString(c),
                                                                Integer.toString(tau),
                                                                Double.toString(delta),
                                                                Double.toString(gamma),
                                                                Double.toString(mu),
                                                                Double.toString(r),
                                                                startWithEmptyNetwork ? "empty" : "full",
                                                                Integer.toString(sim),
                                                                Integer.toString(network.getSusceptibles().size()),
                                                                Integer.toString(network.getInfected().size()),
                                                                Integer.toString(network.getRecovered().size()),
                                                                this.tiesBrokenWithInfectionPresent ? "yes" : "no",
                                                                Integer.toString(this.roundsLastInfection),
                                                                networkTypeBeforeInfection.toString(),
                                                                networkTypeAfterInfection.toString(),
                                                                filename
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
