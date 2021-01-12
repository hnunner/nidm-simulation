/*
 * Copyright (C) 2017 - 2021
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

import java.util.List;

import nl.uu.socnetid.nidm.networks.DegreeDistributionConditions;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensProfessionsParameters extends UtilityModelParameters {

    // social benefits
    private double b1;
    private double b2;
    private double alpha;

    // social costs
    private double c1;
    private double c2;

    // simulation
    private int roundsMax;

    // initializations
    private List<DegreeDistributionConditions> dccs;



    /**
     * @return the b1
     */
    public double getB1() {
        return b1;
    }

    /**
     * @param b1 the b1 to set
     */
    public void setB1(double b1) {
        this.b1 = b1;
    }

    /**
     * @return the b2
     */
    public double getB2() {
        return b2;
    }

    /**
     * @param b2 the b2 to set
     */
    public void setB2(double b2) {
        this.b2 = b2;
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the c1
     */
    public double getC1() {
        return c1;
    }

    /**
     * @param c1 the c1 to set
     */
    public void setC1(double c1) {
        this.c1 = c1;
    }

    /**
     * @return the c2
     */
    public double getC2() {
        return c2;
    }

    /**
     * @param c2 the c2 to set
     */
    public void setC2(double c2) {
        this.c2 = c2;
    }

    /**
     * @return the roundsMax
     */
    public int getRoundsMax() {
        return roundsMax;
    }

    /**
     * @param roundsMax the roundsMax to set
     */
    public void setRoundsMax(int roundsMax) {
        this.roundsMax = roundsMax;
    }

    /**
     * @return the dccs
     */
    public List<DegreeDistributionConditions> getDegreeDistributionConditions() {
        return dccs;
    }

    /**
     * @param dccs the dccs to set
     */
    public void setDegreeDistributionConditions(List<DegreeDistributionConditions> dccs) {
        this.dccs = dccs;
    }

}
