package controller;

import java.util.List;
import java.util.Locale;

import exception.DatabaseException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;
import service.ProductService;
import util.DialogUtil;

/**
 * Controller for dashboard.fxml.
 *
 * It only talks to ProductService (the service layer), never
 * to SQL or JDBC directly. It shows:
 *  - total number of products
 *  - total stock units
 *  - total inventory value
 *  - number of low-stock products and the list of them
 */
public class DashboardController {

    private final ProductService productService =
            new ProductService();

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalStockUnitsLabel;

    @FXML
    private Label totalInventoryValueLabel;

    @FXML
    private Label lowStockCountLabel;

    @FXML
    private Label lowStockMessageLabel;

    @FXML
    private TableView<Product> lowStockTable;

    @FXML
    private TableColumn<Product, String> lowSkuColumn;

    @FXML
    private TableColumn<Product, String> lowNameColumn;

    @FXML
    private TableColumn<Product, String> lowTypeColumn;

    @FXML
    private TableColumn<Product, Integer> lowQuantityColumn;

    @FXML
    private TableColumn<Product, Integer> lowReorderColumn;

    @FXML
    private void initialize() {
        lowSkuColumn.setCellValueFactory(
                new PropertyValueFactory<>("sku")
        );

        lowNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );

        lowTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("productType")
        );

        lowQuantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        lowReorderColumn.setCellValueFactory(
                new PropertyValueFactory<>("reorderLevel")
        );

        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            List<Product> allProducts =
                    productService.getAllProducts();

            List<Product> lowStockProducts =
                    productService.getLowStockProducts();

            int totalStockUnits = 0;
            double totalInventoryValue = 0.0;

            for (Product product : allProducts) {
                totalStockUnits += product.getQuantity();
                totalInventoryValue +=
                        product.calculateStockValue();
            }

            totalProductsLabel.setText(
                    String.valueOf(allProducts.size())
            );

            totalStockUnitsLabel.setText(
                    String.valueOf(totalStockUnits)
            );

            totalInventoryValueLabel.setText(
                    String.format(
                            Locale.US,
                            "%,.2f",
                            totalInventoryValue
                    )
            );

            lowStockCountLabel.setText(
                    String.valueOf(lowStockProducts.size())
            );

            lowStockTable.setItems(
                    FXCollections.observableArrayList(
                            lowStockProducts
                    )
            );

            if (lowStockProducts.isEmpty()) {
                lowStockMessageLabel.setText(
                        "All products are above their reorder level."
                );
            } else {
                lowStockMessageLabel.setText(
                        "These products are at or below their reorder level."
                );
            }

        } catch (DatabaseException exception) {
            DialogUtil.showError(
                    "Database Error",
                    "Unable to load dashboard data: "
                            + exception.getMessage()
            );
        }
    }
}
