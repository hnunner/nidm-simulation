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
package nl.uu.socnetid.nidm.networks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * Test cases for the {@link Network} class.
 *
 * @author Hendrik Nunner
 */
public class NetworkTest {

    // network
    private Network network;
    private Network ringArrangedNetwork;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;
    private Agent ringAgent1;
    private Agent ringAgent2;
    private Agent ringAgent3;
    private Agent ringAgent4;
    private Agent ringAgent5;
    private Agent ringAgent6;

    // utility
    private UtilityFunction uf;

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


	/**
	 * Performed before each test: Initialization of the network.
	 */
	@Before
	public void initNetwork() {
        this.network = new Network("Network Test");

        this.uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1 = this.network.addAgent(uf, ds);
        this.agent2 = this.network.addAgent(uf, ds);
        this.agent3 = this.network.addAgent(uf, ds);
        this.agent4 = this.network.addAgent(uf, ds);
        this.agent5 = this.network.addAgent(uf, ds);
        this.agent6 = this.network.addAgent(uf, ds);

        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent3.addConnection(this.agent4);

        this.agent6.infect(ds);

        // ring arranged network
        this.ringArrangedNetwork = new Network("Ring arranged Network Test", true);
        this.ringAgent1 = this.ringArrangedNetwork.addAgent(uf, ds);
        this.ringAgent2 = this.ringArrangedNetwork.addAgent(uf, ds);
        this.ringAgent3 = this.ringArrangedNetwork.addAgent(uf, ds);
        this.ringAgent4 = this.ringArrangedNetwork.addAgent(uf, ds);
        this.ringAgent5 = this.ringArrangedNetwork.addAgent(uf, ds);
        this.ringAgent6 = this.ringArrangedNetwork.addAgent(uf, ds);
	}


    /**
     * Test of adding an agent.
     */
    @Test
    public void testAddAgent() {
        assertEquals(6, this.network.getAgents().size());
        this.network.addAgent(this.uf, this.ds);
        assertEquals(7, this.network.getAgents().size());
    }

    /**
     * Test of removing an agent.
     */
    @Test
    public void testRemoveAgent() {
        assertEquals(6, this.network.getAgents().size());
        this.network.removeAgent();
        assertEquals(5, this.network.getAgents().size());
    }

    /**
     * Test of removing all connections.
     */
    @Test
    public void testClearConnections() {
        assertEquals(4, this.network.getEdgeCount());
        this.network.clearConnections();
        assertEquals(0, this.network.getEdgeCount());
    }

    /**
     * Test of creating a full network with connection between all agents.
     */
    @Test
    public void testCreateFullNetwork() {
        assertEquals(4, this.network.getEdgeCount());
        this.network.createFullNetwork();
        assertEquals(15, this.network.getEdgeCount());
    }

    /**
     * Test of getting all agents.
     */
    @Test
    public void testGetAgents() {
        assertEquals(6, this.network.getAgents().size());
    }

    /**
     * Test of infecting a random agent.
     */
    @Test
    public void testInfectRandomAgent() {
        Iterator<Agent> agentIt = this.network.getAgentIterator();
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            assertTrue(!agent.isInfected() || agent.equals(this.agent6));
        }
        this.network.infectRandomAgent(this.ds);

