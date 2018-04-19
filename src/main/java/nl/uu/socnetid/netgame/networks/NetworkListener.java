package nl.uu.socnetid.netgame.networks;

import nl.uu.socnetid.netgame.actors.Actor;

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
