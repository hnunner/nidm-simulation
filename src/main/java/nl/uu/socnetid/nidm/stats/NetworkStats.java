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

import nl.uu.socnetid.nidm.agents.AgentAttributes;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class NetworkStats {

    private Network network;

    // TODO make all initializations lazy

    private boolean stable;
    private double density;
    private double assortativityRSigma;
    private double assortativityRPi;
    private double avDegree;
    private double avDegree2;
    private double avDegreeSatisfied;
    private double avDegreeUnsatisfied;
    private double avCloseness;
    private double avClustering;
    private Double avPathLength = null;     // lazy initialization (very costly operations)
    private double avUtility;
    private double avSocialBenefits;
    private double avSocialCosts;
    private double avDiseaseCosts;
    private int susceptiblesTotal;
    private int infectedTotal;
    private int recoveredTotal;
    private int satisfiedTotal;
    private int unsatisfiedTotal;
    private double susceptiblePercent;
    private double infectedPercent;
    private double recoveredPercent;
    private double satisfiedPercent;
    private double unsatisfiedPercent;
    private double tiesBrokenWithInfectionPresent;


    public NetworkStats(Network network) {
        this.network = network;
        this.stable = network.isStable();
        this.density = network.getDensity();
        this.assortativityRSigma = network.getAssortativity(AgentAttributes.RISK_FACTOR_SIGMA);
        this.assortativityRPi = network.getAssortativity(AgentAttributes.RISK_FACTOR_SIGMA);
        this.avDegree = network.getAvDegree();
        this.avDegree2 = network.getAvDegree2();
        this.avDegreeSatisfied = network.getAvDegreeSatisfied();
        this.avDegreeUnsatisfied = network.getAvDegreeUnsatisfied();
        this.avCloseness = network.getAvCloseness();
        this.avClustering = network.getAvClustering();
        this.avUtility = network.getAvUtility();
        this.avSocialBenefits = network.getAvSocialBenefits();
        this.avSocialCosts = network.getAvSocialCosts();
        this.avDiseaseCosts = network.getAvDiseaseCosts();
        this.susceptiblesTotal = network.getSusceptibles().size();
        this.infectedTotal = network.getInfected().size();
        this.recoveredTotal = network.getRecovered().size();
        this.satisfiedTotal = network.getSatisfied().size();
        this.unsatisfiedTotal = network.getUnsatisfied().size();
        double pct = 100D / network.getAgents().size();
        this.susceptiblePercent = pct * this.susceptiblesTotal;
        this.infectedPercent = pct * this.infectedTotal;
        this.recoveredPercent = pct * this.recoveredTotal;
        this.satisfiedPercent = pct * this.satisfiedTotal;
        this.unsatisfiedPercent = pct * this.unsatisfiedTotal;
    }

    /**
     * @return the stable
     */
    public boolean isStable() {
        return stable;
    }

    /**
     * @param stable the stable to set
     */
    public void setStable(boolean stable) {
        this.stable = stable;
    }

    /**
     * @return the density
     */
    public double getDensity() {
        return density;
    }

    /**
     * @param density the density to set
     */
    public void setDensity(double density) {
        this.density = density;
    }

    /**
     * @return the assortativityRSigma
     */
    public double getAssortativityRSigma() {
        return assortativityRSigma;
    }

    /**
     * @param assortativityRSigma the assortativityRSigma to set
     */
    public void setAssortativityRSigma(double assortativityRSigma) {
        this.assortativityRSigma = assortativityRSigma;
    }

    /**
     * @return the assortativityRPi
     */
    public double getAssortativityRPi() {
        return assortativityRPi;
    }

    /**
     * @param assortativityRPi the assortativityRPi to set
     */
    public void setAssortativityRPi(double assortativityRPi) {
        this.assortativityRPi = assortativityRPi;
    }

    /**
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @param avDegree the avDegree to set
     */
    public void setAvDegree(double avDegree) {
        this.avDegree = avDegree;
    }

    /**
     * @return the avDegree2
     */
    public double getAvDegree2() {
        return avDegree2;
    }

    /**
     * @param avDegree2 the adDegree2 to set
     */
    public void setAvDegree2(double avDegree2) {
        this.avDegree2 = avDegree2;
    }

    /**
     * @return the avCloseness
     */
    public double getAvCloseness() {
        return avCloseness;
    }

    /**
     * @param avCloseness the avCloseness to set
     */
    public void setAvCloseness(double avCloseness) {
        this.avCloseness = avCloseness;
    }

    /**
     * @return the avClustering
     */
    public double getAvClustering() {
        return avClustering;
    }

    /**
     * @param avClustering the avClustering to set
     */
    public void setAvClustering(double avClustering) {
        this.avClustering = avClustering;
    }

    /**
     * @return the avPathLength
     */
    public double getAvPathLength() {
        if (this.avPathLength == null) {
            this.avPathLength = network.getAvPathLength();
        }
        return avPathLength;
    }

    /**
     * @param avPathLength the avPathLength to set
     */
    public void setAvPathLength(double avPathLength) {
        this.avPathLength = avPathLength;
    }

    /**
     * @return the avUtility
     */
    public double getAvUtility() {
        return avUtility;
    }

    /**
     * @param avUtility the avUtility to set
     */
    public void setAvUtility(double avUtility) {
        this.avUtility = avUtility;
    }

    /**
     * @return the avSocialBenefits
     */
    public double getAvSocialBenefits() {
        return avSocialBenefits;
    }

    /**
     * @param avSocialBenefits the avSocialBenefits to set
     */
    public void setAvSocialBenefits(double avSocialBenefits) {
        this.avSocialBenefits = avSocialBenefits;
    }

    /**
     * @return the avSocialCosts
     */
    public double getAvSocialCosts() {
        return avSocialCosts;
    }

    /**
     * @param avSocialCosts the avSocialCosts to set
     */
    public void setAvSocialCosts(double avSocialCosts) {
        this.avSocialCosts = avSocialCosts;
    }

    /**
     * @return the avDiseaseCosts
     */
    public double getAvDiseaseCosts() {
        return avDiseaseCosts;
    }

    /**
     * @param avDiseaseCosts the avDiseaseCosts to set
     */
    public void setAvDiseaseCosts(double avDiseaseCosts) {
        this.avDiseaseCosts = avDiseaseCosts;
    }

    /**
     * @return the susceptiblesTotal
     */
    public int getSusceptiblesTotal() {
        return susceptiblesTotal;
    }

    /**
     * @param susceptiblesTotal the susceptiblesTotal to set
     */
    public void setSusceptiblesTotal(int susceptiblesTotal) {
        this.susceptiblesTotal = susceptiblesTotal;
    }

    /**
     * @return the infectedTotal
     */
    public int getInfectedTotal() {
        return infectedTotal;
    }

    /**
     * @param infectedTotal the infectedTotal to set
     */
    public void setInfectedTotal(int infectedTotal) {
        this.infectedTotal = infectedTotal;
    }

    /**
     * @return the recoveredTotal
     */
    public int getRecoveredTotal() {
        return recoveredTotal;
    }

    /**
     * @param recoveredTotal the recoveredTotal to set
     */
    public void setRecoveredTotal(int recoveredTotal) {
        this.recoveredTotal = recoveredTotal;
    }



    /**
     * @return the susceptiblePercent
     */
    public double getSusceptiblePercent() {
        return susceptiblePercent;
    }

    /**
     * @param susceptiblePercent the susceptiblePercent to set
     */
    public void setSusceptiblePercent(double susceptiblePercent) {
        this.susceptiblePercent = susceptiblePercent;
    }

    /**
     * @return the infectedPercent
     */
    public double getInfectedPercent() {
        return infectedPercent;
    }

    /**
     * @param infectedPercent the infectedPercent to set
     */
    public void setInfectedPercent(double infectedPercent) {
        this.infectedPercent = infectedPercent;
    }

    /**
     * @return the recoveredPercent
     */
    public double getRecoveredPercent() {
        return recoveredPercent;
    }

    /**
     * @param recoveredPercent the recoveredPercent to set
     */
    public void setRecoveredPercent(double recoveredPercent) {
        this.recoveredPercent = recoveredPercent;
    }

    /**
     * @return the tiesBrokenWithInfectionPresent
     */
    public double getTiesBrokenWithInfectionPresent() {
        return tiesBrokenWithInfectionPresent;
    }

    /**
     * @param tiesBrokenWithInfectionPresent the tiesBrokenWithInfectionPresent to set
     */
    public void setTiesBrokenWithInfectionPresent(double tiesBrokenWithInfectionPresent) {
        this.tiesBrokenWithInfectionPresent = tiesBrokenWithInfectionPresent;
    }

    /**
     * @return the avDegreeSatisfied
     */
    public double getAvDegreeSatisfied() {
        return avDegreeSatisfied;
    }

    /**
     * @param avDegreeSatisfied the avDegreeSatisfied to set
     */
    public void setAvDegreeSatisfied(double avDegreeSatisfied) {
        this.avDegreeSatisfied = avDegreeSatisfied;
    }

    /**
     * @return the avDegreeUnsatisfied
     */
    public double getAvDegreeUnsatisfied() {
        return avDegreeUnsatisfied;
    }

    /**
     * @param avDegreeUnsatisfied the avDegreeUnsatisfied to set
     */
    public void setAvDegreeUnsatisfied(double avDegreeUnsatisfied) {
        this.avDegreeUnsatisfied = avDegreeUnsatisfied;
    }

    /**
     * @return the satisfiedPercent
     */
    public double getSatisfiedPercent() {
        return satisfiedPercent;
    }

    /**
     * @param satisfiedPercent the satisfiedPercent to set
     */
    public void setSatisfiedPercent(double satisfiedPercent) {
        this.satisfiedPercent = satisfiedPercent;
    }

    /**
     * @return the unsatisfiedPercent
     */
    public double getUnsatisfiedPercent() {
        return unsatisfiedPercent;
    }

    /**
     * @param unsatisfiedPercent the unsatisfiedPercent to set
     */
    public void setUnsatisfiedPercent(double unsatisfiedPercent) {
        this.unsatisfiedPercent = unsatisfiedPercent;
    }

    /**
     * @return the satisfiedTotal
     */
    public int getSatisfiedTotal() {
        return satisfiedTotal;
    }

    /**
     * @param satisfiedTotal the satisfiedTotal to set
     */
    public void setSatisfiedTotal(int satisfiedTotal) {
        this.satisfiedTotal = satisfiedTotal;
    }

    /**
     * @return the unsatisfiedTotal
     */
    public int getUnsatisfiedTotal() {
        return unsatisfiedTotal;
    }

    /**
     * @param unsatisfiedTotal the unsatisfiedTotal to set
     */
    public void setUnsatisfiedTotal(int unsatisfiedTotal) {
        this.unsatisfiedTotal = unsatisfiedTotal;
    }

}
