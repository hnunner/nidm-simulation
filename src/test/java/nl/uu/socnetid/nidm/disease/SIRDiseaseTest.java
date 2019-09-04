package nl.uu.socnetid.nidm.disease;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * Test cases for the {@link Agent} class.
 *
 * @author Hendrik Nunner
 */
public class SIRDiseaseTest {

    // network
    private Network network;

    // s
    private Agent agent1;

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {
        this.network = new Network("Agent Test");
        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);
        this.agent1 = this.network.addAgent(uf, this.ds);
        this.agent1.infect(ds);
    }


	/**
	 * Test of disease evolution.
	 */
	@Test
	public void testEvolve() {
	    assertTrue(this.agent1.isInfected());
	    assertEquals(10, this.agent1.getDisease().getTimeUntilCured());
	    this.agent1.getDisease().evolve();
        assertEquals(9, this.agent1.getDisease().getTimeUntilCured());
	}

}
