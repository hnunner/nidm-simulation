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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentFactory;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.stats.DijkstraShortestPath;
import nl.uu.socnetid.nidm.stats.StatsComputer;
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
    // standard proportion of direct ties to evaluate per agent
    private static final double STANDARD_XI = 0.25;
    // standard share to select assortatively
    private static final double STANDARD_OMEGA = 0.0;
    // standard shuffling of assortatively selected co-agents
    private static final List<AssortativityConditions> STANDARD_ACS = Arrays.asList(AssortativityConditions.RISK_PERCEPTION);
    // stability
    private static final int TIMESTEPS_REQUIRED_FOR_STABILITY = 1;

    // listeners
    private Set<NetworkListener> networkListeners = new CopyOnWriteArraySet<NetworkListener>();


    /**
     * Constructor.
     */
    public Network() {
        this("Network of the Infectious Kind", false, STANDARD_ACS);
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     */
    public Network(String id) {
        this(id, false, STANDARD_ACS);
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
        this(id, arrangeInCircle, STANDARD_ACS);
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     * @param acs
     *          the conditions to realize assortativity for
     */
    public Network(String id, List<AssortativityConditions> acs) {
        this(id, false, acs);
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     * @param arrangeInCircle
     *          flag whether agents to arrange in circle or not
     * @param acs
     *          the conditions to realize assortativity for
     */
    public Network(String id, boolean arrangeInCircle, List<AssortativityConditions> acs) {
        super(id);

        this.addAttribute(NetworkAttributes.ARRANGE_IN_CIRCLE, arrangeInCircle, false);

        this.addAttribute(NetworkAttributes.TIMESTEPS_STABLE, -1, false);
        this.addAttribute(NetworkAttributes.STABLE, false, false);

        this.addAttribute(NetworkAttributes.AV_PATH_LENGTH_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.AV_PATH_LENGTH, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_CLUSTERING_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.AV_CLUSTERING, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE_2, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE_THEORETIC, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE_SATISFIED, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DEGREE_UNSATISFIED, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_BETWEENNESS_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.AV_BETWEENNESS, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_CLOSENESS_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.AV_CLOSENESS, -1.0, false);

        this.addAttribute(NetworkAttributes.MAX_R_PI, -1.0, false);
        this.addAttribute(NetworkAttributes.MAX_R_SIGMA, -1.0, false);
        this.addAttribute(NetworkAttributes.MAX_AGE, -1, false);

        this.addAttribute(NetworkAttributes.AV_UTILITY, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_SOCIAL_BENEFITS, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_SOCIAL_COSTS, -1.0, false);
        this.addAttribute(NetworkAttributes.AV_DISEASE_COSTS, -1.0, false);
        this.addAttribute(NetworkAttributes.N, -1, false);
        this.addAttribute(NetworkAttributes.HAS_ACTIVE_INFECTION, false, false);

        this.addAttribute(NetworkAttributes.EMPTY, true, false);
        this.addAttribute(NetworkAttributes.FULL, false, false);
        this.addAttribute(NetworkAttributes.RING, false, false);
        this.addAttribute(NetworkAttributes.STAR, false, false);

        // TODO create assortativity interface, use list filled with instanciations of subclasses here
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_CONDITIONS, acs, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION, 0.0, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_AGE_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_AGE, 0.0, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION_ROUND_LAST_COMPUTATION, -1, false);
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION, 0.0, false);

        this.setNodeFactory(new AgentFactory());
    }

    /**
     * Reinitializes network after reader import. That is, types are reinitialized from their string representation.
     */
    public void reinitAfterRead() {
        String acsString = (String) this.getAttribute(NetworkAttributes.ASSORTATIVITY_CONDITIONS);
        acsString = acsString.replace("[", "");
        acsString = acsString.replace("]", "");
        acsString = acsString.replaceAll("\\s+","");
        String[] acsStringSplit = acsString.split(",");
        List<AssortativityConditions> acs = new ArrayList<AssortativityConditions>();
        for (String a : acsStringSplit) {
            acs.add(AssortativityConditions.fromString(a));
        }
        this.addAttribute(NetworkAttributes.ASSORTATIVITY_CONDITIONS, acs, false);

        Iterator<Agent> aIt = this.getAgentIterator();
        while (aIt.hasNext()) {
            Agent agent = aIt.next();
            agent.reinitAfterRead();
        }
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
        // TODO clean up addAgent methods -- too many special cases!!!
        return this.addAgent(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL, RISK_FACTOR_NEUTRAL, STANDARD_PHI,
                STANDARD_OMEGA, STANDARD_PSI, STANDARD_XI, AgeStructure.getInstance().getRandomAge(), false, "NA", false);
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
        return this.addAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi, omega, STANDARD_PSI,
                STANDARD_XI, AgeStructure.getInstance().getRandomAge(), false, "NA", false);
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
     * @param xi
     *          the proportion of ties at distance 2 an agent evaluates per round
     * @param age
     *          the agent's age
     * @param considerAge
     *          whether age is considered for peer selection or not
     * @param profession
     *          the agent's profession
     * @param considerProfession
     *          whether profession is considered for peer selection or not
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi,
            double omega, double psi, double xi, int age, boolean considerAge, String profession, boolean considerProfession) {
        Agent agent = this.addNode(String.valueOf(this.getNodeCount() + 1));

        // age randomly drawn from /resources/age-dist.csv
        agent.initAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi, psi, xi, omega, age, considerAge,
                profession, considerProfession);
        notifyAgentAdded(agent);

        // re-position agents if auto-layout is disabled
        if (this.isArrangeInCircle()) {
            arrangeAgentsInCircle();
        }

        this.setMaxRPi();
        this.setMaxRSigma();
        this.setMaxAge();

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
        this.removeNode(agentId);
        notifyAgentRemoved(agentId);

        // re-position agents if auto-layout is disabled
        if (!this.isArrangeInCircle()) {
            arrangeAgentsInCircle();
        }

        setMaxAge();
        setMaxRPi();
        setMaxRSigma();

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
     * Gets the value of an attribute.
     *
     * TODO create super class for agent and network that allows joined attribute handling
     *
     * @param attribute
     *          the attribute to get
     * @return the value of the attribute
     */
    private Object getAttribute(NetworkAttributes attribute) {
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
    @SuppressWarnings("unused")
    private void addAttribute(NetworkAttributes attribute, Object value) {
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
     *          flag whether network listeners ought to be notified of the added attribute
     */
    private void addAttribute(NetworkAttributes attribute, Object value, boolean notify) {
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
    private void changeAttribute(NetworkAttributes attribute, Object oldValue, Object newValue) {
        if (!newValue.equals(oldValue)) {
            this.changeAttribute(attribute, oldValue, newValue, true);
        }
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
     *          flag whether network listeners ought to be notified of the changed attribute
     */
    private void changeAttribute(NetworkAttributes attribute, Object oldValue, Object newValue, boolean notify) {
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
    @SuppressWarnings("unused")
    private void removeAttribute(NetworkAttributes attribute) {
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
     *          flag whether network listeners ought to be notified of the added attribute
     */
    private void removeAttribute(NetworkAttributes attribute, boolean notify) {
        super.removeAttribute(attribute.toString());
        if (notify) {
            notifyAttributeRemoved(attribute);
        }
    }

    /**
     * Notifies listeners of added attributes.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the attribute's value
     */
    private final void notifyAttributeAdded(NetworkAttributes attribute, Object value) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
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
    private final void notifyAttributeChanged(NetworkAttributes attribute, Object oldValue, Object newValue) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
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
    private final void notifyAttributeRemoved(NetworkAttributes attribute) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeRemoved(this, attribute.toString());
        }
    }

    /**
     * Gets whether the network is arranged in circle.
     *
     * @return true if the network is arranged in circle, false otherwise
     */
    public boolean isArrangeInCircle() {
        return (boolean) this.getAttribute(NetworkAttributes.ARRANGE_IN_CIRCLE);
    }

    /**
     * Sets whether the network is arranged in circle.
     *
     * @param arrangeInCircle
     *          true to arrange network in circle, false otherwise
     */
    public void setArrangeInCircle(boolean arrangeInCircle) {
        this.changeAttribute(NetworkAttributes.ARRANGE_IN_CIRCLE, this.getAttribute(NetworkAttributes.ARRANGE_IN_CIRCLE),
                arrangeInCircle);
    }

    /**
     * Gets the number of time steps the network is stable.
     *
     * @return the number of time steps the network is stable
     */
    public int getTimestepsStable() {
        return (int) this.getAttribute(NetworkAttributes.TIMESTEPS_STABLE);
    }

    /**
     * Resets the number of time steps the network is stable to 0.
     */
    public void resetTimestepsStable() {
        this.changeAttribute(NetworkAttributes.TIMESTEPS_STABLE, this.getAttribute(NetworkAttributes.TIMESTEPS_STABLE), 0);
    }

    /**
     * Gets the round number average path length has been computed last.
     *
     * @return the round number average path length has been computed last
     */
    public int getAvPathLengthRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.AV_PATH_LENGTH_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the average path length.
     *
     * @return the average path length
     */
    public double getAvPathLength() {
        this.changeAttribute(NetworkAttributes.AV_PATH_LENGTH, this.getAttribute(NetworkAttributes.AV_PATH_LENGTH),
                computeAvPathLength());
        return (double) this.getAttribute(NetworkAttributes.AV_PATH_LENGTH);
    }

    /**
     * Gets the round number average clustering has been computed last.
     *
     * @return the round number average clustering has been computed last
     */
    public int getAvClusteringRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.AV_CLUSTERING_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the average clustering.
     *
     * @return the average clustering
     */
    public double getAvClustering() {
        this.changeAttribute(NetworkAttributes.AV_CLUSTERING, this.getAttribute(NetworkAttributes.AV_CLUSTERING),
                Toolkit.averageClusteringCoefficient(this));
        return (double) this.getAttribute(NetworkAttributes.AV_CLUSTERING);
    }

    /**
     * Gets the round number average degree has been computed last.
     *
     * @return the round number average degree has been computed last
     */
    public int getAvDegreeRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.AV_DEGREE_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the average degree.
     *
     * @return the average degree
     */
    public double getAvDegree() {
        this.changeAttribute(NetworkAttributes.AV_DEGREE, this.getAttribute(NetworkAttributes.AV_DEGREE),
                Toolkit.averageDegree(this));
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE);
    }

    /**
     * Gets the average degree of the network.
     *
     * @param simRound
     *          the simulation round to get the average degree for
     * @return the average degree of the network
     */
    public double getAvDegree(int simRound) {
        if (this.getAvDegreeRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.AV_DEGREE, this.getAttribute(NetworkAttributes.AV_DEGREE),
                    Toolkit.averageDegree(this));
            this.changeAttribute(NetworkAttributes.AV_DEGREE_ROUND_LAST_COMPUTATION, this.getAvDegreeRoundLastComputation(),
                    simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE);
    }


    // TODO testcase
    // TODO comments
    public Map<String, Double> getAvDegreesByProfessions() {

        HashMap<String, Double> degreesByProfessions = new HashMap<String, Double>();
        Iterator<String> professionsIt = Professions.getInstance().getProfessionsIterator();

        while (professionsIt.hasNext()) {
            String profession = professionsIt.next();
            Iterator<Agent> agentIt = this.getAgentIterator();

            double degree = 0.0;
            double agents = 0;

            while (agentIt.hasNext()) {
                Agent agent = agentIt.next();
                if (agent.getProfession().equals(profession)) {
                    degree += agent.getDegree();
                    agents++;
                }
            }
            degreesByProfessions.put(profession, degree / agents);
        }
        return degreesByProfessions;
    }

    // TODO testcase
    // TODO comments
    public Map<String, Double> getDegreesSdByProfessions() {

        HashMap<String, Double> degreesSdByProfessions = new HashMap<String, Double>();
        Iterator<String> professionsIt = Professions.getInstance().getProfessionsIterator();

        while (professionsIt.hasNext()) {
            String profession = professionsIt.next();
            Iterator<Agent> agentIt = this.getAgentIterator();

            double[] degrees = new double[this.getN()];
            int i = 0;

            while (agentIt.hasNext()) {
                Agent agent = agentIt.next();
                if (agent.getProfession().equals(profession)) {
                    degrees[i++] = agent.getDegree();
                }
            }
            StandardDeviation sd = new StandardDeviation();
            degreesSdByProfessions.put(profession, sd.evaluate(degrees));
        }
        return degreesSdByProfessions;
    }

    /**
     * Gets the the theoretic average degree of the network (dependent on the utility function of the agents).
     *
     * @return the theoretic average degree of the network
     */
    public double getTheoreticAvDegree() {
        if (this.getN() == 0) {
            this.changeAttribute(NetworkAttributes.AV_DEGREE_THEORETIC, this.getAttribute(NetworkAttributes.AV_DEGREE_THEORETIC),
                    0.0);
        } else {
            double allTheoreticDegrees = 0.0;
            Iterator<Agent> aIt = this.getAgentIterator();
            while (aIt.hasNext()) {
                allTheoreticDegrees += aIt.next().getUtilityFunction().getTheoreticDegree();
            }
            this.changeAttribute(NetworkAttributes.AV_DEGREE_THEORETIC, this.getAttribute(NetworkAttributes.AV_DEGREE_THEORETIC),
                    allTheoreticDegrees / this.getN());
        }
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE_THEORETIC);
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
        this.changeAttribute(NetworkAttributes.AV_DEGREE_2, this.getAttribute(NetworkAttributes.AV_DEGREE_2),
                avDegree2/this.getAgents().size());
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE_2);
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
        this.changeAttribute(NetworkAttributes.AV_DEGREE_SATISFIED, this.getAttribute(NetworkAttributes.AV_DEGREE_SATISFIED),
                avDegree/satisfiedAgents);
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE_SATISFIED);
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
        this.changeAttribute(NetworkAttributes.AV_DEGREE_UNSATISFIED, this.getAttribute(NetworkAttributes.AV_DEGREE_UNSATISFIED),
                avDegree/unsatisfiedAgents);
        return (double) this.getAttribute(NetworkAttributes.AV_DEGREE_UNSATISFIED);
    }

    /**
     * Gets the round number average betweenness has been computed last.
     *
     * @return the round number average betweenness has been computed last
     */
    public int getAvBetweennessRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.AV_BETWEENNESS_ROUND_LAST_COMPUTATION);
    }

    /**
     * Computes the average betweenness.
     *
     * @param simRound
     *          the simulation round to compute betweenness for
     * @return the average betweenness
     */
    private double computeAvBetweenness(int simRound) {
        double avBetweenness = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            avBetweenness += agent.getBetweennessNormalized(simRound);
        }
        return avBetweenness/this.getAgents().size();
    }

    /**
     * Gets the average betweenness.
     *
     * @return the average betweenness
     */
