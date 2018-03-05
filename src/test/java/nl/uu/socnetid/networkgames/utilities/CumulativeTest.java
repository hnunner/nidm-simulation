package nl.uu.socnetid.networkgames.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.networks.Network;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CumulativeTest {

    // TODO create more complex network, so that double indirect benefits would be possible
    //      (e.g., take the network from IRTCTest.java)
    // TODO implement TestCase for truncated connections utility function

    // network
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;

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

        // connections
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor3.addConnection(this.actor4);
        this.actor4.addConnection(this.actor5);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(3.5, this.actor1.getUtility().getOverallUtility(), 0);
        assertEquals(2.0, this.actor2.getUtility().getOverallUtility(), 0);
        assertEquals(3.0, this.actor3.getUtility().getOverallUtility(), 0);
        assertEquals(3.5, this.actor4.getUtility().getOverallUtility(), 0);
        assertEquals(2.0, this.actor5.getUtility().getOverallUtility(), 0);
    }

}
