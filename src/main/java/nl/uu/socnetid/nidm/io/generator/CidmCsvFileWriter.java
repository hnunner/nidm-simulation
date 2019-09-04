package nl.uu.socnetid.nidm.io.generator;

import java.io.IOException;

import nl.uu.socnetid.nidm.data.CidmDataGeneratorData;
import nl.uu.socnetid.nidm.io.csv.CsvFileWriter;

/**
 * @author Hendrik Nunner
 */
public abstract class CidmCsvFileWriter extends CsvFileWriter {

    protected CidmDataGeneratorData dgData;


    /**
     * Creates a generic nl.uu.socnetid.nidm.io.csv file writer.
     *
     * @param fileName
     *          the name of the file to store the nl.uu.socnetid.nidm.io.csv data in
     * @param dgData
     *          the data from the data generator to store
     * @throws IOException
     *          if the named file exists but is a directory rather
     *          than a regular file, does not exist but cannot be
     *          created, or cannot be opened for any other reason
     */
    public CidmCsvFileWriter(String fileName, CidmDataGeneratorData dgData) throws IOException {
        super(fileName);
        this.dgData = dgData;
        initCols();
    }


    /**
     * Initializes the CSV by writing the column names.
     */
    protected abstract void initCols();

    /**
     * Writes a line of data as currently stored in dgData.
     */
    public abstract void writeCurrentData();

}
