package nl.uu.socnetid.nidm.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportGEXFPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -8501382512331293601L;

    // graph
    private Network network;

    // components
    private JButton btnExport;
    private JRadioButton rdbtnDynamic;
    private JRadioButton rdbtnStatic;
    private JButton btnChooseExportFile;
    private JButton btnStartRecording;
    private JButton btnStopRecording;

    /// writer
    private GEXFWriter gexfWriter;

    // file
    private String file;

    // recording or not recording
    private boolean recording;

    // listeners
    private final Set<ExportListener> exportListeners =
            new CopyOnWriteArraySet<ExportListener>();
    private JLabel lblNetworkType_1;
    private JSeparator separator;


    /**
     * Create the panel.
     *
     * @param network
     *          the network to export
     */
    public ExportGEXFPanel(Network network) {
        this.network = network;

        setLayout(null);

        btnExport = new JButton("Export");
        btnExport.setIcon(new ImageIcon(getClass().getResource("/save.png")));
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportStaticNetwork();
            }
        });
        btnExport.setBounds(37, 95, 258, 30);
        add(btnExport);

        ButtonGroup buttonGroup = new ButtonGroup();

        btnStartRecording = new JButton("Start recording");
        btnStartRecording.setIcon(new ImageIcon(getClass().getResource("/record.png")));
        btnStartRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRecording();
            }
        });
        btnStartRecording.setBounds(37, 95, 258, 30);
        btnStartRecording.setToolTipText("If no explicit output file is selected, "
                + "a default file will be generated in folder: "
                + GEXFWriter.DEFAULT_EXPORT_DIR);
        add(btnStartRecording);

        btnStopRecording = new JButton("Stop recording");
        btnStopRecording.setIcon(new ImageIcon(getClass().getResource("/stop.png")));
        btnStopRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });
        btnStopRecording.setBounds(37, 127, 258, 30);
        add(btnStopRecording);

        btnChooseExportFile = new JButton("Choose export file");
        btnChooseExportFile.setIcon(new ImageIcon(getClass().getResource("/open.png")));
        btnChooseExportFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseExportFile();
            }
        });
        btnChooseExportFile.setBounds(37, 159, 258, 30);
        add(btnChooseExportFile);

        lblNetworkType_1 = new JLabel("Network type");
        lblNetworkType_1.setToolTipText("Risk behavior of the agent - r<1: risk seeking, r=1: risk neutral, r>1: risk averse");
        lblNetworkType_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblNetworkType_1.setBounds(13, 0, 238, 16);
        add(lblNetworkType_1);

        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBounds(0, 85, 312, 10);
        add(separator);

                rdbtnStatic = new JRadioButton("Static");
                rdbtnStatic.setBounds(35, 50, 141, 23);
                add(rdbtnStatic);
                rdbtnStatic.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateVisibility();
                    }
                });
                buttonGroup.add(rdbtnStatic);

                        rdbtnDynamic = new JRadioButton("Dynamic");
                        rdbtnDynamic.setSelected(true);
                        rdbtnDynamic.setBounds(35, 25, 141, 23);
                        add(rdbtnDynamic);
                        rdbtnDynamic.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateVisibility();
                            }
                        });
                        buttonGroup.add(rdbtnDynamic);

        updateVisibility();
    }


    /**
     * Updates the visisbility of components according to which type of network export is selected.
     */
    private void updateVisibility() {
        this.btnExport.setVisible(this.rdbtnStatic.isSelected());
        this.btnStartRecording.setVisible(this.rdbtnDynamic.isSelected());
        this.btnStopRecording.setVisible(this.rdbtnDynamic.isSelected());
        this.btnChooseExportFile.setVisible(this.rdbtnDynamic.isSelected());
    }

    /**
     * Exports the current network as static GEXF files.
     */
    private void exportStaticNetwork() {
        JFileChooser fileChooser = new JFileChooser();
        int popdownState = fileChooser.showSaveDialog(null);
        if (popdownState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String file = selectedFile.getPath();

            this.gexfWriter = new GEXFWriter();
            this.gexfWriter.writeStaticNetwork(this.network, file);
        }
    }

    /**
     * Creates a file chooser to allow user to choose a custom export file.
     */
    private void chooseExportFile() {
        JFileChooser fileChooser = new JFileChooser();
        int popdownState = fileChooser.showSaveDialog(null);
        if (popdownState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            this.file = selectedFile.getPath();
        }
    }

    /**
     * Starts the recording of dynamic GEXF files.
     */
    private void startRecording() {
        this.gexfWriter = new GEXFWriter();

        if (this.file != null) {
            this.gexfWriter.startRecording(this.network, this.file);
            this.file = null;

        } else {
            StringBuilder sb = new StringBuilder(GEXFWriter.DEFAULT_EXPORT_DIR);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd-HHmmss");
            sb.append("sim-").
            append(sdfDate.format(new Date())).
            append(".gexf").toString();
            this.gexfWriter.startRecording(this.network, sb.toString());
        }

        this.recording = true;
        updateElementsRecording();

        notifyRecordingStarted();
    }

    /**
     * Stops the recording of dynamic GEXF files.
     */
    private void stopRecording() {
        this.gexfWriter.stopRecording();

        this.recording = false;
        updateElementsRecording();

        notifyRecordingStopped();
    }

    /**
     * Updates the interactive elements according to recording status.
     */
    private void updateElementsRecording() {
        // only stop recording button is enabled
        this.btnStopRecording.setEnabled(this.recording);

        this.rdbtnDynamic.setEnabled(!this.recording);
        this.rdbtnStatic.setEnabled(!this.recording);
        this.btnStartRecording.setEnabled(!this.recording);
        this.btnChooseExportFile.setEnabled(!this.recording);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {
        this.btnExport.setEnabled(true);
        this.rdbtnDynamic.setEnabled(true);
        this.rdbtnStatic.setEnabled(true);
        this.btnStartRecording.setEnabled(true);
        this.btnStopRecording.setEnabled(true);
        this.btnChooseExportFile.setEnabled(true);

    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {
        this.btnExport.setEnabled(false);
        this.rdbtnDynamic.setEnabled(false);
        this.rdbtnStatic.setEnabled(false);
        this.btnStartRecording.setEnabled(false);
        this.btnStopRecording.setEnabled(false);
        this.btnChooseExportFile.setEnabled(false);
    }

    /**
     * Adds a listener for export notifications.
     *
     * @param exportListener
     *          the listener to be added
     */
    public void addExportListener(ExportListener exportListener) {
        this.exportListeners.add(exportListener);
    }

    /**
     * Removes a listener for export notifications.
     *
     * @param exportListener
     *          the listener to be removed
     */
    public void removeExportListener(ExportListener exportListener) {
        this.exportListeners.remove(exportListener);
    }

    /**
     * Notifies listeners of finished agent rounds.
     */
    private final void notifyRecordingStarted() {
        Iterator<ExportListener> listenersIt = this.exportListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRecordingStarted();
        }
    }

    /**
     * Notifies listeners of finished agent rounds.
     */
    private final void notifyRecordingStopped() {
        Iterator<ExportListener> listenersIt = this.exportListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRecordingStopped();
        }
    }
}
