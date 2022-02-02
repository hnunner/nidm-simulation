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
package nl.uu.socnetid.nidm.io.network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.data.out.ExperimentAnalyzerParameters;
import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.diseases.types.DiseaseType;
import nl.uu.socnetid.nidm.io.csv.CsvFileReader;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.NunnerBuskens;

/**
 * @author Hendrik Nunner
 */
public class ExperimentAnalyzerReader {

    // logger
    private static final Logger logger = LogManager.getLogger(ExperimentAnalyzerReader.class);
    
    // utility
    private static final double B1 = 1.0;
    private static final double B2 = 0.5;
    private static final double ALPHA_A = 0.0;
    private static final double ALPHA_B = 0.667;
    private static final double C1 = 0.2;
    private static final double C2 = 0.067;
    
    // disease
    private static final int    TAU   = 4;
    private static final double S     = 0.34;
    private static final double GAMMA = 0.15;
    private static final double MU    = 0.0;		// not considered in NunnerBuskens utility function anyways
    private static final DiseaseSpecs DS = new DiseaseSpecs(DiseaseType.SIR, TAU, S, GAMMA, MU);


    /**
     * Constructor.
     */
    public ExperimentAnalyzerReader() { }


    /**
     * Reads experiment analyzer parameters from a csv file
     *
     * @param file
     *          the file to read from
     * @return the network
     */
    public ExperimentAnalyzerParameters read(String file, ExperimentAnalyzerParameters eap) {

    	// initializations
        NunnerBuskens nb = new NunnerBuskens(B1, B2, Double.NaN, C1, C2);
        Network network = new Network();
        Map<String, Agent> agents = new TreeMap<String, Agent>();
        for (int i = 0; i < 60; i++) {
        	Agent agent = network.addAgent(nb, DS);
        	agents.put(agent.getId(), agent);
        }

    	Map<String, Set<String>> nomOutAcceptedIds = new TreeMap<String, Set<String>>();
    	Map<String, Set<String>> nomOutDeclinedIds = new TreeMap<String, Set<String>>();
    	Map<String, Set<String>> disAcceptedIds = new TreeMap<String, Set<String>>();
    	Map<String, Set<String>> disDeclinedIds = new TreeMap<String, Set<String>>();
    	Map<String, Set<String>> nomInAcceptedIds = new TreeMap<String, Set<String>>();
    	Map<String, Set<String>> nomInDeclinedIds = new TreeMap<String, Set<String>>();
        
        try {
        	CsvFileReader reader = new CsvFileReader(file);
        	List<String[]> fileContent = reader.readFile();
        	
        	Iterator<String[]> it = fileContent.iterator();
        	while (it.hasNext()) {
        		String[] line = it.next();
        		
        		// column structure: 	session.id, net.type, round, 
        		//						source.id, source.riskscore, source.diseasestate,  
        		//						target.id, target.riskscore, target.diseasestate
        		
        		eap.setSessionId(line[0]);
        		String netType = line[1];
				eap.setNetType(netType);
        		switch (netType) {
        			case "A":
        				nb.setAlpha(ALPHA_A);
        				break;
        			case "B":
        				nb.setAlpha(ALPHA_B);
        				break;
    				default:
    					logger.error("Alpha cannot be set");
        		}
        		eap.setRound(Integer.valueOf(line[2]));
        		
        		// source agent
        		Agent aSource = agents.get(line[3]);
        		updateAgent(aSource, Integer.valueOf(line[4]), Double.valueOf(line[5]));
        		// target agent
        		String targetId = line[6];
        		if (!targetId.equals("NA")) {
					Agent aTarget = agents.get(targetId);
	        		updateAgent(aTarget, Integer.valueOf(line[7]), Double.valueOf(line[8]));
	        		// tie
	        		if (!aSource.isDirectlyConnectedTo(aTarget)) {
	        			aSource.addConnection(aTarget);
	        		}
        		}

                // nominations out accepted
                if (!nomOutAcceptedIds.containsKey(aSource.getId())) {
                	nomOutAcceptedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[9].split("&"))));
                }
                // nominations out declined
                if (!nomOutDeclinedIds.containsKey(aSource.getId())) {
                	nomOutDeclinedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[10].split("&"))));
                }
                // dissolutions accepted
                if (!disAcceptedIds.containsKey(aSource.getId())) {
                	disAcceptedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[11].split("&"))));
                }
                // dissolutions declined
                if (!disDeclinedIds.containsKey(aSource.getId())) {
                	disDeclinedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[12].split("&"))));
                }
                // nominations in accepted
                if (!nomInAcceptedIds.containsKey(aSource.getId())) {
                	nomInAcceptedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[13].split("&"))));
                }
                // nominations in declined
                if (!nomInDeclinedIds.containsKey(aSource.getId())) {
                	nomInDeclinedIds.put(aSource.getId(), new HashSet<String>(Arrays.asList(line[14].split("&"))));
                }
        	}
        	

        	
        	
        	eap.setNetwork(network);
        	
        	eap.setNomOutAccepted(offeringIdMapToAgentMap(network, nomOutAcceptedIds));
        	eap.setNomOutDeclined(offeringIdMapToAgentMap(network, nomOutDeclinedIds));
        	eap.setDisAccepted(offeringIdMapToAgentMap(network, disAcceptedIds));
        	eap.setDisDeclined(offeringIdMapToAgentMap(network, disDeclinedIds));
        	eap.setNomInAccepted(offeringIdMapToAgentMap(network, nomInAcceptedIds));
        	eap.setNomInDeclined(offeringIdMapToAgentMap(network, nomInDeclinedIds));
        	
        } catch (Exception e) {
            logger.error("Error during import of data from " + file, e);
        }

        logger.info("Data successfully imported and initialized from: " + file);
        return eap;
    }


	private Map<String, Set<Agent>> offeringIdMapToAgentMap(Network network, Map<String, Set<String>> offeringMap) {
		Map<String, Set<Agent>> agentMap = new TreeMap<String, Set<Agent>>();
		
		Iterator<String> agentIdIt = offeringMap.keySet().iterator();
		while (agentIdIt.hasNext()) {
			String agentId = agentIdIt.next();
			HashSet<Agent> offerings = new HashSet<Agent>();
			Iterator<String> offeringIds = offeringMap.get(agentId).iterator();
			while (offeringIds.hasNext()) {
				Agent offering = network.getAgent(offeringIds.next());
				if (offering != null) {
					offerings.add(offering);
				}
			}
			agentMap.put(agentId, offerings);
		}
		return agentMap;
	}
    
    private void updateAgent(Agent agent, int diseaseState, double riskScore) {
    	switch (diseaseState) {
	    	case 2:	
	    		agent.forceInfect(DS);
	    		break;
	    	case 3:
	    		agent.forceInfect(DS);
	    		agent.cure();
	    		break;
	    	case 1: 
	    	default:
	    		agent.makeSusceptible();
    	}
    	agent.updateRiskScores(riskScore);
    }

}
