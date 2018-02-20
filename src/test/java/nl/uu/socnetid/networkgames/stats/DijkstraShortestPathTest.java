package nl.uu.socnetid.networkgames.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.RationalActor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class DijkstraShortestPathTest {

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
        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);


        List<Actor> actors = new ArrayList<Actor>();
        actor1 = RationalActor.newInstance(uf, ds);
        actor2 = RationalActor.newInstance(uf, ds);
        actor3 = RationalActor.newInstance(uf, ds);
        actor4 = RationalActor.newInstance(uf, ds);
        actor5 = RationalActor.newInstance(uf, ds);
        actor6 = RationalActor.newInstance(uf, ds);
        actor7 = RationalActor.newInstance(uf, ds);
        actor8 = RationalActor.newInstance(uf, ds);
        actor9 = RationalActor.newInstance(uf, ds);

        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);
        actors.add(actor4);
        actors.add(actor5);
        actors.add(actor6);
        actors.add(actor7);
        actors.add(actor8);
        actors.add(actor9);

        actor1.initCoActors(actors);
        actor2.initCoActors(actors);
        actor3.initCoActors(actors);
        actor4.initCoActors(actors);
        actor5.initCoActors(actors);
        actor6.initCoActors(actors);
        actor7.initCoActors(actors);
        actor8.initCoActors(actors);
        actor9.initCoActors(actors);

        // connections actor 1
        actor1.addConnection(actor2);
        actor1.addConnection(actor5);
        actor1.addConnection(actor7);

        // connections actor 2
        actor2.addConnection(actor1);
        actor2.addConnection(actor3);

        // connections actor 3
        actor3.addConnection(actor6);
        actor3.addConnection(actor4);

        // connections actor 4
        actor4.addConnection(actor3);

        // connections actor 5
        actor5.addConnection(actor1);
        actor5.addConnection(actor6);
        actor5.addConnection(actor8);

        // connections actor 6
        actor6.addConnection(actor3);
        actor6.addConnection(actor5);

        // connections actor 7
        actor7.addConnection(actor1);
        actor7.addConnection(actor8);

        // connections actor 8
        actor8.addConnection(actor5);
        actor8.addConnection(actor7);
    }


    /**
     * Test of shortest path computation.
     */
    @Test
    public void testGetShortestPath() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(actor1);

        LinkedList<Actor> shortestPathActor4 = dijkstraShortestPath.getShortestPath(actor4);
        assertEquals(4, shortestPathActor4.size());
        assertEquals(actor1, shortestPathActor4.get(0));
        assertEquals(actor2, shortestPathActor4.get(1));
        assertEquals(actor3, shortestPathActor4.get(2));
        assertEquals(actor4, shortestPathActor4.get(3));

        LinkedList<Actor> shortestPathActor6 = dijkstraShortestPath.getShortestPath(actor6);
        assertEquals(3, shortestPathActor6.size());
        assertEquals(actor1, shortestPathActor6.get(0));
        assertEquals(actor5, shortestPathActor6.get(1));
        assertEquals(actor6, shortestPathActor6.get(2));
    }

    /**
     * Test of shortest path length computation.
     */
    @Test
    public void testGetShortestPathLength() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(actor1);

        Integer shortestPathLengthActor4 = dijkstraShortestPath.getShortestPathLength(actor4);
        assertEquals(3, shortestPathLengthActor4.intValue());

        Integer shortestPathLengthActor7 = dijkstraShortestPath.getShortestPathLength(actor7);
        assertEquals(1, shortestPathLengthActor7.intValue());

        Integer shortestPathLengthActor8 = dijkstraShortestPath.getShortestPathLength(actor8);
        assertEquals(2, shortestPathLengthActor8.intValue());

        Integer shortestPathLengthActor9 = dijkstraShortestPath.getShortestPathLength(actor9);
        assertNull(shortestPathLengthActor9);
    }

}
