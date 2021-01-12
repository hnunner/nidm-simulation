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
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.data.out.BurgerBuskensParameters;
import nl.uu.socnetid.nidm.data.out.CarayolRouxParameters;
import nl.uu.socnetid.nidm.data.out.CidmParameters;
import nl.uu.socnetid.nidm.data.out.EpidemicStructures;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensGeneticParameters;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensParameters;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.DegreeDistributionConditions;
import nl.uu.socnetid.nidm.networks.LockdownConditions;


/**
 * @author Hendrik Nunner
 */
public class PropertiesHandler {

    // logger
    private static final Logger logger = LogManager.getLogger(PropertiesHandler.class);

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
    private String rootExportPath;

    // CONFIG PROPERTIES
    // file system
    private String rscriptPath;
    private String rAnalysisAutomatedTemplatePath;
    private String rAnalysisCompleteTemplatePath;
    private String rAnalysisBurgerBuskensTemplatePath;
    private String rAnalysisCarayolRouxTemplatePath;
    private String rAnalysisNunnerBuskensTemplatePath;
    // Cidm parameters
    private boolean generateCidmData;
    private CidmParameters cidmParameters;
    // BurgerBuskens parameters
    private boolean generateBurgerBuskensData;
    private BurgerBuskensParameters bbParameters;
    // CarayolRoux parameters
    private boolean generateCarayolRouxData;
    private CarayolRouxParameters crParameters;
    // NunnerBuskens parameters
    private boolean generateNunnerBuskensData;
    private boolean generateNunnerBuskensNetworks;
    private boolean generateNunnerBuskensNetworksSimple;
    private NunnerBuskensParameters nbParameters;
    // NunnerBuskens networks genetic parameters
    private boolean generateNunnerBuskensNetworksGenetic;
    private NunnerBuskensGeneticParameters nbgParameters;
    // NunnerBuskens networks professions parameters
    private boolean generateNunnerBuskensNetworksProfessions;
    private NunnerBuskensProfessionsParameters nbpParameters;

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

    // DATA IMPORT
    private Path ageDistributionImportPath;
    private Path ageDegreesImportPath;
    private Path ageAssortativityImportPath;
    private Path professionsImportPath;


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
        this.rootExportPath = new StringBuilder().append(this.userDir).append("/exports/").toString();
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
        this.rAnalysisCarayolRouxTemplatePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.carayolroux.path")).toString();
        this.rAnalysisNunnerBuskensTemplatePath = new StringBuilder().append(this.userDir).append(
                configProps.getProperty("r.anlysis.nunnerbuskens.path")).toString();

