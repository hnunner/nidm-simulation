package nl.uu.socnetid.network_games.players;

import java.util.List;
import java.util.Map;

/**
 * Interface of a basic player.
 *
 * @author Hendrik Nunner
 */
public interface Player {

    /**
     * Getter for the player's unique identifier.
     *
     * @return the player's unique identifier
     */
    long getId();

	/**
	 * Initializes the list of co-players. This is done by adding all
	 * players, but the player herself to the list of co-players.
	 *
	 * @param players
	 *         all players of the game, including the player herself
	 */
	void initCoPlayers(List<Player> players);

	/**
	 * Method to perform an action.
	 */
	void performAction();

	/**
     * @return players with no existing connection to this
     */
	Map<Long, Player> getNotConnectedTos();

	/**
     * @return the player's connections
     */
	Map<Long, Player> getConnectedTos();

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
