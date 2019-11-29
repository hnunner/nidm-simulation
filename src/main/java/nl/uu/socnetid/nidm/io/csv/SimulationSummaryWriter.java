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

import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;
import nl.uu.socnetid.nidm.data.UtilityModelParameters;

/**
 * @author Hendrik Nunner
 *
 * @param <UMP>
 *          the type of {@link UtilityModelParameters}
 */
public abstract class SimulationSummaryWriter<UMP extends UtilityModelParameters> extends CsvFileWriter<UMP> {

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
    public SimulationSummaryWriter(String fileName, DataGeneratorData<UMP> dgData) throws IOException {
        super(fileName, dgData);
    }

    /**
     * Adds columns specific for the model implementation.
     *
     * @param cols
     *          the list of columns to add to
     * @return the list with the added columns
     */
    protected abstract List<String> addModelColumns(List<String> cols);

    /**
     * Adds current data specific for the model implementation.
     *
     * @param currData
     *          the list of current data to add to
     * @return the list with the added data
     */
    protected abstract List<String> addCurrModelData(List<String> currData);


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {

        List<String> cols = new LinkedList<String>();

        // INDEPENDENT VARIABLES (I)
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());

        // model specific columns
        cols = addModelColumns(cols);

        // DEPENDENT VARIABLES (D)
        // simulation
        cols.add(LogValues.DV_SIM_EPIDEMIC_DURATION.toString());
        // network
        // pre-epidemic
        cols.add(LogValues.DV_NET_AV_DEGREE_PRE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE2_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_PRE.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH_PRE.toString());
        cols.add(LogValues.DV_NET_AV_UTIL_PRE.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_SOCIAL_PRE.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_SOCIAL_PRE.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DISEASE_PRE.toString());
        cols.add(LogValues.DV_NET_DENSITY_PRE.toString());
        // post-epidemic
        cols.add(LogValues.DV_NET_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_TIES_BROKEN_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE_POST.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE2_POST.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_POST.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_POST.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH_POST.toString());
        cols.add(LogValues.DV_NET_AV_UTIL_POST.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DISEASE_POST.toString());
        cols.add(LogValues.DV_NET_DENSITY_POST.toString());
        // index case
        cols.add(LogValues.DV_INDEX_SATISFIED.toString());
        cols.add(LogValues.DV_INDEX_UTIL.toString());
        cols.add(LogValues.DV_INDEX_BENEFIT_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_COSTS_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_INDEX_DISEASE_STATE.toString());
        cols.add(LogValues.DV_INDEX_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_INDEX_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_INDEX_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_CONS_IN_DECLINED.toString());

        // RELATED EXPORTS
        cols.add(LogValues.GEXF_FILE.toString());

        writeLine(cols);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        // INDEPENDENT VARIABLES
        // simulation
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));

        // model specific data
        currData = addCurrModelData(currData);

        // DEPENDENT VARIABLES
        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getRoundLastInfection()));
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvSocialCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDiseaseCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getDensity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().isTiesBrokenWithInfectionPresent() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvSocialCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvDiseaseCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getDensity()));
        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().isSatisfied()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getUtility()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getSocialCosts()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDiseaseCosts()));
        currData.add(this.dgData.getIndexCaseStats().getDiseaseGroup().name());
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getTimeToRecover()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweenness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBrokenTiesActive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBrokenTiesPassive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAcceptedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDeclinedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAcceptedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDeclinedRequestsIn()));

        // RELATED EXPORTS
        currData.add(this.dgData.getGexfExportFile());

        writeLine(currData);
    }

}
