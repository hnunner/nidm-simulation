package nl.uu.socnetid.network_games.players;

/**
 * Interface of a basic player.
 *
 * @author Hendrik Nunner
 */
public interface Player extends Comparable<Player> {

    /**
     * Getter for the player's unique identifier.
     *
     * @return the player's unique identifier
     */
    long getId();

	/**
	 * Method to perform an action.
	 */
	void performAction();

	/**
	 * Request to create a new connection between the calling player
	 * and the receiving player.
	 *
	 * @param player
	 *         the calling player
	 * @return true if the connection is accepted, false otherwise
	 */
	boolean requestConnection(Player player);

}
