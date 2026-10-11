package controller;

import java.time.LocalDateTime;
import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import model.Product;
import model.StockMovement;
import service.ProductService;
import service.StockService;
import util.DialogUtil;

public class MovementHistoryController {

    private final StockService stockService =
            new StockService();

    private final ProductService productService =
            new ProductService();

    @FXML
    private TableView<StockMovement> movementTable;

    @FXML
    private TableColumn<StockMovement, Integer> idColumn;

    @FXML
    private TableColumn<StockMovement, Integer> productIdColumn;

    @FXML
    private TableColumn<StockMovement, Integer> userIdColumn;

    @FXML
    private TableColumn<StockMovement, String> movementTypeColumn;

    @FXML
    private TableColumn<StockMovement, Integer> quantityColumn;

    @FXML
    private TableColumn<StockMovement, Integer> quantityBeforeColumn;

    @FXML
    private TableColumn<StockMovement, Integer> quantityAfterColumn;

    @FXML
    private TableColumn<StockMovement, LocalDateTime> movementDateColumn;

    @FXML
    private TableColumn<StockMovement, String> notesColumn;

    @FXML
    private ComboBox<Product> productFilterComboBox;

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        productIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("productId")
        );

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("userId")
        );

        movementTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("movementType")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        quantityBeforeColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantityBefore")
        );

        quantityAfterColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantityAfter")
        );

        movementDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("movementDate")
        );

        notesColumn.setCellValueFactory(
                new PropertyValueFactory<>("notes")
        );

        productFilterComboBox.setConverter(
                new StringConverter<Product>() {

                    @Override
                    public String toString(
                            Product product
                    ) {
                        if (product == null) {
                            return "";
                        }

                        return product.getSku()
                                + " - "
                                + product.getProductName();
                    }

                    @Override
                    public Product fromString(
                            String string
                    ) {
                        return null;
                    }
                }
        );

        loadProducts();
        loadAllMovements();
    }

    @FXML
    private void handleFilter() {

        Product product =
                productFilterComboBox.getValue();

        if (product == null) {

            DialogUtil.showWarning(
                    "No Product Selected",
                    "Please select a product to filter."
            );

            return;
        }

        try {

            List<StockMovement> movements =
                    stockService
                            .getStockMovementsByProductId(
                                    product.getId()
                            );

            movementTable
                    .getItems()
                    .setAll(movements);

        } catch (ValidationException exception) {

            DialogUtil.showWarning(
                    "Validation Error",
                    exception.getMessage()
            );

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void handleShowAll() {

        productFilterComboBox.setValue(null);

        loadAllMovements();
    }

    @FXML
    private void handleRefresh() {

        loadProducts();
        loadAllMovements();
    }

    private void loadProducts() {

        try {

            List<Product> products =
                    productService.getAllProducts();

            productFilterComboBox
                    .getItems()
                    .setAll(products);

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void loadAllMovements() {

        try {

            List<StockMovement> movements =
                    stockService
                            .getAllStockMovements();

            movementTable
                    .getItems()
                    .setAll(movements);

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }
}