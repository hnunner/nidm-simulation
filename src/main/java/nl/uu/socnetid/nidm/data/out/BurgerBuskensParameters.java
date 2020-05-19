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
public class BurgerBuskensParameters extends UtilityModelParameters {

    // social benefits
    private boolean b1Random;
    private double b1RandomMin;
    private double b1RandomMax;
    private double[] b1s;
    private double currB1;
    // social costs
    private boolean c1Random;
    private double c1RandomMin;
    private double c1RandomMax;
    private double[] c1s;
    private double currC1;
    private boolean c2Random;
    private double c2RandomMin;
    private double c2RandomMax;
    private double[] c2s;
    private double currC2;
    // triadic closure
    // triadic closure - benefit (Colemanian)
    private boolean b2Random;
    private double b2RandomMin;
    private double b2RandomMax;
    private double[] b2s;
    private double currB2;
    // triadic closure - cost (Burtian)
    private boolean c3Random;
    private double c3RandomMin;
    private double c3RandomMax;
    private double[] c3s;
    private double currC3;


    /**
     * @return the b1s
     */
    public double[] getB1s() {
        return b1s;
    }

    /**
     * @param b1s the b1s to set
     */
    public void setB1s(double[] b1s) {
        this.b1s = b1s;
    }

    /**
     * @return the currB1
     */
    public double getCurrB1() {
        return currB1;
    }

    /**
     * @param currB1 the currB1 to set
     */
    public void setCurrB1(double currB1) {
        this.currB1 = currB1;
    }

    /**
     * @return the b2s
     */
    public double[] getB2s() {
        return b2s;
    }

    /**
     * @param b2s the b2s to set
     */
    public void setB2s(double[] b2s) {
        this.b2s = b2s;
    }

    /**
     * @return the currB2
     */
    public double getCurrB2() {
        return currB2;
    }

    /**
     * @param currB2 the currB2 to set
     */
    public void setCurrB2(double currB2) {
        this.currB2 = currB2;
    }

    /**
     * @return the c1s
     */
    public double[] getC1s() {
        return c1s;
    }

    /**
     * @param c1s the c1s to set
     */
    public void setC1s(double[] c1s) {
        this.c1s = c1s;
    }

    /**
     * @return the currC1
     */
    public double getCurrC1() {
        return currC1;
    }

    /**
     * @param currC1 the currC1 to set
     */
    public void setCurrC1(double currC1) {
        this.currC1 = currC1;
    }

    /**
     * @return the c2s
     */
    public double[] getC2s() {
        return c2s;
    }

    /**
     * @param c2s the c2s to set
     */
    public void setC2s(double[] c2s) {
        this.c2s = c2s;
    }

    /**
     * @return the currC2
     */
    public double getCurrC2() {
        return currC2;
    }

    /**
     * @param currC2 the currC2 to set
     */
    public void setCurrC2(double currC2) {
        this.currC2 = currC2;
    }

    /**
     * @return the c3s
     */
    public double[] getC3s() {
        return c3s;
    }

    /**
     * @param c3s the c3s to set
     */
    public void setC3s(double[] c3s) {
        this.c3s = c3s;
    }

    /**
     * @return the currC3
     */
    public double getCurrC3() {
        return currC3;
    }

    /**
     * @param currC3 the currC3 to set
     */
    public void setCurrC3(double currC3) {
        this.currC3 = currC3;
    }

    /**
     * @return the b1Random
     */
    public boolean isB1Random() {
        return b1Random;
    }

    /**
     * @param b1Random the b1Random to set
     */
    public void setB1Random(boolean b1Random) {
        this.b1Random = b1Random;
    }

    /**
     * @return the c1Random
     */
    public boolean isC1Random() {
        return c1Random;
    }

    /**
     * @param c1Random the c1Random to set
     */
    public void setC1Random(boolean c1Random) {
        this.c1Random = c1Random;
    }

