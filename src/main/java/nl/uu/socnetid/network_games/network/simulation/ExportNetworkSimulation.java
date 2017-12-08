package nl.uu.socnetid.network_games.network.simulation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.network.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class ExportNetworkSimulation implements Runnable, Simulation {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ExportNetworkSimulation.class);

    // maximum amount of rounds
    private static final int MAX_ROUNDS = 50;

    // the network
    private Network network;
    private List<Player> players;

    // writer
    private String filePath;
    private String file;
    private NetworkWriter networkWriter;

    // listener
    private final Set<SimulationCompleteListener> listeners = new CopyOnWriteArraySet<SimulationCompleteListener>();

    // player concurrency
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     */
    public ExportNetworkSimulation(Network network) {
        this.network = network;
        this.players = this.network.getPlayers();
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        // initializations
        int currentRound = 0;
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // for !RE!-starting the simulation
        this.network.clearConnections();
        // loop while network is not stable and maximum simulation rounds not yet reached
        //while (!this.networkStable) {
        while (currentRound < MAX_ROUNDS) {

            // disease dynamics
            computeDiseaseDynamics();

            // player dynamics
            // players performing action in random order
            Collections.shuffle(this.players);

            // each player
            Iterator<Player> playersIt = this.players.iterator();
            while (playersIt.hasNext()) {
                Player currPlayer = playersIt.next();
                currPlayer.setLock(this.lock);
                service.submit(currPlayer);
            }
            currentRound++;
        }

        NetworkFileWriter fileWriter = new NetworkFileWriter(this.filePath, this.file,
                this.networkWriter, this.network);
        fileWriter.write();
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @param networkWriter the networkWriter to set
     */
    public void setNetworkWriter(NetworkWriter networkWriter) {
        this.networkWriter = networkWriter;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.Simulation#addListener(
     * nl.uu.socnetid.network_games.SimulationListener)
     */
    @Override
    public void addListener(SimulationCompleteListener listener) {
        this.listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.Simulation#removeListener(
     * nl.uu.socnetid.network_games.SimulationListener)
     */
    @Override
    public void removeListener(SimulationCompleteListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners of task completion.
     */
    private final void notifyListeners() {
        Iterator<SimulationCompleteListener> listenersIt = this.listeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notify(this);
        }
    }

    /**
     * Initializes the utility function.
     *
     * @param utilityFunction
     *          the utility function
     */
    public void initUtilityFunction(UtilityFunction utilityFunction) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().setUtilityFunction(utilityFunction);
        }
    }

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    public void computeDiseaseDynamics() {
        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();

            if (currPlayer.isInfected()) {
                currPlayer.fightDisease();

                if (currPlayer.isInfectious()) {
                    currPlayer.computeTransmissions();
                }
            }
        }
    }

}
