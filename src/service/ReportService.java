package service;

import java.util.List;

import dao.JdbcReportDAO;
import dao.ReportDAO;
import exception.DatabaseException;
import model.InventoryReportRow;

public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new JdbcReportDAO();
    }

    public ReportService(ReportDAO reportDAO) {
        if (reportDAO == null) {
            throw new IllegalArgumentException(
                    "ReportDAO cannot be null."
            );
        }

        this.reportDAO = reportDAO;
    }

    public List<InventoryReportRow>
            generateInventoryReport()
            throws DatabaseException {

        return reportDAO.generateInventoryReport();
    }

    public double calculateTotalInventoryValue(
            List<InventoryReportRow> reportRows
    ) {
        if (reportRows == null) {
            throw new IllegalArgumentException(
                    "Report rows cannot be null."
            );
        }

        double totalInventoryValue = 0.0;

        for (InventoryReportRow reportRow
                : reportRows) {

            totalInventoryValue +=
                    reportRow.getTotalValue();
        }

        return Math.round(
                totalInventoryValue * 100.0
        ) / 100.0;
    }
}