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
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskens2Panel extends SharedUtilityPanel implements ChangeListener {

    private static final long serialVersionUID = -2525740115196114088L;

    private JSlider sliderTPref;
    private DoubleJFormattedTextField txtSliderTPref;
    private boolean considerTriads;
    private JRadioButton rdbtnConsiderTriadsYes;
    private JRadioButton rdbtnConsiderTriadsNo;
    private JSlider sliderAlpha;
    private DoubleJFormattedTextField txtSliderAlpha;

    // listeners
    private final Set<NunnerBuskens2ChangeListener> changeListeners = new CopyOnWriteArraySet<NunnerBuskens2ChangeListener>();


    /**
     * Create the panel.
     */
    public NunnerBuskens2Panel() {
        setLayout(null);

        JLabel lblSocialBenefits = new JLabel("Network settings:");
        lblSocialBenefits.setBounds(13, 0, 242, 16);
        add(lblSocialBenefits);
        lblSocialBenefits.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblB11 = new JLabel("Preferred number of ties");
        lblB11.setBounds(36, 30, 160, 16);
        add(lblB11);

        JLabel lblB12 = new JLabel("(t     ):");
        lblB12.setHorizontalAlignment(SwingConstants.RIGHT);
        lblB12.setBounds(195, 30, 40, 16);
        add(lblB12);

        JLabel lblB21 = new JLabel("Consider triads:");
        lblB21.setBounds(36, 81, 106, 16);
        add(lblB21);

        JLabel lblAlpha1 = new JLabel("Proportion of closed triads");
        lblAlpha1.setBounds(36, 132, 170, 16);
        add(lblAlpha1);

        JLabel lblAlpha2 = new JLabel("(α):");
        lblAlpha2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAlpha2.setBounds(200, 132, 35, 16);
        add(lblAlpha2);

        JLabel lblPref = new JLabel("pref");
        lblPref.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        lblPref.setHorizontalAlignment(SwingConstants.LEFT);
        lblPref.setBounds(209, 36, 20, 16);
        add(lblPref);

        sliderTPref = new JSlider();
        sliderTPref.setBounds(22, 40, 285, 29);
        sliderTPref.setMaximum(10);
        sliderTPref.setMinimum(0);
        sliderTPref.setValue(4);
        add(sliderTPref);
        sliderTPref.addChangeListener(this);

        txtSliderTPref = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderTPref.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderTPref.setBounds(245, 28, 50, 20);
        txtSliderTPref.setValue(Integer.toString(sliderTPref.getValue()));
        txtSliderTPref.setEnabled(false);
        add(txtSliderTPref);

        sliderAlpha = new JSlider();
        sliderAlpha.setBounds(21, 142, 285, 29);
        sliderAlpha.setMaximum(100);
        sliderAlpha.setMinimum(0);
        sliderAlpha.setValue(80);
        add(sliderAlpha);
        sliderAlpha.addChangeListener(this);

        txtSliderAlpha = new DoubleJFormattedTextField((NumberFormat) null);
        txtSliderAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSliderAlpha.setBounds(245, 130, 50, 20);
        txtSliderAlpha.setValue(Double.toString(sliderAlpha.getValue() / 100.0));
        txtSliderAlpha.setEnabled(false);
        add(txtSliderAlpha);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.WHITE);
        separator.setBounds(34, 71, 260, 10);
        add(separator);

        rdbtnConsiderTriadsNo = new JRadioButton("no");
        rdbtnConsiderTriadsNo.setSelected(true);
        rdbtnConsiderTriadsNo.setBounds(244, 77, 57, 23);
        add(rdbtnConsiderTriadsNo);

        rdbtnConsiderTriadsYes = new JRadioButton("yes");
        rdbtnConsiderTriadsYes.setBounds(244, 98, 57, 23);
        add(rdbtnConsiderTriadsYes);

        ButtonGroup considerTriadsGroup = new ButtonGroup();
        considerTriadsGroup.add(rdbtnConsiderTriadsNo);
        considerTriadsGroup.add(rdbtnConsiderTriadsYes);

        rdbtnConsiderTriadsNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConsiderTriads();
            }
        });

        rdbtnConsiderTriadsYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConsiderTriads();
            }
        });

        updateConsiderTriads();

    }

    /**
     * Gets the preference shift between open and closed triads (alpha).
     *
     * @return the preference shift between open and closed triads (alpha)
     */
    public int getTPref() {
        return Integer.parseInt(this.txtSliderTPref.getText());
    }

    /**
     * @return the considerTriads
     */
    public boolean isConsiderTriads() {
        return this.considerTriads;
    }

    /**
     * Updates whether triads are considered or not.
     */
    private void updateConsiderTriads() {
        this.considerTriads = this.rdbtnConsiderTriadsYes.isSelected();
        this.sliderAlpha.setEnabled(this.considerTriads);
        notifyConsiderTriadsChanged();
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
//        this.txtSliderTPref.setEnabled(true);
//        this.txtSliderAlpha.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        super.diseableComponents();
//        this.txtSliderTPref.setEnabled(false);
//        this.txtSliderAlpha.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent e)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        if (source.equals(this.sliderTPref)) {
            this.txtSliderTPref.setText(Integer.toString(source.getValue()));
            notifyTPrefChanged();
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
    public void addParameterChangeListener(NunnerBuskens2ChangeListener parameterChangeListener) {
        this.changeListeners.add(parameterChangeListener);
    }

    /**
     * Removes a listener for parameter change notifications.
     *
     * @param parameterChangeListener
     *          the listener to be removed
     */
    public void removeParameterChangeListener(NunnerBuskens2ChangeListener parameterChangeListener) {
        this.changeListeners.remove(parameterChangeListener);
    }

    /**
     * Notifies listeners of changed considerTriads.
     */
    private final void notifyConsiderTriadsChanged() {
        Iterator<NunnerBuskens2ChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyConsiderTriadsChanged();
        }
    }

    /**
     * Notifies listeners of changed tPref.
     */
    private final void notifyTPrefChanged() {
        Iterator<NunnerBuskens2ChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyTPrefChanged();
        }
    }

    /**
     * Notifies listeners of changed alpha.
     */
    private final void notifyAlphaChanged() {
        Iterator<NunnerBuskens2ChangeListener> listenersIt = this.changeListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAlphaChanged();
        }
    }
}
