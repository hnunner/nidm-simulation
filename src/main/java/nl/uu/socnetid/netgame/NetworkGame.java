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
import nl.uu.socnetid.netgame.gui.CumulativePanel;
import nl.uu.socnetid.netgame.gui.DeactivatablePanel;
import nl.uu.socnetid.netgame.gui.ExportFrame;
import nl.uu.socnetid.netgame.gui.ExportListener;
import nl.uu.socnetid.netgame.gui.IRTCPanel;
import nl.uu.socnetid.netgame.gui.NodeClick;
import nl.uu.socnetid.netgame.gui.NodeClickListener;
import nl.uu.socnetid.netgame.gui.SIRPanel;
import nl.uu.socnetid.netgame.gui.StatsFrame;
import nl.uu.socnetid.netgame.gui.TruncatedConnectionsPanel;
import nl.uu.socnetid.netgame.networks.DisplayableNetwork;
import nl.uu.socnetid.netgame.simulation.Simulation;
import nl.uu.socnetid.netgame.simulation.SimulationListener;
import nl.uu.socnetid.netgame.stats.StatsComputer;
import nl.uu.socnetid.netgame.utilities.Cumulative;
import nl.uu.socnetid.netgame.utilities.IRTC;
import nl.uu.socnetid.netgame.utilities.TruncatedConnections;
import nl.uu.socnetid.netgame.utilities.UtilityFunction;

/**
 * @author Hendrik Nunner
 */
public class NetworkGame implements NodeClickListener, SimulationListener, ActorListener, ExportListener {

    // logger
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NetworkGame.class);

    // NETWORK
    private final DisplayableNetwork network = new DisplayableNetwork();

    // WINDOWS
    private JFrame settingsFrame = new JFrame("Settings");
    private final StatsFrame statsFrame = new StatsFrame("Statistics");
    private final ExportFrame exportFrame = new ExportFrame("Export", this.network);


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

    // ACTOR
    // amount to add
    private JTextField txtAddAmount;
    // risk behavior
    private JLabel lblR;
    private JTextField txtR = new JTextField();
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
                    window.settingsFrame.setVisible(true);
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
        settingsFrame.setBounds(10, 10, 370, 575);
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.getContentPane().setLayout(null);

        //////////// TABBED PANE (UTILITY, DISEASE, EXPORT, ACTORS) ////////////
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setBorder(null);
        tabbedPane.setBounds(25, 6, 315, 374);
        settingsFrame.getContentPane().add(tabbedPane);


        //////////// UTILITY ////////////
        JPanel utilityPane = new JPanel();
        utilityPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192)));
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
        for (int i = 0; i < utilityFunctions.length; i++) {
            utilityFunctionCBox.addItem(utilityFunctions[i]);
        }
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


        //////////// NETWORK ////////////
        JPanel networkPane = new JPanel();
        networkPane.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        tabbedPane.addTab("Network", networkPane);
        networkPane.setLayout(null);

        JButton btnAddActor = new JButton("Add Actor");
        btnAddActor.setIcon(new ImageIcon(getClass().getResource("/add.png")));
        btnAddActor.setBounds(18, 6, 217, 30);
        networkPane.add(btnAddActor);

        JButton btnRemoveActor = new JButton("Remove Actor");
        btnRemoveActor.setIcon(new ImageIcon(getClass().getResource("/remove.png")));
        btnRemoveActor.setBounds(18, 88, 217, 30);
        networkPane.add(btnRemoveActor);

        JButton btnInfectRandomActor = new JButton("Infect Random Actor");
        btnInfectRandomActor.setIcon(new ImageIcon(getClass().getResource("/infect.png")));
        btnInfectRandomActor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infectRandomActor();
            }
        });
        btnInfectRandomActor.setBounds(18, 120, 217, 30);
        networkPane.add(btnInfectRandomActor);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.setIcon(new ImageIcon(getClass().getResource("/clear.png")));
        btnClearEdges.setBounds(18, 184, 217, 30);
        networkPane.add(btnClearEdges);

        txtR.setText("1.00");
        txtR.setHorizontalAlignment(SwingConstants.RIGHT);
        txtR.setColumns(10);
        txtR.setBounds(168, 63, 60, 20);
        networkPane.add(txtR);

        lblR = new JLabel("Risk factor");
        lblR.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblR.setBounds(27, 65, 96, 16);
        networkPane.add(lblR);

        JPanel pnlNodeClick = new JPanel();
        pnlNodeClick.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        pnlNodeClick.setBounds(18, 255, 217, 81);
        networkPane.add(pnlNodeClick);
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
        btnClearAll.setIcon(new ImageIcon(getClass().getResource("/clearall.png")));
        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        btnClearAll.setBounds(18, 216, 217, 30);
        networkPane.add(btnClearAll);

        txtAddAmount = new JTextField();
        txtAddAmount.setText("1");
        txtAddAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAddAmount.setColumns(10);
        txtAddAmount.setBounds(168, 40, 60, 20);
        networkPane.add(txtAddAmount);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setToolTipText("Risk behavior of the actor - r<1: risk seeking, "
                + "r=1: risk neutral, r>1: risk averse");
        lblAmount.setBounds(27, 42, 103, 16);
        networkPane.add(lblAmount);

        JLabel lblr = new JLabel("(r):");
        lblr.setBounds(138, 65, 24, 16);
        networkPane.add(lblr);

        JLabel lbln = new JLabel("(N):");
        lbln.setBounds(138, 42, 24, 16);
        networkPane.add(lbln);

        JButton btnCreateFullNetwork = new JButton("Create Full Network");
        btnCreateFullNetwork.setIcon(new ImageIcon(getClass().getResource("/full.png")));
        btnCreateFullNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFullNetwork();
            }
        });
        btnCreateFullNetwork.setBounds(18, 152, 217, 29);
        networkPane.add(btnCreateFullNetwork);

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
        btnStart.setBounds(29, 422, 311, 35);
        settingsFrame.getContentPane().add(btnStart);

        simulationDelay = new JSpinner();
        simulationDelay.setBounds(261, 387, 70, 26);
        settingsFrame.getContentPane().add(simulationDelay);

        JLabel simulationDelayLabel = new JLabel("Simulation delay (10 ms):");
        simulationDelayLabel.setBounds(35, 392, 170, 16);
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
        btnPauseSimulation.setBounds(29, 459, 311, 35);
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
        btnReset.setBounds(29, 496, 311, 35);
        settingsFrame.getContentPane().add(btnReset);


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
