package nl.uu.socnetid.networkgames.actors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import nl.uu.socnetid.networkgames.disease.Disease;
import nl.uu.socnetid.networkgames.disease.DiseaseFactory;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseGroup;
import nl.uu.socnetid.networkgames.stats.StatsComputer;
import nl.uu.socnetid.networkgames.utilities.Utility;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Interface of a basic actor.
 *
 * @author Hendrik Nunner
 */
public class Actor implements Comparable<Actor>, Runnable {

    // risk neutral risk factor
    protected static final double RISK_NEUTRAL = 1.0;
    // logger
    private final static Logger logger = Logger.getLogger(Actor.class);

    // unique identifier
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    // risk factor
    private double riskFactor;

    // utility function
    private UtilityFunction utilityFunction;

    // the graph the actor acts as node in
    private Graph graph;
    // co-actors
    private List<Actor> coActors;
    // personal connections
    private List<Actor> connections = new ArrayList<Actor>();

    // disease
    private DiseaseGroup diseaseGroup;
    private DiseaseSpecs diseaseSpecs;
    private Disease disease;

    // flag indicating whether the actor is satisfied with her current connections
    private boolean satisfied = false;

    // concurrency lock
    private Lock lock;

    // listener
    private final Set<ActorListener> actorListeners =
            new CopyOnWriteArraySet<ActorListener>();


