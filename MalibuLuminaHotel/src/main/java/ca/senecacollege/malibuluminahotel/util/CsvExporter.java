package ca.senecacollege.malibuluminahotel.util;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting data to CSV format.
 */
public class CsvExporter {

    /**
     * Export data to CSV file.
     *
     * @param data     List of maps where keys are column headers
     * @param filename Output filename
     * @throws IOException if file cannot be written
     */
    public static void exportToCsv(List<Map<String, String>> data, String filename) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            // Write header
            String[] headers = data.get(0).keySet().toArray(new String[0]);
            writer.writeNext(headers);

            // Write data rows
            for (Map<String, String> row : data) {
                String[] values = new String[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    values[i] = row.getOrDefault(headers[i], "");
                }
                writer.writeNext(values);
            }
        }
    }
}
