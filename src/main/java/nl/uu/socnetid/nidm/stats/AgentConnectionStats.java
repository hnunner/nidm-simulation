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
package nl.uu.socnetid.nidm.stats;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Hendrik Nunner
 */
public class AgentConnectionStats implements Cloneable {

    // logger
    private static final Logger logger = LogManager.getLogger(AgentConnectionStats.class);

    private static final String DECLINED_REQUESTS_IN_EPIDEMIC = "declined requests (in) - epidemic:";
    private static final String ACCEPTED_REQUESTS_IN_EPIDEMIC = "accepted requests (in) - epidemic:";
    private static final String DECLINED_REQUESTS_OUT_EPIDEMIC = "declined requests (out) - epidemic:";
    private static final String ACCEPTED_REQUESTS_OUT_EPIDEMIC = "accepted requests (out) - epidemic:";
    private static final String BROKEN_TIES_PASSIVE_EPIDEMIC = "broken ties (passive) - epidemic:";
    private static final String BROKEN_TIES_ACTIVE_EPIDEMIC = "broken ties (active) - epidemic:";
    private static final String DECLINED_REQUESTS_IN = "declined requests (in):";
    private static final String ACCEPTED_REQUESTS_IN = "accepted requests (in):";
    private static final String DECLINED_REQUESTS_OUT = "declined requests (out):";
    private static final String ACCEPTED_REQUESTS_OUT = "accepted requests (out):";
    private static final String BROKEN_TIES_PASSIVE = "broken ties (passive):";
    private static final String BROKEN_TIES_ACTIVE = "broken ties (active):";
    private static final String STRING_DELIMITER = "; ";

    private int brokenTiesActive = 0;
    private int brokenTiesPassive = 0;
    private int acceptedRequestsOut = 0;
    private int declinedRequestsOut = 0;
    private int acceptedRequestsIn = 0;
    private int declinedRequestsIn = 0;

    private int brokenTiesActiveEpidemic = 0;
    private int brokenTiesPassiveEpidemic = 0;
    private int acceptedRequestsOutEpidemic = 0;
    private int declinedRequestsOutEpidemic = 0;
    private int acceptedRequestsInEpidemic = 0;
    private int declinedRequestsInEpidemic = 0;


    /**
     * @return the actively broken ties
     */
    public int getBrokenTiesActive() {
        return brokenTiesActive;
    }

    /**
     * Increases the amount of actively broken ties.
     */
    public void incBrokenTiesActive() {
        this.brokenTiesActive++;
    }

    /**
     * @return the passively broken ties
     */
    public int getBrokenTiesPassive() {
        return brokenTiesPassive;
    }

    /**
     * Increases the amount of passively broken ties.
     */
    public void incBrokenTiesPassive() {
        this.brokenTiesPassive++;
    }

    /**
     * @return the acceptedRequestsOut
     */
    public int getAcceptedRequestsOut() {
        return acceptedRequestsOut;
    }

    /**
     * Increases the amount of accepted outgoing requests.
     */
    public void incAcceptedRequestsOut() {
        this.acceptedRequestsOut++;
    }

    /**
     * @return the declinedRequestsOut
     */
    public int getDeclinedRequestsOut() {
        return declinedRequestsOut;
    }

    /**
     * Increases the amount of declined outgoing requests.
     */
    public void incDeclinedRequestsOut() {
        this.declinedRequestsOut++;
    }

    /**
     * @return the acceptedRequestsIn
     */
    public int getAcceptedRequestsIn() {
        return acceptedRequestsIn;
    }

    /**
     * Increases the amount of accepted incoming requests.
     */
    public void incAcceptedRequestsIn() {
        this.acceptedRequestsIn++;
    }

    /**
     * @return the declinedRequestsIn
     */
    public int getDeclinedRequestsIn() {
        return declinedRequestsIn;
    }

    /**
     * Increases the amount of declined incoming requests.
     */
    public void incDeclinedRequestsIn() {
        this.declinedRequestsIn++;
    }

    /**
     * @return the actively broken ties during an epidemic
     */
    public int getBrokenTiesActiveEpidemic() {
        return brokenTiesActiveEpidemic;
    }

    /**
     * Increases the amount of actively broken ties during an epidemic.
     */
    public void incBrokenTiesActiveEpidemic() {
        this.brokenTiesActiveEpidemic++;
    }

