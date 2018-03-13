package nl.uu.socnetid.networkgames.utilities;

import java.util.Collection;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public abstract class UtilityFunction {

    /**
     * Computes the utility for a actor based on the social connections.
     *
     * @param actor
     *          the actor to compute the utility for
     * @param connections
     *          the actor's connections
     * @return the actor's utility based on the connections
     */
    public abstract Utility getUtility(Actor actor, Collection<Actor> connections);

    /**
     * @return the name of the utility function to be used in the stats window
     */
    public abstract String getStatsName();

    /**
     * @return the utility for direct connections
     */
    public abstract double getAlpha();

    /**
     * @return the utility for indirect connections
     */
    public abstract double getBeta();

    /**
     * @return the costs to maintain direct connections
     */
    public abstract double getC();


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
