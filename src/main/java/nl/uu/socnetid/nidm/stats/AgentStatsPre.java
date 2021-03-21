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

    private Agent agent;
    private int simRound;

    public AgentStatsPre(Agent agent, int simRound) {
        this.agent = agent;
        this.simRound = simRound;
    }


    /**
     * @return the id
     */
    public String getId() {
        return agent.getId();
    }

    /**
     * @return the rSigma
     */
    public double getrSigma() {
        return agent.getRSigma();
    }

    /**
     * @return the rSigmaNeighborhood
     */
    public double getrSigmaNeighborhood() {
        return agent.getRSigmaNeighborhood();
    }

    /**
     * @return the rPi
     */
    public double getrPi() {
        return agent.getRPi();
    }

    /**
     * @return the rPiNeighborhood
     */
    public double getrPiNeighborhood() {
        return agent.getRPiNeighborhood();
    }

    /**
     * @return the indexCaseDistance
     */
    public Integer getIndexCaseDistance() {
        return agent.getInitialIndexCaseDistance();
    }

    /**
     * @return the satisfied
     */
    public boolean isSatisfied() {
        return agent.isSatisfied();
    }

    /**
     * @return the degree1
     */
    public double getDegree1() {
        return agent.getDegree();
    }

    /**
     * @return the closeness
     */
    public double getCloseness() {
        return agent.getCloseness(simRound);
    }

    /**
     * @return the clustering
     */
    public double getClustering() {
        return agent.getClustering(simRound);
    }

    /**
     * @return the betweennessNormalized
     */
    public double getBetweennessNormalized() {
        return agent.getBetweennessNormalized(simRound);
    }

    /**
     * @return the assortativityRiskPerception
     */
    public double getAssortativityRiskPerception() {
        return agent.getAssortativity(simRound, AssortativityConditions.RISK_PERCEPTION);
    }

    /**
     * @return the assortativityAge
     */
    public double getAssortativityAge() {
        return agent.getAssortativity(simRound, AssortativityConditions.AGE);
    }

    /**
     * @return the assortativityProfession
     */
    public double getAssortativityProfession() {
        return agent.getAssortativity(simRound, AssortativityConditions.PROFESSION);
    }

    /**
     * @return the diseaseGroup
     */
    public DiseaseGroup getDiseaseGroup() {
        return agent.getDiseaseGroup();
    }

    /**
     * @return the timeToRecover
     */
    public int getTimeToRecover() {
        if (agent.isInfected()) {
            return agent.getTimeUntilRecovered();
        }
        return -1;
    }

    /**
     * @return the forceInfected
     */
    public boolean isForceInfected() {
        return agent.isForceInfected();
    }

    /**
     * @return the profession
     */
    public String getProfession() {
        return agent.getProfession();
    }

}
