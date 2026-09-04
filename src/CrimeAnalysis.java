import CrimeAnaylzer.CrimeAnalyzerService;
import CrimeAnaylzer.CrimeAnalyzerServiceImpl;


public class CrimeAnalysis {

    public static void main(String[] args) {

        // Create the service that performs the crime analysis.
        CrimeAnalyzerService crimeAnalyzerService = new CrimeAnalyzerServiceImpl();

        // Read crime-1.csv and save the calculated answers to file2.txt.
        crimeAnalyzerService.analyze("crime-1.csv", "file2.txt");
    }
}
