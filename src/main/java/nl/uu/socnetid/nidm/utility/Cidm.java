package nl.uu.socnetid.nidm.utility;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class Cidm extends UtilityFunction {

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
    public Cidm(double alpha, double kappa, double beta, double lamda, double c) {
        super(alpha, beta, c);
        this.kappa = kappa;
        this.lamda = lamda;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "Cidm";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getAlpha() * (lacs.getnS() + this.kappa * lacs.getnI() + lacs.getnR());
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getBeta() * (lacs.getmS() + this.lamda * lacs.getmI() + lacs.getmR());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalAgentConnectionsStats lacs, Agent agent) {
        return this.getC() * (lacs.getnS() + agent.getDiseaseSpecs().getMu() * lacs.getnI() + lacs.getnR());
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getEffectOfDisease(
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
                s = Math.pow(agent.getDiseaseSpecs().getSigma(), rSigma) ;
                break;

            case INFECTED:
                p = 1;
                s = agent.getDiseaseSpecs().getSigma();
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


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getKappa()
     */
    @Override
    public double getKappa() {
        return this.kappa;
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utility.UtilityFunction#getLamda()
     */
    @Override
    public double getLamda() {
        return this.lamda;
    }

}
