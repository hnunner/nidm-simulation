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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Irtc;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class DGSWriterReaderTest {

    // network
    private Network network;

    // assortativity conditions
    private List<AssortativityConditions> acs;

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
    private static final double omega = 0.0;
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
        this.acs = new ArrayList<AssortativityConditions>();
        this.acs.add(AssortativityConditions.AGE);
        this.acs.add(AssortativityConditions.PROFESSION);

        this.network = new Network("GEXFWriter Test", true, this.acs);

        this.uf = new Irtc(alpha, beta, c);
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent2 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent3 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent4 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent5 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent6 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent7 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent8 = this.network.addAgent(uf, ds, r, r, phi, omega);
        this.agent9 = this.network.addAgent(uf, ds, r, r, phi, omega);

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
    public void testWriteRead() throws Exception {

        // write the file
        String file = this.folder.newFile("test.dgs").getPath();
        DGSWriter dgsWriter = new DGSWriter();
        dgsWriter.writeNetwork(this.network, file);

        // read the file
        DGSReader dgsReader = new DGSReader();
        Network n = dgsReader.readNetwork(file);

        // tests
        assertTrue(this.network.equals(n));
    }

}
