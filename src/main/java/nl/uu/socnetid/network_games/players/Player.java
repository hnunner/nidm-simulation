package nl.uu.socnetid.network_games.players;

import java.util.List;

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
     * @return the coPlayers
     */
	List<Player> getCoPlayers();

	/**
     * @return the connections
     */
	List<Player> getConnections();

}
