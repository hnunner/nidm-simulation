package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Hendrik Nunner
 */
public class TruncatedConnectionsPanel extends JPanel {

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
        txtDelta.setText("0.5");
        txtDelta.setBounds(116, 6, 44, 26);
        add(txtDelta);
        txtDelta.setColumns(10);

        JLabel lblCostsc = new JLabel("Costs (c):");
        lblCostsc.setBounds(6, 39, 61, 16);
        add(lblCostsc);

        txtCosts = new JTextField();
        txtCosts.setText("0.45");
        txtCosts.setBounds(116, 34, 44, 26);
        add(txtCosts);
        txtCosts.setColumns(10);
    }

    public double getDelta() {
        return Double.valueOf(this.txtDelta.getText());
    }

    public double getCosts() {
        return Double.valueOf(this.txtCosts.getText());
    }

}
