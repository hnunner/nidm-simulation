package nl.uu.socnetid.networkgames.utilities;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;

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

        // amount of direct connections according to disease groups
        int nSR = 0;
        int nI = 0;
        // amount of indirect connections neglecting disease groups
        int m = 0;

        // for every direct connection
        Iterator<Actor> directIt = connections.iterator();
        while (directIt.hasNext()) {

            // no direct connection? do nothing
            Actor directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            // keeping track of disease groups of direct connections
            if (directConnection.isInfected()) {
                nI++;
            } else {
                nSR++;
            }

            // no indirect connections --> go to next direct connection
            List<Actor> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            // for every indirect connection at distance 2
            Iterator<Actor> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Actor indirectConnection = indirectIt.next();
                // no double benefit for indirect connections that is also a direct connection
                // however, currently double benefits for an indirect connection that is
                // connected to two different direct connections (i.e. in a ring of four actors,
                // a an actor gets the indirect benefit twice from both direct connections)
                if (indirectConnection.equals(actor)
                        ////////// TODO: ??? ALLOW DOUBLE BENEFITS FOR DIRECT + INDIRECT ??? //////////
                        || connections.contains(indirectConnection)) {
                    ////////// TODO: ??? FORBID DOUBLE BENEFITS FOR SAME INDIRECT OF TWO (OR MORE) DIRECT ??? //////////
                    continue;
                }
                m++;
            }
        }

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
                p = 1 - Math.pow((1 - actor.getDiseaseSpecs().getGamma()), nI);
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

        // logUtility(actor, nSR, nI, m, utility);

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
