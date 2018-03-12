package nl.uu.socnetid.networkgames.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import nl.uu.socnetid.networkgames.io.NetworkFileWriter;
import nl.uu.socnetid.networkgames.io.NetworkWriter;
import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public abstract class ExportCSVPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -6424805545966403618L;

    // components
    private JButton btnExport;

    // network
    private Network network;


    /**
     * Create the panel.
     *
     * @param network
     *          the network to export
     */
    protected ExportCSVPanel(Network network) {
        this.network = network;

        setLayout(null);

        btnExport = new JButton("Export");
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportStaticNetwork();
            }
        });
        btnExport.setBounds(6, 6, 205, 30);
        add(btnExport);
    }

    /**
     * Exports the current network as static GEXF files.
     */
    private void exportStaticNetwork() {
        JFileChooser fileChooser = new JFileChooser();
        int popdownState = fileChooser.showSaveDialog(null);
        if (popdownState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String file = selectedFile.getPath();
            NetworkWriter networkWriter = getNetworkWriter();
            String filePath = file.replace(selectedFile.getName(), "");
            NetworkFileWriter fileWriter = new NetworkFileWriter(filePath, file, networkWriter, this.network);
            fileWriter.write();
        }
    }

    /**
     * Gets the network writer according to the selected export type.
     *
     * @return the network writer
     */
    protected abstract NetworkWriter getNetworkWriter();

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.btnExport.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.btnExport.setEnabled(false);
    }
}
