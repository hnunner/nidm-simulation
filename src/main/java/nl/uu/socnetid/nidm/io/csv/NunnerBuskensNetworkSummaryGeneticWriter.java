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
import java.util.List;

import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensGeneticParameters;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensNetworkSummaryGeneticWriter extends NetworkSummaryWriter<NunnerBuskensGeneticParameters> {

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
    public NunnerBuskensNetworkSummaryGeneticWriter(String fileName, DataGeneratorData<NunnerBuskensGeneticParameters> dgData)
            throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.SimulationSummaryWriter#addModelColumns(List<String> cols)
     */
    @Override
    protected List<String> addModelColumns(List<String> cols) {
        cols.add(LogValues.IV_NB_B1.toString());
        cols.add(LogValues.IV_NB_B2.toString());
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_C1.toString());
        cols.add(LogValues.IV_NB_C2.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_NET_SIZE.toString());
        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_PSI.toString());

        cols.add(LogValues.IV_NB_GEN_TARGET_AVDEGREE.toString());
        cols.add(LogValues.IV_NB_GEN_TARGET_CLUSTERING.toString());

        cols.add(LogValues.IV_NB_GEN_GENERATION.toString());
        cols.add(LogValues.IV_NB_GEN_SIMPLE_ID.toString());
        cols.add(LogValues.IV_NB_GEN_MOTHER.toString());
        cols.add(LogValues.IV_NB_GEN_FATHER.toString());

        cols.add(LogValues.DV_NET_GEN_FITNESS_AV_DEGREE.toString());
        cols.add(LogValues.DV_NET_GEN_FITNESS_CLUSTERING.toString());
        cols.add(LogValues.DV_NET_GEN_FITNESS_OVERALL.toString());

        return cols;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.SimulationSummaryWriter#addCurrModelData(List<String> currData)
     */
    @Override
    protected List<String> addCurrModelData(List<String> currData) {
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getB2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getPsi()));

        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTargetAvDegree()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getTargetClustering()));

        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getGeneration()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getSimpleId()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getMother()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFather()));

        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessAvDegree()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessClustering()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOffspring().getFitnessOverall()));

        return currData;
    }

}
