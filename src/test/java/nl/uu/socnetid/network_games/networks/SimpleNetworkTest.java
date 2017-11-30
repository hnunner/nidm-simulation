package nl.uu.socnetid.network_games.networks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.network.networks.SimpleNetwork;
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

	    player1 = RationalPlayer.newInstance();
	    player2 = RationalPlayer.newInstance();
	    player3 = RationalPlayer.newInstance();
	    player4 = RationalPlayer.newInstance();

        players.add(player1);
        player1.setUtilityFunction(utilityFunction);
		players.add(player2);
        player2.setUtilityFunction(utilityFunction);
		players.add(player3);
        player3.setUtilityFunction(utilityFunction);
		players.add(player4);
        player4.setUtilityFunction(utilityFunction);

		player1.initCoPlayers(players);
		player2.initCoPlayers(players);
		player3.initCoPlayers(players);
		player4.initCoPlayers(players);

		this.network = new SimpleNetwork(players);

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);
	}

    /**
     * Test of getting all connections of a specific player.
     */
    @Test
    public void testGetConnectionsOfPlayer() {

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

}
