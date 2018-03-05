package nl.uu.socnetid.networkgames.networks;

/**
 * @author Hendrik Nunner
 */
public interface NetworkListener {

    /**
     * Entry point for actors being added notifications.
     *
     * @param actorId
     *          the id of the actor being added
     */
    void notifyActorAdded(final String actorId);

    /**
     * Entry point for actors being removed notifications.
     *
     * @param actorId
     *          the id of the actor being removed
     */
    void notifyActorRemoved(final String actorId);

}
