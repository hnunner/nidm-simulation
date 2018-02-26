package nl.uu.socnetid.networkgames.networks.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.io.AdjacencyMatrixWriter;
import nl.uu.socnetid.networkgames.network.io.NetworkWriter;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriterTest {

    // graph
    private Graph graph;

    //actors
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

    // basic network
    private Network network;

    // expected adjacency matrix
    private static String expectedMatrix;


    @BeforeClass
    public static void initSuite() {
        // for "Run as Junit Test"
        StringBuilder sb = new StringBuilder();
        sb.append(",P1,P2,P3,P4").append(System.getProperty("line.separator"));
        sb.append("P1,0,1,1,1").append(System.getProperty("line.separator"));
        sb.append("P2,1,0,0,0").append(System.getProperty("line.separator"));
        sb.append("P3,1,0,0,1").append(System.getProperty("line.separator"));
        sb.append("P4,1,0,1,0").append(System.getProperty("line.separator"));
        expectedMatrix = sb.toString();
    }


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initActor() {

        // init graphstream
        this.graph = new SingleGraph("Adjacency Matrix Writer Test");

        UtilityFunction uf = new Cumulative();

        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        List<Actor> actors = new ArrayList<Actor>();

        this.actor1 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor2 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor3 = Actor.newInstance(uf, this.ds, this.graph);
        this.actor4 = Actor.newInstance(uf, this.ds, this.graph);

        actors.add(this.actor1);
        actors.add(this.actor2);
        actors.add(this.actor3);
        actors.add(this.actor4);

        this.network = new Network(actors);

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
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        NetworkWriter writer = new AdjacencyMatrixWriter();
        String writerMatrix = writer.write(this.network);

        List<String> writerList = new ArrayList<String>(Arrays.asList(writerMatrix.split(",|\\\n")));
        List<String> expectedList = new ArrayList<String>(Arrays.asList(expectedMatrix.split(",|\\\n")));

        assertEquals(expectedList.size(), writerList.size());

        for (int i = 0; i < writerList.size(); i++) {
            String actual = writerList.get(i);

            // leave out the indices, as they might differ due to
            // unknown order of actor generation in test cases
            if (actual.contains("P")) {
                continue;
            }
            String expected = expectedList.get(i);
            assertEquals(expected, actual);
        }

    }

}
