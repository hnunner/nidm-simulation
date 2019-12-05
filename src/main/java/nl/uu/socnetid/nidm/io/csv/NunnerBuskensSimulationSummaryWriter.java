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

import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;
import nl.uu.socnetid.nidm.data.NunnerBuskensParameters;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensSimulationSummaryWriter extends SimulationSummaryWriter<NunnerBuskensParameters> {

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
    public NunnerBuskensSimulationSummaryWriter(String fileName, DataGeneratorData<NunnerBuskensParameters> dgData)
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
        cols.add(LogValues.IV_NB_NET_SIZE.toString());
        cols.add(LogValues.IV_NB_IOTA.toString());
        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        cols.add(LogValues.IV_NB_OMEGA_SHUFFLE.toString());
        cols.add(LogValues.IV_NB_YGLOBAL.toString());

        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
        cols.add(LogValues.IV_NB_TAU.toString());
        cols.add(LogValues.IV_NB_RS_EQUAL.toString());
        cols.add(LogValues.IV_NB_R_SIGMA_AV.toString());
        cols.add(LogValues.IV_NB_R_PI_AV.toString());
        return cols;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.SimulationSummaryWriter#addCurrModelData(List<String> currData)
     */
    @Override
    protected List<String> addCurrModelData(List<String> currData) {
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrB1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrB2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC1()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC2()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrOmega()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrOmegaShuffle() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrYGlobal() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrTau()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRSigmaAv()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRPiAv()));
        return currData;
    }

}
