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
package nl.uu.socnetid.nidm.data.out;

import nl.uu.socnetid.nidm.data.genetic.NunnerBuskensGene;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensGeneticParameters extends UtilityModelParameters {

    // social benefits
    private double b1;
    private double b2;
    private double alpha;

    // social costs
    private double c1;
    private double c2;

    // simulation
    private int roundsMax;

    // genetic
    private double mutationSd;
    private int firstGeneration;
    private int parents;
    private int children;
    private int generations;

    // targets
    private double targetAvDegree;
    private double targetClustering;
    private double targetAssortativity;

    // initial settings
    private double initialAlphaMin;
    private double initialAlphaMax;
    private double initialOmegaMin;
    private double initialOmegaMax;

    // current offspring
    private NunnerBuskensGene offspring;


    /**
     * @return the b1
     */
    public double getB1() {
        return b1;
    }

    /**
     * @param b1 the b1 to set
     */
    public void setB1(double b1) {
        this.b1 = b1;
    }

    /**
     * @return the b2
     */
    public double getB2() {
        return b2;
    }

    /**
     * @param b2 the b2 to set
     */
    public void setB2(double b2) {
        this.b2 = b2;
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the c1
     */
    public double getC1() {
        return c1;
    }

    /**
     * @param c1 the c1 to set
     */
    public void setC1(double c1) {
        this.c1 = c1;
    }

    /**
     * @return the c2
     */
    public double getC2() {
        return c2;
    }

    /**
     * @param c2 the c2 to set
     */
    public void setC2(double c2) {
        this.c2 = c2;
    }

    /**
     * @return the offspring
     */
    public NunnerBuskensGene getOffspring() {
        return offspring;
    }

    /**
     * @param offspring the offspring to set
     */
    public void setOffspring(NunnerBuskensGene offspring) {
        this.offspring = offspring;
    }

    /**
     * @return the roundsMax
     */
    public int getRoundsMax() {
        return roundsMax;
    }

    /**
     * @param roundsMax the roundsMax to set
     */
    public void setRoundsMax(int roundsMax) {
        this.roundsMax = roundsMax;
    }

    /**
     * @return the mutationSd
     */
    public double getMutationSd() {
        return mutationSd;
    }

    /**
     * @param mutationSd the mutationSd to set
     */
    public void setMutationSd(double mutationSd) {
        this.mutationSd = mutationSd;
    }

    /**
     * @return the firstGeneration
     */
    public int getFirstGeneration() {
        return firstGeneration;
    }

    /**
     * @param firstGeneration the firstGeneration to set
     */
    public void setFirstGeneration(int firstGeneration) {
        this.firstGeneration = firstGeneration;
    }

    /**
     * @return the parents
     */
    public int getParents() {
        return parents;
    }

    /**
     * @param parents the parents to set
     */
    public void setParents(int parents) {
        this.parents = parents;
    }

    /**
     * @return the children
     */
    public int getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(int children) {
        this.children = children;
    }

    /**
     * @return the generations
     */
    public int getGenerations() {
        return generations;
    }

    /**
     * @param generations the generations to set
     */
    public void setGenerations(int generations) {
        this.generations = generations;
    }

    /**
     * @param targetAvDegree the targetAvDegree to set
     */
    public void setTargetAvDegree(double targetAvDegree) {
        this.targetAvDegree = targetAvDegree;
    }

    /**
     * @param targetClustering the targetClustering to set
     */
    public void setTargetClustering(double targetClustering) {
        this.targetClustering = targetClustering;
    }

    /**
     * @param targetAssortativity the targetAssortativity to set
     */
    public void setTargetAssortativity(double targetAssortativity) {
        this.targetAssortativity = targetAssortativity;
    }

    /**
     * @return the targetAvDegree
     */
    public double getTargetAvDegree() {
        return targetAvDegree;
    }

    /**
     * @return the targetClustering
     */
    public double getTargetClustering() {
        return targetClustering;
    }

    /**
     * @return the targetAssortativity
     */
    public double getTargetAssortativity() {
        return targetAssortativity;
    }

    /**
     * @return the initialAlphaMin
     */
    public double getInitialAlphaMin() {
        return initialAlphaMin;
    }

    /**
     * @param initialAlphaMin the initialAlphaMin to set
     */
    public void setInitialAlphaMin(double initialAlphaMin) {
        this.initialAlphaMin = initialAlphaMin;
    }

    /**
     * @return the initialAlphaMax
     */
    public double getInitialAlphaMax() {
        return initialAlphaMax;
    }

    /**
     * @param initialAlphaMax the initialAlphaMax to set
     */
    public void setInitialAlphaMax(double initialAlphaMax) {
        this.initialAlphaMax = initialAlphaMax;
    }

    /**
     * @return the initialOmegaMin
     */
    public double getInitialOmegaMin() {
        return initialOmegaMin;
    }

    /**
     * @param initialOmegaMin the initialOmegaMin to set
     */
    public void setInitialOmegaMin(double initialOmegaMin) {
        this.initialOmegaMin = initialOmegaMin;
    }

    /**
     * @return the initialOmegaMax
     */
    public double getInitialOmegaMax() {
        return initialOmegaMax;
    }

    /**
     * @param initialOmegaMax the initialOmegaMax to set
     */
    public void setInitialOmegaMax(double initialOmegaMax) {
        this.initialOmegaMax = initialOmegaMax;
    }

}
