/*
 * Copyright (C) 2017 - 2022
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
package nl.uu.socnetid.nidm.mains;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.io.analyzer.AbstractAnalyzer;
import nl.uu.socnetid.nidm.io.analyzer.ExperimentNetworksEachRoundAnalyzer;

/**
 * @author Hendrik Nunner
 */
public class Analyzer {

    // logger
    private static final Logger logger = LogManager.getLogger(Analyzer.class);

    /**
     * Launches the data analysis.
     *
     * @param args
     *          command line arguments
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public static void main(String[] args) throws IOException {
    	
    	initialize();
    	
//        AbstractAnalyzer analyzer = new ExperimentNetworkAnalyzer();
    	AbstractAnalyzer analyzer = new ExperimentNetworksEachRoundAnalyzer();
        analyzer.launch();
    }


    /**
     * Initializes the data generation, by logging the copyright agreement, preparing the export path,
     * and copying the properties file.
     */
    protected static void initialize() {

        logger.info("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                ":: Copyright (C) 2017 - 2022\n" +
                "::     Hendrik Nunner    <h.nunner@gmail.com>\n" +
                "::\n" +
                ":: This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.\n" +
                "::\n" +
                ":: This project is a stand-alone Java program of the Networking during Infectious Diseases Model\n" +
                ":: (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network\n" +
                ":: formation and infectious diseases.\n" +
                "::\n" +
                ":: This program is free software: you can redistribute it and/or modify it under the\n" +
                ":: terms of the GNU General Public License as published by the Free Software Foundation,\n" +
                ":: either version 3 of the License, or (at your option) any later version.\n" +
                "::\n" +
                ":: This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n" +
                ":: without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n" +
                ":: See the GNU General Public License for more details.\n" +
                "::\n" +
                ":: You should have received a copy of the GNU General Public License along with this program.\n" +
                ":: If not, see <http://www.gnu.org/licenses/>.\n" +
                "::\n" +
                ":: References:\n" +
                "::     Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic\n" +
                "::     social networks and infectious diseases. Manuscript sumbitted for publication.\n"
                + "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");

    }

}
