package nl.uu.socnetid.netgame.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Hendrik Nunner
 */
public class RScriptTest {

    public static final String RSCRIPT_DIR = new StringBuilder().append(System.getProperty("user.dir"))
            .append("/analysis/").toString();





    /**
     * Launches the data generation.
     *
     * @param args
     *          command line arguments
     */
    public static void main(String[] args) {

        try {

            String r = "/usr/local/bin/Rscript";
            String script = RSCRIPT_DIR + "analysis.R";
            String scriptArg = "20190808-134653";

            ProcessBuilder pb = new ProcessBuilder(r, script, scriptArg);
            Process p = pb.start();


            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = p.waitFor();
            System.out.println("\nExited with error code : " + exitCode);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
