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
package nl.uu.socnetid.nidm.io.generator.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.genetic.NunnerBuskensGene;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensGeneticParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensNetworkSummaryGeneticWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.AgentPropertiesWriter;
import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.io.network.NetworkFileWriter;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class NunnerBuskensNetworkGeneratorGenetic extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensNetworkGeneratorGenetic.class);

    // percent of mean as standard deviation for mutation
    private static final double MUTATION_SD =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getMutationSd();

    // number of parents per generation
    private static final int NUMBER_OF_PARENTS =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getParents();
    // size of first generation = iterations of number of parents
    private static final int SIZE_OF_FIRST_GENERATION =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getFirstGeneration();
    // number of children per pair of parents
    private static final int NUMBER_OF_CHILDREN =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getChildren();
    // number of generations to simulate
    private static final int NUMBER_OF_GENERATIONS =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getGenerations();

    // target values
    private static final double TARGET_AV_DEGREE =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getTargetAvDegree();        // controlled by c2
    private static final double TARGET_CLUSTERING =
            PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getTargetClustering();      // controlled by alpha

    // network
    private Network network;

    // list of all genes
    private List<NunnerBuskensGene> allGenes = new LinkedList<NunnerBuskensGene>();
    // current offspring
    private NunnerBuskensGene currentOffspring;

    // stats & writer
    private DataGeneratorData<NunnerBuskensGeneticParameters> dgData;
    private NunnerBuskensNetworkSummaryGeneticWriter nsWriter;
    private int upc = 1;

    // fitness
    private double fitnessPrevRound;

    // simulation
    private boolean simFinished = false;


    /**
     * Constructor.
     *
     * @param rootExportPath
     *          the root export path
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public NunnerBuskensNetworkGeneratorGenetic(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens.genetic";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<NunnerBuskensGeneticParameters>(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters());
        this.dgData.getSimStats().setUpc(0);

        this.dgData.getUtilityModelParams().setN(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getN());
        this.dgData.getUtilityModelParams().setB1(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getB1());
        this.dgData.getUtilityModelParams().setB2(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getB2());
        this.dgData.getUtilityModelParams().setC1(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getC1());
        this.dgData.getUtilityModelParams().setPhi(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getPhi());
        this.dgData.getUtilityModelParams().setPsi(
                PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getPsi());
        this.dgData.getUtilityModelParams().setAssortativityCondition(AssortativityConditions.AGE);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.nsWriter = new NunnerBuskensNetworkSummaryGeneticWriter(getExportPath() + "network-summary-genetic.csv",
                    this.dgData);
        }
    }

    private void initNetwork(double targetAvC2, double targetAlpha) {

        this.network = new Network("Network of the infectious kind", AssortativityConditions.AGE);
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);    // not used

        double b1 = this.dgData.getUtilityModelParams().getB1();
        double b2 = this.dgData.getUtilityModelParams().getB2();
        double c1 = this.dgData.getUtilityModelParams().getC1();

        double targetAvDegree = NunnerBuskens.getAvDegreeFromC2(b1, c1, targetAvC2);
        double correction = 1.00;

        while ((Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0) != (Math.round(targetAvDegree * 100.0) / 100.0)) {

            if ((Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0) < (Math.round(targetAvDegree * 100.0) / 100.0)) {
                correction += 0.01;
            } else {
                correction -= 0.01;
            }

            this.network.clear();
            logger.info("(Re-)sampling random degrees to achieve degree distribution with theoretic average degree of "
                    + (Math.round(targetAvDegree * 100.0) / 100.0));
            double allC2s = 0;
            for (int i = 0; i < this.dgData.getUtilityModelParams().getN(); i++) {
                int age = AgeStructure.getInstance().getRandomAge();

                // corrected target average degree, due to omitting isolates
                double tadCorrected = targetAvDegree * correction;
                ExponentialDistribution ed = new ExponentialDistribution(tadCorrected +
                        (tadCorrected * AgeStructure.getInstance().getErrorAvDegree(age)));
                // TODO consider changing exponential distribution to something more grounded in theory (e.g. Danon et al. (2013))
                // TODO remove sample == 0 check once simple power law distribution has been replaced
                // TODO remove corrected target average degree once simple power law distribution has been replaced
                long targetDegree = 0;
                // omitting isolates
                while (targetDegree == 0) {
                    // target degree dependent on agent's age
                    targetDegree = Math.round(ed.sample());
                }

                double c2 = NunnerBuskens.getC2FromAvDegree(b1, c1, targetDegree);
                allC2s += c2;

                // utility
                UtilityFunction uf = new NunnerBuskens(b1, b2, targetAlpha, c1, c2);

                // add agents
                this.network.addAgent(
                        uf,
                        ds,
                        1.0,
                        1.0,
                        this.dgData.getUtilityModelParams().getPhi(),
                        this.dgData.getUtilityModelParams().getOmega(),
                        this.dgData.getUtilityModelParams().getPsi(),
                        this.dgData.getUtilityModelParams().getXi(),
                        age,
                        this.dgData.getUtilityModelParams().isConsiderAge());
            }

            logger.info("Theoretic average degree: " + (Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0));
            this.dgData.getUtilityModelParams().setC2(allC2s/this.dgData.getUtilityModelParams().getN());
        }
        this.dgData.getUtilityModelParams().setAlpha(targetAlpha);
        logger.info("Network initialization successful.");
        this.dgData.setAgents(new ArrayList<Agent>(this.network.getAgents()));

    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        // simulate generations
        for (int generation = 0; generation <= NUMBER_OF_GENERATIONS; generation++) {

            // select parents
            List<NunnerBuskensGene> parents = selectParents();

            // create offspring
            List<NunnerBuskensGene> offspring = createOffspring(parents, generation);

            // simulate offspring
            Iterator<NunnerBuskensGene> it = offspring.iterator();
            while (it.hasNext()) {
                this.currentOffspring = it.next();
                simulateSingleGene(this.currentOffspring);
            }
        }
        finalizeDataExportFiles();
    }


    private void simulateSingleGene(NunnerBuskensGene nbg) {

        initNetwork(nbg.getAvC2(), nbg.getAlpha());

        // simulate
        Simulation simulation = new Simulation(this.network);
        simulation.addSimulationListener(this);
        simulation.simulateUntilStable(PropertiesHandler.getInstance().getNunnerBuskensGeneticParameters().getRoundsMax());
    }


    /**
     * Creates offspring from a list of parents.
     *
     * @param parents
     *          the list of parents to create offspring for
     * @param generation
     *          the generation the offspring belongs to
     * @return the offspring
     */
    private List<NunnerBuskensGene> createOffspring(List<NunnerBuskensGene> parents, int generation) {

        List<NunnerBuskensGene> offspring = new LinkedList<NunnerBuskensGene>();

        int genMember = 1;
        if (parents.isEmpty()) {
            // first generation (based on target values)
            for (int progenitor = 1; progenitor <= SIZE_OF_FIRST_GENERATION; progenitor++) {

                double avC2 = NunnerBuskens.getC2FromAvDegree(
                        this.dgData.getUtilityModelParams().getB1(),
                        this.dgData.getUtilityModelParams().getC1(),
                        new NormalDistribution(TARGET_AV_DEGREE, TARGET_AV_DEGREE * MUTATION_SD).sample());
                double alpha = ThreadLocalRandom.current().nextDouble(
                        this.dgData.getUtilityModelParams().getInitialAlphaMin(),
                        this.dgData.getUtilityModelParams().getInitialAlphaMax());

                offspring.add(new NunnerBuskensGene(
                        generation,                                                                         // generation
                        String.valueOf(generation) + "-" + String.valueOf(genMember++)
                                + "(god;god)#" + String.valueOf(progenitor),                                // id
                        generation + "-" + progenitor,                                                      // simpleId
                        "god", "god",                                                                       // parents
                        avC2, alpha,                                                                        // settings
                        TARGET_AV_DEGREE, TARGET_CLUSTERING));                                              // targets
            }

        } else {
            // select mother
            for (int motherIndex = 0; motherIndex < parents.size(); motherIndex++) {
                NunnerBuskensGene mother = parents.get(motherIndex);

                // select father
                for (int fatherIndex = motherIndex+1; fatherIndex < parents.size(); fatherIndex++) {
                    NunnerBuskensGene father = parents.get(fatherIndex);

                    // create children
                    for (int child = 1; child <= NUMBER_OF_CHILDREN; child++) {

                        // GENE SELECTION
                        double avC2 = ThreadLocalRandom.current().nextBoolean() ? mother.getAvC2() : father.getAvC2();
                        double alpha = ThreadLocalRandom.current().nextBoolean() ? mother.getAlpha() : father.getAlpha();

                        // GENE MUTATION
                        avC2 += new NormalDistribution(0, avC2 * MUTATION_SD).sample();
                        alpha += new NormalDistribution(0, alpha * MUTATION_SD).sample();

                        // create child
                        offspring.add(new NunnerBuskensGene(
                                generation,                                                                         // generation
                                String.valueOf(generation) + "-" + String.valueOf(genMember++)
                                        + "(" + mother.getSimpleId() + ";" + father.getSimpleId() + ")#" + child,   // id
                                generation + "-" + child,                                                           // simpleId
                                mother.getId(), father.getId(),                                                     // parents
                                avC2, alpha,                                                                        // settings
                                TARGET_AV_DEGREE, TARGET_CLUSTERING));                                              // targets
                    }
                }
            }
        }

        return offspring;
    }


    /**
     * @return
     */
    private List<NunnerBuskensGene> selectParents() {
        List<NunnerBuskensGene> parents = new LinkedList<NunnerBuskensGene>();
        if (!this.allGenes.isEmpty()) {
            Collections.sort(this.allGenes);
            for (int i = 0; i < NUMBER_OF_PARENTS; i++) {
                parents.add(this.allGenes.get(i));
            }
        }
        return parents;
    }


    /**
     * Amends the summary file by writing a row with the current state of the network.
     */
    private void amendSummary() {
        this.dgData.getUtilityModelParams().setOffspring(this.currentOffspring);
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
        this.nsWriter.writeCurrentData();
    }


    /**
     * Exports the network as adjacency matrix, edge list, and Gephi files.
     */
    private void exportNetworks(String nameAppendix) {
        String fileName = this.dgData.getSimStats().getUid();
        if (nameAppendix != null && !nameAppendix.isEmpty()) {
            fileName += "-" + nameAppendix;
        }

        NetworkFileWriter elWriter = new NetworkFileWriter(getExportPath(),
                fileName + ".el",
                new EdgeListWriter(),
                this.network);
        elWriter.write();

        NetworkFileWriter ageWriter = new NetworkFileWriter(getExportPath(),
                fileName + ".age",
                new AgentPropertiesWriter(),
                this.network);
        ageWriter.write();

        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.writeStaticNetwork(this.network, getExportPath() + fileName + ".gexf");
    }

    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            this.nsWriter.flush();
            this.nsWriter.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }


    @Override
    public void notifyRoundFinished(Simulation simulation) {

        this.dgData.getSimStats().incCurrRound();

        // simulation finished when fitness is not improving anymore
        this.simFinished = this.fitnessPrevRound < this.currentOffspring.getFitnessOverall(
                this.network.getAvDegree(), this.network.getAvClustering());

        if (simFinished) {
            // stop simulation
            simulation.stop();
        } else {
            // fitness
            this.currentOffspring.setFitness(
                    this.network.getAvDegree(), this.network.getAvClustering());
            this.fitnessPrevRound = this.currentOffspring.getFitnessOverall();
            // exports only when fitness is still improving
            amendSummary();         // amend summary CSV
            exportNetworks(null);     // overwrite networks with better fitness
        }
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {
        this.fitnessPrevRound = Double.MAX_VALUE;
        this.simFinished = false;

        this.dgData.getSimStats().setUid(this.currentOffspring.getId());
        this.dgData.getSimStats().setUpc(this.upc++);
        this.dgData.getSimStats().setSimPerUpc(1);
        this.dgData.getSimStats().resetCurrRound();
    }

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {}

    @Override
    public void notifySimulationFinished(Simulation simulation) {
        this.allGenes.add(this.currentOffspring);
    }

}
