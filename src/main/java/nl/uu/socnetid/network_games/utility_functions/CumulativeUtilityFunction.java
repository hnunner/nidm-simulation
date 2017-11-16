package nl.uu.socnetid.network_games.utility_functions;

import java.util.Iterator;
import java.util.List;

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
    public double getUtility(Player player, List<Player> connections) {
        double utility = 0;

        Iterator<Player> directIt = connections.iterator();

        while (directIt.hasNext()) {
            Player directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            utility += UTILITY_DIRECT_CONNECTIONS;

            // indirect connections at distance 2
            List<Player> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            Iterator<Player> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Player indirectConnection = indirectIt.next();

                if (indirectConnection.equals(player)
                        || connections.contains(indirectConnection)) {
                    continue;
                }
                utility += UTILITY_INDIRECT_CONNECTIONS;
            }
        }

        return utility;
    }

}
