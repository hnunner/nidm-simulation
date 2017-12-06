package nl.uu.socnetid.network_games.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

/**
 * @author Hendrik Nunner
 */
public class ExportOutputPlayerPanel extends JPanel {

    private static final long serialVersionUID = 8666500343794536885L;

    // amount of players
    private JSpinner playersSpinner;

    /**
     * Create the panel.
     */
    public ExportOutputPlayerPanel() {
        setLayout(null);

        JLabel lblPlayers = new JLabel("Players:");
        lblPlayers.setBounds(6, 6, 61, 16);
        add(lblPlayers);

        playersSpinner = new JSpinner();
        playersSpinner.setBounds(79, 1, 81, 26);
        add(playersSpinner);
    }

    /**
     * Gets the amount of players.
     *
     * @return the amount of players
     */
    public int getPlayerAmount() {
        return (Integer) this.playersSpinner.getValue();
    }

}
