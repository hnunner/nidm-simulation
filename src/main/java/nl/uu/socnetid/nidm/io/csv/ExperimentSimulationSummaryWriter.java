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
import java.util.Map.Entry;

import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ExperimentParameters;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;

/**
 * @author Hendrik Nunner
 */
public class ExperimentSimulationSummaryWriter extends CsvFileWriter<ExperimentParameters> {

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
    public ExperimentSimulationSummaryWriter(String fileName, DataGeneratorData<ExperimentParameters> dgData)
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
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());
        // varied model parameters
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
        cols.add(LogValues.IV_NB_TAU.toString());
        cols.add(LogValues.IV_NB_R_AV.toString());
//        cols.add(LogValues.IV_NB_R_ABOVE_AV.toString());
        cols.add(LogValues.IV_NB_R_MIN.toString());
        cols.add(LogValues.IV_NB_R_MAX.toString());
        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_PSI.toString());
        cols.add(LogValues.IV_NB_XI.toString());

        // PRE-EPIDEMIC
        // network
        cols.add(LogValues.DV_NET_AV_DEGREE_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_PRE.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH_PRE.toString());
        cols.add(LogValues.DV_NET_AV_BETWEENNESS_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_PRE.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_RISK_PERCEPTION_PRE.toString());
        // index case
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_RISK_PERCEPTION.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA_NEIGHBORHOOD.toString());

        // POST-EDIDEMIC
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_TIME.toString());
        cols.add(LogValues.DV_NET_EPIDEMIC_PEAK_SIZE.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_BROKEN_ACTIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_BROKEN_PASSIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_OUT_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_OUT_DECLINED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_IN_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_CONS_IN_DECLINED_EPIDEMIC.toString());

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
        // varied model parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTau()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAverageRiskScore()));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isAboveAverage()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMin()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMax()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPsi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getXi()));

        // PRE-EPIDEMIC
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvBetweenness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityRiskPerception()));
        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityRiskPerception()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigma()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigmaNeighborhood()));

        // POST-EDIDEMIC
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPostDynamic().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationDynamic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakDynamic()));
        currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakSizeDynamic()));

        int brokenTiesActiveEpidemic = 0;
        int brokenTiesPassiveEpidemic = 0;
        int acceptedRequestsOutEpidemic = 0;
        int declinedRequestsOutEpidemic  = 0;
        int acceptedRequestsInEpidemic  = 0;
        int declinedRequestsInEpidemic  = 0;
        for (Entry<String, AgentStatsPost> entry:this.dgData.getAgentStatsPostDynamic().entrySet()) {
            AgentStatsPost agentStats = entry.getValue();
            brokenTiesActiveEpidemic += agentStats.getBrokenTiesActiveEpidemic();
            brokenTiesPassiveEpidemic += agentStats.getBrokenTiesPassiveEpidemic();
            acceptedRequestsOutEpidemic += agentStats.getAcceptedRequestsOutEpidemic();
            declinedRequestsOutEpidemic += agentStats.getDeclinedRequestsOutEpidemic();
            acceptedRequestsInEpidemic += agentStats.getAcceptedRequestsInEpidemic();
            declinedRequestsInEpidemic += agentStats.getDeclinedRequestsInEpidemic();
        }
        currData.add(String.valueOf(brokenTiesActiveEpidemic));
        currData.add(String.valueOf(brokenTiesPassiveEpidemic));
        currData.add(String.valueOf(acceptedRequestsOutEpidemic));
        currData.add(String.valueOf(declinedRequestsOutEpidemic));
        currData.add(String.valueOf(acceptedRequestsInEpidemic));
        currData.add(String.valueOf(declinedRequestsInEpidemic));

        writeLine(currData);
    }

}