        agentIt = this.network.getAgentIterator();
        int infected = 0;
        while (agentIt.hasNext()) {
            Agent agent = agentIt.next();
            if (agent.isInfected()) {
                infected++;
            }
        }
        assertEquals(2, infected);
    }

    /**
     * Test of resetting all agents.
     */
    @Test
    public void testResetAgents() {
        assertEquals(4, this.network.getEdgeCount());
        assertTrue(this.agent1.isSusceptible());
        assertTrue(this.agent6.isInfected());
        this.network.resetAgents();
        assertEquals(0, this.network.getEdgeCount());
        assertTrue(this.agent1.isSusceptible());
        assertTrue(this.agent6.isSusceptible());
    }

    /**
     * Test of toggling infections.
     */
    @Test
    public void testToggleInfection() {
        assertTrue(this.agent5.isSusceptible());
        this.network.toggleInfection(this.agent5.getId(), this.ds);
        assertTrue(this.agent5.isInfected());
        this.network.toggleInfection(this.agent5.getId(), this.ds);
        assertTrue(this.agent5.isRecovered());
        this.network.toggleInfection(this.agent5.getId(), this.ds);
        assertTrue(this.agent5.isSusceptible());
    }

	/**
     * Test of removing a connection.
     */
    @Test
    public void testGetRandomAgent() {
        List<Agent> agents = new LinkedList<Agent>(this.network.getAgents());
        for (int i = 0; i < 100; i++) {
            Agent randomAgent = this.network.getRandomAgent();
            assertNotNull(randomAgent);
            agents.remove(randomAgent);
        }
        assertTrue(agents.isEmpty());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testGetRandomNotInfectedAgent() {
        List<Agent> agents = new LinkedList<Agent>(this.network.getAgents());
        for (int i = 0; i < 100; i++) {
            Agent randomAgent = this.network.getRandomNotInfectedAgent();
            agents.remove(randomAgent);
        }
        assertTrue(agents.size() == 1);
        assertEquals(this.agent6, agents.get(0));
    }

    /**
     * Test of getting empty network type.
     */
    @Test
    public void testEmpty() {

        Network empty = new Network("Empty Network");
        this.agent1 = empty.addAgent(uf, ds);
        this.agent2 = empty.addAgent(uf, ds);
        this.agent3 = empty.addAgent(uf, ds);
        this.agent4 = empty.addAgent(uf, ds);
        this.agent5 = empty.addAgent(uf, ds);
        this.agent6 = empty.addAgent(uf, ds);
        assertEquals(NetworkTypes.EMPTY, empty.getType());
    }

    /**
     * Test of getting full network type.
     */
    @Test
    public void testFull() {

        Network full = new Network("Full Network");
        this.agent1 = full.addAgent(uf, ds);
        this.agent2 = full.addAgent(uf, ds);
        this.agent3 = full.addAgent(uf, ds);
        this.agent4 = full.addAgent(uf, ds);
        this.agent5 = full.addAgent(uf, ds);
        this.agent6 = full.addAgent(uf, ds);
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent1.addConnection(this.agent5);
        this.agent1.addConnection(this.agent6);
        this.agent2.addConnection(this.agent3);
        this.agent2.addConnection(this.agent4);
        this.agent2.addConnection(this.agent5);
        this.agent2.addConnection(this.agent6);
        this.agent3.addConnection(this.agent4);
        this.agent3.addConnection(this.agent5);
        this.agent3.addConnection(this.agent6);
        this.agent4.addConnection(this.agent5);
        this.agent4.addConnection(this.agent6);
        this.agent5.addConnection(this.agent6);
        assertEquals(NetworkTypes.FULL, full.getType());
    }

    /**
     * Test of getting ring network type.
     */
    @Test
    public void testRing() {

        Network ring = new Network("Ring Network");
        this.agent1 = ring.addAgent(uf, ds);
        this.agent2 = ring.addAgent(uf, ds);
        this.agent3 = ring.addAgent(uf, ds);
        this.agent4 = ring.addAgent(uf, ds);
        this.agent5 = ring.addAgent(uf, ds);
        this.agent6 = ring.addAgent(uf, ds);
        this.agent1.addConnection(this.agent2);
        this.agent2.addConnection(this.agent3);
        this.agent3.addConnection(this.agent4);
        this.agent4.addConnection(this.agent5);
        this.agent5.addConnection(this.agent6);
        this.agent6.addConnection(this.agent1);
        assertEquals(NetworkTypes.RING, ring.getType());
    }

    /**
     * Test of getting two rings network type.
     */
    @Test
    public void testTwoRings() {

        Network twoRings = new Network("Two Rings Network");
        this.agent1 = twoRings.addAgent(uf, ds);
        this.agent2 = twoRings.addAgent(uf, ds);
        this.agent3 = twoRings.addAgent(uf, ds);
        this.agent4 = twoRings.addAgent(uf, ds);
        this.agent5 = twoRings.addAgent(uf, ds);
        this.agent6 = twoRings.addAgent(uf, ds);
        this.agent1.addConnection(this.agent2);
        this.agent2.addConnection(this.agent3);
        this.agent3.addConnection(this.agent1);
        this.agent4.addConnection(this.agent5);
        this.agent5.addConnection(this.agent6);
        this.agent6.addConnection(this.agent4);
        assertEquals(NetworkTypes.UNDEFINED, twoRings.getType());
    }

    /**
     * Test of getting star network type.
     */
    @Test
    public void testStar() {

        Network star = new Network("Star Network");
        this.agent1 = star.addAgent(uf, ds);
        this.agent2 = star.addAgent(uf, ds);
        this.agent3 = star.addAgent(uf, ds);
        this.agent4 = star.addAgent(uf, ds);
        this.agent5 = star.addAgent(uf, ds);
        this.agent6 = star.addAgent(uf, ds);
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent1.addConnection(this.agent5);
        this.agent1.addConnection(this.agent6);
        assertEquals(NetworkTypes.STAR, star.getType());
    }

    /**
     * Test of getting incomplete star network type.
     */
    @Test
    public void testIncompleteStar() {

        Network incompleteStar = new Network("Incomplete Star Network");
        this.agent1 = incompleteStar.addAgent(uf, ds);
        this.agent2 = incompleteStar.addAgent(uf, ds);
        this.agent3 = incompleteStar.addAgent(uf, ds);
        this.agent4 = incompleteStar.addAgent(uf, ds);
        this.agent5 = incompleteStar.addAgent(uf, ds);
        this.agent6 = incompleteStar.addAgent(uf, ds);
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent1.addConnection(this.agent5);
        assertEquals(NetworkTypes.UNDEFINED, incompleteStar.getType());

    }

    /**
     * Test whether the average degree is computed correctly.
     */
    @Test
    public void testGetAvDegree() {
        assertEquals(6, this.network.getN());
        assertEquals(1.33, this.network.getAvDegree(1), 0.01);
    }

    /**
     * Test whether the average degree at distance 2 is computed correctly.
     */
    @Test
    public void testGetAvDegree2() {
        assertEquals(6, this.network.getN());
        assertEquals(0.66, this.network.getAvDegree2(), 0.01);
    }

    /**
     * Test whether the average closeness is computed correctly.
     */
    @Test
    public void testGetAvCloseness() {
        assertEquals(6, this.network.getN());
        assertEquals(0.37, this.network.getAvCloseness(1), 0.01);
    }

    /**
     * Test whether average path length is computed correctly.
     */
    //@Test TODO redo with connected graph
    public void testGetAvPathLength() {
        assertEquals(6, this.network.getN());
        assertEquals(0.53, Precision.round(this.network.getAvPathLength(1), 2), 0);
    }

    /**
     * Test whether ring network is arranged correctly.
     */
    @Test
    public void testRingArrangedNetwork() {
        Iterator<Agent> aIt = this.network.getAgentIterator();
        while (aIt.hasNext()) {
            Agent agent = aIt.next();
            assertEquals(0, agent.getX(), 0);
            assertEquals(0, agent.getY(), 0);
        }

        assertEquals(1.0, this.ringAgent1.getX(), 0.01);
        assertEquals(0.0, this.ringAgent1.getY(), 0.01);
        assertEquals(0.5, this.ringAgent2.getX(), 0.01);
        assertEquals(0.87, this.ringAgent2.getY(), 0.01);
        assertEquals(-0.5, this.ringAgent3.getX(), 0.01);
        assertEquals(0.87, this.ringAgent3.getY(), 0.01);
        assertEquals(-1.0, this.ringAgent4.getX(), 0.01);
        assertEquals(0.0, this.ringAgent4.getY(), 0.01);
        assertEquals(-0.5, this.ringAgent5.getX(), 0.01);
        assertEquals(-0.87, this.ringAgent5.getY(), 0.01);
        assertEquals(0.5, this.ringAgent6.getX(), 0.01);
        assertEquals(-0.87, this.ringAgent6.getY(), 0.01);

        assertEquals(1, this.ringAgent1.getGeographicDistanceTo(this.ringAgent2), 0.01);
        assertEquals(1, this.ringAgent2.getGeographicDistanceTo(this.ringAgent3), 0.01);
        assertEquals(1, this.ringAgent3.getGeographicDistanceTo(this.ringAgent4), 0.01);
        assertEquals(1, this.ringAgent4.getGeographicDistanceTo(this.ringAgent5), 0.01);
        assertEquals(1, this.ringAgent5.getGeographicDistanceTo(this.ringAgent6), 0.01);
        assertEquals(1, this.ringAgent6.getGeographicDistanceTo(this.ringAgent1), 0.01);
        assertEquals(1.73, this.ringAgent1.getGeographicDistanceTo(this.ringAgent3), 0.01);
        assertEquals(2, this.ringAgent1.getGeographicDistanceTo(this.ringAgent4), 0.01);
        assertEquals(1.73, this.ringAgent1.getGeographicDistanceTo(this.ringAgent5), 0.01);
    }

    /**
     * Tests whether two networks are equal.
     */
    @Test
    public void testEquals() {

        Network network2 = new Network("Network Test - Comparator");

        Agent a1 = network2.addAgent(
                this.agent1.getUtilityFunction(),
                this.agent1.getDiseaseSpecs(),
                this.agent1.getRSigma(),
                this.agent1.getRPi(),
                this.agent1.getPhi(),
                this.agent1.getOmega(),
                this.agent1.getPsi(),
                this.agent1.getXi(),
                this.agent1.getAge(),
                this.agent1.considerAge(),
                this.agent1.getProfession(),
                this.agent1.considerProfession(),
                this.agent1.isQuarantined());
        Agent a2 = network2.addAgent(
                this.agent2.getUtilityFunction(),
                this.agent2.getDiseaseSpecs(),
                this.agent2.getRSigma(),
                this.agent2.getRPi(),
                this.agent2.getPhi(),
                this.agent2.getOmega(),
                this.agent2.getPsi(),
                this.agent2.getXi(),
                this.agent2.getAge(),
                this.agent2.considerAge(),
                this.agent2.getProfession(),
                this.agent2.considerProfession(),
                this.agent1.isQuarantined());
        Agent a3 = network2.addAgent(
                this.agent3.getUtilityFunction(),
                this.agent3.getDiseaseSpecs(),
                this.agent3.getRSigma(),
                this.agent3.getRPi(),
                this.agent3.getPhi(),
                this.agent3.getOmega(),
                this.agent3.getPsi(),
                this.agent3.getXi(),
                this.agent3.getAge(),
                this.agent3.considerAge(),
                this.agent3.getProfession(),
                this.agent3.considerProfession(),
                this.agent1.isQuarantined());
        Agent a4 = network2.addAgent(
                this.agent4.getUtilityFunction(),
                this.agent4.getDiseaseSpecs(),
                this.agent4.getRSigma(),
                this.agent4.getRPi(),
                this.agent4.getPhi(),
                this.agent4.getOmega(),
                this.agent4.getPsi(),
                this.agent4.getXi(),
                this.agent4.getAge(),
                this.agent4.considerAge(),
                this.agent4.getProfession(),
                this.agent4.considerProfession(),
                this.agent1.isQuarantined());
        network2.addAgent(
                this.agent5.getUtilityFunction(),
                this.agent5.getDiseaseSpecs(),
                this.agent5.getRSigma(),
                this.agent5.getRPi(),
                this.agent5.getPhi(),
                this.agent5.getOmega(),
                this.agent5.getPsi(),
                this.agent5.getXi(),
                this.agent5.getAge(),
                this.agent5.considerAge(),
                this.agent5.getProfession(),
                this.agent5.considerProfession(),
                this.agent1.isQuarantined());
        a1.addConnection(a2);
        a1.addConnection(a3);
        a1.addConnection(a4);
        assertFalse(this.network.equals(network2));

        a3.addConnection(a4);
        assertFalse(this.network.equals(network2));

        Agent a6 = network2.addAgent(
                this.agent6.getUtilityFunction(),
                this.agent6.getDiseaseSpecs(),
                this.agent6.getRSigma(),
                this.agent6.getRPi(),
                this.agent6.getPhi(),
                this.agent6.getOmega(),
                this.agent6.getPsi(),
                this.agent6.getXi(),
                this.agent6.getAge(),
                this.agent6.considerAge(),
                this.agent6.getProfession(),
                this.agent6.considerProfession(),
                this.agent1.isQuarantined());
        assertFalse(this.network.equals(network2));

        a6.infect(this.ds);
        assertTrue(this.network.equals(network2));
    }

}
