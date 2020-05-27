package nl.uu.socnetid.nidm.data.genetic;

/**
 * Class holding genetic information for NunnerBuskens parameter optimization.
 *
 * @author Hendrik Nunner
 */
public class NunnerBuskensGene implements Comparable<NunnerBuskensGene> {

    private final int generation;
    private final String id;
    private final String simpleId;
    private final String mother;
    private final String father;

    private double avC2;
    private double alpha;
    private double omega;

    private final double targetAvDegree;
    private final double targetClustering;
    private final double targetAssortativity;

    private double fitnessAvDegree;
    private double fitnessClustering;
    private double fitnessAssortativity;
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
     * @param avC2
     *          the average marginal costs
     * @param alpha
     *          the proportion of closed triads
     * @param omega
     *          the proportion of similar peers selected for tie creation
     * @param targetAvDegree
     *          the target average degree
     * @param targetClustering
     *          the target clustering
     * @param targetAssortativity
     *          the target assortativity
     */
    public NunnerBuskensGene(int generation, String id, String simpleId, String mother, String father,
            double avC2, double alpha, double omega,
            double targetAvDegree, double targetClustering, double targetAssortativity) {

        this.generation = generation;
        this.id = id;
        this.simpleId = simpleId;
        this.mother = mother;
        this.father = father;

        this.avC2 = avC2;
        this.alpha = alpha;
        this.omega = omega;

        this.targetAvDegree = targetAvDegree;
        this.targetClustering = targetClustering;
        this.targetAssortativity = targetAssortativity;
    }

    /**
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the simpleId
     */
    public String getSimpleId() {
        return simpleId;
    }

    /**
     * @return the mother
     */
    public String getMother() {
        return mother;
    }

    /**
     * @return the father
     */
    public String getFather() {
        return father;
    }

    /**
     * @return the avC2
     */
    public double getAvC2() {
        return avC2;
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @return the omega
     */
    public double getOmega() {
        return omega;
    }

    /**
     * @return the targetAvDegree
     */
    public double getTargetAvDegree() {
        return targetAvDegree;
    }

    /**
     * @return the targetClustering
     */
    public double getTargetClustering() {
        return targetClustering;
    }

    /**
     * @return the targetAssortativity
     */
    public double getTargetAssortativity() {
        return targetAssortativity;
    }

    /**
     * @return the fitnessAvDegree
     */
    public double getFitnessAvDegree() {
        return fitnessAvDegree;
    }

    /**
     * @return the fitnessClustering
     */
    public double getFitnessClustering() {
        return fitnessClustering;
    }

    /**
     * @return the fitnessAssortativity
     */
    public double getFitnessAssortativity() {
        return fitnessAssortativity;
    }

    /**
     * @return the fitnessOverall
     */
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
    private double computePercentageError(double comp, double target) {
        return Math.abs((target - comp) / target);
    }

    /**
     * Gets the fitness of the gene.
     *
     * @param avDegree
     *          the average degree
     * @param clustering
     *          the clustering
     * @param assortativity
     *          the assortativity
     * @return the fitness of the gene
     */
    public double getFitnessOverall(double avDegree, double clustering, double assortativity) {
        return computePercentageError(avDegree, targetAvDegree) +
                computePercentageError(clustering, targetClustering) +
                computePercentageError(assortativity, targetAssortativity);
    }

    /**
     * Sets the fitness for the gene.
     *
     * @param avDegree
     *          the average degree
     * @param clustering
     *          the clustering
     * @param assortativity
     *          the assortativity
     */
    public void setFitness(double avDegree, double clustering, double assortativity) {
        this.fitnessAvDegree = computePercentageError(avDegree, targetAvDegree);
        this.fitnessClustering = computePercentageError(clustering, targetClustering);
        this.fitnessAssortativity = computePercentageError(assortativity, targetAssortativity);
        this.fitnessOverall = this.fitnessAvDegree + this.fitnessClustering + this.fitnessAssortativity;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo()
     */
    @Override
    public int compareTo(NunnerBuskensGene nbg) {
        if (nbg != null && nbg.getFitnessOverall() != null) {
            // ascending
            return this.getFitnessOverall().compareTo(nbg.getFitnessOverall());
            // descending
            //return nbg.getFitnessOverall().compareTo(this.getFitnessOverall());
        }
        return -1;
    }
}
