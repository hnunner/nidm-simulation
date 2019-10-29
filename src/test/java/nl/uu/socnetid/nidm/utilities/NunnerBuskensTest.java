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
    private Network networkYGlobal;
    private Network networkYLocal;

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
    private static final int    tau     = 10;

    // agents
    private Agent agent1YGlobal;
    private Agent agent2YGlobal;
    private Agent agent3YGlobal;
    private Agent agent4YGlobal;
    private Agent agent5YGlobal;
    private Agent agent6YGlobal;
    private Agent agent7YGlobal;
    private Agent agent8YGlobal;
    private Agent agent9YGlobal;
    private Agent agent10YGlobal;
    private Agent agent1YLocal;
    private Agent agent2YLocal;
    private Agent agent3YLocal;
    private Agent agent4YLocal;
    private Agent agent5YLocal;
    private Agent agent6YLocal;
    private Agent agent7YLocal;
    private Agent agent8YLocal;
    private Agent agent9YLocal;
    private Agent agent10YLocal;

    /**
     * Performed before each test: Initialization of the network.
     */
    @Before
    public void initAgent() {
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);

        // y global
        this.networkYGlobal = new Network("NunnerBuskens Y-Global Test");
        UtilityFunction ufYGlobal = new NunnerBuskens(b1, b2, alpha, c1, c2, true);

        // agetns
        this.agent1YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent2YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent3YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent4YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent5YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent6YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent7YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent8YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent9YGlobal  = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);
        this.agent10YGlobal = this.networkYGlobal.addAgent(ufYGlobal, ds, rSigma, rPi, phi);

        // connections
        this.agent1YGlobal.addConnection(this.agent2YGlobal);
        this.agent1YGlobal.addConnection(this.agent3YGlobal);
        this.agent1YGlobal.addConnection(this.agent4YGlobal);
        this.agent1YGlobal.addConnection(this.agent6YGlobal);
        this.agent1YGlobal.addConnection(this.agent7YGlobal);
        this.agent1YGlobal.addConnection(this.agent8YGlobal);
        this.agent2YGlobal.addConnection(this.agent3YGlobal);
        this.agent3YGlobal.addConnection(this.agent4YGlobal);
        this.agent3YGlobal.addConnection(this.agent5YGlobal);
        this.agent4YGlobal.addConnection(this.agent5YGlobal);
        this.agent5YGlobal.addConnection(this.agent6YGlobal);
        this.agent6YGlobal.addConnection(this.agent7YGlobal);
        this.agent7YGlobal.addConnection(this.agent8YGlobal);
        this.agent8YGlobal.addConnection(this.agent9YGlobal);
        this.agent8YGlobal.addConnection(this.agent10YGlobal);

        // y local
        this.networkYLocal = new Network("NunnerBuskens Y-Local Test");
        UtilityFunction ufYLocal = new NunnerBuskens(b1, b2, alpha, c1, c2, false);

        // agetns
        this.agent1YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent2YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent3YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent4YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent5YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent6YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent7YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent8YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent9YLocal  = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);
        this.agent10YLocal = this.networkYLocal.addAgent(ufYLocal, ds, rSigma, rPi, phi);

        // connections
        this.agent1YLocal.addConnection(this.agent2YLocal);
        this.agent1YLocal.addConnection(this.agent3YLocal);
        this.agent1YLocal.addConnection(this.agent4YLocal);
        this.agent1YLocal.addConnection(this.agent6YLocal);
        this.agent1YLocal.addConnection(this.agent7YLocal);
        this.agent1YLocal.addConnection(this.agent8YLocal);
        this.agent2YLocal.addConnection(this.agent3YLocal);
        this.agent3YLocal.addConnection(this.agent4YLocal);
        this.agent3YLocal.addConnection(this.agent5YLocal);
        this.agent4YLocal.addConnection(this.agent5YLocal);
        this.agent5YLocal.addConnection(this.agent6YLocal);
        this.agent6YLocal.addConnection(this.agent7YLocal);
        this.agent7YLocal.addConnection(this.agent8YLocal);
        this.agent8YLocal.addConnection(this.agent9YLocal);
        this.agent8YLocal.addConnection(this.agent10YLocal);
    }


    /**
     * Test of utility calculation.
     */
    @Test
    public void testGetUtility() {

        // with global y = (n*(n-1))/2
        assertEquals(12.1, Precision.round(this.agent1YGlobal.getUtility().getOverallUtility(), 1), 0);
        assertEquals(11.6, Precision.round(this.agent3YGlobal.getUtility().getOverallUtility(), 1), 0);
        assertEquals( 8.8, Precision.round(this.agent8YGlobal.getUtility().getOverallUtility(), 1), 0);

        // with local y = number of direct ties that do not share a tie between each other
        assertEquals(5.3, Precision.round(this.agent1YLocal.getUtility().getOverallUtility(), 1), 0);
        assertEquals(3.1, Precision.round(this.agent3YLocal.getUtility().getOverallUtility(), 1), 0);
        assertEquals(3.1, Precision.round(this.agent8YLocal.getUtility().getOverallUtility(), 1), 0);
    }

}
