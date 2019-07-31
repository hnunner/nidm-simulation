package nl.uu.socnetid.netgame.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseGroup;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;


/**
 * Test cases for the {@link Actor} class.
 *
 * @author Hendrik Nunner
 */
public class ActorTest {

    // network
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private UtilityFunction uf;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {

        this.network = new Network("Actor Test");

        this.uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.actor1 = this.network.addActor(uf, this.ds);
        this.actor2 = this.network.addActor(uf, this.ds);
        this.actor3 = this.network.addActor(uf, this.ds);
        this.actor4 = this.network.addActor(uf, this.ds);
        this.actor5 = this.network.addActor(uf, this.ds);
        this.actor6 = this.network.addActor(uf, this.ds);

        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor3.addConnection(this.actor4);
    }


	/**
	 * Test of id coherence.
	 */
	@Test
	public void testIds() {
		assertNotEquals(this.actor1.getId(), this.actor2.getId());
		assertNotEquals(this.actor1.getId(), this.actor3.getId());
		assertNotEquals(this.actor1.getId(), this.actor4.getId());
        assertNotEquals(this.actor1.getId(), this.actor5.getId());
        assertNotEquals(this.actor1.getId(), this.actor6.getId());

        assertNotEquals(this.actor2.getId(), this.actor3.getId());
        assertNotEquals(this.actor2.getId(), this.actor4.getId());
        assertNotEquals(this.actor2.getId(), this.actor5.getId());
        assertNotEquals(this.actor2.getId(), this.actor6.getId());

		assertNotEquals(this.actor3.getId(), this.actor4.getId());
        assertNotEquals(this.actor3.getId(), this.actor5.getId());
        assertNotEquals(this.actor3.getId(), this.actor6.getId());

        assertNotEquals(this.actor4.getId(), this.actor5.getId());
        assertNotEquals(this.actor4.getId(), this.actor6.getId());
        assertNotEquals(this.actor5.getId(), this.actor6.getId());
	}

    /**
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        assertEquals(3, this.actor1.getConnections().size());
        this.actor1.addConnection(actor5);
        assertEquals(4, this.actor1.getConnections().size());
        assertEquals(0, this.actor6.getConnections().size());
        this.actor6.addConnection(actor1);
        assertEquals(5, this.actor1.getConnections().size());
        assertEquals(1, this.actor6.getConnections().size());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {
        assertEquals(3, this.actor1.getConnections().size());
        assertEquals(1, this.actor2.getConnections().size());
        this.actor1.removeConnection(this.actor2);
        assertEquals(2, this.actor1.getConnections().size());
        assertEquals(0, this.actor2.getConnections().size());
    }

	/**
     * Test of accepting a connection to another actor.
     */
    @Test
    public void testAcceptConnection() {
        // NOTE: test whether actors are capable of accepting connection requests,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        assertTrue(this.actor2.acceptConnection(this.actor4));
        assertTrue(this.actor6.acceptConnection(this.actor5));
    }

    /**
     * Test of getting the co-actors.
     */
    @Test
    public void testGetCoActors() {
        assertEquals(5, this.actor1.getCoActors().size());
        assertEquals(5, this.actor2.getCoActors().size());
        assertEquals(5, this.actor3.getCoActors().size());
        assertEquals(5, this.actor4.getCoActors().size());
        assertEquals(5, this.actor5.getCoActors().size());
        assertEquals(5, this.actor6.getCoActors().size());
    }

    /**
     * Test of getting the connections.
     */
    @Test
    public void testGetConnections() {
        assertEquals(3, this.actor1.getConnections().size());
        assertEquals(1, this.actor2.getConnections().size());
        assertEquals(this.actor1, this.actor2.getConnections().iterator().next());
        assertEquals(2, this.actor3.getConnections().size());
        assertEquals(2, this.actor4.getConnections().size());
        assertEquals(0, this.actor5.getConnections().size());
        assertEquals(0, this.actor6.getConnections().size());
    }

    /**
     * Test of getting the disease group.
     */
    @Test
    public void testGetDiseaseGroup() {
        assertEquals(DiseaseGroup.SUSCEPTIBLE, this.actor1.getDiseaseGroup());
        this.actor1.infect(this.ds);
        assertEquals(DiseaseGroup.INFECTED, this.actor1.getDiseaseGroup());
        this.actor1.cure();
        assertEquals(DiseaseGroup.RECOVERED, this.actor1.getDiseaseGroup());
        this.actor1.makeSusceptible();
        assertEquals(DiseaseGroup.SUSCEPTIBLE, this.actor1.getDiseaseGroup());
    }

