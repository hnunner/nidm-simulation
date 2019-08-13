package nl.uu.socnetid.nidm.gui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Hendrik Nunner
 */
public class IntegerInputVerifier extends InputVerifier {

    private Integer min;
    private Integer max;


    /**
     * Standard constructor with whole integer range as valid values.
     */
    public IntegerInputVerifier() {
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
    public IntegerInputVerifier(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }


    /* (non-Javadoc)
     * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
     */
    @Override
    public boolean verify(JComponent input) {
        int number;

        // integer
        try {
            number = Integer.parseInt(((JTextField)input).getText());
        } catch (Exception e) {
            error();
            return false;
        }

        // value range validity
        if ((min != null && number <= min) || (max != null && number >= max)) {
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
        errorBuilder.append("Input must be whole numbers");
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
