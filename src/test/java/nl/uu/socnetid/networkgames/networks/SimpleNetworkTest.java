package nl.uu.socnetid.networkgames.networks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
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

    // graph
    private Graph graph;

    /** actors */
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;

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
	    this.graph = new SingleGraph("Network Test");

	    UtilityFunction uf = new Cumulative();

        ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

	    List<Actor> actors = new ArrayList<Actor>();

	    this.actor1 = Actor.newInstance(uf, ds, graph);
	    this.actor2 = Actor.newInstance(uf, ds, graph);
	    this.actor3 = Actor.newInstance(uf, ds, graph);
	    this.actor4 = Actor.newInstance(uf, ds, graph);

        actors.add(this.actor1);
		actors.add(this.actor2);
		actors.add(this.actor3);
		actors.add(this.actor4);

		this.actor1.initCoActors(actors);
		this.actor2.initCoActors(actors);
		this.actor3.initCoActors(actors);
		this.actor4.initCoActors(actors);

		this.network = new SimpleNetwork(actors);

        // connections are always bidirectional
		this.actor1.addConnection(this.actor2);
		this.actor2.addConnection(this.actor1);

		this.actor1.addConnection(this.actor3);
		this.actor3.addConnection(this.actor1);

		this.actor1.addConnection(this.actor4);
		this.actor4.addConnection(this.actor1);

		this.actor3.addConnection(this.actor4);
		this.actor4.addConnection(this.actor3);
	}

    /**
     * Test of getting all connections of a specific actor.
     */
    @Test
    public void testGetConnectionsOfActor() {

        List<Actor> connectionsOfActor1 = this.network.getConnectionsOfActor(this.actor1);
        assertFalse(connectionsOfActor1.contains(this.actor1));
        assertTrue(connectionsOfActor1.contains(this.actor2));
        assertTrue(connectionsOfActor1.contains(this.actor3));
        assertTrue(connectionsOfActor1.contains(this.actor4));

        List<Actor> connectionsOfActor3 = this.network.getConnectionsOfActor(this.actor3);
        assertTrue(connectionsOfActor3.contains(this.actor1));
        assertFalse(connectionsOfActor3.contains(this.actor2));
        assertFalse(connectionsOfActor3.contains(this.actor3));
        assertTrue(connectionsOfActor3.contains(this.actor4));
    }

}
