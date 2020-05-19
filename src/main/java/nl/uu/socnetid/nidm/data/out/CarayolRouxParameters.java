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
package nl.uu.socnetid.nidm.data.out;

/**
 * @author Hendrik Nunner
 */
public class CarayolRouxParameters extends UtilityModelParameters {

    // social benefits
    private boolean crOmegaRandom;
    private double crOmegaRandomMin;
    private double crOmegaRandomMax;
    private double[] crOmegas;
    private double currCrOmega;
    private boolean deltaRandom;
    private double deltaRandomMin;
    private double deltaRandomMax;
    private double[] deltas;
    private double currDelta;
    // social costs
    private boolean cRandom;
    private double cRandomMin;
    private double cRandomMax;
    private double[] cs;
    private double currC;


    /**
     * @return the crOmegaRandom
     */
    public boolean isCrOmegaRandom() {
        return crOmegaRandom;
    }

    /**
     * @param crOmegaRandom the crOmegaRandom to set
     */
    public void setCrOmegaRandom(boolean crOmegaRandom) {
        this.crOmegaRandom = crOmegaRandom;
    }

    /**
     * @return the crOmegaRandomMin
     */
    public double getCrOmegaRandomMin() {
        return crOmegaRandomMin;
    }

    /**
     * @param crOmegaRandomMin the omegaRandomMin to set
     */
    public void setCrOmegaRandomMin(double crOmegaRandomMin) {
        this.crOmegaRandomMin = crOmegaRandomMin;
    }

    /**
     * @return the crOmegaRandomMax
     */
    public double getCrOmegaRandomMax() {
        return crOmegaRandomMax;
    }

    /**
     * @param crOmegaRandomMax the crOmegaRandomMax to set
     */
    public void setCrOmegaRandomMax(double crOmegaRandomMax) {
        this.crOmegaRandomMax = crOmegaRandomMax;
    }

    /**
     * @return the crOmegas
     */
    public double[] getCrOmegas() {
        return crOmegas;
    }

    /**
     * @param crOmegas the omegas to set
     */
    public void setCrOmegas(double[] crOmegas) {
        this.crOmegas = crOmegas;
    }

    /**
     * @return the currCrOmega
     */
    public double getCurrCrOmega() {
        return currCrOmega;
    }

    /**
     * @param currCrOmega the currOmega to set
     */
    public void setCurrCrOmega(double currCrOmega) {
        this.currCrOmega = currCrOmega;
    }

    /**
     * @return the deltaRandom
     */
    public boolean isDeltaRandom() {
        return deltaRandom;
    }

    /**
     * @param deltaRandom the deltaRandom to set
     */
    public void setDeltaRandom(boolean deltaRandom) {
        this.deltaRandom = deltaRandom;
    }

    /**
     * @return the deltaRandomMin
     */
    public double getDeltaRandomMin() {
        return deltaRandomMin;
    }

    /**
     * @param deltaRandomMin the deltaRandomMin to set
     */
    public void setDeltaRandomMin(double deltaRandomMin) {
        this.deltaRandomMin = deltaRandomMin;
    }

    /**
     * @return the deltaRandomMax
     */
    public double getDeltaRandomMax() {
        return deltaRandomMax;
    }

    /**
     * @param deltaRandomMax the deltaRandomMax to set
     */
    public void setDeltaRandomMax(double deltaRandomMax) {
        this.deltaRandomMax = deltaRandomMax;
    }

    /**
     * @return the deltas
     */
    public double[] getDeltas() {
        return deltas;
    }

    /**
     * @param deltas the deltas to set
     */
    public void setDeltas(double[] deltas) {
        this.deltas = deltas;
    }

    /**
     * @return the currDelta
     */
    public double getCurrDelta() {
        return currDelta;
    }

    /**
     * @param currDelta the currDelta to set
     */
    public void setCurrDelta(double currDelta) {
        this.currDelta = currDelta;
    }

    /**
     * @return the cRandom
     */
    public boolean isCRandom() {
        return cRandom;
    }

    /**
     * @param cRandom the cRandom to set
     */
    public void setCRandom(boolean cRandom) {
        this.cRandom = cRandom;
    }

    /**
     * @return the cRandomMin
     */
    public double getCRandomMin() {
        return cRandomMin;
    }

    /**
     * @param cRandomMin the cRandomMin to set
     */
    public void setCRandomMin(double cRandomMin) {
        this.cRandomMin = cRandomMin;
    }

    /**
     * @return the cRandomMax
     */
    public double getCRandomMax() {
        return cRandomMax;
    }

    /**
     * @param cRandomMax the cRandomMax to set
     */
    public void setCRandomMax(double cRandomMax) {
        this.cRandomMax = cRandomMax;
    }

    /**
     * @return the cs
     */
    public double[] getCs() {
        return cs;
    }

    /**
     * @param cs the cs to set
     */
    public void setCs(double[] cs) {
        this.cs = cs;
    }

    /**
     * @return the currC
     */
    public double getCurrC() {
        return currC;
    }

    /**
     * @param currC the currC to set
     */
    public void setCurrC(double currC) {
        this.currC = currC;
    }

}
