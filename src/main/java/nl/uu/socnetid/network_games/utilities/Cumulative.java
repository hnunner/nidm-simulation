package nl.uu.socnetid.network_games.utilities;

import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public final class Cumulative implements UtilityFunction {

    // default values
    private static final double DEFAULT_DIRECT = 1.0;
    private static final double DEFAULT_INDIRECT = 0.5;

    // how much is a connection worth
    private final double utilityDirectConnections;
    private final double utilityIndirectConnections;

    /**
     * Constructor with default values.
     */
    public Cumulative() {
        this(DEFAULT_DIRECT, DEFAULT_INDIRECT);
    }

    /**
     * Constructor
     *
     * @param utilityDirectConnections
     *          the utility for direct connections
     * @param utilityIndirectConnections
     *          the utility for indirect connections (distance 2)
     */
    public Cumulative(double utilityDirectConnections, double utilityIndirectConnections) {
        this.utilityDirectConnections = utilityDirectConnections;
        this.utilityIndirectConnections = utilityIndirectConnections;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.network_games.players.Player, java.util.List, nl.uu.socnetid.network_games.disease.Disease)
     */
    @Override
    public Utility getUtility(Player player, List<Player> connections) {

        // BEWARE: disease is being neglected in this function

        double benefitDirectConnections = 0;
        double benefitIndirectConnections = 0;

        Iterator<Player> directIt = connections.iterator();

        while (directIt.hasNext()) {
            Player directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            benefitDirectConnections += this.utilityDirectConnections;

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
                benefitIndirectConnections += this.utilityIndirectConnections;
            }
        }
        return new Utility(benefitDirectConnections, benefitIndirectConnections, 0, 0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CUM";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getAlpha()
     */
    @Override
    public double getAlpha() {
        return this.utilityDirectConnections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getBeta()
     */
    @Override
    public double getBeta() {
        return this.utilityIndirectConnections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getC()
     */
    @Override
    public double getC() {
        return 0;
    }

}
