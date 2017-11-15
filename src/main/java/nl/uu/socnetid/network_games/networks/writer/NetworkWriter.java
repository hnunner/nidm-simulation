package nl.uu.socnetid.network_games.networks.writer;

import nl.uu.socnetid.network_games.networks.Network;

/**
 *
 * @author Hendrik Nunner
 */
public interface NetworkWriter {

    /**
     * Creates a string representation of the network.
     *
     * @param network
     *          the network to write
     * @return a string representation of the network
     */
    String write(Network network);

}
