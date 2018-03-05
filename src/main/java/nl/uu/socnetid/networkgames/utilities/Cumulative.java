package nl.uu.socnetid.networkgames.utilities;

import java.util.Collection;
import java.util.Iterator;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public final class Cumulative extends UtilityFunction {

    // default values
    private static final double DEFAULT_DIRECT = 1.0;
    private static final double DEFAULT_INDIRECT = 0.5;

    // how much is a connection worth
    private final double utilityDirectConnections;
    private final double utilityIndirectConnections;

    /**
     * Constructor with default values.
     */
    public Cumulative() {
        this(DEFAULT_DIRECT, DEFAULT_INDIRECT);
    }

    /**
     * Constructor
     *
     * @param utilityDirectConnections
     *          the utility for direct connections
     * @param utilityIndirectConnections
     *          the utility for indirect connections (distance 2)
     */
    public Cumulative(double utilityDirectConnections, double utilityIndirectConnections) {
        this.utilityDirectConnections = utilityDirectConnections;
        this.utilityIndirectConnections = utilityIndirectConnections;
    }


    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getUtility(
     * nl.uu.socnetid.networkgames.actors.Actor, java.util.Collection)
     */
    @Override
    public Utility getUtility(Actor actor, Collection<Actor> connections) {

        // BEWARE: disease is being neglected in this function

        double benefitDirectConnections = 0;
        double benefitIndirectConnections = 0;

        Iterator<Actor> directIt = connections.iterator();

        while (directIt.hasNext()) {
            Actor directConnection = directIt.next();
            if (directConnection == null) {
                continue;
            }

            benefitDirectConnections += this.utilityDirectConnections;

            // indirect connections at distance 2
            Collection<Actor> indirectConnections = directConnection.getConnections();
            if (indirectConnections == null) {
                continue;
            }

            Iterator<Actor> indirectIt = indirectConnections.iterator();
            while (indirectIt.hasNext()) {
                Actor indirectConnection = indirectIt.next();

                if (indirectConnection.equals(actor)
                        || connections.contains(indirectConnection)) {
                    continue;
                }
                benefitIndirectConnections += this.utilityIndirectConnections;
            }
        }
        return new Utility(benefitDirectConnections, benefitIndirectConnections, 0, 0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CUM";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getAlpha()
     */
    @Override
    public double getAlpha() {
        return this.utilityDirectConnections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getBeta()
     */
    @Override
    public double getBeta() {
        return this.utilityIndirectConnections;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.utilities.UtilityFunction#getC()
     */
    @Override
    public double getC() {
        return 0;
    }

}
