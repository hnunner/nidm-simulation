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

import java.util.List;

import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class NetworkStats {

    private Network network;
    @SuppressWarnings("unused")
    private int simRound;

    // TODO make all initializations lazy

    private boolean stable;
    private double density;
    private List<AssortativityConditions> acs;
    private double assortativityRiskPerception;
    private double assortativityAge;
    private double assortativityProfession;
    private double avDegree;
    private Double avDegree2 = null;     // lazy initialization (very costly operations)
    private double avDegreeSatisfied;
    private double avDegreeUnsatisfied;
    private Double avBetweenness = null;     // lazy initialization (very costly operations)
    private Double avCloseness = null;     // lazy initialization (very costly operations)
    private double avClustering;
    private Double avPathLength = null;     // lazy initialization (very costly operations)
    private double avUtility;
    private double avSocialBenefits;
    private double avSocialCosts;
    private double avDiseaseCosts;
    private Integer susceptiblesTotal = null;
    private Integer infectedTotal = null;
    private Integer recoveredTotal = null;
    private Integer vaccinatedTotal = null;
    private int satisfiedTotal;
    private int unsatisfiedTotal;
    private Double susceptiblePercent = null;
    private Double infectedPercent = null;
    private Double recoveredPercent = null;
    private Double vaccinatedPercent = null;
    private double satisfiedPercent;
    private double unsatisfiedPercent;
    private int tiesBrokenWithInfectionPresent = 0;
    private int networkChangesWithInfectionPresent = 0;


    public NetworkStats(Network network, int simRound) {
        this(network, simRound, true);
    }

    public NetworkStats(Network network, int simRound, boolean init) {

        this.network = network;
        this.simRound = simRound;

        if (!init) {
            return;
        }

        this.stable = network.isStable();
        this.density = network.getDensity();
        this.acs = network.getAssortativityConditions();
        this.assortativityRiskPerception = network.getAssortativityRiskPerception(simRound);
        this.assortativityAge = network.getAssortativityAge(simRound);
        this.assortativityProfession = network.getAssortativityProfession(simRound);
        this.avDegree = network.getAvDegree(simRound);
        this.avDegreeSatisfied = network.getAvDegreeSatisfied();
        this.avDegreeUnsatisfied = network.getAvDegreeUnsatisfied();
        this.avClustering = network.getAvClustering(simRound);
        this.avUtility = network.getAvUtility();
        this.avSocialBenefits = network.getAvSocialBenefits();
        this.avSocialCosts = network.getAvSocialCosts();
        this.avDiseaseCosts = network.getAvDiseaseCosts();
        this.susceptiblesTotal = network.getSusceptibles().size();
        this.infectedTotal = network.getInfected().size();
        this.recoveredTotal = network.getRecovered().size();
        this.vaccinatedTotal = network.getVaccinated().size();
        this.satisfiedTotal = network.getSatisfied().size();
        this.unsatisfiedTotal = network.getUnsatisfied().size();
        double pct = 100D / network.getAgents().size();
        this.susceptiblePercent = pct * this.susceptiblesTotal;
        this.infectedPercent = pct * this.infectedTotal;
        this.recoveredPercent = pct * this.recoveredTotal;
        this.vaccinatedPercent = pct * this.vaccinatedTotal;
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
     * @return the assortativity conditions
     */
    public List<AssortativityConditions> getAssortativityConditions() {
        return acs;
    }

    /**
     * @return the assortativityRiskPerception
     */
    public double getAssortativityRiskPerception() {
        return assortativityRiskPerception;
    }

    /**
     * @param assortativityRiskPerception the assortativityRiskPerception to set
     */
    public void setAssortativityRiskPerception(double assortativityRiskPerception) {
        this.assortativityRiskPerception = assortativityRiskPerception;
    }

    /**
     * @return the assortativityAge
     */
    public double getAssortativityAge() {
        return assortativityAge;
    }

    /**
     * @param assortativityAge the assortativityAge to set
     */
    public void setAssortativityAge(double assortativityAge) {
        this.assortativityAge = assortativityAge;
    }

    /**
     * @return the assortativityProfession
     */
    public double getAssortativityProfession() {
        return assortativityProfession;
    }

    /**
     * @param assortativityProfession the assortativityProfession to set
     */
    public void setAssortativityProfession(double assortativityProfession) {
        this.assortativityProfession = assortativityProfession;
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
        if (this.avDegree2 == null) {
            this.avDegree2 = network.getAvDegree2();
        }
        return this.avDegree2;
    }

    /**
     * @param avDegree2 the adDegree2 to set
     */
    public void setAvDegree2(double avDegree2) {
        this.avDegree2 = avDegree2;
    }

    /**
     * @param simRound
     *          the simulation round to get the average betweenness for
     * @return the avBetweenness
     */
    public double getAvBetweenness(int simRound) {
        if (this.avBetweenness == null) {
            this.avBetweenness = network.getAvBetweenness(simRound);
        }
        return avBetweenness;
    }

    /**
     * @param avBetweenness the avBetweenness to set
     */
    public void setAvBetweenness(double avBetweenness) {
        this.avBetweenness = avBetweenness;
    }

    /**
     * @param simRound
     *          the simulation round to get the average closeness for
     * @return the avCloseness
     */
    public double getAvCloseness(int simRound) {
        if (this.avCloseness == null) {
            this.avCloseness = network.getAvCloseness(simRound);
        }
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
     * @param simRound
     *          the simulation round to get the average path length for
     * @return the avPathLength
     */
    public double getAvPathLength(int simRound) {
        if (this.avPathLength == null) {
            this.avPathLength = network.getAvPathLength(simRound);
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
        if (this.susceptiblesTotal == null) {
            this.susceptiblesTotal = network.getSusceptibles().size();
        }
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
        if (this.infectedTotal == null) {
            this.infectedTotal = network.getInfected().size();
        }
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
        if (this.recoveredTotal == null) {
            this.recoveredTotal = network.getRecovered().size();
        }
        return recoveredTotal;
    }

    /**
     * @param recoveredTotal the recoveredTotal to set
     */
    public void setRecoveredTotal(int recoveredTotal) {
        this.recoveredTotal = recoveredTotal;
    }

    /**
     * @return the vaccinatedTotal
     */
    public int getVaccinatedTotal() {
        if (this.vaccinatedTotal == null) {
            this.vaccinatedTotal = network.getVaccinated().size();
        }
        return vaccinatedTotal;
    }

    /**
     * @return the susceptiblePercent
     */
    public double getSusceptiblePercent() {
        if (this.susceptiblePercent == null) {
            double pct = 100D / network.getAgents().size();
            this.susceptiblePercent = pct * this.susceptiblesTotal;
        }
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
        if (this.infectedPercent == null) {
            double pct = 100D / network.getAgents().size();
            this.infectedPercent = pct * this.infectedTotal;
        }
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
        if (this.recoveredPercent == null) {
            double pct = 100D / network.getAgents().size();
            this.recoveredPercent = pct * this.recoveredTotal;
        }
        return recoveredPercent;
    }

    /**
     * @param recoveredPercent the recoveredPercent to set
     */
    public void setRecoveredPercent(double recoveredPercent) {
        this.recoveredPercent = recoveredPercent;
    }

    /**
     * @return the vaccinatedPercent
     */
    public double getVaccinatedPercent() {
        if (this.vaccinatedPercent == null) {
            double pct = 100D / network.getAgents().size();
            this.vaccinatedPercent = pct * this.vaccinatedTotal;
        }
        return vaccinatedPercent;
    }

    /**
     * @param vaccinatedPercent the vaccinatedPercent to set
     */
    public void setVaccinatedPercent(double vaccinatedPercent) {
        this.vaccinatedPercent = vaccinatedPercent;
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
    public void setTiesBrokenWithInfectionPresent(int tiesBrokenWithInfectionPresent) {
        this.tiesBrokenWithInfectionPresent = tiesBrokenWithInfectionPresent;
    }

    /**
     * @return the networkChangesWithInfectionPresent
     */
    public int getNetworkChangesWithInfectionPresent() {
        return networkChangesWithInfectionPresent;
    }

    /**
     * @param networkChangesWithInfectionPresent the networkChangesWithInfectionPresent to set
     */
    public void setNetworkChangesWithInfectionPresent(int networkChangesWithInfectionPresent) {
        this.networkChangesWithInfectionPresent = networkChangesWithInfectionPresent;
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
