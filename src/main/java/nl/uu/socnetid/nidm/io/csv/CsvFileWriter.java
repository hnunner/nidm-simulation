package nl.uu.socnetid.nidm.io.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Hendrik Nunner
 */
public abstract class CsvFileWriter extends FileWriter {

    private static final Logger logger = Logger.getLogger(CsvFileWriter.class);
    private static final char DEFAULT_SEPARATOR = ',';


    /**
     * Creates a generic nl.uu.socnetid.nidm.io.csv file writer.
     *
     * @param fileName
     *          the name of the file to store the nl.uu.socnetid.nidm.io.csv data in
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CsvFileWriter(String fileName) throws IOException {
        super(fileName);
    }


    //https://tools.ietf.org/html/rfc4180
    private String followCVSformat(String value) {

        String result = value;
        if (result == null) {
            result = "NA";
        }
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    protected void writeLine(List<String> values) {
        writeLine(values, DEFAULT_SEPARATOR, ' ');
    }

    protected void writeLine(List<String> values, char separators, char customQuote) {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        try {
            append(sb.toString());
            flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
