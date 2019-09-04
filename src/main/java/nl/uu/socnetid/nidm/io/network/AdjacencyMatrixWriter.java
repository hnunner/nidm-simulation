package nl.uu.socnetid.nidm.io.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriter implements NetworkWriter {

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.NetworkWriter#write()
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        List<Agent> agents = new ArrayList<Agent>(network.getAgents());
        Collections.sort(agents);

        // first row = all agents
        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();
            sb.append(",").append(AGENT_PREFIX).append(currAgent.getId());
        }
        sb.append(System.getProperty("line.separator"));

        // succeding rows = all connections of corresponding agent
        agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();
            sb.append(AGENT_PREFIX).append(currAgent.getId());

            Collection<Agent> connections = currAgent.getConnections();

            Iterator<Agent> connectionsIt = agents.iterator();
            while (connectionsIt.hasNext()) {
                Agent currConnection = connectionsIt.next();
                if (connections.contains(currConnection)) {
                    sb.append(VALUE_SEPERATOR).append(CONNECTION);
                } else {
                    sb.append(VALUE_SEPERATOR).append(NO_CONNECTION);
                }
            }
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

}