//    public double getAvBetweenness() {
//        return (double) this.getAttribute(NetworkAttributes.AV_BETWEENNESS);
//    }

    /**
     * Gets the average betweenness of the network.
     *
     * @param simRound
     *          the simulation round to compute betweenness for
     * @return the average closeness of the network
     */
    public double getAvBetweenness(int simRound) {
        if (this.getAvBetweennessRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.AV_BETWEENNESS, this.getAttribute(NetworkAttributes.AV_BETWEENNESS),
                    this.computeAvBetweenness(simRound));
            this.changeAttribute(NetworkAttributes.AV_BETWEENNESS_ROUND_LAST_COMPUTATION,
                    this.getAvBetweennessRoundLastComputation(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.AV_BETWEENNESS);
    }

    /**
     * Gets the round number average closeness has been computed last.
     *
     * @return the round number average closeness has been computed last
     */
    public int getAvClosenessRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.AV_CLOSENESS_ROUND_LAST_COMPUTATION);
    }

    /**
     * Computes the average closeness of the network.
     *
     * @param simRound
     *          the simulation round to compute average closeness for
     * @return the average closeness of the network
     */
    private double computeAvCloseness(int simRound) {
        double avCloseness = 0;
        Iterator<Agent> agentsIt = this.getAgents().iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            avCloseness += agent.getCloseness(simRound);
        }
        return avCloseness/this.getAgents().size();
    }

    /**
     * Gets the average closeness.
     *
     * @return the average closeness
     */
