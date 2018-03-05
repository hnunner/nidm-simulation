package nl.uu.socnetid.networkgames.diseases;

/**
 * @author Hendrik Nunner
 */
public final class DiseaseFactory {

    /**
     * Factory method to create an infection.
     *
     * @param diseaseSpecs
     *          the characteristics of the disease
     * @return a new infection
     */
    public static Disease createInfection(DiseaseSpecs diseaseSpecs) {

        switch (diseaseSpecs.getDiseaseType()) {

            case SIR:
                return new SIRDisease(diseaseSpecs);

            default:
                throw new RuntimeException("Unknown disease type: " + diseaseSpecs.getDiseaseType().toString());
        }
    }

}
