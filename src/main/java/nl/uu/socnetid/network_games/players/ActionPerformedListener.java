package nl.uu.socnetid.network_games.players;

/**
 * @author Hendrik Nunner
 */
public interface ActionPerformedListener {

    /**
     * Entry point for action performed notifications.
     *
     * @param player
     *          the player who performed the action
     */
    void notifyActionPerformed(final Player player);

}
