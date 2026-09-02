package CrimeAnaylzer;

import java.util.List;

public interface CrimeAnalyzerService {

     List<CrimeRecord> read(String filename);
     CrimeRecord setCrimeRecord(String[] parts);
     void getCrimeRecord(String filename);



}
