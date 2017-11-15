package nl.uu.socnetid.network_games.players;

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
     * @see nl.uu.socnetid.network_games.Player#performAction()
     */
    @Override
    public void performAction() {

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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Player p) {
        return (int) (this.getId() - p.getId());
    }

}
