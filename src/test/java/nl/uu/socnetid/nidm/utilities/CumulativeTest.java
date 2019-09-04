package nl.uu.socnetid.nidm.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class CumulativeTest {

    // network
    private Network network;

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

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        this.network = new Network("Cumulative Test");
        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds);
        this.agent2 = this.network.addAgent(uf, ds);
        this.agent3 = this.network.addAgent(uf, ds);
        this.agent4 = this.network.addAgent(uf, ds);
        this.agent5 = this.network.addAgent(uf, ds);
        this.agent6 = this.network.addAgent(uf, ds);
        this.agent7 = this.network.addAgent(uf, ds);
        this.agent8 = this.network.addAgent(uf, ds);
        this.agent9 = this.network.addAgent(uf, ds);

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
        assertEquals(4.5, this.agent1.getUtility().getOverallUtility(), 0);
        assertEquals(3.5, this.agent2.getUtility().getOverallUtility(), 0);
        assertEquals(4, this.agent3.getUtility().getOverallUtility(), 0);
        assertEquals(5.5, this.agent4.getUtility().getOverallUtility(), 0);
        assertEquals(5.5, this.agent5.getUtility().getOverallUtility(), 0);
        assertEquals(4.5, this.agent6.getUtility().getOverallUtility(), 0);
        assertEquals(6, this.agent7.getUtility().getOverallUtility(), 0);
        assertEquals(4.5, this.agent8.getUtility().getOverallUtility(), 0);
        assertEquals(2, this.agent9.getUtility().getOverallUtility(), 0);
    }

}
