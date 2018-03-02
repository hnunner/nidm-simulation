package nl.uu.socnetid.networkgames.actors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.networks.Network;
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


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {

        this.network = new Network("Actor Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds);
        this.actor2 = this.network.addActor(uf, ds);
        this.actor3 = this.network.addActor(uf, ds);
        this.actor4 = this.network.addActor(uf, ds);
        this.actor5 = this.network.addActor(uf, ds);
        this.actor6 = this.network.addActor(uf, ds);

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

     * Test of getting a random connection of a specific actor.
     */
    @Test
    public void testGetRandomConnectionOfActor() {
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
    public void throwsException() {
        DiseaseSpecs dsInvalid = new DiseaseSpecs(DiseaseType.SIR, tau+1, delta, gamma, mu);
        this.actor1.infect(dsInvalid);
    }

}
