package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.Disease;
import nl.uu.socnetid.network_games.disease.InfectionState;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * Shared fields and methods for all {@link Player} types.
 *
 * @author Hendrik Nunner
 */
public abstract class AbstractPlayer implements Player {

    // logger
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(AbstractPlayer.class);

    // unique identifier
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();

    // utility function
    public UtilityFunction utilityFunction;

    // co-players
    private List<Player> coPlayers;
    // personal connections
    private List<Player> connections = new ArrayList<Player>();

    // disease
    private InfectionState infectionState;
    private Disease disease;

    // flag indicating whether the player is satisfied with her current connections
    private boolean satisfied = false;

    // concurrency lock
    private Lock lock;


    /**
     * Constructor.
     */
    protected AbstractPlayer() {
        this.infectionState = InfectionState.SUSCEPTIBLE;
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
     * @see nl.uu.socnetid.network_games.Player#getCurrentUtility()
     */
    @Override
    public double getUtility() {
        return getUtility(this.connections);
    }

    /**
     * Gets the utility for a player based on a list of connections.
     *
     * @param connections
     *          the connections to compute the utility for
     * @return the utility for a player based on a list of connections
     */
    protected double getUtility(List<Player> connections) {
        return utilityFunction.getUtility(this, connections);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#setUtilityFunction(
     * nl.uu.socnetid.network_games.utility_functions.UtilityFunction)
     */
    @Override
    public void setUtilityFunction(UtilityFunction utilityFunction) {
        this.utilityFunction = utilityFunction;
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
        return this.connections.add(newConnection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean removeConnection(Player connection) {
        if (connection.equals(this)) {
            throw new RuntimeException("Unable to remove reflexive connections.");
        }
        return this.connections.remove(connection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {
        this.connections = new ArrayList<Player>();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#destroy()
     */
    @Override
    public void destroy() { }




    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getInfectionState()
     */
    @Override
    public InfectionState getInfectionState() {
        return this.infectionState;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#infect(nl.uu.socnetid.network_games.disease.Disease)
     */
    @Override
    public void infect(Disease disease) {
        if (isImmune()) {
            return;
        }

        this.disease = disease;
        this.infectionState = InfectionState.INFECTED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#computeTransmissions()
     */
    @Override
    public void computeTransmissions() {

        Iterator<Player> connectionsIt = this.connections.iterator();
        while (connectionsIt.hasNext()) {
            Player currConnection = connectionsIt.next();

            if (!currConnection.isInfected() && !currConnection.isImmune()
                    && this.disease.isTransmitted()) {
                try {
                    Disease newDiseaseInstance = this.disease.getClass().newInstance();
                    currConnection.infect(newDiseaseInstance);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isInfected()
     */
    @Override
    public boolean isInfected() {
        return this.disease != null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isImmune()
     */
    @Override
    public boolean isImmune() {
        return false;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        if (this.disease == null) {
            return false;
        }
        return this.disease.isInfectious();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#hasSymptoms()
     */
    @Override
    public boolean hasSymptoms() {
        if (this.disease == null) {
            return false;
        }
        return this.disease.isVisible();
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
            this.infectionState = InfectionState.RECOVERED;
        }
    }

}