//    public double getAvCloseness() {
//        return (double) this.getAttribute(NetworkAttributes.AV_CLOSENESS);
//    }

    /**
     * Gets the average closeness of the network.
     *
     * @param simRound
     *          the simulation round to compute betweenness for
     * @return the average closeness of the network
     */
    public double getAvCloseness(int simRound) {
        if (this.getAvClosenessRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.AV_CLOSENESS, this.getAttribute(NetworkAttributes.AV_CLOSENESS),
                    computeAvCloseness(simRound));
            this.changeAttribute(NetworkAttributes.AV_CLOSENESS_ROUND_LAST_COMPUTATION,
                    this.getAvClosenessRoundLastComputation(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.AV_CLOSENESS);
    }

    /**
     * Gets the maximum risk perception for probability of infections of all agents in the network.
     *
     * @return the maximum risk perception for probability of infections of all agents in the network
     */
    public double getMaxRPi() {
        return (double) this.getAttribute(NetworkAttributes.MAX_R_PI);
    }

    /**
     * Sets the maximum risk perception for probability of infections of all agents in the network.
     */
    private void setMaxRPi() {
        double oldMaxRPi = this.getMaxRPi();
        double newMaxRPi = -1.0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent a = agentIt.next();
            if (a.getRPi() > newMaxRPi) {
                newMaxRPi = a.getRPi();
            }
        }
        this.changeAttribute(NetworkAttributes.MAX_R_PI, oldMaxRPi, newMaxRPi);
    }

    /**
     * Gets the maximum risk perception for disease severity of all agents in the network.
     *
     * @return the maximum risk perception for disease severity of all agents in the network
     */
    public double getMaxRSigma() {
        return (double) this.getAttribute(NetworkAttributes.MAX_R_SIGMA);
    }

    /**
     * Sets the maximum risk perception for disease severity for all agents in the network.
     */
    private void setMaxRSigma() {
        double oldMaxRSigma = this.getMaxRSigma();
        double newMaxRSigma = -1.0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent a = agentIt.next();
            if (a.getAge() > newMaxRSigma) {
                newMaxRSigma = a.getRSigma();
            }
        }
        this.changeAttribute(NetworkAttributes.MAX_R_SIGMA, oldMaxRSigma, newMaxRSigma);
    }

    /**
     * Gets the maximum age of all agents in the network.
     *
     * @return the maximum age of all agents in the network
     */
    public int getMaxAge() {
        return (int) this.getAttribute(NetworkAttributes.MAX_AGE);
    }

    /**
     * Sets the maximum age of all agents in the network.
     */
    private void setMaxAge() {
        int oldMaxAge = this.getMaxAge();
        int newMaxAge = -1;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent a = agentIt.next();
            if (a.getAge() > newMaxAge) {
                newMaxAge = a.getAge();
            }
        }
        this.changeAttribute(NetworkAttributes.MAX_AGE, oldMaxAge, newMaxAge);
    }

    /**
     * Gets the assortativity conditions of the network.
     *
     * @return the assortativity conditions of the network
     */
    @SuppressWarnings("unchecked")
    public List<AssortativityConditions> getAssortativityConditions() {
        return (List<AssortativityConditions>) this.getAttribute(NetworkAttributes.ASSORTATIVITY_CONDITIONS);
    }

    /**
     * Gets the assortativity for risk perception.
     *
     * @return the assorativity for risk perception
     */
    public double getAssortativityRiskPerception() {
        this.changeAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION,
                this.getAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION),
                StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.RISK_PERCEPTION));
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION);
    }

    /**
     * Gets the round when assortativity for risk perception has been computed last.
     *
     * @return the round when assortativity for risk perception has been computed last
     */
    public int getAssortativityRiskPerceptionRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the age assortativity for risk perception.
     *
     * @param simRound
     *          the simulation round to get the assortativity for
     * @return the assortativity for risk perception
     */
    public double getAssortativityRiskPerception(int simRound) {
        if (this.getAssortativityRiskPerceptionRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION,
                    this.getAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION),
                    StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.RISK_PERCEPTION));
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION_ROUND_LAST_COMPUTATION,
                    this.getAssortativityRiskPerceptionRoundLastComputation(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_RISK_PERCEPTION);
    }

    /**
     * Gets the assortativity for age.
     *
     * @return the assorativity for age
     */
    public double getAssortativityAge() {
        this.changeAttribute(NetworkAttributes.ASSORTATIVITY_AGE, this.getAttribute(NetworkAttributes.ASSORTATIVITY_AGE),
                StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.AGE));
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_AGE);
    }

    /**
     * Gets the round when assortativity for age has been computed last.
     *
     * @return the round when assortativity for age has been computed last
     */
    public int getAssortativityAgeRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.ASSORTATIVITY_AGE_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the age assortativity for age.
     *
     * @param simRound
     *          the simulation round to get the assortativity for
     * @return the assortativity for age
     */
    public double getAssortativityAge(int simRound) {
        if (this.getAssortativityAgeRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_AGE, this.getAttribute(NetworkAttributes.ASSORTATIVITY_AGE),
                    StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.AGE));
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_AGE_ROUND_LAST_COMPUTATION,
                    this.getAssortativityAgeRoundLastComputation(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_AGE);
    }

    /**
     * Gets the assortativity for profession.
     *
     * @return the assorativity for profession
     */
    public double getAssortativityProfession() {
        this.changeAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION,
                this.getAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION),
                StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.PROFESSION));
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION);
    }

    /**
     * Gets the round when assortativity for profession has been computed last.
     *
     * @return the round when assortativity for profession has been computed last
     */
    public int getAssortativityProfessionRoundLastComputation() {
        return (int) this.getAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the age assortativity for profession.
     *
     * @param simRound
     *          the simulation round to get the assortativity for
     * @return the assortativity for profession
     */
    public double getAssortativityProfession(int simRound) {
        if (this.getAssortativityProfessionRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION,
                    this.getAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION),
                    StatsComputer.computeAssortativity(this.getEdgeSet(), AssortativityConditions.PROFESSION));
            this.changeAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION_ROUND_LAST_COMPUTATION,
                    this.getAssortativityProfessionRoundLastComputation(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.ASSORTATIVITY_PROFESSION);
    }

    /**
     * Gets the round when average clustering has been computed last.
     *
     * @return the round when average clustering has been computed last
     */
    public int getAvClusteringRound() {
        return (int) this.getAttribute(NetworkAttributes.AV_CLUSTERING_ROUND_LAST_COMPUTATION);
    }

    /**
     * Gets the average clustering coefficient of the network.
     *
     * @param simRound
     *          the simulation round to the clustering coefficient for
     * @return the average clustering coefficient of the network
     */
    public double getAvClustering(int simRound) {
        if (this.getAvClusteringRoundLastComputation() < simRound) {
            this.changeAttribute(NetworkAttributes.AV_CLUSTERING, this.getAttribute(NetworkAttributes.AV_CLUSTERING),
                    Toolkit.averageClusteringCoefficient(this));
            this.changeAttribute(NetworkAttributes.AV_CLUSTERING_ROUND_LAST_COMPUTATION, this.getAvClusteringRound(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.AV_CLUSTERING);
    }

    /**
     * Gets the round when average path length has been computed last.
     *
     * @return the round when average clustering has been computed last
     */
    public int getAvPathLengthRound() {
        return (int) this.getAttribute(NetworkAttributes.AV_PATH_LENGTH_ROUND_LAST_COMPUTATION);
    }

    /**
     * Computes the average path length of the network.
     *
     * @return the average path length of the network
     */
    private double computeAvPathLength() {
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
                if (shortestPathLength != null) {
                    totalShortestPathLengths += shortestPathLength.intValue();
                } else {
                    // if nodes cannot be reached: path length = "infinity"
                    totalShortestPathLengths += Integer.MAX_VALUE;
                }
            }
        }
        // average
        return totalShortestPathLengths / (this.getN() * (this.getN()-1));
    }

    /**
     * Gets the average path length of the network.
     *
     * @param simRound
     *          the simulation round to get the average path length for
     * @return the average path length of the network
     */
    public double getAvPathLength(int simRound) {
        if (this.getAvPathLengthRound() < simRound) {
            this.changeAttribute(NetworkAttributes.AV_PATH_LENGTH, this.getAttribute(NetworkAttributes.AV_PATH_LENGTH),
                    computeAvPathLength());
            this.changeAttribute(NetworkAttributes.AV_PATH_LENGTH_ROUND_LAST_COMPUTATION, this.getAvClusteringRound(), simRound);
        }
        return (double) this.getAttribute(NetworkAttributes.AV_PATH_LENGTH);
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
        this.changeAttribute(NetworkAttributes.AV_UTILITY, this.getAttribute(NetworkAttributes.AV_UTILITY),
                avUtility / this.getAgents().size());
        return (double) this.getAttribute(NetworkAttributes.AV_UTILITY);

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
        this.changeAttribute(NetworkAttributes.AV_SOCIAL_BENEFITS, this.getAttribute(NetworkAttributes.AV_SOCIAL_BENEFITS),
                avBenefit1 / this.getAgents().size());
        return (double) this.getAttribute(NetworkAttributes.AV_SOCIAL_BENEFITS);
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
        this.changeAttribute(NetworkAttributes.AV_SOCIAL_COSTS, this.getAttribute(NetworkAttributes.AV_SOCIAL_COSTS),
                avCosts1 / this.getAgents().size());
        return (double) this.getAttribute(NetworkAttributes.AV_SOCIAL_COSTS);
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
        this.changeAttribute(NetworkAttributes.AV_DISEASE_COSTS, this.getAttribute(NetworkAttributes.AV_DISEASE_COSTS),
                avCostsDisease / this.getAgents().size());
        return (double) this.getAttribute(NetworkAttributes.AV_DISEASE_COSTS);
    }

    /**
     * Gets the number of agents in the network.
     *
     * @return the number of agent in the network.
     */
    public int getN() {
        this.changeAttribute(NetworkAttributes.N, this.getAttribute(NetworkAttributes.N),
                this.getAgents().size());
        return (int) this.getAttribute(NetworkAttributes.N);
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
        this.resetTimestepsStable();
    }

    /**
     * Removes all connections between agents and resets the agents to being susceptible.
     */
    public void resetAgents() {
        clearConnections();
        this.resetDiseaseStates();
    }

    /**
     * Resets all agents to being susceptible.
     */
    public void resetDiseaseStates() {
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
                this.resetTimestepsStable();
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
                this.resetTimestepsStable();

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
                        this.resetTimestepsStable();
                        return;
                    }
                } else {
                    if (agent.newConnectionValuable(other) && other.newConnectionValuable(agent)) {
                        this.resetTimestepsStable();
                        return;
                    }
                }
            }
            checkedIds.add(agent.getId());
        }
        this.changeAttribute(NetworkAttributes.TIMESTEPS_STABLE, (int) this.getAttribute(NetworkAttributes.TIMESTEPS_STABLE),
                (int) this.getAttribute(NetworkAttributes.TIMESTEPS_STABLE)+1);
    }

    /**
     * Gets whether the network is pairwise stable or not: there are no two players that
     * want to create a link and where neither one of them wants to delete a link.
     *
     * @return true if network is pairwise stable, false otherwise
     */
    public boolean isStable() {
        this.changeAttribute(NetworkAttributes.STABLE, this.getAttribute(NetworkAttributes.STABLE),
                this.getTimestepsStable() >= TIMESTEPS_REQUIRED_FOR_STABILITY);
        return (boolean) this.getAttribute(NetworkAttributes.STABLE);
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
                this.changeAttribute(NetworkAttributes.HAS_ACTIVE_INFECTION,
                        (boolean) this.getAttribute(NetworkAttributes.HAS_ACTIVE_INFECTION), true);
                return true;
            }
        }
        this.changeAttribute(NetworkAttributes.HAS_ACTIVE_INFECTION,
                (boolean) this.getAttribute(NetworkAttributes.HAS_ACTIVE_INFECTION), false);
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
        this.changeAttribute(NetworkAttributes.EMPTY, (boolean) this.getAttribute(NetworkAttributes.EMPTY),
                this.getEdgeCount() == 0);
        return (boolean) this.getAttribute(NetworkAttributes.EMPTY);
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
                this.changeAttribute(NetworkAttributes.FULL, (boolean) this.getAttribute(NetworkAttributes.FULL), false);
                return false;
            }
        }
        this.changeAttribute(NetworkAttributes.FULL, (boolean) this.getAttribute(NetworkAttributes.FULL), true);
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

        this.changeAttribute(NetworkAttributes.RING, (boolean) this.getAttribute(NetworkAttributes.RING), ring);
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
        this.changeAttribute(NetworkAttributes.STAR, (boolean) this.getAttribute(NetworkAttributes.STAR),
                (centers == 1) && (peripheries == this.getAgents().size() - 1));
        return (boolean) this.getAttribute(NetworkAttributes.STAR);
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
     * Gets the density of the network.
     *
     * @return the density of the network
     */
    public double getDensity() {
        return Toolkit.density(this);
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


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        // same object
        if (o == this) {
            return true;
        }

        // same type
        if (!(o instanceof Network)) {
            return false;
        }

        Network n = (Network) o;

        // same attributes
        Iterator<String> akIt = this.getAttributeKeyIterator();
        while (akIt.hasNext()) {
            String ak = akIt.next();
            if (ak.equals("id")) {
                continue;
            }

            Object thisAttribute = this.getAttribute(ak);
            Object otherAttribute = n.getAttribute(ak);

            if (!thisAttribute.equals(otherAttribute)) {
                logger.warn("Networks unequal: " + ak + "\tthis: " + thisAttribute + "\tother: " + otherAttribute);
                return false;
            }
        }

        // agents
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent thisAgent = agentIt.next();
            Agent otherAgent = n.getAgent(thisAgent.getId());

            if (!thisAgent.same(otherAgent)) {
                return false;
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode() {

        List<Object> os = new ArrayList<Object>();

        Iterator<String> akIt = this.getAttributeKeyIterator();
        while (akIt.hasNext()) {
            String ak = akIt.next();
            os.add(this.getAttribute(ak));
        }

        return Objects.hash(os);
    }

}
