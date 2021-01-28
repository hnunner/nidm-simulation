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
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensProfessionsSimulationSummaryWriter extends CsvFileWriter<NunnerBuskensProfessionsParameters> {

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
    public NunnerBuskensProfessionsSimulationSummaryWriter(String fileName,
            DataGeneratorData<NunnerBuskensProfessionsParameters> dgData) throws IOException {
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
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());

        // most interesting
        cols.add(LogValues.IV_NB_PROF_N.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE_THEORETIC.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_PROFESSION.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINATED.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINE_EFFICACY.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINATED_PERCENT.toString());
        cols.add(LogValues.IV_NB_PROF_QUARANTINED.toString());
        cols.add(LogValues.IV_NB_PROF_QUARANTINED_PERCENT.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_FINAL_SIZE.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_TIME.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_SIZE.toString());

        // network
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_AV_BETWEENNESS.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_AGE.toString());
        cols.add(LogValues.DV_NET_STABLE.toString());

        // index case
        cols.add(LogValues.DV_INDEX_PROFESSION.toString());
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_AGE.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_PROFESSION.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());

        // model parameters
        cols.add(LogValues.IV_NB_PROF_B1.toString());
        cols.add(LogValues.IV_NB_PROF_B2.toString());
        cols.add(LogValues.IV_NB_PROF_ALPHA.toString());
        cols.add(LogValues.IV_NB_PROF_C1.toString());
        cols.add(LogValues.IV_NB_PROF_C2.toString());
        cols.add(LogValues.IV_NB_PROF_OMEGA.toString());
        cols.add(LogValues.IV_NB_PROF_PHI.toString());
        cols.add(LogValues.IV_NB_PROF_PSI.toString());
        cols.add(LogValues.IV_NB_PROF_XI.toString());
        cols.add(LogValues.IV_NB_PROF_ZETA.toString());
        cols.add(LogValues.IV_NB_PROF_ALPHA.toString());
        cols.add(LogValues.IV_NB_PROF_GAMMA.toString());
        cols.add(LogValues.IV_NB_PROF_TAU.toString());

        // network
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            cols.add(LogValues.DV_NB_PROF_N + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_THEORETIC + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD_THEORETIC + profession.replaceAll("\\s+", "_").toLowerCase());
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
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));

        // most interesting
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getN()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegreeTheoretic()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityProfession()));
        List<String> vaccinated = this.dgData.getUtilityModelParams().getVaccinated();
        if (vaccinated == null || vaccinated.isEmpty()) {
            currData.add("");
        } else {
            currData.add(String.valueOf(vaccinated));
        }
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getEta()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getVaccinatedPercent()));

        List<String> quarantined = this.dgData.getUtilityModelParams().getQuarantined();
        if (quarantined == null || quarantined.isEmpty()) {
            currData.add("");
        } else {
            currData.add(String.valueOf(quarantined));
        }
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getQuarantinedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakSizeStatic()));

        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvBetweenness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityAge()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().isStable() ? 1 : 0));

        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getProfession()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityAge()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityProfession()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));

        // model parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPsi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getXi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getZeta()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTau()));

        // professions
        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            // number of agents
            double nByProfession = this.dgData.getNetStatsPre().getNByProfession(profession);
            currData.add(String.valueOf(nByProfession));
            if (nByProfession > 0) {
                // degree by profession
                currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegreeByProfession(profession)));
                currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegreeByProfessionTheoretic(profession)));
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

        // RELATED EXPORTS
        currData.add(this.dgData.getExportFileName());

        writeLine(currData);
    }

}
