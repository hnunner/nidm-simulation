package nl.uu.socnetid.networkgames.network.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ThreadedSimulation extends Simulation implements Runnable {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ThreadedSimulation.class);

    // the executor service used for the actor threads
    private ExecutorService service;
    // actor concurrency
    private final ReentrantLock lock = new ReentrantLock();


    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     * @param delay
     *          the delay in between actor moves
     */
    public ThreadedSimulation(Network network, int delay) {
        super(network, delay);
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        start();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.simulation.Simulation#computeActorRound(
     * nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    protected void computeActorRound(Actor actor) {

        actor.setLock(this.lock);
        service.submit(actor);

        // stop simulaiton if thread is interrupted
        if (Thread.currentThread().isInterrupted()) {
            this.pause();
        }
    }

}
