package nl.uu.socnetid.netgame.actors;

import org.graphstream.graph.Edge;

/**
 * @author Hendrik Nunner
 */
public interface ActorListener {

    /**
     * Entry point for actor attributes being added notifications.
     *
     * @param actor
     *          the actor
     * @param attribute
     *          the attribute
     * @param value
     *          the attribute's value
     */
    void notifyAttributeAdded(final Actor actor, final String attribute, final Object value);

    /**
     * Entry point for actor attributes being changed notifications.
     *
     * @param actor
     *          the actor
     * @param attribute
     *          the attribute
     * @param oldValue
     *          the attribute's old value
     * @param newValue
     *          the attribute's new value
     */
    void notifyAttributeChanged(final Actor actor, final String attribute, final Object oldValue,
            final Object newValue);

    /**
     * Entry point for actor attributes being removed notifications.
     *
     * @param actor
     *          the actor
     * @param attribute
     *          the attribute
     */
    void notifyAttributeRemoved(final Actor actor, final String attribute);

    /**
     * Entry point for connection added between two actors notifications.
     *
     * @param edge
     *          the connection between the two actors
     * @param actor1
     *          the first actor
     * @param actor2
     *          the second actor
     */
    void notifyConnectionAdded(final Edge edge, final Actor actor1, final Actor actor2);

    /**
     * Entry point for edge removed between two actors notifications.
     *
     * @param actor
     *          the actor removing a connection
     * @param edge
     *          the edge
     */
    void notifyConnectionRemoved(final Actor actor, final Edge edge);

    /**
     * Entry point for rounds being finished notifications.
     *
     * @param actor
     *          the actor who finished the round
     */
    void notifyRoundFinished(final Actor actor);

}
