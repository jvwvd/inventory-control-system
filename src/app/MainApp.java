package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import util.SceneManager;

public class MainApp extends Application {

    @Override
    public void start(
            Stage primaryStage
    ) throws IOException {

        SceneManager.setPrimaryStage(
                primaryStage
        );

        SceneManager.switchScene(
                "login.fxml",
                "Inventory Management System - Login"
        );

        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}