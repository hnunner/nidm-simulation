package nl.uu.socnetid.nidm.io.network;

import nl.uu.socnetid.nidm.networks.Network;

/**
 *
 * @author Hendrik Nunner
 */
public interface NetworkWriter {

    static final String AGENT_PREFIX = "A";
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
