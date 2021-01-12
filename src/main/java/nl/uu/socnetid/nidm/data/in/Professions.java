package nl.uu.socnetid.nidm.data.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class Professions {

    // logger
    private static final Logger logger = LogManager.getLogger(Professions.class);

    private List<String> professionDistribution;
    private Map<String, Double> degreePreLockdown;
    private Map<String, Double> errorDegreePreLockdown;
    private Map<String, Double> degreeDuringLockdown;
    private Map<String, Double> errorDegreeDuringLockdown;
    private double avDegreePreLockdown;
    private double avDegreeDuringLockdown;


    /**
     * Singleton: private instantiation.
     */
    private Professions() {
        initProfessions();
    }


    /**
     * Initializes the profession structure of the agents: a list of random pdGlobal is stored
     * (to be randomly drawn from) according to the profession distribution specified in config.properties.
     *
     * TODO add age selection in GUI
     */
    private void initProfessions() {
        this.professionDistribution = new ArrayList<String>();
        this.degreePreLockdown = new HashMap<String, Double>();
        this.errorDegreePreLockdown = new HashMap<String, Double>();
        this.degreeDuringLockdown = new HashMap<String, Double>();
        this.errorDegreeDuringLockdown = new HashMap<String, Double>();

        int n = 0;

        // TODO get reliable numbers; currently proportions taken from Belot et al. 2020
        Path pathToFile = PropertiesHandler.getInstance().getProfessionsImportPath();

        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(";");

                // profession
                String profession = attributes[0];

                // profession distribution
                int i = 0;
                while (i < Integer.valueOf(attributes[1])) {
                    i++;
                    n++;
                    this.professionDistribution.add(profession);
                }

                // profession degree pre lockdown
                Double degPre = Double.valueOf(attributes[2]);
                this.degreePreLockdown.put(profession, degPre);
                this.avDegreePreLockdown += i*degPre;
                this.errorDegreePreLockdown.put(profession, Double.valueOf(attributes[3]));

                // profession degree during lockdown
                Double degDuring = Double.valueOf(attributes[4]);
                this.degreeDuringLockdown.put(profession, degDuring);
                this.avDegreePreLockdown += i*degDuring;
                this.errorDegreeDuringLockdown.put(profession, Double.valueOf(attributes[5]));

                line = br.readLine();
            }

        } catch (IOException ioe) {
            logger.error("Error while parsing profession distribution.", ioe);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                    logger.error("Error while closing BufferedReader.", ioe);
                }
            }
        }

        this.avDegreePreLockdown /= n;
    }


    /**
     * Lazy thread safe initialization of singleton.
     *
     * @author Hendrik Nunner
     */
    private static class LazyHolder {
        public static final Professions AS = new Professions();
    }

    /**
     * Gets the Professions singleton.
     *
     * @return the Professions singleton
     */
    public static Professions getInstance() {
        return LazyHolder.AS;
    }


    /**
     * Draws a random profession from the previously initialized profession distribution (see initProfessionDistribution).
     *
     * @return a randomly drawn profession from the age distribution
     */
    public String getRandomProfession() {
        return this.professionDistribution.get(ThreadLocalRandom.current().nextInt(0, this.professionDistribution.size()));
    }

    /**
     * Gets the degree by profession before lockdown.
     *
     * @param profession
     *          the profession to get the degree before the lockdown for
     * @return the degree by profession before lockdown
     */
    public double getDegreePreLockdown(String profession) {
        return this.degreePreLockdown.get(profession);
    }

    /**
     * Gets the degree by profession during lockdown.
     *
     * @param profession
     *          the profession to get the degree druing lockdown for
     * @return the degree by profession during lockdown
     */
    public double getDegreeDuringLockdown(String profession) {
        return this.degreeDuringLockdown.get(profession);
    }

    /**
     * Gets the degree error by profession prior to lockdown.
     *
     * @param profession
     *          the profession to get the degree druing lockdown for
     * @return the degree error by profession prior to lockdown
     */
    public double getDegreeErrorPreLockdown(String profession) {
        return errorDegreePreLockdown.get(profession);
    }

    /**
     * Gets the degree error by profession during lockdown.
     *
     * @param profession
     *          the profession to get the degree druing lockdown for
     * @return the degree error by profession during lockdown
     */
    public double getDegreeErrorDuringLockdown(String profession) {
        return errorDegreeDuringLockdown.get(profession);
    }

    /**
     * @return the avDegreePreLockdown
     */
    public double getAverageDegreePreLockdown() {
        return avDegreePreLockdown;
    }

    /**
     * @return the avDegreeDuringLockdown
     */
    public double getAverageDegreeDuringLockdown() {
        return avDegreeDuringLockdown;
    }

}
