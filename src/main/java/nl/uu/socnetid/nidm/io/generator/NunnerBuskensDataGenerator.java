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
package nl.uu.socnetid.nidm.io.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.data.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensSimulationSummaryWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO generalize, as this class is very similar to {@link CidmDataGenerator}
 */
public class NunnerBuskensDataGenerator extends AbstractDataGenerator {

    // logger
    private static final Logger logger = Logger.getLogger(NunnerBuskensDataGenerator.class);

    // stats & writer
    private DataGeneratorData<NunnerBuskensParameters> dgData;
    private NunnerBuskensSimulationSummaryWriter ssWriter;


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
    public NunnerBuskensDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<NunnerBuskensParameters>(PropertiesHandler.getInstance().getNunnerBuskensParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new NunnerBuskensSimulationSummaryWriter(getExportPath() + "simulation-summary.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generateData() {

        double[] b1s = this.dgData.getUtilityModelParams().isB1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB1s();
        double[] b2s = this.dgData.getUtilityModelParams().isB2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB2s();
        double[] alphas = this.dgData.getUtilityModelParams().isAlphaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getAlphas();
        double[] c1s = this.dgData.getUtilityModelParams().isC1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC1s();
        double[] c2s = this.dgData.getUtilityModelParams().isC2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC2s();
        boolean[] yGlobals = this.dgData.getUtilityModelParams().isYGlobalRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getYGlobals();
        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();

        // unique parameter combinations
        int upcs =
                b1s.length *
                b2s.length *
                alphas.length *
                c1s.length *
                c2s.length *
                yGlobals.length *
                Ns.length *
                iotas.length *
                phis.length;

        // loop over all possible parameter combinations
        for (double b1 : b1s) {
            this.dgData.getUtilityModelParams().setCurrB1(b1);
            for (double b2 : b2s) {
                this.dgData.getUtilityModelParams().setCurrB2(b2);
                for (double alpha : alphas) {
                    this.dgData.getUtilityModelParams().setCurrAlpha(alpha);
                    for (double c1 : c1s) {
                        this.dgData.getUtilityModelParams().setCurrC1(c1);
                        for (double c2 : c2s) {
                            this.dgData.getUtilityModelParams().setCurrC2(c2);
                            for (boolean yGlobal : yGlobals) {
                                this.dgData.getUtilityModelParams().setCurrYGlobal(yGlobal);
                                for (int N : Ns) {
                                    this.dgData.getUtilityModelParams().setCurrN(N);
                                    for (boolean iota : iotas) {
                                        this.dgData.getUtilityModelParams().setCurrIota(iota);
                                        for (double phi : phis) {
                                            this.dgData.getUtilityModelParams().setCurrPhi(phi);

                                            this.dgData.getSimStats().incUpc();
                                            logger.info("Starting to compute "
                                                    + this.dgData.getUtilityModelParams().
                                                    getSimsPerParameterCombination()
                                                    + " simulations for parameter combination: "
                                                    + this.dgData.getSimStats().getUpc() + " / "
                                                    + upcs);

                                            // multiple simulations for same parameter combination
                                            this.dgData.getSimStats().setSimPerUpc(1);
                                            while (this.dgData.getSimStats().getSimPerUpc()
                                                    <= this.dgData.getUtilityModelParams().
                                                    getSimsPerParameterCombination()) {

                                                // uid = "upc-sim"
                                                this.dgData.getSimStats().setUid(
                                                        String.valueOf(this.dgData.getSimStats().getUpc()) +
                                                        "-" + String.valueOf(
                                                                this.dgData.getSimStats().getSimPerUpc()));

                                                // simulate
                                                performSingleSimulation();

                                                // log simulation summary
                                                if (PropertiesHandler.getInstance().isExportSummary()) {
                                                    this.ssWriter.writeCurrentData();
                                                }

                                                logger.debug("Simulation " + this.dgData.getSimStats().getSimPerUpc() +
                                                        "/" + this.dgData.getUtilityModelParams().getSimsPerParameterCombination() +
                                                        " of parameter combination " +
                                                        this.dgData.getSimStats().getUpc() + "/" + upcs + " finished.");
                                                this.dgData.getSimStats().incSimPerUpc();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in dgData
     */
    private void performSingleSimulation() {

        // create network
        Network network = new Network();

        // setting parameters
        // b1
        if (this.dgData.getUtilityModelParams().isB1Random()) {
            this.dgData.getUtilityModelParams().setCurrB1(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getB1RandomMin(),
                    this.dgData.getUtilityModelParams().getB1RandomMax()));
        }
        // b2
        if (this.dgData.getUtilityModelParams().isB2Random()) {
            this.dgData.getUtilityModelParams().setCurrB2(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getB2RandomMin(),
                    this.dgData.getUtilityModelParams().getB2RandomMax()));
        }
        // alpha
        if (this.dgData.getUtilityModelParams().isAlphaRandom()) {
            this.dgData.getUtilityModelParams().setCurrAlpha(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getAlphaRandomMin(),
                    this.dgData.getUtilityModelParams().getAlphaRandomMax()));
        }
        // c1
        if (this.dgData.getUtilityModelParams().isC1Random()) {
            this.dgData.getUtilityModelParams().setCurrC1(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getC1RandomMin(),
                    this.dgData.getUtilityModelParams().getC1RandomMax()));
        }
        // c2
        if (this.dgData.getUtilityModelParams().isC2Random()) {
            this.dgData.getUtilityModelParams().setCurrC2(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getC2RandomMin(),
                    this.dgData.getUtilityModelParams().getC2RandomMax()));
        }
        // yGlobal
        if (this.dgData.getUtilityModelParams().isYGlobalRandom()) {
            this.dgData.getUtilityModelParams().setCurrYGlobal(ThreadLocalRandom.current().nextBoolean());
        }
        // N
        if (this.dgData.getUtilityModelParams().isNRandom()) {
            this.dgData.getUtilityModelParams().setCurrN(ThreadLocalRandom.current().nextInt(
                    this.dgData.getUtilityModelParams().getNRandomMin(),
                    this.dgData.getUtilityModelParams().getNRandomMax()));
        }
        // iota
        if (this.dgData.getUtilityModelParams().isIotaRandom()) {
            this.dgData.getUtilityModelParams().setCurrIota(ThreadLocalRandom.current().nextBoolean());
        }
        // phi
        if (this.dgData.getUtilityModelParams().isPhiRandom()) {
            this.dgData.getUtilityModelParams().setCurrPhi(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getPhiRandomMin(),
                    this.dgData.getUtilityModelParams().getPhiRandomMax()));
        }

        // create utility
        UtilityFunction uf = new NunnerBuskens(
                this.dgData.getUtilityModelParams().getCurrB1(),
                this.dgData.getUtilityModelParams().getCurrB2(),
                this.dgData.getUtilityModelParams().getCurrAlpha(),
                this.dgData.getUtilityModelParams().getCurrC1(),
                this.dgData.getUtilityModelParams().getCurrC2(),
                this.dgData.getUtilityModelParams().isCurrYGlobal());

        // create disease specs
        DiseaseSpecs ds = new DiseaseSpecs(
                DiseaseType.SIR, 10, 10, 0.1, 1.0);

        // add agents
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            network.addAgent(uf, ds, 0, 0, this.dgData.getUtilityModelParams().getCurrPhi());
        }
        this.dgData.setAgents(new LinkedList<Agent>(network.getAgents()));

        // create full network if required
        if (!this.dgData.getUtilityModelParams().isCurrIota()) {
            network.createFullNetwork();
        }

        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);
        // create simulation
        Simulation simulation = new Simulation(network);
        // simulate
        simulation.simulate();
        // save data
        this.dgData.setNetStatsCurrent(new NetworkStats(network));

    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#prepareAnalysis()
     */
    @Override
    protected String prepareAnalysis() {
        // preparation of R-scripts
        Path srcAnalysis = Paths.get(PropertiesHandler.getInstance().getRAnalysisNunnerBuskensTemplatePath());
        String dstAnalysisPath = getExportPath() + "nunnerbuskens.R";
        Path dstAnalysis = Paths.get(dstAnalysisPath);
        try {
            Files.copy(srcAnalysis, dstAnalysis, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e);
        }
        return dstAnalysisPath;
    }

    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            if (PropertiesHandler.getInstance().isExportSummary()) {
                this.ssWriter.flush();
                this.ssWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
