package nl.uu.socnetid.networkgames.gui;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import nl.uu.socnetid.networkgames.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class ExportFrame extends JFrame {
    public ExportFrame() {
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        panel.setBounds(6, 6, 315, 240);
        getContentPane().add(panel);

        JLabel label = new JLabel("Export:");
        label.setBounds(15, 10, 45, 16);
        panel.add(label);

        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.setBounds(85, 6, 215, 30);
        panel.add(comboBox);

        ExportGEXFPanel exportGEXFPanel = new ExportGEXFPanel((Network) null);
        exportGEXFPanel.setBounds(85, 34, 214, 192);
        panel.add(exportGEXFPanel);

        ExportAdjacencyMatrixPanel exportAdjacencyMatrixPanel = new ExportAdjacencyMatrixPanel((Network) null);
        exportAdjacencyMatrixPanel.setBounds(85, 34, 214, 192);
        panel.add(exportAdjacencyMatrixPanel);

        ExportEdgeListPanel exportEdgeListPanel = new ExportEdgeListPanel((Network) null);
        exportEdgeListPanel.setBounds(85, 34, 214, 192);
        panel.add(exportEdgeListPanel);
    }


}