        // Cidm parameters
        generateCidmData = Boolean.parseBoolean(configProps.getProperty("cidm.generate.data"));
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
        // N
        cidmParameters.setNRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CIDM_NET_SIZE_RANDOM.toString())));
        cidmParameters.setNRandomMin(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_NET_SIZE_RANDOM_MIN.toString())));
        cidmParameters.setNRandomMax(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_NET_SIZE_RANDOM_MAX.toString())));
        cidmParameters.setNs(parseIntArray(configProps.getProperty(LogValues.IV_CIDM_NET_SIZE.toString())));
        // iota
        cidmParameters.setIotaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CIDM_IOTA_RANDOM.toString())));
        cidmParameters.setIotas(parseBooleanArray(configProps.getProperty(LogValues.IV_CIDM_IOTA.toString())));
        // phi
        cidmParameters.setPhiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CIDM_PHI_RANDOM.toString())));
        cidmParameters.setPhiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CIDM_PHI_RANDOM_MIN.toString())));
        cidmParameters.setPhiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CIDM_PHI_RANDOM_MAX.toString())));
        cidmParameters.setPhis(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_PHI.toString())));
        // omega
        cidmParameters.setOmegaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CIDM_OMEGA_RANDOM.toString())));
        cidmParameters.setOmegaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CIDM_OMEGA_RANDOM_MIN.toString())));
        cidmParameters.setOmegaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CIDM_OMEGA_RANDOM_MAX.toString())));
        cidmParameters.setOmegas(parseDoubleArray(configProps.getProperty(LogValues.IV_CIDM_OMEGA.toString())));
        // rest
        cidmParameters.setZeta(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_ZETA.toString())));
        cidmParameters.setEpsilon(Integer.valueOf(configProps.getProperty(LogValues.IV_CIDM_EPSILON.toString())));
        cidmParameters.setTaus(parseIntArray(configProps.getProperty(LogValues.IV_CIDM_TAU.toString())));
        cidmParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_CIDM_SIMS_PER_PC.toString())));

        // BurgerBuskens
        generateBurgerBuskensData = Boolean.parseBoolean(configProps.getProperty("bb.generate.data"));
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
        // omega
        bbParameters.setOmegaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_BB_OMEGA_RANDOM.toString())));
        bbParameters.setOmegaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_BB_OMEGA_RANDOM_MIN.toString())));
        bbParameters.setOmegaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_BB_OMEGA_RANDOM_MAX.toString())));
        bbParameters.setOmegas(parseDoubleArray(configProps.getProperty(LogValues.IV_BB_OMEGA.toString())));
        // n
        bbParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_BB_SIMS_PER_PC.toString())));

        // CarayolRoux
        generateCarayolRouxData = Boolean.parseBoolean(configProps.getProperty("cr.generate.data"));
        crParameters = new CarayolRouxParameters();
        // cromega
        crParameters.setCrOmegaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_CROMEGA_RANDOM.toString())));
        crParameters.setCrOmegaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CR_CROMEGA_RANDOM_MIN.toString())));
        crParameters.setCrOmegaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CR_CROMEGA_RANDOM_MAX.toString())));
        crParameters.setCrOmegas(parseDoubleArray(configProps.getProperty(LogValues.IV_CR_CROMEGA.toString())));
        // delta
        crParameters.setDeltaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_DELTA_RANDOM.toString())));
        crParameters.setDeltaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CR_DELTA_RANDOM_MIN.toString())));
        crParameters.setDeltaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CR_DELTA_RANDOM_MAX.toString())));
        crParameters.setDeltas(parseDoubleArray(configProps.getProperty(LogValues.IV_CR_DELTA.toString())));
        // c
        crParameters.setCRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_C_RANDOM.toString())));
        crParameters.setCRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CR_C_RANDOM_MIN.toString())));
        crParameters.setCRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CR_C_RANDOM_MAX.toString())));
        crParameters.setCs(parseDoubleArray(configProps.getProperty(LogValues.IV_CR_C.toString())));
        // N
        crParameters.setNRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_NET_SIZE_RANDOM.toString())));
        crParameters.setNRandomMin(Integer.valueOf(configProps.getProperty(LogValues.IV_CR_NET_SIZE_RANDOM_MIN.toString())));
        crParameters.setNRandomMax(Integer.valueOf(configProps.getProperty(LogValues.IV_CR_NET_SIZE_RANDOM_MAX.toString())));
        crParameters.setNs(parseIntArray(configProps.getProperty(LogValues.IV_CR_NET_SIZE.toString())));
        // iota
        crParameters.setIotaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_IOTA_RANDOM.toString())));
        crParameters.setIotas(parseBooleanArray(configProps.getProperty(LogValues.IV_CR_IOTA.toString())));
        // phi
        crParameters.setPhiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_PHI_RANDOM.toString())));
        crParameters.setPhiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CR_PHI_RANDOM_MIN.toString())));
        crParameters.setPhiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CR_PHI_RANDOM_MAX.toString())));
        crParameters.setPhis(parseDoubleArray(configProps.getProperty(LogValues.IV_CR_PHI.toString())));
        // omega
        crParameters.setOmegaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_CR_OMEGA_RANDOM.toString())));
        crParameters.setOmegaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_CR_OMEGA_RANDOM_MIN.toString())));
        crParameters.setOmegaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_CR_OMEGA_RANDOM_MAX.toString())));
        crParameters.setOmegas(parseDoubleArray(configProps.getProperty(LogValues.IV_CR_OMEGA.toString())));
        // n
        crParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_CR_SIMS_PER_PC.toString())));

        // NunnerBuskens
        generateNunnerBuskensData = Boolean.parseBoolean(configProps.getProperty("nb.generate.data"));
        generateNunnerBuskensNetworks = Boolean.parseBoolean(configProps.getProperty("nb.generate.networks"));
        generateNunnerBuskensNetworksSimple = Boolean.parseBoolean(configProps.getProperty("nb.generate.networks.simple"));
        nbParameters = new NunnerBuskensParameters();
        // network structure static during epidemics
        nbParameters.setEpStructure(EpidemicStructures.fromString(configProps.getProperty(LogValues.IV_NB_EP_STRUCTURE.toString())));
        // b1
        nbParameters.setB1Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_B1_RANDOM.toString())));
        nbParameters.setB1RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_B1_RANDOM_MIN.toString())));
        nbParameters.setB1RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_B1_RANDOM_MAX.toString())));
        nbParameters.setB1s(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_B1.toString())));
        // c1
        nbParameters.setC1Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_C1_RANDOM.toString())));
        nbParameters.setC1RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_C1_RANDOM_MIN.toString())));
        nbParameters.setC1RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_C1_RANDOM_MAX.toString())));
        nbParameters.setC1s(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_C1.toString())));
        // c2
        nbParameters.setC2Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_C2_RANDOM.toString())));
        nbParameters.setC2RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_C2_RANDOM_MIN.toString())));
        nbParameters.setC2RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_C2_RANDOM_MAX.toString())));
        nbParameters.setC2s(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_C2.toString())));
        // b2
        nbParameters.setB2Random(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_B2_RANDOM.toString())));
        nbParameters.setB2RandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_B2_RANDOM_MIN.toString())));
        nbParameters.setB2RandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_B2_RANDOM_MAX.toString())));
        nbParameters.setB2s(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_B2.toString())));
        // alpha
        nbParameters.setAlphaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_ALPHA_RANDOM.toString())));
        nbParameters.setAlphaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_ALPHA_RANDOM_MIN.toString())));
        nbParameters.setAlphaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_ALPHA_RANDOM_MAX.toString())));
        nbParameters.setAlphas(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_ALPHA.toString())));
        // sigma
        nbParameters.setSigmaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_SIGMA_RANDOM.toString())));
        nbParameters.setSigmaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_SIGMA_RANDOM_MIN.toString())));
        nbParameters.setSigmaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_SIGMA_RANDOM_MAX.toString())));
        nbParameters.setSigmas(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_SIGMA.toString())));
        // gamma
        nbParameters.setGammaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_GAMMA_RANDOM.toString())));
        nbParameters.setGammaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GAMMA_RANDOM_MIN.toString())));
        nbParameters.setGammaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GAMMA_RANDOM_MAX.toString())));
        nbParameters.setGammas(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_GAMMA.toString())));
        // tau
        nbParameters.setTauRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_TAU_RANDOM.toString())));
        nbParameters.setTauRandomMin(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_TAU_RANDOM_MIN.toString())));
        nbParameters.setTauRandomMax(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_TAU_RANDOM_MAX.toString())));
        nbParameters.setTaus(parseIntArray(configProps.getProperty(LogValues.IV_NB_TAU.toString())));
        // r_sigma / r_pi
        nbParameters.setRMinRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_R_MIN_RANDOM.toString())));
        nbParameters.setRMinRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_R_MIN_RANDOM_MIN.toString())));
        nbParameters.setRMinRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_R_MIN_RANDOM_MAX.toString())));
        nbParameters.setRMins(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_R_MIN.toString())));
        nbParameters.setRMaxRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_R_MAX_RANDOM.toString())));
        nbParameters.setRMaxRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_R_MAX_RANDOM_MIN.toString())));
        nbParameters.setRMaxRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_R_MAX_RANDOM_MAX.toString())));
        nbParameters.setRMaxs(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_R_MAX.toString())));
        nbParameters.setRsEqual(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_RS_EQUAL.toString())));
        nbParameters.setRSigmaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_R_SIGMA_RANDOM.toString())));
        nbParameters.setRSigmaRandomHomogeneous(parseBooleanArray(configProps.getProperty(
                LogValues.IV_NB_R_SIGMA_RANDOM_HOMOGENEOUS.toString())));
        nbParameters.setRSigmas(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_R_SIGMA.toString())));
        nbParameters.setRPiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_R_PI_RANDOM.toString())));
        nbParameters.setRPiRandomHomogeneous(parseBooleanArray(configProps.getProperty(
                LogValues.IV_NB_R_PI_RANDOM_HOMOGENEOUS.toString())));
        nbParameters.setRPis(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_R_PI.toString())));
        // N
        nbParameters.setNRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_NET_SIZE_RANDOM.toString())));
        nbParameters.setNRandomMin(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_NET_SIZE_RANDOM_MIN.toString())));
        nbParameters.setNRandomMax(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_NET_SIZE_RANDOM_MAX.toString())));
        nbParameters.setNs(parseIntArray(configProps.getProperty(LogValues.IV_NB_NET_SIZE.toString())));
        // iota
        nbParameters.setIotaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_IOTA_RANDOM.toString())));
        nbParameters.setIotas(parseBooleanArray(configProps.getProperty(LogValues.IV_NB_IOTA.toString())));
        // phi
        nbParameters.setPhiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_PHI_RANDOM.toString())));
        nbParameters.setPhiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PHI_RANDOM_MIN.toString())));
        nbParameters.setPhiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PHI_RANDOM_MAX.toString())));
        nbParameters.setPhis(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_PHI.toString())));
        // psi
        nbParameters.setPsiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_PSI_RANDOM.toString())));
        nbParameters.setPsiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PSI_RANDOM_MIN.toString())));
        nbParameters.setPsiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PSI_RANDOM_MAX.toString())));
        nbParameters.setPsis(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_PSI.toString())));
        // xi
        nbParameters.setXiRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_XI_RANDOM.toString())));
        nbParameters.setXiRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_XI_RANDOM_MIN.toString())));
        nbParameters.setXiRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_XI_RANDOM_MAX.toString())));
        nbParameters.setXis(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_XI.toString())));
        // omega
        nbParameters.setOmegaRandom(Boolean.parseBoolean(configProps.getProperty(LogValues.IV_NB_OMEGA_RANDOM.toString())));
        nbParameters.setOmegaRandomMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_OMEGA_RANDOM_MIN.toString())));
        nbParameters.setOmegaRandomMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_OMEGA_RANDOM_MAX.toString())));
        nbParameters.setOmegas(parseDoubleArray(configProps.getProperty(LogValues.IV_NB_OMEGA.toString())));
        // time steps
        nbParameters.setZeta(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_ZETA.toString())));
        nbParameters.setEpsilon(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_EPSILON.toString())));
        nbParameters.setTaus(parseIntArray(configProps.getProperty(LogValues.IV_NB_TAU.toString())));
        // n
        nbParameters.setSimsPerParameterCombination(Integer.valueOf(configProps.getProperty(
                LogValues.IV_NB_SIMS_PER_PC.toString())));
        // m TODO find better name
        nbParameters.setSimIterations(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_SIM_ITERATIONS.toString())));

        // NunnerBuskens networks genetic
        generateNunnerBuskensNetworksGenetic = Boolean.parseBoolean(configProps.getProperty("nb.generate.networks.genetic"));
        nbgParameters = new NunnerBuskensGeneticParameters();
        nbgParameters.setB1(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_B1.toString())));
        nbgParameters.setB2(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_B2.toString())));
        nbgParameters.setC1(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_C1.toString())));
        nbgParameters.setPhi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_PHI.toString())));
        nbgParameters.setPsi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_PSI.toString())));
        nbgParameters.setXi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_XI.toString())));
        nbgParameters.setRoundsMax(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_ROUNDS_MAX.toString())));
        nbgParameters.setMutationSd(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_MUTATION_SD.toString())));
        nbgParameters.setFirstGeneration(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_FIRST_GENERATION.toString())));
        nbgParameters.setParents(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_PARENTS.toString())));
        nbgParameters.setChildren(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_CHILDREN.toString())));
        nbgParameters.setGenerations(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_GENERATIONS.toString())));
        nbgParameters.setN(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_N.toString())));
        nbgParameters.setTargetAvDegree(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_TARGET_AVDEGREE.toString())));
        nbgParameters.setTargetClustering(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_TARGET_CLUSTERING.toString())));
        nbgParameters.setInitialAlphaMin(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_INITIAL_ALPHA_MIN.toString())));
        nbgParameters.setInitialAlphaMax(Double.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_INITIAL_ALPHA_MAX.toString())));
        nbgParameters.setConsiderAge(Boolean.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_CONSIDER_AGE.toString())));
        nbgParameters.setConsiderProfession(Boolean.valueOf(configProps.getProperty(LogValues.IV_NB_GEN_CONSIDER_PROFESSION.toString())));

        // NunnerBuskens network professions
        generateNunnerBuskensNetworksProfessions = Boolean.parseBoolean(configProps.getProperty("nb.generate.networks.professions"));
        nbpParameters = new NunnerBuskensProfessionsParameters();
        nbpParameters.setN(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_N.toString())));
        nbpParameters.setPhi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_PHI.toString())));
        nbpParameters.setPsi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_PSI.toString())));
        nbpParameters.setXi(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_XI.toString())));
        nbpParameters.setZeta(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_ZETA.toString())));
        nbpParameters.setB1(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_B1.toString())));
        nbpParameters.setB2(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_B2.toString())));
        nbpParameters.setC1(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_C1.toString())));
        nbpParameters.setAlpha(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_ALPHA.toString())));
        nbpParameters.setConsiderAge(Boolean.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_CONSIDER_AGE.toString())));
        nbpParameters.setConsiderProfession(Boolean.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_CONSIDER_PROFESSION.toString())));
        nbpParameters.setAssortativityInitCondition(AssortativityConditions.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_ASSORTATIVITY_INIT_CONDITION.toString())));
        String[] acsString = configProps.getProperty(LogValues.IV_NB_PROF_ASSORTATIVITY_CONDITIONS.toString()).split(",");
        List<AssortativityConditions> acs = new ArrayList<AssortativityConditions>(acsString.length);
        for (int i = 0; i < acsString.length; i++) {
            acs.add(AssortativityConditions.valueOf(acsString[i]));
        }
        nbpParameters.setAssortativityConditions(acs);
        nbpParameters.setOmega(Double.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_OMEGA.toString())));
        nbpParameters.setSimIterations(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_SIM_ITERATIONS.toString())));
        String[] lcsString = configProps.getProperty(LogValues.IV_NB_PROF_LOCKDOWN_CONDITIONS.toString()).split(",");
        List<LockdownConditions> lcs = new ArrayList<LockdownConditions>(lcsString.length);
        for (int i = 0; i < lcsString.length; i++) {
            lcs.add(LockdownConditions.valueOf(lcsString[i]));
        }
        nbpParameters.setLockdownConditions(lcs);
        String[] ddcsString = configProps.getProperty(LogValues.IV_NB_PROF_DEGREE_DISTRIBUTION_CONDITIONS.toString()).split(",");
        List<DegreeDistributionConditions> ddcs = new ArrayList<DegreeDistributionConditions>(ddcsString.length);
        for (int i = 0; i < ddcsString.length; i++) {
            lcs.add(LockdownConditions.valueOf(ddcsString[i]));
        }
        nbpParameters.setDegreeDistributionConditions(ddcs);
        nbpParameters.setRoundsMax(Integer.valueOf(configProps.getProperty(LogValues.IV_NB_PROF_ROUNDS_MAX.toString())));

        // types of data export
        this.exportSummary = Boolean.parseBoolean(configProps.getProperty("export.summary"));
        this.exportSummaryEachRound = Boolean.parseBoolean(configProps.getProperty("export.summary.each.round"));
        this.exportAgentDetails = Boolean.parseBoolean(configProps.getProperty("export.agent.details"));
        this.exportAgentDetailsReduced = Boolean.parseBoolean(configProps.getProperty("export.agent.details.reduced"));
        this.exportGexf = Boolean.parseBoolean(configProps.getProperty("export.gexf"));

        // analyze data?
        this.analyzeData = Boolean.parseBoolean(configProps.getProperty("analyze.data"));

        // data import
        try {
            this.ageDistributionImportPath = Paths.get(getClass().getClassLoader()
                    .getResource(configProps.getProperty("import.age.distribution.path")).toURI());
            this.ageDegreesImportPath = Paths.get(getClass().getClassLoader()
                    .getResource(configProps.getProperty("import.age.degrees.path")).toURI());
            this.ageAssortativityImportPath = Paths.get(getClass().getClassLoader()
                    .getResource(configProps.getProperty("import.age.assortativity.path")).toURI());
        } catch (URISyntaxException use) {
            logger.error("Error while retrieving age structure paths: ", use);
        }
        try {
            this.professionsImportPath = Paths.get(getClass().getClassLoader()
                    .getResource(configProps.getProperty("import.professions.path")).toURI());
        } catch (URISyntaxException use) {
            logger.error("Error while retrieving profession structure path: ", use);
        }

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
     * @return the rootExportPath
     */
    public String getRootExportPath() {
        return rootExportPath;
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
     * Gets whether to generate data for the CIDM model or not.
     *
     * @return true if data ought to be generated, false otherwise
     */
    public boolean isGenerateCidmData() {
        return generateCidmData;
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
     * Gets whether to generate data for the BurgerBuskens model or not.
     *
     * @return true if data ought to be generated, false otherwise
     */
    public boolean isGenerateBurgerBuskensData() {
        return generateBurgerBuskensData;
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
     * Gets whether to generate data for the CarayolRoux model or not.
     *
     * @return true if data ought to be generated, false otherwise
     */
    public boolean isGenerateCarayolRouxData() {
        return generateCarayolRouxData;
    }

    /**
     * Gets the CarayolRoux parameters as defined in the config.properties
     *
     * @return the CarayolRoux parameters as defined in the config.properties
     */
    public CarayolRouxParameters getCarayolRouxParameters() {
        return crParameters;
    }

    /**
     * Gets whether to generate data for the NunnerBuskens model or not.
     *
     * @return true if data ought to be generated, false otherwise
     */
    public boolean isGenerateNunnerBuskensData() {
        return generateNunnerBuskensData;
    }

    /**
     * Gets whether to generate networks for the NunnerBuskens model or not.
     *
     * @return true if networks ought to be generated, false otherwise
     */
    public boolean isGenerateNunnerBuskensNetworks() {
        return generateNunnerBuskensNetworks;
    }

    /**
     * Gets whether to generate simple networks for the NunnerBuskens model or not.
     *
     * @return true if networks ought to be generated, false otherwise
     */
    public boolean isGenerateNunnerBuskensNetworksSimple() {
        return generateNunnerBuskensNetworksSimple;
    }

    /**
     * Gets whether to generate genetic networks for the NunnerBuskens model or not.
     *
     * @return true if networks ought to be generated, false otherwise
     */
    public boolean isGenerateNunnerBuskensNetworksGenetic() {
        return generateNunnerBuskensNetworksGenetic;
    }

    /**
     * Gets whether to generate profession networks for the NunnerBuskens model or not.
     *
     * @return true if networks ought to be generated, false otherwise
     */
    public boolean isGenerateNunnerBuskensNetworksProfessions() {
        return generateNunnerBuskensNetworksProfessions;
    }

    /**
     * Gets the NunnerBuskens parameters as defined in the config.properties
     *
     * @return the NunnerBuskens parameters as defined in the config.properties
     */
    public NunnerBuskensParameters getNunnerBuskensParameters() {
        return nbParameters;
    }

    /**
     * Gets the NunnerBuskensGenetic parameters as defined in the config.properties
     *
     * @return the NunnerBuskens parameters as defined in the config.properties
     */
    public NunnerBuskensGeneticParameters getNunnerBuskensGeneticParameters() {
        return nbgParameters;
    }

    /**
     * Gets the NunnerBuskensProfessions parameters as defined in the config.properties
     *
     * @return the NunnerBuskens parameters as defined in the config.properties
     */
    public NunnerBuskensProfessionsParameters getNunnerBuskensProfessionsParameters() {
        return nbpParameters;
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

    /**
     * @return the rAnalysisCarayolRouxTemplatePath
     */
    public String getRAnalysisCarayolRouxTemplatePath() {
        return rAnalysisCarayolRouxTemplatePath;
    }

    /**
     * @return the rAnalysisNunnerBuskensTemplatePath
     */
    public String getRAnalysisNunnerBuskensTemplatePath() {
        return rAnalysisNunnerBuskensTemplatePath;
    }

    /**
     * @return the ageDistributionImportPath
     */
    public Path getAgeDistributionImportPath() {
        return ageDistributionImportPath;
    }

    /**
     * @return the ageDegreesImportPath
     */
    public Path getAgeDegreesImportPath() {
        return ageDegreesImportPath;
    }

    /**
     * @return the ageAssortativityImportPath
     */
    public Path getAgeAssortativityImportPath() {
        return ageAssortativityImportPath;
    }

    /**
     * @return the professionsImportPath
     */
    public Path getProfessionsImportPath() {
        return professionsImportPath;
    }

}
