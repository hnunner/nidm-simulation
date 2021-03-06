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
package nl.uu.socnetid.nidm.io.csv;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.out.BurgerBuskensParameters;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;

/**
 * @author Hendrik Nunner
 */
public class BurgerBuskensSimulationSummaryWriter extends CsvFileWriter<BurgerBuskensParameters> {

    /**
     * Creates the writer.
     *
     * @param fileName
     *          the name of the file to store the data to
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public BurgerBuskensSimulationSummaryWriter(String fileName, DataGeneratorData<BurgerBuskensParameters> dgData)
            throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {

        List<String> cols = new LinkedList<String>();

        // INDEPENDENT VARIABLES (I)
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        // BurgerBuskens
        cols.add(LogValues.IV_BB_B1.toString());
        cols.add(LogValues.IV_BB_B2.toString());
        cols.add(LogValues.IV_BB_C1.toString());
        cols.add(LogValues.IV_BB_C2.toString());
        cols.add(LogValues.IV_BB_C3.toString());
        cols.add(LogValues.IV_BB_NET_SIZE.toString());
        cols.add(LogValues.IV_BB_IOTA.toString());
        cols.add(LogValues.IV_BB_PHI.toString());
        cols.add(LogValues.IV_BB_OMEGA.toString());

        // DEPENDENT VARIABLES (D)
        // network
        cols.add(LogValues.DV_NET_STABLE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());

        writeLine(cols);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        // INDEPENDENT VARIABLES
        // simulation
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
        // BurgerBuskens
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrB1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrB2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC3()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrOmega()));

        // DEPENDENT VARIABLES
        // network
        currData.add(this.dgData.getNetStatsCurrent().isStable() ? "1" : "0");
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength(this.dgData.getSimStats().getCurrRound())));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));

        writeLine(currData);
    }

}
