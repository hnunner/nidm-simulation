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
package nl.uu.socnetid.nidm.io.analysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.data.LogValues;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 *
 * TODO improve whole class.. it's quite 'hacky' at the moment.
 */
public class RegressionParameterWriter {

    // logger
    private static final Logger logger = LogManager.getLogger(RegressionParameterWriter.class);

    /**
     * Writes regression analyses based on templates (/analysis/) and copies them into the data path.
     *
     * @param destination
     *          the destination where to write the analysis files to
     * @return the path of the automated analysis file
     */
    public String writeRegressionFiles(String destination) {

        // whole regression method
        StringBuilder sbComposition = new StringBuilder();

        // attack rate models
        StringBuilder sbModelAttackRateBeginning = new StringBuilder();
        StringBuilder sbModelAttackRateEnding = new StringBuilder();
        StringBuilder sbModelAttackRateNull = new StringBuilder();
        StringBuilder sbModelAttackRateMain = new StringBuilder();
        StringBuilder sbModelAttackRateMainNet = new StringBuilder();
        StringBuilder sbModelAttackRateInt = new StringBuilder();
        // duration models
        StringBuilder sbModelDurationBeginning = new StringBuilder();
        StringBuilder sbModelDurationEnding = new StringBuilder();
        StringBuilder sbModelDurationNull = new StringBuilder();
        StringBuilder sbModelDurationMain = new StringBuilder();
        StringBuilder sbModelDurationMainNet = new StringBuilder();
        StringBuilder sbModelDurationInt = new StringBuilder();
        // model parameters
        StringBuilder sbModelParamsMain = new StringBuilder();
        StringBuilder sbModelParamsMainNet = new StringBuilder();
        StringBuilder sbModelParamsInt = new StringBuilder();

        List<String> params = new LinkedList<String>();
        int tabWidth1 = 35;
        int tabWidth2 = 44;

        // method declaration
        sbComposition.append("exportRegressionModelsComplete <- function(ssData = loadSimulationSummaryData()) {\n\n");
        sbComposition.append("  # MAIN EFFECTS\n  # Cidm parameters\n");

        // attack rate - standard beginning and ending
        sbModelAttackRateBeginning.append("<- glmer(ssData$");
        sbModelAttackRateBeginning.append(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString()).append("/100 ~\n");
        sbModelAttackRateEnding.append("                      (1 | ").append(LogValues.IV_SIM_UPC).append("),\n");
        sbModelAttackRateEnding.append("                    family = binomial,\n");
        sbModelAttackRateEnding.append("                    data = ssData)");
        // attack rate - null model
        sbModelAttackRateNull.append("  # null-model\n");
        sbModelAttackRateNull.append("  reg00    ").append(sbModelAttackRateBeginning.toString());
        sbModelAttackRateNull.append("                      1 +\n").append(sbModelAttackRateEnding.toString());

        // duration - standard beginning and ending
        sbModelDurationBeginning.append("<- lmer(ssData$");
        sbModelDurationBeginning.append(LogValues.DV_SIM_EPIDEMIC_DURATION.toString()).append(" ~\n");
        sbModelDurationEnding.append("                      (1 | ").append(LogValues.IV_SIM_UPC).append("),\n");
        sbModelDurationEnding.append("                    data = ssData,\n");
        sbModelDurationEnding.append("                    REML = FALSE)");
        // duration - null model
        sbModelDurationNull.append("  # null-model\n");
        sbModelDurationNull.append("  reg00    ").append(sbModelDurationBeginning.toString());
        sbModelDurationNull.append("                      1 +\n").append(sbModelDurationEnding.toString());

        // main effects
        sbModelAttackRateMain.append("  # main effects: varied CIDM parameters\n");
        sbModelAttackRateMain.append("  reg1Main ").append(sbModelAttackRateBeginning.toString());
        sbModelDurationMain.append("  # main effects: varied CIDM parameters\n");
        sbModelDurationMain.append("  reg1Main ").append(sbModelDurationBeginning.toString());
        sbModelParamsMain.append("                      #  model parameters\n");
        for (LogValues logValue : LogValues.getRegressionIndependentVariablesCidm()) {

            String paramFull = logValue.toString();
            String paramName = paramFull.replace("cidm.", "").replace(".av", "");
            if (paramName.contains("r.sigma") && PropertiesHandler.getInstance().getCidmParameters().isRsEqual()) {
                paramName = paramName.replace(".sigma", "");
            }
            params.add(paramName);
            sbModelParamsMain.append("                      ").append(paramName).append(" +\n");

            sbComposition.append("  ").append(paramName);
            for (int i = tabWidth1-paramName.length(); i > 0; i--) {
                sbComposition.append(" ");
            }
            sbComposition.append("<- meanCenter(ssData$").append(paramFull);
            if (paramFull.equals(LogValues.IV_CIDM_NET_SIZE.toString()) ||
                    paramFull.equals(LogValues.IV_CIDM_SIGMA_AV.toString())) {
                sbComposition.append(" / 50");
            }
            sbComposition.append(")\n");
        }
        sbModelAttackRateMain.append(sbModelParamsMain.toString()).append(sbModelAttackRateEnding.toString());
        sbModelDurationMain.append(sbModelParamsMain.toString()).append(sbModelDurationEnding.toString());

        sbComposition.append("  # network properties\n");
        sbModelAttackRateMainNet.append("  # network properties\n");
        sbModelAttackRateMainNet.append("  reg2Main ").append(sbModelAttackRateBeginning.toString());
        sbModelAttackRateMainNet.append(sbModelParamsMain.toString());
        sbModelDurationMainNet.append("  # network properties\n");
        sbModelDurationMainNet.append("  reg2Main ").append(sbModelDurationBeginning.toString());
        sbModelDurationMainNet.append(sbModelParamsMain.toString());
        sbModelParamsMainNet.append("                      #  network properties\n");
        for (LogValues logValue : LogValues.getRegressionDependentVariables()) {

            String propertyFull = logValue.toString();
            String propertyName = propertyFull.replace("prop.", "").replace("net.", "").replace(".pre.epidemic", "");
            params.add(propertyName);
            sbModelParamsMainNet.append("                      ").append(propertyName).append(" +\n");

            sbComposition.append("  ").append(propertyName);
            for (int i = tabWidth1-propertyName.length(); i > 0; i--) {
                sbComposition.append(" ");
            }
            sbComposition.append("<- meanCenter(ssData$").append(propertyFull);
            if (propertyFull.equals(LogValues.DV_INDEX_DEGREE1.toString())) {
                sbComposition.append(" / (ssData$").append(LogValues.IV_CIDM_NET_SIZE.toString()).append("-1)");
            }
            sbComposition.append(")\n");
        }
        sbModelAttackRateMainNet.append(sbModelParamsMainNet.toString()).append(sbModelAttackRateEnding.toString());
        sbModelDurationMainNet.append(sbModelParamsMainNet.toString()).append(sbModelDurationEnding.toString());

        sbComposition.append("\n  # INTERACTION EFFECTS\n");
        sbModelAttackRateInt.append("  # interaction effects\n");
        sbModelAttackRateInt.append("  reg2Int  ").append(sbModelAttackRateBeginning.toString());
        sbModelAttackRateInt.append(sbModelParamsMain.toString());
        sbModelAttackRateInt.append(sbModelParamsMainNet.toString());
        sbModelDurationInt.append("  # interaction effects\n");
        sbModelDurationInt.append("  reg2Int  ").append(sbModelDurationBeginning.toString());
        sbModelDurationInt.append(sbModelParamsMain.toString());
        sbModelDurationInt.append(sbModelParamsMainNet.toString());
        sbModelParamsInt.append("                      #  interactions\n");
        while (!params.isEmpty()) {
            String param = params.get(0);
            params.remove(0);
            if (!params.isEmpty()) {
                sbComposition.append("  # combinations of ").append(param).append("\n");
                List<String> params2 = new LinkedList<String>(params);
                Iterator<String> it = params2.iterator();
                while (it.hasNext()) {
                    String param2 = it.next();
                    String intName = param + ".X." + param2;
                    sbComposition.append("  ").append(intName);
                    sbModelParamsInt.append("                      ").append(intName).append(" +\n");
                    for (int i = tabWidth1-intName.length(); i > 0; i--) {
                        sbComposition.append(" ");
                    }
                    String intExpr = "<- (" + param + " - mean(" + param + "))";
                    sbComposition.append(intExpr);
                    for (int i = tabWidth2-intExpr.length(); i > 0; i--) {
                        sbComposition.append(" ");
                    }
                    sbComposition.append("*  (").append(param2).append(" - mean(").append(param2).append("))\n");
                }
            }
        }
        sbModelAttackRateInt.append(sbModelParamsInt.toString()).append(sbModelAttackRateEnding.toString());
        sbModelDurationInt.append(sbModelParamsInt.toString()).append(sbModelDurationEnding.toString());

        sbComposition.append("\n\n").append("  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###\n");
        sbComposition.append("  ### level 2: parameters combination             ###\n");
        sbComposition.append("  ### level 1: simulation runs                    ###\n");
        sbComposition.append(sbModelAttackRateNull.toString());
        sbComposition.append("\n").append(sbModelAttackRateMain.toString());
        sbComposition.append("\n").append(sbModelAttackRateMainNet.toString());
        sbComposition.append("\n").append(sbModelAttackRateInt.toString());
        sbComposition.append("\n").append("  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), \"reg-attackrate-complete\")");

        sbComposition.append("\n\n").append("  ### 2-LEVEL LINEAR REGRESSIONS (duration)  ###\n");
        sbComposition.append("  ### level 2: parameters combination             ###\n");
        sbComposition.append("  ### level 1: simulation runs                    ###\n");
        sbComposition.append(sbModelDurationNull.toString());
        sbComposition.append("\n").append(sbModelDurationMain.toString());
        sbComposition.append("\n").append(sbModelDurationMainNet.toString());
        sbComposition.append("\n").append(sbModelDurationInt.toString());
        sbComposition.append("\n").append("  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), \"reg-duration-complete\")");
        sbComposition.append("\n}");

        Path pathAutoIn = Paths.get(PropertiesHandler.getInstance().getRAnalysisAutomatedTemplatePath());
        Path pathAutoOut = Paths.get(destination + "automated.R");
        Path pathCompleteIn = Paths.get(PropertiesHandler.getInstance().getRAnalysisCompleteTemplatePath());
        Path pathCompleteOut = Paths.get(destination + "complete.R");
        Charset charset = StandardCharsets.UTF_8;

        try {
            String content = new String(Files.readAllBytes(pathAutoIn), charset);
            content = content.replace("# REPLACE LINE WITH GENERATED 'exportRegressionModelsComplete'", sbComposition.toString());
            Files.write(pathAutoOut, content.getBytes(charset));

            content = new String(Files.readAllBytes(pathCompleteIn), charset);
            content = content.replace("# REPLACE LINE WITH GENERATED 'exportRegressionModelsComplete'", sbComposition.toString());
            Files.write(pathCompleteOut, content.getBytes(charset));

        } catch (IOException e) {
            logger.error(e);
        }

        return pathAutoOut.toString();

    }

}
