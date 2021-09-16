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
package nl.uu.socnetid.nidm.io.csv;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkLockdownParameters;
import nl.uu.socnetid.nidm.networks.LockdownConditions;

/**
 * @author Hendrik Nunner
 */
public class ProfessionNetworkLockdownWriter extends CsvFileWriter<ProfessionNetworkLockdownParameters> {

    /**
     * Creates the writer.
     *
     * @param fileName
     *          the name of the file to store the data to
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public ProfessionNetworkLockdownWriter(String fileName, DataGeneratorData<ProfessionNetworkLockdownParameters> dgData)
            throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {

        List<String> cols = new LinkedList<String>();

        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());

        // varied parameters
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_PROF_LOCKDOWN_CONDITION.toString());

        // network
        cols.add(LogValues.IV_NB_NET_SIZE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
//        cols.add(LogValues.DV_NET_AV_BETWEENNESS.toString());
//        cols.add(LogValues.DV_NET_AV_CLOSENESS.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_PROFESSION.toString());

        // fitness : degree
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            cols.add(LogValues.DV_NB_PROF_N + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_BELOT + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD_BELOT + profession.replaceAll("\\s+", "_").toLowerCase());
        }

        // export file
        cols.add(LogValues.EXPORT_FILENAME.toString());

        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getUid()));
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));

        // varied parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrLockdownCondition()));

        // network
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getN()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
//        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvBetweenness()));
//        currData.add("NA");
//        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvCloseness()));
//        currData.add("NA");
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength()));
//        currData.add("NA");
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativityProfession()));

        // fitness : degree
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            // number of agents
            double nByProfession = this.dgData.getNetStatsCurrent().getNByProfession(profession);
            currData.add(String.valueOf(nByProfession));
            if (nByProfession > 0) {
                // degree by profession
                currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegreeByProfession(profession)));
                currData.add(this.dgData.getUtilityModelParams().getCurrLockdownCondition().equals(LockdownConditions.PRE) ?
                        String.valueOf(Professions.getInstance().getDegreePreLockdown(profession)) :
                            String.valueOf(Professions.getInstance().getDegreeDuringLockdown(profession)));
                // av degree standard deviation by profession
                currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDegreeSdByProfession(profession)));
                currData.add(this.dgData.getUtilityModelParams().getCurrLockdownCondition().equals(LockdownConditions.PRE) ?
                        String.valueOf(Professions.getInstance().getDegreeErrorPreLockdown(profession)) :
                            String.valueOf(Professions.getInstance().getDegreeErrorDuringLockdown(profession)));
            } else {
                // degree by profession
                currData.add("NA");
                currData.add("NA");
                // av degree standard deviation by profession
                currData.add("NA");
                currData.add("NA");
            }
        }

        // RELATED IMPORTS / EXPORTS
        currData.add(this.dgData.getExportFileName());

        writeLine(currData);
    }

}
