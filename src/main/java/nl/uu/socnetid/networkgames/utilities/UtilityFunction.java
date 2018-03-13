package nl.uu.socnetid.networkgames.utilities;

import java.util.Collection;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.stats.LocalActorConnectionsStats;
import nl.uu.socnetid.networkgames.stats.StatsComputer;

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
     * Computes the utility for a actor based on the social connections.
     *
     * @param actor
     *          the actor to compute the utility for
     * @param connections
     *          the actor's connections
     * @return the actor's utility based on the connections
     */
    public Utility getUtility(Actor actor, Collection<Actor> connections) {

        LocalActorConnectionsStats lacs = StatsComputer.computeLocalActorConnectionsStats(actor, connections);

        return new Utility(
                getBenefitOfDirectConnections(lacs),
                getBenefitOfIndirectConnections(lacs),
                getCostsOfDirectConnections(lacs, actor),
                getEffectOfDisease(lacs, actor)
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
     *          the actor's connection stats
     * @return the benefit of direct connections
     */
    protected abstract double getBenefitOfDirectConnections(LocalActorConnectionsStats lacs);

    /**
     * Computes the benefit of indirect connections.
     *
     * @param lacs
     *          the actor's connection stats
     * @return the benefit of indirect connections
     */
    protected abstract double getBenefitOfIndirectConnections(LocalActorConnectionsStats lacs);

    /**
     * Computes the costs of direct connections.
     *
     * @param lacs
     *          the actor's connection stats
     * @param actor
     *          the actor to compute the costs for
     * @return the costs of direct connections
     */
    protected abstract double getCostsOfDirectConnections(LocalActorConnectionsStats lacs, Actor actor);

    /**
     * Computes the effect of a disease.
     *
     * @param lacs
     *          the actor's connection stats
     * @param actor
     *          the actor to compute the effect for
     * @return the benefit of a disease
     */
    protected abstract double getEffectOfDisease(LocalActorConnectionsStats lacs, Actor actor);

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
