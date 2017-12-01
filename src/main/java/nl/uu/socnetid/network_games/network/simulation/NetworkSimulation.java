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
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

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
        this.networkStable = false;
        int currentRound = 0;
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            // for !RE!-starting the simulation
            this.network.clearConnections();
            // loop while network is not stable and maximum simulation rounds not yet reached
            //while (!this.networkStable) {
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
                this.networkStable = allSatisfied;
                currentRound++;

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
        } finally {
            shutdown(service, currentRound);
        }
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

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.Simulation#getStatusMessage()
     */
    @Override
    public String getStatusMessage() {
        return this.networkStable ? "Network stable." : "Network instable.";
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
        logStopMessage(lastRound);
        notifyListeners();
    }

    /**
     * Logs a message at the end of the simulation.
     *
     * @param lastRound
     *          the round the simulation has stopped
     */
    private void logStopMessage(int lastRound) {
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation stopped in round ").append(lastRound).append(". Network was ");
        if (networkStable) {
            sb.append("stable.");
        } else {
            sb.append("not yet stable.");
        }
        logger.debug(sb.toString());
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

                if (currPlayer.isInfectious()) {
                    currPlayer.computeTransmissions();
                }
            }
        }
    }

}
