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
package nl.uu.socnetid.nidm.data;

import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.AgentStats;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.SimulationStats;

/**
 * @author Hendrik Nunner
 *
 * @param <UMP>
 *          the type of utility model parameters
 */
public class DataGeneratorData<UMP extends UtilityModelParameters> {

    // utility model parameters
    private final UMP utilityModelParams;
    // simulation
    private SimulationStats simStats;
    // network stats
    private NetworkStats netStatsPre;
    private NetworkStats netStatsPost;
    private NetworkStats netStatsCurrent;
    // agents
    private List<Agent> agents;
    // index case
    private AgentStats indexCaseStats;
    // export
    private String gexfExportFile;


    /**
     * Constructor.
     *
     * @param utilityModelParams
     *          the utility model parameters
     */
    public DataGeneratorData(UMP utilityModelParams) {
        this.utilityModelParams = utilityModelParams;
        this.simStats = new SimulationStats();
    }


    /**
     * @return the netStatsPre
     */
    public NetworkStats getNetStatsPre() {
        return netStatsPre;
    }

    /**
     * @param netStatsPre the netStatsPre to set
     */
    public void setNetStatsPre(NetworkStats netStatsPre) {
        this.netStatsPre = netStatsPre;
    }

    /**
     * @return the netStatsPost
     */
    public NetworkStats getNetStatsPost() {
        return netStatsPost;
    }

    /**
     * @param netStatsPost the netStatsPost to set
     */
    public void setNetStatsPost(NetworkStats netStatsPost) {
        this.netStatsPost = netStatsPost;
    }

    /**
     * @return the netStatsCurrent
     */
    public NetworkStats getNetStatsCurrent() {
        return netStatsCurrent;
    }

    /**
     * @param netStatsCurrent the netStatsCurrent to set
     */
    public void setNetStatsCurrent(NetworkStats netStatsCurrent) {
        this.netStatsCurrent = netStatsCurrent;
    }

    /**
     * @return the agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * @param agents the agents to set
     */
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    /**
     * @return the indexCaseStats
     */
    public AgentStats getIndexCaseStats() {
        return indexCaseStats;
    }

    /**
     * @param indexCaseStats the indexCaseStats to set
     */
    public void setIndexCaseStats(AgentStats indexCaseStats) {
        this.indexCaseStats = indexCaseStats;
    }

    /**
     * @return the gexfExportFile
     */
    public String getGexfExportFile() {
        return gexfExportFile;
    }

    /**
     * @param gexfExportFile the gexfExportFile to set
     */
    public void setGexfExportFile(String gexfExportFile) {
        this.gexfExportFile = gexfExportFile;
    }

    /**
     * @return the simStats
     */
    public SimulationStats getSimStats() {
        return simStats;
    }

    /**
     * @param simStats the simStats to set
     */
    public void setSimStats(SimulationStats simStats) {
        this.simStats = simStats;
    }

    /**
     * @return the utilityModelParams
     */
    public UMP getUtilityModelParams() {
        return utilityModelParams;
    }

}

