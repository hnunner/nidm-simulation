package nl.uu.socnetid.network_games.networks.writer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;
import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriterTest {

    //players
    Player player1;
    Player player2;
    Player player3;
    Player player4;

    // basic network
    private Network network;

    // expected adjacency matrix
    private static List<String> expectedMatrices = new LinkedList<String>();


    @BeforeClass
    public static void initSuite() {
        // for "Run as Junit Test"
        StringBuilder sb = new StringBuilder();
        sb.append(",1,2,3,4").append(System.getProperty("line.separator"));
        sb.append("1,,x,x,x").append(System.getProperty("line.separator"));
        sb.append("2,x,,,").append(System.getProperty("line.separator"));
        sb.append("3,x,,,x").append(System.getProperty("line.separator"));
        sb.append("4,x,,x,").append(System.getProperty("line.separator"));
        expectedMatrices.add(sb.toString());

        // for "Run as Maven test"
        sb = new StringBuilder();
        sb.append(",21,22,23,24").append(System.getProperty("line.separator"));
        sb.append("21,,x,x,x").append(System.getProperty("line.separator"));
        sb.append("22,x,,,").append(System.getProperty("line.separator"));
        sb.append("23,x,,,x").append(System.getProperty("line.separator"));
        sb.append("24,x,,x,").append(System.getProperty("line.separator"));
        expectedMatrices.add(sb.toString());
    }


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
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        NetworkWriter writer = new AdjacencyMatrixWriter();
        assertTrue(expectedMatrices.contains(writer.write(network)));
    }

}
