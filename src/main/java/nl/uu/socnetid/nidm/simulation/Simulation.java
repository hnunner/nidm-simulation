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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class Simulation implements Runnable {

    // logger
    private static final Logger logger = LogManager.getLogger(Simulation.class);
    // maximum number of rounds
    private static final int MAX_ROUNDS = 1000;

    // the network
    private Network network;
    // whether the network structure remains static while infection is present in network
    private boolean epStatic;
    // switch used to pause the simulation
    private boolean paused = false;
    // switch used to stop the simulation
    private boolean stopped = false;
    // simulation delay
    private int delay = 0;
    // rounds
    private int rounds = 1;
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
        this(network, true);
    }

    /**
     * Constructor.
     *
     * @param network
     *          the network as basis for the simulation
     * @param epStatic
     *          whether the network structure remains static while infection is present in network
     */
    public Simulation(Network network, boolean epStatic) {
        this.network = network;
        this.epStatic = epStatic;
        this.addSimulationListener(network);
    }

    /**
     * Sets the delay between agent moves.
     *
     * @param delay
     *          the delay between agent moves
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
     * Simulates the network dynamics (disease and agents) until the network is stable.
     *
     * @TODO generalize, as this method is very similar to simulate(int rounds)
     */
    public void simulate() {

        this.activeInfection = false;

        while (!this.network.isStable() && this.rounds <= MAX_ROUNDS && !this.stopped) {
            computeSingleRound();
        }

        // status message
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation finished after " + (rounds-1) + " time steps.");
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
            logger.debug(sb.toString());
        }

        // notify simulation finished
        this.notifySimulationFinished();
    }

    /**
     * Simulates the network dynamics (disease and agents) for a number of rounds.
     *
     * @param rounds
     *          the number of rounds
     */
    public void simulate(int rounds) {

        notifySimulationStarted();

        this.activeInfection = false;

        while (!this.network.isStable() && this.rounds <= rounds && !this.stopped) {
            computeSingleRound();
        }

        // status message
//        StringBuilder sb = new StringBuilder();
//        sb.append("Simulation finished after " + rounds + " time steps.");
//        boolean unfinished = false;
//        if (!this.network.isStable()) {
//            sb.append(" Network was unstable.");
//            unfinished = true;
//        }
//        if (this.network.hasActiveInfection()) {
//            sb.append(" Network had active infection.");
//            unfinished = true;
//        }
//        if (unfinished) {
//            logger.warn(sb.toString());
//        } else {
//            logger.debug(sb.toString());
//        }

        // notify simulation finished
        this.notifySimulationFinished();
    }

    /**
     * Simulates the network dynamics (disease and agents) until the network is stable.
     *
     * @param maxRounds
     *          the maximum number of rounds
     *
     * TODO: generalize with method above
     */
    public void simulateUntilStable(int maxRounds) {

        notifySimulationStarted();

        this.activeInfection = false;
        this.rounds = 1;

        while ((!this.network.isStable() || this.network.hasActiveInfection())
                && this.rounds <= maxRounds
                && !this.stopped) {
            computeSingleRound();
//            logger.debug("round " + (this.rounds) + ": finished");
        }

        // status message
        StringBuilder sb = new StringBuilder();
        sb.append("Simulation finished after " + (this.rounds-1) + " time steps.");
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
            logger.debug(sb.toString());
        }

        // notify simulation finished
        this.notifySimulationFinished();
    }

    /**
     * Computes a single round,
     * composed of disease and agent dynamics.
     */
    private void computeSingleRound() {

        if (this.network.hasActiveInfection()) {
            computeDiseaseDynamics();
        }

        // first round with active infection
        if (this.network.hasActiveInfection() && !this.activeInfection) {
            this.activeInfection = true;
        }
        // first round of defeated infection
        if (this.activeInfection && !this.network.hasActiveInfection()) {
            this.activeInfection = false;
            notifyInfectionDefeated();
        }
        if (!this.network.hasActiveInfection() || (this.network.hasActiveInfection() && !this.epStatic)) {
            computeAgentDynamics();
        }

        // notifications
        notifyRoundFinished();

        this.rounds++;
    }

    /**
     * Pauses the simulation
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Pauses the simulation
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    private void computeDiseaseDynamics() {

        // random order
        List<Agent> agents = new ArrayList<Agent>(this.network.getAgents());
        Collections.shuffle(agents);

        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            if (this.paused) {
                return;
            }
            Agent agent = agentsIt.next();
            if (agent.isInfected()) {
                agent.fightDisease();
            }
            agent.computeDiseaseTransmission();
        }
    }

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    private void computeAgentDynamics() {

        List<Agent> agents = new ArrayList<Agent>(this.network.getAgents());
        Collections.shuffle(agents);
        Set<Agent> agentsAll = new HashSet<Agent>(agents);
        Set<Agent> agentsToProcess = new HashSet<Agent>(this.network.getN());
        Set<String> agentsProcessed = new HashSet<String>(this.network.getN());

        while (!agentsAll.isEmpty()) {

            // external interrupts
            if (this.paused || this.stopped) {
                return;
            }

            // randomly select an unconnected and unprocessed agent
            // if no connected agents (left) to process
            if (agentsToProcess.isEmpty()) {
                agentsToProcess.add(agentsAll.iterator().next());
            }

            // PROCESS AGENT
            Agent agent = agentsToProcess.iterator().next();
            computeAgentRound(agent, this.delay);
            // list updates
            agentsProcessed.add(agent.getId());
            agentsToProcess.remove(agent);
            agentsAll.remove(agent);

            // add connections
            List<Agent> connections = agent.getConnections();
            if (!connections.isEmpty()) {
                Iterator<Agent> connectionsIt = connections.iterator();
                for (int i = 0; i < connections.size(); i++) {
                    Agent connection = connectionsIt.next();
                    if (!agentsProcessed.contains(connection.getId())) {
                        agentsToProcess.add(connection);
                    }
                }
            }
        }
    }

    /**
     * Computes a single round of play for a given {@link Agent}.
     *
     * @param agent
     *          the {@link Agent} to compute the single round of play for
     */
    protected void computeAgentRound(Agent agent) {
        agent.computeRound();
    }

    /**
     * Computes a single round of play for a given {@link Agent}.
     *
     * @param agent
     *          the {@link Agent} to compute the single round of play for
     * @param delay
     *          the delay between each network decision
     */
    protected void computeAgentRound(Agent agent, int delay) {
        agent.computeRound(delay);
    }

    /**
     * Checks whether the simulation is running.
     *
     * @return true if the simulation is running, false otherwise
     */
    public boolean isRunning() {
        return !this.paused && !this.stopped;
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
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @return the epStatic
     */
    public boolean isEpStatic() {
        return epStatic;
    }

    /**
     * @param epStatic the epStatic to set
     */
    public void setEpStatic(boolean epStatic) {
        this.epStatic = epStatic;
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
     * Notifies listeners of simulation start.
     */
    private final void notifySimulationStarted() {
        Iterator<SimulationListener> listenersIt = this.simulationListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifySimulationStarted(this);
        }
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

    /**
     * Notifies listeners of finished simulation.
     */
    private final void notifySimulationFinished() {
        Iterator<SimulationListener> listenersIt = this.simulationListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifySimulationFinished(this);
        }
    }

}
