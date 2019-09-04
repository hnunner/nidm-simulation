package nl.uu.socnetid.nidm.io.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.CidmDataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;
import nl.uu.socnetid.nidm.stats.AgentStats;

/**
 * @author Hendrik Nunner
 */
public class CidmAgentDetailsWriter extends CidmCsvFileWriter {

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
    public CidmAgentDetailsWriter(String fileName, CidmDataGeneratorData dgData) throws IOException {
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
        cols.add(LogValues.DV_AGENT_BENEFIT_DIST1.toString());
        cols.add(LogValues.DV_AGENT_BENEFIT_DIST2.toString());
        cols.add(LogValues.DV_AGENT_COSTS_DIST1.toString());
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
            currData.add(String.valueOf(agent.getUtilityFunction().getAlpha()));
            currData.add(String.valueOf(agent.getUtilityFunction().getKappa()));
            currData.add(String.valueOf(agent.getUtilityFunction().getBeta()));
            currData.add(String.valueOf(agent.getUtilityFunction().getLamda()));
            currData.add(String.valueOf(agent.getUtilityFunction().getC()));
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
            currData.add(String.valueOf(agentStats.getBenefit1()));
            currData.add(String.valueOf(agentStats.getBenefit2()));
            currData.add(String.valueOf(agentStats.getCosts1()));
            currData.add(String.valueOf(agentStats.getCostsDisease()));
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
