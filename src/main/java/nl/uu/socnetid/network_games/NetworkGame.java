package nl.uu.socnetid.network_games;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
    // check box to toggle between infect / cure on mouse clicks
    private JCheckBox chckbxToggleInfection;
    // status label for stable network
    private JLabel lblStable;
    // risk behavior of player
    private JLabel lblR;
    private JTextField txtR;

    // concurrency for simulation
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;


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
        frame.setBounds(100, 100, 236, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // panes
        JPanel playerPane = new JPanel();
        JPanel exportPane = new JPanel();
        // tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBounds(6, 6, 224, 304);
        frame.getContentPane().add(tabbedPane);
        JPanel utilityPane = new JPanel();
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
                utilityFunctionCBox.setBounds(6, 6, 165, 27);
                utilityPane.add(utilityFunctionCBox);

                        irtcPanel = new IRTCPanel();
                        irtcPanel.setBounds(6, 45, 166, 232);
                        utilityPane.add(irtcPanel);

                                cumulativePanel = new CumulativePanel();
                                cumulativePanel.setBounds(6, 45, 166, 232);
                                cumulativePanel.setVisible(false);
                                utilityPane.add(cumulativePanel);

                                        truncatedConnectionsPanel = new TruncatedConnectionsPanel();
                                        truncatedConnectionsPanel.setBounds(6, 45, 166, 232);
                                        truncatedConnectionsPanel.setVisible(false);
                                        utilityPane.add(truncatedConnectionsPanel);
        JPanel diseasePane = new JPanel();


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
                        diseaseCBox.setBounds(6, 41, 165, 27);
                        diseasePane.add(diseaseCBox);

                                chckbxToggleInfection = new JCheckBox("Infect / cure on click");
                                chckbxToggleInfection.setBounds(6, 6, 165, 23);
                                diseasePane.add(chckbxToggleInfection);

                                        sirPanel = new SIRPanel();
                                        sirPanel.setBounds(6, 80, 165, 197);
                                        sirPanel.setVisible(true);
                                        diseasePane.add(sirPanel);
        tabbedPane.addTab("Players", playerPane);
        playerPane.setLayout(null);

        JButton btnAddPlayer = new JButton("Add Player");
        btnAddPlayer.setBounds(6, 6, 165, 29);
        playerPane.add(btnAddPlayer);

        JButton btnRemovePlayer = new JButton("Remove Player");
        btnRemovePlayer.setBounds(6, 47, 165, 29);
        playerPane.add(btnRemovePlayer);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.setBounds(6, 88, 165, 29);
        playerPane.add(btnClearEdges);

        txtR = new JTextField();
        txtR.setText("1");
        txtR.setHorizontalAlignment(SwingConstants.RIGHT);
        txtR.setColumns(10);
        txtR.setBounds(120, 141, 44, 26);
        playerPane.add(txtR);

        lblR = new JLabel("risk pref. (r):");
        lblR.setToolTipText("Risk behavior of the player - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblR.setBounds(19, 146, 98, 16);
        playerPane.add(lblR);
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
        edgeWriterCBox.setBounds(6, 6, 165, 27);
        exportPane.add(edgeWriterCBox);

        JButton btnExportNetwork = new JButton("Export");
        btnExportNetwork.setBounds(6, 45, 165, 29);
        exportPane.add(btnExportNetwork);
        btnExportNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportNetwork();
            }
        });

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        btnStart.setBounds(6, 355, 110, 35);
        frame.getContentPane().add(btnStart);
        for (int i = 0; i < edgeWriters.length; i++) {
            edgeWriterCBox.addItem(edgeWriters[i]);
        }
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(180, 322, 50, 26);
        frame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (100 ms):");
        simulationDelayLabel.setBounds(10, 327, 179, 16);
        frame.getContentPane().add(simulationDelayLabel);

        JButton btnStopSimulation = new JButton("Stop");
        btnStopSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                stopSimulation();
            }
        });
        btnStopSimulation.setBounds(120, 355, 110, 35);
        frame.getContentPane().add(btnStopSimulation);

        JPanel panel = new JPanel();
        panel.setBounds(6, 402, 224, 220);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblStats = new JLabel("Stats");
        lblStats.setFont(new Font("Lucida Grande", Font.BOLD, 15));
        lblStats.setBounds(6, 6, 61, 19);
        panel.add(lblStats);

        JLabel lblNetworkStable = new JLabel("Network stable:");
        lblNetworkStable.setBounds(6, 34, 113, 16);
        panel.add(lblNetworkStable);

        lblStable = new JLabel("no");
        lblStable.setBounds(131, 34, 61, 16);
        panel.add(lblStable);
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
     * Stops the simulation of the network game.
     */
    private void stopSimulation() {
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
        if (this.chckbxToggleInfection.isSelected()) {
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
            this.lblStable.setText("yes");
        } else {
            this.lblStable.setText("no");
        }
    }
}
