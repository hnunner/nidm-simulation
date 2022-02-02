/*
 * Copyright (C) 2017 - 2022
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
import nl.uu.socnetid.nidm.data.out.ExperimentAnalyzerParameters;

/**
 * @author Hendrik Nunner
 */
public class ExperimentAnalyzerWriter extends CsvFileWriter<ExperimentAnalyzerParameters> {
	
	private static final double UTIL_NORM_FACTOR = 41.6667; 
	
	

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
    public ExperimentAnalyzerWriter(String fileName, DataGeneratorData<ExperimentAnalyzerParameters> dgData)
            throws IOException {
        super(fileName, dgData);
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {

        List<String> cols = new LinkedList<String>();
        
        // levels
        cols.add("session_id");
        cols.add("cond.clustering");
        cols.add("level.node");
        cols.add("level.round");
        
        // offer
        cols.add("offer");
        cols.add("offer.type");
        cols.add("offer.accept");
    	// utility
        cols.add("util.before.network");
        cols.add("util.before.disease");
        cols.add("util.before.total");
        
        cols.add("util.exp.after.dec.network");
        cols.add("util.exp.after.dec.disease");
        cols.add("util.exp.after.dec.total");
        
        cols.add("util.exp.after.dec.opp.network");
        cols.add("util.exp.after.dec.opp.disease");
        cols.add("util.exp.after.dec.opp.total");
        
        cols.add("util.exp.after.all.network");
        cols.add("util.exp.after.all.disease");
        cols.add("util.exp.after.all.total");
    	// network
        cols.add("net.ties");
        cols.add("net.prop.triads.closed");
        cols.add("net.avpathlength");
        cols.add("net.homophily");

        writeLine(cols);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        ExperimentAnalyzerParameters eap = this.dgData.getUtilityModelParams();
        
        // levels
		currData.add(eap.getSessionId());
        currData.add(eap.getNetType());
        currData.add(String.valueOf(eap.getNode()));
        currData.add(String.valueOf(eap.getRound()));
        
        // offer
        currData.add(eap.getOffer());
        currData.add(String.valueOf(eap.getOfferType()));
        currData.add(eap.isOfferAccept() ? "1" : "0");
        // utility
        double utilBeforeNetwork = eap.getUtilBefore().getSocialBenefits() * UTIL_NORM_FACTOR - eap.getUtilBefore().getSocialCosts() * UTIL_NORM_FACTOR;
        utilBeforeNetwork = utilBeforeNetwork > 0.0 ? utilBeforeNetwork : 0.0;
		currData.add(String.valueOf(utilBeforeNetwork));
        double penaltyBeforeDisease = eap.getUtilBefore().getDiseaseCosts() * UTIL_NORM_FACTOR;
		currData.add(String.valueOf(-penaltyBeforeDisease));
        currData.add(String.valueOf(utilBeforeNetwork - penaltyBeforeDisease));

        double utilExpAfterDecNetwork = eap.getUtilExpAfterDec().getSocialBenefits() * UTIL_NORM_FACTOR - eap.getUtilExpAfterDec().getSocialCosts() * UTIL_NORM_FACTOR;
        utilExpAfterDecNetwork = utilExpAfterDecNetwork > 0.0 ? utilExpAfterDecNetwork : 0.0;
		currData.add(String.valueOf(utilExpAfterDecNetwork));
        double penaltyExpAfterDecDisease = eap.getUtilExpAfterDec().getDiseaseCosts() * UTIL_NORM_FACTOR;
		currData.add(String.valueOf(-penaltyExpAfterDecDisease));
        currData.add(String.valueOf(utilExpAfterDecNetwork - penaltyExpAfterDecDisease));
        
        double utilExpAfterDecOppNetwork = eap.getUtilExpAfterDecOpp().getSocialBenefits() * UTIL_NORM_FACTOR - eap.getUtilExpAfterDecOpp().getSocialCosts() * UTIL_NORM_FACTOR;
        utilExpAfterDecOppNetwork = utilExpAfterDecOppNetwork > 0.0 ? utilExpAfterDecOppNetwork : 0.0;
		currData.add(String.valueOf(utilExpAfterDecOppNetwork));
        double penaltyExpAfterDecOppDisease = eap.getUtilExpAfterDecOpp().getDiseaseCosts() * UTIL_NORM_FACTOR;
		currData.add(String.valueOf(-penaltyExpAfterDecOppDisease));
        currData.add(String.valueOf(utilExpAfterDecOppNetwork - penaltyExpAfterDecOppDisease));
        
        double utilExpAfterAllNetwork = eap.getUtilExpAfterAll().getSocialBenefits() * UTIL_NORM_FACTOR - eap.getUtilExpAfterAll().getSocialCosts() * UTIL_NORM_FACTOR;
        utilExpAfterAllNetwork = utilExpAfterAllNetwork > 0.0 ? utilExpAfterAllNetwork : 0.0;
		currData.add(String.valueOf(utilExpAfterAllNetwork > 0.0 ? utilExpAfterAllNetwork : 0.0));
        double penaltyExpAfterAllDisease = eap.getUtilExpAfterAll().getDiseaseCosts() * UTIL_NORM_FACTOR;
		currData.add(String.valueOf(-penaltyExpAfterAllDisease));
        currData.add(String.valueOf(utilExpAfterAllNetwork - penaltyExpAfterAllDisease));
        
        // network
        currData.add(String.valueOf(eap.getNetTies()));
        currData.add(String.valueOf(eap.getNetPropTriadsClosed()));
        currData.add(String.valueOf(eap.getNetAvPathLength()));
        currData.add(String.valueOf(eap.getNetHomophily()));

        writeLine(currData);
    }

}
