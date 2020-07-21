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
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentListener;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensAgentDetailsWriter;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensSimulationSummaryWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStats;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class NunnerBuskensDataGenerator extends AbstractDataGenerator implements AgentListener, SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensDataGenerator.class);

    // network
    private Network network;
    private double tiesBrokenWithInfectionPresent;

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<NunnerBuskensParameters> dgData;
    private NunnerBuskensSimulationSummaryWriter ssWriter;
    private NunnerBuskensRoundSummaryWriter rsWriter;
    private NunnerBuskensAgentDetailsWriter adWriter;
    private GEXFWriter gexfWriter;


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
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter = new NunnerBuskensRoundSummaryWriter(getExportPath() + "round-summary.csv", this.dgData);
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.adWriter = new NunnerBuskensAgentDetailsWriter(getExportPath() + "agent-details.csv", this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        boolean[] epStatics = this.dgData.getUtilityModelParams().isEpStaticRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getEpStatics();
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
        double[] sigmas = this.dgData.getUtilityModelParams().isSigmaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getSigmas();
        double[] gammas = this.dgData.getUtilityModelParams().isGammaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getGammas();
        int[] taus = this.dgData.getUtilityModelParams().isTauRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getTaus();

        double[] rSigmas = this.dgData.getUtilityModelParams().isRSigmaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getRSigmas();
        boolean[] rSigmaRandomHomogeneouses = this.dgData.getUtilityModelParams().isRSigmaRandom() ?
                this.dgData.getUtilityModelParams().getRSigmaRandomHomogeneous() : new boolean[1];
        double[] rPis = this.dgData.getUtilityModelParams().isRPiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getRPis();
        boolean[] rPiRandomHomogeneouses = this.dgData.getUtilityModelParams().isRPiRandom() ?
                this.dgData.getUtilityModelParams().getRPiRandomHomogeneous() : new boolean[1];

        if (this.dgData.getUtilityModelParams().isRsEqual()) {
            rPis = new double[1];
            rPiRandomHomogeneouses = new boolean[1];
        }


        int[] Ns = this.dgData.getUtilityModelParams().isNRandom() ?
                new int[1] : this.dgData.getUtilityModelParams().getNs();
        boolean[] iotas = this.dgData.getUtilityModelParams().isIotaRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getIotas();
        double[] phis = this.dgData.getUtilityModelParams().isPhiRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getPhis();
        double[] omegas = this.dgData.getUtilityModelParams().isOmegaRandom() ?
                new double[1] : this.dgData.getUtilityModelParams().getOmegas();
        boolean[] omegaShuffles = this.dgData.getUtilityModelParams().isOmegaShuffleRandom() ?
                new boolean[1] : this.dgData.getUtilityModelParams().getOmegaShuffles();

        // unique parameter combinations
        int upcs =
                epStatics.length *
                b1s.length *
                b2s.length *
                alphas.length *
                c1s.length *
                c2s.length *
                sigmas.length *
                gammas.length *
                taus.length *
                rSigmas.length *
                rSigmaRandomHomogeneouses.length *
                rPis.length *
                rPiRandomHomogeneouses.length *
                Ns.length *
                iotas.length *
                phis.length *
                omegas.length *
                omegaShuffles.length;

        // loop over all possible parameter combinations
        for (boolean epStatic : epStatics) {
            this.dgData.getUtilityModelParams().setCurrEpStatic(epStatic);
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
                                for (double sigma : sigmas) {
                                    this.dgData.getUtilityModelParams().setCurrSigma(sigma);
                                    for (double gamma : gammas) {
                                        this.dgData.getUtilityModelParams().setCurrGamma(gamma);
                                        for (int tau : taus) {
                                            this.dgData.getUtilityModelParams().setCurrTau(tau);
                                            for (double rSigma : rSigmas) {
                                                this.dgData.getUtilityModelParams().setCurrRSigma(rSigma);
                                                for (boolean rSigmaRandomHomogeneous : rSigmaRandomHomogeneouses) {
                                                    this.dgData.getUtilityModelParams().setCurrRSigmaRandomHomogeneous(rSigmaRandomHomogeneous);
                                                    for (double rPi : rPis) {
                                                        this.dgData.getUtilityModelParams().setCurrRPi(rPi);
                                                        for (boolean rPiRandomHomogeneous : rPiRandomHomogeneouses) {
                                                            this.dgData.getUtilityModelParams().setCurrRPiRandomHomogeneous(rPiRandomHomogeneous);
                                                            for (int N : Ns) {
                                                                this.dgData.getUtilityModelParams().setCurrN(N);
                                                                for (boolean iota : iotas) {
                                                                    this.dgData.getUtilityModelParams().setCurrIota(iota);
                                                                    for (double phi : phis) {
                                                                        this.dgData.getUtilityModelParams().setCurrPhi(phi);
                                                                        for (double omega : omegas) {
                                                                            this.dgData.getUtilityModelParams().setCurrOmega(omega);
                                                                            for (boolean omegaShuffle : omegaShuffles) {
                                                                                this.dgData.getUtilityModelParams().setCurrOmegaShuffle(omegaShuffle);

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

                                                                                    logger.debug("Simulation " +
                                                                                            this.dgData.getSimStats().getSimPerUpc() +
                                                                                            "/" +
                                                                                            this.dgData.getUtilityModelParams().getSimsPerParameterCombination() +
                                                                                            " of parameter combination " +
                                                                                            this.dgData.getSimStats().getUpc() +
                                                                                            "/" +
                                                                                            upcs +
                                                                                            " finished.");

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

        // setting parameters
        // epStatic
        if (this.dgData.getUtilityModelParams().isEpStaticRandom()) {
            this.dgData.getUtilityModelParams().setCurrEpStatic(ThreadLocalRandom.current().nextBoolean());
        }
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
        // sigma
        if (this.dgData.getUtilityModelParams().isSigmaRandom()) {
            this.dgData.getUtilityModelParams().setCurrSigma(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getSigmaRandomMin(),
                    this.dgData.getUtilityModelParams().getSigmaRandomMax()));
        }
        // gamma
        if (this.dgData.getUtilityModelParams().isGammaRandom()) {
            this.dgData.getUtilityModelParams().setCurrGamma(ThreadLocalRandom.current().nextDouble(
                    this.dgData.getUtilityModelParams().getGammaRandomMin(),
                    this.dgData.getUtilityModelParams().getGammaRandomMax()));
        }
        // tau
        if (this.dgData.getUtilityModelParams().isTauRandom()) {
            this.dgData.getUtilityModelParams().setCurrTau(ThreadLocalRandom.current().nextInt(
                    this.dgData.getUtilityModelParams().getTauRandomMin(),
                    this.dgData.getUtilityModelParams().getTauRandomMax()));
        }
        // N
        if (this.dgData.getUtilityModelParams().isNRandom()) {
            this.dgData.getUtilityModelParams().setCurrN(ThreadLocalRandom.current().nextInt(
                    this.dgData.getUtilityModelParams().getNRandomMin(),
                    this.dgData.getUtilityModelParams().getNRandomMax()));
        }
        // rSigma
        if (this.dgData.getUtilityModelParams().isRSigmaRandom()) {
            if (this.dgData.getUtilityModelParams().isCurrRSigmaRandomHomogeneous()) {
                this.dgData.getUtilityModelParams().setCurrRSigma(ThreadLocalRandom.current().nextDouble(
                        this.dgData.getUtilityModelParams().getRSigmaRandomMin(),
                        this.dgData.getUtilityModelParams().getRSigmaRandomMax()));
            } else {
                int currN = this.dgData.getUtilityModelParams().getCurrN();
                double[] rSigmas = new double[currN];
                for (int i = 0; i < currN; i++) {
                    rSigmas[i] = ThreadLocalRandom.current().nextDouble(
                            this.dgData.getUtilityModelParams().getRSigmaRandomMin(),
                            this.dgData.getUtilityModelParams().getRSigmaRandomMax());
                }
                this.dgData.getUtilityModelParams().setCurrRSigmas(rSigmas);
            }
        }
        // rPi
        if (this.dgData.getUtilityModelParams().isRsEqual()) {
            this.dgData.getUtilityModelParams().setRPiRandom(
                    this.dgData.getUtilityModelParams().isRSigmaRandom());
            this.dgData.getUtilityModelParams().setCurrRPiRandomHomogeneous(
                    this.dgData.getUtilityModelParams().isCurrRSigmaRandomHomogeneous());
            this.dgData.getUtilityModelParams().setCurrRPi(this.dgData.getUtilityModelParams().getCurrRSigma());
            this.dgData.getUtilityModelParams().setCurrRPis(this.dgData.getUtilityModelParams().getCurrRSigmas());
        } else if (this.dgData.getUtilityModelParams().isRPiRandom()) {
            if (this.dgData.getUtilityModelParams().isCurrRPiRandomHomogeneous()) {
                this.dgData.getUtilityModelParams().setCurrRPi(ThreadLocalRandom.current().nextDouble(
                        this.dgData.getUtilityModelParams().getRSigmaRandomMin(),
                        this.dgData.getUtilityModelParams().getRSigmaRandomMax()));
            } else {
                int currN = this.dgData.getUtilityModelParams().getCurrN();
                double[] rPis = new double[currN];
                for (int i = 0; i < currN; i++) {
                    rPis[i] = ThreadLocalRandom.current().nextDouble(
                            this.dgData.getUtilityModelParams().getRSigmaRandomMin(),
                            this.dgData.getUtilityModelParams().getRSigmaRandomMax());
                }
                this.dgData.getUtilityModelParams().setCurrRPis(rPis);
            }
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
        // omegaShuffle
        if (this.dgData.getUtilityModelParams().isOmegaShuffleRandom()) {
            this.dgData.getUtilityModelParams().setCurrOmegaShuffle(ThreadLocalRandom.current().nextBoolean());
        }

        // begin: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter = new GEXFWriter();
            this.dgData.setGexfExportFile(getExportPath() + this.dgData.getSimStats().getUid() + ".gexf");
            gexfWriter.startRecording(network, this.dgData.getGexfExportFile());
        }

        // create utility
        UtilityFunction uf = new NunnerBuskens(
                this.dgData.getUtilityModelParams().getCurrB1(),
                this.dgData.getUtilityModelParams().getCurrB2(),
                this.dgData.getUtilityModelParams().getCurrAlpha(),
                this.dgData.getUtilityModelParams().getCurrC1(),
                this.dgData.getUtilityModelParams().getCurrC2());

        // add agents - with RPi == RSigma!!!
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR,
                this.dgData.getUtilityModelParams().getCurrTau(),
                this.dgData.getUtilityModelParams().getCurrSigma(),
                this.dgData.getUtilityModelParams().getCurrGamma(),
                0);
        for (int i = 0; i < this.dgData.getUtilityModelParams().getCurrN(); i++) {
            double rSigma = this.dgData.getUtilityModelParams().getCurrRSigma();
            if (this.dgData.getUtilityModelParams().isRSigmaRandom() &&
                    !this.dgData.getUtilityModelParams().isCurrRSigmaRandomHomogeneous()) {
                rSigma = this.dgData.getUtilityModelParams().getCurrRSigmas()[i];
            }
            double rPi = this.dgData.getUtilityModelParams().getCurrRPi();
            if (this.dgData.getUtilityModelParams().isRPiRandom() &&
                    !this.dgData.getUtilityModelParams().isCurrRPiRandomHomogeneous()) {
                rPi = this.dgData.getUtilityModelParams().getCurrRPis()[i];
            }
            Agent agent = network.addAgent(uf, ds,
                    rSigma,
                    rPi,
                    this.dgData.getUtilityModelParams().getCurrPhi(),
                    this.dgData.getUtilityModelParams().getCurrOmega(),
                    this.dgData.getUtilityModelParams().isCurrOmegaShuffle(),
                    this.dgData.getUtilityModelParams().getCurrPsi(),
                    this.dgData.getUtilityModelParams().getCurrXi(),
                    // TODO make age optional
                    AgeStructure.getInstance().getRandomAge(),
                    false);
            agent.addAgentListener(this);
        }
        this.dgData.setAgents(new LinkedList<Agent>(network.getAgents()));

        // create full network if required
        if (!this.dgData.getUtilityModelParams().isCurrIota()) {
            network.createFullNetwork();
        }

        // PRE_EPIDEMIC STAGE
        // set up general stats
        this.dgData.getSimStats().setSimStage(SimulationStage.PRE_EPIDEMIC);
        // create simulation
        this.simulation = new Simulation(network, this.dgData.getUtilityModelParams().isCurrEpStatic());
        this.simulation.addSimulationListener(this);
        // simulate
        simulation.simulateUntilStable(this.dgData.getUtilityModelParams().getZeta());
        // save data of last round of pre-epidemic stage
        this.dgData.setNetStatsPre(new NetworkStats(this.network));
        this.dgData.getNetStatsPre().setTiesBrokenWithInfectionPresent(0.0);
        // write agent detail data if necessary
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.adWriter.writeCurrentData();
        }

        // EPIDEMIC AND POST-EPIDEMIC STAGES
        this.tiesBrokenWithInfectionPresent = 0.0;
        Agent indexCase = this.network.infectRandomAgent(ds);
        this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
        // save index case properties of pre-epidemic stage
        this.dgData.setIndexCaseStats(new AgentStats(indexCase));
        this.dgData.getSimStats().setRoundStartInfection(this.simulation.getRounds());
        // simulate
        this.simulation.simulateUntilStable(this.dgData.getUtilityModelParams().getEpsilon());
        // save data of last round of post-epidemic stage
        this.dgData.setNetStatsPost(new NetworkStats(this.network));
        this.dgData.getNetStatsPost().setTiesBrokenWithInfectionPresent(this.tiesBrokenWithInfectionPresent);

        // end: GEXF export
        if (PropertiesHandler.getInstance().isExportGexf()) {
            this.gexfWriter.stopRecording();
        }
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
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                this.rsWriter.flush();
                this.rsWriter.close();
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.adWriter.flush();
                this.adWriter.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeAdded(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object)
     */
    @Override
    public void notifyAttributeAdded(Agent agent, String attribute, Object value) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeChanged(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void notifyAttributeChanged(Agent agent, String attribute, Object oldValue, Object newValue) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyAttributeRemoved(
     * nl.uu.socnetid.nidm.agents.Agent, java.lang.String)
     */
    @Override
    public void notifyAttributeRemoved(Agent agent, String attribute) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyConnectionAdded(
     * org.graphstream.graph.Edge, nl.uu.socnetid.nidm.agents.Agent, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyConnectionAdded(Edge edge, Agent agent1, Agent agent2) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyConnectionRemoved(
     * nl.uu.socnetid.nidm.agents.Agent, org.graphstream.graph.Edge)
     */
    @Override
    public void notifyConnectionRemoved(Agent agent, Edge edge) {
        if (this.dgData.getSimStats().getSimStage() == SimulationStage.ACTIVE_EPIDEMIC) {
            this.tiesBrokenWithInfectionPresent++;
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationStarted(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationStarted(Simulation simulation) { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyRoundFinished(Agent agent) {
        this.dgData.getSimStats().setRounds(this.simulation.getRounds());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (PropertiesHandler.getInstance().isExportSummaryEachRound() || PropertiesHandler.getInstance().isExportAgentDetails()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
        }
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter.writeCurrentData();
        }
        if (PropertiesHandler.getInstance().isExportAgentDetails()) {
            this.adWriter.writeCurrentData();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.dgData.getSimStats().setRoundLastInfection(simulation.getRounds() -
                this.dgData.getSimStats().getRoundStartInfection());
        this.dgData.getSimStats().setSimStage(SimulationStage.POST_EPIDEMIC);
        // TODO improve
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            this.adWriter.writeCurrentData();
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.dgData.getSimStats().getSimStage() == SimulationStage.POST_EPIDEMIC) {
            this.dgData.getSimStats().setSimStage(SimulationStage.FINISHED);
            if (PropertiesHandler.getInstance().isExportSummaryEachRound() ||
                    PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.dgData.setNetStatsCurrent(new NetworkStats(this.network));
            }
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                this.rsWriter.writeCurrentData();
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.adWriter.writeCurrentData();
            }
        }
    }

}
