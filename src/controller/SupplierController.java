package controller;

import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Supplier;
import service.SupplierService;
import util.DialogUtil;

public class SupplierController {

    private final SupplierService supplierService =
            new SupplierService();

    private Supplier selectedSupplier;

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> idColumn;

    @FXML
    private TableColumn<Supplier, String> supplierNameColumn;

    @FXML
    private TableColumn<Supplier, String> contactPersonColumn;

    @FXML
    private TableColumn<Supplier, String> phoneColumn;

    @FXML
    private TableColumn<Supplier, String> emailColumn;

    @FXML
    private TableColumn<Supplier, String> addressColumn;

    @FXML
    private TextField supplierNameField;

    @FXML
    private TextField contactPersonField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea addressArea;

    @FXML
    private TextField searchField;

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        supplierNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("supplierName")
        );

        contactPersonColumn.setCellValueFactory(
                new PropertyValueFactory<>("contactPerson")
        );

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        addressColumn.setCellValueFactory(
                new PropertyValueFactory<>("address")
        );

        supplierTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldSupplier, newSupplier) ->
                                populateForm(newSupplier)
                );

        loadSuppliers();
    }

    @FXML
    private void handleAdd() {

        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                supplierNameField.getText()
        );

        supplier.setContactPerson(
                contactPersonField.getText()
        );

        supplier.setPhone(
                phoneField.getText()
        );

        supplier.setEmail(
                emailField.getText()
        );

        supplier.setAddress(
                addressArea.getText()
        );

        try {

            supplierService.addSupplier(
                    supplier
            );

            DialogUtil.showInformation(
                    "Supplier Added",
                    "Supplier added successfully."
            );

            clearForm();
            loadSuppliers();

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

        if (selectedSupplier == null) {

            DialogUtil.showWarning(
                    "No Supplier Selected",
                    "Please select a supplier to update."
            );

            return;
        }

        selectedSupplier.setSupplierName(
                supplierNameField.getText()
        );

        selectedSupplier.setContactPerson(
                contactPersonField.getText()
        );

        selectedSupplier.setPhone(
                phoneField.getText()
        );

        selectedSupplier.setEmail(
                emailField.getText()
        );

        selectedSupplier.setAddress(
                addressArea.getText()
        );

        try {

            supplierService.updateSupplier(
                    selectedSupplier
            );

            DialogUtil.showInformation(
                    "Supplier Updated",
                    "Supplier updated successfully."
            );

            clearForm();
            loadSuppliers();

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

        if (selectedSupplier == null) {

            DialogUtil.showWarning(
                    "No Supplier Selected",
                    "Please select a supplier to delete."
            );

            return;
        }

        try {

            supplierService.deleteSupplier(
                    selectedSupplier.getId()
            );

            DialogUtil.showInformation(
                    "Supplier Deleted",
                    "Supplier deleted successfully."
            );

            clearForm();
            loadSuppliers();

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

            loadSuppliers();
            return;
        }

        try {

            List<Supplier> suppliers =
                    supplierService.searchSuppliers(
                            keyword.trim()
                    );

            supplierTable.getItems().setAll(
                    suppliers
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

        searchField.clear();

        loadSuppliers();
    }

    private void loadSuppliers() {

        try {

            List<Supplier> suppliers =
                    supplierService.getAllSuppliers();

            supplierTable.getItems().setAll(
                    suppliers
            );

        } catch (DatabaseException exception) {

            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void populateForm(
            Supplier supplier
    ) {

        selectedSupplier = supplier;

        if (supplier == null) {
            return;
        }

        supplierNameField.setText(
                supplier.getSupplierName()
        );

        contactPersonField.setText(
                supplier.getContactPerson()
        );

        phoneField.setText(
                supplier.getPhone()
        );

        emailField.setText(
                supplier.getEmail()
        );

        addressArea.setText(
                supplier.getAddress()
        );
    }

    private void clearForm() {

        selectedSupplier = null;

        supplierNameField.clear();
        contactPersonField.clear();
        phoneField.clear();
        emailField.clear();
        addressArea.clear();

        supplierTable.getSelectionModel()
                .clearSelection();
    }
}