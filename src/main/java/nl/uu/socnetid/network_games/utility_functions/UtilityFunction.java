package nl.uu.socnetid.network_games.utility_functions;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 *
 * @author Hendrik Nunner
 */
public interface UtilityFunction {

    long getUtility(List<Player> players);

}
