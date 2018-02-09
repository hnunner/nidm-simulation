package nl.uu.socnetid.network_games.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.network_games.players.Player;

/**
 * @author Hendrik Nunner
 */
public class StatsFrame extends JFrame {

    private static final long serialVersionUID = -336388312396369016L;

    private static final String NA_STRING = "---";

    private JLabel lblActorID;
    private JLabel lblActorUtilityFunction;
    private JLabel lblActorAlpha;
    private JLabel lblActorBeta;
    private JLabel lblActorC;
    private JLabel lblActorDiseaseGroup;
    private JLabel lblActorDiseaseType;
    private JLabel lblActorTau;
    private JLabel lblActorDiseaseTimeRemaining;
    private JLabel lblActorDelta;
    private JLabel lblActorGamma;
    private JLabel lblActorR;
    private JLabel lblActorRMeaning;
    private JLabel lblActorMu;
    private JLabel lblActorDegree;
    private JLabel lblActorCloseness;
    private JLabel lblActorBetweenness;

    public StatsFrame() {
        super();
        initialize();
    }

    public StatsFrame(String title) {
        super(title);
        initialize();
    }

    private void initialize() {

        this.setBounds(400, 100, 350, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(190, 6, 134, 488);
        getContentPane().add(panel);

        JLabel label = new JLabel("Actor Stats");
        label.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        label.setBounds(6, 6, 113, 19);
        panel.add(label);

        JLabel label_1 = new JLabel("ID");
        label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_1.setBounds(10, 30, 61, 16);
        panel.add(label_1);

        lblActorID = new JLabel(NA_STRING);
        lblActorID.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorID.setBounds(99, 37, 61, 16);
        panel.add(lblActorID);

        JLabel label_3 = new JLabel("Utility");
        label_3.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_3.setBounds(10, 45, 97, 16);
        panel.add(label_3);

        lblActorUtilityFunction = new JLabel(NA_STRING);
        lblActorUtilityFunction.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorUtilityFunction.setBounds(99, 60, 61, 16);
        panel.add(lblActorUtilityFunction);

        JLabel label_5 = new JLabel("Function");
        label_5.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_5.setBounds(20, 60, 97, 16);
        panel.add(label_5);

        lblActorAlpha = new JLabel(NA_STRING);
        lblActorAlpha.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorAlpha.setBounds(99, 75, 61, 16);
        panel.add(lblActorAlpha);

        JLabel label_7 = new JLabel("Dir. benefit");
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_7.setBounds(20, 75, 97, 16);
        panel.add(label_7);

        lblActorBeta = new JLabel(NA_STRING);
        lblActorBeta.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorBeta.setBounds(99, 90, 61, 16);
        panel.add(lblActorBeta);

        JLabel label_9 = new JLabel("Ind. benefit");
        label_9.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_9.setBounds(20, 90, 97, 16);
        panel.add(label_9);

        lblActorC = new JLabel(NA_STRING);
        lblActorC.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorC.setBounds(99, 105, 61, 16);
        panel.add(lblActorC);

        JLabel label_11 = new JLabel("Costs");
        label_11.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_11.setBounds(20, 105, 97, 16);
        panel.add(label_11);

        JLabel label_12 = new JLabel("Disease");
        label_12.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_12.setBounds(10, 120, 97, 16);
        panel.add(label_12);

        lblActorDiseaseGroup = new JLabel(NA_STRING);
        lblActorDiseaseGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDiseaseGroup.setBounds(99, 135, 61, 16);
        panel.add(lblActorDiseaseGroup);

        JLabel label_14 = new JLabel("Group");
        label_14.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_14.setBounds(20, 135, 97, 16);
        panel.add(label_14);

        lblActorDiseaseType = new JLabel(NA_STRING);
        lblActorDiseaseType.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDiseaseType.setBounds(99, 150, 61, 16);
        panel.add(lblActorDiseaseType);

        JLabel label_16 = new JLabel("Type");
        label_16.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_16.setBounds(20, 150, 97, 16);
        panel.add(label_16);

        JLabel label_17 = new JLabel("Recovery time");
        label_17.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_17.setBounds(20, 165, 97, 16);
        panel.add(label_17);

        lblActorTau = new JLabel(NA_STRING);
        lblActorTau.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorTau.setBounds(99, 165, 61, 16);
        panel.add(lblActorTau);

        JLabel label_19 = new JLabel("Time remaining");
        label_19.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_19.setBounds(20, 180, 97, 16);
        panel.add(label_19);

        lblActorDiseaseTimeRemaining = new JLabel(NA_STRING);
        lblActorDiseaseTimeRemaining.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDiseaseTimeRemaining.setBounds(99, 180, 61, 16);
        panel.add(lblActorDiseaseTimeRemaining);

        JLabel label_21 = new JLabel("Severity");
        label_21.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_21.setBounds(20, 195, 97, 16);
        panel.add(label_21);

        lblActorDelta = new JLabel(NA_STRING);
        lblActorDelta.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDelta.setBounds(99, 195, 61, 16);
        panel.add(lblActorDelta);

        JLabel lblTransmissionRate = new JLabel("Transm. rate");
        lblTransmissionRate.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblTransmissionRate.setBounds(20, 210, 97, 16);
        panel.add(lblTransmissionRate);

        lblActorGamma = new JLabel(NA_STRING);
        lblActorGamma.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorGamma.setBounds(99, 210, 61, 16);
        panel.add(lblActorGamma);

        JLabel label_25 = new JLabel("Risk behavior");
        label_25.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_25.setBounds(10, 240, 97, 16);
        panel.add(label_25);

        JLabel label_26 = new JLabel("Risk factor");
        label_26.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_26.setBounds(20, 255, 97, 16);
        panel.add(label_26);

        lblActorR = new JLabel(NA_STRING);
        lblActorR.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorR.setBounds(99, 255, 61, 16);
        panel.add(lblActorR);

        JLabel label_28 = new JLabel("Meaning");
        label_28.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_28.setBounds(20, 270, 97, 16);
        panel.add(label_28);

        lblActorRMeaning = new JLabel(NA_STRING);
        lblActorRMeaning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorRMeaning.setBounds(99, 270, 61, 16);
        panel.add(lblActorRMeaning);

        JLabel label_30 = new JLabel("Care factor");
        label_30.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_30.setBounds(20, 225, 97, 16);
        panel.add(label_30);

        lblActorMu = new JLabel(NA_STRING);
        lblActorMu.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorMu.setBounds(99, 225, 61, 16);
        panel.add(lblActorMu);

        JLabel label_32 = new JLabel("Degree");
        label_32.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_32.setBounds(20, 300, 97, 16);
        panel.add(label_32);

        lblActorDegree = new JLabel(NA_STRING);
        lblActorDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDegree.setBounds(99, 300, 61, 16);
        panel.add(lblActorDegree);

        lblActorCloseness = new JLabel(NA_STRING);
        lblActorCloseness.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorCloseness.setBounds(99, 315, 61, 16);
        panel.add(lblActorCloseness);

        JLabel label_35 = new JLabel("Closeness");
        label_35.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_35.setBounds(20, 315, 97, 16);
        panel.add(label_35);

        JLabel label_36 = new JLabel("Network");
        label_36.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_36.setBounds(10, 285, 97, 16);
        panel.add(label_36);

        JLabel label_37 = new JLabel("Betweenness");
        label_37.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_37.setBounds(20, 330, 97, 16);
        panel.add(label_37);

        lblActorBetweenness = new JLabel(NA_STRING);
        lblActorBetweenness.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorBetweenness.setBounds(99, 330, 61, 16);
        panel.add(lblActorBetweenness);

        JPanel panel_1 = new JPanel();
        panel_1.setLayout(null);
        panel_1.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel_1.setBounds(6, 6, 134, 488);
        getContentPane().add(panel_1);

        JLabel label_39 = new JLabel("Network Stats");
        label_39.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        label_39.setBounds(6, 6, 113, 19);
        panel_1.add(label_39);

        JLabel label_40 = new JLabel("Stable:");
        label_40.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_40.setBounds(16, 30, 71, 16);
        panel_1.add(label_40);

        JLabel label_41 = new JLabel("no");
        label_41.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_41.setBounds(99, 30, 61, 16);
        panel_1.add(label_41);

        JLabel label_42 = new JLabel("ID:");
        label_42.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_42.setBounds(16, 45, 61, 16);
        panel_1.add(label_42);

        JLabel label_43 = new JLabel(NA_STRING);
        label_43.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_43.setBounds(99, 45, 61, 16);
        panel_1.add(label_43);

        JLabel label_44 = new JLabel(NA_STRING);
        label_44.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_44.setBounds(99, 60, 61, 16);
        panel_1.add(label_44);

        JLabel label_45 = new JLabel("Utility function:");
        label_45.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_45.setBounds(16, 60, 97, 16);
        panel_1.add(label_45);
    }

    public void refreshActor(Player player) {
        this.lblActorID.setText(Long.toString(player.getId()));

        // utility
        this.lblActorUtilityFunction.setText(player.getUtilityFunction().getStatsName());
        this.lblActorAlpha.setText(Double.toString(player.getUtilityFunction().getAlpha()));
        this.lblActorBeta.setText(Double.toString(player.getUtilityFunction().getBeta()));
        this.lblActorC.setText(Double.toString(player.getUtilityFunction().getC()));

        // disease
        this.lblActorDiseaseGroup.setText(player.getDiseaseGroup().toString());
        this.lblActorDiseaseType.setText(player.getDiseaseSpecs().getStatsName());
        this.lblActorTau.setText(Integer.toString(player.getDiseaseSpecs().getTau()));
        this.lblActorDelta.setText(Double.toString(player.getDiseaseSpecs().getDelta()));
        this.lblActorGamma.setText(Double.toString(player.getDiseaseSpecs().getGamma()));
        this.lblActorMu.setText(Double.toString(player.getDiseaseSpecs().getMu()));
        if (player.isInfected()) {
            this.lblActorDiseaseTimeRemaining.setText(Integer.toString(player.getTimeUntilRecovered()));
        } else {
            this.lblActorDiseaseTimeRemaining.setText(NA_STRING);
        }

        // risk behavior
        double r = player.getRiskFactor();
        this.lblActorR.setText(Double.toString(r));
        if (r < 1) {
            this.lblActorRMeaning.setText("seeking");
        } else if (r > 1) {
            this.lblActorRMeaning.setText("averse");
        } else {
            this.lblActorRMeaning.setText("neutral");
        }

        // network
        this.lblActorDegree.setText(NA_STRING);
        this.lblActorCloseness.setText(NA_STRING);
        this.lblActorBetweenness.setText(NA_STRING);
    }

}
