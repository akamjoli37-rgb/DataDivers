package CrimeAnaylzer;


/**
 * Defines the main operation required for analyzing the crime dataset.
 */
public interface CrimeAnalyzerService {


    /**
     * Reads crime data from a file, performs the analysis,
     * and saves the answers to another file.
     *
     * @param readFrom path of the input crime CSV file
     * @param saveTo path of the output file
     */
    void analyze(String readFrom, String saveTo);

}
