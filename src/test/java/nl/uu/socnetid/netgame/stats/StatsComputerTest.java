package nl.uu.socnetid.netgame.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.stats.GlobalActorStats;
import nl.uu.socnetid.netgame.stats.GlobalNetworkStats;
import nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats;
import nl.uu.socnetid.netgame.stats.StatsComputer;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class StatsComputerTest {

    // graph
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;
    private Actor actor7;

    // disease
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        this.network = new Network("StatsComputer Test");

        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        // risk neutral
        this.actor1 = this.network.addActor(uf, ds, 1.0);
        this.actor2 = this.network.addActor(uf, ds, 1.0);
        // risk seeking
        this.actor3 = this.network.addActor(uf, ds, 0.25);
        this.actor4 = this.network.addActor(uf, ds, 0.8);
        this.actor5 = this.network.addActor(uf, ds, 0.9);
        // risk averse
        this.actor6 = this.network.addActor(uf, ds, 1.5);
        this.actor7 = this.network.addActor(uf, ds, 2.0);

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
        assertEquals(0.5, StatsComputer.computeCloseness(this.actor1), 0.01);
        assertEquals(0.47, StatsComputer.computeCloseness(this.actor4), 0.01);
        assertEquals(0.17, StatsComputer.computeCloseness(this.actor5), 0.01);
        assertEquals(0, StatsComputer.computeCloseness(this.actor7), 0.01);
    }

    /**
     * Test of first order degree.
     */
    @Test
    public void testFirstOrderDegree() {
        assertEquals(3, StatsComputer.computeFirstOrderDegree(this.actor1));
        assertEquals(2, StatsComputer.computeFirstOrderDegree(this.actor2));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.actor3));
        assertEquals(2, StatsComputer.computeFirstOrderDegree(this.actor4));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.actor5));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.actor6));
    }

    /**
     * Test of second order degree.
     */
    @Test
    public void testSecondOrderDegree() {
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.actor1));
        assertEquals(1, StatsComputer.computeSecondOrderDegree(this.actor2));
        assertEquals(2, StatsComputer.computeSecondOrderDegree(this.actor3));
        assertEquals(1, StatsComputer.computeSecondOrderDegree(this.actor4));
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.actor5));
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.actor6));
    }

    /**
     * Test of global actor stats computation.
     */
    @Test
    public void testComputeGlobalActorStats() {
        // 1 susceptible (actor1)
        // 4 infected
        network.toggleInfection(this.actor2.getId(), this.ds);
        network.toggleInfection(this.actor3.getId(), this.ds);
        network.toggleInfection(this.actor4.getId(), this.ds);
        network.toggleInfection(this.actor5.getId(), this.ds);
        // 2 recovered
        network.toggleInfection(this.actor6.getId(), this.ds);
        network.toggleInfection(this.actor6.getId(), this.ds);
        network.toggleInfection(this.actor7.getId(), this.ds);
        network.toggleInfection(this.actor7.getId(), this.ds);

        GlobalActorStats gas = StatsComputer.computeGlobalActorStats(this.network);
        assertEquals(7, gas.getN());
        assertEquals(1, gas.getnS());
        assertEquals(4, gas.getnI());
        assertEquals(2, gas.getnR());
        assertEquals(2, gas.getnRiskNeutral());
        assertEquals(3, gas.getnRiskSeeking());
        assertEquals(2, gas.getnRiskAverse());
        assertEquals(1.064, gas.getAvRisk(), 0.001);
    }

    /**
     * Test of global network stats computation.
     */
    @Test
    public void testComputeGlobalNetworkStats() {
        GlobalNetworkStats gns = StatsComputer.computeGlobalNetworkStats(this.network);
        assertFalse(gns.isStable());
        assertEquals(10.0/this.network.getActors().size(), gns.getAvDegree(), 0.001);
        assertEquals(5, gns.getConnections());
        // not implemented:
        // gns.getDiameter();
        // gns.getAvDistance();
    }

    /**
     * Test of local actor connections stats computation.
     */
    @Test
    public void testComputeLocalActorConnectionsStats() {
        network.toggleInfection(this.actor3.getId(), this.ds);
        network.toggleInfection(this.actor4.getId(), this.ds);
        network.toggleInfection(this.actor4.getId(), this.ds);

        LocalActorConnectionsStats lacs = StatsComputer.computeLocalActorConnectionsStats(this.actor1);
        assertEquals(3, lacs.getN());
        assertEquals(0, lacs.getM());
        assertEquals(1, lacs.getnS());
        assertEquals(1, lacs.getnI());
        assertEquals(1, lacs.getnR());

        lacs = StatsComputer.computeLocalActorConnectionsStats(this.actor3);
        assertEquals(1, lacs.getN());
        assertEquals(2, lacs.getM());
        assertEquals(1, lacs.getnS());
        assertEquals(0, lacs.getnI());
        assertEquals(0, lacs.getnR());
    }

    /**
     * Test of local actor connections stats computation.
     */
    @Test
    public void testComputeProbabilityOfInfection() {
        // 2 infected with a transmission rate of 0.1 each
        network.toggleInfection(this.actor2.getId(), this.ds);
        network.toggleInfection(this.actor3.getId(), this.ds);

        int infectedConnections = StatsComputer.computeLocalActorConnectionsStats(this.actor1).getnI();
        assertEquals(0.19, StatsComputer.computeProbabilityOfInfection(actor1, infectedConnections), 0.001);
    }

}
