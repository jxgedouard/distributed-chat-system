package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ClientUI extends Application {

    // Composants de l’interface
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;
    private Label usernameLabel;

    @Override
    public void start(Stage primaryStage) {
        // ----- 1️⃣ Layout principal -----
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // ----- 2️⃣ Zone de chat (centre) -----
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPromptText("Messages du chat...");
        root.setCenter(chatArea);

        // ----- 3️⃣ Zone d’envoi (bas) -----
        HBox inputBox = new HBox(10);
        messageField = new TextField();
        messageField.setPromptText("Entrez votre message...");
        sendButton = new Button("Envoyer");

        inputBox.getChildren().addAll(messageField, sendButton);
        inputBox.setPadding(new Insets(10));
        root.setBottom(inputBox);

        // ----- 4️⃣ Barre du haut (pseudo) -----
        usernameLabel = new Label("Utilisateur : Edouard");
        HBox topBar = new HBox(usernameLabel);
        topBar.setPadding(new Insets(5, 10, 5, 10));
        root.setTop(topBar);

        // ----- 5️⃣ Création de la scène -----
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("styles/main.css");

        // ----- 6️⃣ Stage (fenêtre principale) -----
        primaryStage.setTitle("Client - Group Chat");
        primaryStage.setScene(scene);
        primaryStage.show();

        // ----- 7️⃣ Event handler -----
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage()); // Appuyer sur "Entrée"
    }

    // ----- 8️⃣ Méthode d'envoi de message -----
    private void sendMessage() {
        String msg = messageField.getText();
        if (!msg.isEmpty()) {
            chatArea.appendText("Moi : " + msg + "\n");
            messageField.clear();

            // 🔜 plus tard : envoyer au serveur ici
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
