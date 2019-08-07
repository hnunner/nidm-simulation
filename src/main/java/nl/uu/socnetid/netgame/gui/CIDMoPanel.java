package nl.uu.socnetid.netgame.gui;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class CIDMoPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -6334519672487731007L;

    private JFormattedTextField txtAlpha;
    private JFormattedTextField txtKappa;
    private JFormattedTextField txtBeta;
    private JFormattedTextField txtLamda;
    private JFormattedTextField txtC;
    private JFormattedTextField txtMu;
    private JFormattedTextField txtSigma;
    private JFormattedTextField txtGamma;
    private JFormattedTextField txtRSigma;
    private JFormattedTextField txtRPi;
    private JFormattedTextField txtPhi;
    private JFormattedTextField txtTau;

    // INPUT VALIDATION
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();
    // benefit of direct connections (alpha), benefit of indirect connections (beta), maintenance costs (c)
    private static final InputVerifier REAL_NUMBERS_VERIFIER = new DoubleInputVerifier();
    // discounts for infected ties (kappa, lamda)
    private static final InputVerifier DISCOUNT_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // care factor for infected direct connections (mu)
    private static final InputVerifier MU_VERIFIER = new DoubleInputVerifier(1.0, null);
    // disease severity (sigma)
    private static final InputVerifier SIGMA_VERIFIER = new DoubleInputVerifier(1.001, null);
    // probability of infections (gamma)
    private static final InputVerifier GAMMA_VERIFIER = new DoubleInputVerifier(0.0, 1.0);
    // risk perceptions (r_sigma, r_pi)
    private static final InputVerifier R_VERIFIER = new DoubleInputVerifier(0.0, 2.0);
    // share of peers to evaluate per time step (phi)
    private static final InputVerifier PHI_VERIFIER = new DoubleInputVerifier(0.001, 1.0);
    // time steps to recover (tau)
    private static final InputVerifier TAU_VERIFIER = new IntegerInputVerifier(1, null);


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

        txtAlpha = new JFormattedTextField(NUM_FORMAT);
        txtAlpha.setBounds(245, 58, 50, 20);
        add(txtAlpha);
        txtAlpha.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAlpha.setColumns(10);
        txtAlpha.setValue(new Double(10));
        txtAlpha.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JLabel lblKappa1 = new JLabel("Discount for infected tie");
        lblKappa1.setBounds(52, 85, 154, 16);
        add(lblKappa1);

        JLabel lblKappa2 = new JLabel("(κ):");
        lblKappa2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblKappa2.setBounds(200, 85, 35, 16);
        add(lblKappa2);

        txtKappa = new JFormattedTextField(NUM_FORMAT);
        txtKappa.setBounds(245, 83, 50, 20);
        add(txtKappa);
        txtKappa.setHorizontalAlignment(SwingConstants.RIGHT);
        txtKappa.setColumns(10);
        txtKappa.setValue(new Double(1.0));
        txtKappa.setInputVerifier(DISCOUNT_VERIFIER);

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

        txtBeta = new JFormattedTextField(NUM_FORMAT);
        txtBeta.setBounds(245, 143, 50, 20);
        add(txtBeta);
        txtBeta.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBeta.setColumns(10);
        txtBeta.setValue(new Double(8));
        txtBeta.setInputVerifier(REAL_NUMBERS_VERIFIER);

        JLabel lblLamda1 = new JLabel("Discount for infected tie");
        lblLamda1.setBounds(52, 170, 154, 16);
        add(lblLamda1);

        JLabel lblLamda2 = new JLabel("(λ):");
        lblLamda2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblLamda2.setBounds(200, 170, 35, 16);
        add(lblLamda2);

        txtLamda = new JFormattedTextField(NUM_FORMAT);
        txtLamda.setBounds(245, 168, 50, 20);
        add(txtLamda);
        txtLamda.setHorizontalAlignment(SwingConstants.RIGHT);
        txtLamda.setColumns(10);
        txtLamda.setValue(new Double(1.0));
        txtLamda.setInputVerifier(DISCOUNT_VERIFIER);

        JLabel lblC1 = new JLabel("Direct ties");
        lblC1.setBounds(36, 241, 100, 16);
        add(lblC1);

        JLabel lblC2 = new JLabel("(c):");
        lblC2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblC2.setBounds(200, 241, 35, 16);
        add(lblC2);

        txtC = new JFormattedTextField(NUM_FORMAT);
        txtC.setBounds(245, 239, 50, 20);
        add(txtC);
        txtC.setHorizontalAlignment(SwingConstants.RIGHT);
        txtC.setColumns(10);
        txtC.setValue(new Double(9));
        txtC.setInputVerifier(REAL_NUMBERS_VERIFIER);

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

        txtMu = new JFormattedTextField(NUM_FORMAT);
        txtMu.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMu.setColumns(10);
        txtMu.setBounds(245, 264, 50, 20);
        add(txtMu);
        txtMu.setValue(new Double(1.5));
        txtMu.setInputVerifier(MU_VERIFIER);

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

        JLabel lblGamma1 = new JLabel("Probability of infection");
        lblGamma1.setBounds(37, 362, 170, 16);
        add(lblGamma1);

        JLabel lblSigma2 = new JLabel("(σ):");
        lblSigma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSigma2.setBounds(201, 337, 35, 16);
        add(lblSigma2);

        txtSigma = new JFormattedTextField(NUM_FORMAT);
        txtSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSigma.setColumns(10);
        txtSigma.setBounds(246, 335, 50, 20);
        add(txtSigma);
        txtSigma.setValue(new Double(50));
        txtSigma.setInputVerifier(SIGMA_VERIFIER);

        JLabel lblGamma2 = new JLabel("(γ):");
        lblGamma2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGamma2.setBounds(201, 362, 35, 16);
        add(lblGamma2);

        txtGamma = new JFormattedTextField(NUM_FORMAT);
        txtGamma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtGamma.setColumns(10);
        txtGamma.setBounds(246, 360, 50, 20);
        add(txtGamma);
        txtGamma.setValue(new Double(0.1));
        txtGamma.setInputVerifier(GAMMA_VERIFIER);


        JLabel lblRiskPerception = new JLabel("Risk perception:");
        lblRiskPerception.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblRiskPerception.setBounds(37, 392, 242, 16);
        add(lblRiskPerception);

        JLabel lblDiseaseSeverity = new JLabel("Disease severity");
        lblDiseaseSeverity.setBounds(53, 422, 153, 16);
        add(lblDiseaseSeverity);

        txtRSigma = new JFormattedTextField(NUM_FORMAT);
        txtRSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRSigma.setColumns(10);
        txtRSigma.setBounds(246, 420, 50, 20);
        add(txtRSigma);
        txtRSigma.setValue(new Double(1.0));
        txtRSigma.setInputVerifier(R_VERIFIER);

        JLabel lblProbabilityOfInfection = new JLabel("Probability of infection");
        lblProbabilityOfInfection.setBounds(53, 447, 154, 16);
        add(lblProbabilityOfInfection);

        txtRPi = new JFormattedTextField(NUM_FORMAT);
        txtRPi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRPi.setColumns(10);
        txtRPi.setBounds(246, 445, 50, 20);
        add(txtRPi);
        txtRPi.setValue(new Double(1.0));
        txtRPi.setInputVerifier(R_VERIFIER);

        JSeparator separator_4 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_4.setForeground(Color.WHITE);
        separator_4.setBounds(36, 385, 260, 10);
        add(separator_4);

        JLabel label = new JLabel("(r  ):");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(201, 422, 35, 16);
        add(label);

        JLabel label_1 = new JLabel("π");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_1.setBounds(219, 455, 7, 10);
        add(label_1);

        JLabel label_2 = new JLabel("(r  ):");
        label_2.setHorizontalAlignment(SwingConstants.RIGHT);
        label_2.setBounds(201, 447, 35, 16);
        add(label_2);

        JLabel label_3 = new JLabel("σ");
        label_3.setHorizontalAlignment(SwingConstants.RIGHT);
        label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_3.setBounds(219, 430, 7, 10);
        add(label_3);

        JLabel lblNetworkEvaluation = new JLabel("Network - evaluation (per agent):");
        lblNetworkEvaluation.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkEvaluation.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkEvaluation.setBounds(14, 488, 238, 16);
        add(lblNetworkEvaluation);

        JLabel label_9 = new JLabel("% of network per time step");
        label_9.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_9.setBounds(38, 518, 177, 16);
        add(label_9);

        JLabel label_10 = new JLabel("(ϕ):");
        label_10.setHorizontalAlignment(SwingConstants.RIGHT);
        label_10.setBounds(202, 518, 35, 16);
        add(label_10);

        txtPhi = new JFormattedTextField(NUM_FORMAT);
        txtPhi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPhi.setColumns(10);
        txtPhi.setBounds(246, 516, 50, 20);
        add(txtPhi);
        txtPhi.setValue(new Double(0.4));
        txtPhi.setInputVerifier(PHI_VERIFIER);

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(1, 478, 312, 10);
        add(separator_5);

        JSeparator separator_6 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_6.setForeground(Color.LIGHT_GRAY);
        separator_6.setBounds(1, 549, 312, 10);
        add(separator_6);

        JLabel lblSimulation = new JLabel("Simulation:");
        lblSimulation.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblSimulation.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSimulation.setBounds(13, 559, 238, 16);
        add(lblSimulation);

        JLabel label_5 = new JLabel("Time steps to recover");
        label_5.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_5.setBounds(37, 589, 177, 16);
        add(label_5);

        JLabel label_6 = new JLabel("(τ):");
        label_6.setHorizontalAlignment(SwingConstants.RIGHT);
        label_6.setBounds(201, 589, 35, 16);
        add(label_6);

        txtTau = new JFormattedTextField(NUM_FORMAT);
        txtTau.setText("10");
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setColumns(10);
        txtTau.setBounds(245, 587, 50, 20);
        add(txtTau);
        txtTau.setValue(new Integer(10));
        txtTau.setInputVerifier(TAU_VERIFIER);
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

    /**
     * Gets the risk perception for disease severity (rSigma).
     *
     * @return the risk perception for disease severity (rSigma)
     */
    public double getRSigma() {
        return Double.valueOf(this.txtRSigma.getText());
    }

    /**
     * Gets the risk perception for probability of infections (rPi).
     *
     * @return the risk perception for probability of infections (rPi)
     */
    public double getRPi() {
        return Double.valueOf(this.txtRPi.getText());
    }

    /**
     * Gets the share of peers to evaluate per time step (phi).
     *
     * @return the share of peers to evaluate per time step (phi)
     */
    public double getPhi() {
        return Double.valueOf(this.txtPhi.getText());
    }

    /**
     * Gets the number of time step to recover (tau).
     *
     * @return the number of time step to recover (tau)
     */
    public int getTau() {
        return Integer.valueOf(this.txtTau.getText());
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
