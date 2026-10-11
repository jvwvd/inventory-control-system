package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import exception.DatabaseException;
import model.InventoryReportRow;
import service.ReportService;
import util.CsvInventoryExporter;

public class CsvExportTest {

    public static void main(String[] args) {

        String outputFolder =
                "exports";

        String outputFile =
                outputFolder
                        + "/inventory_report.csv";

        try {
            Files.createDirectories(
                    Path.of(outputFolder)
            );

            ReportService reportService =
                    new ReportService();

            List<InventoryReportRow> reportRows =
                    reportService
                            .generateInventoryReport();

            CsvInventoryExporter
                    .exportInventoryReport(
                            reportRows,
                            outputFile
                    );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "CSV EXPORT TEST"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "[PASS] Inventory report exported."
            );

            System.out.println(
                    "Rows exported: "
                            + reportRows.size()
            );

            System.out.println(
                    "File location:"
            );

            System.out.println(
                    Path.of(outputFile)
                            .toAbsolutePath()
            );

        } catch (DatabaseException exception) {
            System.out.println(
                    "[FAILED] Report could not be generated."
            );

            System.out.println(
                    exception.getMessage()
            );

        } catch (IOException exception) {
            System.out.println(
                    "[FAILED] CSV file could not be written."
            );

            System.out.println(
                    exception.getMessage()
            );
        }
    }
}
