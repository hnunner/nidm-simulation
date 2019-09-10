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
package nl.uu.socnetid.nidm.stats;

/**
 * @author Hendrik Nunner
 */
public class GlobalNetworkStats {

    private final boolean stable;
    private final int connections;
    private final double avDegree;
    private final int diameter;
    private final double avDistance;

    /**
     * Constructor
     *
     * @param stable
     *          flag whether the network is stable
     * @param connections
     *          the amount of connections within the network
     * @param avDegree
     *          the average degree within the network
     * @param diameter
     *          the diameter of the network
     * @param avDistance
     *          the average distance of the network
     */
    public GlobalNetworkStats(boolean stable, int connections, double avDegree, int diameter, double avDistance) {
        this.stable = stable;
        this.connections = connections;
        this.avDegree = avDegree;
        this.diameter = diameter;
        this.avDistance = avDistance;
    }


    /**
     * @return the stable
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * @return the connections
     */
    public int getConnections() {
        return connections;
    }

    /**
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @return the diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * @return the avDistance
     */
    public double getAvDistance() {
        return avDistance;
    }

}
