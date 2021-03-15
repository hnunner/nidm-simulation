/*
 * Copyright (C) 2017 - 2020
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.generator.data.BurgerBuskensDataGenerator;
import nl.uu.socnetid.nidm.io.generator.data.CarayolRouxDataGenerator;
import nl.uu.socnetid.nidm.io.generator.data.CidmDataGenerator;
import nl.uu.socnetid.nidm.io.generator.data.NunnerBuskensDataGenerator;
import nl.uu.socnetid.nidm.io.generator.data.NunnerBuskensProfessionsDataGenerator;
import nl.uu.socnetid.nidm.io.generator.network.NunnerBuskensNetworkGenerator;
import nl.uu.socnetid.nidm.io.generator.network.NunnerBuskensNetworkGeneratorGenetic;
import nl.uu.socnetid.nidm.io.generator.network.NunnerBuskensNetworkGeneratorSimple;
import nl.uu.socnetid.nidm.io.generator.network.ProfessionsNetworkGeneratorGenetic;
import nl.uu.socnetid.nidm.io.generator.network.ProfessionsNetworkLockdownGenerator;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class Generator {

    // logger
    private static final Logger logger = LogManager.getLogger(Generator.class);

    // time of invocation
    private static final String TIME_OF_INVOCATION = (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date());

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
        Generator generator = new Generator();
        generator.generate();
    }


    /**
     * Initializes the data generation, by logging the copyright agreement, preparing the export path,
     * and copying the properties file.
     */
    protected void initialize() {

        logger.info("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                ":: Copyright (C) 2017 - 2020\n" +
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
        String datedExportPath = getDatedExportPath();
        File directory = new File(datedExportPath);
        if (!directory.exists()){
            directory.mkdirs();
        }

        // copy properties file to root export directory
        try {
            Path srcProperties = Paths.get(Generator.class.getClassLoader().getResource("config.properties").toURI());
            Path dstProperties = Paths.get(datedExportPath + "/config.properties");
            Files.copy(srcProperties, dstProperties, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }

    }

    /**
     * @return the path for data exports
     */
    private String getDatedExportPath() {
        return PropertiesHandler.getInstance().getRootExportPath() + TIME_OF_INVOCATION;
    }

    /**
     * @return the path for data exports
     */
    private String getDataExportPath() {
        return PropertiesHandler.getInstance().getRootExportPath() +
                TIME_OF_INVOCATION + "/data/";
    }

    /**
     * @return the path for network exports
     */
    private String getNetworkExportPath() {
        return PropertiesHandler.getInstance().getRootExportPath() +
                TIME_OF_INVOCATION + "/networks/";
    }


    /**
     * Performs generation of data, networks, ...
     *
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    private void generate() throws IOException {

        // initialization
        this.initialize();

        // invoke data generators
        AbstractGenerator dataGenerator;
        // CIDM (Nunner, Buskens & Kretzschmar, 2019)
        if (PropertiesHandler.getInstance().isGenerateCidmData()) {
            dataGenerator = new CidmDataGenerator(getDataExportPath());
            dataGenerator.launch();
        }
        // Burger & Buskens (2009)
        if (PropertiesHandler.getInstance().isGenerateBurgerBuskensData()) {
            dataGenerator = new BurgerBuskensDataGenerator(getDataExportPath());
            dataGenerator.launch();
        }
        // Carayol & Roux (2009)
        if (PropertiesHandler.getInstance().isGenerateCarayolRouxData()) {
            dataGenerator = new CarayolRouxDataGenerator(getDataExportPath());
            dataGenerator.launch();
        }
        // Nunner & Buskens (2019)
        if (PropertiesHandler.getInstance().isGenerateNunnerBuskensData()) {
            dataGenerator = new NunnerBuskensDataGenerator(getDataExportPath());
            dataGenerator.launch();
        }
        // Nunner & Buskens (2019)
        if (PropertiesHandler.getInstance().isGenerateNunnerBuskensNetworks()) {
            dataGenerator = new NunnerBuskensNetworkGenerator(getNetworkExportPath());
            dataGenerator.launch();
        }
        // Nunner & Buskens (2019)
        if (PropertiesHandler.getInstance().isGenerateNunnerBuskensNetworksSimple()) {
            dataGenerator = new NunnerBuskensNetworkGeneratorSimple(getNetworkExportPath());
            dataGenerator.launch();
        }
        // Nunner & Buskens (2019)
        if (PropertiesHandler.getInstance().isGenerateNunnerBuskensNetworksGenetic()) {
            dataGenerator = new NunnerBuskensNetworkGeneratorGenetic(getNetworkExportPath());
            dataGenerator.launch();
        }
        // Nunner & Buskens (2019)
        if (PropertiesHandler.getInstance().isGenerateNunnerBuskensNetworksProfessions()) {
//            dataGenerator = new NunnerBuskensNetworkGeneratorProfessions(getNetworkExportPath());
            dataGenerator = new NunnerBuskensProfessionsDataGenerator(getNetworkExportPath());
            dataGenerator.launch();
        }
        // Professions
        if (PropertiesHandler.getInstance().isGenerateProfessionNetworksGenetic()) {
            dataGenerator = new ProfessionsNetworkGeneratorGenetic(getNetworkExportPath());
            dataGenerator.launch();
        }
        // Professions lockdown
        if (PropertiesHandler.getInstance().isGenerateProfessionNetworksLockdown()) {
            dataGenerator = new ProfessionsNetworkLockdownGenerator(getNetworkExportPath());
            dataGenerator.launch();
        }

    }

}
