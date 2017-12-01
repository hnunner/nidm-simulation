package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author Hendrik Nunner
 */
public class TwoStageDiseasePanel extends JPanel {

    private static final long serialVersionUID = -6212434798055285103L;

    private JTextField txtDuration;
    private JTextField txtTransmissionRate;
    private JLabel lblTreatmentCosts;
    private JTextField txtTreatmentCosts;

    /**
     * Create the panel.
     */
    public TwoStageDiseasePanel() {
        setLayout(null);

        JLabel lblBenefitdelta = new JLabel("Duration:");
        lblBenefitdelta.setToolTipText("The duration of the disease in rounds.");
        lblBenefitdelta.setBounds(6, 11, 100, 16);
        add(lblBenefitdelta);

        txtDuration = new JTextField();
        txtDuration.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDuration.setToolTipText("The duration of the disease in rounds.");
        txtDuration.setText("10");
        txtDuration.setBounds(116, 6, 44, 26);
        add(txtDuration);
        txtDuration.setColumns(10);

        JLabel lblCostsc = new JLabel("Transm. Rate:");
        lblCostsc.setToolTipText("The probability at which the disease is transferred within a single round.");
        lblCostsc.setBounds(6, 39, 111, 16);
        add(lblCostsc);

        txtTransmissionRate = new JTextField();
        txtTransmissionRate.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTransmissionRate.setToolTipText("The probability at which the disease is transferred within a single round.");
        txtTransmissionRate.setText("0.5");
        txtTransmissionRate.setBounds(116, 34, 44, 26);
        add(txtTransmissionRate);
        txtTransmissionRate.setColumns(10);

        lblTreatmentCosts = new JLabel("Treatment Costs:");
        lblTreatmentCosts.setToolTipText("The amount of utility that must be spent per round to care for an infected connection.");
        lblTreatmentCosts.setBounds(6, 67, 111, 16);
        add(lblTreatmentCosts);

        txtTreatmentCosts = new JTextField();
        txtTreatmentCosts.setToolTipText("The amount of utility that must be spent per round to care for an infected connection.");
        txtTreatmentCosts.setText("1");
        txtTreatmentCosts.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTreatmentCosts.setBounds(116, 62, 44, 26);
        add(txtTreatmentCosts);
        txtTreatmentCosts.setColumns(10);
    }

    /**
     * Gets the time required to cure the disease.
     *
     * @return the time required to cure the disease
     */
    public int getDuration() {
        return Integer.valueOf(this.txtDuration.getText());
    }

    /**
     * Gets the probability to spread an infection within a single round.
     *
     * @return the probability to spread an infection within a single round
     */
    public double getTransmissionRate() {
        return Double.valueOf(this.txtTransmissionRate.getText());
    }

    /**
     * Gets the costs per round to care for an infected direct connection.
     *
     * @return the costs per round to care for an infected direct connection
     */
    public double getTreatmentCosts() {
        return Double.valueOf(this.txtTreatmentCosts.getText());
    }

}
