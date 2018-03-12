package nl.uu.socnetid.networkgames.io;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.stream.file.FileSinkGEXF;
import org.graphstream.stream.file.FileSinkGEXF.TimeFormat;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorListener;
import nl.uu.socnetid.networkgames.networks.Network;
import nl.uu.socnetid.networkgames.networks.NetworkListener;

/**
 * @author Hendrik Nunner
 */
public class GEXFWriter implements ActorListener, NetworkListener {

    // logger
    private static final Logger logger = Logger.getLogger(GEXFWriter.class);

    // unique time identifier
    private static final AtomicLong TIME = new AtomicLong(1);
    private final long timeId = TIME.getAndIncrement();

    // file sink
    private FileSinkGEXF fileSink;
    // graph identifier
    private String networkId;

    // status of dynamic writer
    private boolean isRecording = false;

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
            fileSink.writeAll(network, file);
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
        this.networkId = network.getId();

        // listeners
        network.addNetworkListener(this);
        Iterator<Actor> actorIt = network.getActorIterator();
        while (actorIt.hasNext()) {
            actorIt.next().addActorListener(this);
        }

        this.fileSink = new FileSinkGEXF();
        this.fileSink.setTimeFormat(TimeFormat.DATETIME);

        try {
            this.fileSink.begin(file);
            this.isRecording = true;
            this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.networks.NetworkListener#notifyActorAdded(
     * nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyActorAdded(Actor actor) {
        actor.addActorListener(this);
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.nodeAdded(this.networkId, this.timeId, actor.getId());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.networks.listeners.NetworkListener#notifyActorRemoved(java.lang.String)
     */
    @Override
    public void notifyActorRemoved(String actorId) {
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.nodeRemoved(this.networkId, this.timeId, actorId);
        try {
            this.fileSink.flush();
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
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.nodeAttributeAdded(this.networkId, this.timeId,
                String.valueOf(actor.getId()), attribute, value.toString());
        try {
            this.fileSink.flush();
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
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.nodeAttributeChanged(this.networkId, this.timeId,
                String.valueOf(actor.getId()), attribute, oldValue.toString(), newValue.toString());
        try {
            this.fileSink.flush();
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
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.nodeAttributeRemoved(this.networkId, this.timeId,
                String.valueOf(actor.getId()), attribute);
        try {
            this.fileSink.flush();
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
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.edgeAdded(this.networkId, this.timeId, edge.getId(),
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
        this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());
        this.fileSink.edgeRemoved(this.networkId, this.timeId, edge.getId());
        try {
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Stops the recording of dynamic networks.
     */
    public void stopRecording() {
        if (!this.isRecording) {
            return;
        }

        try {
            this.fileSink.stepBegins(this.networkId, this.timeId, new Date().getTime());

            // TODO find a better way to do this
            // for now: required to flush the last time step - otherwise the last step is not being considered
            this.fileSink.graphCleared(this.networkId, this.timeId);

            this.fileSink.flush();
            this.fileSink.end();

            this.isRecording = false;

        } catch (IOException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyRoundFinished(
     * nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) {
        // nothing to do
    }

}
