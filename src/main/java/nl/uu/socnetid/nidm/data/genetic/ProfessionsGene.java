package nl.uu.socnetid.nidm.data.genetic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class holding genetic information for NunnerBuskens parameter optimization.
 *
 * @author Hendrik Nunner
 *
 * TODO create abstract class for all genes
 */
public class ProfessionsGene implements Comparable<ProfessionsGene> {

    private final int generation;
    private final String id;
    private final String simpleId;
    private final String mother;
    private final String father;


    private Map<String, Double> belotAvDegrees;
    private Map<String, Double> targetAvDegrees;
    private Map<String, Double> avDegrees;
    private double fitnessAvDegrees;
    private Map<String, Double> fitnessAvDegreesByProfession;

    private double danonClustering;
    private double targetClustering;
    private double alpha;
    private double clustering;
    private double fitnessClustering;

    private double fitnessOverall;

    /**
     * Creates a gene for NunnerBuskens parameter optimization.
     *
     * @param generation
     *          the generation
     * @param id
     *          the id
     * @param simpleId
     *          the simple id
     * @param mother
     *          the mother
     * @param father
     *          the father
     * @param belotAvDegrees
     *          the average degrees of the Danon et al. data set
     * @param targetAvDegrees
     *          the target evrage degres
     * @param danonClustering
     *          the clustering of the Danon et al. data set
     * @param targetClustering
     *          the target clustering
     * @param alpha
     *          the alpha
     */
    public ProfessionsGene(
            int generation, String id, String simpleId, String mother, String father,
            Map<String, Double> belotAvDegrees, Map<String, Double> targetAvDegrees,
            double danonClustering, double targetClustering, double alpha) {

        this.generation = generation;
        this.id = id;
        this.simpleId = simpleId;
        this.mother = mother;
        this.father = father;

        this.belotAvDegrees = belotAvDegrees;
        this.targetAvDegrees = targetAvDegrees;

        this.danonClustering = danonClustering;
        this.targetClustering = targetClustering;
        this.alpha = alpha;
    }

    public void update(Map<String, Double> avDegrees, double clustering) {
        this.avDegrees = avDegrees;
        this.clustering = clustering;
        setFitness(avDegrees, clustering);
    }

    public int getGeneration() {
        return generation;
    }

    public String getId() {
        return id;
    }

    public String getSimpleId() {
        return simpleId;
    }

    public String getMother() {
        return mother;
    }

    public String getFather() {
        return father;
    }

    public double getBelotAvDegree(String profession) {
        return belotAvDegrees.get(profession);
    }

    public Map<String, Double> getBelotAvDegrees() {
        return belotAvDegrees;
    }

    public double getAvDegree(String profession) {
        return avDegrees.get(profession);
    }

    public Map<String, Double> getAvDegrees() {
        return avDegrees;
    }

    public double getAlpha() {
        return alpha;
    }

    public Map<String, Double> getTargetAvDegrees() {
        return targetAvDegrees;
    }

    public double getTargetAvDegree(String profession) {
        return targetAvDegrees.get(profession);
    }

    public double getTargetClustering() {
        return targetClustering;
    }

    public double getDanonClustering() {
        return danonClustering;
    }

    public double getClustering() {
        return clustering;
    }

    public double getFitnessAvDegrees() {
        return fitnessAvDegrees;
    }

    public Map<String, Double> getFitnessAvDegreesByProfession() {
        return fitnessAvDegreesByProfession;
    }

    public double getFitnessAvDegreesByProfession(String profession) {
        return fitnessAvDegreesByProfession.get(profession);
    }

    public double getFitnessClustering() {
        return fitnessClustering;
    }

    public Double getFitnessOverall() {
        return fitnessOverall;
    }

    /**
     * Computes the error between a comparable value and a target value in percent.
     *
     * @param comp
     *          the comparable
     * @param target
     *          the target value
     * @return
     */
    private double computePercentageError(Map<String, Double> comp, Map<String, Double> target) {
        double error = 0.0;
        Iterator<String> keys = comp.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            error += computePercentageError(comp.get(key), target.get(key));
        }
        return error;
    }

    /**
     * Computes the error between a comparable value and a target value in percent.
     *
     * @param comp
     *          the comparable
     * @param target
     *          the target value
     * @return
     */
    private double computePercentageError(double comp, double target) {
        return Math.abs((target - comp) / target);
    }

    /**
     * Gets the fitness of the gene.
     *
     * @param avDegree
     *          the average degree by profession
     * @param clustering
     *          the clustering
     * @return the fitness of the gene
     */
    public double getFitnessOverall(Map<String, Double> avDegree, double clustering) {
        return computePercentageError(avDegree, belotAvDegrees) +
                computePercentageError(clustering, danonClustering);
    }

    /**
     * Sets the fitness for the gene.
     *
     * @param avDegree
     *          the average degree by profession
     * @param clustering
     *          the clustering
     */
    private void setFitness(Map<String, Double> avDegrees, double clustering) {
        this.fitnessAvDegrees = computePercentageError(avDegrees, belotAvDegrees);

        this.fitnessAvDegreesByProfession = new HashMap<String, Double>();
        Iterator<String> professions = avDegrees.keySet().iterator();
        while (professions.hasNext()) {
            String profession = professions.next();
            this.fitnessAvDegreesByProfession.put(profession, computePercentageError(avDegrees.get(profession),
                    this.belotAvDegrees.get(profession)));
        }

        this.fitnessClustering = computePercentageError(clustering, targetClustering);
        this.fitnessOverall = this.fitnessAvDegrees + this.fitnessClustering ;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo()
     */
    @Override
    public int compareTo(ProfessionsGene nbg) {
        if (nbg != null && nbg.getFitnessOverall() != null) {
            // ascending
            return this.getFitnessOverall().compareTo(nbg.getFitnessOverall());
            // descending
            //return nbg.getFitnessOverall().compareTo(this.getFitnessOverall());
        }
        return -1;
    }
}
