package nl.uu.socnetid.network_games.utilities;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public interface UtilityFunction {

    /**
     * Computes the utility for a player based on her social connections.
     *
     * @param player
     *          the player to compute the utility for
     * @param connections
     *          the player's connections
     * @return the player's utility based on her connections
     */
    double getUtility(Player player, List<Player> connections);

    /**
     * @return the name of the utility function to be used in the stats window
     */
    String getStatsName();

    /**
     * @return the utility for direct connections
     */
    double getAlpha();

    /**
     * @return the utility for indirect connections
     */
    double getBeta();

    /**
     * @return the costs to maintain direct connections
     */
    double getC();

}
