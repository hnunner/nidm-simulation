package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CumulativePanel extends JPanel {

    private static final long serialVersionUID = -7455633523735790036L;

    private JTextField txtDirect;
    private JTextField txtIndirect;

    /**
     * Create the panel.
     */
    public CumulativePanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Direct benefit:");
        lblBenefitdelta.setToolTipText("The benefit a player gets from direct connections.");
        lblBenefitdelta.setBounds(6, 11, 100, 16);
        add(lblBenefitdelta);

        txtDirect = new JTextField();
        txtDirect.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDirect.setToolTipText("The benefit a player gets from direct connections.");
        txtDirect.setText("1");
        txtDirect.setBounds(116, 6, 44, 26);
        add(txtDirect);
        txtDirect.setColumns(10);

        JLabel lblCostsc = new JLabel("Indirect benefit:");
        lblCostsc.setToolTipText("The benefit a player gets from indirect connections. That is, connections at distance 2.");
        lblCostsc.setBounds(6, 44, 109, 16);
        add(lblCostsc);

        txtIndirect = new JTextField();
        txtIndirect.setHorizontalAlignment(SwingConstants.RIGHT);
        txtIndirect.setToolTipText("The benefit a player gets from indirect connections. That is, connections at distance 2.");
        txtIndirect.setText("0.5");
        txtIndirect.setBounds(116, 39, 44, 26);
        add(txtIndirect);
        txtIndirect.setColumns(10);
    }

    /**
     * Gets the benefit for direct connections.
     *
     * @return the benefit for direct connections
     */
    public double getDirectBenefit() {
        return Double.valueOf(this.txtDirect.getText());
    }

    /**
     * Gets the benefit for indirect connections (distance 2).
     *
     * @return the benefit for indirect connections (distance 2)
     */
    public double getIndirectBenefit() {
        return Double.valueOf(this.txtIndirect.getText());
    }

}
