/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.mains;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentListener;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.gui.BurgerBuskensPanel;
import nl.uu.socnetid.nidm.gui.CarayolRouxPanel;
import nl.uu.socnetid.nidm.gui.CidmPanel;
import nl.uu.socnetid.nidm.gui.CumulativePanel;
import nl.uu.socnetid.nidm.gui.DeactivatablePanel;
import nl.uu.socnetid.nidm.gui.ExportAdjacencyMatrixPanel;
import nl.uu.socnetid.nidm.gui.ExportEdgeListPanel;
import nl.uu.socnetid.nidm.gui.ExportGEXFPanel;
import nl.uu.socnetid.nidm.gui.ExportListener;
import nl.uu.socnetid.nidm.gui.IntegerInputVerifier;
import nl.uu.socnetid.nidm.gui.NodeClick;
import nl.uu.socnetid.nidm.gui.NodeClickListener;
import nl.uu.socnetid.nidm.gui.NunnerBuskens2Panel;
import nl.uu.socnetid.nidm.gui.NunnerBuskensPanel;
import nl.uu.socnetid.nidm.gui.SharedUtilityPanel;
import nl.uu.socnetid.nidm.gui.StatsFrame;
import nl.uu.socnetid.nidm.networks.DisplayableNetwork;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.BurgerBuskens;
import nl.uu.socnetid.nidm.utility.CarayolRoux;
import nl.uu.socnetid.nidm.utility.Cidm;
import nl.uu.socnetid.nidm.utility.Cumulative;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.NunnerBuskens2;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class UserInterface implements NodeClickListener, SimulationListener, AgentListener, ExportListener {

    // LOGGER
    private static final Logger logger = LogManager.getLogger(UserInterface.class);

    // NETWORK
    private final DisplayableNetwork network = new DisplayableNetwork();

    // WINDOWS
    private JFrame controlsFrame = new JFrame("Controls");
    private final StatsFrame statsFrame = new StatsFrame("Statistics");

    // UTILITY
    // selection
    private JComboBox<String> modelTypeCBox;
    private final String[] utilityFunctions = {
            "CIDM",
            "Burger & Buskens (2009)",
            "Carayol & Roux (2009)",
            "Nunner & Buskens (2019)",
            "Nunner & Buskens 2 (2019)"};
    // panels
    private CumulativePanel cumulativePanel = new CumulativePanel();
    private CidmPanel cidmPanel = new CidmPanel();
    private BurgerBuskensPanel bbPanel = new BurgerBuskensPanel();
    private CarayolRouxPanel crPanel = new CarayolRouxPanel();
    private NunnerBuskensPanel nbPanel = new NunnerBuskensPanel();
    private NunnerBuskens2Panel nb2Panel = new NunnerBuskens2Panel();
    private final DeactivatablePanel[] utilityPanels = {cumulativePanel, cidmPanel, bbPanel, crPanel, nbPanel, nb2Panel};

    // AGENT
    // network size
    private JFormattedTextField txtAddAmount;
    private JFormattedTextField txtRemoveAmount;
    // on node click
    private Agent statsAgent;
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private JCheckBox chckbxShowAgentStats;
    private JCheckBox chckbxToggleInfection;
    // behavior during epidemics
    private JRadioButton rbEpDynamic;
    private JRadioButton rbEpStatic;

    // SIMULATION
    private Simulation simulation;
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;
    private JSpinner simulationDelay;

    // EXPORT
    // selection
    private JComboBox<String> exportCBox;
    private final String[] networkWriters = {"GEXF", "Edge list", "Adjacency matrix"};
    // panels
    ExportGEXFPanel gexfPanel;
    ExportAdjacencyMatrixPanel adjacencyMatrixPanel;
    ExportEdgeListPanel edgeListPanel;

    // INPUT VALIDATION
    // network size (N)
    private static final InputVerifier N_VERIFIER = new IntegerInputVerifier(0, 50);
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance();


    /**
     * Launch the application.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {
        logger.info("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                ":: Copyright (C) 2017 - 2019\n" +
                "::     Hendrik Nunner    <h.nunner@gmail.com>\n" +
                "::\n" +
                ":: This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.\n" +
                "::\n" +
                ":: This project is a stand-alone Java program of the Networking during Infectious Diseases Model\n" +
                ":: (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network\n" +
                ":: formation and infectious diseases.\n" +
                "::\n" +
                ":: This program is free software: you can redistribute it and/or modify it under the\n" +
                ":: terms of the GNU General Public License as published by the Free Software Foundation,\n" +
                ":: either version 3 of the License, or (at your option) any later version.\n" +
                "::\n" +
                ":: This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n" +
                ":: without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n" +
                ":: See the GNU General Public License for more details.\n" +
                "::\n" +
                ":: You should have received a copy of the GNU General Public License along with this program.\n" +
                ":: If not, see <http://www.gnu.org/licenses/>.\n" +
                "::\n" +
                ":: References:\n" +
                "::     Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic\n" +
                "::     social networks and infectious diseases. Manuscript sumbitted for publication.\n"
                + "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UserInterface window = new UserInterface();
                    window.statsFrame.setVisible(true);
                    window.controlsFrame.setVisible(true);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public UserInterface() {
        initialize();
    }

    /**
     * Initialize the contents of the controls frame.
     */
    private void initialize() {

        // initialize controls frame
        controlsFrame.getContentPane().setLayout(null);
        controlsFrame.setTitle("NIDM Simulator");
        controlsFrame.setBounds(10, 10, 1060, 740);
        switch (PropertiesHandler.getInstance().getOsType()) {
            case WIN:
                controlsFrame.setBounds(10, 10, 1075, 755);
                break;
            case MAC:
            case OTHER:
            case UNIX:
            default:
                controlsFrame.setBounds(10, 10, 1060, 740);
                break;
        }
        controlsFrame.setResizable(false);
        controlsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //////////// TABBED PANE (MODEL, SIMULATION, EXPORT) ////////////
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(6, 6, 340, 715);
        controlsFrame.getContentPane().add(tabbedPane);

        //////////// MODEL ////////////
        JPanel modelPane = new JPanel();
        modelPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192)));
        tabbedPane.add("Model", modelPane);
        modelPane.setLayout(null);

        cidmPanel.setBounds(3, 51, 312, 615);
        modelPane.add(cidmPanel);

        bbPanel.setBounds(3, 51, 312, 615);
        modelPane.add(bbPanel);

        crPanel.setBounds(3, 51, 312, 615);
        modelPane.add(crPanel);

        nbPanel.setBounds(3, 51, 312, 615);
        modelPane.add(nbPanel);

        nb2Panel.setBounds(3, 51, 312, 615);
        modelPane.add(nb2Panel);

        cumulativePanel.setBounds(3, 51, 312, 615);
        cumulativePanel.setVisible(false);
        modelPane.add(cumulativePanel);

        modelTypeCBox = new JComboBox<String>();
        for (int i = 0; i < utilityFunctions.length; i++) {
            modelTypeCBox.addItem(utilityFunctions[i]);
        }
        modelTypeCBox.setBounds(111, 5, 195, 30);
        modelPane.add(modelTypeCBox);

        JLabel lblModel = new JLabel("Model type:");
        lblModel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblModel.setBounds(16, 10, 83, 16);
        modelPane.add(lblModel);

        JSeparator separator_3 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_3.setForeground(Color.LIGHT_GRAY);
        separator_3.setBounds(3, 41, 312, 10);
        modelPane.add(separator_3);
        modelTypeCBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (modelTypeCBox.getSelectedIndex()) {
                    case 0:
                        cidmPanel.setVisible(true);
                        bbPanel.setVisible(false);
                        crPanel.setVisible(false);
                        nbPanel.setVisible(false);
                        nb2Panel.setVisible(false);
                        cumulativePanel.setVisible(false);
                        network.enableAutoLayout();
                        break;

                    case 1:
                        cidmPanel.setVisible(false);
                        bbPanel.setVisible(true);
                        crPanel.setVisible(false);
                        nbPanel.setVisible(false);
                        nb2Panel.setVisible(false);
                        cumulativePanel.setVisible(false);
                        network.enableAutoLayout();
                        break;

                    case 2:
                        cidmPanel.setVisible(false);
                        bbPanel.setVisible(false);
                        crPanel.setVisible(true);
                        nbPanel.setVisible(false);
                        nb2Panel.setVisible(false);
                        cumulativePanel.setVisible(false);
                        network.disableAutoLayout();
                        break;

                    case 3:
                        cidmPanel.setVisible(false);
                        bbPanel.setVisible(false);
                        crPanel.setVisible(false);
                        nbPanel.setVisible(true);
                        nb2Panel.setVisible(false);
                        cumulativePanel.setVisible(true);
                        network.enableAutoLayout();
                        break;

                    case 4:
                        cidmPanel.setVisible(false);
                        bbPanel.setVisible(false);
                        crPanel.setVisible(false);
                        nbPanel.setVisible(false);
                        nb2Panel.setVisible(true);
                        cumulativePanel.setVisible(false);
                        network.enableAutoLayout();
                        break;

                    case 5:
                        cidmPanel.setVisible(false);
                        bbPanel.setVisible(false);
                        crPanel.setVisible(false);
                        nbPanel.setVisible(false);
                        nb2Panel.setVisible(false);
                        cumulativePanel.setVisible(true);
                        network.enableAutoLayout();
                        break;

                    default:
                        throw new RuntimeException("Undefined utility function!");
                }
            }
        });

        //////////// SIMULATION ////////////
        JPanel simulationPane = new JPanel();
        simulationPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.add("Simulation", simulationPane);
        simulationPane.setLayout(null);

        JButton btnAddAgent = new JButton("Add agent");
        btnAddAgent.setIcon(new ImageIcon(getClass().getResource("/add.png")));
        btnAddAgent.setBounds(40, 35, 110, 30);
        simulationPane.add(btnAddAgent);

        JButton btnRemoveAgent = new JButton("Remove agent");
        btnRemoveAgent.setIcon(new ImageIcon(getClass().getResource("/remove.png")));
        btnRemoveAgent.setBounds(40, 67, 110, 30);
        simulationPane.add(btnRemoveAgent);

        JButton btnClearEdges = new JButton("Clear ties");
        btnClearEdges.setIcon(new ImageIcon(getClass().getResource("/clear.png")));
        btnClearEdges.setBounds(40, 163, 258, 30);
        simulationPane.add(btnClearEdges);

        JButton btnClearAll = new JButton("Clear all");
        btnClearAll.setIcon(new ImageIcon(getClass().getResource("/clearall.png")));
        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        btnClearAll.setBounds(40, 195, 258, 30);
        simulationPane.add(btnClearAll);

        txtAddAmount = new JFormattedTextField(NUM_FORMAT);
        txtAddAmount.setValue(new Integer(1));
        txtAddAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAddAmount.setColumns(10);
        txtAddAmount.setBounds(248, 41, 50, 20);
        txtAddAmount.setInputVerifier(N_VERIFIER);
        simulationPane.add(txtAddAmount);

        JLabel lblAmount = new JLabel("amount");
        lblAmount.setToolTipText("Risk behavior of the agent - r<1: risk seeking, "
                + "r=1: risk neutral, r>1: risk averse");
        lblAmount.setBounds(157, 43, 60, 16);
        simulationPane.add(lblAmount);

        JLabel lbln = new JLabel("(N):");
        lbln.setHorizontalAlignment(SwingConstants.RIGHT);
        lbln.setBounds(215, 43, 24, 16);
        simulationPane.add(lbln);

        JButton btnCreateFullNetwork = new JButton("Create full fetwork (ɩ)");
        btnCreateFullNetwork.setIcon(new ImageIcon(getClass().getResource("/full.png")));
        btnCreateFullNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFullNetwork();
            }
        });
        btnCreateFullNetwork.setBounds(40, 131, 258, 30);
        simulationPane.add(btnCreateFullNetwork);

        JLabel label = new JLabel("amount");
        label.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label.setBounds(157, 74, 60, 16);
        simulationPane.add(label);

        JLabel label_1 = new JLabel("(N):");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setBounds(215, 74, 24, 16);
        simulationPane.add(label_1);

        txtRemoveAmount = new JFormattedTextField(NUM_FORMAT);
        txtRemoveAmount.setValue(new Integer(1));
        txtRemoveAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRemoveAmount.setColumns(10);
        txtRemoveAmount.setBounds(248, 72, 50, 20);
        txtRemoveAmount.setInputVerifier(N_VERIFIER);
        simulationPane.add(txtRemoveAmount);

        JLabel lblNetworkControls = new JLabel("Network controls:");
        lblNetworkControls.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkControls.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkControls.setBounds(16, 10, 142, 16);
        simulationPane.add(lblNetworkControls);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (10 ms):");
        simulationDelayLabel.setBounds(42, 521, 170, 16);
        simulationPane.add(simulationDelayLabel);

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(217, 516, 80, 26);
        simulationPane.add(simulationDelay);
        simulationDelay.setValue(10);

        JButton btnStart = new JButton(" Start");
        btnStart.setBounds(39, 549, 258, 35);
        simulationPane.add(btnStart);
        btnStart.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnStart.setIcon(new ImageIcon(getClass().getResource("/start.png")));

        JButton btnPauseSimulation = new JButton(" Pause");
        btnPauseSimulation.setBounds(39, 586, 258, 35);
        simulationPane.add(btnPauseSimulation);
        btnPauseSimulation.setIcon(new ImageIcon(getClass().getResource("/pause.png")));
        btnPauseSimulation.setFont(new Font("Lucida Grande", Font.PLAIN, 14));

        JButton btnReset = new JButton(" Reset");
        btnReset.setBounds(39, 623, 258, 35);
        simulationPane.add(btnReset);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        });
        btnReset.setIcon(new ImageIcon(getClass().getResource("/reset.png")));
        btnReset.setFont(new Font("Lucida Grande", Font.PLAIN, 14));

        JButton btnInfectRandomAgent = new JButton("Infect random agent");
        btnInfectRandomAgent.setBounds(40, 99, 258, 30);
        simulationPane.add(btnInfectRandomAgent);
        btnInfectRandomAgent.setIcon(new ImageIcon(getClass().getResource("/infect.png")));

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(3, 237, 312, 10);
        simulationPane.add(separator_5);

        JLabel lblOnNodeClick_1 = new JLabel("On node click:");
        lblOnNodeClick_1.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblOnNodeClick_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblOnNodeClick_1.setBounds(16, 247, 238, 16);
        simulationPane.add(lblOnNodeClick_1);

        chckbxShowAgentStats = new JCheckBox("Show agent stats");
        chckbxShowAgentStats.setBounds(38, 272, 141, 23);
        simulationPane.add(chckbxShowAgentStats);
        chckbxShowAgentStats.setSelected(true);

        chckbxToggleInfection = new JCheckBox("Toggle infection");
        chckbxToggleInfection.setBounds(38, 297, 141, 23);
        simulationPane.add(chckbxToggleInfection);
        chckbxToggleInfection.setSelected(false);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.LIGHT_GRAY);
        separator_1.setBounds(3, 500, 312, 10);
        simulationPane.add(separator_1);

        JSeparator separator_2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_2.setForeground(Color.LIGHT_GRAY);
        separator_2.setBounds(3, 504, 312, 10);
        simulationPane.add(separator_2);

        JSeparator separator_6 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_6.setForeground(Color.LIGHT_GRAY);
        separator_6.setBounds(3, 332, 312, 10);
        simulationPane.add(separator_6);

        JLabel lblBehaviorDuringEpidemics = new JLabel("Behavior during epidemics:");
        lblBehaviorDuringEpidemics.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblBehaviorDuringEpidemics.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblBehaviorDuringEpidemics.setBounds(16, 342, 238, 16);
        simulationPane.add(lblBehaviorDuringEpidemics);

        rbEpDynamic = new JRadioButton("Dynamic");
        rbEpDynamic.setSelected(true);
        rbEpDynamic.setBounds(38, 367, 141, 23);
        simulationPane.add(rbEpDynamic);

        rbEpStatic = new JRadioButton("Static");
        rbEpStatic.setSelected(false);
        rbEpStatic.setBounds(38, 392, 141, 23);
        simulationPane.add(rbEpStatic);

        ButtonGroup group = new ButtonGroup();
        group.add(rbEpDynamic);
        group.add(rbEpStatic);

        JSeparator separator_7 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_7.setForeground(Color.LIGHT_GRAY);
        separator_7.setBounds(3, 427, 312, 10);
        simulationPane.add(separator_7);


        btnInfectRandomAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectRandomAgent();
            }
        });
        btnPauseSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pauseSimulation();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnRemoveAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAgent();
            }
        });
        btnAddAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addAgent();
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        });

        //////////// EXPORT ////////////
        JPanel exportPanel = new JPanel();
        exportPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.addTab("Export", null, exportPanel, null);
        exportPanel.setLayout(null);

        JLabel lblExportType = new JLabel("Type:");
        lblExportType.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblExportType.setBounds(16, 10, 83, 16);
        exportPanel.add(lblExportType);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(3, 41, 312, 10);
        exportPanel.add(separator);

        gexfPanel = new ExportGEXFPanel(this.network);
        gexfPanel.setBounds(3, 51, 312, 615);
        exportPanel.add(gexfPanel);

        adjacencyMatrixPanel = new ExportAdjacencyMatrixPanel(this.network);
        adjacencyMatrixPanel.setBounds(3, 51, 312, 615);
        exportPanel.add(adjacencyMatrixPanel);

        edgeListPanel = new ExportEdgeListPanel(this.network);
        edgeListPanel.setBounds(3, 51, 312, 615);
        exportPanel.add(edgeListPanel);

        exportCBox = new JComboBox<String>();
        exportCBox.setBounds(111, 5, 195, 30);
        for (int i = 0; i < networkWriters.length; i++) {
            exportCBox.addItem(networkWriters[i]);
        }
        exportCBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (exportCBox.getSelectedIndex()) {
                    case 0:
                        gexfPanel.setVisible(true);
                        edgeListPanel.setVisible(false);
                        adjacencyMatrixPanel.setVisible(false);
                        break;

                    case 1:
                        gexfPanel.setVisible(false);
                        edgeListPanel.setVisible(true);
                        adjacencyMatrixPanel.setVisible(false);
                        break;

                    case 2:
                        gexfPanel.setVisible(false);
                        edgeListPanel.setVisible(false);
                        adjacencyMatrixPanel.setVisible(true);
                        break;

                    default:
                        throw new RuntimeException("Undefined export type!");
                }
            }
        });
        exportPanel.add(exportCBox);

        //////////// CREATE A UI REPRESENATION OF THE NETWORK ////////////
        // this.network.show();
        JInternalFrame netFrame = new JInternalFrame("");
        netFrame.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        netFrame.setResizable(false);
        netFrame.setMaximizable(false);
        netFrame.setIconifiable(false);
        netFrame.setClosable(false);
        netFrame.setBounds(351, 11, 698, 698);
        switch (PropertiesHandler.getInstance().getOsType()) {
            case WIN:
                netFrame.setBounds(352, 11, 711, 711);
                break;
            case MAC:
            case OTHER:
            case UNIX:
            default:
                netFrame.setBounds(351, 11, 698, 698);
                break;
        }
        netFrame.getContentPane().add(this.network.getViewPanel());
        controlsFrame.getContentPane().add(netFrame);
        netFrame.setVisible(true);
        // making network pane "undraggable"
        BasicInternalFrameUI basicNetFrame = (BasicInternalFrameUI)netFrame.getUI();
        basicNetFrame.setNorthPane(null);

        //////////// LISTENER ////////////
        NodeClick nodeClickListener = new NodeClick(this.network);
        nodeClickListener.addListener(this);
        this.nodeClickExecutor.submit(nodeClickListener);
    }


    //////////// USER DEFINABLE NETWORK PROPERTIES ////////////
    /**
     * Adds a agent to the game.
     */
    private void addAgent() {

        // disable utility and disease panels
        disableUtilityPanels();

        // get disease and utility specs
        DiseaseSpecs ds = getDiseaseSpecs();
        UtilityFunction uf = getUtilityFunction();

        // add each agent with selected utility function and disease specs
        for (int i = 0; i < ((Number)txtAddAmount.getValue()).intValue(); i++) {
            SharedUtilityPanel suPanel;
            switch (modelTypeCBox.getSelectedIndex()) {
                case 0:
                    suPanel = this.cidmPanel;
                    break;
                case 1:
                    suPanel = this.bbPanel;
                    break;
                case 2:
                    suPanel = this.crPanel;
                    break;
                case 3:
                    suPanel = this.nbPanel;
                    break;
                case 4:
                    suPanel = this.nb2Panel;
                    break;
                case 5:
                default:
                    throw new RuntimeException("Undefined utility function!");
            }

            double rSigma = suPanel.getRSigma();
            double rPi = suPanel.getRPi();
            if (suPanel.isRRandom()) {
                double rRandom = ThreadLocalRandom.current().nextDouble(0.0, 2.0);
                rSigma = rRandom;
                rPi = rRandom;
            }
            Agent agent = this.network.addAgent(uf, ds, rSigma, rPi, suPanel.getPhi(),
                    suPanel.getOmega());
            agent.addAgentListener(this);
        }

        // update stats
        if (this.network.getAgents().size() <= 1) {
            this.statsFrame.refreshGlobalUtilityStats(uf);
            this.statsFrame.refreshGlobalDiseaseStats(ds);
        }
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalAgentStats(this.network));
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
        this.statsFrame.refreshGlobalSimulationStats(StatsComputer.computeGlobalSimulationStats(this.simulation));
    }

    /**
     * Removes a agent from the game.
     */
    private void removeAgent() {
        for (int i = 0; i < ((Number)txtRemoveAmount.getValue()).intValue(); i++) {
            if (!this.network.getAgents().isEmpty()) {
                this.network.removeAgent();
            }
        }

        // if there are no more agents in the networks enable utility and disease panels
        if (this.network.getAgents().isEmpty()) {
            this.statsFrame.resetGlobalAgentStats();
            enableUtilityPanels();
        }

        // update stats
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalAgentStats(this.network));
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
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
     * Clears all edges and nodes within the game.
     */
    private void clearAll() {
        clearEdges();
        while (!this.network.getAgents().isEmpty()) {
            removeAgent();
        }
    }

    /**
     * Creates the full network based on the agents available.
     */
    private void createFullNetwork() {
        if (this.network != null) {
            this.network.createFullNetwork();
        }
    }

    /**
     * Infects a random agent.
     */
    private void infectRandomAgent() {
        Agent agent = this.network.getRandomNotInfectedAgent();
        if (agent != null) {
            agent.infect(getDiseaseSpecs());
        }
    }


    //////////// UI GETTER ////////////
    /**
     * Gets the utility function as selected in the GUI.
     *
     * @return the selected utility function
     */
    private UtilityFunction getUtilityFunction() {
        switch (modelTypeCBox.getSelectedIndex()) {
            case 0:
                return new Cidm(
                        this.cidmPanel.getAlpha(),
                        this.cidmPanel.getKappa(),
                        this.cidmPanel.getBeta(),
                        this.cidmPanel.getLamda(),
                        this.cidmPanel.getC());

            case 1:
                return new BurgerBuskens(
                        this.bbPanel.getB1(),
                        this.bbPanel.getB2(),
                        this.bbPanel.getC1(),
                        this.bbPanel.getC2(),
                        this.bbPanel.getC3(),
                        this.bbPanel);

            case 2:
                return new CarayolRoux(
                        this.crPanel.getCrOmega(),
                        this.crPanel.getDelta(),
                        this.crPanel.getC(),
                        this.crPanel);

            case 3:
                return new NunnerBuskens(
                        this.nbPanel.getB1(),
                        this.nbPanel.getB2(),
                        this.nbPanel.getAlpha(),
                        this.nbPanel.getC1(),
                        this.nbPanel.getC2(),
                        this.nbPanel);

            case 4:
                return new NunnerBuskens2(
                        this.nb2Panel.getTPref(),
                        this.nb2Panel.isConsiderTriads(),
                        this.nb2Panel.getAlpha(),
                        this.nb2Panel);

            case 5:
                return new Cumulative(
                        this.cumulativePanel.getDirectBenefit(),
                        this.cumulativePanel.getIndirectBenefit());

            default:
                throw new RuntimeException("Undefined utility function!");
        }
    }

    /**
     * @return the selected disease
     */
    private DiseaseSpecs getDiseaseSpecs() {
        return new DiseaseSpecs(
                DiseaseType.SIR,
                this.cidmPanel.getTau(),
                this.cidmPanel.getSigma(),
                this.cidmPanel.getGamma(),
                this.cidmPanel.getMu());
    }


    //////////// SIMULATION ////////////
    /**
     * Runs the actual simulation of the network game.
     */
    private void startSimulation() {

        // initializations
        // this.simulation = new ThreadedSimulation(this.network);
        this.simulation = new Simulation(this.network, this.rbEpStatic.isSelected());
        this.simulation.addSimulationListener(this);
        this.simulation.setDelay((Integer) this.simulationDelay.getValue());

        if (this.simulationTask != null) {
            this.simulationTask.cancel(true);
        }
        this.simulationTask = this.simulationExecutor.submit(this.simulation);
    }

    /**
     * Pauses the simulation of the network game.
     */
    private void pauseSimulation() {
        this.simulation.pause();
    }

    /**
     * Clears all edges and resets all agents to being susceptible.
     */
    private void resetSimulation() {
        this.network.resetAgents();
    }


    //////////// UI ALTERATIONS ////////////
    /**
     * Enables the utility panel and its subcomponents.
     */
    private void enableUtilityPanels() {
        for (int i = 0; i < utilityPanels.length; i++) {
            utilityPanels[i].enableComponents();
        }
        this.modelTypeCBox.setEnabled(true);
    }

    /**
     * Disables the utility panel and its subcomponents.
     */
    private void disableUtilityPanels() {
        for (int i = 0; i < utilityPanels.length; i++) {
            utilityPanels[i].diseableComponents();
        }
        this.modelTypeCBox.setEnabled(false);
    }


    //////////// LISTENER CALLBACKS ////////////
    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.NodeClickListener#notify(nl.uu.socnetid.nidm.gui.NodeClick)
     */
    @Override
    public void notify(NodeClick nodeClick) {
        String clickAgentId = nodeClick.getClickedNodeId();

        // toggle infection on node click
        if (this.chckbxToggleInfection.isSelected()) {
            this.network.toggleInfection(clickAgentId, getDiseaseSpecs());
        }

        // show agent stats on node click
        if (this.chckbxShowAgentStats.isSelected()) {
            this.statsAgent = this.network.getAgent(clickAgentId);
            this.statsFrame.refreshLocalAgentStats(network.getAgent(clickAgentId));
        }

        // update stats
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalAgentStats(this.network));
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        updateStats();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) { }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeAdded(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Agent agent, String attribute, Object value) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeChanged(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Agent agent, String attribute, Object oldValue, Object newValue) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.listeners.AgentListener#notifyAttributeRemoved(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Agent agent, String attribute) {
        updateStats();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationStarted(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationStarted(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.listeners.AgentRoundFinishedListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyRoundFinished(Agent agent) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.listeners.AgentConnectionListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.nidm.agents.Agent, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Agent agent1, Agent agent2) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.listeners.AgentConnectionListener#
     * notifyEdgeRemoved(org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Agent agent, Edge edge) {
        updateStats();
    }

    /**
     * Updates the stats frame.
     */
    private void updateStats() {
        // global stats
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
        this.statsFrame.refreshGlobalSimulationStats(StatsComputer.computeGlobalSimulationStats(this.simulation));
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalAgentStats(this.network));

        // local agent stats
        if (this.statsAgent == null) {
            return;
        }
        this.statsFrame.refreshLocalAgentStats(statsAgent);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.ExportListener#notifyRecordingStarted()
     */
    @Override
    public void notifyRecordingStarted() {
        this.statsFrame.refreshSimulationRecording(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.ExportListener#notifyRecordingStopped()
     */
    @Override
    public void notifyRecordingStopped() {
        this.statsFrame.refreshSimulationRecording(false);
    }
}
