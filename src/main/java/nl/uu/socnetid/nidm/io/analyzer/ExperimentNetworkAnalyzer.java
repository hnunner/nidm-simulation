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
package nl.uu.socnetid.nidm.io.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ExperimentNetworkAnalyzer extends AbstractAnalyzer {

    // logger
    private static final Logger logger = LogManager.getLogger(ExperimentNetworkAnalyzer.class);

    /**
     * Constructor.
     */
    public ExperimentNetworkAnalyzer() { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.analyzer.AbstractAnalyzer#analyze()
     */
    @Override
    protected void analyze() {

        DGSReader dgsReader = new DGSReader();

        List<String> networkFiles = Arrays.asList(
                getClass().getClassLoader().getResource("low-clustering_experiment.dgs").getPath(),
                getClass().getClassLoader().getResource("high-clustering_experiment.dgs").getPath());

        for (String networkFile : networkFiles) {
            Network network = dgsReader.readNetwork(networkFile);

            double avDegree = network.getAvDegree();
            double avCloseness = network.getAvCloseness(0);



            int maxDegree = 0;
            double maxCloseness = 0.0;
            TreeMap<String, Integer> degreeByAgentId = new TreeMap<String, Integer>();
            TreeMap<String, Double> closenessByAgentId = new TreeMap<String, Double>();
            Iterator<Agent> aIt = network.getAgentIterator();
            while (aIt.hasNext()) {
                Agent agent = aIt.next();
                int degree = agent.getDegree();
                degreeByAgentId.put(agent.getId(), degree);
                if (degree > maxDegree) {
                    maxDegree = degree;
                }
                double closeness = agent.getCloseness(0);
                closenessByAgentId.put(agent.getId(), closeness);
                if (closeness > maxCloseness) {
                    maxCloseness = closeness;
                }
            }

            logger.info("############################## MAXIMUMS ##############################");
            logger.info("Maximum degree: " + maxDegree);
            logger.info("Maximum closeness: " + maxCloseness);

            double normalizedAvDegree = avDegree / maxDegree;
            double normalizedAvCloseness = avCloseness / maxCloseness;
            logger.info("############################## AVERAGES ##############################");
            logger.info("Average degree: " + avDegree);
            logger.info("Average closeness: " + avCloseness);
            logger.info("Normalized average degree: " + normalizedAvDegree);
            logger.info("Normalized average closeness: " + normalizedAvCloseness);
            logger.info("############################## MAXIMUMS ##############################");
            logger.info("Maximum degree: " + maxDegree);
            logger.info("Maximum closeness: " + maxCloseness);

            TreeMap<Double, List<Agent>> agentsByError = new TreeMap<Double, List<Agent>>();
            for (String agentId : degreeByAgentId.keySet()) {
                Integer degree = degreeByAgentId.get(agentId);
                Double closeness = closenessByAgentId.get(agentId);

                double normalizedDegree = (double) degree / (double) maxDegree;
                double normalizedCloseness = closeness / maxCloseness;

                Double error = (Math.abs(normalizedDegree - normalizedAvDegree) / normalizedAvDegree)
                        + (Math.abs(normalizedCloseness - normalizedAvCloseness) / normalizedAvCloseness);

                List<Agent> agents = agentsByError.get(error);
                if (agents == null) {
                    agents = new ArrayList<Agent>();
                }
                agents.add(network.getAgent(agentId));
                agentsByError.put(error, agents);
            }
            logger.info("############################## PER AGENT ##############################");
            Set<Double> errors = agentsByError.keySet();
            for (Double error : errors) {
                List<Agent> agents = agentsByError.get(error);
                for (Agent agent : agents) {
                    logger.info("Agent #" + agent.getId() +
                            ", error: " + error +
                            ", degree: " + degreeByAgentId.get(agent.getId()) +
                            ", closeness: " + closenessByAgentId.get(agent.getId()));
                }
            }
            logger.info("###########################################################################");
            logger.info("");

        }


    }






}
