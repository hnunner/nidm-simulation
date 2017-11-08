package nl.uu.socnetid.network_games.networks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class BasicNetwork implements Network {

    private Map<Player, List<Player>> network = new HashMap<Player, List<Player>>();

    public BasicNetwork(List<Player> players) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player currPlayer = it.next();
            network.put(currPlayer, new ArrayList<Player>());
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#addConnection(nl.uu.socnetid.network_games.players.Player, nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public void addConnection(Player player1, Player player2) {
        if (player1.getId() == player2.getId()) {
            throw new RuntimeException("Unable to create reflexive connection.");
        }

        List<Player> connectionsPlayer1 = network.get(player1);
        if (!connectionsPlayer1.contains(player2)) {
            connectionsPlayer1.add(player2);
        }

        List<Player> connectionsPlayer2 = network.get(player2);
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
    public List<Player> getConnectionsOfPlayer(Player player) {
        return network.get(player);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.Network#getRandomConnectionOfPlayer(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public Player getRandomConnectionOfPlayer(Player player) {
        List<Player> connections = network.get(player);
        if (connections.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(connections.size());
        return connections.get(index);
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

}
