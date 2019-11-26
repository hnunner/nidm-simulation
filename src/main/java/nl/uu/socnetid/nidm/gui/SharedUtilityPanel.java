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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public abstract class SharedUtilityPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -975419060163534029L;

    private DoubleJFormattedTextField txtSigma;
    private DoubleJFormattedTextField txtGamma;
    private boolean rRandom;
    private JRadioButton rdbtnRRandomYes;
    private JRadioButton rdbtnRRandomNo;
    private DoubleJFormattedTextField txtRSigma;
    private DoubleJFormattedTextField txtRPi;
    private DoubleJFormattedTextField txtPhi;
    protected DoubleJFormattedTextField txtOmega;
    private JFormattedTextField txtTau;

    // INPUT VALIDATION
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();
    // disease severity (sigma)
    private static final InputVerifier SIGMA_VERIFIER = new DoubleInputVerifier(1.001, null);
    // probability of infections (gamma)
    private static final InputVerifier GAMMA_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // risk perceptions (r_sigma, r_pi)
    private static final InputVerifier R_VERIFIER = new DoubleInputVerifier(0.0, 2.0);
    // percentages
    private static final InputVerifier PERCENT_VERIFIER = new DoubleInputVerifier(0.001, 1.0);
    // time steps to recover (tau)
    private static final InputVerifier TAU_VERIFIER = new IntegerInputVerifier(1, null);



    /**
     * Create the panel.
     */
    public SharedUtilityPanel() {
        setLayout(null);

        JSeparator separator_3 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_3.setForeground(Color.LIGHT_GRAY);
        separator_3.setBounds(0, 323, 312, 10);
        add(separator_3);

        JLabel lblPotentialHarmOf = new JLabel("Infections:");
        lblPotentialHarmOf.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblPotentialHarmOf.setBounds(13, 333, 242, 16);
        add(lblPotentialHarmOf);

        JLabel lblSigma1 = new JLabel("Disease severity");
        lblSigma1.setBounds(36, 363, 152, 16);
        add(lblSigma1);

        JLabel lblGamma1 = new JLabel("Probability of infection");
        lblGamma1.setBounds(36, 388, 170, 16);
        add(lblGamma1);

        JLabel lblSigma2 = new JLabel("(σ):");
        lblSigma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSigma2.setBounds(200, 363, 35, 16);
        add(lblSigma2);

        txtSigma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSigma.setColumns(10);
        txtSigma.setBounds(245, 361, 50, 20);
        add(txtSigma);
        txtSigma.setValue(new Double(50));
        txtSigma.setInputVerifier(SIGMA_VERIFIER);

        JLabel lblGamma2 = new JLabel("(γ):");
        lblGamma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGamma2.setBounds(201, 388, 35, 16);
        add(lblGamma2);

        txtGamma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setColumns(10);
        txtGamma.setBounds(245, 386, 50, 20);
        add(txtGamma);
        txtGamma.setValue(new Double(0.1));
        txtGamma.setInputVerifier(GAMMA_VERIFIER);

        JLabel lblRiskPerception = new JLabel("Risk perception:");
        lblRiskPerception.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblRiskPerception.setBounds(36, 443, 242, 16);
        add(lblRiskPerception);

        JLabel lblDiseaseSeverity = new JLabel("Disease severity");
        lblDiseaseSeverity.setBounds(52, 473, 153, 16);
        add(lblDiseaseSeverity);

        txtRSigma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtRSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRSigma.setColumns(10);
        txtRSigma.setBounds(245, 471, 50, 20);
        add(txtRSigma);
        txtRSigma.setValue(new Double(1.0));
        txtRSigma.setInputVerifier(R_VERIFIER);

        JLabel lblProbabilityOfInfection = new JLabel("Probability of infection");
        lblProbabilityOfInfection.setBounds(52, 498, 154, 16);
        add(lblProbabilityOfInfection);

        txtRPi = new DoubleJFormattedTextField(NUM_FORMAT);
        txtRPi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRPi.setColumns(10);
        txtRPi.setBounds(245, 496, 50, 20);
        add(txtRPi);
        txtRPi.setValue(new Double(1.0));
        txtRPi.setInputVerifier(R_VERIFIER);

        JSeparator separator_4 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_4.setForeground(Color.WHITE);
        separator_4.setBounds(35, 436, 260, 10);
        add(separator_4);

        JLabel label = new JLabel("(r  ):");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(201, 473, 35, 16);
        add(label);

        JLabel label_1 = new JLabel("π");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_1.setBounds(219, 506, 7, 10);
        add(label_1);

        JLabel label_2 = new JLabel("(r  ):");
        label_2.setHorizontalAlignment(SwingConstants.RIGHT);
        label_2.setBounds(201, 498, 35, 16);
        add(label_2);

        JLabel label_3 = new JLabel("σ");
        label_3.setHorizontalAlignment(SwingConstants.RIGHT);
        label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_3.setBounds(219, 481, 7, 10);
        add(label_3);

        JLabel lblNetworkEvaluation = new JLabel("Network - evaluation (per agent):");
        lblNetworkEvaluation.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkEvaluation.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkEvaluation.setBounds(13, 536, 238, 16);
        add(lblNetworkEvaluation);

        JLabel label_9 = new JLabel("% of network per time step");
        label_9.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_9.setBounds(36, 566, 177, 16);
        add(label_9);

        JLabel label_10 = new JLabel("(ϕ):");
        label_10.setHorizontalAlignment(SwingConstants.RIGHT);
        label_10.setBounds(201, 566, 35, 16);
        add(label_10);

        txtPhi = new DoubleJFormattedTextField(NUM_FORMAT);
        txtPhi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPhi.setColumns(10);
        txtPhi.setBounds(245, 564, 50, 20);
        add(txtPhi);
        txtPhi.setValue(new Double(0.4));
        txtPhi.setInputVerifier(PERCENT_VERIFIER);

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(0, 526, 312, 10);
        add(separator_5);

        JLabel label_5 = new JLabel("Time steps to recover");
        label_5.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_5.setBounds(36, 413, 177, 16);
        add(label_5);

        JLabel label_6 = new JLabel("(τ):");
        label_6.setHorizontalAlignment(SwingConstants.RIGHT);
        label_6.setBounds(201, 413, 35, 16);
        add(label_6);

        txtTau = new JFormattedTextField(NUM_FORMAT);
        txtTau.setText("10");
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setColumns(10);
        txtTau.setBounds(245, 411, 50, 20);
        add(txtTau);
        txtTau.setValue(new Integer(10));
        txtTau.setInputVerifier(TAU_VERIFIER);

        JLabel lblAssortativity = new JLabel("% assortativity");
        lblAssortativity.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblAssortativity.setBounds(36, 591, 177, 16);
        add(lblAssortativity);

        JLabel label_7 = new JLabel("(ω):");
        label_7.setHorizontalAlignment(SwingConstants.RIGHT);
        label_7.setBounds(201, 591, 35, 16);
        add(label_7);

        txtOmega = new DoubleJFormattedTextField(NUM_FORMAT);
        txtOmega.setHorizontalAlignment(SwingConstants.RIGHT);
        txtOmega.setColumns(10);
        txtOmega.setBounds(245, 589, 50, 20);
        add(txtOmega);
        txtOmega.setValue(new Double(0.8));
        txtOmega.setInputVerifier(PERCENT_VERIFIER);

        rdbtnRRandomNo = new JRadioButton("manual");
        rdbtnRRandomNo.setBounds(150, 441, 75, 23);
        add(rdbtnRRandomNo);

        rdbtnRRandomYes = new JRadioButton("random");
        rdbtnRRandomYes.setSelected(true);
        rdbtnRRandomYes.setBounds(225, 441, 75, 23);
        add(rdbtnRRandomYes);

        ButtonGroup rRandomGroup = new ButtonGroup();
        rRandomGroup.add(rdbtnRRandomNo);
        rRandomGroup.add(rdbtnRRandomYes);

        rdbtnRRandomNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRRandom();
            }
        });

        rdbtnRRandomYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRRandom();
            }
        });

        updateRRandom();

    }


    /**
     * Gets the disease severity (sigma).
     *
     * @return the disease severity (sigma)
     */
    public double getSigma() {
        return this.txtSigma.getDouble();
    }

    /**
     * Gets the probability to get infected per contact (gamma).
     *
     * @return the probability to get infected per contact (gamma)
     */
    public double getGamma() {
        return this.txtGamma.getDouble();
    }

    /**
     * @return the rRandom
     */
    public boolean isRRandom() {
        return this.rRandom;
    }

    /**
     * Gets the risk perception for disease severity (rSigma).
     *
     * @return the risk perception for disease severity (rSigma)
     */
    public double getRSigma() {
        return this.txtRSigma.getDouble();
    }

    /**
     * Gets the risk perception for probability of infections (rPi).
     *
     * @return the risk perception for probability of infections (rPi)
     */
    public double getRPi() {
        return this.txtRPi.getDouble();
    }

    /**
     * Gets the share of peers to evaluate per time step (phi).
     *
     * @return the share of peers to evaluate per time step (phi)
     */
    public double getPhi() {
        return this.txtPhi.getDouble();
    }

    /**
     * Gets the share of peers to select assortatively (omega).
     *
     * @return the share of peers to select assortatively (omega)
     */
    public double getOmega() {
        return this.txtOmega.getDouble();
    }

    /**
     * Gets the number of time step to recover (tau).
     *
     * @return the number of time step to recover (tau)
     */
    public int getTau() {
        return Integer.valueOf(this.txtTau.getText());
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtSigma.setEnabled(true);
        this.txtGamma.setEnabled(true);
        this.txtRSigma.setEnabled(true);
        this.txtRPi.setEnabled(true);
        this.txtPhi.setEnabled(true);
        this.txtOmega.setEnabled(true);
        this.txtTau.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtSigma.setEnabled(false);
        this.txtGamma.setEnabled(false);
        this.txtRSigma.setEnabled(false);
        this.txtRPi.setEnabled(false);
        this.txtPhi.setEnabled(false);
        this.txtOmega.setEnabled(false);
        this.txtTau.setEnabled(false);
    }

    /**
     * Enables the selection of omega for share of peers selected assortatively.
     */
    public void enableAssortativity() {
        this.txtOmega.setEnabled(true);
    }

    /**
     * Disables the selection of omega for share of peers selected assortatively.
     */
    public void disableAssortativity() {
        this.txtOmega.setValue(new Double(0.0));
        this.txtOmega.setEnabled(false);
    }

    /**
     * Updates whether risk perception is added randomly.
     */
    private void updateRRandom() {
        this.rRandom = this.rdbtnRRandomYes.isSelected();
        this.txtRPi.setEnabled(!this.rRandom);
        this.txtRSigma.setEnabled(!this.rRandom);
    }

}
