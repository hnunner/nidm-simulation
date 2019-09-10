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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Hendrik Nunner
 */
public class DoubleInputVerifier extends InputVerifier {

    private Double min;
    private Double max;


    /**
     * Standard constructor with whole double range as valid values.
     */
    public DoubleInputVerifier() {
        this(null, null);
    }

    /**
     * Constructor with explicit value range.
     *
     * @param min
     *          the lower bound of the valid value range
     * @param max
     *          the upper bound of the valid value range
     */
    public DoubleInputVerifier(Double min, Double max) {
        this.min = min;
        this.max = max;
    }


    /* (non-Javadoc)
     * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
     */
    @Override
    public boolean verify(JComponent input) {
        double number;

        // integer
        try {
            number = Double.parseDouble(((JTextField)input).getText());
        } catch (Exception e) {
            error();
            return false;
        }

        // value range validity
        if ((min != null && Double.compare(number, min) < 0) || (max != null && Double.compare(number, max) > 0)) {
            error();
            return false;
        }

        return true;
    }

    /**
     * Displays error message.
     */
    private void error() {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Input must be numbers");
        if (min == null && max == null) {
            errorBuilder.append("!");
        } else if (min != null && max != null) {
            errorBuilder.append(" within the range: ").append(this.min).append("-").append(this.max).append("!");
        } else if (min == null) {
            errorBuilder.append(" with a maximum value of: ").append(this.max).append("!");
        } else if (max == null) {
            errorBuilder.append(" with a minimum value of: ").append(this.min).append("!");
        }
        JOptionPane.showMessageDialog(null,
                errorBuilder.toString(),
                "Invalid input",
                JOptionPane.ERROR_MESSAGE);
    }

}
