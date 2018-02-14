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

import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class NetworkSimulation implements Runnable, Simulation {

    // logger
    private static final Logger logger = Logger.getLogger(NetworkSimulation.class);

    // the network
    private Network network;
    private List<Player> players;
    // flag indicating whether all players are happy with their current connections
    private boolean networkStable = false;

    // simulation delay
    private int delay = 0;

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
    public NetworkSimulation(Network network) {
        this.network = network;
        this.players = this.network.getPlayers();
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        // initializations
        setNetworkStability(false);
        int currentRound = 0;
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        boolean notified = false;

        try {
            while (true) {

                // disease dynamics
                computeDiseaseDynamics();

                // player dynamics
                // players performing action in random order
                Collections.shuffle(this.players);

                // each player
                Iterator<Player> playersIt = this.players.iterator();
                while (playersIt.hasNext()) {

                    // some delay before each player moves (e.g., for animation processes)
                    try {
                        Thread.sleep(this.delay * 100);
                    } catch (InterruptedException e) {
                        return;
                    }

                    Player currPlayer = playersIt.next();
                    currPlayer.setLock(this.lock);
                    service.submit(currPlayer);
                }

                playersIt = this.players.iterator();
                boolean allSatisfied = true;
                while (playersIt.hasNext()) {
                    Player currPlayer = playersIt.next();
                    allSatisfied &= currPlayer.isSatisfied();
                }

                setNetworkStability(allSatisfied);
                currentRound++;

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                if (!notified && this.networkStable) {
                    notified = true;
                    notifyListeners();
                }

            }
        } finally {
            shutdown(service, currentRound);
        }
    }


    /**
     * Sets the network stability.
     *
     * @param stable
     *          flag whether network is stable or not
     */
    private void setNetworkStability(boolean stable) {
        this.networkStable = stable;
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
     * Routine to shuts down the simulation.
     *
     * @param service
     *          the service to be shut down
     * @param lastRound
     *          the round the simulation has stopped
     */
    private void shutdown(ExecutorService service, int lastRound) {
        service.shutdownNow();
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
     * Initializes the delay for the simulation.
     *
     * @param delay
     *          the delay
     */
    public void initSimulationDelay(int delay) {
        this.delay = delay;
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

                if (currPlayer.isInfected()) {
                    currPlayer.computeTransmissions();
                }
            }
        }
    }

}
