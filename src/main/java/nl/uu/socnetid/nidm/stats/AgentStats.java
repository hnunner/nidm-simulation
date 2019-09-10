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

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;

/**
 * @author Hendrik Nunner
 */
public class AgentStats {

    private boolean satisfied;
    private double degree1;
    private double degree2;
    private double closeness;
    private double clustering;
    private double utility;
    private double benefit1;
    private double benefit2;
    private double costs1;
    private double costsDisease;
    private DiseaseGroup diseaseGroup;
    private int timeToRecover;
    private int brokenTiesActive;
    private int brokenTiesPassive;
    private int acceptedRequestsOut;
    private int acceptedRequestsIn;
    private int declinedRequestsOut;
    private int declinedRequestsIn;


    public AgentStats(Agent agent) {
        this.satisfied = agent.isSatisfied();
        this.degree1 = agent.getDegree();
        this.degree2 = agent.getSecondOrderDegree();
        this.closeness = agent.getCloseness();
        this.clustering = agent.getClustering();
        this.utility = agent.getUtility().getOverallUtility();
        this.benefit1 = agent.getUtility().getBenefitDirectConnections();
        this.benefit2 = agent.getUtility().getBenefitIndirectConnections();
        this.costs1 = agent.getUtility().getCostsDirectConnections();
        this.costsDisease = agent.getUtility().getEffectOfDisease();
        this.diseaseGroup = agent.getDiseaseGroup();
        if (agent.isInfected()) {
            this.timeToRecover = agent.getTimeUntilRecovered();
        } else {
            this.timeToRecover = -1;
        }
        this.brokenTiesActive = agent.getConnectionStats().getBrokenTiesActive();
        this.brokenTiesPassive = agent.getConnectionStats().getBrokenTiesPassive();
        this.acceptedRequestsOut = agent.getConnectionStats().getAcceptedRequestsOut();
        this.acceptedRequestsIn = agent.getConnectionStats().getAcceptedRequestsIn();
        this.declinedRequestsOut = agent.getConnectionStats().getDeclinedRequestsOut();
        this.declinedRequestsIn = agent.getConnectionStats().getDeclinedRequestsIn();
    }


    /**
     * @return the degree1
     */
    public double getDegree1() {
        return degree1;
    }

    /**
     * @param degree1 the degree1 to set
     */
    public void setDegree1(double degree1) {
        this.degree1 = degree1;
    }

    /**
     * @return the degree2
     */
    public double getDegree2() {
        return degree2;
    }

    /**
     * @param degree2 the degree2 to set
     */
    public void setDegree2(double degree2) {
        this.degree2 = degree2;
    }

    /**
     * @return the closeness
     */
    public double getCloseness() {
        return closeness;
    }

    /**
     * @param closeness the closeness to set
     */
    public void setCloseness(double closeness) {
        this.closeness = closeness;
    }

    /**
     * @return the clustering
     */
    public double getClustering() {
        return clustering;
    }

    /**
     * @param clustering the clustering to set
     */
    public void setClustering(double clustering) {
        this.clustering = clustering;
    }

    /**
     * @return the utility
     */
    public double getUtility() {
        return utility;
    }

    /**
     * @param utility the utility to set
     */
    public void setUtility(double utility) {
        this.utility = utility;
    }

    /**
     * @return the benefit1
     */
    public double getBenefit1() {
        return benefit1;
    }

    /**
     * @param benefit1 the benefit1 to set
     */
    public void setBenefit1(double benefit1) {
        this.benefit1 = benefit1;
    }

    /**
     * @return the benefit2
     */
    public double getBenefit2() {
        return benefit2;
    }

    /**
     * @param benefit2 the benefit2 to set
     */
    public void setBenefit2(double benefit2) {
        this.benefit2 = benefit2;
    }

    /**
     * @return the costs1
     */
    public double getCosts1() {
        return costs1;
    }

    /**
     * @param costs1 the costs1 to set
     */
    public void setCosts1(double costs1) {
        this.costs1 = costs1;
    }

    /**
     * @return the costsDisease
     */
    public double getCostsDisease() {
        return costsDisease;
    }

    /**
     * @param costsDisease the costsDisease to set
     */
    public void setCostsDisease(double costsDisease) {
        this.costsDisease = costsDisease;
    }

    /**
     * @return the satisfied
     */
    public boolean isSatisfied() {
        return satisfied;
    }

    /**
     * @param satisfied the satisfied to set
     */
    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    /**
     * @return the diseaseGroup
     */
    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    /**
     * @param diseaseGroup the diseaseGroup to set
     */
    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    /**
     * @return the timeToRecover
     */
    public int getTimeToRecover() {
        return timeToRecover;
    }

    /**
     * @param timeToRecover the timeToRecover to set
     */
    public void setTimeToRecover(int timeToRecover) {
        this.timeToRecover = timeToRecover;
    }

    /**
     * @return the brokenTiesActive
     */
    public int getBrokenTiesActive() {
        return brokenTiesActive;
    }

    /**
     * @param brokenTiesActive the brokenTiesActive to set
     */
    public void setBrokenTiesActive(int brokenTiesActive) {
        this.brokenTiesActive = brokenTiesActive;
    }

    /**
     * @return the brokenTiesPassive
     */
    public int getBrokenTiesPassive() {
        return brokenTiesPassive;
    }

    /**
     * @param brokenTiesPassive the brokenTiesPassive to set
     */
    public void setBrokenTiesPassive(int brokenTiesPassive) {
        this.brokenTiesPassive = brokenTiesPassive;
    }

    /**
     * @return the acceptedRequestsOut
     */
    public int getAcceptedRequestsOut() {
        return acceptedRequestsOut;
    }

    /**
     * @param acceptedRequestsOut the acceptedRequestsOut to set
     */
    public void setAcceptedRequestsOut(int acceptedRequestsOut) {
        this.acceptedRequestsOut = acceptedRequestsOut;
    }

    /**
     * @return the acceptedRequestsIn
     */
    public int getAcceptedRequestsIn() {
        return acceptedRequestsIn;
    }

    /**
     * @param acceptedRequestsIn the acceptedRequestsIn to set
     */
    public void setAcceptedRequestsIn(int acceptedRequestsIn) {
        this.acceptedRequestsIn = acceptedRequestsIn;
    }

    /**
     * @return the declinedRequestsOut
     */
    public int getDeclinedRequestsOut() {
        return declinedRequestsOut;
    }

    /**
     * @param declinedRequestsOut the declinedRequestsOut to set
     */
    public void setDeclinedRequestsOut(int declinedRequestsOut) {
        this.declinedRequestsOut = declinedRequestsOut;
    }

    /**
     * @return the declinedRequestsIn
     */
    public int getDeclinedRequestsIn() {
        return declinedRequestsIn;
    }

    /**
     * @param declinedRequestsIn the declinedRequestsIn to set
     */
    public void setDeclinedRequestsIn(int declinedRequestsIn) {
        this.declinedRequestsIn = declinedRequestsIn;
    }

}
