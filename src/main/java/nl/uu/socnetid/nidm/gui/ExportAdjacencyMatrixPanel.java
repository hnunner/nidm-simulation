package nl.uu.socnetid.nidm.gui;

import nl.uu.socnetid.nidm.io.AdjacencyMatrixWriter;
import nl.uu.socnetid.nidm.io.NetworkWriter;
import nl.uu.socnetid.nidm.networks.Network;

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
     * @see nl.uu.socnetid.nidm.gui.ExportCSVPanel#getNetworkWriter()
     */
    @Override
    protected NetworkWriter getNetworkWriter() {
        return new AdjacencyMatrixWriter();
    }

}
