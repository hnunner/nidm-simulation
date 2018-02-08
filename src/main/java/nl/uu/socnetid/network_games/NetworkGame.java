package nl.uu.socnetid.network_games;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import nl.uu.socnetid.network_games.disease.Disease;
import nl.uu.socnetid.network_games.disease.SIRDisease;
import nl.uu.socnetid.network_games.gui.CumulativePanel;
import nl.uu.socnetid.network_games.gui.IRTCPanel;
import nl.uu.socnetid.network_games.gui.NodeClick;
import nl.uu.socnetid.network_games.gui.NodeClickListener;
import nl.uu.socnetid.network_games.gui.SIRPanel;
import nl.uu.socnetid.network_games.gui.TruncatedConnectionsPanel;
import nl.uu.socnetid.network_games.network.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener;
import nl.uu.socnetid.network_games.network.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.network.simulation.NetworkSimulation;
import nl.uu.socnetid.network_games.network.simulation.Simulation;
import nl.uu.socnetid.network_games.network.simulation.SimulationCompleteListener;
import nl.uu.socnetid.network_games.network.writer.AdjacencyMatrixWriter;
import nl.uu.socnetid.network_games.network.writer.EdgeListWriter;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.RationalPlayerNode;
import nl.uu.socnetid.network_games.utilities.Cumulative;
import nl.uu.socnetid.network_games.utilities.IRTC;
import nl.uu.socnetid.network_games.utilities.TruncatedConnections;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements SimulationCompleteListener, NodeClickListener, NetworkStabilityListener {

    // general export path
    @SuppressWarnings("unused")
    private static final String EXPORT_PATH = "./network-exports/";

    // network
    private final Network network = new SimpleNetwork();
    // graph
    private Graph graph;

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // swing components
    // frame
    private JFrame frame;
    // utility function combo box and selection
    private JComboBox<String> utilityFunctionCBox;
    private String[] utilityFunctions = {"IRTC", "Cumulative", "Truncated Connections"};
    // edge writer combo box and selection
    private JComboBox<String> edgeWriterCBox;
    private String[] edgeWriters = {"Edge List", "Adjacency Matrix"};
    // spinner for simulation delay
    private JSpinner simulationDelay;
    // disease selection combo box
    private JComboBox<String> diseaseCBox;
    private String[] diseases = {"SIR"};
    // panel for cumulative model settings
    private CumulativePanel cumulativePanel;
    // panel for truncated connections model settings
    private TruncatedConnectionsPanel truncatedConnectionsPanel;
    // panel for Infections Risk Truncated connections model settings
    private IRTCPanel irtcPanel;
    // panel for generic SIR diseases
    private SIRPanel sirPanel;
    // radio button for showing agent info on node click
    private JRadioButton rdbtnShowAgentInfo;
    // radio button for toggling infection on node click
    private JRadioButton rdbtnToggleInfection;
    // status label for stable network
    private JLabel lblStatStable;
    // label for actor ID
    private JLabel lblStatID;
    // risk behavior of player
    private JLabel lblR;
    private JTextField txtR;

    // concurrency for simulation
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;
    private JTextField textField;


    /**
     * Launch the application.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    NetworkGame window = new NetworkGame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public NetworkGame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // init swing frame
        frame = new JFrame();
        frame.setBounds(100, 100, 285, 835);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // panes
        JPanel playerPane = new JPanel();
        playerPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        JPanel exportPane = new JPanel();
        exportPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        // tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(6, 6, 275, 290);
        frame.getContentPane().add(tabbedPane);
        JPanel utilityPane = new JPanel();
        utilityPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.add("Utility", utilityPane);
        utilityPane.setLayout(null);

                utilityFunctionCBox = new JComboBox<String>();
                utilityFunctionCBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        switch (utilityFunctionCBox.getSelectedIndex()) {
                            case 0:
                                irtcPanel.setVisible(true);
                                lblR.setVisible(true);
                                txtR.setVisible(true);
                                cumulativePanel.setVisible(false);
                                truncatedConnectionsPanel.setVisible(false);
                                break;

                            case 1:
                                irtcPanel.setVisible(false);
                                lblR.setVisible(false);
                                txtR.setVisible(false);
                                cumulativePanel.setVisible(true);
                                truncatedConnectionsPanel.setVisible(false);
                                break;

                            case 2:
                                irtcPanel.setVisible(false);
                                lblR.setVisible(false);
                                txtR.setVisible(false);
                                cumulativePanel.setVisible(false);
                                truncatedConnectionsPanel.setVisible(true);
                                break;

                            default:
                                throw new RuntimeException("Undefined utility function!");
                        }
                    }
                });
                utilityFunctionCBox.setBounds(8, 6, 215, 30);
                utilityPane.add(utilityFunctionCBox);

                        irtcPanel = new IRTCPanel();
                        irtcPanel.setBounds(6, 38, 217, 225);
                        utilityPane.add(irtcPanel);

                                cumulativePanel = new CumulativePanel();
                                cumulativePanel.setBounds(6, 38, 217, 225);
                                cumulativePanel.setVisible(false);
                                utilityPane.add(cumulativePanel);

                                        truncatedConnectionsPanel = new TruncatedConnectionsPanel();
                                        truncatedConnectionsPanel.setBounds(6, 38, 217, 225);
                                        truncatedConnectionsPanel.setVisible(false);
                                        utilityPane.add(truncatedConnectionsPanel);
        JPanel diseasePane = new JPanel();
        diseasePane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));


                tabbedPane.add("Disease", diseasePane);
                diseasePane.setLayout(null);

                        diseaseCBox = new JComboBox<String>();
                        diseaseCBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                switch (diseaseCBox.getSelectedIndex()) {
                                    case 0:
                                        sirPanel.setVisible(true);
                                        break;

                                    default:
                                        throw new RuntimeException("Undefined disease!");
                                }
                            }
                        });
                        diseaseCBox.setBounds(8, 6, 215, 30);
                        diseasePane.add(diseaseCBox);

                                        sirPanel = new SIRPanel();
                                        sirPanel.setBounds(6, 38, 217, 225);
                                        sirPanel.setVisible(true);
                                        diseasePane.add(sirPanel);
        tabbedPane.addTab("Players", playerPane);
        playerPane.setLayout(null);

        JButton btnAddPlayer = new JButton("Add Player");
        btnAddPlayer.setBounds(6, 6, 217, 30);
        playerPane.add(btnAddPlayer);

        JButton btnRemovePlayer = new JButton("Remove Player");
        btnRemovePlayer.setBounds(6, 84, 217, 30);
        playerPane.add(btnRemovePlayer);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.setBounds(6, 110, 217, 30);
        playerPane.add(btnClearEdges);

        txtR = new JTextField();
        txtR.setText("1.00");
        txtR.setHorizontalAlignment(SwingConstants.RIGHT);
        txtR.setColumns(10);
        txtR.setBounds(157, 59, 60, 20);
        playerPane.add(txtR);

        lblR = new JLabel("Risk factor");
        lblR.setToolTipText("Risk behavior of the player - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblR.setBounds(16, 61, 96, 16);
        playerPane.add(lblR);

        ButtonGroup btnGroup = new ButtonGroup();

        JPanel pnlNodeClick = new JPanel();
        pnlNodeClick.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        pnlNodeClick.setBounds(10, 179, 207, 81);
        playerPane.add(pnlNodeClick);
        pnlNodeClick.setLayout(null);

                rdbtnToggleInfection = new JRadioButton("Toggle infection");
                rdbtnToggleInfection.setBounds(6, 52, 141, 23);
                pnlNodeClick.add(rdbtnToggleInfection);
                rdbtnToggleInfection.setSelected(false);
                btnGroup.add(rdbtnToggleInfection);

                        rdbtnShowAgentInfo = new JRadioButton("Show agent info");
                        rdbtnShowAgentInfo.setBounds(6, 29, 141, 23);
                        pnlNodeClick.add(rdbtnShowAgentInfo);
                        rdbtnShowAgentInfo.setSelected(true);
                        btnGroup.add(rdbtnShowAgentInfo);

                        JLabel lblOnNodeClick = new JLabel("On node click:");
                        lblOnNodeClick.setBounds(6, 6, 127, 16);
                        pnlNodeClick.add(lblOnNodeClick);

                        JButton btnClearAll = new JButton("Clear All");
                        btnClearAll.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO implement
                            }
                        });
                        btnClearAll.setBounds(6, 136, 217, 30);
                        playerPane.add(btnClearAll);

                        textField = new JTextField();
                        textField.setText("1");
                        textField.setHorizontalAlignment(SwingConstants.RIGHT);
                        textField.setColumns(10);
                        textField.setBounds(157, 36, 60, 20);
                        playerPane.add(textField);

                        JLabel lblAmount = new JLabel("Amount:");
                        lblAmount.setToolTipText("Risk behavior of the player - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
                        lblAmount.setBounds(16, 38, 103, 16);
                        playerPane.add(lblAmount);

                        JLabel lblr = new JLabel("(r):");
                        lblr.setBounds(127, 61, 24, 16);
                        playerPane.add(lblr);

                        JLabel lbln = new JLabel("(N):");
                        lbln.setBounds(127, 38, 24, 16);
                        playerPane.add(lbln);

        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnRemovePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePlayer();
            }
        });
        btnAddPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        tabbedPane.add("Export", exportPane);
        exportPane.setLayout(null);

        edgeWriterCBox = new JComboBox<String>();
        edgeWriterCBox.setBounds(8, 6, 215, 30);
        exportPane.add(edgeWriterCBox);

        JButton btnExportNetwork = new JButton("Export");
        btnExportNetwork.setBounds(6, 35, 217, 30);
        exportPane.add(btnExportNetwork);
        btnExportNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportNetwork();
            }
        });

        JButton btnStart = new JButton("   Start");
        btnStart.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnStart.setIcon(new ImageIcon(getClass().getResource("/start.png")));
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        btnStart.setBounds(10, 318, 259, 35);
        frame.getContentPane().add(btnStart);
        for (int i = 0; i < edgeWriters.length; i++) {
            edgeWriterCBox.addItem(edgeWriters[i]);
        }
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(198, 290, 70, 26);
        frame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (100 ms):");
        simulationDelayLabel.setBounds(16, 295, 170, 16);
        frame.getContentPane().add(simulationDelayLabel);

        JButton btnPauseSimulation = new JButton(" Pause");
        btnPauseSimulation.setIcon(new ImageIcon(getClass().getResource("/pause.png")));
        btnPauseSimulation.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnPauseSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pauseSimulation();
            }
        });
        btnPauseSimulation.setBounds(10, 355, 259, 35);
        frame.getContentPane().add(btnPauseSimulation);

        JPanel pnlGlobalStats = new JPanel();
        pnlGlobalStats.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        pnlGlobalStats.setBounds(6, 434, 134, 372);
        frame.getContentPane().add(pnlGlobalStats);
        pnlGlobalStats.setLayout(null);

        JLabel lblNetworkStats = new JLabel("Network Stats");
        lblNetworkStats.setBounds(6, 6, 113, 19);
        lblNetworkStats.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        pnlGlobalStats.add(lblNetworkStats);

        JLabel lblStable = new JLabel("Network stable:");
        lblStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStable.setBounds(16, 30, 71, 16);
        pnlGlobalStats.add(lblStable);

        lblStatStable = new JLabel("no");
        lblStatStable.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStatStable.setBounds(99, 30, 61, 16);
        pnlGlobalStats.add(lblStatStable);

        JLabel lblID = new JLabel("ID:");
        lblID.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblID.setBounds(16, 45, 61, 16);
        pnlGlobalStats.add(lblID);

        lblStatID = new JLabel("---");
        lblStatID.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStatID.setBounds(99, 45, 61, 16);
        pnlGlobalStats.add(lblStatID);

        JLabel lblStatUtilityFunction = new JLabel("---");
        lblStatUtilityFunction.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStatUtilityFunction.setBounds(99, 60, 61, 16);
        pnlGlobalStats.add(lblStatUtilityFunction);

        JLabel lblUtilityFunction = new JLabel("Utility function:");
        lblUtilityFunction.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblUtilityFunction.setBounds(16, 60, 97, 16);
        pnlGlobalStats.add(lblUtilityFunction);

        JLabel lblStatDiseaseGroup = new JLabel("---");
        lblStatDiseaseGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblStatDiseaseGroup.setBounds(99, 75, 61, 16);
        pnlGlobalStats.add(lblStatDiseaseGroup);

        JLabel lblDiseaseGroup = new JLabel("Disease Group:");
        lblDiseaseGroup.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDiseaseGroup.setBounds(16, 75, 97, 16);
        pnlGlobalStats.add(lblDiseaseGroup);

        JLabel label = new JLabel("---");
        label.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label.setBounds(99, 90, 61, 16);
        pnlGlobalStats.add(label);

        JLabel label_1 = new JLabel("Disease Group:");
        label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_1.setBounds(16, 90, 97, 16);
        pnlGlobalStats.add(label_1);

        JLabel label_2 = new JLabel("---");
        label_2.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_2.setBounds(99, 105, 61, 16);
        pnlGlobalStats.add(label_2);

        JLabel label_3 = new JLabel("Disease Group:");
        label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_3.setBounds(16, 105, 97, 16);
        pnlGlobalStats.add(label_3);

        JLabel label_4 = new JLabel("---");
        label_4.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_4.setBounds(99, 120, 61, 16);
        pnlGlobalStats.add(label_4);

        JLabel label_5 = new JLabel("Disease Group:");
        label_5.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_5.setBounds(16, 120, 97, 16);
        pnlGlobalStats.add(label_5);

        JLabel label_6 = new JLabel("---");
        label_6.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_6.setBounds(99, 135, 61, 16);
        pnlGlobalStats.add(label_6);

        JLabel label_7 = new JLabel("Disease Group:");
        label_7.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_7.setBounds(16, 135, 97, 16);
        pnlGlobalStats.add(label_7);

        JLabel label_8 = new JLabel("---");
        label_8.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_8.setBounds(99, 150, 61, 16);
        pnlGlobalStats.add(label_8);

        JLabel label_9 = new JLabel("Disease Group:");
        label_9.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_9.setBounds(16, 150, 97, 16);
        pnlGlobalStats.add(label_9);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(145, 434, 134, 372);
        frame.getContentPane().add(panel);

        JLabel lblActorStats_1 = new JLabel("Actor Stats");
        lblActorStats_1.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        lblActorStats_1.setBounds(6, 6, 113, 19);
        panel.add(lblActorStats_1);

        JLabel lblId = new JLabel("ID");
        lblId.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblId.setBounds(10, 30, 61, 16);
        panel.add(lblId);

        JLabel label_15 = new JLabel("---");
        label_15.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_15.setBounds(99, 30, 61, 16);
        panel.add(label_15);

        JLabel lblUtility = new JLabel("Utility");
        lblUtility.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblUtility.setBounds(10, 45, 97, 16);
        panel.add(lblUtility);

        JLabel label_18 = new JLabel("---");
        label_18.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_18.setBounds(99, 60, 61, 16);
        panel.add(label_18);

        JLabel lblFunction = new JLabel("Function");
        lblFunction.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblFunction.setBounds(20, 60, 97, 16);
        panel.add(lblFunction);

        JLabel label_20 = new JLabel("---");
        label_20.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_20.setBounds(99, 75, 61, 16);
        panel.add(label_20);

        JLabel lblDirBenefit = new JLabel("Dir. benefit");
        lblDirBenefit.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblDirBenefit.setBounds(20, 75, 97, 16);
        panel.add(lblDirBenefit);

        JLabel label_22 = new JLabel("---");
        label_22.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_22.setBounds(99, 90, 61, 16);
        panel.add(label_22);

        JLabel lblIndBenefit = new JLabel("Ind. benefit");
        lblIndBenefit.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblIndBenefit.setBounds(20, 90, 97, 16);
        panel.add(lblIndBenefit);

        JLabel label_24 = new JLabel("---");
        label_24.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_24.setBounds(99, 105, 61, 16);
        panel.add(label_24);

        JLabel lblCosts = new JLabel("Costs");
        lblCosts.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblCosts.setBounds(20, 105, 97, 16);
        panel.add(lblCosts);

        JLabel lblDisease = new JLabel("Disease");
        lblDisease.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblDisease.setBounds(10, 120, 97, 16);
        panel.add(lblDisease);

        JLabel label_28 = new JLabel("---");
        label_28.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_28.setBounds(99, 135, 61, 16);
        panel.add(label_28);

        JLabel lblType = new JLabel("Group");
        lblType.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblType.setBounds(20, 135, 97, 16);
        panel.add(lblType);

        JLabel label_10 = new JLabel("---");
        label_10.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_10.setBounds(99, 150, 61, 16);
        panel.add(label_10);

        JLabel lblType_1 = new JLabel("Type");
        lblType_1.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblType_1.setBounds(20, 150, 97, 16);
        panel.add(lblType_1);

        JLabel lblRecoveryTime = new JLabel("Recovery time");
        lblRecoveryTime.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblRecoveryTime.setBounds(20, 165, 97, 16);
        panel.add(lblRecoveryTime);

        JLabel label_13 = new JLabel("---");
        label_13.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_13.setBounds(99, 165, 61, 16);
        panel.add(label_13);

        JLabel lblTimeRemaining = new JLabel("Time remaining");
        lblTimeRemaining.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblTimeRemaining.setBounds(20, 180, 97, 16);
        panel.add(lblTimeRemaining);

        JLabel label_16 = new JLabel("---");
        label_16.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_16.setBounds(99, 180, 61, 16);
        panel.add(label_16);

        JLabel lblSeverity = new JLabel("Severity");
        lblSeverity.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblSeverity.setBounds(20, 195, 97, 16);
        panel.add(lblSeverity);

        JLabel label_19 = new JLabel("---");
        label_19.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_19.setBounds(99, 195, 61, 16);
        panel.add(label_19);

        JLabel lblTransmissionRate = new JLabel("Transm. rate");
        lblTransmissionRate.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblTransmissionRate.setBounds(20, 210, 97, 16);
        panel.add(lblTransmissionRate);

        JLabel label_23 = new JLabel("---");
        label_23.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_23.setBounds(99, 210, 61, 16);
        panel.add(label_23);

        JLabel lblRiskBehavior = new JLabel("Risk behavior");
        lblRiskBehavior.setFont(new Font("Lucida Grande", Font.BOLD, 9));
        lblRiskBehavior.setBounds(10, 240, 97, 16);
        panel.add(lblRiskBehavior);

        JLabel lblFactor = new JLabel("Risk factor");
        lblFactor.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblFactor.setBounds(20, 255, 97, 16);
        panel.add(lblFactor);

        JLabel label_27 = new JLabel("---");
        label_27.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_27.setBounds(99, 255, 61, 16);
        panel.add(label_27);

        JLabel lblMeaning = new JLabel("Meaning");
        lblMeaning.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblMeaning.setBounds(20, 270, 97, 16);
        panel.add(lblMeaning);

        JLabel label_30 = new JLabel("---");
        label_30.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_30.setBounds(99, 270, 61, 16);
        panel.add(label_30);

        JLabel lblCareFactor = new JLabel("Care factor");
        lblCareFactor.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        lblCareFactor.setBounds(20, 225, 97, 16);
        panel.add(lblCareFactor);

        JLabel label_26 = new JLabel("---");
        label_26.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
        label_26.setBounds(99, 225, 61, 16);
        panel.add(label_26);

        JButton btnReset = new JButton(" Reset");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO implement
            }
        });
        btnReset.setIcon(new ImageIcon(getClass().getResource("/reset.png")));
        btnReset.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnReset.setBounds(10, 392, 259, 35);
        frame.getContentPane().add(btnReset);


        for (int i = 0; i < diseases.length; i ++) {
            diseaseCBox.addItem(diseases[i]);
        }

        // init graphstream
        this.graph = new SingleGraph("NetworkGames");
        // graph-stream CSS styles and rendering properties
        this.graph.addAttribute("ui.quality");
        this.graph.addAttribute("ui.antialias");
        URL gsStyles = this.getClass().getClassLoader().getResource("graph-stream.css");
        this.graph.addAttribute("ui.stylesheet", "url('file:" + gsStyles.getPath() + "')");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        // show
        Viewer viewer = this.graph.display();
        // init click listener
        NodeClick nodeClickListener = new NodeClick(graph, viewer);
        nodeClickListener.addListener(this);
        this.nodeClickExecutor.submit(nodeClickListener);

        // init network stability listener
        this.network.addListener(this);
    }


    /**
     * Adds a player to the game.
     */
    private void addPlayer() {
        switch (utilityFunctionCBox.getSelectedIndex()) {
            // IRTC -- player including risk behavior
            case 0:
                this.network.addPlayer(
                        RationalPlayerNode.newInstance(this.graph, Double.valueOf(this.txtR.getText())));
                break;

            default:
                this.network.addPlayer(RationalPlayerNode.newInstance(this.graph));
                break;
        }
    }

    /**
     * Removes a player from the game.
     */
    private void removePlayer() {
        this.network.removePlayer();
    }

    /**
     * Clears all edges within the game.
     */
    private void clearEdges() {
        if (this.network != null) {
            this.network.clearConnections();
        }
    }

    /**
     * Runs the actual simulation of the network game.
     */
    private void startSimulation() {

        // initializations
        UtilityFunction utilityFunction = getUtilityFunction();
        NetworkSimulation networkSimulation = new NetworkSimulation(this.network);
        networkSimulation.addListener(this);
        networkSimulation.initUtilityFunction(utilityFunction);
        networkSimulation.initSimulationDelay((Integer) this.simulationDelay.getValue());

        if (simulationTask != null) {
            simulationTask.cancel(true);
        }
        simulationTask = simulationExecutor.submit(networkSimulation);
    }

    /**
     * Pauses the simulation of the network game.
     */
    private void pauseSimulation() {
        simulationTask.cancel(true);
    }

    /**
     * Gets the utility function as selected in the GUI.
     *
     * @return the selected utility function
     */
    private UtilityFunction getUtilityFunction() {
        switch (utilityFunctionCBox.getSelectedIndex()) {
            case 0:
                return new IRTC(this.irtcPanel.getAlpha(),
                        this.irtcPanel.getBeta(),
                        this.irtcPanel.getC(),
                        this.sirPanel.getDelta(),
                        this.sirPanel.getGamma(),
                        this.sirPanel.getMu());

            case 1:
                return new Cumulative(this.cumulativePanel.getDirectBenefit(),
                        this.cumulativePanel.getIndirectBenefit());

            case 2:
                return new TruncatedConnections(this.truncatedConnectionsPanel.getDelta(),
                        this.truncatedConnectionsPanel.getCosts());

            default:
                throw new RuntimeException("Undefined utility function!");
        }
    }

    /**
     * Exports the network into a csv file.
     */
    private void exportNetwork() {

        JFileChooser fileChooser = new JFileChooser();
        int popdownState = fileChooser.showSaveDialog(null);
        if(popdownState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String file = fileChooser.getSelectedFile().getPath();
            String filePath = file.replace(selectedFile.getName(), "");

            NetworkWriter networkWriter = getSelectedNetworkWriter();

            NetworkFileWriter fileWriter = new NetworkFileWriter(filePath, file, networkWriter, network);
            fileWriter.write();
        }
    }

    /**
     * @return the selected network writer
     */
    private NetworkWriter getSelectedNetworkWriter() {
        switch (edgeWriterCBox.getSelectedIndex()) {
            case 0:
                return new EdgeListWriter();

            case 1:
                return new AdjacencyMatrixWriter();

            default:
                throw new RuntimeException("Undefined network writer!");
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.NodeClickListener#notify(
     * nl.uu.socnetid.network_games.gui.NodeClick)
     */
    @Override
    public void notify(NodeClick nodeClick) {
        // show agent info on node click
        if (this.rdbtnShowAgentInfo.isSelected()) {
            this.lblStatID.setText("???");
        }

        // toggle infection on node click
        if (this.rdbtnToggleInfection.isSelected()) {
            this.network.toggleInfection(nodeClick.getClickedNodeId(), getDisease());
        }
    }

    /**
     * @return the selected disease
     */
    private Disease getDisease() {
        switch (diseaseCBox.getSelectedIndex()) {
            case 0:
                return new SIRDisease(
                        this.sirPanel.getTau(),
                        this.sirPanel.getDelta(),
                        this.sirPanel.getGamma(),
                        this.sirPanel.getMu());

            default:
                throw new RuntimeException("Undefined disease type!");
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.SimulationCompleteListener#notify(
     * nl.uu.socnetid.network_games.network.simulation.Simulation)
     */
    @Override
    public void notify(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.networks.NetworkStabilityListener#notify(
     * nl.uu.socnetid.network_games.network.networks.Network)
     */
    @Override
    public void notify(Network network) {

        if (network.isStable()) {
            this.lblStatStable.setText("yes");
        } else {
            this.lblStatStable.setText("no");
        }
    }
}
