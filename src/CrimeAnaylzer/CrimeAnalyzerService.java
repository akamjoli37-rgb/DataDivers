package CrimeAnaylzer;

import java.util.List;

public interface CrimeAnalyzerService {

     List<CrimeRecord> read(String filename);
     void getCrimeRecord(String filename);
     int crimeReported(List<CrimeRecord> records);
     int incidentsInvolvingShootings(List<CrimeRecord> records);
     int drugViolationIncidents(List<CrimeRecord> records);
     List<String> mostCommonOffenseCodes(List<CrimeRecord> records);
     int crimeReported(List<CrimeRecord> records);
     int shootingCrime(List<CrimeRecord> records);

}
