package nl.uu.socnetid.network_games;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import nl.uu.socnetid.network_games.disease.Disease;
import nl.uu.socnetid.network_games.disease.ThreeStageDisease;
import nl.uu.socnetid.network_games.disease.TwoStageDisease;
import nl.uu.socnetid.network_games.gui.NodeClick;
import nl.uu.socnetid.network_games.gui.NodeClickListener;
import nl.uu.socnetid.network_games.network.io.NetworkFileWriter;
import nl.uu.socnetid.network_games.network.networks.Network;
import nl.uu.socnetid.network_games.network.networks.SimpleNetwork;
import nl.uu.socnetid.network_games.network.simulation.NetworkSimulation;
import nl.uu.socnetid.network_games.network.simulation.Simulation;
import nl.uu.socnetid.network_games.network.simulation.SimulationCompleteListener;
import nl.uu.socnetid.network_games.network.writer.AdjacencyMatrixWriter;
import nl.uu.socnetid.network_games.network.writer.EdgeListWriter;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;
import nl.uu.socnetid.network_games.players.RationalPlayerNode;
import nl.uu.socnetid.network_games.utilities.Cumulative;
import nl.uu.socnetid.network_games.utilities.TruncatedConnections;
import nl.uu.socnetid.network_games.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements SimulationCompleteListener, NodeClickListener {

    // general export path
    private static final String EXPORT_PATH = "./network-exports/";

    // network
    private final Network network = new SimpleNetwork();
    // graph
    private Graph graph;

    // logger
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // swing components
    // frame
    private JFrame frame;
    // utility function combo box and selection
    private JComboBox<String> utilityFunctionCBox;
    private String[] utilityFunctions = {"Cumulative", "Truncated Connections"};
    // edge writer combo box and selection
    private JComboBox<String> edgeWriterCBox;
    private String[] edgeWriters = {"Edge List", "Adjacency Matrix"};
    // spinner for simulation delay
    private JSpinner simulationDelay;
    // toggle button for player infections
    private JToggleButton tglbtnToggleInfection;
    // disease selection combo box
    private JComboBox<String> diseaseCBox;
    private String[] diseases = {"Two Stage", "Three Stage"};
    // connections model
    private JTextField txtConnectionsDelta;
    private JTextField txtConnectionsCosts;

    // concurrency for simulation
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;
    private JTextField txtDiseaseDuration;
    private JTextField txtDiseaseTransmissionRate;
    private JTextField txtDiseaseTreatmentCosts;


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
        frame.setBounds(100, 100, 512, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // panes
        JPanel playerPane = new JPanel();
        JPanel utilityPane = new JPanel();
        JPanel diseasePane = new JPanel();
        JPanel exportPane = new JPanel();
        // tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBounds(6, 6, 224, 304);
        frame.getContentPane().add(tabbedPane);
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
        tabbedPane.add("Utility", utilityPane);
        utilityPane.setLayout(null);

                utilityFunctionCBox = new JComboBox<String>();
                utilityFunctionCBox.setBounds(6, 6, 165, 27);
                utilityPane.add(utilityFunctionCBox);

                JPanel panel = new JPanel();
                panel.setBounds(6, 45, 166, 232);
                utilityPane.add(panel);
        tabbedPane.add("Diseas", diseasePane);
        diseasePane.setLayout(null);

        diseaseCBox = new JComboBox<String>();
        diseaseCBox.setBounds(6, 6, 165, 27);
        diseasePane.add(diseaseCBox);

        JLabel lblDuration = new JLabel("Duration:");
        lblDuration.setBounds(6, 50, 134, 16);
        diseasePane.add(lblDuration);

        txtDiseaseDuration = new JTextField();
        txtDiseaseDuration.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiseaseDuration.setBounds(127, 45, 44, 26);
        diseasePane.add(txtDiseaseDuration);
        txtDiseaseDuration.setText("10");
        txtDiseaseDuration.setColumns(10);

        JLabel lblTransmissionRate = new JLabel("Transmission Rate:");
        lblTransmissionRate.setBounds(6, 78, 134, 16);
        diseasePane.add(lblTransmissionRate);

        txtDiseaseTransmissionRate = new JTextField();
        txtDiseaseTransmissionRate.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiseaseTransmissionRate.setBounds(127, 73, 44, 26);
        diseasePane.add(txtDiseaseTransmissionRate);
        txtDiseaseTransmissionRate.setText("0.1");
        txtDiseaseTransmissionRate.setColumns(10);

        JLabel lblTreatmentCosts = new JLabel("Treatment Costs:");
        lblTreatmentCosts.setBounds(6, 106, 134, 16);
        diseasePane.add(lblTreatmentCosts);

        txtDiseaseTreatmentCosts = new JTextField();
        txtDiseaseTreatmentCosts.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiseaseTreatmentCosts.setBounds(127, 101, 44, 26);
        diseasePane.add(txtDiseaseTreatmentCosts);
        txtDiseaseTreatmentCosts.setText("1");
        txtDiseaseTreatmentCosts.setColumns(10);

        tglbtnToggleInfection = new JToggleButton("Toggle Infection");
        tglbtnToggleInfection.setToolTipText("When activated: Single nodes can be infected or cured by simply clicking on them.");
        tglbtnToggleInfection.setBounds(6, 139, 165, 20);
        diseasePane.add(tglbtnToggleInfection);

        JButton btnInfectPlayer = new JButton("Infect Random Player");
        btnInfectPlayer.setBounds(6, 171, 171, 29);
        diseasePane.add(btnInfectPlayer);
        btnInfectPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectRandomPlayer();
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

                txtConnectionsDelta = new JTextField();
                txtConnectionsDelta.setBounds(388, 63, 44, 26);
                frame.getContentPane().add(txtConnectionsDelta);
                txtConnectionsDelta.setHorizontalAlignment(SwingConstants.RIGHT);
                txtConnectionsDelta.setText("0.5");
                txtConnectionsDelta.setColumns(10);

                        JLabel lblConnectionBenefitdelta = new JLabel("Benefit (delta): ");
                        lblConnectionBenefitdelta.setBounds(267, 68, 134, 16);
                        frame.getContentPane().add(lblConnectionBenefitdelta);

                                JLabel lblConnectionCostsc = new JLabel("Costs (c):");
                                lblConnectionCostsc.setBounds(267, 96, 134, 16);
                                frame.getContentPane().add(lblConnectionCostsc);

                                        txtConnectionsCosts = new JTextField();
                                        txtConnectionsCosts.setBounds(388, 91, 44, 26);
                                        frame.getContentPane().add(txtConnectionsCosts);
                                        txtConnectionsCosts.setHorizontalAlignment(SwingConstants.RIGHT);
                                        txtConnectionsCosts.setText("0.45");
                                        txtConnectionsCosts.setColumns(10);
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
    }


    /**
     * Adds a player to the game.
     */
    private void addPlayer() {
        this.network.addPlayer(RationalPlayerNode.newInstance(this.graph));
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
                return new Cumulative();

            case 1:
                double delta = Double.valueOf(this.txtConnectionsDelta.getText());
                double costs = Double.valueOf(this.txtConnectionsCosts.getText());
                return new TruncatedConnections(delta, costs);

            default:
                throw new RuntimeException("Undefined utility function!");
        }
    }

    /**
     * Exports the network into a csv file.
     */
    private void exportNetwork() {
        NetworkWriter networkWriter;
        String file;
        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss"));

        switch (edgeWriterCBox.getSelectedIndex()) {
            case 0:
                networkWriter = new EdgeListWriter();
                file = EXPORT_PATH + "edge-list_" + formattedNow + ".csv";
                break;

            case 1:
                networkWriter = new AdjacencyMatrixWriter();
                file = EXPORT_PATH + "adjacency-matrix_" + formattedNow + ".csv";
                break;

            default:
                throw new RuntimeException("Undefined network writer!");
        }

        NetworkFileWriter fileWriter = new NetworkFileWriter(EXPORT_PATH, file, networkWriter, network);
        fileWriter.write();
    }

    /**
     * Infects a player.
     */
    private void infectRandomPlayer() {
        this.network.infectRandomPlayer(getDisease());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.gui.NodeClickListener#notify(
     * nl.uu.socnetid.network_games.gui.NodeClick)
     */
    @Override
    public void notify(NodeClick nodeClick) {
        if (tglbtnToggleInfection.isSelected()) {
            this.network.toggleInfection(nodeClick.getClickedNodeId(), getDisease());
        }
    }

    /**
     * @return the selected disease
     */
    private Disease getDisease() {
        switch (diseaseCBox.getSelectedIndex()) {
            case 0:
                int diseaseDuration = Integer.valueOf(this.txtDiseaseDuration.getText());
                Double diseaseTransmissionRate = Double.valueOf(this.txtDiseaseTransmissionRate.getText());
                Double diseaseTreatmentCosts = Double.valueOf(this.txtDiseaseTreatmentCosts.getText());
                return new TwoStageDisease(diseaseDuration, diseaseTransmissionRate, diseaseTreatmentCosts);

            case 1:
                return new ThreeStageDisease();

            default:
                throw new RuntimeException("Undefined disease type!");
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.SimulationCompleteListener#notify(
     * nl.uu.socnetid.network_games.network.simulation.Simulation)
     */
    @Override
    public void notify(Simulation simulation) {
        logger.debug("Simulation completed.");
        JOptionPane.showMessageDialog(null, simulation.getStatusMessage());
    }
}