    /**
     * Test of getting the disease specs.
     */
    @Test
    public void testGetDiseaseSpecs() {
        assertNotNull(this.actor1.getDiseaseSpecs());
        assertEquals(s, this.actor1.getDiseaseSpecs().getS(), 0.0);
        assertNotNull(this.actor2.getDiseaseSpecs());
        assertEquals(mu, this.actor2.getDiseaseSpecs().getMu(), 0.0);
        assertNotNull(this.actor5.getDiseaseSpecs());
        assertEquals(tau, this.actor5.getDiseaseSpecs().getTau(), 0);
        assertNotNull(this.actor6.getDiseaseSpecs());
        assertEquals(gamma, this.actor6.getDiseaseSpecs().getGamma(), 0.0);
    }

    /**
     * Test of getting the utility.
     */
    @Test
    public void testGetUtility() {
        // NOTE: test whether actors are capable of computing utilites,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        assertNotNull(this.actor1.getUtility());
        assertEquals(3, this.actor1.getUtility().getOverallUtility(), 0.0);
        assertEquals(2, this.actor2.getUtility().getOverallUtility(), 0.0);
        assertEquals(2.5, this.actor4.getUtility().getOverallUtility(), 0.0);
        assertEquals(0, this.actor5.getUtility().getOverallUtility(), 0.0);
    }

