package nl.uu.socnetid.networkgames.network.io;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.stream.file.FileSinkGEXF;
import org.graphstream.stream.file.FileSinkGEXF.TimeFormat;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorListener;
import nl.uu.socnetid.networkgames.network.listeners.ActorAmountListener;
import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class GEXFWriter implements ActorListener, ActorAmountListener {

    // logger
    private static final Logger logger = Logger.getLogger(GEXFWriter.class);

    // unique time identifier
    private static final AtomicLong TIME = new AtomicLong(1);
    private final long timeId = TIME.getAndIncrement();

    // file sink
    private FileSinkGEXF fileSink;
    // graph identifier
    private String graphId;

    /**
     * Constructor.
     */
    public GEXFWriter() { }


    /**
     * Writes a static representation of the current graph to a file
     *
     * @param network
     *          the network to write
     * @param file
     *          the file to write to
     */
    public void writeStaticNetwork(Network network, String file) {
        try {
            FileSinkGEXF fileSink = new FileSinkGEXF();
            fileSink.writeAll(network.getGraph(), file);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Begins the recording of dynamic networks.
     *
     * @param network
     *          the network to write
     * @param file
     *          the file to write to
     */
    public void startRecording(Network network, String file) {
        this.graphId = network.getGraph().getId();

        this.fileSink = new FileSinkGEXF();
        this.fileSink.setTimeFormat(TimeFormat.DATETIME);

        try {
            this.fileSink.begin(file);
            this.fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
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
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.nodeAdded(this.graphId, timeId, Long.toString(actorId));
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
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.nodeRemoved(this.graphId, timeId, Long.toString(actorId));
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyAttributeAdded(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Actor actor, String attribute, Object value) {
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.nodeAttributeAdded(this.graphId, timeId, String.valueOf(actor.getId()), attribute, value.toString());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyAttributeChanged(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Actor actor, String attribute, Object oldValue, Object newValue) {
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.nodeAttributeChanged(this.graphId, timeId, String.valueOf(actor.getId()),
                attribute, oldValue.toString(), newValue.toString());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorListener#notifyAttributeRemoved(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Actor actor, String attribute) {
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.nodeAttributeRemoved(this.graphId, timeId, String.valueOf(actor.getId()), attribute);
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorConnectionListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.networkgames.actors.Actor, nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.edgeAdded(this.graphId, timeId, edge.getId(),
                String.valueOf(actor1.getId()), String.valueOf(actor2.getId()), false);
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyEdgeRemoved(
     * nl.uu.socnetid.networkgames.actors.Actor, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Actor actor, Edge edge) {
        fileSink.stepBegins(this.graphId, timeId, new Date().getTime());
        fileSink.edgeRemoved(this.graphId, timeId, edge.getId());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Stops the recording of dynamic networks.
     */
    public void stopRecording() {
        try {
            fileSink.stepBegins(this.graphId, timeId, new Date().getTime());

            // TODO find a better way to do this
            // for now: required to flush the last time step - otherwise the last step is not being considered
            fileSink.graphCleared(this.graphId, timeId);

            fileSink.flush();
            fileSink.end();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyRoundFinished(nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) {
        // nothing to do
    }

}