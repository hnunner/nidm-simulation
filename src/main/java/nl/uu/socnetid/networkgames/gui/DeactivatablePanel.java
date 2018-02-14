package nl.uu.socnetid.networkgames.gui;

import javax.swing.JPanel;

/**
 * @author Hendrik Nunner
 */
public abstract class DeactivatablePanel extends JPanel {

    private static final long serialVersionUID = 5167920276024578376L;

    /**
     * Enables all interactive components of the panel.
     */
    public abstract void enableComponents();

    /**
     * Disables all interactive components of the panel.
     */
    public abstract void diseableComponents();

}
