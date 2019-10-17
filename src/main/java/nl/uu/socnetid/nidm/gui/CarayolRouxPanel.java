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
public class CarayolRouxPanel extends DeactivatablePanel implements ChangeListener {

    private static final long serialVersionUID = -2266756139159852784L;

//    private DoubleJFormattedTextField txtOmega;
    private JSlider sliderOmega;
    private DoubleJFormattedTextField txtOmegaSlider;
//    private DoubleJFormattedTextField txtDelta;
    private JSlider sliderDelta;
    private DoubleJFormattedTextField txtDeltaSlider;
//    private DoubleJFormattedTextField txtC;
    private JSlider sliderC;
    private DoubleJFormattedTextField txtCSlider;
    private DoubleJFormattedTextField txtSigma;
    private DoubleJFormattedTextField txtGamma;
    private DoubleJFormattedTextField txtRSigma;
    private DoubleJFormattedTextField txtRPi;
    private DoubleJFormattedTextField txtPhi;
    private JFormattedTextField txtTau;


    // INPUT VALIDATION
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();
    // benefit of direct connections (alpha), benefit of indirect connections (beta), maintenance costs (c)
//    private static final InputVerifier REAL_NUMBERS_VERIFIER = new DoubleInputVerifier();
    // discounts for delta
//    private static final InputVerifier DELTA_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
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
    private final Set<CarayolRouxChangeListener> changeListeners = new CopyOnWriteArraySet<CarayolRouxChangeListener>();

    /**
     * Create the panel.
     */
    public CarayolRouxPanel() {
        setLayout(null);

        JLabel lblSocialBenefits = new JLabel("Social benefits:");
        lblSocialBenefits.setBounds(13, 0, 242, 16);
        add(lblSocialBenefits);
        lblSocialBenefits.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblB11 = new JLabel("Base value");
        lblB11.setBounds(36, 30, 86, 16);
        add(lblB11);

        JLabel lblB12 = new JLabel("(ω):");
        lblB12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB12.setBounds(200, 30, 35, 16);
        add(lblB12);

//        txtOmega = new DoubleJFormattedTextField(NUM_FORMAT);
//        txtOmega.setBounds(245, 28, 50, 20);
//        add(txtOmega);
//        txtOmega.setHorizontalAlignment(SwingConstants.RIGHT);
//        txtOmega.setColumns(10);
//        txtOmega.setValue(new Double(1.0));
//        txtOmega.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JLabel lblB21 = new JLabel("Geodesic discount");
        lblB21.setBounds(36, 68, 154, 16);
        add(lblB21);

        JLabel lblB22 = new JLabel("(δ):");
        lblB22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB22.setBounds(200, 68, 35, 16);
        add(lblB22);

//        txtDelta = new DoubleJFormattedTextField(NUM_FORMAT);
//        txtDelta.setBounds(245, 53, 50, 20);
//        add(txtDelta);
//        txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
//        txtDelta.setColumns(10);
//        txtDelta.setValue(new Double(0.1));
//        txtDelta.setInputVerifier(DELTA_VERIFIER);

        JLabel lblC11 = new JLabel("Geographic costs");
        lblC11.setBounds(36, 148, 154, 16);
        add(lblC11);

        JLabel lblC12 = new JLabel("(c):");
        lblC12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC12.setBounds(200, 148, 35, 16);
        add(lblC12);

//        txtC = new DoubleJFormattedTextField(NUM_FORMAT);
//        txtC.setBounds(245, 123, 50, 20);
//        add(txtC);
//        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
//        txtC.setColumns(10);
//        txtC.setValue(new Double(1.0));
        //txtC.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 108, 312, 10);
        add(separator);

        JLabel lblSocialCostsHeader = new JLabel("Social maintenance costs:");
        lblSocialCostsHeader.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCostsHeader.setBounds(13, 118, 242, 16);
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

        sliderOmega = new JSlider();
        sliderOmega.setBounds(22, 40, 285, 29);
        sliderOmega.setMaximum(200);
        sliderOmega.setMinimum(0);
        sliderOmega.setValue(30);
        add(sliderOmega);
        sliderOmega.addChangeListener(this);

        txtOmegaSlider = new DoubleJFormattedTextField((NumberFormat) null);
        txtOmegaSlider.setHorizontalAlignment(SwingConstants.RIGHT);
        txtOmegaSlider.setBounds(245, 28, 50, 20);
        txtOmegaSlider.setText(Double.toString(sliderOmega.getValue() / 100.0));
        add(txtOmegaSlider);

        sliderDelta = new JSlider();
        sliderDelta.setBounds(22, 78, 285, 29);
        sliderDelta.setMaximum(100);
        sliderDelta.setMinimum(0);
        sliderDelta.setValue(15);
        add(sliderDelta);
        sliderDelta.addChangeListener(this);

        txtDeltaSlider = new DoubleJFormattedTextField((NumberFormat) null);
        txtDeltaSlider.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDeltaSlider.setBounds(245, 66, 50, 20);
        txtDeltaSlider.setText(Double.toString(sliderDelta.getValue() / 100.0));
        add(txtDeltaSlider);

        sliderC = new JSlider();
        sliderC.setBounds(22, 158, 285, 29);
        sliderC.setMaximum(200);
        sliderC.setMinimum(0);
        sliderC.setValue(100);
        add(sliderC);
        sliderC.addChangeListener(this);

        txtCSlider = new DoubleJFormattedTextField((NumberFormat) null);
        txtCSlider.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCSlider.setBounds(245, 146, 50, 20);
        txtCSlider.setText(Double.toString(sliderC.getValue() / 100.0));
        add(txtCSlider);
    }

