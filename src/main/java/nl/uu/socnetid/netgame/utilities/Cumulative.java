package nl.uu.socnetid.netgame.utilities;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public final class Cumulative extends UtilityFunction {

    // default values
    private static final double DEFAULT_DIRECT = 1.0;
    private static final double DEFAULT_INDIRECT = 0.5;

    /**
     * Constructor with default values.
     */
    public Cumulative() {
        this(DEFAULT_DIRECT, DEFAULT_INDIRECT);
    }

    /**
     * Constructor
     *
     * @param alpha
     *          the utility for direct connections
     * @param beta
     *          the utility for indirect connections (distance 2)
     */
    public Cumulative(double alpha, double beta) {
        super(alpha, beta, 0.0);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CUM";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalActorConnectionsStats lacs) {
        return this.getAlpha() * lacs.getN();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalActorConnectionsStats lacs) {
        return this.getBeta() * lacs.getM();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalActorConnectionsStats lacs, Actor actor) {
        // no costs
        return 0.0;
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getEffectOfDisease(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    protected double getEffectOfDisease(LocalActorConnectionsStats lacs, Actor actor) {
        // no effect
        return 0.0;
    }

}
