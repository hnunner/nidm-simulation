package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CumulativePanel extends DeactivatablePanel {

    private static final long serialVersionUID = -7455633523735790036L;

    private JTextField txtDirect;
    private JTextField txtIndirect;
    private JLabel label;
    private JLabel label_1;

    /**
     * Create the panel.
     */
    public CumulativePanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Direct benefit");
        lblBenefitdelta.setBounds(10, 5, 100, 16);
        lblBenefitdelta.setToolTipText("The benefit a player gets from direct connections.");
        add(lblBenefitdelta);

        txtDirect = new JTextField();
        txtDirect.setBounds(151, 3, 60, 20);
        txtDirect.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDirect.setToolTipText("The benefit a player gets from direct connections.");
        txtDirect.setText("1");
        add(txtDirect);
        txtDirect.setColumns(10);

        JLabel lblCostsc = new JLabel("Indirect benefit");
        lblCostsc.setBounds(10, 28, 109, 16);
        lblCostsc.setToolTipText("The benefit a player gets from indirect connections. That is, connections at distance 2.");
        add(lblCostsc);

        txtIndirect = new JTextField();
        txtIndirect.setBounds(151, 26, 60, 20);
        txtIndirect.setHorizontalAlignment(SwingConstants.RIGHT);
        txtIndirect.setToolTipText("The benefit a player gets from indirect connections. That is, connections at distance 2.");
        txtIndirect.setText("0.5");
        add(txtIndirect);
        txtIndirect.setColumns(10);

        label = new JLabel("(α):");
        label.setBounds(127, 5, 24, 16);
        add(label);

        label_1 = new JLabel("(β):");
        label_1.setBounds(127, 28, 24, 16);
        add(label_1);
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

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtDirect.setEnabled(true);
        this.txtIndirect.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtDirect.setEnabled(false);
        this.txtIndirect.setEnabled(false);
    }

}
