package nl.uu.socnetid.netgame.actors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleNode;

import nl.uu.socnetid.netgame.diseases.Disease;
import nl.uu.socnetid.netgame.diseases.DiseaseFactory;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseGroup;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.stats.ActorConnectionStats;
import nl.uu.socnetid.netgame.stats.StatsComputer;
import nl.uu.socnetid.netgame.utilities.Utility;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * Interface of a basic actor.
 *
 * @author Hendrik Nunner
 */
public class Actor extends SingleNode implements Comparable<Actor>, Runnable {

    // logger
    private final static Logger logger = Logger.getLogger(Actor.class);

    // concurrency lock
    private Lock lock;

    // listeners
    private final Set<ActorListener> actorListeners =
            new CopyOnWriteArraySet<ActorListener>();


    /**
     * Constructor.
     *
     * @param id
     *          the unique identifier
     * @param network
     *          the network the actor is being a part of
     */
    protected Actor(String id, Network network) {
        super(network, id);
    }

    /**
     * Initializes the node attributes.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease characteristics that is or might become present in the network
     * @param riskFactorSigma
     *          the risk factor for disease severity (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @param riskFactorPi
     *          the risk factor for probability of infections (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @param phi
     *          the share of peers to evaluate per round
     */
    public void initActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs,
            Double riskFactorSigma, Double riskFactorPi, Double phi) {
        this.addAttribute(ActorAttributes.UTILITY_FUNCTION, utilityFunction);
        this.addAttribute(ActorAttributes.DISEASE_SPECS, diseaseSpecs);
        DiseaseGroup diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.addAttribute(ActorAttributes.DISEASE_GROUP, diseaseGroup);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.addAttribute(ActorAttributes.UI_CLASS, diseaseGroup.toString(), false);
        this.addAttribute(ActorAttributes.RISK_FACTOR_SIGMA, riskFactorSigma);
        this.addAttribute(ActorAttributes.RISK_FACTOR_PI, riskFactorPi);
        this.addAttribute(ActorAttributes.RISK_MEANING_SIGMA, getRiskMeaning(riskFactorSigma));
        this.addAttribute(ActorAttributes.RISK_MEANING_PI, getRiskMeaning(riskFactorPi));
        this.addAttribute(ActorAttributes.PHI, phi);
        this.addAttribute(ActorAttributes.SATISFIED, false);
        this.addAttribute(ActorAttributes.CONNECTION_STATS, new ActorConnectionStats());
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
     * Gets the network the actor is being a part of.
     *
     * @return the network the actor is being a part of
     */
    public Network getNetwork() {
        return (Network) super.getGraph();
    }

    /**
     * Gets the number of network decisions an actor is allowed to make in a single round.
     * This depends on the average number of connections an actor has.
     *
     * @return the number of network decisions
     */
    public int getNumberOfNetworkDecisions() {
//        return (int) Math.round(this.getNetwork().getAverageDegree());
        return (int) (Math.round((this.getNetwork().getNodeCount() - 1) * this.getPhi()));
    }

    /**
     * Gets the value of an attribute.
     *
     * @param attribute
     *          the attribute to get
     * @return the value of the attribute
     */
    private Object getAttribute(ActorAttributes attribute) {
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
    private void addAttribute(ActorAttributes attribute, Object value) {
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
     *          flag whether actor listeners ought to be notified of the added attribute
     */
    private void addAttribute(ActorAttributes attribute, Object value, boolean notify) {
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
    private void changeAttribute(ActorAttributes attribute, Object oldValue, Object newValue) {
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
     *          flag whether actor listeners ought to be notified of the changed attribute
     */
    private void changeAttribute(ActorAttributes attribute, Object oldValue, Object newValue, boolean notify) {
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
    private void removeAttribute(ActorAttributes attribute) {
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
     *          flag whether actor listeners ought to be notified of the added attribute
     */
    private void removeAttribute(ActorAttributes attribute, boolean notify) {
        super.removeAttribute(attribute.toString());
        if (notify) {
            notifyAttributeRemoved(attribute);
        }
    }

    /**
     * Gets the actor's utility.
     *
     * @return the actor's utility
     */
    public Utility getUtility() {
        return this.getUtility(this.getConnections());
    }

    /**
     * Gets the utility for a actor based on a list of connections.
     *
     * @param connections
     *          the connections to compute the utility for
     * @return the utility for a actor based on a list of connections
     */
    protected Utility getUtility(Collection<Actor> connections) {
        return this.getUtilityFunction().getUtility(this, connections);
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
        this.changeAttribute(ActorAttributes.SATISFIED, oldValue, newValue);
    }

    /**
     * Gets the actor's risk factor for disease severity.
     *
     * @return the actor's risk factor for disease severity
     */
    public double getRSigma() {
        return (double) this.getAttribute(ActorAttributes.RISK_FACTOR_SIGMA);
    }

    /**
     * Gets the actor's risk factor for probability of infections.
     *
     * @return the actor's risk factor for probability of infections
     */
    public double getRPi() {
        return (double) this.getAttribute(ActorAttributes.RISK_FACTOR_PI);
    }

    /**
     * Gets the actor's share of peers to evaluate per round.
     *
     * @return the actor's share of peers to evaluate per round
     */
    public double getPhi() {
        return (double) this.getAttribute(ActorAttributes.PHI);
    }

    /**
     * Gets the actor's utility function.
     *
     * @return the actor's utility function
     */
    public UtilityFunction getUtilityFunction() {
        return (UtilityFunction) this.getAttribute(ActorAttributes.UTILITY_FUNCTION);
    }

    /**
     * Gets the actor's connections.
     *
     * @return the actor's connections
     */
    public Collection<Actor> getConnections() {
        List<Actor> connections = new LinkedList<Actor>();
        Iterator<Actor> neighborIt = getNeighborNodeIterator();
        while (neighborIt.hasNext()) {
            connections.add(neighborIt.next());
        }
        return connections;
    }

    /**
     * Gets the actor's co-actors.
     *
     * @return the actor's co-actors
     */
    public Collection<Actor> getCoActors() {
        Collection<Actor> coActors = new ArrayList<Actor>(getNetwork().getActors());
        coActors.remove(this);
        return coActors;
    }

    /**
     * Gets whether the actor is satisfied with the current connections.
     *
     * @return true if the actor is satisfied with the current connections, false otherwise
     */
    public boolean isSatisfied() {
        return (boolean) this.getAttribute(ActorAttributes.SATISFIED);
    }

    /**
     * Gets the actor's connection stats.
     *
     * @return the actor's connection stats
     */
    public ActorConnectionStats getConnectionStats() {
        return (ActorConnectionStats) this.getAttribute(ActorAttributes.CONNECTION_STATS);
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
     * Keeps track of actively broken ties.
     */
    private void trackBrokenTieActive() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incBrokenTiesActive();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of passively broken ties.
     */
    private void trackBrokenTiePassive() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incBrokenTiesPassive();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of accepted outgoing requests.
     */
    private void trackAcceptedRequestOut() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incAcceptedRequestsOut();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of declined outgoing requests.
     */
    private void trackDeclinedRequestOut() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incDeclinedRequestsOut();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of accepted incoming requests.
     */
    private void trackAcceptedRequestIn() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incAcceptedRequestsIn();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /**
     * Keeps track of an declined incoming request.
     */
    private void trackDeclinedRequestIn() {
        // stats
        ActorConnectionStats oldConnectionStats = getConnectionStats().clone();
        ActorConnectionStats newConnectionStats = getConnectionStats();
        newConnectionStats.incDeclinedRequestsIn();
        changeAttribute(ActorAttributes.CONNECTION_STATS, oldConnectionStats, newConnectionStats);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Actor p) {
        return (int) (Long.valueOf(this.getId()) - Long.valueOf(p.getId()));
    }


    /////////////////////////////////////////////////// CONNECTIONS ///////////////////////////////////////////////////
    /**
     * Computes a single round for an actor. That is, an {@link Actor} tries to connect to
     * or disconnects from another {@link Actor} if it produces higher utility.
     *
     * @param delay
     *          the delay in ms to wait in between network decisions
     */
    public void computeRound(int delay) {
        // starting assumption: current connections are not satisfactory
        boolean satisfied = true;

        // get random collection of co-actors
        List<Actor> randomCoActors = getRandomListOfCoActors(getNumberOfNetworkDecisions());
        Collections.shuffle(randomCoActors);

        Iterator<Actor> it = randomCoActors.iterator();
        while (it.hasNext()) {
            Actor randomCoActor = it.next();
            if (this.hasDirectConnectionTo(randomCoActor)) {
                if (existingConnectionTooCostly(randomCoActor)) {
                    disconnectFrom(randomCoActor);
                    satisfied = false;
                }
            } else {
                if (newConnectionValuable(randomCoActor)) {
                    connectTo(randomCoActor);
                    satisfied = false;
                }
            }

            if (it.hasNext() && delay > 0) {
                // some delay before each actor moves (e.g., for animation processes)
                try {
                    Thread.sleep(delay * 10);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        // update satisfaction
        updateSatisfaction(satisfied);
        // round finished
        notifyRoundFinished();
    }

    /**
     * Computes a single round for an actor. That is, an {@link Actor} tries to connect to
     * or disconnects from another {@link Actor} if it produces higher utility.
     */
    public void computeRound() {
        this.computeRound(0);
    }

    /**
     * Checks whether a new connection adds value to the overall utility of an actor.
     *
     * @param newConnection
     *          the actor on the other side of the new connection
     * @return true if the new connection adds value to the overall utility of an actor
     */
    public boolean newConnectionValuable(Actor newConnection) {
        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.add(newConnection);
        return this.getUtility(potentialConnections).getOverallUtility() >= this.getUtility().getOverallUtility();
    }

    /**
     * Creates a connection between this actor and another actor.
     *
     * @param actor
     *          the actor to connect to
     * @return true if connection was accepted and created, false otherwise
     */
    public boolean connectTo(Actor actor) {
        // other actor accepting connection?
        if (actor.acceptConnection(this)) {
            addConnection(actor);
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
     *          the actor on the other side of the existing connection
     * @return true if the existing connection create more costs than it provides benefits
     */
    public boolean existingConnectionTooCostly(Actor existingConnection) {
        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.remove(existingConnection);
        return this.getUtility(potentialConnections).getOverallUtility() > this.getUtility().getOverallUtility();
    }

    /**
     * Disconnects this actor from another actor.
     *
     * @param actor
     *          the actor to disconnect from
     */
    public void disconnectFrom(Actor actor) {
        this.removeConnection(actor);
        this.trackBrokenTieActive();
        actor.notifyBrokenTie(this);
    }

    /**
     * Entry point for incoming connection requests.
     *
     * @param newConnection
     *          the actor requesting to establish a connection
     * @return true if the connection is being accepted, false otherwise
     */
    public boolean acceptConnection(Actor newConnection) {
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
    public void notifyBrokenTie(Actor initiator) {
        trackBrokenTiePassive();
    }

    /**
     * Creates a collection of randomly selected co-actors.
     *
     * @param amount
     *          the amount of co-actors to add
     * @return a random collection of co-actors
     */
    public List<Actor> getRandomListOfCoActors(int amount) {
        List<Actor> collect = new ArrayList<Actor>(amount);
        Collection<Actor> coActors = getCoActors();
        while (collect.size() < amount) {
            int index = ThreadLocalRandom.current().nextInt(coActors.size());
            collect.add((Actor) coActors.toArray()[index]);
        }
        return collect;
    }

    /**
     * Helper class to ensure edge consistency: lower index comes first.
     *
     * @author Hendrik Nunner
     */
    private class Connector {
        private String edgeId;
        private String idActor1;
        private String idActor2;

        private Actor actor1;
        private Actor actor2;

        protected Connector(Actor actor1, Actor actor2) {
            // edge id consistency: lower index comes always first
            ArrayList<Actor> actors = new ArrayList<Actor>();
            actors.add(actor1);
            actors.add(actor2);
            Collections.sort(actors);

            this.actor1 = actors.get(0);
            this.actor2 = actors.get(1);

            this.edgeId = String.valueOf(this.actor1.getId()) + String.valueOf(this.actor2.getId());
            this.idActor1 = String.valueOf(this.actor1.getId());
            this.idActor2 = String.valueOf(this.actor2.getId());
        }

        /**
         * @return the edgeId
         */
        protected String getEdgeId() {
            return edgeId;
        }

        /**
         * @return the idActor1
         */
        protected String getIdActor1() {
            return idActor1;
        }

        /**
         * @return the idActor2
         */
        protected String getIdActor2() {
            return idActor2;
        }

        /**
         * @return the actor1
         */
        public Actor getActor1() {
            return actor1;
        }

        /**
         * @return the actor2
         */
        public Actor getActor2() {
            return actor2;
        }
    }

    /**
     * Adds a new connection to another actor.
     *
     * @param newConnection
     *          the actor to connect to
     */
    public void addConnection(Actor newConnection) {

        // check node consistency
        if (!checkNewConnectionConsistency(newConnection)) {
            logger.warn("Request to add new connection aborted.");
            return;
        }

        Connector connector = new Connector(this, newConnection);
        String edgeId = connector.getEdgeId();

        Network network = this.getNetwork();
        if (network.getEdge(edgeId) == null) {
            network.addEdge(edgeId, connector.getIdActor1(), connector.getIdActor2());
        }
        notifyConnectionAdded(network.getEdge(edgeId), connector.getActor1(), connector.getActor2());
    }

    /**
     * Checks whether the actor has a direct connection to another actor
     *
     * @param actor
     *          the other actor to check the connection to
     * @return true if there is a connection, false otherwise
     */
    public boolean hasDirectConnectionTo(Actor actor) {
        Connector connector = new Connector(this, actor);
        return this.getNetwork().getEdge(connector.getEdgeId()) != null;
    }

    /**
     * Checks whether the actor has a connection somehow to another actor.
     *
     * @param actor
     *          the actor to check for an existing connection
     * @return true if the actors are somehow connected, false otherwise
     */
    public boolean hasConnectionTo(Actor actor) {
        Iterator<Node> bfIt = this.getBreadthFirstIterator();
        while (bfIt.hasNext()) {
            Node node = bfIt.next();
            if (node.getId() == actor.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates connections to all other co-actors.
     */
    public void connectToAll() {
        ArrayList<Actor> noConnections = new ArrayList<Actor>(getCoActors());
        noConnections.removeAll(getConnections());

        Iterator<Actor> noConnectionsIt = noConnections.iterator();
        while (noConnectionsIt.hasNext()) {
            Actor noConnection = noConnectionsIt.next();
            addConnection(noConnection);
        }
    }

    /**
     * @param newConnection
     *          the new connection to check for consistency
     * @return true if new node can be added, false otherwise
     */
    protected boolean checkNewConnectionConsistency(Actor newConnection) {
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
     * Removes a connection to another actor.
     *
     * @param connection
     *          the actore to remove the connection from
     */
    public void removeConnection(Actor connection) {

        // check node consistency
        if (connection.equals(this)) {
            logger.warn("Unable to remove reflexive connections.");
            return;
        }

        // edge id consistency
        ArrayList<Actor> tmpActors = new ArrayList<Actor>();
        tmpActors.add(this);
        tmpActors.add(connection);
        Collections.sort(tmpActors);

        // remove
        Network network = this.getNetwork();
        String edgeId = String.valueOf(tmpActors.get(0).getId()) + String.valueOf(tmpActors.get(1).getId());
        Edge edge = network.getEdge(edgeId);
        if (edge != null) {
            network.removeEdge(edgeId);
            notifyConnectionRemoved(edge);
        }
    }

    /**
     * Removes all connections to other actors.
     */
    public void removeAllConnections() {
        Network network = this.getNetwork();
        // remove all graph edges of the current actor
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
     * Sets the lock required to synchronize threaded actors.
     *
     * @param lock
     *          the lock used to synchronize threaded actors.
     */
    public void setLock(Lock lock) {
        this.lock = lock;
    }


    ///////////////////////////////////////////////////// DISEASE /////////////////////////////////////////////////////
    /**
     * Gets the specifications of the disease the actor considers for decision making processes.
     *
     * @return the specifications of the disease the actor considers for decision making processes
     */
    public DiseaseSpecs getDiseaseSpecs() {
        return (DiseaseSpecs) this.getAttribute(ActorAttributes.DISEASE_SPECS);
    }

    /**
     * Gets the disease group the actor is in.
     *
     * @return the disease group the actor is in
     */
    public DiseaseGroup getDiseaseGroup() {
        return (DiseaseGroup) this.getAttribute(ActorAttributes.DISEASE_GROUP);
    }

    /**
     * Checks whether the actor is susceptible.
     *
     * @return true if the actor is susceptible, false otherwise
     */
    public boolean isSusceptible() {
        return this.getDiseaseGroup() == DiseaseGroup.SUSCEPTIBLE;
    }

    /**
     * Checks whether the actor is infected.
     *
     * @return true if the actor is infected, false otherwise
     */
    public boolean isInfected() {
        return this.getDiseaseGroup() == DiseaseGroup.INFECTED;
    }

    /**
     * Checks whether the actor is recovered.
     *
     * @return true if the actor is recovered, false otherwise
     */
    public boolean isRecovered() {
        return this.getDiseaseGroup() == DiseaseGroup.RECOVERED;
    }

    /**
     * Makes the actor susceptible.
     */
    public void makeSusceptible() {
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        if (this.isInfected()) {
            this.removeAttribute(ActorAttributes.DISEASE_INFECTION);
        }
        this.changeAttribute(ActorAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.SUSCEPTIBLE);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(ActorAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.SUSCEPTIBLE.toString(), false);
    }

    /**
     * Gets the disease the actor is infected with.
     *
     * @return the disease the actor is infected with
     */
    public Disease getDisease() {
        if (this.isInfected()) {
            return (Disease) this.getAttribute(ActorAttributes.DISEASE_INFECTION);
        }
        return null;
    }

    /**
     * Computes whether the actor is being infected by one of his infected connections.
     */
    public void computeDiseaseTransmission() {
        if (this.isSusceptible()) {
            int nI = StatsComputer.computeLocalActorConnectionsStats(this).getnI();
            if (ThreadLocalRandom.current().nextDouble() <=
                    StatsComputer.computeProbabilityOfInfection(this, nI)) {
                this.infect(this.getDiseaseSpecs());
            }
        }
    }

    /**
     * Infects the actor with a disease.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the actor is infected with
     */
    public void infect(DiseaseSpecs diseaseSpecs) {
        if (isRecovered()) {
            return;
        }
        forceInfect(diseaseSpecs);
    }

    /**
     * Forces an infection onto the actor no matter whether the actor is immune or not.
     *
     * @param diseaseSpecs
     *          the specificationso of the disease the actor is infected with
     */
    public void forceInfect(DiseaseSpecs diseaseSpecs) {

        // coherence check
        if (!this.getDiseaseSpecs().equals(diseaseSpecs)) {
            throw new RuntimeException("Known disease and caught disease mismatch!");
        }

        // infect
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        this.addAttribute(ActorAttributes.DISEASE_INFECTION, DiseaseFactory.createInfection(diseaseSpecs));
        this.changeAttribute(ActorAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.INFECTED);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(ActorAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.INFECTED.toString(), false);
    }

    /**
     * Triggers the actor to fight the disease.
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
     * Gets the time remaining before the actor has recovered from a disease.
     *
     * @return the time remaining before the actor has recovered from a disease
     */
    public int getTimeUntilRecovered() {
        if (isInfected()) {
            return this.getDisease().getTimeUntilCured();
        }
        return 0;
    }

    /**
     * Cures the actor from a disease.
     */
    public void cure() {
        // cure
        DiseaseGroup prevDiseaseGroup = this.getDiseaseGroup();
        this.changeAttribute(ActorAttributes.DISEASE_GROUP, prevDiseaseGroup, DiseaseGroup.RECOVERED);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.changeAttribute(ActorAttributes.UI_CLASS,
                prevDiseaseGroup.toString(), DiseaseGroup.RECOVERED.toString(), false);
        this.removeAttribute(ActorAttributes.DISEASE_INFECTION);
    }


    //////////////////////////////////////////// LISTENERS / NOTIFICATIONS ////////////////////////////////////////////
    /**
     * Adds a listener for actor notifications.
     *
     * @param actorListener
     *          the listener to be added
     */
    public void addActorListener(ActorListener actorListener) {
        this.actorListeners.add(actorListener);
    }

    /**
     * Removes a listener for actor notifications.
     *
     * @param actorListener
     *          the listener to be removed
     */
    public void removeActorListener(ActorListener actorListener) {
        this.actorListeners.remove(actorListener);
    }

    /**
     * Notifies listeners of added attributes.
     *
     * @param attribute
     *          the attribute
     * @param value
     *          the attribute's value
     */
    private final void notifyAttributeAdded(ActorAttributes attribute, Object value) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
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
    private final void notifyAttributeChanged(ActorAttributes attribute, Object oldValue, Object newValue) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
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
    private final void notifyAttributeRemoved(ActorAttributes attribute) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeRemoved(this, attribute.toString());
        }
    }

    /**
     * Notifies listeners of added connections.
     *
     * @param edge
     *          the new connection
     * @param actor1
     *          the first actor the connection has been added to
     * @param actor2
     *          the second actor the connection has been added to
     */
    private final void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConnectionAdded(edge, actor1, actor2);
        }
    }

    /**
     * Notifies the listeners of removed edges.
     *
     * @param edge
     *          the removed edge
     */
    private final void notifyConnectionRemoved(Edge edge) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConnectionRemoved(this, edge);
        }
    }

    /**
     * Notifies listeners of finished actor rounds.
     */
    private final void notifyRoundFinished() {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRoundFinished(this);
        }
    }

}
