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

import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.UtilityModelParameters;

/**
 * @author Hendrik Nunner
 *
 * @param <UMP>
 *          the type of {@link UtilityModelParameters}
 */
public abstract class RoundSummaryWriter<UMP extends UtilityModelParameters> extends CsvFileWriter<UMP> {

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
    public RoundSummaryWriter(String fileName, DataGeneratorData<UMP> dgData) throws IOException {
        super(fileName, dgData);
    }


    /**
     * Adds columns specific for the model implementation.
     *
     * @param cols
     *          the list of columns to add to
     * @return the list with the added columns
     */
    protected abstract List<String> addModelColumns(List<String> cols);

    /**
     * Adds current data specific for the model implementation.
     *
     * @param currData
     *          the list of current data to add to
     * @return the list with the added data
     */
    protected abstract List<String> addCurrModelData(List<String> currData);


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());
        cols.add(LogValues.IV_SIM_ROUND.toString());

        // model specific columns
        cols = addModelColumns(cols);

        // PROPERTIES
        // simulation
        cols.add(LogValues.DV_SIM_STAGE.toString());
        // network
        cols.add(LogValues.DV_NET_STABLE.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());

        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY.toString());

        // TODO
//        cols.add(LogValues.DV_NET_AV_DEGREE_INDEX.toString());
//        cols.add(LogValues.DV_NET_AV_CLUSTERING_INDEX.toString());
//        cols.add(LogValues.DV_NET_AV_BETWEENNESS_INDEX.toString());
//        cols.add(LogValues.DV_NET_AV_ASSORTATIVITY_INDEX.toString());

        cols.add(LogValues.DV_NET_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());

        // FILE SYSTEM
        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        // PARAMETERS
        // simulation
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));
        currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));

        // model specific data
        currData = addCurrModelData(currData);

        // PROPERTIES
        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getSimStage()));
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().isStable()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativity()));

        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getRecoveredPercent()));

        writeLine(currData);
    }

}
