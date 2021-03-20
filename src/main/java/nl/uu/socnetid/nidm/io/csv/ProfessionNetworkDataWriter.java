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
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkDataParameters;

/**
 * @author Hendrik Nunner
 */
public class ProfessionNetworkDataWriter extends CsvFileWriter<ProfessionNetworkDataParameters> {

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
    public ProfessionNetworkDataWriter(String fileName, DataGeneratorData<ProfessionNetworkDataParameters> dgData)
            throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();
        cols.addAll(this.dgData.getUtilityModelParams().getColNames());

        cols.add(LogValues.IV_NB_SIM_ITERATIONS.toString());

        cols.add(LogValues.IV_NB_PROF_VACCINE_DISTRIBUTION.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINE_AVAILIBILITY.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINE_EFFICACY.toString());

        cols.add(LogValues.IV_NB_PROF_VACCINE_SHOTS_GIVEN.toString());
        cols.add(LogValues.IV_NB_PROF_VACCINE_AGENTS_IMMUNIZED.toString());
        cols.add(LogValues.IV_NB_PROF_GROUPS_RECEIVED_SHOTS.toString());

        cols.add(LogValues.DV_NET_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_VACCINATED.toString());

        cols.add(LogValues.DV_NET_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_SIZE.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_TIME.toString());

        cols.add(LogValues.DV_INDEX_ID.toString());
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
//        cols.add(LogValues.DV_INDEX_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
//        cols.add(LogValues.DV_INDEX_BETWEENNESS.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_PROFESSION.toString());

        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {

        List<String> currData = new LinkedList<String>();
        currData.addAll(this.dgData.getUtilityModelParams().getCurrNetworkSummaryLine());

        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));

        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrVaxDist()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrTheta()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrEta()));

        currData.add(String.valueOf(this.dgData.getSimStats().getShotsGiven()));
        currData.add(String.valueOf(this.dgData.getSimStats().getAgentsImmunized()));
        currData.add(String.valueOf(this.dgData.getSimStats().getProfessionsReceivedShots()));

        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getVaccinatedPercent()));

        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakSizeStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakStatic()));

        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getId()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
//        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
//        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweenness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityProfession()));

        writeLine(currData);
    }

}
