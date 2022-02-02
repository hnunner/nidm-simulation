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
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
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

    // agentListeners
    private Set<AgentListener> agentListeners = new CopyOnWriteArraySet<AgentListener>();


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
     * @param age
     *          the age of the agent
     * @param considerAge
     *          whether age is considered for peer selection or not
     * @param profession
     *          the profession of the agent
     * @param considerProfession
     *          whether profession is considered for peer selection or not
     * @param quarantined
     *          whether the agent is quarantined or not
     */
    public void initAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs,
            Double riskFactorSigma, Double riskFactorPi, Double phi, Double psi, Double xi, Double omega,
            Integer age, boolean considerAge, String profession, boolean considerProfession, boolean quarantined) {
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
        this.addAttribute(AgentAttributes.SATISFIED, false);
        this.addAttribute(AgentAttributes.CONNECTION_STATS, new AgentConnectionStats());

        // TODO make a list of properties usable for homophily, rather than hard coded properties
        this.addAttribute(AgentAttributes.AGE, age);
        this.addAttribute(AgentAttributes.CONSIDER_AGE, considerAge);
        this.addAttribute(AgentAttributes.PROFESSION, profession);
        this.addAttribute(AgentAttributes.CONSIDER_PROFESSION, considerProfession);

        this.addAttribute(AgentAttributes.QUARANTINED, quarantined);

        this.addAttribute(AgentAttributes.FORCE_INFECTED, false);
        this.addAttribute(AgentAttributes.WHEN_INFECTED, -1);
        this.addAttribute("ui.label", this.getId());
        this.addAttribute(AgentAttributes.CLOSENESS, -1);
        this.addAttribute(AgentAttributes.CLOSENESS_LAST_COMPUTATION, -1);
        this.addAttribute(AgentAttributes.BETWEENNESS, -1);
        this.addAttribute(AgentAttributes.BETWEENNESS_LAST_COMPUTATION, -1);
        this.addAttribute(AgentAttributes.CLUSTERING, -1);
        this.addAttribute(AgentAttributes.CLUSTERING_LAST_COMPUTATION, -1);

        // TODO create assortativity interface, use list filled with instanciations of subclasses here
        this.addAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION, -1);
        this.addAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION_LAST_COMPUTATION, -1);
        this.addAttribute(AgentAttributes.ASSORTATIVITY_AGE, -1);
        this.addAttribute(AgentAttributes.ASSORTATIVITY_AGE_LAST_COMPUTATION, -1);
        this.addAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION, -1);
        this.addAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION_LAST_COMPUTATION, -1);

        this.addAttribute(AgentAttributes.INITIAL_INDEX_CASE_DISTANCE, -1);
    }

    /**
     * Reinitializes agent after reader import. That is, types are reinitialized from their string representation.
     */
    public void reinitAfterRead() {
        String dgString = (String) this.getAttribute(AgentAttributes.DISEASE_GROUP);
        this.addAttribute(AgentAttributes.DISEASE_GROUP, DiseaseGroup.fromString(dgString), false);

        String csString = (String) this.getAttribute(AgentAttributes.CONNECTION_STATS);
        this.addAttribute(AgentAttributes.CONNECTION_STATS, AgentConnectionStats.fromString(csString), false);

        String dsString = (String) this.getAttribute(AgentAttributes.DISEASE_SPECS);
        this.addAttribute(AgentAttributes.DISEASE_SPECS, DiseaseSpecs.fromString(dsString), false);

        String ufString = (String) this.getAttribute(AgentAttributes.UTILITY_FUNCTION);
        this.addAttribute(AgentAttributes.UTILITY_FUNCTION, UtilityFunction.fromString(ufString), false);
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
     * Adds an attribute and notifies the agentListeners of the added attribute by default.
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
     *          flag whether agent agentListeners ought to be notified of the added attribute
     */
    private void addAttribute(AgentAttributes attribute, Object value, boolean notify) {
        super.addAttribute(attribute.toString(), value);
        if (notify) {
            notifyAttributeAdded(attribute, value);
        }
    }

    /**
     * Changes an attribute and notifies the agentListeners of the changed attribute by default.
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
     *          flag whether agent agentListeners ought to be notified of the changed attribute
     */
    private void changeAttribute(AgentAttributes attribute, Object oldValue, Object newValue, boolean notify) {
        super.changeAttribute(attribute.toString(), newValue);
        if (notify) {
            notifyAttributeChanged(attribute, oldValue, newValue);
        }
    }

    /**
     * Removes an attribute and notifies the agentListeners of the removed attribute by default.
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
     *          flag whether agent agentListeners ought to be notified of the added attribute
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
    public Utility getUtilityWith(Agent with) {
    	Set<Agent> withs = new HashSet<Agent>();
    	withs.add(with);
        return this.getUtilityFunction().getUtilityWith(this, withs);
    }

    /**
     * Gets the utility for a agent excluding an agent as connection.
     *
     * @param without
     *          the agent to exclude as direct connection
     * @return the utility for a agent based on a list of connections
     */
    public Utility getUtilityWithout(Agent without) {
    	Set<Agent> withouts = new HashSet<Agent>();
    	withouts.add(without);
        return this.getUtilityFunction().getUtilityWithout(this, withouts);
    }

    /**
     * Gets the utility for an agent including and excluding agents as connections.
     *
     * @param withs
     * 			the agents to include as direct connections
     * @param withouts
     *          the agents to exclude as direct connections
     * @return the utility for a agent based on a list of connections
     */
    public Utility getUtility(Set<Agent> withs, Set<Agent> withouts) {
        return this.getUtilityFunction().getUtility(this, withs, withouts);
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
     * Updates the agent's risk factor for disease severity.
     * @param rSigma
     *          the new factor for disease severity
     */
    public void updateRSigma(double rSigma) {
        this.changeAttribute(AgentAttributes.RISK_FACTOR_SIGMA, this.getRPi(), rSigma);
    }

    /**
     * Updates the agent's risk scores.
     * @param r
     *          the new risk score
     */
    public void updateRiskScores(double r) {
        this.updateRSigma(r);
        this.updateRPi(r);
    }

    /**
     * Gets the agent's neighborhood's risk factor for disease severity.
     *
     * @return the agent's neighborhood's risk factor for disease severity
     */
    public double getRSigmaNeighborhood() {

        double rSigmaNeighbors = 0.0;

        Iterator<Agent> it = this.getNeighborNodeIterator();
        int neighbors = 0;
        while (it.hasNext()) {
            Agent neighbor = it.next();
            rSigmaNeighbors += neighbor.getRSigma();
            neighbors++;
        }

        return rSigmaNeighbors / neighbors;
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
     * Updates the agent's risk factor for probability of infections.
     * @param rPi
     *          the new factor for probability of infections
     */
    public void updateRPi(double rPi) {
        this.changeAttribute(AgentAttributes.RISK_FACTOR_PI, this.getRPi(), rPi);
    }

    /**
     * Gets the agent's neighborhood's risk factor for probability of infections.
     *
     * @return the agent's neighborhood's risk factor for probability of infections
     */
    public double getRPiNeighborhood() {

        double rPiNeighbors = 0.0;

        Iterator<Agent> it = this.getNeighborNodeIterator();
        int neighbors = 0;
        while (it.hasNext()) {
            Agent neighbor = it.next();
            rPiNeighbors += neighbor.getRPi();
            neighbors++;
        }

        return rPiNeighbors / neighbors;
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
     * Gets the agent's utility function.
     *
     * @return the agent's utility function
     */
    public UtilityFunction getUtilityFunction() {
        return (UtilityFunction) this.getAttribute(AgentAttributes.UTILITY_FUNCTION);
    }

    /**
     * Updates the agent's utility function.
     *
     * @param uf
     *          the utility function
     */
    public void updateUtilityFunction(UtilityFunction uf) {
        if (uf.getClass().equals(this.getUtilityFunction().getClass())) {
            this.changeAttribute(AgentAttributes.UTILITY_FUNCTION, this.getUtilityFunction(), uf);
        } else {
            logger.warn("Invalid utility function: " + uf.getClass() + ". Update aborted.");
        }
    }

    /**
     * Updates the agent's disease specs.
     *
     * @param disease
     *          the diseaseSpecs
     */
    public void updateDisease(DiseaseSpecs disease) {
        this.changeAttribute(AgentAttributes.DISEASE_SPECS, this.getDiseaseSpecs(), disease);
    }

    public void updateAgentSelection(double phi, double psi, double xi) {
        this.changeAttribute(AgentAttributes.PHI, this.getPhi(), phi);
        this.changeAttribute(AgentAttributes.PSI, this.getPsi(), psi);
        this.changeAttribute(AgentAttributes.XI, this.getXi(), xi);
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
     * Gets the agent's co-agent ids sorted by distance, starting with the closest agents.
     *
     * @return the agent's co-agent ids sorted by distance, starting with the closest agents
     */
    public TreeMap<Integer, List<String>> getCoAgentsByDistance() {

        TreeMap<Integer, List<String>> coAgentsByDistance = new TreeMap<Integer, List<String>>();

        List<Agent> coAgents = new ArrayList<Agent>(getNetwork().getAgents());
        coAgents.remove(this);

        Iterator<Agent> aIt = coAgents.iterator();
        while (aIt.hasNext()) {
            Agent coAgent = aIt.next();
            Integer dist = this.getGeodesicDistanceTo(coAgent);
            List<String> currCoAgentsByDistance = coAgentsByDistance.get(dist);
            if (currCoAgentsByDistance == null) {
                currCoAgentsByDistance = new ArrayList<>();
            }
            currCoAgentsByDistance.add(coAgent.getId());
            coAgentsByDistance.put(dist, currCoAgentsByDistance);
        }

        return coAgentsByDistance;
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
     * Gets whether the agent considers age when selecting a peer for network decisions.
     *
     * @return true when the agent considers age, false otherwise
     */
    public boolean considerAge() {
        return (boolean) this.getAttribute(AgentAttributes.CONSIDER_AGE);
    }

    /**
     * Gets the agent's profession.
     *
     * @return the agent's profession
     */
    public String getProfession() {
        return (String) this.getAttribute(AgentAttributes.PROFESSION);
    }

    /**
     * Gets whether the agent considers profession when selecting a peer for network decisions.
     *
     * @return true when the agent considers profession, false otherwise
     */
    public boolean considerProfession() {
        return (boolean) this.getAttribute(AgentAttributes.CONSIDER_PROFESSION);
    }

    /**
     * Gets whether the agent is quarantined or not.
     *
     * @return true when the agent is quarantined, false otherwise
     */
    public boolean isQuarantined() {
        return (boolean) this.getAttribute(AgentAttributes.QUARANTINED);
    }

    /**
     * Gets the second order degree.
     *
     * @return the second order degree
     */
    public double getSecondOrderDegree() {
        return StatsComputer.computeSecondOrderDegree(this);
    }

    private int getClosenessLastComputation() {
        return (int) this.getAttribute(AgentAttributes.CLOSENESS_LAST_COMPUTATION);
    }

    /**
     * Gets the closeness.
     *
     * @param simRound
     *          the simulation round to compute closeness for
     * @return the closeness
     */
    public double getCloseness(int simRound) {
        int lastComputation = this.getClosenessLastComputation();
        if (lastComputation < simRound) {

            this.changeAttribute(AgentAttributes.CLOSENESS,
                    this.getAttribute(AgentAttributes.CLOSENESS),
                    StatsComputer.computeCloseness(this));
            this.changeAttribute(AgentAttributes.CLOSENESS_LAST_COMPUTATION,
                    lastComputation,
                    simRound);
        }
        return (double) this.getAttribute(AgentAttributes.CLOSENESS);
    }

    private int getClusteringLastComputation() {
        return (int) this.getAttribute(AgentAttributes.CLUSTERING_LAST_COMPUTATION);
    }

    /**
     * Gets the clustering.
     *
     * @param simRound
     *          the simulation round to compute closeness for
     * @return the clustering
     */
    public double getClustering(int simRound) {
        int lastComputation = this.getClusteringLastComputation();
        if (lastComputation < simRound) {
            this.changeAttribute(AgentAttributes.CLUSTERING,
                    this.getAttribute(AgentAttributes.CLUSTERING),
                    Toolkit.clusteringCoefficient(this));
            this.changeAttribute(AgentAttributes.CLUSTERING_LAST_COMPUTATION,
                    lastComputation,
                    simRound);
        }
        return (double) this.getAttribute(AgentAttributes.CLUSTERING);
    }


    private int getBetweennessLastComputation() {
        return (int) this.getAttribute(AgentAttributes.BETWEENNESS_LAST_COMPUTATION);
    }

    /**
     * Gets the betweenness.
     *
     * @param simRound
     *          the simulation round to compute betweenness for
     * @return the betweenness
     */
    public double getBetweenness(int simRound) {
        int lastComputation = this.getBetweennessLastComputation();
        if (lastComputation < simRound) {
            BetweennessCentrality bc = new BetweennessCentrality();
            bc.setUnweighted();
            bc.init(this.getNetwork());
            bc.compute();
            this.changeAttribute(AgentAttributes.BETWEENNESS,
                    this.getAttribute(AgentAttributes.BETWEENNESS),
                    bc.centrality(this));
            this.changeAttribute(AgentAttributes.BETWEENNESS_LAST_COMPUTATION, lastComputation, simRound);
        }
        return (double) this.getAttribute(AgentAttributes.BETWEENNESS);
    }

    /**
     * Gets the normalized betweenness.
     *
     * @param simRound
     *          the simulation round to compute betweenness for
     * @return the normalized betweenness
     */
    public double getBetweennessNormalized(int simRound) {
        double onShortestPaths = this.getBetweenness(simRound);
        double N = this.getNetwork().getN();
        return onShortestPaths / (((N-1)/2) * (N-2));
    }

    public void setInitialIndexCaseDistance(Agent indexCase) {
        this.addAttribute(AgentAttributes.INITIAL_INDEX_CASE_DISTANCE, this.getGeodesicDistanceTo(indexCase));
    }

    public Integer getInitialIndexCaseDistance() {
        return (Integer) this.getAttribute(AgentAttributes.INITIAL_INDEX_CASE_DISTANCE);
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
        if (this.getId().equals(agent.getId())) {
            return 0;
        }
        DijkstraShortestPath dsp = new DijkstraShortestPath();
        dsp.executeShortestPaths(this);
        return dsp.getShortestPathLength(agent);
    }

    private int getAssortativityLastComputation(AssortativityConditions ac) {
        switch (ac) {
            case RISK_PERCEPTION:
                return (int) this.getAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION_LAST_COMPUTATION);
            case AGE:
                return (int) this.getAttribute(AgentAttributes.ASSORTATIVITY_AGE_LAST_COMPUTATION);
            case PROFESSION:
                return (int) this.getAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION_LAST_COMPUTATION);
            default:
                logger.warn("Unknown assortativity condition: " + ac);
                return -1;
        }
    }

    private void updateAssortativity(AssortativityConditions ac, int simRound, int lastComputation) {
        switch (ac) {
            case RISK_PERCEPTION:
                this.changeAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION,
                        this.getAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION),
                        StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.RISK_PERCEPTION));
                this.changeAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION_LAST_COMPUTATION,
                        lastComputation,
                        simRound);
                break;

            case AGE:
                this.changeAttribute(AgentAttributes.ASSORTATIVITY_AGE,
                        this.getAttribute(AgentAttributes.ASSORTATIVITY_AGE),
                        StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.AGE));

                this.changeAttribute(AgentAttributes.ASSORTATIVITY_AGE_LAST_COMPUTATION,
                        lastComputation,
                        simRound);
                break;

            case PROFESSION:
                this.changeAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION,
                        this.getAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION),
                        StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.PROFESSION));

                this.changeAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION_LAST_COMPUTATION,
                        lastComputation,
                        simRound);
                break;

            default:
                logger.warn("Unknown assortativity condition: " + ac);
        }
    }

    /**
     * Gets the assortativity of the agent.
     *
     * @param simRound
     *          the current simulation round
     * @param ac
     *          the assortativity condition
     * @return the assortativity
     */
    public double getAssortativity(int simRound, AssortativityConditions ac) {
        int lastComputation = this.getAssortativityLastComputation(ac);

        if (lastComputation < simRound) {
            updateAssortativity(ac, simRound, lastComputation);
        }

        switch (ac) {
            case RISK_PERCEPTION:
                return (double) this.getAttribute(AgentAttributes.ASSORTATIVITY_RISK_PERCEPTION);
            case AGE:
                return (double) this.getAttribute(AgentAttributes.ASSORTATIVITY_AGE);
            case PROFESSION:
                return (double) this.getAttribute(AgentAttributes.ASSORTATIVITY_PROFESSION);
            default:
                logger.warn("Unknown assortativity condition: " + ac);
        }
        return -1;
    }

    /**
     * Keeps track of actively broken ties.
     */
    private void trackBrokenTieActive() {
        // stats
        AgentConnectionStats oldConnectionStats = getConnectionStats().clone();
        AgentConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incBrokenTiesActive();
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incBrokenTiesActiveEpidemic();
        }
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
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incBrokenTiesPassiveEpidemic();
        }
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
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incAcceptedRequestsOutEpidemic();
        }
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
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incDeclinedRequestsOutEpidemic();
        }
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
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incAcceptedRequestsInEpidemic();
        }
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
        if (this.getNetwork().hasActiveInfection()) {
            newConnectionStats.incDeclinedRequestsInEpidemic();
        }
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
    public void computeRound() {
        this.computeRound(0);
    }

    /**
     * Computes a single round for an agent. That is, an {@link Agent} tries to connect to
     * or disconnects from another {@link Agent} if it produces higher utility.
     *
     * @param delay
     *          animation delay between two network decisions
     */
    public void computeRound(int delay) {

        // starting assumption: current connections are not satisfactory
        boolean satisfied = true;

        int decisions = this.getNumberOfNetworkDecisions();

        // helper collections
        // agents that have been processed before
        Set<Agent> agentsProcessed = new HashSet<Agent>(decisions);
        // distance 1
        List<Agent> distance1AgentsAssorted = new ArrayList<Agent>(this.getConnections());
        sortByAssortativityConditions(distance1AgentsAssorted);
        List<Agent> distance1AgentsShuffled = new ArrayList<Agent>(this.getConnections());
        Collections.shuffle(distance1AgentsShuffled);
        // distance 2
        List<Agent> distance2AgentsAssorted = new ArrayList<Agent>(this.getConnectionsAtDistance2());
        sortByAssortativityConditions(distance2AgentsAssorted);
        List<Agent> distance2AgentsShuffled = new ArrayList<Agent>(this.getConnectionsAtDistance2());
        Collections.shuffle(distance2AgentsShuffled);
        // any agents
        List<Agent> allAgentsAssorted = new ArrayList<Agent>(this.getNetwork().getAgents());
        sortByAssortativityConditions(allAgentsAssorted);
        List<Agent> allAgentsShuffled = new ArrayList<Agent>(this.getNetwork().getAgents());
        Collections.shuffle(allAgentsShuffled);

        while (agentsProcessed.size() < decisions) {

            // some delay before processing of each other agent (e.g., for animation processes)
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }

            // creating a base list of agents to draw an agent from
            Agent other = null;
            double randPsi = ThreadLocalRandom.current().nextDouble();
            double randOmega = ThreadLocalRandom.current().nextDouble();
            HashSet<Agent> removals = new HashSet<Agent>(agentsProcessed);
            removals.add(this);
            List<Agent> drawBase = null;
            // distance 1
            if (randPsi <= this.getPsi()) {
                if (randOmega <= this.getOmega()) {
                    drawBase = distance1AgentsAssorted;
                } else {
                    drawBase = distance1AgentsShuffled;
                }
                drawBase.removeAll(removals);
            }
            // distance 2
            if (randPsi <= (this.getPsi() + this.getXi())) {
                if (randOmega <= this.getOmega()) {
                    drawBase = distance2AgentsAssorted;
                } else {
                    drawBase = distance2AgentsShuffled;
                }

                drawBase.removeAll(removals);
            }
            // all agents
            if (drawBase == null || drawBase.isEmpty() || randPsi > (this.getPsi() + this.getXi())) {
                if (randOmega <= this.getOmega()) {
                    drawBase = allAgentsAssorted;
                } else {
                    drawBase = allAgentsShuffled;
                }
                drawBase.removeAll(removals);
            }

            // draw agent to process
            if (drawBase.isEmpty()) {
                logger.warn("No other agent found to process for agent " + this.getId());
                return;
            }
            other = drawBase.get(0);

            // processing drawn agent
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
            agent.trackAcceptedRequestIn();
            return true;
        }
        trackDeclinedRequestOut();
        agent.trackDeclinedRequestIn();
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
     * Gets the normalized assortativity difference (0.0 - 1.0) between this agent
     * and another agent depending on the assortativity condition.
     *
     * @param otherAgent
     *          the agents to compute the assortativity difference for
     * @return the normalized assortativity difference (0.0 - 1.0) between this agent and another agent
     */
    private double getNormalizedAssortativityDiff(Agent otherAgent, AssortativityConditions ac) {
        switch (ac) {
            case RISK_PERCEPTION:
                return Math.abs((this.getRPi() + this.getRSigma()) - (otherAgent.getRPi() + otherAgent.getRSigma())) /
                        (this.getNetwork().getMaxRPi() + this.getNetwork().getMaxRSigma());

            case AGE:
                return ((double) Math.abs(this.getAge() - otherAgent.getAge())) / ((double) this.getNetwork().getMaxAge());

            case PROFESSION:
                return Math.abs(this.getProfession().equals(otherAgent.getProfession()) ? 0 : 1);

            default:
                logger.warn("Unimplemented assortativity condition: " + ac);
        }
        return -1;
    }

    /**
     * Creates a map of normalized assortativity differences (key) and list of agents with the same difference (value)
     * for a given list of agents.
     *
     * @param agents
     *          the list of agents to create the assortativity difference map for
     * @return a map of assortativity differences (key) and list of agents with the same difference (value)
     */
    private TreeMap<Double, List<Agent>> getNormalizedAssortativityDiffMap(List<Agent> agents, AssortativityConditions ac) {
        TreeMap<Double, List<Agent>> diffMap = new TreeMap<Double, List<Agent>>();
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);

            double diff = getNormalizedAssortativityDiff(agent, ac);

            if (diffMap.containsKey(diff)) {
                List<Agent> list = diffMap.get(diff);
                list.add(agent);
                diffMap.put(diff, list);
            } else {
                List<Agent> list = new ArrayList<Agent>();
                list.add(agents.get(i));
                diffMap.put(diff, list);
            }
        }
        return diffMap;
    }



    // TODO comments!!!
    private TreeMap<Double, List<Agent>> reduceMaps(TreeMap<Double, List<Agent>> map1, TreeMap<Double, List<Agent>> map2) {

        TreeMap<Double, List<Agent>> reducedMap = new TreeMap<Double, List<Agent>>();
        double maxNewKey = 0.0;

        Iterator<Double> key1It = map1.keySet().iterator();
        while (key1It.hasNext()) {
            Double key1 = key1It.next();
            List<Agent> agents1 = map1.get(key1);


            Iterator<Double> key2It = map2.keySet().iterator();
            while (key2It.hasNext()) {
                Double key2 = key2It.next();
                List<Agent> agents2 = map2.get(key2);


                Iterator<Agent> agents2It = agents2.iterator();
                while (agents2It.hasNext()) {
                    Agent agent = agents2It.next();

                    if (agents1.contains(agent)) {
                        Double newKey = key1 + key2;

                        List<Agent> list = reducedMap.get(newKey);
                        if (list != null) {
                            list.add(agent);
                        } else {
                            list = new ArrayList<Agent>();
                            list.add(agent);
                            reducedMap.put(newKey, list);
                        }

                        if (newKey > maxNewKey) {
                            maxNewKey = newKey;
                        }
                    }
                }
            }
        }

        // normalizing keys
        TreeMap<Double, List<Agent>> normReducedMap = new TreeMap<Double, List<Agent>>();
        Iterator<Double> it = reducedMap.keySet().iterator();
        while (it.hasNext()) {
            Double key = it.next();
            normReducedMap.put(key/maxNewKey, reducedMap.get(key));
        }

        return normReducedMap;
    }

    /**
     * Sorts the given list of agents by differences of assortativity conditions.
     *
     * @param agents
     *          the list of agents to sort
     */
    public void sortByAssortativityConditions(List<Agent> agents) {

        // struct used to create order depending on order of assortativity conditions
        // first level: list per assortativity condition
        // second level: map of assortativity differences (key) and list of agents with the same difference (value)
        List<TreeMap<Double, List<Agent>>> diffList = new ArrayList<TreeMap<Double, List<Agent>>>();

        Iterator<AssortativityConditions> acsIt = this.getNetwork().getAssortativityConditions().iterator();
        while (acsIt.hasNext()) {
            AssortativityConditions ac = acsIt.next();
            diffList.add(getNormalizedAssortativityDiffMap(agents, ac));
        }

        for (int i = diffList.size()-1; i > 0; i--) {
            // combine last two maps into one, while combining the ordering of the two
            diffList.add(reduceMaps(diffList.remove(i), diffList.remove(i-1)));
        }

        if (diffList.size() > 1) {
            logger.error("Error during reduction of assortativity order maps.");
            return;
        }

        // order list of agents according to assortativity order maps
        TreeMap<Double, List<Agent>> map = diffList.get(0);
        int index = 0;
        for (Map.Entry<Double, List<Agent>> entry : map.entrySet()) {
            List<Agent> list = map.get(entry.getKey());
            for (int i = 0; i < list.size(); i++) {
                    agents.set(index++, list.get(i));
            }
        }
    }

    private List<Agent> getRandomListOfAgents(List<Agent> agents, int amount) {
        List<Agent> res = new ArrayList<Agent>(amount);

        if (agents != null && !agents.isEmpty()) {

            // fill (omega share) according assortativity condition
            sortByAssortativityConditions(agents);
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
     * Checks whether the agent was infected from outside the network.
     *
     * @return true if the agent was infected from outside the network, false otherwise
     */
    public boolean isForceInfected() {
        return (boolean) this.getAttribute(AgentAttributes.FORCE_INFECTED);
    }

    /**
     * Gets the simulation round the agent was infected.
     *
     * @return the simulation round the agent was infected
     */
    public int getWhenInfected() {
        return (int) this.getAttribute(AgentAttributes.WHEN_INFECTED);
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
     * Checks whether the agent is vaccinated.
     *
     * @return true if the agent is vaccinated, false otherwise
     */
    public boolean isVaccinated() {
        return this.getDiseaseGroup() == DiseaseGroup.VACCINATED;
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

        this.addAttribute(AgentAttributes.WHEN_INFECTED, -1);
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
     *
     * @param simRound
     *          the simulation round of the infection
     */
    public void computeDiseaseTransmission(int simRound) {
        if (this.isSusceptible()) {
            int nI = StatsComputer.computeLocalAgentConnectionsStats(this).getnI();
            if (ThreadLocalRandom.current().nextDouble() <=
                    StatsComputer.computeProbabilityOfInfection(this, nI)) {
                this.infect(this.getDiseaseSpecs(), simRound);
            }
        }
    }

    /**
     * Infects the agent with a disease.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the agent is infected with
     * @param simRound
     *          the simulation round of the infection
     */
    public void infect(DiseaseSpecs diseaseSpecs, int simRound) {
        if (isRecovered()) {
            return;
        }
        this.infect(diseaseSpecs, false, simRound);
    }

    /**
     * Forces an infection onto the agent no matter whether the agent is immune or not.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the agent is infected with
     */
    public void forceInfect(DiseaseSpecs diseaseSpecs) {
        this.infect(diseaseSpecs, true, 0);
    }

    /**
     * Forces an infection onto the agent no matter whether the agent is immune or not.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the agent is infected with
     * @param simRound
     *          the simulation round of the infection
     */
    public void forceInfect(DiseaseSpecs diseaseSpecs, int simRound) {
        this.infect(diseaseSpecs, true, simRound);
    }

    private void infect(DiseaseSpecs diseaseSpecs, boolean forceInfect, int simRound) {
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

        if (forceInfect) {
            // set force infected flag
            this.changeAttribute(AgentAttributes.FORCE_INFECTED, false, true);
        }

        this.changeAttribute(AgentAttributes.WHEN_INFECTED, -1, simRound);
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

    /**
     * Vaccinates the agent.
     */
    public void vaccinate() {
        // cure
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        this.changeAttribute(AgentAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.VACCINATED);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(AgentAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.VACCINATED.toString(), false);
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
     * Notifies agentListeners of added attributes.
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
     * Notifies agentListeners of changed attributes.
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
     * Notifies agentListeners of removed attributes.
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
     * Notifies agentListeners of added connections.
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
     * Notifies the agentListeners of removed edges.
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
     * Notifies agentListeners of finished agent rounds.
     */
    private final void notifyRoundFinished() {
        Iterator<AgentListener> listenersIt = this.agentListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRoundFinished(this);
        }
    }

    /**
     * XXX Workaround: graphstream seems to use standard equals method (Object.equals) for tests before adding nodes
     * to the network. This method was intended to replace the equals method, but caused conflicts with simple addition
     * of nodes. Thus, it is only used for explicit calls to test agent (and not node) equality.
     *
     * @param o
     *          the object to test for equality
     * @return true if this and the provided objects are equal, false otherwise
     */
    public boolean same(Object o) {

        // same object
        if (o == this) {
            return true;
        }

        // same type
        if (!(o instanceof Agent)) {
            return false;
        }

        Agent a = (Agent) o;

        // same attributes
        Iterator<String> akIt = this.getAttributeKeyIterator();
        while (akIt.hasNext()) {
            String ak = akIt.next();
            if (ak.equals("ui.label") || ak.equals("xyz")) {
                continue;
            }

            Object thisAttribute = this.getAttribute(ak);
            Object otherAttribute = a.getAttribute(ak);

            if (!thisAttribute.equals(otherAttribute)) {
                logger.warn("Agents unequal: " + ak + "\tthis: " + thisAttribute + "\tother: " + otherAttribute);
                return false;
            }
        }

        return true;
    }

}
