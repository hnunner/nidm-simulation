package nl.uu.socnetid.nidm.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.mains.UserInterface;
import nl.uu.socnetid.nidm.stats.GlobalAgentStats;
import nl.uu.socnetid.nidm.stats.GlobalNetworkStats;
import nl.uu.socnetid.nidm.stats.GlobalSimulationStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.utilities.Utility;
import nl.uu.socnetid.nidm.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class StatsFrame extends JFrame {

    private static final long serialVersionUID = -5532614279437810025L;

    private static final String NA_STRING = "---";
    private static final boolean SHOW_DIAMETER_AND_AV_DISTANCE = false;

    // labels global stats
    // utility
    private JLabel lblGlobalUtilityFunction;
    private JLabel lblGlobalAlpha;
    private JLabel lblGlobalBeta;
    private JLabel lblGlobalC;
    // disease
    private JLabel lblGlobalDiseaseType;
    private JLabel lblGlobalTau;
    private JLabel lblGlobalS;
    private JLabel lblGlobalGamma;
    private JLabel lblGlobalMu;
    // agents
    private JLabel lblGlobalAgentsOverall;
    private JLabel lblGlobalSusceptibles;
    private JLabel lblGlobalInfected;
    private JLabel lblGlobalRecovered;
    private JLabel lblGlobalRSigmaAverse;
    private JLabel lblGlobalRSigmaNeutrals;
    private JLabel lblGlobalRSigmaSeeking;
    private JLabel lblGlobalAvRSigma;
    private JLabel lblGlobalRPiAverse;
    private JLabel lblGlobalRPiNeutrals;
    private JLabel lblGlobalRPiSeeking;
    private JLabel lblGlobalAvRPi;
    // network
    private JLabel lblGlobalStable;
    private JLabel lblGlobalConnections;
    private JLabel lblGlobalAvDegree;
    private JLabel lblGlobalDiameter;
    private JLabel lblGlobalAvDistance;
    // simulation
    private JLabel lblGlobalSimulationRunning;
    private JLabel lblGlobalSimulationRound;
    private JLabel lblGlobalSimulationRecording;

    // labels agent stats
    private JLabel lblAgentID;
    private JLabel lblAgentSatisfied;
    private JLabel lblAgentUtility;
    // benefit
    private JLabel lblAgentBenefit;
    private JLabel lblAgentBenefitDirect;
    private JLabel lblAgentBenefitIndirect;
    // costs
    private JLabel lblAgentCosts;
    private JLabel lblAgentCostsDirect;
    private JLabel lblAgentCostsDisease;
    // disease
    private JLabel lblAgentDiseaseGroup;
    private JLabel lblAgentDiseaseTimeRemaining;
    // risk behavior
    private JLabel lblAgentRSigma;
    private JLabel lblAgentRSigmaMeaning;
    private JLabel lblAgentRPi;
    private JLabel lblAgentRPiMeaning;
    // network
    private JLabel lblAgentFirstOrderDegree;
    private JLabel lblAgentCloseness;
    private JLabel lblAgentSecondOrderDegree;
    // connections
    private JLabel lblAgentTiesBrokenActive;
    private JLabel lblAgentTiesBrokenPassive;
    private JLabel lblAgentAcceptedTiesOut;
    private JLabel lblAgentDeclinedTiesOut;
    private JLabel lblAgentAcceptedTiesIn;
    private JLabel lblAgentDeclinedTiesIn;

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
        setResizable(false);
        initialize();
    }

    /**
     * Initializes the window frame.
     */
    private void initialize() {

        this.setBounds(1200, 10, 400, 600);
        switch (UserInterface.osType) {
            case WIN:
                this.setBounds(1200, 10, 407, 607);
                break;
            case MAC:
            case OTHER:
            case UNIX:
            default:
                break;
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(203, 6, 192, 566);
        getContentPane().add(panel);

        JLabel label = new JLabel("Agent Stats");
        label.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        label.setBounds(6, 6, 113, 19);
        panel.add(label);

        JLabel label_1 = new JLabel("ID");
        label_1.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_1.setBounds(10, 30, 61, 16);
        panel.add(label_1);

        lblAgentID = new JLabel(NA_STRING);
        lblAgentID.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgentID.setBounds(130, 30, 61, 16);
        panel.add(lblAgentID);

        lblAgentDiseaseGroup = new JLabel(NA_STRING);
        lblAgentDiseaseGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentDiseaseGroup.setBounds(130, 180, 61, 16);
        panel.add(lblAgentDiseaseGroup);

        JLabel label_14 = new JLabel("Group");
        label_14.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_14.setBounds(20, 180, 97, 16);
        panel.add(label_14);

        JLabel label_19 = new JLabel("Time remaining");
        label_19.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_19.setBounds(20, 195, 97, 16);
        panel.add(label_19);

        lblAgentDiseaseTimeRemaining = new JLabel(NA_STRING);
        lblAgentDiseaseTimeRemaining.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentDiseaseTimeRemaining.setBounds(130, 195, 61, 16);
        panel.add(lblAgentDiseaseTimeRemaining);

        JLabel label_25 = new JLabel("Risk behavior");
        label_25.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_25.setBounds(10, 210, 97, 16);
        panel.add(label_25);

        JLabel lblDiseaseSeverity = new JLabel("Disease severity");
        lblDiseaseSeverity.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDiseaseSeverity.setBounds(20, 225, 97, 16);
        panel.add(lblDiseaseSeverity);

        lblAgentRSigma = new JLabel(NA_STRING);
        lblAgentRSigma.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentRSigma.setBounds(130, 225, 61, 16);
        panel.add(lblAgentRSigma);

        JLabel label_28 = new JLabel("Meaning");
        label_28.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_28.setBounds(20, 240, 97, 16);
        panel.add(label_28);

        lblAgentRSigmaMeaning = new JLabel(NA_STRING);
        lblAgentRSigmaMeaning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentRSigmaMeaning.setBounds(130, 240, 61, 16);
        panel.add(lblAgentRSigmaMeaning);

        JLabel lblstOrderDegree = new JLabel("1st order degree");
        lblstOrderDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblstOrderDegree.setBounds(20, 300, 97, 16);
        panel.add(lblstOrderDegree);

        lblAgentFirstOrderDegree = new JLabel(NA_STRING);
        lblAgentFirstOrderDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentFirstOrderDegree.setBounds(130, 300, 61, 16);
        panel.add(lblAgentFirstOrderDegree);

        lblAgentCloseness = new JLabel(NA_STRING);
        lblAgentCloseness.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentCloseness.setBounds(130, 330, 61, 16);
        panel.add(lblAgentCloseness);

        JLabel label_35 = new JLabel("Closeness");
        label_35.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_35.setBounds(20, 330, 97, 16);
        panel.add(label_35);

        JLabel label_36 = new JLabel("Network");
        label_36.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_36.setBounds(10, 285, 97, 16);
        panel.add(label_36);

        JLabel lblndOrderDegree = new JLabel("2nd order degree");
        lblndOrderDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblndOrderDegree.setBounds(20, 315, 97, 16);
        panel.add(lblndOrderDegree);

        lblAgentSecondOrderDegree = new JLabel(NA_STRING);
        lblAgentSecondOrderDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentSecondOrderDegree.setBounds(130, 315, 61, 16);
        panel.add(lblAgentSecondOrderDegree);

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

        lblAgentBenefitDirect = new JLabel(NA_STRING);
        lblAgentBenefitDirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentBenefitDirect.setBounds(130, 90, 56, 16);
        panel.add(lblAgentBenefitDirect);

        JLabel lblIndirectConnections = new JLabel("Indirect connections");
        lblIndirectConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblIndirectConnections.setBounds(20, 105, 99, 16);
        panel.add(lblIndirectConnections);

        JLabel label_41 = new JLabel(":");
        label_41.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_41.setBounds(117, 105, 10, 16);
        panel.add(label_41);

        lblAgentBenefitIndirect = new JLabel(NA_STRING);
        lblAgentBenefitIndirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentBenefitIndirect.setBounds(130, 105, 56, 16);
        panel.add(lblAgentBenefitIndirect);

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

        lblAgentCostsDirect = new JLabel(NA_STRING);
        lblAgentCostsDirect.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentCostsDirect.setBounds(130, 135, 56, 16);
        panel.add(lblAgentCostsDirect);

        JLabel lblEffectOfDisease = new JLabel("Effect of disease");
        lblEffectOfDisease.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblEffectOfDisease.setBounds(20, 150, 99, 16);
        panel.add(lblEffectOfDisease);

        JLabel label_53 = new JLabel(":");
        label_53.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_53.setBounds(117, 150, 10, 16);
        panel.add(label_53);

        lblAgentCostsDisease = new JLabel(NA_STRING);
        lblAgentCostsDisease.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentCostsDisease.setBounds(130, 150, 56, 16);
        panel.add(lblAgentCostsDisease);

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

        lblAgentUtility = new JLabel(NA_STRING);
        lblAgentUtility.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgentUtility.setBounds(130, 60, 61, 16);
        panel.add(lblAgentUtility);

        JLabel label_52 = new JLabel(":");
        label_52.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_52.setBounds(117, 240, 10, 16);
        panel.add(label_52);

        JLabel label_57 = new JLabel(":");
        label_57.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_57.setBounds(117, 300, 10, 16);
        panel.add(label_57);

        JLabel label_58 = new JLabel(":");
        label_58.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_58.setBounds(117, 330, 10, 16);
        panel.add(label_58);

        JLabel label_59 = new JLabel(":");
        label_59.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_59.setBounds(117, 315, 10, 16);
        panel.add(label_59);

        JLabel lblr_4 = new JLabel("(r  ):");
        lblr_4.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_4.setBounds(101, 225, 24, 16);
        panel.add(lblr_4);

        JLabel label_24 = new JLabel(":");
        label_24.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_24.setBounds(117, 75, 10, 16);
        panel.add(label_24);

        lblAgentBenefit = new JLabel(NA_STRING);
        lblAgentBenefit.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgentBenefit.setBounds(130, 75, 61, 16);
        panel.add(lblAgentBenefit);

        JLabel label_62 = new JLabel(":");
        label_62.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_62.setBounds(117, 120, 10, 16);
        panel.add(label_62);

        lblAgentCosts = new JLabel(NA_STRING);
        lblAgentCosts.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgentCosts.setBounds(130, 120, 61, 16);
        panel.add(lblAgentCosts);

        lblAgentSatisfied = new JLabel(NA_STRING);
        lblAgentSatisfied.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgentSatisfied.setBounds(130, 45, 61, 16);
        panel.add(lblAgentSatisfied);

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

        lblGlobalS = new JLabel(NA_STRING);
        lblGlobalS.setBounds(130, 150, 56, 16);
        panel_1.add(lblGlobalS);
        lblGlobalS.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel lblTransmissionRate = new JLabel("Transmission rate");
        lblTransmissionRate.setBounds(20, 165, 83, 16);
        panel_1.add(lblTransmissionRate);
        lblTransmissionRate.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        lblGlobalGamma = new JLabel(NA_STRING);
        lblGlobalGamma.setBounds(130, 165, 56, 16);
        panel_1.add(lblGlobalGamma);
        lblGlobalGamma.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

        JLabel label_30 = new JLabel("Care fagent");
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

        JLabel lblAgents = new JLabel("Agents");
        lblAgents.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblAgents.setBounds(10, 195, 97, 16);
        panel_1.add(lblAgents);

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

        lblGlobalAgentsOverall = new JLabel(NA_STRING);
        lblGlobalAgentsOverall.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAgentsOverall.setBounds(130, 210, 56, 16);
        panel_1.add(lblGlobalAgentsOverall);

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

        lblGlobalRSigmaAverse = new JLabel(NA_STRING);
        lblGlobalRSigmaAverse.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRSigmaAverse.setBounds(130, 270, 56, 16);
        panel_1.add(lblGlobalRSigmaAverse);

        JLabel lblAverageRiskFagent = new JLabel("∅ Risk fagent");
        lblAverageRiskFagent.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAverageRiskFagent.setBounds(20, 315, 97, 16);
        panel_1.add(lblAverageRiskFagent);

        JLabel lblRiskSeeking = new JLabel("Risk seeking");
        lblRiskSeeking.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRiskSeeking.setBounds(20, 300, 99, 16);
        panel_1.add(lblRiskSeeking);

        JLabel lblRiskNeutral = new JLabel("Risk neutral");
        lblRiskNeutral.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRiskNeutral.setBounds(20, 285, 89, 16);
        panel_1.add(lblRiskNeutral);

        lblGlobalRSigmaNeutrals = new JLabel(NA_STRING);
        lblGlobalRSigmaNeutrals.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRSigmaNeutrals.setBounds(130, 285, 56, 16);
        panel_1.add(lblGlobalRSigmaNeutrals);

        lblGlobalRSigmaSeeking = new JLabel(NA_STRING);
        lblGlobalRSigmaSeeking.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRSigmaSeeking.setBounds(130, 300, 56, 16);
        panel_1.add(lblGlobalRSigmaSeeking);

        lblGlobalAvRSigma = new JLabel(NA_STRING);
        lblGlobalAvRSigma.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvRSigma.setBounds(130, 315, 56, 16);
        panel_1.add(lblGlobalAvRSigma);

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

        JLabel lblr = new JLabel("(r  ):");
        lblr.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr.setBounds(102, 315, 24, 16);
        panel_1.add(lblr);

        JLabel lblr_1 = new JLabel("(r  >1):");
        lblr_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_1.setBounds(89, 270, 33, 16);
        panel_1.add(lblr_1);

        JLabel lblr_2 = new JLabel("(r  =1):");
        lblr_2.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_2.setBounds(89, 285, 35, 16);
        panel_1.add(lblr_2);

        JLabel lblr_3 = new JLabel("(r  <1):");
        lblr_3.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_3.setBounds(89, 300, 35, 16);
        panel_1.add(lblr_3);

        JLabel lblNetwork = new JLabel("Network");
        lblNetwork.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblNetwork.setBounds(10, 390, 97, 16);
        panel_1.add(lblNetwork);

        JLabel lblStable = new JLabel("Stable");
        lblStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStable.setBounds(20, 405, 83, 16);
        panel_1.add(lblStable);

        JLabel label_29 = new JLabel(":");
        label_29.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_29.setBounds(117, 405, 10, 16);
        panel_1.add(label_29);

        lblGlobalStable = new JLabel(NA_STRING);
        lblGlobalStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalStable.setBounds(130, 405, 56, 16);
        panel_1.add(lblGlobalStable);

        JLabel lblConnections = new JLabel("Connections");
        lblConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblConnections.setBounds(20, 420, 67, 16);
        panel_1.add(lblConnections);

        JLabel label_34 = new JLabel(":");
        label_34.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_34.setBounds(117, 420, 10, 16);
        panel_1.add(label_34);

        lblGlobalConnections = new JLabel(NA_STRING);
        lblGlobalConnections.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalConnections.setBounds(130, 420, 56, 16);
        panel_1.add(lblGlobalConnections);

        JLabel lblDegree = new JLabel("∅ Degree");
        lblDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDegree.setBounds(20, 435, 67, 16);
        panel_1.add(lblDegree);

        JLabel label_47 = new JLabel(":");
        label_47.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_47.setBounds(117, 435, 10, 16);
        panel_1.add(label_47);

        lblGlobalAvDegree = new JLabel(NA_STRING);
        lblGlobalAvDegree.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvDegree.setBounds(130, 435, 56, 16);
        panel_1.add(lblGlobalAvDegree);

        JLabel lblDiameter = new JLabel("Diameter");
        lblDiameter.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDiameter.setBounds(20, 450, 83, 16);
        lblDiameter.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(lblDiameter);

        JLabel label_50 = new JLabel(":");
        label_50.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_50.setBounds(117, 450, 10, 16);
        label_50.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(label_50);

        lblGlobalDiameter = new JLabel(NA_STRING);
        lblGlobalDiameter.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalDiameter.setBounds(130, 450, 56, 16);
        lblGlobalDiameter.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(lblGlobalDiameter);

        JLabel lblDistance = new JLabel("∅ Distance");
        lblDistance.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDistance.setBounds(20, 465, 83, 16);
        lblDistance.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(lblDistance);

        lblGlobalAvDistance = new JLabel(NA_STRING);
        lblGlobalAvDistance.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvDistance.setBounds(130, 465, 56, 16);
        lblGlobalAvDistance.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(lblGlobalAvDistance);

        JLabel label_27 = new JLabel(":");
        label_27.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_27.setBounds(117, 465, 10, 16);
        label_27.setVisible(SHOW_DIAMETER_AND_AV_DISTANCE);
        panel_1.add(label_27);

        JLabel label_31 = new JLabel(":");
        label_31.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_31.setBounds(117, 120, 10, 16);
        panel_1.add(label_31);

        JLabel lblSimulation = new JLabel("Simulation");
        lblSimulation.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblSimulation.setBounds(10, SHOW_DIAMETER_AND_AV_DISTANCE ? 480 : 480 - (2*15), 97, 16);
        panel_1.add(lblSimulation);

        JLabel lblSimRunning = new JLabel("Running");
        lblSimRunning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRunning.setBounds(20, SHOW_DIAMETER_AND_AV_DISTANCE ? 495 : 495 - (2*15), 83, 16);
        panel_1.add(lblSimRunning);

        JLabel lblSimRunningColon = new JLabel(":");
        lblSimRunningColon.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRunningColon.setBounds(117, SHOW_DIAMETER_AND_AV_DISTANCE ? 495 : 495 - (2*15),10, 16);
        panel_1.add(lblSimRunningColon);

        lblGlobalSimulationRunning = new JLabel("no");
        lblGlobalSimulationRunning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalSimulationRunning.setBounds(130, SHOW_DIAMETER_AND_AV_DISTANCE ? 495 : 495 - (2*15), 56, 16);
        panel_1.add(lblGlobalSimulationRunning);

        JLabel lblSimRound = new JLabel("Time step");
        lblSimRound.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRound.setBounds(20, SHOW_DIAMETER_AND_AV_DISTANCE ? 510 : 510 - (2*15), 83, 16);
        panel_1.add(lblSimRound);

        JLabel lblSimRoundColon = new JLabel(":");
        lblSimRoundColon.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRoundColon.setBounds(117, SHOW_DIAMETER_AND_AV_DISTANCE ? 510 : 510 - (2*15), 10, 16);
        panel_1.add(lblSimRoundColon);

        lblGlobalSimulationRound = new JLabel("---");
        lblGlobalSimulationRound.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalSimulationRound.setBounds(130, SHOW_DIAMETER_AND_AV_DISTANCE ? 510 : 510 - (2*15), 56, 16);
        panel_1.add(lblGlobalSimulationRound);

        lblGlobalSimulationRecording = new JLabel("no");
        lblGlobalSimulationRecording.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalSimulationRecording.setBounds(130, SHOW_DIAMETER_AND_AV_DISTANCE ? 525 : 525 - (2*15), 56, 16);
        panel_1.add(lblGlobalSimulationRecording);

        JLabel lblSimRecordingColon = new JLabel(":");
        lblSimRecordingColon.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRecordingColon.setBounds(117, SHOW_DIAMETER_AND_AV_DISTANCE ? 525 : 525 - (2*15), 10, 16);
        panel_1.add(lblSimRecordingColon);

        JLabel lblSimRecording = new JLabel("Recording");
        lblSimRecording.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSimRecording.setBounds(20, SHOW_DIAMETER_AND_AV_DISTANCE ? 525 : 525 - (2*15), 83, 16);
        panel_1.add(lblSimRecording);

        JLabel label_55 = new JLabel("Risk averse");
        label_55.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_55.setBounds(20, 330, 83, 16);
        panel_1.add(label_55);

        lblGlobalRPiAverse = new JLabel("---");
        lblGlobalRPiAverse.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRPiAverse.setBounds(130, 330, 56, 16);
        panel_1.add(lblGlobalRPiAverse);

        JLabel label_68 = new JLabel("∅ Risk fagent");
        label_68.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_68.setBounds(20, 375, 97, 16);
        panel_1.add(label_68);

        JLabel label_74 = new JLabel("Risk seeking");
        label_74.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_74.setBounds(20, 360, 99, 16);
        panel_1.add(label_74);

        JLabel label_75 = new JLabel("Risk neutral");
        label_75.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_75.setBounds(20, 345, 89, 16);
        panel_1.add(label_75);

        lblGlobalRPiNeutrals = new JLabel("---");
        lblGlobalRPiNeutrals.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRPiNeutrals.setBounds(130, 345, 56, 16);
        panel_1.add(lblGlobalRPiNeutrals);

        lblGlobalRPiSeeking = new JLabel("---");
        lblGlobalRPiSeeking.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalRPiSeeking.setBounds(130, 360, 56, 16);
        panel_1.add(lblGlobalRPiSeeking);

        lblGlobalAvRPi = new JLabel("---");
        lblGlobalAvRPi.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblGlobalAvRPi.setBounds(130, 375, 56, 16);
        panel_1.add(lblGlobalAvRPi);

        JLabel lblr_8 = new JLabel("(r  ):");
        lblr_8.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_8.setBounds(102, 375, 24, 16);
        panel_1.add(lblr_8);

        JLabel lblr_7 = new JLabel("(r  >1):");
        lblr_7.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_7.setBounds(89, 330, 33, 16);
        panel_1.add(lblr_7);

        JLabel lblr_6 = new JLabel("(r  =1):");
        lblr_6.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_6.setBounds(89, 345, 35, 16);
        panel_1.add(lblr_6);

        JLabel lblr_9 = new JLabel("(r  <1):");
        lblr_9.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_9.setBounds(89, 360, 35, 16);
        panel_1.add(lblr_9);

        JLabel label_80 = new JLabel("π");
        label_80.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_80.setBounds(95, 336, 10, 11);
        panel_1.add(label_80);

        JLabel label_81 = new JLabel("π");
        label_81.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_81.setBounds(95, 351, 10, 11);
        panel_1.add(label_81);

        JLabel label_82 = new JLabel("π");
        label_82.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_82.setBounds(95, 366, 10, 11);
        panel_1.add(label_82);

        JLabel label_83 = new JLabel("π");
        label_83.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_83.setBounds(108, 381, 10, 11);
        panel_1.add(label_83);

        JLabel label_84 = new JLabel("σ");
        label_84.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_84.setBounds(95, 276, 10, 11);
        panel_1.add(label_84);

        JLabel label_85 = new JLabel("σ");
        label_85.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_85.setBounds(95, 291, 10, 11);
        panel_1.add(label_85);

        JLabel label_86 = new JLabel("σ");
        label_86.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_86.setBounds(95, 306, 10, 11);
        panel_1.add(label_86);

        JLabel label_79 = new JLabel("σ");
        label_79.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_79.setBounds(108, 321, 10, 11);
        panel_1.add(label_79);

        JLabel label_42 = new JLabel("Connections");
        label_42.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        label_42.setBounds(10, 345, 97, 16);
        panel.add(label_42);

        JLabel label_48 = new JLabel("Broken (active)");
        label_48.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_48.setBounds(20, 360, 97, 16);
        panel.add(label_48);

        JLabel label_63 = new JLabel(":");
        label_63.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_63.setBounds(117, 360, 10, 16);
        panel.add(label_63);

        lblAgentTiesBrokenActive = new JLabel("---");
        lblAgentTiesBrokenActive.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentTiesBrokenActive.setBounds(130, 360, 61, 16);
        panel.add(lblAgentTiesBrokenActive);

        JLabel label_49 = new JLabel("Broken (passive)");
        label_49.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_49.setBounds(20, 375, 97, 16);
        panel.add(label_49);

        JLabel label_64 = new JLabel(":");
        label_64.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_64.setBounds(117, 375, 10, 16);
        panel.add(label_64);

        lblAgentTiesBrokenPassive = new JLabel("---");
        lblAgentTiesBrokenPassive.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentTiesBrokenPassive.setBounds(130, 375, 61, 16);
        panel.add(lblAgentTiesBrokenPassive);

        JLabel label_51 = new JLabel("Accepted (out)");
        label_51.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_51.setBounds(20, 390, 97, 16);
        panel.add(label_51);

        JLabel label_65 = new JLabel(":");
        label_65.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_65.setBounds(117, 390, 10, 16);
        panel.add(label_65);

        lblAgentAcceptedTiesOut = new JLabel("---");
        lblAgentAcceptedTiesOut.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentAcceptedTiesOut.setBounds(130, 390, 61, 16);
        panel.add(lblAgentAcceptedTiesOut);

        JLabel label_66 = new JLabel("Declined (out)");
        label_66.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_66.setBounds(20, 405, 97, 16);
        panel.add(label_66);

        JLabel label_67 = new JLabel(":");
        label_67.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_67.setBounds(117, 405, 10, 16);
        panel.add(label_67);

        lblAgentDeclinedTiesOut = new JLabel("---");
        lblAgentDeclinedTiesOut.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentDeclinedTiesOut.setBounds(130, 405, 61, 16);
        panel.add(lblAgentDeclinedTiesOut);

        JLabel label_69 = new JLabel("Accepted (in)");
        label_69.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_69.setBounds(20, 420, 97, 16);
        panel.add(label_69);

        JLabel label_70 = new JLabel(":");
        label_70.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_70.setBounds(117, 420, 10, 16);
        panel.add(label_70);

        lblAgentAcceptedTiesIn = new JLabel("---");
        lblAgentAcceptedTiesIn.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentAcceptedTiesIn.setBounds(130, 420, 61, 16);
        panel.add(lblAgentAcceptedTiesIn);

        JLabel label_72 = new JLabel("Declined (in)");
        label_72.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_72.setBounds(20, 435, 97, 16);
        panel.add(label_72);

        JLabel label_73 = new JLabel(":");
        label_73.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_73.setBounds(117, 435, 10, 16);
        panel.add(label_73);

        lblAgentDeclinedTiesIn = new JLabel("---");
        lblAgentDeclinedTiesIn.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentDeclinedTiesIn.setBounds(130, 435, 61, 16);
        panel.add(lblAgentDeclinedTiesIn);

        JLabel lblProbabilityInfection = new JLabel("Probability inf.");
        lblProbabilityInfection.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblProbabilityInfection.setBounds(20, 255, 97, 16);
        panel.add(lblProbabilityInfection);

        lblAgentRPi = new JLabel("---");
        lblAgentRPi.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentRPi.setBounds(130, 255, 61, 16);
        panel.add(lblAgentRPi);

        JLabel label_61 = new JLabel("Meaning");
        label_61.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_61.setBounds(20, 270, 97, 16);
        panel.add(label_61);

        lblAgentRPiMeaning = new JLabel("---");
        lblAgentRPiMeaning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblAgentRPiMeaning.setBounds(130, 270, 61, 16);
        panel.add(lblAgentRPiMeaning);

        JLabel label_71 = new JLabel(":");
        label_71.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_71.setBounds(117, 270, 10, 16);
        panel.add(label_71);

        JLabel lblr_5 = new JLabel("(r  ):");
        lblr_5.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblr_5.setBounds(101, 255, 24, 16);
        panel.add(lblr_5);

        JLabel label_26 = new JLabel("π");
        label_26.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_26.setBounds(108, 261, 10, 11);
        panel.add(label_26);

        JLabel label_54 = new JLabel("σ");
        label_54.setFont(new Font("Lucida Grande", Font.PLAIN, 7));
        label_54.setBounds(107, 231, 10, 11);
        panel.add(label_54);
    }

    /**
     * Refreshes the global utility stats for agents.
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
     * Refreshes the global disease stats for agents.
     *
     * @param ds
     *          the disease specs
     */
    public void refreshGlobalDiseaseStats(DiseaseSpecs ds) {
        this.lblGlobalDiseaseType.setText(ds.getStatsName());
        this.lblGlobalTau.setText(Integer.toString(ds.getTau()));
        this.lblGlobalS.setText(Double.toString(ds.getS()));
        this.lblGlobalGamma.setText(Double.toString(ds.getGamma()));
        this.lblGlobalMu.setText(Double.toString(ds.getMu()));
    }

    /**
     * Refreshes the global agent stats.
     *
     * @param globalAgentStats
     *          the global agent stats
     */
    public void refreshGlobalAgentStats(GlobalAgentStats globalAgentStats) {
        this.lblGlobalAgentsOverall.setText(Integer.toString(globalAgentStats.getN()));
        this.lblGlobalSusceptibles.setText(Integer.toString(globalAgentStats.getnS()));
        this.lblGlobalInfected.setText(Integer.toString(globalAgentStats.getnI()));
        this.lblGlobalRecovered.setText(Integer.toString(globalAgentStats.getnR()));
        this.lblGlobalRSigmaAverse.setText(Integer.toString(globalAgentStats.getnRSigmaAverse()));
        this.lblGlobalRSigmaNeutrals.setText(Integer.toString(globalAgentStats.getnRSigmaNeutral()));
        this.lblGlobalRSigmaSeeking.setText(Integer.toString(globalAgentStats.getnRSigmaSeeking()));
        this.lblGlobalAvRSigma.setText(Double.toString(globalAgentStats.getAvRSigma()));
        this.lblGlobalRPiAverse.setText(Integer.toString(globalAgentStats.getnRPiAverse()));
        this.lblGlobalRPiNeutrals.setText(Integer.toString(globalAgentStats.getnRPiNeutral()));
        this.lblGlobalRPiSeeking.setText(Integer.toString(globalAgentStats.getnRPiSeeking()));
        this.lblGlobalAvRPi.setText(Double.toString(globalAgentStats.getAvRPi()));
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
     * Refreshes the global simulation stats.
     *
     * @param globalSimulationStats
     *          the global simulation stats
     */
    public void refreshGlobalSimulationStats(GlobalSimulationStats globalSimulationStats) {
        this.lblGlobalSimulationRunning.setText(globalSimulationStats.isRunning() ? "yes" : "no");
        this.lblGlobalSimulationRound.setText(Integer.toString(globalSimulationStats.getRound()));
    }

    public void refreshSimulationRecording(boolean recording) {
        this.lblGlobalSimulationRecording.setText(recording ? "yes" : "no");
    }

    /**
     * Resets the global stats for agents.
     */
    public void resetGlobalAgentStats() {
        // utility
        this.lblGlobalUtilityFunction.setText(NA_STRING);
        this.lblGlobalAlpha.setText(NA_STRING);
        this.lblGlobalBeta.setText(NA_STRING);
        this.lblGlobalC.setText(NA_STRING);

        // disease
        this.lblGlobalDiseaseType.setText(NA_STRING);
        this.lblGlobalTau.setText(NA_STRING);
        this.lblGlobalS.setText(NA_STRING);
        this.lblGlobalGamma.setText(NA_STRING);
        this.lblGlobalMu.setText(NA_STRING);
    }

    /**
     * Refreshes the stats for one specific agent.
     *
     * @param agent
     *          the agent to refresh the stats for
     */
    public void refreshLocalAgentStats(Agent agent) {

        // identifier
        this.lblAgentID.setText(agent.getId());

        // satisfaction
        this.lblAgentSatisfied.setText(agent.isSatisfied() ? "yes" : "no");

        // utility
        Utility utility = agent.getUtility();
        this.lblAgentUtility.setText(Double.toString(utility.getOverallUtility()));

        // benefit
        this.lblAgentBenefit.setText(Double.toString(utility.getBenefitDirectConnections()
                + utility.getBenefitIndirectConnections()));
        this.lblAgentBenefitDirect.setText(Double.toString(utility.getBenefitDirectConnections()));
        this.lblAgentBenefitIndirect.setText(Double.toString(utility.getBenefitIndirectConnections()));

        // costs
        this.lblAgentCosts.setText(Double.toString(utility.getCostsDirectConnections()
                - utility.getEffectOfDisease()));
        this.lblAgentCostsDirect.setText(Double.toString(utility.getCostsDirectConnections()));
        this.lblAgentCostsDisease.setText(Double.toString(utility.getEffectOfDisease()));

        // disease
        this.lblAgentDiseaseGroup.setText(agent.getDiseaseGroup().toString());
        if (agent.isInfected()) {
            this.lblAgentDiseaseTimeRemaining.setText(Integer.toString(agent.getTimeUntilRecovered()));
        } else {
            this.lblAgentDiseaseTimeRemaining.setText(NA_STRING);
        }

        // risk behavior
        double rSigma = agent.getRSigma();
        this.lblAgentRSigma.setText(Double.toString(rSigma));
        if (rSigma < 1) {
            this.lblAgentRSigmaMeaning.setText("seeking");
        } else if (rSigma > 1) {
            this.lblAgentRSigmaMeaning.setText("averse");
        } else {
            this.lblAgentRSigmaMeaning.setText("neutral");
        }
        double rPi = agent.getRPi();
        this.lblAgentRPi.setText(Double.toString(rPi));
        if (rPi < 1) {
            this.lblAgentRPiMeaning.setText("seeking");
        } else if (rPi > 1) {
            this.lblAgentRPiMeaning.setText("averse");
        } else {
            this.lblAgentRPiMeaning.setText("neutral");
        }

        // network
        this.lblAgentFirstOrderDegree.setText(Integer.toString(StatsComputer.computeFirstOrderDegree(agent)));
        this.lblAgentSecondOrderDegree.setText(Integer.toString(StatsComputer.computeSecondOrderDegree(agent)));
        this.lblAgentCloseness.setText(Double.toString(StatsComputer.computeCloseness(agent)));

        // connections
        this.lblAgentTiesBrokenActive.setText(Integer.toString(agent.getConnectionStats().getBrokenTiesActive()));
        this.lblAgentTiesBrokenPassive.setText(Integer.toString(agent.getConnectionStats().getBrokenTiesPassive()));
        this.lblAgentAcceptedTiesOut.setText(Integer.toString(agent.getConnectionStats().getAcceptedRequestsOut()));
        this.lblAgentDeclinedTiesOut.setText(Integer.toString(agent.getConnectionStats().getDeclinedRequestsOut()));
        this.lblAgentAcceptedTiesIn.setText(Integer.toString(agent.getConnectionStats().getAcceptedRequestsIn()));
        this.lblAgentDeclinedTiesIn.setText(Integer.toString(agent.getConnectionStats().getDeclinedRequestsIn()));
    }
}
