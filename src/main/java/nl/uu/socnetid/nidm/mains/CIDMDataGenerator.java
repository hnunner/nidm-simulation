package nl.uu.socnetid.nidm.mains;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.agents.AgentListener;
import nl.uu.socnetid.nidm.data.LogParameters;
import nl.uu.socnetid.nidm.data.LogProperties;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.CSVUtils;
import nl.uu.socnetid.nidm.io.GEXFWriter;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.DataGeneratorStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utilities.CIDM;
import nl.uu.socnetid.nidm.utilities.CIDMParameters;
import nl.uu.socnetid.nidm.utilities.UtilityFunction;


/**
 * @author Hendrik Nunner
 */
public class CIDMDataGenerator implements AgentListener, SimulationListener {

    // logger
    private static final Logger logger = Logger.getLogger(CIDMDataGenerator.class);

    // parameters
    private CIDMParameters cidmParams = PropertiesHandler.getInstance().getCidmParameters();
    private boolean isRsEqual = PropertiesHandler.getInstance().getCidmParameters().isRsEqual();

    // stats
    DataGeneratorStats dgStats = new DataGeneratorStats();

    // data export
    private String gexfExportFile = "NA";
    private GEXFWriter gexfWriter;
    private FileWriter simulationSummaryCSVWriter;
    private FileWriter roundSummaryCSVWriter;
    private FileWriter agentDetailsCSVWriter;
    private static final String FULL_DATA_EXPORT_PATH = PropertiesHandler.getInstance().getDataExportPath() +
            (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date()) + "/";


    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {
        CIDMDataGenerator dataGenerator = new CIDMDataGenerator();
        dataGenerator.generateData();
    }


    /**
     * Constructor.
     */
    public CIDMDataGenerator() {
        // initializations
        // create export directory if it does not exist
        File directory = new File(FULL_DATA_EXPORT_PATH);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }


