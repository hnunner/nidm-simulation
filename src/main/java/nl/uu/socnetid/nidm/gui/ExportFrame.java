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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportFrame extends JFrame implements ExportListener {

    private static final long serialVersionUID = 7342411057803731515L;

    // network
    private Network network;

    // selection
    private JComboBox<String> exportCBox;
    private final String[] networkWriters = {"GEXF", "Edge List", "Adjacency Matrix"};

    // panels
    private ExportGEXFPanel gexfPanel;
    private ExportEdgeListPanel edgeListPanel;
    private ExportAdjacencyMatrixPanel adjacencyMatrixPanel;


    /**
     * Constructor with title settings.
     *
     * @param title
     *          the title of the window
     * @param network
     *          the network to be exported
     */
    public ExportFrame(String title, Network network) {
        super(title);
        this.network = network;
        initialize();
    }

    /**
     * Initializes the export frame.
     */
    private void initialize() {

        this.setBounds(10, 590, 280, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(25, 10, 231, 240);
        getContentPane().add(panel);

        exportCBox = new JComboBox<String>();
        exportCBox.setBounds(6, 6, 215, 30);
        for (int i = 0; i < networkWriters.length; i++) {
            exportCBox.addItem(networkWriters[i]);
        }
        exportCBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (exportCBox.getSelectedIndex()) {
                    case 0:
                        gexfPanel.setVisible(true);
                        edgeListPanel.setVisible(false);
                        adjacencyMatrixPanel.setVisible(false);
                        break;

                    case 1:
                        gexfPanel.setVisible(false);
                        edgeListPanel.setVisible(true);
                        adjacencyMatrixPanel.setVisible(false);
                        break;

                    case 2:
                        gexfPanel.setVisible(false);
                        edgeListPanel.setVisible(false);
                        adjacencyMatrixPanel.setVisible(true);
                        break;

                    default:
                        throw new RuntimeException("Undefined export type!");
                }
            }
        });
        panel.add(exportCBox);

        gexfPanel = new ExportGEXFPanel(this.network);
        gexfPanel.setBounds(6, 34, 214, 192);
        gexfPanel.setVisible(true);
        gexfPanel.addExportListener(this);
        panel.add(gexfPanel);

        adjacencyMatrixPanel = new ExportAdjacencyMatrixPanel(this.network);
        adjacencyMatrixPanel.setBounds(6, 34, 214, 192);
        adjacencyMatrixPanel.setVisible(false);
        panel.add(adjacencyMatrixPanel);

        edgeListPanel = new ExportEdgeListPanel(this.network);
        edgeListPanel.setBounds(6, 34, 214, 192);
        edgeListPanel.setVisible(false);
        panel.add(edgeListPanel);
    }


    /**
     * Adds an {@link ExportListener} for export callbacks.
     *
     * @param exportListener
     *          the {@link ExportListener} to be notified
     */
    public void addExportListener(ExportListener exportListener) {
        this.gexfPanel.addExportListener(exportListener);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.ExportListener#notifyRecordingStarted()
     */
    @Override
    public void notifyRecordingStarted() {
        this.exportCBox.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.ExportListener#notifyRecordingStopped()
     */
    @Override
    public void notifyRecordingStopped() {
        this.exportCBox.setEnabled(true);
    }


}
