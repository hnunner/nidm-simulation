package nl.uu.socnetid.networkgames.disease;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link Actor} class.
 *
 * @author Hendrik Nunner
 */
public class SIRDiseaseTest {

    // network
    private Network network;

    // actors
    private Actor actor1;

    // disease related
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {
        this.network = new Network("Actor Test");
        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);
        this.actor1 = this.network.addActor(uf, this.ds);
        this.actor1.infect(ds);
    }


	/**
	 * Test of disease evolution.
	 */
	@Test
	public void testEvolve() {
	    assertTrue(this.actor1.isInfected());
	    assertEquals(10, this.actor1.getDisease().getTimeUntilCured());
	    this.actor1.getDisease().evolve();
        assertEquals(9, this.actor1.getDisease().getTimeUntilCured());
	}

}
