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
package nl.uu.socnetid.nidm.io.network;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class EdgeListReader {

    // logger
    private static final Logger logger = LogManager.getLogger(DGSReader.class);


    /**
     * Constructor.
     */
    public EdgeListReader() { }


    /**
     * Reads a network from a DGS format file
     *
     * @param file
     *          the file to write to
     * @param networkSize
     *          the size of the network
     * @return the network
     */
    public Network readNetwork(String file, Integer networkSize) {

        Network network = new Network();

        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader(file));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (CsvValidationException e) {
            logger.error(e);
        }

        if (!records.isEmpty()) {
            Iterator<List<String>> recordsIt = records.iterator();
            Map<String, List<String>> nodes = new TreeMap<String, List<String>>();

            while (recordsIt.hasNext()) {
                List<String> record = recordsIt.next();

                if (record.get(0).equals("Source")) {
                    continue;
                }

                String source = record.get(0);
                String target = record.get(1);

                if (nodes.containsKey(source)) {
                    nodes.get(source).add(target);
                } else {
                    List<String> targets = new ArrayList<>();
                    targets.add(target);
                    nodes.put(source, targets);
                }
            }

            // adding agents
            if (networkSize == null) {
                networkSize = nodes.keySet().size();
            }
            for (int node = 1; node <= networkSize; node++) {
                network.addAgent(Integer.toString(node));
            }

            // adding ties
            for (String node : nodes.keySet()) {
                List<String> ties = nodes.get(node);
                for (String tie : ties) {
                    if (!network.getAgent(node).isDirectlyConnectedTo(network.getAgent(tie))) {
                        network.getAgent(node).addConnection(network.getAgent(tie));
                    }
                }
            }
        }

        logger.info("Network successfully imported and initialized from: " + file);

        return network;
    }

}
