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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;

import nl.uu.socnetid.nidm.io.network.EdgeListReader;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ExperimentDataAnalyzer extends AbstractAnalyzer {

    // logger
    private static final Logger logger = LogManager.getLogger(ExperimentDataAnalyzer.class);

    // data source path
    private static final String DATA_PATH = "src/main/resources/exp-network-data/";
    // data export file
    private static final String EXPORT_FILE = "src/main/resources/exp-network-data/!summary.csv";

    /**
     * Constructor.
     */
    public ExperimentDataAnalyzer() { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.analyzer.AbstractAnalyzer#analyze()
     */
    @Override
    protected void analyze() {

        EdgeListReader elReader = new EdgeListReader();
        File[] files = new File(DATA_PATH).listFiles();

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(EXPORT_FILE));
                CSVWriter csvWriter = new CSVWriter(writer,
                        ICSVWriter.DEFAULT_SEPARATOR, ICSVWriter.NO_QUOTE_CHARACTER,
                        ICSVWriter.DEFAULT_ESCAPE_CHARACTER, ICSVWriter.DEFAULT_LINE_END);
                ) {
            String[] headerRecord = {"session_id", "net_type", "game_round", "degree_av", "clustering_av", "pathlength_av"};
            csvWriter.writeNext(headerRecord);

            for (File file : files) {
                if (file.isFile() & file.getName().endsWith(".csv")) {

                    Network network = elReader.readNetwork(DATA_PATH + file.getName(), 60);
                    String[] metaData = file.getName().replace("network_data_", "").replace(".csv", "").split("_");

                    csvWriter.writeNext(new String[]{metaData[0], metaData[1], metaData[2],
                            Double.toString(network.getAvDegree()),
                            Double.toString(network.getAvClustering()),
                            Double.toString(network.getAvPathLength())});
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
