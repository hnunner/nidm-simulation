/*
 * Copyright (C) 2017 - 2019
 *      Hendrik Nunner    <h.nunner@gmail.com>
 *
 * This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
 *
 * This project is a stand-alone Java program of the Networking during Infectious Diseases Model
 * (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
 * formation and infectious diseases.
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * References:
 *      Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
 *      social networks and infectious diseases. Manuscript sumbitted for publication.
 */
package nl.uu.socnetid.nidm.io.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public abstract class NetworkFileWriter {

    private final File file;
    private final Network network;

    /**
     * Constructor. Basic initialization.
     *
     * @param path
     *          the path of the file to write
     * @param file
     *          the name of the file to write to
     * @param network
     *          the network data to be stored
     */
    public NetworkFileWriter(String path, String file, Network network) {

        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.file = new File(path, file);
        this.network = network;
    }

    /**
     * Writes the network to the specified file.
     */
    public abstract void write();

    /**
     * @return the file
     */
    protected File getFile() {
        return file;
    }

    /**
     * @return the network
     */
    protected Network getNetwork() {
        return network;
    }

}
