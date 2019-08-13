package nl.uu.socnetid.nidm.gui;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

/**
 * BEWARE: this class is not type safe!
 * TODO make type safe.
 *
 * @author Hendrik Nunner
 */
public class DoubleJFormattedTextField extends JFormattedTextField {

    private static final long serialVersionUID = -6681446407799331328L;

    /**
     * Constructor.
     *
     * @param nf
     *          the number format
     */
    public DoubleJFormattedTextField(NumberFormat nf) {
        super(nf);
    }


    /**
     * Gets the double value of the text field.
     *
     * @return the double value of the text field
     */
    public double getDouble() {
        String s = super.getText();
        return Double.valueOf(s.replace(",", "."));
    }

}
