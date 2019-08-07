package nl.uu.socnetid.netgame.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import nl.uu.socnetid.netgame.agents.Agent;

/**
 * @author Hendrik Nunner
 *
 * Parts of this code are strongly influenced by the following online sources:
 *
 * Title?   "Dijkstra’s shortest path algorithm in Java - Tutorial"
 * Author?  "Lars Vogel"
 * Source?  "http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html"
 * License? "Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Germany"
 *          (http://creativecommons.org/licenses/by-nc-sa/3.0/de/deed.en)
 */
public class DijkstraShortestPath {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DijkstraShortestPath.class);

    // computation queues for settled and unsettled nodes
    private Set<Agent> settledNodes;
    private Set<Agent> unSettledNodes;

    // maps to hold the results of the computations
    private Map<Agent, Agent> predecessors;
    private Map<Agent, Integer> distance;


    /**
     * Executes the Dijkstra algorithm to find a shortest
     * path between a source and all of its connections.
     *
     * @param source
     *          the node to start with
     */
    public void executeShortestPaths(Agent source) {

        // init or clean up
        this.settledNodes = new HashSet<Agent>();
        this.unSettledNodes = new HashSet<Agent>();
        this.predecessors = new HashMap<Agent, Agent>();
        this.distance = new HashMap<Agent, Integer>();

        // start with source node
        distance.put(source, 0);
        unSettledNodes.add(source);

        // compute paths for all connected nodes
        while (!unSettledNodes.isEmpty()) {
            Agent node = getMinimumDistanceNode(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private Agent getMinimumDistanceNode(Set<Agent> nodes) {
        Agent minimum = null;
        for (Agent node : nodes) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }
        return minimum;
    }

    private void findMinimalDistances(Agent node) {
        List<Agent> unsettledNeighbors = getUnsettledNeighbors(node);
        for (Agent target : unsettledNeighbors) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance()) {
                distance.put(target, getShortestDistance(node) + getDistance());
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }

    /**
     * Gets the neighbors of a node that are not yet settled.
     *
     * @param node
     *          the node to get the unsettled neighbors for
     * @return the neighbors of a node that are not yet settled
     */
    private List<Agent> getUnsettledNeighbors(Agent node) {
        List<Agent> neighbors = new ArrayList<Agent>();
        Iterator<Agent> connectionsIt = node.getConnections().iterator();
        while (connectionsIt.hasNext()) {
            Agent connection = connectionsIt.next();
            if (!isSettled(connection)) {
                neighbors.add(connection);
            }
        }
        return neighbors;
    }

    /**
     * Checks whether a node is settled
     *
     * @param node
     *          the node to check if it is settled
     * @return true, if the node is settled, false otherwise
     */
    private boolean isSettled(Agent node) {
        return settledNodes.contains(node);
    }

    /**
     * Gets the shortest distance of a destination node.
     *
     * @param destination
     *          the destination node
     * @return the shortest distance of a destination node
     */
    private int getShortestDistance(Agent destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        }
        return d;
    }

    private int getDistance() {    //Agent node, Agent target) {
        // no implementation of weighted connections
        return 1;
        /*
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
        */
    }

    /**
     * Gets the shortest path between the node the computation
     * has been executed with and a target node.
     *
     * @param target
     *          the target node
     * @return the shortest path between the node the computation has been executed with and a target node
     */
    public LinkedList<Agent> getShortestPath(Agent target) {
        LinkedList<Agent> path = new LinkedList<Agent>();
        Agent step = target;

        // no path
        if (predecessors.get(step) == null) {
            return null;
        }

        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }

        // put path into the correct order
        Collections.reverse(path);

        return path;
    }

    /**
     * Gets the shortest path length between the node the computation
     * has been executed with and a target node.
     *
     * @param target
     *          the target node
     * @return the shortest path length between the node the computation has been executed with and a target node
     */
    public Integer getShortestPathLength(Agent target) {
        LinkedList<Agent> shortestPath = getShortestPath(target);
        if (shortestPath == null) {
            return null;
        }

        // all connections have the same weight
        // also, shortest path contains the agents, thus the
        // amount of edges is the number of agents minus one
        return shortestPath.size() - 1;
    }

}
