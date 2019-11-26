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
public abstract class UtilityModelParameters {

    // NETWORK
    // N
    private boolean NRandom;
    private int NRandomMin;
    private int NRandomMax;
    private int[] Ns;
    private int currN;
    // iota - start with full network
    private boolean iotaRandom;
    private boolean[] iotas;
    private boolean currIota;
    // phi - share of agents to evaluate per time step
    private boolean phiRandom;
    private double phiRandomMin;
    private double phiRandomMax;
    private double[] phis;
    private double currPhi;
    // phi - share of agents to select assortatively
    private boolean omegaRandom;
    private double omegaRandomMin;
    private double omegaRandomMax;
    private double[] omegas;
    private double currOmega;

    // INFECTIONS
    // sigma - severity
    private double[] sigmas;
    private double currSigma;
    // gamma - probability of getting infected per contact
    private double[] gammas;
    private double currGamma;
    // r - risk perception (rSigma - perceived disease severity, rPi - perceived susceptibility)
    private boolean rsEqual;
    private double[] rSigmas;
    private double currRSigma;
    private double[] rPis;
    private double currRPi;
    // recovery time in timesteps
    private int[] taus;
    private int currTau;

    // SIMULATION
    // time steps network initialization stage
    private int zeta;
    // time steps epidemic stage
    private int epsilon;
    // simulations per parameter combination
    private int simsPerParameterCombination;


    /**
     * @return the nRandom
     */
    public boolean isNRandom() {
        return NRandom;
    }

    /**
     * @param nRandom the nRandom to set
     */
    public void setNRandom(boolean nRandom) {
        NRandom = nRandom;
    }

    /**
     * @return the nRandomMin
     */
    public int getNRandomMin() {
        return NRandomMin;
    }

    /**
     * @param nRandomMin the nRandomMin to set
     */
    public void setNRandomMin(int nRandomMin) {
        NRandomMin = nRandomMin;
    }

    /**
     * @return the nRandomMax
     */
    public int getNRandomMax() {
        return NRandomMax;
    }

    /**
     * @param nRandomMax the nRandomMax to set
     */
    public void setNRandomMax(int nRandomMax) {
        NRandomMax = nRandomMax;
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
    public int getCurrN() {
        return currN;
    }

    /**
     * @param currN the currN to set
     */
    public void setCurrN(int currN) {
        this.currN = currN;
    }

    /**
     * @return the iotaRandom
     */
    public boolean isIotaRandom() {
        return iotaRandom;
    }

    /**
     * @param iotaRandom the iotaRandom to set
     */
    public void setIotaRandom(boolean iotaRandom) {
        this.iotaRandom = iotaRandom;
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
     * @return the phiRandom
     */
    public boolean isPhiRandom() {
        return phiRandom;
    }

    /**
     * @param phiRandom the phiRandom to set
     */
    public void setPhiRandom(boolean phiRandom) {
        this.phiRandom = phiRandom;
    }

    /**
     * @return the phiRandomMin
     */
    public double getPhiRandomMin() {
        return phiRandomMin;
    }

    /**
     * @param phiRandomMin the phiRandomMin to set
     */
    public void setPhiRandomMin(double phiRandomMin) {
        this.phiRandomMin = phiRandomMin;
    }

    /**
     * @return the phiRandomMax
     */
    public double getPhiRandomMax() {
        return phiRandomMax;
    }

    /**
     * @param phiRandomMax the phiRandomMax to set
     */
    public void setPhiRandomMax(double phiRandomMax) {
        this.phiRandomMax = phiRandomMax;
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
     * @return the omegaRandom
     */
    public boolean isOmegaRandom() {
        return omegaRandom;
    }

    /**
     * @param omegaRandom the omegaRandom to set
     */
    public void setOmegaRandom(boolean omegaRandom) {
        this.omegaRandom = omegaRandom;
    }

    /**
     * @return the omegaRandomMin
     */
    public double getOmegaRandomMin() {
        return omegaRandomMin;
    }

    /**
     * @param omegaRandomMin the omegaRandomMin to set
     */
    public void setOmegaRandomMin(double omegaRandomMin) {
        this.omegaRandomMin = omegaRandomMin;
    }

    /**
     * @return the omegaRandomMax
     */
    public double getOmegaRandomMax() {
        return omegaRandomMax;
    }

    /**
     * @param omegaRandomMax the omegaRandomMax to set
     */
    public void setOmegaRandomMax(double omegaRandomMax) {
        this.omegaRandomMax = omegaRandomMax;
    }

    /**
     * @return the omegas
     */
    public double[] getOmegas() {
        return omegas;
    }

    /**
     * @param omegas the omegas to set
     */
    public void setOmegas(double[] omegas) {
        this.omegas = omegas;
    }

    /**
     * @return the currOmega
     */
    public double getCurrOmega() {
        return currOmega;
    }

    /**
     * @param currOmega the currOmega to set
     */
    public void setCurrOmega(double currOmega) {
        this.currOmega = currOmega;
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
     * @return the rsEqual
     */
    public boolean isRsEqual() {
        return rsEqual;
    }

    /**
     * @param rsEqual the rsEqual to set
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

}
