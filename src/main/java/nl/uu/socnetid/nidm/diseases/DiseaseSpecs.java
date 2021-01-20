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
package nl.uu.socnetid.nidm.diseases;

import java.util.Arrays;

import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import scala.collection.mutable.StringBuilder;

/**
 * @author Hendrik Nunner
 */
public class DiseaseSpecs {

    private static final String DISEASE_TYPE_STRING = "disease type:";
    private static final String MU_STRING = "mu:";
    private static final String GAMMA_STRING = "gamma:";
    private static final String SIGMA_STRING = "sigma:";
    private static final String TAU_STRING = "tau:";
    private static final String STRING_DELIMITER = "; ";

    private final DiseaseType diseaseType;
    private final int tau;
    private final double sigma;
    private final double gamma;
    private final double mu;

    /**
     * Constructor initializations.
     *
     * @param diseaseType
     *          the type of disease
     * @param tau
     *          the duration a disease requires to recover from in rounds
     * @param sigma
     *          the severity of the disease represented by the amount of punishment for having a disease
     * @param gamma
     *          the transmission rate - the probability a disease is spread between an infected and a non-infected
     *          agent per round
     * @param mu
     *          the factor that increases maintenance costs for infected connections
     */
    public DiseaseSpecs(DiseaseType diseaseType, int tau, double sigma, double gamma, double mu) {
        this.diseaseType = diseaseType;
        this.tau = tau;
        this.sigma = sigma;
        this.gamma = gamma;
        this.mu = mu;
    }

    /**
     * @return the type of disease
     */
    public DiseaseType getDiseaseType() {
        return this.diseaseType;
    }

    /**
     * @return the severity of the disease represented by the amount of punishment for having a disease
     */
    public double getSigma() {
        return this.sigma;
    }

    /**
     * @return the duration a disease requires to recover from in rounds
     */
    public int getTau() {
        return this.tau;
    }

    /**
     * @return the transmission rate - the probability a disease is spread between an infected and a non-infected
     *          agent per round
     */
    public double getGamma() {
        return this.gamma;
    }

    /**
     * @return the factor that increases maintenance costs for infected connections
     */
    public double getMu() {
        return this.mu;
    }

    /**
     * @return the name of the disease as presented in the stats window
     */
    public String getStatsName() {
        return this.diseaseType.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DiseaseSpecs)) {
            return false;
        }

        DiseaseSpecs specs2 = (DiseaseSpecs) obj;
        return diseaseType.equals(specs2.getDiseaseType()) &&
                this.tau == specs2.getTau() &&
                this.sigma == specs2.getSigma() &&
                this.gamma == specs2.getGamma() &&
                this.mu == specs2.getMu();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.diseaseType,
                this.tau,
                this.sigma,
                this.gamma,
                this.mu
         });
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(DISEASE_TYPE_STRING).append(this.diseaseType.toString());
        sb.append(STRING_DELIMITER).append(TAU_STRING).append(this.tau);
        sb.append(STRING_DELIMITER).append(SIGMA_STRING).append(this.sigma);
        sb.append(STRING_DELIMITER).append(GAMMA_STRING).append(this.gamma);
        sb.append(STRING_DELIMITER).append(MU_STRING).append(this.mu);

        return sb.toString();
    }

    /**
     * Creates the connection stats from a given string.
     * @param text
     *          the string to create the connection stats for
     * @return the connection stats
     */
    public static DiseaseSpecs fromString(String text) {

        DiseaseType diseaseType = null;
        int tau = 0;
        double sigma = 0.0;
        double gamma = 0.0;
        double mu = 0.0;

        String[] split = text.split(STRING_DELIMITER);
        for (String value : split) {

            if (value.contains(DISEASE_TYPE_STRING)) {
                value = value.replace(DISEASE_TYPE_STRING, "");
                diseaseType = DiseaseType.fromString(value);

            } else if (value.contains(TAU_STRING)) {
                value = value.replace(TAU_STRING, "");
                tau = Integer.valueOf(value);

            } else if (value.contains(SIGMA_STRING)) {
                value = value.replace(SIGMA_STRING, "");
                sigma = Double.valueOf(value);

            } else if (value.contains(GAMMA_STRING)) {
                value = value.replace(GAMMA_STRING, "");
                gamma = Double.valueOf(value);

            } else if (value.contains(MU_STRING)) {
                value = value.replace(MU_STRING, "");
                mu = Double.valueOf(value);

            } else {
                throw new IllegalArgumentException("No constant with text " + value + " found.");
            }
        }

        return new DiseaseSpecs(diseaseType, tau, sigma, gamma, mu);
    }

}
