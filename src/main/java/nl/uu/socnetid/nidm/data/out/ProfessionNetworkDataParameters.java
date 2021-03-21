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
package nl.uu.socnetid.nidm.data.out;

import java.util.List;
import java.util.Map;

/**
 * @author Hendrik Nunner
 */
public class ProfessionNetworkDataParameters extends UtilityModelParameters {

    private final List<String> colNames;
    private final Map<String, List<String>> networkSummaryLines;

    private String currFile;


    public ProfessionNetworkDataParameters(List<String> colNames, Map<String, List<String>> networkSummaryLines) {
        this.colNames = colNames;
        this.networkSummaryLines = networkSummaryLines;
    }


    /**
     * @return the colNames
     */
    public List<String> getColNames() {
        return colNames;
    }

    /**
     * @return the network summary line of the file
     */
    public List<String> getCurrNetworkSummaryLine() {
        return networkSummaryLines.get(this.currFile);
    }

    /**
     * @param currFile the currFile to set
     */
    public void setCurrFile(String currFile) {
        this.currFile = currFile;
    }

    /**
     * @return the currFile
     */
    public String getCurrFile() {
        return currFile;
    }

}
