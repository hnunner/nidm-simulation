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
public class CidmParameters implements UtilityModelParameters {

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
    // potential harm of infections (currently: r_pi == r_sigma)
    private double[] sigmas;
    private double currSigma;
    private double[] gammas;
    private double currGamma;
    private boolean rsEqual;
    private double[] rSigmas;
    private double currRSigma;
    private double[] rPis;
    private double currRPi;
    // network
    private int[] Ns;
    private double currN;
    private boolean[] iotas;
    private boolean currIota;
    private double[] phis;
    private double currPhi;
    // simulation
    private int zeta;
    private int epsilon;
    private int[] taus;
    private int currTau;
    private int simsPerParameterCombination;


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
     * @return the sigmas
     */
    public double[] getSigmas() {
        return sigmas;
    }

    /**
     * @param sigmas the sigmas to set
     */
    public void setSigmas(double[] sigmas) {
        this.sigmas = sigmas;
    }

    /**
     * @return the gammas
     */
    public double[] getGammas() {
        return gammas;
    }

    /**
     * @param gammas the gammas to set
     */
    public void setGammas(double[] gammas) {
        this.gammas = gammas;
    }

    /**
     * @return the rsEqual
     */
    public boolean isRsEqual() {
        return rsEqual;
    }

    /**
     * @param rsEqual the rsCombined to set
     */
    public void setRsEqual(boolean rsEqual) {
        this.rsEqual = rsEqual;
    }

    /**
     * @return the rSigmas
     */
    public double[] getRSigmas() {
        return rSigmas;
    }

    /**
     * @param rSigmas the rSigmas to set
     */
    public void setRSigmas(double[] rSigmas) {
        this.rSigmas = rSigmas;
    }

    /**
     * @return the rPis
     */
    public double[] getRPis() {
        return rPis;
    }

    /**
     * @param rPis the rPis to set
     */
    public void setRPis(double[] rPis) {
        this.rPis = rPis;
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
     * @return the zeta
     */
    public int getZeta() {
        return zeta;
    }

    /**
     * @param zeta the zeta to set
     */
    public void setZeta(int zeta) {
        this.zeta = zeta;
    }

    /**
     * @return the epsilon
     */
    public int getEpsilon() {
        return epsilon;
    }

    /**
     * @param epsilon the epsilon to set
     */
    public void setEpsilon(int epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * @return the taus
     */
    public int[] getTaus() {
        return taus;
    }

    /**
     * @param taus the taus to set
     */
    public void setTaus(int[] taus) {
        this.taus = taus;
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

    /**
     * @return the currSigma
     */
    public double getCurrSigma() {
        return currSigma;
    }

    /**
     * @param currSigma the currSigma to set
     */
    public void setCurrSigma(double currSigma) {
        this.currSigma = currSigma;
    }

    /**
     * @return the currGamma
     */
    public double getCurrGamma() {
        return currGamma;
    }

    /**
     * @param currGamma the currGamma to set
     */
    public void setCurrGamma(double currGamma) {
        this.currGamma = currGamma;
    }

    /**
     * @return the rSigmas
     */
    public double[] getrSigmas() {
        return rSigmas;
    }

    /**
     * @param rSigmas the rSigmas to set
     */
    public void setrSigmas(double[] rSigmas) {
        this.rSigmas = rSigmas;
    }

    /**
     * @return the currRSigma
     */
    public double getCurrRSigma() {
        return currRSigma;
    }

    /**
     * @param currRSigma the currRSigma to set
     */
    public void setCurrRSigma(double currRSigma) {
        this.currRSigma = currRSigma;
    }

    /**
     * @return the rPis
     */
    public double[] getrPis() {
        return rPis;
    }

    /**
     * @param rPis the rPis to set
     */
    public void setrPis(double[] rPis) {
        this.rPis = rPis;
    }

    /**
     * @return the currRPi
     */
    public double getCurrRPi() {
        return currRPi;
    }

    /**
     * @param currRPi the currRPi to set
     */
    public void setCurrRPi(double currRPi) {
        this.currRPi = currRPi;
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
     * @return the currTau
     */
    public int getCurrTau() {
        return currTau;
    }

    /**
     * @param currTau the currTau to set
     */
    public void setCurrTau(int currTau) {
        this.currTau = currTau;
    }

}
