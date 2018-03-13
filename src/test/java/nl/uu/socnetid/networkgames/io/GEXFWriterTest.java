package nl.uu.socnetid.networkgames.io;

import static org.junit.Assert.assertEquals;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorAttributes;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.utilities.IRTC;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class GEXFWriterTest {

    // network
    private Network network;

    // utility
    private static final double alpha = 5.3;
    private static final double beta  = 1.2;
    private static final double c     = 4.1;
    private UtilityFunction uf;

    // disease
    private static final double delta = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private static final int    tau   = 10;
    private DiseaseSpecs ds;

    // actors
    private static final double r     = 1.2;
    private Actor actor1;
    private Actor actor2;
    private Actor actor3;
    private Actor actor4;
    private Actor actor5;
    private Actor actor6;
    private Actor actor7;
    private Actor actor8;
    private Actor actor9;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {
        this.network = new Network("GEXFWriter Test");

        this.uf = new IRTC(alpha, beta, c);
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, delta, gamma, mu);

        this.actor1 = this.network.addActor(uf, ds, r);
        this.actor2 = this.network.addActor(uf, ds, r);
        this.actor3 = this.network.addActor(uf, ds, r);
        this.actor4 = this.network.addActor(uf, ds, r);
        this.actor5 = this.network.addActor(uf, ds, r);
        this.actor6 = this.network.addActor(uf, ds, r);
        this.actor7 = this.network.addActor(uf, ds, r);
        this.actor8 = this.network.addActor(uf, ds, r);
        this.actor9 = this.network.addActor(uf, ds, r);

        // connections
        this.actor1.addConnection(this.actor2);
        this.actor1.addConnection(this.actor3);
        this.actor1.addConnection(this.actor4);
        this.actor2.addConnection(this.actor6);
        this.actor3.addConnection(this.actor5);
        this.actor4.addConnection(this.actor5);
        this.actor4.addConnection(this.actor6);
        this.actor4.addConnection(this.actor7);
        this.actor5.addConnection(this.actor7);
        this.actor5.addConnection(this.actor8);
        this.actor6.addConnection(this.actor7);
        this.actor7.addConnection(this.actor8);
        this.actor8.addConnection(this.actor9);
    }

    /**
     * Test of writing a static gexf representation of the network.
     *
     * @throws Exception
     *          if something goes wrong with the file handling
     */
    @Test
    public void testStaticWrite() throws Exception {

        // write the file
        String file = this.folder.newFile("test.gexf").getPath();
        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.writeStaticNetwork(this.network, file);

        // read the file
        Graph graph = new SingleGraph("GEXFWriter Test");
        FileSource fs = FileSourceFactory.sourceFor(file);
        fs.addSink(graph);
        fs.readAll(file);

        // tests
        assertEquals(this.network.getEdgeSet().size(), graph.getEdgeSet().size());
        assertEquals(this.network.getNodeSet().size(), graph.getNodeSet().size());
        for (int i = 0; i < 20; i++) {
            Actor actor = this.network.getRandomActor();
            Node expectedNode = this.network.getNode(actor.getId());
            Node actualNode = graph.getNode(actor.getId());
            assertEquals(expectedNode.getId(), actualNode.getId());
            assertEquals(expectedNode.getAttributeCount(), actualNode.getAttributeCount());
            String expectedDiseaseGroup = expectedNode.getAttribute(
                    ActorAttributes.DISEASE_GROUP.toString()).toString();
            String actualDiseaseGroup = actualNode.getAttribute(
                    ActorAttributes.DISEASE_GROUP.toString()).toString();
            assertEquals(expectedDiseaseGroup, actualDiseaseGroup);
            assertEquals(expectedNode.getDegree(), actualNode.getDegree());
        }
    }



    /**
     * Test of writing a dynamic gexf representation of the network.
     *
     * @throws Exception
     *          if something goes wrong with the file handling
     */
    @Test
    public void testDynamicWrite() throws Exception {

        // preparations
        Network dynamicNetwork = new Network("Dynamic GEXFWriter Test (write)");
        String file = this.folder.newFile("test.gexf").getPath();
        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.startRecording(dynamicNetwork, file);

        Actor dynamicActor1 = dynamicNetwork.addActor(this.uf, this.ds);
        Actor dynamicActor2 = dynamicNetwork.addActor(this.uf, this.ds);
        Actor dynamicActor3 = dynamicNetwork.addActor(this.uf, this.ds);
        Actor dynamicActor4 = dynamicNetwork.addActor(this.uf, this.ds);

        dynamicActor1.addConnection(dynamicActor2);
        dynamicActor1.addConnection(dynamicActor3);
        dynamicActor1.addConnection(dynamicActor4);
        dynamicActor3.addConnection(dynamicActor4);

        dynamicActor1.infect(this.ds);
        dynamicActor2.infect(this.ds);
        dynamicActor3.infect(this.ds);
        dynamicActor1.cure();
        dynamicActor4.infect(this.ds);
        dynamicActor2.cure();
        dynamicActor1.makeSusceptible();

        gexfWriter.stopRecording();

        // read the file
        Graph graph = new SingleGraph("Dynamic GEXFWriter Test (read)");
        FileSource fs = FileSourceFactory.sourceFor(file);
        fs.addSink(graph);
        fs.readAll(file);

        // tests
        assertEquals(dynamicNetwork.getEdgeSet().size(), graph.getEdgeSet().size());
        assertEquals(dynamicNetwork.getNodeSet().size(), graph.getNodeSet().size());
        for (int i = 0; i < 20; i++) {
            Actor actor = dynamicNetwork.getRandomActor();
            Node expectedNode = dynamicNetwork.getNode(actor.getId());
            Node actualNode = graph.getNode(actor.getId());
            assertEquals(expectedNode.getId(), actualNode.getId());
            String expectedDiseaseGroup = expectedNode.getAttribute(
                    ActorAttributes.DISEASE_GROUP.toString()).toString();
            String actualDiseaseGroup = actualNode.getAttribute(
                    ActorAttributes.DISEASE_GROUP.toString()).toString();
            assertEquals(expectedDiseaseGroup, actualDiseaseGroup);
            assertEquals(expectedNode.getDegree(), actualNode.getDegree());
        }
    }

}
