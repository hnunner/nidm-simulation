package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class IRTCPanel extends JPanel {

    private static final long serialVersionUID = -434189213444843258L;

    private JTextField txtAlpha;
    private JTextField txtBeta;
    private JTextField txtMu;
    private JTextField txtC;

    /**
     * Create the panel.
     */
    public IRTCPanel() {
        setLayout(null);

        JLabel lblAlpha = new JLabel("dir. benefit (α):");
        lblAlpha.setBounds(6, 11, 100, 16);
        add(lblAlpha);

        txtAlpha = new JTextField();
        txtAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAlpha.setText("5");
        txtAlpha.setBounds(116, 6, 44, 26);
        add(txtAlpha);
        txtAlpha.setColumns(10);

        JLabel lblBeta = new JLabel("ind. benefit (β):");
        lblBeta.setBounds(6, 39, 100, 16);
        add(lblBeta);

        txtBeta = new JTextField();
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setText("1");
        txtBeta.setBounds(116, 34, 44, 26);
        add(txtBeta);
        txtBeta.setColumns(10);

        JLabel lblMu = new JLabel("care factor (μ):");
        lblMu.setBounds(6, 67, 100, 16);
        add(lblMu);

        txtMu = new JTextField();
        txtMu.setText("2.5");
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(116, 62, 44, 26);
        add(txtMu);

        JLabel lblC = new JLabel("costs (c):");
        lblC.setBounds(6, 95, 100, 16);
        add(lblC);

        txtC = new JTextField();
        txtC.setText("4");
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);
        txtC.setBounds(116, 90, 44, 26);
        add(txtC);
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
     * Gets the care factor for infected direct connections (mu).
     *
     * @return the care factor for infected direct connections (mu)
     */
    public double getMu() {
        return Double.valueOf(this.txtMu.getText());
    }

    /**
     * Gets the costs for direct connections (c).
     *
     * @return the costs for direct connections (c)
     */
    public double getC() {
        return Double.valueOf(this.txtC.getText());
    }
}
