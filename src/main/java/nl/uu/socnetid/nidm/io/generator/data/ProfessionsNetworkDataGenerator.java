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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkDataParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.ProfessionNetworkDataWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ProfessionsNetworkDataGenerator extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsNetworkDataGenerator.class);

    // path to network files used to generate data
    private static final String NETWORKS_PATH = "/Users/hendrik/git/uu/nidm/simulation/exports/20210320-105001/"
            + "networks/professions.lockdown/";
    private static final String NETWORKS_SUMMARY_FILE = NETWORKS_PATH + "profession-networks-lockdown.csv";

    // PARAMETER VARIATIONS
    // vaccine efficacy
//    private static final List<Double> ETAS = Arrays.asList(0.6, 0.75, 0.9);
    private static final List<Double> ETAS = Arrays.asList(0.6, 0.9);
    // vaccine availibility
//    private static final List<Double> THETAS = Arrays.asList(0.05, 0.10, 0.2);
    private static final List<Double> THETAS = Arrays.asList(0.05, 0.10);
    // vaccine distribution
    private static final String VAX_DIST_NONE = "none";
    private static final String VAX_DIST_RANDOM = "random";
    private static final String VAX_DIST_BY_AV_DEGREE_PER_PROF_GROUP = "by.av.degree.per.prof.group";
    private static final List<String> VAX_DISTRIBUTIONS = Arrays.asList(
            VAX_DIST_NONE,
            VAX_DIST_RANDOM,
            VAX_DIST_BY_AV_DEGREE_PER_PROF_GROUP);
