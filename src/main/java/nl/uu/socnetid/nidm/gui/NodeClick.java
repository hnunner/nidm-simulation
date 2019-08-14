package nl.uu.socnetid.nidm.gui;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import nl.uu.socnetid.nidm.networks.DisplayableNetwork;

/**
 * @author Hendrik Nunner
 */
public class NodeClick implements Runnable, ViewerListener {  // , MouseManager {


    private View view;


    private GraphicElement selectedElement = null;
    private Integer lastX = null;
    private Integer lastY = null;



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
        // network.getViewer().getDefaultView().setMouseManager(this);
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


//    /* (non-Javadoc)
//     * @see org.graphstream.ui.view.util.MouseManager#init(org.graphstream.ui.graphicGraph.GraphicGraph,
//     * org.graphstream.ui.view.View)
//     */
//    @Override
//    public void init(GraphicGraph graph, View view) {
//        this.view = view;
//        view.addMouseListener(this);
//        view.addMouseMotionListener(this);
//    }
//
//    /* (non-Javadoc)
//     * @see org.graphstream.ui.view.util.MouseManager#release()
//     */
//    @Override
//    public void release() {
//        view.removeMouseListener(this);
//        view.removeMouseMotionListener(this);
//
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseClicked(MouseEvent e) { }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mousePressed(MouseEvent e) {
//        this.selectedElement = view.findNodeOrSpriteAt(e.getX(), e.getY());
//        this.lastX = e.getX();
//        this.lastY = e.getY();
//    }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        this.clickedNodeId = this.selectedElement.getId();
//        notifyListeners();
//
//        this.selectedElement = null;
//        this.lastX = null;
//        this.lastY = null;
//    }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseEntered(MouseEvent e) { }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseExited(MouseEvent e) { }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        if (this.selectedElement != null) {
//
//            double xDelta = Double.valueOf(e.getX() - lastX) / 75;
//            double yDelta = Double.valueOf(e.getY() - lastY) / 75;
//
//            System.out.println("Moving element " + selectedElement.getId() +
//                    " from (" + selectedElement.getX() + ", " + selectedElement.getY() + ")" +
//                    " to (" + xDelta + ", " + yDelta + ")");
//
//            selectedElement.changeAttribute("x", this.selectedElement.getX() + xDelta);
//            selectedElement.changeAttribute("y", this.selectedElement.getY() - yDelta);
//        }
//        this.lastX = e.getX();
//        this.lastY = e.getY();
//    }
//
//
//    /* (non-Javadoc)
//     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
//     */
//    @Override
//    public void mouseMoved(MouseEvent e) { }


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
