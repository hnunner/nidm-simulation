package nl.uu.socnetid.networkgames.actors;

/**
 * @author Hendrik Nunner
 */
public interface ActionPerformedListener {

    /**
     * Entry point for action performed notifications.
     *
     * @param actor
     *          the actor who performed the action
     */
    void notifyActionPerformed(final Actor actor);

}
