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

import java.util.Iterator;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class CarayolRoux extends UtilityFunction {

    // benefits of connections
    private final double omega;
    // distance dependent decay of benefits of connections
    private final double delta;
    // costs of connections
    private final double c;


    /**
     * Constructor.
     *
     * @param omega
     *          the benefits of connections
     * @param delta
     *          the distance dependent decay of benefits of connections
     * @param c
     *          the costs of connections
     */
    public CarayolRoux(double omega, double delta, double c) {
        this.omega = omega;
        this.delta = delta;
        this.c = c;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CR";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {
        double benefits = 0.0;

        Iterator<Agent> connectionsIt = lacs.getConnections().iterator();
        while (connectionsIt.hasNext()) {
            Agent connection = connectionsIt.next();
            Integer gd = agent.getGeodesicDistanceTo(connection);
            if (gd != null) {
                double benefit = this.omega * Math.pow(delta, gd);
                benefits += benefit;
                System.out.println("Benefits from " + agent.getId() + " to " + connection.getId() + ": " + benefit);
            }
        }

        return benefits;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        double costs = 0.0;

        Iterator<Agent> connectionsIt = lacs.getConnections().iterator();
        while (connectionsIt.hasNext()) {
            Agent connection = connectionsIt.next();
            double cost = this.c *
                    (agent.getGeographicDistanceTo(connection) * Math.pow(Math.ceil(agent.getNetwork().getN() / 2), -1));
            costs += cost;
            System.out.println("Costs from " + agent.getId() + " to " + connection.getId() + ": " + cost);
        }

        return costs;
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
        sb.append(" | omega:").append(this.getOmega());
        sb.append(" | delta:").append(this.getDelta());
        sb.append(" | c:").append(this.getC());
        return sb.toString();
    }

    /**
     * @return the omega
     */
    public double getOmega() {
        return omega;
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @return the c
     */
    public double getC() {
        return c;
    }

}
