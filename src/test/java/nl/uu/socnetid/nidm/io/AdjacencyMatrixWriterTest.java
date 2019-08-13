package nl.uu.socnetid.nidm.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.network.AdjacencyMatrixWriter;
import nl.uu.socnetid.nidm.io.network.NetworkWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utilities.Cumulative;
import nl.uu.socnetid.nidm.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriterTest {

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
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
    public void initNetwork() {

        // network
        this.network = new Network("AdjacencyMatrixWriter Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        Agent agent1 = this.network.addAgent(uf, ds);
        Agent agent2 = this.network.addAgent(uf, ds);
        Agent agent3 = this.network.addAgent(uf, ds);
        Agent agent4 = this.network.addAgent(uf, ds);

        agent1.addConnection(agent2);
        agent1.addConnection(agent3);
        agent1.addConnection(agent4);
        agent3.addConnection(agent4);
    }

    /**
     * Test of writing an adjacency matrix representation of the network.
     */
    @Test
    public void testWrite() {
        NetworkWriter writer = new AdjacencyMatrixWriter();
        String writerMatrix = writer.write(this.network);

        List<String> writerList = new ArrayList<String>(Arrays.asList(writerMatrix.split(",|\\\n")));
        List<String> expectedList = new ArrayList<String>(Arrays.asList(expectedMatrix.split(",|\\\n")));

        assertEquals(expectedList.size(), writerList.size());

        for (int i = 0; i < writerList.size(); i++) {
            String actual = writerList.get(i);

            // leave out the indices, as they might differ due to
            // unknown order of agent generation in test cases
            if (actual.contains("P")) {
                continue;
            }
            String expected = expectedList.get(i);
            assertEquals(expected, actual);
        }

    }

}
