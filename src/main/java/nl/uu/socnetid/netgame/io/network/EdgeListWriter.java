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
public class EdgeListWriter implements NetworkWriter {

    private static final String SOURCE_COLUMN = "Source";
    private static final String TARGET_COLUMN = "Target";


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.writer.NetworkWriter#
     *              write(nl.uu.socnetid.netgame.networks.Network)
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        // first column
        sb.append(SOURCE_COLUMN).append(VALUE_SEPERATOR).append(TARGET_COLUMN);
        sb.append(System.getProperty("line.separator"));

        List<Agent> agents = new ArrayList<Agent>(network.getAgents());
        Collections.sort(agents);

        // first row = all agents
        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();

            Collection<Agent> connections = currAgent.getConnections();
            Iterator<Agent> connectionsIt = agents.iterator();
            while (connectionsIt.hasNext()) {
                Agent currConnection = connectionsIt.next();

                if (connections.contains(currConnection)) {
                    sb.append(currAgent.getId()).append(VALUE_SEPERATOR).append(currConnection.getId());
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }

        return sb.toString();
    }

}
