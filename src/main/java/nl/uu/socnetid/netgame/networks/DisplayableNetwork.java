package nl.uu.socnetid.netgame.networks;

import java.net.URL;

import org.apache.log4j.Logger;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class DisplayableNetwork extends Network {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayableNetwork.class);

    // graphstream
    private Viewer viewer;

    // sprite manager
    private SpriteManager spriteManager;


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

        // sprite manager
        this.spriteManager = new SpriteManager(this);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.Network#addActor(
     * nl.uu.socnetid.netgame.utilities.UtilityFunction, nl.uu.socnetid.netgame.diseases.DiseaseSpecs,
     * double)
     */
    @Override
    public Actor addActor(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double riskFactor) {
        Actor actor = super.addActor(utilityFunction, diseaseSpecs, riskFactor);

        // sprite
        Sprite sprite = this.spriteManager.addSprite(actor.getId());
        sprite.setPosition(Units.PX, 15, 0, -90);
        sprite.attachToNode(actor.getId());
        sprite.setAttribute("label", actor.getLabel());

        return actor;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.networks.Network#removeActor()
     */
    @Override
    public String removeActor() {
        String actorId = super.removeActor();
        this.spriteManager.removeSprite(actorId);
        return actorId;
    }


    /**
     * Creates a ui representation of the network.
     */
    public void show() {
        this.viewer = this.display();
        this.viewer.enableAutoLayout();
    }

    /**
     * @return the viewer
     */
    public Viewer getViewer() {
        return viewer;
    }

}
