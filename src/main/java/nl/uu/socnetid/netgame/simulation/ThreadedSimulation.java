package nl.uu.socnetid.netgame.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ThreadedSimulation extends Simulation {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ThreadedSimulation.class);

    // the executor actorExecutor used for the actor threads
    private ExecutorService actorExecutor;
    // actor concurrency
    private final ReentrantLock lock = new ReentrantLock();


    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     */
    public ThreadedSimulation(Network network) {
        super(network);
        this.actorExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.Simulation#computeActorRound(
     * nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    protected void computeActorRound(Actor actor) {
        actor.setLock(this.lock);
        actorExecutor.submit(actor);
        // stop simulaiton if thread is interrupted
        if (Thread.currentThread().isInterrupted()) {
            this.pause();
        }
    }

}