package nl.uu.socnetid.networkgames.utilities;

import java.util.Collection;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public interface UtilityFunction {

    /**
     * Computes the utility for a actor based on the social connections.
     *
     * @param actor
     *          the actor to compute the utility for
     * @param connections
     *          the actor's connections
     * @return the actor's utility based on the connections
     */
    Utility getUtility(Actor actor, Collection<Actor> connections);

    /**
     * @return the name of the utility function to be used in the stats window
     */
    String getStatsName();

    /**
     * @return the utility for direct connections
     */
    double getAlpha();

    /**
     * @return the utility for indirect connections
     */
    double getBeta();

    /**
     * @return the costs to maintain direct connections
     */
    double getC();

}
