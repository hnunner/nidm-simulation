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
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;
import nl.uu.socnetid.nidm.networks.LockdownConditions;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensNetworkSummaryProfessionsWriter extends NetworkSummaryWriter<NunnerBuskensProfessionsParameters> {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensNetworkSummaryProfessionsWriter.class);

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
    public NunnerBuskensNetworkSummaryProfessionsWriter(String fileName,
            DataGeneratorData<NunnerBuskensProfessionsParameters> dgData) throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.SimulationSummaryWriter#addModelColumns(List<String> cols)
     */
    @Override
    protected List<String> addModelColumns(List<String> cols) {
        cols.add(LogValues.IV_NB_PROF_B1.toString());
        cols.add(LogValues.IV_NB_PROF_B2.toString());
        cols.add(LogValues.IV_NB_PROF_ALPHA.toString());
        cols.add(LogValues.IV_NB_PROF_C1.toString());
        cols.add(LogValues.IV_NB_PROF_C2.toString());
        cols.add(LogValues.IV_NB_PROF_OMEGA.toString());
        cols.add(LogValues.IV_NB_PROF_N.toString());
        cols.add(LogValues.IV_NB_PROF_PHI.toString());
        cols.add(LogValues.IV_NB_PROF_PSI.toString());

        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();
            cols.add(LogValues.DV_NB_PROF_DEGREE + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_DIFF + profession.replaceAll("\\s+", "_").toLowerCase());
            cols.add(LogValues.DV_NB_PROF_DEGREE_SD_DIFF + profession.replaceAll("\\s+", "_").toLowerCase());
        }

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

        Iterator<String> professionIt = Professions.getInstance().getProfessionsIterator();
        while (professionIt.hasNext()) {
            String profession = professionIt.next();

            // av degree by profession
            double avDegreeByProfession = this.dgData.getNetStatsCurrent().getAvDegreeByProfession(profession);
            currData.add(String.valueOf(avDegreeByProfession));

            // av degree standard deviation by profession
            double degreeSdByProfession = this.dgData.getNetStatsCurrent().getDegreeSdByProfession(profession);
            currData.add(String.valueOf(degreeSdByProfession));

            // av degree and av degree standard deviation difference by profession
            double avDegreeDiffByProfession;
            double degreeSdDiffByProfession;
            LockdownConditions lc = this.dgData.getUtilityModelParams().getCurrLockdownCondition();
            switch (lc) {
                case DURING:
                    avDegreeDiffByProfession = avDegreeByProfession -
                            Professions.getInstance().getDegreeDuringLockdown(profession);
                    degreeSdDiffByProfession = degreeSdByProfession -
                            Professions.getInstance().getDegreeErrorDuringLockdown(profession);
                    break;

                case POST:
                default:
                    logger.warn("Lockdown condition not implemented: " + lc + ". Using pre lockdown instead.");
                    //$FALL-THROUGH$
                case PRE:
                    avDegreeDiffByProfession = avDegreeByProfession -
                            Professions.getInstance().getDegreePreLockdown(profession);
                    degreeSdDiffByProfession = degreeSdByProfession -
                            Professions.getInstance().getDegreeErrorPreLockdown(profession);
                    break;
            }
            currData.add(String.valueOf(avDegreeDiffByProfession));
            currData.add(String.valueOf(degreeSdDiffByProfession));
        }

        return currData;
    }

}
