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

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class NetworkStatsPre {

    private Network network;

    private final boolean stable;
    private final double assortativity;
    private final double avDegree;
    private final double avBetweenness;
    private final double avCloseness;
    private final double avClustering;
    private final double avPathLength;


    public NetworkStatsPre(Network network, int simRound) {
        this.network = network;
        this.stable = network.isStable();
        this.assortativity = network.getAssortativity(simRound);
        this.avDegree = network.getAvDegree(simRound);
        this.avBetweenness = network.getAvBetweenness(simRound);
        this.avCloseness = network.getAvCloseness(simRound);
        this.avClustering = network.getAvClustering(simRound);
        this.avPathLength = network.getAvPathLength(simRound);
    }

    /**
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @return the stable
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * @return the assortativity
     */
    public double getAssortativity() {
        return assortativity;
    }

    /**
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @return the avBetweenness
     */
    public Double getAvBetweenness() {
        return avBetweenness;
    }

    /**
     * @return the avCloseness
     */
    public Double getAvCloseness() {
        return avCloseness;
    }

    /**
     * @return the avClustering
     */
    public double getAvClustering() {
        return avClustering;
    }

    /**
     * @return the avPathLength
     */
    public Double getAvPathLength() {
        return avPathLength;
    }

}
