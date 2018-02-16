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

        // infections
        actor3.infect(ds);
        actor5.infect(ds);

        actors.add(actor1);
        actors.add(actor2);
        actors.add(actor3);
        actors.add(actor4);
        actors.add(actor5);

        actor1.initCoActors(actors);
        actor2.initCoActors(actors);
        actor3.initCoActors(actors);
        actor4.initCoActors(actors);
        actor5.initCoActors(actors);

        // connections are always bidirectional
        actor1.addConnection(actor2);
        actor2.addConnection(actor1);

        actor1.addConnection(actor3);
        actor3.addConnection(actor1);

        actor1.addConnection(actor4);
        actor4.addConnection(actor1);

        actor3.addConnection(actor4);
        actor4.addConnection(actor3);

        actor4.addConnection(actor5);
        actor5.addConnection(actor4);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(-2.6, Precision.round(actor1.getUtility().getOverallUtility(), 1), 0);
        assertEquals( 3.6, Precision.round(actor2.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-3.6, Precision.round(actor3.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-9.9, Precision.round(actor4.getUtility().getOverallUtility(), 1), 0);
        assertEquals(-4.8, Precision.round(actor5.getUtility().getOverallUtility(), 1), 0);
    }

}