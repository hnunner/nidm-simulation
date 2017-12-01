package nl.uu.socnetid.network_games.gui;

/**
 * @author Hendrik Nunner
 */
public interface NodeClickListener {

    /**
     * Entry point for notification of node clicks.
     *
     * @param nodeClick
     *          the node click event
     */
    void notify(final NodeClick nodeClick);

}
