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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class Cidm extends UtilityFunction {

    private static final Logger logger = LogManager.getLogger(BurgerBuskens.class);

    // utility of direct connections
    private final double alpha;
    // discount for infected direct connections
    private final double kappa;
    // utility of indirect connections
    private final double beta;
    // discount for infected indirect connections
    private final double lamda;
    // costs to maintain direct connections
    private final double c;

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param kappa
     *          the discount for infected direct connections
     * @param beta
     *          the benefit of an indirect connection
     * @param lamda
     *          the discount for infected indirect connections
     * @param c
     *          the maintenance costs for a direct connection
     */
    public Cidm(double alpha, double kappa, double beta, double lamda, double c) {
        this.alpha = alpha;
        this.kappa = kappa;
        this.beta = beta;
        this.lamda = lamda;
        this.c = c;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "Cidm";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {
        return
                // direct connections
                this.getAlpha() * (lacs.getnS() + this.kappa * lacs.getnI() + lacs.getnR()) +
                // indirect connections
                this.getBeta() * (lacs.getmS() + this.lamda * lacs.getmI() + lacs.getmR());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        return this.getC() * (lacs.getnS() + agent.getDiseaseSpecs().getMu() * lacs.getnI() + lacs.getnR());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getTheoreticDegree()
     */
    @Override
    public double getTheoreticDegree() {
        logger.warn("getTheoreticDegree not implemented for " + getClass().getName());
        return 0.0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(getStatsName());
        sb.append(" | alpha:").append(this.getAlpha());
        sb.append(" | kappa:").append(this.getKappa());
        sb.append(" | beta:").append(this.getBeta());
        sb.append(" | lamda:").append(this.getLamda());
        sb.append(" | c:").append(this.getC());
        return sb.toString();
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @return the kappa
     */
    public double getKappa() {
        return kappa;
    }

    /**
     * @return the beta
     */
    public double getBeta() {
        return beta;
    }

    /**
     * @return the lamda
     */
    public double getLamda() {
        return lamda;
    }

    /**
     * @return the c
     */
    public double getC() {
        return c;
    }

}
