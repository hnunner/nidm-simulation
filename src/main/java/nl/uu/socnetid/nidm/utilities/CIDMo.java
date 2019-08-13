package nl.uu.socnetid.nidm.utilities;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class CIDMo extends UtilityFunction {

    // discount for infected direct connections
    private final double kappa;
    // discount for infected indirect connections
    private final double lamda;

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param kappa
     *          the discount for infected direct connections
     * @param beta
     *          the benefit of an indirect connection
     * @param lamda
     *          the discount for infected indirect connections
     * @param c
     *          the maintenance costs for a direct connection
     */
    public CIDMo(double alpha, double kappa, double beta, double lamda, double c) {
        super(alpha, beta, c);
        this.kappa = kappa;
        this.lamda = lamda;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "CIDMo";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getAlpha() * (lacs.getnS() + this.kappa * lacs.getnI() + lacs.getnR());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getBeta() * (lacs.getmS() + this.lamda * lacs.getmI() + lacs.getmR());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalAgentConnectionsStats lacs, Agent agent) {
        return this.getC() * (lacs.getnS() + agent.getDiseaseSpecs().getMu() * lacs.getnI() + lacs.getnR());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getEffectOfDisease(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getEffectOfDisease(LocalAgentConnectionsStats lacs, Agent agent) {
        int nI = lacs.getnI();
        double p;
        double s;
        double rSigma = agent.getRSigma();
        double rPi = agent.getRPi();

        // depending own agent's own risk group
        switch (agent.getDiseaseGroup()) {
            case SUSCEPTIBLE:
                p = Math.pow(StatsComputer.computeProbabilityOfInfection(agent, nI), (2 - rPi));
                s = Math.pow(agent.getDiseaseSpecs().getS(), rSigma) ;
                break;

            case INFECTED:
                p = 1;
                s = agent.getDiseaseSpecs().getS();
                break;

            case RECOVERED:
                p = 0;
                s = 0;
                break;

            default:
                throw new RuntimeException("Unknown disease group: " + agent.getDiseaseGroup());
        }

        return p * s;
    }

}
