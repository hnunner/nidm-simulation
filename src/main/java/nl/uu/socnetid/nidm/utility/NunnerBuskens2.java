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
import nl.uu.socnetid.nidm.gui.NunnerBuskens2ChangeListener;
import nl.uu.socnetid.nidm.gui.NunnerBuskens2Panel;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskens2 extends UtilityFunction implements NunnerBuskens2ChangeListener {

    // CONSTANTS
    // costs of direct connections
    private static final double C1 = 0.2;

    // SET BY USER
    // preferred number of ties
    private int tPref;
    // whether to consider triads or not
    private boolean considerTriads;
    // preference shift between open and closed triads
    private double alpha;

    // the panel to track GUI parameter changes from
    private NunnerBuskens2Panel nbPanel;


    /**
     * Constructor.
     *
     * @param tPref
     *          the preferred number of ties per agent
     * @param considerTriads
     *          whether triads are considered for utility computation or not
     * @param alpha
     *          the preference shift between open and closed triads
     */
    public NunnerBuskens2(int tPref, boolean considerTriads, double alpha) {
        this(tPref, considerTriads, alpha, null);
    }

    /**
     * Constructor.
     *
     * @param tPref
     *          the preferred number of ties per agent
     * @param considerTriads
     *          whether triads are considered for utility computation or not
     * @param alpha
     *          the preference shift between open and closed triads
     * @param nbPanel
     *          the panel to track GUI parameter changes from
     */
    public NunnerBuskens2(int tPref, boolean considerTriads, double alpha, NunnerBuskens2Panel nbPanel) {
        this.tPref = tPref;
        this.considerTriads = considerTriads;
        this.alpha = alpha;
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
        return "NB2";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {

        // shared parameters
        int t           = lacs.getN();                  // number of direct ties
        double y        = lacs.getY();                  // number of open triads
        double z        = lacs.getZ();                  // number of closed triads
        double x        = (y+z) == 0 ? 0 : z/(y+z);     // proportion of closed triads

        // BENEFIT OF DIRECT TIES
        // parameters
        double b1       = getB1(x);                     // weight of direct ties
        // computation
        double bTies    = b1 * t;

        // BENEFIT OF TRIADS
        // parameters
        double b2       = getB2();                      // weight of triads
        double alpha    = this.alpha;
        // computation
        double bTriads  = b2 * (1 - 2 * ((Math.abs(x - alpha)) / (Math.max(alpha, 1-alpha))));

        // result
        return bTies + bTriads;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {

        // parameters
        int t           = lacs.getN();                  // number of direct ties
        double c1       = getC1();                      // weight of costs for direct ties
        double c2       = getC2();                      // weight of quadratic costs for direct ties
        // computation
        double c        = c1 * t + c2 * Math.pow(t, 2);

        // result
        return c;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#toString()
     */
    @Override
    public String toString() {

        // TODO implement

        StringBuilder sb = new StringBuilder();
//        sb.append("type:").append(getStatsName());
//        sb.append(" | b1:").append(getB1());
//        sb.append(" | b2:").append(getB2());
//        sb.append(" | c1:").append(getC1());
//        sb.append(" | c2:").append(getC2());
//        sb.append(" | alpha:").append(this.alpha);
        return sb.toString();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskens2ChangeListener#notifyConsiderTriadsChanged()
     */
    @Override
    public void notifyConsiderTriadsChanged() {
        this.considerTriads = this.nbPanel.isConsiderTriads();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskens2ChangeListener#notifyTPrefChanged()
     */
    @Override
    public void notifyTPrefChanged() {
        this.tPref = this.nbPanel.getTPref();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NunnerBuskens2ChangeListener#notifyAlphaChanged()
     */
    @Override
    public void notifyAlphaChanged() {
        this.alpha = this.nbPanel.getAlpha();
    }


    /**
     * Gets the weight of benefit for direct ties.
     *
     * @param x
     *          the proportion of closed triads
     * @return the weight of benefit for direct ties
     */
    private double getB1(double x) {

        return 1.0;

//        // not considering triads
//        if (!this.considerTriads) {
//            return 1.0;
//        }
//
//        // considering triads
//        // more closed triads than preferred
//        if (x >= this.alpha) {
//            return (this.tPref + 25*x - 28) / 10;
//        }
//        // less closed triads than preferred
//        return (2*this.tPref - 25*x + 24) / 20;

    }

    /**
     * Gets the weight of benefit for triads.
     *
     * @return the weight of benefit for triads
     */
    private double getB2() {

        // not considering triads
        if (!this.considerTriads) {
            return 0.0;
        }

        // considering triads
        return 1.0;

    }

    /**
     * Gets the weight of costs for direct ties.
     *
     * @return the weight of costs for direct ties
     */
    private double getC1() {
        return NunnerBuskens2.C1;
    }

    /**
     * Gets the weight of quadratic costs for direct ties.
     *
     * @param t
     *          the number of direct ties
     * @return the weight of quadratic costs for direct ties
     */
    private double getC2() {

        return 0.4 / this.tPref;

//        // not considering triads
//        if (!this.considerTriads) {
//            return 0.4 / this.tPref;
//        }
//
//        // considering triads
//        throw new RuntimeException("Not yet implemented!");
    }

}
