package nl.uu.socnetid.networkgames.network.networks;

import java.util.ArrayList;
import java.util.List;

import nl.uu.socnetid.networkgames.actors.Actor;

/**
 * @author Hendrik Nunner
 */
public class SimpleNetwork extends AbstractNetwork implements Network {

    /**
     * Constructor.
     */
    public SimpleNetwork() {
        this(new ArrayList<Actor>());
    }

    /**
     * Constructor.
     *
     * @param actors
     *          list of actors in the network
     */
    public SimpleNetwork(List<Actor> actors) {
        super(actors);
    }

}
