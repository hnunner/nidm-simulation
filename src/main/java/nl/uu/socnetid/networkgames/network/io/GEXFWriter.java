package nl.uu.socnetid.networkgames.network.io;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkGEXF;
import org.graphstream.stream.file.FileSinkGEXF.TimeFormat;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.listeners.ConnectionChangeListener;
import nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener;

/**
 * @author Hendrik Nunner
 */
public class GEXFWriter implements ActorAmountListener, ConnectionChangeListener {

    // logger
    private static final Logger logger = Logger.getLogger(GEXFWriter.class);

    // the graph to write
    private Graph graph;
    // the file and sink used to write to
    private String file;
    private FileSinkGEXF fileSink;

    // unique time identifier
    private static final AtomicLong TIME = new AtomicLong(1);
    private final long timeId = TIME.getAndIncrement();


    /**
     * Constructor.
     *
     * @param graph
     *          the graph to write
     * @param file
     *          the file to write to
     */
    public GEXFWriter(Graph graph, String file) {
        this.graph = graph;
        this.file = file;
        this.fileSink = new FileSinkGEXF();
        this.fileSink.setTimeFormat(TimeFormat.DATETIME);
    }


    /**
     * Begins the recording of dynamic networks.
     */
    public void startRecording() {
        try {
            this.fileSink.begin(file);
            this.fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener#notifyActorAdded(long)
     */
    @Override
    public void notifyActorAdded(long actorId) {
        fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());
        fileSink.nodeAdded(this.graph.getId(), timeId, Long.toString(actorId));
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener#notifyActorRemoved(long)
     */
    @Override
    public void notifyActorRemoved(long actorId) {
        fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());
        fileSink.nodeRemoved(this.graph.getId(), timeId, Long.toString(actorId));
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ConnectionChangeListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.networkgames.actors.Actor, nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());
        fileSink.edgeAdded(this.graph.getId(), timeId, edge.getId(),
                String.valueOf(actor1.getId()), String.valueOf(actor2.getId()), false);
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ConnectionChangeListener#
     * notifyEdgeRemoved(org.graphstream.graph.Edge)
     */
    @Override
    public void notifyEdgeRemoved(Edge edge) {
        fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());
        fileSink.edgeRemoved(this.graph.getId(), timeId, edge.getId());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ConnectionChangeListener#
     * notifyConnectionRemoved(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyConnectionRemoved(Actor actor) {
        // nothing to do
    }

    /**
     * Stops the recording of dynamic networks.
     */
    public void stopRecording() {
        try {
            fileSink.stepBegins(this.graph.getId(), timeId, new Date().getTime());

            // TODO find a better way to do this
            // for now: required to flush the last time step - otherwise the last step is not being considered
            fileSink.graphCleared(this.graph.getId(), timeId);

            fileSink.flush();
            fileSink.end();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
