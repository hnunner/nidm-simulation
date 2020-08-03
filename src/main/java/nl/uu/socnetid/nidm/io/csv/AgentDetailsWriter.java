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
import nl.uu.socnetid.nidm.data.out.UtilityModelParameters;
import nl.uu.socnetid.nidm.stats.AgentStats;

/**
 * @author Hendrik Nunner
 *
 * @param <UMP>
 *          the type of {@link UtilityModelParameters}
 */
public abstract class AgentDetailsWriter<UMP extends UtilityModelParameters> extends CsvFileWriter<UMP> {

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
    public AgentDetailsWriter(String fileName, DataGeneratorData<UMP> dgData) throws IOException {
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
     * @param agent
     *          the agent to write data for
     * @return the list with the added data
     */
    protected abstract List<String> addCurrModelData(List<String> currData, Agent agent);


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
        cols.add(LogValues.IV_SIM_ROUND.toString());
        cols.add(LogValues.IV_AGENT_ID.toString());

        // model specific columns
        cols = addModelColumns(cols);

        // DEPENDENT VARIABLES
        // simulation
        cols.add(LogValues.DV_SIM_STAGE.toString());
        // network
        cols.add(LogValues.DV_NET_STABLE.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_CONDITION.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY.toString());
        // agent
        cols.add(LogValues.DV_AGENT_SATISFIED.toString());
        cols.add(LogValues.DV_AGENT_UTIL.toString());
        cols.add(LogValues.DV_AGENT_BENEFIT_SOCIAL.toString());
        cols.add(LogValues.DV_AGENT_COSTS_SOCIAL.toString());
        cols.add(LogValues.DV_AGENT_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_AGENT_DISEASE_STATE.toString());
        cols.add(LogValues.DV_AGENT_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_AGENT_FORCE_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_DEGREE1.toString());
        cols.add(LogValues.DV_AGENT_DEGREE2.toString());
        cols.add(LogValues.DV_AGENT_CLOSENESS.toString());
        cols.add(LogValues.DV_AGENT_CLUSTERING.toString());
        cols.add(LogValues.DV_AGENT_BETWEENNESS.toString());
        cols.add(LogValues.DV_AGENT_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_DECLINED.toString());
        // neighborhood
        cols.add(LogValues.DV_AGENT_NEIGHBORHOOD_R_SIGMA_AV.toString());
        cols.add(LogValues.DV_AGENT_NEIGHBORHOOD_R_PI_AV.toString());
        // index case
        cols.add(LogValues.DV_AGENT_INDEX_NEIGHBORHOOD_R_SIGMA_AV.toString());
        cols.add(LogValues.DV_AGENT_INDEX_NEIGHBORHOOD_R_PI_AV.toString());

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

            // PARAMETERS
            // simulation
            currData.add(this.dgData.getSimStats().getUid());
            currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));
            currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));
            currData.add(agent.getId());

            // model specific data
            currData = addCurrModelData(currData, agent);

            // PROPERTIES
            // simulation
            currData.add(String.valueOf(this.dgData.getSimStats().getSimStage()));
            // network
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().isStable()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength()));
            currData.add(this.dgData.getNetStatsCurrent().getAssortativityCondition().toString());
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativity()));
            // agent
            AgentStats agentStats = new AgentStats(agent);
            currData.add(String.valueOf(agentStats.isSatisfied()));
            currData.add(String.valueOf(agentStats.getUtility()));
            currData.add(String.valueOf(agentStats.getSocialBenefits()));
            currData.add(String.valueOf(agentStats.getSocialCosts()));
            currData.add(String.valueOf(agentStats.getDiseaseCosts()));
            currData.add(agentStats.getDiseaseGroup().name());
            currData.add(String.valueOf(agentStats.getTimeToRecover()));
            currData.add(String.valueOf(agentStats.isForceInfected()));
            currData.add(String.valueOf(agentStats.getDegree1()));
            currData.add(String.valueOf(agentStats.getDegree2()));
            currData.add(String.valueOf(agentStats.getCloseness()));
            currData.add(String.valueOf(agentStats.getClustering()));
            currData.add(String.valueOf(agentStats.getBetweenness()));
            currData.add(String.valueOf(agentStats.getBetweennessNormalized()));
            currData.add(String.valueOf(agentStats.getBrokenTiesActive()));
            currData.add(String.valueOf(agentStats.getBrokenTiesPassive()));
            currData.add(String.valueOf(agentStats.getAcceptedRequestsOut()));
            currData.add(String.valueOf(agentStats.getDeclinedRequestsOut()));
            currData.add(String.valueOf(agentStats.getAcceptedRequestsIn()));
            currData.add(String.valueOf(agentStats.getDeclinedRequestsIn()));
            // neighborhood
            currData.add(String.valueOf(agentStats.getrSigmaNeighborhood()));
            currData.add(String.valueOf(agentStats.getrPiNeighborhood()));
            // index case
            currData.add(String.valueOf(this.dgData.getIndexCaseStats() != null
                    ? this.dgData.getIndexCaseStats().getrSigmaNeighborhood()
                    : "NA"));
            currData.add(String.valueOf(this.dgData.getIndexCaseStats() != null
                    ? this.dgData.getIndexCaseStats().getrPiNeighborhood()
                    : "NA"));

            writeLine(currData);
        }
    }

}
