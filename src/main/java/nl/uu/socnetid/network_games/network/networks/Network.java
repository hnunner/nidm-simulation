package nl.uu.socnetid.network_games.network.networks;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public interface Network {

    /**
     * Adds a player to the network.
     *
     * @param player
     *          the player to be added
     */
    void addPlayer(Player player);

    /**
     * Removes a player from the network.
     */
    void removePlayer();

    /**
     * Gets all players within the network.
     *
     * @return all players within the network.
     */
    List<Player> getPlayers();

    /**
     * Gets all connections of a specific player within the network.
     *
     * @param player
     *          the player to get the connections for
     * @return all network connections of the specified player
     */
    List<Player> getConnectionsOfPlayer(Player player);

    /**
     * Clears all connections between the players.
     */
    void clearConnections();

    /**
     * Infects a random player.
     */
    void infectRandomPlayer();

    /**
     * Infects a specific player.
     *
     * @param id
     *          the id of the player to be infectd
     */
    void infectPlayer(long id);

}
