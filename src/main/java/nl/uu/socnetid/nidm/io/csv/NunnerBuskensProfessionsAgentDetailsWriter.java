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
package nl.uu.socnetid.nidm.io.csv;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.LogValues;
import nl.uu.socnetid.nidm.data.out.NunnerBuskensProfessionsParameters;
import nl.uu.socnetid.nidm.diseases.types.DiseaseGroup;
import nl.uu.socnetid.nidm.stats.AgentStats;

/**
 * @author Hendrik Nunner
 */
public class NunnerBuskensProfessionsAgentDetailsWriter extends CsvFileWriter<NunnerBuskensProfessionsParameters> {

    /**
     * Creates the writer.
     *
     * @param fileName
     *          the name of the file to store the data to
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public NunnerBuskensProfessionsAgentDetailsWriter(String fileName,
            DataGeneratorData<NunnerBuskensProfessionsParameters> dgData) throws IOException {
        super(fileName, dgData);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();

        // INDEPENDENT VARIABLES
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        cols.add(LogValues.IV_SIM_IT.toString());
        cols.add(LogValues.IV_SIM_ROUND.toString());
        cols.add(LogValues.IV_AGENT_ID.toString());

        // model parameters
        cols.add(LogValues.IV_NB_PROF_C2.toString());

        // agent
        cols.add(LogValues.DV_AGENT_PROFESSION.toString());
        cols.add(LogValues.DV_AGENT_DEGREE1.toString());
        cols.add(LogValues.DV_AGENT_INFECTED.toString());
        cols.add(LogValues.DV_AGENT_FORCE_INFECTED.toString());

        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {

        List<Agent> agents = this.dgData.getAgents();
        Collections.sort(agents);

        for (Agent agent : agents) {

            // a single CSV row
            List<String> currData = new LinkedList<String>();

            // INDEPENDENT VARIABLES
            // simulation
            currData.add(this.dgData.getSimStats().getUid());
            currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
            currData.add(String.valueOf(this.dgData.getSimStats().getSimIt()));
            currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));
            currData.add(agent.getId());

            // model parameters
            currData.add(String.valueOf(this.dgData.getUtilityModelParams().getC2()));

            // agent
            AgentStats agentStats = this.dgData.getAgentStatsCurrent().get(agent.getId());
            currData.add(String.valueOf(agentStats.getProfession()));
            currData.add(String.valueOf(agentStats.getDegree1()));
            currData.add(agentStats.getDiseaseGroup() == DiseaseGroup.INFECTED
                    || agentStats.getDiseaseGroup() == DiseaseGroup.RECOVERED ? "1" : "0");
            currData.add(agentStats.isForceInfected() ? "1" : "0");

            writeLine(currData);
        }

    }
}
