/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.io.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class EdgeListWriter implements NetworkCSVWriter {

    private static final String SOURCE_COLUMN = "Source";
    private static final String TARGET_COLUMN = "Target";

    private Iterator<Agent> agentsIt = null;


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.writer.NetworkWriter#
     *              write(nl.uu.socnetid.nidm.networks.Network)
     */
    @Override
    public String write(Network network) {
        return this.write(network, network.getAgents().size());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.writer.NetworkWriter#
     *              write(nl.uu.socnetid.nidm.networks.Network)
     */
    @Override
    public String write(Network network, int numAgents) {

        StringBuilder sb = new StringBuilder();

        if (agentsIt == null) {
            // first row
            sb.append(SOURCE_COLUMN).append(VALUE_SEPERATOR).append(TARGET_COLUMN);
            sb.append(System.getProperty("line.separator"));

            // prepare iterator
            List<Agent> agents = new ArrayList<Agent>(network.getAgents());
            Collections.sort(agents);
            this.agentsIt = agents.iterator();
        }

        int currNumAgents = 0;
        while (agentsIt.hasNext() && currNumAgents <= numAgents) {
            Agent currAgent = agentsIt.next();

            List<Agent> connections = currAgent.getConnections();
            Collections.sort(connections);
            Iterator<Agent> connectionsIt = connections.iterator();
            while (connectionsIt.hasNext()) {
                Agent currConnection = connectionsIt.next();
                sb.append(currAgent.getId()).append(VALUE_SEPERATOR).append(currConnection.getId());
                sb.append(System.getProperty("line.separator"));
            }
            currNumAgents++;
        }

        return sb.toString();
    }

}
