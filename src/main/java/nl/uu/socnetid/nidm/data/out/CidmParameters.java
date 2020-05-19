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
public class CidmParameters extends UtilityModelParameters {

    // social benefits
    private double[] alphas;
    private double currAlpha;
    private double[] kappas;
    private double currKappa;
    private double[] betas;
    private double currBeta;
    private double[] lamdas;
    private double currLamda;
    // social maintenance costs
    private double[] cs;
    private double currC;
    private double[] mus;
    private double currMu;


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
     * @return the kappas
     */
    public double[] getKappas() {
        return kappas;
    }

    /**
     * @param kappas the kappas to set
     */
    public void setKappas(double[] kappas) {
        this.kappas = kappas;
    }

    /**
     * @return the betas
     */
    public double[] getBetas() {
        return betas;
    }

    /**
     * @param betas the betas to set
     */
    public void setBetas(double[] betas) {
        this.betas = betas;
    }

    /**
     * @return the lamdas
     */
    public double[] getLamdas() {
        return lamdas;
    }

    /**
     * @param lamdas the lamdas to set
     */
    public void setLamdas(double[] lamdas) {
        this.lamdas = lamdas;
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
     * @return the mus
     */
    public double[] getMus() {
        return mus;
    }

    /**
     * @param mus the mus to set
     */
    public void setMus(double[] mus) {
        this.mus = mus;
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
     * @return the currKappa
     */
    public double getCurrKappa() {
        return currKappa;
    }

    /**
     * @param currKappa the currKappa to set
     */
    public void setCurrKappa(double currKappa) {
        this.currKappa = currKappa;
    }

    /**
     * @return the currBeta
     */
    public double getCurrBeta() {
        return currBeta;
    }

    /**
     * @param currBeta the currBeta to set
     */
    public void setCurrBeta(double currBeta) {
        this.currBeta = currBeta;
    }

    /**
     * @return the currLamda
     */
    public double getCurrLamda() {
        return currLamda;
    }

    /**
     * @param currLamda the currLamda to set
     */
    public void setCurrLamda(double currLamda) {
        this.currLamda = currLamda;
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

    /**
     * @return the currMu
     */
    public double getCurrMu() {
        return currMu;
    }

    /**
     * @param currMu the currMu to set
     */
    public void setCurrMu(double currMu) {
        this.currMu = currMu;
    }

}
