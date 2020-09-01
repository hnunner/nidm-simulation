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
public class AgentStatsPre {

    private double rSigma;
    private double rSigmaNeighborhood;
    private double rPi;
    private double rPiNeighborhood;
    private boolean satisfied;
    private double degree1;
    private double closeness;
    private double clustering;
    private double betweennessNormalized;
    private double assortativity;
    private DiseaseGroup diseaseGroup;
    private int timeToRecover;
    private boolean forceInfected;
    private Integer indexCaseDistance;


    public AgentStatsPre(Agent agent, int simRound) {

        this.rSigma = agent.getRSigma();
        this.rPi = agent.getRPi();
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
        this.closeness = agent.getCloseness(simRound);
        this.clustering = agent.getClustering();
        this.betweennessNormalized = agent.getBetweennessNormalized(simRound);
        this.assortativity = agent.getAssortativity();
        this.indexCaseDistance = agent.getInitialIndexCaseDistance();
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
     * @return the indexCaseDistance
     */
    public Integer getIndexCaseDistance() {
        return indexCaseDistance;
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
     * @return the betweennessNormalized
     */
    public double getBetweennessNormalized() {
        return betweennessNormalized;
    }

    /**
     * @return the assortativity
     */
    public double getAssortativity() {
        return assortativity;
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

}
