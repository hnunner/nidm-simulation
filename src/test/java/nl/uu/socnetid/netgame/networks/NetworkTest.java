package nl.uu.socnetid.netgame.networks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;


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
    private static final double s     = 8.4;
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
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

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

    /**
     * Test of getting the network type.
     */
    @Test
    public void testGetType() {

        Network empty = new Network("Empty Network");
        Actor actor1 = empty.addActor(uf, ds);
        Actor actor2 = empty.addActor(uf, ds);
        Actor actor3 = empty.addActor(uf, ds);
        Actor actor4 = empty.addActor(uf, ds);
        Actor actor5 = empty.addActor(uf, ds);
        Actor actor6 = empty.addActor(uf, ds);
        assertEquals(NetworkTypes.EMPTY, empty.getType());

        Network full = new Network("Full Network");
        actor1 = full.addActor(uf, ds);
        actor2 = full.addActor(uf, ds);
        actor3 = full.addActor(uf, ds);
        actor4 = full.addActor(uf, ds);
        actor5 = full.addActor(uf, ds);
        actor6 = full.addActor(uf, ds);
        actor1.addConnection(this.actor2);
        actor1.addConnection(this.actor3);
        actor1.addConnection(this.actor4);
        actor1.addConnection(this.actor5);
        actor1.addConnection(this.actor6);
        actor2.addConnection(this.actor3);
        actor2.addConnection(this.actor4);
        actor2.addConnection(this.actor5);
        actor2.addConnection(this.actor6);
        actor3.addConnection(this.actor4);
        actor3.addConnection(this.actor5);
        actor3.addConnection(this.actor6);
        actor4.addConnection(this.actor5);
        actor4.addConnection(this.actor6);
        actor5.addConnection(this.actor6);
        assertEquals(NetworkTypes.FULL, full.getType());

        Network ring = new Network("Ring Network");
        actor1 = ring.addActor(uf, ds);
        actor2 = ring.addActor(uf, ds);
        actor3 = ring.addActor(uf, ds);
        actor4 = ring.addActor(uf, ds);
        actor5 = ring.addActor(uf, ds);
        actor6 = ring.addActor(uf, ds);
        actor1.addConnection(this.actor2);
        actor2.addConnection(this.actor3);
        actor3.addConnection(this.actor4);
        actor4.addConnection(this.actor5);
        actor5.addConnection(this.actor6);
        actor6.addConnection(this.actor1);
        assertEquals(NetworkTypes.RING, ring.getType());

        Network twoRings = new Network("Two Rings Network");
        actor1 = twoRings.addActor(uf, ds);
        actor2 = twoRings.addActor(uf, ds);
        actor3 = twoRings.addActor(uf, ds);
        actor4 = twoRings.addActor(uf, ds);
        actor5 = twoRings.addActor(uf, ds);
        actor6 = twoRings.addActor(uf, ds);
        actor1.addConnection(this.actor2);
        actor2.addConnection(this.actor3);
        actor3.addConnection(this.actor1);
        actor4.addConnection(this.actor5);
        actor5.addConnection(this.actor6);
        actor6.addConnection(this.actor4);
        assertEquals(NetworkTypes.UNDEFINED, twoRings.getType());

        Network star = new Network("Star Network");
        actor1 = star.addActor(uf, ds);
        actor2 = star.addActor(uf, ds);
        actor3 = star.addActor(uf, ds);
        actor4 = star.addActor(uf, ds);
        actor5 = star.addActor(uf, ds);
        actor6 = star.addActor(uf, ds);
        actor1.addConnection(this.actor2);
        actor1.addConnection(this.actor3);
        actor1.addConnection(this.actor4);
        actor1.addConnection(this.actor5);
        actor1.addConnection(this.actor6);
        assertEquals(NetworkTypes.STAR, star.getType());

        Network incompleteStar = new Network("Incomplete Star Network");
        actor1 = incompleteStar.addActor(uf, ds);
        actor2 = incompleteStar.addActor(uf, ds);
        actor3 = incompleteStar.addActor(uf, ds);
        actor4 = incompleteStar.addActor(uf, ds);
        actor5 = incompleteStar.addActor(uf, ds);
        actor6 = incompleteStar.addActor(uf, ds);
        actor1.addConnection(this.actor2);
        actor1.addConnection(this.actor3);
        actor1.addConnection(this.actor4);
        actor1.addConnection(this.actor5);
        assertEquals(NetworkTypes.UNDEFINED, incompleteStar.getType());

    }

    /**
     * Test whether the average degree is computed correctly.
     */
    @Test
    public void testGetAverageDegree() {
        Network network = new Network("Network Average Degree Test");

        // f(5) = 2.28
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(5, network.getN());
        assertEquals(2.28, network.getAverageDegree(), 0.2);

        // f(10) = 3.58
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(10, network.getN());
        assertEquals(3.58, network.getAverageDegree(), 0.2);

        // f(15) = 4.62
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(15, network.getN());
        assertEquals(4.62, network.getAverageDegree(), 0.2);

        // f(20) = 5.49
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(20, network.getN());
        assertEquals(5.49, network.getAverageDegree(), 0.2);

        // f(25) = 6.25
        for (int i = 0; i < 5; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(25, network.getN());
        assertEquals(6.25, network.getAverageDegree(), 0.2);

        // f(50) = 9.33
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(50, network.getN());
        assertEquals(9.33, network.getAverageDegree(), 0.2);

        // f(75) = 11.89
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(75, network.getN());
        assertEquals(11.89, network.getAverageDegree(), 0.2);

        // f(100) = 14.13
        for (int i = 0; i < 25; i++) {
            network.addActor(uf, ds);
        }
        assertEquals(100, network.getN());
        assertEquals(14.13, network.getAverageDegree(), 0.2);
    }

}
