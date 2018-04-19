package nl.uu.socnetid.netgame.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CumulativeTest {

    // network
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;
    private Actor actor7;
    private Actor actor8;
    private Actor actor9;

    // disease related
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        this.network = new Network("Cumulative Test");
        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds);
        this.actor2 = this.network.addActor(uf, ds);
        this.actor3 = this.network.addActor(uf, ds);
        this.actor4 = this.network.addActor(uf, ds);
        this.actor5 = this.network.addActor(uf, ds);
        this.actor6 = this.network.addActor(uf, ds);
        this.actor7 = this.network.addActor(uf, ds);
        this.actor8 = this.network.addActor(uf, ds);
        this.actor9 = this.network.addActor(uf, ds);

        // connections
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor2.addConnection(this.actor6);
        this.actor3.addConnection(this.actor5);
        this.actor4.addConnection(this.actor5);
        this.actor4.addConnection(this.actor6);
        this.actor4.addConnection(this.actor7);
        this.actor5.addConnection(this.actor7);
        this.actor5.addConnection(this.actor8);
        this.actor6.addConnection(this.actor7);
        this.actor7.addConnection(this.actor8);
        this.actor8.addConnection(this.actor9);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(4.5, this.actor1.getUtility().getOverallUtility(), 0);
        assertEquals(3.5, this.actor2.getUtility().getOverallUtility(), 0);
        assertEquals(4, this.actor3.getUtility().getOverallUtility(), 0);
        assertEquals(5.5, this.actor4.getUtility().getOverallUtility(), 0);
        assertEquals(5.5, this.actor5.getUtility().getOverallUtility(), 0);
        assertEquals(4.5, this.actor6.getUtility().getOverallUtility(), 0);
        assertEquals(6, this.actor7.getUtility().getOverallUtility(), 0);
        assertEquals(4.5, this.actor8.getUtility().getOverallUtility(), 0);
        assertEquals(2, this.actor9.getUtility().getOverallUtility(), 0);
    }

}
