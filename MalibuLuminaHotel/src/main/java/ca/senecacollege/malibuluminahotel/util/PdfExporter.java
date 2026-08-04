package ca.senecacollege.malibuluminahotel.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting data to PDF format.
 */
public class PdfExporter {

    /**
     * Export data to PDF file.
     *
     * @param data     List of maps where keys are column headers
     * @param filename Output filename
     * @param title    Report title
     * @throws FileNotFoundException if file cannot be created
     */
    public static void exportToPdf(List<Map<String, String>> data, String filename, String title)
            throws FileNotFoundException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        document.add(new Paragraph(title).setFontSize(18).setBold());
        document.add(new Paragraph(" ")); // Spacing

        // Create table
        String[] headers = data.get(0).keySet().toArray(new String[0]);
        Table table = new Table(headers.length);

        // Add headers
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        // Add data rows
        for (Map<String, String> row : data) {
            for (String header : headers) {
                table.addCell(row.getOrDefault(header, ""));
            }
        }

        document.add(table);
        document.close();
    }
}
