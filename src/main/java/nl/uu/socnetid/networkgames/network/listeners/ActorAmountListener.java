package nl.uu.socnetid.networkgames.network.listeners;

/**
 * @author Hendrik Nunner
 */
public interface ActorAmountListener {

    /**
     * Entry point for actors being added notifications.
     *
     * @param actorId
     *          the id of the actor being added
     */
    void notifyActorAdded(final long actorId);

    /**
     * Entry point for actors being removed notifications.
     *
     * @param actorId
     *          the id of the actor being removed
     */
    void notifyActorRemoved(final long actorId);

}
