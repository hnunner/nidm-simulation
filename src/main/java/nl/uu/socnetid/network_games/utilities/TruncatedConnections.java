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


    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.network_games.players.Player, java.util.List)
     */
    @Override
    public Utility getUtility(Player player, List<Player> connections) {
        double benefitDirectConnections = 0.0;
        double benefitIndirectConnections = 0.0;
        double costsDirectConnections = 0.0;
        double effectOfDisease = 0.0;

        Iterator<Player> directIt = connections.iterator();

        while (directIt.hasNext()) {
            Player directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            benefitDirectConnections += this.directUtility;
            if (directConnection.isInfected()) {
                costsDirectConnections += this.costs * player.getDiseaseSpecs().getMu();
            } else {
                costsDirectConnections += this.costs;
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
                benefitIndirectConnections += this.indirectUtility;
            }
        }

        return new Utility(benefitDirectConnections,
                benefitIndirectConnections,
                costsDirectConnections,
                effectOfDisease);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "TC";
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getAlpha()
     */
    @Override
    public double getAlpha() {
        return this.directUtility;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getBeta()
     */
    @Override
    public double getBeta() {
        return this.indirectUtility;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getC()
     */
    @Override
    public double getC() {
        return this.costs;
    }

}
