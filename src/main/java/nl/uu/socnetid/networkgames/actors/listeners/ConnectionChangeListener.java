package nl.uu.socnetid.networkgames.actors.listeners;

import org.graphstream.graph.Edge;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public interface ConnectionChangeListener {

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
     * Entry point for connections removed by an actor.
     *
     * @param actor
     *          the actor removing a connection
     */
    void notifyConnectionRemoved(final Actor actor);

    /**
     * Entry point for edge removed between two actors notifications.
     *
     * @param edge
     *          the edge
     */
    void notifyEdgeRemoved(final Edge edge);

}
