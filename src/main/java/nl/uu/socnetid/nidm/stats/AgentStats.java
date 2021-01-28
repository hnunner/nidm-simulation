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
import nl.uu.socnetid.nidm.networks.AssortativityConditions;

/**
 * @author Hendrik Nunner
 */
public class AgentStats {

    private Agent agent;
    @SuppressWarnings("unused")
    private int simRound;

    private double rSigma;
    private double rSigmaNeighborhood;
    private double rPi;
    private double rPiNeighborhood;
    private boolean satisfied;
    private Integer degree1 = null;
    private double degree2;
    private double closeness;
    private double clustering;
    private double betweenness;
    private double betweennessNormalized;
    private double assortativityRiskPerception;
    private double assortativityAge;
    private double assortativityProfession;
    private double utility;
    private double socialBenefits;
    private double socialCosts;
    private double diseaseCosts;
    private DiseaseGroup diseaseGroup = null;
    private int timeToRecover;
    private Boolean forceInfected = null;
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
    private String profession;


    public AgentStats(Agent agent, int simRound) {
        this(agent, simRound, true);
    }

    public AgentStats(Agent agent, int simRound, boolean init) {

        this.agent = agent;
        this.simRound = simRound;

        if (!init) {
            return;
        }

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

        this.rSigmaNeighborhood = agent.getRSigmaNeighborhood();
        this.rPiNeighborhood = agent.getRPiNeighborhood();
        this.satisfied = agent.isSatisfied();
        this.degree1 = agent.getDegree();
        this.degree2 = agent.getSecondOrderDegree();
        this.closeness = agent.getCloseness(simRound);
        this.clustering = agent.getClustering(simRound);
        this.betweenness = agent.getBetweenness(simRound);
        this.betweennessNormalized = agent.getBetweennessNormalized(simRound);
        this.assortativityRiskPerception = agent.getAssortativity(simRound, AssortativityConditions.RISK_PERCEPTION);
        this.assortativityAge = agent.getAssortativity(simRound, AssortativityConditions.AGE);
        this.assortativityProfession = agent.getAssortativity(simRound, AssortativityConditions.PROFESSION);
        this.initialIndexCaseDistance = agent.getInitialIndexCaseDistance();
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

        this.profession = agent.getProfession();
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
     * @return the degree1
     */
    public int getDegree1() {
        if (this.degree1 == null) {
            this.degree1 = this.agent.getDegree();
        }
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
     * @return the assortativityRiskPerception
     */
    public double getAssortativityRiskPerception() {
        return assortativityRiskPerception;
    }

    /**
     * @return the assortativityAge
     */
    public double getAssortativityAge() {
        return assortativityAge;
    }

    /**
     * @return the assortativityProfession
     */
    public double getAssortativityProfession() {
        return assortativityProfession;
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
        if (this.diseaseGroup == null) {
            this.diseaseGroup = this.agent.getDiseaseGroup();
        }
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
        if (this.forceInfected == null) {
            this.forceInfected = agent.isForceInfected();
        }
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
     * @return the profession
     */
    public String getProfession() {
        if (this.profession == null || this.profession.isEmpty()) {
            this.profession = this.agent.getProfession();
        }
        return profession;
    }

}
