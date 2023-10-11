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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkLockdownParameters;
import nl.uu.socnetid.nidm.io.csv.ProfessionNetworkLockdownWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.AgentPropertiesWriter;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.io.network.NetworkFileWriter;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class FabioNetworkGenerator extends AbstractGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(FabioNetworkGenerator.class);


    // input network files
    private static final List<String> NETWORK_FILES = Arrays.asList(
            "/Users/hendrik/git/uu/nidm/simulation/exports/networks.fabio.2/1#1#1-round_12.dgs",
            "/Users/hendrik/git/uu/nidm/simulation/exports/networks.fabio.2/1#1#1-round_18.dgs");

    // stats & writer
    private DataGeneratorData<ProfessionNetworkLockdownParameters> dgData;
    private ProfessionNetworkLockdownWriter pnlWriter;


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
    public FabioNetworkGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "fabio";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<ProfessionNetworkLockdownParameters>(
                new ProfessionNetworkLockdownParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.pnlWriter = new ProfessionNetworkLockdownWriter(getExportPath() +
                    "fabio.csv", this.dgData);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {


        DGSReader dgsReader = new DGSReader();

        int uid = 1;
        int upc = 1;
        for (String networkFile : NETWORK_FILES) {

            logger.debug("Starting to reduce assortativity for file '" + networkFile + "'.");
            Network network = dgsReader.readNetwork(networkFile);

            // retrieve parameter settings
            this.dgData.getSimStats().setUid(String.valueOf(uid));
            this.dgData.getSimStats().setUpc(upc);
            Agent randAgent = network.getRandomAgent();
            this.dgData.getUtilityModelParams().setOmega(randAgent.getOmega());
            this.dgData.getUtilityModelParams().setAlpha(((NunnerBuskens) (randAgent.getUtilityFunction())).getAlpha());

            exportNetworks(upc, uid, network, "_ass-hi");
            
            while (network.getAssortativityRiskPerception() > 0.401) {
            	logger.debug("Current assortativity: " + network.getAssortativityRiskPerception());
            	Iterator<Agent> agents = network.getAgentIterator();
            	while (agents.hasNext()) {
            		Agent agent = agents.next();
            		if (ThreadLocalRandom.current().nextDouble() < 0.001) {
            			agent.updateRiskScores(ThreadLocalRandom.current().nextDouble(0.0, 2.0));
            		}
            	}
            }
            exportNetworks(upc, uid, network, "_ass-md");
            
            while (network.getAssortativityRiskPerception() > 0.001) {
            	logger.debug("Current assortativity: " + network.getAssortativityRiskPerception());
            	Iterator<Agent> agents = network.getAgentIterator();
            	while (agents.hasNext()) {
            		Agent agent = agents.next();
            		if (ThreadLocalRandom.current().nextDouble() < 0.02) {
            			agent.updateRiskScores(ThreadLocalRandom.current().nextDouble(0.0, 2.0));
            		}
            	}
            }
            exportNetworks(upc, uid, network, "_ass-lo");
            upc += 1;
        }
    }

	private void exportNetworks(int upc, int uid, Network network, String fileSuffix) {
		
		// export DGS file
		DGSWriter dgsWriter = new DGSWriter();
		String fileName = getExportPath() + upc + fileSuffix + ".dgs";
		dgsWriter.writeNetwork(network, fileName);
		this.dgData.setExportFileName(fileName);

		// export as Gephi file
		GEXFWriter gexfWriter;
		gexfWriter = new GEXFWriter();
		String gexfFileDuring = getExportPath() + upc + fileSuffix + ".gexf";
		gexfWriter.writeStaticNetwork(network, gexfFileDuring);
		
		// export as edge list
		NetworkFileWriter elWriter = new NetworkFileWriter(getExportPath(),
		        upc + fileSuffix + ".el",
		        new EdgeListWriter(),
		        network);
		elWriter.write();
		
		// agent properties
		NetworkFileWriter ageWriter = new NetworkFileWriter(getExportPath(),
				upc + fileSuffix + ".agnt",
		        new AgentPropertiesWriter(),
		        network);
		ageWriter.write();
		
		// export lockdown stats
        this.dgData.getSimStats().setUid(String.valueOf(uid));
        this.dgData.getSimStats().setUpc(upc++);
        this.dgData.getUtilityModelParams().setCurrLockdownCondition(LockdownConditions.DURING);
        this.dgData.setNetStatsCurrent(new NetworkStats(network, gexfFileDuring));
        this.pnlWriter.writeCurrentData();
		
	}


}
