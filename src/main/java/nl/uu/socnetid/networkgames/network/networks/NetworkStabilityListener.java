package nl.uu.socnetid.networkgames.network.networks;

/**
 * @author Hendrik Nunner
 */
public interface NetworkStabilityListener {

    /**
     * Entry point for network stability notifications.
     *
     * @param network
     *          the network
     */
    void notify(final Network network);

}
