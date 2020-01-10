/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ThreadedSimulation extends Simulation {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(ThreadedSimulation.class);

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
     * @see nl.uu.socnetid.nidm.simulation.Simulation#computeAgentRound(
     * nl.uu.socnetid.nidm.agents.Agent)
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
