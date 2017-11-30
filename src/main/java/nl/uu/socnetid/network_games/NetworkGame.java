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

import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

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
import nl.uu.socnetid.network_games.utility_functions.CumulativeUtilityFunction;
import nl.uu.socnetid.network_games.utility_functions.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements SimulationCompleteListener {

    // general export path
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
    private String[] utilityFunctions = {"Cumulative"};
    // edge writer combo box and selection
    private JComboBox<String> edgeWriterCBox;
    private String[] edgeWriters = {"Edge List", "Adjacency Matrix"};
    // spinner for simulation delay
    private JSpinner simulationDelay;

    // concurrency for simulation
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
        frame.setBounds(100, 100, 430, 680);
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
        edgeWriterCBox.setBounds(16, 479, 178, 29);
        frame.getContentPane().add(edgeWriterCBox);

        JButton btnExportNetwork = new JButton("Export Network as:");
        btnExportNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportNetwork();
            }
        });
        btnExportNetwork.setBounds(6, 438, 188, 29);
        frame.getContentPane().add(btnExportNetwork);

        utilityFunctionCBox = new JComboBox<String>();
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }
        utilityFunctionCBox.setBounds(28, 201, 166, 27);
        frame.getContentPane().add(utilityFunctionCBox);

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(140, 240, 44, 26);
        frame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay:");
        simulationDelayLabel.setBounds(28, 245, 126, 16);
        frame.getContentPane().add(simulationDelayLabel);

        JButton btnInfectPlayer = new JButton("Infect Player");
        btnInfectPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectPlayer();
            }
        });
        btnInfectPlayer.setBounds(30, 316, 197, 29);
        frame.getContentPane().add(btnInfectPlayer);

        JButton btnStopSimulation = new JButton("Stop Simulation");
        btnStopSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                stopSimulation();
            }
        });
        btnStopSimulation.setBounds(198, 145, 126, 45);
        frame.getContentPane().add(btnStopSimulation);

        // init graphstream
        this.graph = new SingleGraph("NetworkGames");
        // graph-stream CSS styles and rendering properties
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        URL gsStyles = this.getClass().getClassLoader().getResource("graph-stream.css");
        graph.addAttribute("ui.stylesheet", "url('file:" + gsStyles.getPath() + "')");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        // show
        this.graph.display();
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
                return new CumulativeUtilityFunction();
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
    private void infectPlayer() {
        this.network.infectRandomPlayer();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.network_games.network.simulation.SimulationCompleteListener#notifyOfSimulationComplete(nl.uu.socnetid.network_games.network.simulation.Simulation)
     */
    @Override
    public void notifyOfSimulationComplete(Simulation simulation) {
        logger.debug("Simulation completed.");
        JOptionPane.showMessageDialog(null, simulation.getStatusMessage());
    }

}
