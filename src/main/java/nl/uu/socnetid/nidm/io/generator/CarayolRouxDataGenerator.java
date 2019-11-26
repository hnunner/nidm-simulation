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
import nl.uu.socnetid.nidm.data.CarayolRouxParameters;
import nl.uu.socnetid.nidm.data.DataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.CarayolRouxSimulationSummaryWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.CarayolRoux;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO generalize, as this class is very similar to {@link CidmDataGenerator}
 */
public class CarayolRouxDataGenerator extends AbstractDataGenerator {

    // logger
    private static final Logger logger = Logger.getLogger(CarayolRouxDataGenerator.class);

    // stats & writer
    private DataGeneratorData<CarayolRouxParameters> dgData;
    private CarayolRouxSimulationSummaryWriter ssWriter;


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
    public CarayolRouxDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "carayolroux";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<CarayolRouxParameters>(PropertiesHandler.getInstance().getCarayolRouxParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new CarayolRouxSimulationSummaryWriter(getExportPath() + "simulation-summary.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generateData() {

        double[] crOmegas = this.dgData.getUtilityModelParams().isCrOmegaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getCrOmegas();
        double[] deltas = this.dgData.getUtilityModelParams().isDeltaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getDeltas();
        double[] cs = this.dgData.getUtilityModelParams().isCRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getCs();
        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();

        // unique parameter combinations
        int upcs =
                crOmegas.length *
                deltas.length *
                cs.length *
                Ns.length *
                iotas.length *
                phis.length;

        // loop over all possible parameter combinations
        for (double crOmega : crOmegas) {
            this.dgData.getUtilityModelParams().setCurrCrOmega(crOmega);
            for (double delta : deltas) {
                this.dgData.getUtilityModelParams().setCurrDelta(delta);
                for (double c : cs) {
                    this.dgData.getUtilityModelParams().setCurrC(c);
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

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in crData
     */
    private void performSingleSimulation() {

        // create network
        Network network = new Network();

        // setting parameters
        // CarayolRoux omega
        if (this.dgData.getUtilityModelParams().isCrOmegaRandom()) {
            this.dgData.getUtilityModelParams().setCurrCrOmega(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getCrOmegaRandomMin(),
                    this.dgData.getUtilityModelParams().getCrOmegaRandomMax()));
        }
        // delta
        if (this.dgData.getUtilityModelParams().isDeltaRandom()) {
            this.dgData.getUtilityModelParams().setCurrDelta(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getDeltaRandomMin(),
                    this.dgData.getUtilityModelParams().getDeltaRandomMax()));
        }
        // c
        if (this.dgData.getUtilityModelParams().isCRandom()) {
            this.dgData.getUtilityModelParams().setCurrC(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getCRandomMin(),
                    this.dgData.getUtilityModelParams().getCRandomMax()));
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
        // assortative mixing not feasible for CarayolRoux due to geographic distance
        this.dgData.getUtilityModelParams().setCurrOmega(0.0);
//        if (this.dgData.getUtilityModelParams().isOmegaRandom()) {
//            this.dgData.getUtilityModelParams().setCurrOmega(ThreadLocalRandom.current().nextDouble(
//                    this.dgData.getUtilityModelParams().getOmegaRandomMin(),
//                    this.dgData.getUtilityModelParams().getOmegaRandomMax()));
//        }

        // create utility
        UtilityFunction uf = new CarayolRoux(
                this.dgData.getUtilityModelParams().getCurrCrOmega(),
                this.dgData.getUtilityModelParams().getCurrDelta(),
                this.dgData.getUtilityModelParams().getCurrC());

        // create disease specs
        DiseaseSpecs ds = new DiseaseSpecs(
                DiseaseType.SIR, 10, 10, 0.1, 1.0);

        // add agents
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            network.addAgent(uf, ds, 0, 0, this.dgData.getUtilityModelParams().getCurrPhi(),
                    this.dgData.getUtilityModelParams().getCurrPhi());
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
        Path srcAnalysis = Paths.get(PropertiesHandler.getInstance().getRAnalysisCarayolRouxTemplatePath());
        String dstAnalysisPath = getExportPath() + "carayolroux.R";
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
