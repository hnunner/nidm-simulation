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

import java.io.IOException;

import nl.uu.socnetid.nidm.data.BurgerBuskensDataGeneratorData;
import nl.uu.socnetid.nidm.io.csv.CsvFileWriter;

/**
 * @author Hendrik Nunner
 *
 * TODO generalize, as this class resembles {@link CidmCsvFileWriter} too much
 */
public abstract class BurgerBuskensCsvFileWriter extends CsvFileWriter {

    protected BurgerBuskensDataGeneratorData dgData;


    /**
     * Creates a generic nl.uu.socnetid.nidm.io.csv file writer.
     *
     * @param fileName
     *          the name of the file to store the nl.uu.socnetid.nidm.io.csv data in
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public BurgerBuskensCsvFileWriter(String fileName, BurgerBuskensDataGeneratorData dgData) throws IOException {
        super(fileName);
        this.dgData = dgData;
        initCols();
    }


    /**
     * Initializes the CSV by writing the column names.
     */
    protected abstract void initCols();

    /**
     * Writes a line of data as currently stored in dgData.
     */
    public abstract void writeCurrentData();

}
