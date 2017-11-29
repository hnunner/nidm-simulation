package nl.uu.socnetid.network_games.networks;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public interface Network extends Runnable {

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
     * Initializes the utility function.
     *
     * @param utilityFunction
     *          the utility function
     */
    void initUtilityFunction(UtilityFunction utilityFunction);

    /**
     * Initializes the delay for the simulation.
     *
     * @param delay
     *          the delay
     */
    void initSimulationDelay(int delay);

    /**
     * Infects a random player.
     */
    void infectRandomPlayer();

    /**
     * Computes a single round of the disease dynamics of the network.
     */
    void computeDiseaseDynamics();

}
