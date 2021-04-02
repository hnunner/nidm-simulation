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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkAgentStatsParameters;
import nl.uu.socnetid.nidm.io.csv.ProfessionNetworkAgentStatsWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ProfessionsNetworkAgentStatsGenerator extends AbstractGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsNetworkAgentStatsGenerator.class);

    // network files to be put in lockdown
    private static final String NETWORK_FILES_FOLDER = "C:/Users/Hendrik/git/NIDM/simulation/data/c19-professions/networks";

    // stats & writer
    private DataGeneratorData<ProfessionNetworkAgentStatsParameters> dgData;
    private ProfessionNetworkAgentStatsWriter pnasWriter;


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
    public ProfessionsNetworkAgentStatsGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "professions.agent.stats";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<ProfessionNetworkAgentStatsParameters>(
                new ProfessionNetworkAgentStatsParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.pnasWriter = new ProfessionNetworkAgentStatsWriter(getExportPath() +
                    "profession-agent-stats.csv", this.dgData);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        DGSReader dgsReader = new DGSReader();

        List<String> networkFiles = null;
        try (Stream<Path> walk = Files.walk(Paths.get(NETWORK_FILES_FOLDER))) {
            networkFiles = walk.map(x -> x.toString()).filter(f -> f.endsWith(".dgs")).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error while retrieving names for network network files from " + NETWORK_FILES_FOLDER, e);
            return;
        }

        for (String networkFile : networkFiles) {

            logger.debug("Starting to extract agent stats from network: " + networkFile);
            Network network = dgsReader.readNetwork(networkFile);

            // data preparations
            this.dgData.getSimStats().setUid(networkFile);
            this.dgData.setAgents(new ArrayList<Agent>(network.getAgents()));
            this.dgData.getUtilityModelParams().setCurrLockdownCondition(networkFile.contains("pre") ?
                    LockdownConditions.PRE : LockdownConditions.DURING);

            // write
            this.pnasWriter.writeCurrentData();
        }
    }

}
