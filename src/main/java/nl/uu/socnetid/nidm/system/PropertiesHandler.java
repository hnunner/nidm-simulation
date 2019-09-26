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
package nl.uu.socnetid.nidm.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.data.BurgerBuskensParameters;
import nl.uu.socnetid.nidm.data.CidmParameters;
import nl.uu.socnetid.nidm.data.LogValues;


/**
 * @author Hendrik Nunner
 */
public class PropertiesHandler {

    // logger
    private static final Logger logger = Logger.getLogger(PropertiesHandler.class);

    // singleton of PropertiesHandler
    private static PropertiesHandler pHandler = new PropertiesHandler();

    // SYSTEM PROPERTIES
    private OsTypes osType;
    private String osArch;
    private String osName;
    private String osVersion;
    private String javaVersion;
    private String javaVendor;
    private String javaVendorUrl;
    private String userDir;
    private String dataExportPath;

    // CONFIG PROPERTIES
    // file system
    private String rscriptPath;
    private String rAnalysisAutomatedTemplatePath;
    private String rAnalysisCompleteTemplatePath;
    private String rAnalysisBurgerBuskensTemplatePath;
    // Cidm parameters
    private CidmParameters cidmParameters;
    // BurgerBuskens parameters
    private BurgerBuskensParameters bbParameters;
    // DATA EXPORT
    // types of data export
    private boolean exportSummary;
    private boolean exportSummaryEachRound;
    private boolean exportAgentDetails;
    private boolean exportAgentDetailsReduced;
    private boolean exportGexf;
    // data analysis?
    private boolean analyzeData;

    // GIT PROPERTIES
    private String gitCommitId;
    private String gitBranch;
    private String gitBuildTime;
    private String gitBuildUserName;
    private String gitRemoteOriginUrl;


    /**
     * Invisible constructor.
     */
    private PropertiesHandler() {
        readProperties();
        logProperties();
    }



    private void readProperties() {
        readSystemProperties();
        readConfigProperties();
        readGitProperties();
    }

    private void readSystemProperties() {

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            this.osType = OsTypes.WIN;
        } else if (os.indexOf("mac") >= 0) {
            this.osType = OsTypes.MAC;
        } else if (os.indexOf("nix") >= 0 ||
                os.indexOf("nux") >= 0 ||
                os.indexOf("aix") >= 0) {
            this.osType = OsTypes.UNIX;
        } else {
            this.osType = OsTypes.OTHER;
        }

