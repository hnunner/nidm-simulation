package nl.uu.socnetid.nidm.stats;

import nl.uu.socnetid.nidm.diseases.DiseaseSpecs;
import nl.uu.socnetid.nidm.simulation.SimulationStage;

/**
 * @author Hendrik Nunner
 */
public class DataGeneratorStats {

    // general
    private int upc = 0;
    private String uid;
    private int simPerUpc;
    private boolean iota;
    private int roundStartInfection;
    private int roundsLastInfection;
    private boolean tiesBrokenWithInfectionPresent;
    private SimulationStage simStage;
    // pre-epidemic
    private double densityPre;
    private double avDegreePre;
    private double avDegree2Pre;
    private double avClosenessPre;
    private double avClusteringPre;
    private double avUtility;
    private double avBenefitDistance1;
    private double avBenefitDistance2;
    private double avCostsDistance1;
    private double avCostsDisease;
    // index case
    private double indexDegree1;
    private double indexDegree2;
    private double indexCloseness;
    private double indexClustering;
    private double indexUtility;
    private double indexBenefit1;
    private double indexBenefit2;
    private double indexCosts1;
    private double indexCostsDisease;
    // disease specs
    private DiseaseSpecs diseaseSpecs;


    /**
     * @return the roundStartInfection
     */
    public int getRoundStartInfection() {
        return roundStartInfection;
    }

    /**
     * @param roundStartInfection the roundStartInfection to set
     */
    public void setRoundStartInfection(int roundStartInfection) {
        this.roundStartInfection = roundStartInfection;
    }

    /**
     * @return the roundsLastInfection
     */
    public int getRoundsLastInfection() {
        return roundsLastInfection;
    }

    /**
     * @param roundsLastInfection the roundsLastInfection to set
     */
    public void setRoundsLastInfection(int roundsLastInfection) {
        this.roundsLastInfection = roundsLastInfection;
    }

    /**
     * @return the tiesBrokenWithInfectionPresent
     */
    public boolean isTiesBrokenWithInfectionPresent() {
        return tiesBrokenWithInfectionPresent;
    }

    /**
     * @param tiesBrokenWithInfectionPresent the tiesBrokenWithInfectionPresent to set
     */
    public void setTiesBrokenWithInfectionPresent(boolean tiesBrokenWithInfectionPresent) {
        this.tiesBrokenWithInfectionPresent = tiesBrokenWithInfectionPresent;
    }

    /**
     * @return the densityPre
     */
    public double getDensityPre() {
        return densityPre;
    }

    /**
     * @param densityPre the densityPre to set
     */
    public void setDensityPre(double densityPre) {
        this.densityPre = densityPre;
    }

    /**
     * @return the avDegreePre
     */
    public double getAvDegreePre() {
        return avDegreePre;
    }

    /**
     * @param avDegreePre the avDegreePre to set
     */
    public void setAvDegreePre(double avDegreePre) {
        this.avDegreePre = avDegreePre;
    }

    /**
     * @return the avDegree2Pre
     */
    public double getAvDegree2Pre() {
        return avDegree2Pre;
    }

    /**
     * @param avDegree2Pre the avDegree2Pre to set
     */
    public void setAvDegree2Pre(double avDegree2Pre) {
        this.avDegree2Pre = avDegree2Pre;
    }

    /**
     * @return the avClosenessPre
     */
    public double getAvClosenessPre() {
        return avClosenessPre;
    }

    /**
     * @param avClosenessPre the avClosenessPre to set
     */
    public void setAvClosenessPre(double avClosenessPre) {
        this.avClosenessPre = avClosenessPre;
    }

    /**
     * @return the avClusteringPre
     */
    public double getAvClusteringPre() {
        return avClusteringPre;
    }

    /**
     * @param avClusteringPre the avClusteringPre to set
     */
    public void setAvClusteringPre(double avClusteringPre) {
        this.avClusteringPre = avClusteringPre;
    }

    /**
     * @return the avUtility
     */
    public double getAvUtility() {
        return avUtility;
    }

    /**
     * @param avUtility the avUtility to set
     */
    public void setAvUtility(double avUtility) {
        this.avUtility = avUtility;
    }

    /**
     * @return the avBenefitDistance1
     */
    public double getAvBenefitDistance1() {
        return avBenefitDistance1;
    }

    /**
     * @param avBenefitDistance1 the avBenefitDistance1 to set
     */
    public void setAvBenefitDistance1(double avBenefitDistance1) {
        this.avBenefitDistance1 = avBenefitDistance1;
    }

    /**
     * @return the avBenefitDistance2
     */
    public double getAvBenefitDistance2() {
        return avBenefitDistance2;
    }

    /**
     * @param avBenefitDistance2 the avBenefitDistance2 to set
     */
    public void setAvBenefitDistance2(double avBenefitDistance2) {
        this.avBenefitDistance2 = avBenefitDistance2;
    }

