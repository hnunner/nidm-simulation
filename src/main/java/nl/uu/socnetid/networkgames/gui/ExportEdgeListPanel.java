package nl.uu.socnetid.networkgames.gui;

import nl.uu.socnetid.networkgames.network.io.EdgeListWriter;
import nl.uu.socnetid.networkgames.network.io.NetworkWriter;
import nl.uu.socnetid.networkgames.network.networks.Network;

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
     * @see nl.uu.socnetid.networkgames.gui.ExportCSVPanel#getNetworkWriter()
     */
    @Override
    protected NetworkWriter getNetworkWriter() {
        return new EdgeListWriter();
    }

}
