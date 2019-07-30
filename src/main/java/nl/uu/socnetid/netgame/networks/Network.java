package nl.uu.socnetid.netgame.networks;

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
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.implementations.SingleGraph;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.actors.ActorFactory;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class Network extends SingleGraph {

    // logger
    private static final Logger logger = Logger.getLogger(Network.class);

    // risk factor for risk neutral actors
    private static final double RISK_FACTOR_NEUTRAL = 1.0;

    // standard share to evaluate per actor
    private static final double STANDARD_PHI = 0.4;

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
        return this.addActor(utilityFunction, diseaseSpecs, RISK_FACTOR_NEUTRAL, RISK_FACTOR_NEUTRAL, STANDARD_PHI);
    }

    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param rSigma
     *          the factor describing how the actor perceives severity of diseases:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param rPi
     *          the factor describing how the actor perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi) {
        return this.addActor(utilityFunction, diseaseSpecs, rSigma, rPi, STANDARD_PHI);
    }

    /**
     * Creates and adds an actor to the network.
     *
     * @param utilityFunction
     *          the actor's utility function
     * @param diseaseSpecs
     *          the disease specs
     * @param rSigma
     *          the factor describing how the actor perceives severity of diseases:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param rPi
     *          the factor describing how the actor perceives the risk of an infection:
     *          <1: risk seeking, =1: risk neutral; >1: risk averse
     * @param phi
     *          the share of peers an actor evaluates per round
     * @return the newly added actor.
     */
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi) {
        Actor actor = this.addNode(String.valueOf(this.getNodeCount() + 1));
        actor.initActor(utilityFunction, diseaseSpecs, rSigma, rPi, phi);
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
     * Gets the number of actors in the network.
     *
     * @return the number of actor in the network.
     */
    public int getN() {
        return this.getActors().size();
    }

    /**
     * The formula was fitted using the average degree per actor,
     * dependent on the network size.
     *
     * @return the average degree dependent on network size
     */
    public double getAverageDegree() {
        return 0.8628 * Math.pow(this.getN(), 0.6246);
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
     * @return the infected actor, null if no actor was infected
     */
    public Actor infectRandomActor(DiseaseSpecs diseaseSpecs) {
        if (this.getActors() == null || this.getActors().isEmpty()) {
            logger.info("Unable to infect a random actor. No actor available.");
            return null;
        }

        // actors performing action in random order
        List<Actor> actors = new ArrayList<Actor>(this.getActors());
        Collections.shuffle(actors);
        Iterator<Actor> actorsIt = actors.iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            if (actor.isSusceptible()) {
                actor.forceInfect(diseaseSpecs);
                return actor;
            }
        }
        logger.info("Unable to infect a random actor. No susceptible actor available.");
        return null;
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
                ring = ring && firstActor.hasConnectionTo(actor);
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

    /**
     * Gets the average degree of the network.
     *
     * @return the average degree of the network
     */
    public double getAvDegree() {
        return Toolkit.averageDegree(this);
    }

    /**
     * Gets the average degree at distance 2 of the network.
     *
     * @return the average degree at distance 2 of the network
     */
    public double getAvDegree2() {
        double avDegree2 = 0;
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            avDegree2 += actor.getSecondOrderDegree();
        }
        return avDegree2/this.getActors().size();
    }

    /**
     * Gets the average closeness of the network.
     *
     * @return the average closeness of the network
     */
    public double getAvCloseness() {
        double avCloseness = 0;
        Iterator<Actor> actorsIt = this.getActors().iterator();
        while (actorsIt.hasNext()) {
            Actor actor = actorsIt.next();
            avCloseness += actor.getCloseness();
        }
        return avCloseness/this.getActors().size();
    }

    /**
     * Gets the density of the network.
     *
     * @return the density of the network
     */
    public double getDensity() {
        return Toolkit.density(this);
    }

    /**
     * Gets the average clustering coefficient of the network.
     *
     * @return the average clustering coefficient of the network
     */
    public double getAvClustering() {
        return Toolkit.averageClusteringCoefficient(this);
    }

    /**
     * Gets the average alpha of all actors in the network.
     *
     * @return the average alpha of all actors in the network
     */
    public double getAvAlpha() {
        double avAlpha = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avAlpha += actor.getUtilityFunction().getAlpha();
        }
        return (avAlpha / this.getActors().size());
    }


    /**
     * Gets the average beta of all actors in the network.
     *
     * @return the average beta of all actors in the network
     */
    public double getAvBeta() {
        double avBeta = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avBeta += actor.getUtilityFunction().getBeta();
        }
        return (avBeta / this.getActors().size());
    }

    /**
     * Gets the network maintenance costs of all actors in the network.
     *
     * @return the network maintenance costs of all actors in the network
     */
    public double getAvC() {
        double avC = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avC += actor.getUtilityFunction().getC();
        }
        return (avC / this.getActors().size());
    }

    /**
     * Gets the average risk factor for disease severity of all actors in the network.
     *
     * @return the average risk factor for disease severity of all actors in the network
     */
    public double getAvRSigma() {
        double avRSigma = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avRSigma += actor.getRSigma();
        }
        return (avRSigma / this.getActors().size());
    }

    /**
     * Gets the average risk factor for probability of infections of all actors in the network.
     *
     * @return the average risk factor for probability of infections of all actors in the network
     */
    public double getAvRPi() {
        double avRPi = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avRPi += actor.getRPi();
        }
        return (avRPi / this.getActors().size());
    }

    /**
     * Gets the average utility of all actors in the network.
     *
     * @return the average utility of all actors in the network
     */
    public double getAvUtility() {
        double avUtility = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avUtility += actor.getUtility().getOverallUtility();
        }
        return (avUtility / this.getActors().size());
    }

    /**
     * Gets the average benefit at distance 1 of all actors in the network.
     *
     * @return the average benefit at distance 1 of all actors in the network
     */
    public double getAvBenefitDistance1() {
        double avBenefit1 = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avBenefit1 += actor.getUtility().getBenefitDirectConnections();
        }
        return (avBenefit1 / this.getActors().size());
    }

    /**
     * Gets the average benefit at distance 2 of all actors in the network.
     *
     * @return the average benefit at distance 2 of all actors in the network
     */
    public double getAvBenefitDistance2() {
        double avBenefit2 = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avBenefit2 += actor.getUtility().getBenefitIndirectConnections();
        }
        return (avBenefit2 / this.getActors().size());
    }

    /**
     * Gets the average costs for actors at distance 1 of all actors in the network.
     *
     * @return the average costs for actors at distance 1 of all actors in the network
     */
    public double getAvCostsDistance1() {
        double avCosts1 = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avCosts1 += actor.getUtility().getCostsDirectConnections();
        }
        return (avCosts1 / this.getActors().size());
    }

    /**
     * Gets the average disease costs of all actors in the network.
     *
     * @return the average disease costs of all actors in the network
     */
    public double getAvCostsDisease() {
        double avCostsDisease = 0;
        Iterator<Actor> actorIt = this.getActorIterator();
        while (actorIt.hasNext()) {
            Actor actor = actorIt.next();
            avCostsDisease += actor.getUtility().getEffectOfDisease();
        }
        return (avCostsDisease / this.getActors().size());
    }


}
