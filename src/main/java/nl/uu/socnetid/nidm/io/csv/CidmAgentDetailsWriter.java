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
import nl.uu.socnetid.nidm.data.CidmParameters;
import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;
import nl.uu.socnetid.nidm.stats.AgentStats;
import nl.uu.socnetid.nidm.utility.Cidm;

/**
 * @author Hendrik Nunner
 */
public class CidmAgentDetailsWriter extends CsvFileWriter<CidmParameters> {

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
    public CidmAgentDetailsWriter(String fileName, DataGeneratorData<CidmParameters> dgData) throws IOException {
        super(fileName, dgData);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();

        // DEPENDENT VARIABLES
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_ROUND.toString());
        cols.add(LogValues.IV_AGENT_ID.toString());
        // Cidm
        cols.add(LogValues.IV_CIDM_ALPHA.toString());
        cols.add(LogValues.IV_CIDM_KAPPA.toString());
        cols.add(LogValues.IV_CIDM_BETA.toString());
        cols.add(LogValues.IV_CIDM_LAMDA.toString());
        cols.add(LogValues.IV_CIDM_C.toString());
        cols.add(LogValues.IV_CIDM_MU.toString());
        cols.add(LogValues.IV_CIDM_SIGMA.toString());
        cols.add(LogValues.IV_CIDM_GAMMA.toString());
        cols.add(LogValues.IV_CIDM_RS_EQUAL.toString());
        cols.add(LogValues.IV_CIDM_R_SIGMA.toString());
        cols.add(LogValues.IV_CIDM_R_PI.toString());
        cols.add(LogValues.IV_CIDM_NET_SIZE.toString());
        cols.add(LogValues.IV_CIDM_IOTA.toString());
        cols.add(LogValues.IV_CIDM_PHI_AV.toString());
        cols.add(LogValues.IV_CIDM_TAU_AV.toString());

        // INDEPENDENT VARIABLES
        // simulation
        cols.add(LogValues.DV_SIM_STAGE.toString());
        // network
        cols.add(LogValues.DV_NET_STABLE.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        // agent
        cols.add(LogValues.DV_AGENT_SATISFIED.toString());
        cols.add(LogValues.DV_AGENT_UTIL.toString());
        cols.add(LogValues.DV_AGENT_BENEFIT_SOCIAL.toString());
        cols.add(LogValues.DV_AGENT_COSTS_SOCIAL.toString());
        cols.add(LogValues.DV_AGENT_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_AGENT_DISEASE_STATE.toString());
        cols.add(LogValues.DV_AGENT_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_AGENT_DEGREE1.toString());
        cols.add(LogValues.DV_AGENT_DEGREE2.toString());
        cols.add(LogValues.DV_AGENT_CLOSENESS.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_AGENT_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_AGENT_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_AGENT_CONS_IN_DECLINED.toString());

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
            currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));
            currData.add(agent.getId());
            // Cidm
            Cidm uf = (Cidm) agent.getUtilityFunction();
            currData.add(String.valueOf(uf.getAlpha()));
            currData.add(String.valueOf(uf.getKappa()));
            currData.add(String.valueOf(uf.getBeta()));
            currData.add(String.valueOf(uf.getLamda()));
            currData.add(String.valueOf(uf.getC()));
            currData.add(String.valueOf(agent.getDiseaseSpecs().getMu()));
            currData.add(String.valueOf(agent.getDiseaseSpecs().getSigma()));
            currData.add(String.valueOf(agent.getDiseaseSpecs().getGamma()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
            currData.add(String.valueOf(agent.getRSigma()));
            currData.add(String.valueOf(agent.getRPi()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
            currData.add(String.valueOf(agent.getPhi()));
            currData.add(String.valueOf(agent.getDiseaseSpecs().getTau()));

            // PROPERTIES
            // simulation
            currData.add(String.valueOf(this.dgData.getSimStats().getSimStage()));
            // network
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().isStable()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
            currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
            // agent
            AgentStats agentStats = new AgentStats(agent);
            currData.add(String.valueOf(agentStats.isSatisfied()));
            currData.add(String.valueOf(agentStats.getUtility()));
            currData.add(String.valueOf(agentStats.getSocialBenefits()));
            currData.add(String.valueOf(agentStats.getSocialCosts()));
            currData.add(String.valueOf(agentStats.getDiseaseCosts()));
            currData.add(agentStats.getDiseaseGroup().name());
            currData.add(String.valueOf(agentStats.getTimeToRecover()));
            currData.add(String.valueOf(agentStats.getDegree1()));
            currData.add(String.valueOf(agentStats.getDegree2()));
            currData.add(String.valueOf(agentStats.getCloseness()));
            currData.add(String.valueOf(agentStats.getBrokenTiesActive()));
            currData.add(String.valueOf(agentStats.getBrokenTiesPassive()));
            currData.add(String.valueOf(agentStats.getAcceptedRequestsOut()));
            currData.add(String.valueOf(agentStats.getDeclinedRequestsOut()));
            currData.add(String.valueOf(agentStats.getAcceptedRequestsIn()));
            currData.add(String.valueOf(agentStats.getDeclinedRequestsIn()));

            writeLine(currData);
        }
    }

}
