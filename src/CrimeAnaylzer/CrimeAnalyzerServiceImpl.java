package CrimeAnaylzer;

import Location.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CrimeAnalyzerServiceImpl implements CrimeAnalyzerService {


    @Override
    public List<CrimeRecord> read(String filename) {
        List<CrimeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip the header

            while ((line = br.readLine()) != null) {

                String[] parts = splitCSV(line);

                CrimeRecord r = setCrimeRecord(parts);

                records.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
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


    // How many crimes were reported in the year 2013 and 2014 combined?
    @Override
    public int crimeReported(List<CrimeRecord> records) {

        int count = 0;

        for (CrimeRecord record : records) {

            String date = record.getOccurredOnDate();

            if (date == null) {
                continue;
            }

            if (date.startsWith("2013") || date.startsWith("2014")) {
                count++;
            }
        }

        return count;
    }

    // How many incidents involving shootings were reported in the year “2015” and “2016” combined?
    @Override
    public int incidentsInvolvingShootings(List<CrimeRecord> records) {

        int count = 0;

        for(CrimeRecord record : records){
            String date = record.getOccurredOnDate();
            boolean isShooting = "Y".equals(record.getShooting());

            if (date != null &&
                    (date.startsWith("2015") || date.startsWith("2016")) &&
                    isShooting) {

                count++;
            }
        }

        return count;

    }

    // How many “drug violation” incidents occurred in all Districts “A” and “B” combined?
    @Override
    public int drugViolationIncidents(List<CrimeRecord> records) {

        int count = 0;

        for (CrimeRecord record : records) {
            String offenseCodeGroup = record.getOffenseCodeGroup();
            String district = record.getDistrict();

            if (offenseCodeGroup != null && district != null) {
                if (offenseCodeGroup.equalsIgnoreCase("Drug Violation") && (district.startsWith("A") || district.startsWith("B"))) {
                    count++;
                }
            }
        }

        return count;
    }


    // Which were the three most common offense codes in 2016 (sorted in descending order of the number of offenses)?
    @Override
    public List<String> mostCommonOffenseCodes(List<CrimeRecord> records) {

        List<String> offenseCodes = new ArrayList<>();

        for (CrimeRecord record : records){
            if(record.getOffenseCode() != null && record.getOccurredOnDate() != null){
                if(record.getOccurredOnDate().startsWith("2016")){
                    offenseCodes.add(record.getOffenseCode());
                }
            }
        }



        return offenseCodes;
    }


    // helper functions
    private String[] splitCSV(String line) {

        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }

        // Add the last column
        values.add(value.toString());

        return values.toArray(new String[0]);
    }

    private CrimeRecord setCrimeRecord(String[] parts) {

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
        r.setLocation(parts[11]);

        return r;
    }


}
