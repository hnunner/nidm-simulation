package nl.uu.socnetid.netgame.io.network;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.stream.file.FileSinkGEXF;
import org.graphstream.stream.file.FileSinkGEXF2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.actors.ActorListener;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.networks.NetworkListener;

/**
 * @author Hendrik Nunner
 */
public class GEXFWriter implements ActorListener, NetworkListener {

    // logger
    private static final Logger logger = Logger.getLogger(GEXFWriter.class);

    // default export directory
    public static final String DEFAULT_EXPORT_DIR = new StringBuilder().append(System.getProperty("user.dir"))
            .append("/network-exports/").toString();

    // unique time identifier
    private static final AtomicLong TIME = new AtomicLong(1);
    private final long timeId = TIME.getAndIncrement();

    // recorder step counter
    private static final AtomicLong RECORD_STEP = new AtomicLong(1);

    // file sink
    private FileSinkGEXF2 fileSink;
    // graph identifier
    private String networkId;
    // the file to write to
    private String file;

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
        this.file = file;

        // listeners
        network.addNetworkListener(this);
        Iterator<Actor> actorIt = network.getActorIterator();
        while (actorIt.hasNext()) {
            actorIt.next().addActorListener(this);
        }

        this.fileSink = new FileSinkGEXF2();
        try {
            this.fileSink.begin(this.file);
            this.isRecording = true;
            this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.NetworkListener#notifyActorAdded(
     * nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyActorAdded(Actor actor) {
        actor.addActorListener(this);
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
        this.fileSink.nodeAdded(this.networkId, this.timeId, actor.getId());
        try {
            fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.listeners.NetworkListener#notifyActorRemoved(java.lang.String)
     */
    @Override
    public void notifyActorRemoved(String actorId) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
        this.fileSink.nodeRemoved(this.networkId, this.timeId, actorId);
        try {
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeAdded(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Actor actor, String attribute, Object value) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
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
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeChanged(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Actor actor, String attribute, Object oldValue, Object newValue) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
        this.fileSink.nodeAttributeChanged(this.networkId, this.timeId,
                String.valueOf(actor.getId()), attribute, oldValue.toString(), newValue.toString());
        try {
            this.fileSink.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorListener#notifyAttributeRemoved(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Actor actor, String attribute) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
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
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorConnectionListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.netgame.actors.Actor, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
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
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyEdgeRemoved(
     * nl.uu.socnetid.netgame.actors.Actor, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Actor actor, Edge edge) {
        this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());
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
            this.fileSink.stepBegins(this.networkId, this.timeId, RECORD_STEP.getAndIncrement());

            // XXX check whether there is a better way to make sure that the last simulation step is still recorded.
            // for now: required to clear the graph and flush the last time step
            // -- otherwise the last step is not being considered
            this.fileSink.graphCleared(this.networkId, this.timeId);

            this.fileSink.flush();
            this.fileSink.end();

            // XXX this is quite dodgy, as graphstream should add the 'mode="dynamic"' attribute to the node attributes.
            // However, this seems not to work out of the box and I don't know how to trigger this explicitly.
            makeNodeAttributesDynamic();

            this.isRecording = false;
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * Reads the GEXF file, adds the 'mode="dynamic"' attribute to graph nodes, and writes it back to the file.
     * This is a workaround, as I don't know how to trigger this using the GEXF file sink as provided by graphstream.
     *
     * This method is partly taken from / strongly inspired by:
     * https://www.mkyong.com/java/how-to-modify-xml-file-in-java-dom-parser/
     */
    private void makeNodeAttributesDynamic() {
        try {
            // read the file
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(this.file);

            // get the "attributes" node
            Node graph = doc.getElementsByTagName("graph").item(0);
            NodeList childNodes = graph.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
               if (node.getNodeName().equals("attributes")) {
                   // add the
                   ((Element) node).setAttribute("mode", "dynamic");
               }
            }

            // write back into GEXF file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(this.file));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            logger.error(e);
        } catch (SAXException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (TransformerConfigurationException e) {
            logger.error(e);
        } catch (TransformerException e) {
            logger.error(e);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) {
        // nothing to do
    }

}
