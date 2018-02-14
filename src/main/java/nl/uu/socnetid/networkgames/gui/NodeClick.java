package nl.uu.socnetid.networkgames.gui;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

/**
 * @author Hendrik Nunner
 */
public class NodeClick implements Runnable, ViewerListener {

    // pipe required for continuous checks
    private final ViewerPipe fromViewer;
    private boolean loop = true;

    // id of clicked node
    private long clickedNodeId;

    // listener
    private final Set<NodeClickListener> listeners = new CopyOnWriteArraySet<NodeClickListener>();


    /**
     * Constructor
     *
     * @param graph
     *          the graph to use as sink
     * @param viewer
     *          the viewer to listen to clicks on
     */
    public NodeClick(Graph graph, Viewer viewer) {
        this.fromViewer = viewer.newViewerPipe();
        this.fromViewer.addViewerListener(this);
        this.fromViewer.addSink(graph);
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
        this.clickedNodeId = Long.valueOf(id);
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
    public long getClickedNodeId() {
        return clickedNodeId;
    }

}
