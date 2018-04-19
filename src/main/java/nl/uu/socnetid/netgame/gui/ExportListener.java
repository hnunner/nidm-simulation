package nl.uu.socnetid.netgame.gui;

/**
 * @author Hendrik Nunner
 */
public interface ExportListener {

    /**
     * Entry point for export recording being started notifications.
     */
    void notifyRecordingStarted();

    /**
     * Entry point for export recording being stopped notifications.
     */
    void notifyRecordingStopped();

}
