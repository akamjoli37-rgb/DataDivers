package CrimeAnaylzer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implements the crime analysis operations.
 * The class reads crime records from a CSV file, answers the required
 * questions, and writes the results to an output file.
 */
public class CrimeAnalyzerServiceImpl implements CrimeAnalyzerService {

    /**
     * Reads crime records from a CSV file.
     *
     * @param filename the path of the CSV file to read
     * @return a list containing all crime records from the file
     */
    private List<CrimeRecord> read(String filename) {
        List<CrimeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // Skip the first line because it contains the CSV column headings.
            br.readLine();

            while ((line = br.readLine()) != null) {

                // Split the CSV row into separate column values.
                String[] parts = splitLine(line);

                // Convert the values into a CrimeRecord object.
                CrimeRecord r = setCrimeRecord(parts);

                records.add(r);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage() + "\n");
        }
        return records;
    }

    /**
     * Counts crimes reported during 2013 and 2014 combined.
     *
     * @param records the crime records to examine
     * @return the total number of crimes reported in 2013 and 2014
     */
    private int crimeReported(List<CrimeRecord> records) {

        int count = 0;

        for (CrimeRecord record : records) {

            String date = record.getOccurredOnDate();

            // Ignore records that do not have a date.
            if (date == null) {
                continue;
            }

            if (date.startsWith("2013") || date.startsWith("2014")) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts shooting incidents reported during 2015 and 2016 combined.
     *
     * @param records the crime records to examine
     * @return the number of shooting incidents in 2015 and 2016
     */
    private int incidentsInvolvingShootings(List<CrimeRecord> records) {

        int count = 0;

        for (CrimeRecord record : records) {
            String date = record.getOccurredOnDate();
            boolean isShooting = "Y".equalsIgnoreCase(record.getShooting());

            if (date != null &&
                    (date.startsWith("2015") || date.startsWith("2016")) &&
                    isShooting) {

                count++;
            }
        }

        return count;
    }

    /**
     * Counts drug violation incidents in districts beginning with A or B.
     *
     * @param records the crime records to examine
     * @return the number of drug violation incidents in districts A and B
     */
    private int drugViolationIncidents(List<CrimeRecord> records) {

        int count = 0;

        for (CrimeRecord record : records) {

            if ("Drug Violation".equalsIgnoreCase(record.getOffenseCodeGroup())
                    && record.getDistrict() != null
                    && (record.getDistrict().startsWith("A") || record.getDistrict().startsWith("B"))) {
                count++;
            }
        }

        return count;
    }

    /**
     * Finds the three most common offense codes reported in 2016.
     *
     * @param records the crime records to examine
     * @return the three most common offense codes in descending order
     */
    private List<String> mostCommonOffenseCodes(List<CrimeRecord> records) {

        // Stores each offense code and the number of times it appears.
        HashMap<String, Integer> offenseCodeCount = new HashMap<>();

        for (CrimeRecord record : records) {

            String offenseCode = record.getOffenseCode();
            String date = record.getOccurredOnDate();

            if (offenseCode != null && date != null) {

                if (date.startsWith("2016")) {

                    if (offenseCodeCount.containsKey(offenseCode)) {

                        int currentCount = offenseCodeCount.get(offenseCode);
                        offenseCodeCount.put(offenseCode, currentCount + 1);

                    } else {
                        // First time this offense code has been found.
                        offenseCodeCount.put(offenseCode, 1);
                    }
                }
            }
        }

        // Copy the offense codes into a list so that they can be sorted.
        List<String> counters = new ArrayList<>(offenseCodeCount.keySet());

        // Sort from the highest occurrence count to the lowest.
        counters.sort((o1, o2) -> Integer.compare(offenseCodeCount.get(o2), offenseCodeCount.get(o1)));

        return counters.subList(0, 3);
    }

    /**
     * Finds the two years with the highest number of robberies.
     *
     * @param records the crime records to examine
     * @return the two years with the most robberies in descending order
     */
    private List<String> mostRobberies(List<CrimeRecord> records) {

        // Stores each year and its number of robbery records.
        HashMap<String, Integer> yearCount = new HashMap<>();

        for (CrimeRecord record : records) {
            if (record.getOffenseCodeGroup() != null && record.getOccurredOnDate() != null) {
                if ("Robbery".equalsIgnoreCase(record.getOffenseCodeGroup())) {

                    // The first four characters of the date represent the year.
                    String key = record.getOccurredOnDate().substring(0, 4);
                    int count = 1;

                    if (yearCount.containsKey(key)) {
                        count = yearCount.get(key) + 1;
                    }

                    yearCount.put(key, count);
                }
            }
        }

        List<String> years = new ArrayList<>(yearCount.keySet());

        // Sort years by robbery count from highest to lowest.
        years.sort((o1, o2) -> Integer.compare(yearCount.get(o2), yearCount.get(o1)));

        return years.subList(0, Math.min(2, years.size()));
    }

    /**
     * Finds the three districts with the most unresolved crime cases.
     * Partially resolved cases are also counted as unresolved.
     *
     * @param records the crime records to examine
     * @return the three districts with the most unresolved cases
     */
    public List<String> mostUnsolvedCrimes(List<CrimeRecord> records) {

        // Stores each district and its number of unresolved cases.
        HashMap<String, Integer> districtsCounter = new HashMap<>();

        for (CrimeRecord record : records) {
            if (record.getStatus() != null && record.getDistrict() != null && !record.getDistrict().isBlank()) {
                if (record.getStatus().equalsIgnoreCase("Unresolved")
                        || record.getStatus().equalsIgnoreCase("Partially Resolved")) {

                    String district = record.getDistrict();
                    int count = 1;

                    if (districtsCounter.containsKey(district)) {
                        count = districtsCounter.get(district) + 1;
                    }
                    districtsCounter.put(district, count);
                }
            }
        }

        List<String> districts = new ArrayList<>(districtsCounter.keySet());

        // Sort districts from the highest unresolved-case count to the lowest.
        districts.sort((o1, o2) -> Integer.compare(districtsCounter.get(o2), districtsCounter.get(o1)));

        return districts.subList(0, Math.min(3, districts.size()));
    }

    /**
     * Splits one CSV row into individual values while allowing commas
     * inside quoted values.
     *
     * @param line one line from the CSV file
     * @return an array containing the values from the row
     */
    private String[] splitLine(String line) {

        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        // Tracks whether the current character is inside quotation marks.
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                // A comma outside quotes marks the end of a CSV value.
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }

        // Add the final value after the loop ends.
        values.add(value.toString());

        return values.toArray(new String[0]);
    }

    /**
     * Creates a CrimeRecord from the values of one CSV row.
     *
     * @param parts the column values from the CSV row
     * @return a populated CrimeRecord object
     */
    private CrimeRecord setCrimeRecord(String[] parts) {

        // A valid row must contain all 12 expected columns.
        if (parts.length < 12) {
            throw new IllegalArgumentException("Invalid crime record: expected 12 columns but got " + parts.length);
        }

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

    /**
     * Reads the input file, performs all required crime analyses,
     * and writes the answers to the output file.
     *
     * @param readFrom path of the CSV file containing crime records
     * @param saveTo path of the file where the answers will be saved
     */
    @Override
    public void analyze(String readFrom, String saveTo) {

        // Read the CSV file once and reuse the records for every question.
        List<CrimeRecord> readFile = read(readFrom);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveTo));

            // Write each question followed by its calculated answer.
            writer.write("Question a: How many crimes were reported in the year 2013 and 2014 combined? \nAnswer: " + crimeReported(readFile) + "\n\n");
            writer.write("Question b: How many incidents involving shootings were reported in the year “2015” and “2016” combined? \nAnswer: " + incidentsInvolvingShootings(readFile) + "\n\n");
            writer.write("Question c: How many “drug violation” incidents occurred in all Districts “A” and “B” combined? \nAnswer: " + drugViolationIncidents(readFile) + "\n\n");
            writer.write("Question d: Which were the three most common offense codes in 2016 (sorted in descending order of the number of offenses)? \nAnswer: " + mostCommonOffenseCodes(readFile) + "\n\n");
            writer.write("Question e: Which two years had the most robberies (sorted in descending order of the number of robberies)? \nAnswer: " + mostRobberies(readFile) + "\n\n");
            writer.write("Question f: Which three Districts had the most “unresolved” crime cases (sorted in descending order of unresolved cases)? \nAnswer: " + mostUnsolvedCrimes(readFile) + "\n\n");

            writer.close();

            System.out.println("Analysis completed successfully, answers saved to file2.txt");
        } catch (IOException e) {
            throw new RuntimeException("Error writing file: " + e.getMessage() + "\n");
        }
    }
}
