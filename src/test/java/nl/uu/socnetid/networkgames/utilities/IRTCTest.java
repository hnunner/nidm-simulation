package nl.uu.socnetid.networkgames.utilities;

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

    // actors
    Actor actor1;
    Actor actor2;
    Actor actor3;
    Actor actor4;
    Actor actor5;
    Actor actor6;
    Actor actor7;
    Actor actor8;
    Actor actor9;

    // utility function
    UtilityFunction uf;

    // disease specs
    DiseaseSpecs ds;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {
        uf = new IRTC(alpha, beta, c);
        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);


        List<Actor> actors = new ArrayList<Actor>();
        actor1 = RationalActor.newInstance(uf, ds, r);
        actor2 = RationalActor.newInstance(uf, ds, r);
        actor3 = RationalActor.newInstance(uf, ds, r);
        actor4 = RationalActor.newInstance(uf, ds, r);
        actor5 = RationalActor.newInstance(uf, ds, r);
        actor6 = RationalActor.newInstance(uf, ds, r);
        actor7 = RationalActor.newInstance(uf, ds, r);
        actor8 = RationalActor.newInstance(uf, ds, r);
        actor9 = RationalActor.newInstance(uf, ds, r);

        // infections
        actor4.infect(ds);
        actor6.infect(ds);
        actor8.infect(ds);

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
        actor1.addConnection(actor3);
        actor1.addConnection(actor4);

        // connections actor 2
        actor2.addConnection(actor1);
        actor2.addConnection(actor6);

        // connections actor 3
        actor3.addConnection(actor1);
        actor3.addConnection(actor5);

        // connections actor 4
        actor4.addConnection(actor1);
        actor4.addConnection(actor2);
        actor4.addConnection(actor3);
        actor4.addConnection(actor5);
        actor4.addConnection(actor6);
        actor4.addConnection(actor7);

        // connections actor 5
        actor5.addConnection(actor3);
        actor5.addConnection(actor4);
        actor5.addConnection(actor7);
        actor5.addConnection(actor8);

        // connections actor 6
        actor6.addConnection(actor2);
        actor6.addConnection(actor4);
        actor6.addConnection(actor7);

        // connections actor 7
        actor7.addConnection(actor4);
        actor7.addConnection(actor5);
        actor7.addConnection(actor6);
        actor7.addConnection(actor8);

        // connections actor 8
        actor8.addConnection(actor5);
        actor8.addConnection(actor7);
        actor8.addConnection(actor9);

        // connections actor 9
        actor9.addConnection(actor8);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals( -0.24, Precision.round(actor1.getUtility().getOverallUtility(), 2), 0);
        assertEquals( -6.15, Precision.round(actor4.getUtility().getOverallUtility(), 2), 0);
        assertEquals(-12.33, Precision.round(actor7.getUtility().getOverallUtility(), 2), 0);
    }

}
