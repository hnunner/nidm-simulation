package nl.uu.socnetid.networkgames.network.writer;

import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 *
 * @author Hendrik Nunner
 */
public interface NetworkWriter {

    static final String PLAYER_PREFIX = "P";
    static final String VALUE_SEPERATOR = ",";
    static final String CONNECTION = "1";
    static final String NO_CONNECTION = "0";

    /**
     * Creates a string representation of the network.
     *
     * @param network
     *          the network to write
     * @return a string representation of the network
     */
    String write(Network network);

}
