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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;

/**
 * @author Hendrik Nunner
 */
public final class StatsComputer {

    private static final Logger logger = Logger.getLogger(StatsComputer.class);

    /** Private construtor. Inhibits unwanted instantiation of class. */
    private StatsComputer() { }


    /**
     * Computes the first order degree of an agent.
     *
     * @param agent
     *          the agent to compute the first degree for
     * @return the first order degree of the agent
     */
    public static int computeFirstOrderDegree(Agent agent) {
        return agent.getDegree();
    }

    /**
     * Computes the second order degree of an agent.
     *
     * @param agent
     *          the agent to compute the second degree for
     * @return the second order degree of the agent
     */
    public static int computeSecondOrderDegree(Agent agent) {
        return StatsComputer.computeLocalAgentConnectionsStats(agent).getM();
    }

    /**
     * Computes the closeness of an agent. Based on Buechel & Buskens (2013) formula (1), p.163.
     *
     * @param agent
     *          the agent to compute the closeness for
     * @return the closeness of the agent
     */
    public static double computeCloseness(Agent agent) {

        // Note: M = n, see Buechel & Buskens (2013), p. 162
        double n = agent.getCoAgents().size() + 1;              // all co-agents plus the agent himself
        double M = n;
        double cumulatedDistance = 0.0;

        // distances
        // 1st calculate shortest paths for all co-agents
        DijkstraShortestPath dsp = new DijkstraShortestPath();
        dsp.executeShortestPaths(agent);
        Iterator<Agent> coAgentsIt = agent.getCoAgents().iterator();
        while (coAgentsIt.hasNext()) {
            Agent coAgent = coAgentsIt.next();
            Integer shortestPathLength = dsp.getShortestPathLength(coAgent);
            // if path exists
            if (shortestPathLength != null) {
                // distance of connected nodes
                cumulatedDistance += Double.valueOf(shortestPathLength);
            } else {
                // distance of not connected nodes = n
                cumulatedDistance += n;
            }
        }

        // average distance including normalization
        return (M / (M - 1)) - (cumulatedDistance / ((M - 1) * (n - 1)));
    }

    /**
     * Computes the global network stats of a given network.
     *
     * @param network
     *          the network to compute the global stats for
     * @return the global network stats
     */
    public static GlobalNetworkStats computeGlobalNetworkStats(Network network) {

        Collection<Agent> agents = network.getAgents();

        boolean stable = true;
        int connections = 0;
        double avDegree = 0.0;

        // TODO implement diameter and average distance
        int diameter = 0;
        double avDistance = 0.0;

        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            stable &= agent.isSatisfied();
            connections += agent.getConnections().size();
        }
        avDegree = agents.size() == 0 ? 0.0 : (double) connections / agents.size();
        connections /= 2;

