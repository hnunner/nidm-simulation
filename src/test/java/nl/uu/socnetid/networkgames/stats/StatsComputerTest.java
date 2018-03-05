package nl.uu.socnetid.networkgames.stats;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.util.Precision;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class StatsComputerTest {

    // graph
    private Network network;

    // constants
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;
    private Actor actor7;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        this.network = new Network("StatsComputer Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds);
        this.actor2 = this.network.addActor(uf, ds);
        this.actor3 = this.network.addActor(uf, ds);
        this.actor4 = this.network.addActor(uf, ds);
        this.actor5 = this.network.addActor(uf, ds);
        this.actor6 = this.network.addActor(uf, ds);
        this.actor7 = this.network.addActor(uf, ds);

        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor2.addConnection(this.actor4);
        this.actor5.addConnection(this.actor6);
    }


    /**
     * Test of closeness computation.
     */
    @Test
    public void testComputeCloseness() {
        assertEquals(0.5, Precision.round(StatsComputer.computeCloseness(this.actor1), 2), 0);
        assertEquals(0.47, Precision.round(StatsComputer.computeCloseness(this.actor4), 2), 0);
        assertEquals(0.17, Precision.round(StatsComputer.computeCloseness(this.actor5), 2), 0);
        assertEquals(0, Precision.round(StatsComputer.computeCloseness(this.actor7), 2), 0);
    }

}
