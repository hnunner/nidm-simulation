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
package nl.uu.socnetid.nidm.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.BurgerBuskens;
import nl.uu.socnetid.nidm.utility.CarayolRoux;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * Tests for {@link BurgerBuskens} class.
 *
 * @author Hendrik Nunner
 */
public class CarayolRouxTest {

    // network
    private Network network;

    // constants
    private static final double crOmega = 1;
    private static final double delta   = 0.5;
    private static final double c       = 1;
    private static final double s       = 8.4;
    private static final double gamma   = 0.1;
    private static final double mu      = 2.5;
    private static final double rSigma  = 1.2;
    private static final double rPi     = 0.5;
    private static final double phi     = 0.4;
    private static final double omega   = 0.0;
    private static final int    tau     = 10;

    // agents
    private Agent agent1;
    private Agent agent2;
    private Agent agent3;
    private Agent agent4;
    private Agent agent5;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        this.network = new Network("CarayolRoux Test");

        UtilityFunction uf = new CarayolRoux(crOmega, delta, c);
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        this.agent1  = this.network.addAgent(uf, ds, rSigma, rPi, phi, omega);
        this.agent2  = this.network.addAgent(uf, ds, rSigma, rPi, phi, omega);
        this.agent3  = this.network.addAgent(uf, ds, rSigma, rPi, phi, omega);
        this.agent4  = this.network.addAgent(uf, ds, rSigma, rPi, phi, omega);
        this.agent5  = this.network.addAgent(uf, ds, rSigma, rPi, phi, omega);

        // infections
        //this.agent1.infect(ds);

        // connections
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent5);

        this.agent2.addConnection(this.agent4);

        this.agent4.addConnection(this.agent5);

        // coordinates
        this.agent1.setXY(0, 0);
        this.agent2.setXY(1, 1);
        this.agent3.setXY(-1, 1);
        this.agent4.setXY(2, 2);
        this.agent5.setXY(2, 0);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(0.14, this.agent1.getUtility().getOverallUtility(), 0.01);
        assertEquals(0.56, this.agent2.getUtility().getOverallUtility(), 0.01);
        assertEquals(0.65, this.agent3.getUtility().getOverallUtility(), 0.01);
        assertEquals(0.24, this.agent4.getUtility().getOverallUtility(), 0.01);
        assertEquals(0.17, this.agent5.getUtility().getOverallUtility(), 0.01);
    }

}
