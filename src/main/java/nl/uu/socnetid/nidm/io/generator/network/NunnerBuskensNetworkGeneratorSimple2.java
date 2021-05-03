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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensNetworkSummaryWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.io.network.NetworkFileWriter;
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
public class NunnerBuskensNetworkGeneratorSimple2 extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensNetworkGeneratorSimple2.class);

    // network
    private Network network;

    // simulation
    private Simulation simulation;

    private int exports;

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
    public NunnerBuskensNetworkGeneratorSimple2(String rootExportPath) throws IOException {
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
        this.dgData.getSimStats().setUpc(1);
        this.dgData.getUtilityModelParams().setCurrN(60);

        this.dgData.getUtilityModelParams().setCurrB1(1.0);
        this.dgData.getUtilityModelParams().setCurrB2(0.5);
        this.dgData.getUtilityModelParams().setCurrC1(0.2);
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

        NunnerBuskensParameters umps = this.dgData.getUtilityModelParams();

        // clustering
        double[] alphas = {0.267, 0.333};
        for (double alpha : alphas) {
            umps.setCurrAlpha(alpha);
            umps.setCurrPhi(0.2);

            umps.setCurrPsi(0.4);
            umps.setCurrXi(0.2);

//            if (alpha < 0.5) {
//                umps.setCurrPsi(0);
//                umps.setCurrXi(0);
//            } else {
//                umps.setCurrPsi(0.2);
//                umps.setCurrXi(0.65);
//            }

            // degrees
            int[] targetMeans = {6};
            for (double targetMean : targetMeans) {
                umps.setCurrC2(NunnerBuskens.getC2FromAvDegree(umps.getCurrB1(), umps.getCurrC1(), targetMean));

                this.exports = 1;

                while (exports <= 10) {

                    this.dgData.getSimStats().setSimIt(exports);
                    this.dgData.getSimStats().resetCurrRound();

                    // CREATE NETWORK
                    this.network = new Network("Network of the infectious kind");
                    this.network.resetAgents();
                    // disease specs
                    DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);
                    // utility function
                    UtilityFunction uf = new NunnerBuskens(umps.getCurrB1(), umps.getCurrB2(), umps.getCurrAlpha(),
                            umps.getCurrC1(), umps.getCurrC2());
                    // agents
                    for (int j = 0; j < umps.getCurrN(); j++) {
                        network.addAgent(uf, ds, 1.0, 1.0, umps.getCurrPhi(), umps.getCurrOmega(), umps.getCurrPsi(),
                                umps.getCurrXi(), AgeStructure.getInstance().getRandomAge(), false,
                                Professions.getInstance().getRandomProfession(), false, false);
                    }

                    // simulate
                    this.simulation = new Simulation(network);
                    this.simulation.addSimulationListener(this);
                    this.simulation.simulateUntilStable(50);
                }

                this.dgData.getSimStats().incUpc();
            }
        }
        finalizeDataExportFiles();
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
    private void exportNetworks() {
        NetworkFileWriter elWriter = new NetworkFileWriter(getExportPath(),
                this.dgData.getSimStats().getUid() + ".el",
                new EdgeListWriter(),
                this.network);
        elWriter.write();

        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.writeStaticNetwork(this.network, getExportPath() + this.dgData.getSimStats().getUid() + ".gexf");

        DGSWriter dgsWriter = new DGSWriter();
        String fileName = getExportPath() + this.dgData.getSimStats().getUid() + ".dgs";
        dgsWriter.writeNetwork(network, fileName);
        this.dgData.setExportFileName(fileName);
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
    public void notifyRoundFinished(Simulation simulation) {}

    @Override
    public void notifySimulationStarted(Simulation simulation) {}

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {}

    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.network.getAvDegree() >= 5.93 && this.network.getAvDegree() < 5.94 &&
                this.network.isStable()) {
//                ((this.dgData.getUtilityModelParams().getCurrAlpha() <= 0.1 && this.network.getAvClustering() <= 0.15) ||
//                ((this.dgData.getUtilityModelParams().getCurrAlpha() == 0.133 && this.network.getAvClustering() <= 0.14) ||
//                        (this.dgData.getUtilityModelParams().getCurrAlpha() == 0.2 && this.network.getAvClustering() <= 0.21) ||
//                        (this.dgData.getUtilityModelParams().getCurrAlpha() == 0.333 && this.network.getAvClustering() <= 0.35) ||
//                        (this.dgData.getUtilityModelParams().getCurrAlpha() >= 0.6 && this.network.getAvClustering() >= 0.6))) {

            exportNetworks();
            amendSummary();
            this.exports++;
        } else {
            logger.debug("Simulation finished unsuccesfully");
        }
    }

}
