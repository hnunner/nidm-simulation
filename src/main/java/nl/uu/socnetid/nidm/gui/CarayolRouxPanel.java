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

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Hendrik Nunner
 */
public class CarayolRouxPanel extends SharedUtilityPanel implements ChangeListener {

    private static final long serialVersionUID = -2266756139159852784L;

    private JSlider sliderCrOmega;
    private DoubleJFormattedTextField txtOmegaSlider;
    private JSlider sliderDelta;
    private DoubleJFormattedTextField txtDeltaSlider;
    private JSlider sliderC;
    private DoubleJFormattedTextField txtCSlider;

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

        sliderCrOmega = new JSlider();
        sliderCrOmega.setBounds(22, 40, 285, 29);
        sliderCrOmega.setMaximum(200);
        sliderCrOmega.setMinimum(0);
        sliderCrOmega.setValue(30);
        add(sliderCrOmega);
        sliderCrOmega.addChangeListener(this);

        txtOmegaSlider = new DoubleJFormattedTextField((NumberFormat) null);
        txtOmegaSlider.setHorizontalAlignment(SwingConstants.RIGHT);
        txtOmegaSlider.setBounds(245, 28, 50, 20);
        txtOmegaSlider.setText(Double.toString(sliderCrOmega.getValue() / 100.0));
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

        disableAssortativity();
    }

    /**
     * Gets the benefits of connections (omega).
     *
     * @return the benefits of direct connections (omega)
     */
    public double getCrOmega() {
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


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        super.enableComponents();
//        this.txtOmegaSlider.setEnabled(true);
//        this.txtDeltaSlider.setEnabled(true);
//        this.txtCSlider.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        super.diseableComponents();
//        this.txtOmegaSlider.setEnabled(false);
//        this.txtDeltaSlider.setEnabled(false);
//        this.txtCSlider.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent e)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        if (source.equals(this.sliderCrOmega)) {
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
            listenersIt.next().notifyCrOmegaChanged();
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
