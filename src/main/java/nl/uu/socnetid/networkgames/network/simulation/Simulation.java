package nl.uu.socnetid.networkgames.network.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class Simulation implements Runnable {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(Simulation.class);

    // the network
    private Network network;
    // switch used to stop the simulation
    private boolean paused = false;
    // simulation delay
    private int delay = 0;


    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     * @param delay
     *          the delay in between actor moves
     */
    public Simulation(Network network, int delay) {
        this.network = network;
        this.delay = delay;
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        this.paused = false;
        while (!this.paused) {
            computeDiseaseDynamics();
            computeActorDynamics();
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
            // some delay before each actor moves (e.g., for animation processes)
            try {
                Thread.sleep(this.delay * 100);
            } catch (InterruptedException e) {
                return;
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

}
