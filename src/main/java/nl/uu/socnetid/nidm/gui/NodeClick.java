/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.gui;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import nl.uu.socnetid.nidm.networks.DisplayableNetwork;

/**
 * @author Hendrik Nunner
 */
public class NodeClick implements Runnable, ViewerListener {

    // pipe required for continuous checks
    private final ViewerPipe viewerPipe;
    private boolean loop = true;

    // id of clicked node
    private String clickedNodeId;

    // listener
    private final Set<NodeClickListener> listeners = new CopyOnWriteArraySet<NodeClickListener>();


    /**
     * Constructor
     *
     * @param network
     *          the network
     */
    public NodeClick(DisplayableNetwork network) {
        this.viewerPipe = network.getViewer().newViewerPipe();
        this.viewerPipe.addViewerListener(this);
        this.viewerPipe.addSink(network);
        this.viewerPipe.addAttributeSink(network);
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (loop) {
            this.viewerPipe.pump();
        }
    }


    /* (non-Javadoc)
     * @see org.graphstream.ui.view.ViewerListener#viewClosed(java.lang.String)
     */
    @Override
    public void viewClosed(String viewName) {
        this.loop = false;
    }

    /* (non-Javadoc)
     * @see org.graphstream.ui.view.ViewerListener#buttonPushed(java.lang.String)
     */
    @Override
    public void buttonPushed(String id) { }

    /* (non-Javadoc)
     * @see org.graphstream.ui.view.ViewerListener#buttonReleased(java.lang.String)
     */
    @Override
    public void buttonReleased(String id) {
        this.clickedNodeId = id;
        notifyListeners();
    }


    /**
     * Notifies the listeners of task completion.
     */
    private final void notifyListeners() {
        Iterator<NodeClickListener> listenersIt = this.listeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notify(this);
        }
    }

    /**
     * Adds a listener to be notified when a gui event occurs.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addListener(NodeClickListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeListener(NodeClickListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * @return the clickedNodeId
     */
    public String getClickedNodeId() {
        return clickedNodeId;
    }

}
