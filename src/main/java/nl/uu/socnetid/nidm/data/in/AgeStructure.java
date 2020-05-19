package nl.uu.socnetid.nidm.data.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.uu.socnetid.nidm.system.PropertiesHandler;

/**
 * @author Hendrik Nunner
 */
public class AgeStructure {

    // logger
    private static final Logger logger = LogManager.getLogger(AgeStructure.class);

    private List<Integer> ageDistribution;
    private double ageAssortativity;


    /**
     * Singleton: private instantiation.
     */
    private AgeStructure() {
        initAgeDistribution();
        initAgeAssortativity();
    }


    /**
     * Initializes the age structure of the agents: a list of random ageDistribution is stored into this.ages
     * (to be randomly drawn from) according to the age distribution specified in config.properties.
     *
     * TODO add age selection in GUI
     */
    private void initAgeDistribution() {
        this.ageDistribution = new ArrayList<Integer>();

        Path pathToFile = PropertiesHandler.getInstance().getAgeDistributionImportPath();

        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
            String line = br.readLine();    // skip column names
            line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(";");
                int min = Integer.valueOf(attributes[0]);
                int max = Integer.valueOf(attributes[1]);
                int totalNorm = Integer.valueOf(attributes[3]);

                for (int i = 0; i < totalNorm; i++) {
                    // store a random age within the year ranges of the age group
                    this.ageDistribution.add(ThreadLocalRandom.current().nextInt(min, max+1));
                }

                line = br.readLine();
            }

        } catch (IOException ioe) {
            logger.error("Error while parsing age distribution.", ioe);
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
    }

    /**
     * Casts a String array into an array of doubles.
     *
     * @param line
     *          the array of Strings
     * @return the array of doubles
     */
    private double[] lineToDoubles(String[] line) {
        double[] doubles = new double[line.length];
        for (int i=0; i<line.length; i++) {
            doubles[i] = Double.parseDouble(line[i].trim().replaceAll("\\uFEFF", ""));
        }
        return doubles;
    }

    /**
     * Initializes the age assortativity of the agents stored in /resources/age-ass.csv.
     *
     * TODO generalize assortativity computation
     */
    private void initAgeAssortativity() {

        Path pathToFile = PropertiesHandler.getInstance().getAgeAssortativityImportPath();

        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);

            // columns = age groups
            String line = br.readLine();
            double[] ageGroups = lineToDoubles(line.split(";"));

            int i = 0;                  // running index for age source
            List<Double> agesSrc = new ArrayList<Double>();
            List<Double> agesDst = new ArrayList<Double>();
            line = br.readLine();
            while (line != null) {
                double ageSrc = ageGroups[i];
                double[] freqs = lineToDoubles(line.split(";"));
                int j = 0;          // running index for age destination

                for (int k=0; k<freqs.length; k++) {
                    double freq = freqs[k];
                    double ageDst = ageGroups[j];
                    for (int l=0; l<freq; l++) {
                        agesSrc.add(ageSrc);
                        agesDst.add(ageDst);
                    }
                    j++;
                }
                i++;
                line = br.readLine();
            }

            this.ageAssortativity = 0.0;

            // Pearson correlation coefficient
            if (agesSrc.size() > 1 || agesDst.size() > 1) {
                try {
                    this.ageAssortativity = new PearsonsCorrelation().correlation(
                            agesSrc.stream().mapToDouble(Double::doubleValue).toArray(),
                            agesDst.stream().mapToDouble(Double::doubleValue).toArray());
                    if (Double.isNaN(this.ageAssortativity)) {
                        this.ageAssortativity = 0.0;
                    }
                } catch (Exception e) {
                    logger.error("Computation of Pearson's correlation coefficient failed: ", e);
                    this.ageAssortativity = 0.0;
                }
            }
            logger.info("Age assortativity: " + Math.round(this.ageAssortativity*100.0)/100.0);

        } catch (IOException ioe) {
            logger.error("Error while parsing age assortativity.", ioe);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                    logger.error("Error while closing BufferedReader", ioe);
                }
            }
        }
    }



    /**
     * Lazy thread safe initialization of singleton.
     *
     * @author Hendrik Nunner
     */
    private static class LazyHolder {
        public static final AgeStructure AS = new AgeStructure();
    }

    /**
     * Gets the AgeStructure singleton.
     *
     * @return the AgeStructure singleton
     */
    public static AgeStructure getInstance() {
        return LazyHolder.AS;
    }


    /**
     * Draws a random age from the previously initialized age distribution (see initAgeDistribution).
     *
     * @return a randomly drawn age from the age distribution
     */
    public int getRandomAge() {
        return this.ageDistribution.get(ThreadLocalRandom.current().nextInt(0, this.ageDistribution.size()));
    }

    /**
     * Gets the age assortativity.
     *
     * @return the age assortativity
     */
    public double getAgeAssortativity() {
        return this.ageAssortativity;
    }

}
