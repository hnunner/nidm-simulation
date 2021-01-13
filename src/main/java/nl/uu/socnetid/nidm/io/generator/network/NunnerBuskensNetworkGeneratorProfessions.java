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
package nl.uu.socnetid.nidm.io.generator.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.AgeStructure;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.NunnerBuskensNetworkSummaryProfessionsWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.AgentPropertiesWriter;
import nl.uu.socnetid.nidm.io.network.EdgeListWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.io.network.NetworkCSVFileWriter;
import nl.uu.socnetid.nidm.networks.AssortativityConditions;
import nl.uu.socnetid.nidm.networks.DegreeDistributionConditions;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
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
public class NunnerBuskensNetworkGeneratorProfessions extends AbstractGenerator implements SimulationListener {

    // logger
    private static final Logger logger = LogManager.getLogger(NunnerBuskensNetworkGeneratorProfessions.class);

    // network
    private Network network;

    // simulation
    private Simulation simulation;

    // stats & writer
    private DataGeneratorData<NunnerBuskensProfessionsParameters> dgData;
    private NunnerBuskensNetworkSummaryProfessionsWriter nsWriter;


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
    public NunnerBuskensNetworkGeneratorProfessions(String rootExportPath) throws IOException {
        super(rootExportPath);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "nunnerbuskens.professions";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        NunnerBuskensProfessionsParameters nbpParams = PropertiesHandler.getInstance().getNunnerBuskensProfessionsParameters();
        this.dgData = new DataGeneratorData<NunnerBuskensProfessionsParameters>(nbpParams);
        this.dgData.getSimStats().setUpc(0);
        this.dgData.getUtilityModelParams().setN(nbpParams.getN());
        this.dgData.getUtilityModelParams().setPhi(nbpParams.getPhi());
        this.dgData.getUtilityModelParams().setPsi(nbpParams.getPsi());
        this.dgData.getUtilityModelParams().setXi(nbpParams.getXi());
        this.dgData.getUtilityModelParams().setZeta(nbpParams.getZeta());
        this.dgData.getUtilityModelParams().setB1(nbpParams.getB1());
        this.dgData.getUtilityModelParams().setB2(nbpParams.getB2());
        this.dgData.getUtilityModelParams().setC1(nbpParams.getC1());
        this.dgData.getUtilityModelParams().setAlpha(nbpParams.getAlpha());
        this.dgData.getUtilityModelParams().setConsiderAge(nbpParams.isConsiderAge());
        this.dgData.getUtilityModelParams().setConsiderProfession(nbpParams.isConsiderProfession());
        this.dgData.getUtilityModelParams().setAssortativityInitCondition(nbpParams.getAssortativityInitCondition());
        this.dgData.getUtilityModelParams().setAssortativityConditions(nbpParams.getAssortativityConditions());
        this.dgData.getUtilityModelParams().setOmega(nbpParams.getOmega());
        this.dgData.getUtilityModelParams().setSimIterations(nbpParams.getSimIterations());
        this.dgData.getUtilityModelParams().setLockdownConditions(nbpParams.getLockdownConditions());
        this.dgData.getUtilityModelParams().setDegreeDistributionConditions(nbpParams.getDegreeDistributionConditions());
        this.dgData.getUtilityModelParams().setRoundsMax(nbpParams.getRoundsMax());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.nsWriter = new NunnerBuskensNetworkSummaryProfessionsWriter(getExportPath() + "network-summary-professions.csv",
                    this.dgData);
        }
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {

        NunnerBuskensProfessionsParameters umps = this.dgData.getUtilityModelParams();

        Iterator<DegreeDistributionConditions> ddcsIt = umps.getDegreeDistributionConditions().iterator();
        while (ddcsIt.hasNext()) {
            DegreeDistributionConditions ddc = ddcsIt.next();

            Iterator<LockdownConditions> lcsIt = umps.getLockdownConditions().iterator();
            while (lcsIt.hasNext()) {
                LockdownConditions lc = lcsIt.next();

                // simulate iterations of same parameter settings
                for (int it = 0; it <= umps.getSimIterations(); it++) {

                    // init network
                    AssortativityConditions aic = umps.getAssortativityInitCondition();
                    switch (aic) {

                        case RISK_PERCEPTION:
                        case AGE:
                            logger.warn("Assortativity init condition not implemented: " + aic + ". Using profession instead.");
                            //$FALL-THROUGH$
                        default:
                        case PROFESSION:
                            initNetworkByProfessions(lc, ddc);
                            break;
                    }

                    // simulate
                    this.simulation = new Simulation(this.network);
                    simulation.addSimulationListener(this);
                    simulation.simulateUntilStable(umps.getRoundsMax());
                }
            }
        }

        finalizeDataExportFiles();
    }

