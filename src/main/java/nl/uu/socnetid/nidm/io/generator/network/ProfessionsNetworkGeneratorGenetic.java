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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.data.genetic.ProfessionsGene;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkGeneticParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.ProfessionGeneticNetworkSummaryWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ProfessionsNetworkGeneratorGenetic extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsNetworkGeneratorGenetic.class);

    // size of first generation = iterations of number of parents
    private static final int SIZE_OF_FIRST_GENERATION = 4;          // no variations in first generation
    // number of parents per generation
    private static final int NUMBER_OF_PARENTS = 4;
    // number of children per pair of parents
    private static final int NUMBER_OF_CHILDREN = 2;
    // number of generations to simulate
    private static final int NUMBER_OF_GENERATIONS = 20;
    // maximum number of consecutive rounds allowed without improvement
    private static final int MAX_ROUNDS_NO_IMPROVEMENT = 5;

    // parameter variations
//    private static final double[] TARGET_CLUSTERINGS = {0.3, 0.4, 0.5};
//    private static final double[] TARGET_HOMOPHILIES = {0.0, 0.4, 0.8};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE, LockdownConditions.DURING};

//    private static final double[] TARGET_CLUSTERINGS = {0.3};
//    private static final double[] TARGET_HOMOPHILIES = {0.0};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.4};
//    private static final double[] TARGET_HOMOPHILIES = {0.0};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.5};
//    private static final double[] TARGET_HOMOPHILIES = {0.0};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.3};
//    private static final double[] TARGET_HOMOPHILIES = {0.4};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.4};
//    private static final double[] TARGET_HOMOPHILIES = {0.4};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.5};
//    private static final double[] TARGET_HOMOPHILIES = {0.4};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.3};
//    private static final double[] TARGET_HOMOPHILIES = {0.8};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
//    private static final double[] TARGET_CLUSTERINGS = {0.4};
//    private static final double[] TARGET_HOMOPHILIES = {0.8};
//    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};
//
    private static final double[] TARGET_CLUSTERINGS = {0.4};
    private static final double[] TARGET_HOMOPHILIES = {0.8};
    private static final LockdownConditions[] TARGET_LOCKDOWN_CONDITIONS = {LockdownConditions.PRE};


    // target degrees
    private static final Map<String, Double> BELOT_DEGREES_PRE = Professions.getInstance().getDegreesPreLockdown();
    private static final Map<String, Double> BELOT_DEGREES_DURING = Professions.getInstance().getDegreesDuringLockdown();
    private static final Map<String, Double> BELOT_DEGREE_SDS_PRE = Professions.getInstance().getDegreeErrorsPreLockdown();
    private static final Map<String, Double> BELOT_DEGREE_SDS_DURING = Professions.getInstance().getDegreeErrorsDuringLockdown();

    private Map<String, Double> belotDegrees;
    private Map<String, Double> belotDegreeSds;
