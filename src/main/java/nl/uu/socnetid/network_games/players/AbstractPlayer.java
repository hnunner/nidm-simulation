package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

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

    /** all co-players in the game */
    private List<Player> coPlayers = new ArrayList<>();
    /** connections to co-players in the game */
    private List<Player> connections = new ArrayList<>();


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
                coPlayers.add(currPlayer);
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.Player#performAction()
     */
    @Override
    public void performAction() {
        if (coPlayers.isEmpty()) {
            logger.warn("List of co-players is empty. This may be an error in "
                    + "case there are more than one player involved in the game.");
        }

        // create a new connection?
        Long coPlayerId = checkToCreateConnection();
        if (coPlayerId != null) {

            // only one move per player per round
            return;
        }

        // break an existing connection?
        coPlayerId = checkToBreakConnection();
        if (coPlayerId != null) {

            // only one move per player per round
            return;
        }

    }

    /**
     * Checks whether a new connection creates higher utility. If so,
     * the ID of the player for a new desired connection is returned,
     * Null otherwise.
     *
     * @return the ID of the player to create a new conneciton to,
     * or Null in case no new connection is ought to be created
     */
    protected abstract Long checkToCreateConnection();

    /**
     * Checks whether the removal of an existing connection creates
     * higher utility. If so, the ID of the player to break the
     * connection is returned, Null otherwise.
     *
     * @return the ID of the player to break a new conneciton,
     * or Null in case no new connection is ought to be broken
     */
    protected abstract Long checkToBreakConnection();


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getCoPlayers()
     */
    @Override
    public List<Player> getCoPlayers() {
        return coPlayers;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#getConnections()
     */
    @Override
    public List<Player> getConnections() {
        return connections;
    }

}
