package nl.uu.socnetid.networkgames.actors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.disease.Disease;
import nl.uu.socnetid.networkgames.disease.DiseaseFactory;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseGroup;
import nl.uu.socnetid.networkgames.utilities.Utility;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Shared fields and methods for all {@link Actor} types.
 *
 * @author Hendrik Nunner
 */
public abstract class AbstractActor implements Actor {

    // risk neutral risk factor
    protected static final double RISK_NEUTRAL = 1.0;

    // logger
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(AbstractActor.class);

    // unique identifier
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    // risk factor
    private double riskFactor;

    // utility function
    private UtilityFunction utilityFunction;

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
    private final Set<ActionPerformedListener> actionPerformedListeners =
            new CopyOnWriteArraySet<ActionPerformedListener>();
    private final Set<DiseaseChangeListener> diseaseChangeListeners =
            new CopyOnWriteArraySet<DiseaseChangeListener>();


    /**
     * Constructor.
     *
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease characteristics that is or might become present in the network
     * @param riskFactor
     *          the risk factor of a actor (<1: risk seeking, =1: risk neutral; >1: risk averse)
     */
    protected AbstractActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        this.utilityFunction = utilityFunction;
        this.diseaseSpecs = diseaseSpecs;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.riskFactor = riskFactor;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#initCoActors()
     */
    @Override
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
     * @see nl.uu.socnetid.networkgames.Actor#setLock()
     */
    @Override
    public void setLock(Lock lock) {
        this.lock = lock;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // assumption that current connections are not optimal
        this.satisfied = false;
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
                        this.satisfied = true;
                    }
                }

                // 1st try to disconnect - 2nd try to connect if no disconnection desired
            } else {

                // try to disconnect
                if (!tryToDisconnect()) {
                    // try to connect
                    if (!tryToConnect()) {
                        this.satisfied = true;
                    }
                }
            }
            notifyActionPerformedListeners();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
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


    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getUtility()
     */
    @Override
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

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getRiskFactor()
     */
    @Override
    public double getRiskFactor() {
        return this.riskFactor;
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#getUtilityFunction()
     */
    @Override
    public UtilityFunction getUtilityFunction() {
        return this.utilityFunction;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getConnections()
     */
    @Override
    public List<Actor> getConnections() {
        return this.connections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#isSatisfied()
     */
    @Override
    public boolean isSatisfied() {
        return this.satisfied;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getRandomConnection()
     */
    @Override
    public Actor getRandomConnection() {
        if (connections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return connections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.Actor#getRandomNotYetConnectedActor()
     */
    @Override
    public Actor getRandomNotYetConnectedActor() {
        ArrayList<Actor> noConnections = new ArrayList<Actor>(coActors);
        noConnections.removeAll(connections);

        if (noConnections.size() == 0) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(noConnections.size());
        return noConnections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#addConnection(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public boolean addConnection(Actor newConnection) {
        if (newConnection.equals(this)) {
            throw new RuntimeException("Unable to create reflexive connections.");
        }
        if (connections.contains(newConnection)) {
            throw new RuntimeException("Unable to create more than one connection to the same actor.");
        }
        boolean connectionAdded = this.connections.add(newConnection);
        notifyActionPerformedListeners();
        return connectionAdded;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeConnection(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public boolean removeConnection(Actor connection) {
        if (connection.equals(this)) {
            throw new RuntimeException("Unable to remove reflexive connections.");
        }
        boolean connectionRemoved = this.connections.remove(connection);
        notifyActionPerformedListeners();
        return connectionRemoved;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {
        this.connections = new ArrayList<Actor>();
        notifyActionPerformedListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#destroy()
     */
    @Override
    public void destroy() { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#cure()
     */
    @Override
    public void cure() {
        this.disease = null;
        this.diseaseGroup = DiseaseGroup.RECOVERED;
        notifyDiseaseChangeListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#infect(nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void infect(DiseaseSpecs diseaseSpecs) {
        if (isRecovered()) {
            return;
        }
        forceInfect(diseaseSpecs);
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#forceInfect(nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void forceInfect(DiseaseSpecs diseaseSpecs) {
        if (!this.diseaseSpecs.equals(diseaseSpecs)) {
            throw new RuntimeException("Known disease and caught disease mismatch!");
        }
        this.disease = DiseaseFactory.createInfection(diseaseSpecs);
        this.diseaseGroup = DiseaseGroup.INFECTED;
        notifyDiseaseChangeListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#computeTransmissions()
     */
    @Override
    public void computeTransmissions() {

        Iterator<Actor> connectionsIt = this.connections.iterator();
        while (connectionsIt.hasNext()) {
            Actor currConnection = connectionsIt.next();

            if (!currConnection.isInfected() && !currConnection.isRecovered()
                    && this.disease.isTransmitted()) {
                currConnection.infect(diseaseSpecs);
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#isSusceptible()
     */
    @Override
    public boolean isSusceptible() {
        return this.diseaseGroup == DiseaseGroup.SUSCEPTIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#makeSusceptible()
     */
    @Override
    public void makeSusceptible() {
        this.disease = null;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        notifyDiseaseChangeListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#getDiseaseGroup()
     */
    @Override
    public DiseaseGroup getDiseaseGroup() {
        return this.diseaseGroup;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#isInfected()
     */
    @Override
    public boolean isInfected() {
        return this.diseaseGroup == DiseaseGroup.INFECTED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#isRecovered()
     */
    @Override
    public boolean isRecovered() {
        return this.diseaseGroup == DiseaseGroup.RECOVERED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#fightDisease()
     */
    @Override
    public void fightDisease() {
        if (this.disease == null) {
            return;
        }

        this.disease.evolve();
        if (this.disease.isCured()) {
            this.disease = null;
            this.diseaseGroup = DiseaseGroup.RECOVERED;
        }
        notifyDiseaseChangeListeners();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#getDiseaseSpecs()
     */
    @Override
    public DiseaseSpecs getDiseaseSpecs() {
        return diseaseSpecs;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#getTimeUntilRecovered()
     */
    @Override
    public int getTimeUntilRecovered() {
        if (isInfected()) {
            return this.disease.getTimeUntilCured();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#addListener(
     * nl.uu.socnetid.networkgames.actors.ActionPerformedListener)
     */
    @Override
    public void addActionPerformedListener(ActionPerformedListener actionPerformedListener) {
        this.actionPerformedListeners.add(actionPerformedListener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeActionPerformedListener(
     * nl.uu.socnetid.networkgames.actors.ActionPerformedListener)
     */
    @Override
    public void removeActionPerformedListener(ActionPerformedListener actionPerformedListener) {
        this.actionPerformedListeners.remove(actionPerformedListener);
    }

    /**
     * Notifies the actionPerformedListeners of task completion.
     */
    private final void notifyActionPerformedListeners() {
        Iterator<ActionPerformedListener> listenersIt = this.actionPerformedListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActionPerformed(this);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#addDiseaseChangeListener(nl.uu.socnetid.networkgames.actors.DiseaseChangeListener)
     */
    @Override
    public void addDiseaseChangeListener(DiseaseChangeListener diseaseChangeListener) {
        this.diseaseChangeListeners.add(diseaseChangeListener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeDiseaseChangeListener(nl.uu.socnetid.networkgames.actors.DiseaseChangeListener)
     */
    @Override
    public void removeDiseaseChangeListener(DiseaseChangeListener diseaseChangeListener) {
        this.diseaseChangeListeners.remove(diseaseChangeListener);
    }

    /**
     * Notifies the diseaseChangeListeners of task completion.
     */
    private final void notifyDiseaseChangeListeners() {
        Iterator<DiseaseChangeListener> listenersIt = this.diseaseChangeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyDiseaseChanged(this);
        }
    }

}
