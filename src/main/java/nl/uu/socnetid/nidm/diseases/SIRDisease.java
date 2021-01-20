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

import nl.uu.socnetid.nidm.diseases.types.DiseaseState;

/**
 * @author Hendrik Nunner
 */
public class SIRDisease implements Disease {

    // the characteristics of the disease
    private final DiseaseSpecs diseaseSpecs;

    // time the already disease lasts
    private int currDuration;

    // state of the disease
    private DiseaseState diseaseState;

    /**
     * Constructor initializations.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease
     */
    protected SIRDisease(DiseaseSpecs diseaseSpecs) {
        this.diseaseSpecs = diseaseSpecs;
        this.currDuration = 0;
        this.diseaseState = DiseaseState.INFECTIOUS;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.diseases.Disease#evolve()
     */
    @Override
    public void evolve() {
        this.currDuration++;

        if (currDuration >= this.diseaseSpecs.getTau()) {
            this.diseaseState = DiseaseState.DEFEATED;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.diseases.Disease#getDiseaseSpecs()
     */
    @Override
    public DiseaseSpecs getDiseaseSpecs() {
        return this.diseaseSpecs;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.diseases.Disease#isInfectious()
     */
    @Override
    public boolean isInfectious() {
        return (this.diseaseState == DiseaseState.INFECTIOUS);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.diseases.Disease#isDefeated()
     */
    @Override
    public boolean isCured() {
        return this.diseaseState == DiseaseState.DEFEATED;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.diseases.Disease#getTimeRemaining()
     */
    @Override
    public int getTimeUntilCured() {
        return this.getDiseaseSpecs().getTau() - this.currDuration;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("specs:[").append(this.diseaseSpecs.toString()).append("]");
        sb.append(" | duration:").append(this.currDuration);
        sb.append(" | state:").append(this.diseaseState.toString());

        return sb.toString();
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

        if (!(obj instanceof SIRDisease)) {
            return false;
        }

        SIRDisease d = (SIRDisease) obj;
        return this.diseaseSpecs.equals(d.getDiseaseSpecs()) &&
                this.getTimeUntilCured() == d.getTimeUntilCured() &&
                this.isInfectious() == d.isInfectious() &&
                this.isCured() == d.isCured();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.currDuration,
                this.diseaseSpecs,
                this.diseaseState
         });
    }
}
