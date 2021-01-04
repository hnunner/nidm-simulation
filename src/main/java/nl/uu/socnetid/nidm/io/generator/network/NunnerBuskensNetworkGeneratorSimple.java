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
import java.util.Iterator;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
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
public class NunnerBuskensNetworkGeneratorSimple extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensNetworkGeneratorSimple.class);

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
    public NunnerBuskensNetworkGeneratorSimple(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens.simple";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<NunnerBuskensParameters>(PropertiesHandler.getInstance().getNunnerBuskensParameters());
        this.dgData.getSimStats().setUpc(0);
        this.dgData.getUtilityModelParams().setCurrN(100);
        this.dgData.getUtilityModelParams().setCurrB1(1.0);
        this.dgData.getUtilityModelParams().setCurrB2(0.5);
        this.dgData.getUtilityModelParams().setCurrC1(0.2);
        this.dgData.getUtilityModelParams().setCurrPhi(0.60);
        this.dgData.getUtilityModelParams().setCurrPsi(0.6);
        this.dgData.getUtilityModelParams().setCurrOmega(0.8);
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


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {
        // settings for clustering of 0.21: 0.625 <= alpha <= 0.630
        for (double alpha = 0.9; alpha <= 0.9; alpha = Math.round((alpha + 0.1) * 1000.0) / 1000.0) {
            this.dgData.getUtilityModelParams().setCurrAlpha(alpha);
            // settings for av. degree of 5.44:  5.56 <= tMean <= 5.57
            for (double targetMean = 6; targetMean <= 6; targetMean = Math.round((targetMean + 2) * 100.0) / 100.0) {

                logger.debug("starting to simulate 3 run(s) for alpha: " + alpha + " and target mean: " + targetMean);

                this.dgData.getSimStats().incUpc();
                for (int run = 1; run <= 3; run++) {
                    logger.debug("run " + run + ": started.");
                    this.dgData.getSimStats().setSimPerUpc(run);
                    // uid = "upc-sim"
                    this.dgData.getSimStats().setUid(
                            String.valueOf(this.dgData.getSimStats().getUpc()) +
                            "#" + String.valueOf(
                                    this.dgData.getSimStats().getSimPerUpc()));

                    // create vector of degree according to exponential distribution
                    long[] ties = new long[this.dgData.getUtilityModelParams().getCurrN()];
                    // 0.09 for tMean = 5.5x; 0.17 for tMean = 3.xx; 0.25 for tMean = 2.xx
                    ExponentialDistribution ed = new ExponentialDistribution(targetMean - targetMean*0.09);
                    double mean = 0;
                    while ((Math.round(mean * 100.0) / 100.0) != targetMean) {
                        double allSamples = 0;
                        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
                            long sample = 0;
                            while (sample == 0) {
                                sample = Math.round(ed.sample());
                            }
                            ties[i] = sample;
                            allSamples += sample;
                        }
                        mean = (Math.round((allSamples / this.dgData.getUtilityModelParams().getCurrN()) * 100.0) / 100.0);
                        if (mean != targetMean) {
                            logger.debug("redoing sampling, as mean (" + mean +
                                    ") too far from target mean (" + targetMean + ").");
                        }
                    }

                    // create vector of marginal costs to match degrees
                    double allC2s = 0.0;
                    double[] c2s = new double[ties.length];
                    NunnerBuskensParameters ump = this.dgData.getUtilityModelParams();
                    for (int i = 0; i < ties.length; i++) {
                        double c2 = (ump.getCurrB1() - ump.getCurrC1()) / (2*ties[i]);
                        c2s[i] = c2;
                        allC2s += c2;
                    }
                    this.dgData.getUtilityModelParams().setCurrC2(allC2s/this.dgData.getUtilityModelParams().getCurrN());

                    // create network
                    this.network = new Network("Network of the infectious kind", false, AssortativityConditions.AGE);
                    // disease specs - same for all
                    DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);
                    for (int i = 0; i < c2s.length; i++) {
                        // utility - differing in c2 to realize degree distribution
                        UtilityFunction uf = new NunnerBuskens(
                                this.dgData.getUtilityModelParams().getCurrB1(),
                                this.dgData.getUtilityModelParams().getCurrB2(),
                                this.dgData.getUtilityModelParams().getCurrAlpha(),
                                this.dgData.getUtilityModelParams().getCurrC1(),
                                c2s[i]);
                        // add agents
                        network.addAgent(
                                uf,
                                ds,
                                1.0,
                                1.0,
                                this.dgData.getUtilityModelParams().getCurrPhi(),
                                this.dgData.getUtilityModelParams().getCurrOmega(),
                                this.dgData.getUtilityModelParams().getCurrPsi(),
                                this.dgData.getUtilityModelParams().getXi(),
                                AgeStructure.getInstance().getRandomAge(),
                                this.dgData.getUtilityModelParams().isConsiderAge(),
                                Professions.getInstance().getRandomProfession(),
                                this.dgData.getUtilityModelParams().isConsiderProfession());
                    }
                    this.dgData.setAgents(new ArrayList<Agent>(network.getAgents()));
                    logger.debug("theoretic mean degree: " + this.network.getTheoreticAvDegree());


                    // create simulation
                    this.simulation = new Simulation(network);
                    this.simulation.addSimulationListener(this);
                    // simulate
                    this.dgData.getSimStats().resetCurrRound();
                    logger.debug("Trying to approximate av. clustering (" + alpha
                            + ") and av. degree (" + targetMean + ").");
                    approximate(alpha, 100.0, targetMean, 100.0, 10);

                    double newAlpha = alpha;
                    double newTargetMean = targetMean;
                    // MUNICIPALITY
                    for (int i = 0; i < 15; i++) {
                        logger.debug("Preparing lower clustering.");
                        newAlpha = Math.round((newAlpha - 0.05) * 1000.0) / 1000.0;
                        lowerAlpha(newAlpha);
                        double newC = Math.round((this.network.getAvClustering(this.simulation.getRounds())-0.05) * 100.0) / 100.0;
                        newTargetMean = 5.53  + (0.0453 * Math.pow(Math.E, 2.589 * newAlpha));
                        lowerC2s(newTargetMean);
                        logger.debug("Trying to approximate av. clustering (" + newC
                                + ") and av. degree (" + newTargetMean + ").");
                        approximate(newC, 100.0, newTargetMean, 100.0, 10);
                    }

                    logger.debug("run " + run + ": finished.");
                }
            }
        }
        finalizeDataExportFiles();
    }


    /**
     * @param muniAlpha
     */
    private void lowerAlpha(double alpha) {
        Iterator<Agent> aIt = this.network.getAgentIterator();
        while (aIt.hasNext()) {
            Agent agent = aIt.next();
            NunnerBuskens uf = (NunnerBuskens) agent.getUtilityFunction();
            uf.setAlpha(alpha);
        }
        this.dgData.getUtilityModelParams().setCurrAlpha(alpha);
    }


    private void lowerC2s(double targetDegree) {
        Iterator<Agent> aIt;
        double allC2s = 0.0;
        while (Math.round((this.network.getTheoreticAvDegree() * 100.0)) / 100.0 > targetDegree) {
            aIt = this.network.getAgentIterator();
            allC2s = 0.0;
            while (aIt.hasNext()) {
                Agent agent = aIt.next();
                NunnerBuskens uf = (NunnerBuskens) agent.getUtilityFunction();
                double newC2 = uf.getC2() + uf.getC2()*0.003;
                uf.setC2(newC2);
                allC2s += newC2;
            }
            logger.debug("new mean degree: " +
            Math.round((this.network.getTheoreticAvDegree() * 100.0)) / 100.0);
        }
        this.dgData.getUtilityModelParams().setCurrC2(allC2s/this.dgData.getUtilityModelParams().getCurrN());
    }


    private void approximate(double appC, double cPrecision, double appD, double dPrecision, int maxRounds) {

        int round = 0;

        double avC = Math.round(this.network.getAvClustering(this.simulation.getRounds()) * cPrecision) / cPrecision;
        double avD = Math.round(this.network.getAvDegree(this.simulation.getRounds()) * dPrecision) / dPrecision;

        while (true) {//!(avC == appC && avD == appD)) {
            this.simulation.simulate(1);
            avC = Math.round(this.network.getAvClustering(this.simulation.getRounds()) * cPrecision) / cPrecision;
            avD = Math.round(this.network.getAvDegree(this.simulation.getRounds()) * dPrecision) / dPrecision;
            round++;
            logger.debug("round: " + round +
                    "\t clustering: " + avC +
                    "\t av. degree: " + avD);
            if (round >= maxRounds) {
                break;
            }
        }
        logger.debug("Approximation " + (avC == appC && avD == appD ? "successful"
                : "failed (av. clustering: " + Math.round(this.network.getAvClustering(this.simulation.getRounds()) * cPrecision) / cPrecision +
                "; av. degree: " + Math.round(this.network.getAvDegree(this.simulation.getRounds()) * dPrecision) / dPrecision));
    }


    /**
     * Amends the summary file by writing a row with the current state of the network.
     */
    private void amendSummary() {
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network, this.simulation.getRounds()));
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
