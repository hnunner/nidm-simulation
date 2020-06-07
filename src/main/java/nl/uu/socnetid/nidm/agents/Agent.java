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
package nl.uu.socnetid.nidm.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import nl.uu.socnetid.nidm.diseases.Disease;
import nl.uu.socnetid.nidm.diseases.DiseaseFactory;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.stats.AgentConnectionStats;
import nl.uu.socnetid.nidm.stats.DijkstraShortestPath;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.utility.Utility;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * Interface of a basic agent.
 *
 * @author Hendrik Nunner
 */
public class Agent extends SingleNode implements Comparable<Agent>, Runnable {

    // logger
    private final static Logger logger = LogManager.getLogger(Agent.class);

    // concurrency lock
    private Lock lock;

    // listeners
    private final Set<AgentListener> agentListeners =
            new CopyOnWriteArraySet<AgentListener>();


    /**
     * Constructor.
     *
     * @param id
     *          the unique identifier
     * @param network
     *          the network the agent is being a part of
     */
    protected Agent(String id, Network network) {
        super(network, id);
    }

    /**
     * Initializes the node attributes.
     *
     * @param utilityFunction
     *          the function the agent uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease characteristics that is or might become present in the network
     * @param riskFactorSigma
     *          the risk factor for disease severity (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @param riskFactorPi
     *          the risk factor for probability of infections (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @param phi
     *          the share of peers to evaluate per round
     * @param psi
     *          the proportion of ties to evaluate per round
     * @param xi
     *          the proportion of ties at distance 2 to evaluate per round
     * @param omega
     *          the share of peers to select assortatively
     * @param omegaShuffle
     *          whether assortatively selected agents ought to be shuffled before processing
     * @param age
     *          the age of the agent
     */
    public void initAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs,
            Double riskFactorSigma, Double riskFactorPi, Double phi, Double psi, Double xi, Double omega, boolean omegaShuffle,
            Integer age) {
        this.addAttribute(AgentAttributes.UTILITY_FUNCTION, utilityFunction);
        this.addAttribute(AgentAttributes.DISEASE_SPECS, diseaseSpecs);
        DiseaseGroup diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.addAttribute(AgentAttributes.DISEASE_GROUP, diseaseGroup);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.addAttribute(AgentAttributes.UI_CLASS, diseaseGroup.toString(), false);
        this.addAttribute(AgentAttributes.RISK_FACTOR_SIGMA, riskFactorSigma);
        this.addAttribute(AgentAttributes.RISK_FACTOR_PI, riskFactorPi);
        this.addAttribute(AgentAttributes.RISK_MEANING_SIGMA, getRiskMeaning(riskFactorSigma));
        this.addAttribute(AgentAttributes.RISK_MEANING_PI, getRiskMeaning(riskFactorPi));
        this.addAttribute(AgentAttributes.PHI, phi);
        this.addAttribute(AgentAttributes.PSI, psi);
        this.addAttribute(AgentAttributes.XI, xi);
        this.addAttribute(AgentAttributes.OMEGA, omega);
        this.addAttribute(AgentAttributes.OMEGA_SHUFFLE, omegaShuffle);
        this.addAttribute(AgentAttributes.SATISFIED, false);
        this.addAttribute(AgentAttributes.CONNECTION_STATS, new AgentConnectionStats());
        this.addAttribute(AgentAttributes.AGE, age);
        this.addAttribute("ui.label", this.getId());
    }

    public String getLabel() {
        return "";
        // TODO create better label
//        StringBuilder sb = new StringBuilder();
//        sb.append("[r=").append(this.getRiskFactor()).append("; ")
//        .append(this.getRiskMeaning(this.getRiskFactor())).append("]");
//        return sb.toString();
    }

    /**
     * Translates the risk factor into interpretable format.
     *
     * @param riskFactor
     *          the risk factor
     * @return interpretable format of risk factor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     */
    private String getRiskMeaning(double riskFactor) {
        if (riskFactor < 1.0) {
            return "risk seeking";
        } else if (riskFactor > 1.0) {
            return "risk averse";
        }
        return "risk neutral";
    }

    /**
     * Gets the network the agent is being a part of.
     *
     * @return the network the agent is being a part of
     */
    public Network getNetwork() {
        return (Network) super.getGraph();
    }

    /**
     * Gets the number of network decisions an agent is allowed to make in a single round.
     * This depends on the average number of connections an agent has.
     *
     * @return the number of network decisions
     */
    public int getNumberOfNetworkDecisions() {
        return (int) (Math.round((this.getNetwork().getNodeCount() - 1) * this.getPhi()));
    }

    /**
     * Gets the value of an attribute.
     *
     * @param attribute
     *          the attribute to get
     * @return the value of the attribute
     */
    private Object getAttribute(AgentAttributes attribute) {
        return super.getAttribute(attribute.toString());
    }

    /**
     * Adds an attribute and notifies the listeners of the added attribute by default.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the value
     */
    private void addAttribute(AgentAttributes attribute, Object value) {
        this.addAttribute(attribute, value, true);
    }

    /**
     * Adds an attribute.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the value
     * @param notify
     *          flag whether agent listeners ought to be notified of the added attribute
     */
    private void addAttribute(AgentAttributes attribute, Object value, boolean notify) {
        super.addAttribute(attribute.toString(), value);
        if (notify) {
            notifyAttributeAdded(attribute, value);
        }
    }

    /**
     * Changes an attribute and notifies the listeners of the changed attribute by default.
     *
     * @param attribute
     *          the attribute
     * @param oldValue
     *          the old value
     * @param newValue
     *          the new value
     */
    private void changeAttribute(AgentAttributes attribute, Object oldValue, Object newValue) {
        this.changeAttribute(attribute, oldValue, newValue, true);
    }

    /**
     * Changes an attribute.
     *
     * @param attribute
     *          the attribute
     * @param oldValue
     *          the old value
     * @param newValue
     *          the new value
     * @param notify
     *          flag whether agent listeners ought to be notified of the changed attribute
     */
    private void changeAttribute(AgentAttributes attribute, Object oldValue, Object newValue, boolean notify) {
        super.changeAttribute(attribute.toString(), newValue);
        if (notify) {
            notifyAttributeChanged(attribute, oldValue, newValue);
        }
    }

    /**
     * Removes an attribute and notifies the listeners of the removed attribute by default.
     *
     * @param attribute
     *          the attribute
     */
    private void removeAttribute(AgentAttributes attribute) {
        this.removeAttribute(attribute, true);
    }

    /**
     * Adds an attribute.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the value
     * @param notify
     *          flag whether agent listeners ought to be notified of the added attribute
     */
    private void removeAttribute(AgentAttributes attribute, boolean notify) {
        super.removeAttribute(attribute.toString());
        if (notify) {
            notifyAttributeRemoved(attribute);
        }
    }


    /**
     * Gets the agent's utility.
     *
     * @return the agent's utility
     */
    public Utility getUtility() {
        return this.getUtilityFunction().getUtility(this);
    }

    /**
     * Gets the utility for a agent including an additional agent as connection.
     *
     * @param with
     *          the agent to include as direct connection
     * @return the utility for a agent based on a list of connections
     */
    protected Utility getUtilityWith(Agent with) {
        return this.getUtilityFunction().getUtilityWith(this, with);
    }

    /**
     * Gets the utility for a agent excluding an agent as connection.
     *
     * @param without
     *          the agent to exclude as direct connection
     * @return the utility for a agent based on a list of connections
     */
    protected Utility getUtilityWithout(Agent without) {
        return this.getUtilityFunction().getUtilityWithout(this, without);
    }

    /**
     * Update the node's satisfaction attribute.
     */
    private void updateSatisfaction(boolean satisfied) {

        Boolean oldValue = this.isSatisfied();
        Boolean newValue = satisfied;

        // no change - do nothing
        if (oldValue == newValue) {
            return;
        }
        // change - update
        this.changeAttribute(AgentAttributes.SATISFIED, oldValue, newValue);
    }

    /**
     * Gets the agent's risk factor for disease severity.
     *
     * @return the agent's risk factor for disease severity
     */
    public double getRSigma() {
        return (double) this.getAttribute(AgentAttributes.RISK_FACTOR_SIGMA);
    }

    /**
     * Gets the agent's risk factor for probability of infections.
     *
     * @return the agent's risk factor for probability of infections
     */
    public double getRPi() {
        return (double) this.getAttribute(AgentAttributes.RISK_FACTOR_PI);
    }

    /**
     * Gets the agent's share of peers to evaluate per round.
     *
     * @return the agent's share of peers to evaluate per round
     */
    public double getPhi() {
        return (double) this.getAttribute(AgentAttributes.PHI);
    }

    /**
     * Gets the agent's proportion of direct ties to evaluate per round.
     *
     * @return the agent's proportion of direct ties to evaluate per round
     */
    public double getPsi() {
        return (double) this.getAttribute(AgentAttributes.PSI);
    }

    /**
     * Gets the agent's proportion of ties at distance 2 to evaluate per round.
     *
     * @return the agent's proportion of ties at distance 2 to evaluate per round
     */
    public double getXi() {
        return (double) this.getAttribute(AgentAttributes.XI);
    }

    /**
     * Gets the agent's share of assortatively selected peers.
     *
     * @return the agent's share of assortatively selected peers
     */
    public double getOmega() {
        return (double) this.getAttribute(AgentAttributes.OMEGA);
    }

    /**
     * Gets the whether assortatively selected co-agents ought to be shuffled before processing.
     *
     * @return true if assortatively selected co-agents ought to be shuffled before processing, false otherwise
     */
    public boolean isOmegaShuffle() {
        return (boolean) this.getAttribute(AgentAttributes.OMEGA_SHUFFLE);
    }

    /**
     * Gets the agent's utility function.
     *
     * @return the agent's utility function
     */
    public UtilityFunction getUtilityFunction() {
        return (UtilityFunction) this.getAttribute(AgentAttributes.UTILITY_FUNCTION);
    }

    /**
     * Gets the agent's connections.
     *
     * @return the agent's connections
     */
    public List<Agent> getConnections() {
        List<Agent> connections = new ArrayList<Agent>();
        Iterator<Agent> neighborIt = getNeighborNodeIterator();
        while (neighborIt.hasNext()) {
            connections.add(neighborIt.next());
        }
        return connections;
    }

    /**
     * Gets the agent's connections at distance 2.
     *
     * @return the agent's connections at distance 2
     */
    public List<Agent> getConnectionsAtDistance2() {
        List<Agent> connectionsDist2 = new ArrayList<Agent>();

        List<Agent> connections = new ArrayList<Agent>();
        Iterator<Agent> connectionsIt = this.getNeighborNodeIterator();
        while (connectionsIt.hasNext()) {
            Agent connection = connectionsIt.next();
            connections.add(connection);

            Iterator<Agent> connectionsDist2It = connection.getNeighborNodeIterator();
            while (connectionsDist2It.hasNext()) {
                Agent connectionDist2 = connectionsDist2It.next();
                if (connectionDist2.getId().equals(this.getId())) {
                    continue;
                }
                connectionsDist2.add(connectionDist2);
            }
        }

        connectionsDist2.removeAll(connections);
        return connectionsDist2;
    }

    /**
     * Gets the agent's co-agents.
     *
     * @return the agent's co-agents
     */
    public List<Agent> getCoAgents() {
        List<Agent> coAgents = new ArrayList<Agent>(getNetwork().getAgents());
        coAgents.remove(this);
        return coAgents;
    }

    /**
     * Gets whether the agent is satisfied with the current connections.
     *
     * @return true if the agent is satisfied with the current connections, false otherwise
     */
    public boolean isSatisfied() {
        return (boolean) this.getAttribute(AgentAttributes.SATISFIED);
    }

    /**
     * Gets the agent's connection stats.
     *
     * @return the agent's connection stats
     */
    public AgentConnectionStats getConnectionStats() {
        return (AgentConnectionStats) this.getAttribute(AgentAttributes.CONNECTION_STATS);
    }

    /**
     * Gets the agent's age.
     *
     * @return the agent's age
     */
    public int getAge() {
        return (int) this.getAttribute(AgentAttributes.AGE);
    }

    /**
     * Gets the second order degree.
     *
     * @return the second order degree
     */
    public double getSecondOrderDegree() {
        return StatsComputer.computeSecondOrderDegree(this);
    }

    /**
     * Gets the closeness.
     *
     * @return the closeness
     */
    public double getCloseness() {
        return StatsComputer.computeCloseness(this);
    }

    /**
     * Gets the clustering.
     *
     * @return the clustering
     */
    public double getClustering() {
        return Toolkit.clusteringCoefficient(this);
    }

    /**
     * Gets the betweenness.
     *
     * @return the betweenness
     */
    public double getBetweenness() {
        BetweennessCentrality bc = new BetweennessCentrality();
        bc.setUnweighted();
        bc.init(this.getNetwork());
        bc.compute();
        return bc.centrality(this);
    }

    /**
     * Gets the normalized betweenness.
     *
     * @return the normalized betweenness
     */
    public double getBetweennessNormalized() {
        double onShortestPaths = this.getBetweenness();
        double N = this.getNetwork().getN();
        return onShortestPaths / (((N-1)/2) * (N-2));
    }

    /**
     * Sets the 2-dimensional geographic coordinates of the agent.
     *
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     */
    public void setXY(double x, double y) {
        this.setAttribute("xyz", x, y, 0);
    }

    /**
     * Gets the x-coordinate of the agent.
     *
     * @return the x-coordinate of the agent
     */
    public double getX() {
        return GraphPosLengthUtils.nodePosition(this)[0];
    }

    /**
     * Gets the y-coordinate of the agent.
     *
     * @return the x-coordinate of the agent
     */
    public double getY() {
        return GraphPosLengthUtils.nodePosition(this)[1];
    }

    /**
     * Gets the geographic distance to another agent.
     *
     * @param agent
     *          the agent to get the geographic distance to
     * @return the geographic distance to another agent
     */
    public double getGeographicDistanceTo(Agent agent) {
        return Math.sqrt(
                Math.pow(this.getX() - agent.getX(), 2) +
                Math.pow(this.getY() - agent.getY(), 2));
    }

    /**
     * Gets the geodesic distance to another agent.
     *
     * @param agent
     *          the agent to get the geographic distance to
     * @return the geodesic distance to another agent
     */
    public Integer getGeodesicDistanceTo(Agent agent) {
        DijkstraShortestPath dsp = new DijkstraShortestPath();
        dsp.executeShortestPaths(this);
        return dsp.getShortestPathLength(agent);
    }

    /**
     * Gets the value used for assortativity comparisons.
     *
     * @return the value used for assortativity comparisons
     */
    private double getAssortativityComparativeValue() {
        switch (this.getNetwork().getAssortativityCondition()) {
            case AGE:
                return this.getAge();

            case RISK_PERCEPTION:
                return (this.getRPi() + this.getRSigma());

            default:
                logger.warn("Unknown assortativity condition: " + this.getNetwork().getAssortativityCondition());
                break;
        }
        return 0.0;
    }

    /**
     * Gets the absolute difference between the comp(aritive) value and the actual value,
     * according to the assortativity condition.
     *
     * @param comp
     *          the value to compare to
     * @return the absolute difference between the value to compare to and the actual value,
     *         according to the assortativity condition
     */
    private double getAssortDiff(double comp) {
        switch (this.getNetwork().getAssortativityCondition()) {
            case AGE:
                return Math.abs((comp) - this.getAge());

            case RISK_PERCEPTION:
                return Math.abs((comp) - (this.getRPi() + this.getRSigma()));

            default:
                logger.warn("Unknown assortativity condition: " + this.getNetwork().getAssortativityCondition());
                break;
        }
        return 0.0;
    }


    /**
     * Keeps track of actively broken ties.
     */
    private void trackBrokenTieActive() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incBrokenTiesActive();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of passively broken ties.
     */
    private void trackBrokenTiePassive() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incBrokenTiesPassive();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of accepted outgoing requests.
     */
    private void trackAcceptedRequestOut() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incAcceptedRequestsOut();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of declined outgoing requests.
     */
    private void trackDeclinedRequestOut() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incDeclinedRequestsOut();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of accepted incoming requests.
     */
    private void trackAcceptedRequestIn() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incAcceptedRequestsIn();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of an declined incoming request.
     */
    private void trackDeclinedRequestIn() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incDeclinedRequestsIn();
        changeAttribute(AgentAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Agent p) {
        return (int) (Long.valueOf(this.getId()) - Long.valueOf(p.getId()));
    }


    /////////////////////////////////////////////////// CONNECTIONS ///////////////////////////////////////////////////
    /**
     * Computes a single round for an agent. That is, an {@link Agent} tries to connect to
     * or disconnects from another {@link Agent} if it produces higher utility.
     */
    public void computeRound() {

        // starting assumption: current connections are not satisfactory
        boolean satisfied = true;

        int decisions = this.getNumberOfNetworkDecisions();
        Set<Agent> agentsProcessed = new HashSet<Agent>(decisions);
        while (agentsProcessed.size() < decisions) {

            double rand = ThreadLocalRandom.current().nextDouble();

            Agent other = null;
            if (rand <= this.getPsi()) {
                other = this.getRandomConnection(agentsProcessed);
            }
            if (other == null && rand <= (this.getPsi() + this.getXi())) {
                other = this.getRandomConnectionAtDistance2(agentsProcessed);
            }
            if (other == null) {
                other = this.getRandomPeer(agentsProcessed);
            }

            if (this.isDirectlyConnectedTo(other)) {
                if (existingConnectionTooCostly(other)) {
                    disconnectFrom(other);
                    satisfied = false;
                }
            } else {
                if (newConnectionValuable(other)) {
                    connectTo(other);
                    satisfied = false;
                }
            }
            agentsProcessed.add(other);
        }

        // update satisfaction
        updateSatisfaction(satisfied);
        // round finished
        notifyRoundFinished();
    }

    /**
     * Checks whether a new connection adds value to the overall utility of an agent.
     *
     * @param newConnection
     *          the agent on the other side of the new connection
     * @return true if the new connection adds value to the overall utility of an agent
     */
    public boolean newConnectionValuable(Agent newConnection) {
        return this.getUtilityWith(newConnection).getOverallUtility() >= this.getUtility().getOverallUtility();
    }

    /**
     * Creates a connection between this agent and another agent.
     *
     * @param agent
     *          the agent to connect to
     * @return true if connection was accepted and created, false otherwise
     */
    public boolean connectTo(Agent agent) {
        // other agent accepting connection?
        double currUtility = agent.getUtility().getOverallUtility();
        // TODO use connectionValuableWith to avoid GUI glitches
        agent.addConnection(this);
        double newUtility = agent.getUtility().getOverallUtility();
        agent.removeConnection(this);
        if (newUtility >= currUtility) {
            addConnection(agent);
            trackAcceptedRequestOut();
            return true;
        }
        trackDeclinedRequestOut();
        return false;
    }

    /**
     * Checks whether an existing connection creates more costs than it provides benefits.
     *
     * @param existingConnection
     *          the agent on the other side of the existing connection
     * @return true if the existing connection create more costs than it provides benefits
     */
    public boolean existingConnectionTooCostly(Agent existingConnection) {
        return this.getUtilityWithout(existingConnection).getOverallUtility() > this.getUtility().getOverallUtility();
    }

    /**
     * Disconnects this agent from another agent.
     *
     * @param agent
     *          the agent to disconnect from
     */
    public void disconnectFrom(Agent agent) {
        this.removeConnection(agent);
        this.trackBrokenTieActive();
        agent.notifyBrokenTie(this);
    }

    /**
     * Entry point for incoming connection requests.
     *
     * @param newConnection
     *          the agent requesting to establish a connection
     * @return true if the connection is being accepted, false otherwise
     */
    public boolean acceptConnection(Agent newConnection) {
        boolean accept = newConnectionValuable(newConnection);
        if (accept) {
            trackAcceptedRequestIn();
        } else {
            trackDeclinedRequestIn();
        }
        return accept;
    }

    /**
     * Entry point for broken tie notifications.
     *
     * @param initiator
     *          the initiatior who broke the tie
     */
    public void notifyBrokenTie(Agent initiator) {
        trackBrokenTiePassive();
    }




    /**
     * Sorts the given list of agents by absolute difference of a comparative value.
     *
     * @param agents
     *          the list of agents to sort
     * @param comp
     *          the comparative value to create the absolute difference for
     */
    private void sortByRDiff(List<Agent> agents, double comp) {

            TreeMap<Double, ArrayList<Agent>> map = new TreeMap<>();

            // store values in map with difference as key
            for (int i = 0; i < agents.size(); i++) {

                Agent agent = agents.get(i);
                double diff = agent.getAssortDiff(comp);

                if (map.containsKey(diff)) {
                    ArrayList<Agent> list = map.get(diff);
                    list.add(agent);
                    map.put(diff, list);
                } else {
                    ArrayList<Agent> list = new ArrayList<Agent>();
                    list.add(agents.get(i));
                    map.put(diff, list);
                }
            }

            // Update the values of array
            int index = 0;
            for (Map.Entry<Double, ArrayList<Agent>> entry : map.entrySet()) {
                ArrayList<Agent> list = map.get(entry.getKey());
                for (int i = 0; i < list.size(); i++) {
                        agents.set(index++, list.get(i));
                }
            }
    }


    private List<Agent> getRandomListOfAgents(List<Agent> agents, int amount) {
        List<Agent> res = new ArrayList<Agent>(amount);

        if (agents != null && !agents.isEmpty()) {

            // fill (omega share) according assortativity condition
            sortByRDiff(agents, this.getAssortativityComparativeValue());
            Iterator<Agent> it = agents.iterator();
            long amountAss = Math.round(amount * this.getOmega());
            while (it.hasNext() &&
                    res.size() < amountAss) {
                res.add(it.next());
            }

            // fill rest with randomly drawn agent
            Collections.shuffle(agents);
            it = agents.iterator();
            while (it.hasNext() &&
                    res.size() < amount) {
                Agent agent = it.next();
                if (!res.contains(agent)) {
                    res.add(agent);
                }
            }
        }

        return res;
    }

    /**
     * Creates a collection of randomly selected co-agents.
     *
     * @param amount
     *          the amount of co-agents to add
     * @return a random collection of co-agents
     */
    public List<Agent> getRandomListOfCoAgents(int amount) {
        List<Agent> res = new ArrayList<Agent>(amount);

        // 1. share (psi) of randomly selected direct ties
        int amountTies = (int) (Math.round(amount * this.getPsi()));
        res.addAll(this.getRandomListOfAgents(this.getConnections(), amountTies));

        // 2. share (xi) of randomly selected ties at distance 2
        int amountTiesAtDistance2 = (int) (Math.round(amount * this.getXi()));
        res.addAll(this.getRandomListOfAgents(this.getConnectionsAtDistance2(), amountTiesAtDistance2));

        // 3. rest taken from untied random agents
        List<Agent> coAgents = this.getCoAgents();
        coAgents.removeAll(res);
        res.addAll(this.getRandomListOfAgents(coAgents, amount - res.size()));

        Collections.shuffle(res);
        return res;
    }

    /**
     * @param agents
     * @return
     */
    private Agent drawRandomAgent(List<Agent> agents) {
        // no feasible agent
        if (agents.isEmpty()) {
            return null;
        }

        // assortativity condition
        if (ThreadLocalRandom.current().nextDouble() <= this.getOmega()) {
            sortByRDiff(agents, this.getAssortativityComparativeValue());
            return agents.get(0);
        }

        // random selection
        return agents.get(ThreadLocalRandom.current().nextInt(agents.size()));
    }

    private Agent getRandomConnection(Set<Agent> exclusions) {
        exclusions.add(this);
        List<Agent> connections = this.getConnections();
        connections.removeAll(exclusions);
        return drawRandomAgent(connections);
    }

    private Agent getRandomConnectionAtDistance2(Set<Agent> exclusions) {
        exclusions.add(this);
        List<Agent> connectionsDist2 = this.getConnectionsAtDistance2();
        connectionsDist2.removeAll(exclusions);
        return drawRandomAgent(connectionsDist2);
    }

    private Agent getRandomPeer(Set<Agent> exclusions) {
        exclusions.add(this);
        exclusions.addAll(this.getConnections());
        exclusions.addAll(this.getConnectionsAtDistance2());

        List<Agent> peers = new ArrayList<Agent>();
        Iterator<Agent> agentIt = this.getNetwork().getAgentIterator();
        for (int i = 0; i < this.getNetwork().getN(); i++) {
            Agent agent = agentIt.next();
            if (exclusions.contains(agent)) {
                continue;
            }
            peers.add(agent);
        }
        return drawRandomAgent(peers);
    }

    private Agent getAgent1(Agent agent1, Agent agent2) {
        return Long.valueOf(agent1.getId()) < Long.valueOf(agent2.getId()) ? agent1 : agent2;
    }

    private Agent getAgent2(Agent agent1, Agent agent2) {
        return Long.valueOf(agent2.getId()) > Long.valueOf(agent1.getId()) ? agent2 : agent1;
    }

    private String getEdgeId(Agent agent1, Agent agent2) {
        return this.getAgent1(agent1, agent2).getId().concat(this.getAgent2(agent1, agent2).getId());
    }

    /**
     * Adds a new connection to another agent.
     *
     * @param newConnection
     *          the agent to connect to
     */
    public void addConnection(Agent newConnection) {

        // check node consistency
        if (!checkNewConnectionConsistency(newConnection)) {
            logger.warn("Request to add new connection aborted.");
            return;
        }

        String edgeId = this.getEdgeId(this, newConnection);

        Network network = this.getNetwork();
        if (network.getEdge(edgeId) == null) {
            Agent agent1 = this.getAgent1(this, newConnection);
            Agent agent2 = this.getAgent2(this, newConnection);
            network.addEdge(edgeId, agent1.getId(), agent2.getId());
            notifyConnectionAdded(network.getEdge(edgeId), agent1, agent2);
        }
    }

    /**
     * Checks whether the agent has a direct connection to another agent
     *
     * @param agent
     *          the other agent to check the connection to
     * @return true if there is a connection, false otherwise
     */
    public boolean isDirectlyConnectedTo(Agent agent) {
        return this.getNetwork().getEdge(this.getEdgeId(this, agent)) != null;
    }

    /**
     * Checks whether the agent has a connection at distance 2 to another agent.
     *
     * @param agent
     *          the other agent to check the connection at distance 2 to
     * @return true if there is a connection at distance 2, false otherwise
     */
    public boolean isConnectedToAtDistance2(Agent agent) {
        Iterator<Agent> neighborIt = this.getNeighborNodeIterator();
        while (neighborIt.hasNext()) {
            Agent neighbor = neighborIt.next();
            if (this.getNetwork().getEdge(this.getEdgeId(neighbor, agent)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the agent has a connection somehow to another agent.
     *
     * @param agent
     *          the agent to check for an existing connection
     * @return true if the agents are somehow connected, false otherwise
     */
    public boolean isSomehowConnectedTo(Agent agent) {
        Iterator<Node> bfIt = this.getBreadthFirstIterator();
        while (bfIt.hasNext()) {
            Node node = bfIt.next();
            if (node.getId().equals(agent.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates connections to all other co-agents.
     */
    public void connectToAll() {
        ArrayList<Agent> noConnections = new ArrayList<Agent>(getCoAgents());
        noConnections.removeAll(getConnections());

        Iterator<Agent> noConnectionsIt = noConnections.iterator();
        while (noConnectionsIt.hasNext()) {
            Agent noConnection = noConnectionsIt.next();
            addConnection(noConnection);
        }
    }

    /**
     * @param newConnection
     *          the new connection to check for consistency
     * @return true if new node can be added, false otherwise
     */
    protected boolean checkNewConnectionConsistency(Agent newConnection) {
        if (newConnection.equals(this)) {
            logger.warn("Inconsistent new connection: reflexive");
            return false;
        }
        if (this.getConnections().contains(newConnection)) {
            logger.warn("Inconsistent new connection: already existing");
            return false;
        }
        return true;
    }

    /**
     * Removes a connection to another agent.
     *
     * @param connection
     *          the agent to remove the connection from
     */
    public void removeConnection(Agent connection) {

        // check node consistency
        if (connection.equals(this)) {
            logger.warn("Unable to remove reflexive connections.");
            return;
        }

        // edge id consistency
        ArrayList<Agent> tmpAgents = new ArrayList<Agent>();
        tmpAgents.add(this);
        tmpAgents.add(connection);
        Collections.sort(tmpAgents);

        // remove
        Network network = this.getNetwork();
        String edgeId = String.valueOf(tmpAgents.get(0).getId()) + String.valueOf(tmpAgents.get(1).getId());
        Edge edge = network.getEdge(edgeId);
        if (edge != null) {
            network.removeEdge(edgeId);
            notifyConnectionRemoved(edge);
        }
    }

    /**
     * Removes all connections to other agents.
     */
    public void removeAllConnections() {
        Network network = this.getNetwork();
        // remove all graph edges of the current agent
        Edge[] edges = network.getNode(String.valueOf(getId())).getEdgeSet().toArray(new Edge[0]);
        for(int i = 0; i < edges.length; ++i){
            Edge edge = edges[i];
            network.removeEdge(edge);
            notifyConnectionRemoved(edge);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        lock.lock();
        try {
            computeRound();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the lock required to synchronize threaded agents.
     *
     * @param lock
     *          the lock used to synchronize threaded agents.
     */
    public void setLock(Lock lock) {
        this.lock = lock;
    }


    ///////////////////////////////////////////////////// DISEASE /////////////////////////////////////////////////////
    /**
     * Gets the specifications of the disease the agent considers for decision making processes.
     *
     * @return the specifications of the disease the agent considers for decision making processes
     */
    public DiseaseSpecs getDiseaseSpecs() {
        return (DiseaseSpecs) this.getAttribute(AgentAttributes.DISEASE_SPECS);
    }

    /**
     * Gets the disease group the agent is in.
     *
     * @return the disease group the agent is in
     */
    public DiseaseGroup getDiseaseGroup() {
        return (DiseaseGroup) this.getAttribute(AgentAttributes.DISEASE_GROUP);
    }

    /**
     * Checks whether the agent is susceptible.
     *
     * @return true if the agent is susceptible, false otherwise
     */
    public boolean isSusceptible() {
        return this.getDiseaseGroup() == DiseaseGroup.SUSCEPTIBLE;
    }

    /**
     * Checks whether the agent is infected.
     *
     * @return true if the agent is infected, false otherwise
     */
    public boolean isInfected() {
        return this.getDiseaseGroup() == DiseaseGroup.INFECTED;
    }

    /**
     * Checks whether the agent is recovered.
     *
     * @return true if the agent is recovered, false otherwise
     */
    public boolean isRecovered() {
        return this.getDiseaseGroup() == DiseaseGroup.RECOVERED;
    }

    /**
     * Makes the agent susceptible.
     */
    public void makeSusceptible() {
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        if (this.isInfected()) {
            this.removeAttribute(AgentAttributes.DISEASE_INFECTION);
        }
        this.changeAttribute(AgentAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.SUSCEPTIBLE);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(AgentAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.SUSCEPTIBLE.toString(), false);
    }

    /**
     * Gets the disease the agent is infected with.
     *
     * @return the disease the agent is infected with
     */
    public Disease getDisease() {
        if (this.isInfected()) {
            return (Disease) this.getAttribute(AgentAttributes.DISEASE_INFECTION);
        }
        return null;
    }

    /**
     * Computes whether the agent is being infected by one of his infected connections.
     */
    public void computeDiseaseTransmission() {
        if (this.isSusceptible()) {
            int nI = StatsComputer.computeLocalAgentConnectionsStats(this).getnI();
            if (ThreadLocalRandom.current().nextDouble() <=
                    StatsComputer.computeProbabilityOfInfection(this, nI)) {
                this.infect(this.getDiseaseSpecs());
            }
        }
    }

    /**
     * Infects the agent with a disease.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the agent is infected with
     */
    public void infect(DiseaseSpecs diseaseSpecs) {
        if (isRecovered()) {
            return;
        }
        forceInfect(diseaseSpecs);
    }

    /**
     * Forces an infection onto the agent no matter whether the agent is immune or not.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the agent is infected with
     */
    public void forceInfect(DiseaseSpecs diseaseSpecs) {

        // coherence check
        if (!this.getDiseaseSpecs().equals(diseaseSpecs)) {
            throw new RuntimeException("Known disease and caught disease mismatch!");
        }

        // infect
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        this.addAttribute(AgentAttributes.DISEASE_INFECTION, DiseaseFactory.createInfection(diseaseSpecs));
        this.changeAttribute(AgentAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.INFECTED);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(AgentAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.INFECTED.toString(), false);
    }

    /**
     * Triggers the agent to fight the disease.
     */
    public void fightDisease() {
        Disease disease = this.getDisease();
        if (disease == null) {
            return;
        }
        disease.evolve();
        if (disease.isCured()) {
            this.cure();
        }
    }

    /**
     * Gets the time remaining before the agent has recovered from a disease.
     *
     * @return the time remaining before the agent has recovered from a disease
     */
    public int getTimeUntilRecovered() {
        if (isInfected()) {
            return this.getDisease().getTimeUntilCured();
        }
        return 0;
    }

    /**
     * Cures the agent from a disease.
     */
    public void cure() {
        // cure
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        this.changeAttribute(AgentAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.RECOVERED);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(AgentAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.RECOVERED.toString(), false);
        this.removeAttribute(AgentAttributes.DISEASE_INFECTION);
    }


    //////////////////////////////////////////// LISTENERS / NOTIFICATIONS ////////////////////////////////////////////
    /**
     * Adds a listener for agent notifications.
     *
     * @param agentListener
     *          the listener to be added
     */
    public void addAgentListener(AgentListener agentListener) {
        this.agentListeners.add(agentListener);
    }

    /**
     * Removes a listener for agent notifications.
     *
     * @param agentListener
     *          the listener to be removed
     */
    public void removeAgentListener(AgentListener agentListener) {
        this.agentListeners.remove(agentListener);
    }

    /**
     * Notifies listeners of added attributes.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the attribute's value
     */
    private final void notifyAttributeAdded(AgentAttributes attribute, Object value) {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeAdded(this, attribute.toString(), value);
        }
    }

    /**
     * Notifies listeners of changed attributes.
     *
     * @param attribute
     *          the attribute
     * @param oldValue
     *          the attribute's old value
     * @param newValue
     *          the attribute's new value
     */
    private final void notifyAttributeChanged(AgentAttributes attribute, Object oldValue, Object newValue) {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeChanged(this, attribute.toString(), oldValue, newValue);
        }
    }

    /**
     * Notifies listeners of removed attributes.
     *
     * @param attribute
     *          the attribute
     */
    private final void notifyAttributeRemoved(AgentAttributes attribute) {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeRemoved(this, attribute.toString());
        }
    }

    /**
     * Notifies listeners of added connections.
     *
     * @param edge
     *          the new connection
     * @param agent1
     *          the first agent the connection has been added to
     * @param agent2
     *          the second agent the connection has been added to
     */
    private final void notifyConnectionAdded(Edge edge, Agent agent1, Agent agent2) {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConnectionAdded(edge, agent1, agent2);
        }
    }

    /**
     * Notifies the listeners of removed edges.
     *
     * @param edge
     *          the removed edge
     */
    private final void notifyConnectionRemoved(Edge edge) {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConnectionRemoved(this, edge);
        }
    }

    /**
     * Notifies listeners of finished agent rounds.
     */
    private final void notifyRoundFinished() {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRoundFinished(this);
        }
    }

}
