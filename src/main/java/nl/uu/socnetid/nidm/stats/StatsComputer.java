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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;

/**
 * @author Hendrik Nunner
 */
public final class StatsComputer {

    private static final Logger logger = LogManager.getLogger(StatsComputer.class);

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

        int connections = 0;
        double avDegree = 0.0;

        // TODO implement diameter and average distance
        int diameter = 0;
        double avDistance = 0.0;

        Iterator<Agent> agentsIt = agents.iterator();
        while (agentsIt.hasNext()) {
            Agent agent = agentsIt.next();
            connections += agent.getConnections().size();
        }
        avDegree = agents.size() == 0 ? 0.0 : (double) connections / agents.size();
        connections /= 2;

        return new GlobalNetworkStats(network.isStable(), connections, avDegree,
                diameter, avDistance, network.getAssortativityRiskPerception(), network.getAssortativityAge(),
                network.getAssortativityProfession());
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
        int nV = 0;

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

                case VACCINATED:
                    nV++;
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
                n, nS, nI, nR, nV,
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
     * Computes the geographic distance between an agent and all direct connections and returns it grouped by disease states.
     *
     * @param agent
     *          the agent to get all geographic distances to
     * @return the geographic distance between an agent and all direct connections and returns it grouped by disease states
     */
    public static Map<DiseaseGroup, Map<Double, Integer>> computeDirectConnectionsByDiseaseGroupAtGeographicDistance(Agent agent) {
        Map<DiseaseGroup, Map<Double, Integer>> directConsByDiseaseGroupAtGeographicDistance =
                new HashMap<DiseaseGroup, Map<Double, Integer>>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
            directConsByDiseaseGroupAtGeographicDistance.put(dg, new HashMap<Double, Integer>());
        }

        Iterator<Agent> it = agent.getNetwork().getAgentIterator();
        while (it.hasNext()) {
            Agent otherAgent = it.next();

            if (agent.isDirectlyConnectedTo(otherAgent)) {
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

                    case VACCINATED:
                        directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.VACCINATED).put(ggd,
                                directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.VACCINATED).get(ggd) != null ?
                                        directConsByDiseaseGroupAtGeographicDistance.get(DiseaseGroup.VACCINATED).get(ggd) + 1 : 1);
                        break;

                    default:
                        logger.warn("Unhandled disease group: " + otherAgent.getDiseaseGroup());
                }
            }
        }
        return directConsByDiseaseGroupAtGeographicDistance;
    }

    /**
     * Gets the number of direct connections by geographic distance (irrespective of disease group):
     * map<distance, number>.
     *
     * @param agent
     *          the agent to get the geographic distances for
     * @return the number of direct connections by disease group and distance
     */
    public static Map<Double, Integer> getDirectConnectionsByGeographicDistance(Agent agent) {

        Map<Double, Integer> consByDist = new HashMap<Double, Integer>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
             for (Map.Entry<Double, Integer> entry : computeDirectConnectionsByDiseaseGroupAtGeographicDistance(agent).
                     get(dg).entrySet()) {
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
     * Computes the geodesic distance between an agent and all other agents and returns it grouped by disease states.
     *
     * @param agent
     *          the agent to get geodesic distances to
     * @return the geodesic distance between an agent and all other agents and returns it grouped by disease states
     */
    public static Map<DiseaseGroup, Map<Integer, Integer>> computeConnectionsByDiseaseGroupAtGeodesicDistance(Agent agent) {
        // initialize maps of agents by disease group and distance
        Map<DiseaseGroup, Map<Integer, Integer>> consByDiseaseGroupAtGeodesicDistance =
                new HashMap<DiseaseGroup, Map<Integer, Integer>>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
            consByDiseaseGroupAtGeodesicDistance.put(dg, new HashMap<Integer, Integer>());
        }

        Iterator<Agent> it = agent.getNetwork().getAgentIterator();
        while (it.hasNext()) {
            Agent otherAgent = it.next();

            // geodesic distance to other agent
            Integer gdd = agent.getGeodesicDistanceTo(otherAgent);

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

                    case VACCINATED:
                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.VACCINATED).put(gdd,
                                consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.VACCINATED).get(gdd) != null ?
                                        consByDiseaseGroupAtGeodesicDistance.get(DiseaseGroup.VACCINATED).get(gdd) + 1 : 1);
                        break;

                    default:
                        logger.warn("Unhandled disease group: " + otherAgent.getDiseaseGroup());
                }
            }
        }

        return consByDiseaseGroupAtGeodesicDistance;
    }

