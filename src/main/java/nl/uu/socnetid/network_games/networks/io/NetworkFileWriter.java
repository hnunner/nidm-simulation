package nl.uu.socnetid.network_games.networks.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.uu.socnetid.network_games.networks.Network;
import nl.uu.socnetid.network_games.networks.writer.NetworkWriter;

/**
 * @author Hendrik Nunner
 */
public class NetworkFileWriter {

    private String path;
    private String file;
    private NetworkWriter networkWriter;
    private Network network;

    /**
     * Constructor. Basic initialization.
     *
     * @param path
     *          the path of the file to write
     * @param file
     *          the name of the file to write to
     * @param networkWriter
     *          the network writer used to format the network data
     * @param network
     *          the network data to be stored
     */
    public NetworkFileWriter(String path, String file, NetworkWriter networkWriter, Network network) {
        this.path = path;
        this.file = file;
        this.networkWriter = networkWriter;
        this.network = network;
    }


    /**
     * Writes the NetworkWriter's contents to the specified file.
     */
    public void write() {
        Path path = Paths.get(this.path);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file))) {
            bw.write(this.networkWriter.write(this.network));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
