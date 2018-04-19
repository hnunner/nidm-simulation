package nl.uu.socnetid.netgame.utilities;

/**
 * @author Hendrik Nunner
 */
public class Utility {

    // overall utility
    private double overallUtility;

    // benefits
    private double benefitDirectConnections;
    private double benefitIndirectConnections;

    // costs
    private double costsDirectConnections;
    private double effectOfDisease;

    /**
     * Constructor.
     *
     * @param benefitDirectConnections
     *          the benefit of direct connections
     * @param benefitIndirectConnections
     *          the benefit of indirect connections
     * @param costsDirectConnections
     *          the costs of direct connections
     * @param effectOfDisease
     *          the effect of the disease
     */
    public Utility(double benefitDirectConnections, double benefitIndirectConnections,
            double costsDirectConnections, double effectOfDisease) {

        this.benefitDirectConnections = benefitDirectConnections;
        this.benefitIndirectConnections = benefitIndirectConnections;
        this.costsDirectConnections = costsDirectConnections;
        this.effectOfDisease = effectOfDisease;

        this.overallUtility = benefitDirectConnections
                + benefitIndirectConnections
                - costsDirectConnections
                - effectOfDisease;
    }

    /**
     * @return the overallUtility
     */
    public double getOverallUtility() {
        return overallUtility;
    }

    /**
     * @return the benefitDirectConnections
     */
    public double getBenefitDirectConnections() {
        return benefitDirectConnections;
    }

    /**
     * @return the benefitIndirectConnections
     */
    public double getBenefitIndirectConnections() {
        return benefitIndirectConnections;
    }

    /**
     * @return the costsDirectConnections
     */
    public double getCostsDirectConnections() {
        return costsDirectConnections;
    }

    /**
     * @return the effectOfDisease
     */
    public double getEffectOfDisease() {
        return effectOfDisease;
    }

}
