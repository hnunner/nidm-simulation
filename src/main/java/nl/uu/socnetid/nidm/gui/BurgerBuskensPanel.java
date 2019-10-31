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
public class BurgerBuskensPanel extends SharedUtilityPanel implements ChangeListener {

    private static final long serialVersionUID = -185698368265056724L;

    private JSlider sliderB1;
    private DoubleJFormattedTextField txtSliderB1;
    private JSlider sliderB2;
    private DoubleJFormattedTextField txtSliderB2;
    private JSlider sliderC1;
    private DoubleJFormattedTextField txtSliderC1;
    private JSlider sliderC2;
    private DoubleJFormattedTextField txtSliderC2;
    private JSlider sliderC3;
    private DoubleJFormattedTextField txtSliderC3;

    // listeners
    private final Set<BurgerBuskensChangeListener> changeListeners = new CopyOnWriteArraySet<BurgerBuskensChangeListener>();


    /**
     * Create the panel.
     */
    public BurgerBuskensPanel() {
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

        JLabel lblB21 = new JLabel("Triadic closure");
        lblB21.setBounds(37, 68, 154, 16);
        add(lblB21);

        JLabel lblB22 = new JLabel("(b  ):");
        lblB22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB22.setBounds(201, 68, 35, 16);
        add(lblB22);

        JLabel lblC11 = new JLabel("Standard");
        lblC11.setBounds(37, 158, 86, 16);
        add(lblC11);

        JLabel lblC12 = new JLabel("(c  ):");
        lblC12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC12.setBounds(201, 158, 35, 16);
        add(lblC12);

        JLabel lblC21 = new JLabel("Quadratic");
        lblC21.setBounds(37, 196, 154, 16);
        add(lblC21);

        JLabel lblC22 = new JLabel("(c  ):");
        lblC22.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC22.setBounds(201, 196, 35, 16);
        add(lblC22);

        JLabel lblC31 = new JLabel("Triadic closure");
        lblC31.setBounds(37, 236, 100, 16);
        add(lblC31);

        JLabel lblC32 = new JLabel("(c  ):");
        lblC32.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC32.setBounds(201, 236, 35, 16);
        add(lblC32);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(1, 118, 312, 10);
        add(separator);

        JLabel lblSocialCostsHeader = new JLabel("Social maintenance costs:");
        lblSocialCostsHeader.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCostsHeader.setBounds(14, 128, 242, 16);
        add(lblSocialCostsHeader);

        JLabel label_4 = new JLabel("1");
        label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_4.setHorizontalAlignment(SwingConstants.LEFT);
        label_4.setBounds(220, 37, 13, 16);
        add(label_4);

        JLabel label_7 = new JLabel("1");
        label_7.setHorizontalAlignment(SwingConstants.LEFT);
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_7.setBounds(221, 165, 13, 16);
        add(label_7);

        JLabel label_8 = new JLabel("2");
        label_8.setHorizontalAlignment(SwingConstants.LEFT);
        label_8.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_8.setBounds(221, 75, 13, 16);
        add(label_8);

        JLabel label_11 = new JLabel("2");
        label_11.setHorizontalAlignment(SwingConstants.LEFT);
        label_11.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_11.setBounds(221, 203, 13, 16);
        add(label_11);

        JLabel label_12 = new JLabel("3");
        label_12.setHorizontalAlignment(SwingConstants.LEFT);
        label_12.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_12.setBounds(221, 243, 13, 16);
        add(label_12);

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
        sliderB2.setMaximum(100);
        sliderB2.setMinimum(0);
        sliderB2.setValue(20);
        add(sliderB2);
        sliderB2.addChangeListener(this);

        txtSliderB2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderB2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderB2.setBounds(246, 66, 50, 20);
        txtSliderB2.setValue(Double.toString(sliderB2.getValue() / 100.0));
        txtSliderB2.setEnabled(false);
        add(txtSliderB2);

        sliderC1 = new JSlider();
        sliderC1.setBounds(22, 168, 285, 29);
        sliderC1.setMaximum(100);
        sliderC1.setMinimum(0);
        sliderC1.setValue(20);
        add(sliderC1);
        sliderC1.addChangeListener(this);

        txtSliderC1 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC1.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC1.setBounds(246, 156, 50, 20);
        txtSliderC1.setValue(Double.toString(sliderC1.getValue() / 100.0));
        txtSliderC1.setEnabled(false);
        add(txtSliderC1);

        sliderC2 = new JSlider();
        sliderC2.setBounds(22, 208, 285, 29);
        sliderC2.setMaximum(100);
        sliderC2.setMinimum(0);
        sliderC2.setValue(20);
        add(sliderC2);
        sliderC2.addChangeListener(this);

        txtSliderC2 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC2.setBounds(246, 196, 50, 20);
        txtSliderC2.setValue(Double.toString(sliderC2.getValue() / 100.0));
        txtSliderC2.setEnabled(false);
        add(txtSliderC2);

        sliderC3 = new JSlider();
        sliderC3.setBounds(22, 248, 285, 29);
        sliderC3.setMaximum(100);
        sliderC3.setMinimum(0);
        sliderC3.setValue(20);
        add(sliderC3);
        sliderC3.addChangeListener(this);

        txtSliderC3 = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderC3.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderC3.setBounds(246, 236, 50, 20);
        txtSliderC3.setValue(Double.toString(sliderC3.getValue() / 100.0));
        txtSliderC3.setEnabled(false);
        add(txtSliderC3);
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
     * Gets the benefits of triadic closures (b2).
     *
     * @return the benefits of triadic closures (b2)
     */
    public double getB2() {
        return this.txtSliderB2.getDouble();
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
     * Gets the costs of triadic closures (c3).
     *
     * @return the costs of triadic closures (c3)
     */
    public double getC3() {
        return this.txtSliderC3.getDouble();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        super.enableComponents();
//        this.txtSliderB1.setEnabled(true);
//        this.txtSliderB2.setEnabled(true);
//        this.txtSliderC1.setEnabled(true);
//        this.txtSliderC2.setEnabled(true);
//        this.txtSliderC3.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        super.diseableComponents();
//        this.txtSliderB1.setEnabled(false);
//        this.txtSliderB2.setEnabled(false);
//        this.txtSliderC1.setEnabled(false);
//        this.txtSliderC2.setEnabled(false);
//        this.txtSliderC3.setEnabled(false);
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

        if (source.equals(this.sliderC3)) {
            this.txtSliderC3.setText(Double.toString(source.getValue() / 100.0));
            notifyC3Changed();
        }
    }


    /**
     * Adds a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be added
     */
    public void addParameterChangeListener(BurgerBuskensChangeListener parameterChangeListener) {
        this.changeListeners.add(parameterChangeListener);
    }

    /**
     * Removes a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be removed
     */
    public void removeParameterChangeListener(BurgerBuskensChangeListener parameterChangeListener) {
        this.changeListeners.remove(parameterChangeListener);
    }

    /**
     * Notifies listeners of changed b1.
     */
    private final void notifyB1Changed() {
        Iterator<BurgerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyB1Changed();
        }
    }

    /**
     * Notifies listeners of changed b2.
     */
    private final void notifyB2Changed() {
        Iterator<BurgerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyB2Changed();
        }
    }

    /**
     * Notifies listeners of changed c1.
     */
    private final void notifyC1Changed() {
        Iterator<BurgerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyC1Changed();
        }
    }

    /**
     * Notifies listeners of changed c2.
     */
    private final void notifyC2Changed() {
        Iterator<BurgerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyC2Changed();
        }
    }

    /**
     * Notifies listeners of changed c3.
     */
    private final void notifyC3Changed() {
        Iterator<BurgerBuskensChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyC3Changed();
        }
    }
}
