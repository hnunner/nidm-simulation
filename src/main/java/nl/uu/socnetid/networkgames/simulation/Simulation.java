package nl.uu.socnetid.networkgames.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class Simulation implements Runnable {

    // logger
    private static final Logger logger = Logger.getLogger(Simulation.class);

    // the network
    private Network network;
    // switch used to stop the simulation
    private boolean paused = false;
    // simulation delay
    private int delay = 0;
    // rounds
    private int rounds = 0;
    // flag whether network has active infection or not
    private boolean activeInfection;

    // listeners
    private final Set<SimulationListener> simulationListeners =
            new CopyOnWriteArraySet<SimulationListener>();


    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     */
    public Simulation(Network network) {
        this.network = network;
    }

    /**
     * Sets the delay between actor moves.
     *
     * @param delay
     *          the delay between actor moves
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        this.paused = false;
        this.activeInfection = false;
        while (!this.paused) {
            computeSingleRound();
        }
    }

    /**
     * Simulates the network dynamics (disease and actors) until either:
     * the network is stable and a possible disease is eradicated,
     * or until a maximum number of rounds has been simulated.
     *
     * @param maxRounds
     *          the maximum number of rounds
     */
    public void simulate(int maxRounds) {
        int safetyMargin = 3;
        int stableRounds = 0;
        this.activeInfection = false;

        for (int i = 0; i < maxRounds; i++) {
            logger.info("Starting to compute round " + i + ".");
            computeSingleRound();

            if (network.isStable()) {
                stableRounds++;
                // to be on the safe side: network needs to be stable for three consecutive rounds
                if (stableRounds >= safetyMargin) {
                    if (!this.network.hasActiveInfection()) {
                        return;
                    }
                }
            } else {
                stableRounds = 0;
            }
        }

        // status message
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation finished after maximum number of rounds (" + maxRounds + ").");
        boolean unfinished = false;
        if (!this.network.isStable()) {
            sb.append(" Network was unstable.");
            unfinished = true;
        }
        if (this.network.hasActiveInfection()) {
            sb.append(" Network had active infection.");
            unfinished = true;
        }
        if (unfinished) {
            logger.warn(sb.toString());
        } else {
            logger.info(sb.toString());
        }
    }

    /**
     * Computes a single round,
     * composed of disease and actor dynamics.
     */
    private void computeSingleRound() {

        computeDiseaseDynamics();
        computeActorDynamics();

        this.rounds++;

        // notifications
        notifyRoundFinished();

        // first round with active infection
        if (this.network.hasActiveInfection() && !this.activeInfection) {
            this.activeInfection = true;
        }

        // first round of defeated infection
        if (this.activeInfection && !this.network.hasActiveInfection()) {
            this.activeInfection = false;
            notifyInfectionDefeated();
        }
    }

    /**
     * Pauses the simulation
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    private void computeDiseaseDynamics() {

        // random order
        List<Actor> actors = new ArrayList<Actor>(this.network.getActors());
        Collections.shuffle(actors);

        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            if (this.paused) {
                return;
            }
            Actor actor = actorsIt.next();
            if (actor.isInfected()) {
                actor.fightDisease();
            }
            actor.computeDiseaseTransmission();
        }
    }

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    private void computeActorDynamics() {

        // random order
        List<Actor> actors = new ArrayList<Actor>(this.network.getActors());
        Collections.shuffle(actors);

        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            if (this.paused) {
                return;
            }
            if (this.delay > 0) {
                // some delay before each actor moves (e.g., for animation processes)
                try {
                    Thread.sleep(this.delay * 10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            computeActorRound(actorsIt.next());
        }
    }

    /**
     * Computes a single round of play for a given {@link Actor}.
     *
     * @param actor
     *          the {@link Actor} to compute the single round of play for
     */
    protected void computeActorRound(Actor actor) {
        actor.computeRound();
    }

    /**
     * Checks whether the simulation is running.
     *
     * @return true if the simulation is running, false otherwise
     */
    public boolean isRunning() {
        return !this.paused;
    }

    /**
     * Gets the amount of rounds that have been simulated so far.
     *
     * @return the amount of rounds
     */
    public int getRounds() {
        return this.rounds;
    }

    /**
     * Adds a listener for simulation notifications.
     *
     * @param simulationListener
     *          the listener to be added
     */
    public void addSimulationListener(SimulationListener simulationListener) {
        this.simulationListeners.add(simulationListener);
    }

    /**
     * Removes a listener for simulation notifications.
     *
     * @param simulationListener
     *          the listener to be removed
     */
    public void removeSimulationListener(SimulationListener simulationListener) {
        this.simulationListeners.remove(simulationListener);
    }

    /**
     * Notifies listeners of finished simulation rounds.
     */
    private final void notifyRoundFinished() {
        Iterator<SimulationListener> listenersIt = this.simulationListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRoundFinished(this);
        }
    }

    /**
     * Notifies listeners of defated infections.
     */
    private final void notifyInfectionDefeated() {
        Iterator<SimulationListener> listenersIt = this.simulationListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyInfectionDefeated(this);
        }
    }

}