    /**
     * @return the passively broken ties during an epidemic
     */
    public int getBrokenTiesPassiveEpidemic() {
        return brokenTiesPassiveEpidemic;
    }

    /**
     * Increases the amount of passively broken ties during an epidemic.
     */
    public void incBrokenTiesPassiveEpidemic() {
        this.brokenTiesPassiveEpidemic++;
    }

    /**
     * @return the acceptedRequestsOutEpidemic
     */
    public int getAcceptedRequestsOutEpidemic() {
        return acceptedRequestsOutEpidemic;
    }

    /**
     * Increases the amount of accepted outgoing requests during an epidemic.
     */
    public void incAcceptedRequestsOutEpidemic() {
        this.acceptedRequestsOutEpidemic++;
    }

    /**
     * @return the declinedRequestsOutEpidemic
     */
    public int getDeclinedRequestsOutEpidemic() {
        return declinedRequestsOutEpidemic;
    }

    /**
     * Increases the amount of declined outgoing requests during an epidemic.
     */
    public void incDeclinedRequestsOutEpidemic() {
        this.declinedRequestsOutEpidemic++;
    }

    /**
     * @return the acceptedRequestsInEpidemic
     */
    public int getAcceptedRequestsInEpidemic() {
        return acceptedRequestsInEpidemic;
    }

    /**
     * Increases the amount of accepted incoming requests during an epidemic.
     */
    public void incAcceptedRequestsInEpidemic() {
        this.acceptedRequestsInEpidemic++;
    }

    /**
     * @return the declinedRequestsInEpidemic
     */
    public int getDeclinedRequestsInEpidemic() {
        return declinedRequestsInEpidemic;
    }

    /**
     * Increases the amount of declined incoming requests during an epidemic.
     */
    public void incDeclinedRequestsInEpidemic() {
        this.declinedRequestsInEpidemic++;
    }



    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public AgentConnectionStats clone() {
        AgentConnectionStats acs = null;
        try {
            acs = (AgentConnectionStats) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error(e);
        }
        return acs;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(BROKEN_TIES_ACTIVE).append(this.getBrokenTiesActive());
        sb.append(STRING_DELIMITER).append(BROKEN_TIES_PASSIVE).append(this.getBrokenTiesPassive());
        sb.append(STRING_DELIMITER).append(ACCEPTED_REQUESTS_OUT).append(this.getAcceptedRequestsOut());
        sb.append(STRING_DELIMITER).append(DECLINED_REQUESTS_OUT).append(this.getDeclinedRequestsOut());
        sb.append(STRING_DELIMITER).append(ACCEPTED_REQUESTS_IN).append(this.getAcceptedRequestsIn());
        sb.append(STRING_DELIMITER).append(DECLINED_REQUESTS_IN).append(this.getDeclinedRequestsIn());
        sb.append(STRING_DELIMITER).append(BROKEN_TIES_ACTIVE_EPIDEMIC).append(this.getBrokenTiesActiveEpidemic());
        sb.append(STRING_DELIMITER).append(BROKEN_TIES_PASSIVE_EPIDEMIC).append(this.getBrokenTiesPassiveEpidemic());
        sb.append(STRING_DELIMITER).append(ACCEPTED_REQUESTS_OUT_EPIDEMIC).append(this.getAcceptedRequestsOutEpidemic());
        sb.append(STRING_DELIMITER).append(DECLINED_REQUESTS_OUT_EPIDEMIC).append(this.getDeclinedRequestsOutEpidemic());
        sb.append(STRING_DELIMITER).append(ACCEPTED_REQUESTS_IN_EPIDEMIC).append(this.getAcceptedRequestsInEpidemic());
        sb.append(STRING_DELIMITER).append(DECLINED_REQUESTS_IN_EPIDEMIC).append(this.getDeclinedRequestsInEpidemic());

        return sb.toString();
    }

