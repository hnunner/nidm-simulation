package nl.uu.socnetid.networkgames.io.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class AdjacencyMatrixWriter implements NetworkWriter {

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.networks.NetworkWriter#write()
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        List<Actor> actors = new ArrayList<Actor>(network.getActors());
        Collections.sort(actors);

        // first row = all actors
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor currActor = actorsIt.next();
            sb.append(",").append(PLAYER_PREFIX).append(currActor.getId());
        }
        sb.append(System.getProperty("line.separator"));

        // succeding rows = all connections of corresponding actor
        actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor currActor = actorsIt.next();
            sb.append(PLAYER_PREFIX).append(currActor.getId());

            Collection<Actor> connections = currActor.getConnections();

            Iterator<Actor> connectionsIt = actors.iterator();
            while (connectionsIt.hasNext()) {
                Actor currConnection = connectionsIt.next();
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
