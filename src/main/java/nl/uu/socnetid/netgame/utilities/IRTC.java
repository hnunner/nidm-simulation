package nl.uu.socnetid.netgame.utilities;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats;
import nl.uu.socnetid.netgame.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class IRTC extends UtilityFunction {

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param beta
     *          the benefit of an indirect connection
     * @param c
     *          the maintenance costs for a direct connection
     */
    public IRTC(double alpha, double beta, double c) {
        super(alpha, beta, c);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "IRTC";
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
        int nI = lacs.getnI();
        double p;
        double s;
        double rSigma = actor.getRSigma();
        double rPi = actor.getRPi();

        // depending own actor's own risk group
        switch (actor.getDiseaseGroup()) {
            case SUSCEPTIBLE:
                p = Math.pow(StatsComputer.computeProbabilityOfInfection(actor, nI), (2 - rPi));
                s = Math.pow(actor.getDiseaseSpecs().getS(), rSigma) ;
                break;

            case INFECTED:
                p = 1;
                s = actor.getDiseaseSpecs().getS();
                break;

            case RECOVERED:
                p = 0;
                s = 0;
                break;

            default:
                throw new RuntimeException("Unknown disease group: " + actor.getDiseaseGroup());
        }

        return p * s;
    }

}
