package nl.uu.socnetid.network_games.network.networks;

import java.util.List;

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
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
     * Get the player with the corresponding identifier.
     *
     * @param id
     *          the identifier of the player to get
     * @return the player with the corresponding identifier
     */
    Player getPlayer(long id);

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
     *
     * @param diseaseSpecs
     *          the characteristics of the disease to infect a player with
     */
    void infectRandomPlayer(DiseaseSpecs diseaseSpecs);

    /**
     * Toggles the infection of a specific player.
     *
     * @param playerId
     *          the player's id
     * @param diseaseSpecs
     *          the characteristics of the disease to infect the player with
     */
    void toggleInfection(long playerId, DiseaseSpecs diseaseSpecs);

    /**
     * Sets whether the network is stable or not.
     *
     * @param stable
     *          flag whether the network is stable or not
     */
    void setStable(boolean stable);

    /**
     * @return true if no player wants to add or remove connections, false otherwise
     */
    boolean isStable();

    /**
     * Adds a listener to be notified when network stability changes.
     *
     * @param listener
     *          the listener to be notified
     */
    void addListener(final NetworkStabilityListener listener);

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    void removeListener(final NetworkStabilityListener listener);

}
