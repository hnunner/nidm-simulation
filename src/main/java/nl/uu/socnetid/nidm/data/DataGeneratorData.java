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
public abstract class DataGeneratorData<UMP extends UtilityModelParameters> {

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
     */
    public DataGeneratorData() {
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
    public abstract UMP getUtilityModelParams();

}
