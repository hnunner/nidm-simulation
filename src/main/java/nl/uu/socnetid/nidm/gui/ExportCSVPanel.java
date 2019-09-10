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
