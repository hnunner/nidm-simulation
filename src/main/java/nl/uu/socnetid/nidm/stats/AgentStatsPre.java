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
public class AgentStatsPre {

    private final String id;
    private final double rSigma;
    private final double rSigmaNeighborhood;
    private final double rPi;
    private final double rPiNeighborhood;
    private final int indexCaseDistance;
    private final boolean satisfied;
    private final double degree1;
    private final double closeness;
    private final double clustering;
    private final double betweennessNormalized;
    private final double assortativityRiskPerception;
    private final double assortativityAge;
    private final double assortativityProfession;
    private final DiseaseGroup diseaseGroup;
    private final int timeToRecover;
    private final boolean forceInfected;
    private final String profession;

    public AgentStatsPre(Agent agent, int simRound) {
        this.id = agent.getId();
        this.rSigma = agent.getRSigma();
        this.rSigmaNeighborhood = agent.getRSigmaNeighborhood();
        this.rPi = agent.getRPi();
        this.rPiNeighborhood = agent.getRPiNeighborhood();
        this.indexCaseDistance = agent.getInitialIndexCaseDistance();
        this.satisfied = agent.isSatisfied();
        this.degree1 = agent.getDegree();
        this.closeness = agent.getCloseness(simRound);
        this.clustering = agent.getClustering(simRound);
        this.betweennessNormalized = agent.getBetweennessNormalized(simRound);
        this.assortativityRiskPerception = agent.getAssortativity(simRound, AssortativityConditions.RISK_PERCEPTION);
        this.assortativityAge = agent.getAssortativity(simRound, AssortativityConditions.AGE);
        this.assortativityProfession = agent.getAssortativity(simRound, AssortativityConditions.PROFESSION);
        this.diseaseGroup = agent.getDiseaseGroup();
        this.timeToRecover = agent.getTimeUntilRecovered();
        this.forceInfected = agent.isForceInfected();
        this.profession = agent.getProfession();
    }


    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return the rSigma
     */
    public double getrSigma() {
        return this.rSigma;
    }

    /**
     * @return the rSigmaNeighborhood
     */
    public double getrSigmaNeighborhood() {
        return this.rSigmaNeighborhood;
    }

    /**
     * @return the rPi
     */
    public double getrPi() {
        return this.rPi;
    }

    /**
     * @return the rPiNeighborhood
     */
    public double getrPiNeighborhood() {
        return this.rPiNeighborhood;
    }

    /**
     * @return the indexCaseDistance
     */
    public Integer getIndexCaseDistance() {
        return this.indexCaseDistance;
    }

    /**
     * @return the satisfied
     */
    public boolean isSatisfied() {
        return this.satisfied;
    }

    /**
     * @return the degree1
     */
    public double getDegree1() {
        return this.degree1;
    }

    /**
     * @return the closeness
     */
    public double getCloseness() {
        return this.closeness;
    }

    /**
     * @return the clustering
     */
    public double getClustering() {
        return this.clustering;
    }

    /**
     * @return the betweennessNormalized
     */
    public double getBetweennessNormalized() {
        return this.betweennessNormalized;
    }

    /**
     * @return the assortativityRiskPerception
     */
    public double getAssortativityRiskPerception() {
        return this.assortativityRiskPerception;
    }

    /**
     * @return the assortativityAge
     */
    public double getAssortativityAge() {
        return this.assortativityAge;
    }

    /**
     * @return the assortativityProfession
     */
    public double getAssortativityProfession() {
        return this.assortativityProfession;
    }

    /**
     * @return the diseaseGroup
     */
    public DiseaseGroup getDiseaseGroup() {
        return this.diseaseGroup;
    }

    /**
     * @return the timeToRecover
     */
    public int getTimeToRecover() {
        return this.timeToRecover;
    }

    /**
     * @return the forceInfected
     */
    public boolean isForceInfected() {
        return this.forceInfected;
    }

    /**
     * @return the profession
     */
    public String getProfession() {
        return this.profession;
    }

}
