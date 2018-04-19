package nl.uu.socnetid.netgame.utilities;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnections extends UtilityFunction {

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit for connections, deteriorating over distance
     * @param c
     *          the c to maintain direct connections
     */
    public TruncatedConnections(double alpha, double c) {
        super(alpha, alpha * alpha, c);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "TC";
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
        int nSR = lacs.getnS() + lacs.getnR();
        int nI = lacs.getnI();
        return (nSR + (nI * actor.getDiseaseSpecs().getMu())) * this.getC();
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
