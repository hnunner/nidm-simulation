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
package nl.uu.socnetid.nidm.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentAttributes;
import nl.uu.socnetid.nidm.agents.AgentFactory;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.stats.DijkstraShortestPath;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class Network extends SingleGraph implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(Network.class);

    // risk factor for risk neutral agents
    private static final double RISK_FACTOR_NEUTRAL = 1.0;

    // standard share to evaluate per agent
    private static final double STANDARD_PHI = 0.4;

    // standard proportion of direct ties to evaluate per agent
    private static final double STANDARD_PSI = 0.25;

    // standard share to select assortatively
    private static final double STANDARD_OMEGA = 0.0;

    // standard shuffling of assortatively selected co-agents
    private static final boolean STANDARD_OMEGA_SHUFFLE = true;

    // listener
    private final Set<NetworkListener> networkListeners = new CopyOnWriteArraySet<NetworkListener>();

    // flag for arranging agents in circle
    protected boolean arrangeInCircle;

    // stability
    private int timestepsStable = 0;
    private static final int TIMESTEPS_REQUIRED_FOR_STABILITY = 1;

    /**
     * Constructor.
     */
    public Network() {
        this("Network of the Infectious Kind", false);
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     */
    public Network(String id) {
        this(id, false);
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     * @param arrangeInCircle
     *          flag whether agents to arrange in circle or not
     */
    public Network(String id, boolean arrangeInCircle) {
        super(id);
        this.arrangeInCircle = arrangeInCircle;
        this.setNodeFactory(new AgentFactory());
    }


    /**
     * Creates and adds an agent to the network.
     *
     * @param utilityFunction
     *          the agent's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return this.addAgent(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL, RISK_FACTOR_NEUTRAL, STANDARD_PHI,
                STANDARD_OMEGA, STANDARD_OMEGA_SHUFFLE, STANDARD_PSI);
    }

    /**
     * Creates and adds an agent to the network.
     *
     * @param utilityFunction
     *          the agent's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param rSigma
     *          the factor describing how the agent perceives severity of diseases:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param rPi
     *          the factor describing how the agent perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param phi
     *          the share of peers an agent evaluates per round
     * @param omega
     *          the share of peers to select assortatively
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi,
            double omega) {
        return this.addAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi, omega, STANDARD_OMEGA_SHUFFLE, STANDARD_PSI);
    }

    /**
     * Creates and adds an agent to the network.
     *
     * @param utilityFunction
     *          the agent's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param rSigma
     *          the factor describing how the agent perceives severity of diseases:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param rPi
     *          the factor describing how the agent perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param phi
     *          the share of peers an agent evaluates per round
     * @param omega
     *          the share of peers to select assortatively
     * @param psi
     *          the proportion of direct ties an agent evaluates per round
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi,
            double omega, double psi) {
        return this.addAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi, omega, STANDARD_OMEGA_SHUFFLE, psi);
    }

    /**
     * Creates and adds an agent to the network.
     *
     * @param utilityFunction
     *          the agent's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param rSigma
     *          the factor describing how the agent perceives severity of diseases:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param rPi
     *          the factor describing how the agent perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param phi
     *          the share of peers an agent evaluates per round
     * @param omega
     *          the share of peers to select assortatively
     * @param omegaShuffle
     *          whether assortatively selected co-agents ought to be shuffled before processing
     * @param psi
     *          the proportion of direct ties an agent evaluates per round
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi,
            double omega, boolean omegaShuffle, double psi) {
        Agent agent = this.addNode(String.valueOf(this.getNodeCount() + 1));
        agent.initAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi, psi, omega, omegaShuffle);
        notifyAgentAdded(agent);

        // re-position agents if auto-layout is disabled
        if (this.arrangeInCircle) {
            arrangeAgentsInCircle();
        }

        return agent;
    }

    /**
     * Removes a agent from the network.
     *
     * @return the id of the removed agent
     */
    public String removeAgent() {
        if (this.getAgents().size() == 0) {
            return null;
        }
        String agentId = this.getLastAgent().getId();
        this.removeNode(String.valueOf(agentId));
        notifyAgentRemoved(agentId);

        // re-position agents if auto-layout is disabled
        if (!this.arrangeInCircle) {
            arrangeAgentsInCircle();
        }

        return agentId;
    }

    /**
     * Arranges all agent in a circle.
     */
    private void arrangeAgentsInCircle() {
        Collection<Agent> agents = getAgents();
        int n = agents.size();

        Iterator<Agent> agentsIt = agents.iterator();
        int i = 0;

        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();
            currAgent.setXY(
                    1 * Math.cos(i * 2 * Math.PI / n),
                    1 * Math.sin(i * 2 * Math.PI / n));

            i++;
        }
    }

    /**
     * Creates the full network based on the agents available.
     */
    public void createFullNetwork() {
        if (this.getAgents().size() == 0) {
            return;
        }

        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            agent.connectToAll();
        }
    }

    /**
     * Gets the agent with the corresponding identifier.
     *
     * @param id
     *          the identifier of the agent to get
     * @return the agent with the corresponding identifier
     */
    public Agent getAgent(String id) {
        return (Agent) this.getNode(id);
    }

    /**
     * Gets a random agent.
     *
     * @return a random agent
     */
    public Agent getRandomAgent() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, getAgents().size());
        return (Agent) this.getNode(randomIndex);
    }

    /**
     * Gets a random agent that is not infected.
     *
     * @return a random not infected agent
     */
    public Agent getRandomNotInfectedAgent() {
        List<Agent> tmpAgents = new LinkedList<Agent>(this.getAgents());
        while (!tmpAgents.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, tmpAgents.size());
            Agent agent = tmpAgents.get(randomIndex);
            if (!agent.isInfected()) {
                return agent;
            }
            tmpAgents.remove(agent);
        }
        return null;
    }

    /**
     * Gets a random susceptible agent.
     *
     * @return a random susceptible agent
     */
    public Agent getRandomSusceptibleAgent() {
        List<Agent> tmpAgents = new LinkedList<Agent>(this.getAgents());
        while (!tmpAgents.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, tmpAgents.size());
            Agent agent = tmpAgents.get(randomIndex);
            if (agent.isSusceptible()) {
                return agent;
            }
            tmpAgents.remove(agent);
        }
        return null;
    }

    /**
     * Gets the agent with the highest index.
     *
     * @return the agent with the highest index
     */
    public Agent getLastAgent() {
        return this.getNode(this.getNodeSet().size() - 1);
    }

    /**
     * Gets all agents within the network.
     *
     * @return all agents within the network.
     */
    public Collection<Agent> getAgents() {
        return this.getNodeSet();
    }

    /**
     * Gets the number of agents in the network.
     *
     * @return the number of agent in the network.
     */
    public int getN() {
        return this.getAgents().size();
    }

    /**
     * The formula was fitted using the average degree per agent,
     * dependent on the network size.
     *
     * @return the average degree dependent on network size
     */
    public double getAverageDegree() {
        return 0.8628 * Math.pow(this.getN(), 0.6246);
    }

    /**
     * Gets all susceptible agents within the network.
     *
     * @return all susceptible agents within the network.
     */
    public Collection<Agent> getSusceptibles() {
        List<Agent> susceptibles = new LinkedList<Agent>();

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isSusceptible()) {
                susceptibles.add(agent);
            }
        }

        return susceptibles;
    }

    /**
     * Gets all infected agents within the network.
     *
     * @return all infected agents within the network.
     */
    public Collection<Agent> getInfected() {
        List<Agent> infected = new LinkedList<Agent>();

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isInfected()) {
                infected.add(agent);
            }
        }

        return infected;
    }

    /**
     * Gets all recovered agents within the network.
     *
     * @return all recovered agents within the network.
     */
    public Collection<Agent> getRecovered() {
        List<Agent> recovered = new LinkedList<Agent>();

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isRecovered()) {
                recovered.add(agent);
            }
        }

        return recovered;
    }

    /**
     * Gets all satisfied agents within the network.
     *
     * @return all satisfied agents within the network.
     */
    public Collection<Agent> getSatisfied() {
        List<Agent> satisfied = new LinkedList<Agent>();

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isSatisfied()) {
                satisfied.add(agent);
            }
        }

        return satisfied;
    }

    /**
     * Gets all unsatisfied agents within the network.
     *
     * @return all unsatisfied agents within the network.
     */
    public Collection<Agent> getUnsatisfied() {
        List<Agent> unsatisfied = new LinkedList<Agent>();

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (!agent.isSatisfied()) {
                unsatisfied.add(agent);
            }
        }

        return unsatisfied;
    }

    /**
     * Gets an iterator over all agents in an undefined order.
     *
     * @return an iterator over all agents in an undefined order
     */
    public Iterator<Agent> getAgentIterator() {
        return this.getNodeIterator();
    }

    /**
     * Clears all connections between the agents.
     */
    public void clearConnections() {
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            agentsIt.next().removeAllConnections();
        }
        this.timestepsStable = 0;
    }

    /**
     * Removes all connections between agents and resets the agents to being susceptible.
     */
    public void resetAgents() {
        clearConnections();
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            agent.makeSusceptible();
        }
    }

    /**
     * Infects a random agent.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease to infect a agent with
     * @return the infected agent, null if no agent was infected
     */
    public Agent infectRandomAgent(DiseaseSpecs diseaseSpecs) {
        if (this.getAgents() == null || this.getAgents().isEmpty()) {
            logger.info("Unable to infect a random agent. No agent available.");
            return null;
        }

        // agents performing action in random order
        List<Agent> agents = new ArrayList<Agent>(this.getAgents());
        Collections.shuffle(agents);
        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            if (agent.isSusceptible()) {
                agent.forceInfect(diseaseSpecs);
                this.timestepsStable = 0;
                return agent;
            }
        }
        logger.info("Unable to infect a random agent. No susceptible agent available.");
        return null;
    }

    /**
     * Toggles the infection of a specific agent.
     *
     * @param agentId
     *          the agent's id
     * @param diseaseSpecs
     *          the characteristics of the disease to infect the agent with
     */
    public void toggleInfection(String agentId, DiseaseSpecs diseaseSpecs) {
        Iterator<Agent> agentsIt = this.getAgentIterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            if (agent.getId().equals(agentId)) {

                switch (agent.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        agent.forceInfect(diseaseSpecs);
                        break;

                    case INFECTED:
                        agent.cure();
                        break;

                    case RECOVERED:
                        agent.makeSusceptible();
                        break;

                    default:
                        break;
                }

                this.timestepsStable = 0;

            }
        }
    }

    /**
     * Checks whether the network is pairwise stable: there are no two players that
     * want to create a link and where neither one of them wants to delete a link.
     */
    private void computeStability() {

        Collection<String> checkedIds = new ArrayList<String>();

        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();

            Collection<Agent> others = new ArrayList<Agent>(this.getAgents());
            Iterator<Agent> othersIt = others.iterator();
            while (othersIt.hasNext()) {
                Agent other = othersIt.next();

                if (agent.getId() == other.getId() || checkedIds.contains(other.getId())) {
                    continue;
                }

                if (agent.isDirectlyConnectedTo(other)) {
                    if (agent.existingConnectionTooCostly(other)) {
                        this.timestepsStable = 0;
                        return;
                    }
                } else {
                    if (agent.newConnectionValuable(other) && other.newConnectionValuable(agent)) {
                        this.timestepsStable = 0;
                        return;
                    }
                }
            }
            checkedIds.add(agent.getId());
        }
        this.timestepsStable++;
    }

    /**
     * Gets whether the network is pairwise stable or not: there are no two players that
     * want to create a link and where neither one of them wants to delete a link.
     *
     * @return true if network is pairwise stable, false otherwise
     */
    public boolean isStable() {
        return this.timestepsStable >= TIMESTEPS_REQUIRED_FOR_STABILITY;
    }

    /**
     * Checks whether the network has an active infection. That is, whether at least one agent
     * is infected and therefore is infectious to others.
     *
     * @return true if the network has an active infection, false otherwise
     */
    public boolean hasActiveInfection() {
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isInfected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the network type.
     *
     * @return the network type
     */
    public NetworkTypes getType() {

        if (this.isEmpty()) {
            return NetworkTypes.EMPTY;
        }

        if (this.isFull()) {
            return NetworkTypes.FULL;
        }

        if (this.isRing()) {
            return NetworkTypes.RING;
        }

        if (this.isStar()) {
            return NetworkTypes.STAR;
        }

        // TODO implement missing network types, such as bipartite

        return NetworkTypes.UNDEFINED;
    }


    /**
     * Checks whether the network is empty. That is, whether there are no connections between any agents whatsoever.
     *
     * @return true if the network is empty, false otherwise
     */
    private boolean isEmpty() {
        return (this.getEdgeCount() == 0);
    }

    /**
     * Checks whether the network is full. That is, whether every agent is connected to every other agent.
     *
     * @return true if the network is full, false otherwise
     */
    private boolean isFull() {
        Iterator<Agent> agentIt = getAgentIterator();
        // every agent needs to have connections to every other agent
        while (agentIt.hasNext()) {
            if (agentIt.next().getDegree() != (this.getAgents().size() - 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the network is a ring. That is, whether all agents are connected to two other agents,
     * forming a ring that connects all agents within the network.
     *
     * @return true if the network is a ring, false otherwise
     */
    private boolean isRing() {

        boolean ring = true;

        Iterator<Agent> agentIt = getAgentIterator();
        Agent firstAgent = null;
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();

            // ring - 1st condition: every agent needs to be connected to exactly two other agents
            ring = ring && (agent.getDegree() == 2);

            // ring - 2nd condition: make sure it's a single ring,
            // meaning a node can reach every other node
            if (firstAgent == null) {
                firstAgent = agent;
            } else {
                ring = ring && firstAgent.isSomehowConnectedTo(agent);
            }
        }

        return ring;
    }

    /**
     * Checks whether the network is a star. That is, whether there is a single center node that is connected to
     * all other nodes, while the other nodes are solely connected to the center node.
     *
     * @return true if the network is a star, false otherwise
     */
    private boolean isStar() {

        int centers = 0;
        int peripheries = 0;

        Iterator<Agent> agentIt = getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();

            if (agent.getDegree() == 1) {
                peripheries++;
            } else if (agent.getDegree() == (this.getAgents().size() - 1)) {
                centers++;
            } else {
                return false;
            }
        }

        return ((centers == 1) && (peripheries == this.getAgents().size() - 1));
    }


    /**
     * Adds a listener to be notified when the network changes.
     *
     * @param networkListener
     *          the listener to be added
     */
    public void addNetworkListener(NetworkListener networkListener) {
        this.networkListeners.add(networkListener);
    }

    /**
     * Removes a listener to be notified when the network changes.
     *
     * @param networkListener
     *          the listener to be removed
     */
    public void removeNetworkListener(NetworkListener networkListener) {
        this.networkListeners.remove(networkListener);
    }

    /**
     * Notifies the listeners of the added agent.
     *
     * @param agent
     *          the agent being added
     */
    private final void notifyAgentAdded(Agent agent) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAgentAdded(agent);
        }
    }

    /**
     * Notifies the agentRemovedListeners of the added agent.
     *
     * @param agentId
     *          the id of the agent being removed
     */
    private final void notifyAgentRemoved(String agentId) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAgentRemoved(agentId);
        }
    }

    /**
     * Gets the average degree of the network.
     *
     * @return the average degree of the network
     */
    public double getAvDegree() {
        return Toolkit.averageDegree(this);
    }

    /**
     * Gets the average degree at distance 2 of the network.
     *
     * @return the average degree at distance 2 of the network
     */
    public double getAvDegree2() {
        double avDegree2 = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            avDegree2 += agent.getSecondOrderDegree();
        }
        return avDegree2/this.getAgents().size();
    }

    /**
     * Gets the average degree of satisfied agents.
     *
     * @return the average degree of satisfied agents
     */
    public double getAvDegreeSatisfied() {
        double avDegree = 0;
        int satisfiedAgents = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            if (agent.isSatisfied()) {
                avDegree += agent.getDegree();
                satisfiedAgents++;
            }
        }
        return avDegree/satisfiedAgents;
    }

    /**
     * Gets the average degree of unsatisfied agents.
     *
     * @return the average degree of unsatisfied agents
     */
    public double getAvDegreeUnsatisfied() {
        double avDegree = 0;
        int unsatisfiedAgents = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            if (!agent.isSatisfied()) {
                avDegree += agent.getDegree();
                unsatisfiedAgents++;
            }
        }
        return avDegree/unsatisfiedAgents;
    }

    /**
     * Gets the average closeness of the network.
     *
     * @return the average closeness of the network
     */
    public double getAvCloseness() {
        double avCloseness = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            avCloseness += agent.getCloseness();
        }
        return avCloseness/this.getAgents().size();
    }

    /**
     * Gets the density of the network.
     *
     * @return the density of the network
     */
    public double getDensity() {
        return Toolkit.density(this);
    }

    /**
     * Gets the assortativity of the network.
     *
     * @param a
     *          the agent attribute to get the assortativity for
     * @return the assortativity of the network
     */
    public double getAssortativity(AgentAttributes a) {

        // collect attributes of all node pairs
        double[] attributes1 = new double[this.getEdgeCount()];
        double[] attributes2 = new double[this.getEdgeCount()];

        Iterator<Edge> eIt = this.getEdgeIterator();
        int i = 0;
        while (eIt.hasNext()) {
            Edge edge = eIt.next();
            switch (a) {
                case RISK_FACTOR_SIGMA:
                    attributes1[i] = ((Agent) edge.getNode0()).getRSigma();
                    attributes2[i] = ((Agent) edge.getNode1()).getRSigma();
                    break;

                case RISK_FACTOR_PI:
                    attributes1[i] = ((Agent) edge.getNode0()).getRPi();
                    attributes2[i] = ((Agent) edge.getNode1()).getRPi();
                    break;

                case CONNECTION_STATS:
                case DISEASE_GROUP:
                case DISEASE_INFECTION:
                case DISEASE_SPECS:
                case OMEGA:
                case OMEGA_SHUFFLE:
                case PHI:
                case RISK_MEANING_PI:
                case RISK_MEANING_SIGMA:
                case SATISFIED:
                case UI_CLASS:
                case UTILITY_FUNCTION:
                default:
                    logger.warn("assortativity not available for: " + a);
                    break;
            }
            i++;
        }

        // Pearson correlation coefficient
        if (attributes1.length == 0 || attributes2.length == 0) {
            return 0;
        }
        return new PearsonsCorrelation().correlation(attributes1, attributes2);
    }

    /**
     * Gets the average clustering coefficient of the network.
     *
     * @return the average clustering coefficient of the network
     */
    public double getAvClustering() {
        return Toolkit.averageClusteringCoefficient(this);
    }

    /**
     * Gets the average path length of the network.
     *
     * @return the average path length of the network
     */
    public double getAvPathLength() {
        double totalShortestPathLengths = 0;

        Collection<Agent> agents1 = this.getAgents();
        Iterator<Agent> agents1It = agents1.iterator();
        while (agents1It.hasNext()) {
            Agent a1 = agents1It.next();
            DijkstraShortestPath dsp = new DijkstraShortestPath();
            dsp.executeShortestPaths(a1);

            Collection<Agent> agents2 = this.getAgents();
            Iterator<Agent> agents2It = agents2.iterator();
            while (agents2It.hasNext()) {
                Agent a2 = agents2It.next();
                // skip if a1 and a2 are identical
                if (a1.getId().equals(a2.getId())) {
                    continue;
                }
                Integer shortestPathLength = dsp.getShortestPathLength(a2);
                // if nodes cannot be reached: path length = 0
                if (shortestPathLength != null) {
                    totalShortestPathLengths += shortestPathLength.intValue();
                }
            }
        }

        // average
        return totalShortestPathLengths / (this.getN() * (this.getN()-1));
    }

    /**
     * Gets the average utility of all agents in the network.
     *
     * @return the average utility of all agents in the network
     */
    public double getAvUtility() {
        double avUtility = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avUtility += agent.getUtility().getOverallUtility();
        }
        return (avUtility / this.getAgents().size());
    }

    /**
     * Gets the average social benefits of all agents in the network.
     *
     * @return the average social benefits of all agents in the network
     */
    public double getAvSocialBenefits() {
        double avBenefit1 = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avBenefit1 += agent.getUtility().getSocialBenefits();
        }
        return (avBenefit1 / this.getAgents().size());
    }

    /**
     * Gets the average costs of social connections of all agents in the network.
     *
     * @return the average costs of social connections of all agents in the network
     */
    public double getAvSocialCosts() {
        double avCosts1 = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avCosts1 += agent.getUtility().getSocialCosts();
        }
        return (avCosts1 / this.getAgents().size());
    }

    /**
     * Gets the average disease costs of all agents in the network.
     *
     * @return the average disease costs of all agents in the network
     */
    public double getAvDiseaseCosts() {
        double avCostsDisease = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avCostsDisease += agent.getUtility().getDiseaseCosts();
        }
        return (avCostsDisease / this.getAgents().size());
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationStarted(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationStarted(Simulation simulation) {
        computeStability();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        computeStability();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) { }

}
