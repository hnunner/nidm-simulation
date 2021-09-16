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
public class NunnerBuskensPanel extends SharedUtilityPanel implements ChangeListener {

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
        lblB21.setBounds(36, 68, 154, 16);
        add(lblB21);

        JLabel lblB22 = new JLabel("(b  ):");
        lblB22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB22.setBounds(200, 68, 35, 16);
        add(lblB22);

        JLabel lblC11 = new JLabel("Standard");
        lblC11.setBounds(42, 213, 86, 16);
        add(lblC11);

        JLabel lblC12 = new JLabel("(c  ):");
        lblC12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC12.setBounds(206, 213, 35, 16);
        add(lblC12);

        JLabel lblC21 = new JLabel("Quadratic");
        lblC21.setBounds(42, 251, 154, 16);
        add(lblC21);

        JLabel lblC22 = new JLabel("(c  ):");
        lblC22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC22.setBounds(206, 251, 35, 16);
        add(lblC22);

        JLabel lblAlpha1 = new JLabel("Open vs. closed preference");
        lblAlpha1.setBounds(37, 106, 170, 16);
        add(lblAlpha1);

        JLabel lblAlpha2 = new JLabel("(α):");
        lblAlpha2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAlpha2.setBounds(201, 106, 35, 16);
        add(lblAlpha2);

        JLabel lblSocialCostsHeader = new JLabel("Social maintenance costs:");
        lblSocialCostsHeader.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCostsHeader.setBounds(19, 183, 242, 16);
        add(lblSocialCostsHeader);

        JLabel label_4 = new JLabel("1");
        label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_4.setHorizontalAlignment(SwingConstants.LEFT);
        label_4.setBounds(220, 37, 13, 16);
        add(label_4);

        JLabel label_7 = new JLabel("1");
        label_7.setHorizontalAlignment(SwingConstants.LEFT);
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_7.setBounds(226, 220, 13, 16);
        add(label_7);

        JLabel lblB22_sub = new JLabel("2");
        lblB22_sub.setHorizontalAlignment(SwingConstants.LEFT);
        lblB22_sub.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        lblB22_sub.setBounds(220, 75, 13, 16);
        add(lblB22_sub);

        JLabel label_11 = new JLabel("2");
        label_11.setHorizontalAlignment(SwingConstants.LEFT);
        label_11.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_11.setBounds(226, 258, 13, 16);
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
        sliderB2.setBounds(22, 78, 285, 29);
        sliderB2.setMaximum(200);
        sliderB2.setMinimum(0);
        sliderB2.setValue(50);
        add(sliderB2);
        sliderB2.addChangeListener(this);

        txtSliderB2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderB2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderB2.setBounds(245, 66, 50, 20);
        txtSliderB2.setValue(Double.toString(sliderB2.getValue() / 100.0));
        txtSliderB2.setEnabled(false);
        add(txtSliderB2);

        sliderC1 = new JSlider();
        sliderC1.setBounds(27, 223, 285, 29);
        sliderC1.setMaximum(100);
        sliderC1.setMinimum(0);
        sliderC1.setValue(20);
        add(sliderC1);
        sliderC1.addChangeListener(this);

        txtSliderC1 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC1.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC1.setBounds(252, 211, 50, 20);
        txtSliderC1.setValue(Double.toString(sliderC1.getValue() / 100.0));
        txtSliderC1.setEnabled(false);
        add(txtSliderC1);

        sliderC2 = new JSlider();
        sliderC2.setBounds(27, 261, 285, 29);
        sliderC2.setMaximum(100);
        sliderC2.setMinimum(0);
        sliderC2.setValue(10);
        add(sliderC2);
        sliderC2.addChangeListener(this);

        txtSliderC2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC2.setBounds(251, 249, 50, 20);
        txtSliderC2.setValue(Double.toString(sliderC2.getValue() / 100.0));
        txtSliderC2.setEnabled(false);
        add(txtSliderC2);

        sliderAlpha = new JSlider();
        sliderAlpha.setBounds(22, 116, 285, 29);
        sliderAlpha.setMaximum(100);
        sliderAlpha.setMinimum(0);
        sliderAlpha.setValue(0);
        add(sliderAlpha);
        sliderAlpha.addChangeListener(this);

        txtSliderAlpha = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderAlpha.setBounds(246, 104, 50, 20);
        txtSliderAlpha.setValue(Double.toString(sliderAlpha.getValue() / 100.0));
        txtSliderAlpha.setEnabled(false);
        add(txtSliderAlpha);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.LIGHT_GRAY);
        separator_1.setBounds(0, 173, 312, 10);
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

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        super.enableComponents();
//        this.txtSliderB1.setEnabled(true);
//        this.txtSliderC1.setEnabled(true);
//        this.txtSliderC2.setEnabled(true);
//        this.txtSliderB2.setEnabled(true);
//        this.txtSliderAlpha.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        super.diseableComponents();
//        this.txtSliderB1.setEnabled(false);
//        this.txtSliderC1.setEnabled(false);
//        this.txtSliderC2.setEnabled(false);
//        this.txtSliderB2.setEnabled(false);
//        this.txtSliderAlpha.setEnabled(false);
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
