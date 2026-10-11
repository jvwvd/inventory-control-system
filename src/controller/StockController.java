package controller;

import java.util.List;

import exception.DatabaseException;
import exception.InsufficientStockException;
import exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import model.InventoryUser;
import model.Product;
import model.StockMovement;
import service.ProductService;
import service.StockService;
import util.DialogUtil;
import util.SessionManager;

public class StockController {

    private final StockService stockService =
            new StockService();

    private final ProductService productService =
            new ProductService();

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private Label currentQuantityLabel;

    @FXML
    private TextField quantityField;

    @FXML
    private TextArea notesArea;

    @FXML
    private void initialize() {

        productComboBox.setConverter(
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

        productComboBox
                .valueProperty()
                .addListener(
                        (observable, oldProduct, newProduct) ->
                                updateCurrentQuantity(
                                        newProduct
                                )
                );

        loadProducts();
    }

    @FXML
    private void handleStockIn() {

        Product product =
                productComboBox.getValue();

        InventoryUser user =
                SessionManager.getCurrentUser();

        if (product == null) {
            DialogUtil.showWarning(
                    "Validation Error",
                    "Please select a product."
            );

            return;
        }

        if (user == null) {
            DialogUtil.showWarning(
                    "Session Error",
                    "No logged-in user was found."
            );

            return;
        }

        try {

            int quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim()
                    );

            StockMovement movement =
                    stockService.recordStockIn(
                            product.getId(),
                            user.getId(),
                            quantity,
                            notesArea.getText()
                    );

            DialogUtil.showInformation(
                    "Stock In Successful",
                    "Stock added successfully.\n"
                            + "Quantity before: "
                            + movement.getQuantityBefore()
                            + "\nQuantity after: "
                            + movement.getQuantityAfter()
            );

            clearForm();
            loadProducts();

        } catch (NumberFormatException exception) {

            DialogUtil.showWarning(
                    "Validation Error",
                    "Please enter a valid whole number "
                            + "for stock quantity."
            );

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
    private void handleStockOut() {

        Product product =
                productComboBox.getValue();

        InventoryUser user =
                SessionManager.getCurrentUser();

        if (product == null) {
            DialogUtil.showWarning(
                    "Validation Error",
                    "Please select a product."
            );

            return;
        }

        if (user == null) {
            DialogUtil.showWarning(
                    "Session Error",
                    "No logged-in user was found."
            );

            return;
        }

        try {

            int quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim()
                    );

            StockMovement movement =
                    stockService.recordStockOut(
                            product.getId(),
                            user.getId(),
                            quantity,
                            notesArea.getText()
                    );

            DialogUtil.showInformation(
                    "Stock Out Successful",
                    "Stock removed successfully.\n"
                            + "Quantity before: "
                            + movement.getQuantityBefore()
                            + "\nQuantity after: "
                            + movement.getQuantityAfter()
            );

            clearForm();
            loadProducts();

        } catch (NumberFormatException exception) {

            DialogUtil.showWarning(
                    "Validation Error",
                    "Please enter a valid whole number "
                            + "for stock quantity."
            );

        } catch (InsufficientStockException exception) {

            DialogUtil.showWarning(
                    "Insufficient Stock",
                    exception.getMessage()
            );

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
    private void handleClear() {
        clearForm();
    }

    private void loadProducts() {

        try {

            List<Product> products =
                    productService.getAllProducts();

            productComboBox
                    .getItems()
                    .setAll(products);

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void updateCurrentQuantity(
            Product product
    ) {

        if (product == null) {
            currentQuantityLabel.setText(
                    "Current Quantity: -"
            );

            return;
        }

        currentQuantityLabel.setText(
                "Current Quantity: "
                        + product.getQuantity()
        );
    }

    private void clearForm() {

        productComboBox.setValue(null);
        quantityField.clear();
        notesArea.clear();

        currentQuantityLabel.setText(
                "Current Quantity: -"
        );
    }
}