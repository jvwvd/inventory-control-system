package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.DatabaseException;
import model.InventoryReportRow;
import util.DatabaseConnection;

public class JdbcReportDAO implements ReportDAO {

    private static final String INVENTORY_REPORT_SQL =
            "SELECT p.sku, p.product_name, p.product_type, "
                    + "c.category_name, s.supplier_name, "
                    + "p.quantity, p.reorder_level, "
                    + "p.unit_price, "
                    + "ROUND(p.quantity * p.unit_price, 2) "
                    + "AS total_value, "
                    + "CASE "
                    + "WHEN p.quantity <= p.reorder_level "
                    + "THEN 'Low Stock' "
                    + "ELSE 'In Stock' "
                    + "END AS stock_status "
                    + "FROM products p "
                    + "JOIN categories c "
                    + "ON p.category_id = c.category_id "
                    + "JOIN suppliers s "
                    + "ON p.supplier_id = s.supplier_id "
                    + "ORDER BY p.product_name";

    @Override
    public List<InventoryReportRow>
            generateInventoryReport()
            throws DatabaseException {

        List<InventoryReportRow> reportRows =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                INVENTORY_REPORT_SQL
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                reportRows.add(
                        mapReportRow(resultSet)
                );
            }

            return reportRows;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to generate inventory report.",
                    exception
            );
        }
    }

    private InventoryReportRow mapReportRow(
            ResultSet resultSet
    ) throws SQLException {

        InventoryReportRow reportRow =
                new InventoryReportRow();

        reportRow.setSku(
                resultSet.getString("sku")
        );

        reportRow.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        reportRow.setProductType(
                resultSet.getString(
                        "product_type"
                )
        );

        reportRow.setCategoryName(
                resultSet.getString(
                        "category_name"
                )
        );

        reportRow.setSupplierName(
                resultSet.getString(
                        "supplier_name"
                )
        );

        reportRow.setQuantity(
                resultSet.getInt("quantity")
        );

        reportRow.setReorderLevel(
                resultSet.getInt(
                        "reorder_level"
                )
        );

        reportRow.setUnitPrice(
                resultSet.getDouble(
                        "unit_price"
                )
        );

        reportRow.setTotalValue(
                resultSet.getDouble(
                        "total_value"
                )
        );

        reportRow.setStockStatus(
                resultSet.getString(
                        "stock_status"
                )
        );

        return reportRow;
    }
}