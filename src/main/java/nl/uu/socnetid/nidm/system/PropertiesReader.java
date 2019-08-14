package nl.uu.socnetid.nidm.system;

import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author Hendrik Nunner
 */
public class PropertiesReader {

    // logger
    private static final Logger logger = Logger.getLogger(PropertiesReader.class);

    // singleton
    private static PropertiesReader pReader = null;
    // properties
    private OsTypes osType = null;
    private String userDir = null;


    /**
     * Invisible constructor.
     */
    private PropertiesReader() {

        // determine operating system
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            this.osType = OsTypes.WIN;
        } else if (os.indexOf("mac") >= 0) {
            osType = OsTypes.MAC;
        } else if (os.indexOf("nix") >= 0
                || os.indexOf("nux") >= 0
                || os.indexOf("aix") >= 0) {
            this.osType = OsTypes.UNIX;
        } else {
            this.osType = OsTypes.OTHER;
        }

        // determine user directory
        this.userDir = System.getProperty("user.dir");

        // logging properties
        StringBuilder sb = new StringBuilder();
        sb.append("\n:::::::::::::::::::::::::::::::::::::::::::: SYSTEM INFOS ::::::::::::::::::::::::::::::::::::::::::::");
        // maven
        MavenXpp3Reader mvnReader = new MavenXpp3Reader();
        try {
            FileReader fileReader = new FileReader("pom.xml");
            try {
                Model model = mvnReader.read(fileReader);
                sb.append("\nBuild: ").append(model.getId());
            } catch (IOException|XmlPullParserException e) {
                logger.error(e);
            } finally {
                fileReader.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
        // system
        sb.append("\nSystem: ").append(
                System.getProperty("os.arch"));
        sb.append("\nOS: ").append(
                System.getProperty("os.name")).append(" (").append(
                        System.getProperty("os.version")).append(")");
        sb.append("\nUsed Java version: ").append(
                System.getProperty("java.version")).append(" by ").append(
                        System.getProperty("java.vendor")).append(" (").append(
                                System.getProperty("java.vendor.url")).append(")");
        sb.append("\nUser directory: ").append(this.userDir);
        sb.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info(sb.toString());
    }

    /**
     * Gets the type of operating system the simulation is running on.
     *
     * @return the type of operating system the simulation is running on
     */
    public static OsTypes getOsType() {
        if (pReader == null) {
            pReader = new PropertiesReader();
        }
        return pReader.osType;
    }

    /**
     * Gets the user's working directory.
     *
     * @return the user's working directory
     */
    public static String getUserDir() {
        if (pReader == null) {
            pReader = new PropertiesReader();
        }
        return pReader.userDir;
    }

}
