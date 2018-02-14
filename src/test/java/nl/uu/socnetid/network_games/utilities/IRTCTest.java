package nl.uu.socnetid.network_games.utilities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
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
public class IRTCTest {

    // constants
    private static final double alpha = 5.3;
    private static final double beta  = 1.2;
    private static final double c     = 4.1;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private static final double r     = 1.2;
    private static final int    tau   = 10;

    // players
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;

    // utility function
    UtilityFunction uf;

    // disease specs
    DiseaseSpecs ds;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initPlayer() {
        uf = new IRTC(alpha, beta, c);
        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);


        List<Player> players = new ArrayList<Player>();
        player1 = RationalPlayer.newInstance(uf, ds, r);
        player2 = RationalPlayer.newInstance(uf, ds, r);
        player3 = RationalPlayer.newInstance(uf, ds, r);
        player4 = RationalPlayer.newInstance(uf, ds, r);
        player5 = RationalPlayer.newInstance(uf, ds, r);

        // infections
        player3.infect(ds);
        player5.infect(ds);

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
        assertEquals(-2.6, Precision.round(player1.getUtility().getOverallUtility(), 1), 0);
        assertEquals( 3.6, Precision.round(player2.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-3.6, Precision.round(player3.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-9.9, Precision.round(player4.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-4.8, Precision.round(player5.getUtility().getOverallUtility(), 1), 0);
    }

}
