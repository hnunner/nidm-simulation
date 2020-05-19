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
public class LocalAgentConnectionsStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int m;
    private final int mS;
    private final int mI;
    private final int mR;
    private final int y;
    private final int z;
    private final int netSize;

    /**
     * Constructor.
     *
     * @param n
     *          the number of direct ties (irrespective of disease state)
     * @param nS
     *          the number of direct susceptible ties
     * @param nI
     *          the number of direct infected ties
     * @param nR
     *          the number of direct recovered ties
     * @param m
     *          the number of ties at distance 2 (irrespective of disease state)
     * @param mS
     *          the number of susceptible ties at distance 2
     * @param mI
     *          the number of infected ties at distance 2
     * @param mR
     *          the number of recovered ties at distance 2
     * @param y
     *          the amount of open triads the agent is part of (ties of agent that do not share a tie between each other)
     * @param z
     *          the amount of closed triads the agent is part of
     * @param netSize
     *          the network size
     */
    public LocalAgentConnectionsStats(int n, int nS, int nI, int nR, int m, int mS, int mI, int mR, int y, int z, int netSize) {
        this.n = n;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.m = m;
        this.mS = mS;
        this.mI = mI;
        this.mR = mR;
        this.y = y;
        this.z = z;
        this.netSize = netSize;
    }


    /**
     * @return the yLocal
     */
    public int getY() {
        return y;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the netSize
     */
    public int getNetSize() {
        return netSize;
    }

    /**
     * @return the number of direct connections
     */
    public int getN() {
        return this.n;
    }

    /**
     * @return the number of susceptible direct connections
     */
    public int getnS() {
        return this.nS;
    }

    /**
     * @return the number of infected direct connections
     */
    public int getnI() {
        return this.nI;
    }

    /**
     * @return the number of recovered direct connections
     */
    public int getnR() {
        return this.nR;
    }

    /**
     * @return the number of indirect connections
     */
    public int getM() {
        return this.m;
    }

    /**
     * @return the number of susceptible indirect connections
     */
    public int getmS() {
        return this.mS;
    }

    /**
     * @return the number of infected indirect connections
     */
    public int getmI() {
        return this.mI;
    }

    /**
     * @return the number of recovered indirect connections
     */
    public int getmR() {
        return this.mR;
    }

}
