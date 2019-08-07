package nl.uu.socnetid.netgame.stats;

import org.apache.log4j.Logger;

/**
 * @author Hendrik Nunner
 */
public class AgentConnectionStats implements Cloneable {

    // logger
    private static final Logger logger = Logger.getLogger(AgentConnectionStats.class);

    // stats
    private int brokenTiesActive = 0;
    private int brokenTiesPassive = 0;
    private int acceptedRequestsOut = 0;
    private int declinedRequestsOut = 0;
    private int acceptedRequestsIn = 0;
    private int declinedRequestsIn = 0;


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

        sb.append("broken ties (active):").append(this.getBrokenTiesActive());
        sb.append(" | broken ties (passive):").append(this.getBrokenTiesPassive());
        sb.append(" | accepted requests (out):").append(this.getAcceptedRequestsOut());
        sb.append(" | declined requests (out):").append(this.getDeclinedRequestsOut());
        sb.append(" | accepted requests (in):").append(this.getAcceptedRequestsIn());
        sb.append(" | declined requests (in):").append(this.getDeclinedRequestsIn());

        return sb.toString();
    }

}
