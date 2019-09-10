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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.NetworkWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class EdgeListWriterTest {

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
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
     * Test of writing an edge list representation of the network.
     */
    @Test
    public void testWrite() {
        NetworkWriter writer = new EdgeListWriter();
        String actualEdgeList = writer.write(this.network);
        assertEquals(expectedEdgeList, actualEdgeList);
    }

}
