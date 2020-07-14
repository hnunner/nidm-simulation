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
package nl.uu.socnetid.nidm.io;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import nl.uu.socnetid.nidm.data.in.AgeStructure;

/**
 * @author Hendrik Nunner
 */
public class AgeStructureTest {

    // TODO comments
    // TODO proper tests
    @Test
    public void testDynamicWrite() throws Exception {

        Map<Integer, Integer> ages = new TreeMap<Integer, Integer>();

        for (int i = 0; i < 1000000; i++) {
            Integer age = AgeStructure.getInstance().getAgeFromAgeDependentDegreeDistribution(35);
            Integer currAge = ages.get(age);
            if (currAge == null) {
                ages.put(age, 1);
            } else {
                ages.put(age, ++currAge);
            }
        }

        Iterator<Integer> keyIt = ages.keySet().iterator();
        Double occsNormCum = 0.0;
        while (keyIt.hasNext()) {
            Integer age = keyIt.next();
            Integer occs = ages.get(age);
            Double occsNorm = occs/1000000.0;
            occsNormCum += occsNorm;
            System.out.println("Age: " + age +
                    "\t\toccs:  " + occs +
                    "\t\toccurences (normalized): " + Math.round(occsNorm * 10000.0) / 10000.0 +
                    "\t\toccurences (cumulated): " + Math.round(occsNormCum * 10000.0) / 10000.0);
        }
    }

}
