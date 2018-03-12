package nl.uu.socnetid.networkgames.actors;

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
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleNode;

import nl.uu.socnetid.networkgames.diseases.Disease;
import nl.uu.socnetid.networkgames.diseases.DiseaseFactory;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseGroup;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.stats.StatsComputer;
import nl.uu.socnetid.networkgames.utilities.Utility;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

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
     * @param riskFactor
     *          the risk factor of a actor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     */
    public void initActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, Double riskFactor) {
        this.addAttribute(ActorAttributes.UTILITY_FUNCTION, utilityFunction);
        this.addAttribute(ActorAttributes.DISEASE_SPECS, diseaseSpecs);
        DiseaseGroup diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.addAttribute(ActorAttributes.DISEASE_GROUP, diseaseGroup);
        // ui-class required only for ui properties as defined in resources/graph-stream.css
        // --> no listener notifications
        this.addAttribute(ActorAttributes.UI_CLASS, diseaseGroup.toString(), false);
        this.addAttribute(ActorAttributes.RISK_FACTOR, riskFactor);
        this.addAttribute(ActorAttributes.RISK_MEANING, getRiskMeaning(riskFactor));
        this.addAttribute(ActorAttributes.SATISFIED, false);
        this.addAttribute("ui.label", this.getLabel());
    }

    private String getLabel() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getId()).append(" [").append(this.getRiskMeaning(this.getRiskFactor())).append("]");

        return sb.toString();
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
     * Gets the actor's risk factor.
     *
     * @return the actor's risk factor
     */
    public double getRiskFactor() {
        return (double) this.getAttribute(ActorAttributes.RISK_FACTOR);
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
     */
    public void computeRound() {
        // starting assumption that current connections are not optimal
        boolean satisfied = false;

        // actors try to connect or disconnect first in random order
        boolean tryToConnectFirst = ThreadLocalRandom.current().nextBoolean();

        // 1st try to connect - 2nd try to disconnect if no new connection desired
        if (tryToConnectFirst) {

            // try to connect
            if (!tryToConnect()) {
                // try to disconnect
                if (!tryToDisconnect()) {
                    satisfied = true;
                }
            }

        // 1st try to disconnect - 2nd try to connect if no disconnection desired
        } else {

            // try to disconnect
            if (!tryToDisconnect()) {
                // try to connect
                if (!tryToConnect()) {
                    satisfied = true;
                }
            }
        }

        // update satisfaction
        updateSatisfaction(satisfied);
        // round finished
        notifyRoundFinished();
    }

    /**
     * A actor tries to connect. That means she first seeks a connection that gives higher
     * utility as the current utility; and then requests the corresponding actor to establish
     * the new connection.
     */
    private boolean tryToConnect() {
        Actor potentialNewConnection = seekNewConnection();
        if (potentialNewConnection != null) {
            // other actor accepting connection?
            if (potentialNewConnection.acceptConnection(this)) {
                addConnection(potentialNewConnection);
            }
        }
        // the desire to create new connection counts as a move
        return (potentialNewConnection != null);
    }

    /**
     * A actor tries to disconnect. That means she seeks a connection that creates more costs
     * than benefits. In case she finds such a connection, she removes the costly connection.
     */
    private boolean tryToDisconnect() {
        Actor costlyConnection = seekCostlyConnection();
        if (costlyConnection != null) {
            this.removeConnection(costlyConnection);
        }
        // the desire to remove a connection counts as a move
        return (costlyConnection != null);
    }

    /**
     * Tries to find a new desirable connection. That is, a connection that creates heigher utility than
     * the utility the actor currently receives.
     *
     * @return the actor to create a desirable connection to, null if no such connection exists
     */
    public Actor seekNewConnection() {
        // should this consider non-infected first?
        // Q&A Feb 20th 2018: no, as even an infected might provide
        // high utility through many indirect connections
        Actor potentialConnection = getRandomNotYetConnectedActor();

        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.add(potentialConnection);

        // connect if new connection creates higher or equal utility
        if (this.getUtility(potentialConnections).getOverallUtility() >= this.getUtility().getOverallUtility()) {
            return potentialConnection;
        }
        return null;
    }

    /**
     * Tries to find a connection that creates higher costs than benefits.
     *
     * @return the actor whose connection to creates higher costs than benefits, null if no such connection exists
     */
    public Actor seekCostlyConnection() {

        // should this consider infected first?
        // Q&A Feb 20th 2018: no, as even an infected might provide
        // high utility through many indirect connections
        Actor potentialRemoval = getRandomConnection();

        List<Actor> potentialConnections = new ArrayList<Actor>(this.getConnections());
        potentialConnections.remove(potentialRemoval);

        // disconnect only if removal creates higher utility
        if (this.getUtility(potentialConnections).getOverallUtility() > this.getUtility().getOverallUtility()) {
            return potentialRemoval;
        }

        return null;
    }

    /**
     * Entry point for new connection requests.
     *
     * @param newConnection
     *          the actor requesting to establish a connection
     * @return true if the connection is being accepted, false otherwise
     */
    public boolean acceptConnection(Actor newConnection) {
        List<Actor> prospectiveConnections = new ArrayList<Actor>(this.getConnections());
        prospectiveConnections.add(newConnection);

        // accept connection if the new connection creates higher or equal utility
        return this.getUtility(prospectiveConnections).getOverallUtility() >= this.getUtility().getOverallUtility();
    }

    /**
     * Gets a random connection.
     *
     * @return a random connection
     */
    public Actor getRandomConnection() {
        Collection<Actor> connections = this.getConnections();
        if (connections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return (Actor) connections.toArray()[index];
    }

    /**
     * Gets a random not yet connected co-actor.
     *
     * @return a random not yet connected co-actor
     */
    public Actor getRandomNotYetConnectedActor() {
        ArrayList<Actor> noConnections = new ArrayList<Actor>(this.getCoActors());
        noConnections.removeAll(this.getConnections());
        if (noConnections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(noConnections.size());
        return noConnections.get(index);
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

        // edge id consistency
        ArrayList<Actor> actors = new ArrayList<Actor>();
        actors.add(this);
        actors.add(newConnection);
        Collections.sort(actors);

        Actor actor1 = actors.get(0);
        Actor actor2 = actors.get(1);

        String edgeId = String.valueOf(actor1.getId()) + String.valueOf(actor2.getId());
        String idActor1 = String.valueOf(actor1.getId());
        String idActor2 = String.valueOf(actor2.getId());

        Network network = this.getNetwork();
        Edge edge = network.getEdge(edgeId);
        if (edge == null) {
            network.addEdge(edgeId, idActor1, idActor2);
        }
        notifyConnectionAdded(edge, actor1, actor2);
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
    private Disease getDisease() {
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
