package nl.uu.socnetid.network_games.players;

import org.apache.log4j.Logger;

/**
 * Implementation of a simple {@link Player}.
 *
 * @author Hendrik Nunner
 */
public class SimplePlayer extends AbstractPlayer implements Player {

    /** Logger */
	private final static Logger logger = Logger.getLogger(SimplePlayer.class);


    /**
     * Private constructor.
     */
    private SimplePlayer() { }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @return a new {@link Player} instance
     */
    public static Player newInstance() {
        return new SimplePlayer();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToCreateConnection()
     */
    @Override
    protected Long checkToCreateConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToBreakConnection()
     */
    @Override
    protected Long checkToBreakConnection() {
        // TODO Auto-generated method stub
        return null;
    }

}
