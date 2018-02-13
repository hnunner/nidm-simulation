package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnectionsPanel extends DeactivatablePanel {

    private static final long serialVersionUID = 9040001912667323447L;

    private JTextField txtDelta;
    private JTextField txtCosts;
    private JLabel label;
    private JLabel lblc;

    /**
     * Create the panel.
     */
    public TruncatedConnectionsPanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Benefit");
        lblBenefitdelta.setBounds(10, 5, 100, 16);
        add(lblBenefitdelta);

        txtDelta = new JTextField();
        txtDelta.setBounds(151, 3, 60, 20);
        txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDelta.setText("0.5");
        add(txtDelta);
        txtDelta.setColumns(10);

        JLabel lblCostsc = new JLabel("Costs");
        lblCostsc.setBounds(10, 28, 109, 16);
        add(lblCostsc);

        txtCosts = new JTextField();
        txtCosts.setBounds(151, 26, 60, 20);
        txtCosts.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCosts.setText("0.45");
        add(txtCosts);
        txtCosts.setColumns(10);

        label = new JLabel("(δ):");
        label.setBounds(127, 5, 24, 16);
        add(label);

        lblc = new JLabel("(c):");
        lblc.setBounds(127, 28, 24, 16);
        add(lblc);
    }

    /**
     * Gets the delta. That is, the benefit that deteriorates over distance.
     *
     * @return the delta
     */
    public double getDelta() {
        return Double.valueOf(this.txtDelta.getText());
    }

    /**
     * Gets the costs to maintain a direct connection.
     *
     * @return the costs to maintain a direct connection
     */
    public double getCosts() {
        return Double.valueOf(this.txtCosts.getText());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtDelta.setEnabled(true);
        this.txtCosts.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtDelta.setEnabled(false);
        this.txtCosts.setEnabled(false);
    }

}
