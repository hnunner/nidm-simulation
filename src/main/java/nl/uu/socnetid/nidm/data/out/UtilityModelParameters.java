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

import nl.uu.socnetid.nidm.networks.AssortativityConditions;

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
    // psi - proportion of direct ties to evaluate per time step
    private boolean psiRandom;
    private double psiRandomMin;
    private double psiRandomMax;
    private double[] psis;
    private double currPsi;
    // omega - share of agents to select assortatively
    private boolean omegaRandom;
    private double omegaRandomMin;
    private double omegaRandomMax;
    private double[] omegas;
    private double currOmega;
    private boolean omegaShuffleRandom;
    private boolean[] omegaShuffles;
    private boolean currOmegaShuffle;
    // ac - assortativity condition
    private AssortativityConditions ac;     // TODO move to a more appropriate place (not really a utility parameter)

    // INFECTIONS
    // sigma - severity
    private boolean sigmaRandom;
    private double sigmaRandomMin;
    private double sigmaRandomMax;
    private double[] sigmas;
    private double currSigma;
    // gamma - probability of getting infected per contact
    private boolean gammaRandom;
    private double gammaRandomMin;
    private double gammaRandomMax;
    private double[] gammas;
    private double currGamma;
    // r - risk perception (rSigma - perceived disease severity, rPi - perceived susceptibility)
    private boolean rsEqual;
    private boolean rSigmaRandom;
    private boolean[] rSigmaRandomHomogeneous;
    private boolean currRSigmaRandomHomogeneous;
    private double rSigmaRandomMin;
    private double rSigmaRandomMax;
    private double[] rSigmas;
    private double currRSigma;
    private double[] currRSigmas;
    private boolean rPiRandom;
    private boolean[] rPiRandomHomogeneous;
    private boolean currRPiRandomHomogeneous;
    private double rPiRandomMin;
    private double rPiRandomMax;
    private double[] rPis;
    private double currRPi;
    private double[] currRPis;
    // recovery time in timesteps
    private boolean tauRandom;
    private int tauRandomMin;
    private int tauRandomMax;
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
     * @return the N
     */
    public int getN() {
        return currN;
    }

    /**
     * @param currN the currN to set
     */
    public void setCurrN(int currN) {
        this.currN = currN;
    }

    /**
     * @param n the n to set
     */
    public void setN(int n) {
        this.currN = n;
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
     * @return the phi
     */
    public double getPhi() {
        return currPhi;
    }

    /**
     * @param currPhi the currPhi to set
     */
    public void setCurrPhi(double currPhi) {
        this.currPhi = currPhi;
    }

    /**
     * @param phi the phi to set
     */
    public void setPhi(double phi) {
        this.currPhi = phi;
    }

    /**
     * @return the psiRandom
     */
    public boolean isPsiRandom() {
        return psiRandom;
    }

    /**
     * @param psiRandom the psiRandom to set
     */
    public void setPsiRandom(boolean psiRandom) {
        this.psiRandom = psiRandom;
    }

    /**
     * @return the psiRandomMin
     */
    public double getPsiRandomMin() {
        return psiRandomMin;
    }

    /**
     * @param psiRandomMin the psiRandomMin to set
     */
    public void setPsiRandomMin(double psiRandomMin) {
        this.psiRandomMin = psiRandomMin;
    }

    /**
     * @return the psiRandomMax
     */
    public double getPsiRandomMax() {
        return psiRandomMax;
    }

    /**
     * @param psiRandomMax the psiRandomMax to set
     */
    public void setPsiRandomMax(double psiRandomMax) {
        this.psiRandomMax = psiRandomMax;
    }

    /**
     * @return the psis
     */
    public double[] getPsis() {
        return psis;
    }

    /**
     * @param psis the psis to set
     */
    public void setPsis(double[] psis) {
        this.psis = psis;
    }

    /**
     * @return the currPsi
     */
    public double getCurrPsi() {
        return currPsi;
    }

    /**
     * @return the psi
     */
    public double getPsi() {
        return currPsi;
    }

    /**
     * @param currPsi the currPsi to set
     */
    public void setCurrPsi(double currPsi) {
        this.currPsi = currPsi;
    }

    /**
     * @param psi the psi to set
     */
    public void setPsi(double psi) {
        this.currPsi = psi;
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
     * @return the omega
     */
    public double getOmega() {
        return currOmega;
    }

    /**
     * @param currOmega the currOmega to set
     */
    public void setCurrOmega(double currOmega) {
        this.currOmega = currOmega;
    }

    /**
     * @param omega the omega to set
     */
    public void setOmega(double omega) {
        this.currOmega = omega;
    }

    /**
     * @return the sigmaRandom
     */
    public boolean isSigmaRandom() {
        return sigmaRandom;
    }

    /**
     * @param sigmaRandom the sigmaRandom to set
     */
    public void setSigmaRandom(boolean sigmaRandom) {
        this.sigmaRandom = sigmaRandom;
    }

    /**
     * @return the sigmaRandomMin
     */
    public double getSigmaRandomMin() {
        return sigmaRandomMin;
    }

    /**
     * @param sigmaRandomMin the sigmaRandomMin to set
     */
    public void setSigmaRandomMin(double sigmaRandomMin) {
        this.sigmaRandomMin = sigmaRandomMin;
    }

    /**
     * @return the sigmaRandomMax
     */
    public double getSigmaRandomMax() {
        return sigmaRandomMax;
    }

    /**
     * @param sigmaRandomMax the sigmaRandomMax to set
     */
    public void setSigmaRandomMax(double sigmaRandomMax) {
        this.sigmaRandomMax = sigmaRandomMax;
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
     * @return the gammaRandom
     */
    public boolean isGammaRandom() {
        return gammaRandom;
    }

    /**
     * @param gammaRandom the gammaRandom to set
     */
    public void setGammaRandom(boolean gammaRandom) {
        this.gammaRandom = gammaRandom;
    }

    /**
     * @return the gammaRandomMin
     */
    public double getGammaRandomMin() {
        return gammaRandomMin;
    }

    /**
     * @param gammaRandomMin the gammaRandomMin to set
     */
    public void setGammaRandomMin(double gammaRandomMin) {
        this.gammaRandomMin = gammaRandomMin;
    }

    /**
     * @return the gammaRandomMax
     */
    public double getGammaRandomMax() {
        return gammaRandomMax;
    }

    /**
     * @param gammaRandomMax the gammaRandomMax to set
     */
    public void setGammaRandomMax(double gammaRandomMax) {
        this.gammaRandomMax = gammaRandomMax;
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
     * @return the rSigmaRandom
     */
    public boolean isRSigmaRandom() {
        return rSigmaRandom;
    }

    /**
     * @param rSigmaRandom the rSigmaRandom to set
     */
    public void setRSigmaRandom(boolean rSigmaRandom) {
        this.rSigmaRandom = rSigmaRandom;
    }

    /**
     * @return the rSigmaRandomMin
     */
    public double getRSigmaRandomMin() {
        return rSigmaRandomMin;
    }

    /**
     * @param rSigmaRandomMin the rSigmaRandomMin to set
     */
    public void setRSigmaRandomMin(double rSigmaRandomMin) {
        this.rSigmaRandomMin = rSigmaRandomMin;
    }

    /**
     * @return the rSigmaRandomMax
     */
    public double getRSigmaRandomMax() {
        return rSigmaRandomMax;
    }

    /**
     * @param rSigmaRandomMax the rSigmaRandomMax to set
     */
    public void setRSigmaRandomMax(double rSigmaRandomMax) {
        this.rSigmaRandomMax = rSigmaRandomMax;
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
     * @return the rPiRandom
     */
    public boolean isRPiRandom() {
        return rPiRandom;
    }

    /**
     * @param rPiRandom the rPiRandom to set
     */
    public void setRPiRandom(boolean rPiRandom) {
        this.rPiRandom = rPiRandom;
    }

    /**
     * @return the rPiRandomMin
     */
    public double getRPiRandomMin() {
        return rPiRandomMin;
    }

    /**
     * @param rPiRandomMin the rPiRandomMin to set
     */
    public void setRPiRandomMin(double rPiRandomMin) {
        this.rPiRandomMin = rPiRandomMin;
    }

    /**
     * @return the rPiRandomMax
     */
    public double getRPiRandomMax() {
        return rPiRandomMax;
    }

    /**
     * @param rPiRandomMax the rPiRandomMax to set
     */
    public void setRPiRandomMax(double rPiRandomMax) {
        this.rPiRandomMax = rPiRandomMax;
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
     * @return the tauRandom
     */
    public boolean isTauRandom() {
        return tauRandom;
    }

    /**
     * @param tauRandom the tauRandom to set
     */
    public void setTauRandom(boolean tauRandom) {
        this.tauRandom = tauRandom;
    }

    /**
     * @return the tauRandomMin
     */
    public int getTauRandomMin() {
        return tauRandomMin;
    }

    /**
     * @param tauRandomMin the tauRandomMin to set
     */
    public void setTauRandomMin(int tauRandomMin) {
        this.tauRandomMin = tauRandomMin;
    }

    /**
     * @return the tauRandomMax
     */
    public int getTauRandomMax() {
        return tauRandomMax;
    }

    /**
     * @param tauRandomMax the tauRandomMax to set
     */
    public void setTauRandomMax(int tauRandomMax) {
        this.tauRandomMax = tauRandomMax;
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

    /**
     * @return the currRSigmas
     */
    public double[] getCurrRSigmas() {
        return currRSigmas;
    }

    /**
     * @param currRSigmas the currRSigmas to set
     */
    public void setCurrRSigmas(double[] currRSigmas) {
        this.currRSigmas = currRSigmas;
    }


    /**
     * @return the currRPis
     */
    public double[] getCurrRPis() {
        return currRPis;
    }

    /**
     * @param currRPis the currRPis to set
     */
    public void setCurrRPis(double[] currRPis) {
        this.currRPis = currRPis;
    }

    /**
     * @return the rSigmaRandomHomogeneous
     */
    public boolean[] getRSigmaRandomHomogeneous() {
        return rSigmaRandomHomogeneous;
    }

    /**
     * @param rSigmaRandomHomogeneous the rSigmaRandomHomogeneous to set
     */
    public void setRSigmaRandomHomogeneous(boolean[] rSigmaRandomHomogeneous) {
        this.rSigmaRandomHomogeneous = rSigmaRandomHomogeneous;
    }

    /**
     * @return the currRSigmaRandomHomogeneous
     */
    public boolean isCurrRSigmaRandomHomogeneous() {
        return currRSigmaRandomHomogeneous;
    }

    /**
     * @param currRSigmaRandomHomogeneous the currRSigmaRandomHomogeneous to set
     */
    public void setCurrRSigmaRandomHomogeneous(boolean currRSigmaRandomHomogeneous) {
        this.currRSigmaRandomHomogeneous = currRSigmaRandomHomogeneous;
    }

    /**
     * @return the rPiRandomHomogeneous
     */
    public boolean[] getRPiRandomHomogeneous() {
        return rPiRandomHomogeneous;
    }

    /**
     * @param rPiRandomHomogeneous the rPiRandomHomogeneous to set
     */
    public void setRPiRandomHomogeneous(boolean[] rPiRandomHomogeneous) {
        this.rPiRandomHomogeneous = rPiRandomHomogeneous;
    }

    /**
     * @return the currRPiRandomHomogeneous
     */
    public boolean isCurrRPiRandomHomogeneous() {
        return currRPiRandomHomogeneous;
    }

    /**
     * @param currRPiRandomHomogeneous the currRPiRandomHomogeneous to set
     */
    public void setCurrRPiRandomHomogeneous(boolean currRPiRandomHomogeneous) {
        this.currRPiRandomHomogeneous = currRPiRandomHomogeneous;
    }

    /**
     * Gets the avergae r_sigma
     *
     * @return the avergae r_sigma
     */
    public double getRSigmaAv() {

        if (this.currRSigmas == null || this.currRSigmas.length == 0) {
            return this.currRSigma;
        }

        double rSigmaTotal = 0;
        int length = this.currRSigmas.length;
        for (int i = 0; i < length; i++) {
            rSigmaTotal += this.currRSigmas[i];
        }
        return rSigmaTotal/length;
    }

    /**
     * Gets the avergae r_pi
     *
     * @return the avergae r_pi
     */
    public double getRPiAv() {

        if (this.currRPis == null || this.currRPis.length == 0) {
            return this.currRPi;
        }

        double rPiTotal = 0;
        int length = this.currRPis.length;
        for (int i = 0; i < length; i++) {
            rPiTotal += this.currRPis[i];
        }
        return rPiTotal/length;
    }

    /**
     * @return the omegaShuffleRandom
     */
    public boolean isOmegaShuffleRandom() {
        return omegaShuffleRandom;
    }

    /**
     * @param omegaShuffleRandom the omegaShuffleRandom to set
     */
    public void setOmegaShuffleRandom(boolean omegaShuffleRandom) {
        this.omegaShuffleRandom = omegaShuffleRandom;
    }

    /**
     * @return the omegaShuffles
     */
    public boolean[] getOmegaShuffles() {
        return omegaShuffles;
    }

    /**
     * @param omegaShuffles the omegaShuffles to set
     */
    public void setOmegaShuffles(boolean[] omegaShuffles) {
        this.omegaShuffles = omegaShuffles;
    }

    /**
     * @return the currOmegaShuffle
     */
    public boolean isCurrOmegaShuffle() {
        return currOmegaShuffle;
    }

    /**
     * @param currOmegaShuffle the currOmegaShuffle to set
     */
    public void setCurrOmegaShuffle(boolean currOmegaShuffle) {
        this.currOmegaShuffle = currOmegaShuffle;
    }

    /**
     * @return the ac
     */
    public AssortativityConditions getAssortativityCondition() {
        return ac;
    }

    /**
     * @param ac the ac to set
     */
    public void setAssortativityCondition(AssortativityConditions ac) {
        this.ac = ac;
    }

}
