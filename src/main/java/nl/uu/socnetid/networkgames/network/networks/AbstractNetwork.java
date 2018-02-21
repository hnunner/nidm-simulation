package nl.uu.socnetid.networkgames.network.networks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;

/**
 * @author Hendrik Nunner
 */
public abstract class AbstractNetwork implements Network {

    // logger
    private static final Logger logger = Logger.getLogger(AbstractNetwork.class);

    // set of actors
    private List<Actor> actors;


    /**
     * Constructor.
     *
     * @param actors
     *          list of actors in the network
     */
    protected AbstractNetwork(List<Actor> actors) {
        this.actors = actors;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#addActor(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void addActor(Actor actor) {
        this.actors.add(actor);

        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#removeActor()
     */
    @Override
    public void removeActor() {
        if (this.actors.size() == 0) {
            return;
        }
        Actor actor = this.actors.get(this.actors.size() - 1);

        // remove
        actor.destroy();
        this.actors.remove(actor);


        // update co-actors
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().initCoActors(this.actors);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#getActors()
     */
    @Override
    public List<Actor> getActors() {
        return this.actors;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#getActor(long)
     */
    @Override
    public Actor getActor(long id) {
        // dirty but okay for now
        Iterator<Actor> it = this.actors.iterator();
        while (it.hasNext()) {
            Actor actor = it.next();
            if (actor.getId() == id) {
                return actor;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#getConnectionsForActor(nl.uu.socnetid.networkgames.
     * actors.Actor)
     */
    @Override
    public List<Actor> getConnectionsOfActor(Actor actor) {
        return actor.getConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#clearConnections()
     */
    @Override
    public void clearConnections() {
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().removeAllConnections();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#resetActors()
     */
    @Override
    public void resetActors() {
        clearConnections();
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.makeSusceptible();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.Network#infectRandomActor(
     * nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void infectRandomActor(DiseaseSpecs diseaseSpecs) {
        if (this.actors == null || this.actors.isEmpty()) {
            return;
        }

        // actors performing action in random order
        Collections.shuffle(this.actors);

        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.isInfected()) {
                continue;
            }

            if (actor.isInfected()) {
                logger.warn("Unable to (force-) infect an actor that is already infected");
                return;
            }
            actor.forceInfect(diseaseSpecs);

            return;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.networks.Network#toggleInfection(long,
     * nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void toggleInfection(long actorId, DiseaseSpecs diseaseSpecs) {
        Iterator<Actor> actorsIt = this.actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.getId() == actorId) {

                switch (actor.getDiseaseGroup()) {
                    case SUSCEPTIBLE:
                        actor.forceInfect(diseaseSpecs);
                        break;

                    case INFECTED:
                        actor.cure();
                        break;

                    case RECOVERED:
                        actor.makeSusceptible();
                        break;

                    default:
                        break;
                }
            }
        }
    }

}
