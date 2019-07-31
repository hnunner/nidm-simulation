package nl.uu.socnetid.netgame.utilities;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.util.Precision;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CIDMoTest {

    // network
    private Network network;

    // constants
    private static final double alpha   = 5.3;
    private static final double kappa   = 0.7;
    private static final double beta    = 1.2;
    private static final double lamda   = 0.9;
    private static final double c       = 4.1;
    private static final double s       = 8.4;
    private static final double gamma   = 0.1;
    private static final double mu      = 2.5;
    private static final double rSigma  = 1.2;
    private static final double rPi     = 0.5;
    private static final double phi     = 0.4;
    private static final int    tau     = 10;

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
        this.network = new Network("IRTC Test");

        UtilityFunction uf = new CIDMo(alpha, kappa, beta, lamda, c);
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor2 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor3 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor4 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor5 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor6 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor7 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor8 = this.network.addActor(uf, ds, rSigma, rPi, phi);
        this.actor9 = this.network.addActor(uf, ds, rSigma, rPi, phi);

        // infections
        this.actor4.infect(ds);
        this.actor6.infect(ds);
        this.actor8.infect(ds);

        // connections
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor2.addConnection(this.actor6);
        this.actor3.addConnection(this.actor5);
        this.actor4.addConnection(this.actor5);
        this.actor4.addConnection(this.actor6);
        this.actor4.addConnection(this.actor7);
        this.actor5.addConnection(this.actor7);
        this.actor5.addConnection(this.actor8);
        this.actor6.addConnection(this.actor7);
        this.actor7.addConnection(this.actor8);
        this.actor8.addConnection(this.actor9);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals( -1.07, Precision.round(this.actor1.getUtility().getOverallUtility(), 2), 0);
        assertEquals( -7.86, Precision.round(this.actor4.getUtility().getOverallUtility(), 2), 0);
        assertEquals(-15.43, Precision.round(this.actor7.getUtility().getOverallUtility(), 2), 0);
    }

}
