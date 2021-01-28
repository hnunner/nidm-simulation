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
public class GlobalAgentStats {

    private final int n;
    private final int nS;
    private final int nI;
    private final int nR;
    private final int nV;
    private final int nRSigmaAverse;
    private final int nRSigmaNeutral;
    private final int nRSigmaSeeking;
    private final double avRSigma;
    private final int nRPiAverse;
    private final int nRPiNeutral;
    private final int nRPiSeeking;
    private final double avRPi;

    /**
     * Constructor.
     *
     * @param n
     *          the overall amount of agents
     * @param nS
     *          the amount of susceptibles
     * @param nI
     *          the amount of infected
     * @param nR
     *          the amount of recovered
     * @param nV
     *          the amount of vaccinated
     * @param nRSigmaAverse
     *          the amount of risk averse (disease severity)
     * @param nRSigmaNeutral
     *          the amount of risk neutral (disease severity)
     * @param nRSigmaSeeking
     *          the amount of risk seeking (disease severity)
     * @param avRSigma
     *          the average risk factor (disease severity)
     * @param nRPiAverse
     *          the amount of risk averse (probability of infection)
     * @param nRPiNeutral
     *          the amount of risk neutral (probability of infection)
     * @param nRPiSeeking
     *          the amount of risk seeking (probability of infection)
     * @param avRPi
     *          the average risk factor (probability of infection)
     */
    public GlobalAgentStats(int n, int nS, int nI, int nR, int nV,
            int nRSigmaAverse, int nRSigmaNeutral, int nRSigmaSeeking, double avRSigma,
            int nRPiAverse, int nRPiNeutral, int nRPiSeeking, double avRPi) {
        this.n = n;
        this.nS = nS;
        this.nI = nI;
        this.nR = nR;
        this.nV = nV;
        this.nRSigmaAverse = nRSigmaAverse;
        this.nRSigmaNeutral = nRSigmaNeutral;
        this.nRSigmaSeeking = nRSigmaSeeking;
        this.avRSigma= avRSigma;
        this.nRPiAverse = nRPiAverse;
        this.nRPiNeutral = nRPiNeutral;
        this.nRPiSeeking = nRPiSeeking;
        this.avRPi = avRPi;
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
     * @return the nRSigmaAverse
     */
    public int getnRSigmaAverse() {
        return nRSigmaAverse;
    }

    /**
     * @return the nRSigmaNeutral
     */
    public int getnRSigmaNeutral() {
        return nRSigmaNeutral;
    }

    /**
     * @return the nRSigmaSeeking
     */
    public int getnRSigmaSeeking() {
        return nRSigmaSeeking;
    }

    /**
     * @return the avRSigma
     */
    public double getAvRSigma() {
        return avRSigma;
    }

    /**
     * @return the nRPiAverse
     */
    public int getnRPiAverse() {
        return nRPiAverse;
    }

    /**
     * @return the nRPiNeutral
     */
    public int getnRPiNeutral() {
        return nRPiNeutral;
    }

    /**
     * @return the nRPiSeeking
     */
    public int getnRPiSeeking() {
        return nRPiSeeking;
    }

    /**
     * @return the avRPi
     */
    public double getAvRPi() {
        return avRPi;
    }

    /**
     * @return the nV
     */
    public int getnV() {
        return nV;
    }

}
