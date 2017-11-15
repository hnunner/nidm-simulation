package nl.uu.socnetid.network_games.players;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test cases for the {@link SimplePlayer} class.
 *
 * @author Hendrik Nunner
 */
public class SimplePlayerTest {

    /** instance of the players to be tested. */
	private static Player player1;
	private static Player player2;

	/** list of all players */
	private static List<Player> players = new ArrayList<Player>();


	/**
	 * Performed before the whole test case: Initialization of the player.
	 */
	@BeforeClass
	public static void initPlayer() {
		player1 = SimplePlayer.newInstance();
		players.add(player1);

		player2 = SimplePlayer.newInstance();
		players.add(player2);
	}

	/**
	 * Test of id coherence.
	 */
	@Test
	public void testIds() {
		assertEquals(1, player1.getId());
        assertEquals(2, player2.getId());
	}

}
