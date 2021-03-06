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
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensAgentDetailsWriter extends AgentDetailsWriter<NunnerBuskensParameters> {

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
    public NunnerBuskensAgentDetailsWriter(String fileName, DataGeneratorData<NunnerBuskensParameters> dgData) throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.AgentDetailsWriter#addModelColumns(List<String> cols)
     */
    @Override
    protected List<String> addModelColumns(List<String> cols) {
        cols.add(LogValues.IV_NB_EP_STRUCTURE.toString());
//        cols.add(LogValues.IV_NB_B1.toString());
//        cols.add(LogValues.IV_NB_C1.toString());
//        cols.add(LogValues.IV_NB_C2.toString());
//        cols.add(LogValues.IV_NB_B2.toString());
        cols.add(LogValues.IV_NB_ALPHA.toString());
        cols.add(LogValues.IV_NB_SIGMA.toString());
        cols.add(LogValues.IV_NB_GAMMA.toString());
//        cols.add(LogValues.IV_NB_TAU.toString());
        cols.add(LogValues.IV_NB_R_MIN.toString());
        cols.add(LogValues.IV_NB_R_MAX.toString());
//        cols.add(LogValues.IV_NB_RS_EQUAL.toString());
//        cols.add(LogValues.IV_NB_R_SIGMA_RANDOM.toString());
//        cols.add(LogValues.IV_NB_R_SIGMA_RANDOM_HOMOGENEOUS.toString());
        cols.add(LogValues.IV_NB_R_SIGMA.toString());
//        cols.add(LogValues.IV_NB_R_PI_RANDOM.toString());
//        cols.add(LogValues.IV_NB_R_PI_RANDOM_HOMOGENEOUS.toString());
//        cols.add(LogValues.IV_NB_R_PI.toString());
//        cols.add(LogValues.IV_NB_NET_SIZE.toString());
//        cols.add(LogValues.IV_NB_IOTA.toString());
//        cols.add(LogValues.IV_NB_PHI.toString());
        cols.add(LogValues.IV_NB_OMEGA.toString());
        return cols;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.AgentDetailsWriter#addCurrModelData(List<String> currData, Agent agent)
     */
    @Override
    protected List<String> addCurrModelData(List<String> currData, Agent agent) {
        NunnerBuskens uf = (NunnerBuskens) agent.getUtilityFunction();

        currData.add(this.dgData.getUtilityModelParams().getCurrEpStructure().toString());
//        currData.add(String.valueOf(uf.getB1()));
//        currData.add(String.valueOf(uf.getC1()));
//        currData.add(String.valueOf(uf.getC2()));
//        currData.add(String.valueOf(uf.getB2()));
        currData.add(String.valueOf(uf.getAlpha()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getSigma()));
        currData.add(String.valueOf(agent.getDiseaseSpecs().getGamma()));
//        currData.add(String.valueOf(agent.getDiseaseSpecs().getTau()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMin()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRMax()));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRSigmaRandom() ? 1 : 0));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrRSigmaRandomHomogeneous() ? 1 : 0));
        currData.add(String.valueOf(agent.getRSigma()));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRPiRandom() ? 1 : 0));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrRPiRandomHomogeneous() ? 1 : 0));
//        currData.add(String.valueOf(agent.getRPi()));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
//        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
//        currData.add(String.valueOf(agent.getPhi()));
        currData.add(String.valueOf(agent.getOmega()));

        return currData;
    }

}
