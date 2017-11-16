package nl.uu.socnetid.network_games.networks;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public interface Network {

    List<Player> getPlayers();

    List<Player> getConnectionsOfPlayer(Player player);

}
