package nl.uu.socnetid.nidm.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

import nl.uu.socnetid.nidm.utilities.CIDMParameters;


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
    private String rAnalysisFilePath;
    // CIDM parameters
    private CIDMParameters cidmParameters;
    // DATA EXPORT
    // types of data export
    private boolean exportSummary;
    private boolean exportSummaryEachRound;
    private boolean exportAgentDetails;
    private boolean exportAgentDetailsReduced;
    private boolean exportGexf;

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

        // R script for data analysis
        this.rAnalysisFilePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.file.path")).toString();

        // CIDM parameters
        // social maintenance costs
        cidmParameters = new CIDMParameters();
        cidmParameters.setAlphas(parseDoubleArray(configProps.getProperty("cidm.alphas")));
        cidmParameters.setKappas(parseDoubleArray(configProps.getProperty("cidm.kappas")));
        cidmParameters.setBetas(parseDoubleArray(configProps.getProperty("cidm.betas")));
        cidmParameters.setLamdas(parseDoubleArray(configProps.getProperty("cidm.lamdas")));
        cidmParameters.setCs(parseDoubleArray(configProps.getProperty("cidm.cs")));
        cidmParameters.setMus(parseDoubleArray(configProps.getProperty("cidm.mus")));
        cidmParameters.setSigmas(parseDoubleArray(configProps.getProperty("cidm.sigmas")));
        cidmParameters.setGammas(parseDoubleArray(configProps.getProperty("cidm.gammas")));
        cidmParameters.setRs(parseDoubleArray(configProps.getProperty("cidm.rs")));
        cidmParameters.setNs(parseIntArray(configProps.getProperty("cidm.Ns")));
        cidmParameters.setIotas(parseBooleanArray(configProps.getProperty("cidm.iotas")));
        cidmParameters.setPhis(parseDoubleArray(configProps.getProperty("cidm.phis")));
        cidmParameters.setZeta(Integer.valueOf(configProps.getProperty("cidm.zeta")));
        cidmParameters.setEpsilon(Integer.valueOf(configProps.getProperty("cidm.epsilon")));
        cidmParameters.setTaus(parseIntArray(configProps.getProperty("cidm.taus")));
        cidmParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty("cidm.n")));

        // types of data export
        this.exportSummary = Boolean.parseBoolean(configProps.getProperty("export.summary"));
        this.exportSummaryEachRound = Boolean.parseBoolean(configProps.getProperty("export.summary.each.round"));
        this.exportAgentDetails = Boolean.parseBoolean(configProps.getProperty("export.agent.details"));
        this.exportAgentDetailsReduced = Boolean.parseBoolean(configProps.getProperty("export.agent.details.reduced"));
        this.exportGexf = Boolean.parseBoolean(configProps.getProperty("export.gexf"));
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
     * @return the rAnalysisFilePath
     */
    public String getRAnalysisFilePath() {
        return rAnalysisFilePath;
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
     * Gets the CIDM parameters as defined in the config.properties
     *
     * @return the CIDM parameters as defined in the config.properties
     */
    public CIDMParameters getCidmParameters() {
        return cidmParameters;
    }

}