    // TODO comments
    private void initNetworkByProfessions(LockdownConditions lc, DegreeDistributionConditions ddc) {

        NunnerBuskensProfessionsParameters umps = this.dgData.getUtilityModelParams();

        this.network = new Network("Network of the infectious kind", umps.getAssortativityConditions());
        DiseaseSpecs ds = new DiseaseSpecs(DiseaseType.SIR, 0, 0, 0, 0);    // not used

        double allC2s = 0;

        for (int i = 0; i < umps.getN(); i++) {

            String profession = Professions.getInstance().getRandomProfession();
            double degree = 0.0;
            double degreeError = 0.0;

            switch (lc) {
                case DURING:
                    degree = Professions.getInstance().getDegreeDuringLockdown(profession);
                    degreeError = Professions.getInstance().getDegreeErrorDuringLockdown(profession);
                    break;

                case POST:
                    logger.warn("Lockdown condition not implemented: " + lc + ". Using pre lockdown instead.");
                    //$FALL-THROUGH$
                case PRE:
                default:
                    degree = Professions.getInstance().getDegreePreLockdown(profession);
                    degreeError = Professions.getInstance().getDegreeErrorPreLockdown(profession);
                    break;
            }

            switch (ddc) {
                case EXP:
                    degree = new ExponentialDistribution(degree + (degree * degreeError)).sample();
                    break;
                case NONE:
                default:
                    // use unaltered degree
                    break;

            }

            double c2 = NunnerBuskens.getC2FromAvDegree(umps.getB1(), umps.getC1(), degree);
            allC2s += c2;

            // utility
            UtilityFunction uf = new NunnerBuskens(umps.getB1(), umps.getB2(), umps.getAlpha(), umps.getC1(), c2);

            // add agents
            this.network.addAgent(
                    uf,
                    ds,
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

        logger.info("Theoretic average degree: " + (Math.round(this.network.getTheoreticAvDegree() * 100.0) / 100.0));
        umps.setC2(allC2s/umps.getN());
        logger.info("Network initialization successful.");
        this.dgData.setAgents(new ArrayList<Agent>(this.network.getAgents()));
    }

    /**
     * Amends the summary file by writing a row with the current state of the network.
     */
    private void amendSummary() {
        this.dgData.setNetStatsCurrent(new NetworkStats(this.network, this.simulation.getRounds()));
        this.nsWriter.writeCurrentData();
    }

    /**
     * Exports the network as edge list, agent properties, and Gephi files.
     *
     *
     *
     * TODO do not overwrite previous files, but use new names and put names into summary
     *
     *
     */
    private void exportNetworks(String nameAppendix) {
        String fileName = this.dgData.getSimStats().getUid();
        if (nameAppendix != null && !nameAppendix.isEmpty()) {
            fileName += "-" + nameAppendix;
        }

        NetworkCSVFileWriter elWriter = new NetworkCSVFileWriter(getExportPath(),
                fileName + ".el",
                new EdgeListWriter(),
                this.network);
        elWriter.write();

        NetworkCSVFileWriter profWriter = new NetworkCSVFileWriter(getExportPath(),
                fileName + ".prof",
                new AgentPropertiesWriter(),
                this.network);
        profWriter.write();

        GEXFWriter gexfWriter = new GEXFWriter();
        gexfWriter.writeStaticNetwork(this.network, getExportPath() + fileName + ".gexf");
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
    public void notifyRoundFinished(Simulation simulation) {
        this.dgData.getSimStats().incCurrRound();
        amendSummary();             // amend summary CSV
        exportNetworks(null);       // overwrite networks with better fitness
    }

    @Override
    public void notifySimulationStarted(Simulation simulation) {
        this.dgData.getSimStats().resetCurrRound();
    }

    @Override
    public void notifyInfectionDefeated(Simulation simulation) {}

    @Override
    public void notifySimulationFinished(Simulation simulation) {}

}
