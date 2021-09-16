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
import nl.uu.socnetid.nidm.data.out.ExperimentParameters;
import nl.uu.socnetid.nidm.data.out.LogValues;

/**
 * @author Hendrik Nunner
 */
public class ExperimentRoundSummaryWriter extends CsvFileWriter<ExperimentParameters> {

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
    public ExperimentRoundSummaryWriter(String fileName, DataGeneratorData<ExperimentParameters> dgData) throws IOException {
        super(fileName, dgData);
    }


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

        // varied model parameters
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
        cols.add(LogValues.IV_NB_TAU.toString());
        cols.add(LogValues.IV_NB_R_AV.toString());
        cols.add(LogValues.IV_NB_R_ABOVE_AV.toString());

        // PROPERTIES
        // simulation
        cols.add(LogValues.DV_SIM_STAGE.toString());
        // network
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_AV_PATHLENGTH.toString());
        cols.add(LogValues.DV_NET_AV_BETWEENNESS.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS.toString());
        cols.add(LogValues.DV_NET_ASSORTATIVITY_RISK_PERCEPTION.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());
        // index case
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_BETWEENNESS_NORMALIZED.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_ASSORTATIVITY_RISK_PERCEPTION.toString());

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

        // varied model parameters
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTau()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAverageRiskScore()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isAboveAverage()));


        // PROPERTIES
        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getSimStage()));
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvPathLength(this.dgData.getSimStats().getCurrRound())));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvBetweenness(this.dgData.getSimStats().getCurrRound())));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvCloseness(this.dgData.getSimStats().getCurrRound())));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAssortativityRiskPerception()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getRecoveredPercent()));
        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsCurrent().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsCurrent().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsCurrent().getBetweennessNormalized()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsCurrent().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStatsCurrent().getAssortativityRiskPerception()));

        writeLine(currData);
    }

}
