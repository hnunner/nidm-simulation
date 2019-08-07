package nl.uu.socnetid.netgame.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.netgame.agents.Agent;
import nl.uu.socnetid.netgame.agents.AgentFactory;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class Network extends SingleGraph {

    // logger
    private static final Logger logger = Logger.getLogger(Network.class);

    // risk factor for risk neutral agents
    private static final double RISK_FACTOR_NEUTRAL = 1.0;

    // standard share to evaluate per agent
    private static final double STANDARD_PHI = 0.4;

    // listener
    private final Set<NetworkListener> networkListeners =
            new CopyOnWriteArraySet<NetworkListener>();


    /**
     * Constructor.
     */
    public Network() {
        this("Network of the Infectious Kind");
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     */
    public Network(String id) {
        super(id);
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
        return this.addAgent(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL, RISK_FACTOR_NEUTRAL, STANDARD_PHI);
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
     * @return the newly added agent.
     */
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi) {
        Agent agent = this.addNode(String.valueOf(this.getNodeCount() + 1));
        agent.initAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi);
        notifyAgentAdded(agent);
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
        return agentId;
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
            }
        }
    }

    /**
     * Checks whether the network is stable. That is, all agents are satisfied.
     * An agent is satisfied if (s)he does not prefer to add a non-existing tie,
     * or remove an existing tie.
     *
     * @return true if the network is stable, false otherwise
     */
    public boolean isStable() {
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (!agent.isSatisfied()) {
                return false;
            }
        }
        return true;
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
                ring = ring && firstAgent.hasConnectionTo(agent);
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
     * Gets the average clustering coefficient of the network.
     *
     * @return the average clustering coefficient of the network
     */
    public double getAvClustering() {
        return Toolkit.averageClusteringCoefficient(this);
    }

    /**
     * Gets the average alpha of all agents in the network.
     *
     * @return the average alpha of all agents in the network
     */
    public double getAvAlpha() {
        double avAlpha = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avAlpha += agent.getUtilityFunction().getAlpha();
        }
        return (avAlpha / this.getAgents().size());
    }


    /**
     * Gets the average beta of all agents in the network.
     *
     * @return the average beta of all agents in the network
     */
    public double getAvBeta() {
        double avBeta = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avBeta += agent.getUtilityFunction().getBeta();
        }
        return (avBeta / this.getAgents().size());
    }

    /**
     * Gets the network maintenance costs of all agents in the network.
     *
     * @return the network maintenance costs of all agents in the network
     */
    public double getAvC() {
        double avC = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avC += agent.getUtilityFunction().getC();
        }
        return (avC / this.getAgents().size());
    }

    /**
     * Gets the average risk factor for disease severity of all agents in the network.
     *
     * @return the average risk factor for disease severity of all agents in the network
     */
    public double getAvRSigma() {
        double avRSigma = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avRSigma += agent.getRSigma();
        }
        return (avRSigma / this.getAgents().size());
    }

    /**
     * Gets the average risk factor for probability of infections of all agents in the network.
     *
     * @return the average risk factor for probability of infections of all agents in the network
     */
    public double getAvRPi() {
        double avRPi = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avRPi += agent.getRPi();
        }
        return (avRPi / this.getAgents().size());
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
     * Gets the average benefit at distance 1 of all agents in the network.
     *
     * @return the average benefit at distance 1 of all agents in the network
     */
    public double getAvBenefitDistance1() {
        double avBenefit1 = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avBenefit1 += agent.getUtility().getBenefitDirectConnections();
        }
        return (avBenefit1 / this.getAgents().size());
    }

    /**
     * Gets the average benefit at distance 2 of all agents in the network.
     *
     * @return the average benefit at distance 2 of all agents in the network
     */
    public double getAvBenefitDistance2() {
        double avBenefit2 = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avBenefit2 += agent.getUtility().getBenefitIndirectConnections();
        }
        return (avBenefit2 / this.getAgents().size());
    }

    /**
     * Gets the average costs for agents at distance 1 of all agents in the network.
     *
     * @return the average costs for agents at distance 1 of all agents in the network
     */
    public double getAvCostsDistance1() {
        double avCosts1 = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avCosts1 += agent.getUtility().getCostsDirectConnections();
        }
        return (avCosts1 / this.getAgents().size());
    }

    /**
     * Gets the average disease costs of all agents in the network.
     *
     * @return the average disease costs of all agents in the network
     */
    public double getAvCostsDisease() {
        double avCostsDisease = 0;
        Iterator<Agent> agentIt = this.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            avCostsDisease += agent.getUtility().getEffectOfDisease();
        }
        return (avCostsDisease / this.getAgents().size());
    }


}
