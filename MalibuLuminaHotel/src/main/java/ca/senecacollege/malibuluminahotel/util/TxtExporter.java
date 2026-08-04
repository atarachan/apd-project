package ca.senecacollege.malibuluminahotel.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for exporting data to TXT format.
 */
public class TxtExporter {

    /**
     * Export content to text file.
     *
     * @param content  Text content to write
     * @param filename Output filename
     * @throws IOException if file cannot be written
     */
    public static void exportToTxt(String content, String filename) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }
}
