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
public class TruncatedConnections extends UtilityFunction {

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit for connections, deteriorating over distance
     * @param c
     *          the c to maintain direct connections
     */
    public TruncatedConnections(double alpha, double c) {
        super(alpha, alpha * alpha, c);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "TC";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getAlpha() * lacs.getN();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getBeta() * lacs.getM();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalAgentConnectionsStats lacs, Agent agent) {
        int nSR = lacs.getnS() + lacs.getnR();
        int nI = lacs.getnI();
        return (nSR + (nI * agent.getDiseaseSpecs().getMu())) * this.getC();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getEffectOfDisease(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getEffectOfDisease(LocalAgentConnectionsStats lacs, Agent agent) {
        // no effect
        return 0.0;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getKappa()
     */
    @Override
    public double getKappa() {
        return 0;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getLamda()
     */
    @Override
    public double getLamda() {
        return 0;
    }

}
