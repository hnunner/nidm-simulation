package nl.uu.socnetid.network_games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import nl.uu.socnetid.network_games.network.writer.AdjacencyMatrixWriter;
import nl.uu.socnetid.network_games.network.writer.EdgeListWriter;
import nl.uu.socnetid.network_games.network.writer.NetworkOutputType;
import nl.uu.socnetid.network_games.network.writer.NetworkWriter;

/**
 * @author Hendrik Nunner
 */
public class ExportOutputExportPanel extends JPanel {

    private static final long serialVersionUID = 7454884529858650022L;

    // listener
    private final Set<ExportOutputExportListener> exportNetworkListeners =
            new CopyOnWriteArraySet<ExportOutputExportListener>();

    // edge writer combo box and selection
    private JComboBox<String> edgeWriterCBox;
    // amount of simulation
    private JSpinner simAmount;
    // final state / sequence
    private JRadioButton rdbtnFinalState;
    private JRadioButton rdbtnSequence;
    private ButtonGroup bgStateVsSequence;

    /**
     * Create the panel.
     */
    public ExportOutputExportPanel() {
        setLayout(null);

        edgeWriterCBox = new JComboBox<String>();
        edgeWriterCBox.setBounds(6, 132, 154, 27);
        add(edgeWriterCBox);

        for (NetworkOutputType type : NetworkOutputType.values()) {
            edgeWriterCBox.addItem(type.toString());
        }

        JButton btnNewButton = new JButton("Export");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyExportNetworkListeners();
            }
        });
        btnNewButton.setBounds(6, 171, 154, 29);
        add(btnNewButton);

        JLabel lblNewLabel = new JLabel("Simulations:");
        lblNewLabel.setBounds(6, 6, 83, 16);
        add(lblNewLabel);

        simAmount = new JSpinner();
        simAmount.setBounds(101, 1, 59, 26);
        add(simAmount);

        JLabel lblFinalNetwork = new JLabel("Final State / Sequence:");
        lblFinalNetwork.setBounds(6, 34, 154, 16);
        add(lblFinalNetwork);

        rdbtnFinalState = new JRadioButton("Final State");
        rdbtnFinalState.setBounds(19, 62, 141, 23);
        add(rdbtnFinalState);

        rdbtnSequence = new JRadioButton("Sequence");
        rdbtnSequence.setBounds(19, 97, 141, 23);
        add(rdbtnSequence);

        bgStateVsSequence = new ButtonGroup();
        bgStateVsSequence.add(rdbtnFinalState);
        bgStateVsSequence.add(rdbtnSequence);
    }

    /**
     * Adds a listener to be notified when network is ought to be exported.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addListener(ExportOutputExportListener listener) {
        this.exportNetworkListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeExportNetworkListener(ExportOutputExportListener listener) {
        this.exportNetworkListeners.remove(listener);
    }

    /**
     * Notifies the listeners of added player.
     */
    private final void notifyExportNetworkListeners() {
        Iterator<ExportOutputExportListener> listenersIt = this.exportNetworkListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyExportNetwork(this);
        }
    }

    /**
     * Gets the selected type of network writer.
     *
     * @return the selected type of network writer
     */
    public NetworkWriter getSelectedNetworkWriter() {
        switch (edgeWriterCBox.getSelectedIndex()) {
            case 0:
                return new EdgeListWriter();

            case 1:
                return new AdjacencyMatrixWriter();

            default:
                throw new RuntimeException("Undefined network writer!");
        }
    }

    /**
     * Gets the amount of simulations.
     *
     * @return the amount of simulations
     */
    public int getSimAmount() {
        return (Integer) this.simAmount.getValue();
    }

    /**
     * @return true, if simulation is to be exported as sequence, false otherwise
     */
    public boolean exportAsSequence() {
        return this.rdbtnSequence.isSelected();
    }

}
