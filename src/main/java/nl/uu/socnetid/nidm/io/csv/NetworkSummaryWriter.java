/*
 * Copyright (C) 2017 - 2020
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
public abstract class NetworkSummaryWriter<UMP extends UtilityModelParameters> extends CsvFileWriter<UMP> {

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
    public NetworkSummaryWriter(String fileName, DataGeneratorData<UMP> dgData) throws IOException {
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

        // INDEPENDENT VARIABLES (I)
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());
        cols.add(LogValues.IV_SIM_ROUND.toString());

        // model specific columns
        cols = addModelColumns(cols);

        // DEPENDENT VARIABLES (D)
        // network
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());
//        cols.add(LogValues.DV_NET_ASSORTATIVITY_CONDITIONS.toString());
//        cols.add(LogValues.DV_NET_ASSORTATIVITY_RISK_PERCEPTION.toString());
//        cols.add(LogValues.DV_NET_ASSORTATIVITY_AGE.toString());
//        cols.add(LogValues.DV_NET_ASSORTATIVITY_PROFESSION.toString());
        cols.add(LogValues.DV_NET_STABLE.toString());

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
        currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));
        currData.add(String.valueOf(this.dgData.getSimStats().getCurrRound()));

        // model specific data
        currData = addCurrModelData(currData);

        // DEPENDENT VARIABLES
        // network - pre epidemic
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));

//        currData.add("NA");
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvCloseness(this.dgData.getSimStats().getCurrRound())));

        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));

//        currData.add("NA");
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength(this.dgData.getSimStats().getCurrRound())));

        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));
//        currData.add(this.dgData.getNetStatsCurrent().getAssortativityConditions().toString());
//        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativityRiskPerception()));
//        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativityAge()));
//        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativityProfession()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().isStable() ? 1 : 0));

        writeLine(currData);
    }

}
