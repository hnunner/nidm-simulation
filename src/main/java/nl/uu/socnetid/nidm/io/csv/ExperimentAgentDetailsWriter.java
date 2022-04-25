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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ExperimentParameters;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;

/**
 * @author Hendrik Nunner
 */
public class ExperimentAgentDetailsWriter extends CsvFileWriter<ExperimentParameters> {

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
    public ExperimentAgentDetailsWriter(String fileName, DataGeneratorData<ExperimentParameters> dgData) throws IOException {
        super(fileName, dgData);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();

        // INDEPENDENT VARIABLES
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());
        cols.add(LogValues.IV_AGENT_ID.toString());

        // varied model parameters
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
        cols.add(LogValues.IV_NB_TAU.toString());
        cols.add(LogValues.IV_NB_R_AV.toString());
        cols.add(LogValues.IV_NB_R_SIGMA.toString());
        cols.add(LogValues.IV_NB_R_SIGMA_NEIGHBORHOOD.toString());
        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_PSI.toString());
        cols.add(LogValues.IV_NB_XI.toString());
        cols.add("rationality");
        cols.add("overestimate");
        cols.add("rationality.infected.neighbor");

        // PRE-EPIDEMIC
        // agent
        cols.add(LogValues.DV_AGENT_DEGREE1.toString());
        cols.add(LogValues.DV_AGENT_CLUSTERING.toString());
        cols.add(LogValues.DV_AGENT_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_AGENT_ASSORTATIVITY_RISK_PERCEPTION.toString());
        cols.add(LogValues.DV_AGENT_CLOSENESS.toString());
        cols.add(LogValues.DV_AGENT_INDEX_DISTANCE.toString());

        // POST-EPIDEMIC
        // agent
        cols.add(LogValues.DV_AGENT_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_FORCE_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_WHEN_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_ACTIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_PASSIVE_EPIDEMIC.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_DECLINED_EPIDEMIC.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_ACCEPTED_EPIDEMIC.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_DECLINED_EPIDEMIC.toString());

        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {

        List<Agent> agents = this.dgData.getAgents();
        Collections.sort(agents);

        for (Agent agent : agents) {

            // a single CSV row
            List<String> currData = new LinkedList<String>();

            // INDEPENDENT VARIABLES
            // simulation
            currData.add(this.dgData.getSimStats().getUid());
            currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));
            currData.add(agent.getId());

            // varied model parameters
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getSigma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getGamma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTau()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAverageRiskScore()));

            AgentStatsPre agentStatsPre = this.dgData.getAgentStatsPre().get(agent.getId());
            currData.add(String.valueOf(agentStatsPre.getrSigma()));
            currData.add(String.valueOf(agentStatsPre.getrSigmaNeighborhood()));

            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPhi()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPsi()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getXi()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRationality()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOverestimate()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRationalityInfectedNeighbor()));

            // PRE-EPIDEMIC
            // agent
            currData.add(String.valueOf(agentStatsPre.getDegree1()));
            currData.add(String.valueOf(agentStatsPre.getClustering()));
            currData.add(String.valueOf(agentStatsPre.getBetweennessNormalized()));
            currData.add(String.valueOf(agentStatsPre.getAssortativityRiskPerception()));
            currData.add(String.valueOf(agentStatsPre.getCloseness()));
            currData.add(String.valueOf(agentStatsPre.getIndexCaseDistance()));

            // POST-EPIDEMIC
            // agent
            AgentStatsPost agentStatsPost = this.dgData.getAgentStatsPostDynamic().get(agent.getId());
            currData.add(agentStatsPost.getDiseaseGroup() == DiseaseGroup.INFECTED
                    || agentStatsPost.getDiseaseGroup() == DiseaseGroup.RECOVERED ? "1" : "0");
            currData.add(agentStatsPost.isForceInfected() ? "1" : "0");
            currData.add(String.valueOf(agentStatsPost.getWhenInfected()));

            currData.add(String.valueOf(agentStatsPost.getBrokenTiesActiveEpidemic()));
            currData.add(String.valueOf(agentStatsPost.getBrokenTiesPassiveEpidemic()));
            currData.add(String.valueOf(agentStatsPost.getAcceptedRequestsOutEpidemic()));
            currData.add(String.valueOf(agentStatsPost.getDeclinedRequestsOutEpidemic()));
            currData.add(String.valueOf(agentStatsPost.getAcceptedRequestsInEpidemic()));
            currData.add(String.valueOf(agentStatsPost.getDeclinedRequestsInEpidemic()));

            writeLine(currData);
        }

    }
}
