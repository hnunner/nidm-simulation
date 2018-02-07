package nl.uu.socnetid.network_games.utilities;

import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnections implements UtilityFunction {

    // how much is a connection worth
    private final double directUtility;
    private final double indirectUtility;
    private final double costs;

    /**
     * Constructor.
     *
     * @param delta
     *          the benefit for connections, deteriorating over distance
     * @param costs
     *          the costs to maintain direct connections
     */
    public TruncatedConnections(double delta, double costs) {
        this.directUtility = delta;
        this.indirectUtility = delta * delta;
        this.costs = costs;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.network_games.players.Player, java.util.List)
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

            if (directConnection.isInfected()) {
                utility += this.directUtility - this.costs * directConnection.getMu();
            } else {
                utility += this.directUtility - this.costs;
            }

            // indirect connections at distance 2
            List<Player> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            Iterator<Player> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Player indirectConnection = indirectIt.next();

                if (indirectConnection.equals(player)




                        ////////// TODO: ??? DOUBLE BENEFITS FOR DIRECT + INDIRECT ??? //////////
                        || connections.contains(indirectConnection)) {
                    continue;
                }
                utility += this.indirectUtility;
            }
        }

        return utility;
    }

}