    /**
     * Gets the benefits of connections (omega).
     *
     * @return the benefits of direct connections (omega)
     */
    public double getOmega() {
        return this.txtOmegaSlider.getDouble();
    }

    /**
     * Gets the distance dependent decay of benefits of connections (delta).
     *
     * @return the distance dependent decay of benefits of connections (delta)
     */
    public double getDelta() {
        return this.txtDeltaSlider.getDouble();
    }

    /**
     * Gets the costs of connections (c).
     *
     * @return the costs of connections (c)
     */
    public double getC() {
        return this.txtCSlider.getDouble();
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
//        this.txtOmega.setEnabled(true);
//        this.txtDelta.setEnabled(true);
//        this.txtC.setEnabled(true);
//        this.txtSigma.setEnabled(true);
//        this.txtGamma.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
//        this.txtOmega.setEnabled(false);
//        this.txtDelta.setEnabled(false);
//        this.txtC.setEnabled(false);
//        this.txtSigma.setEnabled(false);
//        this.txtGamma.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent e)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        if (source.equals(this.sliderOmega)) {
            txtOmegaSlider.setText(Double.toString(source.getValue() / 100.0));
            notifyOmegaChanged();
        }

        if (source.equals(this.sliderDelta)) {
            txtDeltaSlider.setText(Double.toString(source.getValue() / 100.0));
            notifyDeltaChanged();
        }

        if (source.equals(this.sliderC)) {
            txtCSlider.setText(Double.toString(source.getValue() / 100.0));
            notifyCChanged();
        }
    }


    /**
     * Adds a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be added
     */
    public void addParameterChangeListener(CarayolRouxChangeListener parameterChangeListener) {
        this.changeListeners.add(parameterChangeListener);
    }

    /**
     * Removes a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be removed
     */
    public void removeParameterChangeListener(CarayolRouxChangeListener parameterChangeListener) {
        this.changeListeners.remove(parameterChangeListener);
    }

    /**
     * Notifies listeners of changed omega.
     */
    private final void notifyOmegaChanged() {
        Iterator<CarayolRouxChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyOmegaChanged();
        }
    }

    /**
     * Notifies listeners of changed delta.
     */
    private final void notifyDeltaChanged() {
        Iterator<CarayolRouxChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyDeltaChanged();
        }
    }

    /**
     * Notifies listeners of changed c.
     */
    private final void notifyCChanged() {
        Iterator<CarayolRouxChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyCChanged();
        }
    }

}
