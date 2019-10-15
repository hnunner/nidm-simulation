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
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public abstract class UtilityFunction {


    /**
     * Computes the utility for a agent based on the social connections.
     *
     * @param agent
     *          the agent to compute the utility for
     * @return the agent's utility based on the connections
     */
    public Utility getUtility(Agent agent) {

        LocalAgentConnectionsStats lacs = StatsComputer.computeLocalAgentConnectionsStats(agent);

        return new Utility(
                getSocialBenefits(lacs, agent),
                getSocialCosts(lacs, agent),
                getDiseaseCosts(lacs, agent));
    }

    /**
     * @return the name of the utility function to be used in the stats window
     */
    public abstract String getStatsName();

    /**
     * Computes the benefits of social connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the benefits for
     * @return the benefit of direct connections
     */
    protected abstract double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * Computes the costs of social connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the costs for
     * @return the costs of direct connections
     */
    protected abstract double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * Computes the costs of a disease.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the effect for
     * @return the benefit of a disease
     */
    protected abstract double getDiseaseCosts(LocalAgentConnectionsStats lacs, Agent agent);


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();
}
