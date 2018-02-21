package nl.uu.socnetid.networkgames.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.disease.DiseaseSpecs;
import nl.uu.socnetid.networkgames.stats.GlobalActorStats;
import nl.uu.socnetid.networkgames.stats.GlobalNetworkStats;
import nl.uu.socnetid.networkgames.stats.StatsComputer;
import nl.uu.socnetid.networkgames.utilities.Utility;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class StatsFrame extends JFrame {

    private static final long serialVersionUID = -5532614279437810025L;

    private static final String NA_STRING = "---";

    // labels global stats
    // utility
    private JLabel lblGlobalUtilityFunction;
    private JLabel lblGlobalAlpha;
    private JLabel lblGlobalBeta;
    private JLabel lblGlobalC;
    // disease
    private JLabel lblGlobalDiseaseType;
    private JLabel lblGlobalTau;
    private JLabel lblGlobalDelta;
    private JLabel lblGlobalGamma;
    private JLabel lblGlobalMu;
    // actors
    private JLabel lblGlobalActorsOverall;
    private JLabel lblGlobalSusceptibles;
    private JLabel lblGlobalInfected;
    private JLabel lblGlobalRecovered;
    private JLabel lblGlobalRiskAverse;
    private JLabel lblGlobalRiskNeutrals;
    private JLabel lblGlobalRiskSeeking;
    private JLabel lblGlobalAvRisk;
    // network
    private JLabel lblGlobalStable;
    private JLabel lblGlobalConnections;
    private JLabel lblGlobalAvDegree;
    private JLabel lblGlobalDiameter;
    private JLabel lblGlobalAvDistance;


    // labels actor stats
    private JLabel lblActorID;
    private JLabel lblActorSatisfied;
    private JLabel lblActorUtility;
    // benefit
    private JLabel lblActorBenefit;
    private JLabel lblActorBenefitDirect;
    private JLabel lblActorBenefitIndirect;
    // costs
    private JLabel lblActorCosts;
    private JLabel lblActorCostsDirect;
    private JLabel lblActorCostsDisease;
    // disease
    private JLabel lblActorDiseaseGroup;
    private JLabel lblActorDiseaseTimeRemaining;
    // risk behavior
    private JLabel lblActorR;
    private JLabel lblActorRMeaning;
    // network
    private JLabel lblActorDegree;
    private JLabel lblActorCloseness;
    private JLabel lblActorBetweenness;

    /**
     * Constructor.
     */
    public StatsFrame() {
        super();
        initialize();
    }

    /**
     * Constructor with title settings.
     *
     * @param title
     *          the title of the window.
     */
    public StatsFrame(String title) {
        super(title);
        initialize();
    }

    /**
     * Initializes the window frame.
     */
    private void initialize() {

        this.setBounds(400, 100, 400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(203, 6, 192, 566);
        getContentPane().add(panel);

        JLabel label = new JLabel("Actor Stats");
        label.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        label.setBounds(6, 6, 113, 19);
        panel.add(label);

        JLabel label_1 = new JLabel("ID");
        label_1.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_1.setBounds(10, 30, 61, 16);
        panel.add(label_1);

        lblActorID = new JLabel(NA_STRING);
        lblActorID.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActorID.setBounds(130, 30, 61, 16);
        panel.add(lblActorID);

        lblActorDiseaseGroup = new JLabel(NA_STRING);
        lblActorDiseaseGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDiseaseGroup.setBounds(130, 180, 61, 16);
        panel.add(lblActorDiseaseGroup);

        JLabel label_14 = new JLabel("Group");
        label_14.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_14.setBounds(20, 180, 97, 16);
        panel.add(label_14);

        JLabel label_19 = new JLabel("Time remaining");
        label_19.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_19.setBounds(20, 195, 97, 16);
        panel.add(label_19);

        lblActorDiseaseTimeRemaining = new JLabel(NA_STRING);
        lblActorDiseaseTimeRemaining.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDiseaseTimeRemaining.setBounds(130, 195, 61, 16);
        panel.add(lblActorDiseaseTimeRemaining);

        JLabel label_25 = new JLabel("Risk behavior");
        label_25.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_25.setBounds(10, 210, 97, 16);
        panel.add(label_25);

        JLabel label_26 = new JLabel("Risk factor");
        label_26.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_26.setBounds(20, 225, 97, 16);
        panel.add(label_26);

        lblActorR = new JLabel(NA_STRING);
        lblActorR.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorR.setBounds(130, 225, 61, 16);
        panel.add(lblActorR);

        JLabel label_28 = new JLabel("Meaning");
        label_28.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_28.setBounds(20, 240, 97, 16);
        panel.add(label_28);

        lblActorRMeaning = new JLabel(NA_STRING);
        lblActorRMeaning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorRMeaning.setBounds(130, 240, 61, 16);
        panel.add(lblActorRMeaning);

        JLabel label_32 = new JLabel("Degree");
        label_32.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_32.setBounds(20, 270, 97, 16);
        panel.add(label_32);

        lblActorDegree = new JLabel(NA_STRING);
        lblActorDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorDegree.setBounds(130, 270, 61, 16);
        panel.add(lblActorDegree);

        lblActorCloseness = new JLabel(NA_STRING);
        lblActorCloseness.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorCloseness.setBounds(130, 285, 61, 16);
        panel.add(lblActorCloseness);

        JLabel label_35 = new JLabel("Closeness");
        label_35.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_35.setBounds(20, 285, 97, 16);
        panel.add(label_35);

        JLabel label_36 = new JLabel("Network");
        label_36.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_36.setBounds(10, 255, 97, 16);
        panel.add(label_36);

        JLabel label_37 = new JLabel("Betweenness");
        label_37.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_37.setBounds(20, 300, 97, 16);
        panel.add(label_37);

        lblActorBetweenness = new JLabel(NA_STRING);
        lblActorBetweenness.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorBetweenness.setBounds(130, 300, 61, 16);
        panel.add(lblActorBetweenness);

        JLabel label_2 = new JLabel("Disease");
        label_2.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_2.setBounds(10, 165, 97, 16);
        panel.add(label_2);

        JLabel lblCurrentBenefit = new JLabel("Current benefit");
        lblCurrentBenefit.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblCurrentBenefit.setBounds(10, 75, 97, 16);
        panel.add(lblCurrentBenefit);

        JLabel lblDirectConnections_1 = new JLabel("Direct connections");
        lblDirectConnections_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDirectConnections_1.setBounds(20, 90, 99, 16);
        panel.add(lblDirectConnections_1);

        JLabel label_33 = new JLabel(":");
        label_33.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_33.setBounds(117, 90, 10, 16);
        panel.add(label_33);

        lblActorBenefitDirect = new JLabel(NA_STRING);
        lblActorBenefitDirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorBenefitDirect.setBounds(130, 90, 56, 16);
        panel.add(lblActorBenefitDirect);

        JLabel lblIndirectConnections = new JLabel("Indirect connections");
        lblIndirectConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblIndirectConnections.setBounds(20, 105, 99, 16);
        panel.add(lblIndirectConnections);

        JLabel label_41 = new JLabel(":");
        label_41.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_41.setBounds(117, 105, 10, 16);
        panel.add(label_41);

        lblActorBenefitIndirect = new JLabel(NA_STRING);
        lblActorBenefitIndirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorBenefitIndirect.setBounds(130, 105, 56, 16);
        panel.add(lblActorBenefitIndirect);

        JLabel lblCurrentCosts = new JLabel("Current costs");
        lblCurrentCosts.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblCurrentCosts.setBounds(10, 120, 97, 16);
        panel.add(lblCurrentCosts);

        JLabel lblDirectConnections = new JLabel("Direct connections");
        lblDirectConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDirectConnections.setBounds(20, 135, 91, 16);
        panel.add(lblDirectConnections);

        JLabel label_45 = new JLabel(":");
        label_45.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_45.setBounds(117, 135, 10, 16);
        panel.add(label_45);

        lblActorCostsDirect = new JLabel(NA_STRING);
        lblActorCostsDirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorCostsDirect.setBounds(130, 135, 56, 16);
        panel.add(lblActorCostsDirect);

        JLabel lblEffectOfDisease = new JLabel("Effect of disease");
        lblEffectOfDisease.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblEffectOfDisease.setBounds(20, 150, 99, 16);
        panel.add(lblEffectOfDisease);

        JLabel label_53 = new JLabel(":");
        label_53.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_53.setBounds(117, 150, 10, 16);
        panel.add(label_53);

        lblActorCostsDisease = new JLabel(NA_STRING);
        lblActorCostsDisease.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblActorCostsDisease.setBounds(130, 150, 56, 16);
        panel.add(lblActorCostsDisease);

        JLabel label_4 = new JLabel(":");
        label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_4.setBounds(117, 195, 10, 16);
        panel.add(label_4);

        JLabel label_43 = new JLabel(":");
        label_43.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_43.setBounds(117, 180, 10, 16);
        panel.add(label_43);

        JLabel label_56 = new JLabel(":");
        label_56.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_56.setBounds(117, 30, 10, 16);
        panel.add(label_56);

        JLabel lblUtility = new JLabel("Utility");
        lblUtility.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblUtility.setBounds(10, 60, 61, 16);
        panel.add(lblUtility);

        JLabel label_40 = new JLabel(":");
        label_40.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_40.setBounds(117, 60, 10, 16);
        panel.add(label_40);

        lblActorUtility = new JLabel(NA_STRING);
        lblActorUtility.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActorUtility.setBounds(130, 60, 61, 16);
        panel.add(lblActorUtility);

        JLabel label_52 = new JLabel(":");
        label_52.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_52.setBounds(117, 240, 10, 16);
        panel.add(label_52);

        JLabel label_57 = new JLabel(":");
        label_57.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_57.setBounds(117, 270, 10, 16);
        panel.add(label_57);

        JLabel label_58 = new JLabel(":");
        label_58.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_58.setBounds(117, 285, 10, 16);
        panel.add(label_58);

        JLabel label_59 = new JLabel(":");
        label_59.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_59.setBounds(117, 300, 10, 16);
        panel.add(label_59);

        JLabel label_60 = new JLabel("(r):");
        label_60.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_60.setBounds(108, 225, 24, 16);
        panel.add(label_60);

        JLabel label_24 = new JLabel(":");
        label_24.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_24.setBounds(117, 75, 10, 16);
        panel.add(label_24);

        lblActorBenefit = new JLabel(NA_STRING);
        lblActorBenefit.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActorBenefit.setBounds(130, 75, 61, 16);
        panel.add(lblActorBenefit);

        JLabel label_62 = new JLabel(":");
        label_62.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_62.setBounds(117, 120, 10, 16);
        panel.add(label_62);

        lblActorCosts = new JLabel(NA_STRING);
        lblActorCosts.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActorCosts.setBounds(130, 120, 61, 16);
        panel.add(lblActorCosts);

        lblActorSatisfied = new JLabel(NA_STRING);
        lblActorSatisfied.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActorSatisfied.setBounds(130, 45, 61, 16);
        panel.add(lblActorSatisfied);

        JLabel label_39 = new JLabel(":");
        label_39.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_39.setBounds(117, 45, 10, 16);
        panel.add(label_39);

        JLabel lblSatisfied = new JLabel("Satisfied");
        lblSatisfied.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblSatisfied.setBounds(10, 45, 61, 16);
        panel.add(lblSatisfied);

        JPanel panel_1 = new JPanel();
        panel_1.setLayout(null);
        panel_1.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel_1.setBounds(6, 6, 192, 566);
        getContentPane().add(panel_1);

        JLabel lblGlobalStats = new JLabel("Global Stats");
        lblGlobalStats.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        lblGlobalStats.setBounds(6, 6, 113, 19);
        panel_1.add(lblGlobalStats);

        JLabel label_3 = new JLabel("Utility");
        label_3.setBounds(10, 30, 97, 16);
        panel_1.add(label_3);
        label_3.setFont(new Font("Lucida Grande", Font.BOLD, 9));

        JLabel label_5 = new JLabel("Function");
        label_5.setBounds(20, 45, 67, 16);
        panel_1.add(label_5);
        label_5.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalUtilityFunction = new JLabel(NA_STRING);
        lblGlobalUtilityFunction.setBounds(130, 45, 56, 16);
        panel_1.add(lblGlobalUtilityFunction);
        lblGlobalUtilityFunction.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel lblDirectBenefit = new JLabel("Direct benefit");
        lblDirectBenefit.setBounds(20, 60, 67, 16);
        panel_1.add(lblDirectBenefit);
        lblDirectBenefit.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalAlpha = new JLabel(NA_STRING);
        lblGlobalAlpha.setBounds(130, 60, 56, 16);
        panel_1.add(lblGlobalAlpha);
        lblGlobalAlpha.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel lblIndirectBenefit = new JLabel("Indirect benefit");
        lblIndirectBenefit.setBounds(20, 75, 71, 16);
        panel_1.add(lblIndirectBenefit);
        lblIndirectBenefit.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalBeta = new JLabel(NA_STRING);
        lblGlobalBeta.setBounds(130, 75, 56, 16);
        panel_1.add(lblGlobalBeta);
        lblGlobalBeta.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_11 = new JLabel("Costs");
        label_11.setBounds(20, 90, 67, 16);
        panel_1.add(label_11);
        label_11.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalC = new JLabel(NA_STRING);
        lblGlobalC.setBounds(130, 90, 56, 16);
        panel_1.add(lblGlobalC);
        lblGlobalC.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_12 = new JLabel("Disease");
        label_12.setBounds(10, 105, 97, 16);
        panel_1.add(label_12);
        label_12.setFont(new Font("Lucida Grande", Font.BOLD, 9));

        JLabel label_16 = new JLabel("Type");
        label_16.setBounds(20, 120, 67, 16);
        panel_1.add(label_16);
        label_16.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalDiseaseType = new JLabel(NA_STRING);
        lblGlobalDiseaseType.setBounds(130, 120, 56, 16);
        panel_1.add(lblGlobalDiseaseType);
        lblGlobalDiseaseType.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_17 = new JLabel("Recovery time");
        label_17.setBounds(20, 135, 67, 16);
        panel_1.add(label_17);
        label_17.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalTau = new JLabel(NA_STRING);
        lblGlobalTau.setBounds(130, 135, 56, 16);
        panel_1.add(lblGlobalTau);
        lblGlobalTau.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_21 = new JLabel("Severity");
        label_21.setBounds(20, 150, 67, 16);
        panel_1.add(label_21);
        label_21.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalDelta = new JLabel(NA_STRING);
        lblGlobalDelta.setBounds(130, 150, 56, 16);
        panel_1.add(lblGlobalDelta);
        lblGlobalDelta.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel lblTransmissionRate = new JLabel("Transmission rate");
        lblTransmissionRate.setBounds(20, 165, 83, 16);
        panel_1.add(lblTransmissionRate);
        lblTransmissionRate.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalGamma = new JLabel(NA_STRING);
        lblGlobalGamma.setBounds(130, 165, 56, 16);
        panel_1.add(lblGlobalGamma);
        lblGlobalGamma.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_30 = new JLabel("Care factor");
        label_30.setBounds(20, 180, 67, 16);
        panel_1.add(label_30);
        label_30.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalMu = new JLabel(NA_STRING);
        lblGlobalMu.setBounds(130, 180, 56, 16);
        panel_1.add(lblGlobalMu);
        lblGlobalMu.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_6 = new JLabel("(α):");
        label_6.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_6.setBounds(105, 60, 24, 16);
        panel_1.add(label_6);

        JLabel label_8 = new JLabel("(β):");
        label_8.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_8.setBounds(105, 75, 24, 16);
        panel_1.add(label_8);

        JLabel label_10 = new JLabel("(c):");
        label_10.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_10.setBounds(105, 90, 24, 16);
        panel_1.add(label_10);

        JLabel label_13 = new JLabel("(τ):");
        label_13.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_13.setBounds(105, 135, 24, 16);
        panel_1.add(label_13);

        JLabel label_15 = new JLabel("(δ):");
        label_15.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_15.setBounds(105, 150, 24, 16);
        panel_1.add(label_15);

        JLabel label_18 = new JLabel("(ɣ):");
        label_18.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_18.setBounds(105, 165, 24, 16);
        panel_1.add(label_18);

        JLabel label_20 = new JLabel("(μ):");
        label_20.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_20.setBounds(105, 180, 24, 16);
        panel_1.add(label_20);

        JLabel lblActors = new JLabel("Actors");
        lblActors.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblActors.setBounds(10, 195, 97, 16);
        panel_1.add(lblActors);

        JLabel lblAmountOverall = new JLabel("Amount overall");
        lblAmountOverall.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAmountOverall.setBounds(20, 210, 83, 16);
        panel_1.add(lblAmountOverall);

        JLabel lblSusceptibles = new JLabel("Susceptibles");
        lblSusceptibles.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSusceptibles.setBounds(20, 225, 67, 16);
        panel_1.add(lblSusceptibles);

        JLabel lblInfected = new JLabel("Infected");
        lblInfected.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblInfected.setBounds(20, 240, 67, 16);
        panel_1.add(lblInfected);

        JLabel lblRecovered = new JLabel("Recovered");
        lblRecovered.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRecovered.setBounds(20, 255, 83, 16);
        panel_1.add(lblRecovered);

        JLabel lblRiskAverse = new JLabel("Risk averse");
        lblRiskAverse.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRiskAverse.setBounds(20, 270, 83, 16);
        panel_1.add(lblRiskAverse);

        lblGlobalActorsOverall = new JLabel(NA_STRING);
        lblGlobalActorsOverall.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalActorsOverall.setBounds(130, 210, 56, 16);
        panel_1.add(lblGlobalActorsOverall);

        lblGlobalSusceptibles = new JLabel(NA_STRING);
        lblGlobalSusceptibles.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalSusceptibles.setBounds(130, 225, 56, 16);
        panel_1.add(lblGlobalSusceptibles);

        lblGlobalInfected = new JLabel(NA_STRING);
        lblGlobalInfected.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalInfected.setBounds(130, 240, 56, 16);
        panel_1.add(lblGlobalInfected);

        lblGlobalRecovered = new JLabel(NA_STRING);
        lblGlobalRecovered.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRecovered.setBounds(130, 255, 56, 16);
        panel_1.add(lblGlobalRecovered);

        lblGlobalRiskAverse = new JLabel(NA_STRING);
        lblGlobalRiskAverse.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRiskAverse.setBounds(130, 270, 56, 16);
        panel_1.add(lblGlobalRiskAverse);

        JLabel lblAverageRiskFactor = new JLabel("∅ Risk factor");
        lblAverageRiskFactor.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAverageRiskFactor.setBounds(20, 315, 97, 16);
        panel_1.add(lblAverageRiskFactor);

        JLabel lblRiskSeeking = new JLabel("Risk seeking");
        lblRiskSeeking.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRiskSeeking.setBounds(20, 300, 99, 16);
        panel_1.add(lblRiskSeeking);

        JLabel lblRiskNeutral = new JLabel("Risk neutral");
        lblRiskNeutral.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRiskNeutral.setBounds(20, 285, 89, 16);
        panel_1.add(lblRiskNeutral);

        lblGlobalRiskNeutrals = new JLabel(NA_STRING);
        lblGlobalRiskNeutrals.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRiskNeutrals.setBounds(130, 285, 56, 16);
        panel_1.add(lblGlobalRiskNeutrals);

        lblGlobalRiskSeeking = new JLabel(NA_STRING);
        lblGlobalRiskSeeking.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRiskSeeking.setBounds(130, 300, 56, 16);
        panel_1.add(lblGlobalRiskSeeking);

        lblGlobalAvRisk = new JLabel(NA_STRING);
        lblGlobalAvRisk.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvRisk.setBounds(130, 315, 56, 16);
        panel_1.add(lblGlobalAvRisk);

        JLabel label_7 = new JLabel(":");
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_7.setBounds(117, 45, 10, 16);
        panel_1.add(label_7);

        JLabel label_9 = new JLabel(":");
        label_9.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_9.setBounds(117, 210, 10, 16);
        panel_1.add(label_9);

        JLabel label_22 = new JLabel(":");
        label_22.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_22.setBounds(117, 225, 10, 16);
        panel_1.add(label_22);

        JLabel label_23 = new JLabel(":");
        label_23.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_23.setBounds(117, 240, 10, 16);
        panel_1.add(label_23);

        JLabel label_46 = new JLabel(":");
        label_46.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_46.setBounds(117, 255, 10, 16);
        panel_1.add(label_46);

        JLabel lblr = new JLabel("(r):");
        lblr.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr.setBounds(108, 315, 24, 16);
        panel_1.add(lblr);

        JLabel lblr_1 = new JLabel("(r>1):");
        lblr_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_1.setBounds(95, 270, 33, 16);
        panel_1.add(lblr_1);

        JLabel lblr_2 = new JLabel("(r=1):");
        lblr_2.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_2.setBounds(95, 285, 35, 16);
        panel_1.add(lblr_2);

        JLabel lblr_3 = new JLabel("(r<1):");
        lblr_3.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_3.setBounds(95, 300, 35, 16);
        panel_1.add(lblr_3);

        JLabel lblNetwork = new JLabel("Network");
        lblNetwork.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblNetwork.setBounds(10, 330, 97, 16);
        panel_1.add(lblNetwork);

        JLabel lblStable = new JLabel("Stable");
        lblStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStable.setBounds(20, 345, 83, 16);
        panel_1.add(lblStable);

        JLabel label_29 = new JLabel(":");
        label_29.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_29.setBounds(117, 345, 10, 16);
        panel_1.add(label_29);

        lblGlobalStable = new JLabel(NA_STRING);
        lblGlobalStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalStable.setBounds(130, 345, 56, 16);
        panel_1.add(lblGlobalStable);

        JLabel lblConnections = new JLabel("Connections");
        lblConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblConnections.setBounds(20, 360, 67, 16);
        panel_1.add(lblConnections);

        JLabel label_34 = new JLabel(":");
        label_34.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_34.setBounds(117, 360, 10, 16);
        panel_1.add(label_34);

        lblGlobalConnections = new JLabel(NA_STRING);
        lblGlobalConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalConnections.setBounds(130, 360, 56, 16);
        panel_1.add(lblGlobalConnections);

        JLabel lblDegree = new JLabel("∅ Degree");
        lblDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDegree.setBounds(20, 375, 67, 16);
        panel_1.add(lblDegree);

        JLabel label_47 = new JLabel(":");
        label_47.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_47.setBounds(117, 375, 10, 16);
        panel_1.add(label_47);

        lblGlobalAvDegree = new JLabel(NA_STRING);
        lblGlobalAvDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvDegree.setBounds(130, 375, 56, 16);
        panel_1.add(lblGlobalAvDegree);

        JLabel lblDiameter = new JLabel("Diameter");
        lblDiameter.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDiameter.setBounds(20, 390, 83, 16);
        lblDiameter.setVisible(false);
        panel_1.add(lblDiameter);

        JLabel label_50 = new JLabel(":");
        label_50.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_50.setBounds(117, 390, 10, 16);
        label_50.setVisible(false);
        panel_1.add(label_50);

        lblGlobalDiameter = new JLabel(NA_STRING);
        lblGlobalDiameter.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalDiameter.setBounds(130, 390, 56, 16);
        lblGlobalDiameter.setVisible(false);
        panel_1.add(lblGlobalDiameter);

        JLabel lblDistance = new JLabel("∅ Distance");
        lblDistance.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDistance.setBounds(20, 405, 83, 16);
        lblDistance.setVisible(false);
        panel_1.add(lblDistance);

        lblGlobalAvDistance = new JLabel(NA_STRING);
        lblGlobalAvDistance.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvDistance.setBounds(130, 405, 56, 16);
        lblGlobalAvDistance.setVisible(false);
        panel_1.add(lblGlobalAvDistance);

        JLabel label_27 = new JLabel(":");
        label_27.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_27.setBounds(117, 405, 10, 16);
        label_27.setVisible(false);
        panel_1.add(label_27);

        JLabel label_31 = new JLabel(":");
        label_31.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_31.setBounds(117, 120, 10, 16);
        panel_1.add(label_31);
    }

    /**
     * Refreshes the global utility stats for actors.
     *
     * @param uf
     *          the utility function
     */
    public void refreshGlobalUtilityStats(UtilityFunction uf) {
        this.lblGlobalUtilityFunction.setText(uf.getStatsName());
        this.lblGlobalAlpha.setText(Double.toString(uf.getAlpha()));
        this.lblGlobalBeta.setText(Double.toString(uf.getBeta()));
        this.lblGlobalC.setText(Double.toString(uf.getC()));
    }

    /**
     * Refreshes the global disease stats for actors.
     *
     * @param ds
     *          the disease specs
     */
    public void refreshGlobalDiseaseStats(DiseaseSpecs ds) {
        this.lblGlobalDiseaseType.setText(ds.getStatsName());
        this.lblGlobalTau.setText(Integer.toString(ds.getTau()));
        this.lblGlobalDelta.setText(Double.toString(ds.getDelta()));
        this.lblGlobalGamma.setText(Double.toString(ds.getGamma()));
        this.lblGlobalMu.setText(Double.toString(ds.getMu()));
    }

    /**
     * Refreshes the global actor stats.
     *
     * @param globalActorStats
     *          the global actor stats
     */
    public void refreshGlobalActorStats(GlobalActorStats globalActorStats) {
        this.lblGlobalActorsOverall.setText(Integer.toString(globalActorStats.getActorsOverall()));
        this.lblGlobalSusceptibles.setText(Integer.toString(globalActorStats.getSusceptibles()));
        this.lblGlobalInfected.setText(Integer.toString(globalActorStats.getInfected()));
        this.lblGlobalRecovered.setText(Integer.toString(globalActorStats.getRecovered()));
        this.lblGlobalRiskAverse.setText(Integer.toString(globalActorStats.getRiskAverse()));
        this.lblGlobalRiskNeutrals.setText(Integer.toString(globalActorStats.getRiskNeutrals()));
        this.lblGlobalRiskSeeking.setText(Integer.toString(globalActorStats.getRiskSeeking()));
        this.lblGlobalAvRisk.setText(Double.toString(globalActorStats.getAvRisk()));
    }

    /**
     * Refreshes the global network stats.
     *
     * @param globalNetworkStats
     *          the global network stats
     */
    public void refreshGlobalNetworkStats(GlobalNetworkStats globalNetworkStats) {
        this.lblGlobalStable.setText(globalNetworkStats.isStable() ? "yes" : "no");
        this.lblGlobalConnections.setText(Integer.toString(globalNetworkStats.getConnections()));
        this.lblGlobalAvDegree.setText(Double.toString(globalNetworkStats.getAvDegree()));
        this.lblGlobalDiameter.setText(Integer.toString(globalNetworkStats.getDiameter()));
        this.lblGlobalAvDistance.setText(Double.toString(globalNetworkStats.getAvDistance()));
    }

    /**
     * Resets the global stats for actors.
     */
    public void resetGlobalActorStats() {
        // utility
        this.lblGlobalUtilityFunction.setText(NA_STRING);
        this.lblGlobalAlpha.setText(NA_STRING);
        this.lblGlobalBeta.setText(NA_STRING);
        this.lblGlobalC.setText(NA_STRING);

        // disease
        this.lblGlobalDiseaseType.setText(NA_STRING);
        this.lblGlobalTau.setText(NA_STRING);
        this.lblGlobalDelta.setText(NA_STRING);
        this.lblGlobalGamma.setText(NA_STRING);
        this.lblGlobalMu.setText(NA_STRING);
    }

    /**
     * Refreshes the stats for one specific actor.
     *
     * @param actor
     *          the actor to refresh the stats for
     */
    public void refreshLocalActorStats(Actor actor) {

        // TODO use StatsComputer and LocalActorStats bean to retrieve the stats


        // identifier
        this.lblActorID.setText(Long.toString(actor.getId()));

        // satisfaction
        this.lblActorSatisfied.setText(actor.isSatisfied() ? "yes" : "no");

        // utility
        Utility utility = actor.getUtility();
        this.lblActorUtility.setText(Double.toString(utility.getOverallUtility()));

        // benefit
        this.lblActorBenefit.setText(Double.toString(utility.getBenefitDirectConnections()
                + utility.getBenefitIndirectConnections()));
        this.lblActorBenefitDirect.setText(Double.toString(utility.getBenefitDirectConnections()));
        this.lblActorBenefitIndirect.setText(Double.toString(utility.getBenefitIndirectConnections()));

        // costs
        this.lblActorCosts.setText(Double.toString(utility.getCostsDirectConnections()
                - utility.getEffectOfDisease()));
        this.lblActorCostsDirect.setText(Double.toString(utility.getCostsDirectConnections()));
        this.lblActorCostsDisease.setText(Double.toString(utility.getEffectOfDisease()));

        // disease
        this.lblActorDiseaseGroup.setText(actor.getDiseaseGroup().toString());
        if (actor.isInfected()) {
            this.lblActorDiseaseTimeRemaining.setText(Integer.toString(actor.getTimeUntilRecovered()));
        } else {
            this.lblActorDiseaseTimeRemaining.setText(NA_STRING);
        }

        // risk behavior
        double r = actor.getRiskFactor();
        this.lblActorR.setText(Double.toString(r));
        if (r < 1) {
            this.lblActorRMeaning.setText("seeking");
        } else if (r > 1) {
            this.lblActorRMeaning.setText("averse");
        } else {
            this.lblActorRMeaning.setText("neutral");
        }

        // network
        this.lblActorDegree.setText(Integer.toString(StatsComputer.computeFirstDegree(actor)));
        this.lblActorCloseness.setText(Double.toString(StatsComputer.computeCloseness(actor)));
    }
}
