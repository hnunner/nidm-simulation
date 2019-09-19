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
    private final int z;

    /**
     * Constructor.
     *
     * @param nS
     *          the amount of susceptible direct connections
     * @param nI
     *          the amount of infected direct connections
     * @param nR
     *          the amount of recovered direct connections
     * @param mS
     *          the amount of susceptible indirect connections
     * @param mI
     *          the amount of infected indirect connections
     * @param mR
     *          the amount of recovered indirect connections
     * @param z
     *          the amount of closed triads the agent is part of
     */
    public LocalAgentConnectionsStats(int nS, int nI, int nR, int mS, int mI, int mR, int z) {
        this.n = nS + nI + nR;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.m = mS + mI + mR;
        this.mS = mS;
        this.mI = mI;
        this.mR = mR;
        this.z = z;
    }

    /**
     * @return the n
     */
    public int getN() {
        return n;
    }

    /**
     * @return the nS
     */
    public int getnS() {
        return nS;
    }

    /**
     * @return the nI
     */
    public int getnI() {
        return nI;
    }

    /**
     * @return the nR
     */
    public int getnR() {
        return nR;
    }

    /**
     * @return the m
     */
    public int getM() {
        return m;
    }

    /**
     * @return the mS
     */
    public int getmS() {
        return mS;
    }

    /**
     * @return the mI
     */
    public int getmI() {
        return mI;
    }

    /**
     * @return the mR
     */
    public int getmR() {
        return mR;
    }

    /**
     * @return the number of closed triads
     */
    public int getZ() {
        return z;
    }

}
