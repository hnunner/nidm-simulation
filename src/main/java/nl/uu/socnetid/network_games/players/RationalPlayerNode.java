package nl.uu.socnetid.network_games.players;

import java.util.ArrayList;
import java.util.Collections;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import nl.uu.socnetid.network_games.disease.Disease;

/**
 * @author Hendrik Nunner
 */
public class RationalPlayerNode extends RationalPlayer implements Player {

    // the graph the player acts as node in
    private Graph graph;


    /**
     * Private constructor.
     *
     * @param graph
     *          the graph the player acts as node in
     */
    protected RationalPlayerNode(Graph graph) {
        super();
        this.graph = graph;
        this.graph.addNode(String.valueOf(getId()));
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @param graph
     *          the graph the player acts as node in
     * @return a new {@link Player} instance
     */
    public static Player newInstance(Graph graph) {
        return new RationalPlayerNode(graph);
    }

    /**
     * Private constructor.
     *
     * @param graph
     *          the graph the player acts as node in
     * @param riskFactor
     *          the risk factor of the new player
     */
    protected RationalPlayerNode(Graph graph, double riskFactor) {
        super(riskFactor);
        this.graph = graph;
        this.graph.addNode(String.valueOf(getId()));
    }

    /**
     * Factory method returning a new {@link Player} instance.
     *
     * @param graph
     *          the graph the player acts as node in
     * @param riskFactor
     *          the risk factor of the new player
     * @return a new {@link Player} instance
     */
    public static Player newInstance(Graph graph, double riskFactor) {
        return new RationalPlayerNode(graph, riskFactor);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#addConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean addConnection(Player newConnection) {
        // edge id consistency
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(this);
        players.add(newConnection);
        Collections.sort(players);

        String edgeId = String.valueOf(players.get(0).getId()) + String.valueOf(players.get(1).getId());
        String nodeId1 = String.valueOf(players.get(0).getId());
        String nodeId2 = String.valueOf(players.get(1).getId());

        if (this.graph.getEdge(edgeId) == null) {
            this.graph.addEdge(edgeId, nodeId1, nodeId2);
        }

        return super.addConnection(newConnection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeConnection(nl.uu.socnetid.network_games.players.Player)
     */
    @Override
    public boolean removeConnection(Player connection) {
        // edge id consistency
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(this);
        players.add(connection);
        Collections.sort(players);

        String edgeId = String.valueOf(players.get(0).getId()) + String.valueOf(players.get(1).getId());
        if (this.graph.getEdge(edgeId) != null) {
            this.graph.removeEdge(edgeId);
        }

        return super.removeConnection(connection);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#removeAllConnections()
     */
    @Override
    public void removeAllConnections() {

        // remove all graph edges of the current player
        Edge[] edges = this.graph.getNode(String.valueOf(getId())).getEdgeSet().toArray(new Edge[0]);
        for(int i = 0; i < edges.length; ++i){
            graph.removeEdge(edges[i]);
        }

        // do whatever generic stuff has to be done
        super.removeAllConnections();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#destroy()
     */
    @Override
    public void destroy() {
        this.graph.removeNode(String.valueOf(getId()));
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#fightDisease()
     */
    @Override
    public void fightDisease() {
        super.fightDisease();
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#cure()
     */
    @Override
    public void cure() {
        super.cure();
        updateAppearance();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.players.Player#infect(nl.uu.socnetid.network_games.disease.Disease)
     */
    @Override
    public void infect(Disease disease) {
        super.infect(disease);
        updateAppearance();
    }

    /**
     * Updates the appearance of the displayed node.
     */
    private void updateAppearance() {
        Node node = this.graph.getNode(String.valueOf(getId()));

        // not infected
        if (!this.isInfected()) {
            node.removeAttribute("ui.class");
            return;
        }

        // infected without symptoms
        if (!this.getDisease().isVisible()) {
            node.addAttribute("ui.class", "infectedinvisible");
            return;
        }

        // infected with symptoms
        node.addAttribute("ui.class", "infectedvisible");
    }

}
