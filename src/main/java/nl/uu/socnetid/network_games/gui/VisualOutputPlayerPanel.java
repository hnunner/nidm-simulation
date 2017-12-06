package nl.uu.socnetid.network_games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Hendrik Nunner
 */
public class VisualOutputPlayerPanel extends JPanel {

    private static final long serialVersionUID = 4017345956211560879L;

    // listener
    private final Set<AddPlayerListener> addPlayerListeners = new CopyOnWriteArraySet<AddPlayerListener>();
    private final Set<RemovePlayerListener> removePlayerListeners = new CopyOnWriteArraySet<RemovePlayerListener>();
    private final Set<ClearEdgesListener> clearEdgesListeners = new CopyOnWriteArraySet<ClearEdgesListener>();


    /**
     * Create the panel.
     */
    public VisualOutputPlayerPanel() {
        setLayout(null);

        JButton btnAddPlayer = new JButton("Add Player");
        btnAddPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyAddPlayerListeners();
            }
        });
        btnAddPlayer.setBounds(6, 5, 154, 29);
        add(btnAddPlayer);

        JButton btnRemovePlayer = new JButton("Remove Player");
        btnRemovePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyRemovePlayerListeners();
            }
        });
        btnRemovePlayer.setBounds(6, 46, 154, 29);
        add(btnRemovePlayer);

        JButton btnClearEdges = new JButton("Clear Edges");
        btnClearEdges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyClearEdgesListeners();
            }
        });
        btnClearEdges.setBounds(6, 87, 154, 29);
        add(btnClearEdges);

    }

    /**
     * Adds a listener to be notified when a player is added.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addAddPlayerListener(AddPlayerListener listener) {
        this.addPlayerListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeAddPlayerListener(AddPlayerListener listener) {
        this.addPlayerListeners.remove(listener);
    }

    /**
     * Notifies the listeners of added player.
     */
    private final void notifyAddPlayerListeners() {
        Iterator<AddPlayerListener> listenersIt = this.addPlayerListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyAddPlayer();
        }
    }

    /**
     * Adds a listener to be notified when a player is removed.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addRemovePlayerListener(RemovePlayerListener listener) {
        this.removePlayerListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeRemovePlayerListener(RemovePlayerListener listener) {
        this.removePlayerListeners.remove(listener);
    }

    /**
     * Notifies the listeners of added player.
     */
    private final void notifyRemovePlayerListeners() {
        Iterator<RemovePlayerListener> listenersIt = this.removePlayerListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyRemovePlayer();
        }
    }

    /**
     * Adds a listener to be notified when a edges are cleared.
     *
     * @param listener
     *          the listener to be notified
     */
    public void addClearEdgesListener(ClearEdgesListener listener) {
        this.clearEdgesListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener
     *          the listener to be removed
     */
    public void removeClearEdgesListener(ClearEdgesListener listener) {
        this.clearEdgesListeners.remove(listener);
    }

    /**
     * Notifies the listeners of added player.
     */
    private final void notifyClearEdgesListeners() {
        Iterator<ClearEdgesListener> listenersIt = this.clearEdgesListeners.iterator();
        while (listenersIt.hasNext()) {
            listenersIt.next().notifyClearEdges();
        }
    }

}