        return new GlobalNetworkStats(stable, connections, avDegree, diameter, avDistance);
    }

    /**
     * Computes the global agent stats for the given network.
     *
     * @param network
     *          the network to compute the global network stats for
     * @return the global agent stats for the given network
     */
    public static GlobalAgentStats computeGlobalAgentStats(Network network) {

        // all agents
        Collection<Agent> agents = network.getAgents();
        int n = agents.size();

        // disease groups
        int nS = 0;
        int nI = 0;
        int nR = 0;

        // risk behavior
        // disease severity
        int nRSigmaAverse = 0;
        int nRSigmaNeutral = 0;
        int nRSigmaSeeking = 0;
        double cumRSigma = 0.0;
        // probability of infection
        int nRPiAverse = 0;
        int nRPiNeutral = 0;
        int nRPiSeeking = 0;
        double cumRPi = 0.0;

        // check all agents
        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();

            // disease group
            switch (agent.getDiseaseGroup()) {
                case SUSCEPTIBLE:
                    nS++;
                    break;

                case INFECTED:
                    nI++;
                    break;

                case RECOVERED:
                    nR++;
                    break;

                default:
                    logger.warn("Unknown disease group: " + agent.getDiseaseGroup());
            }

            // risk behavior
            // disease severity
            double rSigma = agent.getRSigma();
            if (rSigma > 1) {
                nRSigmaAverse++;
            } else if (rSigma < 1) {
                nRSigmaSeeking++;
            } else {
                nRSigmaNeutral++;
            }
            cumRSigma += rSigma;
            // probability of infection
            double rPi = agent.getRPi();
            if (rPi > 1) {
                nRPiAverse++;
            } else if (rPi < 1) {
                nRPiSeeking++;
            } else {
                nRPiNeutral++;
            }
            cumRPi += rPi;
        }

        return new GlobalAgentStats(
                n, nS, nI, nR,
                nRSigmaAverse, nRSigmaNeutral, nRSigmaSeeking, cumRSigma / n,
                nRPiAverse, nRPiNeutral, nRPiSeeking, cumRPi / n);
    }

    public static GlobalSimulationStats computeGlobalSimulationStats(Simulation simulation) {
        if (simulation == null) {
            return new GlobalSimulationStats(false, 0);
        }
        return new GlobalSimulationStats(simulation.isRunning(), simulation.getRounds());
    }

    /**
     * Computes the stats for a single agent's connections.
     *
     * @param agent
     *          the agent
     * @return the stats for a single agent's connections.
     */
    public static LocalAgentConnectionsStats computeLocalAgentConnectionsStats(Agent agent) {
        return StatsComputer.computeLocalAgentConnectionsStats(agent, null, null);
    }

    /**
     * Computes the stats for a single agent's connections with a potentially new connection.
     *
     * @param agent
     *          the agent
     * @param with
     *          the potentially new connection
     * @return the stats for a single agent's connections.
     */
    public static LocalAgentConnectionsStats computeLocalAgentConnectionsStatsWith(Agent agent, Agent with) {
        return StatsComputer.computeLocalAgentConnectionsStats(agent, with, null);
    }

    /**
     * Computes the stats for a single agent's connections without an existing connection.
     *
     * @param agent
     *          the agent
     * @param without
     *          the existing connection not to consider
     * @return the stats for a single agent's connections.
     */
    public static LocalAgentConnectionsStats computeLocalAgentConnectionsStatsWithout(Agent agent, Agent without) {
        return StatsComputer.computeLocalAgentConnectionsStats(agent, null, without);
    }

    /**
     * Computes the stats for a single agent's connections with a potentially new connection and without an existing connection.
     *
     * @param agent
     *          the agent
     * @param with
     *          the potentially new connection
     * @param without
     *          the existing connection not to consider
     * @return the stats for a single agent's connections.
     */
    public static LocalAgentConnectionsStats computeLocalAgentConnectionsStats(Agent agent, Agent with, Agent without) {

        // initialize maps of agents by disease group and distance
        Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtGeodesicDistance =
                new HashMap<DiseaseGroup, Map<Integer, Integer>>();
        Map<DiseaseGroup, Map<Double, Integer>> directConsByDiseaseGroupAtGeographicDistance =
                new HashMap<DiseaseGroup, Map<Double, Integer>>();
        for (DiseaseGroup dg : DiseaseGroup.values()) {
            consByDiseaseGroupAtGeodesicDistance.put(dg, new HashMap<Integer, Integer>());
            directConsByDiseaseGroupAtGeographicDistance.put(dg, new HashMap<Double, Integer>());
        }

        // initialize list of direct connections
        List<Agent> directConnections = new LinkedList<Agent>();

        // fill map of agents by disease group and distance
        Iterator<Agent> it = agent.getNetwork().getAgentIterator();
        while (it.hasNext()) {
            Agent otherAgent = it.next();

            // geodesic distance to other agent
            Integer gdd = null;
            if ((with != null) && (otherAgent.getId() == with.getId())) {
                gdd = 1;
            } else if ((without != null) && (otherAgent.getId() == without.getId())) {
                // gd = null;
            } else {
                gdd = agent.getGeodesicDistanceTo(otherAgent);
            }

            // if geodesic connection exists
            if (gdd != null) {
                // store disease group and geodesic distance
                switch (otherAgent.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.SUSCEPTIBLE).put(gdd,
                                consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.SUSCEPTIBLE).get(gdd) != null ?
                                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.SUSCEPTIBLE).get(gdd) + 1 : 1);
                        break;

                    case INFECTED:
                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.INFECTED).put(gdd,
                                consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.INFECTED).get(gdd) != null ?
                                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.INFECTED).get(gdd) + 1 : 1);
                        break;

                    case RECOVERED:
                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.RECOVERED).put(gdd,
                                consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.RECOVERED).get(gdd) != null ?
                                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.RECOVERED).get(gdd) + 1 : 1);
                        break;

                    default:
                        logger.warn("Unhandled disease group: " + otherAgent.getDiseaseGroup());
                }

                // direct connections
                if (gdd == 1) {
                    // store for triad checks
                    directConnections.add(otherAgent);

                    // geographic distance to direct connections
                    double ggd = agent.getGeographicDistanceTo(otherAgent);
                    // store disease group and geographic distance
                    switch (otherAgent.getDiseaseGroup()) {
                        case SUSCEPTIBLE:
                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.SUSCEPTIBLE).put(ggd,
                                    directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.SUSCEPTIBLE).get(ggd) != null ?
                                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.SUSCEPTIBLE).get(ggd) + 1 : 1);
                            break;

                        case INFECTED:
                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.INFECTED).put(ggd,
                                    directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.INFECTED).get(ggd) != null ?
                                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.INFECTED).get(ggd) + 1 : 1);
                            break;

                        case RECOVERED:
                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.RECOVERED).put(ggd,
                                    directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.RECOVERED).get(ggd) != null ?
                                            directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.RECOVERED).get(ggd) + 1 : 1);
                            break;

                        default:
                            logger.warn("Unhandled disease group: " + otherAgent.getDiseaseGroup());
                    }
                }
            }

        }

        // determine number of triads agent belongs to
        int z = 0;
        it = directConnections.iterator();
        // consider direct connections only once for triads
        List<Agent> consideredDirectConnections = new LinkedList<Agent>(directConnections);
        while (it.hasNext()) {
            Agent directConnection = it.next();
            consideredDirectConnections.remove(directConnection);
            Iterator<Agent> cit = consideredDirectConnections.iterator();
            while (cit.hasNext()) {
                Agent potentialTriad = cit.next();
                if (directConnection.hasDirectConnectionTo(potentialTriad)) {
                    z++;
                }
            }
        }

        return new LocalAgentConnectionsStats(
                consByDiseaseGroupAtGeodesicDistance,
                directConsByDiseaseGroupAtGeographicDistance,
                z,
                agent.getNetwork().getN());
    }

    /**
     * Computes the agent's probability of getting infected.
     *
     * @param agent
     *          the agent to compute the probability for
     * @param nI
     *          the number of infected direct connections
     * @return the agent's probability of getting infected
     */
    public static double computeProbabilityOfInfection(Agent agent, int nI) {
        return 1 - Math.pow((1 - agent.getDiseaseSpecs().getGamma()), nI);
    }

}
