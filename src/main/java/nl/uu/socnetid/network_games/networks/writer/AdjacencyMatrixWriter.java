package nl.uu.socnetid.network_games.networks.writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriter implements NetworkWriter {

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.networks.NetworkWriter#write()
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        List<Player> players = new ArrayList<Player>(network.getPlayers());
        Collections.sort(players);

        // first row = all players
        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();
            sb.append(",").append(PLAYER_PREFIX).append(currPlayer.getId());
        }
        sb.append(System.getProperty("line.separator"));

        // succeding rows = all connections of corresponding player
        playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();
            sb.append(PLAYER_PREFIX).append(currPlayer.getId());

            List<Player> connections = network.getConnectionsOfPlayer(currPlayer);

            Iterator<Player> connectionsIt = players.iterator();
            while (connectionsIt.hasNext()) {
                Player currConnection = connectionsIt.next();
                if (connections.contains(currConnection)) {
                    sb.append(VALUE_SEPERATOR).append(CONNECTION);
                } else {
                    sb.append(VALUE_SEPERATOR).append(NO_CONNECTION);
                }
            }
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

}
