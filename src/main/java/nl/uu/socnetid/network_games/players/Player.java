package nl.uu.socnetid.network_games.players;

import java.util.List;

import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * Interface of a basic player.
 *
 * @author Hendrik Nunner
 */
public interface Player extends Comparable<Player> {

    /**
     * Initializes the player's list of co-players.
     *
     * @param allPlayers
     *          all player's engaged in the game
     */
    void initCoPlayers(List<Player> allPlayers);

    /**
     * Getter for the player's unique identifier.
     *
     * @return the player's unique identifier
     */
    long getId();

    /**
     * Getter for the player's current utility.
     *
     * @return the player's current utility
     */
    public double getUtility();

    /**
     * Sets the utility function.
     *
     * @param utilityFunction
     *          the utility function to set
     */
    public void setUtilityFunction(UtilityFunction utilityFunction);

    /**
     * Gets the connections of the player.
     *
     * @return the connections of the player
     */
    List<Player> getConnections();

    /**
     * Gets a random connection of the player.
     *
     * @return a random connection of the player.
     */
    Player getRandomConnection();

    /**
     * Gets a co-player that is not yet connected to the player herself.
     *
     * @return a co-player that is not yet connected to the player herself
     */
    Player getRandomNotYetConnectedPlayer();

    /**
     * Adds a new connection between the player and another player.
     *
     * @param newConnection
     *          the player to create the new connection to
     * @return true if connection is created, false otherwise
     */
    boolean addConnection(Player newConnection);

    /**
     * Removes a connection between the player and another player.
     *
     * @param connection
     *          the other player to remove the connection to
     * @return true if connection is removed, false otherwise
     */
    boolean removeConnection(Player connection);

    /**
     * Removes all connections of the player.
     */
    void removeAllConnections();

    /**
     * Checks whether a new connection creates higher utility. If so,
     * the player for a new desired connection is returned, null otherwise.
     *
     * @return the player to create a new connection to, or null in case no
     * new connection is ought to be created
     */
    Player seekNewConnection();

    /**
     * Checks whether the removal of an existing connection creates higher
     * utility. If so, the player to break the connection is returned,
     * Null otherwise.
     *
     * @return the player to break the connection, or null in case no new
     * connection is ought to be broken
     */
    Player seekCostlyConnection();

	/**
	 * Request to accept a new connection.
	 *
	 * @param newConnection
	 *         the player requesting the new connection
	 * @return true if the connection is accepted, false otherwise
	 */
	boolean acceptConnection(Player newConnection);

	/**
	 * Clean up routine when player is removed from the game.
	 */
	void destroy();

}
