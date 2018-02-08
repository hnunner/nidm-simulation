package nl.uu.socnetid.network_games.networks.writer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.network.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.network.writer.AdjacencyMatrixWriter;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;
import nl.uu.socnetid.network_games.utilities.Cumulative;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

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
        sb.append(",P1,P2,P3,P4").append(System.getProperty("line.separator"));
        sb.append("P1,0,1,1,1").append(System.getProperty("line.separator"));
        sb.append("P2,1,0,0,0").append(System.getProperty("line.separator"));
        sb.append("P3,1,0,0,1").append(System.getProperty("line.separator"));
        sb.append("P4,1,0,1,0").append(System.getProperty("line.separator"));
        expectedMatrices.add(sb.toString());

        // for "Run as Maven test"
        sb = new StringBuilder();
        sb.append(",P31,P32,P33,P34").append(System.getProperty("line.separator"));
        sb.append("P31,0,1,1,1").append(System.getProperty("line.separator"));
        sb.append("P32,1,0,0,0").append(System.getProperty("line.separator"));
        sb.append("P33,1,0,0,1").append(System.getProperty("line.separator"));
        sb.append("P34,1,0,1,0").append(System.getProperty("line.separator"));
        expectedMatrices.add(sb.toString());
    }


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initPlayer() {
        UtilityFunction utilityFunction = new Cumulative();

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
