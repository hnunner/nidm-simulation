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

import nl.uu.socnetid.network_games.disease.DiseaseSpecs;
import nl.uu.socnetid.network_games.disease.types.DiseaseType;
import nl.uu.socnetid.network_games.gui.CumulativePanel;
import nl.uu.socnetid.network_games.gui.IRTCPanel;
import nl.uu.socnetid.network_games.gui.NodeClick;
import nl.uu.socnetid.network_games.gui.NodeClickListener;
import nl.uu.socnetid.network_games.gui.SIRPanel;
import nl.uu.socnetid.network_games.gui.StatsFrame;
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
    // windows
    private JFrame settingsFrame;
    private final StatsFrame statsFrame = new StatsFrame("Statistics");
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
    private String[] diseases = {DiseaseType.SIR.toString()};
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
                    window.settingsFrame.setVisible(true);
                    window.statsFrame.setVisible(true);
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
        // init settings frame
        settingsFrame = new JFrame();
        settingsFrame.setBounds(100, 100, 285, 460);
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.getContentPane().setLayout(null);

        // panes
        JPanel playerPane = new JPanel();
        playerPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        JPanel exportPane = new JPanel();
        exportPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        // tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(6, 6, 275, 290);
        settingsFrame.getContentPane().add(tabbedPane);
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
                        lblAmount.setToolTipText("Risk behavior of the player - r<1: risk seeking, "
                                + "r=1: risk neutral, r>1: risk averse");
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
        settingsFrame.getContentPane().add(btnStart);
        for (int i = 0; i < edgeWriters.length; i++) {
            edgeWriterCBox.addItem(edgeWriters[i]);
        }
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(198, 290, 70, 26);
        settingsFrame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (100 ms):");
        simulationDelayLabel.setBounds(16, 295, 170, 16);
        settingsFrame.getContentPane().add(simulationDelayLabel);

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
        settingsFrame.getContentPane().add(btnPauseSimulation);

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
        settingsFrame.getContentPane().add(btnReset);


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
                        RationalPlayerNode.newInstance(this.graph, getUtilityFunction(), getDiseaseSpecs(),
                                Double.valueOf(this.txtR.getText())));
                break;

            default:
                this.network.addPlayer(
                        RationalPlayerNode.newInstance(this.graph, getUtilityFunction(), getDiseaseSpecs()));
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
        NetworkSimulation networkSimulation = new NetworkSimulation(this.network);
        networkSimulation.addListener(this);
        networkSimulation.initSimulationDelay((Integer) this.simulationDelay.getValue());

        // Deprecated: actor are initialized with utility function when they are added (see addPlayer())
//        networkSimulation.initUtilityFunction(getUtilityFunction());


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
                        this.irtcPanel.getC());

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
        // TODO add NodeClickListener to StatsFrame
        if (this.rdbtnShowAgentInfo.isSelected()) {
            this.statsFrame.refreshActor(network.getPlayer(nodeClick.getClickedNodeId()));
        }

        // toggle infection on node click
        if (this.rdbtnToggleInfection.isSelected()) {
            this.network.toggleInfection(nodeClick.getClickedNodeId(), getDiseaseSpecs());
        }
    }

    /**
     * @return the selected disease
     */
    private DiseaseSpecs getDiseaseSpecs() {
        switch (diseaseCBox.getSelectedIndex()) {
            case 0:
                return new DiseaseSpecs(
                        DiseaseType.SIR,
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
// TODO move to StatsFrame
//        if (network.isStable()) {
//            this.lblStatStable.setText("yes");
//        } else {
//            this.lblStatStable.setText("no");
//        }
    }
}