    /**
     * @return the avCostsDistance1
     */
    public double getAvCostsDistance1() {
        return avCostsDistance1;
    }

    /**
     * @param avCostsDistance1 the avCostsDistance1 to set
     */
    public void setAvCostsDistance1(double avCostsDistance1) {
        this.avCostsDistance1 = avCostsDistance1;
    }

    /**
     * @return the avCostsDisease
     */
    public double getAvCostsDisease() {
        return avCostsDisease;
    }

    /**
     * @param avCostsDisease the avCostsDisease to set
     */
    public void setAvCostsDisease(double avCostsDisease) {
        this.avCostsDisease = avCostsDisease;
    }

    /**
     * @return the indexDegree1
     */
    public double getIndexDegree1() {
        return indexDegree1;
    }

    /**
     * @param indexDegree1 the indexDegree1 to set
     */
    public void setIndexDegree1(double indexDegree1) {
        this.indexDegree1 = indexDegree1;
    }

    /**
     * @return the indexDegree2
     */
    public double getIndexDegree2() {
        return indexDegree2;
    }

    /**
     * @param indexDegree2 the indexDegree2 to set
     */
    public void setIndexDegree2(double indexDegree2) {
        this.indexDegree2 = indexDegree2;
    }

    /**
     * @return the indexCloseness
     */
    public double getIndexCloseness() {
        return indexCloseness;
    }

    /**
     * @param indexCloseness the indexCloseness to set
     */
    public void setIndexCloseness(double indexCloseness) {
        this.indexCloseness = indexCloseness;
    }

    /**
     * @return the indexClustering
     */
    public double getIndexClustering() {
        return indexClustering;
    }

    /**
     * @param indexClustering the indexClustering to set
     */
    public void setIndexClustering(double indexClustering) {
        this.indexClustering = indexClustering;
    }

    /**
     * @return the indexUtility
     */
    public double getIndexUtility() {
        return indexUtility;
    }

    /**
     * @param indexUtility the indexUtility to set
     */
    public void setIndexUtility(double indexUtility) {
        this.indexUtility = indexUtility;
    }

    /**
     * @return the indexBenefit1
     */
    public double getIndexBenefit1() {
        return indexBenefit1;
    }

    /**
     * @param indexBenefit1 the indexBenefit1 to set
     */
    public void setIndexBenefit1(double indexBenefit1) {
        this.indexBenefit1 = indexBenefit1;
    }
    /**
     * @return the indexBenefit2
     */
    public double getIndexBenefit2() {
        return indexBenefit2;
    }

    /**
     * @param indexBenefit2 the indexBenefit2 to set
     */
    public void setIndexBenefit2(double indexBenefit2) {
        this.indexBenefit2 = indexBenefit2;
    }

    /**
     * @return the indexCosts1
     */
    public double getIndexCosts1() {
        return indexCosts1;
    }

    /**
     * @param indexCosts1 the indexCosts1 to set
     */
    public void setIndexCosts1(double indexCosts1) {
        this.indexCosts1 = indexCosts1;
    }

    /**
     * @return the indexCostsDisease
     */
    public double getIndexCostsDisease() {
        return indexCostsDisease;
    }

    /**
     * @param indexCostsDisease the indexCostsDisease to set
     */
    public void setIndexCostsDisease(double indexCostsDisease) {
        this.indexCostsDisease = indexCostsDisease;
    }

    /**
     * @return the upc
     */
    public int getUpc() {
        return upc;
    }

    /**
     * @param upc the upc to set
     */
    public void setUpc(int upc) {
        this.upc = upc;
    }

    /**
     * increases the unique parameter combination
     */
    public void incUpc() {
        this.upc += 1;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the simPerUpc
     */
    public int getSimPerUpc() {
        return simPerUpc;
    }

    /**
     * @param simPerUpc the simPerUpc to set
     */
    public void setSimPerUpc(int simPerUpc) {
        this.simPerUpc = simPerUpc;
    }

    /**
     * increases the simPerUpc
     */
    public void incSimPerUpc() {
        ++this.simPerUpc;
    }

    /**
     * @return the iota
     */
    public boolean isIota() {
        return iota;
    }

    /**
     * @param iota the iota to set
     */
    public void setIota(boolean iota) {
        this.iota = iota;
    }

    /**
     * @return the simStage
     */
    public SimulationStage getSimStage() {
        return simStage;
    }

    /**
     * @param simStage the simStage to set
     */
    public void setSimStage(SimulationStage simStage) {
        this.simStage = simStage;
    }

    /**
     * @return the diseaseSpecs
     */
    public DiseaseSpecs getDiseaseSpecs() {
        return diseaseSpecs;
    }

    /**
     * @param diseaseSpecs the diseaseSpecs to set
     */
    public void setDiseaseSpecs(DiseaseSpecs diseaseSpecs) {
        this.diseaseSpecs = diseaseSpecs;
    }

}
