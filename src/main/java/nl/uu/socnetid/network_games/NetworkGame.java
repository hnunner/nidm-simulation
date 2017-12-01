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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

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
        frame.setBounds(100, 100, 250, 747);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton btnStart = new JButton("(Re-) Start with:");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        btnStart.setBounds(30, 145, 142, 45);
        frame.getContentPane().add(btnStart);

        JButton btnAddPlayer = new JButton("Add Player");
        btnAddPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });
        btnAddPlayer.setBounds(30, 22, 142, 29);
        frame.getContentPane().add(btnAddPlayer);

        JButton btnRemovePlayer = new JButton("Remove Player");
        btnRemovePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePlayer();
            }
        });
        btnRemovePlayer.setBounds(30, 63, 142, 29);
        frame.getContentPane().add(btnRemovePlayer);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnClearEdges.setBounds(30, 104, 142, 29);
        frame.getContentPane().add(btnClearEdges);

        edgeWriterCBox = new JComboBox<String>();
        for (int i = 0; i < edgeWriters.length; i++) {
            edgeWriterCBox.addItem(edgeWriters[i]);
        }
        edgeWriterCBox.setBounds(31, 690, 178, 29);
        frame.getContentPane().add(edgeWriterCBox);

        JButton btnExportNetwork = new JButton("Export Network as:");
        btnExportNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportNetwork();
            }
        });
        btnExportNetwork.setBounds(21, 649, 188, 29);
        frame.getContentPane().add(btnExportNetwork);

        utilityFunctionCBox = new JComboBox<String>();
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }
        utilityFunctionCBox.setBounds(28, 201, 166, 27);
        frame.getContentPane().add(utilityFunctionCBox);

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(142, 312, 44, 26);
        frame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay:");
        simulationDelayLabel.setBounds(30, 317, 126, 16);
        frame.getContentPane().add(simulationDelayLabel);

        JButton btnInfectPlayer = new JButton("Infect Player");
        btnInfectPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectRandomPlayer();
            }
        });
        btnInfectPlayer.setBounds(30, 583, 197, 29);
        frame.getContentPane().add(btnInfectPlayer);

        JButton btnStopSimulation = new JButton("Stop Simulation");
        btnStopSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                stopSimulation();
            }
        });
        btnStopSimulation.setBounds(48, 350, 126, 45);
        frame.getContentPane().add(btnStopSimulation);

        tglbtnToggleInfection = new JToggleButton("Toggle Infection");
        tglbtnToggleInfection.setBounds(73, 551, 126, 20);
        frame.getContentPane().add(tglbtnToggleInfection);

        diseaseCBox = new JComboBox<String>();
        for (int i = 0; i < diseases.length; i ++) {
            diseaseCBox.addItem(diseases[i]);
        }
        diseaseCBox.setBounds(48, 428, 161, 27);
        frame.getContentPane().add(diseaseCBox);

        JLabel lblConnectionBenefitdelta = new JLabel("Connection benefit (delta): ");
        lblConnectionBenefitdelta.setBounds(6, 245, 173, 16);
        frame.getContentPane().add(lblConnectionBenefitdelta);

        txtConnectionsDelta = new JTextField();
        txtConnectionsDelta.setText("0.5");
        txtConnectionsDelta.setBounds(177, 240, 50, 26);
        frame.getContentPane().add(txtConnectionsDelta);
        txtConnectionsDelta.setColumns(10);

        JLabel lblConnectionCostsc = new JLabel("Connection Costs (c):");
        lblConnectionCostsc.setBounds(6, 273, 166, 16);
        frame.getContentPane().add(lblConnectionCostsc);

        txtConnectionsCosts = new JTextField();
        txtConnectionsCosts.setText("0.45");
        txtConnectionsCosts.setBounds(177, 268, 50, 26);
        frame.getContentPane().add(txtConnectionsCosts);
        txtConnectionsCosts.setColumns(10);

        JLabel lblDuration = new JLabel("Duration:");
        lblDuration.setBounds(6, 465, 166, 16);
        frame.getContentPane().add(lblDuration);

        JLabel lblTransmissionRate = new JLabel("Transmission Rate:");
        lblTransmissionRate.setBounds(6, 489, 166, 16);
        frame.getContentPane().add(lblTransmissionRate);

        JLabel lblTreatmentCosts = new JLabel("Treatment Costs:");
        lblTreatmentCosts.setBounds(6, 517, 166, 16);
        frame.getContentPane().add(lblTreatmentCosts);

        txtDiseaseDuration = new JTextField();
        txtDiseaseDuration.setText("10");
        txtDiseaseDuration.setBounds(177, 460, 50, 26);
        frame.getContentPane().add(txtDiseaseDuration);
        txtDiseaseDuration.setColumns(10);

        txtDiseaseTransmissionRate = new JTextField();
        txtDiseaseTransmissionRate.setText("0.1");
        txtDiseaseTransmissionRate.setBounds(177, 484, 50, 26);
        frame.getContentPane().add(txtDiseaseTransmissionRate);
        txtDiseaseTransmissionRate.setColumns(10);

        txtDiseaseTreatmentCosts = new JTextField();
        txtDiseaseTreatmentCosts.setText("1");
        txtDiseaseTreatmentCosts.setBounds(177, 513, 50, 26);
        frame.getContentPane().add(txtDiseaseTreatmentCosts);
        txtDiseaseTreatmentCosts.setColumns(10);


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
