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
public class EdgeListWriter implements NetworkWriter {

    private static final String SOURCE_COLUMN = "Source";
    private static final String TARGET_COLUMN = "Target";


    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.networks.writer.NetworkWriter#
     *              write(nl.uu.socnetid.networkgames.networks.Network)
     */
    @Override
    public String write(Network network) {

        StringBuilder sb = new StringBuilder();

        // first column
        sb.append(SOURCE_COLUMN).append(VALUE_SEPERATOR).append(TARGET_COLUMN);
        sb.append(System.getProperty("line.separator"));

        List<Actor> actors = new ArrayList<Actor>(network.getActors());
        Collections.sort(actors);

        // first row = all actors
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor currActor = actorsIt.next();

            Collection<Actor> connections = currActor.getConnections();
            Iterator<Actor> connectionsIt = actors.iterator();
            while (connectionsIt.hasNext()) {
                Actor currConnection = connectionsIt.next();

                if (connections.contains(currConnection)) {
                    sb.append(currActor.getId()).append(VALUE_SEPERATOR).append(currConnection.getId());
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }

        return sb.toString();
    }

}