//    private Map<String, Double> bestFitFiles;

    // target clustering
    private double currTargetClustering;

    // network
    private Network network;

    // list of all genes
    private List<ProfessionsGene> allGenes;
    // current offspring
    private ProfessionsGene currentOffspring;

    // stats & writer
    private DataGeneratorData<ProfessionNetworkGeneticParameters> dgData;
    private ProfessionGeneticNetworkSummaryWriter nsWriter;
    private int upc = 1;

    // fitness
    private double fitnessPrevRound;
    private int improvedRoundsAgo = 0;

    // simulation
    private boolean simFinished = false;
    private Simulation simulation;


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
    public ProfessionsNetworkGeneratorGenetic(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "professions.genetic";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<ProfessionNetworkGeneticParameters>(
                PropertiesHandler.getInstance().getProfessionNetworkGeneticParameters());
        this.dgData.getSimStats().setUpc(0);

        // TODO move to config file!!!
        // PARAMETER CONSTANTS
        // network & simulation
        this.dgData.getUtilityModelParams().setN(10000);
        this.dgData.getUtilityModelParams().setPhi(0.001);
        this.dgData.getUtilityModelParams().setPsi(0.3);
        this.dgData.getUtilityModelParams().setXi(0.5);
        // utility
        this.dgData.getUtilityModelParams().setB1(1.0);
        this.dgData.getUtilityModelParams().setB2(0.5);
        this.dgData.getUtilityModelParams().setC1(0.2);
        // assortativity
        this.dgData.getUtilityModelParams().setAssortativityConditions(Arrays.asList(AssortativityConditions.PROFESSION));
        this.dgData.getUtilityModelParams().setAssortativityInitCondition(AssortativityConditions.PROFESSION);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.nsWriter = new ProfessionGeneticNetworkSummaryWriter(getExportPath() + "profession-networks-genetic-summary.csv",
                    this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        // PARAMETER VARIATIONS
        // TODO use iteration over list entries!!!

        int s = 1;
        int l = 1;
        for (LockdownConditions targetLockdownCondition : TARGET_LOCKDOWN_CONDITIONS) {

            this.dgData.getUtilityModelParams().setCurrLockdownCondition(targetLockdownCondition);
            switch (targetLockdownCondition) {
                case DURING:
                    this.belotDegrees = BELOT_DEGREES_DURING;
                    this.belotDegreeSds = BELOT_DEGREE_SDS_DURING;
                    break;

                case POST:
                    logger.warn("Lockdown condition '" + targetLockdownCondition + "' not implemented. Using pre instead.");
                    //$FALL-THROUGH$
                case PRE:
                default:
                    this.belotDegrees = BELOT_DEGREES_PRE;
                    this.belotDegreeSds = BELOT_DEGREE_SDS_PRE;
                    break;
            }

            int h = 1;
            for (double targetHomophily : TARGET_HOMOPHILIES) {

                this.dgData.getUtilityModelParams().setOmega(targetHomophily);

                int c = 1;
                for (double targetClustering : TARGET_CLUSTERINGS) {

                    this.currTargetClustering = targetClustering;
                    this.dgData.getUtilityModelParams().setTargetClustering(this.currTargetClustering);
                    this.dgData.getUtilityModelParams().setAlpha(this.currTargetClustering);

                    // simulate pre lockdown generations
                    allGenes = new LinkedList<ProfessionsGene>();

                    //            this.dgData.getUtilityModelParams().setLockdownNetworkBaseFile("NA");
                    //        bestFitFiles = new HashMap<String, Double>();

                    logger.info("Starting to simulate generations for new parameter combination:" +
                            "\n\t\t\t\t\t\t\t\t\tlockdown condition:\t" + l + " of " + TARGET_LOCKDOWN_CONDITIONS.length +
                            "\n\t\t\t\t\t\t\t\t\thomophily:\t\t" + h + " of " + TARGET_HOMOPHILIES.length +
                            "\n\t\t\t\t\t\t\t\t\tclustering:\t\t" + c + " of " + TARGET_CLUSTERINGS.length +
                            "\n\t\t\t\t\t\t\t\t\ttotal combinations:\t" + s + " of " +
                                    (TARGET_CLUSTERINGS.length *
                                    TARGET_HOMOPHILIES.length * TARGET_LOCKDOWN_CONDITIONS.length));

                    for (int generation = 0; generation <= NUMBER_OF_GENERATIONS; generation++) {

                        // select parents
                        List<ProfessionsGene> parents = selectParents();

                        // create offspring
                        List<ProfessionsGene> offspring = createOffspring(parents, generation);

                        // simulate offspring
                        Iterator<ProfessionsGene> it = offspring.iterator();

                        while (it.hasNext()) {
                            this.currentOffspring = it.next();
                            simulateSingleGene(this.currentOffspring);

                        }
                        if (generation == 0) {
                            logger.info("Creation of god's children for new generation finished after "
                                    + this.dgData.getSimStats().getCurrRound() + " rounds.");
                        } else {
                            logger.info("Creation of generation " + generation + " of " + NUMBER_OF_GENERATIONS + " finished after "
                                    + this.dgData.getSimStats().getCurrRound() + " rounds.");
                        }
                    }
                    s++;
                    c++;
                    this.dgData.getSimStats().setUpc(this.upc++);
                }
                h++;
            }
            l++;
        }

//        // simulate lockdown generations
//        allGenes = new LinkedList<ProfessionsGene>();
//        this.dgData.getUtilityModelParams().setCurrLockdownCondition(LockdownConditions.DURING);
//        belotDegrees = BELOT_DEGREES_DURING;
//        for (String bestFitFile : bestFitFiles.keySet()) {
//            this.dgData.getUtilityModelParams().setLockdownNetworkBaseFile(bestFitFile);
//
//            for (int generation = 0; generation <= NUMBER_OF_GENERATIONS; generation++) {
//
//                // select parents
//                List<ProfessionsGene> parents = selectParents();
//
//                // create offspring
//                List<ProfessionsGene> offspring = createOffspring(parents, generation);
//
//                // simulate offspring
//                Iterator<ProfessionsGene> it = offspring.iterator();
//                while (it.hasNext()) {
//                    this.currentOffspring = it.next();
//                    simulateSingleGene(this.currentOffspring);
//                }
//            }
//        }

        finalizeDataExportFiles();
    }


    private void initNetworkByProfessions(ProfessionsGene pg) {

        ProfessionNetworkGeneticParameters umps = this.dgData.getUtilityModelParams();

//        switch (umps.getCurrLockdownCondition()) {
//            case DURING:
//                this.network = new DGSReader().readNetwork(umps.getLockdownNetworkBaseFile());
//                Iterator<Agent> agents = this.network.getAgentIterator();
//                while (agents.hasNext()) {
//                    Agent agent = agents.next();
//                    double degree = pg.getTargetAvDegree(agent.getProfession());
//                    double c2 = NunnerBuskens.getC2FromAvDegree(umps.getB1(), umps.getC1(), degree);
//
//                    NunnerBuskens nb = (NunnerBuskens) agent.getUtilityFunction();
//                    nb.setC2(c2);
//                    agent.updateUtilityFunction(nb);
//                }
//
//                break;
//
//            case POST:
//                logger.warn("Lockdown '" + umps.getCurrLockdownCondition() + " not implemented. Using pre lockdown instead.");
//                //$FALL-THROUGH$
//            case PRE:
//            default:
        this.network = new Network("Network of the infectious kind", umps.getAssortativityConditions());
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);    // not used

        Set<String> professions = new HashSet<String>();

        while (professions.size() != this.belotDegrees.keySet().size()) {
            for (int i = 0; i < umps.getN(); i++) {

                String profession = Professions.getInstance().getRandomProfession();
                professions.add(profession);
                double degree = pg.getTargetAvDegree(profession);
                double c2 = NunnerBuskens.getC2FromAvDegree(umps.getB1(), umps.getC1(), degree);

                // utility
                UtilityFunction uf = new NunnerBuskens(umps.getB1(), umps.getB2(), pg.getAlpha(), umps.getC1(), c2);

                // add agents
                this.network.addAgent(
                        uf,
                        ds,
                        1.0,
                        1.0,
                        umps.getPhi(),
                        umps.getOmega(),
                        umps.getPsi(),
                        umps.getXi(),
                        AgeStructure.getInstance().getRandomAge(),
                        umps.isConsiderAge(),
                        profession,
                        umps.isConsiderProfession(),
                        false);
            }
        }
//                break;
//
//        }

//        logger.info("Network initialization successful.");
    }


    private void simulateSingleGene(ProfessionsGene pg) {

        // init network
        AssortativityConditions aic = this.dgData.getUtilityModelParams().getAssortativityInitCondition();
        switch (aic) {
            case RISK_PERCEPTION:
            case AGE:
                logger.warn("Assortativity init condition not implemented: " + aic + ". Using age instead");
                //$FALL-THROUGH$
            case PROFESSION:
            default:
                initNetworkByProfessions(pg);
                break;
        }

        // simulate
        this.simulation = new Simulation(this.network);
        simulation.addSimulationListener(this);
        this.improvedRoundsAgo = 0;
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
    private List<ProfessionsGene> createOffspring(List<ProfessionsGene> parents, int generation) {

        List<ProfessionsGene> offspring = new LinkedList<ProfessionsGene>();

        int genMember = 1;
        if (parents.isEmpty()) {
            // first generation (based on target values)
            for (int progenitor = 1; progenitor <= SIZE_OF_FIRST_GENERATION; progenitor++) {

                offspring.add(new ProfessionsGene(
                        generation,                                                                         // generation
                        this.dgData.getSimStats().getUpc() + "_" +
                        this.dgData.getUtilityModelParams().getCurrLockdownCondition().toString() + "_" +
                        String.valueOf(generation) + "_" + String.valueOf(genMember++)
                                + "(god-god)#" + String.valueOf(progenitor),                                // id
                        generation + "-" + progenitor,                                                      // simpleId
                        "god", "god",                                                                       // parents

                        this.belotDegrees, this.belotDegrees,
                        this.currTargetClustering, this.currTargetClustering, this.currTargetClustering));
            }

        } else {
            // select mother
            for (int motherIndex = 0; motherIndex < parents.size(); motherIndex++) {
                ProfessionsGene mother = parents.get(motherIndex);

                // select father
                for (int fatherIndex = motherIndex+1; fatherIndex < parents.size(); fatherIndex++) {
                    ProfessionsGene father = parents.get(fatherIndex);

                    // create children
                    for (int child = 1; child <= NUMBER_OF_CHILDREN; child++) {

                        // GENE SELECTION
                        Map<String, Double> oldAvDegrees = mother.getAvDegrees();
                        Map<String, Double> oldTargetAvDegrees = mother.getTargetAvDegrees();
                        if (!ThreadLocalRandom.current().nextBoolean()) {
                            oldAvDegrees = father.getAvDegrees();
                            oldTargetAvDegrees = father.getTargetAvDegrees();
                        }

                        double alpha = mother.getAlpha();
                        double clustering = mother.getClustering();
                        if (!ThreadLocalRandom.current().nextBoolean()) {
                            alpha = father.getAlpha();
                            clustering = father.getClustering();
                        }

                        // GENE MUTATION
                        Map<String, Double> newTargetAvDegrees = new HashMap<String, Double>();
                        Iterator<String> professions = oldAvDegrees.keySet().iterator();
                        while (professions.hasNext()) {
                            String profession = professions.next();

                            double oldAvDegree = oldAvDegrees.get(profession);
                            Double belotAvDegree = this.belotDegrees.get(profession);
                            double errorDegree = Math.abs((belotAvDegree - oldAvDegree) / belotAvDegree);

                            double newTargetAvDegree = oldTargetAvDegrees.get(profession);

                            if (errorDegree > 0.02) {
                                if (oldAvDegree < belotAvDegree) {
//                                    newTargetAvDegree += newTargetAvDegree * errorDegree;
                                    newTargetAvDegree += newTargetAvDegree * ThreadLocalRandom.current().nextDouble(errorDegree);
//                                    newTargetAvDegree += newTargetAvDegree * ThreadLocalRandom.current().nextDouble(MUTATION_SD);

//                                    logger.debug("Increasing degree for " + profession + ":\t" + String.format("%.2f", oldAvDegree)
//                                            + " to " + String.format("%.2f", newTargetAvDegree));
                                }
                                if (oldAvDegree > belotAvDegree) {
//                                  newTargetAvDegree -= newTargetAvDegree * errorDegree;
                                  newTargetAvDegree -= newTargetAvDegree * ThreadLocalRandom.current().nextDouble(errorDegree);
//                                  newTargetAvDegree -= newTargetAvDegree * ThreadLocalRandom.current().nextDouble(MUTATION_SD);

//                                  logger.debug("Decreasing degree for " + profession + ":\t" + String.format("%.2f", oldAvDegree)
//                                          + " to " + String.format("%.2f", newTargetAvDegree));
                                }
                            }

                            newTargetAvDegrees.put(profession, newTargetAvDegree);
                        }


                        double errorClustering = Math.abs((this.currTargetClustering - clustering) / this.currTargetClustering);
                        if (errorClustering > 0.02) {

                            if (clustering < this.currTargetClustering) {
//                                double oldAlpha = alpha;
//                                alpha += alpha * ThreadLocalRandom.current().nextDouble(MUTATION_SD);
                                alpha += alpha * errorClustering;
//                                logger.debug("Increasing alpha:\t\t" + String.format("%.2f", oldAlpha)
//                                        + " to " + String.format("%.2f", alpha));
                            }
                            if (clustering > this.currTargetClustering) {
//                                double oldAlpha = alpha;
//                                alpha -= alpha * ThreadLocalRandom.current().nextDouble(MUTATION_SD);
                                alpha -= alpha * errorClustering;
//                                logger.debug("Decreasing alpha:\t\t" + String.format("%.2f", oldAlpha)
//                                        + " to " + String.format("%.2f", alpha));
                            }
                        }

                        // create child
                        offspring.add(new ProfessionsGene(
                                generation,                                                                         // generation
                                this.dgData.getSimStats().getUpc() + "_" +
                                this.dgData.getUtilityModelParams().getCurrLockdownCondition().toString() + "_" +
                                String.valueOf(generation) + "_" + String.valueOf(genMember++)
                                        + "(" + mother.getSimpleId() + "-" + father.getSimpleId() + ")#" + child,   // id
                                generation + "-" + child,                                                           // simpleId
                                mother.getId(), father.getId(),                                                     // parents

                                // parents
                                this.belotDegrees, newTargetAvDegrees,
                                this.currTargetClustering, this.currTargetClustering, alpha));
                    }
                }
            }
        }

        return offspring;
    }


    /**
     * @return
     */
    private List<ProfessionsGene> selectParents() {
        List<ProfessionsGene> parents = new LinkedList<ProfessionsGene>();
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
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network, this.simulation.getRounds()));
        this.nsWriter.writeCurrentData();
    }


    /**
     * Exports the network as adjacency matrix, edge list, and Gephi files.
     *
     * @return file
     */
    private String exportNetworks(String nameAppendix) {
        String fileName = this.dgData.getSimStats().getUid();
        if (nameAppendix != null && !nameAppendix.isEmpty()) {
            fileName += "-" + nameAppendix;
        }

        DGSWriter dgsWriter = new DGSWriter();
        String file = getExportPath() + fileName + ".dgs";
        dgsWriter.writeNetwork(this.network, file);
        this.dgData.setExportFileName(file);
//        logger.info("Network successfully exported to: " + file);
        return file;
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
        Map<String, Double> avDegreesByProfessions = this.network.getAvDegreesByProfessions();
        double fitnessOverall = this.currentOffspring.getFitnessOverall(
                avDegreesByProfessions, this.network.getAvClustering(simulation.getRounds()));

        boolean improved = this.fitnessPrevRound >= fitnessOverall;

//        int lastImproved = this.improvedRoundsAgo;
        this.improvedRoundsAgo = improved ? 1 : this.improvedRoundsAgo+1;
        this.simFinished = this.improvedRoundsAgo >= MAX_ROUNDS_NO_IMPROVEMENT;

        if (this.simFinished) {
            // stop simulation
            simulation.stop();
//            logger.debug("Gene finished after " + this.improvedRoundsAgo + " rounds of no improvement!");
//            logger.debug("Gene finished after " + this.dgData.getSimStats().getCurrRound() + " rounds.");
        } else if (improved) {
            // overwrite networks with better fitness
            exportNetworks(null);

//            String file = exportNetworks(null);
//
//            if (this.dgData.getUtilityModelParams().getCurrLockdownCondition() == LockdownConditions.PRE) {
//                if (bestFitFiles.size() < NUMBER_OF_BEST_FITS) {
//                    bestFitFiles.put(file, fitnessOverall);
//                } else {
//                    Iterator<String> files = bestFitFiles.keySet().iterator();
//                    double worstFit = 0.0;
//                    String worstFitFile = "";
//                    logger.debug("Current best fits: ");
//                    while (files.hasNext()) {
//                        String currFile = files.next();
//                        Double currFit = bestFitFiles.get(currFile);
//                        logger.debug(currFile + ": " + currFit);
//                        if (currFit > worstFit) {
//                            worstFit = currFit;
//                            worstFitFile = currFile;
//                        }
//                    }
//
//                    if (fitnessOverall < worstFit) {
//                        Map<String, Double> dummy = new HashMap<String, Double>();
//                        files = bestFitFiles.keySet().iterator();
//                        while (files.hasNext()) {
//                            String currFile = files.next();
//                            if (!currFile.equals(worstFitFile)) {
//                                dummy.put(currFile, bestFitFiles.get(currFile));
//                            }
//                        }
//                        dummy.put(file, fitnessOverall);
//                        bestFitFiles = dummy;
//
//                        logger.debug("Current fitness (" + fitnessOverall + ") improved over worst best fit (" + worstFit + ").");
//
//                    }
//                }
//            }

            // fitness
            this.currentOffspring.update(avDegreesByProfessions, this.network.getAvClustering(simulation.getRounds()));
            this.fitnessPrevRound = this.currentOffspring.getFitnessOverall();
            // network stats
            NetworkStatsPre netStatsPre = new NetworkStatsPre(this.network, this.simulation.getRounds());
            netStatsPre.setDegreesSdByProfessionTheoretic(this.belotDegreeSds);
            this.dgData.setNetStatsPre(netStatsPre);
            // exports only when fitness is still improving
            amendSummary();         // amend summary CSV
//            logger.debug("Result improved after " + (lastImproved+1) + " round(s) since last improvement.");
        }
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {
        this.fitnessPrevRound = Double.MAX_VALUE;
        this.simFinished = false;

        this.dgData.getSimStats().setUid(this.currentOffspring.getId());
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

