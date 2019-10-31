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
import java.text.NumberFormat;

import javax.swing.InputVerifier;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CidmPanel extends SharedUtilityPanel {

    private static final long serialVersionUID = -6334519672487731007L;

    private DoubleJFormattedTextField txtAlpha;
    private DoubleJFormattedTextField txtKappa;
    private DoubleJFormattedTextField txtBeta;
    private DoubleJFormattedTextField txtLamda;
    private DoubleJFormattedTextField txtC;
    private DoubleJFormattedTextField txtMu;

    // INPUT VALIDATION
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();
    // benefit of direct connections (alpha), benefit of indirect connections (beta), maintenance costs (c)
    private static final InputVerifier REAL_NUMBERS_VERIFIER = new DoubleInputVerifier();
    // discounts for infected ties (kappa, lamda)
    private static final InputVerifier DISCOUNT_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // care factor for infected direct connections (mu)
    private static final InputVerifier MU_VERIFIER = new DoubleInputVerifier(1.0, null);


    /**
     * Create the panel.
     */
    public CidmPanel() {
        setLayout(null);

        JLabel lblBenefitsOfConnections = new JLabel("Direct ties:");
        lblBenefitsOfConnections.setBounds(36, 30, 242, 16);
        add(lblBenefitsOfConnections);
        lblBenefitsOfConnections.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblAlpha1 = new JLabel("Benefit");
        lblAlpha1.setBounds(52, 60, 86, 16);
        add(lblAlpha1);

        JLabel lblAlpha2 = new JLabel("(α):");
        lblAlpha2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAlpha2.setBounds(200, 58, 35, 16);
        add(lblAlpha2);

        txtAlpha = new DoubleJFormattedTextField(NUM_FORMAT);
        txtAlpha.setBounds(245, 58, 50, 20);
        add(txtAlpha);
        txtAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAlpha.setColumns(10);
        txtAlpha.setValue(new Double(10));
        txtAlpha.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JLabel lblKappa1 = new JLabel("Discount for infected tie");
        lblKappa1.setBounds(52, 85, 154, 16);
        add(lblKappa1);

        JLabel lblKappa2 = new JLabel("(κ):");
        lblKappa2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblKappa2.setBounds(200, 85, 35, 16);
        add(lblKappa2);

        txtKappa = new DoubleJFormattedTextField(NUM_FORMAT);
        txtKappa.setBounds(245, 83, 50, 20);
        add(txtKappa);
        txtKappa.setHorizontalAlignment(SwingConstants.RIGHT);
        txtKappa.setColumns(10);
        txtKappa.setValue(new Double(1.0));
        txtKappa.setInputVerifier(DISCOUNT_VERIFIER);

        JLabel lblDiscountOfInfected_2 = new JLabel("Indirect ties:");
        lblDiscountOfInfected_2.setBounds(36, 115, 242, 16);
        add(lblDiscountOfInfected_2);
        lblDiscountOfInfected_2.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblBeta1 = new JLabel("Benefit");
        lblBeta1.setBounds(52, 145, 86, 16);
        add(lblBeta1);

        JLabel lblBeta2 = new JLabel("(β):");
        lblBeta2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBeta2.setBounds(200, 145, 35, 16);
        add(lblBeta2);

        txtBeta = new DoubleJFormattedTextField(NUM_FORMAT);
        txtBeta.setBounds(245, 143, 50, 20);
        add(txtBeta);
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setColumns(10);
        txtBeta.setValue(new Double(8));
        txtBeta.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JLabel lblLamda1 = new JLabel("Discount for infected tie");
        lblLamda1.setBounds(52, 170, 154, 16);
        add(lblLamda1);

        JLabel lblLamda2 = new JLabel("(λ):");
        lblLamda2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLamda2.setBounds(200, 170, 35, 16);
        add(lblLamda2);

        txtLamda = new DoubleJFormattedTextField(NUM_FORMAT);
        txtLamda.setBounds(245, 168, 50, 20);
        add(txtLamda);
        txtLamda.setHorizontalAlignment(SwingConstants.RIGHT);
        txtLamda.setColumns(10);
        txtLamda.setValue(new Double(1.0));
        txtLamda.setInputVerifier(DISCOUNT_VERIFIER);

        JLabel lblC1 = new JLabel("Direct ties");
        lblC1.setBounds(36, 241, 100, 16);
        add(lblC1);

        JLabel lblC2 = new JLabel("(c):");
        lblC2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC2.setBounds(200, 241, 35, 16);
        add(lblC2);

        txtC = new DoubleJFormattedTextField(NUM_FORMAT);
        txtC.setBounds(245, 239, 50, 20);
        add(txtC);
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);
        txtC.setValue(new Double(9));
        txtC.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 201, 312, 10);
        add(separator);

        JLabel lblSocialBenefits = new JLabel("Social benefits:");
        lblSocialBenefits.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialBenefits.setBounds(13, 0, 242, 16);
        add(lblSocialBenefits);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.WHITE);
        separator_1.setBounds(35, 23, 260, 10);
        add(separator_1);

        JSeparator separator_2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_2.setForeground(Color.WHITE);
        separator_2.setBounds(35, 108, 260, 10);
        add(separator_2);

        JLabel lblSocialCosts = new JLabel("Social maintenance costs:");
        lblSocialCosts.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCosts.setBounds(13, 211, 242, 16);
        add(lblSocialCosts);

        JLabel lblMu1 = new JLabel("Care factor for infected ties");
        lblMu1.setBounds(36, 266, 179, 16);
        add(lblMu1);

        JLabel lblMu2 = new JLabel("(μ):");
        lblMu2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMu2.setBounds(200, 266, 35, 16);
        add(lblMu2);

        txtMu = new DoubleJFormattedTextField(NUM_FORMAT);
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(245, 264, 50, 20);
        add(txtMu);
        txtMu.setValue(new Double(1.5));
        txtMu.setInputVerifier(MU_VERIFIER);
    }

    /**
     * Gets the benefit of a direct connection (alpha).
     *
     * @return the benefit of a direct connection (alpha)
     */
    public double getAlpha() {
        return this.txtAlpha.getDouble();
    }

    /**
     * Gets the discount for infected direct connections (kappa).
     *
     * @return the discount for infected direct connections (kappa)
     */
    public double getKappa() {
        return this.txtKappa.getDouble();
    }

    /**
     * Gets the benefit of an indirect connection (beta).
     *
     * @return the benefit of an indirect connection (beta)
     */
    public double getBeta() {
        return this.txtBeta.getDouble();
    }

    /**
     * Gets the discount for infected indirect connections (lamda).
     *
     * @return the discount for infected indirect connections (lamda)
     */
    public double getLamda() {
        return this.txtLamda.getDouble();
    }

    /**
     * Gets the costs for direct connections (c).
     *
     * @return the costs for direct connections (c)
     */
    public double getC() {
        return this.txtC.getDouble();
    }

    /**
     * Gets the care factor for infected direct connections (mu).
     *
     * @return the care factor for infected direct connections (mu)
     */
    public double getMu() {
        return this.txtMu.getDouble();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        super.enableComponents();
        this.txtAlpha.setEnabled(true);
        this.txtKappa.setEnabled(true);
        this.txtBeta.setEnabled(true);
        this.txtLamda.setEnabled(true);
        this.txtC.setEnabled(true);
        this.txtMu.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        super.diseableComponents();
        this.txtAlpha.setEnabled(false);
        this.txtKappa.setEnabled(false);
        this.txtBeta.setEnabled(false);
        this.txtLamda.setEnabled(false);
        this.txtC.setEnabled(false);
        this.txtMu.setEnabled(false);
    }
}
