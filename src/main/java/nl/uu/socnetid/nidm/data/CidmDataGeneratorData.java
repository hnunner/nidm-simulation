package nl.uu.socnetid.nidm.data;

import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class CidmDataGeneratorData extends DataGeneratorData<CidmParameters> {

    // cidm parameters
    private CidmParameters cidmParams;


    /**
     * Constructor.
     */
    public CidmDataGeneratorData() {
        super();
        this.cidmParams = PropertiesHandler.getInstance().getCidmParameters();
    }


    /* (non-Javadoc)
     * @see nl.uu.socnetid.nidm.data.DataGeneratorData#getUtilityModelParams()
     */
    @Override
    public CidmParameters getUtilityModelParams() {
        return this.cidmParams;
    }

}