        this.osArch = System.getProperty("os.arch");
        this.osName = System.getProperty("os.name");
        this.osVersion = System.getProperty("os.version");
        this.javaVersion = System.getProperty("java.version");
        this.javaVendor = System.getProperty("java.vendor");
        this.javaVendorUrl = System.getProperty("java.vendor.url");
        this.userDir = System.getProperty("user.dir");
        this.dataExportPath = new StringBuilder().append(this.userDir).append("/data/").toString();
    }

    private void readConfigProperties() {
        // read config.properties
        Properties configProps = new Properties();
        try (InputStream input = PropertiesHandler.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Unable to find config.properties!");
            } else {
                configProps.load(input);
            }
        } catch (IOException e) {
            logger.error(e);
        }

        // Rscript path depending operating system
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            this.rscriptPath = configProps.getProperty("r.script.path.win");
        } else if (os.indexOf("mac") >= 0) {
            this.rscriptPath = configProps.getProperty("r.script.path.mac");
        } else if (os.indexOf("nix") >= 0 ||
                os.indexOf("nux") >= 0 ||
                os.indexOf("aix") >= 0) {
            this.rscriptPath = configProps.getProperty("r.script.path.unix");
        } else {
            this.rscriptPath = configProps.getProperty("r.script.path.other");
        }

        // R scripts for data analysis
        this.rAnalysisAutomatedTemplatePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.automated.template.path")).toString();
        this.rAnalysisCompleteTemplatePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.complete.template.path")).toString();
        this.rAnalysisBurgerBuskensTemplatePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.burgerbuskens.path")).toString();

        // Cidm parameters
        cidmParameters = new CidmParameters();
        cidmParameters.setAlphas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_ALPHA.toString())));
        cidmParameters.setKappas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_KAPPA.toString())));
        cidmParameters.setBetas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_BETA.toString())));
        cidmParameters.setLamdas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_LAMDA.toString())));
        cidmParameters.setCs(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_C.toString())));
        cidmParameters.setMus(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_MU.toString())));
        cidmParameters.setSigmas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_SIGMA.toString())));
        cidmParameters.setGammas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_GAMMA.toString())));
        cidmParameters.setRsEqual(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CIDM_RS_EQUAL.toString())));
        cidmParameters.setRSigmas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_R_SIGMA.toString())));
        cidmParameters.setRPis(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_R_PI.toString())));
        cidmParameters.setNs(parseIntArray(configProps.getProperty(LogValues.IV_CIDM_NET_SIZE.toString())));
        cidmParameters.setIotas(parseBooleanArray(configProps.getProperty(LogValues.IV_CIDM_IOTA.toString())));
        cidmParameters.setPhis(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_PHI.toString())));
        cidmParameters.setZeta(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_ZETA.toString())));
        cidmParameters.setEpsilon(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_EPSILON.toString())));
        cidmParameters.setTaus(parseIntArray(configProps.getProperty(LogValues.IV_CIDM_TAU.toString())));
        cidmParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_CIDM_SIMS_PER_PC.toString())));

        // BurgerBuskens parameters
        bbParameters = new BurgerBuskensParameters();
        // b1
        bbParameters.setB1Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_B1_RANDOM.toString())));
        bbParameters.setB1RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_B1_RANDOM_MIN.toString())));
        bbParameters.setB1RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_B1_RANDOM_MAX.toString())));
        bbParameters.setB1s(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_B1.toString())));
        // c1
        bbParameters.setC1Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_C1_RANDOM.toString())));
        bbParameters.setC1RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C1_RANDOM_MIN.toString())));
        bbParameters.setC1RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C1_RANDOM_MAX.toString())));
        bbParameters.setC1s(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_C1.toString())));
        // c2
        bbParameters.setC2Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_C2_RANDOM.toString())));
        bbParameters.setC2RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C2_RANDOM_MIN.toString())));
        bbParameters.setC2RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C2_RANDOM_MAX.toString())));
        bbParameters.setC2s(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_C2.toString())));
        // b2
        bbParameters.setB2Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_B2_RANDOM.toString())));
        bbParameters.setB2RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_B2_RANDOM_MIN.toString())));
        bbParameters.setB2RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_B2_RANDOM_MAX.toString())));
        bbParameters.setB2s(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_B2.toString())));
        // c3
        bbParameters.setC3Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_C3_RANDOM.toString())));
        bbParameters.setC3RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C3_RANDOM_MIN.toString())));
        bbParameters.setC3RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_C3_RANDOM_MAX.toString())));
        bbParameters.setC3s(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_C3.toString())));
        // N
        bbParameters.setNRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_NET_SIZE_RANDOM.toString())));
        bbParameters.setNRandomMin(Integer.valueOf(configProps.getProperty(LogValues.IV_BB_NET_SIZE_RANDOM_MIN.toString())));
        bbParameters.setNRandomMax(Integer.valueOf(configProps.getProperty(LogValues.IV_BB_NET_SIZE_RANDOM_MAX.toString())));
        bbParameters.setNs(parseIntArray(configProps.getProperty(LogValues.IV_BB_NET_SIZE.toString())));
        // iota
        bbParameters.setIotaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_IOTA_RANDOM.toString())));
        bbParameters.setIotas(parseBooleanArray(configProps.getProperty(LogValues.IV_BB_IOTA.toString())));
        // phi
        bbParameters.setPhiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_PHI_RANDOM.toString())));
        bbParameters.setPhiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_PHI_RANDOM_MIN.toString())));
        bbParameters.setPhiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_PHI_RANDOM_MAX.toString())));
        bbParameters.setPhis(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_PHI.toString())));
        // n
        bbParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_BB_SIMS_PER_PC.toString())));

        // types of data export
        this.exportSummary = Boolean.parseBoolean(configProps.getProperty("export.summary"));
        this.exportSummaryEachRound = Boolean.parseBoolean(configProps.getProperty("export.summary.each.round"));
        this.exportAgentDetails = Boolean.parseBoolean(configProps.getProperty("export.agent.details"));
        this.exportAgentDetailsReduced = Boolean.parseBoolean(configProps.getProperty("export.agent.details.reduced"));
        this.exportGexf = Boolean.parseBoolean(configProps.getProperty("export.gexf"));

        // analyze data?
        this.analyzeData = Boolean.parseBoolean(configProps.getProperty("analyze.data"));
    }

    private void readGitProperties() {
        // read git.properties
        Properties gitProps = new Properties();
        try (InputStream input = PropertiesHandler.class.getClassLoader().getResourceAsStream("git.properties")) {
            if (input == null) {
                logger.warn("Unable to find git.properties!");
            } else {
                gitProps.load(input);
            }
        } catch (IOException e) {
            logger.error(e);
        }

        this.gitCommitId = gitProps.getProperty("git.commit.id.describe");
        this.gitBranch = gitProps.getProperty("git.branch");
        this.gitBuildTime = gitProps.getProperty("git.build.time");
        this.gitBuildUserName = gitProps.getProperty("git.build.user.name");
        this.gitRemoteOriginUrl = gitProps.getProperty("git.remote.origin.url");
    }


    private void logProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCompilationInfos());
        sb.append(getSystemInfos());
        sb.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info(sb.toString());
    }

    private String getCompilationInfos() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n::::::::::::::::::::::::::::::::::::::::: COMPILATION INFOS ::::::::::::::::::::::::::::::::::::::::::");
        sb.append("\nGit commit:\t\t").append(this.gitCommitId);
        sb.append("\nGit branch:\t\t").append(this.gitBranch);
        sb.append("\nGit build time:\t\t").append(this.gitBuildTime);
        sb.append("\nGit builder user name:\t").append(this.gitBuildUserName);
        sb.append("\nGit remote origin url:\t").append(this.gitRemoteOriginUrl);
        return sb.toString();
    }

    private String getSystemInfos() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n:::::::::::::::::::::::::::::::::::::::::::: SYSTEM INFOS ::::::::::::::::::::::::::::::::::::::::::::");
        sb.append("\nSystem:\t\t\t").append(this.osArch);
        sb.append("\nOS:\t\t\t").append(this.osName).append(" (").append(this.osVersion).append(")");
        sb.append("\nUsed Java version:\t").append(this.javaVersion).append(" by ").append(
                this.javaVendor).append(" (").append(this.javaVendorUrl).append(")");
        sb.append("\nRscript path:\t\t").append(this.rscriptPath);
        sb.append("\nUser directory:\t\t").append(this.userDir);
        return sb.toString();
    }







    /**
     * Static method to retrieve the sole instance of the properties handler.
     *
     * @return the sole instance of the properties handler
     */
    public static PropertiesHandler getInstance() {
        return pHandler;
    }


    private double[] parseDoubleArray(String s) {
        return Arrays.stream(s.trim().split(",")).mapToDouble(Double::parseDouble).toArray();
    }

    private int[] parseIntArray(String s) {
        return Arrays.stream(s.trim().split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private boolean[] parseBooleanArray(String s) {
        String[] boolsString = s.trim().split(",");
        boolean[] bools = new boolean[boolsString.length];

        for (int i = 0; i < boolsString.length; i++) {
            bools[i] = Boolean.parseBoolean(boolsString[i]);
        }
        return bools;
    }


    /**
     * @return the dataExportPath
     */
    public String getDataExportPath() {
        return dataExportPath;
    }

    /**
     * @return the rscriptPath
     */
    public String getRscriptPath() {
        return rscriptPath;
    }

    /**
     * @return the exportSummary
     */
    public boolean isExportSummary() {
        return exportSummary;
    }

    /**
     * @return the exportSummaryEachRound
     */
    public boolean isExportSummaryEachRound() {
        return exportSummaryEachRound;
    }

    /**
     * @return the exportAgentDetails
     */
    public boolean isExportAgentDetails() {
        return exportAgentDetails;
    }

    /**
     * @return the exportAgentDetailsReduced
     */
    public boolean isExportAgentDetailsReduced() {
        return exportAgentDetailsReduced;
    }

    /**
     * @return the exportGexf
     */
    public boolean isExportGexf() {
        return exportGexf;
    }

    /**
     * @return the osType
     */
    public OsTypes getOsType() {
        return osType;
    }

    /**
     * Gets the Cidm parameters as defined in the config.properties
     *
     * @return the Cidm parameters as defined in the config.properties
     */
    public CidmParameters getCidmParameters() {
        return cidmParameters;
    }

    /**
     * Gets the BurgerBuskens parameters as defined in the config.properties
     *
     * @return the BurgerBuskens parameters as defined in the config.properties
     */
    public BurgerBuskensParameters getBurgerBuskensParameters() {
        return bbParameters;
    }

    /**
     * @return the analyzeData
     */
    public boolean isAnalyzeData() {
        return analyzeData;
    }

    /**
     * @return the rAnalysisAutomatedTemplatePath
     */
    public String getRAnalysisAutomatedTemplatePath() {
        return rAnalysisAutomatedTemplatePath;
    }

    /**
     * @return the rAnalysisCompleteTemplatePath
     */
    public String getRAnalysisCompleteTemplatePath() {
        return rAnalysisCompleteTemplatePath;
    }

    /**
     * @return the rAnalysisBurgerBuskensTemplatePath
     */
    public String getRAnalysisBurgerBuskensTemplatePath() {
        return rAnalysisBurgerBuskensTemplatePath;
    }

}
