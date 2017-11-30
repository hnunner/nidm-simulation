package nl.uu.socnetid.network_games.network.writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class EdgeListWriter implements NetworkWriter {

    private static final String SOURCE_COLUMN = "Source";
    private static final String TARGET_COLUMN = "Target";


    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.writer.NetworkWriter#
     *              write(nl.uu.socnetid.network_games.network.Network)
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        // first column
        sb.append(SOURCE_COLUMN).append(VALUE_SEPERATOR).append(TARGET_COLUMN);
        sb.append(System.getProperty("line.separator"));

        List<Player> players = new ArrayList<Player>(network.getPlayers());
        Collections.sort(players);

        // first row = all players
        Iterator<Player> playersIt = players.iterator();
        while (playersIt.hasNext()) {
            Player currPlayer = playersIt.next();

            List<Player> connections = network.getConnectionsOfPlayer(currPlayer);
            Iterator<Player> connectionsIt = players.iterator();
            while (connectionsIt.hasNext()) {
                Player currConnection = connectionsIt.next();

                if (connections.contains(currConnection)) {
                    sb.append(currPlayer.getId()).append(VALUE_SEPERATOR).append(currConnection.getId());
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }

        return sb.toString();
    }

}
