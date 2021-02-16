/*
 * Copyright (C) 2017 - 2021
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensProfessionsAgentDetailsWriter;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensProfessionsRoundSummaryWriter;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensProfessionsSimulationSummaryWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.mains.Generator;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.simulation.Simulation;
import nl.uu.socnetid.nidm.simulation.SimulationListener;
import nl.uu.socnetid.nidm.simulation.SimulationStage;
import nl.uu.socnetid.nidm.stats.AgentStats;
import nl.uu.socnetid.nidm.stats.AgentStatsPre;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.stats.NetworkStatsPost;
import nl.uu.socnetid.nidm.stats.NetworkStatsPre;
import nl.uu.socnetid.nidm.stats.SimulationStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;
import nl.uu.socnetid.nidm.utility.UtilityFunction;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class NunnerBuskensProfessionsDataGenerator extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensProfessionsDataGenerator.class);

    // network
    private Network network;
    private Map<String, LockdownConditions> conditionsByProfession ;

    // disease
    private DiseaseSpecs disease;

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<NunnerBuskensProfessionsParameters> dgData;
    private NunnerBuskensProfessionsSimulationSummaryWriter ssWriter;
    private NunnerBuskensProfessionsRoundSummaryWriter rsWriter;
    private NunnerBuskensProfessionsAgentDetailsWriter adWriter;
    private Double errorLastRound;
    private double errorThisRound;
    private String dgsFileNameBestMatch;
    private double avDegreeTheoretic;
    private Map<String, Double> avDegreesByProfessionTheoretic;
    private Map<String, Double> degreesSdByProfessionTheoretic;
    private int quarantined;
    private int epidemicPeakSize;

    @SuppressWarnings("unused")
    private double degreeDiffTotal;
    @SuppressWarnings("unused")
    private double degreeDiffPercent;


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
    public NunnerBuskensProfessionsDataGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);

        // copy profession files to export directory
        try {
            Path srcProfessions =
                    Paths.get(Generator.class.getClassLoader().getResource("professions.csv").toURI());
            Path dstProfessions = Paths.get(getExportPath()  + "/professions.csv");
            Files.copy(srcProfessions, dstProfessions, StandardCopyOption.REPLACE_EXISTING);

            Path srcProfessionDistribution =
                    Paths.get(Generator.class.getClassLoader().getResource("profession-dist.csv").toURI());
            Path dstProfessionDistribution = Paths.get(getExportPath()  + "/profession-dist.csv");
            Files.copy(srcProfessionDistribution, dstProfessionDistribution, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens.professions.epidemics";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {

        NunnerBuskensProfessionsParameters nbpParams = PropertiesHandler.getInstance().getNunnerBuskensProfessionsParameters();
        this.dgData = new DataGeneratorData<NunnerBuskensProfessionsParameters>(nbpParams);
        this.dgData.getUtilityModelParams().setN(nbpParams.getN());
        this.dgData.getUtilityModelParams().setPhi(nbpParams.getPhi());
        this.dgData.getUtilityModelParams().setPsi(nbpParams.getPsi());
        this.dgData.getUtilityModelParams().setXi(nbpParams.getXi());
        this.dgData.getUtilityModelParams().setZeta(nbpParams.getZeta());
        this.dgData.getUtilityModelParams().setB1(nbpParams.getB1());
        this.dgData.getUtilityModelParams().setB2(nbpParams.getB2());
        this.dgData.getUtilityModelParams().setC1(nbpParams.getC1());
        this.dgData.getUtilityModelParams().setGamma(nbpParams.getGamma());
        this.dgData.getUtilityModelParams().setTau(nbpParams.getTau());
        this.dgData.getUtilityModelParams().setConsiderAge(nbpParams.isConsiderAge());
        this.dgData.getUtilityModelParams().setConsiderProfession(nbpParams.isConsiderProfession());
        this.dgData.getUtilityModelParams().setAssortativityInitCondition(nbpParams.getAssortativityInitCondition());
        this.dgData.getUtilityModelParams().setAssortativityConditions(nbpParams.getAssortativityConditions());
        this.dgData.getUtilityModelParams().setSimsPerParameterCombination(nbpParams.getSimsPerParameterCombination());
        this.dgData.getUtilityModelParams().setLockdownConditions(nbpParams.getLockdownConditions());
        this.dgData.getUtilityModelParams().setDegreeDistributionConditions(nbpParams.getDegreeDistributionConditions());
        this.dgData.getUtilityModelParams().setRoundsMax(nbpParams.getRoundsMax());


        this.disease = new DiseaseSpecs(DiseaseType.SIR, this.dgData.getUtilityModelParams().getTau(), 1.0,
                this.dgData.getUtilityModelParams().getGamma(), 0.0);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.ssWriter = new NunnerBuskensProfessionsSimulationSummaryWriter(getExportPath() + "simulation-summary.csv",
                    this.dgData);
        }
        // round summary CSV
        if (PropertiesHandler.getInstance().isExportSummaryEachRound()) {
            this.rsWriter = new NunnerBuskensProfessionsRoundSummaryWriter(getExportPath() + "round-summary.csv", this.dgData);
        }
        // agent details
        if (PropertiesHandler.getInstance().isExportAgentDetails() ||
                PropertiesHandler.getInstance().isExportAgentDetailsReduced()) {
            this.adWriter = new NunnerBuskensProfessionsAgentDetailsWriter(getExportPath() + "agent-details.csv", this.dgData);
        }
    }

    private class CounterMeasure {
        private final List<String> vaccinationGroups;
        private final List<String> quarantineGroups;

        /**
         * Constructor
         *
         * @param vaccinationGroups
         *          groups to be vaccinated
         * @param quarantineGroups
         *          groups to be quarantined
         */
        public CounterMeasure(List<String> vaccinationGroups, List<String> quarantineGroups) {
            this.vaccinationGroups = vaccinationGroups;
            this.quarantineGroups = quarantineGroups;
        }

        /**
         * @return the vaccinationGroups
         */
        public List<String> getVaccinationGroups() {
            return vaccinationGroups;
        }

        /**
         * @return the quarantineGroups
         */
        public List<String> getQuarantineGroups() {
            return quarantineGroups;
        }

    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        // VARIATION OF: scenarios
        List<CounterMeasure> scenarios = new ArrayList<CounterMeasure>();
        // scenario 1
        scenarios.add(new CounterMeasure(
                // vaccinated: none
                new ArrayList<String>(
                        Arrays.asList(Professions.NONE)),
                // quarantined: none
                new ArrayList<String>(
                        Arrays.asList(Professions.NONE))));
        // scenario 2
        scenarios.add(new CounterMeasure(
                // vaccinated: all
                new ArrayList<String>(
                        Arrays.asList(Professions.ALL)),
                // quarantined: all
                new ArrayList<String>(
                        Arrays.asList(Professions.ALL))));
        // scenario 3
        scenarios.add(new CounterMeasure(
                // vaccinated: all
                new ArrayList<String>(
                        Arrays.asList(Professions.NONE)),
                // quarantined: all
                new ArrayList<String>(
                        Arrays.asList(Professions.ALL))));
        // scenario 4
        scenarios.add(new CounterMeasure(
                // vaccinated: all
                new ArrayList<String>(
                        Arrays.asList(Professions.ALL)),
                // quarantined: all
                new ArrayList<String>(
                        Arrays.asList(Professions.NONE))));
      // scenario 5
      scenarios.add(new CounterMeasure(
              // vaccinated: health-related professions
              new ArrayList<String>(
                      Arrays.asList("HPT", "EIL", "PCS")),
              // quarantined: none
              new ArrayList<String>(
                      Arrays.asList(Professions.NONE))));
        // scenario 6
        scenarios.add(new CounterMeasure(
                // vaccinated: health-related professions
                new ArrayList<String>(
                        Arrays.asList("HPT", "EIL", "PCS")),
                // quarantined: rest
                new ArrayList<String>(
                        Arrays.asList("AE", "ADESM", "CM", "BFO", "CSS", "CM", "CE", "FFF", "FPS", "HS", "IMR", "L",
                                "LPSS", "M", "OAS", "P", "PS", "S", "TMM", "R", "U"))));

        Iterator<CounterMeasure> sIt = scenarios.iterator();
        while (sIt.hasNext()) {
            CounterMeasure scenario = sIt.next();

            // VARIATION OF: clustering
            double[] alphas = {0.4, 0.6};
            for (double alpha : alphas) {
                this.dgData.getUtilityModelParams().setAlpha(alpha);

                // VARIATION OF: profession assortativity
                double[] omegas = {0.0, 0.9};
                for (double omega : omegas) {
                    this.dgData.getUtilityModelParams().setOmega(omega);

                    // VARIATION OF: vaccine efficacy
                    List<Double> etas = Arrays.asList(0.0);
                    if (scenario.getVaccinationGroups() != null &&
                            !scenario.getVaccinationGroups().isEmpty() &&
                            !(scenario.getVaccinationGroups().size() == 1 &&
                            scenario.getVaccinationGroups().get(0).equals(Professions.NONE))) {
                        etas = Arrays.asList(0.6, 0.9);
                    }
                    for (double eta : etas) {
                        this.dgData.getUtilityModelParams().setEta(eta);

                        logger.info("Starting new scenario.");
                        runSimulation(scenario);
                    }
                }
            }

        }

        finalizeDataExportFiles();
    }

    /**
     *
     */
    private void runSimulation(CounterMeasure scenario) {

        NunnerBuskensProfessionsParameters umps = this.dgData.getUtilityModelParams();
        SimulationStats simStats = this.dgData.getSimStats();

        errorLastRound = null;
        errorThisRound = 0.0;
        degreeDiffTotal = 0.0;
        degreeDiffPercent = 0.0;

        simStats.incUpc();
        int sims = this.dgData.getUtilityModelParams().getSimsPerParameterCombination();

        for (int sim = 0; sim < sims; sim++) {

            logger.info("Starting " + (sim+1) + " of " + sims + " simulations.");

            simStats.setSimPerUpc(sim+1);

            // init utilities
            // VARIATION OF: network composition (random number per e.g. professional group)
            AssortativityConditions aic = umps.getAssortativityInitCondition();
            switch (aic) {

                case RISK_PERCEPTION:
                case AGE:
                    logger.warn("Assortativity init condition not implemented: " + aic + ". Using profession instead.");
                    //$FALL-THROUGH$
                default:
                case PROFESSION:
                    List<String> quarantineGroups = scenario.getQuarantineGroups();
                    initUtilityByProfessions(quarantineGroups);
                    umps.setQuarantined(quarantineGroups);
                    break;
            }

            // init network structure
            logger.info("Starting network initialization.");
            this.simulation = new Simulation(this.network);
            this.simulation.addSimulationListener(this);
            simStats.setSimStage(SimulationStage.PRE_EPIDEMIC);
            this.simulation.simulate(umps.getRoundsMax());
            logger.info("Network initialization successful.");
            logger.info("#############################################################");

            // get best matching network
            this.network = new DGSReader().readNetwork(this.dgsFileNameBestMatch);
            this.dgData.setNetStatsPre(new NetworkStatsPre(this.network, this.simulation.getRounds()));
            this.dgData.getNetStatsPre().setAvDegreeTheoretic(this.avDegreeTheoretic);
            this.dgData.getNetStatsPre().setAvDegreesByProfessionTheoretic(this.avDegreesByProfessionTheoretic);
            this.dgData.getNetStatsPre().setDegreesSdByProfessionTheoretic(this.degreesSdByProfessionTheoretic);

            int its = this.dgData.getUtilityModelParams().getSimIterations();

            for (int it = 0; it < its; it++) {

                logger.info("Starting " + (it+1) + " of " + sims + " epidemics.");

                simStats.setSimIt(it+1);

                // reset disease states
                this.network.resetDiseaseStates();

                // vaccinate
                List<String> vaccinationGroups = scenario.getVaccinationGroups();
                if (vaccinationGroups != null && !vaccinationGroups.isEmpty()) {
                    Iterator<String> tbV = vaccinationGroups.iterator();
                    while (tbV.hasNext()) {
                        this.network.vaccinate(tbV.next(), umps.getEta());
                    }
                }
                umps.setVaccinated(vaccinationGroups);

                // run epidemic
                Agent indexCase = null;
                do {
                    indexCase = this.network.getRandomSusceptibleAgent();
                } while (indexCase.getDegree() <= 0);
                logger.info("Force infecting index case: " + indexCase.getId());
                indexCase.forceInfect(this.disease);

                this.simulation = new Simulation(this.network);
                this.simulation.addSimulationListener(this);
                this.dgData.setIndexCaseStats(new AgentStatsPre(indexCase, this.simulation.getRounds()));
                simStats.setSimStage(SimulationStage.ACTIVE_EPIDEMIC);
                this.simulation.simulateUntilEpidemicFinished();

                logger.info("Epidemic " + (it+1) + " of " + sims + " finished after " + this.simulation.getRounds() + " rounds.");

                this.dgData.setNetStatsCurrent(new NetworkStats(this.network, this.simulation.getRounds()));
                this.dgData.setNetStatsPostStatic(new NetworkStatsPost(this.network));
                this.dgData.getNetStatsPostStatic().setQuarantined(this.quarantined);
                simStats.setEpidemicDurationStatic(this.simulation.getRounds());
                this.ssWriter.writeCurrentData();

                logger.info((it+1) + " of " + sims + " epidemics finished.");

            }
        }
    }


    // TODO comments + unify with NunnerBuskensNetworkGeneratorProfessions
    private void initUtilityByProfessions(List<String> toBeQuarantined) {       //, DegreeDistributionConditions ddc) {

        NunnerBuskensProfessionsParameters umps = this.dgData.getUtilityModelParams();

        this.network = new Network("Network of the infectious kind", umps.getAssortativityConditions());
        this.conditionsByProfession = new HashMap<String, LockdownConditions>();

        this.avDegreesByProfessionTheoretic = new HashMap<String, Double>();
        this.degreesSdByProfessionTheoretic = new HashMap<String, Double>();

        double allC2s = 0;
        this.quarantined = 0;

        for (int i = 0; i < umps.getN(); i++) {

            String profession = Professions.getInstance().getRandomProfession();
            double degree = 0.0;
            double degreeError = 0.0;

            if (toBeQuarantined != null &&
                    ((toBeQuarantined.size() == 1 && toBeQuarantined.contains(Professions.ALL)) ||
                            toBeQuarantined.contains(profession))) {
                degree = Professions.getInstance().getDegreeDuringLockdown(profession);
                degreeError = Professions.getInstance().getDegreeErrorDuringLockdown(profession);
                if (!this.conditionsByProfession.containsKey(profession)) {
                    this.conditionsByProfession.put(profession, LockdownConditions.DURING);
                }
                this.quarantined++;

                logger.debug("Quarantined: " + profession + "(" + degree + ")");

            } else {
                degree = Professions.getInstance().getDegreePreLockdown(profession);
                degreeError = Professions.getInstance().getDegreeErrorPreLockdown(profession);
                if (!this.conditionsByProfession.containsKey(profession)) {
                    this.conditionsByProfession.put(profession, LockdownConditions.PRE);
                }


                logger.debug("Not quarantined: " + profession + "(" + degree + ")");

            }

            if (!this.avDegreesByProfessionTheoretic.containsKey(profession)) {
                this.avDegreesByProfessionTheoretic.put(profession, degree);
            }
            if (!this.degreesSdByProfessionTheoretic.containsKey(profession)) {
                this.degreesSdByProfessionTheoretic.put(profession, degreeError);
            }

//            switch (ddc) {
//                case EXP:
//                    degree = new ExponentialDistribution(degree + (degree * degreeError)).sample();
//                    break;
//                case NONE:
//                default:
//                    // use unaltered degree
//                    break;
//
//            }

            double c2 = NunnerBuskens.getC2FromAvDegree(umps.getB1(), umps.getC1(), degree);
            allC2s += c2;

            // utility
            UtilityFunction uf = new NunnerBuskens(umps.getB1(), umps.getB2(), umps.getAlpha(), umps.getC1(), c2);

            // add agents
            this.network.addAgent(
                    uf,
                    this.disease,
                    1.0,
                    1.0,
                    umps.getPhi(),
                    umps.getOmega(),
                    umps.getPsi(),
                    umps.getXi(),
                    AgeStructure.getInstance().getRandomAge(),
                    umps.isConsiderAge(),
                    profession,
                    umps.isConsiderProfession());
        }

        this.avDegreeTheoretic = this.network.getTheoreticAvDegree();

        this.dgData.setAgents(new ArrayList<Agent>(this.network.getAgents()));
        umps.setC2(allC2s/umps.getN());
        logger.info("Utility initialization successful with theoretic average degree: " +
                (Math.round(this.avDegreeTheoretic * 100.0) / 100.0) + " and quarantined agents: " + this.quarantined);
    }


    /**
     * Amends the summary file by writing a row with the current state of the network.
     */
    private void amendRoundWriters() {
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network, this.simulation.getRounds()));
        HashMap<String, AgentStats> agentStats = new HashMap<String, AgentStats>();
        Iterator<Agent> aIt = this.network.getAgentIterator();
        while (aIt.hasNext()) {
            Agent agent = aIt.next();
            agentStats.put(agent.getId(), new AgentStats(agent, this.simulation.getRounds(), false));
        }
        this.dgData.setAgentStatsCurrent(agentStats);

        this.rsWriter.writeCurrentData();
        this.adWriter.writeCurrentData();
    }

    /**
     * Exports the network.
     */
    private void exportNetwork() {
        String fileName = this.dgData.getSimStats().getUpc() + "-" + this.dgData.getSimStats().getSimPerUpc();
        this.dgData.setExportFileName(fileName);

//        NetworkFileWriter elWriter = new NetworkFileWriter(getExportPath(),
//                fileName + ".el",
//                new EdgeListWriter(),
//                this.network);
//        elWriter.write();
//
//        NetworkFileWriter profWriter = new NetworkFileWriter(getExportPath(),
//                fileName + ".props",
//                new AgentPropertiesWriter(),
//                this.network);
//        profWriter.write();
//
//        GEXFWriter gexfWriter = new GEXFWriter();
//        gexfWriter.writeStaticNetwork(this.network, getExportPath() + fileName + ".gexf");

        DGSWriter dgsWriter = new DGSWriter();
        dgsFileNameBestMatch = getExportPath() + fileName + ".dgs";
        dgsWriter.writeNetwork(this.network, dgsFileNameBestMatch);
        logger.info("Network successfully exported to: " + this.dgsFileNameBestMatch);
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

    @Override
    public void notifyRoundFinished(Simulation simulation) {

        SimulationStats simStats = this.dgData.getSimStats();
        simStats.setRounds(simulation.getRounds());

        switch (this.dgData.getSimStats().getSimStage()) {
            case ACTIVE_EPIDEMIC:
                int epidemicSize = simulation.getNetwork().getInfected().size();
                if (epidemicSize > this.epidemicPeakSize) {
                    this.epidemicPeakSize = epidemicSize;
                    this.dgData.getSimStats().setEpidemicPeakSizeStatic(epidemicSize);
                    this.dgData.getSimStats().setEpidemicPeakStatic(simulation.getRounds());
                }
                amendRoundWriters();
                break;

            case PRE_EPIDEMIC:

                logger.info("#############################################################");
                double newDiffTotal = Professions.getInstance().getDegreeDiffTotal(this.conditionsByProfession, this.network);
                double newDiffPercent = Professions.getInstance().getDegreeDiffPercent(this.conditionsByProfession, this.network);
                if (this.errorLastRound == null) {
                    this.errorLastRound = newDiffPercent;
                    logger.info("Initial degree percentage error: " + Math.round(this.errorLastRound * 100.0) / 100.0);
                    exportNetwork();
                    this.degreeDiffTotal = newDiffTotal;
                    this.degreeDiffPercent = newDiffPercent;
                } else {
                    if (newDiffPercent < this.errorLastRound) {
                        logger.info("Error has improved - from: " + Math.round(this.errorLastRound * 10000.0) / 10000.0
                                + " to: " + Math.round(newDiffPercent * 10000.0) / 10000.0);
                        exportNetwork();
                        this.degreeDiffTotal = newDiffTotal;
                        this.degreeDiffPercent = newDiffPercent;
                    }
                    this.errorLastRound = this.errorThisRound;
                    this.errorThisRound = newDiffPercent;
                }

                logger.info("Network initialization round " + simStats.getRounds() + " of " +
                        this.dgData.getUtilityModelParams().getRoundsMax() + " finished.");
                break;

            case POST_EPIDEMIC:
            case FINISHED:
            default:
                break;
        }
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {
        this.epidemicPeakSize = 0;
    }

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {}

    @Override
    public void notifySimulationFinished(Simulation simulation) {}

}
