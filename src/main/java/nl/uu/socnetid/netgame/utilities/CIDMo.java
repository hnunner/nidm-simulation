package nl.uu.socnetid.netgame.utilities;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats;
import nl.uu.socnetid.netgame.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class CIDMo extends UtilityFunction {

    // discount for infected direct connections
    private final double kappa;
    // discount for infected indirect connections
    private final double lamda;

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param kappa
     *          the discount for infected direct connections
     * @param beta
     *          the benefit of an indirect connection
     * @param lamda
     *          the discount for infected indirect connections
     * @param c
     *          the maintenance costs for a direct connection
     */
    public CIDMo(double alpha, double kappa, double beta, double lamda, double c) {
        super(alpha, beta, c);
        this.kappa = kappa;
        this.lamda = lamda;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CIDMo";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalActorConnectionsStats lacs) {
        return this.getAlpha() * (lacs.getnS() + this.kappa * lacs.getnI() + lacs.getnR());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalActorConnectionsStats lacs) {
        return this.getBeta() * (lacs.getmS() + this.lamda * lacs.getmI() + lacs.getmR());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.utilities.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.netgame.stats.LocalActorConnectionsStats, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalActorConnectionsStats lacs, Actor actor) {
        return this.getC() * (lacs.getnS() + actor.getDiseaseSpecs().getMu() * lacs.getnI() + lacs.getnR());
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
