package nl.uu.socnetid.network_games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import nl.uu.socnetid.network_games.network.writer.AdjacencyMatrixWriter;
import nl.uu.socnetid.network_games.network.writer.EdgeListWriter;
import nl.uu.socnetid.network_games.network.writer.NetworkOutputType;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;

/**
 * @author Hendrik Nunner
 */
public class VisualOutputExportPanel extends JPanel {

    private static final long serialVersionUID = 8666500343794536885L;

    // listener
    private final Set<ExportNetworkListener> exportNetworkListeners = new CopyOnWriteArraySet<ExportNetworkListener>();

    // edge writer combo box and selection
    private JComboBox<String> edgeWriterCBox;

    /**
     * Create the panel.
     */
    public VisualOutputExportPanel() {
        setLayout(null);

        edgeWriterCBox = new JComboBox<String>();
        edgeWriterCBox.setBounds(6, 6, 154, 27);
        add(edgeWriterCBox);

        for (NetworkOutputType type : NetworkOutputType.values()) {
            edgeWriterCBox.addItem(type.toString());
        }

        JButton btnNewButton = new JButton("Export");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyExportNetworkListeners();
            }
        });
        btnNewButton.setBounds(6, 45, 154, 29);
        add(btnNewButton);
    }

    /**
     * Adds a listener to be notified when network is ought to be exported.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addAddListener(ExportNetworkListener listener) {
        this.exportNetworkListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeExportNetworkListener(ExportNetworkListener listener) {
        this.exportNetworkListeners.remove(listener);
    }

    /**
     * Notifies the listeners of added player.
     */
    private final void notifyExportNetworkListeners() {
        Iterator<ExportNetworkListener> listenersIt = this.exportNetworkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyExportNetwork(this);
        }
    }

    /**
     * Gets the selected type of network writer.
     *
     * @return the selected type of network writer
     */
    public NetworkWriter getSelectedNetworkWriter() {
        switch (edgeWriterCBox.getSelectedIndex()) {
            case 0:
                return new EdgeListWriter();

            case 1:
                return new AdjacencyMatrixWriter();

            default:
                throw new RuntimeException("Undefined network writer!");
        }
    }



}
