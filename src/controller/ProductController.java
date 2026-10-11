package controller;

import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import model.Category;
import model.ElectronicProduct;
import model.GeneralProduct;
import model.PerishableProduct;
import model.Product;
import model.Supplier;
import service.CategoryService;
import service.ProductService;
import service.SupplierService;
import util.DialogUtil;

public class ProductController {

    private final ProductService productService =
            new ProductService();

    private final CategoryService categoryService =
            new CategoryService();

    private final SupplierService supplierService =
            new SupplierService();

    private Product selectedProduct;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> skuColumn;

    @FXML
    private TableColumn<Product, String> productNameColumn;

    @FXML
    private TableColumn<Product, String> productTypeColumn;

    @FXML
    private TableColumn<Product, Integer> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> supplierColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Integer> reorderLevelColumn;

    @FXML
    private TableColumn<Product, Double> unitPriceColumn;

    @FXML
    private TableColumn<Product, String> specialInfoColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField skuField;

    @FXML
    private TextField productNameField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private ComboBox<String> productTypeComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField reorderLevelField;

    @FXML
    private TextField unitPriceField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker expiryDatePicker;

    @FXML
    private TextField warrantyMonthsField;

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

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
                new PropertyValueFactory<>("categoryId")
        );

        supplierColumn.setCellValueFactory(
                new PropertyValueFactory<>("supplierId")
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

        specialInfoColumn.setCellValueFactory(
                new PropertyValueFactory<>("specialInformation")
        );

        productTypeComboBox.getItems().addAll(
                "GENERAL",
                "PERISHABLE",
                "ELECTRONIC"
        );

        productTypeComboBox.setValue(
                "GENERAL"
        );

        categoryComboBox.setConverter(
                new StringConverter<Category>() {

                    @Override
                    public String toString(
                            Category category
                    ) {
                        if (category == null) {
                            return "";
                        }

                        return category.getCategoryName();
                    }

                    @Override
                    public Category fromString(
                            String string
                    ) {
                        return null;
                    }
                }
        );

        supplierComboBox.setConverter(
                new StringConverter<Supplier>() {

                    @Override
                    public String toString(
                            Supplier supplier
                    ) {
                        if (supplier == null) {
                            return "";
                        }

                        return supplier.getSupplierName();
                    }

                    @Override
                    public Supplier fromString(
                            String string
                    ) {
                        return null;
                    }
                }
        );

        productTypeComboBox
                .valueProperty()
                .addListener(
                        (observable, oldType, newType) ->
                                updateTypeFields()
                );

        productTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldProduct, newProduct) ->
                                populateForm(newProduct)
                );

        loadReferenceData();
        loadProducts();
        updateTypeFields();
    }

    @FXML
    private void handleAdd() {

        try {

            Product product =
                    createProductFromForm();

            if (product == null) {
                return;
            }

            productService.addProduct(
                    product
            );

            DialogUtil.showInformation(
                    "Product Added",
                    "Product added successfully."
            );

            clearForm();
            loadProducts();

        } catch (NumberFormatException exception) {

            DialogUtil.showWarning(
                    "Validation Error",
                    "Please enter valid numbers for "
                            + "quantity, reorder level, "
                            + "unit price and warranty months."
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
    private void handleUpdate() {

        if (selectedProduct == null) {

            DialogUtil.showWarning(
                    "No Product Selected",
                    "Please select a product to update."
            );

            return;
        }

        try {

            Product product =
                    createProductFromForm();

            if (product == null) {
                return;
            }

            product.setId(
                    selectedProduct.getId()
            );

            product.setCreatedAt(
                    selectedProduct.getCreatedAt()
            );

            productService.updateProduct(
                    product
            );

            DialogUtil.showInformation(
                    "Product Updated",
                    "Product updated successfully."
            );

            clearForm();
            loadProducts();

        } catch (NumberFormatException exception) {

            DialogUtil.showWarning(
                    "Validation Error",
                    "Please enter valid numbers for "
                            + "quantity, reorder level, "
                            + "unit price and warranty months."
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
    private void handleDelete() {

        if (selectedProduct == null) {

            DialogUtil.showWarning(
                    "No Product Selected",
                    "Please select a product to delete."
            );

            return;
        }

        try {

            productService.deleteProduct(
                    selectedProduct.getId()
            );

            DialogUtil.showInformation(
                    "Product Deleted",
                    "Product deleted successfully."
            );

            clearForm();
            loadProducts();

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
    private void handleSearch() {

        String keyword =
                searchField.getText();

        if (keyword == null
                || keyword.isBlank()) {

            loadProducts();
            return;
        }

        try {

            List<Product> products =
                    productService.searchProducts(
                            keyword.trim()
                    );

            productTable.getItems().setAll(
                    products
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

        searchField.clear();

        clearForm();
        loadProducts();
    }

    private Product createProductFromForm() {

        String productType =
                productTypeComboBox.getValue();

        if (productType == null) {

            DialogUtil.showWarning(
                    "Validation Error",
                    "Please select a product type."
            );

            return null;
        }

        Product product;

        if ("PERISHABLE".equals(productType)) {

            PerishableProduct perishableProduct =
                    new PerishableProduct();

            perishableProduct.setExpiryDate(
                    expiryDatePicker.getValue()
            );

            product = perishableProduct;

        } else if ("ELECTRONIC".equals(productType)) {

            ElectronicProduct electronicProduct =
                    new ElectronicProduct();

            String warrantyText =
                    warrantyMonthsField
                            .getText()
                            .trim();

            int warrantyMonths = 0;

            if (!warrantyText.isBlank()) {
                warrantyMonths =
                        Integer.parseInt(
                                warrantyText
                        );
            }

            electronicProduct.setWarrantyMonths(
                    warrantyMonths
            );

            product = electronicProduct;

        } else {

            product =
                    new GeneralProduct();
        }

        product.setSku(
                skuField.getText()
        );

        product.setProductName(
                productNameField.getText()
        );

        Category category =
                categoryComboBox.getValue();

        if (category == null) {
            product.setCategoryId(0);
        } else {
            product.setCategoryId(
                    category.getId()
            );
        }

        Supplier supplier =
                supplierComboBox.getValue();

        if (supplier == null) {
            product.setSupplierId(0);
        } else {
            product.setSupplierId(
                    supplier.getId()
            );
        }

        product.setQuantity(
                Integer.parseInt(
                        quantityField
                                .getText()
                                .trim()
                )
        );

        product.setReorderLevel(
                Integer.parseInt(
                        reorderLevelField
                                .getText()
                                .trim()
                )
        );

        product.setUnitPrice(
                Double.parseDouble(
                        unitPriceField
                                .getText()
                                .trim()
                )
        );

        product.setDescription(
                descriptionArea.getText()
        );

        return product;
    }

    private void loadReferenceData() {

        try {

            List<Category> categories =
                    categoryService
                            .getAllCategories();

            categoryComboBox
                    .getItems()
                    .setAll(categories);

            List<Supplier> suppliers =
                    supplierService
                            .getAllSuppliers();

            supplierComboBox
                    .getItems()
                    .setAll(suppliers);

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void loadProducts() {

        try {

            List<Product> products =
                    productService
                            .getAllProducts();

            productTable
                    .getItems()
                    .setAll(products);

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void populateForm(
            Product product
    ) {

        selectedProduct = product;

        if (product == null) {
            return;
        }

        skuField.setText(
                product.getSku()
        );

        productNameField.setText(
                product.getProductName()
        );

        quantityField.setText(
                String.valueOf(
                        product.getQuantity()
                )
        );

        reorderLevelField.setText(
                String.valueOf(
                        product.getReorderLevel()
                )
        );

        unitPriceField.setText(
                String.valueOf(
                        product.getUnitPrice()
                )
        );

        descriptionArea.setText(
                product.getDescription()
        );

        productTypeComboBox.setValue(
                product.getProductType()
        );

        selectCategory(
                product.getCategoryId()
        );

        selectSupplier(
                product.getSupplierId()
        );

        expiryDatePicker.setValue(null);
        warrantyMonthsField.clear();

        if (product
                instanceof PerishableProduct) {

            PerishableProduct perishableProduct =
                    (PerishableProduct) product;

            expiryDatePicker.setValue(
                    perishableProduct
                            .getExpiryDate()
            );
        }

        if (product
                instanceof ElectronicProduct) {

            ElectronicProduct electronicProduct =
                    (ElectronicProduct) product;

            warrantyMonthsField.setText(
                    String.valueOf(
                            electronicProduct
                                    .getWarrantyMonths()
                    )
            );
        }

        updateTypeFields();
    }

    private void selectCategory(
            int categoryId
    ) {

        for (Category category
                : categoryComboBox.getItems()) {

            if (category.getId()
                    == categoryId) {

                categoryComboBox.setValue(
                        category
                );

                return;
            }
        }

        categoryComboBox.setValue(null);
    }

    private void selectSupplier(
            int supplierId
    ) {

        for (Supplier supplier
                : supplierComboBox.getItems()) {

            if (supplier.getId()
                    == supplierId) {

                supplierComboBox.setValue(
                        supplier
                );

                return;
            }
        }

        supplierComboBox.setValue(null);
    }

    private void updateTypeFields() {

        String productType =
                productTypeComboBox.getValue();

        boolean perishable =
                "PERISHABLE".equals(
                        productType
                );

        boolean electronic =
                "ELECTRONIC".equals(
                        productType
                );

        expiryDatePicker.setDisable(
                !perishable
        );

        warrantyMonthsField.setDisable(
                !electronic
        );
    }

    private void clearForm() {

        selectedProduct = null;

        skuField.clear();
        productNameField.clear();
        categoryComboBox.setValue(null);
        supplierComboBox.setValue(null);
        quantityField.clear();
        reorderLevelField.clear();
        unitPriceField.clear();
        descriptionArea.clear();
        expiryDatePicker.setValue(null);
        warrantyMonthsField.clear();

        productTypeComboBox.setValue(
                "GENERAL"
        );

        productTable
                .getSelectionModel()
                .clearSelection();

        updateTypeFields();
    }
}