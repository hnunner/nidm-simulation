package nl.uu.socnetid.networkgames.networks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.RationalActor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.network.networks.SimpleNetwork;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;


/**
 * Test cases for the {@link SimpleNetwork} class.
 *
 * @author Hendrik Nunner
 */
public class SimpleNetworkTest {

    /** actors */
    Actor actor1;
    Actor actor2;
    Actor actor3;
    Actor actor4;

    // disease related
    DiseaseSpecs ds;
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

	/** network */
	private Network network;


	/**
	 * Performed before each test: Initialization of the network.
	 */
	@Before
	public void initActor() {
	    UtilityFunction uf = new Cumulative();

        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

	    List<Actor> actors = new ArrayList<Actor>();

	    actor1 = RationalActor.newInstance(uf, ds);
	    actor2 = RationalActor.newInstance(uf, ds);
	    actor3 = RationalActor.newInstance(uf, ds);
	    actor4 = RationalActor.newInstance(uf, ds);

        actors.add(actor1);
		actors.add(actor2);
		actors.add(actor3);
		actors.add(actor4);

		actor1.initCoActors(actors);
		actor2.initCoActors(actors);
		actor3.initCoActors(actors);
		actor4.initCoActors(actors);

		this.network = new SimpleNetwork(actors);

        // connections are always bidirectional
        actor1.addConnection(actor2);
        actor2.addConnection(actor1);

        actor1.addConnection(actor3);
        actor3.addConnection(actor1);

        actor1.addConnection(actor4);
        actor4.addConnection(actor1);

        actor3.addConnection(actor4);
        actor4.addConnection(actor3);
	}

    /**
     * Test of getting all connections of a specific actor.
     */
    @Test
    public void testGetConnectionsOfActor() {

        List<Actor> connectionsOfActor1 = this.network.getConnectionsOfActor(actor1);
        assertFalse(connectionsOfActor1.contains(actor1));
        assertTrue(connectionsOfActor1.contains(actor2));
        assertTrue(connectionsOfActor1.contains(actor3));
        assertTrue(connectionsOfActor1.contains(actor4));

        List<Actor> connectionsOfActor3 = this.network.getConnectionsOfActor(actor3);
        assertTrue(connectionsOfActor3.contains(actor1));
        assertFalse(connectionsOfActor3.contains(actor2));
        assertFalse(connectionsOfActor3.contains(actor3));
        assertTrue(connectionsOfActor3.contains(actor4));
    }

}
