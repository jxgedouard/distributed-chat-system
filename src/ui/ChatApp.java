package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ChatApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Bienvenue dans le Group Chat JavaFX !");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Java Group Chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
