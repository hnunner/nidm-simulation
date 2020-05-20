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
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensNetworkSummaryWriter;
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
    private static final double PCT_SD = 0.05;


    // target values
    private double targetAvDegree = 5.44;           // controlled by c_2
    private double targetClustering = 0.21;         // controlled by alpha
    private double targetAssortativity = 0.37;      // controlled by omega

    // network
    private Network network;

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<NunnerBuskensParameters> dgData;
    private NunnerBuskensNetworkSummaryWriter nsWriter;


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
        this.dgData = new DataGeneratorData<NunnerBuskensParameters>(PropertiesHandler.getInstance().getNunnerBuskensParameters());
        this.dgData.getSimStats().setUpc(0);

        // TODO move constant initial values to config.properties
        this.dgData.getUtilityModelParams().setCurrN(100);
        this.dgData.getUtilityModelParams().setCurrB1(1.0);
        this.dgData.getUtilityModelParams().setCurrB2(0.5);
        this.dgData.getUtilityModelParams().setCurrC1(0.2);
        this.dgData.getUtilityModelParams().setCurrPhi(0.6);
        this.dgData.getUtilityModelParams().setCurrPsi(0.6);
        this.dgData.getUtilityModelParams().setAssortativityCondition(AssortativityConditions.AGE);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.nsWriter = new NunnerBuskensNetworkSummaryWriter(getExportPath() + "network-summary.csv", this.dgData);
        }
    }

    /**
     * Class holding genetic information for NunnerBuskens parameter optimization.
     *
     * @author Hendrik Nunner
     */
    private class NunnerBuskensGene implements Comparable<NunnerBuskensGene> {

        private final int generation;
        private final String id;
        private final String mother;
        private final String father;

        private final double avC2;
        private final double alpha;
        private final double omega;

        private final double avDegree;
        private final double clustering;
        private final double assortativity;

        private final double targetAvDegree;
        private final double targetClustering;
        private final double targetAssortativity;

        private final double fitnessAvDegree;
        private final double fitnessClustering;
        private final double fitnessAssortativity;
        private final double fitnessOverall;

        /**
         * Creates a gene for NunnerBuskens parameter optimization.
         *
         * @param avC2
         *          the average marginal costs
         * @param alpha
         *          the proportion of closed triads
         * @param omega
         *          the proportion of similar peers selected for tie creation
         * @param avDegree
         *          the resulting average degree
         * @param clustering
         *          the resulting clustering
         * @param assortativity
         *          the resulting assortativity
         * @param targetAvDegree
         *          the target average degree
         * @param targetClustering
         *          the target clustering
         * @param targetAssortativity
         *          the target assortativity
         */
        protected NunnerBuskensGene(int generation, String id, String mother, String father,
                double avC2, double alpha, double omega,
                double avDegree, double clustering, double assortativity,
                double targetAvDegree, double targetClustering, double targetAssortativity) {

            this.generation = generation;
            this.id = id;
            this.mother = mother;
            this.father = father;

            this.avC2 = avC2;
            this.alpha = alpha;
            this.omega = omega;
            this.avDegree = avDegree;
            this.clustering = clustering;
            this.assortativity = assortativity;
            this.targetAvDegree = targetAvDegree;
            this.targetClustering = targetClustering;
            this.targetAssortativity = targetAssortativity;

            this.fitnessAvDegree = computePercentageError(avDegree, targetAvDegree);
            this.fitnessClustering = computePercentageError(clustering, targetClustering);
            this.fitnessAssortativity = computePercentageError(assortativity, targetAssortativity);
            this.fitnessOverall = this.fitnessAvDegree + this.fitnessClustering + this.fitnessAssortativity;
        }

        /**
         * Computes the error between a comparable value and a target value in percent.
         *
         * @param comp
         *          the comparable
         * @param target
         *          the target value
         * @return
         */
        private double computePercentageError(double comp, double target) {
            return Math.abs((target - comp) / target);
        }

        /**
         * @return the generation
         */
        public int getGeneration() {
            return generation;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the mother
         */
        public String getMother() {
            return mother;
        }

        /**
         * @return the father
         */
        public String getFather() {
            return father;
        }

        /**
         * @return the avDegree
         */
        protected double getAvDegree() {
            return avDegree;
        }

        /**
         * @return the clustering
         */
        protected double getClustering() {
            return clustering;
        }

        /**
         * @return the assortativity
         */
        protected double getAssortativity() {
            return assortativity;
        }

        /**
         * @return the avC2
         */
        protected double getAvC2() {
            return avC2;
        }

        /**
         * @return the alpha
         */
        protected double getAlpha() {
            return alpha;
        }

        /**
         * @return the omega
         */
        protected double getOmega() {
            return omega;
        }

        /**
         * @return the targetAvDegree
         */
        protected double getTargetAvDegree() {
            return targetAvDegree;
        }

        /**
         * @return the targetClustering
         */
        protected double getTargetClustering() {
            return targetClustering;
        }

        /**
         * @return the targetAssortativity
         */
        protected double getTargetAssortativity() {
            return targetAssortativity;
        }

        /**
         * @return the fitnessAvDegree
         */
        protected double getFitnessAvDegree() {
            return fitnessAvDegree;
        }

        /**
         * @return the fitnessClustering
         */
        protected double getFitnessClustering() {
            return fitnessClustering;
        }

        /**
         * @return the fitnessAssortativity
         */
        protected double getFitnessAssortativity() {
            return fitnessAssortativity;
        }

        /**
         * @return the fitnessOverall
         */
        protected Double getFitnessOverall() {
            return fitnessOverall;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo()
         */
        @Override
        public int compareTo(NunnerBuskensGene nbg) {
            if (nbg != null && nbg.getFitnessOverall() != null) {
                // ascending
                return this.getFitnessOverall().compareTo(nbg.getFitnessOverall());
                // descending
                //return nbg.getFitnessOverall().compareTo(this.getFitnessOverall());
            }
            return -1;
        }
    }


    private void initNetwork(double targetAvC2, double targetAlpha, double targetOmega) {

        this.network = new Network("Network of the infectious kind", AssortativityConditions.AGE);
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);    // not used

        double targetAvDegree = NunnerBuskens.getAvDegreeFromC2(
                this.dgData.getUtilityModelParams().getCurrB1(),
                this.dgData.getUtilityModelParams().getCurrC1(),
                targetAvC2);

        // TODO change exponential distribution to something more grounded in theory (e.g. Danon et al. (2013))
        ExponentialDistribution ed = new ExponentialDistribution(targetAvDegree);

        while ((Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0) != (Math.round(targetAvDegree * 100.0) / 100.0)) {
            this.network.clear();
            logger.info("(Re-)sampling random degrees to achieve degree distribution with theoretic average degree of "
                    + (Math.round(targetAvDegree * 100.0) / 100.0));
            for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
                // TODO remove sample == 0 check once simple exponential distribution has been replaced
                long targetDegree = 0;
                while (targetDegree == 0) {
                    targetDegree = Math.round(ed.sample());
                }

                double c2 = NunnerBuskens.getC2FromAvDegree(
                        this.dgData.getUtilityModelParams().getCurrB1(),
                        this.dgData.getUtilityModelParams().getCurrC1(),
                        targetDegree);

                // utility
                UtilityFunction uf = new NunnerBuskens(
                        this.dgData.getUtilityModelParams().getCurrB1(),
                        this.dgData.getUtilityModelParams().getCurrB2(),
                        targetAlpha,
                        this.dgData.getUtilityModelParams().getCurrC1(),
                        c2);

                // add agents
                this.network.addAgent(uf,
                        ds,
                        1.0,
                        1.0,
                        this.dgData.getUtilityModelParams().getCurrPhi(),
                        targetOmega,
                        this.dgData.getUtilityModelParams().getCurrPsi());
            }
            logger.info("Theoretic average degree: " + (Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0));
        }
        logger.info("Network initialization successful.");
        this.dgData.setAgents(new ArrayList<Agent>(this.network.getAgents()));
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        List<NunnerBuskensGene> allGenes = new LinkedList<NunnerBuskensGene>();
        List<NunnerBuskensGene> parents = new LinkedList<NunnerBuskensGene>();


        for (int i = 0; i < 12; i++) {

            // init network with target values
            NormalDistribution ndAvDegree = new NormalDistribution(this.targetAvDegree, this.targetAvDegree * PCT_SD);
            double targetAvC2 = NunnerBuskens.getC2FromAvDegree(
                    this.dgData.getUtilityModelParams().getCurrB1(),
                    this.dgData.getUtilityModelParams().getCurrC1(),
                    ndAvDegree.sample());

            NormalDistribution ndAlpha = new NormalDistribution(this.targetClustering, this.targetClustering * PCT_SD);
            double targetAlpha = ndAlpha.sample();

            NormalDistribution ndOmega = new NormalDistribution(this.targetAssortativity, this.targetAssortativity * PCT_SD);
            double targetOmega = ndOmega.sample();

            initNetwork(targetAvC2, targetAlpha, targetOmega);

            // simulate
            this.simulation = new Simulation(this.network);
            this.simulation.addSimulationListener(this);
            this.simulation.simulateUntilStable(50);

            allGenes.add(new NunnerBuskensGene(
                    0, "0#"+i, "god", "god",
                    targetAvC2, targetAlpha, targetOmega,
                    this.network.getAvDegree(), this.network.getAvClustering(), this.network.getAssortativity(),
                    this.targetAvDegree, this.targetClustering, this.targetAssortativity));


        }
        // select parents
        Collections.sort(allGenes);
        for (int i = 0; i < 4; i++) {
            parents.add(allGenes.get(i));
        }

        for (int i = 0; i < 50; i++) {
            List<NunnerBuskensGene> offspring = new LinkedList<NunnerBuskensGene>();
            int oCnt = 1;
            // recombine genes
            for (int x = 0; x < parents.size(); x++) {              // mother
                NunnerBuskensGene mother = parents.get(x);

                for (int y = x+1; y < parents.size(); y++) {        // father
                    NunnerBuskensGene father = parents.get(y);

                    for (int z = 0; z < 2; z++) {                   // two children

                        // select genes
                        double avC2 = ThreadLocalRandom.current().nextBoolean() ? mother.getAvC2() : father.getAvC2();
                        double alpha = ThreadLocalRandom.current().nextBoolean() ? mother.getAlpha() : father.getAlpha();
                        double omega = ThreadLocalRandom.current().nextBoolean() ? mother.getOmega() : father.getOmega();

                        // mutate genes
                        avC2 += new NormalDistribution(0, avC2 * PCT_SD).sample();
                        alpha += new NormalDistribution(0, alpha * PCT_SD).sample();
                        omega += new NormalDistribution(0, omega * PCT_SD).sample();

                        offspring.add(new NunnerBuskensGene(
                                i+1, i+1 + "#" + oCnt, mother.getId(), father.getId(),
                                avC2, alpha, omega,
                                0.0, 0.0, 0.0,                      // to be computed
                                this.targetAvDegree, this.targetClustering, this.targetAssortativity));
                    }
                }
            }

            // simulate all offspring genes
            Iterator<NunnerBuskensGene> it = offspring.iterator();
            while (it.hasNext()) {
                NunnerBuskensGene o = it.next();

                // TODO generalize with lines above
                // init network with target values
                double avDegree = NunnerBuskens.getAvDegreeFromC2(this.dgData.getUtilityModelParams().getCurrB1(),
                        this.dgData.getUtilityModelParams().getCurrC1(), o.getAvC2());
                NormalDistribution ndAvDegree = new NormalDistribution(avDegree, avDegree * PCT_SD);
                double targetAvC2 = NunnerBuskens.getC2FromAvDegree(
                        this.dgData.getUtilityModelParams().getCurrB1(),
                        this.dgData.getUtilityModelParams().getCurrC1(),
                        ndAvDegree.sample());

                NormalDistribution ndAlpha = new NormalDistribution(o.getAlpha(), o.getAlpha() * PCT_SD);
                double targetAlpha = ndAlpha.sample();

                NormalDistribution ndOmega = new NormalDistribution(o.getOmega(), o.getOmega() * PCT_SD);
                double targetOmega = ndOmega.sample();

                initNetwork(targetAvC2, targetAlpha, targetOmega);

                // simulate
                this.simulation = new Simulation(this.network);
                this.simulation.addSimulationListener(this);
                this.simulation.simulate(20);

                allGenes.add(new NunnerBuskensGene(
                        o.getGeneration(), o.getId(), o.getMother(), o.getFather(),
                        targetAvC2, targetAlpha, targetOmega,
                        this.network.getAvDegree(), this.network.getAvClustering(), this.network.getAssortativity(),
                        this.targetAvDegree, this.targetClustering, this.targetAssortativity));
                oCnt++;
            }

            // select parents
            parents.clear();
            Collections.sort(allGenes);
            for (int j = 0; j < 4; j++) {
                parents.add(allGenes.get(j));
            }
        }
        logger.info("finished");
    }


    /**
     * Amends the summary file by writing a row with the current state of the network.
     */
    private void amendSummary() {
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
        this.nsWriter.writeCurrentData();
    }


    /**
     * Exports the network as adjacency matrix, edge list, and Gephi files.
     */
    private void exportNetworks(String nameAppendix) {
        NetworkFileWriter elWriter = new NetworkFileWriter(getExportPath(),
                this.dgData.getSimStats().getUid() + "-" + nameAppendix + ".el",
                new EdgeListWriter(),
                this.network);
        elWriter.write();

        NetworkFileWriter ageWriter = new NetworkFileWriter(getExportPath(),
                this.dgData.getSimStats().getUid() + "-" + nameAppendix + ".age",
                new AgentPropertiesWriter(),
                this.network);
        ageWriter.write();

        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.writeStaticNetwork(this.network, getExportPath() + this.dgData.getSimStats().getUid() +
                "-" + nameAppendix + ".gexf");
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
        if (simulation.getRounds() % 10 == 0) {
            amendSummary();
            exportNetworks("round_" + simulation.getRounds());
        }
        this.dgData.getSimStats().incCurrRound();
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {}

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {}

    @Override
    public void notifySimulationFinished(Simulation simulation) {}

}
