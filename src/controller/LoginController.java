package controller;

import java.io.IOException;

import exception.AuthenticationException;
import exception.DatabaseException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.InventoryUser;
import service.AuthenticationService;
import util.SceneManager;
import util.SessionManager;

public class LoginController {

    private final AuthenticationService authenticationService =
            new AuthenticationService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private void handleLogin() {

        loginErrorLabel.setText("");

        try {
            InventoryUser loggedInUser =
                    authenticationService.login(
                            usernameField.getText(),
                            passwordField.getText()
                    );

            SessionManager.setCurrentUser(
                    loggedInUser
            );

            SceneManager.switchScene(
                    "main_layout.fxml",
                    "Inventory Management System"
            );

        } catch (AuthenticationException exception) {

            loginErrorLabel.setText(
                    exception.getMessage()
            );

        } catch (DatabaseException exception) {

            loginErrorLabel.setText(
                    "Database error: "
                            + exception.getMessage()
            );

        } catch (IOException exception) {

            exception.printStackTrace();

            loginErrorLabel.setText(
                    "Unable to open the main screen."
            );
        }
    }
}