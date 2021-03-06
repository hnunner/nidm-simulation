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
package nl.uu.socnetid.nidm.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * Tests for {@link Cumulative} class.
 *
 * @author Hendrik Nunner
 */
public class DijkstraShortestPathTest {

    // network
    private Network network;

    // constants
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;
    private Agent agent7;
    private Agent agent8;
    private Agent agent9;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        this.network = new Network("Dijkstra Shortest Path Test");

        UtilityFunction uf = new Cumulative();
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds);
        this.agent2 = this.network.addAgent(uf, ds);
        this.agent3 = this.network.addAgent(uf, ds);
        this.agent4 = this.network.addAgent(uf, ds);
        this.agent5 = this.network.addAgent(uf, ds);
        this.agent6 = this.network.addAgent(uf, ds);
        this.agent7 = this.network.addAgent(uf, ds);
        this.agent8 = this.network.addAgent(uf, ds);
        this.agent9 = this.network.addAgent(uf, ds);

        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent5);
        this.agent1.addConnection(this.agent7);
        this.agent2.addConnection(this.agent3);
        this.agent3.addConnection(this.agent4);
        this.agent3.addConnection(this.agent6);
        this.agent5.addConnection(this.agent6);
        this.agent5.addConnection(this.agent8);
        this.agent7.addConnection(this.agent8);
    }


    /**
     * Test of shortest path computation.
     */
    @Test
    public void testGetShortestPath() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(this.agent1);

        LinkedList<Agent> shortestPathAgent4 = dijkstraShortestPath.getShortestPath(this.agent4);
        assertEquals(4, shortestPathAgent4.size());
        assertEquals(this.agent1, shortestPathAgent4.get(0));
        assertEquals(this.agent2, shortestPathAgent4.get(1));
        assertEquals(this.agent3, shortestPathAgent4.get(2));
        assertEquals(this.agent4, shortestPathAgent4.get(3));

        LinkedList<Agent> shortestPathAgent6 = dijkstraShortestPath.getShortestPath(this.agent6);
        assertEquals(3, shortestPathAgent6.size());
        assertEquals(this.agent1, shortestPathAgent6.get(0));
        assertEquals(this.agent5, shortestPathAgent6.get(1));
        assertEquals(this.agent6, shortestPathAgent6.get(2));
    }

    /**
     * Test of shortest path length computation.
     */
    @Test
    public void testGetShortestPathLength() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath();
        dijkstraShortestPath.executeShortestPaths(this.agent1);

        Integer shortestPathLengthAgent4 = dijkstraShortestPath.getShortestPathLength(this.agent4);
        assertEquals(3, shortestPathLengthAgent4.intValue());

        Integer shortestPathLengthAgent7 = dijkstraShortestPath.getShortestPathLength(this.agent7);
        assertEquals(1, shortestPathLengthAgent7.intValue());

        Integer shortestPathLengthAgent8 = dijkstraShortestPath.getShortestPathLength(this.agent8);
        assertEquals(2, shortestPathLengthAgent8.intValue());

        Integer shortestPathLengthAgent9 = dijkstraShortestPath.getShortestPathLength(this.agent9);
        assertNull(shortestPathLengthAgent9);
    }

}