    /**
     * Constructor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease characteristics that is or might become present in the network
     * @param riskFactor
     *          the risk factor of a actor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     * @param graph
     *          the graph the actor acts as node in
     */
    private Actor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor, Graph graph) {
        this.utilityFunction = utilityFunction;
        this.diseaseSpecs = diseaseSpecs;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.riskFactor = riskFactor;
        this.graph = graph;
        this.graph.addNode(String.valueOf(getId()));
        initAttributes();
    }

    /**
     * Constructor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param graph
     *          the graph the actor acts as node in
     */
    private Actor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, Graph graph) {
        this(utilityFunction, diseaseSpecs, RISK_NEUTRAL, graph);
    }



    /**
     * Factory method returning a new {@link Actor} instance with a custom risk factor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the custom risk factor
     * @param graph
     *          the graph the actor acts as node in
     * @return a new {@link Actor} instance with a custom risk factor
     */
    public static Actor newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor,
            Graph graph) {
        return new Actor(utilityFunction, diseaseSpecs, riskFactor, graph);
    }

    /**
     * Factory method returning a new {@link Actor} instance.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param graph
     *          the graph the actor acts as node in
     * @return a new {@link Actor} instance
     */
    public static Actor newInstance(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, Graph graph) {
        return new Actor(utilityFunction, diseaseSpecs, graph);
    }


    /**
     * Initializes the node attributes.
     */
    private void initAttributes() {
        // TODO consider: Actor extending graphstream's Node and overriding addAttribute to accept ActorAttributes!

        Node node = this.graph.getNode(String.valueOf(getId()));

        // disease group
        String diseaseGroupAttr = ActorAttributes.DISEASE_GROUP.toString();
        String diseaseGroup = this.getDiseaseGroup().toString();
        node.addAttribute(diseaseGroupAttr, diseaseGroup);
        notifyAttributeAdded(diseaseGroupAttr, diseaseGroup);
        // ui-class required only for ui properties as defined in resources/graph-stream.css --> no notifications
        String uiClassAttr = ActorAttributes.UI_CLASS.toString();
        node.addAttribute(uiClassAttr, diseaseGroup);

        // satisfaction
        String satisfiedAttr = ActorAttributes.SATISFIED.toString();
        String satisfied = this.isSatisfied() ? "true" : "false";
        node.addAttribute(satisfiedAttr, satisfied);
        notifyAttributeAdded(satisfiedAttr, satisfied);

        // infected actors require also diseaseType
        if (this.isInfected()) {
            String diseaseTypeAttr = ActorAttributes.DISEASE_TYPE.toString();
            String diseaseType = this.getDiseaseSpecs().getDiseaseType().toString();
            node.addAttribute(diseaseTypeAttr, diseaseType);
            notifyAttributeAdded(diseaseTypeAttr, diseaseType);
        }
    }

    /**
     * Updates the node's disease attributes.
     *
     * @param diseaseGroupPreviousRound
     *          the disease group in the previous round
     */
    private void updateDiseaseAttributes(DiseaseGroup diseaseGroupPreviousRound) {
        Node node = this.graph.getNode(String.valueOf(getId()));

        // change of disease group
        if (diseaseGroupPreviousRound != this.diseaseGroup) {
            String oldDiseaseGroup = diseaseGroupPreviousRound.toString();
            String currentDiseaseGroup = this.diseaseGroup.toString();

            // disease group
            String diseaseGroupAttr = ActorAttributes.DISEASE_GROUP.toString();
            node.changeAttribute(diseaseGroupAttr, currentDiseaseGroup);
            notifyAttributeChanged(diseaseGroupAttr, oldDiseaseGroup, currentDiseaseGroup);
            // ui-class required only for ui properties as defined in resources/graph-stream.css --> no notifications
            String uiClassAttr = ActorAttributes.UI_CLASS.toString();
            node.changeAttribute(uiClassAttr, currentDiseaseGroup);

            // newly infected actors require also disease type and disease timer attributes
            if (this.isInfected()) {
                // disease type
                String diseaseTypeAttr = ActorAttributes.DISEASE_TYPE.toString();
                String diseaseType = this.getDiseaseSpecs().getDiseaseType().toString();
                node.addAttribute(diseaseTypeAttr, diseaseType);
                notifyAttributeAdded(diseaseTypeAttr, diseaseType);
                // disease timer
                String diseaseTimeUntilCuredAttr = ActorAttributes.DISEASE_TIMEUNTILCURED.toString();
                String diseaseTimeUntilCured = String.valueOf(this.disease.getTimeUntilCured());
                node.addAttribute(diseaseTimeUntilCuredAttr, diseaseTimeUntilCured);
                notifyAttributeAdded(diseaseTimeUntilCuredAttr, diseaseTimeUntilCured);
            }
            // formerly infected actors remove disease type and disease timer attributes
            if (diseaseGroupPreviousRound == DiseaseGroup.INFECTED) {
                // disease type
                String diseaseTypeAttr = ActorAttributes.DISEASE_TYPE.toString();
                node.removeAttribute(diseaseTypeAttr);
                notifyAttributeRemoved(diseaseTypeAttr);
                // disease timer
                String diseaseTimeUntilCuredAttr = ActorAttributes.DISEASE_TIMEUNTILCURED.toString();
                node.removeAttribute(diseaseTimeUntilCuredAttr);
                notifyAttributeRemoved(diseaseTimeUntilCuredAttr);
            }
        }

        // actor still being infected: update disease timer
        if (diseaseGroupPreviousRound == this.diseaseGroup && this.diseaseGroup == DiseaseGroup.INFECTED) {
            String diseaseTimeUntilCuredAttr = ActorAttributes.DISEASE_TIMEUNTILCURED.toString();
            String oldValue = String.valueOf(this.disease.getTimeUntilCured() + 1);
            String newValue = String.valueOf(this.disease.getTimeUntilCured());
            node.changeAttribute(diseaseTimeUntilCuredAttr, newValue);
            notifyAttributeChanged(diseaseTimeUntilCuredAttr, oldValue, newValue);
        }
    }

    /**
     * Update the node's satisfaction attribute.
     */
    private void updateSatisfactionAttribute() {
        Node node = this.graph.getNode(String.valueOf(getId()));

        // satisfaction
        String satisfiedAttr = ActorAttributes.SATISFIED.toString();
        String newValue = this.isSatisfied() ? "true" : "false";
        String oldValue = this.isSatisfied() ? "false" : "true";
        node.changeAttribute(satisfiedAttr, newValue);
        notifyAttributeChanged(satisfiedAttr, oldValue, newValue);
    }



















    // TODO clean up whole class



    /**
     * Initializes the actor's list of co-actors.
     *
     * @param allActors
     *          all actor's within the game
     */
    public void initCoActors(List<Actor> allActors) {
        coActors = new ArrayList<Actor>(allActors);
        coActors.remove(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Actor p) {
        return (int) (this.getId() - p.getId());
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // starting assumption that current connections are not optimal
        boolean tmpSatisfied = false;
        lock.lock();

        try {

            // some delay before each actor moves (e.g., for animation processes)
            //Thread.sleep(this.delay * 100);

            // actors try to connect or disconnect first in random order
            boolean tryToConnectFirst = ThreadLocalRandom.current().nextBoolean();

            // 1st try to connect - 2nd try to disconnect if no new connection desired
            if (tryToConnectFirst) {

                // try to connect
                if (!tryToConnect()) {
                    // try to disconnect
                    if (!tryToDisconnect()) {
                        tmpSatisfied = true;
                    }
                }

                // 1st try to disconnect - 2nd try to connect if no disconnection desired
            } else {

                // try to disconnect
                if (!tryToDisconnect()) {
                    // try to connect
                    if (!tryToConnect()) {
                        tmpSatisfied = true;
                    }
                }
            }

            if (this.satisfied != tmpSatisfied) {
                this.satisfied = tmpSatisfied;
                updateSatisfactionAttribute();
            }
            notifyRoundFinished();

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
                potentialNewConnection.addConnection(this);
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
            costlyConnection.removeConnection(this);
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
     * Gets the actor's unique identifier.
     *
     * @return the actor's unique identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the actor's utility.
     *
     * @return the actor's utility
     */
    public Utility getUtility() {
        return getUtility(this.connections);
    }

    /**
     * Gets the utility for a actor based on a list of connections.
     *
     * @param connections
     *          the connections to compute the utility for
     * @return the utility for a actor based on a list of connections
     */
    protected Utility getUtility(List<Actor> connections) {
        return utilityFunction.getUtility(this, connections);
    }

    /**
     * Gets the actor's risk factor.
     *
     * @return the actor's risk factor
     */
    public double getRiskFactor() {
        return this.riskFactor;
    }

    /**
     * Gets the actor's utility function.
     *
     * @return the actor's utility function
     */
    public UtilityFunction getUtilityFunction() {
        return this.utilityFunction;
    }

    /**
     * Gets the actor's connections.
     *
     * @return the actor's connections
     */
    public List<Actor> getConnections() {
        return this.connections;
    }

    /**
     * Gets the actor's co-actors.
     *
     * @return the actor's co-actors
     */
    public List<Actor> getCoActors() {
        return this.coActors;
    }

    /**
     * Gets whether the actor is satisfied with the current connections.
     *
     * @return true if the actor is satisfied with the current connections, false otherwise
     */
    public boolean isSatisfied() {
        return this.satisfied;
    }

    /**
     * Gets a random connection.
     *
     * @return a random connection
     */
    public Actor getRandomConnection() {
        if (connections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return connections.get(index);
    }

    /**
     * Gets a random not yet connected co-actor.
     *
     * @return a random not yet connected co-actor
     */
    public Actor getRandomNotYetConnectedActor() {
        ArrayList<Actor> noConnections = new ArrayList<Actor>(coActors);
        noConnections.removeAll(connections);

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
     * @return true if the connection is created successfully, false otherwise
     */
    public boolean addConnection(Actor newConnection) {

        // check node consistency
        if (!checkNewConnectionConsistency(newConnection)) {
            logger.info("Request to add new connection aborted.");
            return false;
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

        Edge edge = this.graph.getEdge(edgeId);
        if (edge == null) {
            this.graph.addEdge(edgeId, idActor1, idActor2);
        }

        boolean connectionAdded = this.connections.add(newConnection);
        notifyConnectionAdded(edge, actor1, actor2);

        return connectionAdded;
    }

    /**
     * Creates connections to all other co-actors.
     */
    public void connectToAll() {
        ArrayList<Actor> noConnections = new ArrayList<Actor>(coActors);
        noConnections.removeAll(connections);

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
            logger.info("Inconsistent new connection: reflexive");
            return false;
        }
        if (connections.contains(newConnection)) {
            logger.info("Inconsistent new connection: already existing");
            return false;
        }
        return true;
    }

    /**
     * Removes a connection to another actor.
     *
     * @param connection
     *          the actore to remove the connection from
     * @return true if the connection was removed successfully, false otherwise
     */
    public boolean removeConnection(Actor connection) {

        if (connection.equals(this)) {
            throw new RuntimeException("Unable to remove reflexive connections.");
        }

        // edge id consistency
        ArrayList<Actor> tmpActors = new ArrayList<Actor>();
        tmpActors.add(this);
        tmpActors.add(connection);
        Collections.sort(tmpActors);

        String edgeId = String.valueOf(tmpActors.get(0).getId()) + String.valueOf(tmpActors.get(1).getId());
        Edge edge = this.graph.getEdge(edgeId);
        if (edge != null) {
            this.graph.removeEdge(edgeId);
            notifyEdgeRemoved(edge);
        }

        notifyConnectionRemoved();
        return this.connections.remove(connection);
    }

    /**
     * Removes all connections to other actors.
     */
    public void removeAllConnections() {
        // remove all graph edges of the current actor
        Edge[] edges = this.graph.getNode(String.valueOf(getId())).getEdgeSet().toArray(new Edge[0]);
        for(int i = 0; i < edges.length; ++i){
            Edge edge = edges[i];
            graph.removeEdge(edge);
            notifyEdgeRemoved(edge);
        }
        notifyConnectionRemoved();
        this.connections = new ArrayList<Actor>();
    }

    /**
     * Destroy routine for an actor
     */
    public void destroy() {
        this.graph.removeNode(String.valueOf(getId()));
    }


    /**
     * Cures the actor from a disease.
     */
    public void cure() {
        DiseaseGroup diseaseGroupPreviousRound = this.diseaseGroup;

        this.disease = null;
        this.diseaseGroup = DiseaseGroup.RECOVERED;

        updateDiseaseAttributes(diseaseGroupPreviousRound);
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
        DiseaseGroup diseaseGroupPreviousRound = this.diseaseGroup;

        if (!this.diseaseSpecs.equals(diseaseSpecs)) {
            throw new RuntimeException("Known disease and caught disease mismatch!");
        }
        this.disease = DiseaseFactory.createInfection(diseaseSpecs);
        this.diseaseGroup = DiseaseGroup.INFECTED;

        updateDiseaseAttributes(diseaseGroupPreviousRound);
    }

    /**
     * Computes whether the actor is being infected by one of his infected connections.
     */
    public void computeDiseaseTransmission() {
        if (this.isSusceptible()) {
            int nI = StatsComputer.computeLocalActorConnectionsStats(this).getnI();
            if (ThreadLocalRandom.current().nextDouble() <=
                    StatsComputer.computeProbabilityOfInfection(this, nI)) {
                this.infect(this.diseaseSpecs);
            }
        }
    }

    /**
     * Checks whether the actor is susceptible.
     *
     * @return true if the actor is susceptible, false otherwise
     */
    public boolean isSusceptible() {
        return this.diseaseGroup == DiseaseGroup.SUSCEPTIBLE;
    }

    /**
     * Makes the actor susceptible.
     */
    public void makeSusceptible() {
        DiseaseGroup diseaseGroupPreviousRound = this.diseaseGroup;

        this.disease = null;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;

        updateDiseaseAttributes(diseaseGroupPreviousRound);
    }

    /**
     * Gets the disease group the actor is in.
     *
     * @return the disease group the actor is in
     */
    public DiseaseGroup getDiseaseGroup() {
        return this.diseaseGroup;
    }

    /**
     * Checks whether the actor is infected.
     *
     * @return true if the actor is infected, false otherwise
     */
    public boolean isInfected() {
        return this.diseaseGroup == DiseaseGroup.INFECTED;
    }

    /**
     * Checks whether the actor is recovered.
     *
     * @return true if the actor is recovered, false otherwise
     */
    public boolean isRecovered() {
        return this.diseaseGroup == DiseaseGroup.RECOVERED;
    }

    /**
     * Triggers the actor to fight the disease.
     */
    public void fightDisease() {
        if (this.disease == null) {
            return;
        }

        this.disease.evolve();
        if (this.disease.isCured()) {
            this.cure();
        } else {
            updateDiseaseAttributes(this.diseaseGroup);
        }
    }

    /**
     * Gets the specifications of the disease the actor considers for decision making processes.
     *
     * @return the specifications of the disease the actor considers for decision making processes
     */
    public DiseaseSpecs getDiseaseSpecs() {
        return diseaseSpecs;
    }

    /**
     * Gets the time remaining before the actor has recovered from a disease.
     *
     * @return the time remaining before the actor has recovered from a disease
     */
    public int getTimeUntilRecovered() {
        if (isInfected()) {
            return this.disease.getTimeUntilCured();
        }
        return 0;
    }


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
    public void removeAttributeListener(ActorListener actorListener) {
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
    private final void notifyAttributeAdded(String attribute, String value) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeAdded(this, attribute, value);
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
    private final void notifyAttributeChanged(String attribute, String oldValue, String newValue) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeChanged(this, attribute, oldValue, newValue);
        }
    }

    /**
     * Notifies listeners of removed attributes.
     *
     * @param attribute
     *          the attribute
     */
    private final void notifyAttributeRemoved(String attribute) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAttributeRemoved(this, attribute);
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
     * Notifies the listeners of removed connections.
     */
    private final void notifyConnectionRemoved() {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConnectionRemoved(this);
        }
    }

    /**
     * Notifies the listeners of removed edges.
     *
     * @param edge
     *          the removed edge
     */
    private final void notifyEdgeRemoved(Edge edge) {
        Iterator<ActorListener> listenersIt = this.actorListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyEdgeRemoved(edge);
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
