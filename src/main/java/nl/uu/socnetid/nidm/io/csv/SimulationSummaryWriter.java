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

        // model specific columns
        cols = addModelColumns(cols);

        // DEPENDENT VARIABLES (D)
        // simulation
        cols.add(LogValues.DV_SIM_EPIDEMIC_DURATION.toString());

        // PRE-EPIDEMIC
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
        cols.add(LogValues.DV_NET_ASSORTATIVITY_CONDITION.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_PRE.toString());
        cols.add(LogValues.DV_NET_STABLE_PRE.toString());

        // POST-EDIDEMIC
        // static
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_STATIC_TIES_BROKEN_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_NETWORK_CHANGES_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_DEGREE_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_DEGREE2_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_CLOSENESS_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_CLUSTERING_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_PATHLENGTH_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_UTIL_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_BENEFIT_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_COSTS_SOCIAL_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_AV_COSTS_DISEASE_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_DENSITY_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_ASSORTATIVITY_CONDITION.toString());
        cols.add(LogValues.DV_NET_STATIC_ASSORTATIVITY_POST.toString());
        cols.add(LogValues.DV_NET_STATIC_STABLE_POST.toString());
        cols.add(LogValues.DV_INDEX_STATIC_SATISFIED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_UTIL.toString());
        cols.add(LogValues.DV_INDEX_STATIC_BENEFIT_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_STATIC_COSTS_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_STATIC_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_INDEX_STATIC_DISEASE_STATE.toString());
        cols.add(LogValues.DV_INDEX_STATIC_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_INDEX_STATIC_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_STATIC_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_STATIC_BETWEENNESS.toString());
        cols.add(LogValues.DV_INDEX_STATIC_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_CONS_IN_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_STATIC_R_SIGMA.toString());
        cols.add(LogValues.DV_INDEX_STATIC_R_PI.toString());
        // dynamic
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
        cols.add(LogValues.DV_NET_DYNAMIC_ASSORTATIVITY_CONDITION.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_ASSORTATIVITY_POST.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_STABLE_POST.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_SATISFIED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_UTIL.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_BENEFIT_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_COSTS_SOCIAL.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_DISEASE_STATE.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_BETWEENNESS.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_CONS_IN_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_R_SIGMA.toString());
        cols.add(LogValues.DV_INDEX_DYNAMIC_R_PI.toString());

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

        // PRE-EPIDEMIC
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
        currData.add(this.dgData.getNetStatsPre().getAssortativityCondition().toString());
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().isStable() ? 1 : 0));

        // POST-EDIDEMIC
        // static
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getTiesBrokenWithInfectionPresent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getNetworkChangesWithInfectionPresent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvSocialCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAvDiseaseCosts()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getDensity()));
        currData.add(this.dgData.getNetStatsPre().getAssortativityCondition().toString());
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getAssortativity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().isStable() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().isSatisfied()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getUtility()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getSocialCosts()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getDiseaseCosts()));
        currData.add(this.dgData.getIndexCaseStatsStatic().getDiseaseGroup().name());
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getTimeToRecover()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getBetweenness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getBrokenTiesActive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getBrokenTiesPassive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getAcceptedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getDeclinedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getAcceptedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getDeclinedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getrSigma()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsStatic().getrPi()));
        // dynamic
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
        currData.add(this.dgData.getNetStatsPre().getAssortativityCondition().toString());
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getAssortativity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().isStable() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().isSatisfied()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getUtility()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getSocialBenefits()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getSocialCosts()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getDiseaseCosts()));
        currData.add(this.dgData.getIndexCaseStatsDynamic().getDiseaseGroup().name());
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getTimeToRecover()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getBetweenness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getBrokenTiesActive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getBrokenTiesPassive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getAcceptedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getDeclinedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getAcceptedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getDeclinedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getrSigma()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsDynamic().getrPi()));

        // RELATED EXPORTS
        currData.add(this.dgData.getGexfExportFile());

        writeLine(currData);
    }

}
