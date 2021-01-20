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

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener;
import nl.uu.socnetid.nidm.gui.BurgerBuskensPanel;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class BurgerBuskens extends UtilityFunction implements BurgerBuskensChangeListener {

    private static final String B1 = "b1:";
    private static final String B2 = "b2:";
    private static final String C1 = "c1:";
    private static final String C2 = "c2:";
    private static final String C3 = "c3:";

    private static final Logger logger = LogManager.getLogger(BurgerBuskens.class);

    // benefits of direct connections
    private double b1;
    // benefits of closed triads
    private double b2;
    // costs of direct connections
    private double c1;
    // quadratic costs of additional direct connections
    private double c2;
    // costs of closed triads
    private double c3;
    // the panel to track GUI parameter changes from
    private BurgerBuskensPanel bbPanel;


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
        this(b1, b2, c1, c2, c3, null);
    }

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
     * @param bbPanel
     *          the panel to track GUI parameter changes from
     */
    public BurgerBuskens(double b1, double b2, double c1, double c2, double c3, BurgerBuskensPanel bbPanel) {
        this.b1 = b1;
        this.b2 = b2;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.bbPanel = bbPanel;
        if (this.bbPanel != null) {
            this.bbPanel.addParameterChangeListener(this);
        }
    }

    /**
     * Constructor from a string array containing he utility function's details.
     *
     * @param ufSplit
     *          the string array containing he utility function's details
     */
    protected BurgerBuskens(String[] ufSplit) {

        for (String value : ufSplit) {

            if (value.contains(UF_TYPE)) {
                continue;

            } else if (value.contains(B1)) {
                value = value.replace(B1, "");
                this.b1 = Double.valueOf(value);

            } else if (value.contains(B2)) {
                value = value.replace(B2, "");
                this.b2 = Double.valueOf(value);

            } else if (value.contains(C1)) {
                value = value.replace(C1, "");
                this.c1 = Double.valueOf(value);

            } else if (value.contains(C2)) {
                value = value.replace(C2, "");
                this.c2 = Double.valueOf(value);

            } else if (value.contains(C3)) {
                value = value.replace(C3, "");
                this.c3 = Double.valueOf(value);

            } else {
                throw new IllegalAccessError("Unknown value: " + value);
            }
        }

        this.bbPanel = null;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return TYPE_BURGER_BUSKENS;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialBenefits(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialBenefits(LocalAgentConnectionsStats lacs, Agent agent) {
        return
                // benefits of direct connections
                this.b1 * lacs.getN() +
                // benefits for closed triads
                this.b2 * lacs.getZ();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getSocialCosts(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getSocialCosts(LocalAgentConnectionsStats lacs, Agent agent) {
        return
                // costs of direct connections
                this.c1 * lacs.getN() +
                // quadratic costs of direct connections
                this.c2 * (lacs.getN()*lacs.getN()) +
                // costs for closed triads
                this.c3 * lacs.getZ();
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
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getUtilityFunctionDetails()
     */
    @Override
    protected String getUtilityFunctionDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append(B1).append(this.b1).append(STRING_DELIMITER);
        sb.append(B2).append(this.b2).append(STRING_DELIMITER);
        sb.append(C1).append(this.c1).append(STRING_DELIMITER);
        sb.append(C2).append(this.c2).append(STRING_DELIMITER);
        sb.append(C3).append(this.c3).append(STRING_DELIMITER);
        return sb.toString();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener#notifyB1Changed()
     */
    @Override
    public void notifyB1Changed() {
        this.b1 = this.bbPanel.getB1();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener#notifyB2Changed()
     */
    @Override
    public void notifyB2Changed() {
        this.b2 = this.bbPanel.getB2();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener#notifyC1Changed()
     */
    @Override
    public void notifyC1Changed() {
        this.c1 = this.bbPanel.getC1();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener#notifyC2Changed()
     */
    @Override
    public void notifyC2Changed() {
        this.c2 = this.bbPanel.getC2();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.BurgerBuskensChangeListener#notifyC3Changed()
     */
    @Override
    public void notifyC3Changed() {
        this.c3 = this.bbPanel.getC3();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals()
     */
    @Override
    public boolean equals(Object o) {

        // not null
        if (o == null) {
            return false;
        }

        // same object
        if (o == this) {
            return true;
        }

        // same type
        if (!(o instanceof BurgerBuskens)) {
            return false;
        }

        BurgerBuskens bb = (BurgerBuskens) o;

        // same values
        return this.b1 == bb.b1 &&
                this.b2 == bb.b2 &&
                this.c1 == bb.c1 &&
                this.c2 == bb.c2 &&
                this.c3 == bb.c3;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.b1, this.b2, this.c1, this.c2, this.c3);
    }

}
