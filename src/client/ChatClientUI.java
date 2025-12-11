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

    // --- COULEURS STYLE DISCORD ---
    private static final String COLOR_BG_DARK = "#2C2F33";
    private static final String COLOR_BG_LIGHT = "#36393F";
    private static final String COLOR_SIDEBAR = "#23272A";
    private static final String COLOR_TEXT_WHITE = "#FFFFFF";
    private static final String COLOR_TEXT_GRAY = "#99AAB5";
    private static final String COLOR_ACCENT = "#7289DA"; // Blurple
    private static final String COLOR_ACCENT_HOVER = "#677BC4";
    private static final String FONT_FAMILY = "Segoe UI";

    // --- COMPOSANTS UI ---
    private Stage primaryStage;
    private VBox messageContainer; // Contient les TextFlows des messages
    private ScrollPane chatScrollPane;
    private TextField inputField;
    private ListView<String> userList;
    private Label channelLabel;
    private Label connectionStatusLabel;

    // --- CALLBACKS ---
    private Consumer<String> onMessageSentHandler;
    private Consumer<String> onLoginHandler; // Pour notifier le Main qu'on a le pseudo

    // Variable temporaire pour stocker le username avant connexion réelle
    private String tempUsername;

    // Controller pour gérer la logique réseau
    private ClientController controller;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("JavaGroupChat - Discord Style");

        // Créer le controller qui gère la connexion réseau
        controller = new ClientController(this);

        // Démarrage sur l'écran de connexion
        showLoginScreen();
        stage.show();
    }

    /**
     * ECRAN 1 : CONNEXION
     */
    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";");
        root.setPadding(new Insets(50));

        Label title = new Label("Bienvenue sur le Chat");
        title.setStyle("-fx-text-fill: " + COLOR_TEXT_WHITE + "; -fx-font-size: 24px; -fx-font-family: '" + FONT_FAMILY + "'; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Entrez votre pseudo...");
        usernameField.setMaxWidth(300);
        usernameField.setStyle(getTextFieldStyle());

        Button connectBtn = createStyledButton("Se connecter", COLOR_ACCENT);
        connectBtn.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Runnable doLogin = () -> {
            String pseudo = usernameField.getText().trim();
            if (!pseudo.isEmpty()) {
                this.tempUsername = pseudo;
                // Transition vers l'interface principale
                // Note : Dans un vrai cas, on attendrait la confirmation du Socket, 
                // mais ici on switch l'UI immédiatement pour l'exemple.
                showMainChatScreen();
                
                // Si un handler de login externe est défini (ex: pour initier le Socket)
                if (onLoginHandler != null) {
                    onLoginHandler.accept(pseudo);
                }
            } else {
                errorLabel.setText("Le pseudo ne peut pas être vide !");
            }
        };

        connectBtn.setOnAction(e -> doLogin.run());
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) doLogin.run();
        });

        root.getChildren().addAll(title, usernameField, connectBtn, errorLabel);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
    }

    /**
     * ECRAN 2 : CHAT PRINCIPAL
     */
    private void showMainChatScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";");

        // --- TOP (Header) ---
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + COLOR_SIDEBAR + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        channelLabel = new Label("# général");
        channelLabel.setStyle("-fx-text-fill: " + COLOR_TEXT_WHITE + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnChannels = createStyledButton("Channels", "#4F545C");
        btnChannels.setOnAction(e -> sendInternalCommand("/channels"));
        
        Button btnQuit = createStyledButton("Quitter", "#ED4245");
        btnQuit.setOnAction(e -> {
             sendInternalCommand("/leave");
             // Optionnel : fermer l'appli ou revenir au login
             Platform.exit();
        });

        topBar.getChildren().addAll(channelLabel, spacer, btnChannels, btnQuit);
        root.setTop(topBar);

        // --- CENTER (Zone de messages) ---
        messageContainer = new VBox(5); // Espacement vertical entre messages
        messageContainer.setPadding(new Insets(15));
        messageContainer.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");
        // Important pour que le TextFlow prenne toute la largeur
        messageContainer.setFillWidth(true); 

        chatScrollPane = new ScrollPane(messageContainer);
        chatScrollPane.setFitToWidth(true); // Force les messages à s'adapter à la largeur
        chatScrollPane.setStyle("-fx-background: " + COLOR_BG_LIGHT + "; -fx-background-color: " + COLOR_BG_LIGHT + ";");
        // Suppression des bordures par défaut du ScrollPane
        chatScrollPane.getStyleClass().add("edge-to-edge"); 
        
        // Auto-scroll logic
        messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });

        root.setCenter(chatScrollPane);

        // --- BOTTOM (Input) ---
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setStyle("-fx-background-color: " + COLOR_BG_DARK + ";");
        bottomBar.setAlignment(Pos.CENTER);

        inputField = new TextField();
        inputField.setPromptText("Envoyer un message à " + channelLabel.getText());
        inputField.setStyle(getTextFieldStyle());
        inputField.setPrefHeight(40);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendBtn = createStyledButton("Envoyer", COLOR_ACCENT);
        sendBtn.setPrefHeight(40);

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

        // --- RIGHT (Liste utilisateurs) ---
        VBox rightPane = new VBox(10);
        rightPane.setPrefWidth(200);
        rightPane.setPadding(new Insets(10));
        rightPane.setStyle("-fx-background-color: " + COLOR_SIDEBAR + ";");

        Label usersTitle = new Label("CONNECTÉS");
        usersTitle.setStyle("-fx-text-fill: " + COLOR_TEXT_GRAY + "; -fx-font-size: 12px; -fx-font-weight: bold;");

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
                    setText(item);
                    setTextFill(Color.web(COLOR_TEXT_WHITE));
                    setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
                }
            }
        });

        rightPane.getChildren().addAll(usersTitle, userList);
        root.setRight(rightPane);

        Scene mainScene = new Scene(root, 900, 600);
        // Hack CSS pour cacher la bordure du ScrollPane si nécessaire
        mainScene.getStylesheets().add("data:text/css,.scroll-pane > .viewport { -fx-background-color: transparent; }");
        
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    // --- LOGIQUE INTERNE ---

    private void sendInternalCommand(String message) {
        if (onMessageSentHandler != null) {
            onMessageSentHandler.accept(message);
        }
    }

    private String getTextFieldStyle() {
        return "-fx-background-color: #40444B; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10px;";
    }

    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + colorHex + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-cursor: hand;"));
        return btn;
    }

    // --- API PUBLIQUE (POUR LE CLIENT.JAVA) ---

    /**
     * Définit l'action à effectuer quand l'utilisateur clique sur "Se connecter"
     */
    public void setOnLoginRequest(Consumer<String> handler) {
        this.onLoginHandler = handler;
    }

    /**
     * Définit l'action à effectuer quand l'utilisateur envoie un message
     */
    public void setOnMessageSent(Consumer<String> handler) {
        this.onMessageSentHandler = handler;
    }

    public void updateChannelName(String channelName) {
        Platform.runLater(() -> {
            if (channelLabel != null) {
                channelLabel.setText("Channel : " + channelName);
                if (inputField != null) inputField.setPromptText("Envoyer un message à " + channelName);
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
     * Ajoute un message à la zone de chat avec un formatage intelligent.
     * Détecte automatiquement les MP, les messages système, etc.
     */
    public void appendMessage(String rawMessage) {
        Platform.runLater(() -> {
            if (messageContainer == null) return;

            TextFlow messageFlow = new TextFlow();
            messageFlow.setPadding(new Insets(2, 0, 2, 0));

            // Timestamp (local si non fourni, sinon on pourrait parser le message)
            Text timeText = new Text("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ");
            timeText.setFill(Color.web(COLOR_TEXT_GRAY));
            timeText.setFont(Font.font("Monospaced", 11));

            Text contentText = new Text();
            contentText.setFont(Font.font(FONT_FAMILY, 13));

            // Logique de couleur selon le type de message
            if (rawMessage.startsWith("[MP") || rawMessage.contains("chuchote")) {
                // MESSAGE PRIVÉ
                contentText.setText(rawMessage);
                contentText.setFill(Color.web("#FAA61A")); // Or/Jaune pour bien ressortir ou le bleu demandé #7289DA
                contentText.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 13));
                messageFlow.setStyle("-fx-background-color: rgba(114, 137, 218, 0.1); -fx-background-radius: 5px;");
            } 
            else if (rawMessage.startsWith("Server:") || rawMessage.startsWith("System:") || rawMessage.startsWith("Bienvenue") || rawMessage.contains("a rejoint") || rawMessage.contains("a quitté")) {
                // MESSAGE SYSTÈME
                contentText.setText(rawMessage);
                contentText.setFill(Color.web(COLOR_TEXT_GRAY));
                contentText.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 12));
            } 
            else {
                // MESSAGE PUBLIC STANDARD
                // Tentative de séparer "User => Message" pour mettre l'user en couleur
                if (rawMessage.contains("=>")) {
                    String[] parts = rawMessage.split("=>", 2);
                    Text userText = new Text(parts[0] + " ");
                    userText.setFill(Color.web(COLOR_ACCENT)); // Pseudo en bleu
                    userText.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 13));
                    
                    Text msgText = new Text("=> " + parts[1]);
                    msgText.setFill(Color.web(COLOR_TEXT_WHITE));
                    
                    messageFlow.getChildren().addAll(timeText, userText, msgText);
                    messageContainer.getChildren().add(messageFlow);
                    return; // Sortie anticipée car on a géré les enfants manuellement
                } else {
                    contentText.setText(rawMessage);
                    contentText.setFill(Color.web(COLOR_TEXT_WHITE));
                }
            }

            messageFlow.getChildren().addAll(timeText, contentText);
            messageContainer.getChildren().add(messageFlow);
            
            // Notification sonore simple (bip système)
            // java.awt.Toolkit.getDefaultToolkit().beep(); 
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}