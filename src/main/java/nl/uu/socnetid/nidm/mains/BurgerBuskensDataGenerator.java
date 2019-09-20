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

import java.io.File;
import java.io.IOException;
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
import nl.uu.socnetid.nidm.data.BurgerBuskensDataGeneratorData;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.generator.BurgerBuskensSimulationSummaryWriter;
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
 * TODO generalize, as this class is very similar to {@link CidmDataGenerator}
 */
public class BurgerBuskensDataGenerator {

    // logger
    private static final Logger logger = Logger.getLogger(BurgerBuskensDataGenerator.class);

    // stats
    private BurgerBuskensDataGeneratorData dgData = new BurgerBuskensDataGeneratorData();

    // network
    private Network network;

    // simulation
    private Simulation simulation;

    // data export
    private static final String FULL_DATA_EXPORT_PATH = PropertiesHandler.getInstance().getDataExportPath() +
            (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";
    private BurgerBuskensSimulationSummaryWriter ssWriter;


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

        BurgerBuskensDataGenerator dataGenerator = new BurgerBuskensDataGenerator();
        dataGenerator.generateData();
    }


    /**
     * Constructor.
     *
     * @throws IOException
     *          if the export file(s) exist(s) but is a directory rather
     *          than a regular file, do(es) not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public BurgerBuskensDataGenerator() throws IOException {
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
            this.ssWriter = new BurgerBuskensSimulationSummaryWriter(FULL_DATA_EXPORT_PATH + "simulation-summary.csv", this.dgData);
        }
    }


    /**
     * Generates data.
     */
    public void generateData() {

        // benefits and costs of triadic closure
        double[] b2s = this.dgData.getUtilityModelParams().getB2s();
        double[] c3s = this.dgData.getUtilityModelParams().getC3s();
        if (this.dgData.getUtilityModelParams().isB2c3Random()) {
            b2s = new double[1];
            c3s = new double[1];
        }

        // unique parameter combinations
        int upcs =
                this.dgData.getUtilityModelParams().getB1s().length *
                b2s.length *
                this.dgData.getUtilityModelParams().getC1s().length *
                this.dgData.getUtilityModelParams().getC2s().length *
                c3s.length *
                this.dgData.getUtilityModelParams().getNs().length *
                this.dgData.getUtilityModelParams().getIotas().length *
                this.dgData.getUtilityModelParams().getPhis().length;

        // loop over all possible parameter combinations
        for (double b1 : this.dgData.getUtilityModelParams().getB1s()) {
            this.dgData.getUtilityModelParams().setCurrB1(b1);
            for (double b2 : b2s) {
                this.dgData.getUtilityModelParams().setCurrB2(b2);
                for (double c1 : this.dgData.getUtilityModelParams().getC1s()) {
                    this.dgData.getUtilityModelParams().setCurrC1(c1);
                    for (double c2 : this.dgData.getUtilityModelParams().getC2s()) {
                        this.dgData.getUtilityModelParams().setCurrC2(c2);
                        for (double c3 : c3s) {
                            this.dgData.getUtilityModelParams().setCurrC3(c3);
                            for (int N : this.dgData.getUtilityModelParams().getNs()) {
                                this.dgData.getUtilityModelParams().setCurrN(N);
                                for (boolean iota : this.dgData.getUtilityModelParams().getIotas()) {
                                    this.dgData.getUtilityModelParams().setCurrIota(iota);
                                    for (double phi : this.dgData.getUtilityModelParams().getPhis()) {
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

        // finish data generation
        finalizeDataExportFiles();
    }


    /**
     * Performs a single simulation based on parameters set in dgData
     */
    private void performSingleSimulation() {

        // create network
        this.network = new Network();

        // benefits/costs for triadic closures
        double b2 = this.dgData.getUtilityModelParams().getCurrB2();
        double c3 = this.dgData.getUtilityModelParams().getCurrC3();
        if (this.dgData.getUtilityModelParams().isB2c3Random()) {
            b2 = ThreadLocalRandom.current().nextDouble(0,1);
            this.dgData.getUtilityModelParams().setCurrB2(b2);
            c3 = ThreadLocalRandom.current().nextDouble(0,1);
            this.dgData.getUtilityModelParams().setCurrC3(c3);
        }

        // create utility
        UtilityFunction uf = new BurgerBuskens(
                this.dgData.getUtilityModelParams().getCurrB1(),
                b2,
                this.dgData.getUtilityModelParams().getCurrC1(),
                this.dgData.getUtilityModelParams().getCurrC2(),
                c3);

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
            this.network.createFullNetwork();
        }

        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);
        // create simulation
        this.simulation = new Simulation(network);
        // simulate
        this.simulation.simulate();
        // save data
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network));

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
