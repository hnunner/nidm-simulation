package nl.uu.socnetid.network_games.utilities;

import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class IRTC implements UtilityFunction {

    private final double alpha;
    private final double beta;
    private final double mu;
    private final double c;
    private final double gamma;
    private final double delta;

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param beta
     *          the benefit of an indirect connection
     * @param mu
     *          the care factor for an infected direct connection
     * @param c
     *          the maintenance costs for a direct connection
     * @param gamma
     *          the infection rate of the infectious disease
     * @param delta
     *          the severity of the disease
     */
    public IRTC(double alpha, double beta, double mu, double c, double gamma, double delta) {
        this.alpha = alpha;
        this.beta = beta;
        this.mu = mu;
        this.c= c;
        this.gamma = gamma;
        this.delta = delta;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.network_games.players.Player, java.util.List)
     */
    @Override
    public double getUtility(Player player, List<Player> connections) {
        double utility = 0;
        double riskFactor = player.getRiskFactor();

        Iterator<Player> directIt = connections.iterator();
        while (directIt.hasNext()) {

            Player directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            // TODO implement utility of direct connections (benefits - costs)





//            utility += this.directUtility - this.costs;
//            if (directConnection.isInfected()) {
//                utility -= directConnection.getNursingCosts();
//            }


            // TODO implement effect of disease





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


                // TODO implement utility of indirect connections


//                utility += this.indirectUtility;




            }
        }

        return utility;
    }

}
