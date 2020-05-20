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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.gui.CarayolRouxChangeListener;
import nl.uu.socnetid.nidm.gui.CarayolRouxPanel;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class CarayolRoux extends UtilityFunction implements CarayolRouxChangeListener {

    private static final Logger logger = LogManager.getLogger(BurgerBuskens.class);

    // benefits of connections
    private double crOmega;
    // distance dependent decay of benefits of connections
    private double delta;
    // costs of connections
    private double c;
    // the panel to track GUI parameter changes from
    private CarayolRouxPanel crPanel;


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
        this(omega, delta, c, null);
    }

    /**
     * Constructor.
     *
     * @param crOmega
     *          the benefits of connections
     * @param delta
     *          the distance dependent decay of benefits of connections
     * @param c
     *          the costs of connections
     * @param crPanel
     *          the panel to track GUI parameter changes from
     */
    public CarayolRoux(double crOmega, double delta, double c, CarayolRouxPanel crPanel) {
        this.crOmega = crOmega;
        this.delta = delta;
        this.c = c;
        this.crPanel = crPanel;
        if (this.crPanel != null) {
            this.crPanel.addParameterChangeListener(this);
        }
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
        Map<Integer, Integer> connectionsByDistance = StatsComputer.getConnectionsByGeodesicDistance(agent);

        for (Entry<Integer, Integer> entry : connectionsByDistance.entrySet()) {
            Integer gdd = entry.getKey();
            Integer cons = entry.getValue();
            benefits += cons * (this.crOmega * Math.pow(delta, gdd));
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
        Map<Double, Integer> connectionsByDistance = StatsComputer.getDirectConnectionsByGeographicDistance(agent);

        for (Entry<Double, Integer> entry : connectionsByDistance.entrySet()) {
            Double ggd = entry.getKey();
            Integer cons = entry.getValue();
            costs += cons * (this.c * ggd * Math.pow(Math.ceil(lacs.getNetSize() / 2.0), -1));
        }

        return costs;



//        double costs = 0.0;
//
//        Iterator<Agent> connectionsIt = agent.getConnections().iterator();
//        while (connectionsIt.hasNext()) {
//            Agent connection = connectionsIt.next();
//            costs += this.c *
//                    (agent.getGeographicDistanceTo(connection) * Math.pow(Math.ceil(agent.getNetwork().getN() / 2.0), -1));
//        }
//
//        return costs;
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
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(getStatsName());
        sb.append(" | omega:").append(this.crOmega);
        sb.append(" | delta:").append(this.delta);
        sb.append(" | c:").append(this.c);
        return sb.toString();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.CarayolRouxChangeListener#notifyOmegaChanged()
     */
    @Override
    public void notifyCrOmegaChanged() {
        this.crOmega = this.crPanel.getCrOmega();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.CarayolRouxChangeListener#notifyDeltaChanged()
     */
    @Override
    public void notifyDeltaChanged() {
        this.delta = this.crPanel.getDelta();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.CarayolRouxChangeListener#notifyCChanged()
     */
    @Override
    public void notifyCChanged() {
        this.c = this.crPanel.getC();
    }

}
