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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
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
        return StatsComputer.computeLocalAgentConnectionsStats(agent, agent.getConnections());
    }

    /**
     * Computes the stats for a single agent's connections.
     *
     * @param agent
     *          the agent
     * @param connections
     *          the connections - given explicitly and not used from the agent's existing connections,
     *          because this might be used to compare the agent's current connections with potentially
     *          altered connections
     * @return the stats for a single agent's connections.
     */
    public static LocalAgentConnectionsStats computeLocalAgentConnectionsStats(Agent agent,
            Collection<Agent> connections) {

        // number of connections
        int nS = 0;
        int nI = 0;
        int nR = 0;
        int mS = 0;
        int mI = 0;
        int mR = 0;
        int z  = 0;

        // consider indirect connections only once
        List<Agent> consideredIndirectAgents = new LinkedList<Agent>();
        // consider agents only once for triads
        List<Agent> consideredTriadAgents = new LinkedList<Agent>();

        // for every direct connection
        Iterator<Agent> directIt = connections.iterator();
        while (directIt.hasNext()) {

            // no direct connection? do nothing
            Agent directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            // disease group of direct connection
            switch (directConnection.getDiseaseGroup()) {
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
                    logger.warn("Unhandled disease group: " + directConnection.getDiseaseGroup());
            }

            // no indirect connections --> go to next direct connection
            Collection<Agent> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            // for every indirect connection at distance 2
            Iterator<Agent> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Agent indirectConnection = indirectIt.next();

                // ignore self
                if (indirectConnection.equals(agent)) {
                    continue;
                }

                // do not consider indirect connections that have been considered before
                if (consideredIndirectAgents.contains(indirectConnection)) {
                    continue;
                }

                // connections that are direct and indirect connections at the same time ...
                if (connections.contains(indirectConnection)) {
                    // ... form a closed triad with self, but count only once
                    if (!consideredTriadAgents.contains(directConnection)) {
                        z++;
                        consideredTriadAgents.add(indirectConnection);
                    }
                    // ... must not be counted as indirect connections
                    continue;
                }

                // disease group
                switch (indirectConnection.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        mS++;
                        break;

                    case INFECTED:
                        mI++;
                        break;

                    case RECOVERED:
                        mR++;
                        break;

                    default:
                        logger.warn("Unhandled disease group: " + indirectConnection.getDiseaseGroup());
                }
                consideredIndirectAgents.add(indirectConnection);
            }
        }
        return new LocalAgentConnectionsStats(nS, nI, nR, mS, mI, mR, z);
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
