package nl.uu.socnetid.network_games.networks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import nl.uu.socnetid.network_games.networks.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    /** map of players (player id, corresponding connections) */
    private Map<Player, Set<Player>> network = new HashMap<Player, Set<Player>>();
    /** writer used for network representation */
    protected NetworkWriter networkWriter;

    /**
     * Constructor.
     *
     * @param players
     *          list of players in the network
     * @param networkWriter
     *          writer for network representation
     */
    public AbstractNetwork(Set<Player> players, NetworkWriter networkWriter) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player currPlayer = it.next();
            network.put(currPlayer, new HashSet<Player>());
        }

        this.networkWriter = networkWriter;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#addConnection(nl.uu.socnetid.network_games.players.Player, nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void addConnection(Player player1, Player player2) {
        if (player1.getId() == player2.getId()) {
            throw new RuntimeException("Unable to create reflexive connection.");
        }

        Set<Player> connectionsPlayer1 = network.get(player1);
        if (!connectionsPlayer1.contains(player2)) {
            connectionsPlayer1.add(player2);
        }

        Set<Player> connectionsPlayer2 = network.get(player2);
        if (!connectionsPlayer2.contains(player1)) {
            connectionsPlayer2.add(player1);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#removeConnection(nl.uu.socnetid.network_games.players.Player, nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void removeConnection(Player player1, Player player2) {
        network.get(player1).remove(player2);
        network.get(player2).remove(player1);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getConnectionsForPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public Set<Player> getConnectionsOfPlayer(Player player) {
        return network.get(player);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getRandomConnectionOfPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public Player getRandomConnectionOfPlayer(Player player) {

        Player[] connections = network.get(player).toArray(new Player[network.get(player).size()]);

        if (connections.length == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.length);
        return connections[index];
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getRandomNotYetConnectedPlayerForPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public Player getRandomNotYetConnectedPlayerForPlayer(Player player) {

        ArrayList<Player> notYetConnectedForPlayer = new ArrayList<Player>();
        notYetConnectedForPlayer.addAll(network.keySet());
        notYetConnectedForPlayer.remove(player);
        notYetConnectedForPlayer.removeAll(network.get(player));

        if (notYetConnectedForPlayer.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(notYetConnectedForPlayer.size());
        return notYetConnectedForPlayer.get(index);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#write()
     */
    @Override
    public String write() {
        return this.networkWriter.write(this);
    }

    /**
     * @return the network
     */
    @Override
    public Set<Player> getPlayers() {
        return network.keySet();
    }

}
