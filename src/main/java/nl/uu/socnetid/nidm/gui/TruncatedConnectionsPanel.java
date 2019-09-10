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

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnectionsPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -1496000693509866694L;

    private JTextField txtDelta;
    private JTextField txtCosts;
    private JLabel label;
    private JLabel lblc;

    /**
     * Create the panel.
     */
    public TruncatedConnectionsPanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Benefit");
        lblBenefitdelta.setBounds(10, 5, 100, 16);
        add(lblBenefitdelta);

        txtDelta = new JTextField();
        txtDelta.setBounds(151, 3, 60, 20);
        txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDelta.setText("0.5");
        add(txtDelta);
        txtDelta.setColumns(10);

        JLabel lblCostsc = new JLabel("Costs");
        lblCostsc.setBounds(10, 28, 109, 16);
        add(lblCostsc);

        txtCosts = new JTextField();
        txtCosts.setBounds(151, 26, 60, 20);
        txtCosts.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCosts.setText("0.45");
        add(txtCosts);
        txtCosts.setColumns(10);

        label = new JLabel("(δ):");
        label.setBounds(127, 5, 24, 16);
        add(label);

        lblc = new JLabel("(c):");
        lblc.setBounds(127, 28, 24, 16);
        add(lblc);
    }

    /**
     * Gets the delta. That is, the benefit that deteriorates over distance.
     *
     * @return the delta
     */
    public double getDelta() {
        return Double.valueOf(this.txtDelta.getText());
    }

    /**
     * Gets the costs to maintain a direct connection.
     *
     * @return the costs to maintain a direct connection
     */
    public double getCosts() {
        return Double.valueOf(this.txtCosts.getText());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtDelta.setEnabled(true);
        this.txtCosts.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtDelta.setEnabled(false);
        this.txtCosts.setEnabled(false);
    }

}
