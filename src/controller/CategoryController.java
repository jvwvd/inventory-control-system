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
import model.Category;
import service.CategoryService;
import util.DialogUtil;

public class CategoryController {

    private final CategoryService categoryService =
            new CategoryService();

    private Category selectedCategory;

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> categoryNameColumn;

    @FXML
    private TableColumn<Category, String> descriptionColumn;

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField searchField;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        categoryNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("categoryName")
        );

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        categoryTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldCategory, newCategory) ->
                                populateForm(newCategory)
                );

        loadCategories();
    }

    @FXML
    private void handleAdd() {
        Category category = new Category();

        category.setCategoryName(
                categoryNameField.getText()
        );

        category.setDescription(
                descriptionArea.getText()
        );

        try {
            categoryService.addCategory(category);

            DialogUtil.showInformation(
                    "Category Added",
                    "Category added successfully."
            );

            clearForm();
            loadCategories();

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
        if (selectedCategory == null) {
            DialogUtil.showWarning(
                    "No Category Selected",
                    "Please select a category to update."
            );

            return;
        }

        selectedCategory.setCategoryName(
                categoryNameField.getText()
        );

        selectedCategory.setDescription(
                descriptionArea.getText()
        );

        try {
            categoryService.updateCategory(
                    selectedCategory
            );

            DialogUtil.showInformation(
                    "Category Updated",
                    "Category updated successfully."
            );

            clearForm();
            loadCategories();

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
        if (selectedCategory == null) {
            DialogUtil.showWarning(
                    "No Category Selected",
                    "Please select a category to delete."
            );

            return;
        }

        try {
            categoryService.deleteCategory(
                    selectedCategory.getId()
            );

            DialogUtil.showInformation(
                    "Category Deleted",
                    "Category deleted successfully."
            );

            clearForm();
            loadCategories();

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
        String keyword = searchField.getText();

        if (keyword == null || keyword.isBlank()) {
            loadCategories();
            return;
        }

        try {
            List<Category> categories =
                    categoryService.searchCategories(
                            keyword.trim()
                    );

            categoryTable.getItems().setAll(
                    categories
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
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Category> categories =
                    categoryService.getAllCategories();

            categoryTable.getItems().setAll(
                    categories
            );

        } catch (DatabaseException exception) {
            DialogUtil.showError(
                    "Database Error",
                    exception.getMessage()
            );
        }
    }

    private void populateForm(Category category) {
        selectedCategory = category;

        if (category == null) {
            return;
        }

        categoryNameField.setText(
                category.getCategoryName()
        );

        descriptionArea.setText(
                category.getDescription()
        );
    }

    private void clearForm() {
        selectedCategory = null;

        categoryNameField.clear();
        descriptionArea.clear();

        categoryTable.getSelectionModel()
                .clearSelection();
    }
}