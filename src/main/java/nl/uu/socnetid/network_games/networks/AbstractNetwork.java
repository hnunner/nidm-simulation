package nl.uu.socnetid.network_games.networks;

import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // set of players
    private List<Player> players;

    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     */
    public AbstractNetwork(List<Player> players) {
        this.players = players;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getPlayers()
     */
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getConnectionsForPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public List<Player> getConnectionsOfPlayer(Player player) {
        return player.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            player.removeAllConnections();
        }
    }

}
