package nl.uu.socnetid.networkgames.utilities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
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
public class IRTCTest {

    // constants
    private static final double alpha = 5.3;
    private static final double beta  = 1.2;
    private static final double c     = 4.1;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private static final double r     = 1.2;
    private static final int    tau   = 10;

    // graph
    private Graph graph;
    // utility function
    private UtilityFunction uf;
    // disease specs
    private DiseaseSpecs ds;

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
        this.graph = new SingleGraph("IRTC Test");
        this.uf = new IRTC(alpha, beta, c);
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);


        List<Actor> actors = new ArrayList<Actor>();
        this.actor1 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor2 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor3 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor4 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor5 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor6 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor7 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor8 = Actor.newInstance(this.uf, this.ds, r, this.graph);
        this.actor9 = Actor.newInstance(this.uf, this.ds, r, this.graph);

        // infections
        this.actor4.infect(this.ds);
        this.actor6.infect(this.ds);
        this.actor8.infect(this.ds);

        actors.add(this.actor1);
        actors.add(this.actor2);
        actors.add(this.actor3);
        actors.add(this.actor4);
        actors.add(this.actor5);
        actors.add(this.actor6);
        actors.add(this.actor7);
        actors.add(this.actor8);
        actors.add(this.actor9);

        this.actor1.initCoActors(actors);
        this.actor2.initCoActors(actors);
        this.actor3.initCoActors(actors);
        this.actor4.initCoActors(actors);
        this.actor5.initCoActors(actors);
        this.actor6.initCoActors(actors);
        this.actor7.initCoActors(actors);
        this.actor8.initCoActors(actors);
        this.actor9.initCoActors(actors);

        // connections actor 1
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);

        // connections actor 2
        this.actor2.addConnection(this.actor1);
        this.actor2.addConnection(this.actor6);

        // connections actor 3
        this.actor3.addConnection(this.actor1);
        this.actor3.addConnection(this.actor5);

        // connections actor 4
        this.actor4.addConnection(this.actor1);
        this.actor4.addConnection(this.actor2);
        this.actor4.addConnection(this.actor3);
        this.actor4.addConnection(this.actor5);
        this.actor4.addConnection(this.actor6);
        this.actor4.addConnection(this.actor7);

        // connections actor 5
        this.actor5.addConnection(this.actor3);
        this.actor5.addConnection(this.actor4);
        this.actor5.addConnection(this.actor7);
        this.actor5.addConnection(this.actor8);

        // connections actor 6
        this.actor6.addConnection(this.actor2);
        this.actor6.addConnection(this.actor4);
        this.actor6.addConnection(this.actor7);

        // connections actor 7
        this.actor7.addConnection(this.actor4);
        this.actor7.addConnection(this.actor5);
        this.actor7.addConnection(this.actor6);
        this.actor7.addConnection(this.actor8);

        // connections actor 8
        this.actor8.addConnection(this.actor5);
        this.actor8.addConnection(this.actor7);
        this.actor8.addConnection(this.actor9);

        // connections actor 9
        this.actor9.addConnection(this.actor8);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals( -0.24, Precision.round(this.actor1.getUtility().getOverallUtility(), 2), 0);
        assertEquals( -6.15, Precision.round(this.actor4.getUtility().getOverallUtility(), 2), 0);
        assertEquals(-12.33, Precision.round(this.actor7.getUtility().getOverallUtility(), 2), 0);
    }

}
