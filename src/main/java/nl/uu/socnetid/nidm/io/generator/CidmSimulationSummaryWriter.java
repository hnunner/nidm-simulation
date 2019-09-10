package nl.uu.socnetid.nidm.io.generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nl.uu.socnetid.nidm.data.CidmDataGeneratorData;
import nl.uu.socnetid.nidm.data.LogValues;

/**
 * @author Hendrik Nunner
 */
public class CidmSimulationSummaryWriter extends CidmCsvFileWriter {

    /**
     * Creates the writer.
     *
     * @param fileName
     *          the name of the file to store the data to
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CidmSimulationSummaryWriter(String fileName, CidmDataGeneratorData dgData) throws IOException {
        super(fileName, dgData);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#initCols()
     */
    @Override
    protected void initCols() {

        List<String> cols = new LinkedList<String>();

        // INDEPENDENT VARIABLES (I)
        // simulation
        cols.add(LogValues.IV_SIM_UID.toString());
        cols.add(LogValues.IV_SIM_UPC.toString());
        cols.add(LogValues.IV_SIM_CNT.toString());
        // Cidm
        cols.add(LogValues.IV_CIDM_ALPHA_AV.toString());
        cols.add(LogValues.IV_CIDM_KAPPA_AV.toString());
        cols.add(LogValues.IV_CIDM_BETA_AV.toString());
        cols.add(LogValues.IV_CIDM_LAMDA_AV.toString());
        cols.add(LogValues.IV_CIDM_C_AV.toString());
        cols.add(LogValues.IV_CIDM_MU_AV.toString());
        cols.add(LogValues.IV_CIDM_SIGMA_AV.toString());
        cols.add(LogValues.IV_CIDM_GAMMA_AV.toString());
        cols.add(LogValues.IV_CIDM_RS_EQUAL.toString());
        cols.add(LogValues.IV_CIDM_R_SIGMA_AV.toString());
        cols.add(LogValues.IV_CIDM_R_PI_AV.toString());
        cols.add(LogValues.IV_CIDM_NET_SIZE.toString());
        cols.add(LogValues.IV_CIDM_IOTA.toString());
        cols.add(LogValues.IV_CIDM_PHI_AV.toString());
        cols.add(LogValues.IV_CIDM_TAU_AV.toString());
        // other exports
        cols.add(LogValues.GEXF_FILE.toString());

        // DEPENDENT VARIABLES (D)
        // simulation
        cols.add(LogValues.DV_SIM_EPIDEMIC_DURATION.toString());
        // network
        // pre-epidemic
        cols.add(LogValues.DV_NET_AV_DEGREE_PRE.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE2_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_PRE.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_PRE.toString());
        cols.add(LogValues.DV_NET_AV_UTIL_PRE.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_DIST1_PRE.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_DIST2_PRE.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DIST1_PRE.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DISEASE_PRE.toString());
        cols.add(LogValues.DV_NET_DENSITY_PRE.toString());
        // post-epidemic
        cols.add(LogValues.DV_NET_PERCENTAGE_SUSCEPTIBLE.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_INFECTED.toString());
        cols.add(LogValues.DV_NET_PERCENTAGE_RECOVERED.toString());
        cols.add(LogValues.DV_NET_TIES_BROKEN_EPIDEMIC.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE_POST.toString());
        cols.add(LogValues.DV_NET_AV_DEGREE2_POST.toString());
        cols.add(LogValues.DV_NET_AV_CLOSENESS_POST.toString());
        cols.add(LogValues.DV_NET_AV_CLUSTERING_POST.toString());
        cols.add(LogValues.DV_NET_AV_UTIL_POST.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_DIST1_POST.toString());
        cols.add(LogValues.DV_NET_AV_BENEFIT_DIST2_POST.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DIST1_POST.toString());
        cols.add(LogValues.DV_NET_AV_COSTS_DISEASE_POST.toString());
        cols.add(LogValues.DV_NET_DENSITY_POST.toString());
        // index case
        cols.add(LogValues.DV_INDEX_SATISFIED.toString());
        cols.add(LogValues.DV_INDEX_UTIL.toString());
        cols.add(LogValues.DV_INDEX_BENEFIT_DIST1.toString());
        cols.add(LogValues.DV_INDEX_BENEFIT_DIST2.toString());
        cols.add(LogValues.DV_INDEX_COSTS_DIST1.toString());
        cols.add(LogValues.DV_INDEX_COSTS_DISEASE.toString());
        cols.add(LogValues.DV_INDEX_DISEASE_STATE.toString());
        cols.add(LogValues.DV_INDEX_DISEASE_ROUNDS_REMAINING.toString());
        cols.add(LogValues.DV_INDEX_DEGREE1.toString());
        cols.add(LogValues.DV_INDEX_DEGREE2.toString());
        cols.add(LogValues.DV_INDEX_CLOSENESS.toString());
        cols.add(LogValues.DV_INDEX_CLUSTERING.toString());
        cols.add(LogValues.DV_INDEX_CONS_BROKEN_ACTIVE.toString());
        cols.add(LogValues.DV_INDEX_CONS_BROKEN_PASSIVE.toString());
        cols.add(LogValues.DV_INDEX_CONS_OUT_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_CONS_OUT_DECLINED.toString());
        cols.add(LogValues.DV_INDEX_CONS_IN_ACCEPTED.toString());
        cols.add(LogValues.DV_INDEX_CONS_IN_DECLINED.toString());

        writeLine(cols);
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.io.CSVFileWriter#writeCurrentData()
     */
    @Override
    public void writeCurrentData() {
        List<String> currData = new LinkedList<String>();

        // INDEPENDENT VARIABLES
        // simulation
        currData.add(this.dgData.getSimStats().getUid());
        currData.add(String.valueOf(this.dgData.getSimStats().getUpc()));
        currData.add(String.valueOf(this.dgData.getSimStats().getSimPerUpc()));
        // Cidm
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrAlpha()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrKappa()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrBeta()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrLamda()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrC()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrMu()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrGamma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isRsEqual() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRSigma()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrRPi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrN()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().isCurrIota() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrPhi()));
        currData.add(String.valueOf(this.dgData.getUtilityModelParams().getCurrTau()));
        // other exports
        currData.add(this.dgData.getGexfExportFile());

        // DEPENDENT VARIABLES
        // simulation
        currData.add(String.valueOf(this.dgData.getSimStats().getRoundLastInfection()));
        // network
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvBenefitDistance1()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvBenefitDistance2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCostsDistance1()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getAvCostsDisease()));
        currData.add(String.valueOf(this.dgData.getNetStatsPre().getDensity()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getSusceptiblePercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getInfectedPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getRecoveredPercent()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().isTiesBrokenWithInfectionPresent() ? 1 : 0));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvDegree()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvDegree2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvCloseness()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvClustering()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvUtility()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvBenefitDistance1()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvBenefitDistance2()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvCostsDistance1()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getAvCostsDisease()));
        currData.add(String.valueOf(this.dgData.getNetStatsPost().getDensity()));
        // index case
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().isSatisfied()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getUtility()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBenefit1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBenefit2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCosts1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCostsDisease()));
        currData.add(this.dgData.getIndexCaseStats().getDiseaseGroup().name());
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getTimeToRecover()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree1()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDegree2()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getCloseness()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getClustering()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBrokenTiesActive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getBrokenTiesPassive()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAcceptedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDeclinedRequestsOut()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getAcceptedRequestsIn()));
        currData.add(String.valueOf(this.dgData.getIndexCaseStats().getDeclinedRequestsIn()));

        writeLine(currData);
    }

}