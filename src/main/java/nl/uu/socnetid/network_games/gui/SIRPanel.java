package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class SIRPanel extends JPanel {

    private static final long serialVersionUID = 8094932631749545446L;

    private JTextField txtTau;
    private JTextField txtDelta;
    private JTextField txtGamma;
    private JTextField txtMu;

    /**
     * Create the panel.
     */
    public SIRPanel() {
        setLayout(null);

        JLabel lblTau = new JLabel("recov. time (τ):");
        lblTau.setToolTipText("The duration of the disease in rounds.");
        lblTau.setBounds(6, 11, 100, 16);
        add(lblTau);

        txtTau = new JTextField();
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setToolTipText("The duration of the disease in rounds.");
        txtTau.setText("10");
        txtTau.setBounds(116, 6, 44, 26);
        add(txtTau);
        txtTau.setColumns(10);

        JLabel lblDelta = new JLabel("severity (δ):");
        lblDelta.setToolTipText("The punishment for an agent to be infected.");
        lblDelta.setBounds(6, 44, 111, 16);
        add(lblDelta);

        txtDelta = new JTextField();
        txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDelta.setToolTipText("The punishment for an agent to be infected.");
        txtDelta.setText("5");
        txtDelta.setBounds(116, 39, 44, 26);
        add(txtDelta);
        txtDelta.setColumns(10);

        JLabel lblGamma = new JLabel("transm. rate (ɣ):");
        lblGamma.setToolTipText("The probability a disease is spread between an infected "
                + "and a non-infected agent per round.");
        lblGamma.setBounds(6, 77, 111, 16);
        add(lblGamma);

        txtGamma = new JTextField();
        txtGamma.setToolTipText("The probability a disease is spread between an infected "
                + "and a non-infected agent per round.");
        txtGamma.setText("0.1");
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setBounds(116, 72, 44, 26);
        add(txtGamma);
        txtGamma.setColumns(10);

        JLabel lblMu = new JLabel("care factor (μ):");
        lblMu.setBounds(6, 110, 100, 16);
        add(lblMu);

        txtMu = new JTextField();
        txtMu.setText("2.5");
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(116, 105, 44, 26);
        add(txtMu);
    }



    /**
     * @return the duration a disease requires to recover from in rounds (tau)
     */
    public int getTau() {
        return Integer.valueOf(this.txtTau.getText());
    }

    /**
     * @return the severity of the disease represented by the amount of punishment for having a disease (delta)
     */
    public double getDelta() {
        return Double.valueOf(this.txtDelta.getText());
    }

    /**
     * @return transmission rate (gamma) - the probability a disease is spread between
     *          an infected and a non-infected agent per round
     */
    public double getGamma() {
        return Double.valueOf(this.txtGamma.getText());
    }

    /**
     * @return the factor that increases maintenance costs for infected connections (mu)
     */
    public double getMu() {
        return Double.valueOf(this.txtMu.getText());
    }

}
