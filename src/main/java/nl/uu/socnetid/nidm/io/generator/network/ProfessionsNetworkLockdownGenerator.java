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
package nl.uu.socnetid.nidm.io.generator.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ProfessionsNetworkLockdownGenerator extends AbstractGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsNetworkLockdownGenerator.class);

    // network files to be put in lockdown
    private static final List<String> NETWORK_FILES = Arrays.asList(
            "/Users/hendrik/git/uu/nidm/simulation/exports/20210315-091400/networks/professions.genetic/4-2(3-1:2-2)#2.dgs");


    /**
     * Constructor.
     *
     * @param rootExportPath
     *          the root export path
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public ProfessionsNetworkLockdownGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "professions.lockdown";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {


        DGSReader dgsReader = new DGSReader();

        for (String networkFile : NETWORK_FILES) {

            logger.debug("Starting to put network " + networkFile + " in lockdown.");
            Network network = dgsReader.readNetwork(networkFile);

            logger.debug("\n\nNetwork prior to lockdown:");
            debugNetwork(network, false);

            Iterator<Edge> edges = network.getEdgeIterator();
            while (edges.hasNext()) {
                Edge edge = edges.next();
                Agent a0 = (Agent) edge.getNode0();
                Agent a1 = (Agent) edge.getNode1();

                if (ThreadLocalRandom.current().nextDouble() <=
                        (Professions.getInstance().getDegreeReductionLockdown(a0.getProfession()) +
                        (Professions.getInstance().getDegreeReductionLockdown(a1.getProfession())) / 2)) {
                    a0.disconnectFrom(a1);
                }
            }

            logger.debug("\n\nNetwork in lockdown:");
            debugNetwork(network, true);
        }


    }

    /**
     * @param network
     */
    private void debugNetwork(Network network, boolean duringLockdown) {
        logger.debug("Av. degree (overall): \t\t" + String.format("%.2f", network.getAvDegree()));
        logger.debug("Av. clustering (overall): \t\t" + String.format("%.2f", network.getAvClustering()));

        Map<String, Double> degProf = network.getAvDegreesByProfessions();

        for (String profession : Professions.getInstance().getProfessions()) {

            String tabs = "\t\t\t";
//            if (profession.length() >= 5) {
//                tabs = "\t\t\t";
//            }

            if (duringLockdown) {
                logger.debug("Av. degree (" + profession + "):" + tabs +
                        String.format("%.2f", degProf.get(profession)) +
                        "\t(exp: " + String.format("%.2f", Professions.getInstance().getDegreeDuringLockdown(profession)) + ")");
            } else {
                logger.debug("Av. degree (" + profession + "):" + tabs +
                        String.format("%.2f", degProf.get(profession)) +
                        "\t(exp: " + String.format("%.2f", Professions.getInstance().getDegreePreLockdown(profession)) + ")");
            }


        }
    }

}











