    /**
     * @return the c2Random
     */
    public boolean isC2Random() {
        return c2Random;
    }

    /**
     * @param c2Random the c2Random to set
     */
    public void setC2Random(boolean c2Random) {
        this.c2Random = c2Random;
    }

    /**
     * @return the b2Random
     */
    public boolean isB2Random() {
        return b2Random;
    }

    /**
     * @param b2Random the b2Random to set
     */
    public void setB2Random(boolean b2Random) {
        this.b2Random = b2Random;
    }

    /**
     * @return the c3Random
     */
    public boolean isC3Random() {
        return c3Random;
    }

    /**
     * @param c3Random the c3Random to set
     */
    public void setC3Random(boolean c3Random) {
        this.c3Random = c3Random;
    }

    /**
     * @return the b1RandomMin
     */
    public double getB1RandomMin() {
        return b1RandomMin;
    }

    /**
     * @param b1RandomMin the b1RandomMin to set
     */
    public void setB1RandomMin(double b1RandomMin) {
        this.b1RandomMin = b1RandomMin;
    }

    /**
     * @return the b1RandomMax
     */
    public double getB1RandomMax() {
        return b1RandomMax;
    }

    /**
     * @param b1RandomMax the b1RandomMax to set
     */
    public void setB1RandomMax(double b1RandomMax) {
        this.b1RandomMax = b1RandomMax;
    }

    /**
     * @return the c1RandomMin
     */
    public double getC1RandomMin() {
        return c1RandomMin;
    }

    /**
     * @param c1RandomMin the c1RandomMin to set
     */
    public void setC1RandomMin(double c1RandomMin) {
        this.c1RandomMin = c1RandomMin;
    }

    /**
     * @return the c1RandomMax
     */
    public double getC1RandomMax() {
        return c1RandomMax;
    }

    /**
     * @param c1RandomMax the c1RandomMax to set
     */
    public void setC1RandomMax(double c1RandomMax) {
        this.c1RandomMax = c1RandomMax;
    }

    /**
     * @return the c2RandomMin
     */
    public double getC2RandomMin() {
        return c2RandomMin;
    }

    /**
     * @param c2RandomMin the c2RandomMin to set
     */
    public void setC2RandomMin(double c2RandomMin) {
        this.c2RandomMin = c2RandomMin;
    }

    /**
     * @return the c2RandomMax
     */
    public double getC2RandomMax() {
        return c2RandomMax;
    }

    /**
     * @param c2RandomMax the c2RandomMax to set
     */
    public void setC2RandomMax(double c2RandomMax) {
        this.c2RandomMax = c2RandomMax;
    }

    /**
     * @return the b2RandomMin
     */
    public double getB2RandomMin() {
        return b2RandomMin;
    }

    /**
     * @param b2RandomMin the b2RandomMin to set
     */
    public void setB2RandomMin(double b2RandomMin) {
        this.b2RandomMin = b2RandomMin;
    }

    /**
     * @return the b2RandomMax
     */
    public double getB2RandomMax() {
        return b2RandomMax;
    }

    /**
     * @param b2RandomMax the b2RandomMax to set
     */
    public void setB2RandomMax(double b2RandomMax) {
        this.b2RandomMax = b2RandomMax;
    }

    /**
     * @return the c3RandomMin
     */
    public double getC3RandomMin() {
        return c3RandomMin;
    }

    /**
     * @param c3RandomMin the c3RandomMin to set
     */
    public void setC3RandomMin(double c3RandomMin) {
        this.c3RandomMin = c3RandomMin;
    }

    /**
     * @return the c3RandomMax
     */
    public double getC3RandomMax() {
        return c3RandomMax;
    }

    /**
     * @param c3RandomMax the c3RandomMax to set
     */
    public void setC3RandomMax(double c3RandomMax) {
        this.c3RandomMax = c3RandomMax;
    }

}
