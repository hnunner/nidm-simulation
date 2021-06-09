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
package nl.uu.socnetid.nidm.diseases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.UtilityFunction;


/**
 * Test cases for the {@link Agent} class.
 *
 * @author Hendrik Nunner
 */
public class SIRDiseaseTest {

    // network
    private Network network;

    // s
    private Agent agent1;

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
        this.network = new Network("Agent Test");
        UtilityFunction uf = new Cumulative();
        this.ds = new DiseaseSpecs(DiseaseType.SIR, tau, s, gamma, mu);
        this.agent1 = this.network.addAgent(uf, this.ds);
        this.agent1.infect(ds, 0);
    }


	/**
	 * Test of disease evolution.
	 */
	@Test
	public void testEvolve() {
	    assertTrue(this.agent1.isInfected());
	    assertEquals(10, this.agent1.getDisease().getTimeUntilCured());
	    this.agent1.getDisease().evolve();
        assertEquals(9, this.agent1.getDisease().getTimeUntilCured());
	}

}
