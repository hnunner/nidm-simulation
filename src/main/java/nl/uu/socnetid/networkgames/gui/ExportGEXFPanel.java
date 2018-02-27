package nl.uu.socnetid.networkgames.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.MatteBorder;

/**
 * @author Hendrik Nunner
 */
public class ExportGEXFPanel extends DeactivatablePanel {


    // components
    private JButton btnExport;
    private JLabel lblNetworkType;
    private JRadioButton rdbtnDynamic;
    private JRadioButton rdbtnStatic;
    private JButton btnSelectDynamicOutputFile;
    private JButton btnStartRecording;
    private JButton btnStopRecording;


    /**
     * Create the panel.
     */
    public ExportGEXFPanel() {
        setLayout(null);

        btnExport = new JButton("Export");
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

        lblNetworkType = new JLabel("Network type:");
        lblNetworkType.setBounds(6, 6, 127, 16);
        panel.add(lblNetworkType);

        rdbtnDynamic = new JRadioButton("Dynamic");
        rdbtnDynamic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVisibility();
            }
        });
        rdbtnDynamic.setBounds(6, 52, 141, 23);
        panel.add(rdbtnDynamic);

        rdbtnStatic = new JRadioButton("Static");
        rdbtnStatic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVisibility();
            }
        });
        rdbtnStatic.setBounds(6, 27, 141, 23);
        panel.add(rdbtnStatic);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rdbtnStatic);
        buttonGroup.add(rdbtnDynamic);
        rdbtnStatic.setSelected(true);

        btnSelectDynamicOutputFile = new JButton("Select output file");
        btnSelectDynamicOutputFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDynamicOutputFile();
            }
        });
        btnSelectDynamicOutputFile.setBounds(6, 95, 202, 30);
        add(btnSelectDynamicOutputFile);

        btnStartRecording = new JButton("Start recording");
        btnStartRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRecording();
            }
        });
        btnStartRecording.setBounds(6, 125, 202, 30);
        add(btnStartRecording);

        btnStopRecording = new JButton("Stop recording");
        btnStopRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
            }
        });
        btnStopRecording.setBounds(6, 155, 202, 30);
        add(btnStopRecording);

        updateVisibility();
    }


    /**
     * Updates the visisbility of components according to which type of network export is selected.
     */
    private void updateVisibility() {
        btnExport.setVisible(rdbtnStatic.isSelected());
        btnSelectDynamicOutputFile.setVisible(rdbtnDynamic.isSelected());
        btnStartRecording.setVisible(rdbtnDynamic.isSelected());
        btnStopRecording.setVisible(rdbtnDynamic.isSelected());
    }

    /**
     * Exports the current network as static GEXF files.
     */
    private void exportStaticNetwork() {
        // TODO implement
    }

    /**
     * Selects the output file for dynamic GEXF files.
     */
    private void selectDynamicOutputFile() {
        // TODO implement
    }

    /**
     * Starts the recording of dynamic GEXF files.
     */
    private void startRecording() {
        // TODO implement
    }

    /**
     * Stops the recording of dynamic GEXF files.
     */
    private void stopRecording() {
        // TODO implement
    }



//    /**
//     * @return the duration a disease requires to recover from in rounds (tau)
//     */
//    public int getTau() {
//        return Integer.valueOf(this.txtTau.getText());
//    }
//
//    /**
//     * @return the severity of the disease represented by the amount of punishment for having a disease (delta)
//     */
//    public double getDelta() {
//        return Double.valueOf(this.txtDelta.getText());
//    }
//
//    /**
//     * @return transmission rate (gamma) - the probability a disease is spread between
//     *          an infected and a non-infected agent per round
//     */
//    public double getGamma() {
//        return Double.valueOf(this.txtGamma.getText());
//    }
//
//    /**
//     * @return the factor that increases maintenance costs for infected connections (mu)
//     */
//    public double getMu() {
//        return Double.valueOf(this.txtMu.getText());
//    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#enableComponents()
     */
    @Override
    public void enableComponents() {

        // TODO implement


//        this.txtTau.setEnabled(true);
//        this.txtDelta.setEnabled(true);
//        this.txtGamma.setEnabled(true);
//        this.txtMu.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.networkgames.gui.DeactivatablePanel#diseableComponents()
     */
    @Override
    public void diseableComponents() {

        // TODO implement


//        this.txtTau.setEnabled(false);
//        this.txtDelta.setEnabled(false);
//        this.txtGamma.setEnabled(false);
//        this.txtMu.setEnabled(false);
    }
}
