package nl.uu.socnetid.netgame.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CIDMoPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -6334519672487731007L;

    private JTextField txtAlpha;
    private JTextField txtKappa;
    private JTextField txtBeta;
    private JTextField txtLamda;
    private JTextField txtC;
    private JTextField txtMu;
    private JTextField txtSigma;
    private JTextField txtGamma;

    /**
     * Create the panel.
     */
    public CIDMoPanel() {
        setLayout(null);

        JLabel lblBenefitsOfConnections = new JLabel("Direct ties:");
        lblBenefitsOfConnections.setBounds(36, 30, 242, 16);
        add(lblBenefitsOfConnections);
        lblBenefitsOfConnections.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblAlpha1 = new JLabel("Benefit");
        lblAlpha1.setBounds(52, 60, 86, 16);
        add(lblAlpha1);

        JLabel lblAlpha2 = new JLabel("(α):");
        lblAlpha2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAlpha2.setBounds(200, 58, 35, 16);
        add(lblAlpha2);

        txtAlpha = new JTextField();
        txtAlpha.setBounds(245, 58, 50, 20);
        add(txtAlpha);
        txtAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAlpha.setText("10");
        txtAlpha.setColumns(10);

        JLabel lblKappa1 = new JLabel("Discount for infected tie");
        lblKappa1.setBounds(52, 85, 154, 16);
        add(lblKappa1);

        JLabel lblKappa2 = new JLabel("(κ):");
        lblKappa2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblKappa2.setBounds(200, 85, 35, 16);
        add(lblKappa2);

        txtKappa = new JTextField();
        txtKappa.setBounds(245, 83, 50, 20);
        add(txtKappa);
        txtKappa.setHorizontalAlignment(SwingConstants.RIGHT);
        txtKappa.setText("1.0");
        txtKappa.setColumns(10);

        JLabel lblDiscountOfInfected_2 = new JLabel("Indirect ties:");
        lblDiscountOfInfected_2.setBounds(36, 115, 242, 16);
        add(lblDiscountOfInfected_2);
        lblDiscountOfInfected_2.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        JLabel lblBeta1 = new JLabel("Benefit");
        lblBeta1.setBounds(52, 145, 86, 16);
        add(lblBeta1);

        JLabel lblBeta2 = new JLabel("(β):");
        lblBeta2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBeta2.setBounds(200, 145, 35, 16);
        add(lblBeta2);

        txtBeta = new JTextField();
        txtBeta.setBounds(245, 143, 50, 20);
        add(txtBeta);
        txtBeta.setText("2");
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setColumns(10);

        JLabel lblLamda1 = new JLabel("Discount for infected tie");
        lblLamda1.setBounds(52, 170, 154, 16);
        add(lblLamda1);

        JLabel lblLamda2 = new JLabel("(λ):");
        lblLamda2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLamda2.setBounds(200, 170, 35, 16);
        add(lblLamda2);

        txtLamda = new JTextField();
        txtLamda.setBounds(245, 168, 50, 20);
        add(txtLamda);
        txtLamda.setText("1.0");
        txtLamda.setHorizontalAlignment(SwingConstants.RIGHT);
        txtLamda.setColumns(10);

        JLabel lblC1 = new JLabel("Direct ties");
        lblC1.setBounds(36, 241, 100, 16);
        add(lblC1);

        JLabel lblC2 = new JLabel("(c):");
        lblC2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC2.setBounds(200, 241, 35, 16);
        add(lblC2);

        txtC = new JTextField();
        txtC.setBounds(245, 239, 50, 20);
        add(txtC);
        txtC.setText("9");
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 201, 312, 10);
        add(separator);

        JLabel lblSocialBenefits = new JLabel("Social benefits:");
        lblSocialBenefits.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialBenefits.setBounds(13, 0, 242, 16);
        add(lblSocialBenefits);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.WHITE);
        separator_1.setBounds(35, 23, 260, 10);
        add(separator_1);

        JSeparator separator_2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_2.setForeground(Color.WHITE);
        separator_2.setBounds(35, 108, 260, 10);
        add(separator_2);

        JLabel lblSocialCosts = new JLabel("Social maintenance costs:");
        lblSocialCosts.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSocialCosts.setBounds(13, 211, 242, 16);
        add(lblSocialCosts);

        JLabel lblMu1 = new JLabel("Care factor for infected ties");
        lblMu1.setBounds(36, 266, 179, 16);
        add(lblMu1);

        JLabel lblMu2 = new JLabel("(μ):");
        lblMu2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMu2.setBounds(200, 266, 35, 16);
        add(lblMu2);

        txtMu = new JTextField();
        txtMu.setText("1.0");
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(245, 264, 50, 20);
        add(txtMu);

        JSeparator separator_3 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_3.setForeground(Color.LIGHT_GRAY);
        separator_3.setBounds(1, 297, 312, 10);
        add(separator_3);

        JLabel lblPotentialHarmOf = new JLabel("Potential harm of infections:");
        lblPotentialHarmOf.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblPotentialHarmOf.setBounds(14, 307, 242, 16);
        add(lblPotentialHarmOf);

        JLabel lblSigma1 = new JLabel("Disease severity");
        lblSigma1.setBounds(37, 337, 152, 16);
        add(lblSigma1);

        JLabel lblGamma1 = new JLabel("Probability to get infected");
        lblGamma1.setBounds(37, 362, 170, 16);
        add(lblGamma1);

        JLabel lblSigma2 = new JLabel("(σ):");
        lblSigma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSigma2.setBounds(201, 337, 35, 16);
        add(lblSigma2);

        txtSigma = new JTextField();
        txtSigma.setText("10");
        txtSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSigma.setColumns(10);
        txtSigma.setBounds(246, 335, 50, 20);
        add(txtSigma);

        JLabel lblGamma2 = new JLabel("(γ):");
        lblGamma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGamma2.setBounds(201, 362, 35, 16);
        add(lblGamma2);

        txtGamma = new JTextField();
        txtGamma.setText("0.1");
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setColumns(10);
        txtGamma.setBounds(246, 360, 50, 20);
        add(txtGamma);
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
     * Gets the discount for infected direct connections (kappa).
     *
     * @return the discount for infected direct connections (kappa)
     */
    public double getKappa() {
        return Double.valueOf(this.txtKappa.getText());
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
     * Gets the discount for infected indirect connections (lamda).
     *
     * @return the discount for infected indirect connections (lamda)
     */
    public double getLamda() {
        return Double.valueOf(this.txtLamda.getText());
    }

    /**
     * Gets the costs for direct connections (c).
     *
     * @return the costs for direct connections (c)
     */
    public double getC() {
        return Double.valueOf(this.txtC.getText());
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
     * Gets the disease severity (sigma).
     *
     * @return the disease severity (sigma)
     */
    public double getSigma() {
        return Double.valueOf(this.txtSigma.getText());
    }

    /**
     * Gets the probability to get infected per contact (gamma).
     *
     * @return the probability to get infected per contact (gamma)
     */
    public double getGamma() {
        return Double.valueOf(this.txtGamma.getText());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.txtAlpha.setEnabled(true);
        this.txtKappa.setEnabled(true);
        this.txtBeta.setEnabled(true);
        this.txtLamda.setEnabled(true);
        this.txtC.setEnabled(true);
        this.txtMu.setEnabled(true);
        this.txtSigma.setEnabled(true);
        this.txtGamma.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.txtAlpha.setEnabled(false);
        this.txtKappa.setEnabled(false);
        this.txtBeta.setEnabled(false);
        this.txtLamda.setEnabled(false);
        this.txtC.setEnabled(false);
        this.txtMu.setEnabled(false);
        this.txtSigma.setEnabled(false);
        this.txtGamma.setEnabled(false);
    }
}
