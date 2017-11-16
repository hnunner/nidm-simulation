package nl.uu.socnetid.network_games.networks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;
import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;


/**
 * Test cases for the {@link SimpleNetwork} class.
 *
 * @author Hendrik Nunner
 */
public class SimpleNetworkTest {

    /** players */
    Player player1;
    Player player2;
    Player player3;
    Player player4;

	/** network */
	private Network network;


	/**
	 * Performed before each test: Initialization of the network.
	 */
	@Before
	public void initPlayer() {
	    UtilityFunction utilityFunction = new CumulativeUtilityFunction();

	    List<Player> players = new ArrayList<Player>();

	    player1 = RationalPlayer.newInstance(utilityFunction);
	    player2 = RationalPlayer.newInstance(utilityFunction);
	    player3 = RationalPlayer.newInstance(utilityFunction);
	    player4 = RationalPlayer.newInstance(utilityFunction);

        players.add(player1);
		players.add(player2);
		players.add(player3);
		players.add(player4);

		player1.initCoPlayers(players);
		player2.initCoPlayers(players);
		player3.initCoPlayers(players);
		player4.initCoPlayers(players);

		this.network = new SimpleNetwork(players);
	}

	/**
	 * Test of adding a connection.
	 */
	@Test
	public void testAddConnection() {

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

	    assertEquals(3, network.getConnectionsOfPlayer(player1).size());
	    assertEquals(1, network.getConnectionsOfPlayer(player2).size());
	    assertEquals(2, network.getConnectionsOfPlayer(player3).size());
	    assertEquals(2, network.getConnectionsOfPlayer(player4).size());
	}

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

        // remove connections
        player1.removeConnection(player2);
        player2.removeConnection(player1);

        player3.removeConnection(player4);
        player4.removeConnection(player3);

        assertEquals(2, network.getConnectionsOfPlayer(player1).size());
        assertEquals(0, network.getConnectionsOfPlayer(player2).size());
        assertEquals(1, network.getConnectionsOfPlayer(player3).size());
        assertEquals(1, network.getConnectionsOfPlayer(player4).size());
    }

    /**
     * Test of getting all connections of a specific player.
     */
    @Test
    public void testGetConnectionsOfPlayer() {

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

        List<Player> connectionsOfPlayer1 = this.network.getConnectionsOfPlayer(player1);
        assertFalse(connectionsOfPlayer1.contains(player1));
        assertTrue(connectionsOfPlayer1.contains(player2));
        assertTrue(connectionsOfPlayer1.contains(player3));
        assertTrue(connectionsOfPlayer1.contains(player4));

        List<Player> connectionsOfPlayer3 = this.network.getConnectionsOfPlayer(player3);
        assertTrue(connectionsOfPlayer3.contains(player1));
        assertFalse(connectionsOfPlayer3.contains(player2));
        assertFalse(connectionsOfPlayer3.contains(player3));
        assertTrue(connectionsOfPlayer3.contains(player4));
    }

    /**
     * Test of getting a random connection of a specific player.
     */
    @Test
    public void testGetRandomConnectionOfPlayer() {

        // connections are always bidirectional
        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

        Player randomConnectionOfPlayer1 = player1.getRandomConnection();
        assertTrue(randomConnectionOfPlayer1.equals(player3)
                || randomConnectionOfPlayer1.equals(player4));
        assertFalse(randomConnectionOfPlayer1.equals(player2));

        Player randomConnectionOfPlayer2 = player2.getRandomConnection();
        assertNull(randomConnectionOfPlayer2);
    }

    /**
     * Test of getting a random not yet connected player for a specific player.
     */
    @Test
    public void testGetRandomNotYetConnectedPlayerForPlayer() {

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

        Player randomNotYetConnectedPlayerForPlayer1 = player1.getRandomNotYetConnectedPlayer();
        assertNull(randomNotYetConnectedPlayerForPlayer1);

        Player randomNotYetConnectedPlayerForPlayer2 = player2.getRandomNotYetConnectedPlayer();
        assertTrue(randomNotYetConnectedPlayerForPlayer2.equals(player3) ||
                randomNotYetConnectedPlayerForPlayer2.equals(player4));
    }

}
