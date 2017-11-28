package nl.uu.socnetid.network_games.networks;

import java.util.ArrayList;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class SimpleNetwork extends AbstractNetwork implements Network {

    /**
     * Constructor.
     */
    public SimpleNetwork() {
        this(new ArrayList<Player>());
    }

    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     */
    public SimpleNetwork(List<Player> players) {
        super(players);
    }

}
