package nl.uu.socnetid.network_games.networks;

import java.util.Set;

import nl.uu.socnetid.network_games.networks.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class BasicNetwork extends AbstractNetwork implements Network {

    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     * @param networkWriter
     *          writer for network representation
     */
    public BasicNetwork(Set<Player> players, NetworkWriter networkWriter) {
        super(players, networkWriter);
    }

}
