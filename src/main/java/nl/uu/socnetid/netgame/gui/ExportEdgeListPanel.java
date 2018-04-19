package nl.uu.socnetid.netgame.gui;

import nl.uu.socnetid.netgame.io.network.EdgeListWriter;
import nl.uu.socnetid.netgame.io.network.NetworkWriter;
import nl.uu.socnetid.netgame.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportEdgeListPanel extends ExportCSVPanel {

    private static final long serialVersionUID = -7700061908878468554L;

    /**
     * Create the panel.
     *
     * @param network
     *          the network to write
     */
    public ExportEdgeListPanel(Network network) {
        super(network);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.ExportCSVPanel#getNetworkWriter()
     */
    @Override
    protected NetworkWriter getNetworkWriter() {
        return new EdgeListWriter();
    }

}
