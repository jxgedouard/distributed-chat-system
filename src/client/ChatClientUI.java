package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ChatClientUI extends Application {

    // --- COULEURS STYLE APPLE / MACOS DARK ---
    private static final String COLOR_BG_DARK = "#1E1E1E";      // Fond principal (très sombre)
    private static final String COLOR_BG_LIGHT = "#1E1E1E";     // Zone de chat (unifié avec le fond)
    private static final String COLOR_SIDEBAR = "#252526";      // Sidebar subtile
    private static final String COLOR_BUBBLE_SELF = "#0A84FF";  // iMessage Blue
    private static final String COLOR_BUBBLE_OTHER = "#3A3A3C"; // Gris foncé bulles
    private static final String COLOR_TEXT_WHITE = "#F2F2F7";   // Blanc cassé (moins agressif)
    private static final String COLOR_TEXT_GRAY = "#8E8E93";    // Gris Apple
    private static final String COLOR_ACCENT = "#0A84FF";       // Bleu accent
    private static final String COLOR_ACCENT_HOVER = "#0071E3";
    private static final String FONT_FAMILY = "System";         // Police système par défaut

    // --- COMPOSANTS UI (Noms préservés) ---
    private Stage primaryStage;
    private VBox messageContainer; 
    private ScrollPane chatScrollPane;
    private TextField inputField;
    private ListView<String> userList;
    private Label channelLabel;
    
    // --- CALLBACKS & CONTROLLERS (Noms préservés) ---
    private Consumer<String> onMessageSentHandler;
    private Consumer<String> onLoginHandler;
    private String tempUsername;
    private ClientController controller;

    // Constructeur vide requis par JavaFX ou instanciation externe
    public ChatClientUI() {
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Messages"); // Titre plus sobre style app native

        // Initialisation controller
        controller = new ClientController(this);

        showLoginScreen();
        stage.show();
    }

    public void setUsername(String username) {
        this.tempUsername = username;
        Platform.runLater(() -> {
            primaryStage.setTitle("Messages - " + username);
        });
    }

    /**
     * ECRAN 1 : CONNEXION (Style Minimaliste Centré)
     */
    private void showLoginScreen() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";");
        root.setPadding(new Insets(60));

        // Icone ou Cercle décoratif
        Circle logoPlaceholder = new Circle(30, Color.web(COLOR_ACCENT));
        
        Label title = new Label("Bienvenue");
        title.setStyle("-fx-text-fill: " + COLOR_TEXT_WHITE + "; -fx-font-size: 28px; -fx-font-family: '" + FONT_FAMILY + "'; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Votre pseudo");
        usernameField.setMaxWidth(280);
        // Style "Pill" input
        usernameField.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 12px 20px; -fx-font-size: 14px;");

        Button connectBtn = createStyledButton("Entrer", COLOR_ACCENT);
        connectBtn.setMaxWidth(280);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#FF453A")); // Apple Red

        Runnable doLogin = () -> {
            String pseudo = usernameField.getText().trim();
            if (!pseudo.isEmpty()) {
                this.tempUsername = pseudo;
                showMainChatScreen();
                if (onLoginHandler != null) {
                    onLoginHandler.accept(pseudo);
                }
            } else {
                errorLabel.setText("Merci de choisir un pseudo.");
            }
        };

        connectBtn.setOnAction(e -> doLogin.run());
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) doLogin.run();
        });

        root.getChildren().addAll(logoPlaceholder, title, usernameField, connectBtn, errorLabel);
        Scene scene = new Scene(root, 400, 500); // Format un peu plus vertical style mobile/moderne
        primaryStage.setScene(scene);
    }

    /**
     * ECRAN 2 : CHAT PRINCIPAL (Layout BorderPane préservé)
     */
    private void showMainChatScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";");

        // --- TOP (Header Épuré) ---
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        // Bordure subtile en bas seulement
        topBar.setStyle("-fx-background-color: " + COLOR_BG_DARK + "; -fx-border-color: #333333; -fx-border-width: 0 0 1 0;");

        channelLabel = new Label("# général");
        channelLabel.setStyle("-fx-text-fill: " + COLOR_TEXT_WHITE + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnQuit = new Button("Quitter");
        // Bouton style "Ghost" (juste du texte rouge)
        btnQuit.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF453A; -fx-font-size: 14px; -fx-cursor: hand;");
        btnQuit.setOnAction(e -> {
             sendInternalCommand("/leave");
             Platform.exit();
        });

        topBar.getChildren().addAll(channelLabel, spacer, btnQuit);
        root.setTop(topBar);

        // --- CENTER (Zone de messages) ---
        messageContainer = new VBox(15); // Plus d'espace entre les messages
        messageContainer.setPadding(new Insets(20));
        messageContainer.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");
        messageContainer.setFillWidth(true); 

        chatScrollPane = new ScrollPane(messageContainer);
        chatScrollPane.setFitToWidth(true);
        // Style scrollbar moderne/invisible
        chatScrollPane.setStyle("-fx-background: " + COLOR_BG_LIGHT + "; -fx-background-color: " + COLOR_BG_LIGHT + ";");
        chatScrollPane.getStyleClass().add("edge-to-edge");
        
        // Auto-scroll
        messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });

        root.setCenter(chatScrollPane);

        // --- BOTTOM (Input Moderne) ---
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(20));
        bottomBar.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";"); // Unifié
        bottomBar.setAlignment(Pos.CENTER);

        inputField = new TextField();
        inputField.setPromptText("Message...");
        // Style "Pill" input
        inputField.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 25px; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        inputField.setPrefHeight(45);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Bouton Rond avec flèche ou texte simple
        Button sendBtn = createStyledButton("↑", COLOR_ACCENT);
        sendBtn.setPrefSize(45, 45); // Rond
        sendBtn.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-background-radius: 50%; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");

        Runnable sendAction = () -> {
            String msg = inputField.getText();
            if (!msg.isEmpty()) {
                sendInternalCommand(msg);
                inputField.clear();
                inputField.requestFocus();
            }
        };

        sendBtn.setOnAction(e -> sendAction.run());
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendAction.run();
        });

        bottomBar.getChildren().addAll(inputField, sendBtn);
        root.setBottom(bottomBar);

        // --- RIGHT (Sidebar minimaliste) ---
        VBox rightPane = new VBox(15);
        rightPane.setPrefWidth(220);
        rightPane.setPadding(new Insets(20));
        // Bordure gauche subtile
        rightPane.setStyle("-fx-background-color: " + COLOR_SIDEBAR + "; -fx-border-color: #333333; -fx-border-width: 0 0 0 1;");

        Label usersTitle = new Label("EN LIGNE");
        usersTitle.setStyle("-fx-text-fill: " + COLOR_TEXT_GRAY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 5 10;");

        userList = new ListView<>();
        userList.setStyle("-fx-background-color: transparent; -fx-control-inner-background: " + COLOR_SIDEBAR + ";");
        userList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Item user avec petit point vert
                    HBox userRow = new HBox(10);
                    userRow.setAlignment(Pos.CENTER_LEFT);
                    Circle statusDot = new Circle(4, Color.web("#30D158")); // Apple Green
                    Label nameLbl = new Label(item);
                    nameLbl.setTextFill(Color.web(COLOR_TEXT_WHITE));
                    nameLbl.setFont(Font.font(FONT_FAMILY, 13));
                    userRow.getChildren().addAll(statusDot, nameLbl);
                    
                    setGraphic(userRow);
                    setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
                }
            }
        });

        rightPane.getChildren().addAll(usersTitle, userList);
        root.setRight(rightPane);

        Scene mainScene = new Scene(root, 950, 650);
        // CSS Data URI pour cacher la scrollbar native moche et faire une version fine
        String scrollbarCss = "data:text/css," +
                ".scroll-pane > .viewport { -fx-background-color: transparent; }" +
                ".scroll-bar:vertical { -fx-background-color: transparent; -fx-pref-width: 10; }" +
                ".scroll-bar:vertical .thumb { -fx-background-color: #555; -fx-background-radius: 5; }";
        mainScene.getStylesheets().add(scrollbarCss);
        
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    // --- LOGIQUE INTERNE ---

    private void sendInternalCommand(String message) {
        if (onMessageSentHandler != null) {
            onMessageSentHandler.accept(message);
        }
    }

    // Méthode utilitaire pour bouton "Pill" large
    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(text);
        String baseStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-padding: 10px 20px;";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + COLOR_ACCENT_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-cursor: hand; -fx-padding: 10px 20px;"));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    // --- API PUBLIQUE (INTACTE) ---

    public void setOnLoginRequest(Consumer<String> handler) {
        this.onLoginHandler = handler;
    }

    public void setOnMessageSent(Consumer<String> handler) {
        this.onMessageSentHandler = handler;
    }

    public void updateChannelName(String channelName) {
        Platform.runLater(() -> {
            if (channelLabel != null) {
                channelLabel.setText(channelName);
                if (inputField != null) inputField.setPromptText("Message dans " + channelName);
            }
        });
    }

    public void updateUserList(List<String> users) {
        Platform.runLater(() -> {
            if (userList != null) {
                userList.getItems().clear();
                userList.getItems().addAll(users);
            }
        });
    }

    /**
     * Ajoute un message à la zone de chat.
     * MODIFICATION : Utilisation de "bulles" visuelles au lieu de texte brut.
     */
    public void appendMessage(String rawMessage) {
        Platform.runLater(() -> {
            if (messageContainer == null) return;

            // Conteneur global du message (permet de gérer l'alignement si besoin plus tard)
            VBox bubbleContainer = new VBox(2);
            
            // TextFlow qui sera la "bulle" colorée
            TextFlow messageBubble = new TextFlow();
            messageBubble.setPadding(new Insets(10, 15, 10, 15));
            messageBubble.setMaxWidth(600); // Empêche les bulles trop larges

            Text timeText = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " ");
            timeText.setFill(Color.web(COLOR_TEXT_GRAY));
            timeText.setFont(Font.font("Monospaced", 10));

            Text contentText = new Text();
            contentText.setFont(Font.font(FONT_FAMILY, 14));
            contentText.setFill(Color.web(COLOR_TEXT_WHITE));

            // --- LOGIQUE DE PARSING (INTACTE) ---
            
            if (rawMessage.startsWith("[MP") || rawMessage.contains("chuchote")) {
                // MP -> Bulle Violette/Indigo
                contentText.setText(rawMessage);
                messageBubble.getChildren().addAll(contentText); // Pas de timestamp dans la bulle MP pour le style
                
                messageBubble.setStyle("-fx-background-color: #5856D6; -fx-background-radius: 18px;"); // Apple Indigo
                bubbleContainer.getChildren().addAll(timeText, messageBubble); // Time au dessus ou à coté
                
            } 
            else if (rawMessage.startsWith("Server:") || rawMessage.startsWith("System:") || rawMessage.startsWith("Bienvenue") || rawMessage.contains("a rejoint") || rawMessage.contains("a quitté")) {
                // Message système -> Centré, gris, pas de bulle
                Text sysText = new Text(rawMessage);
                sysText.setFill(Color.web(COLOR_TEXT_GRAY));
                sysText.setFont(Font.font(FONT_FAMILY, 12));
                
                HBox systemBox = new HBox(sysText);
                systemBox.setAlignment(Pos.CENTER);
                systemBox.setPadding(new Insets(5, 0, 5, 0));
                
                messageContainer.getChildren().add(systemBox);
                return; // STOP ICI
            } 
            else {
                // Message Public Standard
                if (rawMessage.contains("=>")) {
                    String[] parts = rawMessage.split("=>", 2);
                    String username = parts[0].trim();
                    String msgContent = parts[1].trim();
                    
                    // Nom de l'utilisateur au dessus de la bulle
                    Label userLabel = new Label(username);
                    userLabel.setTextFill(Color.web(COLOR_TEXT_GRAY));
                    userLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 11));
                    userLabel.setPadding(new Insets(0, 0, 0, 5));

                    contentText.setText(msgContent);
                    messageBubble.getChildren().add(contentText);
                    
                    // Style Bulle gris foncé
                    messageBubble.setStyle("-fx-background-color: " + COLOR_BUBBLE_OTHER + "; -fx-background-radius: 0 18px 18px 18px;");
                    
                    bubbleContainer.getChildren().addAll(userLabel, messageBubble);
                } else {
                    // Fallback (message simple sans format)
                    contentText.setText(rawMessage);
                    messageBubble.getChildren().add(contentText);
                    messageBubble.setStyle("-fx-background-color: " + COLOR_BUBBLE_OTHER + "; -fx-background-radius: 18px;");
                    bubbleContainer.getChildren().add(messageBubble);
                }
            }

            // Ajout d'un wrapper pour le timestamp à côté si voulu, ici on fait simple
            HBox row = new HBox(8);
            row.getChildren().add(bubbleContainer);
            messageContainer.getChildren().add(row);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}