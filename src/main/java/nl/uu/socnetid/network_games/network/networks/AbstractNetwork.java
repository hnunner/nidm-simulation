package nl.uu.socnetid.network_games.network.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
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
    private boolean stable = false;

    // listener
    private final Set<NetworkStabilityListener> listeners = new CopyOnWriteArraySet<NetworkStabilityListener>();


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
     * @see nl.uu.socnetid.network_games.network.networks.Network#getPlayer(long)
     */
    @Override
    public Player getPlayer(long id) {
        // dirty but okay for now
        Iterator<Player> it = this.players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getId() == id) {
                return player;
            }
        }

        logger.warn("No player found for ID " + id);
        return null;
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
     * @see nl.uu.socnetid.network_games.network.Network#infectRandomPlayer(
     * nl.uu.socnetid.network_games.disease.DiseaseSpecs)
     */
    @Override
    public void infectRandomPlayer(DiseaseSpecs diseaseSpecs) {
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
            player.forceInfect(diseaseSpecs);
            return;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#toggleInfection(long,
     * nl.uu.socnetid.network_games.disease.DiseaseSpecs)
     */
    @Override
    public void toggleInfection(long playerId, DiseaseSpecs diseaseSpecs) {
        Iterator<Player> playersIt = this.players.iterator();
        while (playersIt.hasNext()) {
            Player player = playersIt.next();
            if (player.getId() == playerId) {
                if (player.isInfected()) {
                    player.cure();
                } else {
                    player.forceInfect(diseaseSpecs);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#setStable(boolean)
     */
    @Override
    public void setStable(boolean stable) {
        if (this.stable != stable) {
            this.stable = stable;
            notifyListeners();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#isStable()
     */
    @Override
    public boolean isStable() {
        return this.stable;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#addListener(
     * nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener)
     */
    @Override
    public void addListener(NetworkStabilityListener listener) {
        this.listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.Network#removeListener(
     * nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener)
     */
    @Override
    public void removeListener(NetworkStabilityListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners of task completion.
     */
    private final void notifyListeners() {
        Iterator<NetworkStabilityListener> listenersIt = this.listeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notify(this);
        }
    }

}
