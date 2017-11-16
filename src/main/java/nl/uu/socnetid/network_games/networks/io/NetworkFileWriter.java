package nl.uu.socnetid.network_games.networks.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.writer.NetworkWriter;

/**
 * @author Hendrik Nunner
 */
public class NetworkFileWriter {

    private String file;
    private NetworkWriter networkWriter;
    private Network network;

    /**
     * Constructor. Basic initialization.
     *
     * @param file
     *          the full path and name of the file to write to
     * @param networkWriter
     *          the network writer used to format the network data
     * @param network
     *          the network data to be stored
     */
    public NetworkFileWriter(String file, NetworkWriter networkWriter, Network network) {
        this.file = file;
        this.networkWriter = networkWriter;
        this.network = network;
    }


    /**
     * Writes the NetworkWriter's contents to the specified file.
     */
    public void write() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file))) {
            bw.write(this.networkWriter.write(this.network));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
