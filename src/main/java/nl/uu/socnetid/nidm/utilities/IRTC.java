package nl.uu.socnetid.nidm.utilities;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats;
import nl.uu.socnetid.nidm.stats.StatsComputer;

/**
 * @author Hendrik Nunner
 */
public class IRTC extends UtilityFunction {

    /**
     * Constructor.
     *
     * @param alpha
     *          the benefit of a direct connection
     * @param beta
     *          the benefit of an indirect connection
     * @param c
     *          the maintenance costs for a direct connection
     */
    public IRTC(double alpha, double beta, double c) {
        super(alpha, beta, c);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getStatsName()
     */
    @Override
    public String getStatsName() {
        return "IRTC";
    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getBenefitOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfDirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getAlpha() * lacs.getN();

    }

    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getBenefitOfIndirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats)
     */
    @Override
    protected double getBenefitOfIndirectConnections(LocalAgentConnectionsStats lacs) {
        return this.getBeta() * lacs.getM();
    }

    /*
     * (non-Javadoc)
     * @see nl.uu.socnetid.nidm.utilities.UtilityFunction#getCostsOfDirectConnections(
     * nl.uu.socnetid.nidm.stats.LocalAgentConnectionsStats, nl.uu.socnetid.nidm.agents.Agent)
     */
    @Override
    protected double getCostsOfDirectConnections(LocalAgentConnectionsStats lacs, Agent agent) {
        int nSR = lacs.getnS() + lacs.getnR();
        int nI = lacs.getnI();
        return (nSR + (nI * agent.getDiseaseSpecs().getMu())) * this.getC();
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
