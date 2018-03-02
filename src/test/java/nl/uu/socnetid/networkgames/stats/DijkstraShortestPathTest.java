package nl.uu.socnetid.networkgames.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class DijkstraShortestPathTest {

    // network
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
    private Actor actor8;
    private Actor actor9;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        this.network = new Network("Dijkstra Shortest Path Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds);
        this.actor2 = this.network.addActor(uf, ds);
        this.actor3 = this.network.addActor(uf, ds);
        this.actor4 = this.network.addActor(uf, ds);
        this.actor5 = this.network.addActor(uf, ds);
        this.actor6 = this.network.addActor(uf, ds);
        this.actor7 = this.network.addActor(uf, ds);
        this.actor8 = this.network.addActor(uf, ds);
        this.actor9 = this.network.addActor(uf, ds);

        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor5);
        this.actor1.addConnection(this.actor7);
        this.actor2.addConnection(this.actor3);
        this.actor3.addConnection(this.actor4);
        this.actor3.addConnection(this.actor6);
        this.actor5.addConnection(this.actor6);
        this.actor5.addConnection(this.actor8);
        this.actor7.addConnection(this.actor8);
    }


    /**
     * Test of shortest path computation.
     */
    @Test
    public void testGetShortestPath() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(this.actor1);

        LinkedList<Actor> shortestPathActor4 = dijkstraShortestPath.getShortestPath(this.actor4);
        assertEquals(4, shortestPathActor4.size());
        assertEquals(this.actor1, shortestPathActor4.get(0));
        assertEquals(this.actor2, shortestPathActor4.get(1));
        assertEquals(this.actor3, shortestPathActor4.get(2));
        assertEquals(this.actor4, shortestPathActor4.get(3));

        LinkedList<Actor> shortestPathActor6 = dijkstraShortestPath.getShortestPath(this.actor6);
        assertEquals(3, shortestPathActor6.size());
        assertEquals(this.actor1, shortestPathActor6.get(0));
        assertEquals(this.actor5, shortestPathActor6.get(1));
        assertEquals(this.actor6, shortestPathActor6.get(2));
    }

    /**
     * Test of shortest path length computation.
     */
    @Test
    public void testGetShortestPathLength() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(this.actor1);

        Integer shortestPathLengthActor4 = dijkstraShortestPath.getShortestPathLength(this.actor4);
        assertEquals(3, shortestPathLengthActor4.intValue());

        Integer shortestPathLengthActor7 = dijkstraShortestPath.getShortestPathLength(this.actor7);
        assertEquals(1, shortestPathLengthActor7.intValue());

        Integer shortestPathLengthActor8 = dijkstraShortestPath.getShortestPathLength(this.actor8);
        assertEquals(2, shortestPathLengthActor8.intValue());

        Integer shortestPathLengthActor9 = dijkstraShortestPath.getShortestPathLength(this.actor9);
        assertNull(shortestPathLengthActor9);
    }

}
