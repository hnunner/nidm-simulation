package nl.uu.socnetid.network_games.utilities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
import nl.uu.socnetid.network_games.disease.types.DiseaseType;
import nl.uu.socnetid.network_games.players.Player;
import nl.uu.socnetid.network_games.players.RationalPlayer;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CumulativeTest {

    // players
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;

    // utility function
    UtilityFunction uf;

    // disease related
    DiseaseSpecs ds;
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initPlayer() {
        uf = new Cumulative();

        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        List<Player> players = new ArrayList<Player>();

        player1 = RationalPlayer.newInstance(uf, ds);
        player2 = RationalPlayer.newInstance(uf, ds);
        player3 = RationalPlayer.newInstance(uf, ds);
        player4 = RationalPlayer.newInstance(uf, ds);
        player5 = RationalPlayer.newInstance(uf, ds);

        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        players.add(player5);

        player1.initCoPlayers(players);
        player2.initCoPlayers(players);
        player3.initCoPlayers(players);
        player4.initCoPlayers(players);
        player5.initCoPlayers(players);

        // connections are always bidirectional
        player1.addConnection(player2);
        player2.addConnection(player1);

        player1.addConnection(player3);
        player3.addConnection(player1);

        player1.addConnection(player4);
        player4.addConnection(player1);

        player3.addConnection(player4);
        player4.addConnection(player3);

        player4.addConnection(player5);
        player5.addConnection(player4);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(3.5, player1.getUtility(), 0);
        assertEquals(2.0, player2.getUtility(), 0);
        assertEquals(3.0, player3.getUtility(), 0);
        assertEquals(3.5, player4.getUtility(), 0);
        assertEquals(2.0, player5.getUtility(), 0);
    }

}
