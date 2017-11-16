package nl.uu.socnetid.network_games.utility_functions;

import java.util.Iterator;
import java.util.Set;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public final class CumulativeUtilityFunction implements UtilityFunction {

    // how much is a connection worth
    private static final float UTILITY_DIRECT_CONNECTIONS = 1f;
    private static final float UTILITY_INDIRECT_CONNECTIONS = 0.5f;

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utility_functions.UtilityFunction#getUtility()
     */
    @Override
    public double getUtility(Player player, Set<Player> connections) {
        double utility = 0;

        Iterator<Player> directIt = connections.iterator();

        while (directIt.hasNext()) {
            Player directConnection = directIt.next();
            utility += UTILITY_DIRECT_CONNECTIONS;

            // indirect connections at distance 2
            Iterator<Player> indirectIt = directConnection.getConnections().iterator();
            while (indirectIt.hasNext()) {
                Player indirectConnection = indirectIt.next();

                if (indirectConnection.equals(player)) {
                    continue;
                }
                utility += UTILITY_INDIRECT_CONNECTIONS;
            }
        }

        return utility;
    }

}
