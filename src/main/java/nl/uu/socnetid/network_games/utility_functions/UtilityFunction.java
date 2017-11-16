package nl.uu.socnetid.network_games.utility_functions;

import java.util.Set;

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
    double getUtility(Player player, Set<Player> connections);

}
