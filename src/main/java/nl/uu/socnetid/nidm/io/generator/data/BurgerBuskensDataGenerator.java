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
package nl.uu.socnetid.nidm.io.generator.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.BurgerBuskensParameters;
import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.BurgerBuskensSimulationSummaryWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.BurgerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class BurgerBuskensDataGenerator extends AbstractDataGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(BurgerBuskensDataGenerator.class);

    // stats & writer
    private DataGeneratorData<BurgerBuskensParameters> dgData;
    private BurgerBuskensSimulationSummaryWriter ssWriter;


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
    public BurgerBuskensDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "burgerbuskens";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<BurgerBuskensParameters>(PropertiesHandler.getInstance().getBurgerBuskensParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new BurgerBuskensSimulationSummaryWriter(getExportPath() + "simulation-summary.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        double[] b1s = this.dgData.getUtilityModelParams().isB1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB1s();
        double[] c1s = this.dgData.getUtilityModelParams().isC1Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC1s();
        double[] c2s = this.dgData.getUtilityModelParams().isC2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC2s();
        double[] b2s = this.dgData.getUtilityModelParams().isB2Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getB2s();
        double[] c3s = this.dgData.getUtilityModelParams().isC3Random() ?
                new double[1] : this.dgData.getUtilityModelParams().getC3s();
        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();
        double[] omegas = this.dgData.getUtilityModelParams().isOmegaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getOmegas();

        // unique parameter combinations
        int upcs =
                b1s.length *
                b2s.length *
                c1s.length *
                c2s.length *
                c3s.length *
                Ns.length *
                iotas.length *
                phis.length *
                omegas.length;

        // loop over all possible parameter combinations
        for (double b1 : b1s) {
            this.dgData.getUtilityModelParams().setCurrB1(b1);
            for (double b2 : b2s) {
                this.dgData.getUtilityModelParams().setCurrB2(b2);
                for (double c1 : c1s) {
                    this.dgData.getUtilityModelParams().setCurrC1(c1);
                    for (double c2 : c2s) {
                        this.dgData.getUtilityModelParams().setCurrC2(c2);
                        for (double c3 : c3s) {
                            this.dgData.getUtilityModelParams().setCurrC3(c3);
                            for (int N : Ns) {
                                this.dgData.getUtilityModelParams().setCurrN(N);
                                for (boolean iota : iotas) {
                                    this.dgData.getUtilityModelParams().setCurrIota(iota);
                                    for (double phi : phis) {
                                        this.dgData.getUtilityModelParams().setCurrPhi(phi);
                                        for (double omega : omegas) {
                                            this.dgData.getUtilityModelParams().setCurrOmega(omega);

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
        // c3
        if (this.dgData.getUtilityModelParams().isC3Random()) {
            this.dgData.getUtilityModelParams().setCurrC3(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getC3RandomMin(),
                    this.dgData.getUtilityModelParams().getC3RandomMax()));
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
        // omega
        if (this.dgData.getUtilityModelParams().isOmegaRandom()) {
            this.dgData.getUtilityModelParams().setCurrOmega(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getOmegaRandomMin(),
                    this.dgData.getUtilityModelParams().getOmegaRandomMax()));
        }

        // create utility
        UtilityFunction uf = new BurgerBuskens(
                this.dgData.getUtilityModelParams().getCurrB1(),
                this.dgData.getUtilityModelParams().getCurrB2(),
                this.dgData.getUtilityModelParams().getCurrC1(),
                this.dgData.getUtilityModelParams().getCurrC2(),
                this.dgData.getUtilityModelParams().getCurrC3());

        // create disease specs
        DiseaseSpecs ds = new DiseaseSpecs(
                DiseaseType.SIR, 10, 10, 0.1, 1.0);

        // add agents
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            network.addAgent(uf, ds, 0, 0, this.dgData.getUtilityModelParams().getCurrPhi(),
                    this.dgData.getUtilityModelParams().getCurrOmega());
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
        Path srcAnalysis = Paths.get(PropertiesHandler.getInstance().getRAnalysisBurgerBuskensTemplatePath());
        String dstAnalysisPath = getExportPath() + "burgerbuskens.R";
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
