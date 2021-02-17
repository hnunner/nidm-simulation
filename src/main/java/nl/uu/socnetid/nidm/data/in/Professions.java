package nl.uu.socnetid.nidm.data.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.agents.Agent;
import nl.uu.socnetid.nidm.networks.LockdownConditions;
import nl.uu.socnetid.nidm.networks.Network;
import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class Professions {

    // logger
    private static final Logger logger = LogManager.getLogger(Professions.class);

    // constants describing no and all professional groups
    public static final String NONE = "none";
    public static final String ALL = "all";

    private List<String> professions;
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
        this.professions = new ArrayList<String>();
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
            line = line.replaceAll("(?U)\\p{Cntrl}|\\p{Gc=Cf}", "");

            while (line != null) {
                String[] attributes = line.split(";");

                // profession
                String profession = attributes[0];
                this.professions.add(profession);

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
     * Get an iterator over all available professions.
     *
     * @return an iterator over all available professions
     */
    public Iterator<String> getProfessionsIterator() {
        return this.professions.iterator();
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
     * @return the professionDistribution
     */
    public List<String> getProfessionDistribution() {
        return professionDistribution;
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

    /**
     * Gets the error between a map of professions and degrees prior to lockdown
     * and the actual degrees prior to lockdown.
     *
     * @param compDegreePreLockdown
     *          the map of degrees prior to lockdown to compare
     * @return the error
     */
    public double getDegreePercentageErrorPreLockdown(Map<String, Double> compDegreePreLockdown) {

        double target = 0.0;
        double comp = 0.0;

        Iterator<String> it = compDegreePreLockdown.keySet().iterator();
        while (it.hasNext()) {
            String profession = it.next();
            comp += compDegreePreLockdown.get(profession);
            target += this.degreePreLockdown.get(profession);
        }

        return Math.abs((target - comp) / target);
    }

    /**
     * Gets the total difference between a map of professions and degrees and the actual degrees.
     *
     * @param conditionsByProfession
     *          the map of lockdown conditions by profession
     * @param network
     *          the network to get the degree error for
     * @return the difference
     */
    public double getDegreeDiffTotal(Map<String, LockdownConditions> conditionsByProfession, Network network) {

        double professionDiff = 0.0;

        for (Entry<String, LockdownConditions> cbp : conditionsByProfession.entrySet()) {

            String profession = cbp.getKey();

            double target = 0.0;
            switch (cbp.getValue()) {
                case DURING:
                    target += this.degreeDuringLockdown.get(profession);
                    break;

                case POST:
                    logger.warn("Unimplemented lockdown condition: '" + cbp.getValue() + "'. Using prior to lockdown instead.");
                    //$FALL-THROUGH$
                default:
                case PRE:
                    target += this.degreePreLockdown.get(profession);
                    break;
            }

            // double comp = compDegree.get(profession);
            Iterator<Agent> it = network.getAgents(profession).iterator();
            while (it.hasNext()) {
                Agent agent = it.next();
                professionDiff += target - agent.getDegree();
            }
        }

        return professionDiff;
    }

    /**
     * Gets the percentage difference between a map of professions and degrees and the actual degrees.
     *
     * @param conditionsByProfession
     *          the map of lockdown conditions by profession
     * @param network
     *          the network to get the degree error for
     * @return the difference
     */
    public double getDegreeDiffPercent(Map<String, LockdownConditions> conditionsByProfession, Network network) {

        double professionDiff = 0.0;

        for (Entry<String, LockdownConditions> cbp : conditionsByProfession.entrySet()) {

            String profession = cbp.getKey();

            double target = 0.0;
            switch (cbp.getValue()) {
                case DURING:
                    target += this.degreeDuringLockdown.get(profession);
                    break;

                case POST:
                    logger.warn("Unimplemented lockdown condition: '" + cbp.getValue() + "'. Using prior to lockdown instead.");
                    //$FALL-THROUGH$
                default:
                case PRE:
                    target += this.degreePreLockdown.get(profession);
                    break;
            }

            // double comp = compDegree.get(profession);
            Iterator<Agent> it = network.getAgents(profession).iterator();
            while (it.hasNext()) {
                Agent agent = it.next();
                professionDiff += Math.abs((target - agent.getDegree()) / target);
            }
        }

        return professionDiff/network.getN();
    }

}
