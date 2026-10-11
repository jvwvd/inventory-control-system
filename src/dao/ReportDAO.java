package dao;

import java.util.List;

import exception.DatabaseException;
import model.InventoryReportRow;

public interface ReportDAO {

    List<InventoryReportRow> generateInventoryReport()
            throws DatabaseException;
}