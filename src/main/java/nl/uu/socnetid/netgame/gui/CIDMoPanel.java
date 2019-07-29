package nl.uu.socnetid.netgame.gui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CIDMoPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -6334519672487731007L;

    private JTextField txtAlpha;
    private JTextField txtBeta;
    private JTextField txtC;
    private JLabel label;
    private JLabel label_1;
    private JLabel lblc;

    /**
     * Create the panel.
     */
    public CIDMoPanel() {
        setLayout(null);

        JLabel lblAlpha = new JLabel("Direct benefit");
        lblAlpha.setBounds(10, 5, 100, 16);
        add(lblAlpha);

        txtAlpha = new JTextField();
        txtAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAlpha.setText("5");
        txtAlpha.setBounds(151, 3, 60, 20);
        add(txtAlpha);
        txtAlpha.setColumns(10);

        JLabel lblBeta = new JLabel("Indirect benefit");
        lblBeta.setBounds(10, 28, 109, 16);
        add(lblBeta);

        txtBeta = new JTextField();
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setText("1");
        txtBeta.setBounds(151, 26, 60, 20);
        add(txtBeta);
        txtBeta.setColumns(10);

        JLabel lblC = new JLabel("costs");
        lblC.setBounds(10, 51, 100, 16);
        add(lblC);

        txtC = new JTextField();
        txtC.setText("4");
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);
        txtC.setBounds(151, 49, 60, 20);
        add(txtC);

        label = new JLabel("(α):");
        label.setBounds(127, 5, 24, 16);
        add(label);

        label_1 = new JLabel("(β):");
        label_1.setBounds(127, 28, 24, 16);
        add(label_1);

        lblc = new JLabel("(c):");
        lblc.setBounds(127, 51, 24, 16);
        add(lblc);
    }

    /**
     * Gets the benefit of a direct connection (alpha).
     *
     * @return the benefit of a direct connection (alpha)
     */
    public double getAlpha() {
        return Double.valueOf(this.txtAlpha.getText());
    }

    /**
     * Gets the benefit of an indirect connection (beta).
     *
     * @return the benefit of an indirect connection (beta)
     */
    public double getBeta() {
        return Double.valueOf(this.txtBeta.getText());
    }

    /**
     * Gets the costs for direct connections (c).
     *
     * @return the costs for direct connections (c)
     */
    public double getC() {
        return Double.valueOf(this.txtC.getText());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtAlpha.setEnabled(true);
        this.txtBeta.setEnabled(true);
        this.txtC.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtAlpha.setEnabled(false);
        this.txtBeta.setEnabled(false);
        this.txtC.setEnabled(false);
    }

}
