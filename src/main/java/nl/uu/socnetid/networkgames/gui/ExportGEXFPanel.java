package nl.uu.socnetid.networkgames.gui;

import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.networkgames.io.GEXFWriter;
import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportGEXFPanel extends DeactivatablePanel {

    private static final long serialVersionUID = -8501382512331293601L;

    // default export directory
    private static final String DEFAULT_EXPORT_DIR = new StringBuilder().append(System.getProperty("user.dir"))
            .append("/network-exports/").toString();

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
        btnExport.setBounds(6, 95, 202, 30);
        add(btnExport);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(6, 6, 202, 81);
        add(panel);

        JLabel lblNetworkType = new JLabel("Network type:");
        lblNetworkType.setBounds(6, 6, 127, 16);
        panel.add(lblNetworkType);

        rdbtnDynamic = new JRadioButton("Dynamic");
        rdbtnDynamic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVisibility();
            }
        });
        rdbtnDynamic.setBounds(6, 27, 141, 23);
        panel.add(rdbtnDynamic);

        rdbtnStatic = new JRadioButton("Static");
        rdbtnStatic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVisibility();
            }
        });
        rdbtnStatic.setBounds(6, 52, 141, 23);
        panel.add(rdbtnStatic);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rdbtnStatic);
        buttonGroup.add(rdbtnDynamic);
        rdbtnDynamic.setSelected(true);

        btnStartRecording = new JButton("Start recording");
        btnStartRecording.setIcon(new ImageIcon(getClass().getResource("/record.png")));
        btnStartRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRecording();
            }
        });
        btnStartRecording.setBounds(6, 95, 202, 30);
        btnStartRecording.setToolTipText("If no explicit output file is selected, "
                + "a default file will be generated in folder: "
                + DEFAULT_EXPORT_DIR);
        add(btnStartRecording);

        btnStopRecording = new JButton("Stop recording");
        btnStopRecording.setIcon(new ImageIcon(getClass().getResource("/stop.png")));
        btnStopRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });
        btnStopRecording.setBounds(6, 125, 202, 30);
        add(btnStopRecording);

        btnChooseExportFile = new JButton("Choose export file");
        btnChooseExportFile.setIcon(new ImageIcon(getClass().getResource("/open.png")));
        btnChooseExportFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseExportFile();
            }
        });
        btnChooseExportFile.setBounds(6, 155, 202, 30);
        add(btnChooseExportFile);

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
            StringBuilder sb = new StringBuilder(DEFAULT_EXPORT_DIR);
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
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#enableComponents()
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
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#diseableComponents()
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
     * Notifies listeners of finished actor rounds.
     */
    private final void notifyRecordingStarted() {
        Iterator<ExportListener> listenersIt = this.exportListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRecordingStarted();
        }
    }

    /**
     * Notifies listeners of finished actor rounds.
     */
    private final void notifyRecordingStopped() {
        Iterator<ExportListener> listenersIt = this.exportListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRecordingStopped();
        }
    }
}
