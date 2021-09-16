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
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkGeneticParameters;

/**
 * @author Hendrik Nunner
 */
public class ProfessionGeneticNetworkSummaryWriter extends CsvFileWriter<ProfessionNetworkGeneticParameters> {

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
    public ProfessionGeneticNetworkSummaryWriter(String fileName, DataGeneratorData<ProfessionNetworkGeneticParameters> dgData)
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
//        cols.add(LogValues.IV_SIM_CNT.toString());
//        cols.add(LogValues.IV_SIM_IT.toString());

        // genetics
        cols.add(LogValues.IV_NB_GEN_GENERATION.toString());
        cols.add(LogValues.IV_NB_GEN_SIMPLE_ID.toString());
        cols.add(LogValues.IV_NB_GEN_MOTHER.toString());
        cols.add(LogValues.IV_NB_GEN_FATHER.toString());

        // varied parameters
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_PROF_LOCKDOWN_CONDITION.toString());

        // fitness : overall
        cols.add(LogValues.DV_NET_GEN_FITNESS_OVERALL.toString());
        // fitness : clustering
        cols.add(LogValues.IV_NB_GEN_CLUSTERING_DANON.toString());
        cols.add(LogValues.IV_NB_GEN_CLUSTERING_TARGET.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_GEN_FITNESS_CLUSTERING.toString());
        // fitness : degree
        cols.add(LogValues.DV_NET_GEN_FITNESS_AV_DEGREE.toString());
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            cols.add(LogValues.DV_NB_PROF_N + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_BELOT + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_TARGET + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NET_GEN_FITNESS_DEGREE + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD_TARGET + profession.replaceAll("\\s+", "_").toLowerCase());
        }

        // model parameters
        cols.add(LogValues.IV_NB_B1.toString());
        cols.add(LogValues.IV_NB_B2.toString());
        cols.add(LogValues.IV_NB_C1.toString());
        cols.add(LogValues.IV_NB_NET_SIZE.toString());
        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_PSI.toString());
        cols.add(LogValues.IV_NB_XI.toString());

        // export file
//        cols.add(LogValues.IV_NB_PROF_LOCKDOWN_NETWORK_BASE_FILE.toString());
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
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
//        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
//        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));

        // genetics
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getGeneration()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getSimpleId()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getMother()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFather()));
        // varied parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrLockdownCondition()));
        // fitness : overall
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessOverall()));
        // fitness : clustering
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getDanonClustering()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getTargetClustering()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getClustering()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessClustering()));
        // fitness : degree
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessAvDegrees()));
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            // number of agents
            double nByProfession = this.dgData.getNetStatsPre().getNByProfession(profession);
            currData.add(String.valueOf(nByProfession));
            if (nByProfession > 0) {
                // degree by profession
                currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getBelotAvDegree(profession)));
                currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getTargetAvDegree(profession)));
                currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getAvDegree(profession)));
                currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessAvDegreesByProfession(profession)));
                // av degree standard deviation by profession
                currData.add(String.valueOf(this.dgData.getNetStatsPre().getDegreeSdByProfession(profession)));
                currData.add(String.valueOf(this.dgData.getNetStatsPre().getDegreeSdByProfessionTheoretic(profession)));
            } else {
                // degree by profession
                currData.add("NA");
                currData.add("NA");
                // av degree standard deviation by profession
                currData.add("NA");
                currData.add("NA");
            }
        }

        // model parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPsi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getXi()));

        // RELATED IMPORTS / EXPORTS
//        currData.add(this.dgData.getUtilityModelParams().getLockdownNetworkBaseFile());
        currData.add(this.dgData.getExportFileName());

        writeLine(currData);
    }

}
