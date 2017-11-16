package nl.uu.socnetid.network_games.players;

import java.util.List;

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
    public long getCurrentUtility();




    List<Player> getConnections();

    Player getRandomConnection();

    /**
     * Gets a co-player that is not yet connected to the player herself.
     *
     * @return a co-player that is not yet connected to the player herself
     */
    Player getRandomNotYetConnectedPlayer();

    boolean addConnection(Player newConnection);

    boolean removeConnection(Player connection);









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

}
