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
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensSimulationSummaryWriterReduced extends CsvFileWriter<NunnerBuskensParameters> {

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
    public NunnerBuskensSimulationSummaryWriterReduced(String fileName, DataGeneratorData<NunnerBuskensParameters> dgData)
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
        // network (static / dynamic)
        cols.add(LogValues.IV_NB_EP_STRUCTURE.toString());
        // varied model parameters
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_SELECTIVE.toString());
        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
        cols.add(LogValues.IV_NB_R_MIN.toString());
        cols.add(LogValues.IV_NB_R_MAX.toString());
        cols.add(LogValues.IV_NB_R_SIGMA_AV.toString());

        // PRE-EPIDEMIC
        // network
        cols.add(LogValues.DV_NET_AV_DEGREE_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_PRE.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH_PRE.toString());
        cols.add(LogValues.DV_NET_AV_BETWEENNESS_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_PRE.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_RISK_PERCEPTION_PRE.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_AGE_PRE.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_PROFESSION_PRE.toString());
        cols.add(LogValues.DV_NET_STABLE_PRE.toString());
        // index case
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_RISK_PERCEPTION.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_AGE.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_PROFESSION.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA.toString());
        cols.add(LogValues.DV_INDEX_R_SIGMA_NEIGHBORHOOD.toString());

        // POST-EDIDEMIC
        // static
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_STATIC_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_PEAK.toString());
        cols.add(LogValues.DV_NET_STATIC_EPIDEMIC_PEAK_SIZE.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_BROKEN_ACTIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_BROKEN_PASSIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_OUT_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_OUT_DECLINED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_IN_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_STATIC_CONS_IN_DECLINED_EPIDEMIC.toString());

        // dynamic
        cols.add(LogValues.DV_NET_DYNAMIC_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_DURATION.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_PEAK.toString());
        cols.add(LogValues.DV_NET_DYNAMIC_EPIDEMIC_PEAK_SIZE.toString());
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
        // network (static / dynamic)
        currData.add(this.dgData.getUtilityModelParams().getEpStructure().toString());
        // varied model parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrSelective()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMin()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMax()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRSigmaAv()));

        // PRE-EPIDEMIC
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvBetweenness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityRiskPerception()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityAge()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAssortativityProfession()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().isStable() ? 1 : 0));
        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityRiskPerception()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityAge()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAssortativityProfession()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigma()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getrSigmaNeighborhood()));

        // POST-EDIDEMIC
        // static
        if (this.dgData.getNetStatsPostStatic() == null) {
        	for (int i = 0; i < 11; i++) {
        		currData.add("NA");
        	}
        } else {
        	currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getRecoveredPercent()));
        	currData.add(String.valueOf(this.dgData.getNetStatsPostStatic().getInfectedPercent()));
        	currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicDurationStatic()));
        	currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakStatic()));
        	currData.add(String.valueOf(this.dgData.getSimStats().getEpidemicPeakSizeStatic()));

        	int brokenTiesActiveEpidemic = 0;
        	int brokenTiesPassiveEpidemic = 0;
        	int acceptedRequestsOutEpidemic = 0;
        	int declinedRequestsOutEpidemic  = 0;
        	int acceptedRequestsInEpidemic  = 0;
        	int declinedRequestsInEpidemic  = 0;
        	for (Entry<String, AgentStatsPost> entry:this.dgData.getAgentStatsPostStatic().entrySet()) {
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
        }

        // dynamic
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
