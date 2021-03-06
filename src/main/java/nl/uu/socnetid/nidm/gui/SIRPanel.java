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
public class SIRPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -6579761342558000359L;

    private JTextField txtTau;
    private JTextField txtS;
    private JTextField txtGamma;
    private JTextField txtMu;

    /**
     * Create the panel.
     */
    public SIRPanel() {
        setLayout(null);

        JLabel lblTau = new JLabel("Recovery time");
        lblTau.setToolTipText("The duration of the disease in rounds.");
        lblTau.setBounds(10, 5, 91, 16);
        add(lblTau);

        txtTau = new JTextField();
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setToolTipText("The duration of the disease in rounds.");
        txtTau.setText("10");
        txtTau.setBounds(151, 3, 60, 20);
        add(txtTau);
        txtTau.setColumns(10);

        JLabel lblS = new JLabel("Severity");
        lblS.setToolTipText("The punishment for an agent to be infected.");
        lblS.setBounds(10, 28, 65, 16);
        add(lblS);

        txtS = new JTextField();
        txtS.setHorizontalAlignment(SwingConstants.RIGHT);
        txtS.setToolTipText("The punishment for an agent to be infected.");
        txtS.setText("5");
        txtS.setBounds(151, 26, 60, 20);
        add(txtS);
        txtS.setColumns(10);

        JLabel lblGamma = new JLabel("Transmission rate");
        lblGamma.setToolTipText("The probability a disease is spread between an infected "
                + "and a non-infected agent per round.");
        lblGamma.setBounds(10, 51, 116, 16);
        add(lblGamma);

        txtGamma = new JTextField();
        txtGamma.setToolTipText("The probability a disease is spread between an infected "
                + "and a non-infected agent per round.");
        txtGamma.setText("0.1");
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setBounds(151, 49, 60, 20);
        add(txtGamma);
        txtGamma.setColumns(10);

        JLabel lblMu = new JLabel("Care factor");
        lblMu.setBounds(10, 74, 77, 16);
        add(lblMu);

        txtMu = new JTextField();
        txtMu.setText("2.5");
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(151, 72, 60, 20);
        add(txtMu);

        JLabel lblTau2 = new JLabel("(τ):");
        lblTau2.setBounds(127, 5, 24, 16);
        add(lblTau2);

        JLabel label = new JLabel("(s):");
        label.setBounds(127, 28, 24, 16);
        add(label);

        JLabel label_1 = new JLabel("(ɣ):");
        label_1.setBounds(127, 51, 24, 16);
        add(label_1);

        JLabel label_2 = new JLabel("(μ):");
        label_2.setBounds(127, 74, 24, 16);
        add(label_2);
    }



    /**
     * @return the duration a disease requires to recover from in rounds (tau)
     */
    public int getTau() {
        return Integer.valueOf(this.txtTau.getText());
    }

    /**
     * @return the severity of the disease represented by the amount of punishment for having a disease (s)
     */
    public double getS() {
        return Double.valueOf(this.txtS.getText());
    }

    /**
     * @return transmission rate (gamma) - the probability a disease is spread between
     *          an infected and a non-infected agent per round
     */
    public double getGamma() {
        return Double.valueOf(this.txtGamma.getText());
    }

    /**
     * @return the factor that increases maintenance costs for infected connections (mu)
     */
    public double getMu() {
        return Double.valueOf(this.txtMu.getText());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtTau.setEnabled(true);
        this.txtS.setEnabled(true);
        this.txtGamma.setEnabled(true);
        this.txtMu.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtTau.setEnabled(false);
        this.txtS.setEnabled(false);
        this.txtGamma.setEnabled(false);
        this.txtMu.setEnabled(false);
    }
}