    /**
     * Generates data.
     */
    public void generateData() {

        // risk perceptions the same with regards to susceptibility (pi) and diseases severity (sigma)?
        double[] rSigmas = this.cidmParams.getRSigmas();
        double[] rPis = this.cidmParams.getRPis();
        if (this.isRsEqual) {
            rPis = new double[1];
        }

        // unique parameter combinations
        int upcs = this.cidmParams.getAlphas().length *
                this.cidmParams.getKappas().length *
                this.cidmParams.getBetas().length *
                this.cidmParams.getLamdas().length *
                this.cidmParams.getCs().length *
                this.cidmParams.getMus().length *
                this.cidmParams.getSigmas().length *
                this.cidmParams.getGammas().length *
                rSigmas.length *
                rPis.length *
                this.cidmParams.getNs().length *
                this.cidmParams.getIotas().length *
                this.cidmParams.getPhis().length *
                this.cidmParams.getTaus().length;

        // initialization of data export files
        initDataExportFiles();

        // loop over all possible parameter combinations
        for (double alpha : this.cidmParams.getAlphas()) {
            for (double kappa : this.cidmParams.getKappas()) {
                for (double beta : this.cidmParams.getBetas()) {
                    for (double lamda : this.cidmParams.getLamdas()) {
                        for (double c : this.cidmParams.getCs()) {
                            for (double mu : this.cidmParams.getMus()) {
                                for (double s : this.cidmParams.getSigmas()) {
                                    for (double gamma : this.cidmParams.getGammas()) {
                                        for (double rSigma : rSigmas) {
                                            for (double rPi : rPis) {
                                                for (int N : this.cidmParams.getNs()) {
                                                    for (boolean iota : this.cidmParams.getIotas()) {
                                                        for (double phi : this.cidmParams.getPhis()) {
                                                            for (int tau : this.cidmParams.getTaus()) {

                                                                // setting correct values for r_sigma and r_pi
                                                                if (this.isRsEqual) {
                                                                    rPi = rSigma;
                                                                }

                                                                this.dgStats.setIota(iota);
                                                                this.dgStats.incUpc();

                                                                logger.info("Starting to compute "
                                                                        + this.cidmParams.getSimsPerParameterCombination()
                                                                        + " simulations for parameter combination: "
                                                                        + this.dgStats.getUpc() + " / "
                                                                        + upcs);

                                                                // multiple simulations for same parameter combination
                                                                this.dgStats.setSimPerUpc(1);
                                                                while (this.dgStats.getSimPerUpc()
                                                                        <= this.cidmParams.getSimsPerParameterCombination()) {

                                                                    // INITIALIZATIONS
                                                                    // uid = "upc-sim"
                                                                    this.dgStats.setUid(String.valueOf(this.dgStats.getUpc())
                                                                            + "-" + String.valueOf(
                                                                                    this.dgStats.getSimPerUpc()));

                                                                    // create network
                                                                    Network network = new Network();

                                                                    // begin: GEXF export
                                                                    if (PropertiesHandler.getInstance().isExportGexf()) {
                                                                        this.gexfWriter = new GEXFWriter();
                                                                        this.gexfExportFile = FULL_DATA_EXPORT_PATH
                                                                                + this.dgStats.getUid() + ".gexf";
                                                                        gexfWriter.startRecording(network, this.gexfExportFile);
                                                                    }

                                                                    // create utility and disease specs
                                                                    UtilityFunction uf = new CIDM(alpha, kappa, beta, lamda, c);
                                                                    this.dgStats.setDiseaseSpecs(
                                                                            new DiseaseSpecs(DiseaseType.SIR, tau, s,
                                                                                    gamma, mu));

                                                                    // add agents - with RPi == RSigma!!!
                                                                    for (int i = 0; i < N; i++) {
                                                                        Agent agent = network.addAgent(
                                                                                uf, this.dgStats.getDiseaseSpecs(),
                                                                                rSigma, rPi, phi);
                                                                        agent.addAgentListener(this);
                                                                    }

                                                                    // create full network if required
                                                                    if (!iota) {
                                                                        network.createFullNetwork();
                                                                    }

                                                                    // SIMULATION
                                                                    // PRE_EPIDEMIC STAGE
                                                                    this.dgStats.setSimStage(SimulationStage.PRE_EPIDEMIC);
                                                                    this.dgStats.setTiesBrokenWithInfectionPresent(false);
                                                                    Simulation simulation = new Simulation(network);
                                                                    simulation.addSimulationListener(this);
                                                                    // simulate
                                                                    simulation.simulate(this.cidmParams.getZeta());

                                                                    // save data of last round of pre-epidemic stage
                                                                    this.dgStats.setDensityPre(network.getDensity());
                                                                    this.dgStats.setAvDegreePre(network.getAvDegree());
                                                                    this.dgStats.setAvDegree2Pre(network.getAvDegree2());
                                                                    this.dgStats.setAvClosenessPre(network.getAvCloseness());
                                                                    this.dgStats.setAvClusteringPre(network.getAvClustering());
                                                                    this.dgStats.setAvUtility(network.getAvUtility());
                                                                    this.dgStats.setAvBenefitDistance1(
                                                                            network.getAvBenefitDistance1());
                                                                    this.dgStats.setAvBenefitDistance2(
                                                                            network.getAvBenefitDistance2());
                                                                    this.dgStats.setAvCostsDistance1(
                                                                            network.getAvCostsDistance1());
                                                                    this.dgStats.setAvCostsDisease(network.getAvCostsDisease());

                                                                    // log agent detail data if necessary
                                                                    if (!PropertiesHandler.getInstance().
                                                                            isExportAgentDetails() &&
                                                                            PropertiesHandler.getInstance().
                                                                            isExportAgentDetailsReduced()) {
                                                                        logAgentDetails(simulation);
                                                                    }

                                                                    // EPIDEMIC AND POST-EPIDEMIC STAGES
                                                                    Agent indexCase = network.infectRandomAgent(
                                                                            this.dgStats.getDiseaseSpecs());
                                                                    this.dgStats.setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
                                                                    // save index case properties of pre-epidemic stage
                                                                    this.dgStats.setIndexDegree1(indexCase.getDegree());
                                                                    this.dgStats.setIndexDegree2(
                                                                            indexCase.getSecondOrderDegree());
                                                                    this.dgStats.setIndexCloseness(indexCase.getCloseness());
                                                                    this.dgStats.setIndexClustering(indexCase.getClustering());
                                                                    this.dgStats.setIndexUtility(
                                                                            indexCase.getUtility().getOverallUtility());
                                                                    this.dgStats.setIndexBenefit1(
                                                                            indexCase.getUtility().
                                                                            getBenefitDirectConnections());
                                                                    this.dgStats.setIndexBenefit2(
                                                                            indexCase.getUtility().
                                                                            getBenefitIndirectConnections());
                                                                    this.dgStats.setIndexCosts1(
                                                                            indexCase.getUtility().
                                                                            getCostsDirectConnections());
                                                                    this.dgStats.setIndexCostsDisease(
                                                                            indexCase.getUtility().getEffectOfDisease());
                                                                    this.dgStats.setRoundStartInfection(simulation.getRounds());
                                                                    // simulate
                                                                    simulation.simulate(this.cidmParams.getEpsilon());

                                                                    // end: GEXF export
                                                                    if (PropertiesHandler.getInstance().isExportGexf()) {
                                                                        this.gexfWriter.stopRecording();
                                                                    }

                                                                    // log simulation summary
                                                                    if (PropertiesHandler.getInstance().isExportSummary()) {
                                                                        logSimulationSummaryCSV(simulation);
                                                                    }

                                                                    this.dgStats.incSimPerUpc();
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

        // OPTIONAL DATA ANALYSIS
        if (PropertiesHandler.getInstance().isAnalyzeData()) {
            try {
                // invocation of R-script
                ProcessBuilder pb = new ProcessBuilder(PropertiesHandler.getInstance().getRscriptPath(),
                        PropertiesHandler.getInstance().getRAnalysisFilePath(), FULL_DATA_EXPORT_PATH);
                logger.info("Starting analysis of simulated data. "
                        + "Invoking R-script: "
                        + pb.command().toString());
                Process p = pb.start();

                // status messages of R-script
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
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
    }


    /**
     * Initializes the files for data export.
     */
    private void initDataExportFiles() {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            initSimulationSummaryCSV();
        }
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            initRoundSummaryCSV();
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            initAgentDetailsCSV();
        }
    }

    /**
     * Initializes the CSV file for simulation summary data.
     */
    private void initSimulationSummaryCSV() {

        List<String> simulationSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        simulationSummaryCSVCols.add(LogParameters.SIM_UID.toString());
        simulationSummaryCSVCols.add(LogParameters.SIM_UPC.toString());
        simulationSummaryCSVCols.add(LogParameters.SIM_CNT.toString());
        // CIDM
        simulationSummaryCSVCols.add(LogParameters.CIDM_ALPHA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_KAPPA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_BETA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_LAMDA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_C_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_MU_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_SIGMA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_GAMMA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_RS_EQUAL.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_R_SIGMA_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_R_PI_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_NET_SIZE.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_IOTA.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_PHI_AV.toString());
        simulationSummaryCSVCols.add(LogParameters.CIDM_TAU_AV.toString());
        // other exports
        simulationSummaryCSVCols.add(LogParameters.GEXF_FILE.toString());

        // PROPERTIES
        // simulation
        simulationSummaryCSVCols.add(LogProperties.SIM_EPIDEMIC_DURATION.toString());
        // network
        simulationSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_SUSCEPTIBLE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_INFECTED.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_RECOVERED.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_TIES_BROKEN_EPIDEMIC.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_DEGREE_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_DEGREE_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_DEGREE2_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_DEGREE2_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_CLOSENESS_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_CLOSENESS_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_CLUSTERING_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_CLUSTERING_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_UTIL_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_UTIL_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_BENEFIT_DIST1_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_BENEFIT_DIST1_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_BENEFIT_DIST2_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_BENEFIT_DIST2_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_COSTS_DIST1_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_COSTS_DIST1_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_COSTS_DISEASE_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_AV_COSTS_DISEASE_POST.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_DENSITY_PRE.toString());
        simulationSummaryCSVCols.add(LogProperties.NET_DENSITY_POST.toString());
        // index case
        simulationSummaryCSVCols.add(LogProperties.AGENT_DEGREE1.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_DEGREE2.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_CLOSENESS.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_CLUSTERING.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_UTIL.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_BENEFIT_DIST1.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_BENEFIT_DIST2.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_COSTS_DIST1.toString());
        simulationSummaryCSVCols.add(LogProperties.AGENT_COSTS_DISEASE.toString());

        // FILE SYSTEM
        try {
            this.simulationSummaryCSVWriter = new FileWriter(FULL_DATA_EXPORT_PATH + "simulation-summary.csv");
            CSVUtils.writeLine(this.simulationSummaryCSVWriter, simulationSummaryCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the summary data of a single simulation.
     *
     * @param simulation
     *          the simulation to be logged.
     */
    private void logSimulationSummaryCSV(Simulation simulation) {

        Network network = simulation.getNetwork();
        List<String> simulationSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        simulationSummaryCSVCols.add(this.dgStats.getUid());
        simulationSummaryCSVCols.add(String.valueOf(this.dgStats.getUpc()));
        simulationSummaryCSVCols.add(String.valueOf(this.dgStats.getSimPerUpc()));
        // CIDM
        simulationSummaryCSVCols.add(String.valueOf(network.getAvAlpha()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvKappa()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvBeta()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvLamda()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvC()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvMu()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvSigma()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvGamma()));
        simulationSummaryCSVCols.add(String.valueOf(this.isRsEqual ? 1 : 0));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvRSigma()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvRPi()));
        simulationSummaryCSVCols.add(String.valueOf(network.getN()));
        simulationSummaryCSVCols.add(String.valueOf(this.dgStats.isIota() ? 1 : 0));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvPhi()));
        simulationSummaryCSVCols.add(String.valueOf(network.getAvTau()));
        // other exports
        simulationSummaryCSVCols.add(gexfExportFile);

        // PROPERTIES
        // simulation
        simulationSummaryCSVCols.add(Integer.toString(this.dgStats.getRoundLastInfection()));
        // network
        double pct = 100D / network.getN();
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getSusceptibles().size()));
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getInfected().size()));
        simulationSummaryCSVCols.add(String.valueOf(pct * network.getRecovered().size()));
        simulationSummaryCSVCols.add(String.valueOf(this.dgStats.isTiesBrokenWithInfectionPresent() ? 1 : 0));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvDegreePre()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvDegree()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvDegree2Pre()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvDegree2()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvClosenessPre()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCloseness()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvClusteringPre()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvClustering()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvUtility()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvUtility()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvBenefitDistance1()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvBenefitDistance1()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvBenefitDistance2()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvBenefitDistance2()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvCostsDistance1()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCostsDistance1()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getAvCostsDisease()));
        simulationSummaryCSVCols.add(Double.toString(network.getAvCostsDisease()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getDensityPre()));
        simulationSummaryCSVCols.add(Double.toString(network.getDensity()));
        // index case
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexDegree1()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexDegree2()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexCloseness()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexClustering()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexUtility()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexBenefit1()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexBenefit2()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexCosts1()));
        simulationSummaryCSVCols.add(Double.toString(this.dgStats.getIndexCostsDisease()));

        // FILE SYSTEM
        try {
            CSVUtils.writeLine(this.simulationSummaryCSVWriter, simulationSummaryCSVCols);
            this.simulationSummaryCSVWriter.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Initializes the CSV file for round summary data.
     */
    private void initRoundSummaryCSV() {

        List<String> roundSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        roundSummaryCSVCols.add(LogParameters.SIM_UID.toString());
        // roundSummaryCSVCols.add(LogParameters.SIM_UPC.toString());
        // roundSummaryCSVCols.add(LogParameters.SIM_CNT.toString());
        // CIDM - not all parameters to reduce export file size
        // roundSummaryCSVCols.add(LogParameters.CIDM_ALPHA_AV.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_KAPPA_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_BETA_AV.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_LAMDA_AV.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_C_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_MU_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_SIGMA_AV.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_GAMMA_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_RS_EQUAL.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_R_SIGMA_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_R_PI_AV.toString());
        roundSummaryCSVCols.add(LogParameters.CIDM_NET_SIZE.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_IOTA.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_PHI_AV.toString());
        // roundSummaryCSVCols.add(LogParameters.CIDM_TAU_AV.toString());

        // PROPERTIES
        // simulation
        roundSummaryCSVCols.add(LogProperties.SIM_ROUND.toString());
        roundSummaryCSVCols.add(LogProperties.SIM_STAGE.toString());
        // network
        // roundSummaryCSVCols.add(LogProperties.NET_STABLE.toString());
        roundSummaryCSVCols.add(LogProperties.NET_DENSITY.toString());
        roundSummaryCSVCols.add(LogProperties.NET_AV_DEGREE.toString());
        // roundSummaryCSVCols.add(LogProperties.NET_AV_CLUSTERING.toString());
        roundSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_SUSCEPTIBLE.toString());
        roundSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_INFECTED.toString());
        roundSummaryCSVCols.add(LogProperties.NET_PERCENTAGE_RECOVERED.toString());

        // FILE SYSTEM
        try {
            this.roundSummaryCSVWriter = new FileWriter(FULL_DATA_EXPORT_PATH + "round-summary.csv");
            CSVUtils.writeLine(this.roundSummaryCSVWriter, roundSummaryCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the summary data of a single simulation round.
     *
     * @param simulation
     *          the simulation to log the round for.
     */
    private void logRoundSummaryCSV(Simulation simulation) {

        Network network = simulation.getNetwork();
        List<String> roundSummaryCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        roundSummaryCSVCols.add(this.dgStats.getUid());
        // roundSummaryCSVCols.add(String.valueOf(this.dgStats.getUpc()));
        // roundSummaryCSVCols.add(String.valueOf(this.dgStats.getSimPerUpc()));
        // CIDM
        // roundSummaryCSVCols.add(String.valueOf(network.getAvAlpha()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvKappa()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvBeta()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvLamda()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvC()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvMu()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvSigma()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvGamma()));
        roundSummaryCSVCols.add(String.valueOf(this.isRsEqual ? 1 : 0));
        roundSummaryCSVCols.add(String.valueOf(network.getAvRSigma()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvRPi()));
        roundSummaryCSVCols.add(String.valueOf(network.getN()));
        // roundSummaryCSVCols.add(String.valueOf(this.dgStats.isIota() ? 1 : 0));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvPhi()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvTau()));

        // PROPERTIES
        // simulation
        roundSummaryCSVCols.add(String.valueOf(simulation.getRounds()));
        roundSummaryCSVCols.add(String.valueOf(this.dgStats.getSimStage()));
        // network
        // roundSummaryCSVCols.add(String.valueOf(network.isStable()));
        roundSummaryCSVCols.add(String.valueOf(network.getDensity()));
        roundSummaryCSVCols.add(String.valueOf(network.getAvDegree()));
        // roundSummaryCSVCols.add(String.valueOf(network.getAvClustering()));
        double pct = 100D / network.getN();
        roundSummaryCSVCols.add(String.valueOf(pct * network.getSusceptibles().size()));
        roundSummaryCSVCols.add(String.valueOf(pct * network.getInfected().size()));
        roundSummaryCSVCols.add(String.valueOf(pct * network.getRecovered().size()));

        try {
            CSVUtils.writeLine(this.roundSummaryCSVWriter, roundSummaryCSVCols);
            this.roundSummaryCSVWriter.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Initializes the CSV file for agent detail data.
     */
    private void initAgentDetailsCSV() {

        List<String> agentDetailsCSVCols = new LinkedList<String>();

        // PARAMETERS
        // simulation
        agentDetailsCSVCols.add(LogParameters.SIM_UID.toString());
        agentDetailsCSVCols.add(LogParameters.SIM_UPC.toString());
        agentDetailsCSVCols.add(LogParameters.SIM_CNT.toString());
        agentDetailsCSVCols.add(LogParameters.AGENT_ID.toString());
        // CIDM
        agentDetailsCSVCols.add(LogParameters.CIDM_ALPHA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_KAPPA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_BETA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_LAMDA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_C.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_MU.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_SIGMA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_GAMMA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_RS_EQUAL.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_R_SIGMA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_R_PI.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_NET_SIZE.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_IOTA.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_PHI_AV.toString());
        agentDetailsCSVCols.add(LogParameters.CIDM_TAU_AV.toString());

        // PROPERTIES
        // simulation
        agentDetailsCSVCols.add(LogProperties.SIM_ROUND.toString());
        agentDetailsCSVCols.add(LogProperties.SIM_STAGE.toString());
        // network
        agentDetailsCSVCols.add(LogProperties.NET_STABLE.toString());
        agentDetailsCSVCols.add(LogProperties.NET_DENSITY.toString());
        agentDetailsCSVCols.add(LogProperties.NET_AV_DEGREE.toString());
        agentDetailsCSVCols.add(LogProperties.NET_AV_CLUSTERING.toString());
        // agent
        agentDetailsCSVCols.add(LogProperties.AGENT_SATISFIED.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_UTIL.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_BENEFIT_DIST1.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_BENEFIT_DIST2.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_COSTS_DIST1.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_COSTS_DISEASE.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_DISEASE_STATE.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_DISEASE_ROUNDS_REMAINING.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_DEGREE1.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_DEGREE2.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CLOSENESS.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_BROKEN_ACTIVE.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_BROKEN_PASSIVE.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_OUT_ACCEPTED.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_OUT_DECLINED.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_IN_ACCEPTED.toString());
        agentDetailsCSVCols.add(LogProperties.AGENT_CONS_IN_DECLINED.toString());

        try {
            this.agentDetailsCSVWriter = new FileWriter(FULL_DATA_EXPORT_PATH + "agent-details.csv");
            CSVUtils.writeLine(this.agentDetailsCSVWriter, agentDetailsCSVCols);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Logs the agent detail data.
     *
     * @param simulation
     *          the simulation to log the agent details for.
     */
    private void logAgentDetails(Simulation simulation) {
        Network network = simulation.getNetwork();
        List<Agent> agents = new LinkedList<Agent>(network.getAgents());
        Collections.sort(agents);

        for (Agent agent : agents) {

            // a single CSV row
            List<String> agentDetailsCSVCols = new LinkedList<String>();

            // PARAMETERS
            // simulation
            agentDetailsCSVCols.add(this.dgStats.getUid());
            agentDetailsCSVCols.add(String.valueOf(this.dgStats.getUpc()));
            agentDetailsCSVCols.add(String.valueOf(this.dgStats.getSimPerUpc()));
            agentDetailsCSVCols.add(agent.getId());
            // CIDM
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getAlpha()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getKappa()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getBeta()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getLamda()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtilityFunction().getC()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getMu()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getSigma()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getGamma()));
            agentDetailsCSVCols.add(String.valueOf(this.isRsEqual ? 1 : 0));
            agentDetailsCSVCols.add(String.valueOf(agent.getRSigma()));
            agentDetailsCSVCols.add(String.valueOf(agent.getRPi()));
            agentDetailsCSVCols.add(String.valueOf(network.getN()));
            agentDetailsCSVCols.add(String.valueOf(this.dgStats.isIota() ? 1 : 0));
            agentDetailsCSVCols.add(String.valueOf(agent.getPhi()));
            agentDetailsCSVCols.add(String.valueOf(agent.getDiseaseSpecs().getTau()));

            // PROPERTIES
            // simulation
            agentDetailsCSVCols.add(String.valueOf(simulation.getRounds()));
            agentDetailsCSVCols.add(String.valueOf(this.dgStats.getSimStage()));
            // network
            agentDetailsCSVCols.add(String.valueOf(network.isStable()));
            agentDetailsCSVCols.add(String.valueOf(network.getDensity()));
            agentDetailsCSVCols.add(String.valueOf(network.getAvDegree()));
            agentDetailsCSVCols.add(String.valueOf(network.getAvClustering()));
            // agent
            agentDetailsCSVCols.add(String.valueOf(agent.isSatisfied()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getOverallUtility()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getBenefitDirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getBenefitIndirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getCostsDirectConnections()));
            agentDetailsCSVCols.add(String.valueOf(agent.getUtility().getEffectOfDisease()));
            agentDetailsCSVCols.add(agent.getDiseaseGroup().name());
            if (agent.isInfected()) {
                agentDetailsCSVCols.add(String.valueOf(agent.getTimeUntilRecovered()));
            } else {
                agentDetailsCSVCols.add("NA");
            }
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeFirstOrderDegree(agent)));
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeSecondOrderDegree(agent)));
            agentDetailsCSVCols.add(String.valueOf(StatsComputer.computeCloseness(agent)));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getBrokenTiesActive()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getBrokenTiesPassive()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getAcceptedRequestsOut()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getDeclinedRequestsOut()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getAcceptedRequestsIn()));
            agentDetailsCSVCols.add(String.valueOf(agent.getConnectionStats().getDeclinedRequestsIn()));

            try {
                CSVUtils.writeLine(this.agentDetailsCSVWriter, agentDetailsCSVCols);
                this.agentDetailsCSVWriter.flush();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            if (PropertiesHandler.getInstance().isExportSummary()) {
                this.simulationSummaryCSVWriter.flush();
                this.simulationSummaryCSVWriter.close();
            }
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                this.roundSummaryCSVWriter.flush();
                this.roundSummaryCSVWriter.close();
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                this.agentDetailsCSVWriter.flush();
                this.agentDetailsCSVWriter.close();
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
        if (this.dgStats.getSimStage() == SimulationStage.ACTIVE_EPIDEMIC) {
            this.dgStats.setTiesBrokenWithInfectionPresent(true);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.agents.AgentListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    public void notifyRoundFinished(Agent agent) { }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyRoundFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyRoundFinished(Simulation simulation) {
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            logRoundSummaryCSV(simulation);
        }
        if (PropertiesHandler.getInstance().isExportAgentDetails()) {
            logAgentDetails(simulation);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifyInfectionDefeated(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.dgStats.setRoundLastInfection(simulation.getRounds() - this.dgStats.getRoundStartInfection());
        this.dgStats.setSimStage(SimulationStage.POST_EPIDEMIC);
        // TODO improve
        if (!PropertiesHandler.getInstance().isExportAgentDetails() &&
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            logAgentDetails(simulation);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.simulation.SimulationListener#notifySimulationFinished(
     * nl.uu.socnetid.nidm.simulation.Simulation)
     */
    @Override
    public void notifySimulationFinished(Simulation simulation) {
        if (this.dgStats.getSimStage() == SimulationStage.POST_EPIDEMIC) {
            this.dgStats.setSimStage(SimulationStage.FINISHED);
            if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
                logRoundSummaryCSV(simulation);
            }
            if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                    PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
                logAgentDetails(simulation);
            }
        }
    }

}
