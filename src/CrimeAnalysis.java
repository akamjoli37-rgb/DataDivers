import CrimeAnaylzer.CrimeAnalyzerService;
import CrimeAnaylzer.CrimeAnalyzerServiceImpl;
import CrimeAnaylzer.CrimeRecord;
import Location.Location;

import java.io.File;

import java.util.*;


//function to read the csv file and organize the attributes into arrays.
public class CrimeAnalysis {


    public static void main(String[] args) {

        CrimeAnalyzerService crimeAnalyzerService = new CrimeAnalyzerServiceImpl();

        // read the csv file
        List<CrimeRecord> readFile = crimeAnalyzerService.read("crime-1.csv");

        // a. How many crimes were reported in the year 2013 and 2014 combined?
        System.out.println("Question a: How many crimes were reported in the year 2013 and 2014 combined? \nAnswer: " + crimeAnalyzerService.crimeReported(readFile));

        // b. How many incidents involving shootings were reported in the year “2015” and “2016” combined?
        System.out.println("Question b: How many incidents involving shootings were reported in the year “2015” and “2016” combined? \nAnswer: " + crimeAnalyzerService.incidentsInvolvingShootings(readFile));

        // c. How many “drug violation” incidents occurred in all Districts “A” and “B” combined?
        System.out.println("Question c: How many “drug violation” incidents occurred in all Districts “A” and “B” combined? \nAnswer: " + crimeAnalyzerService.drugViolationIncidents(readFile));

        // d. Which were the three most common offense codes in 2016 (sorted in descending order of the number of offenses)?
        System.out.println("Question d: Which were the three most common offense codes in 2016 (sorted in descending order of the number of offenses)? \nAnswer: " + crimeAnalyzerService.mostCommonOffenseCodes(readFile));


        crimeAnalyzerService.getCrimeRecord("crime-1.csv");

        System.out.println("Records read successfully!!");
    }
}