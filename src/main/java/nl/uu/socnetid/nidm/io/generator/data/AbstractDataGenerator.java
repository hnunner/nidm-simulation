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
package nl.uu.socnetid.nidm.io.generator.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractDataGenerator extends AbstractGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(AbstractDataGenerator.class);

    /**
     * Constructor.
     *
     * @param rootExportPath
     *          path to store generated data in
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public AbstractDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#generate()
     */
    @Override
    public void launch() {
        super.launch();
        if (PropertiesHandler.getInstance().isAnalyzeData()) {
            analyzeData(prepareAnalysis());
        }
    }

    /**
     * Prepares the data analysis by copying necessary file(s) to their locations.
     *
     * @return the path of the main analysis file
     */
    protected abstract String prepareAnalysis();

    /**
     * Analyzes the data.
     *
     * @param analysisPath
     *          the path of the main analysis file
     */
    protected void analyzeData(String analysisPath) {
        try {
            // invocation of R-script
            ProcessBuilder pb = new ProcessBuilder(PropertiesHandler.getInstance().getRscriptPath(),
                    analysisPath, getExportPath());
            logger.info("Starting analysis of simulated data. "
                    + "Invoking R-script: "
                    + pb.command().toString());
            Process p = pb.start();

            // status messages of R-script
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            // wait for analysis to finish (blocking)
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                logger.info("Analysis finished successfully.");
            } else {
                logger.error("Analysis finished with error code: " + exitCode);
            }

        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

}
