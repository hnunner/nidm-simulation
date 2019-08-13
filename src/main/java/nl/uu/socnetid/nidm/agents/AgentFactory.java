package nl.uu.socnetid.nidm.agents;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public final class AgentFactory implements NodeFactory<Agent> {

    /* (non-Javadoc)
     * @see org.graphstream.graph.NodeFactory#newInstance(java.lang.String, org.graphstream.graph.Graph)
     */
    @Override
    public Agent newInstance(String id, Graph graph) {
        return new Agent(id, (Network) graph);
    }

}
