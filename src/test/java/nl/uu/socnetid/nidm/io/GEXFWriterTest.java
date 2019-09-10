/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.io;

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

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentAttributes;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Irtc;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

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
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private static final int    tau   = 10;
    private DiseaseSpecs ds;

    // agents
    private static final double r     = 1.2;
    private static final double phi   = 0.4;
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;
    private Agent agent7;
    private Agent agent8;
    private Agent agent9;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {
        this.network = new Network("GEXFWriter Test");

        this.uf = new Irtc(alpha, beta, c);
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent2 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent3 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent4 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent5 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent6 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent7 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent8 = this.network.addAgent(uf, ds, r, r, phi);
        this.agent9 = this.network.addAgent(uf, ds, r, r, phi);

        // connections
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent2.addConnection(this.agent6);
        this.agent3.addConnection(this.agent5);
        this.agent4.addConnection(this.agent5);
        this.agent4.addConnection(this.agent6);
        this.agent4.addConnection(this.agent7);
        this.agent5.addConnection(this.agent7);
        this.agent5.addConnection(this.agent8);
        this.agent6.addConnection(this.agent7);
        this.agent7.addConnection(this.agent8);
        this.agent8.addConnection(this.agent9);
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
            Agent agent = this.network.getRandomAgent();
            Node expectedNode = this.network.getNode(agent.getId());
            Node actualNode = graph.getNode(agent.getId());
            assertEquals(expectedNode.getId(), actualNode.getId());
            assertEquals(expectedNode.getAttributeCount(), actualNode.getAttributeCount());
            String expectedDiseaseGroup = expectedNode.getAttribute(
                    AgentAttributes.DISEASE_GROUP.toString()).toString();
            String actualDiseaseGroup = actualNode.getAttribute(
                    AgentAttributes.DISEASE_GROUP.toString()).toString();
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

        Agent dynamicAgent1 = dynamicNetwork.addAgent(this.uf, this.ds);
        Agent dynamicAgent2 = dynamicNetwork.addAgent(this.uf, this.ds);
        Agent dynamicAgent3 = dynamicNetwork.addAgent(this.uf, this.ds);
        Agent dynamicAgent4 = dynamicNetwork.addAgent(this.uf, this.ds);

        dynamicAgent1.addConnection(dynamicAgent2);
        dynamicAgent1.addConnection(dynamicAgent3);
        dynamicAgent1.addConnection(dynamicAgent4);
        dynamicAgent3.addConnection(dynamicAgent4);

        dynamicAgent1.infect(this.ds);
        dynamicAgent2.infect(this.ds);
        dynamicAgent3.infect(this.ds);
        dynamicAgent1.cure();
        dynamicAgent4.infect(this.ds);
        dynamicAgent2.cure();
        dynamicAgent1.makeSusceptible();

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
            Agent agent = dynamicNetwork.getRandomAgent();
            Node expectedNode = dynamicNetwork.getNode(agent.getId());
            Node actualNode = graph.getNode(agent.getId());
            assertEquals(expectedNode.getId(), actualNode.getId());
            String expectedDiseaseGroup = expectedNode.getAttribute(
                    AgentAttributes.DISEASE_GROUP.toString()).toString();
            String actualDiseaseGroup = actualNode.getAttribute(
                    AgentAttributes.DISEASE_GROUP.toString()).toString();
            assertEquals(expectedDiseaseGroup, actualDiseaseGroup);
            assertEquals(expectedNode.getDegree(), actualNode.getDegree());
        }
    }

}
