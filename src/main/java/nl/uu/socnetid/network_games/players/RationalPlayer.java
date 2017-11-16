package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

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
    private RationalPlayer(UtilityFunction utilityFunction) {
        super(utilityFunction);
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @param utilityFunction
     *          the utility function (rules of the game)
     * @return a new {@link Player} instance
     */
    public static Player newInstance(UtilityFunction utilityFunction) {
        return new RationalPlayer(utilityFunction);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToCreateConnection()
     */
    @Override
    public Player seekNewConnection() {
        Player potentialConnection = getRandomNotYetConnectedPlayer();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.add(potentialConnection);

        if (this.getUtility() > this.getUtility(potentialConnections)) {
            return null;
        }
        return potentialConnection;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.AbstractPlayer#checkToBreakConnection()
     */
    @Override
    public Player seekCostlyConnection() {
        Player potentialRemoval = getRandomConnection();

        List<Player> potentialConnections = new ArrayList<Player>(this.getConnections());
        potentialConnections.remove(potentialRemoval);

        if (this.getUtility() > this.getUtility(potentialConnections)) {
            return null;
        }
        return potentialRemoval;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#requestConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean acceptConnection(Player newConnection) {
        List<Player> prospectiveConnections = new ArrayList<Player>(this.getConnections());
        prospectiveConnections.add(newConnection);
        return this.getUtility() < utilityFunction.getUtility(this, prospectiveConnections);
    }

}
