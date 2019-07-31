package nl.uu.socnetid.netgame;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.netgame.actors.Actor;
import nl.uu.socnetid.netgame.actors.ActorListener;
import nl.uu.socnetid.netgame.diseases.DiseaseSpecs;
import nl.uu.socnetid.netgame.diseases.types.DiseaseType;
import nl.uu.socnetid.netgame.gui.CIDMoPanel;
import nl.uu.socnetid.netgame.gui.CumulativePanel;
import nl.uu.socnetid.netgame.gui.DeactivatablePanel;
import nl.uu.socnetid.netgame.gui.ExportAdjacencyMatrixPanel;
import nl.uu.socnetid.netgame.gui.ExportEdgeListPanel;
import nl.uu.socnetid.netgame.gui.ExportFrame;
import nl.uu.socnetid.netgame.gui.ExportGEXFPanel;
import nl.uu.socnetid.netgame.gui.ExportListener;
import nl.uu.socnetid.netgame.gui.NodeClick;
import nl.uu.socnetid.netgame.gui.NodeClickListener;
import nl.uu.socnetid.netgame.gui.OsType;
import nl.uu.socnetid.netgame.gui.StatsFrame;
import nl.uu.socnetid.netgame.networks.DisplayableNetwork;
import nl.uu.socnetid.netgame.networks.Network;
import nl.uu.socnetid.netgame.simulation.Simulation;
import nl.uu.socnetid.netgame.simulation.SimulationListener;
import nl.uu.socnetid.netgame.stats.StatsComputer;
import nl.uu.socnetid.netgame.utilities.CIDMo;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements NodeClickListener, SimulationListener, ActorListener, ExportListener {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // OS TYPE
    public static OsType osType = OsType.OTHER;

    // NETWORK
    private final DisplayableNetwork network = new DisplayableNetwork();

    // WINDOWS
    private JFrame controlsFrame = new JFrame("Settings");
    private final StatsFrame statsFrame = new StatsFrame("Statistics");
    private final ExportFrame exportFrame = new ExportFrame("Export", this.network);


    // UTILITY
    // selection
    private JComboBox<String> modelTypeCBox;
    private final String[] utilityFunctions = {"CIDMo"};  //, "Cumulative"};
    // panels
    private CumulativePanel cumulativePanel = new CumulativePanel();
    private CIDMoPanel cidmoPanel = new CIDMoPanel();
    private final DeactivatablePanel[] utilityPanels = {cumulativePanel, cidmoPanel};

    // ACTOR
    // amount to add
    private JTextField txtAddAmount;
    // risk behavior
    private JLabel lblR;
    private JTextField txtRPi = new JTextField();
    // on node click
    private Actor statsActor;
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private JCheckBox chckbxShowActorStats;
    private JCheckBox chckbxToggleInfection;

    // SIMULATION
    private Simulation simulation;
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;
    private JSpinner simulationDelay;
    private JTextField txtRemoveAmount;
    private JTextField txtRSigma;
    private JTextField txtPhi;
    private JTextField txtTau;


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
                    window.exportFrame.setVisible(true);
                    window.statsFrame.setVisible(true);
                    window.controlsFrame.setVisible(true);
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

        // os type
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            osType = OsType.WIN;
        } else if (os.indexOf("mac") >= 0) {
            osType = OsType.MAC;
        } else if (os.indexOf("nix") >= 0
                || os.indexOf("nux") >= 0
                || os.indexOf("aix") >= 0) {
            osType = OsType.UNIX;
        }

        // init settings frame
        controlsFrame.getContentPane().setLayout(null);
        controlsFrame.setTitle("");
        controlsFrame.setBounds(10, 10, 352, 510);
        switch (osType) {
            case WIN:
                break;
            case MAC:
                controlsFrame.setBounds(10, 10, 352, 510);
                break;
            case OTHER:
            case UNIX:
            default:
                break;
        }
        controlsFrame.setResizable(false);
        controlsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //////////// TABBED PANE (UTILITY, DISEASE, EXPORT, ACTORS) ////////////
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(6, 6, 340, 483);
        controlsFrame.getContentPane().add(tabbedPane);


        //////////// UTILITY ////////////
        JPanel utilityPane = new JPanel();
        utilityPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192)));
        tabbedPane.add("Utility", utilityPane);
        utilityPane.setLayout(null);

        cidmoPanel.setBounds(3, 51, 312, 380);
        utilityPane.add(cidmoPanel);

        cumulativePanel.setBounds(3, 51, 312, 380);
        cumulativePanel.setVisible(false);
        utilityPane.add(cumulativePanel);

        modelTypeCBox = new JComboBox<String>();
        for (int i = 0; i < utilityFunctions.length; i++) {
            modelTypeCBox.addItem(utilityFunctions[i]);
        }
        modelTypeCBox.setBounds(111, 5, 195, 30);
        utilityPane.add(modelTypeCBox);

        JLabel lblModel = new JLabel("Model type:");
        lblModel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblModel.setBounds(16, 10, 83, 16);
        utilityPane.add(lblModel);

        JSeparator separator_3 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_3.setForeground(Color.LIGHT_GRAY);
        separator_3.setBounds(3, 41, 312, 10);
        utilityPane.add(separator_3);
        modelTypeCBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (modelTypeCBox.getSelectedIndex()) {
                    case 0:
                        cidmoPanel.setVisible(true);
                        txtRPi.setEnabled(true);
                        cumulativePanel.setVisible(false);
                        break;

                    case 1:
                        cidmoPanel.setVisible(false);
                        txtRPi.setEnabled(false);
                        txtRPi.setText("1.00");
                        cumulativePanel.setVisible(true);
                        break;

                    case 2:
                        cidmoPanel.setVisible(false);
                        txtRPi.setEnabled(false);
                        txtRPi.setText("1.00");
                        cumulativePanel.setVisible(false);
                        break;

                    default:
                        throw new RuntimeException("Undefined utility function!");
                }
            }
        });


        //////////// NETWORK ////////////
        JPanel networkPane = new JPanel();
        networkPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.addTab("Network", networkPane);
        networkPane.setLayout(null);

        JButton btnAddAgent = new JButton("Add agent");
        btnAddAgent.setIcon(new ImageIcon(getClass().getResource("/add.png")));
        btnAddAgent.setBounds(40, 200, 110, 30);
        networkPane.add(btnAddAgent);

        JButton btnRemoveAgent = new JButton("Remove agent");
        btnRemoveAgent.setIcon(new ImageIcon(getClass().getResource("/remove.png")));
        btnRemoveAgent.setBounds(40, 231, 110, 30);
        networkPane.add(btnRemoveAgent);

        JButton btnClearEdges = new JButton("Clear ties");
        btnClearEdges.setIcon(new ImageIcon(getClass().getResource("/clear.png")));
        btnClearEdges.setBounds(40, 336, 258, 30);
        networkPane.add(btnClearEdges);

        txtRPi.setText("1.00");
        txtRPi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRPi.setColumns(10);
        txtRPi.setBounds(248, 61, 50, 20);
        networkPane.add(txtRPi);

        lblR = new JLabel("Risk perception (per agent):");
        lblR.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        //        Font font = lblR.getFont();
        //        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        //        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        //        lblR.setFont(font.deriveFont(attributes));
        lblR.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblR.setBounds(16, 10, 201, 16);
        networkPane.add(lblR);

        JButton btnClearAll = new JButton("Clear all");
        btnClearAll.setIcon(new ImageIcon(getClass().getResource("/clearall.png")));
        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        btnClearAll.setBounds(40, 368, 258, 30);
        networkPane.add(btnClearAll);

        txtAddAmount = new JTextField();
        txtAddAmount.setText("1");
        txtAddAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAddAmount.setColumns(10);
        txtAddAmount.setBounds(248, 206, 50, 20);
        networkPane.add(txtAddAmount);

        JLabel lblAmount = new JLabel("amount");
        lblAmount.setToolTipText("Risk behavior of the actor - r<1: risk seeking, "
                + "r=1: risk neutral, r>1: risk averse");
        lblAmount.setBounds(157, 208, 60, 16);
        networkPane.add(lblAmount);

        JLabel lblr = new JLabel("(r  ):");
        lblr.setHorizontalAlignment(SwingConstants.RIGHT);
        lblr.setBounds(204, 38, 35, 16);
        networkPane.add(lblr);

        JLabel lbln = new JLabel("(N):");
        lbln.setHorizontalAlignment(SwingConstants.RIGHT);
        lbln.setBounds(215, 208, 24, 16);
        networkPane.add(lbln);

        JButton btnCreateFullNetwork = new JButton("Create full fetwork (ɩ)");
        btnCreateFullNetwork.setIcon(new ImageIcon(getClass().getResource("/full.png")));
        btnCreateFullNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFullNetwork();
            }
        });
        btnCreateFullNetwork.setBounds(40, 304, 258, 29);
        networkPane.add(btnCreateFullNetwork);

        JLabel label = new JLabel("amount");
        label.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label.setBounds(157, 238, 60, 16);
        networkPane.add(label);

        JLabel label_1 = new JLabel("(N):");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setBounds(215, 238, 24, 16);
        networkPane.add(label_1);

        txtRemoveAmount = new JTextField();
        txtRemoveAmount.setText("1");
        txtRemoveAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRemoveAmount.setColumns(10);
        txtRemoveAmount.setBounds(248, 236, 50, 20);
        networkPane.add(txtRemoveAmount);

        JLabel lblProbabilityOfInfection = new JLabel("Probability of infection");
        lblProbabilityOfInfection.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblProbabilityOfInfection.setBounds(40, 63, 163, 16);
        networkPane.add(lblProbabilityOfInfection);

        JLabel label_2 = new JLabel("π");
        label_2.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_2.setHorizontalAlignment(SwingConstants.RIGHT);
        label_2.setBounds(222, 71, 7, 10);
        networkPane.add(label_2);

        txtRSigma = new JTextField();
        txtRSigma.setText("1.00");
        txtRSigma.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRSigma.setColumns(10);
        txtRSigma.setBounds(248, 36, 50, 20);
        networkPane.add(txtRSigma);

        JLabel label_3 = new JLabel("(r  ):");
        label_3.setHorizontalAlignment(SwingConstants.RIGHT);
        label_3.setBounds(204, 63, 35, 16);
        networkPane.add(label_3);

        JLabel lblDiseaseSeverity = new JLabel("Disease severity");
        lblDiseaseSeverity.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblDiseaseSeverity.setBounds(40, 38, 163, 16);
        networkPane.add(lblDiseaseSeverity);

        JLabel label_5 = new JLabel("σ");
        label_5.setHorizontalAlignment(SwingConstants.RIGHT);
        label_5.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
        label_5.setBounds(222, 46, 7, 10);
        networkPane.add(label_5);

        JLabel lblNetworkSize = new JLabel("Evaluation of peers (per agent):");
        lblNetworkSize.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkSize.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkSize.setBounds(16, 104, 238, 16);
        networkPane.add(lblNetworkSize);

        JLabel lblNetworkStructure = new JLabel("Network structure:");
        lblNetworkStructure.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkStructure.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkStructure.setBounds(16, 279, 142, 16);
        networkPane.add(lblNetworkStructure);

        JLabel label_6 = new JLabel("Network size:");
        label_6.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        label_6.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        label_6.setBounds(16, 175, 142, 16);
        networkPane.add(label_6);

        JLabel lblOfNetwork = new JLabel("% of network per time step");
        lblOfNetwork.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblOfNetwork.setBounds(40, 134, 177, 16);
        networkPane.add(lblOfNetwork);

        JLabel label_8 = new JLabel("(ϕ):");
        label_8.setHorizontalAlignment(SwingConstants.RIGHT);
        label_8.setBounds(204, 134, 35, 16);
        networkPane.add(label_8);

        txtPhi = new JTextField();
        txtPhi.setText("1.00");
        txtPhi.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPhi.setColumns(10);
        txtPhi.setBounds(248, 132, 50, 20);
        networkPane.add(txtPhi);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(3, 94, 312, 10);
        networkPane.add(separator);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.LIGHT_GRAY);
        separator_1.setBounds(3, 165, 312, 10);
        networkPane.add(separator_1);

        JSeparator separator_2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_2.setForeground(Color.LIGHT_GRAY);
        separator_2.setBounds(3, 269, 312, 10);
        networkPane.add(separator_2);

        //////////// DISEASE ////////////
        JPanel simulationPane = new JPanel();
        simulationPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.add("Simulation", simulationPane);
        simulationPane.setLayout(null);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (10 ms):");
        simulationDelayLabel.setBounds(40, 210, 170, 16);
        simulationPane.add(simulationDelayLabel);

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(215, 205, 80, 26);
        simulationPane.add(simulationDelay);
        simulationDelay.setValue(10);


        //////////// SIMULATION ////////////
        JButton btnStart = new JButton(" Start");
        btnStart.setBounds(37, 238, 260, 35);
        simulationPane.add(btnStart);
        btnStart.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnStart.setIcon(new ImageIcon(getClass().getResource("/start.png")));

        JButton btnPauseSimulation = new JButton(" Pause");
        btnPauseSimulation.setBounds(37, 275, 260, 35);
        simulationPane.add(btnPauseSimulation);
        btnPauseSimulation.setIcon(new ImageIcon(getClass().getResource("/pause.png")));
        btnPauseSimulation.setFont(new Font("Lucida Grande", Font.PLAIN, 14));

        JButton btnReset = new JButton(" Reset");
        btnReset.setBounds(37, 312, 260, 35);
        simulationPane.add(btnReset);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        });
        btnReset.setIcon(new ImageIcon(getClass().getResource("/reset.png")));
        btnReset.setFont(new Font("Lucida Grande", Font.PLAIN, 14));

        JButton btnInfectRandomActor = new JButton("Infect Random Actor");
        btnInfectRandomActor.setBounds(37, 62, 260, 30);
        simulationPane.add(btnInfectRandomActor);
        btnInfectRandomActor.setIcon(new ImageIcon(getClass().getResource("/infect.png")));

        JLabel lblTimeStepsTo = new JLabel("Disease related:");
        lblTimeStepsTo.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblTimeStepsTo.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblTimeStepsTo.setBounds(16, 10, 238, 16);
        simulationPane.add(lblTimeStepsTo);

        JLabel lblTau1 = new JLabel("Time steps to recover");
        lblTau1.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblTau1.setBounds(40, 40, 177, 16);
        simulationPane.add(lblTau1);

        JLabel lblTau2 = new JLabel("(τ):");
        lblTau2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTau2.setBounds(204, 40, 35, 16);
        simulationPane.add(lblTau2);

        txtTau = new JTextField();
        txtTau.setText("10");
        txtTau.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTau.setColumns(10);
        txtTau.setBounds(248, 38, 50, 20);
        simulationPane.add(txtTau);

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(3, 100, 312, 10);
        simulationPane.add(separator_5);

        JLabel lblOnNodeClick_1 = new JLabel("On node click:");
        lblOnNodeClick_1.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblOnNodeClick_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblOnNodeClick_1.setBounds(16, 110, 238, 16);
        simulationPane.add(lblOnNodeClick_1);

        chckbxShowActorStats = new JCheckBox("Show actor stats");
        chckbxShowActorStats.setBounds(46, 135, 141, 23);
        simulationPane.add(chckbxShowActorStats);
        chckbxShowActorStats.setSelected(true);

        chckbxToggleInfection = new JCheckBox("Toggle infection");
        chckbxToggleInfection.setBounds(46, 160, 141, 23);
        simulationPane.add(chckbxToggleInfection);
        chckbxToggleInfection.setSelected(false);

        JSeparator separator_4 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_4.setForeground(Color.LIGHT_GRAY);
        separator_4.setBounds(3, 195, 312, 10);
        simulationPane.add(separator_4);
        btnInfectRandomActor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectRandomActor();
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
        JPanel exportPanel = new JPanel();
        exportPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.addTab("Export", null, exportPanel, null);
        exportPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(159, 5, 1, 1);
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        exportPanel.add(panel);

        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.setBounds(6, 6, 215, 30);
        panel.add(comboBox);

        ExportGEXFPanel exportGEXFPanel = new ExportGEXFPanel((Network) null);
        exportGEXFPanel.setBounds(6, 34, 214, 192);
        panel.add(exportGEXFPanel);

        ExportAdjacencyMatrixPanel exportAdjacencyMatrixPanel = new ExportAdjacencyMatrixPanel((Network) null);
        exportAdjacencyMatrixPanel.setBounds(6, 34, 214, 192);
        panel.add(exportAdjacencyMatrixPanel);

        ExportEdgeListPanel exportEdgeListPanel = new ExportEdgeListPanel((Network) null);
        exportEdgeListPanel.setBounds(6, 34, 214, 192);
        panel.add(exportEdgeListPanel);

        JPanel panel_1 = new JPanel();
        panel_1.setLayout(null);
        panel_1.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel_1.setBounds(59, 170, 231, 240);
        exportPanel.add(panel_1);

        JComboBox<String> comboBox_1 = new JComboBox<String>();
        comboBox_1.setBounds(6, 6, 215, 30);
        panel_1.add(comboBox_1);

        ExportGEXFPanel exportGEXFPanel_1 = new ExportGEXFPanel((Network) null);
        exportGEXFPanel_1.setBounds(6, 34, 214, 192);
        panel_1.add(exportGEXFPanel_1);

        ExportAdjacencyMatrixPanel exportAdjacencyMatrixPanel_1 = new ExportAdjacencyMatrixPanel((Network) null);
        exportAdjacencyMatrixPanel_1.setBounds(6, 34, 214, 192);
        panel_1.add(exportAdjacencyMatrixPanel_1);

        ExportEdgeListPanel exportEdgeListPanel_1 = new ExportEdgeListPanel((Network) null);
        exportEdgeListPanel_1.setBounds(6, 34, 214, 192);
        panel_1.add(exportEdgeListPanel_1);

        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnRemoveAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeActor();
            }
        });
        btnAddAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addActor();
            }
        });


        //////////// CREATE A UI REPRESENATION OF THE NETWORK ////////////
        this.network.show();


        //////////// LISTENER ////////////
        NodeClick nodeClickListener = new NodeClick(this.network);
        nodeClickListener.addListener(this);
        this.nodeClickExecutor.submit(nodeClickListener);
        this.exportFrame.addExportListener(this);
    }


    //////////// USER ALTERABLE NETWORK PROPERTIES ////////////
    /**
     * Adds a actor to the game.
     */
    private void addActor() {

        // disable utility and disease panels
        disableUtilityPanels();

        // get disease and utility specs
        DiseaseSpecs ds = getDiseaseSpecs();
        UtilityFunction uf = getUtilityFunction();

        // add each actor with selected utility function and disease specs
        for (int i = 0; i < Integer.parseInt(txtAddAmount.getText()); i++) {
            Actor actor = this.network.addActor(uf, ds,
                    Double.valueOf(this.txtRSigma.getText()),
                    Double.valueOf(this.txtRPi.getText()),
                    Double.valueOf(this.txtPhi.getText()));
            actor.addActorListener(this);
        }

        // update stats
        if (this.network.getActors().size() <= 1) {
            this.statsFrame.refreshGlobalUtilityStats(uf);
            this.statsFrame.refreshGlobalDiseaseStats(ds);
        }
        this.statsFrame.refreshGlobalActorStats(StatsComputer.computeGlobalActorStats(this.network));
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
        this.statsFrame.refreshGlobalSimulationStats(StatsComputer.computeGlobalSimulationStats(this.simulation));
    }

    /**
     * Removes a actor from the game.
     */
    private void removeActor() {
        for (int i = 0; i < Integer.parseInt(txtAddAmount.getText()); i++) {
            if (!this.network.getActors().isEmpty()) {
                this.network.removeActor();
            }
        }

        // if there are no more actors in the networks enable utility and disease panels
        if (this.network.getActors().isEmpty()) {
            this.statsFrame.resetGlobalActorStats();
            enableUtilityPanels();
        }

        // update stats
        this.statsFrame.refreshGlobalActorStats(StatsComputer.computeGlobalActorStats(this.network));
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
        while (!this.network.getActors().isEmpty()) {
            removeActor();
        }
    }

    /**
     * Creates the full network based on the actors available.
     */
    private void createFullNetwork() {
        if (this.network != null) {
            this.network.createFullNetwork();
        }
    }

    /**
     * Infects a random actor.
     */
    private void infectRandomActor() {
        Actor actor = this.network.getRandomNotInfectedActor();
        if (actor != null) {
            actor.infect(getDiseaseSpecs());
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
                return new CIDMo(
                        this.cidmoPanel.getAlpha(),
                        this.cidmoPanel.getKappa(),
                        this.cidmoPanel.getBeta(),
                        this.cidmoPanel.getLamda(),
                        this.cidmoPanel.getC());

            case 1:
                return new Cumulative(this.cumulativePanel.getDirectBenefit(),
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
                Integer.valueOf(this.txtTau.getText()),
                this.cidmoPanel.getSigma(),
                this.cidmoPanel.getGamma(),
                this.cidmoPanel.getMu());
    }


    //////////// SIMULATION ////////////
    /**
     * Runs the actual simulation of the network game.
     */
    private void startSimulation() {

        // initializations
        if (this.simulation == null) {
            // this.simulation = new ThreadedSimulation(this.network);
            this.simulation = new Simulation(this.network);
        }
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
     * Clears all edges and resets all actors to being susceptible.
     */
    private void resetSimulation() {
        this.network.resetActors();
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
     * @see nl.uu.socnetid.netgame.gui.NodeClickListener#notify(nl.uu.socnetid.netgame.gui.NodeClick)
     */
    @Override
    public void notify(NodeClick nodeClick) {
        String clickActorId = nodeClick.getClickedNodeId();

        // toggle infection on node click
        if (this.chckbxToggleInfection.isSelected()) {
            this.network.toggleInfection(clickActorId, getDiseaseSpecs());
        }

        // show actor stats on node click
        if (this.chckbxShowActorStats.isSelected()) {
            this.statsActor = this.network.getActor(clickActorId);
            this.statsFrame.refreshLocalActorStats(network.getActor(clickActorId));
        }

        // update stats
        this.statsFrame.refreshGlobalActorStats(StatsComputer.computeGlobalActorStats(this.network));
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        updateStats();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.netgame.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) { }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeAdded(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Actor actor, String attribute, Object value) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.ActorListener#notifyAttributeChanged(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Actor actor, String attribute, Object oldValue, Object newValue) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorListener#notifyAttributeRemoved(
     * nl.uu.socnetid.netgame.actors.Actor, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Actor actor, String attribute) {
        updateStats();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorRoundFinishedListener#notifyRoundFinished(
     * nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorConnectionListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.netgame.actors.Actor, nl.uu.socnetid.netgame.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.netgame.actors.listeners.ActorConnectionListener#
     * notifyEdgeRemoved(org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Actor actor, Edge edge) {
        updateStats();
    }

    /**
     * Updates the stats frame.
     */
    private void updateStats() {
        // global stats
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
        this.statsFrame.refreshGlobalSimulationStats(StatsComputer.computeGlobalSimulationStats(this.simulation));

        // actor stats
        if (this.statsActor == null) {
            return;
        }
        this.statsFrame.refreshLocalActorStats(statsActor);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.ExportListener#notifyRecordingStarted()
     */
    @Override
    public void notifyRecordingStarted() {
        this.statsFrame.refreshSimulationRecording(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.netgame.gui.ExportListener#notifyRecordingStopped()
     */
    @Override
    public void notifyRecordingStopped() {
        this.statsFrame.refreshSimulationRecording(false);
    }
}
