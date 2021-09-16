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
public class AgentStatsPost {

    private double rSigma;
    private double rPi;
    private boolean satisfied;
    private double utility;
    private double socialBenefits;
    private double socialCosts;
    private double diseaseCosts;
    private DiseaseGroup diseaseGroup;
    private int timeToRecover;
    private boolean forceInfected;
    private int brokenTiesActive;
    private int brokenTiesPassive;
    private int acceptedRequestsOut;
    private int acceptedRequestsIn;
    private int declinedRequestsOut;
    private int declinedRequestsIn;
    private int brokenTiesActiveEpidemic;
    private int brokenTiesPassiveEpidemic;
    private int acceptedRequestsOutEpidemic;
    private int acceptedRequestsInEpidemic;
    private int declinedRequestsOutEpidemic;
    private int declinedRequestsInEpidemic;
    private Integer initialIndexCaseDistance;
    private int whenInfected;


    public AgentStatsPost(Agent agent) {

        this.rSigma = agent.getRSigma();
        this.rPi = agent.getRPi();
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

        this.whenInfected = agent.getWhenInfected();
    }


    /**
     * @return the rSigma
     */
    public double getrSigma() {
        return rSigma;
    }

    /**
     * @return the rPi
     */
    public double getrPi() {
        return rPi;
    }

    /**
     * @return the initialIndexCaseDistance
     */
    public Integer getInitialIndexCaseDistance() {
        return initialIndexCaseDistance;
    }

    /**
     * @return the satisfied
     */
    public boolean isSatisfied() {
        return satisfied;
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

    /**
     * @return the whenInfected
     */
    public int getWhenInfected() {
        return whenInfected;
    }

}
