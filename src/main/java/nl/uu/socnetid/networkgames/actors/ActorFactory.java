package nl.uu.socnetid.networkgames.actors;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public final class ActorFactory implements NodeFactory<Actor> {

    /* (non-Javadoc)
     * @see org.graphstream.graph.NodeFactory#newInstance(java.lang.String, org.graphstream.graph.Graph)
     */
    @Override
    public Actor newInstance(String id, Graph graph) {
        return new Actor(id, (Network) graph);
    }

}
