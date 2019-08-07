package nl.uu.socnetid.netgame.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.netgame.agents.Agent;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ThreadedSimulation extends Simulation {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ThreadedSimulation.class);

    // the executor agentExecutor used for the agent threads
    private ExecutorService agentExecutor;
    // agent concurrency
    private final ReentrantLock lock = new ReentrantLock();


    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     */
    public ThreadedSimulation(Network network) {
        super(network);
        this.agentExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.Simulation#computeAgentRound(
     * nl.uu.socnetid.netgame.agents.Agent)
     */
    @Override
    protected void computeAgentRound(Agent agent) {
        agent.setLock(this.lock);
        agentExecutor.submit(agent);
        // stop simulaiton if thread is interrupted
        if (Thread.currentThread().isInterrupted()) {
            this.pause();
        }
    }

}
