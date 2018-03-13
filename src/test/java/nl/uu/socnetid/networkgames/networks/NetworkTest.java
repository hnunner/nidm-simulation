package nl.uu.socnetid.networkgames.networks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link Network} class.
 *
 * @author Hendrik Nunner
 */
public class NetworkTest {

    // network
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;

    // utility
    private UtilityFunction uf;

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
        this.network = new Network("Network Test");

        this.uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

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

        this.actor6.infect(ds);
	}


    /**
     * Test of adding an actor.
     */
    @Test
    public void testAddActor() {
        assertEquals(6, this.network.getActors().size());
        this.network.addActor(this.uf, this.ds);
        assertEquals(7, this.network.getActors().size());
    }

    /**
     * Test of removing an actor.
     */
    @Test
    public void testRemoveActor() {
        assertEquals(6, this.network.getActors().size());
        this.network.removeActor();
        assertEquals(5, this.network.getActors().size());
    }

    /**
     * Test of removing all connections.
     */
    @Test
    public void testClearConnections() {
        assertEquals(4, this.network.getEdgeCount());
        this.network.clearConnections();
        assertEquals(0, this.network.getEdgeCount());
    }

    /**
     * Test of creating a full network with connection between all actors.
     */
    @Test
    public void testCreateFullNetwork() {
        assertEquals(4, this.network.getEdgeCount());
        this.network.createFullNetwork();
        assertEquals(15, this.network.getEdgeCount());
    }

    /**
     * Test of getting all actors.
     */
    @Test
    public void testGetActors() {
        assertEquals(6, this.network.getActors().size());
    }

    /**
     * Test of infecting a random actor.
     */
    @Test
    public void testInfectRandomActor() {
        Iterator<Actor> actorIt = this.network.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            assertTrue(!actor.isInfected() || actor.equals(this.actor6));
        }
        this.network.infectRandomActor(this.ds);

        actorIt = this.network.getActorIterator();
        int infected = 0;
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (actor.isInfected()) {
                infected++;
            }
        }
        assertEquals(2, infected);
    }

    /**
     * Test of resetting all actors.
     */
    @Test
    public void testResetActors() {
        assertEquals(4, this.network.getEdgeCount());
        assertTrue(this.actor1.isSusceptible());
        assertTrue(this.actor6.isInfected());
        this.network.resetActors();
        assertEquals(0, this.network.getEdgeCount());
        assertTrue(this.actor1.isSusceptible());
        assertTrue(this.actor6.isSusceptible());
    }

    /**
     * Test of toggling infections.
     */
    @Test
    public void testToggleInfection() {
        assertTrue(this.actor5.isSusceptible());
        this.network.toggleInfection(this.actor5.getId(), this.ds);
        assertTrue(this.actor5.isInfected());
        this.network.toggleInfection(this.actor5.getId(), this.ds);
        assertTrue(this.actor5.isRecovered());
        this.network.toggleInfection(this.actor5.getId(), this.ds);
        assertTrue(this.actor5.isSusceptible());
    }

	/**
     * Test of removing a connection.
     */
    @Test
    public void testGetRandomActor() {
        List<Actor> actors = new LinkedList<Actor>(this.network.getActors());
        for (int i = 0; i < 100; i++) {
            Actor randomActor = this.network.getRandomActor();
            assertNotNull(randomActor);
            actors.remove(randomActor);
        }
        assertTrue(actors.isEmpty());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testGetRandomNotInfectedActor() {
        List<Actor> actors = new LinkedList<Actor>(this.network.getActors());
        for (int i = 0; i < 100; i++) {
            Actor randomActor = this.network.getRandomNotInfectedActor();
            actors.remove(randomActor);
        }
        assertTrue(actors.size() == 1);
        assertEquals(this.actor6, actors.get(0));
    }

}
