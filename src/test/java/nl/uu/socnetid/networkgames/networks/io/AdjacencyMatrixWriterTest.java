package nl.uu.socnetid.networkgames.networks.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.RationalActor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.disease.types.DiseaseType;
import nl.uu.socnetid.networkgames.network.io.AdjacencyMatrixWriter;
import nl.uu.socnetid.networkgames.network.io.NetworkWriter;
import nl.uu.socnetid.networkgames.network.networks.Network;
import nl.uu.socnetid.networkgames.network.networks.SimpleNetwork;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriterTest {

    //actors
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
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        NetworkWriter writer = new AdjacencyMatrixWriter();
        String writerMatrix = writer.write(network);

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
