package nl.uu.socnetid.network_games.players;

/**
 * @author Hendrik Nunner
 */
public interface DiseaseChangeListener {

    /**
     * Entry point for disease change notifications.
     *
     * @param player
     *          the player whose disease has changed
     */
    void notifyDiseaseChanged(final Player player);

}
