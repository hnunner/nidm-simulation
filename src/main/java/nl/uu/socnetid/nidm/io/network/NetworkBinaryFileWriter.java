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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.networks.Network;

/**
 * @author Hendrik Nunner
 */
public class NetworkBinaryFileWriter extends NetworkFileWriter {

    // logger
    private static final Logger logger = LogManager.getLogger(NetworkBinaryFileWriter.class);

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
    public NetworkBinaryFileWriter(String path, String file, Network network) {
        super(path, file, network);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.network.NetworkFileWriter#write()
     */
    @Override
    public void write() {

        try {

            FileOutputStream fos = new FileOutputStream(this.getFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this.getNetwork());

            oos.close();
            fos.close();

        } catch (FileNotFoundException e) {
            logger.error("File not found: " + this.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error during stream initialization.");
        }

    }

}
