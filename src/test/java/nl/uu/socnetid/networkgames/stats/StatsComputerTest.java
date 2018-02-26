package nl.uu.socnetid.networkgames.stats;

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
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class StatsComputerTest {

    // graph
    private Graph graph;

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


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        this.graph = new SingleGraph("Stats Computer Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);


        List<Actor> actors = new ArrayList<Actor>();
        this.actor1 = Actor.newInstance(uf, ds, this.graph);
        this.actor2 = Actor.newInstance(uf, ds, this.graph);
        this.actor3 = Actor.newInstance(uf, ds, this.graph);
        this.actor4 = Actor.newInstance(uf, ds, this.graph);
        this.actor5 = Actor.newInstance(uf, ds, this.graph);
        this.actor6 = Actor.newInstance(uf, ds, this.graph);
        this.actor7 = Actor.newInstance(uf, ds, this.graph);

        actors.add(this.actor1);
        actors.add(this.actor2);
        actors.add(this.actor3);
        actors.add(this.actor4);
        actors.add(this.actor5);
        actors.add(this.actor6);
        actors.add(this.actor7);

        this.actor1.initCoActors(actors);
        this.actor2.initCoActors(actors);
        this.actor3.initCoActors(actors);
        this.actor4.initCoActors(actors);
        this.actor5.initCoActors(actors);
        this.actor6.initCoActors(actors);
        this.actor7.initCoActors(actors);

        // connections actor 1
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);

        // connections actor 2
        this.actor2.addConnection(this.actor1);
        this.actor2.addConnection(this.actor4);

        // connections actor 3
        this.actor3.addConnection(this.actor1);

        // connections actor 4
        this.actor4.addConnection(this.actor1);
        this.actor4.addConnection(this.actor2);

        // connections actor 5
        this.actor5.addConnection(this.actor6);

        // connections actor 6
        this.actor6.addConnection(this.actor5);

        // connections actor 7
    }


    /**
     * Test of closeness computation.
     */
    @Test
    public void testComputeCloseness() {
        assertEquals(0.5, Precision.round(StatsComputer.computeCloseness(this.actor1), 2), 0);
        assertEquals(0.47, Precision.round(StatsComputer.computeCloseness(this.actor4), 2), 0);
        assertEquals(0.17, Precision.round(StatsComputer.computeCloseness(this.actor5), 2), 0);
        assertEquals(0, Precision.round(StatsComputer.computeCloseness(this.actor7), 2), 0);
    }

}
