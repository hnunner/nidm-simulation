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

import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;

/**
 * @author Hendrik Nunner
 */
public class LocalAgentConnectionsStats {

    private final Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtDistance;
    private final int z;

    /**
     * Constructor.
     *
     * @param consByDiseaseGroupAtDistance
     *          map of amount of connections grouped by distance and disease group (map<distance, map<disease_group, amount>>)
     * @param z
     *          the amount of closed triads the agent is part of
     */
    public LocalAgentConnectionsStats(Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtDistance, int z) {
        this.consByDiseaseGroupAtDistance = consByDiseaseGroupAtDistance;
        this.z = z;
    }


    /**
     * @return the consAtDistanceByDiseaseGroup
     */
    public Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtDistance() {
        return consByDiseaseGroupAtDistance;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * Gets the number of direct connections by disease group and distance.
     *
     * @param dg
     *          the disease group
     * @param dist
     *          the distance
     * @return the number of direct connections by disease group and distance
     */
    public int getConnectionsByDiseaseGroupAndDistance(DiseaseGroup dg, Integer dist) {
        Map<Integer, Integer> consByDiseaseGroup = this.consByDiseaseGroupAtDistance.get(dg);
        if (consByDiseaseGroup != null) {
            Integer consByDistance = consByDiseaseGroup.get(dist);
            if (consByDistance != null) {
                return consByDistance;
            }
        }
        return 0;
    }

    /**
     * @return the number of direct connections
     */
    public int getN() {
        int n = 0;
        for (DiseaseGroup dg : DiseaseGroup.values()) {
            n += getConnectionsByDiseaseGroupAndDistance(dg, 1);
        }
        return n;
    }

    /**
     * @return the number of susceptible direct connections
     */
    public int getnS() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.SUSCEPTIBLE, 1);
    }

    /**
     * @return the number of infected direct connections
     */
    public int getnI() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.INFECTED, 1);
    }

    /**
     * @return the number of recovered direct connections
     */
    public int getnR() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.RECOVERED, 1);
    }

    /**
     * @return the number of indirect connections
     */
    public int getM() {
        int m = 0;
        for (DiseaseGroup dg : DiseaseGroup.values()) {
            m += getConnectionsByDiseaseGroupAndDistance(dg, 2);
        }
        return m;
    }

    /**
     * @return the number of susceptible indirect connections
     */
    public int getmS() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.SUSCEPTIBLE, 2);
    }

    /**
     * @return the number of infected indirect connections
     */
    public int getmI() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.INFECTED, 2);
    }

    /**
     * @return the number of recovered indirect connections
     */
    public int getmR() {
        return getConnectionsByDiseaseGroupAndDistance(DiseaseGroup.RECOVERED, 2);
    }

}
