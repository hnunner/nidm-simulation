package nl.uu.socnetid.network_games.network.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.GenericDisease;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // set of players
    private List<Player> players;
    // flag indicating whether all players are happy with their current connections
    boolean networkStable = false;


    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     */
    protected AbstractNetwork(List<Player> players) {
        this.players = players;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#addPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void addPlayer(Player player) {
        this.players.add(player);

        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#removePlayer()
     */
    @Override
    public void removePlayer() {
        if (this.players.size() == 0) {
            return;
        }
        Player player = this.players.get(this.players.size() - 1);
        player.destroy();
        this.players.remove(player);

        // update co-players
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().initCoPlayers(this.players);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#getPlayers()
     */
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#getConnectionsForPlayer(nl.uu.socnetid.network_games.
     * players.Player)
     */
    @Override
    public List<Player> getConnectionsOfPlayer(Player player) {
        return player.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            playersIt.next().removeAllConnections();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.Network#infectRandomPlayer()
     */
    @Override
    public void infectRandomPlayer() {
        if (this.players == null || this.players.isEmpty()) {
            return;
        }

        // players performing action in random order
        Collections.shuffle(this.players);

        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();

            if (player.isInfected()) {
                continue;
            }

            GenericDisease disease = new GenericDisease();
            player.infect(disease);
            return;
        }
    }

}
