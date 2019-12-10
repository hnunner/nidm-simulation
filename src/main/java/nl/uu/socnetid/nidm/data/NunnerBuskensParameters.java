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
package nl.uu.socnetid.nidm.data;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensParameters extends UtilityModelParameters {

    // network structure static during epidemics
    private boolean epStaticRandom;
    private boolean[] epStatics;
    private boolean currEpStatic;
    // direct ties - benefit weight
    private boolean b1Random;
    private double b1RandomMin;
    private double b1RandomMax;
    private double[] b1s;
    private double currB1;
    // triadic closure - benefit weight
    private boolean b2Random;
    private double b2RandomMin;
    private double b2RandomMax;
    private double[] b2s;
    private double currB2;
    // open vs. closed
    private boolean alphaRandom;
    private double alphaRandomMin;
    private double alphaRandomMax;
    private double[] alphas;
    private double currAlpha;

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

    // yGlobal
    private boolean yGlobalRandom;
    private boolean[] yGlobals;
    private boolean currYGlobal;


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
     * @return the alphaRandom
     */
    public boolean isAlphaRandom() {
        return alphaRandom;
    }

    /**
     * @param alphaRandom the alphaRandom to set
     */
    public void setAlphaRandom(boolean alphaRandom) {
        this.alphaRandom = alphaRandom;
    }

    /**
     * @return the alphaRandomMin
     */
    public double getAlphaRandomMin() {
        return alphaRandomMin;
    }

    /**
     * @param alphaRandomMin the alphaRandomMin to set
     */
    public void setAlphaRandomMin(double alphaRandomMin) {
        this.alphaRandomMin = alphaRandomMin;
    }

    /**
     * @return the alphaRandomMax
     */
    public double getAlphaRandomMax() {
        return alphaRandomMax;
    }

    /**
     * @param alphaRandomMax the alphaRandomMax to set
     */
    public void setAlphaRandomMax(double alphaRandomMax) {
        this.alphaRandomMax = alphaRandomMax;
    }

    /**
     * @return the alphas
     */
    public double[] getAlphas() {
        return alphas;
    }

    /**
     * @param alphas the alphas to set
     */
    public void setAlphas(double[] alphas) {
        this.alphas = alphas;
    }

    /**
     * @return the currAlpha
     */
    public double getCurrAlpha() {
        return currAlpha;
    }

    /**
     * @param currAlpha the currAlpha to set
     */
    public void setCurrAlpha(double currAlpha) {
        this.currAlpha = currAlpha;
    }

    /**
     * @param b2RandomMax the b2RandomMax to set
     */
    public void setB2RandomMax(double b2RandomMax) {
        this.b2RandomMax = b2RandomMax;
    }

    /**
     * @return the yGlobalRandom
     */
    public boolean isYGlobalRandom() {
        return yGlobalRandom;
    }

    /**
     * @param yGlobalRandom the yGlobalRandom to set
     */
    public void setYGlobalRandom(boolean yGlobalRandom) {
        this.yGlobalRandom = yGlobalRandom;
    }

    /**
     * @return the yGlobals
     */
    public boolean[] getYGlobals() {
        return yGlobals;
    }

    /**
     * @param yGlobals the yGlobals to set
     */
    public void setYGlobals(boolean[] yGlobals) {
        this.yGlobals = yGlobals;
    }

    /**
     * @return the currYGlobal
     */
    public boolean isCurrYGlobal() {
        return currYGlobal;
    }

    /**
     * @param currYGlobal the currYGlobal to set
     */
    public void setCurrYGlobal(boolean currYGlobal) {
        this.currYGlobal = currYGlobal;
    }

    /**
     * @return the epStaticRandom
     */
    public boolean isEpStaticRandom() {
        return epStaticRandom;
    }

    /**
     * @param epStaticRandom the epStaticRandom to set
     */
    public void setEpStaticRandom(boolean epStaticRandom) {
        this.epStaticRandom = epStaticRandom;
    }

    /**
     * @return the epStatics
     */
    public boolean[] getEpStatics() {
        return epStatics;
    }

    /**
     * @param epStatics the epStatics to set
     */
    public void setEpStatics(boolean[] epStatics) {
        this.epStatics = epStatics;
    }

    /**
     * @return the currEpStatic
     */
    public boolean isCurrEpStatic() {
        return currEpStatic;
    }

    /**
     * @param currEpStatic the currEpStatic to set
     */
    public void setCurrEpStatic(boolean currEpStatic) {
        this.currEpStatic = currEpStatic;
    }

    /**
     * @return the yGlobalRandom
     */
    public boolean isyGlobalRandom() {
        return yGlobalRandom;
    }

    /**
     * @param yGlobalRandom the yGlobalRandom to set
     */
    public void setyGlobalRandom(boolean yGlobalRandom) {
        this.yGlobalRandom = yGlobalRandom;
    }

    /**
     * @return the yGlobals
     */
    public boolean[] getyGlobals() {
        return yGlobals;
    }

    /**
     * @param yGlobals the yGlobals to set
     */
    public void setyGlobals(boolean[] yGlobals) {
        this.yGlobals = yGlobals;
    }

}
