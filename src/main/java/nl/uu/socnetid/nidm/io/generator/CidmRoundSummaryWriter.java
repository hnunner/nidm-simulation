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
package nl.uu.socnetid.nidm.io.generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.CidmDataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;

/**
 * @author Hendrik Nunner
 */
public class CidmRoundSummaryWriter extends CidmCsvFileWriter {

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
    public CidmRoundSummaryWriter(String fileName, CidmDataGeneratorData dgData) throws IOException {
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
        // cols.add(LogValues.IV_SIM_UPC.toString());
        // cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_ROUND.toString());
        // Cidm - not all parameters to reduce export file size
        // cols.add(LogValues.IV_CIDM_ALPHA_AV.toString());
        // cols.add(LogValues.IV_CIDM_KAPPA_AV.toString());
        cols.add(LogValues.IV_CIDM_BETA_AV.toString());
        // cols.add(LogValues.IV_CIDM_LAMDA_AV.toString());
        // cols.add(LogValues.IV_CIDM_C_AV.toString());
        cols.add(LogValues.IV_CIDM_MU_AV.toString());
        cols.add(LogValues.IV_CIDM_SIGMA_AV.toString());
        // cols.add(LogValues.IV_CIDM_GAMMA_AV.toString());
        cols.add(LogValues.IV_CIDM_RS_EQUAL.toString());
        cols.add(LogValues.IV_CIDM_R_SIGMA_AV.toString());
        cols.add(LogValues.IV_CIDM_R_PI_AV.toString());
        cols.add(LogValues.IV_CIDM_NET_SIZE.toString());
        // cols.add(LogValues.IV_CIDM_IOTA.toString());
        // cols.add(LogValues.IV_CIDM_PHI_AV.toString());
        // cols.add(LogValues.IV_CIDM_TAU_AV.toString());

        // PROPERTIES
        // simulation
        cols.add(LogValues.DV_SIM_STAGE.toString());
        // network
        // cols.add(LogValues.DV_NET_STABLE.toString());
        cols.add(LogValues.DV_NET_DENSITY.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE.toString());
        // cols.add(LogValues.DV_NET_AV_CLUSTERING.toString());
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
        // currData.add(String.valueOf(this.dgData.getUpc()));
        // currData.add(String.valueOf(this.dgData.getSimPerUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));
        // Cidm
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrAlpha()));
        // currData.add(String.valueOf(this.dgDat.getUtilityModelParams().getCurrKappa()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrBeta()));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrLamda()));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrMu()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrSigma()));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRPi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrPhi()));
        // currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrTau()));

        // PROPERTIES
        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getSimStage()));
        // network
        // currData.add(String.valueOf(this.dgData.getNetStatsCurrent().isStable()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getDensity()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvDegree()));
        // currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsCurrent().getRecoveredPercent()));

        writeLine(currData);
    }

}
