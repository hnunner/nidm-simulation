package nl.uu.socnetid.networkgames.utilities;

import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.stats.LocalActorConnectionsStats;
import nl.uu.socnetid.networkgames.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class IRTC implements UtilityFunction {

    // logger
    private static final Logger logger = Logger.getLogger(IRTC.class);

    private final double alpha;
    private final double beta;
    private final double c;

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
        this.alpha = alpha;
        this.beta = beta;
        this.c= c;
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.networkgames.actors.Actor, java.util.List)
     */
    @Override
    public Utility getUtility(Actor actor, List<Actor> connections) {

        // amount of connections according to distance and disease group
        LocalActorConnectionsStats lacs = StatsComputer.computeLocalActorConnectionsStats(actor, connections);
        int nSR = lacs.getnS() + lacs.getnR();
        int nI = lacs.getnI();
        int m = lacs.getM();

        // benefit of direct connections
        double benefitDirectConnections = this.alpha * (nSR + nI);

        // benefit of indirect connections
        double benefitIndirectConnections = this.beta * m;

        // costs to maintain direct connection
        double costsDirectConnections = (nSR + (nI * actor.getDiseaseSpecs().getMu())) * this.c;

        // effect of disease
        double p;
        double r;
        // depending own actor's own risk group
        switch (actor.getDiseaseGroup()) {
            case SUSCEPTIBLE:
                p = StatsComputer.computeProbabilityOfInfection(actor, nI);
                r = actor.getRiskFactor();
                break;

            case INFECTED:
                p = 1;
                r = 1;
                break;

            case RECOVERED:
                p = 0;
                r = 1;
                break;

            default:
                throw new RuntimeException("Unknown disease group: " + actor.getDiseaseGroup());
        }
        double effectOfDisease = p * Math.pow(actor.getDiseaseSpecs().getDelta(), r);
        // end: effect of disease

        return new Utility(benefitDirectConnections,
                benefitIndirectConnections,
                costsDirectConnections,
                effectOfDisease);
    }

    /**
     * Logs information
     *
     * @param actor
     * @param connections
     * @param utility
     */
    @SuppressWarnings("unused")
    private void logUtility(Actor actor, int nSR, int nI, int m, double utility) {
        StringBuilder sb = new StringBuilder();

        sb.append("Utility for actor ").append(actor.getId()).append("\n\t").
                append("(nSR  = ").append(nSR).append(",\n\t").
                append("(nI   = ").append(nI).append(",\n\t").
                append("(m    = ").append(m).append(",\n\t").
                append("alpha = ").append(this.alpha).append(",\n\t").
                append("beta  = ").append(this.beta).append(",\n\t").
                append("c     = ").append(this.c).append(",\n\t").
                append("delta = ").append(actor.getDiseaseSpecs().getDelta()).append(",\n\t").
                append("gamma = ").append(actor.getDiseaseSpecs().getGamma()).append(",\n\t").
                append("mu    = ").append(actor.getDiseaseSpecs().getMu()).append(",\n\t").
                append("r     = ").append(actor.getRiskFactor()).append(",\n").
                append("): ").append(utility);

        logger.debug(sb.toString());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "IRTC";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getAlpha()
     */
    @Override
    public double getAlpha() {
        return this.alpha;
    }

    /*(non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getBeta()
     */
    @Override
    public double getBeta() {
        return this.beta;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getC()
     */
    @Override
    public double getC() {
        return this.c;
    }

}