//    /**
//     * Gets the number of direct connections by disease group and distance.
//     *
//     * @param dg
//     *          the disease group
//     * @param dist
//     *          the distance
//     * @return the number of direct connections by disease group and distance
//     */
//    public static int getConnectionsByDiseaseGroupAndGeodesicDistance(DiseaseGroup dg, Integer dist) {
//        Map<Integer, Integer> consByDiseaseGroup = computeConnectionsByDiseaseGroupAtGeodesicDistance().get(dg);
//        if (consByDiseaseGroup != null) {
//            Integer consByDistance = consByDiseaseGroup.get(dist);
//            if (consByDistance != null) {
//                return consByDistance;
//            }
//        }
//        return 0;
//    }

    /**
     * Gets the number of connections by geodesic distance (irrespective of disease group):
     * map<distance, number>.
     *
     * @param agent
     *          the agent to get the geodesic distances for
     * @return the number of direct connections by disease group and distance
     */
    public static Map<Integer, Integer> getConnectionsByGeodesicDistance(Agent agent) {

        Map<Integer, Integer> consByDist = new HashMap<Integer, Integer>();

        for (DiseaseGroup dg : DiseaseGroup.values()) {
             for (Map.Entry<Integer, Integer> entry : computeConnectionsByDiseaseGroupAtGeodesicDistance(agent).get(dg).entrySet()) {
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

        // direct ties
        int n = 0;
        int nS = 0;
        int nI = 0;
        int nR = 0;
        int nV = 0;
        // ties at distance 2
        int m = 0;
        int mS = 0;
        int mI = 0;
        int mR = 0;
        int mV = 0;
        // open triads
        int y = 0;
        // closed triads
        int z = 0;
        // network size
        int netSize = agent.getNetwork().getN();
        double disPrev = (double) agent.getNetwork().getInfected().size() / (double) netSize;

        // preparing the list of direct connections
        Collection<Agent> directConnections = agent.getConnections();
        if (with != null) {
            directConnections.add(with);
        }
        if (without != null && !directConnections.isEmpty() && directConnections.contains(without)) {
            directConnections.remove(without);
        }

        if (!directConnections.isEmpty()) {

            n = directConnections.size();

            Iterator<Agent> it = directConnections.iterator();
            List<Agent> consideredAtDistance2 = new ArrayList<Agent>();
            List<Agent> consideredDirectConnections = new ArrayList<Agent>(directConnections);
            while (it.hasNext()) {
                Agent directConnection = it.next();

                // direct connections by disease state
                switch(directConnection.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        nS++;
                        break;
                    case INFECTED:
                        nI++;
                        break;
                    case RECOVERED:
                        nR++;
                        break;
                    case VACCINATED:
                        nV++;
                        break;
                    default:
                        logger.warn("Unknown disease state: " + directConnection.getDiseaseGroup());
                }


                // XXX DISTANCE 2 STATS REMOVED FOR AGENTS
                // TODO implement distance 2 in different method and call only if needed
                // connections at distance 2
                Collection<Agent> connectionsAtDistance2 = directConnection.getConnections();
                Iterator<Agent> cDist2It = connectionsAtDistance2.iterator();
                while (cDist2It.hasNext()) {
                    Agent connectionAtDistance2 = cDist2It.next();
                    if (consideredAtDistance2.contains(connectionAtDistance2) ||
                            directConnections.contains(connectionAtDistance2) ||
                            agent.equals(connectionAtDistance2)) {
                        continue;
                    }
                    m++;
                    switch(connectionAtDistance2.getDiseaseGroup()) {
                        case SUSCEPTIBLE:
                            mS++;
                            break;
                        case INFECTED:
                            mI++;
                            break;
                        case RECOVERED:
                            mR++;
                            break;
                        case VACCINATED:
                            mV++;
                            break;
                        default:
                            logger.warn("Unknown disease state: " + directConnection.getDiseaseGroup());
                    }
                    consideredAtDistance2.add(connectionAtDistance2);
                }
                connectionsAtDistance2 = null;

                // XXX TRIAD STATS REMOVED FOR AGENTS
                // counting open and closed triads
                consideredDirectConnections.remove(directConnection);
                Iterator<Agent> cit = consideredDirectConnections.iterator();
                while (cit.hasNext()) {
                    Agent potentialTriad = cit.next();
                    if (directConnection.isDirectlyConnectedTo(potentialTriad)) {
                        z++;
                    } else {
                        y++;
                    }
                }
            }
            //consideredAtDistance2 = null;
            consideredDirectConnections = null;
        }

        directConnections = null;

        return new LocalAgentConnectionsStats(n, nS, nI, nR, nV, m, mS, mI, mR, mV, y, z, netSize, disPrev);
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

    /**
     * Computes the pearson correlation coefficient for two attribute arrays.
     *
     * @param attributes1
     *          attribute array 1
     * @param attributes2
     *          attribute array 2
     * @return the pearson correlation coefficient
     */
    private static double getPearsonCorrelationCoefficient(double[] attributes1, double[] attributes2) {
        double pcc = 0.0;
        if (attributes1.length > 1 || attributes2.length > 1) {
            try {
                pcc = new PearsonsCorrelation().correlation(attributes1, attributes2);
                if (Double.isNaN(pcc)) {
                    pcc = 0.0;
                }
            } catch (Exception e) {
                logger.error("Computation of Pearson's correlation coefficient failed: ", e);
                pcc = 0.0;
            }
        }
        return pcc;
    }

    public static double computeAssortativity(Collection<Edge> edges, AssortativityConditions ac) {

        double a = 0.0;

        // TODO improve (e.g., one method per type of assortativity condition)
        // collect attributes for numerical variables of all node pairs
        double[] attributes1 = new double[edges.size()];
        double[] attributes2 = new double[edges.size()];
        // collect sum off all equal attributes for categorical variables
        double equals = 0.0;

        Iterator<Edge> eIt = edges.iterator();

        int i = 0;
        while (eIt.hasNext()) {
            Edge edge = eIt.next();
            switch (ac) {
                case AGE:
                    attributes1[i] = ((Agent) edge.getNode0()).getAge();
                    attributes2[i] = ((Agent) edge.getNode1()).getAge();
                    break;

                case RISK_PERCEPTION:
                    attributes1[i] = ((Agent) edge.getNode0()).getRSigma() + ((Agent) edge.getNode0()).getRPi();
                    attributes2[i] = ((Agent) edge.getNode1()).getRSigma() + ((Agent) edge.getNode1()).getRPi();
                    break;

                case PROFESSION:
                    equals += ((Agent) edge.getNode0()).getProfession().equals(((Agent) edge.getNode1()).getProfession()) ?
                            1 : 0;
                    break;

                default:
                    logger.warn("assortativity not available for: " + ac);
                    break;
            }
            i++;
        }


        switch (ac) {
            // Pearson correlation coefficient for numerical variables
            case AGE:
            case RISK_PERCEPTION:
                a = getPearsonCorrelationCoefficient(attributes1, attributes2);
                break;

            case PROFESSION:
                a = equals / edges.size();
                break;

            default:
                logger.warn("assortativity not available for: " + ac);
                break;
        }

        return a;

    }

}
