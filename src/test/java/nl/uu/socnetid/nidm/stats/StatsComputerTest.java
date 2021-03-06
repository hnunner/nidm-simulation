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
import static org.junit.Assert.assertFalse;

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
public class StatsComputerTest {

    // graph
    private Network network;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;
    private Agent agent6;
    private Agent agent7;

    //
    private static final double phi   = 0.4;
    private static final double omega = 0.0;
    // disease
    private static final int    tau   = 10;
    private static final double s     = 8.4;
    private static final double gamma = 0.1;
    private static final double mu    = 2.5;
    private DiseaseSpecs ds;


    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        this.network = new Network("StatsComputer Test");

        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        // sigma: risk neutral, pi: risk seeking
        this.agent1 = this.network.addAgent(uf, ds, 1.0, 0.9, phi, omega);
        this.agent2 = this.network.addAgent(uf, ds, 1.0, 0.33, phi, omega);
        // sigma: risk seeking, pi: risk averse
        this.agent3 = this.network.addAgent(uf, ds, 0.25, 1.5, phi, omega);
        this.agent4 = this.network.addAgent(uf, ds, 0.8, 1.4, phi, omega);
        this.agent5 = this.network.addAgent(uf, ds, 0.9, 1.3, phi, omega);
        // sigma: risk averse; pi: risk neutral
        this.agent6 = this.network.addAgent(uf, ds, 1.5, 1.0, phi, omega);
        this.agent7 = this.network.addAgent(uf, ds, 2.0, 1.0, phi, omega);

        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent2.addConnection(this.agent4);
        this.agent5.addConnection(this.agent6);
    }


    /**
     * Test of closeness computation.
     */
    @Test
    public void testComputeCloseness() {
        assertEquals(0.5, StatsComputer.computeCloseness(this.agent1), 0.01);
        assertEquals(0.47, StatsComputer.computeCloseness(this.agent4), 0.01);
        assertEquals(0.17, StatsComputer.computeCloseness(this.agent5), 0.01);
        assertEquals(0, StatsComputer.computeCloseness(this.agent7), 0.01);
    }

    /**
     * Test of first order degree.
     */
    @Test
    public void testFirstOrderDegree() {
        assertEquals(3, StatsComputer.computeFirstOrderDegree(this.agent1));
        assertEquals(2, StatsComputer.computeFirstOrderDegree(this.agent2));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.agent3));
        assertEquals(2, StatsComputer.computeFirstOrderDegree(this.agent4));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.agent5));
        assertEquals(1, StatsComputer.computeFirstOrderDegree(this.agent6));
    }

    /**
     * Test of second order degree.
     */
    @Test
    public void testSecondOrderDegree() {
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.agent1));
        assertEquals(1, StatsComputer.computeSecondOrderDegree(this.agent2));
        assertEquals(2, StatsComputer.computeSecondOrderDegree(this.agent3));
        assertEquals(1, StatsComputer.computeSecondOrderDegree(this.agent4));
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.agent5));
        assertEquals(0, StatsComputer.computeSecondOrderDegree(this.agent6));
    }

    /**
     * Test of global agent stats computation.
     */
    @Test
    public void testComputeGlobalAgentStats() {
        // 1 susceptible (agent1)
        // 4 infected
        network.toggleInfection(this.agent2.getId(), this.ds);
        network.toggleInfection(this.agent3.getId(), this.ds);
        network.toggleInfection(this.agent4.getId(), this.ds);
        network.toggleInfection(this.agent5.getId(), this.ds);
        // 2 recovered
        network.toggleInfection(this.agent6.getId(), this.ds);
        network.toggleInfection(this.agent6.getId(), this.ds);
        network.toggleInfection(this.agent7.getId(), this.ds);
        network.toggleInfection(this.agent7.getId(), this.ds);

        GlobalAgentStats gas = StatsComputer.computeGlobalAgentStats(this.network);
        assertEquals(7, gas.getN());
        assertEquals(1, gas.getnS());
        assertEquals(4, gas.getnI());
        assertEquals(2, gas.getnR());
        assertEquals(2, gas.getnRSigmaNeutral());
        assertEquals(3, gas.getnRSigmaSeeking());
        assertEquals(2, gas.getnRSigmaAverse());
        assertEquals(1.064, gas.getAvRSigma(), 0.001);
        assertEquals(2, gas.getnRPiNeutral());
        assertEquals(2, gas.getnRPiSeeking());
        assertEquals(3, gas.getnRPiAverse());
        assertEquals(1.061, gas.getAvRPi(), 0.001);
    }

    /**
     * Test of global network stats computation.
     */
    @Test
    public void testComputeGlobalNetworkStats() {
        GlobalNetworkStats gns = StatsComputer.computeGlobalNetworkStats(this.network);
        assertFalse(gns.isStable());
        assertEquals(10.0/this.network.getAgents().size(), gns.getAvDegree(), 0.001);
        assertEquals(5, gns.getConnections());
        // not implemented:
        // gns.getDiameter();
        // gns.getAvDistance();
    }

    /**
     * Test of local agent connections stats computation.
     */
    @Test
    public void testComputeLocalAgentConnectionsStats() {
        network.toggleInfection(this.agent3.getId(), this.ds);
        network.toggleInfection(this.agent4.getId(), this.ds);
        network.toggleInfection(this.agent4.getId(), this.ds);

        LocalAgentConnectionsStats lacs = StatsComputer.computeLocalAgentConnectionsStats(this.agent1);
        assertEquals(3, lacs.getN());
        assertEquals(0, lacs.getM());
        assertEquals(1, lacs.getnS());
        assertEquals(1, lacs.getnI());
        assertEquals(1, lacs.getnR());
        assertEquals(1, lacs.getZ());

        lacs = StatsComputer.computeLocalAgentConnectionsStats(this.agent3);
        assertEquals(1, lacs.getN());
        assertEquals(2, lacs.getM());
        assertEquals(1, lacs.getnS());
        assertEquals(0, lacs.getnI());
        assertEquals(0, lacs.getnR());
        assertEquals(0, lacs.getZ());
    }

    /**
     * Test of local agent connections stats computation.
     */
    @Test
    public void testComputeProbabilityOfInfection() {
        // 2 infected with a transmission rate of 0.1 each
        network.toggleInfection(this.agent2.getId(), this.ds);
        network.toggleInfection(this.agent3.getId(), this.ds);

        int infectedConnections = StatsComputer.computeLocalAgentConnectionsStats(this.agent1).getnI();
        assertEquals(0.19, StatsComputer.computeProbabilityOfInfection(agent1, infectedConnections), 0.001);
    }

}
