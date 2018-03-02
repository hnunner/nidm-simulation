package nl.uu.socnetid.networkgames.networks;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link Network} class.
 *
 * @author Hendrik Nunner
 */
public class NetworkTest {

    // network
    private Network network;

    // actors
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;

    // disease related
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;


	/**
	 * Performed before each test: Initialization of the network.
	 */
	@Before
	public void initNetwork() {
        this.network = new Network("Network Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds);
        this.actor2 = this.network.addActor(uf, ds);
        this.actor3 = this.network.addActor(uf, ds);
        this.actor4 = this.network.addActor(uf, ds);
        this.actor5 = this.network.addActor(uf, ds);
        this.actor6 = this.network.addActor(uf, ds);

        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor3.addConnection(this.actor4);
	}


    /**
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        assertEquals(3, this.actor1.getConnections().size());
        this.actor1.addConnection(actor5);
        assertEquals(4, this.actor1.getConnections().size());

        assertEquals(0, this.actor6.getConnections().size());
        this.actor6.addConnection(actor1);
        assertEquals(5, this.actor1.getConnections().size());
        assertEquals(1, this.actor6.getConnections().size());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {
        assertEquals(3, this.actor1.getConnections().size());
        assertEquals(1, this.actor2.getConnections().size());
        this.actor1.removeConnection(this.actor2);
        assertEquals(2, this.actor1.getConnections().size());
        assertEquals(0, this.actor2.getConnections().size());
    }

}
