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
