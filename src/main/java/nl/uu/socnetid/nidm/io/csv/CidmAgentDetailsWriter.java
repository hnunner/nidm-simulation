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

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.CidmParameters;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.utility.Cidm;

/**
 * @author Hendrik Nunner
 */
public class CidmAgentDetailsWriter extends AgentDetailsWriter<CidmParameters> {

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
    public CidmAgentDetailsWriter(String fileName, DataGeneratorData<CidmParameters> dgData) throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.AgentDetailsWriter#addModelColumns(List<String> cols)
     */
    @Override
    protected List<String> addModelColumns(List<String> cols) {
        cols.add(LogValues.IV_CIDM_ALPHA.toString());
        cols.add(LogValues.IV_CIDM_KAPPA.toString());
        cols.add(LogValues.IV_CIDM_BETA.toString());
        cols.add(LogValues.IV_CIDM_LAMDA.toString());
        cols.add(LogValues.IV_CIDM_C.toString());
        cols.add(LogValues.IV_CIDM_MU.toString());
        cols.add(LogValues.IV_CIDM_SIGMA.toString());
        cols.add(LogValues.IV_CIDM_GAMMA.toString());
        cols.add(LogValues.IV_CIDM_RS_EQUAL.toString());
        cols.add(LogValues.IV_CIDM_R_SIGMA.toString());
        cols.add(LogValues.IV_CIDM_R_PI.toString());
        cols.add(LogValues.IV_CIDM_NET_SIZE.toString());
        cols.add(LogValues.IV_CIDM_IOTA.toString());
        cols.add(LogValues.IV_CIDM_PHI_AV.toString());
        cols.add(LogValues.IV_CIDM_OMEGA_AV.toString());
        cols.add(LogValues.IV_CIDM_TAU_AV.toString());
        return cols;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.AgentDetailsWriter#addCurrModelData(List<String> currData, Agent agent)
     */
    @Override
    protected List<String> addCurrModelData(List<String> currData, Agent agent) {
        Cidm uf = (Cidm) agent.getUtilityFunction();
        currData.add(String.valueOf(uf.getAlpha()));
        currData.add(String.valueOf(uf.getKappa()));
        currData.add(String.valueOf(uf.getBeta()));
        currData.add(String.valueOf(uf.getLamda()));
        currData.add(String.valueOf(uf.getC()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getMu()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getSigma()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
        currData.add(String.valueOf(agent.getRSigma()));
        currData.add(String.valueOf(agent.getRPi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
        currData.add(String.valueOf(agent.getPhi()));
        currData.add(String.valueOf(agent.getOmega()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getTau()));
        return currData;
    }

}
