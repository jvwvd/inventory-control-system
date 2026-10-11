package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.DatabaseException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.InventoryReportRow;
import service.ReportService;
import util.CsvInventoryExporter;
import util.DialogUtil;

public class InventoryReportController {

    private final ReportService reportService =
            new ReportService();

    @FXML
    private TableView<InventoryReportRow> reportTable;

    @FXML
    private TableColumn<InventoryReportRow, String> skuColumn;

    @FXML
    private TableColumn<InventoryReportRow, String> productNameColumn;

    @FXML
    private TableColumn<InventoryReportRow, String> productTypeColumn;

    @FXML
    private TableColumn<InventoryReportRow, String> categoryColumn;

    @FXML
    private TableColumn<InventoryReportRow, String> supplierColumn;

    @FXML
    private TableColumn<InventoryReportRow, Integer> quantityColumn;

    @FXML
    private TableColumn<InventoryReportRow, Integer> reorderLevelColumn;

    @FXML
    private TableColumn<InventoryReportRow, Double> unitPriceColumn;

    @FXML
    private TableColumn<InventoryReportRow, Double> totalValueColumn;

    @FXML
    private TableColumn<InventoryReportRow, String> stockStatusColumn;

    @FXML
    private Label totalInventoryValueLabel;

    @FXML
    private void initialize() {

        skuColumn.setCellValueFactory(
                new PropertyValueFactory<>("sku")
        );

        productNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        productTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("productType")
        );

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("categoryName")
        );

        supplierColumn.setCellValueFactory(
                new PropertyValueFactory<>("supplierName")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        reorderLevelColumn.setCellValueFactory(
                new PropertyValueFactory<>("reorderLevel")
        );

        unitPriceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice")
        );

        totalValueColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalValue")
        );

        stockStatusColumn.setCellValueFactory(
                new PropertyValueFactory<>("stockStatus")
        );

        formatMoneyColumn(
                unitPriceColumn
        );

        formatMoneyColumn(
                totalValueColumn
        );

        loadReport();
    }

    @FXML
    private void handleRefresh() {
        loadReport();
    }

    @FXML
    private void handleExportCsv() {

        if (reportTable.getItems().isEmpty()) {

            DialogUtil.showWarning(
                    "No Report Data",
                    "There are no report rows to export."
            );

            return;
        }

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Save Inventory Report"
        );

        fileChooser.setInitialFileName(
                "Inventory_Report.csv"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV Files",
                        "*.csv"
                )
        );

        File selectedFile =
                fileChooser.showSaveDialog(
                        reportTable
                                .getScene()
                                .getWindow()
                );

        if (selectedFile == null) {
            return;
        }

        String filePath =
                selectedFile.getAbsolutePath();

        if (!filePath
                .toLowerCase()
                .endsWith(".csv")) {

            filePath += ".csv";
        }

        List<InventoryReportRow> displayedRows =
                new ArrayList<>(
                        reportTable.getItems()
                );

        try {

            CsvInventoryExporter
                    .exportInventoryReport(
                            displayedRows,
                            filePath
                    );

            DialogUtil.showInformation(
                    "Export Successful",
                    "Inventory report exported successfully."
            );

        } catch (IOException exception) {

            DialogUtil.showError(
                    "Export Error",
                    "Unable to export the inventory report."
            );

        } catch (IllegalArgumentException exception) {

            DialogUtil.showWarning(
                    "Export Error",
                    exception.getMessage()
            );
        }
    }

    private void loadReport() {

        try {

            List<InventoryReportRow> reportRows =
                    reportService
                            .generateInventoryReport();

            reportTable
                    .getItems()
                    .setAll(reportRows);

            double totalInventoryValue =
                    reportService
                            .calculateTotalInventoryValue(
                                    reportRows
                            );

            totalInventoryValueLabel.setText(
                    String.format(
                            Locale.UK,
                            "Total Inventory Value: £%.2f",
                            totalInventoryValue
                    )
            );

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void formatMoneyColumn(
            TableColumn<InventoryReportRow, Double> column
    ) {

        column.setCellFactory(
                tableColumn ->
                        new TableCell<InventoryReportRow, Double>() {

                            @Override
                            protected void updateItem(
                                    Double value,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        value,
                                        empty
                                );

                                if (empty || value == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            String.format(
                                                    Locale.UK,
                                                    "£%.2f",
                                                    value
                                            )
                                    );
                                }
                            }
                        }
        );
    }
}