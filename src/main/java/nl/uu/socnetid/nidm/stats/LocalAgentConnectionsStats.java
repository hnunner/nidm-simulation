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

import java.util.HashMap;
import java.util.Map;

import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;

/**
 * @author Hendrik Nunner
 */
public class LocalAgentConnectionsStats {

    private final Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtGeodesicDistance;
    private final Map<DiseaseGroup, Map<Double, Integer>> directConsByDiseaseGroupAtGeographicDistance;
    private final int z;
    private final int netSize;

    /**
     * Constructor.
     *
     * @param consByDiseaseGroupAtGeodesicDistance
     *          map of amount of connections grouped by geodesic distance and disease group:
     *          (map<geodesic_distance, map<disease_group, amount>>)
     * @param directConsByDiseaseGroupAtGeographicDistance
     *          map of amount of connections grouped by geographic distance and disease group:
     *          (map<geographic_distance, map<disease_group, amount>>)
     * @param z
     *          the amount of closed triads the agent is part of
     * @param netSize
     *          the network size
     */
    public LocalAgentConnectionsStats(Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtGeodesicDistance,
            Map<DiseaseGroup, Map<Double, Integer>> directConsByDiseaseGroupAtGeographicDistance, int z, int netSize) {
        this.consByDiseaseGroupAtGeodesicDistance = consByDiseaseGroupAtGeodesicDistance;
        this.directConsByDiseaseGroupAtGeographicDistance = directConsByDiseaseGroupAtGeographicDistance;
        this.z = z;
        this.netSize = netSize;
    }


    /**
     * @return the consAtDistanceByDiseaseGroup
     */
    public Map<DiseaseGroup, Map<Integer, Integer>> getConsByDiseaseGroupAtGeodesicDistance() {
        return consByDiseaseGroupAtGeodesicDistance;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the netSize
     */
    public int getNetSize() {
        return netSize;
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
    public int getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup dg, Integer dist) {
        Map<Integer, Integer> consByDiseaseGroup = this.consByDiseaseGroupAtGeodesicDistance.get(dg);
        if (consByDiseaseGroup != null) {
            Integer consByDistance = consByDiseaseGroup.get(dist);
            if (consByDistance != null) {
                return consByDistance;
            }
        }
        return 0;
    }

    /**
     * Gets the number of connections by distance (irrespective of disease group):
     * map<distance, number>.
     *
     * @return the number of direct connections by disease group and distance
     */
    public Map<Integer, Integer> getConnectionsByGeodesicDistance() {

        Map<Integer, Integer> consByDist = new HashMap<Integer, Integer>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
             for (Map.Entry<Integer, Integer> entry : this.consByDiseaseGroupAtGeodesicDistance.get(dg).entrySet()) {
                 Integer dist = entry.getKey();
                 Integer cons = entry.getValue();
                 consByDist.put(dist,
                         consByDist.get(dist) != null ?
                                 consByDist.get(dist) + cons :
                                     cons);
             }
        }

        return consByDist;
    }

    /**
     * Gets the number of connections by distance (irrespective of disease group):
     * map<distance, number>.
     *
     * @return the number of direct connections by disease group and distance
     */
    public Map<Double, Integer> getDirectConnectionsByGeographicDistance() {

        Map<Double, Integer> consByDist = new HashMap<Double, Integer>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
             for (Map.Entry<Double, Integer> entry : this.directConsByDiseaseGroupAtGeographicDistance.get(dg).entrySet()) {
                 Double dist = entry.getKey();
                 Integer cons = entry.getValue();
                 consByDist.put(dist,
                         consByDist.get(dist) != null ?
                                 consByDist.get(dist) + cons :
                                     cons);
             }
        }

        return consByDist;
    }


    /**
     * @return the number of direct connections
     */
    public int getN() {
        int n = 0;
        for (DiseaseGroup dg : DiseaseGroup.values()) {
            n += getConnectionsByDiseaseGroupAndGeodesicDistance(dg, 1);
        }
        return n;
    }

    /**
     * @return the number of susceptible direct connections
     */
    public int getnS() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.SUSCEPTIBLE, 1);
    }

    /**
     * @return the number of infected direct connections
     */
    public int getnI() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.INFECTED, 1);
    }

    /**
     * @return the number of recovered direct connections
     */
    public int getnR() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.RECOVERED, 1);
    }

    /**
     * @return the number of indirect connections
     */
    public int getM() {
        int m = 0;
        for (DiseaseGroup dg : DiseaseGroup.values()) {
            m += getConnectionsByDiseaseGroupAndGeodesicDistance(dg, 2);
        }
        return m;
    }

    /**
     * @return the number of susceptible indirect connections
     */
    public int getmS() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.SUSCEPTIBLE, 2);
    }

    /**
     * @return the number of infected indirect connections
     */
    public int getmI() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.INFECTED, 2);
    }

    /**
     * @return the number of recovered indirect connections
     */
    public int getmR() {
        return getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup.RECOVERED, 2);
    }

}
