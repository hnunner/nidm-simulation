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
package nl.uu.socnetid.nidm.agents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * Test cases for the {@link Agent} class.
 *
 * @author Hendrik Nunner
 */
public class AgentTest {

    // GENERAL TESTS
    // network
    private Network network;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;

    // disease related
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;

    private static final double phi     = 0.4;
    private static final double omega   = 1.0;

    private UtilityFunction uf;
    private DiseaseSpecs ds;


    // ASSORTATIVITY TESTS
    // networks
    private Network networkAss;
    private List<Agent> agentsAss;
    private Agent agentAss;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initNetwork() {

        this.network = new Network("Agent Test");

        this.uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        double r1 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent1 = this.network.addAgent(uf, this.ds, r1, r1, phi, omega);
        double r2 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent2 = this.network.addAgent(uf, this.ds, r2, r2, phi, omega);
        double r3 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent3 = this.network.addAgent(uf, this.ds, r3, r3, phi, omega);
        double r4 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent4 = this.network.addAgent(uf, this.ds, r4, r4, phi, omega);
        double r5 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent5 = this.network.addAgent(uf, this.ds, r5, r5, phi, omega);
        double r6 = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
        this.agent6 = this.network.addAgent(uf, this.ds, r6, r6, phi, omega);

        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent3.addConnection(this.agent4);

        this.agent1.setXY(0, 0);
        this.agent2.setXY(1, 1);
        this.agent3.setXY(3, 1);


        this.networkAss = new Network("Assortativity conditions test",
                Arrays.asList(AssortativityConditions.PROFESSION, AssortativityConditions.AGE));
        this.agentsAss = new ArrayList<Agent>(11);
        this.agentAss = networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 90, true, "A", true, false);

        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 80, true, "B", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 70, true, "A", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 91, true, "C", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 30, true, "A", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5,  5, true, "C", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 50, true, "B", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 95, true, "C", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 20, true, "B", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 10, true, "A", true, false));
        this.agentsAss.add(networkAss.addAgent(uf, ds, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5, 60, true, "C", true, false));
    }


	/**
	 * Test of id coherence.
	 */
	@Test
	public void testIds() {
		assertNotEquals(this.agent1.getId(), this.agent2.getId());
		assertNotEquals(this.agent1.getId(), this.agent3.getId());
		assertNotEquals(this.agent1.getId(), this.agent4.getId());
        assertNotEquals(this.agent1.getId(), this.agent5.getId());
        assertNotEquals(this.agent1.getId(), this.agent6.getId());

        assertNotEquals(this.agent2.getId(), this.agent3.getId());
        assertNotEquals(this.agent2.getId(), this.agent4.getId());
        assertNotEquals(this.agent2.getId(), this.agent5.getId());
        assertNotEquals(this.agent2.getId(), this.agent6.getId());

		assertNotEquals(this.agent3.getId(), this.agent4.getId());
        assertNotEquals(this.agent3.getId(), this.agent5.getId());
        assertNotEquals(this.agent3.getId(), this.agent6.getId());

        assertNotEquals(this.agent4.getId(), this.agent5.getId());
        assertNotEquals(this.agent4.getId(), this.agent6.getId());
        assertNotEquals(this.agent5.getId(), this.agent6.getId());
	}

    /**
     * Test of adding a connection.
     */
    @Test
    public void testAddConnection() {
        assertEquals(3, this.agent1.getConnections().size());
        this.agent1.addConnection(agent5);
        assertEquals(4, this.agent1.getConnections().size());
        assertEquals(0, this.agent6.getConnections().size());
        this.agent6.addConnection(agent1);
        assertEquals(5, this.agent1.getConnections().size());
        assertEquals(1, this.agent6.getConnections().size());
    }

    /**
     * Test of removing a connection.
     */
    @Test
    public void testRemoveConnection() {
        assertEquals(3, this.agent1.getConnections().size());
        assertEquals(1, this.agent2.getConnections().size());
        this.agent1.removeConnection(this.agent2);
        assertEquals(2, this.agent1.getConnections().size());
        assertEquals(0, this.agent2.getConnections().size());
    }

	/**
     * Test of accepting a connection to another agent.
     */
    @Test
    public void testAcceptConnection() {
        // NOTE: test whether agents are capable of accepting connection requests,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        assertTrue(this.agent2.acceptConnection(this.agent4));
        assertTrue(this.agent6.acceptConnection(this.agent5));
    }

    /**
     * Test of getting the co-agents.
     */
    @Test
    public void testGetCoagents() {
        assertEquals(5, this.agent1.getCoAgents().size());
        assertEquals(5, this.agent2.getCoAgents().size());
        assertEquals(5, this.agent3.getCoAgents().size());
        assertEquals(5, this.agent4.getCoAgents().size());
        assertEquals(5, this.agent5.getCoAgents().size());
        assertEquals(5, this.agent6.getCoAgents().size());
    }

    /**
     * Test of getting the connections.
     */
    @Test
    public void testGetConnections() {
        assertEquals(3, this.agent1.getConnections().size());
        assertEquals(1, this.agent2.getConnections().size());
        assertEquals(this.agent1, this.agent2.getConnections().iterator().next());
        assertEquals(2, this.agent3.getConnections().size());
        assertEquals(2, this.agent4.getConnections().size());
        assertEquals(0, this.agent5.getConnections().size());
        assertEquals(0, this.agent6.getConnections().size());
    }

    /**
     * Test of getting the disease group.
     */
    @Test
    public void testGetDiseaseGroup() {
        assertEquals(DiseaseGroup.SUSCEPTIBLE, this.agent1.getDiseaseGroup());
        this.agent1.infect(this.ds);
        assertEquals(DiseaseGroup.INFECTED, this.agent1.getDiseaseGroup());
        this.agent1.cure();
        assertEquals(DiseaseGroup.RECOVERED, this.agent1.getDiseaseGroup());
        this.agent1.makeSusceptible();
        assertEquals(DiseaseGroup.SUSCEPTIBLE, this.agent1.getDiseaseGroup());
    }

    /**
     * Test of getting the disease specs.
     */
    @Test
    public void testGetDiseaseSpecs() {
        assertNotNull(this.agent1.getDiseaseSpecs());
        assertEquals(s, this.agent1.getDiseaseSpecs().getSigma(), 0.0);
        assertNotNull(this.agent2.getDiseaseSpecs());
        assertEquals(mu, this.agent2.getDiseaseSpecs().getMu(), 0.0);
        assertNotNull(this.agent5.getDiseaseSpecs());
        assertEquals(tau, this.agent5.getDiseaseSpecs().getTau(), 0);
        assertNotNull(this.agent6.getDiseaseSpecs());
        assertEquals(gamma, this.agent6.getDiseaseSpecs().getGamma(), 0.0);
    }

    /**
     * Test of getting the utility.
     */
    @Test
    public void testGetUtility() {
        // NOTE: test whether agents are capable of computing utilites,
        // more elaborated tests regarding utilities can be found within the tests of specific utility functions
        assertNotNull(this.agent1.getUtility());
        assertEquals(3, this.agent1.getUtility().getOverallUtility(), 0.0);
        assertEquals(2, this.agent2.getUtility().getOverallUtility(), 0.0);
        assertEquals(2.5, this.agent4.getUtility().getOverallUtility(), 0.0);
        assertEquals(0, this.agent5.getUtility().getOverallUtility(), 0.0);
    }

    /**
     * Test whether a agent is being successfully infected with a valid disease.
     */
    @Test
    public void testValidInfect() {
        assertTrue(this.agent1.isSusceptible());
        DiseaseSpecs dsValid = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);
        this.agent1.infect(dsValid);
        assertTrue(this.agent1.isInfected());
    }

    /**
     * Test whether an exception is thrown when a agent is being infected with an invalid disease.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidInfect() {
        DiseaseSpecs dsInvalid = new DiseaseSpecs(DiseaseType.SIR, tau+1, s, gamma, mu);
        this.agent1.infect(dsInvalid);
    }

    /**
     * Test whether an returns the correct number of network decisions.
     */
    @Test
    @Ignore
    public void testOldGetNumberOfNetworkDecisions() {
        Network network = new Network("Network Network Decisions Test");

        // f(5) = 2.28
        Agent agent = network.addAgent(uf, ds);
        for (int i = 0; i < 4; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(5, network.getN());
        assertEquals(2, agent.getNumberOfNetworkDecisions());

        // f(10) = 3.58
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(10, network.getN());
        assertEquals(4, agent.getNumberOfNetworkDecisions());

        // f(15) = 4.62
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(15, network.getN());
        assertEquals(5, agent.getNumberOfNetworkDecisions());

        // f(20) = 5.49
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(20, network.getN());
        assertEquals(5, agent.getNumberOfNetworkDecisions());

        // f(25) = 6.25
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(25, network.getN());
        assertEquals(6, agent.getNumberOfNetworkDecisions());

        // f(50) = 9.33
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(50, network.getN());
        assertEquals(9, agent.getNumberOfNetworkDecisions());

        // f(75) = 11.89
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(75, network.getN());
        assertEquals(12, agent.getNumberOfNetworkDecisions());

        // f(100) = 14.13
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(100, network.getN());
        assertEquals(14, agent.getNumberOfNetworkDecisions());
    }

    /**
     * Test whether an returns the correct number of network decisions.
     */
    @Test
    public void testGetNumberOfNetworkDecisions() {
        Network network = new Network("Network Network Decisions Test");

        double phi = 0.4;

        // f(5) = 1
        Agent agent = network.addAgent(uf, ds);
        for (int i = 0; i < 4; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(5, network.getN());
        double evalShare = (network.getN()-1)*phi;
        int exp = (int) Math.round(evalShare);
        assertEquals(exp, agent.getNumberOfNetworkDecisions());

        // f(10) = 2
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(10, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(15) = 3
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(15, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(20) = 4
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(20, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(25) = 5
        for (int i = 0; i < 5; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(25, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(50) = 10
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(50, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(75) = 15
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(75, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

        // f(100) = 20
        for (int i = 0; i < 25; i++) {
            network.addAgent(uf, ds);
        }
        assertEquals(100, network.getN());
        evalShare = (network.getN()-1)*phi;
        exp = (int) Math.round(evalShare);
        assertEquals((int) Math.round((network.getN()-1)*phi), agent.getNumberOfNetworkDecisions());

    }

    /**
     * Test whether a new connection is considered valuable.
     */
    @Test
    public void testNewConnectionValuable() {
        assertTrue(this.agent1.newConnectionValuable(this.agent6));

        Network networkCostly = new Network("Costly Connections Test");
        UtilityFunction ufCostly = new Cumulative(-1.0, 2.0);
        Agent agentCostly1 = networkCostly.addAgent(ufCostly, this.ds);
        Agent agentCostly2 = networkCostly.addAgent(ufCostly, this.ds);
        assertFalse(agentCostly1.newConnectionValuable(agentCostly2));
    }

    /**
     * Test whether an existing connection is considered too costly.
     */
    @Test
    public void testExistingConnectionTooCostly() {
        assertFalse(this.agent1.existingConnectionTooCostly(this.agent2));

        Network networkCostly = new Network("Costly Connections Test");
        UtilityFunction ufCostly = new Cumulative(-1.0, 2.0);
        Agent agentCostly1 = networkCostly.addAgent(ufCostly, this.ds);
        Agent agentCostly2 = networkCostly.addAgent(ufCostly, this.ds);
        agentCostly1.addConnection(agentCostly2);
        assertTrue(agentCostly1.existingConnectionTooCostly(agentCostly2));
    }

    /**
     * Test whether the correct amount of random co-agents is retrieved.
     */
    //@Test
    // TODO fix now that the list is randomized
    public void testGetRandomCoagents() {

        for (int i = 1; i < this.network.getAgents().size(); i++) {

            Collection<Agent> randomListOfCoagents = this.agent1.getRandomListOfCoAgents(i);
            assertEquals(i, randomListOfCoagents.size());

            Iterator<Agent> it = randomListOfCoagents.iterator();
            double prevDiff = 0;
            while (it.hasNext()) {
                Agent coAgent = it.next();
                double currDiff = Math.abs((this.agent1.getRPi() + this.agent1.getRSigma()) -
                        (coAgent.getRPi() + coAgent.getRSigma()));
                assertTrue(prevDiff <= currDiff);
                prevDiff = currDiff;
            }
        }
    }

    /**
     * Test whether connections exist between agents.
     */
    @Test
    public void testHasDirectConnectionTo() {
        assertTrue(this.agent1.isDirectlyConnectedTo(this.agent2));
        assertTrue(this.agent1.isDirectlyConnectedTo(this.agent3));
        assertTrue(this.agent1.isDirectlyConnectedTo(this.agent4));
        assertFalse(this.agent1.isDirectlyConnectedTo(this.agent5));
        assertFalse(this.agent1.isDirectlyConnectedTo(this.agent6));

        assertTrue(this.agent2.isDirectlyConnectedTo(this.agent1));
        assertFalse(this.agent2.isDirectlyConnectedTo(this.agent3));
        assertFalse(this.agent2.isDirectlyConnectedTo(this.agent4));
        assertFalse(this.agent2.isDirectlyConnectedTo(this.agent5));
        assertFalse(this.agent2.isDirectlyConnectedTo(this.agent6));

        assertTrue(this.agent3.isDirectlyConnectedTo(this.agent1));
        assertFalse(this.agent3.isDirectlyConnectedTo(this.agent2));
        assertTrue(this.agent3.isDirectlyConnectedTo(this.agent4));
        assertFalse(this.agent3.isDirectlyConnectedTo(this.agent5));
        assertFalse(this.agent3.isDirectlyConnectedTo(this.agent6));

        assertTrue(this.agent4.isDirectlyConnectedTo(this.agent1));
        assertFalse(this.agent4.isDirectlyConnectedTo(this.agent2));
        assertTrue(this.agent4.isDirectlyConnectedTo(this.agent3));
        assertFalse(this.agent4.isDirectlyConnectedTo(this.agent5));
        assertFalse(this.agent4.isDirectlyConnectedTo(this.agent6));

        assertFalse(this.agent5.isDirectlyConnectedTo(this.agent1));
        assertFalse(this.agent5.isDirectlyConnectedTo(this.agent2));
        assertFalse(this.agent5.isDirectlyConnectedTo(this.agent3));
        assertFalse(this.agent5.isDirectlyConnectedTo(this.agent4));
        assertFalse(this.agent5.isDirectlyConnectedTo(this.agent6));

        assertFalse(this.agent6.isDirectlyConnectedTo(this.agent1));
        assertFalse(this.agent6.isDirectlyConnectedTo(this.agent2));
        assertFalse(this.agent6.isDirectlyConnectedTo(this.agent3));
        assertFalse(this.agent6.isDirectlyConnectedTo(this.agent4));
        assertFalse(this.agent6.isDirectlyConnectedTo(this.agent5));
    }

    /**
     * Test whether connections exist between agents.
     */
    @Test
    public void testHasConnectionTo() {
        assertTrue(this.agent2.isSomehowConnectedTo(this.agent1));
        assertTrue(this.agent2.isSomehowConnectedTo(this.agent3));
        assertTrue(this.agent2.isSomehowConnectedTo(this.agent4));
        assertFalse(this.agent2.isSomehowConnectedTo(this.agent5));
        assertFalse(this.agent2.isSomehowConnectedTo(this.agent6));
    }

    /**
     * Test whether closeness is computed correctly.
     */
    @Test
    public void testGetCloseness() {
        assertEquals(0.6, this.agent1.getCloseness(1), 0.01);
        assertEquals(0.52, this.agent2.getCloseness(1), 0.01);
        assertEquals(0.56, this.agent3.getCloseness(1), 0.01);
        assertEquals(0.56, this.agent4.getCloseness(1), 0.01);
        assertEquals(0, this.agent5.getCloseness(1), 0.01);
        assertEquals(0, this.agent6.getCloseness(1), 0.01);
    }

    /**
     * Test whether geodesic distance is computed correctly.
     */
    @Test
    public void testGetGeodesicDistance() {
        assertEquals(1, this.agent4.getGeodesicDistanceTo(this.agent1), 0);
        assertEquals(2, this.agent4.getGeodesicDistanceTo(this.agent2), 0);
        assertNull(this.agent4.getGeodesicDistanceTo(this.agent5));
    }

    /**
     * Test whether geographic distance is computed correctly.
     */
    @Test
    public void testGetGeographicDistance() {
        assertEquals(1.41, this.agent1.getGeographicDistanceTo(this.agent2), 0.01);
        assertEquals(3.16, this.agent1.getGeographicDistanceTo(this.agent3), 0.01);
        assertEquals(2, this.agent2.getGeographicDistanceTo(this.agent3), 0.01);
    }

    /**
     * Test whether betweenness is computed correctly.
     */
    @Test
    public void testGetBetweenness() {
        assertEquals(4, this.agent1.getBetweenness(1), 0.01);
        assertEquals(0, this.agent2.getBetweenness(1), 0.01);
        assertEquals(0, this.agent3.getBetweenness(1), 0.01);
        assertEquals(0, this.agent4.getBetweenness(1), 0.01);
        assertEquals(0, this.agent5.getBetweenness(1), 0.01);
        assertEquals(0, this.agent6.getBetweenness(1), 0.01);
    }

    /**
     * Test whether normalized betweenness is computed correctly.
     */
    @Test
    public void testGetNormalizedBetweenness() {
        assertEquals(0.4, this.agent1.getBetweennessNormalized(1), 0.01);
        assertEquals(0, this.agent2.getBetweennessNormalized(1), 0.01);
        assertEquals(0, this.agent3.getBetweennessNormalized(1), 0.01);
        assertEquals(0, this.agent4.getBetweennessNormalized(1), 0.01);
        assertEquals(0, this.agent5.getBetweennessNormalized(1), 0.01);
        assertEquals(0, this.agent6.getBetweennessNormalized(1), 0.01);
    }

    /**
     * Test whether agents are sorted by assortativity conditions.
     */
    @Test
    public void testSortByProfessionAndAge() {

        agentAss.sortByAssortativityConditions(agentsAss);

        assertEquals("A", agentsAss.get(0).getProfession());
        assertEquals(70, agentsAss.get(0).getAge());

        assertEquals("A", agentsAss.get(1).getProfession());
        assertEquals(30, agentsAss.get(1).getAge());

        assertEquals("A", agentsAss.get(2).getProfession());
        assertEquals(10, agentsAss.get(2).getAge());

        assertEquals("C", agentsAss.get(3).getProfession());
        assertEquals(91, agentsAss.get(3).getAge());

        assertEquals("C", agentsAss.get(4).getProfession());
        assertEquals(95, agentsAss.get(4).getAge());

        assertEquals("B", agentsAss.get(5).getProfession());
        assertEquals(80, agentsAss.get(5).getAge());

        assertEquals("C", agentsAss.get(6).getProfession());
        assertEquals(60, agentsAss.get(6).getAge());

        assertEquals("B", agentsAss.get(7).getProfession());
        assertEquals(50, agentsAss.get(7).getAge());

        assertEquals("B", agentsAss.get(8).getProfession());
        assertEquals(20, agentsAss.get(8).getAge());

        assertEquals("C", agentsAss.get(9).getProfession());
        assertEquals(5, agentsAss.get(9).getAge());

    }

}
