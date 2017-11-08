package nl.uu.socnetid.network_games.players;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * Shared fields and methods for all {@link Player} types.
 *
 * @author Hendrik Nunner
 */
public abstract class AbstractPlayer implements Player {

    /** Logger */
    private final static Logger logger = Logger.getLogger(AbstractPlayer.class);

    /** unique identifier */
    private static final AtomicLong NEXT_ID = new AtomicLong(1);
    private final long id = NEXT_ID.getAndIncrement();



    ////////// USE (BASIC)NETWORK INSTEAD ////////////

//    /** co-players with noin the game */
//    protected Map<Long, Player> notConnectedTos = new HashMap<Long, Player>();
//    /** connections to co-players in the game */
//    protected Map<Long, Player> connectedTos = new HashMap<Long, Player>();
//    this is realized with BasicNetwork.java

    /** utility function */
    protected UtilityFunction utilFunc;
    /** current utility */
    protected long currUtil = 0;


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#initCoPlayers(java.util.List)
     */
    @Override
    public void initCoPlayers(List<Player> players) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player currPlayer = it.next();
            if (currPlayer.getId() != this.getId()) {
                notConnectedTos.put(currPlayer.getId(), currPlayer);
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#performAction()
     */
    @Override
    public void performAction() {
//        if (notConnectedTos.isEmpty() && connectedTos.isEmpty()) {
//            logger.warn("No co-players available. This may be an error in case "
//                    + "there are more than one player involved in the game.");
//        }
//
//        // create a new connection?
//        Player coPlayer = checkToCreateConnection();
//        if (coPlayer != null) {
//            logger.debug("Trying to create a new connection between players "
//                    + getId() + " and " + coPlayer.getId() + ".");
//
//            if (coPlayer.requestConnection(this)) {
//                connectedTos.put(coPlayer.getId(), coPlayer);
//            }
//
//            // only one move per player per round
//            return;
//        }
//
//        // break an existing connection?
//        coPlayer = checkToBreakConnection();
//        if (coPlayer != null) {
//            logger.debug("Trying to break the connection between players "
//                    + getId() + " and " + coPlayer.getId() + ".");
//
//            // only one move per player per round
//            return;
//        }
    }

    /**
     * Checks whether a new connection creates higher utility. If so,
     * the player for a new desired connection is returned, null otherwise.
     *
     * @return the player to create a new connection to, or null in case no
     * new connection is ought to be created
     */
    protected abstract Player checkToCreateConnection();

    /**
     * Checks whether the removal of an existing connection creates higher
     * utility. If so, the player to break the connection is returned,
     * Null otherwise.
     *
     * @return the player to break the connection, or null in case no new
     * connection is ought to be broken
     */
    protected abstract Player checkToBreakConnection();


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getCoPlayers()
     */
    @Override
    public Map<Long, Player> getNotConnectedTos() {
        return notConnectedTos;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getConnections()
     */
    @Override
    public Map<Long, Player> getConnectedTos() {
//        return connectedTos;
        return null;
    }

}
