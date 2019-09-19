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
package nl.uu.socnetid.nidm.utility;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class BurgerBuskens extends UtilityFunction {

    // benefits of direct connections
    private final double b1;
    // benefits of closed triads
    private final double b2;
    // costs of direct connections
    private final double c1;
    // quadratic costs of additional direct connections
    private final double c2;
    // costs of closed triads
    private final double c3;


    /**
     * Constructor.
     *
     * @param b1
     *          the benefits of direct connections
     * @param b2
     *          the benefits of closed triads
     * @param c1
     *          the costs of direct connections
     * @param c2
     *          the quadratic costs of additional direct connections
     * @param c3
     *          the costs of closed triads
     */
    public BurgerBuskens(double b1, double b2, double c1, double c2, double c3) {
        this.b1 = b1;
        this.b2 = b2;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "BB";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {
        // TODO add disease states
        return
                // benefits of direct connections
                this.getB1() * lacs.getN() +
                // benefits for closed triads
                this.getB2() * lacs.getZ();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        // TODO add disease states
        return
                // costs of direct connections
                this.getC1() * lacs.getN() +
                // quadratic costs of direct connections
                this.getC2() * (lacs.getN()*lacs.getN()) +
                // costs for closed triads
                this.getC3() * lacs.getZ();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getDiseaseCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getDiseaseCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        // TODO add costs for disease
        return 0;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(getStatsName());
        sb.append(" | b1:").append(this.getB1());
        sb.append(" | b2:").append(this.getB2());
        sb.append(" | c1:").append(this.getC1());
        sb.append(" | c2:").append(this.getC2());
        sb.append(" | c3:").append(this.getC3());
        return sb.toString();
    }

    /**
     * @return the b1
     */
    public double getB1() {
        return b1;
    }

    /**
     * @return the b2
     */
    public double getB2() {
        return b2;
    }

    /**
     * @return the c1
     */
    public double getC1() {
        return c1;
    }

    /**
     * @return the c2
     */
    public double getC2() {
        return c2;
    }

    /**
     * @return the c3
     */
    public double getC3() {
        return c3;
    }

}
