package nl.uu.socnetid.netgame.gui;

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
