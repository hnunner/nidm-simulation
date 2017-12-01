package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnectionsPanel extends JPanel {

    private static final long serialVersionUID = 9040001912667323447L;

    private JTextField txtDelta;
    private JTextField txtCosts;

    /**
     * Create the panel.
     */
    public TruncatedConnectionsPanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Benefit (delta):");
        lblBenefitdelta.setBounds(6, 11, 100, 16);
        add(lblBenefitdelta);

        txtDelta = new JTextField();
        txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDelta.setText("0.5");
        txtDelta.setBounds(116, 6, 44, 26);
        add(txtDelta);
        txtDelta.setColumns(10);

        JLabel lblCostsc = new JLabel("Costs (c):");
        lblCostsc.setBounds(6, 39, 61, 16);
        add(lblCostsc);

        txtCosts = new JTextField();
        txtCosts.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCosts.setText("0.45");
        txtCosts.setBounds(116, 34, 44, 26);
        add(txtCosts);
        txtCosts.setColumns(10);
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

}
