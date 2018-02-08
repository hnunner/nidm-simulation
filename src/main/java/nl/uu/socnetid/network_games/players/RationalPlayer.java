package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implementation of a simple {@link Player}.
 *
 * @author Hendrik Nunner
 */
public class RationalPlayer extends AbstractPlayer implements Player {

    // logger
	@SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(RationalPlayer.class);


    /**
     * Private constructor.
     */
    protected RationalPlayer() {
        super();
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @return a new {@link Player} instance
     */
    public static Player newInstance() {
        return new RationalPlayer();
    }


    /**
     * Private constructor.
     *
     * @param riskFactor
     *          the risk factor of the new player
     */
    protected RationalPlayer(double riskFactor) {
        super();
        setRiskFactor(riskFactor);
    }

    /**
     * Factory method returning a new {@link Player} instance with a custom risk factor.
     *
     * @param riskFactor
     *          the custom risk factor
     * @return a new {@link Player} instance with a custom risk factor
     */
    public static Player newInstance(double riskFactor) {
        return new RationalPlayer(riskFactor);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToCreateConnection()
     */
    @Override
    public Player seekNewConnection() {
        Player potentialConnection = getRandomNotYetConnectedPlayer();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.add(potentialConnection);

        // connect if new connection creates higher or equal utility
        // TODO is higher OR EQUAL what we want, or only higher?
        if (this.getUtility(potentialConnections) >= this.getUtility()) {
            return potentialConnection;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToBreakConnection()
     */
    @Override
    public Player seekCostlyConnection() {
        Player potentialRemoval = getRandomConnection();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.remove(potentialRemoval);

        // disconnect only if removal creates higher utility
        if (this.getUtility(potentialConnections) > this.getUtility()) {
            return potentialRemoval;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#requestConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean acceptConnection(Player newConnection) {
        List<Player> prospectiveConnections = new ArrayList<Player>(this.getConnections());
        prospectiveConnections.add(newConnection);

        // accept connection if the new connection creates higher or equal utility
        // TODO is higher OR EQUAL what we want, or only higher?
        return this.getUtility(prospectiveConnections) >= this.getUtility();
    }

}
