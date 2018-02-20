package nl.uu.socnetid.networkgames.stats;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
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
public class StatsComputerTest {

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

        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);
        actors.add(actor4);
        actors.add(actor5);
        actors.add(actor6);
        actors.add(actor7);

        actor1.initCoActors(actors);
        actor2.initCoActors(actors);
        actor3.initCoActors(actors);
        actor4.initCoActors(actors);
        actor5.initCoActors(actors);
        actor6.initCoActors(actors);
        actor7.initCoActors(actors);

        // connections actor 1
        actor1.addConnection(actor2);
        actor1.addConnection(actor3);
        actor1.addConnection(actor4);

        // connections actor 2
        actor2.addConnection(actor1);
        actor2.addConnection(actor4);

        // connections actor 3
        actor3.addConnection(actor1);

        // connections actor 4
        actor4.addConnection(actor1);
        actor4.addConnection(actor2);

        // connections actor 5
        actor5.addConnection(actor6);

        // connections actor 6
        actor6.addConnection(actor5);

        // connections actor 7
    }


    /**
     * Test of closeness computation.
     */
    @Test
    public void testComputeCloseness() {
        assertEquals(0.5, Precision.round(StatsComputer.computeCloseness(actor1), 2), 0);
        assertEquals(0.47, Precision.round(StatsComputer.computeCloseness(actor4), 2), 0);
        assertEquals(0.17, Precision.round(StatsComputer.computeCloseness(actor5), 2), 0);
        assertEquals(0, Precision.round(StatsComputer.computeCloseness(actor7), 2), 0);
    }

}
