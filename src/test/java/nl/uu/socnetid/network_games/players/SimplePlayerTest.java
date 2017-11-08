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

		player1.initCoPlayers(players);
		player2.initCoPlayers(players);
	}
//
//	/**
//	 * Performed before each single test: Debug output.
//	 */
//	@Before
//	public void beforeEachTest() {
//		System.out.println("This is executed before each Test");
//	}
//
//	/**
//	 * Performed after eacg single test: Debug output.
//	 */
//	@After
//	public void afterEachTest() {
//		System.out.println("This is exceuted after each Test");
//	}

	/**
	 * Test of performing an action.
	 */
	@Test
	public void testIds() {
		assertEquals(1, player1.getId());
        assertEquals(2, player2.getId());
	}

    /**
     * Test of performing an action.
     */
    @Test
    public void testCoPlayers() {
        List<Player> p1CoPlayers = player1.getCoPlayers();
        assertEquals(1, p1CoPlayers.size());
        assertEquals(2, p1CoPlayers.get(0).getId());

        List<Player> p2CoPlayers = player2.getCoPlayers();
        assertEquals(1, p2CoPlayers.size());
        assertEquals(1, p2CoPlayers.get(0).getId());
    }

}
