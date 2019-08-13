package nl.uu.socnetid.nidm.utilities;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.util.Precision;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utilities.Cumulative;
import nl.uu.socnetid.nidm.utilities.IRTC;
import nl.uu.socnetid.nidm.utilities.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class IRTCTest {

    // network
    private Network network;

    // constants
    private static final double alpha = 5.3;
    private static final double beta  = 1.2;
    private static final double c     = 4.1;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private static final double r     = 1.2;
    private static final double phi   = 0.4;
    private static final int    tau   = 10;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;
    private Agent agent7;
    private Agent agent8;
    private Agent agent9;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        this.network = new Network("IRTC Test");

        UtilityFunction uf = new IRTC(alpha, beta, c);
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent2 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent3 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent4 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent5 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent6 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent7 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent8 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent9 = this.network.addAgent(uf, ds, r, r, phi);

        // infections
        this.agent4.infect(ds);
        this.agent6.infect(ds);
        this.agent8.infect(ds);

        // connections
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent2.addConnection(this.agent6);
        this.agent3.addConnection(this.agent5);
        this.agent4.addConnection(this.agent5);
        this.agent4.addConnection(this.agent6);
        this.agent4.addConnection(this.agent7);
        this.agent5.addConnection(this.agent7);
        this.agent5.addConnection(this.agent8);
        this.agent6.addConnection(this.agent7);
        this.agent7.addConnection(this.agent8);
        this.agent8.addConnection(this.agent9);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals( -0.99, Precision.round(this.agent1.getUtility().getOverallUtility(), 2), 0);
        assertEquals( -6.15, Precision.round(this.agent4.getUtility().getOverallUtility(), 2), 0);
        assertEquals(-13.37, Precision.round(this.agent7.getUtility().getOverallUtility(), 2), 0);
    }

}