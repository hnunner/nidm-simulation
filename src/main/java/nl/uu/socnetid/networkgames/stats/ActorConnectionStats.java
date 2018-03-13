package nl.uu.socnetid.networkgames.stats;

import org.apache.log4j.Logger;

/**
 * @author Hendrik Nunner
 */
public class ActorConnectionStats implements Cloneable {

    // logger
    private static final Logger logger = Logger.getLogger(ActorConnectionStats.class);

    // stats
    private int activelyBrokenTies = 0;
    private int passivelyBrokenTies = 0;
    private int acceptedOutgoingRequests = 0;
    private int declinedOutgoingRequests = 0;
    private int acceptedIncomingRequests = 0;
    private int declinedIncomingRequests = 0;


    /**
     * @return the actively broken ties
     */
    public int getActivelyBrokenTies() {
        return activelyBrokenTies;
    }

    /**
     * Increases the amount of actively broken ties.
     */
    public void incActivelyBrokenTies() {
        this.activelyBrokenTies++;
    }

    /**
     * @return the passively broken ties
     */
    public int getPassivelyBrokenTies() {
        return passivelyBrokenTies;
    }

    /**
     * Increases the amount of passively broken ties.
     */
    public void incPassivelyBrokenTies() {
        this.passivelyBrokenTies++;
    }

    /**
     * @return the acceptedOutgoingRequests
     */
    public int getAcceptedOutgoingRequests() {
        return acceptedOutgoingRequests;
    }

    /**
     * Increases the amount of accepted outgoing requests.
     */
    public void incAcceptedOutgoingRequests() {
        this.acceptedOutgoingRequests++;
    }

    /**
     * @return the declinedOutgoingRequests
     */
    public int getDeclinedOutgoingRequests() {
        return declinedOutgoingRequests;
    }

    /**
     * Increases the amount of declined outgoing requests.
     */
    public void incDeclinedOutgoingRequests() {
        this.declinedOutgoingRequests++;
    }

    /**
     * @return the acceptedIncomingRequests
     */
    public int getAcceptedIncomingRequests() {
        return acceptedIncomingRequests;
    }

    /**
     * Increases the amount of accepted incoming requests.
     */
    public void incAcceptedIncomingRequests() {
        this.acceptedIncomingRequests++;
    }

    /**
     * @return the declinedIncomingRequests
     */
    public int getDeclinedIncomingRequests() {
        return declinedIncomingRequests;
    }

    /**
     * Increases the amount of declined incoming requests.
     */
    public void incDeclinedIncomingRequests() {
        this.declinedIncomingRequests++;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public ActorConnectionStats clone() {
        ActorConnectionStats acs = null;
        try {
            acs = (ActorConnectionStats) super.clone();
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

        sb.append("broken ties (active):").append(this.getActivelyBrokenTies());
        sb.append(" | broken ties (passive):").append(this.getPassivelyBrokenTies());
        sb.append(" | accepted requests (out):").append(this.getAcceptedOutgoingRequests());
        sb.append(" | declined requests (out):").append(this.getDeclinedOutgoingRequests());
        sb.append(" | accepted requests (in):").append(this.getAcceptedIncomingRequests());
        sb.append(" | declined requests (in):").append(this.getDeclinedIncomingRequests());

        return sb.toString();
    }

}
