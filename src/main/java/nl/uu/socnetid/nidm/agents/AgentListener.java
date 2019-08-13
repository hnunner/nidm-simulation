package nl.uu.socnetid.nidm.agents;

import org.graphstream.graph.Edge;

/**
 * @author Hendrik Nunner
 */
public interface AgentListener {

    /**
     * Entry point for agent attributes being added notifications.
     *
     * @param agent
     *          the agent
     * @param attribute
     *          the attribute
     * @param value
     *          the attribute's value
     */
    void notifyAttributeAdded(final Agent agent, final String attribute, final Object value);

    /**
     * Entry point for agent attributes being changed notifications.
     *
     * @param agent
     *          the agent
     * @param attribute
     *          the attribute
     * @param oldValue
     *          the attribute's old value
     * @param newValue
     *          the attribute's new value
     */
    void notifyAttributeChanged(final Agent agent, final String attribute, final Object oldValue,
            final Object newValue);

    /**
     * Entry point for agent attributes being removed notifications.
     *
     * @param agent
     *          the agent
     * @param attribute
     *          the attribute
     */
    void notifyAttributeRemoved(final Agent agent, final String attribute);

    /**
     * Entry point for connection added between two agents notifications.
     *
     * @param edge
     *          the connection between the two agents
     * @param agent1
     *          the first agent
     * @param agent2
     *          the second agent
     */
    void notifyConnectionAdded(final Edge edge, final Agent agent1, final Agent agent2);

    /**
     * Entry point for edge removed between two agents notifications.
     *
     * @param agent
     *          the agent removing a connection
     * @param edge
     *          the edge
     */
    void notifyConnectionRemoved(final Agent agent, final Edge edge);

    /**
     * Entry point for rounds being finished notifications.
     *
     * @param agent
     *          the agent who finished the round
     */
    void notifyRoundFinished(final Agent agent);

}
