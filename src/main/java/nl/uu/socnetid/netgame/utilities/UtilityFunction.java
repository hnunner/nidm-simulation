package nl.uu.socnetid.netgame.utilities;

import java.util.Collection;

import nl.uu.socnetid.netgame.agents.Agent;
import nl.uu.socnetid.netgame.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.netgame.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public abstract class UtilityFunction {

    // utility of direct connections
    private final double alpha;
    // utility of indirect connections
    private final double beta;
    // costs to maintain direct connections
    private final double c;


    /**
     * Constructor.
     *
     * @param alpha
     *          the utility of direct connections
     * @param beta
     *          the utility of indirect connections
     * @param c
     *          the costs to maintain direct connections
     */
    protected UtilityFunction(double alpha, double beta, double c) {
        this.alpha = alpha;
        this.beta = beta;
        this.c = c;
    }


    /**
     * Computes the utility for a agent based on the social connections.
     *
     * @param agent
     *          the agent to compute the utility for
     * @param connections
     *          the agent's connections
     * @return the agent's utility based on the connections
     */
    public Utility getUtility(Agent agent, Collection<Agent> connections) {

        LocalAgentConnectionsStats lacs = StatsComputer.computeLocalAgentConnectionsStats(agent, connections);

        return new Utility(
                getBenefitOfDirectConnections(lacs),
                getBenefitOfIndirectConnections(lacs),
                getCostsOfDirectConnections(lacs, agent),
                getEffectOfDisease(lacs, agent)
                );
    }

    /**
     * @return the name of the utility function to be used in the stats window
     */
    public abstract String getStatsName();

    /**
     * Computes the benefit of direct connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @return the benefit of direct connections
     */
    protected abstract double getBenefitOfDirectConnections(LocalAgentConnectionsStats lacs);

    /**
     * Computes the benefit of indirect connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @return the benefit of indirect connections
     */
    protected abstract double getBenefitOfIndirectConnections(LocalAgentConnectionsStats lacs);

    /**
     * Computes the costs of direct connections.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the costs for
     * @return the costs of direct connections
     */
    protected abstract double getCostsOfDirectConnections(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * Computes the effect of a disease.
     *
     * @param lacs
     *          the agent's connection stats
     * @param agent
     *          the agent to compute the effect for
     * @return the benefit of a disease
     */
    protected abstract double getEffectOfDisease(LocalAgentConnectionsStats lacs, Agent agent);

    /**
     * @return the utility for direct connections
     */
    public double getAlpha() {
        return this.alpha;
    }

    /**
     * @return the utility for indirect connections
     */
    public double getBeta() {
        return this.beta;
    }

    /**
     * @return the costs to maintain direct connections
     */
    public double getC() {
        return this.c;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("type:").append(getStatsName());
        sb.append(" | alpha:").append(this.getAlpha());
        sb.append(" | beta:").append(this.getBeta());
        sb.append(" | c:").append(this.getC());

        return sb.toString();
    }

}
