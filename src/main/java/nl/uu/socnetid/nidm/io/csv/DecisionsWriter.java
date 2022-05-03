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
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ExperimentParameters;


/**
 * @author Hendrik Nunner
 */
public class DecisionsWriter extends CsvFileWriter<ExperimentParameters> {

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
    public DecisionsWriter(String fileName, DataGeneratorData<ExperimentParameters> dgData) throws IOException {
        super(fileName, dgData);
    }
    
    public DataGeneratorData<ExperimentParameters> getDgData() {
    	return this.dgData;
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {
        List<String> cols = new LinkedList<String>();

        cols.add("rationality");
        cols.add("overestimate");
        cols.add("rationality.infected.neighbor");
        
        cols.add("session.id");
        cols.add("level.game");
        cols.add("level.node");
        cols.add("level.round");
        cols.add("level.offer");
        
        cols.add("offer.type");
        cols.add("offer.accept");
        
        cols.add("cond.clustering");
        cols.add("cond.mixing");
        
        cols.add("dstate.node");
        cols.add("dstate.offer");
//        cols.add("offers.by.type");
        
        cols.add("util.before.network");
        cols.add("util.before.disease");
        cols.add("util.before.total");
        cols.add("util.exp.after.dec.network");
        cols.add("util.exp.after.dec.disease");
        cols.add("util.exp.after.dec.total");
        cols.add("util.exp.after.dec.opp.network");
        cols.add("util.exp.after.dec.opp.disease");
        cols.add("util.exp.after.dec.opp.total");
//        cols.add("util.exp.after.all.network");
//        cols.add("util.exp.after.all.disease");
//        cols.add("util.exp.after.all.total");
        
        cols.add("net.ties");
        cols.add("net.ties.inf");
        cols.add("net.prop.triads.closed");
        cols.add("net.prop.triads.closed.exp.after.dec");
//        cols.add("net.prop.triads.closed.exp.after.all");
//        cols.add("net.avpathlength");
        cols.add("net.homophily");
        
        cols.add("rscore");

        writeLine(cols);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
    	
    	List<String> currData = new LinkedList<String>();
    	
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRationality()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getOverestimate()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getRationalityInfectedNeighbor()));
    	
    	currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
    	currData.add(String.valueOf((this.dgData.getSimStats().getSimPerUpc() * 2) + (this.dgData.getSimStats().getSimIt() - 2)));
    	currData.add(this.dgData.getUtilityModelParams().getEgoId());
    	currData.add(String.valueOf(this.dgData.getSimStats().getRounds()));
    	currData.add(this.dgData.getUtilityModelParams().getAlterId());

    	currData.add(this.dgData.getUtilityModelParams().getOfferType());
    	currData.add(this.dgData.getUtilityModelParams().getOfferAccept());
    	
    	currData.add(this.dgData.getUtilityModelParams().getCondClustering());
    	currData.add(this.dgData.getUtilityModelParams().getCondMixing());
    	
    	currData.add(this.dgData.getUtilityModelParams().getdStateNode());
    	currData.add(this.dgData.getUtilityModelParams().getdStateOffer());
    	
    	currData.add(this.dgData.getUtilityModelParams().getUtilBeforeNetwork());
    	currData.add(this.dgData.getUtilityModelParams().getUtilBeforeDisease());
    	currData.add(this.dgData.getUtilityModelParams().getUtilBeforeTotal());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecNetwork());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecDisease());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecTotal());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecOppNetwork());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecOppDisease());
    	currData.add(this.dgData.getUtilityModelParams().getUtilExpAfterDecOppTotal());

    	currData.add(this.dgData.getUtilityModelParams().getNetTies());
    	currData.add(this.dgData.getUtilityModelParams().getNetTiesInf());
    	currData.add(this.dgData.getUtilityModelParams().getNetPropTriadsClosed());
    	currData.add(this.dgData.getUtilityModelParams().getNetPropTriadsClosedExpAfterDec());
//    	currData.add(this.dgData.getUtilityModelParams().getNetAvPathLength());
    	currData.add(this.dgData.getUtilityModelParams().getNetHomophily());
    	
    	currData.add(this.dgData.getUtilityModelParams().getrScore());

        writeLine(currData);

    }
}
