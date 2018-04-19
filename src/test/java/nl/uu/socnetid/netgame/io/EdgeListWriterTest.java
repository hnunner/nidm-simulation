package nl.uu.socnetid.netgame.io;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.io.network.EdgeListWriter;
import nl.uu.socnetid.netgame.io.network.NetworkWriter;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class EdgeListWriterTest {

    // disease related
    private static final int    tau   = 10;
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    // basic network
    private Network network;

    // expected adjacency matrix
    private static String expectedEdgeList;


    @BeforeClass
    public static void initSuite() {
        // for "Run as Junit Test"
        StringBuilder sb = new StringBuilder();
        sb.append("Source,Target").append(System.getProperty("line.separator"));
        sb.append("1,2").append(System.getProperty("line.separator"));
        sb.append("1,3").append(System.getProperty("line.separator"));
        sb.append("1,4").append(System.getProperty("line.separator"));
        sb.append("2,1").append(System.getProperty("line.separator"));
        sb.append("3,1").append(System.getProperty("line.separator"));
        sb.append("3,4").append(System.getProperty("line.separator"));
        sb.append("4,1").append(System.getProperty("line.separator"));
        sb.append("4,3").append(System.getProperty("line.separator"));
        expectedEdgeList = sb.toString();
    }


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {

        // network
        this.network = new Network("EdgeListWriter Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        Actor actor1 = this.network.addActor(uf, ds);
        Actor actor2 = this.network.addActor(uf, ds);
        Actor actor3 = this.network.addActor(uf, ds);
        Actor actor4 = this.network.addActor(uf, ds);

        actor1.addConnection(actor2);
        actor1.addConnection(actor3);
        actor1.addConnection(actor4);
        actor3.addConnection(actor4);
    }

    /**
     * Test of writing an edge list representation of the network.
     */
    @Test
    public void testWrite() {
        NetworkWriter writer = new EdgeListWriter();
        String actualEdgeList = writer.write(this.network);
        assertEquals(expectedEdgeList, actualEdgeList);
    }

}
