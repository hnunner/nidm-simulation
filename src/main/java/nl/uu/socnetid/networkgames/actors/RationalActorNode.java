package nl.uu.socnetid.networkgames.actors;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class RationalActorNode extends RationalActor implements Actor {

    // logger
    private static final Logger logger = Logger.getLogger(RationalActorNode.class);

    // the graph the actor acts as node in
    private Graph graph;


    /**
     * Private constructor.
     *
     * @param graph
     *          the graph the actor acts as node in
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     */
    protected RationalActorNode(Graph graph, UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        super(utilityFunction, diseaseSpecs);
        this.graph = graph;
        this.graph.addNode(String.valueOf(getId()));
    }

    /**
     * Factory method returning a new {@link Actor} instance.
     *
     * @param graph
     *          the graph the actor acts as node in
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @return a new {@link Actor} instance
     */
    public static Actor newInstance(Graph graph, UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs) {
        return new RationalActorNode(graph, utilityFunction, diseaseSpecs);
    }

    /**
     * Private constructor.
     *
     * @param graph
     *          the graph the actor acts as node in
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the risk factor of the new actor
     */
    protected RationalActorNode(Graph graph, UtilityFunction utilityFunction,
            DiseaseSpecs diseaseSpecs, double riskFactor) {
        super(utilityFunction, diseaseSpecs, riskFactor);
        this.graph = graph;
        this.graph.addNode(String.valueOf(getId()));
    }

    /**
     * Factory method returning a new {@link Actor} instance.
     *
     * @param graph
     *          the graph the actor acts as node in
     * @param utilityFunction
     *          the function the actor uses to compute his utility of the network
     * @param diseaseSpecs
     *          the disease that is or might become present in the network
     * @param riskFactor
     *          the risk factor of the new actor
     * @return a new {@link Actor} instance
     */
    public static Actor newInstance(Graph graph, UtilityFunction utilityFunction,
            DiseaseSpecs diseaseSpecs, double riskFactor) {
        return new RationalActorNode(graph, utilityFunction, diseaseSpecs, riskFactor);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#addConnection(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public boolean addConnection(Actor newConnection) {

        // check node consistency
        if (!checkNewConnectionConsistency(newConnection)) {
            logger.info("Request to add new connection aborted.");
            return false;
        }

        // edge id consistency
        ArrayList<Actor> actors = new ArrayList<Actor>();
        actors.add(this);
        actors.add(newConnection);
        Collections.sort(actors);

        String edgeId = String.valueOf(actors.get(0).getId()) + String.valueOf(actors.get(1).getId());
        String nodeId1 = String.valueOf(actors.get(0).getId());
        String nodeId2 = String.valueOf(actors.get(1).getId());

        if (this.graph.getEdge(edgeId) == null) {
            this.graph.addEdge(edgeId, nodeId1, nodeId2);
        }

        return super.addConnection(newConnection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeConnection(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public boolean removeConnection(Actor connection) {
        // edge id consistency
        ArrayList<Actor> actors = new ArrayList<Actor>();
        actors.add(this);
        actors.add(connection);
        Collections.sort(actors);

        String edgeId = String.valueOf(actors.get(0).getId()) + String.valueOf(actors.get(1).getId());
        if (this.graph.getEdge(edgeId) != null) {
            this.graph.removeEdge(edgeId);
        }

        return super.removeConnection(connection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {

        // remove all graph edges of the current actor
        Edge[] edges = this.graph.getNode(String.valueOf(getId())).getEdgeSet().toArray(new Edge[0]);
        for(int i = 0; i < edges.length; ++i){
            graph.removeEdge(edges[i]);
        }

        // do whatever generic stuff has to be done
        super.removeAllConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#destroy()
     */
    @Override
    public void destroy() {
        this.graph.removeNode(String.valueOf(getId()));
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#fightDisease()
     */
    @Override
    public void fightDisease() {
        super.fightDisease();
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#cure()
     */
    @Override
    public void cure() {
        super.cure();
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.Actor#infect(nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void infect(DiseaseSpecs diseaseSpecs) {
        super.infect(diseaseSpecs);
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.AbstractActor#forceInfect(
     * nl.uu.socnetid.networkgames.disease.DiseaseSpecs)
     */
    @Override
    public void forceInfect(DiseaseSpecs diseaseSpecs) {
        super.forceInfect(diseaseSpecs);
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.AbstractActor#makeSusceptible()
     */
    @Override
    public void makeSusceptible() {
        super.makeSusceptible();
        updateAppearance();
    }

    /**
     * Updates the appearance of the displayed node.
     */
    private void updateAppearance() {
        Node node = this.graph.getNode(String.valueOf(getId()));

        // susceptible
        if (this.isSusceptible()) {
            node.addAttribute("ui.class", "susceptible");
            return;
        }

        // infected
        if (this.isInfected()) {
            node.addAttribute("ui.class", "infected");
            return;
        }

        // recovered
        if (this.isRecovered()) {
            node.addAttribute("ui.class", "recovered");
            return;
        }
    }

}
