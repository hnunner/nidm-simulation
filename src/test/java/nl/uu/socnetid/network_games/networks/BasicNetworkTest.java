package nl.uu.socnetid.network_games.networks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.SimplePlayer;


/**
 * Test cases for the {@link BasicNetwork} class.
 *
 * @author Hendrik Nunner
 */
public class BasicNetworkTest {

    /** players */
    Player player1;
    Player player2;
    Player player3;
    Player player4;

	/** basic network */
	private Network network;


	/**
	 * Performed before each test: Initialization of the network.
	 */
	@Before
	public void initPlayer() {
	    List<Player> players = new LinkedList<Player>();

	    player1 = SimplePlayer.newInstance();
	    player2 = SimplePlayer.newInstance();
	    player3 = SimplePlayer.newInstance();
	    player4 = SimplePlayer.newInstance();

        players.add(player1);
		players.add(player2);
		players.add(player3);
		players.add(player4);

		this.network = new BasicNetwork(players);
	}

	/**
	 * Test of adding a connection.
	 */
	@Test
	public void testAddConnection() {

	    this.network.addConnection(player1, player2);
	    this.network.addConnection(player1, player3);
	    this.network.addConnection(player1, player4);
	    this.network.addConnection(player3, player4);

	    // this shouldn't make a difference
	    this.network.addConnection(player2, player1);

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

        this.network.addConnection(player1, player2);
        this.network.addConnection(player1, player3);
        this.network.addConnection(player1, player4);
        this.network.addConnection(player3, player4);

        // this shouldn't make a difference
        this.network.addConnection(player2, player1);

        this.network.removeConnection(player1, player2);
        this.network.removeConnection(player4, player3);

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

        this.network.addConnection(player1, player2);
        this.network.addConnection(player1, player3);
        this.network.addConnection(player1, player4);
        this.network.addConnection(player3, player4);

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

        this.network.addConnection(player1, player3);
        this.network.addConnection(player1, player4);
        this.network.addConnection(player3, player4);

        Player randomConnectionOfPlayer1 = this.network.getRandomConnectionOfPlayer(player1);
        assertTrue(randomConnectionOfPlayer1.equals(player3)
                || randomConnectionOfPlayer1.equals(player4));
        assertFalse(randomConnectionOfPlayer1.equals(player2));

        Player randomConnectionOfPlayer2 = this.network.getRandomConnectionOfPlayer(player2);
        assertNull(randomConnectionOfPlayer2);
    }

    /**
     * Test of getting a random not yet connected player for a specific player.
     */
    @Test
    public void testGetRandomNotYetConnectedPlayerForPlayer() {

        this.network.addConnection(player1, player2);
        this.network.addConnection(player1, player3);
        this.network.addConnection(player1, player4);
        this.network.addConnection(player3, player4);

        Player randomNotYetConnectedPlayerForPlayer1 = this.network.
                getRandomNotYetConnectedPlayerForPlayer(player1);
        assertNull(randomNotYetConnectedPlayerForPlayer1);

        Player randomNotYetConnectedPlayerForPlayer2 = this.network.
                getRandomNotYetConnectedPlayerForPlayer(player2);
        assertTrue(randomNotYetConnectedPlayerForPlayer2.equals(player3) ||
                randomNotYetConnectedPlayerForPlayer2.equals(player4));
    }

}
