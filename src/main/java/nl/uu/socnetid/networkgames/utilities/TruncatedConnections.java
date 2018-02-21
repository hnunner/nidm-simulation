package nl.uu.socnetid.networkgames.utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnections implements UtilityFunction {

    // how much is a connection worth
    private final double directUtility;
    private final double indirectUtility;
    private final double costs;

    /**
     * Constructor.
     *
     * @param delta
     *          the benefit for connections, deteriorating over distance
     * @param costs
     *          the costs to maintain direct connections
     */
    public TruncatedConnections(double delta, double costs) {
        this.directUtility = delta;
        this.indirectUtility = delta * delta;
        this.costs = costs;
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.networkgames.actors.Actor, java.util.List)
     */
    @Override
    public Utility getUtility(Actor actor, List<Actor> connections) {
        double benefitDirectConnections = 0.0;
        double benefitIndirectConnections = 0.0;
        double costsDirectConnections = 0.0;
        double effectOfDisease = 0.0;

        // indirect connections giving benefit only once
        List<Actor> bookedIndirectBenefits = new LinkedList<Actor>();

        // for every direct connection
        Iterator<Actor> directIt = connections.iterator();
        while (directIt.hasNext()) {
            Actor directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            benefitDirectConnections += this.directUtility;
            if (directConnection.isInfected()) {
                costsDirectConnections += this.costs * actor.getDiseaseSpecs().getMu();
            } else {
                costsDirectConnections += this.costs;
            }

            // indirect connections at distance 2
            List<Actor> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            Iterator<Actor> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Actor indirectConnection = indirectIt.next();

                if (indirectConnection.equals(actor)
                        || connections.contains(indirectConnection)
                        || bookedIndirectBenefits.contains(indirectConnection)) {
                    continue;
                }
                benefitIndirectConnections += this.indirectUtility;
                bookedIndirectBenefits.add(indirectConnection);
            }
        }

        return new Utility(benefitDirectConnections,
                benefitIndirectConnections,
                costsDirectConnections,
                effectOfDisease);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "TC";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getAlpha()
     */
    @Override
    public double getAlpha() {
        return this.directUtility;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getBeta()
     */
    @Override
    public double getBeta() {
        return this.indirectUtility;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getC()
     */
    @Override
    public double getC() {
        return this.costs;
    }

}
