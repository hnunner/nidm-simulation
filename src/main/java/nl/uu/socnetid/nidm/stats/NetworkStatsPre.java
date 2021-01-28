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

import java.util.Map;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class NetworkStatsPre {

    private Network network;

    private final boolean stable;
    private double assortativityRiskPerception;
    private double assortativityAge;
    private double assortativityProfession;
    private final double avDegree;
    private final double avBetweenness;
    private final double avCloseness;
    private final double avClustering;
    private final double avPathLength;
    private double avDegreeDiffTotal;
    private double avDegreeDiffPercent;

    private double avDegreeTheoretic;
    private Map<String, Integer> nByProfession;
    private Map<String, Double> avDegreesByProfession;
    private Map<String, Double> avDegreesByProfessionTheoretic;
    private Map<String, Double> degreesSdByProfession;
    private Map<String, Double> degreesSdByProfessionTheoretic;


    public NetworkStatsPre(Network network, int simRound) {
        this.network = network;
        this.stable = network.isStable();
        this.avDegree = network.getAvDegree(simRound);
        this.assortativityRiskPerception = network.getAssortativityRiskPerception(simRound);
        this.assortativityAge = network.getAssortativityAge(simRound);
        this.assortativityProfession = network.getAssortativityProfession(simRound);
        this.avBetweenness = -1.0;                                          //network.getAvBetweenness(simRound);
        this.avCloseness = -1.0;                                            // network.getAvCloseness(simRound);
        this.avClustering = network.getAvClustering(simRound);
        this.avPathLength = -1.0;                                           // network.getAvPathLength(simRound);

        this.nByProfession = network.getNByProfessions();
        this.avDegreesByProfession = network.getAvDegreesByProfessions();
        this.avDegreesByProfessionTheoretic = null;
        this.degreesSdByProfession = network.getDegreesSdByProfessions();
        this.degreesSdByProfessionTheoretic = null;
    }

    /**
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @return the stable
     */
    public boolean isStable() {
        return stable;
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
     * @return the avDegree
     */
    public double getAvDegree() {
        return avDegree;
    }

    /**
     * @return the avBetweenness
     */
    public Double getAvBetweenness() {
        return avBetweenness;
    }

    /**
     * @return the avCloseness
     */
    public Double getAvCloseness() {
        return avCloseness;
    }

    /**
     * @return the avClustering
     */
    public double getAvClustering() {
        return avClustering;
    }

    /**
     * @return the avPathLength
     */
    public Double getAvPathLength() {
        return avPathLength;
    }

    /**
     * @return the avDegreeDiffPercent
     */
    public double getAvDegreeDiffPercent() {
        return avDegreeDiffPercent;
    }

    /**
     * @param avDegreeDiffPercent the avDegreeDiffPercent to set
     */
    public void setAvDegreeDiffPercent(double avDegreeDiffPercent) {
        this.avDegreeDiffPercent = avDegreeDiffPercent;
    }

    /**
     * @return the avDegreeDiffTotal
     */
    public double getAvDegreeDiffTotal() {
        return avDegreeDiffTotal;
    }

    /**
     * @param avDegreeDiffTotal the avDegreeDiffTotal to set
     */
    public void setAvDegreeDiffTotal(double avDegreeDiffTotal) {
        this.avDegreeDiffTotal = avDegreeDiffTotal;
    }

    /**
     * @param profession
     *          the profession to get average degree for
     * @return the avDegree for profession
     */
    public double getNByProfession(String profession) {
        return this.nByProfession.get(profession);
    }

    /**
     * @param profession
     *          the profession to get average degree for
     * @return the avDegree for profession
     */
    public double getAvDegreeByProfession(String profession) {
        return this.avDegreesByProfession.get(profession);
    }

    /**
     * @param profession
     *          the profession to get average degree for
     * @return the avDegree for profession
     */
    public double getAvDegreeByProfessionTheoretic(String profession) {
        return this.avDegreesByProfessionTheoretic.get(profession);
    }

    /**
     * @param avDegreesByProfessionTheoretic the avDegreesByProfessionTheoretic to set
     */
    public void setAvDegreesByProfessionTheoretic(Map<String, Double> avDegreesByProfessionTheoretic) {
        this.avDegreesByProfessionTheoretic = avDegreesByProfessionTheoretic;
    }

    /**
     * @param profession
     *          the profession to get average degree standard deviation for
     * @return the avDegreeSd for profession
     */
    public double getDegreeSdByProfession(String profession) {
        return this.degreesSdByProfession.get(profession);
    }

    /**
     * @param profession
     *          the profession to get average degree for
     * @return the degreesSdByProfessionTheoretic
     */
    public double getDegreeSdByProfessionTheoretic(String profession) {
        return degreesSdByProfessionTheoretic.get(profession);
    }

    /**
     * @param degreesSdByProfessionTheoretic the degreesSdByProfessionTheoretic to set
     */
    public void setDegreesSdByProfessionTheoretic(Map<String, Double> degreesSdByProfessionTheoretic) {
        this.degreesSdByProfessionTheoretic = degreesSdByProfessionTheoretic;
    }

    /**
     * @return the avDegreeTheoretic
     */
    public double getAvDegreeTheoretic() {
        return avDegreeTheoretic;
    }

    /**
     * @param avDegreeTheoretic the avDegreeTheoretic to set
     */
    public void setAvDegreeTheoretic(double avDegreeTheoretic) {
        this.avDegreeTheoretic = avDegreeTheoretic;
    }

}
