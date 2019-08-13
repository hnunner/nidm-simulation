package nl.uu.socnetid.nidm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import nl.uu.socnetid.nidm.io.network.NetworkFileWriter;
import nl.uu.socnetid.nidm.io.network.NetworkWriter;
import nl.uu.socnetid.nidm.networks.Network;

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
        btnExport.setIcon(new ImageIcon(getClass().getResource("/save.png")));
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportStaticNetwork();
            }
        });
        btnExport.setBounds(37, 0, 258, 30);
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
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.btnExport.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.btnExport.setEnabled(false);
    }
}
