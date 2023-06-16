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
    private final double targetHomophily;

    private double fitnessAvDegree;
    private double fitnessClustering;
    private double fitnessHomophily;
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
     * 			the omega
     * @param targetAvDegree
     *          the target average degree
     * @param targetClustering
     *          the target clustering
     */
    public NunnerBuskensGene(int generation, String id, String simpleId, String mother, String father,
            double avC2, double alpha, double omega,
            double targetAvDegree, double targetClustering, double targetHomophily) {

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
        this.targetHomophily = targetHomophily;
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
     * @return the fitnessHomophily
     */
    public double getFitnessHomophily() {
        return fitnessHomophily;
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
     * @param homophily
     * 			the homophily
     * @return the fitness of the gene
     */
    public double getFitnessOverall(double avDegree, double clustering, double homophily) {
        double fitnessAvDegree = computePercentageError(avDegree, targetAvDegree);
		double fitnessClustering = computePercentageError(clustering, targetClustering);
		return fitnessAvDegree + fitnessClustering;
//		double fitnessHomophily = computePercentageError(homophily, targetHomophily);
//		return fitnessAvDegree + fitnessClustering + fitnessHomophily;
    }

    /**
     * Sets the fitness for the gene.
     *
     * @param avDegree
     *          the average degree
     * @param clustering
     *          the clustering
     * @param homophily
     * 			the homophily
     */
    public void setFitness(double avDegree, double clustering, double homophily) {
        this.fitnessAvDegree = computePercentageError(avDegree, targetAvDegree);
        this.fitnessClustering = computePercentageError(clustering, targetClustering);
        this.fitnessHomophily = computePercentageError(homophily, targetHomophily);
        this.fitnessOverall = this.fitnessAvDegree + this.fitnessClustering; // + fitnessHomophily;
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
