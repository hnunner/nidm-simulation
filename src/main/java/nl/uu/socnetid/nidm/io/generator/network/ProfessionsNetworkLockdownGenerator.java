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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.in.Professions;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ProfessionNetworkLockdownParameters;
import nl.uu.socnetid.nidm.io.csv.ProfessionNetworkLockdownWriter;
import nl.uu.socnetid.nidm.io.generator.AbstractGenerator;
import nl.uu.socnetid.nidm.io.network.DGSReader;
import nl.uu.socnetid.nidm.io.network.DGSWriter;
import nl.uu.socnetid.nidm.io.network.GEXFWriter;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.stats.NetworkStats;
import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ProfessionsNetworkLockdownGenerator extends AbstractGenerator {

    // logger
    private static final Logger logger = LogManager.getLogger(ProfessionsNetworkLockdownGenerator.class);

    // offset for lockdown
    private static final double OFFSET_LOCKDOWN_BOUNDS = 0.03;

    // network files to be put in lockdown
    // TODO write R script giving the best fitting networks per summary.csv in the appropriate format
    private static final List<String> NETWORK_FILES = Arrays.asList(
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_5(2-2-2-1)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_2_8(1-2-1-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_3_9(2-1-2-2)#1.dgs");
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_2_8(1-2-1-1)#2.dgs");
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/");

//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_5(2-2-2-1)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_2_8(1-2-1-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_3_9(2-1-2-2)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_8(3-1-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_8(3-1-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_6(2-2-2-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_6(2-2-2-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_5(2-2-2-1)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_2(2-2-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-161531/networks/professions.genetic/0_lc.pre_5_2(2-2-3-1)#2.dgs",
//
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_5(2-2-2-1)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_2_8(1-2-1-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_3_9(2-1-2-2)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_8(3-1-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_8(3-1-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_6(2-2-2-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_6(2-2-2-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_5(2-2-2-1)#1.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_2(2-2-3-1)#2.dgs",
//            "/Users/hendrik/git/uu/nidm/simulation/exports/20210316-1615312/networks/professions.genetic/0_lc.pre_5_2(2-2-3-1)#2.dgs");

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_12_1(11-2-10-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_11_2(10-1-9-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_13_3(12-1-10-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_19_1(12-1-11-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_10_5(9-2-3-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_13_7(11-2-10-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_12_3(11-2-9-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_9_2(7-2-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_10_4(9-2-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073048/networks/professions.genetic/0_lc.pre_14_6(12-1-10-1)#2.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_12_1(11-1-7-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_11_9(5-2-9-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_15_12(7-2-5-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_7_6(5-2-5-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_5_2(4-1-3-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_4_3(3-1-3-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_9_11(4-1-5-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_5_10(3-1-3-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_5_5(4-1-3-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073056/networks/professions.genetic/0_lc.pre_7_1(5-2-4-1)#1.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_6_12(2-2-5-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_14_7(4-1-6-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_17_5(6-2-6-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_4_9(2-2-3-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_6_8(2-1-2-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_2_7(1-1-1-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_2_12(1-2-1-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_5_9(2-1-2-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_7_12(6-2-2-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073105/networks/professions.genetic/0_lc.pre_11_9(4-1-2-1)#1.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_6_10(5-1-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_6_9(5-1-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_5_3(2-2-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_6_9(5-1-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_5_7(4-1-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_6_10(5-1-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_5_7(4-1-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_5_3(2-2-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_2_2(1-1-1-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073121/networks/professions.genetic/0_lc.pre_2_2(1-1-1-1)#2.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_6_11(5-1-2-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_9_10(4-2-8-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_12_10(9-2-4-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_15_5(6-1-14-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_15_9(9-2-14-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_14_2(6-1-9-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_17_1(6-1-9-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_17_8(9-2-12-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_18_9(9-2-15-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073129/networks/professions.genetic/0_lc.pre_10_2(6-1-9-2)#2.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_19_9(5-2-12-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_10_1(5-2-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_5_2(4-1-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_4_7(2-1-3-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_12_7(5-2-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_6_4(5-2-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_9_9(4-1-7-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_7_11(6-2-4-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_4_1(3-2-2-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073137/networks/professions.genetic/0_lc.pre_12_8(5-2-4-1)#2.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_18_6(4-2-17-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_4_8(2-1-3-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_4_2(3-2-2-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_14_12(13-1-5-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_17_5(4-2-13-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_14_12(13-1-5-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_4_2(3-2-2-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_13_5(4-2-3-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_5_12(3-2-2-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073144/networks/professions.genetic/0_lc.pre_3_6(2-1-1-1)#2.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_7_2(6-1-5-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_10_8(6-1-5-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_13_1(7-2-10-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_11_5(7-2-5-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_6_11(5-2-5-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_5_3(2-1-3-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_2_9(1-1-1-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_5_12(3-1-2-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_6_9(2-1-5-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073154/networks/professions.genetic/0_lc.pre_6_3(5-1-5-2)#1.dgs",

            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_11_3(8-1-9-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_8_3(7-2-7-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_12_11(7-2-9-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_7_10(4-1-3-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_9_5(8-1-6-1)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_10_8(7-2-9-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_8_6(7-2-4-1)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_19_6(11-1-7-2)#2.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_6_9(3-1-5-2)#1.dgs",
            "C:/Users/Hendrik/git/NIDM/simulation/exports/20210316-073200/networks/professions.genetic/0_lc.pre_8_11(7-1-4-1)#1.dgs");

    // stats & writer
    private DataGeneratorData<ProfessionNetworkLockdownParameters> dgData;
    private ProfessionNetworkLockdownWriter pnlWriter;


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
    public ProfessionsNetworkLockdownGenerator(String rootExportPath) throws IOException {
        super(rootExportPath);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#getFolderName()
     */
    @Override
    protected String getFolderName() {
        return "professions.lockdown";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#initData()
     */
    @Override
    protected void initData() {
        this.dgData = new DataGeneratorData<ProfessionNetworkLockdownParameters>(
                new ProfessionNetworkLockdownParameters());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractGenerator#initWriters()
     */
    @Override
    protected void initWriters() throws IOException {
        // summary CSV
        if (PropertiesHandler.getInstance().isExportSummary()) {
            this.pnlWriter = new ProfessionNetworkLockdownWriter(getExportPath() +
                    "profession-networks-lockdown.csv", this.dgData);
        }
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.generator.AbstractDataGenerator#generateData()
     */
    @Override
    protected void generate() {


        DGSReader dgsReader = new DGSReader();

        int uid = 1;
        int upc = 1;
        for (String networkFile : NETWORK_FILES) {

            logger.debug("Starting to put network " + networkFile + " in lockdown.");
            Network network = dgsReader.readNetwork(networkFile);

            // copy pre lockdown network
            File preLockdownDgsSource = new File(networkFile);
            File preLockdownDgsDest = new File(getExportPath() + upc + "-pre.dgs");
            try {
                FileUtils.copyFile(preLockdownDgsSource, preLockdownDgsDest);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // export as Gephi file
            GEXFWriter gexfWriter = new GEXFWriter();
            String gexfFilePre = getExportPath() + upc + "-pre.gexf";
            gexfWriter.writeStaticNetwork(network, gexfFilePre);

            // export pre lockdown stats
            this.dgData.getSimStats().setUid(String.valueOf(uid));
            this.dgData.getSimStats().setUpc(upc);
            Agent agent = network.getRandomAgent();
            this.dgData.getUtilityModelParams().setOmega(agent.getOmega());
            this.dgData.getUtilityModelParams().setAlpha(((NunnerBuskens) (agent.getUtilityFunction())).getAlpha());
            this.dgData.getUtilityModelParams().setCurrLockdownCondition(LockdownConditions.PRE);
            this.dgData.setNetStatsCurrent(new NetworkStats(network, gexfFilePre));
            this.dgData.setExportFileName(preLockdownDgsDest.getPath());
            this.pnlWriter.writeCurrentData();

            logger.debug("\n\nNetwork prior to lockdown:");
            debugNetwork(network, false);


            boolean netChangePrevRound = true;
            while (!inLockdown(network) && netChangePrevRound) {

                netChangePrevRound = false;
                Iterator<Edge> edges = network.getEdgeIterator();

                while (edges.hasNext()) {
                    Edge edge = edges.next();

                    Agent a0 = (Agent) edge.getNode0();
                    Agent a1 = (Agent) edge.getNode1();

                    // sever tie depending on 1. chance to sever tie based on the average degree reduction per professional group,
                    if (((ThreadLocalRandom.current().nextDouble() <=
                            (Professions.getInstance().getDegreeReductionLockdown(a0.getProfession())
                                    + Professions.getInstance().getDegreeReductionLockdown(a1.getProfession())) / 2))
                            // and 2. whether both agents are not yet in lockdown
                            && (!inLockdown(network, a0.getProfession()) && !inLockdown(network, a1.getProfession()))) {
                        a0.disconnectFrom(a1);
                        netChangePrevRound = true;
                    }
                }
            }

            // export DGS file
            DGSWriter dgsWriter = new DGSWriter();
            String fileName = getExportPath() + upc + "-during.dgs";
            dgsWriter.writeNetwork(network, fileName);
            this.dgData.setExportFileName(fileName);

            // export as Gephi file
            gexfWriter = new GEXFWriter();
            String gexfFileDuring = getExportPath() + upc + "-during.gexf";
            gexfWriter.writeStaticNetwork(network, gexfFileDuring);

            // export lockdown stats
            this.dgData.getSimStats().setUid(String.valueOf(uid));
            this.dgData.getSimStats().setUpc(upc++);
            this.dgData.getUtilityModelParams().setCurrLockdownCondition(LockdownConditions.DURING);
            this.dgData.setNetStatsCurrent(new NetworkStats(network, gexfFileDuring));
            this.pnlWriter.writeCurrentData();

            logger.debug("\n\nNetwork in lockdown:");
            debugNetwork(network, true);
        }


    }




    private boolean inLockdown(Network network, String profession) {

        double avDegree = network.getAvDegreeByProfession(profession);

        double avDegreeLockdown = Professions.getInstance().getDegreeDuringLockdown(profession);
//        double avDegreeLockdownLowerBound = avDegreeLockdown - (avDegreeLockdown * OFFSET_LOCKDOWN_BOUNDS);
        double avDegreeLockdownUpperBound = avDegreeLockdown + (avDegreeLockdown * OFFSET_LOCKDOWN_BOUNDS);

//        boolean inLockdown = (avDegreeLockdownLowerBound <= avDegree) && (avDegree <= avDegreeLockdownUpperBound);
        boolean inLockdown = avDegree <= avDegreeLockdownUpperBound;

        return inLockdown;
    }



    private boolean inLockdown(Network network) {
        boolean inLockdown = true;
        Iterator<String> professions = Professions.getInstance().getDegreesDuringLockdown().keySet().iterator();
        while (professions.hasNext()) {
            inLockdown &= inLockdown(network, professions.next());
        }

        return inLockdown;
    }




    /**
     * @param network
     */
    private void debugNetwork(Network network, boolean duringLockdown) {
        logger.debug("Av. degree (overall): \t\t" + String.format("%.2f", network.getAvDegree()));
        logger.debug("Av. clustering (overall): \t\t" + String.format("%.2f", network.getAvClustering()));

        int disconnected = 0;
        Iterator<Agent> agents = network.getAgentIterator();
        while (agents.hasNext()) {
            Agent agent = agents.next();
            if (agent.getConnections().size() == 0) {
                disconnected++;
            }
        }
        logger.debug("Agents disconnected: \t\t" + disconnected);

        Map<String, Double> degProf = network.getAvDegreesByProfessions();

        for (String profession : Professions.getInstance().getProfessions()) {
            String tabs = "\t\t\t";
            if (duringLockdown) {
                logger.debug("Av. degree (" + profession + "):" + tabs +
                        String.format("%.2f", degProf.get(profession)) +
                        "\t(exp: " + String.format("%.2f", Professions.getInstance().getDegreeDuringLockdown(profession)) + ")");
            } else {
                logger.debug("Av. degree (" + profession + "):" + tabs +
                        String.format("%.2f", degProf.get(profession)) +
                        "\t(exp: " + String.format("%.2f", Professions.getInstance().getDegreePreLockdown(profession)) + ")");
            }
        }
    }

}