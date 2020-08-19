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
import nl.uu.socnetid.nidm.data.out.UtilityModelParameters;

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
        cols.add(LogValues.IV_SIM_IT.toString());

        // model specific columns
        cols = addModelColumns(cols);

        // DEPENDENT VARIABLES (D)
        // PRE-EPIDEMIC
        // network
        cols.add(LogValues.DV_NET_AV_DEGREE_PRE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE2_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_PRE.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH_PRE.toString());
        cols.add(LogValues.DV_NET_AV_UTIL_PRE.toString());
//        cols.add(LogValues.DV_NET_AV_BENEFIT_SOCIAL_PRE.toString());
//        cols.add(LogValues.DV_NET_AV_COSTS_SOCIAL_PRE.toString());
//        cols.add(LogValues.DV_NET_AV_COSTS_DISEASE_PRE.toString());
        cols.add(LogValues.DV_NET_DENSITY_PRE.toString());
//        cols.add(LogValues.DV_NET_ASSORTATIVITY_CONDITION.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_PRE.toString());
        cols.add(LogValues.DV_NET_STABLE_PRE.toString());
        // index case
        cols.add(LogValues.DV_INDEX_DISEASE_STATE.toString());
        cols.add(LogValues.DV_INDEX_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
//        cols.add(LogValues.DV_INDEX_BETWEENNESS.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA_NEIGHBORHOOD.toString());
        cols.add(LogValues.DV_INDEX_R_PI.toString());
        cols.add(LogValues.DV_INDEX_R_PI_NEIGHBORHOOD.toString());

        // POST-EDIDEMIC
        // static
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_MAX_INFECTIONS.toString());
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_PEAK.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_STATIC_TIES_BROKEN_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_NETWORK_CHANGES_EPIDEMIC.toString());
        // dynamic
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_MAX_INFECTIONS.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_PEAK.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_TIES_BROKEN_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_NETWORK_CHANGES_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_DEGREE_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_DEGREE2_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_CLOSENESS_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_CLUSTERING_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_PATHLENGTH_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_UTIL_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_BENEFIT_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_COSTS_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_AV_COSTS_DISEASE_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_DENSITY_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_ASSORTATIVITY_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_STABLE_POST.toString());

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
        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));

        // model specific data
        currData = addCurrModelData(currData);

        // DEPENDENT VARIABLES
        // PRE-EPIDEMIC
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvUtility()));
//        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvSocialBenefits()));
//        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvSocialCosts()));
//        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDiseaseCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getDensity()));
//        currData.add(this.dgData.getNetStatsPre().getAssortativityCondition().toString());
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().isStable() ? 1 : 0));
        // index case
        currData.add(this.dgData.getIndexCaseStats().getDiseaseGroup().name());
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getTimeToRecover()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
//        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweenness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigma()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigmaNeighborhood()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrPi()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrPiNeighborhood()));

        // POST-EDIDEMIC
        // static
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicMaxInfectionsStatic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakStatic()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getTiesBrokenWithInfectionPresent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getNetworkChangesWithInfectionPresent()));
        // dynamic
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationDynamic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicMaxInfectionsDynamic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakDynamic()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getTiesBrokenWithInfectionPresent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getNetworkChangesWithInfectionPresent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvSocialCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAvDiseaseCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getDensity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAssortativity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().isStable() ? 1 : 0));

        // RELATED EXPORTS
        currData.add(this.dgData.getGexfExportFile());

        writeLine(currData);
    }

}
