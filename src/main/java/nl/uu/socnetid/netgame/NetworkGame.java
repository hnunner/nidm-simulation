package nl.uu.socnetid.netgame;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
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
import nl.uu.socnetid.netgame.gui.ExportGEXFPanel;
import nl.uu.socnetid.netgame.gui.ExportListener;
import nl.uu.socnetid.netgame.gui.IntegerInputVerifier;
import nl.uu.socnetid.netgame.gui.NodeClick;
import nl.uu.socnetid.netgame.gui.NodeClickListener;
import nl.uu.socnetid.netgame.gui.OsType;
import nl.uu.socnetid.netgame.gui.StatsFrame;
import nl.uu.socnetid.netgame.networks.DisplayableNetwork;
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
    private JFrame controlsFrame = new JFrame("Controls");
    private final StatsFrame statsFrame = new StatsFrame("Statistics");

    // UTILITY
    // selection
    private JComboBox<String> modelTypeCBox;
    private final String[] utilityFunctions = {"CIDMo"};  //, "Cumulative"};
    // panels
    private CumulativePanel cumulativePanel = new CumulativePanel();
    private CIDMoPanel cidmoPanel = new CIDMoPanel();
    private final DeactivatablePanel[] utilityPanels = {cumulativePanel, cidmoPanel};

    // ACTOR
    // network size
    private JFormattedTextField txtAddAmount;
    private JFormattedTextField txtRemoveAmount;
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

    // EXPORT
    // selection
    private JComboBox<String> exportCBox;
    private final String[] networkWriters = {"GEXF", "Edge List", "Adjacency Matrix"};
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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NetworkGame window = new NetworkGame();
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
        controlsFrame.setBounds(10, 10, 352, 740);
        switch (osType) {
            case WIN:
                break;
            case MAC:
                controlsFrame.setBounds(10, 10, 352, 740);
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
        tabbedPane.setBounds(6, 6, 340, 715);
        controlsFrame.getContentPane().add(tabbedPane);


        //////////// UTILITY ////////////
        JPanel modelPane = new JPanel();
        modelPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(192, 192, 192)));
        tabbedPane.add("Model", modelPane);
        modelPane.setLayout(null);

        cidmoPanel.setBounds(3, 51, 312, 615);
        modelPane.add(cidmoPanel);

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
                        cidmoPanel.setVisible(true);
                        cumulativePanel.setVisible(false);
                        break;

                    case 1:
                        cidmoPanel.setVisible(false);
                        cumulativePanel.setVisible(true);
                        break;

                    default:
                        throw new RuntimeException("Undefined utility function!");
                }
            }
        });

        //////////// DISEASE ////////////
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
        btnClearAll.setBounds(40, 196, 258, 30);
        simulationPane.add(btnClearAll);

        txtAddAmount = new JFormattedTextField(NUM_FORMAT);
        txtAddAmount.setValue(new Integer(1));
        txtAddAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        txtAddAmount.setColumns(10);
        txtAddAmount.setBounds(248, 41, 50, 20);
        txtAddAmount.setInputVerifier(N_VERIFIER);
        simulationPane.add(txtAddAmount);

        JLabel lblAmount = new JLabel("amount");
        lblAmount.setToolTipText("Risk behavior of the actor - r<1: risk seeking, "
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
        label.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
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
        lblNetworkControls.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
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


        //////////// SIMULATION ////////////
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

        JButton btnInfectRandomActor = new JButton("Infect Random Actor");
        btnInfectRandomActor.setBounds(40, 99, 258, 30);
        simulationPane.add(btnInfectRandomActor);
        btnInfectRandomActor.setIcon(new ImageIcon(getClass().getResource("/infect.png")));

        JSeparator separator_5 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_5.setForeground(Color.LIGHT_GRAY);
        separator_5.setBounds(3, 237, 312, 10);
        simulationPane.add(separator_5);

        JLabel lblOnNodeClick_1 = new JLabel("On node click:");
        lblOnNodeClick_1.setToolTipText("Risk behavior of the actor - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblOnNodeClick_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblOnNodeClick_1.setBounds(16, 247, 238, 16);
        simulationPane.add(lblOnNodeClick_1);

        chckbxShowActorStats = new JCheckBox("Show agent stats");
        chckbxShowActorStats.setBounds(38, 272, 141, 23);
        simulationPane.add(chckbxShowActorStats);
        chckbxShowActorStats.setSelected(true);

        chckbxToggleInfection = new JCheckBox("Toggle infection");
        chckbxToggleInfection.setBounds(38, 297, 141, 23);
        simulationPane.add(chckbxToggleInfection);
        chckbxToggleInfection.setSelected(false);

        JSeparator separator_4 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_4.setForeground(Color.LIGHT_GRAY);
        separator_4.setBounds(3, 332, 312, 10);
        simulationPane.add(separator_4);

        JSeparator separator_1 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_1.setForeground(Color.LIGHT_GRAY);
        separator_1.setBounds(3, 500, 312, 10);
        simulationPane.add(separator_1);

        JSeparator separator_2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator_2.setForeground(Color.LIGHT_GRAY);
        separator_2.setBounds(3, 504, 312, 10);
        simulationPane.add(separator_2);
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
        for (int i = 0; i < ((Number)txtAddAmount.getValue()).intValue(); i++) {
            Actor actor = this.network.addActor(uf, ds, cidmoPanel.getRSigma(), cidmoPanel.getRPi(), cidmoPanel.getPhi());
            actor.addActorListener(this);
        }

        // update stats
        if (this.network.getActors().size() <= 1) {
            this.statsFrame.refreshGlobalUtilityStats(uf);
            this.statsFrame.refreshGlobalDiseaseStats(ds);
        }
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalActorStats(this.network));
        this.statsFrame.refreshGlobalNetworkStats(StatsComputer.computeGlobalNetworkStats(this.network));
        this.statsFrame.refreshGlobalSimulationStats(StatsComputer.computeGlobalSimulationStats(this.simulation));
    }

    /**
     * Removes a actor from the game.
     */
    private void removeActor() {
        for (int i = 0; i < ((Number)txtRemoveAmount.getValue()).intValue(); i++) {
            if (!this.network.getActors().isEmpty()) {
                this.network.removeActor();
            }
        }

        // if there are no more actors in the networks enable utility and disease panels
        if (this.network.getActors().isEmpty()) {
            this.statsFrame.resetGlobalAgentStats();
            enableUtilityPanels();
        }

        // update stats
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalActorStats(this.network));
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
                this.cidmoPanel.getTau(),
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
            this.statsFrame.refreshLocalAgentStats(network.getActor(clickActorId));
        }

        // update stats
        this.statsFrame.refreshGlobalAgentStats(StatsComputer.computeGlobalActorStats(this.network));
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
        this.statsFrame.refreshLocalAgentStats(statsActor);
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
