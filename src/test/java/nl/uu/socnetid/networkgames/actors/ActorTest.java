package nl.uu.socnetid.networkgames.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseGroup;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


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
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {

        this.network = new Network("Actor Test");

        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

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
        assertEquals(delta, this.actor1.getDiseaseSpecs().getDelta(), 0.0);
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
     * Test of seeking costly connections.
     */
    @Test
    public void testSeekCostlyConnections() {
        // NOTE: test whether actors are capable of (not) finding costly connections,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        assertNull(this.actor1.seekCostlyConnection());
        assertNull(this.actor2.seekCostlyConnection());
        assertNull(this.actor3.seekCostlyConnection());
        assertNull(this.actor4.seekCostlyConnection());
        assertNull(this.actor5.seekCostlyConnection());
        assertNull(this.actor6.seekCostlyConnection());
    }

    /**
     * Test of seeking new connections.
     */
    @Test
    public void testSeekNewConnections() {
        // NOTE: test whether actors are capable of (not) finding costly connections,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        Actor newConnectionActor1 = this.actor1.seekNewConnection();
        assertNotNull(newConnectionActor1);
        assertTrue(newConnectionActor1.equals(this.actor5)
                || newConnectionActor1.equals(this.actor6)
                && !newConnectionActor1.equals(this.actor2)
                && !newConnectionActor1.equals(this.actor3)
                && !newConnectionActor1.equals(this.actor4));
        assertNotNull(this.actor2.seekNewConnection());
        assertNotNull(this.actor3.seekNewConnection());
        assertNotNull(this.actor4.seekNewConnection());
        assertNotNull(this.actor5.seekNewConnection());
        Actor newConnectionActor6 = this.actor6.seekNewConnection();
        assertNotNull(newConnectionActor6);
        assertTrue(newConnectionActor6.equals(this.actor1)
                || newConnectionActor6.equals(this.actor2)
                || newConnectionActor6.equals(this.actor3)
                || newConnectionActor6.equals(this.actor4)
                || newConnectionActor6.equals(this.actor5));
    }

    /**
     * Test of getting a random connection of a specific actor.
     */
    @Test
    public void testGetRandomConnection() {
        Actor randomConnectionOfActor1 = this.actor1.getRandomConnection();
        assertTrue(randomConnectionOfActor1.equals(this.actor2)
                || randomConnectionOfActor1.equals(this.actor3)
                || randomConnectionOfActor1.equals(this.actor4));
        assertTrue(!randomConnectionOfActor1.equals(this.actor5)
                && !randomConnectionOfActor1.equals(this.actor6));

        Actor randomConnectionOfActor2 = this.actor2.getRandomConnection();
        assertTrue(randomConnectionOfActor2.equals(this.actor1));
        assertTrue(!randomConnectionOfActor2.equals(this.actor3)
                && !randomConnectionOfActor2.equals(this.actor4)
                && !randomConnectionOfActor2.equals(this.actor5)
                && !randomConnectionOfActor2.equals(this.actor6));


        Actor randomConnectionOfActor5 = this.actor5.getRandomConnection();
        assertNull(randomConnectionOfActor5);

        Actor randomConnectionOfActor6 = this.actor6.getRandomConnection();
        assertNull(randomConnectionOfActor6);
    }

    /**
     * Test of getting a random not yet connected actor for a specific actor.
     */
    @Test
    public void testGetRandomNotYetConnectedActorForActor() {
        Actor randomNotYetConnectedActorForActor1 = this.actor1.getRandomNotYetConnectedActor();
        assertTrue(randomNotYetConnectedActorForActor1.equals(this.actor5)
                || randomNotYetConnectedActorForActor1.equals(this.actor6));
        assertTrue(!randomNotYetConnectedActorForActor1.equals(this.actor2)
                && !randomNotYetConnectedActorForActor1.equals(this.actor3)
                && !randomNotYetConnectedActorForActor1.equals(this.actor4));



        Actor randomNotYetConnectedActorForActor6 = this.actor6.getRandomNotYetConnectedActor();
        assertTrue(randomNotYetConnectedActorForActor6.equals(this.actor1)
                || randomNotYetConnectedActorForActor6.equals(this.actor2)
                || randomNotYetConnectedActorForActor6.equals(this.actor3)
                || randomNotYetConnectedActorForActor6.equals(this.actor4)
                || randomNotYetConnectedActorForActor6.equals(this.actor5));
    }

    /**
     * Test whether a actor is being successfully infected with a valid disease.
     */
    @Test
    public void testValidInfect() {
        assertTrue(this.actor1.isSusceptible());
        DiseaseSpecs dsValid = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);
        this.actor1.infect(dsValid);
        assertTrue(this.actor1.isInfected());
    }

    /**
     * Test whether an exception is thrown when a actor is being infected with an invalid disease.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidInfect() {
        DiseaseSpecs dsInvalid = new DiseaseSpecs(DiseaseType.SIR, tau+1, delta, gamma, mu);
        this.actor1.infect(dsInvalid);
    }

}
