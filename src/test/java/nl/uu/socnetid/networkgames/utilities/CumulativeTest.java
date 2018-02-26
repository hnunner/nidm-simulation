package nl.uu.socnetid.networkgames.utilities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CumulativeTest {

    // TODO create more complex network, so that double indirect benefits would be possible
    //      (e.g., take the network from IRTCTest.java)
    // TODO implement TestCase for truncated connections utility function

    // graph
    private Graph graph;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;

    // utility function
    private UtilityFunction uf;

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
        this.graph = new SingleGraph("Cumulative Test");
        this.uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        List<Actor> actors = new ArrayList<Actor>();

        this.actor1 = Actor.newInstance(uf, ds, this.graph);
        this.actor2 = Actor.newInstance(uf, ds, this.graph);
        this.actor3 = Actor.newInstance(uf, ds, this.graph);
        this.actor4 = Actor.newInstance(uf, ds, this.graph);
        this.actor5 = Actor.newInstance(uf, ds, this.graph);

        actors.add(this.actor1);
        actors.add(this.actor2);
        actors.add(this.actor3);
        actors.add(this.actor4);
        actors.add(this.actor5);

        this.actor1.initCoActors(actors);
        this.actor2.initCoActors(actors);
        this.actor3.initCoActors(actors);
        this.actor4.initCoActors(actors);
        this.actor5.initCoActors(actors);

        // connections are always bidirectional
        this.actor1.addConnection(this.actor2);
        this.actor2.addConnection(this.actor1);

        this.actor1.addConnection(this.actor3);
        this.actor3.addConnection(this.actor1);

        this.actor1.addConnection(this.actor4);
        this.actor4.addConnection(this.actor1);

        this.actor3.addConnection(this.actor4);
        this.actor4.addConnection(this.actor3);

        this.actor4.addConnection(this.actor5);
        this.actor5.addConnection(this.actor4);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(3.5, this.actor1.getUtility().getOverallUtility(), 0);
        assertEquals(2.0, this.actor2.getUtility().getOverallUtility(), 0);
        assertEquals(3.0, this.actor3.getUtility().getOverallUtility(), 0);
        assertEquals(3.5, this.actor4.getUtility().getOverallUtility(), 0);
        assertEquals(2.0, this.actor5.getUtility().getOverallUtility(), 0);
    }

}
