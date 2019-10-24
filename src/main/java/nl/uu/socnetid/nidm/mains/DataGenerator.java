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
package nl.uu.socnetid.nidm.mains;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator;
import nl.uu.socnetid.nidm.io.generator.BurgerBuskensDataGenerator;
import nl.uu.socnetid.nidm.io.generator.CarayolRouxDataGenerator;
import nl.uu.socnetid.nidm.io.generator.CidmDataGenerator;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class DataGenerator {

    // logger
    private static final Logger logger = Logger.getLogger(DataGenerator.class);


    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public static void main(String[] args) throws IOException {
        logger.info("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                ":: Copyright (C) 2017 - 2019\n" +
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



        // initializations
        // create root export directory if it does not exist
        String rootExportPath = PropertiesHandler.getInstance().getDataExportPath() +
                (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
        File directory = new File(rootExportPath);
        if (!directory.exists()){
            directory.mkdirs();
        }

        // copy properties file to root export directory
        try {
            Path srcProperties = Paths.get(DataGenerator.class.getClassLoader().getResource("config.properties").toURI());
            Path dstProperties = Paths.get(rootExportPath + "config.properties");
            Files.copy(srcProperties, dstProperties, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException e) {
            logger.error(e);
        }

        // invoke data generators
        AbstractDataGenerator dataGenerator;
        // CIDM
        if (PropertiesHandler.getInstance().isGenerateCidm()) {
            dataGenerator = new CidmDataGenerator(rootExportPath);
            dataGenerator.launch();
        }
        // Burger & Buskens (2009)
        if (PropertiesHandler.getInstance().isGenerateBurgerBuskens()) {
            dataGenerator = new BurgerBuskensDataGenerator(rootExportPath);
            dataGenerator.launch();
        }
        // Carayol & Roux (2009)
        if (PropertiesHandler.getInstance().isGenerateCarayolRoux()) {
            dataGenerator = new CarayolRouxDataGenerator(rootExportPath);
            dataGenerator.launch();
        }
    }
}
