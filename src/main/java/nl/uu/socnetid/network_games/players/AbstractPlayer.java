package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

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

    // current utility
    protected long currentUtility = 0;
    // utility function
    protected UtilityFunction utilityFunction;

    // co-players
    private List<Player> coPlayers;
    // personal connections
    private List<Player> connections = new ArrayList<Player>();


    /**
     * Constructor.
     *
     * @param utilityFunction
     *          the utility function (rules of the network game)
     */
    protected AbstractPlayer(UtilityFunction utilityFunction) {
        this.utilityFunction = utilityFunction;
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
    public long getCurrentUtility() {
        return currentUtility;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getConnections()
     */
    @Override
    public List<Player> getConnections() {
        return connections;
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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Player p) {
        return (int) (this.getId() - p.getId());
    }

}
