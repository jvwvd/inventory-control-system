package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import model.InventoryReportRow;

public final class CsvInventoryExporter {

    private CsvInventoryExporter() {
    }

    public static void exportInventoryReport(
            List<InventoryReportRow> reportRows,
            String filePath
    ) throws IOException {

        if (reportRows == null) {
            throw new IllegalArgumentException(
                    "Report rows cannot be null."
            );
        }

        if (filePath == null
                || filePath.isBlank()) {

            throw new IllegalArgumentException(
                    "CSV file path is required."
            );
        }

        Path outputPath =
                Path.of(filePath.trim());

        try (BufferedWriter writer =
                Files.newBufferedWriter(
                        outputPath,
                        StandardCharsets.UTF_8
                )) {

            writeHeader(writer);

            for (InventoryReportRow reportRow
                    : reportRows) {

                if (reportRow != null) {
                    writeReportRow(
                            writer,
                            reportRow
                    );
                }
            }
        }
    }

    private static void writeHeader(
            BufferedWriter writer
    ) throws IOException {

        writer.write(
                "SKU,"
                        + "Product Name,"
                        + "Product Type,"
                        + "Category,"
                        + "Supplier,"
                        + "Quantity,"
                        + "Reorder Level,"
                        + "Unit Price (£),"
                        + "Total Value (£),"
                        + "Stock Status"
        );

        writer.newLine();
    }

    private static void writeReportRow(
            BufferedWriter writer,
            InventoryReportRow reportRow
    ) throws IOException {

        String csvRow =
                String.join(
                        ",",
                        escapeCsv(
                                reportRow.getSku()
                        ),
                        escapeCsv(
                                reportRow.getProductName()
                        ),
                        escapeCsv(
                                reportRow.getProductType()
                        ),
                        escapeCsv(
                                reportRow.getCategoryName()
                        ),
                        escapeCsv(
                                reportRow.getSupplierName()
                        ),
                        Integer.toString(
                                reportRow.getQuantity()
                        ),
                        Integer.toString(
                                reportRow.getReorderLevel()
                        ),
                        formatMoney(
                                reportRow.getUnitPrice()
                        ),
                        formatMoney(
                                reportRow.getTotalValue()
                        ),
                        escapeCsv(
                                reportRow.getStockStatus()
                        )
                );

        writer.write(csvRow);
        writer.newLine();
    }

    private static String formatMoney(
            double value
    ) {
        return String.format(
                Locale.UK,
                "%.2f",
                value
        );
    }

    private static String escapeCsv(
            String value
    ) {
        if (value == null) {
            return "";
        }

        String escapedValue =
                value.replace(
                        "\"",
                        "\"\""
                );

        if (escapedValue.contains(",")
                || escapedValue.contains("\"")
                || escapedValue.contains("\n")
                || escapedValue.contains("\r")) {

            return "\""
                    + escapedValue
                    + "\"";
        }

        return escapedValue;
    }
}