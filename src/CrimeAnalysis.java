import CrimeAnaylzer.CrimeAnalyzerService;
import CrimeAnaylzer.CrimeAnalyzerServiceImpl;
import CrimeAnaylzer.CrimeRecord;
import Location.Location;

import java.util.*;


//function to read the csv file and organize the attributes into arrays.
public class CrimeAnalysis {


    public static void main(String[] args) {

        CrimeAnalyzerService crimeAnalyzerService = new CrimeAnalyzerServiceImpl();

        crimeAnalyzerService.getCrimeRecord("crime-1.csv");

        System.out.println("Records read successfully!!");
    }
}