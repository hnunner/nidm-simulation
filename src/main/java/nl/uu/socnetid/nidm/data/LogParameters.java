package nl.uu.socnetid.nidm.data;

import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.system.PropertiesHandler;
import nl.uu.socnetid.nidm.utilities.CIDMParameters;

/**
 * @author Hendrik Nunner
 */
public enum LogParameters {

    // simulation
    SIM_UID("param.sim.uid"),
    SIM_UPC("param.sim.upc"),
    SIM_CNT("param.sim.cnt"),
    GEXF_FILE("param.sim.gexf.file"),
    // agent
    AGENT_ID("param.agent.id"),
    // CIDM
    CIDM_ALPHA("param.cidm.alpha"),
    CIDM_ALPHA_AV("param.cidm.alpha.av"),
    CIDM_KAPPA("param.cidm.kappa"),
    CIDM_KAPPA_AV("param.cidm.kappa.av"),
    CIDM_BETA("param.cidm.beta"),
    CIDM_BETA_AV("param.cidm.beta.av"),
    CIDM_LAMDA("param.cidm.lamda"),
    CIDM_LAMDA_AV("param.cidm.lamda.av"),
    CIDM_C("param.cidm.c"),
    CIDM_C_AV("param.cidm.c.av"),
    CIDM_MU("param.cidm.mu"),
    CIDM_MU_AV("param.cidm.mu.av"),
    CIDM_SIGMA("param.cidm.sigma"),
    CIDM_SIGMA_AV("param.cidm.sigma.av"),
    CIDM_GAMMA("param.cidm.gamma"),
    CIDM_GAMMA_AV("param.cidm.gamma.av"),
    CIDM_RS_EQUAL("param.cidm.rs.equal"),
    CIDM_R_SIGMA("param.cidm.r.sigma"),
    CIDM_R_SIGMA_AV("param.cidm.r.sigma.av"),
    CIDM_R_PI("param.cidm.r.pi"),
    CIDM_R_PI_AV("param.cidm.r.pi.av"),
    CIDM_NET_SIZE("param.cidm.N"),
    CIDM_IOTA("param.cidm.iota"),
    CIDM_PHI("param.cidm.phi"),
    CIDM_PHI_AV("param.cidm.phi.av"),
    CIDM_TAU("param.cidm.tau"),
    CIDM_TAU_AV("param.cidm.tau.av"),
    CIDM_ZETA("param.cidm.zeta"),
    CIDM_EPSILON("param.cidm.epsilon"),
    CIDM_SIMS_PER_PC("param.cidm.n");


    // the name
    private String name;

    /**
     * Constructor, setting the name.
     *
     * @param name
     *          the name of the enum
     */
    LogParameters(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the parameters feasible for regression analyses.
     *
     * @return the parameters feasible for regression analyses
     */
    public static List<LogParameters> getRegressionParameters() {
        List<LogParameters> regParams = new LinkedList<LogParameters>();
        CIDMParameters cidmParams = PropertiesHandler.getInstance().getCidmParameters();
        if (cidmParams.getAlphas().length > 1) regParams.add(CIDM_ALPHA_AV);
        if (cidmParams.getKappas().length > 1) regParams.add(CIDM_KAPPA_AV);
        if (cidmParams.getBetas().length > 1) regParams.add(CIDM_BETA_AV);
        if (cidmParams.getLamdas().length > 1) regParams.add(CIDM_LAMDA_AV);
        if (cidmParams.getCs().length > 1) regParams.add(CIDM_C_AV);
        if (cidmParams.getMus().length > 1) regParams.add(CIDM_MU_AV);
        if (cidmParams.getSigmas().length > 1) regParams.add(CIDM_SIGMA_AV);
        if (cidmParams.getGammas().length > 1) regParams.add(CIDM_GAMMA_AV);
        if (cidmParams.getRSigmas().length > 1) regParams.add(LogParameters.CIDM_R_SIGMA);
        if (cidmParams.getRPis().length > 1) regParams.add(CIDM_R_PI_AV);
        if (cidmParams.getNs().length > 1) regParams.add(CIDM_NET_SIZE);
        if (cidmParams.getIotas().length > 1) regParams.add(CIDM_IOTA);
        if (cidmParams.getPhis().length > 1) regParams.add(CIDM_PHI_AV);
        if (cidmParams.getTaus().length > 1) regParams.add(CIDM_TAU_AV);
        return regParams;
    }

}
