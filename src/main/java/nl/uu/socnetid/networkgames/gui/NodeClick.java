package nl.uu.socnetid.networkgames.gui;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import nl.uu.socnetid.networkgames.networks.DisplayableNetwork;

/**
 * @author Hendrik Nunner
 */
public class NodeClick implements Runnable, ViewerListener {

    // pipe required for continuous checks
    private final ViewerPipe fromViewer;
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
        this.fromViewer = network.getViewer().newViewerPipe();
        this.fromViewer.addViewerListener(this);
        this.fromViewer.addSink(network);
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (loop) {
            this.fromViewer.pump();
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
