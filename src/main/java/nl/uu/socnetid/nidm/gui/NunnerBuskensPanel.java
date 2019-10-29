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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensPanel extends DeactivatablePanel implements ChangeListener {

    private static final long serialVersionUID = -975419060163534029L;

    private JSlider sliderB1;
    private DoubleJFormattedTextField txtSliderB1;
    private JSlider sliderC1;
    private DoubleJFormattedTextField txtSliderC1;
    private JSlider sliderC2;
    private DoubleJFormattedTextField txtSliderC2;
    private JSlider sliderB2;
    private DoubleJFormattedTextField txtSliderB2;
    private JSlider sliderAlpha;
    private DoubleJFormattedTextField txtSliderAlpha;
    private DoubleJFormattedTextField txtSigma;
    private DoubleJFormattedTextField txtGamma;
    private DoubleJFormattedTextField txtRSigma;
    private DoubleJFormattedTextField txtRPi;
    private DoubleJFormattedTextField txtPhi;
    private JFormattedTextField txtTau;

    // INPUT VALIDATION
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();
    // benefit of direct connections (alpha), benefit of indirect connections (beta), maintenance costs (c)
    // private static final InputVerifier REAL_NUMBERS_VERIFIER = new DoubleInputVerifier();
    // discounts for infected ties (kappa, lamda)
    //private static final InputVerifier DISCOUNT_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // care factor for infected direct connections (mu)
    //private static final InputVerifier MU_VERIFIER = new DoubleInputVerifier(1.0, null);
    // disease severity (sigma)
    private static final InputVerifier SIGMA_VERIFIER = new DoubleInputVerifier(1.001, null);
    // probability of infections (gamma)
    private static final InputVerifier GAMMA_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // risk perceptions (r_sigma, r_pi)
    private static final InputVerifier R_VERIFIER = new DoubleInputVerifier(0.0, 2.0);
    // share of peers to evaluate per time step (phi)
    private static final InputVerifier PHI_VERIFIER = new DoubleInputVerifier(0.001, 1.0);
    // time steps to recover (tau)
    private static final InputVerifier TAU_VERIFIER = new IntegerInputVerifier(1, null);

    // listeners
    private final Set<NunnerBuskensChangeListener> changeListeners = new CopyOnWriteArraySet<NunnerBuskensChangeListener>();


    /**
     * Create the panel.
     */
    public NunnerBuskensPanel() {
        setLayout(null);

        JLabel lblSocialBenefits = new JLabel("Social benefits:");
        lblSocialBenefits.setBounds(13, 0, 242, 16);
        add(lblSocialBenefits);
        lblSocialBenefits.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblB11 = new JLabel("Direct ties");
        lblB11.setBounds(36, 30, 86, 16);
        add(lblB11);

        JLabel lblB12 = new JLabel("(b  ):");
        lblB12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB12.setBounds(200, 30, 35, 16);
        add(lblB12);

        JLabel lblB21 = new JLabel("Benefit");
        lblB21.setBounds(27, 224, 154, 16);
        add(lblB21);

        JLabel lblB22 = new JLabel("(b  ):");
        lblB22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB22.setBounds(191, 224, 35, 16);
        add(lblB22);

        JLabel lblC11 = new JLabel("Standard");
        lblC11.setBounds(36, 111, 86, 16);
        add(lblC11);

        JLabel lblC12 = new JLabel("(c  ):");
        lblC12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC12.setBounds(200, 111, 35, 16);
        add(lblC12);

        JLabel lblC21 = new JLabel("Quadratic");
        lblC21.setBounds(36, 149, 154, 16);
        add(lblC21);

        JLabel lblC22 = new JLabel("(c  ):");
        lblC22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC22.setBounds(200, 149, 35, 16);
        add(lblC22);

        JLabel lblAlpha1 = new JLabel("Open vs. closed preference");
        lblAlpha1.setBounds(28, 258, 170, 16);
        add(lblAlpha1);

        JLabel lblAlpha2 = new JLabel("(α):");
        lblAlpha2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAlpha2.setBounds(192, 258, 35, 16);
        add(lblAlpha2);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 72, 312, 10);
        add(separator);

        JLabel lblSocialCostsHeader = new JLabel("Social maintenance costs:");
        lblSocialCostsHeader.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCostsHeader.setBounds(13, 81, 242, 16);
        add(lblSocialCostsHeader);

        JSeparator separator_3 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_3.setForeground(Color.LIGHT_GRAY);
        separator_3.setBounds(1, 297, 312, 10);
        add(separator_3);

        JLabel lblPotentialHarmOf = new JLabel("Potential harm of infections:");
        lblPotentialHarmOf.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblPotentialHarmOf.setBounds(14, 307, 242, 16);
        add(lblPotentialHarmOf);

        JLabel lblSigma1 = new JLabel("Disease severity");
        lblSigma1.setBounds(37, 337, 152, 16);
        add(lblSigma1);

        JLabel lblGamma1 = new JLabel("Probability of infection");
        lblGamma1.setBounds(37, 362, 170, 16);
        add(lblGamma1);

        JLabel lblSigma2 = new JLabel("(σ):");
        lblSigma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSigma2.setBounds(201, 337, 35, 16);
        add(lblSigma2);

        txtSigma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSigma.setColumns(10);
        txtSigma.setBounds(246, 335, 50, 20);
        add(txtSigma);
        txtSigma.setValue(new Double(50));
        txtSigma.setInputVerifier(SIGMA_VERIFIER);

        JLabel lblGamma2 = new JLabel("(γ):");
        lblGamma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGamma2.setBounds(201, 362, 35, 16);
        add(lblGamma2);

        txtGamma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setColumns(10);
        txtGamma.setBounds(246, 360, 50, 20);
        add(txtGamma);
        txtGamma.setValue(new Double(0.1));
        txtGamma.setInputVerifier(GAMMA_VERIFIER);


        JLabel lblRiskPerception = new JLabel("Risk perception:");
        lblRiskPerception.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblRiskPerception.setBounds(37, 392, 242, 16);
        add(lblRiskPerception);

        JLabel lblDiseaseSeverity = new JLabel("Disease severity");
        lblDiseaseSeverity.setBounds(53, 422, 153, 16);
        add(lblDiseaseSeverity);

        txtRSigma = new DoubleJFormattedTextField(NUM_FORMAT);
        txtRSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRSigma.setColumns(10);
        txtRSigma.setBounds(246, 420, 50, 20);
        add(txtRSigma);
        txtRSigma.setValue(new Double(1.0));
        txtRSigma.setInputVerifier(R_VERIFIER);

        JLabel lblProbabilityOfInfection = new JLabel("Probability of infection");
        lblProbabilityOfInfection.setBounds(53, 447, 154, 16);
        add(lblProbabilityOfInfection);

        txtRPi = new DoubleJFormattedTextField(NUM_FORMAT);
        txtRPi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRPi.setColumns(10);
        txtRPi.setBounds(246, 445, 50, 20);
        add(txtRPi);
        txtRPi.setValue(new Double(1.0));
        txtRPi.setInputVerifier(R_VERIFIER);

        JSeparator separator_4 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_4.setForeground(Color.WHITE);
        separator_4.setBounds(36, 385, 260, 10);
        add(separator_4);

        JLabel label = new JLabel("(r  ):");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(201, 422, 35, 16);
        add(label);

        JLabel label_1 = new JLabel("π");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_1.setBounds(219, 455, 7, 10);
        add(label_1);

        JLabel label_2 = new JLabel("(r  ):");
        label_2.setHorizontalAlignment(SwingConstants.RIGHT);
        label_2.setBounds(201, 447, 35, 16);
        add(label_2);

        JLabel label_3 = new JLabel("σ");
        label_3.setHorizontalAlignment(SwingConstants.RIGHT);
        label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_3.setBounds(219, 430, 7, 10);
        add(label_3);

        JLabel lblNetworkEvaluation = new JLabel("Network - evaluation (per agent):");
        lblNetworkEvaluation.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkEvaluation.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkEvaluation.setBounds(14, 488, 238, 16);
        add(lblNetworkEvaluation);

        JLabel label_9 = new JLabel("% of network per time step");
        label_9.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_9.setBounds(38, 518, 177, 16);
        add(label_9);

        JLabel label_10 = new JLabel("(ϕ):");
        label_10.setHorizontalAlignment(SwingConstants.RIGHT);
        label_10.setBounds(202, 518, 35, 16);
        add(label_10);

        txtPhi = new DoubleJFormattedTextField(NUM_FORMAT);
        txtPhi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPhi.setColumns(10);
        txtPhi.setBounds(246, 516, 50, 20);
        add(txtPhi);
        txtPhi.setValue(new Double(0.4));
        txtPhi.setInputVerifier(PHI_VERIFIER);

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(1, 478, 312, 10);
        add(separator_5);

        JSeparator separator_6 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_6.setForeground(Color.LIGHT_GRAY);
        separator_6.setBounds(1, 549, 312, 10);
        add(separator_6);

        JLabel lblSimulation = new JLabel("Simulation:");
        lblSimulation.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblSimulation.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSimulation.setBounds(13, 559, 238, 16);
        add(lblSimulation);

        JLabel label_5 = new JLabel("Time steps to recover");
        label_5.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_5.setBounds(37, 589, 177, 16);
        add(label_5);

        JLabel label_6 = new JLabel("(τ):");
        label_6.setHorizontalAlignment(SwingConstants.RIGHT);
        label_6.setBounds(201, 589, 35, 16);
        add(label_6);

        txtTau = new JFormattedTextField(NUM_FORMAT);
        txtTau.setText("10");
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setColumns(10);
        txtTau.setBounds(245, 587, 50, 20);
        add(txtTau);
        txtTau.setValue(new Integer(10));
        txtTau.setInputVerifier(TAU_VERIFIER);

        JLabel label_4 = new JLabel("1");
        label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_4.setHorizontalAlignment(SwingConstants.LEFT);
        label_4.setBounds(220, 37, 13, 16);
        add(label_4);

        JLabel label_7 = new JLabel("1");
        label_7.setHorizontalAlignment(SwingConstants.LEFT);
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_7.setBounds(220, 118, 13, 16);
        add(label_7);

        JLabel lblB22_sub = new JLabel("2");
        lblB22_sub.setHorizontalAlignment(SwingConstants.LEFT);
        lblB22_sub.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        lblB22_sub.setBounds(211, 231, 13, 16);
        add(lblB22_sub);

        JLabel label_11 = new JLabel("2");
        label_11.setHorizontalAlignment(SwingConstants.LEFT);
        label_11.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_11.setBounds(220, 156, 13, 16);
        add(label_11);

        sliderB1 = new JSlider();
        sliderB1.setBounds(22, 40, 285, 29);
        sliderB1.setMaximum(200);
        sliderB1.setMinimum(0);
        sliderB1.setValue(100);
        add(sliderB1);
        sliderB1.addChangeListener(this);

        txtSliderB1 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderB1.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderB1.setBounds(245, 28, 50, 20);
        txtSliderB1.setValue(Double.toString(sliderB1.getValue() / 100.0));
        txtSliderB1.setEnabled(false);
        add(txtSliderB1);

        sliderB2 = new JSlider();
        sliderB2.setBounds(13, 234, 285, 29);
        sliderB2.setMaximum(500);
        sliderB2.setMinimum(-500);
        sliderB2.setValue(0);
        add(sliderB2);
        sliderB2.addChangeListener(this);

        txtSliderB2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderB2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderB2.setBounds(236, 222, 50, 20);
        txtSliderB2.setValue(Double.toString(sliderB2.getValue() / 100.0));
        txtSliderB2.setEnabled(false);
        add(txtSliderB2);

        sliderC1 = new JSlider();
        sliderC1.setBounds(21, 121, 285, 29);
        sliderC1.setMaximum(100);
        sliderC1.setMinimum(0);
        sliderC1.setValue(20);
        add(sliderC1);
        sliderC1.addChangeListener(this);

        txtSliderC1 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC1.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC1.setBounds(246, 109, 50, 20);
        txtSliderC1.setValue(Double.toString(sliderC1.getValue() / 100.0));
        txtSliderC1.setEnabled(false);
        add(txtSliderC1);

        sliderC2 = new JSlider();
        sliderC2.setBounds(21, 161, 285, 29);
        sliderC2.setMaximum(100);
        sliderC2.setMinimum(0);
        sliderC2.setValue(10);
        add(sliderC2);
        sliderC2.addChangeListener(this);

        txtSliderC2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC2.setBounds(245, 149, 50, 20);
        txtSliderC2.setValue(Double.toString(sliderC2.getValue() / 100.0));
        txtSliderC2.setEnabled(false);
        add(txtSliderC2);

        sliderAlpha = new JSlider();
        sliderAlpha.setBounds(13, 271, 285, 29);
        sliderAlpha.setMaximum(100);
        sliderAlpha.setMinimum(0);
        sliderAlpha.setValue(50);
        add(sliderAlpha);
        sliderAlpha.addChangeListener(this);

        txtSliderAlpha = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderAlpha.setBounds(237, 256, 50, 20);
        txtSliderAlpha.setValue(Double.toString(sliderAlpha.getValue() / 100.0));
        txtSliderAlpha.setEnabled(false);
        add(txtSliderAlpha);

        JLabel lblTriadicClosure = new JLabel("Triadic closure:");
        lblTriadicClosure.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblTriadicClosure.setBounds(13, 202, 242, 16);
        add(lblTriadicClosure);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.LIGHT_GRAY);
        separator_1.setBounds(0, 194, 312, 10);
        add(separator_1);
    }

    /**
     * Gets the benefits of a direct connection (b1).
     *
     * @return the benefits of a direct connection (b1)
     */
    public double getB1() {
        return this.txtSliderB1.getDouble();
    }

    /**
     * Gets the normal costs of direct connections (c1).
     *
     * @return the normal costs of direct connections (c1)
     */
    public double getC1() {
        return this.txtSliderC1.getDouble();
    }

    /**
     * Gets the quadratic costs of direct connections (c2).
     *
     * @return the quadratic costs of direct connections (c2)
     */
    public double getC2() {
        return this.txtSliderC2.getDouble();
    }

    /**
     * Gets the weight of benefits for triads (b2).
     *
     * @return the weight of benefits for triads (b2)
     */
    public double getB2() {
        return this.txtSliderB2.getDouble();
    }

    /**
     * Gets the preference shift between open and closed triads (alpha).
     *
     * @return the preference shift between open and closed triads (alpha)
     */
    public double getAlpha() {
        return this.txtSliderAlpha.getDouble();
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
        this.txtSliderB1.setEnabled(true);
        this.txtSliderC1.setEnabled(true);
        this.txtSliderC2.setEnabled(true);
        this.txtSliderB2.setEnabled(true);
        this.txtSliderAlpha.setEnabled(true);
        this.txtSigma.setEnabled(true);
        this.txtGamma.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtSliderB1.setEnabled(false);
        this.txtSliderC1.setEnabled(false);
        this.txtSliderC2.setEnabled(false);
        this.txtSliderB2.setEnabled(false);
        this.txtSliderAlpha.setEnabled(false);
        this.txtSigma.setEnabled(false);
        this.txtGamma.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent e)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        if (source.equals(this.sliderB1)) {
            this.txtSliderB1.setText(Double.toString(source.getValue() / 100.0));
            notifyB1Changed();
        }

        if (source.equals(this.sliderB2)) {
            this.txtSliderB2.setText(Double.toString(source.getValue() / 100.0));
            notifyB2Changed();
        }

        if (source.equals(this.sliderC1)) {
            this.txtSliderC1.setText(Double.toString(source.getValue() / 100.0));
            notifyC1Changed();
        }

        if (source.equals(this.sliderC2)) {
            this.txtSliderC2.setText(Double.toString(source.getValue() / 100.0));
            notifyC2Changed();
        }

        if (source.equals(this.sliderAlpha)) {
            this.txtSliderAlpha.setText(Double.toString(source.getValue() / 100.0));
            notifyAlphaChanged();
        }
    }


    /**
     * Adds a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be added
     */
    public void addParameterChangeListener(NunnerBuskensChangeListener parameterChangeListener) {
        this.changeListeners.add(parameterChangeListener);
    }

    /**
     * Removes a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be removed
     */
    public void removeParameterChangeListener(NunnerBuskensChangeListener parameterChangeListener) {
        this.changeListeners.remove(parameterChangeListener);
    }

    /**
     * Notifies listeners of changed b1.
     */
    private final void notifyB1Changed() {
        Iterator<NunnerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyB1Changed();
        }
    }

    /**
     * Notifies listeners of changed b2.
     */
    private final void notifyB2Changed() {
        Iterator<NunnerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyB2Changed();
        }
    }

    /**
     * Notifies listeners of changed c1.
     */
    private final void notifyC1Changed() {
        Iterator<NunnerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyC1Changed();
        }
    }

    /**
     * Notifies listeners of changed c2.
     */
    private final void notifyC2Changed() {
        Iterator<NunnerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyC2Changed();
        }
    }

    /**
     * Notifies listeners of changed alpha.
     */
    private final void notifyAlphaChanged() {
        Iterator<NunnerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAlphaChanged();
        }
    }

}
