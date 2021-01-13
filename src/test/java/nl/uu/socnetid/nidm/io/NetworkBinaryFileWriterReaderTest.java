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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.network.NetworkBinaryFileReader;
import nl.uu.socnetid.nidm.io.network.NetworkBinaryFileWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkBinaryFileWriterReaderTest {

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    // basic network
    private Network network;

    // temporary file folder
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private String file = "test.net";
    private String path;


    /**
     * Performed before each test.
     *
     * @throws IOException
     *          if something goes wrong with file handling
     */
    @Before
    public void init() throws IOException {

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

        this.path = this.folder.newFile(this.file).getParent();
    }

    /**
     * Test of writing a binary representation of the network.
     *
     * @throws Exception
     *          if something goes wrong with file handling
     */
    @Test
    public void testWriteRead() throws Exception {
        NetworkBinaryFileWriter nbfw = new NetworkBinaryFileWriter(path, file, network);
        nbfw.write();

        NetworkBinaryFileReader nbfr = new NetworkBinaryFileReader(path, file);
        Network n = nbfr.read();

        assertNotNull(n);

        System.out.println("Done!");
    }

}
