package nl.uu.socnetid.networkgames.networks;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public interface NetworkListener {

    /**
     * Entry point for actors being added notifications.
     *
     * @param actor
     *          the actor being added
     */
    void notifyActorAdded(final Actor actor);

    /**
     * Entry point for actors being removed notifications.
     *
     * @param actorId
     *          the id of the actor being removed
     */
    void notifyActorRemoved(final String actorId);

}
