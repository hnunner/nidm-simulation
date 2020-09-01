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
package nl.uu.socnetid.nidm.data.out;

import java.util.List;
import java.util.Map;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.AgentStatsPost;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
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
    private NetworkStatsPre netStatsPre;
    private NetworkStatsPost netStatsPostStatic;
    private NetworkStatsPost netStatsPostDynamic;
    private NetworkStats netStatsCurrent;
    // agent stats
    private Map<String, AgentStatsPre> agentStatsPre;
    private Map<String, AgentStatsPost> agentStatsPostStatic;
    private Map<String, AgentStatsPost> agentStatsPostDynamic;


    // agents
    private List<Agent> agents;
    // index case
    private AgentStatsPre indexCaseStats;
    // export
    private String gexfExportFile = "/";


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
    public NetworkStatsPre getNetStatsPre() {
        return netStatsPre;
    }

    /**
     * @param netStatsPre the netStatsPre to set
     */
    public void setNetStatsPre(NetworkStatsPre netStatsPre) {
        this.netStatsPre = netStatsPre;
    }

    /**
     * @return the netStatsPostStatic
     */
    public NetworkStatsPost getNetStatsPostStatic() {
        return netStatsPostStatic;
    }


    /**
     * @param netStatsPostStatic the netStatsPostStatic to set
     */
    public void setNetStatsPostStatic(NetworkStatsPost netStatsPostStatic) {
        this.netStatsPostStatic = netStatsPostStatic;
    }


    /**
     * @return the netStatsPostDynamic
     */
    public NetworkStatsPost getNetStatsPostDynamic() {
        return netStatsPostDynamic;
    }


    /**
     * @param netStatsPostDynamic the netStatsPostDynamic to set
     */
    public void setNetStatsPostDynamic(NetworkStatsPost netStatsPostDynamic) {
        this.netStatsPostDynamic = netStatsPostDynamic;
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
    public AgentStatsPre getIndexCaseStats() {
        return indexCaseStats;
    }

    /**
     * @param indexCaseStats the indexCaseStats to set
     */
    public void setIndexCaseStats(AgentStatsPre indexCaseStats) {
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

    /**
     * @return the agentStatsPre
     */
    public Map<String, AgentStatsPre> getAgentStatsPre() {
        return agentStatsPre;
    }

    /**
     * @param agentStatsPre the agentStatsPre to set
     */
    public void setAgentStatsPre(Map<String, AgentStatsPre> agentStatsPre) {
        this.agentStatsPre = agentStatsPre;
    }

    /**
     * @return the agentStatsPostStatic
     */
    public Map<String, AgentStatsPost> getAgentStatsPostStatic() {
        return agentStatsPostStatic;
    }

    /**
     * @param agentStatsPostStatic the agentStatsPostStatic to set
     */
    public void setAgentStatsPostStatic(Map<String, AgentStatsPost> agentStatsPostStatic) {
        this.agentStatsPostStatic = agentStatsPostStatic;
    }

    /**
     * @return the agentStatsPostDynamic
     */
    public Map<String, AgentStatsPost> getAgentStatsPostDynamic() {
        return agentStatsPostDynamic;
    }

    /**
     * @param agentStatsPostDynamic the agentStatsPostDynamic to set
     */
    public void setAgentStatsPostDynamic(Map<String, AgentStatsPost> agentStatsPostDynamic) {
        this.agentStatsPostDynamic = agentStatsPostDynamic;
    }

}

