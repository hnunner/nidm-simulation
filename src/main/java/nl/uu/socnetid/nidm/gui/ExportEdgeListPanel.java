package nl.uu.socnetid.nidm.gui;

import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.NetworkWriter;
import nl.uu.socnetid.nidm.networks.Network;

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
     * @see nl.uu.socnetid.nidm.gui.ExportCSVPanel#getNetworkWriter()
     */
    @Override
    protected NetworkWriter getNetworkWriter() {
        return new EdgeListWriter();
    }

}
