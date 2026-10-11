package controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import model.InventoryUser;
import util.SceneManager;
import util.SessionManager;

public class MainLayoutController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label userNameLabel;

    @FXML
    private void initialize() {
        userNameLabel.setText(
                getLoggedInUserName()
        );

        loadDashboard();
    }

    @FXML
    private void handleDashboard() {
        loadDashboard();
    }

    @FXML
    private void handleCategories() {
        loadCenterView(
                "category_management.fxml"
        );
    }

    @FXML
    private void handleSuppliers() {
        loadCenterView(
                "supplier_management.fxml"
        );
    }

    @FXML
    private void handleProducts() {
        loadCenterView(
                "product_management.fxml"
        );
    }

    @FXML
    private void handleStockManagement() {
        loadCenterView(
                "stock_management.fxml"
        );
    }

    @FXML
    private void handleMovementHistory() {
        loadCenterView(
                "movement_history.fxml"
        );
    }

    @FXML
    private void handleInventoryReport() {
        loadCenterView(
                "inventory_report.fxml"
        );
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();

        try {
            SceneManager.switchScene(
                    "login.fxml",
                    "Inventory Management System - Login"
            );
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void loadDashboard() {
        loadCenterView(
                "dashboard.fxml"
        );
    }

    private void loadCenterView(String fxmlFile) {
        try {
            URL location =
                    getClass().getResource(
                            "/fxml/" + fxmlFile
                    );

            if (location == null) {
                throw new IOException(
                        "FXML file not found: "
                                + fxmlFile
                );
            }

            Parent view =
                    FXMLLoader.load(location);

            mainBorderPane.setCenter(
                    view
            );

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String getLoggedInUserName() {
        InventoryUser user =
                SessionManager.getCurrentUser();

        if (user == null) {
            return "User";
        }

        if (user.getFullName() != null
                && !user.getFullName().isBlank()) {

            return user.getFullName();
        }

        return user.getUsername();
    }
}