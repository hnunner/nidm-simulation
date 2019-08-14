package nl.uu.socnetid.nidm.networks;

import org.apache.log4j.Logger;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class DisplayableNetwork extends Network {

    private static final String GS_CSS = ""
            + "graph {"
            + "     fill-color: #FFFFFF;"
            + "}"
            + "node {"
            + "     shape: circle;"
            + "     fill-color: #FECD0A;"
            + "     size: 15px;"
            + "     stroke-mode: plain;"
            + "     stroke-color: black;"
            + "     stroke-width: 1px;"
            + "     shadow-mode: gradient-radial;"
            + "     shadow-color: #AAAAAA;"
            + "     shadow-width: 1.5;"
            + "     shadow-offset: 1.5, -1.5;"
            + "}"
            + "node.susceptible {"
            + "     fill-color: #FECD0A;"
            + "}"
            + "node.infected {"
            + "     fill-color: #FF5500;"
            + "}"
            + "node.recovered {"
            + "     fill-color: #56B4E9;"
            + "}"
            + "node:clicked {"
            + "     shape:circle;"
            + "     size: 30px;"
            + "}"
            + "sprite {"
            + "     shape: box;"
            + "     size: 0px, 0px;"
            + "}";


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
        // URL gsStyles = this.getClass().getResource("/graph-stream.css");
        // this.addAttribute("ui.stylesheet", "url('file:" + gsStyles.getPath() + "')");
        this.addAttribute("ui.stylesheet", GS_CSS);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // sprite manager
        this.spriteManager = new SpriteManager(this);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.Network#addAgent(
     * nl.uu.socnetid.nidm.utilities.UtilityFunction, nl.uu.socnetid.nidm.diseases.DiseaseSpecs,
     * double)
     */
    @Override
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi) {
        Agent agent = super.addAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi);

        // sprite
        Sprite sprite = this.spriteManager.addSprite(agent.getId());
        sprite.setPosition(Units.PX, 15, 0, -90);
        sprite.attachToNode(agent.getId());
        sprite.setAttribute("label", agent.getLabel());

        return agent;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.Network#removeAgent()
     */
    @Override
    public String removeAgent() {
        String agentId = super.removeAgent();
        this.spriteManager.removeSprite(agentId);
        return agentId;
    }


    /**
     * Creates a graphstream default frame for the network.
     */
    public void show() {
        this.viewer = this.display();
        this.viewer.enableAutoLayout();
    }

    /**
     * Creates a view for displaying networks that can be integrated into a frame manually.
     *
     * @return the view for displaying networks that can be integrated into a frame manually
     */
    public ViewPanel createView() {
        this.viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        this.viewer.enableAutoLayout();
        return viewer.addDefaultView(false);
    }

    /**
     * @return the viewer
     */
    public Viewer getViewer() {
        return viewer;
    }

}
