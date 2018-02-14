package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.Disease;
import nl.uu.socnetid.network_games.disease.DiseaseFactory;
import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
import nl.uu.socnetid.network_games.disease.types.DiseaseGroup;
import nl.uu.socnetid.network_games.utilities.Utility;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

/**
 * Shared fields and methods for all {@link Player} types.
 *
 * @author Hendrik Nunner
 */
public abstract class AbstractPlayer implements Player {

    // risk neutral risk factor
    protected static final double RISK_NEUTRAL = 1.0;

    // logger
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(AbstractPlayer.class);

    // unique identifier
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    // risk factor
    private double riskFactor;

    // utility function
    private UtilityFunction utilityFunction;

    // co-players
    private List<Player> coPlayers;
    // personal connections
    private List<Player> connections = new ArrayList<Player>();

    // disease
    private DiseaseGroup diseaseGroup;
    private DiseaseSpecs diseaseSpecs;
    private Disease disease;

    // flag indicating whether the player is satisfied with her current connections
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
     *          the function the player uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease characteristics that is or might become present in the network
     * @param riskFactor
     *          the risk factor of a player (<1: risk seeking, =1: risk neutral; >1: risk averse)
     */
    protected AbstractPlayer(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        this.utilityFunction = utilityFunction;
        this.diseaseSpecs = diseaseSpecs;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        this.riskFactor = riskFactor;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#initCoPlayers()
     */
    @Override
    public void initCoPlayers(List<Player> allPlayers) {
        coPlayers = new ArrayList<Player>(allPlayers);
        coPlayers.remove(this);
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Player p) {
        return (int) (this.getId() - p.getId());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#setLock()
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

            // some delay before each player moves (e.g., for animation processes)
            //Thread.sleep(this.delay * 100);

            // players try to connect or disconnect first in random order
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
     * A player tries to connect. That means she first seeks a connection that gives higher
     * utility as the current utility; and then requests the corresponding player to establish
     * the new connection.
     */
    private boolean tryToConnect() {
        Player potentialNewConnection = seekNewConnection();
        if (potentialNewConnection != null) {
            // other player accepting connection?
            if (potentialNewConnection.acceptConnection(this)) {
                addConnection(potentialNewConnection);
                potentialNewConnection.addConnection(this);
            }
        }
        // the desire to create new connection counts as a move
        return (potentialNewConnection != null);
    }

    /**
     * A player tries to disconnect. That means she seeks a connection that creates more costs
     * than benefits. In case she finds such a connection, she removes the costly connection.
     */
    private boolean tryToDisconnect() {
        Player costlyConnection = seekCostlyConnection();
        if (costlyConnection != null) {
            this.removeConnection(costlyConnection);
            costlyConnection.removeConnection(this);
        }
        // the desire to remove a connection counts as a move
        return (costlyConnection != null);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getUtility()
     */
    @Override
    public Utility getUtility() {
        return getUtility(this.connections);
    }

    /**
     * Gets the utility for a player based on a list of connections.
     *
     * @param connections
     *          the connections to compute the utility for
     * @return the utility for a player based on a list of connections
     */
    protected Utility getUtility(List<Player> connections) {
        return utilityFunction.getUtility(this, connections);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getRiskFactor()
     */
    @Override
    public double getRiskFactor() {
        return this.riskFactor;
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getUtilityFunction()
     */
    @Override
    public UtilityFunction getUtilityFunction() {
        return this.utilityFunction;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getConnections()
     */
    @Override
    public List<Player> getConnections() {
        return this.connections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#isSatisfied()
     */
    @Override
    public boolean isSatisfied() {
        return this.satisfied;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getRandomConnection()
     */
    @Override
    public Player getRandomConnection() {
        if (connections.size() == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return connections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getRandomNotYetConnectedPlayer()
     */
    @Override
    public Player getRandomNotYetConnectedPlayer() {
        ArrayList<Player> noConnections = new ArrayList<Player>(coPlayers);
        noConnections.removeAll(connections);

        if (noConnections.size() == 0) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(noConnections.size());
        return noConnections.get(index);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#addConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean addConnection(Player newConnection) {
        if (newConnection.equals(this)) {
            throw new RuntimeException("Unable to create reflexive connections.");
        }
        if (connections.contains(newConnection)) {
            throw new RuntimeException("Unable to create more than one connection to the same player.");
        }
        boolean connectionAdded = this.connections.add(newConnection);
        notifyActionPerformedListeners();
        return connectionAdded;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean removeConnection(Player connection) {
        if (connection.equals(this)) {
            throw new RuntimeException("Unable to remove reflexive connections.");
        }
        boolean connectionRemoved = this.connections.remove(connection);
        notifyActionPerformedListeners();
        return connectionRemoved;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {
        this.connections = new ArrayList<Player>();
        notifyActionPerformedListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#destroy()
     */
    @Override
    public void destroy() { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#cure()
     */
    @Override
    public void cure() {
        this.disease = null;
        this.diseaseGroup = DiseaseGroup.RECOVERED;
        notifyDiseaseChangeListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#infect(nl.uu.socnetid.network_games.disease.DiseaseSpecs)
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
     * @see nl.uu.socnetid.network_games.players.Player#forceInfect(nl.uu.socnetid.network_games.disease.DiseaseSpecs)
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
     * @see nl.uu.socnetid.network_games.players.Player#computeTransmissions()
     */
    @Override
    public void computeTransmissions() {

        Iterator<Player> connectionsIt = this.connections.iterator();
        while (connectionsIt.hasNext()) {
            Player currConnection = connectionsIt.next();

            if (!currConnection.isInfected() && !currConnection.isRecovered()
                    && this.disease.isTransmitted()) {
                currConnection.infect(diseaseSpecs);
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isSusceptible()
     */
    @Override
    public boolean isSusceptible() {
        return this.diseaseGroup == DiseaseGroup.SUSCEPTIBLE;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#makeSusceptible()
     */
    @Override
    public void makeSusceptible() {
        this.disease = null;
        this.diseaseGroup = DiseaseGroup.SUSCEPTIBLE;
        notifyDiseaseChangeListeners();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getDiseaseGroup()
     */
    @Override
    public DiseaseGroup getDiseaseGroup() {
        return this.diseaseGroup;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isInfected()
     */
    @Override
    public boolean isInfected() {
        return this.diseaseGroup == DiseaseGroup.INFECTED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isRecovered()
     */
    @Override
    public boolean isRecovered() {
        return this.diseaseGroup == DiseaseGroup.RECOVERED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#fightDisease()
     */
    @Override
    public void fightDisease() {
        if (this.disease == null) {
            return;
        }

        this.disease.evolve();
        if (this.disease.isDefeated()) {
            this.disease = null;
            this.diseaseGroup = DiseaseGroup.RECOVERED;
        }
        notifyDiseaseChangeListeners();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getDiseaseSpecs()
     */
    @Override
    public DiseaseSpecs getDiseaseSpecs() {
        return diseaseSpecs;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getTimeUntilRecovered()
     */
    @Override
    public int getTimeUntilRecovered() {
        if (isInfected()) {
            return this.disease.getTimeUntilRecovered();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#addListener(
     * nl.uu.socnetid.network_games.players.ActionPerformedListener)
     */
    @Override
    public void addActionPerformedListener(ActionPerformedListener actionPerformedListener) {
        this.actionPerformedListeners.add(actionPerformedListener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeActionPerformedListener(
     * nl.uu.socnetid.network_games.players.ActionPerformedListener)
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
     * @see nl.uu.socnetid.network_games.players.Player#addDiseaseChangeListener(nl.uu.socnetid.network_games.players.DiseaseChangeListener)
     */
    @Override
    public void addDiseaseChangeListener(DiseaseChangeListener diseaseChangeListener) {
        this.diseaseChangeListeners.add(diseaseChangeListener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeDiseaseChangeListener(nl.uu.socnetid.network_games.players.DiseaseChangeListener)
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
