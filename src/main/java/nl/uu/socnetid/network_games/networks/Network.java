package nl.uu.socnetid.network_games.networks;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public interface Network {

    void addConnection(Player player1, Player player2);

    void removeConnection(Player player1, Player player2);

    List<Player> getConnectionsOfPlayer(Player player);

    Player getRandomConnectionOfPlayer(Player player);

    Player getRandomNotYetConnectedPlayerForPlayer(Player player);

}