    /**
     * Creates the connection stats from a given string.
     * @param text
     *          the string to create the connection stats for
     * @return the connection stats
     */
    public static AgentConnectionStats fromString(String text) {
        AgentConnectionStats res = new AgentConnectionStats();

        String[] split = text.split(STRING_DELIMITER);
        for (String value : split) {

            if (value.contains(BROKEN_TIES_ACTIVE)) {
                value = value.replace(BROKEN_TIES_ACTIVE, "");
                res.brokenTiesActive = Integer.valueOf(value);

            } else if (value.contains(BROKEN_TIES_PASSIVE)) {
                value = value.replace(BROKEN_TIES_PASSIVE, "");
                res.brokenTiesPassive = Integer.valueOf(value);

            } else if (value.contains(ACCEPTED_REQUESTS_OUT)) {
                value = value.replace(ACCEPTED_REQUESTS_OUT, "");
                res.acceptedRequestsOut = Integer.valueOf(value);

            } else if (value.contains(DECLINED_REQUESTS_OUT)) {
                value = value.replace(DECLINED_REQUESTS_OUT, "");
                res.declinedRequestsOut = Integer.valueOf(value);

            } else if (value.contains(ACCEPTED_REQUESTS_IN)) {
                value = value.replace(ACCEPTED_REQUESTS_IN, "");
                res.acceptedRequestsIn = Integer.valueOf(value);

            } else if (value.contains(DECLINED_REQUESTS_IN)) {
                value = value.replace(DECLINED_REQUESTS_IN, "");
                res.declinedRequestsIn = Integer.valueOf(value);

            } else if (value.contains(BROKEN_TIES_ACTIVE_EPIDEMIC)) {
                value = value.replace(BROKEN_TIES_ACTIVE_EPIDEMIC, "");
                res.brokenTiesActiveEpidemic = Integer.valueOf(value);

            } else if (value.contains(BROKEN_TIES_PASSIVE_EPIDEMIC)) {
                value = value.replace(BROKEN_TIES_PASSIVE_EPIDEMIC, "");
                res.brokenTiesPassiveEpidemic = Integer.valueOf(value);

            } else if (value.contains(ACCEPTED_REQUESTS_OUT_EPIDEMIC)) {
                value = value.replace(ACCEPTED_REQUESTS_OUT_EPIDEMIC, "");
                res.acceptedRequestsOutEpidemic = Integer.valueOf(value);

            } else if (value.contains(DECLINED_REQUESTS_OUT_EPIDEMIC)) {
                value = value.replace(DECLINED_REQUESTS_OUT_EPIDEMIC, "");
                res.declinedRequestsOutEpidemic = Integer.valueOf(value);

            } else if (value.contains(ACCEPTED_REQUESTS_IN_EPIDEMIC)) {
                value = value.replace(ACCEPTED_REQUESTS_IN_EPIDEMIC, "");
                res.acceptedRequestsInEpidemic = Integer.valueOf(value);

            } else if (value.contains(DECLINED_REQUESTS_IN_EPIDEMIC)) {
                value = value.replace(DECLINED_REQUESTS_IN_EPIDEMIC, "");
                res.declinedRequestsInEpidemic = Integer.valueOf(value);

            } else {
                throw new IllegalArgumentException("No constant with text " + value + " found.");
            }
        }

        return res;
    }


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
        if (!(o instanceof AgentConnectionStats)) {
            return false;
        }

        AgentConnectionStats acs = (AgentConnectionStats) o;

        // same values
        return this.brokenTiesActive == acs.getBrokenTiesActive() &&
                this.brokenTiesPassive == acs.getBrokenTiesPassive() &&
                this.acceptedRequestsOut == acs.getAcceptedRequestsOut() &&
                this.declinedRequestsOut == acs.getDeclinedRequestsOut() &&
                this.acceptedRequestsIn == acs.getAcceptedRequestsIn() &&
                this.declinedRequestsIn == acs.getDeclinedRequestsIn() &&
                this.brokenTiesActiveEpidemic == acs.getBrokenTiesActiveEpidemic() &&
                this.brokenTiesPassiveEpidemic == acs.getBrokenTiesPassiveEpidemic() &&
                this.acceptedRequestsOutEpidemic == acs.getAcceptedRequestsOutEpidemic() &&
                this.declinedRequestsOutEpidemic == acs.getDeclinedRequestsOutEpidemic() &&
                this.acceptedRequestsInEpidemic == acs.getAcceptedRequestsInEpidemic() &&
                this.declinedRequestsInEpidemic == acs.getDeclinedRequestsInEpidemic();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.brokenTiesActive,
                this.brokenTiesPassive,
                this.acceptedRequestsOut,
                this.declinedRequestsOut,
                this.acceptedRequestsIn,
                this.declinedRequestsIn,
                this.brokenTiesActiveEpidemic,
                this.brokenTiesPassiveEpidemic,
                this.acceptedRequestsOutEpidemic,
                this.declinedRequestsOutEpidemic,
                this.acceptedRequestsInEpidemic,
                this.declinedRequestsInEpidemic);
    }

}
