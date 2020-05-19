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

import org.apache.commons.math3.util.Precision;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.BurgerBuskens;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * Tests for {@link BurgerBuskens} class.
 *
 * @author Hendrik Nunner
 */
public class NunnerBuskensTest {

    // networks
    private Network network;

    // constants
    private static final double b1      = 1;
    private static final double c1      = 0.2;
    private static final double c2      = 0.1;
    private static final double b2      = 0.5;
    private static final double alpha   = 0.3;
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
    private Agent agent6;
    private Agent agent7;
    private Agent agent8;
    private Agent agent9;
    private Agent agent10;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        // y local
        this.network = new Network("NunnerBuskens Test");
        UtilityFunction ufYLocal = new NunnerBuskens(b1, b2, alpha, c1, c2);

        // agetns
        this.agent1  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent2  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent3  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent4  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent5  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent6  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent7  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent8  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent9  = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);
        this.agent10 = this.network.addAgent(ufYLocal, ds, rSigma, rPi, phi, omega);

        // connections
        this.agent1.addConnection(this.agent2);
        this.agent1.addConnection(this.agent3);
        this.agent1.addConnection(this.agent4);
        this.agent1.addConnection(this.agent6);
        this.agent1.addConnection(this.agent7);
        this.agent1.addConnection(this.agent8);
        this.agent2.addConnection(this.agent3);
        this.agent3.addConnection(this.agent4);
        this.agent3.addConnection(this.agent5);
        this.agent4.addConnection(this.agent5);
        this.agent5.addConnection(this.agent6);
        this.agent6.addConnection(this.agent7);
        this.agent7.addConnection(this.agent8);
        this.agent8.addConnection(this.agent9);
        this.agent8.addConnection(this.agent10);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {
        assertEquals(1.7, Precision.round(this.agent1.getUtility().getOverallUtility(), 1), 0);
        assertEquals(1.8, Precision.round(this.agent3.getUtility().getOverallUtility(), 1), 0);
        assertEquals(1.9, Precision.round(this.agent8.getUtility().getOverallUtility(), 1), 0);
    }

}
