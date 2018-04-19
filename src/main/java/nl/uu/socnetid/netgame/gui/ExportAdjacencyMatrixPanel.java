package nl.uu.socnetid.netgame.gui;

import nl.uu.socnetid.netgame.io.network.AdjacencyMatrixWriter;
import nl.uu.socnetid.netgame.io.network.NetworkWriter;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportAdjacencyMatrixPanel extends ExportCSVPanel {

    private static final long serialVersionUID = -6380530687392288407L;

    /**
     * Create the panel.
     *
     * @param network
     *          the network to write
     */
    public ExportAdjacencyMatrixPanel(Network network) {
        super(network);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.ExportCSVPanel#getNetworkWriter()
     */
    @Override
    protected NetworkWriter getNetworkWriter() {
        return new AdjacencyMatrixWriter();
    }

}
