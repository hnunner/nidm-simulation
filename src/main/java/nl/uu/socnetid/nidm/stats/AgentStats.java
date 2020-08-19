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

    private final double rSigma;
    private final double rSigmaNeighborhood;
    private final double rPi;
    private final double rPiNeighborhood;
    private final boolean satisfied;
    private final double degree1;
    private final double degree2;
    private final double closeness;
    private final double clustering;
    private final double betweenness;
    private final double betweennessNormalized;
    private final double utility;
    private final double socialBenefits;
    private final double socialCosts;
    private final double diseaseCosts;
    private final DiseaseGroup diseaseGroup;
    private final int timeToRecover;
    private final boolean forceInfected;
    private final int brokenTiesActive;
    private final int brokenTiesPassive;
    private final int acceptedRequestsOut;
    private final int acceptedRequestsIn;
    private final int declinedRequestsOut;
    private final int declinedRequestsIn;
    private final int brokenTiesActiveEpidemic;
    private final int brokenTiesPassiveEpidemic;
    private final int acceptedRequestsOutEpidemic;
    private final int acceptedRequestsInEpidemic;
    private final int declinedRequestsOutEpidemic;
    private final int declinedRequestsInEpidemic;


    public AgentStats(Agent agent) {

        this.rSigma = agent.getRSigma();
        this.rSigmaNeighborhood = agent.getRSigmaNeighborhood();
        this.rPi = agent.getRPi();
        this.rPiNeighborhood = agent.getRPiNeighborhood();
        this.satisfied = agent.isSatisfied();
        this.degree1 = agent.getDegree();
        this.degree2 = agent.getSecondOrderDegree();
        this.closeness = agent.getCloseness();
        this.clustering = agent.getClustering();
        this.betweenness = 0; //agent.getBetweenness(); TODO remove comment / parameterize
        this.betweennessNormalized = agent.getBetweennessNormalized();
        this.utility = agent.getUtility().getOverallUtility();
        this.socialBenefits = agent.getUtility().getSocialBenefits();
        this.socialCosts = agent.getUtility().getSocialCosts();
        this.diseaseCosts = agent.getUtility().getDiseaseCosts();
        this.diseaseGroup = agent.getDiseaseGroup();
        if (agent.isInfected()) {
            this.timeToRecover = agent.getTimeUntilRecovered();
        } else {
            this.timeToRecover = -1;
        }
        this.forceInfected = agent.isForceInfected();
        this.brokenTiesActive = agent.getConnectionStats().getBrokenTiesActive();
        this.brokenTiesPassive = agent.getConnectionStats().getBrokenTiesPassive();
        this.acceptedRequestsOut = agent.getConnectionStats().getAcceptedRequestsOut();
        this.acceptedRequestsIn = agent.getConnectionStats().getAcceptedRequestsIn();
        this.declinedRequestsOut = agent.getConnectionStats().getDeclinedRequestsOut();
        this.declinedRequestsIn = agent.getConnectionStats().getDeclinedRequestsIn();

        this.brokenTiesActiveEpidemic = agent.getConnectionStats().getBrokenTiesActiveEpidemic();
        this.brokenTiesPassiveEpidemic = agent.getConnectionStats().getBrokenTiesPassiveEpidemic();
        this.acceptedRequestsOutEpidemic = agent.getConnectionStats().getAcceptedRequestsOutEpidemic();
        this.acceptedRequestsInEpidemic = agent.getConnectionStats().getAcceptedRequestsInEpidemic();
        this.declinedRequestsOutEpidemic = agent.getConnectionStats().getDeclinedRequestsOutEpidemic();
        this.declinedRequestsInEpidemic = agent.getConnectionStats().getDeclinedRequestsInEpidemic();
    }


    /**
     * @return the rSigma
     */
    public double getrSigma() {
        return rSigma;
    }

    /**
     * @return the rSigmaNeighborhood
     */
    public double getrSigmaNeighborhood() {
        return rSigmaNeighborhood;
    }

    /**
     * @return the rPi
     */
    public double getrPi() {
        return rPi;
    }

    /**
     * @return the rPiNeighborhood
     */
    public double getrPiNeighborhood() {
        return rPiNeighborhood;
    }

    /**
     * @return the satisfied
     */
    public boolean isSatisfied() {
        return satisfied;
    }

    /**
     * @return the degree1
     */
    public double getDegree1() {
        return degree1;
    }

    /**
     * @return the degree2
     */
    public double getDegree2() {
        return degree2;
    }

    /**
     * @return the closeness
     */
    public double getCloseness() {
        return closeness;
    }

    /**
     * @return the clustering
     */
    public double getClustering() {
        return clustering;
    }

    /**
     * @return the betweenness
     */
    public double getBetweenness() {
        return betweenness;
    }

    /**
     * @return the betweennessNormalized
     */
    public double getBetweennessNormalized() {
        return betweennessNormalized;
    }

    /**
     * @return the utility
     */
    public double getUtility() {
        return utility;
    }

    /**
     * @return the socialBenefits
     */
    public double getSocialBenefits() {
        return socialBenefits;
    }

    /**
     * @return the socialCosts
     */
    public double getSocialCosts() {
        return socialCosts;
    }

    /**
     * @return the diseaseCosts
     */
    public double getDiseaseCosts() {
        return diseaseCosts;
    }

    /**
     * @return the diseaseGroup
     */
    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    /**
     * @return the timeToRecover
     */
    public int getTimeToRecover() {
        return timeToRecover;
    }

    /**
     * @return the forceInfected
     */
    public boolean isForceInfected() {
        return forceInfected;
    }

    /**
     * @return the brokenTiesActive
     */
    public int getBrokenTiesActive() {
        return brokenTiesActive;
    }

    /**
     * @return the brokenTiesPassive
     */
    public int getBrokenTiesPassive() {
        return brokenTiesPassive;
    }

    /**
     * @return the acceptedRequestsOut
     */
    public int getAcceptedRequestsOut() {
        return acceptedRequestsOut;
    }

    /**
     * @return the acceptedRequestsIn
     */
    public int getAcceptedRequestsIn() {
        return acceptedRequestsIn;
    }

    /**
     * @return the declinedRequestsOut
     */
    public int getDeclinedRequestsOut() {
        return declinedRequestsOut;
    }

    /**
     * @return the declinedRequestsIn
     */
    public int getDeclinedRequestsIn() {
        return declinedRequestsIn;
    }

    /**
     * @return the brokenTiesActiveEpidemic
     */
    public int getBrokenTiesActiveEpidemic() {
        return brokenTiesActiveEpidemic;
    }

    /**
     * @return the brokenTiesPassiveEpidemic
     */
    public int getBrokenTiesPassiveEpidemic() {
        return brokenTiesPassiveEpidemic;
    }

    /**
     * @return the acceptedRequestsOutEpidemic
     */
    public int getAcceptedRequestsOutEpidemic() {
        return acceptedRequestsOutEpidemic;
    }

    /**
     * @return the acceptedRequestsInEpidemic
     */
    public int getAcceptedRequestsInEpidemic() {
        return acceptedRequestsInEpidemic;
    }

    /**
     * @return the declinedRequestsOutEpidemic
     */
    public int getDeclinedRequestsOutEpidemic() {
        return declinedRequestsOutEpidemic;
    }

    /**
     * @return the declinedRequestsInEpidemic
     */
    public int getDeclinedRequestsInEpidemic() {
        return declinedRequestsInEpidemic;
    }

}
