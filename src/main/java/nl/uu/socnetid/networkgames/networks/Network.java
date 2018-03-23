package nl.uu.socnetid.networkgames.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorFactory;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class Network extends SingleGraph {

    // logger
    private static final Logger logger = Logger.getLogger(Network.class);

    // risk factor for risk neutral actors
    private static final double RISK_FACTOR_NEUTRAL = 1.0;

    // listener
    private final Set<NetworkListener> networkListeners =
            new CopyOnWriteArraySet<NetworkListener>();


    /**
     * Constructor.
     */
    public Network() {
        this("Network of the Infectious Kind");
    }

    /**
     * Constructor.
     *
     * @param id
     *          the network's unique identifier
     */
    public Network(String id) {
        super(id);
        this.setNodeFactory(new ActorFactory());
    }


    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return this.addActor(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL);
    }

    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param riskFactor
     *          the factor describing how the actor perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        Actor actor = this.addNode(String.valueOf(this.getNodeCount() + 1));
        actor.initActor(utilityFunction, diseaseSpecs, riskFactor);
        notifyActorAdded(actor);
        return actor;
    }


    /**
     * Removes a actor from the network.
     *
     * @return the id of the removed actor
     */
    public String removeActor() {
        if (this.getActors().size() == 0) {
            return null;
        }
        String actorId = this.getLastActor().getId();
        this.removeNode(String.valueOf(actorId));
        notifyActorRemoved(actorId);
        return actorId;
    }

    /**
     * Creates the full network based on the actors available.
     */
    public void createFullNetwork() {
        if (this.getActors().size() == 0) {
            return;
        }

        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.connectToAll();
        }
    }

    /**
     * Gets the actor with the corresponding identifier.
     *
     * @param id
     *          the identifier of the actor to get
     * @return the actor with the corresponding identifier
     */
    public Actor getActor(String id) {
        return (Actor) this.getNode(id);
    }

    /**
     * Gets a random actor.
     *
     * @return a random actor
     */
    public Actor getRandomActor() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, getActors().size());
        return (Actor) this.getNode(randomIndex);
    }

    /**
     * Gets a random actor that is not infected.
     *
     * @return a random not infected actor
     */
    public Actor getRandomNotInfectedActor() {
        List<Actor> tmpActors = new LinkedList<Actor>(this.getActors());
        while (!tmpActors.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, tmpActors.size());
            Actor actor = tmpActors.get(randomIndex);
            if (!actor.isInfected()) {
                return actor;
            }
            tmpActors.remove(actor);
        }
        return null;
    }

    /**
     * Gets the actor with the highest index.
     *
     * @return the actor with the highest index
     */
    public Actor getLastActor() {
        return this.getNode(this.getNodeSet().size() - 1);
    }

    /**
     * Gets all actors within the network.
     *
     * @return all actors within the network.
     */
    public Collection<Actor> getActors() {
        return this.getNodeSet();
    }

    /**
     * Gets all susceptible actors within the network.
     *
     * @return all susceptible actors within the network.
     */
    public Collection<Actor> getSusceptibles() {
        List<Actor> susceptibles = new LinkedList<Actor>();

        Iterator<Actor> actorIt = getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (actor.isSusceptible()) {
                susceptibles.add(actor);
            }
        }

        return susceptibles;
    }

    /**
     * Gets all infected actors within the network.
     *
     * @return all infected actors within the network.
     */
    public Collection<Actor> getInfected() {
        List<Actor> infected = new LinkedList<Actor>();

        Iterator<Actor> actorIt = getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (actor.isInfected()) {
                infected.add(actor);
            }
        }

        return infected;
    }

    /**
     * Gets all recovered actors within the network.
     *
     * @return all recovered actors within the network.
     */
    public Collection<Actor> getRecovered() {
        List<Actor> recovered = new LinkedList<Actor>();

        Iterator<Actor> actorIt = getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (actor.isRecovered()) {
                recovered.add(actor);
            }
        }

        return recovered;
    }

    /**
     * Gets an iterator over all actors in an undefined order.
     *
     * @return an iterator over all actors in an undefined order
     */
    public Iterator<Actor> getActorIterator() {
        return this.getNodeIterator();
    }

    /**
     * Clears all connections between the actors.
     */
    public void clearConnections() {
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            actorsIt.next().removeAllConnections();
        }
    }

    /**
     * Removes all connections between actors and resets the actors to being susceptible.
     */
    public void resetActors() {
        clearConnections();
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            actor.makeSusceptible();
        }
    }

    /**
     * Infects a random actor.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease to infect a actor with
     */
    public void infectRandomActor(DiseaseSpecs diseaseSpecs) {
        if (this.getActors() == null || this.getActors().isEmpty()) {
            logger.info("Unable to infect a random actor. No actor available.");
            return;
        }

        // actors performing action in random order
        List<Actor> actors = new ArrayList<Actor>(this.getActors());
        Collections.shuffle(actors);
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.isSusceptible()) {
                actor.forceInfect(diseaseSpecs);
                return;
            }
        }
        logger.info("Unable to infect a random actor. No susceptible actor available.");
    }

    /**
     * Toggles the infection of a specific actor.
     *
     * @param actorId
     *          the actor's id
     * @param diseaseSpecs
     *          the characteristics of the disease to infect the actor with
     */
    public void toggleInfection(String actorId, DiseaseSpecs diseaseSpecs) {
        Iterator<Actor> actorsIt = this.getActorIterator();
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

    /**
     * Checks whether the network is stable. That is, all actors are satisfied.
     * An actor is satisfied if (s)he does not prefer to add a non-existing tie,
     * or remove an existing tie.
     *
     * @return true if the network is stable, false otherwise
     */
    public boolean isStable() {
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (!actor.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the network has an active infection. That is, whether at least one actor
     * is infected and therefore is infectious to others.
     *
     * @return true if the network has an active infection, false otherwise
     */
    public boolean hasActiveInfection() {
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            if (actor.isInfected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the network type.
     *
     * @return the network type
     */
    public NetworkTypes getType() {

        if (this.isEmpty()) {
            return NetworkTypes.EMPTY;
        }

        if (this.isFull()) {
            return NetworkTypes.FULL;
        }

        if (this.isRing()) {
            return NetworkTypes.RING;
        }

        if (this.isStar()) {
            return NetworkTypes.STAR;
        }

        // TODO implement missing network types, such as bipartite

        return NetworkTypes.UNDEFINED;
    }


    /**
     * Checks whether the network is empty. That is, whether there are no connections between any actors whatsoever.
     *
     * @return true if the network is empty, false otherwise
     */
    private boolean isEmpty() {
        return (this.getEdgeCount() == 0);
    }

    /**
     * Checks whether the network is full. That is, whether every actor is connected to every other actor.
     *
     * @return true if the network is full, false otherwise
     */
    private boolean isFull() {
        Iterator<Actor> actorIt = getActorIterator();
        // every actor needs to have connections to every other actor
        while (actorIt.hasNext()) {
            if (actorIt.next().getDegree() != (this.getActors().size() - 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the network is a ring. That is, whether all actors are connected to two other actors,
     * forming a ring that connects all actors within the network.
     *
     * @return true if the network is a ring, false otherwise
     */
    private boolean isRing() {

        boolean ring = true;

        Iterator<Actor> actorIt = getActorIterator();
        Actor firstActor = null;
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();

            // ring - 1st condition: every actor needs to be connected to exactly two other actors
            ring = ring && (actor.getDegree() == 2);

            // ring - 2nd condition: make sure it's a single ring,
            // meaning a node can reach every other node
            if (firstActor == null) {
                firstActor = actor;
            } else {
                ring = ring && firstActor.isConnectedTo(actor);
            }
        }

        return ring;
    }

    /**
     * Checks whether the network is a star. That is, whether there is a single center node that is connected to
     * all other nodes, while the other nodes are solely connected to the center node.
     *
     * @return true if the network is a star, false otherwise
     */
    private boolean isStar() {

        int centers = 0;
        int peripheries = 0;

        Iterator<Actor> actorIt = getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();

            if (actor.getDegree() == 1) {
                peripheries++;
            } else if (actor.getDegree() == (this.getActors().size() - 1)) {
                centers++;
            } else {
                return false;
            }
        }

        return ((centers == 1) && (peripheries == this.getActors().size() - 1));
    }


    /**
     * Adds a listener to be notified when the network changes.
     *
     * @param networkListener
     *          the listener to be added
     */
    public void addNetworkListener(NetworkListener networkListener) {
        this.networkListeners.add(networkListener);
    }

    /**
     * Removes a listener to be notified when the network changes.
     *
     * @param networkListener
     *          the listener to be removed
     */
    public void removeNetworkListener(NetworkListener networkListener) {
        this.networkListeners.remove(networkListener);
    }

    /**
     * Notifies the listeners of the added actor.
     *
     * @param actor
     *          the actor being added
     */
    private final void notifyActorAdded(Actor actor) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActorAdded(actor);
        }
    }

    /**
     * Notifies the actorRemovedListeners of the added actor.
     *
     * @param actorId
     *          the id of the actor being removed
     */
    private final void notifyActorRemoved(String actorId) {
        Iterator<NetworkListener> listenersIt = this.networkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyActorRemoved(actorId);
        }
    }

}
