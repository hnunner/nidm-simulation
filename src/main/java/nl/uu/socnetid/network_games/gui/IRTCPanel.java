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
        lblBeta.setBounds(6, 44, 100, 16);
        add(lblBeta);

        txtBeta = new JTextField();
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setText("1");
        txtBeta.setBounds(116, 39, 44, 26);
        add(txtBeta);
        txtBeta.setColumns(10);

        JLabel lblC = new JLabel("costs (c):");
        lblC.setBounds(6, 77, 100, 16);
        add(lblC);

        txtC = new JTextField();
        txtC.setText("4");
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);
        txtC.setBounds(116, 72, 44, 26);
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
     * Gets the costs for direct connections (c).
     *
     * @return the costs for direct connections (c)
     */
    public double getC() {
        return Double.valueOf(this.txtC.getText());
    }

}
