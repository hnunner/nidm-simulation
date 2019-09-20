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
public class BurgerBuskensParameters implements UtilityModelParameters {

    // social benefits
    private double[] b1s;
    private double currB1;
    // social costs
    private double[] c1s;
    private double currC1;
    private double[] c2s;
    private double currC2;
    // triadic closure
    private boolean b2c3Random;
    // triadic closure - benefit (Colemanian)
    private double[] b2s;
    private double currB2;
    // triadic closure - cost (Burtian)
    private double[] c3s;
    private double currC3;
    // network
    private int[] Ns;
    private double currN;
    private boolean[] iotas;
    private boolean currIota;
    private double[] phis;
    private double currPhi;
    // simulation
    private int simsPerParameterCombination;


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
     * @return the ns
     */
    public int[] getNs() {
        return Ns;
    }

    /**
     * @param ns the ns to set
     */
    public void setNs(int[] ns) {
        Ns = ns;
    }

    /**
     * @return the currN
     */
    public double getCurrN() {
        return currN;
    }

    /**
     * @param currN the currN to set
     */
    public void setCurrN(double currN) {
        this.currN = currN;
    }

    /**
     * @return the iotas
     */
    public boolean[] getIotas() {
        return iotas;
    }

    /**
     * @param iotas the iotas to set
     */
    public void setIotas(boolean[] iotas) {
        this.iotas = iotas;
    }

    /**
     * @return the currIota
     */
    public boolean isCurrIota() {
        return currIota;
    }

    /**
     * @param currIota the currIota to set
     */
    public void setCurrIota(boolean currIota) {
        this.currIota = currIota;
    }

    /**
     * @return the phis
     */
    public double[] getPhis() {
        return phis;
    }

    /**
     * @param phis the phis to set
     */
    public void setPhis(double[] phis) {
        this.phis = phis;
    }

    /**
     * @return the currPhi
     */
    public double getCurrPhi() {
        return currPhi;
    }

    /**
     * @param currPhi the currPhi to set
     */
    public void setCurrPhi(double currPhi) {
        this.currPhi = currPhi;
    }

    /**
     * @return the simsPerParameterCombination
     */
    public int getSimsPerParameterCombination() {
        return simsPerParameterCombination;
    }

    /**
     * @param simsPerParameterCombination the simsPerParameterCombination to set
     */
    public void setSimsPerParameterCombination(int simsPerParameterCombination) {
        this.simsPerParameterCombination = simsPerParameterCombination;
    }

    /**
     * @return the b2c3Random
     */
    public boolean isB2c3Random() {
        return b2c3Random;
    }

    /**
     * @param b2c3Random the b2c3Random to set
     */
    public void setB2c3Random(boolean b2c3Random) {
        this.b2c3Random = b2c3Random;
    }

}
