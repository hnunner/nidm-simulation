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
package nl.uu.socnetid.nidm.io.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.DataGeneratorData;
import nl.uu.socnetid.nidm.data.out.ExperimentAnalyzerParameters;
import nl.uu.socnetid.nidm.io.csv.ExperimentAnalyzerWriter;
import nl.uu.socnetid.nidm.io.network.ExperimentAnalyzerReader;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;
import nl.uu.socnetid.nidm.utility.Utility;

/**
 * @author Hendrik Nunner
 *
 * TODO unify data generators
 */
public class ExperimentNetworksEachRoundAnalyzer extends AbstractAnalyzer {

    // logger
    private static final Logger logger = LogManager.getLogger(ExperimentNetworksEachRoundAnalyzer.class);
    

    // stats & writer
    private static final String TIME_OF_INVOCATION = (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date());
    private static final String EXPORT_PATH = new StringBuilder().append(System.getProperty("user.dir"))
    		.append("/exports/").append(TIME_OF_INVOCATION).append("/data/experimenteachround/").toString();
    private static final String EXPORT_FILE = new StringBuilder().append(EXPORT_PATH).append("analysis.csv").toString();
    private ExperimentAnalyzerWriter eaWriter;


    /**
     * Constructor.
     */
    public ExperimentNetworksEachRoundAnalyzer() { }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.analyzer.AbstractAnalyzer#analyze()
     */
    @Override
    protected void analyze() {
    	
    	ExperimentAnalyzerReader eaReader = new ExperimentAnalyzerReader();
    	ExperimentAnalyzerParameters eap = new ExperimentAnalyzerParameters();
		DataGeneratorData<ExperimentAnalyzerParameters> dgData = new DataGeneratorData<ExperimentAnalyzerParameters>(eap);
    	try {
    		Path exportPath = Paths.get(EXPORT_PATH);
    		if (!Files.exists(exportPath)) {
    			Files.createDirectories(exportPath);
    		}
			this.eaWriter = new ExperimentAnalyzerWriter(EXPORT_FILE, dgData);
		} catch (IOException e) {
			logger.error(e);
		}
    	
    	// loop over files
    	File[] files = new File(getClass().getClassLoader().getResource("networks-experiment-by-round").getPath()).listFiles();
    	for (int i = 0; i < files.length; i++) {

    		// read file
    		String filePath = files[i].getPath();
    		if (!filePath.endsWith(".csv") ) {
    			continue;
    		}
			eaReader.read(filePath, eap);
	        
	        // network stats
	        Network network = eap.getNetwork();
	        eap.setNetAvPathLength(network.getAvPathLength());
	        eap.setNetHomophily(network.getAssortativityRiskPerception());
	        
	        // analyze agents
			Iterator<Agent> agents = network.getAgentIterator();
	        while (agents.hasNext()) {
	        	Agent agent = agents.next();
	        	eap.setNode(agent.getId());
	        	
	        	// NETWORK
	        	LocalAgentConnectionsStats stats = StatsComputer.computeLocalAgentConnectionsStats(agent);
	        	// number of ties
	        	eap.setNetTies(stats.getN());
	        	// proportion of closed triads
	            double y = stats.getY(); 		// open triads
	            double z = stats.getZ();		// closed triads
	        	eap.setNetPropTriadsClosed((y+z) == 0 ? 0 : z / (y + z));

	        	// UTILITY
	        	// before
	        	Utility utilBefore = agent.getUtility();
				eap.setUtilBefore(utilBefore);
				// all accepted offers (util.exp.after.all)
	        	Set<Agent> withs = new HashSet<Agent>();
				withs.addAll(eap.getNomOutAccepted().get(agent.getId()));
	        	withs.addAll(eap.getNomInAccepted().get(agent.getId()));
	        	Set<Agent> withouts = eap.getDisAccepted().get(agent.getId());
	        	eap.setUtilExpAfterAll(agent.getUtility(withs, withouts));
	        	// accepted nominations out
        		Iterator<Agent> nomOutsAcc = eap.getNomOutAccepted().get(agent.getId()).iterator();
        		while (nomOutsAcc.hasNext()) {
        			Agent nomOutAcc = nomOutsAcc.next();
        			eap.setOffer(nomOutAcc.getId());
        			eap.setUtilExpAfterDec(agent.getUtilityWith(nomOutAcc));
        			eap.setUtilExpAfterDecOpp(utilBefore);
        			eap.setOfferType(1);
        			eap.setOfferAccept(true);
        			this.eaWriter.writeCurrentData();
        		}	
	        	// declined nominations out
        		Iterator<Agent> nomOutsDec = eap.getNomOutDeclined().get(agent.getId()).iterator();
        		while (nomOutsDec.hasNext()) {
        			Agent nomOutDec = nomOutsDec.next();
        			eap.setOffer(nomOutDec.getId());
        			eap.setUtilExpAfterDec(utilBefore);
        			eap.setUtilExpAfterDecOpp(agent.getUtilityWith(nomOutDec));
        			eap.setOfferType(1);
        			eap.setOfferAccept(false);
        			this.eaWriter.writeCurrentData();
        		}	
	        	// accepted dissolutions
        		Iterator<Agent> dissAcc = eap.getDisAccepted().get(agent.getId()).iterator();
        		while (dissAcc.hasNext()) {
        			Agent disAcc = dissAcc.next();
        			eap.setOffer(disAcc.getId());
        			eap.setUtilExpAfterDec(agent.getUtilityWithout(disAcc));
        			eap.setUtilExpAfterDecOpp(utilBefore);
        			eap.setOfferType(2);
        			eap.setOfferAccept(true);
        			this.eaWriter.writeCurrentData();
        		}	
	        	// declined dissolutions
        		Iterator<Agent> dissDec = eap.getDisDeclined().get(agent.getId()).iterator();
        		while (dissDec.hasNext()) {
        			Agent disDec = dissDec.next();
        			eap.setOffer(disDec.getId());
        			eap.setUtilExpAfterDec(utilBefore);
        			eap.setUtilExpAfterDecOpp(agent.getUtilityWithout(disDec));
        			eap.setOfferType(2);
        			eap.setOfferAccept(false);
        			this.eaWriter.writeCurrentData();
        		}	
	        	// accepted nominations in
        		Iterator<Agent> nomInsAcc = eap.getNomInAccepted().get(agent.getId()).iterator();
        		while (nomInsAcc.hasNext()) {
        			Agent nomInAcc = nomInsAcc.next();
        			eap.setOffer(nomInAcc.getId());
        			eap.setUtilExpAfterDec(agent.getUtilityWith(nomInAcc));
        			eap.setUtilExpAfterDecOpp(utilBefore);
        			eap.setOfferType(3);
        			eap.setOfferAccept(true);
        			this.eaWriter.writeCurrentData();
        		}	
	        	// declined nominations out
        		Iterator<Agent> nomInsDec = eap.getNomInDeclined().get(agent.getId()).iterator();
        		while (nomInsDec.hasNext()) {
        			Agent nomInDec = nomInsDec.next();
        			eap.setOffer(nomInDec.getId());
        			eap.setUtilExpAfterDec(utilBefore);
        			eap.setUtilExpAfterDecOpp(agent.getUtilityWith(nomInDec));
        			eap.setOfferType(3);
        			eap.setOfferAccept(false);
        			this.eaWriter.writeCurrentData();
        		}	
        		
	        	
	        	
//	        	Set<String> offersAccepted = eap.getOffersAccepted().get(agent.getId());
//	        	if (offersAccepted != null && !offersAccepted.isEmpty()) {
//	        		Iterator<String> offersAcceptedIt = offersAccepted.iterator();
//	        		while (offersAcceptedIt.hasNext()) {
//	        			Agent offerAccepted = network.getAgent(offersAcceptedIt.next());
//	        			if (offerAccepted != null) {
//	        				if (agent.isDirectlyConnectedTo(offerAccepted)) {
//	        					withouts.add(offerAccepted);
//	        				} else {
//	        					withs.add(offerAccepted);
//	        				}
//	        			}
//	        		}
//	        		eap.setUtilExpAfterAll(agent.getUtility(withs, withouts).getOverallUtility());
//	        		// single accepted offers
//	        		offersAcceptedIt = offersAccepted.iterator();
//	        		while (offersAcceptedIt.hasNext()) {
//	        			Agent offerAccepted = network.getAgent(offersAcceptedIt.next());
//
//	        			if (offerAccepted == null) {
//	        				eap.setOffer("NA");
//	        				eap.setUtilExpAfterDec(eap.getUtilBefore());
//	        				eap.setUtilExpAfterDecOpp(eap.getUtilBefore());
//	        			} else {
//	        				eap.setOffer(offerAccepted.getId()); 
//	        				double utilWithout = agent.getUtilityWithout(offerAccepted).getOverallUtility();
//	        				double utilWith = agent.getUtilityWith(offerAccepted).getOverallUtility();
//	        				if (agent.isDirectlyConnectedTo(offerAccepted)) {
//	        					eap.setUtilExpAfterDec(utilWithout);
//	        					eap.setUtilExpAfterDecOpp(utilWith);
//	        				} else {
//	        					eap.setUtilExpAfterDec(utilWith);
//	        					eap.setUtilExpAfterDecOpp(utilWithout);
//	        				}
//	        			}
//	        			this.eaWriter.writeCurrentData();
//	        		}	
//	        	}
//	        	// declined offers
// 	        	Set<String> offersDeclined = eap.getOffersDeclined().get(agent.getId());
//	        	if (offersDeclined != null && !offersDeclined.isEmpty()) {
//	        		Iterator<String> offersDeclinedIt = offersDeclined.iterator();
//	        		while (offersDeclinedIt.hasNext()) {
//	        			Agent offerDeclined = network.getAgent(offersDeclinedIt.next());
//
//	        			if (offerDeclined == null) {
//	        				eap.setOffer("NA");
//	        				eap.setUtilExpAfterDec(eap.getUtilBefore());
//	        				eap.setUtilExpAfterDecOpp(eap.getUtilBefore());
//	        			} else {
//	        				eap.setOffer(offerDeclined.getId()); 
//	        				double utilWithout = agent.getUtilityWithout(offerDeclined).getOverallUtility();
//	        				double utilWith = agent.getUtilityWith(offerDeclined).getOverallUtility();
//	        				if (agent.isDirectlyConnectedTo(offerDeclined)) {
//	        					eap.setUtilExpAfterDec(utilWith);
//	        					eap.setUtilExpAfterDecOpp(utilWithout);
//	        				} else {
//	        					eap.setUtilExpAfterDec(utilWithout);
//	        					eap.setUtilExpAfterDecOpp(utilWith);
//	        				}
//	        			}
//	        			this.eaWriter.writeCurrentData();
//	        		}	
//	        	}
	        }
    	}
    	finalizeDataExportFiles();
    }


    /**
     * Finalizes the export of data files.
     */
    private void finalizeDataExportFiles() {
        try {
            this.eaWriter.flush();
            this.eaWriter.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