    /**
     * Test whether a actor is being successfully infected with a valid disease.
     */
    @Test
    public void testValidInfect() {
        assertTrue(this.actor1.isSusceptible());
        DiseaseSpecs dsValid = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);
        this.actor1.infect(dsValid);
        assertTrue(this.actor1.isInfected());
    }

    /**
     * Test whether an exception is thrown when a actor is being infected with an invalid disease.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidInfect() {
        DiseaseSpecs dsInvalid = new DiseaseSpecs(DiseaseType.SIR, tau+1, s, gamma, mu);
        this.actor1.infect(dsInvalid);
    }

    /**
     * Test whether an returns the correct number of network decisions.
     */
    @Test
    @Ignore
    public void testOldGetNumberOfNetworkDecisions() {
        Network network = new Network("Network Network Decisions Test");

        // f(5) = 2.28
        Actor actor = network.addActor(uf, ds);
        for (int i = 0; i < 4; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(5, network.getN());
        assertEquals(2, actor.getNumberOfNetworkDecisions());

        // f(10) = 3.58
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(10, network.getN());
        assertEquals(4, actor.getNumberOfNetworkDecisions());

        // f(15) = 4.62
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(15, network.getN());
        assertEquals(5, actor.getNumberOfNetworkDecisions());

        // f(20) = 5.49
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(20, network.getN());
        assertEquals(5, actor.getNumberOfNetworkDecisions());

        // f(25) = 6.25
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(25, network.getN());
        assertEquals(6, actor.getNumberOfNetworkDecisions());

        // f(50) = 9.33
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(50, network.getN());
        assertEquals(9, actor.getNumberOfNetworkDecisions());

        // f(75) = 11.89
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(75, network.getN());
        assertEquals(12, actor.getNumberOfNetworkDecisions());

        // f(100) = 14.13
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(100, network.getN());
        assertEquals(14, actor.getNumberOfNetworkDecisions());
    }

    /**
     * Test whether an returns the correct number of network decisions.
     */
    @Test
    public void testGetNumberOfNetworkDecisions() {
        Network network = new Network("Network Network Decisions Test");

        double phi = 0.4;

        // f(5) = 1
        Actor actor = network.addActor(uf, ds);
        for (int i = 0; i < 4; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(5, network.getN());
        double evalShare = (network.getN()-1)*phi;
        int exp = (int) Math.round(evalShare);
        assertEquals(exp, actor.getNumberOfNetworkDecisions());

        // f(10) = 2
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(10, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(15) = 3
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(15, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(20) = 4
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(20, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(25) = 5
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(25, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(50) = 10
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(50, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(75) = 15
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(75, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

        // f(100) = 20
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(100, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), actor.getNumberOfNetworkDecisions());

    }

    /**
     * Test whether a new connection is considered valuable.
     */
    @Test
    public void testNewConnectionValuable() {
        assertTrue(this.actor1.newConnectionValuable(this.actor6));

        Network networkCostly = new Network("Costly Connections Test");
        UtilityFunction ufCostly = new Cumulative(-1.0, 2.0);
        Actor actorCostly1 = networkCostly.addActor(ufCostly, this.ds);
        Actor actorCostly2 = networkCostly.addActor(ufCostly, this.ds);
        assertFalse(actorCostly1.newConnectionValuable(actorCostly2));
    }

    /**
     * Test whether an existing connection is considered too costly.
     */
    @Test
    public void testExistingConnectionTooCostly() {
        assertFalse(this.actor1.existingConnectionTooCostly(this.actor2));

        Network networkCostly = new Network("Costly Connections Test");
        UtilityFunction ufCostly = new Cumulative(-1.0, 2.0);
        Actor actorCostly1 = networkCostly.addActor(ufCostly, this.ds);
        Actor actorCostly2 = networkCostly.addActor(ufCostly, this.ds);
        actorCostly1.addConnection(actorCostly2);
        assertTrue(actorCostly1.existingConnectionTooCostly(actorCostly2));
    }

    /**
     * Test whether the correct amount of random co-actors is retrieved.
     */
    @Test
    public void testGetRandomCoActors() {
        for (int i = 1; i < this.network.getActors().size(); i++) {
            Collection<Actor> randomListOfCoActors = this.actor1.getRandomListOfCoActors(i);
            assertEquals(i, randomListOfCoActors.size());
        }
    }

    /**
     * Test whether connections exist between actors.
     */
    @Test
    public void testHasDirectConnectionTo() {
        assertTrue(this.actor1.hasDirectConnectionTo(this.actor2));
        assertTrue(this.actor1.hasDirectConnectionTo(this.actor3));
        assertTrue(this.actor1.hasDirectConnectionTo(this.actor4));
        assertFalse(this.actor1.hasDirectConnectionTo(this.actor5));
        assertFalse(this.actor1.hasDirectConnectionTo(this.actor6));

        assertTrue(this.actor2.hasDirectConnectionTo(this.actor1));
        assertFalse(this.actor2.hasDirectConnectionTo(this.actor3));
        assertFalse(this.actor2.hasDirectConnectionTo(this.actor4));
        assertFalse(this.actor2.hasDirectConnectionTo(this.actor5));
        assertFalse(this.actor2.hasDirectConnectionTo(this.actor6));

        assertTrue(this.actor3.hasDirectConnectionTo(this.actor1));
        assertFalse(this.actor3.hasDirectConnectionTo(this.actor2));
        assertTrue(this.actor3.hasDirectConnectionTo(this.actor4));
        assertFalse(this.actor3.hasDirectConnectionTo(this.actor5));
        assertFalse(this.actor3.hasDirectConnectionTo(this.actor6));

        assertTrue(this.actor4.hasDirectConnectionTo(this.actor1));
        assertFalse(this.actor4.hasDirectConnectionTo(this.actor2));
        assertTrue(this.actor4.hasDirectConnectionTo(this.actor3));
        assertFalse(this.actor4.hasDirectConnectionTo(this.actor5));
        assertFalse(this.actor4.hasDirectConnectionTo(this.actor6));

        assertFalse(this.actor5.hasDirectConnectionTo(this.actor1));
        assertFalse(this.actor5.hasDirectConnectionTo(this.actor2));
        assertFalse(this.actor5.hasDirectConnectionTo(this.actor3));
        assertFalse(this.actor5.hasDirectConnectionTo(this.actor4));
        assertFalse(this.actor5.hasDirectConnectionTo(this.actor6));

        assertFalse(this.actor6.hasDirectConnectionTo(this.actor1));
        assertFalse(this.actor6.hasDirectConnectionTo(this.actor2));
        assertFalse(this.actor6.hasDirectConnectionTo(this.actor3));
        assertFalse(this.actor6.hasDirectConnectionTo(this.actor4));
        assertFalse(this.actor6.hasDirectConnectionTo(this.actor5));
    }

    /**
     * Test whether connections exist between actors.
     */
    @Test
    public void testHasConnectionTo() {
        assertTrue(this.actor2.hasConnectionTo(this.actor1));
        assertTrue(this.actor2.hasConnectionTo(this.actor3));
        assertTrue(this.actor2.hasConnectionTo(this.actor4));
        assertFalse(this.actor2.hasConnectionTo(this.actor5));
        assertFalse(this.actor2.hasConnectionTo(this.actor6));
    }

    /**
     * Test whether closeness is computed correctly.
     */
    @Test
    public void testGetCloseness() {
        assertEquals(0.6, this.actor1.getCloseness(), 0.01);
        assertEquals(0.52, this.actor2.getCloseness(), 0.01);
        assertEquals(0.56, this.actor3.getCloseness(), 0.01);
        assertEquals(0.56, this.actor4.getCloseness(), 0.01);
        assertEquals(0, this.actor5.getCloseness(), 0.01);
        assertEquals(0, this.actor6.getCloseness(), 0.01);
    }

}
