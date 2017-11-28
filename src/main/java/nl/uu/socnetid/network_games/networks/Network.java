package nl.uu.socnetid.network_games.networks;

import java.util.List;

import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

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
     * Triggers to start the simulation. This process runs until either a stable network has
     * been found (when no player wants to add or remove a connection), or a (standard) has
     * been computed.
     */
    void simulate();

    /**
     * Triggers to start the simulation. This process runs until either a stable network has
     * been found (when no player wants to add or remove a connection), or a to be defined
     * has been computed. Further a delay can be defined that increases the contemplation
     * time of a player (e.g., for graphical simulations).
     *
     * @param maxRounds
     *          the maximum number of rounds
     * @param delay
     *          the delay between the actions of two players
     */
    void simulate(int maxRounds, int delay);

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

}
