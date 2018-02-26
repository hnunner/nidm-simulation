package nl.uu.socnetid.networkgames.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link Actor} class.
 *
 * @author Hendrik Nunner
 */
public class ActorTest {

    // graph
    private Graph graph;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;

    // disease related
    private DiseaseSpecs ds;
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        // init graphstream
        this.graph = new SingleGraph("Actor Test");

        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        List<Actor> actors = new ArrayList<Actor>();

        this.actor1 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor2 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor3 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor4 = Actor.newInstance(uf, this.ds, this.graph);

        actors.add(this.actor1);
        actors.add(this.actor2);
        actors.add(this.actor3);
        actors.add(this.actor4);

        this.actor1.initCoActors(actors);
        this.actor2.initCoActors(actors);
        this.actor3.initCoActors(actors);
        this.actor4.initCoActors(actors);

        // connections are always bidirectional
        this.actor1.addConnection(this.actor2);
        this.actor2.addConnection(this.actor1);

        this.actor1.addConnection(this.actor3);
        this.actor3.addConnection(this.actor1);

        this.actor1.addConnection(this.actor4);
        this.actor4.addConnection(this.actor1);

        this.actor3.addConnection(this.actor4);
        this.actor4.addConnection(this.actor3);
    }


	/**
	 * Test of id coherence.
	 */
	@Test
	public void testIds() {
		assertNotEquals(this.actor1.getId(), this.actor2.getId());
		assertNotEquals(this.actor1.getId(), this.actor3.getId());
		assertNotEquals(this.actor1.getId(), this.actor4.getId());
		assertNotEquals(this.actor2.getId(), this.actor3.getId());
		assertNotEquals(this.actor2.getId(), this.actor4.getId());
		assertNotEquals(this.actor3.getId(), this.actor4.getId());
	}

	/**
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        assertEquals(3, this.actor1.getConnections().size());
        assertEquals(1, this.actor2.getConnections().size());
        assertEquals(2, this.actor3.getConnections().size());
        assertEquals(2, this.actor4.getConnections().size());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {
        // remove connections
        this.actor1.removeConnection(this.actor2);
        this.actor2.removeConnection(this.actor1);

        this.actor3.removeConnection(this.actor4);
        this.actor4.removeConnection(this.actor3);

        assertEquals(2, this.actor1.getConnections().size());
        assertEquals(0, this.actor2.getConnections().size());
        assertEquals(1, this.actor3.getConnections().size());
        assertEquals(1, this.actor4.getConnections().size());
    }



    /**
     * Test of getting a random connection of a specific actor.
     */
    @Test
    public void testGetRandomConnectionOfActor() {
        // remove connection between actors 1 and 2
        this.actor1.removeConnection(this.actor2);
        this.actor2.removeConnection(this.actor1);

        Actor randomConnectionOfActor1 = this.actor1.getRandomConnection();
        assertTrue(randomConnectionOfActor1.equals(this.actor3)
                || randomConnectionOfActor1.equals(this.actor4));
        assertFalse(randomConnectionOfActor1.equals(this.actor2));

        Actor randomConnectionOfActor2 = this.actor2.getRandomConnection();
        assertNull(randomConnectionOfActor2);
    }

    /**
     * Test of getting a random not yet connected actor for a specific actor.
     */
    @Test
    public void testGetRandomNotYetConnectedActorForActor() {
        Actor randomNotYetConnectedActorForActor1 = this.actor1.getRandomNotYetConnectedActor();
        assertNull(randomNotYetConnectedActorForActor1);

        Actor randomNotYetConnectedActorForActor2 = this.actor2.getRandomNotYetConnectedActor();
        assertTrue(randomNotYetConnectedActorForActor2.equals(this.actor3) ||
                randomNotYetConnectedActorForActor2.equals(this.actor4));
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
