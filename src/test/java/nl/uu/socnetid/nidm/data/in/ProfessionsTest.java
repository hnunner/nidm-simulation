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
package nl.uu.socnetid.nidm.data.in;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.system.PropertiesHandler;


/**
 * Test cases for the {@link Agent} class.
 *
 * @author Hendrik Nunner
 */
public class ProfessionsTest {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsTest.class);

    // profession distribution
    Map<String, Double> pdGlobal;
    // profession degrees pre lockdown
    Map<String, Double> degreesPreGlobal;
    // profession degree during lockdown
    Map<String, Double> degreesDuringGlobal;

    /**
     * Initialize data required for comparison tests.
     */
    @Before
    public void init() {
        this.pdGlobal = new HashMap<String, Double>();
        double totalEntries = 0;

        this.degreesPreGlobal = new HashMap<String, Double>();
        this.degreesDuringGlobal = new HashMap<String, Double>();


        Path pathToFile = PropertiesHandler.getInstance().getProfessionsImportPath();
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(";");

                String profession = attributes[0];
                Double entries = Double.valueOf(attributes[1]);

                // profession distribution
                this.pdGlobal.put(profession, entries);
                totalEntries += entries;

                // profession degree pre lockdown
                this.degreesPreGlobal.put(profession, Double.valueOf(attributes[2 ]));

                // profession degree during lockdown
                this.degreesDuringGlobal.put(profession, Double.valueOf(attributes[4]));

                line = br.readLine();
            }

        } catch (IOException ioe) {
            logger.error("Error while parsing profession distribution.", ioe);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                    logger.error("Error while closing BufferedReader.", ioe);
                }
            }
        }

        for (Entry<String, Double> entry:this.pdGlobal.entrySet()) {
            this.pdGlobal.put(entry.getKey(), entry.getValue()/totalEntries);
        }
    }


    /**
     * Test whether profession distribution is loaded properly.
     */
    @Test
    public void testProfessionDistribution() {

        Map<String, Integer> pd = new HashMap<String, Integer>();
        int maxIt = 100000;

        for (int i = 0; i < maxIt; i++) {
            String profession = Professions.getInstance().getRandomProfession();
            if (pd.containsKey(profession)) {
                pd.put(profession, pd.get(profession) + 1);
            } else {
                pd.put(profession, 1);
            }
        }

        for (Entry<String, Integer> entry:pd.entrySet()) {
            assertEquals(this.pdGlobal.get(entry.getKey()), ((double) entry.getValue()) / maxIt, 0.01);
        }
    }

    /**
     * Test getting degrees per profession prior to lockdown.
     */
    @Test
    public void testGetDegrees() {
        for (String profession:this.pdGlobal.keySet()) {
            assertEquals(this.degreesPreGlobal.get(profession),
                    Professions.getInstance().getDegreePreLockdown(profession), 0.01);

            assertEquals(this.degreesDuringGlobal.get(profession),
                    Professions.getInstance().getDegreeDuringLockdown(profession), 0.01);
        }
    }

}
