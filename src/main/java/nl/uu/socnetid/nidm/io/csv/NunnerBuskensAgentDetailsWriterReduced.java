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
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensAgentDetailsWriterReduced extends CsvFileWriter<NunnerBuskensParameters> {

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
    public NunnerBuskensAgentDetailsWriterReduced(String fileName, DataGeneratorData<NunnerBuskensParameters> dgData) throws IOException {
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
        cols.add(LogValues.IV_NB_R_SIGMA.toString());
        cols.add(LogValues.IV_NB_R_SIGMA_AV.toString());
        cols.add(LogValues.IV_NB_R_SIGMA_NEIGHBORHOOD.toString());

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
        // agent
        cols.add(LogValues.DV_AGENT_DEGREE1.toString());
        cols.add(LogValues.DV_AGENT_CLUSTERING.toString());
        cols.add(LogValues.DV_AGENT_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_AGENT_ASSORTATIVITY_RISK_PERCEPTION.toString());
        cols.add(LogValues.DV_AGENT_ASSORTATIVITY_AGE.toString());
        cols.add(LogValues.DV_AGENT_ASSORTATIVITY_PROFESSION.toString());
        cols.add(LogValues.DV_AGENT_CLOSENESS.toString());
        cols.add(LogValues.DV_AGENT_INDEX_DISTANCE.toString());

        // POST-EPIDEMIC
        // agent
        cols.add(LogValues.DV_AGENT_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_FORCE_INFECTED.toString());
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
            // network (static / dynamic)
            currData.add(this.dgData.getUtilityModelParams().getCurrEpStructure().toString());
            // varied model parameters
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrAlpha()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrOmega()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrSelective()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrSigma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrGamma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMin()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMax()));
            currData.add(String.valueOf(agent.getRSigma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRSigmaAv()));
            currData.add(String.valueOf(agent.getRSigmaNeighborhood()));

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
            // agent
            AgentStatsPre agentStatsPre = this.dgData.getAgentStatsPre().get(agent.getId());
            currData.add(String.valueOf(agentStatsPre.getDegree1()));
            currData.add(String.valueOf(agentStatsPre.getClustering()));
            currData.add(String.valueOf(agentStatsPre.getBetweennessNormalized()));
            currData.add(String.valueOf(agentStatsPre.getAssortativityRiskPerception()));
            currData.add(String.valueOf(agentStatsPre.getAssortativityAge()));
            currData.add(String.valueOf(agentStatsPre.getAssortativityProfession()));
            currData.add(String.valueOf(agentStatsPre.getCloseness()));
            currData.add(String.valueOf(agentStatsPre.getIndexCaseDistance()));

            // POST-EPIDEMIC
            // agent
            AgentStatsPost agentStatsPost = null;
            switch (this.dgData.getUtilityModelParams().getCurrEpStructure()) {
                case DYNAMIC:
                    agentStatsPost = this.dgData.getAgentStatsPostDynamic().get(agent.getId());
                    break;

                case STATIC:
                    agentStatsPost = this.dgData.getAgentStatsPostStatic().get(agent.getId());
                    break;

                case BOTH:
                default:
                    throw new RuntimeException("Invalid epidemic structure: "
                + this.dgData.getUtilityModelParams().getEpStructure());
            }
            currData.add(agentStatsPost.getDiseaseGroup() == DiseaseGroup.INFECTED
                    || agentStatsPost.getDiseaseGroup() == DiseaseGroup.RECOVERED ? "1" : "0");
            currData.add(agentStatsPost.isForceInfected() ? "1" : "0");

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
