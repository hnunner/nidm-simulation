package nl.uu.socnetid.netgame.networks;

import nl.uu.socnetid.netgame.agents.Agent;

/**
 * @author Hendrik Nunner
 */
public interface NetworkListener {

    /**
     * Entry point for agents being added notifications.
     *
     * @param agent
     *          the agent being added
     */
    void notifyAgentAdded(final Agent agent);

    /**
     * Entry point for agents being removed notifications.
     *
     * @param agentId
     *          the id of the agent being removed
     */
    void notifyAgentRemoved(final String agentId);

}
