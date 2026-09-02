package CrimeAnaylzer;

import Location.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CrimeAnalyzerServiceImpl implements CrimeAnalyzerService {

    @Override
    public List<CrimeRecord> read(String filename) {
        List<CrimeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 12) continue;

                CrimeRecord r = setCrimeRecord(parts);

                records.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public CrimeRecord setCrimeRecord(String[] parts) {

        CrimeRecord r = new CrimeRecord();
        r.setIncidentNumber(parts[0]);
        r.setOffenseCode(parts[1]);
        r.setOffenseCodeGroup(parts[2]);
        r.setOffenseDescription(parts[3]);
        r.setDistrict(parts[4]);
        r.setStatus(parts[5]);
        r.setReportingArea(parts[6]);
        r.setShooting(parts[7]);
        r.setOccurredOnDate(parts[8]);
        r.setUcrPart(parts[9]);
        r.setStreet(parts[10]);
        r.setLocation(new Location(parts[11], parts[12]));

        return r;
    }

    @Override
    public void getCrimeRecord(String filename) {

        for (CrimeRecord record : read(filename)) {
            System.out.printf("%s %s %s %s %s %s %s %s %s %s %s %s%n",
                    record.incidentNumber,
                    record.offenseCode,
                    record.offenseCodeGroup,
                    record.offenseDescription,
                    record.district,
                    record.status,
                    record.reportingArea,
                    record.shooting,
                    record.occurredOnDate,
                    record.ucrPart,
                    record.street,
                    record.location
            );
        }



    }


}
