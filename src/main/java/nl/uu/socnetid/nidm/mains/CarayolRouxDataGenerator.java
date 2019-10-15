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
package nl.uu.socnetid.nidm.mains;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.CarayolRouxDataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.generator.CarayolRouxSimulationSummaryWriter;
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
public class CarayolRouxDataGenerator {

    // logger
    private static final Logger logger = Logger.getLogger(CarayolRouxDataGenerator.class);

    // stats
    private CarayolRouxDataGeneratorData crData = new CarayolRouxDataGeneratorData();

    // network
    private Network network;

    // simulation
    private Simulation simulation;

    // data export
    private static final String FULL_DATA_EXPORT_PATH = PropertiesHandler.getInstance().getDataExportPath() +
            (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
    private CarayolRouxSimulationSummaryWriter ssWriter;


    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public static void main(String[] args) throws IOException {
        logger.info("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                ":: Copyright (C) 2017 - 2019\n" +
                "::     Hendrik Nunner    <h.nunner@gmail.com>\n" +
                "::\n" +
                ":: This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.\n" +
                "::\n" +
                ":: This project is a stand-alone Java program of the Networking during Infectious Diseases Model\n" +
                ":: (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network\n" +
                ":: formation and infectious diseases.\n" +
                "::\n" +
                ":: This program is free software: you can redistribute it and/or modify it under the\n" +
                ":: terms of the GNU General Public License as published by the Free Software Foundation,\n" +
                ":: either version 3 of the License, or (at your option) any later version.\n" +
                "::\n" +
                ":: This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n" +
                ":: without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n" +
                ":: See the GNU General Public License for more details.\n" +
                "::\n" +
                ":: You should have received a copy of the GNU General Public License along with this program.\n" +
                ":: If not, see <http://www.gnu.org/licenses/>.\n" +
                "::\n" +
                ":: References:\n" +
                "::     Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic\n" +
                "::     social networks and infectious diseases. Manuscript sumbitted for publication.\n"
                + "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");

        CarayolRouxDataGenerator dataGenerator = new CarayolRouxDataGenerator();
        dataGenerator.generateData();
        if (PropertiesHandler.getInstance().isAnalyzeData()) {
            dataGenerator.anaylzeData();
        }
    }


    /**
     * Constructor.
     *
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CarayolRouxDataGenerator() throws IOException {
        // initializations
        // create export directory if it does not exist
        File directory = new File(FULL_DATA_EXPORT_PATH);
        if (!directory.exists()){
            directory.mkdirs();
        }

        // copy properties file to output folder
        try {
            Path srcProperties = Paths.get(CidmDataGenerator.class.getClassLoader().getResource("config.properties").toURI());
            Path dstProperties = Paths.get(FULL_DATA_EXPORT_PATH + "config.properties");
            Files.copy(srcProperties, dstProperties, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException e) {
            logger.error(e);
        }

        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new CarayolRouxSimulationSummaryWriter(FULL_DATA_EXPORT_PATH + "simulation-summary.csv", this.crData);
        }
    }


    /**
     * Generates data.
     */
    public void generateData() {

        double[] omegas = this.crData.getUtilityModelParams().isOmegaRandom() ?
                new double[1] : this.crData.getUtilityModelParams().getOmegas();
        double[] deltas = this.crData.getUtilityModelParams().isDeltaRandom() ?
                new double[1] : this.crData.getUtilityModelParams().getDeltas();
        double[] cs = this.crData.getUtilityModelParams().isCRandom() ?
                new double[1] : this.crData.getUtilityModelParams().getCs();
        int[] Ns = this.crData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.crData.getUtilityModelParams().getNs();
        boolean[] iotas = this.crData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.crData.getUtilityModelParams().getIotas();
        double[] phis = this.crData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.crData.getUtilityModelParams().getPhis();

        // unique parameter combinations
        int upcs =
                omegas.length *
                deltas.length *
                cs.length *
                Ns.length *
                iotas.length *
                phis.length;

        // loop over all possible parameter combinations
        for (double omega : omegas) {
            this.crData.getUtilityModelParams().setCurrOmega(omega);
            for (double delta : deltas) {
                this.crData.getUtilityModelParams().setCurrDelta(delta);
                for (double c : cs) {
                    this.crData.getUtilityModelParams().setCurrC(c);
                    for (int N : Ns) {
                        this.crData.getUtilityModelParams().setCurrN(N);
                        for (boolean iota : iotas) {
                            this.crData.getUtilityModelParams().setCurrIota(iota);
                            for (double phi : phis) {
                                this.crData.getUtilityModelParams().setCurrPhi(phi);

                                this.crData.getSimStats().incUpc();
                                logger.info("Starting to compute "
                                        + this.crData.getUtilityModelParams().
                                        getSimsPerParameterCombination()
                                        + " simulations for parameter combination: "
                                        + this.crData.getSimStats().getUpc() + " / "
                                        + upcs);

                                // multiple simulations for same parameter combination
                                this.crData.getSimStats().setSimPerUpc(1);
                                while (this.crData.getSimStats().getSimPerUpc()
                                        <= this.crData.getUtilityModelParams().
                                        getSimsPerParameterCombination()) {

                                    // uid = "upc-sim"
                                    this.crData.getSimStats().setUid(
                                            String.valueOf(this.crData.getSimStats().getUpc()) +
                                            "-" + String.valueOf(
                                                    this.crData.getSimStats().getSimPerUpc()));

                                    // simulate
                                    performSingleSimulation();

                                    // log simulation summary
                                    if (PropertiesHandler.getInstance().isExportSummary()) {
                                        this.ssWriter.writeCurrentData();
                                    }

                                    logger.debug("Simulation " + this.crData.getSimStats().getSimPerUpc() +
                                            "/" + this.crData.getUtilityModelParams().getSimsPerParameterCombination() +
                                            " of parameter combination " +
                                            this.crData.getSimStats().getUpc() + "/" + upcs + " finished.");
                                    this.crData.getSimStats().incSimPerUpc();
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
        this.network = new Network();



        // setting parameters
        // omega
        if (this.crData.getUtilityModelParams().isOmegaRandom()) {
            this.crData.getUtilityModelParams().setCurrOmega(ThreadLocalRandom.current().nextDouble(
                    this.crData.getUtilityModelParams().getOmegaRandomMin(),
                    this.crData.getUtilityModelParams().getOmegaRandomMax()));
        }
        // delta
        if (this.crData.getUtilityModelParams().isDeltaRandom()) {
            this.crData.getUtilityModelParams().setCurrDelta(ThreadLocalRandom.current().nextDouble(
                    this.crData.getUtilityModelParams().getDeltaRandomMin(),
                    this.crData.getUtilityModelParams().getDeltaRandomMax()));
        }
        // c
        if (this.crData.getUtilityModelParams().isCRandom()) {
            this.crData.getUtilityModelParams().setCurrC(ThreadLocalRandom.current().nextDouble(
                    this.crData.getUtilityModelParams().getCRandomMin(),
                    this.crData.getUtilityModelParams().getCRandomMax()));
        }
        // N
        if (this.crData.getUtilityModelParams().isNRandom()) {
            this.crData.getUtilityModelParams().setCurrN(ThreadLocalRandom.current().nextInt(
                    this.crData.getUtilityModelParams().getNRandomMin(),
                    this.crData.getUtilityModelParams().getNRandomMax()));
        }
        // iota
        if (this.crData.getUtilityModelParams().isIotaRandom()) {
            this.crData.getUtilityModelParams().setCurrIota(ThreadLocalRandom.current().nextBoolean());
        }
        // phi
        if (this.crData.getUtilityModelParams().isPhiRandom()) {
            this.crData.getUtilityModelParams().setCurrPhi(ThreadLocalRandom.current().nextDouble(
                    this.crData.getUtilityModelParams().getPhiRandomMin(),
                    this.crData.getUtilityModelParams().getPhiRandomMax()));
        }

        // create utility
        UtilityFunction uf = new CarayolRoux(
                this.crData.getUtilityModelParams().getCurrOmega(),
                this.crData.getUtilityModelParams().getCurrDelta(),
                this.crData.getUtilityModelParams().getCurrC());

        // create disease specs
        DiseaseSpecs ds = new DiseaseSpecs(
                DiseaseType.SIR, 10, 10, 0.1, 1.0);

        // add agents
        for (int i = 0; i < this.crData.getUtilityModelParams().getCurrN(); i++) {
            network.addAgent(uf, ds, 0, 0, this.crData.getUtilityModelParams().getCurrPhi());
        }
        this.crData.setAgents(new LinkedList<Agent>(network.getAgents()));

        // create full network if required
        if (!this.crData.getUtilityModelParams().isCurrIota()) {
            this.network.createFullNetwork();
        }

        // set up general stats
        this.crData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);
        // create simulation
        this.simulation = new Simulation(network);
        // simulate
        this.simulation.simulate();
        // save data
        this.crData.setNetStatsCurrent(new NetworkStats(this.network));

    }


    /**
     * Analyzes data.
     */
    private void anaylzeData() {
        try {
            // preparation of R-scripts
            Path srcAnalysis = Paths.get(PropertiesHandler.getInstance().getRAnalysisCarayolRouxTemplatePath());
            String dstAnalysisPath = FULL_DATA_EXPORT_PATH + "carayolroux.R";
            Path dstAnalysis = Paths.get(dstAnalysisPath);
            Files.copy(srcAnalysis, dstAnalysis, StandardCopyOption.REPLACE_EXISTING);

            // invocation of R-script
            ProcessBuilder pb = new ProcessBuilder(PropertiesHandler.getInstance().getRscriptPath(),
                    dstAnalysisPath, FULL_DATA_EXPORT_PATH);
            logger.info("Starting analysis of simulated data. "
                    + "Invoking R-script: "
                    + pb.command().toString());
            Process p = pb.start();

            // status messages of R-script
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            // wait for analysis to finish (blocking)
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                logger.info("Analysis finished successfully.");
            } else {
                logger.error("Analysis finished with error code: " + exitCode);
            }

        } catch (IOException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
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
