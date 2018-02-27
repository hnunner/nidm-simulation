package nl.uu.socnetid.networkgames.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import nl.uu.socnetid.networkgames.network.io.NetworkFileWriter;
import nl.uu.socnetid.networkgames.network.io.NetworkWriter;
import nl.uu.socnetid.networkgames.network.networks.Network;

/**
 * @author Hendrik Nunner
 */
public abstract class ExportCSVPanel extends DeactivatablePanel {

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
        if(popdownState == JFileChooser.APPROVE_OPTION) {
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


//    /**
//     * @return the duration a disease requires to recover from in rounds (tau)
//     */
//    public int getTau() {
//        return Integer.valueOf(this.txtTau.getText());
//    }
//
//    /**
//     * @return the severity of the disease represented by the amount of punishment for having a disease (delta)
//     */
//    public double getDelta() {
//        return Double.valueOf(this.txtDelta.getText());
//    }
//
//    /**
//     * @return transmission rate (gamma) - the probability a disease is spread between
//     *          an infected and a non-infected agent per round
//     */
//    public double getGamma() {
//        return Double.valueOf(this.txtGamma.getText());
//    }
//
//    /**
//     * @return the factor that increases maintenance costs for infected connections (mu)
//     */
//    public double getMu() {
//        return Double.valueOf(this.txtMu.getText());
//    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {

        // TODO implement


//        this.txtTau.setEnabled(true);
//        this.txtDelta.setEnabled(true);
//        this.txtGamma.setEnabled(true);
//        this.txtMu.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {

        // TODO implement


//        this.txtTau.setEnabled(false);
//        this.txtDelta.setEnabled(false);
//        this.txtGamma.setEnabled(false);
//        this.txtMu.setEnabled(false);
    }
}
