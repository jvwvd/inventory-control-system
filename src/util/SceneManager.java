package util;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {
        // Prevent object creation.
    }

    public static void setPrimaryStage(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException(
                    "Stage cannot be null."
            );
        }

        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(
            String fxmlFile,
            String windowTitle
    ) throws IOException {

        if (primaryStage == null) {
            throw new IllegalStateException(
                    "Primary stage has not been set."
            );
        }

        URL fxmlLocation =
                SceneManager.class.getResource(
                        "/fxml/" + fxmlFile
                );

        if (fxmlLocation == null) {
            throw new IOException(
                    "FXML file not found: " + fxmlFile
            );
        }

        Parent root =
                FXMLLoader.load(fxmlLocation);

        Scene scene =
                new Scene(root);

        primaryStage.setTitle(windowTitle);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}