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
package nl.uu.socnetid.nidm.io.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import nl.uu.socnetid.nidm.data.out.UtilityModelParameters;

/**
 * @author Hendrik Nunner
 *
 * @param <UMP>
 *          the type of {@link UtilityModelParameters}
 */
public class CsvFileReader {


    private static final Logger logger = LogManager.getLogger(CsvFileReader.class);
    private static final char DEFAULT_SEPARATOR = ',';
    
    private final String fileName;


    /**
     * Creates a generic nl.uu.socnetid.nidm.io.csv file writer.
     *
     * @param fileName
     *          the name of the file to store the nl.uu.socnetid.nidm.io.csv data in
     * @param dgData
     *          the data generator data
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CsvFileReader(String fileName) throws IOException {
        this.fileName = fileName;
    }


    public List<String[]> readFile() {
    	
    	List<String[]> res = null;
    	
    	CSVParser csvParser = new CSVParserBuilder().withSeparator(DEFAULT_SEPARATOR).build();
    	  try(CSVReader reader = new CSVReaderBuilder(
    	          new FileReader(fileName))
    	          .withCSVParser(csvParser)
    	          .withSkipLines(1)           // skip the first line, header info
    	          .build()){
    	      res = reader.readAll();
    	  } catch (IOException | CsvException e) {
			logger.error("Error while reading file '" + this.fileName + ",", e);;
		}
    	
    	return res; 
    }

}
