/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.networks;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

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
    private ViewPanel viewPanel;

    // flag for auto-layout
    private boolean autoLayout;

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
        this.addAttribute("ui.stylesheet", GS_CSS);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // sprite manager
        this.spriteManager = new SpriteManager(this);

        // view
        this.viewer = new Viewer(this, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        enableAutoLayout();
        this.viewPanel = viewer.addDefaultView(false);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.Network#addAgent(
     * nl.uu.socnetid.nidm.utility.UtilityFunction, nl.uu.socnetid.nidm.diseases.DiseaseSpecs,
     * double)
     */
    @Override
    public Agent addAgent(UtilityFunction utilityFunction, DiseaseSpecs diseaseSpecs, double rSigma, double rPi, double phi) {
        Agent agent = super.addAgent(utilityFunction, diseaseSpecs, rSigma, rPi, phi);

        // re-position agents if auto-layout is disabled
        if (!this.autoLayout) {
            positionAgentsInCircle();
        }

        // sprite
        Sprite sprite = this.spriteManager.addSprite(agent.getId());
        sprite.setPosition(Units.PX, 15, 0, -90);
        sprite.attachToNode(agent.getId());
        sprite.setAttribute("label", agent.getLabel());

        return agent;
    }

    /**
     *
     */
    private void positionAgentsInCircle() {
        Collection<Agent> agents = getAgents();
        int n = agents.size();

        Iterator<Agent> agentsIt = agents.iterator();
        int i = 0;

        while (agentsIt.hasNext()) {
            Agent currAgent = agentsIt.next();
            currAgent.setAttribute("xyz",
                    // x-coordinate
                    10 * Math.cos(i * 2 * Math.PI / n),
                    // y-coordinate
                    10 * Math.sin(i * 2 * Math.PI / n),
                    // z-coordinate (0, as graph is 2-dimensional)
                    0);

            i++;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.networks.Network#removeAgent()
     */
    @Override
    public String removeAgent() {
        String agentId = super.removeAgent();
        this.spriteManager.removeSprite(agentId);
        // re-position agents if auto-layout is disabled
        if (!this.autoLayout) {
            positionAgentsInCircle();
        }
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
     * Gets the view panel for displaying networks that can be integrated into a frame manually.
     *
     * @return the view panel for displaying networks that can be integrated into a frame manually
     */
    public ViewPanel getViewPanel() {
        return this.viewPanel;
    }

    /**
     * Enables auto layout of network view.
     */
    public void enableAutoLayout() {
        this.viewer.enableAutoLayout();
        this.autoLayout = true;
    }

    /**
     * Disables auto layout of network view.
     */
    public void disableAutoLayout() {
        this.viewer.disableAutoLayout();
        this.autoLayout = false;
    }

    /**
     * @return the viewer
     */
    public Viewer getViewer() {
        return viewer;
    }

}
