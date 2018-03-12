package nl.uu.socnetid.networkgames;

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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.networkgames.actors.Actor;
import nl.uu.socnetid.networkgames.actors.ActorListener;
import nl.uu.socnetid.networkgames.diseases.DiseaseSpecs;
import nl.uu.socnetid.networkgames.diseases.types.DiseaseType;
import nl.uu.socnetid.networkgames.gui.CumulativePanel;
import nl.uu.socnetid.networkgames.gui.DeactivatablePanel;
import nl.uu.socnetid.networkgames.gui.ExportAdjacencyMatrixPanel;
import nl.uu.socnetid.networkgames.gui.ExportEdgeListPanel;
import nl.uu.socnetid.networkgames.gui.ExportGEXFPanel;
import nl.uu.socnetid.networkgames.gui.IRTCPanel;
import nl.uu.socnetid.networkgames.gui.NodeClick;
import nl.uu.socnetid.networkgames.gui.NodeClickListener;
import nl.uu.socnetid.networkgames.gui.SIRPanel;
import nl.uu.socnetid.networkgames.gui.StatsFrame;
import nl.uu.socnetid.networkgames.gui.TruncatedConnectionsPanel;
import nl.uu.socnetid.networkgames.networks.DisplayableNetwork;
import nl.uu.socnetid.networkgames.simulation.Simulation;
import nl.uu.socnetid.networkgames.simulation.SimulationListener;
import nl.uu.socnetid.networkgames.simulation.ThreadedSimulation;
import nl.uu.socnetid.networkgames.stats.StatsComputer;
import nl.uu.socnetid.networkgames.utilities.Cumulative;
import nl.uu.socnetid.networkgames.utilities.IRTC;
import nl.uu.socnetid.networkgames.utilities.TruncatedConnections;
import nl.uu.socnetid.networkgames.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements NodeClickListener, SimulationListener, ActorListener {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // NETWORK
    private final DisplayableNetwork network = new DisplayableNetwork();

    // WINDOWS
    private JFrame settingsFrame;
    private final StatsFrame statsFrame = new StatsFrame("Statistics");

    // UTILITY
    // selection
    private JComboBox<String> utilityFunctionCBox;
    private final String[] utilityFunctions = {"IRTC", "Cumulative", "Truncated Connections"};
    // panels
    private CumulativePanel cumulativePanel = new CumulativePanel();
    private TruncatedConnectionsPanel truncatedConnectionsPanel = new TruncatedConnectionsPanel();
    private IRTCPanel irtcPanel = new IRTCPanel();
    private final DeactivatablePanel[] utilityPanels = {cumulativePanel, truncatedConnectionsPanel, irtcPanel};

    // DISEASE
    // selection
    private JComboBox<String> diseaseCBox;
    private String[] diseases = {DiseaseType.SIR.toString()};
    // panels
    private SIRPanel sirPanel = new SIRPanel();
    private final DeactivatablePanel[] diseasePanels = {sirPanel};

    // EXPORT
    // selection
    private JComboBox<String> exportCBox;
    private final String[] networkWriters = {"GEXF", "Edge List", "Adjacency Matrix"};
    // panels
    private ExportGEXFPanel gexfPanel;
    private ExportEdgeListPanel edgeListPanel;
    private ExportAdjacencyMatrixPanel adjacencyMatrixPanel;
    private final DeactivatablePanel[] exportPanels = {gexfPanel, edgeListPanel, adjacencyMatrixPanel};

    // ACTOR
    // amount to add
    private JTextField txtAddAmount;
    // risk behavior
    private JLabel lblR;
    private JTextField txtR;
    // on node click
    private Actor statsActor;
    private ExecutorService nodeClickExecutor = Executors.newSingleThreadExecutor();
    private JCheckBox chckbxShowActorStats;
    private JCheckBox chckbxToggleInfection;

    // SIMULATION
    private ThreadedSimulation simulation;
    private ExecutorService simulationExecutor = Executors.newSingleThreadExecutor();
    private Future<?> simulationTask;
    private JSpinner simulationDelay;


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
        settingsFrame.setBounds(100, 100, 370, 555);
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.getContentPane().setLayout(null);

        //////////// TABBED PANE (UTILITY, DISEASE, EXPORT, ACTORS) ////////////
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(25, 6, 315, 350);
        settingsFrame.getContentPane().add(tabbedPane);


        //////////// UTILITY ////////////
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
                        txtR.setEnabled(true);
                        cumulativePanel.setVisible(false);
                        truncatedConnectionsPanel.setVisible(false);
                        break;

                    case 1:
                        irtcPanel.setVisible(false);
                        txtR.setEnabled(false);
                        txtR.setText("1.00");
                        cumulativePanel.setVisible(true);
                        truncatedConnectionsPanel.setVisible(false);
                        break;

                    case 2:
                        irtcPanel.setVisible(false);
                        txtR.setEnabled(false);
                        txtR.setText("1.00");
                        cumulativePanel.setVisible(false);
                        truncatedConnectionsPanel.setVisible(true);
                        break;

                    default:
                        throw new RuntimeException("Undefined utility function!");
                }
            }
        });
        utilityFunctionCBox.setBounds(20, 6, 215, 30);
        utilityPane.add(utilityFunctionCBox);

        irtcPanel.setBounds(20, 38, 214, 225);
        utilityPane.add(irtcPanel);

        cumulativePanel.setBounds(20, 38, 214, 225);
        cumulativePanel.setVisible(false);
        utilityPane.add(cumulativePanel);

        truncatedConnectionsPanel.setBounds(20, 38, 214, 225);
        truncatedConnectionsPanel.setVisible(false);
        utilityPane.add(truncatedConnectionsPanel);


        //////////// DISEASE ////////////
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
        for (int i = 0; i < diseases.length; i ++) {
            diseaseCBox.addItem(diseases[i]);
        }
        diseaseCBox.setBounds(20, 6, 215, 30);
        diseasePane.add(diseaseCBox);

        sirPanel.setBounds(20, 38, 214, 225);
        sirPanel.setVisible(true);
        diseasePane.add(sirPanel);


        //////////// EXPORT ////////////
        JPanel exportPane = new JPanel();
        exportPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.add("Export", exportPane);
        exportPane.setLayout(null);

        gexfPanel = new ExportGEXFPanel(this.network);
        gexfPanel.setBounds(20, 38, 214, 225);
        exportPane.add(gexfPanel);

        adjacencyMatrixPanel = new ExportAdjacencyMatrixPanel(this.network);
        adjacencyMatrixPanel.setBounds(20, 38, 214, 225);
        exportPane.add(adjacencyMatrixPanel);

        edgeListPanel = new ExportEdgeListPanel(this.network);
        edgeListPanel.setBounds(20, 38, 214, 225);
        exportPane.add(edgeListPanel);

        exportCBox = new JComboBox<String>();
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
        exportCBox.setBounds(20, 6, 215, 30);
        exportPane.add(exportCBox);


        //////////// ACTORS ////////////
        JPanel actorPane = new JPanel();
        actorPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.addTab("Actors", actorPane);
        actorPane.setLayout(null);

        JButton btnAddActor = new JButton("Add Actor");
        btnAddActor.setBounds(18, 6, 217, 30);
        actorPane.add(btnAddActor);

        JButton btnRemoveActor = new JButton("Remove Actor");
        btnRemoveActor.setBounds(18, 88, 217, 30);
        actorPane.add(btnRemoveActor);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.setBounds(18, 155, 217, 30);
        actorPane.add(btnClearEdges);

        txtR = new JTextField();
        txtR.setText("1.00");
        txtR.setHorizontalAlignment(SwingConstants.RIGHT);
        txtR.setColumns(10);
        txtR.setBounds(168, 63, 60, 20);
        actorPane.add(txtR);

        lblR = new JLabel("Risk factor");
        lblR.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblR.setBounds(27, 65, 96, 16);
        actorPane.add(lblR);

        JPanel pnlNodeClick = new JPanel();
        pnlNodeClick.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        pnlNodeClick.setBounds(18, 230, 217, 81);
        actorPane.add(pnlNodeClick);
        pnlNodeClick.setLayout(null);

        chckbxToggleInfection = new JCheckBox("Toggle infection");
        chckbxToggleInfection.setBounds(6, 52, 141, 23);
        pnlNodeClick.add(chckbxToggleInfection);
        chckbxToggleInfection.setSelected(false);

        chckbxShowActorStats = new JCheckBox("Show actor stats");
        chckbxShowActorStats.setBounds(6, 29, 141, 23);
        pnlNodeClick.add(chckbxShowActorStats);
        chckbxShowActorStats.setSelected(true);

        JLabel lblOnNodeClick = new JLabel("On node click:");
        lblOnNodeClick.setBounds(6, 6, 127, 16);
        pnlNodeClick.add(lblOnNodeClick);

        JButton btnClearAll = new JButton("Clear All");
        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        btnClearAll.setBounds(18, 188, 217, 30);
        actorPane.add(btnClearAll);

        txtAddAmount = new JTextField();
        txtAddAmount.setText("1");
        txtAddAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAddAmount.setColumns(10);
        txtAddAmount.setBounds(168, 40, 60, 20);
        actorPane.add(txtAddAmount);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setToolTipText("Risk behavior of the actor - r<1: risk seeking, "
                + "r=1: risk neutral, r>1: risk averse");
        lblAmount.setBounds(27, 42, 103, 16);
        actorPane.add(lblAmount);

        JLabel lblr = new JLabel("(r):");
        lblr.setBounds(138, 65, 24, 16);
        actorPane.add(lblr);

        JLabel lbln = new JLabel("(N):");
        lbln.setBounds(138, 42, 24, 16);
        actorPane.add(lbln);

        JButton btnCreateFullNetwork = new JButton("Create Full Network");
        btnCreateFullNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFullNetwork();
            }
        });
        btnCreateFullNetwork.setBounds(18, 122, 217, 29);
        actorPane.add(btnCreateFullNetwork);

        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEdges();
            }
        });
        btnRemoveActor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeActor();
            }
        });
        btnAddActor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addActor();
            }
        });


        //////////// SIMULATION ////////////
        JButton btnStart = new JButton("   Start");
        btnStart.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnStart.setIcon(new ImageIcon(getClass().getResource("/start.png")));
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        btnStart.setBounds(29, 405, 311, 35);
        settingsFrame.getContentPane().add(btnStart);
        for (int i = 0; i < networkWriters.length; i++) {
            exportCBox.addItem(networkWriters[i]);
        }
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(261, 370, 70, 26);
        settingsFrame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (100 ms):");
        simulationDelayLabel.setBounds(36, 373, 170, 16);
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
        btnPauseSimulation.setBounds(29, 442, 311, 35);
        settingsFrame.getContentPane().add(btnPauseSimulation);

        JButton btnReset = new JButton(" Reset");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        });
        btnReset.setIcon(new ImageIcon(getClass().getResource("/reset.png")));
        btnReset.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        btnReset.setBounds(29, 479, 311, 35);
        settingsFrame.getContentPane().add(btnReset);


        //////////// CLICK LISTENER ////////////
        NodeClick nodeClickListener = new NodeClick(this.network);
        nodeClickListener.addListener(this);
        this.nodeClickExecutor.submit(nodeClickListener);


        //////////// CREATE A UI REPRESENATION OF THE NETWORK ////////////
        this.network.show();
    }


    //////////// USER ALTERABLE NETWORK PROPERTIES ////////////
    /**
     * Adds a actor to the game.
     */
    private void addActor() {

        // disable utility and disease panels
        disableUtilityPanels();
        disableDiseasePanels();

        // get disease and utility specs
        DiseaseSpecs ds = getDiseaseSpecs();
        UtilityFunction uf = getUtilityFunction();

        // add each actor with selected utility function and disease specs
        for (int i = 0; i < Integer.parseInt(txtAddAmount.getText()); i++) {
            Actor actor = this.network.addActor(uf, ds, Double.valueOf(this.txtR.getText()));
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
        this.network.removeActor();

        // if there are no more actors in the networks enable utility and disease panels
        if (this.network.getActors().isEmpty()) {
            this.statsFrame.resetGlobalActorStats();
            enableUtilityPanels();
            enableDiseasePanels();
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


    //////////// UI GETTER ////////////
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


    //////////// SIMULATION ////////////
    /**
     * Runs the actual simulation of the network game.
     */
    private void startSimulation() {

        // initializations
        if (this.simulation == null) {
            this.simulation = new ThreadedSimulation(this.network);
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
     * Enables the disease panel and its subcomponents.
     */
    private void enableDiseasePanels() {
        for (int i = 0; i < diseasePanels.length; i++) {
            diseasePanels[i].enableComponents();
        }
        this.diseaseCBox.setEnabled(true);
    }

    /**
     * Enables the utility panel and its subcomponents.
     */
    private void enableUtilityPanels() {
        for (int i = 0; i < utilityPanels.length; i++) {
            utilityPanels[i].enableComponents();
        }
        this.utilityFunctionCBox.setEnabled(true);
    }

    /**
     * Enables the export panel and its subcomponents.
     */
    @SuppressWarnings("unused")
    private void enableExportPanels() {
        for (int i = 0; i < exportPanels.length; i++) {
            exportPanels[i].enableComponents();
        }
        this.exportCBox.setEnabled(true);
    }

    /**
     * Disables the disease panel and its subcomponents.
     */
    private void disableDiseasePanels() {
        for (int i = 0; i < diseasePanels.length; i++) {
            diseasePanels[i].diseableComponents();
        }
        this.diseaseCBox.setEnabled(false);
    }

    /**
     * Disables the utility panel and its subcomponents.
     */
    private void disableUtilityPanels() {
        for (int i = 0; i < utilityPanels.length; i++) {
            utilityPanels[i].diseableComponents();
        }
        this.utilityFunctionCBox.setEnabled(false);
    }

    /**
     * Disables the export panel and its subcomponents.
     */
    @SuppressWarnings("unused")
    private void disableExportPanels() {
        for (int i = 0; i < exportPanels.length; i++) {
            exportPanels[i].diseableComponents();
        }
        this.exportCBox.setEnabled(false);
    }


    //////////// LISTENER CALLBACKS ////////////
    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.NodeClickListener#notify(nl.uu.socnetid.networkgames.gui.NodeClick)
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
     * @see nl.uu.socnetid.networkgames.simulation.SimulationListener#notifyRoundFinished(nl.uu.socnetid.networkgames.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyAttributeAdded(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Actor actor, String attribute, Object value) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.ActorListener#notifyAttributeChanged(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Actor actor, String attribute, Object oldValue, Object newValue) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorListener#notifyAttributeRemoved(
     * nl.uu.socnetid.networkgames.actors.Actor, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Actor actor, String attribute) {
        updateStats();
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorRoundFinishedListener#notifyRoundFinished(
     * nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyRoundFinished(Actor actor) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorConnectionListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.networkgames.actors.Actor, nl.uu.socnetid.networkgames.actors.Actor)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Actor actor1, Actor actor2) {
        updateStats();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.actors.listeners.ActorConnectionListener#
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

}
