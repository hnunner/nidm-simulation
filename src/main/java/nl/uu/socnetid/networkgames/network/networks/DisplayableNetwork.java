package nl.uu.socnetid.networkgames.network.networks;

import java.net.URL;

import org.apache.log4j.Logger;
import org.graphstream.ui.view.Viewer;

/**
 * @author Hendrik Nunner
 */
public class DisplayableNetwork extends Network {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayableNetwork.class);

    // graphstream
    private Viewer viewer;


    /**
     * Constructor.
     */
    public DisplayableNetwork() {
        super();

        // init graph-stream ui settings
        this.addAttribute("ui.quality");
        this.addAttribute("ui.antialias");
        URL gsStyles = this.getClass().getClassLoader().getResource("graph-stream.css");
        this.addAttribute("ui.stylesheet", "url('file:" + gsStyles.getPath() + "')");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    }


    /**
     * Creates a ui representation of the network.
     */
    public void show() {
        this.viewer = this.display();
    }

    /**
     * @return the viewer
     */
    public Viewer getViewer() {
        return viewer;
    }

}