//    private static final int ITERATIONS = 50;

    private static final int ITERATIONS = 3;

    // network
    private Network network;

    // disease
    private DiseaseSpecs disease;
    private int epidemicPeakSize;

    // simulation
    private Simulation simulation;
    private List<String> indexCaseIds;

    // stats & writer
    private DataGeneratorData<ProfessionNetworkDataParameters> dgData;
    private ProfessionNetworkDataWriter pndWriter;


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
    public ProfessionsNetworkDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "professions.data";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<ProfessionNetworkDataParameters>(
                new ProfessionNetworkDataParameters(this.getImportColNames(), this.getNetworkSummaryLines()));

        this.dgData.getUtilityModelParams().setTau(10);
        this.dgData.getUtilityModelParams().setGamma(0.15);

        this.disease = new DiseaseSpecs(DiseaseType.SIRV,
                this.dgData.getUtilityModelParams().getTau(), 1.0, this.dgData.getUtilityModelParams().getGamma(), 0.0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.pndWriter = new ProfessionNetworkDataWriter(getExportPath() +
                    "profession-data.csv", this.dgData);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        DGSReader dgsReader = new DGSReader();

        // VARIATION OF: network properties (clustering, profession homophily, pre vs. during lockdown)
        List<String> networkFilePaths = this.getNetworkFilePaths();
        for (String networkFilePath : networkFilePaths) {

            logger.debug("Starting epidemic in network: " + networkFilePath);
            this.dgData.getUtilityModelParams().setCurrFile(networkFilePath);

            if (!(networkFilePath.contains("pre.dgs") ||  networkFilePath.contains("during.dgs"))) {
                throw new RuntimeException("Unable to determine lockdown condition for: " + networkFilePath);
            }
            boolean preLockdown = networkFilePath.contains("pre.dgs");
            if (preLockdown) {
                this.indexCaseIds = new ArrayList<String>();
            }

            // copy network
            File networkSource = new File(networkFilePath);
            String filename = networkSource.getPath().replace(NETWORKS_PATH, "");
            File networkDest = new File(getExportPath() + filename);
            try {
                FileUtils.copyFile(networkSource, networkDest);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // prepare network
            this.network = dgsReader.readNetwork(networkFilePath);
            this.network.updateDisease(this.disease);

            // VARIATION OF: vaccine distribution
            for (String vaxDist : VAX_DISTRIBUTIONS) {
                this.dgData.getUtilityModelParams().setCurrVaxDist(vaxDist);

                if (vaxDist.equals(VAX_DIST_NONE)) {
                    // baseline epidemic
                    // ITERATIONS
                    for (int iteration = 1; iteration <= ITERATIONS; iteration++) {
                        this.dgData.getSimStats().setSimIt(iteration);

                        logger.info("Starting epidemic " + iteration + " of " + ITERATIONS
                                + " for baseline of current parameter combination.");

                        this.dgData.getSimStats().setShotsGiven(0);
                        this.dgData.getSimStats().setAgentsImmunized(0);
                        this.dgData.getSimStats().setProfessionsReceivedShots(null);

                        runEpidemic(preLockdown);
                    }
                    continue;
                }

                // VARIATION OF: vaccine availibility
                for (double theta : THETAS) {
                    this.dgData.getUtilityModelParams().setCurrTheta(theta);

                    // VARIATION OF: vaccine efficacy
                    for (double eta : ETAS) {
                        this.dgData.getUtilityModelParams().setCurrEta(eta);

                        // ITERATIONS OF SAME PARAMETER SETTINGS
                        for (int iteration = 1; iteration <= ITERATIONS; iteration++) {
                            this.dgData.getSimStats().setSimIt(iteration);

                            logger.info("\n\nStarting epidemic " + iteration + " of " + ITERATIONS
                                    + " for current parameter combination.");

                            // baseline
                            if (vaxDist.equals(VAX_DIST_NONE)) {
                                logger.warn("Baseline has been simulated before. Skipping iteration!");
                            }

                            // random vaccine distribution
                            else if (vaxDist.equals(VAX_DIST_RANDOM)) {
                                List<Agent> agents = new ArrayList<Agent>(this.network.getAgents());
                                Collections.shuffle(agents);
                                deliverShots(theta, eta, agents);
                                runEpidemic(preLockdown);
                            }

                            // vaccine distribution according to largest degree by profession
                            else if (vaxDist.equals(VAX_DIST_BY_AV_DEGREE_PER_PROF_GROUP)) {
                                List<Agent> agents = new ArrayList<Agent>();
                                Iterator<String> profsByDegree = preLockdown ?
                                        Professions.getInstance().getProfessionsOrderedByAvDegreeBeforeLockdown().iterator() :
                                            Professions.getInstance().getProfessionsOrderedByAvDegreeDuringLockdown().iterator();
                                while (profsByDegree.hasNext()) {
                                    List<Agent> agentsByProfession = new ArrayList<Agent>(this.network.getAgents(profsByDegree.next()));
                                    Collections.shuffle(agentsByProfession);
                                    agents.addAll(agentsByProfession);
                                }
                                deliverShots(theta, eta, agents);
                                runEpidemic(preLockdown);
                            }

                            // undefined distribution
                            else {
                                logger.warn("Unknown vaccination distribution: '" + vaxDist + "'. Vaccination skipped!");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param theta
     * @param eta
     * @param agents
     */
    private void deliverShots(double theta, double eta, List<Agent> agents) {
        int shotsGiven = 0;
        int agentsImmunized = 0;
        Map<String, Integer> professionsReceivedShots = new HashMap<String, Integer>();
        Iterator<Agent> agentsIt = agents.iterator();

        while ((Double.valueOf(shotsGiven) / Double.valueOf(this.network.getN()) < theta) &&
                (agentsIt.hasNext())) {

            Agent agent = agentsIt.next();

            // immunization successful?
            if (ThreadLocalRandom.current().nextDouble() < eta) {
                agent.vaccinate();
                agentsImmunized++;
            }

            // shot given in any case
            if (!professionsReceivedShots.containsKey(agent.getProfession())) {
                professionsReceivedShots.put(agent.getProfession(), 1);
            } else {
                professionsReceivedShots.put(agent.getProfession(),
                        professionsReceivedShots.get(agent.getProfession())+1);
            }
            shotsGiven++;
        }
        this.dgData.getSimStats().setShotsGiven(shotsGiven);
        this.dgData.getSimStats().setAgentsImmunized(agentsImmunized);
        this.dgData.getSimStats().setProfessionsReceivedShots(professionsReceivedShots);
    }

    /**
     *
     */
    private void runEpidemic(boolean preLockdown) {

        // select index case
        Agent indexCase = null;
        if (preLockdown) {
            do {
                indexCase = network.getRandomSusceptibleAgent();
            } while (indexCase.getDegree() <= 0);
            this.indexCaseIds.add(indexCase.getId());
        } else {
            indexCase = this.network.getAgent(this.indexCaseIds.get(0));
            this.indexCaseIds.remove(0);
        }
        // infect index case
        indexCase.forceInfect(this.disease);

        // run epidemic
        this.simulation = new Simulation(this.network);
        this.simulation.addSimulationListener(this);
        this.dgData.setIndexCaseStats(new AgentStatsPre(indexCase, this.simulation.getRounds()));
        this.dgData.getSimStats().setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
        this.simulation.simulateUntilEpidemicFinished();
        this.network.resetDiseaseStates();
    }

    private List<String> getNetworkFilePaths() {

        List<String> networkFilePaths = new LinkedList<String>();
        String lineSep = ";";

        try{
            BufferedReader br = new BufferedReader(new FileReader(NETWORKS_SUMMARY_FILE));
            String line = null;
            int exportFilenameIndex = -1;

            while ((line = br.readLine()) != null) {
                String[] cols =line.split(lineSep);

                if (exportFilenameIndex == -1) {
                    for (int colIndex = 0; colIndex < cols.length; colIndex++) {
                        if (cols[colIndex].equals("export.filename")) {
                            exportFilenameIndex = colIndex;
                        }
                    }
                } else {
                    networkFilePaths.add(cols[exportFilenameIndex]);
                }
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return networkFilePaths;
    }

    private List<String> getImportColNames() {

        List<String> colNames = null;
        String lineSep = ";";

        try{
            BufferedReader br = new BufferedReader(new FileReader(NETWORKS_SUMMARY_FILE));
            String line = br.readLine();
            line = line.replace("export.filename", "import.filename");
            colNames = new ArrayList<String>(Arrays.asList(line.split(lineSep)));
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return colNames;
    }

    private Map<String, List<String>> getNetworkSummaryLines() {

        Map<String, List<String>> networkSummaryLines = new HashMap<String, List<String>>();
        String lineSep = ";";

        try{
            BufferedReader br = new BufferedReader(new FileReader(NETWORKS_SUMMARY_FILE));
            String line = null;
            int exportFilenameIndex = -1;

            while ((line = br.readLine()) != null) {
                String[] cols =line.split(lineSep);

                if (exportFilenameIndex == -1) {
                    for (int colIndex = 0; colIndex < cols.length; colIndex++) {
                        if (cols[colIndex].equals("export.filename")) {
                            exportFilenameIndex = colIndex;
                        }
                    }
                } else {
                    networkSummaryLines.put(cols[exportFilenameIndex], Arrays.asList(line));
                }
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return networkSummaryLines;
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {
        this.epidemicPeakSize = 0;
    }

    @Override
    public void notifyRoundFinished(Simulation simulation) {
        int epidemicSize = simulation.getNetwork().getInfected().size();
        if (epidemicSize > this.epidemicPeakSize) {
            this.epidemicPeakSize = epidemicSize;
            this.dgData.getSimStats().setEpidemicPeakSizeStatic(epidemicSize);
            this.dgData.getSimStats().setEpidemicPeakStatic(simulation.getRounds());
        }
    }

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {
        this.dgData.setNetStatsPostStatic(new NetworkStatsPost(this.network));
        this.dgData.getSimStats().setEpidemicDurationStatic(this.simulation.getRounds());
    }

    @Override
    public void notifySimulationFinished(Simulation simulation) {
        this.pndWriter.writeCurrentData();
    }

}
