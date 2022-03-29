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
package nl.uu.socnetid.nidm.data.out;

import java.util.Map;
import java.util.Set;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.utility.Utility;

/**
 * @author Hendrik Nunner
 */
public class ExperimentAnalyzerParameters extends UtilityModelParameters {
	
	// IN
	private String sessionId;
	private String netType;
	private String node;
	private int round;
	private Network network;
	private Map<String, Set<Agent>> nomOutAccepted;
	private Map<String, Set<Agent>> nomOutDeclined;
	private Map<String, Set<Agent>> disAccepted;
	private Map<String, Set<Agent>> disDeclined;
	private Map<String, Set<Agent>> nomInAccepted;
	private Map<String, Set<Agent>> nomInDeclined;

	// OUT
	private String offer;
	private Integer offerType;
	private boolean offerAccept;
	// utility
	private Utility utilBefore;
	private Utility utilExpAfterDec;
	private Utility utilExpAfterDecOpp;
	private Utility utilExpAfterAll;
	// network
	private int netTies;
	private double netPropTriadsClosed;
	private double netPropTriadsClosedExpAfterDec;
	private double netPropTriadsClosedExpAfterAll;
	private double netAvPathLength;
	private double netHomophily;
	

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public Map<String, Set<Agent>> getNomOutAccepted() {
		return nomOutAccepted;
	}

	public void setNomOutAccepted(Map<String, Set<Agent>> nomOutAccepted) {
		this.nomOutAccepted = nomOutAccepted;
	}

	public Map<String, Set<Agent>> getNomOutDeclined() {
		return nomOutDeclined;
	}

	public void setNomOutDeclined(Map<String, Set<Agent>> nomOutDeclined) {
		this.nomOutDeclined = nomOutDeclined;
	}

	public Map<String, Set<Agent>> getDisAccepted() {
		return disAccepted;
	}

	public void setDisAccepted(Map<String, Set<Agent>> disAccepted) {
		this.disAccepted = disAccepted;
	}

	public Map<String, Set<Agent>> getDisDeclined() {
		return disDeclined;
	}

	public void setDisDeclined(Map<String, Set<Agent>> disDeclined) {
		this.disDeclined = disDeclined;
	}

	public Map<String, Set<Agent>> getNomInAccepted() {
		return nomInAccepted;
	}

	public void setNomInAccepted(Map<String, Set<Agent>> nomInAccepted) {
		this.nomInAccepted = nomInAccepted;
	}

	public Map<String, Set<Agent>> getNomInDeclined() {
		return nomInDeclined;
	}

	public void setNomInDeclined(Map<String, Set<Agent>> nomInDeclined) {
		this.nomInDeclined = nomInDeclined;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public Integer getOfferType() {
		return offerType;
	}

	public void setOfferType(Integer offerType) {
		this.offerType = offerType;
	}

	public boolean isOfferAccept() {
		return offerAccept;
	}

	public void setOfferAccept(boolean offerAccept) {
		this.offerAccept = offerAccept;
	}

	public Utility getUtilBefore() {
		return utilBefore;
	}

	public void setUtilBefore(Utility utilBefore) {
		this.utilBefore = utilBefore;
	}

	public Utility getUtilExpAfterDec() {
		return utilExpAfterDec;
	}

	public void setUtilExpAfterDec(Utility utilExpAfterDec) {
		this.utilExpAfterDec = utilExpAfterDec;
	}

	public Utility getUtilExpAfterDecOpp() {
		return utilExpAfterDecOpp;
	}

	public void setUtilExpAfterDecOpp(Utility utilExpAfterDecOpp) {
		this.utilExpAfterDecOpp = utilExpAfterDecOpp;
	}

	public Utility getUtilExpAfterAll() {
		return utilExpAfterAll;
	}

	public void setUtilExpAfterAll(Utility utilExpAfterAll) {
		this.utilExpAfterAll = utilExpAfterAll;
	}

	public int getNetTies() {
		return netTies;
	}

	public void setNetTies(int netTies) {
		this.netTies = netTies;
	}

	public double getNetPropTriadsClosed() {
		return netPropTriadsClosed;
	}

	public void setNetPropTriadsClosed(double netPropTriadsClosed) {
		this.netPropTriadsClosed = netPropTriadsClosed;
	}
	
	public double getNetPropTriadsClosedExpAfterDec() {
		return netPropTriadsClosedExpAfterDec;
	}

	public void setNetPropTriadsClosedExpAfterDec(double netPropTriadsClosedExpAfterDec) {
		this.netPropTriadsClosedExpAfterDec = netPropTriadsClosedExpAfterDec;
	}
	
	public double getNetPropTriadsClosedExpAfterAll() {
		return netPropTriadsClosedExpAfterAll;
	}

	public void setNetPropTriadsClosedExpAfterAll(double netPropTriadsClosedExpAfterAll) {
		this.netPropTriadsClosedExpAfterAll = netPropTriadsClosedExpAfterAll;
	}

	public double getNetAvPathLength() {
		return netAvPathLength;
	}

	public void setNetAvPathLength(double netAvPathLength) {
		this.netAvPathLength = netAvPathLength;
	}

	public double getNetHomophily() {
		return netHomophily;
	}

	public void setNetHomophily(double netHomophily) {
		this.netHomophily = netHomophily;
	}

}
