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
package nl.uu.socnetid.nidm.io.generator;

import java.io.File;
import java.io.IOException;

import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractDataGenerator {

    // paths
    private String rootExportPath;
    private String exportPath;


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
        this.rootExportPath = rootExportPath;
        this.exportPath = this.rootExportPath + this.getFolderName() + "/";
        initPaths();
        initData();
        initWriters();
    }


    /**
     * Gets the name of the folder to store generator specific data in.
     *
     * @return the name of the folder to store generator specific data in
     */
    protected abstract String getFolderName();

    /**
     * Initializes the required paths.
     */
    private void initPaths() {
        File directory = new File(getExportPath());
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    /**
     * Initializes the required data.
     */
    protected abstract void initData();

    /**
     * Initializes the required writers.
     *
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    protected abstract void initWriters() throws IOException;

    /**
     * Launches the data generation.
     */
    public void launch() {
        generateData();
        if (PropertiesHandler.getInstance().isAnalyzeData()) {
            analyzeData();
        }
    }

    /**
     * Generates the data.
     */
    protected abstract void generateData();

    /**
     * Analyzes the data.
     */
    protected abstract void analyzeData();


    /**
     * @return the rootExportPath
     */
    protected String getRootExportPath() {
        return rootExportPath;
    }

    /**
     * @return the exportPath
     */
    protected String getExportPath() {
        return this.exportPath;
    }

}
