package nl.uu.socnetid.netgame.io.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.netgame.agents.Agent;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriter implements NetworkWriter {

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.NetworkWriter#write()
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
            sb.append(",").append(PLAYER_PREFIX).append(currAgent.getId());
        }
        sb.append(System.getProperty("line.separator"));

        // succeding rows = all connections of corresponding agent
        agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();
            sb.append(PLAYER_PREFIX).append(currAgent.getId());

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
