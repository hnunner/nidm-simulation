package nl.uu.socnetid.network_games.gui;

import javax.swing.JPanel;

/**
 * @author Hendrik Nunner
 */
public abstract class DeactivatablePanel extends JPanel {

    /**
     * Enables all interactive components of the panel.
     */
    public abstract void enableComponents();

    /**
     * Disables all interactive components of the panel.
     */
    public abstract void diseableComponents();

}
