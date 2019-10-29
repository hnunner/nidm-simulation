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
import nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener;
import nl.uu.socnetid.nidm.gui.NunnerBuskensPanel;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskens extends UtilityFunction implements NunnerBuskensChangeListener {

    // benefits of direct connections
    private double b1;
    // weight of benefits for triads
    private double b2;
    // preference shift between open and closed triads
    private double alpha;
    // costs of direct connections
    private double c1;
    // quadratic costs of additional direct connections
    private double c2;
    // whether y is computed globally (n*(n-1)/2) or locally (ties of agent that do not share a tie between each other)
    private boolean yGlobal;
    // the panel to track GUI parameter changes from
    private NunnerBuskensPanel nbPanel;


    /**
     * Constructor.
     *
     * @param b1
     *          the benefits of direct connections
     * @param c1
     *          the costs of direct connections
     * @param c2
     *          the quadratic costs of additional direct connections
     * @param b2
     *          the weight of benefits for triads
     * @param alpha
     *          the preference shift between open and closed triads
     * @param yGlobal
     *           whether y is computed globally (n*(n-1)/2) or locally (ties of agent that do not share a tie between each other)
     */
    public NunnerBuskens(double b1, double b2, double alpha, double c1, double c2, boolean yGlobal) {
        this(b1, b2, alpha, c1, c2, yGlobal, null);
    }

    /**
     * Constructor.
     *
     * @param b1
     *          the benefits of direct connections
     * @param c1
     *          the costs of direct connections
     * @param c2
     *          the quadratic costs of additional direct connections
     * @param b2
     *          the weight of benefits for triads
     * @param alpha
     *          the preference shift between open and closed triads
     * @param yGlobal
     *           whether y is computed globally (n*(n-1)/2) or locally (ties of agent that do not share a tie between each other)
     * @param nbPanel
     *          the panel to track GUI parameter changes from
     */
    public NunnerBuskens(double b1, double b2, double alpha, double c1, double c2, boolean yGlobal, NunnerBuskensPanel nbPanel) {
        this.b1 = b1;
        this.b2 = b2;
        this.alpha = alpha;
        this.c1 = c1;
        this.c2 = c2;
        this.yGlobal = yGlobal;
        this.nbPanel = nbPanel;
        if (this.nbPanel != null) {
            this.nbPanel.addParameterChangeListener(this);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "NB";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {
        return
                this.b1 * lacs.getN() +
                this.b2 * (Math.pow(lacs.getZ(), this.alpha) *
                        Math.pow(this.yGlobal ? lacs.getYGlobal() : lacs.getYLocal(), 1-this.alpha));
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        return
                this.c1 * lacs.getN() +
                this.c2 * Math.pow(lacs.getN(), 2);
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
        sb.append(" | b1:").append(this.b1);
        sb.append(" | b2:").append(this.b2);
        sb.append(" | c1:").append(this.c1);
        sb.append(" | c2:").append(this.c2);
        sb.append(" | alpha:").append(this.alpha);
        return sb.toString();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener#notifyB1Changed()
     */
    @Override
    public void notifyB1Changed() {
        this.b1 = this.nbPanel.getB1();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener#notifyC1Changed()
     */
    @Override
    public void notifyC1Changed() {
        this.c1 = this.nbPanel.getC1();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener#notifyC2Changed()
     */
    @Override
    public void notifyC2Changed() {
        this.c2 = this.nbPanel.getC2();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener#notifyB2Changed()
     */
    @Override
    public void notifyB2Changed() {
        this.b2 = this.nbPanel.getB2();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskensChangeListener#notifyAlphaChanged()
     */
    @Override
    public void notifyAlphaChanged() {
        this.alpha = this.nbPanel.getAlpha();
    }

}
