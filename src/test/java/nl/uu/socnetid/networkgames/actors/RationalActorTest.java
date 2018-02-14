package nl.uu.socnetid.networkgames.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link RationalActor} class.
 *
 * @author Hendrik Nunner
 */
public class RationalActorTest {

    // actors
    Actor actor1;
    Actor actor2;
    Actor actor3;
    Actor actor4;

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
    public void initActor() {
        UtilityFunction uf = new Cumulative();

        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        List<Actor> actors = new ArrayList<Actor>();

        actor1 = RationalActor.newInstance(uf, ds);
        actor2 = RationalActor.newInstance(uf, ds);
        actor3 = RationalActor.newInstance(uf, ds);
        actor4 = RationalActor.newInstance(uf, ds);

        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);
        actors.add(actor4);

        actor1.initCoActors(actors);
        actor2.initCoActors(actors);
        actor3.initCoActors(actors);
        actor4.initCoActors(actors);

        // connections are always bidirectional
        actor1.addConnection(actor2);
        actor2.addConnection(actor1);

        actor1.addConnection(actor3);
        actor3.addConnection(actor1);

        actor1.addConnection(actor4);
        actor4.addConnection(actor1);

        actor3.addConnection(actor4);
        actor4.addConnection(actor3);
    }


	/**
	 * Test of id coherence.
	 */
	@Test
	public void testIds() {
		assertEquals(5, actor1.getId());
        assertEquals(6, actor2.getId());
	}

	/**
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        assertEquals(3, actor1.getConnections().size());
        assertEquals(1, actor2.getConnections().size());
        assertEquals(2, actor3.getConnections().size());
        assertEquals(2, actor4.getConnections().size());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {
        // remove connections
        actor1.removeConnection(actor2);
        actor2.removeConnection(actor1);

        actor3.removeConnection(actor4);
        actor4.removeConnection(actor3);

        assertEquals(2, actor1.getConnections().size());
        assertEquals(0, actor2.getConnections().size());
        assertEquals(1, actor3.getConnections().size());
        assertEquals(1, actor4.getConnections().size());
    }



    /**
     * Test of getting a random connection of a specific actor.
     */
    @Test
    public void testGetRandomConnectionOfActor() {
        // remove connection between actors 1 and 2
        actor1.removeConnection(actor2);
        actor2.removeConnection(actor1);

        Actor randomConnectionOfActor1 = actor1.getRandomConnection();
        assertTrue(randomConnectionOfActor1.equals(actor3)
                || randomConnectionOfActor1.equals(actor4));
        assertFalse(randomConnectionOfActor1.equals(actor2));

        Actor randomConnectionOfActor2 = actor2.getRandomConnection();
        assertNull(randomConnectionOfActor2);
    }

    /**
     * Test of getting a random not yet connected actor for a specific actor.
     */
    @Test
    public void testGetRandomNotYetConnectedActorForActor() {
        Actor randomNotYetConnectedActorForActor1 = actor1.getRandomNotYetConnectedActor();
        assertNull(randomNotYetConnectedActorForActor1);

        Actor randomNotYetConnectedActorForActor2 = actor2.getRandomNotYetConnectedActor();
        assertTrue(randomNotYetConnectedActorForActor2.equals(actor3) ||
                randomNotYetConnectedActorForActor2.equals(actor4));
    }

    /**
     * Test whether a actor is being successfully infected with a valid disease.
     */
    @Test
    public void testValidInfect() {
        assertTrue(actor1.isSusceptible());
        DiseaseSpecs dsValid = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);
        actor1.infect(dsValid);
        assertTrue(actor1.isInfected());
    }

    /**
     * Test whether an exception is thrown when a actor is being infected with an invalid disease.
     */
    @Test(expected = RuntimeException.class)
    public void throwsException() {
        DiseaseSpecs dsInvalid = new DiseaseSpecs(DiseaseType.SIR, tau+1, delta, gamma, mu);
        actor1.infect(dsInvalid);
    }

}
