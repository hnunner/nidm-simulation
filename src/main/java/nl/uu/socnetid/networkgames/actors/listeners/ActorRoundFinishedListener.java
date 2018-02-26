package nl.uu.socnetid.networkgames.actors.listeners;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public interface ActorRoundFinishedListener {

    /**
     * Entry point for rounds being finished notifications.
     *
     * @param actor
     *          the actor who finished the round
     */
    void notifyRoundFinished(final Actor actor);

}
