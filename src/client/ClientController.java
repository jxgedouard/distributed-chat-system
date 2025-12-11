package client;

import javafx.application.Application;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PONT entre l'UI JavaFX et la logique réseau Socket.
 * Gère la connexion au serveur et la synchronisation UI ↔ Réseau.
 */
public class ClientController {

    private static final String HOST = "localhost";
    private static final int PORT = 3000;

    private ChatClientUI ui;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private Thread listenerThread;

    public ClientController(ChatClientUI ui) {
        this.ui = ui;
        setupUICallbacks();
    }

    /**
     * Configure les callbacks de l'UI pour réagir aux actions de l'utilisateur
     */
    private void setupUICallbacks() {
        // Quand l'utilisateur clique sur "Se connecter"
        ui.setOnLoginRequest(pseudo -> {
            this.username = pseudo;
            ui.setUsername(pseudo); // Afficher le username dans le titre
            connectToServer();
        });

        // Quand l'utilisateur envoie un message
        ui.setOnMessageSent(message -> {
            sendToServer(message);
        });
    }

    /**
     * Connexion au serveur TCP
     */
    private void connectToServer() {
        try {
            socket = new Socket(HOST, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Envoyer le pseudo au serveur
            output.println(username);

            // Démarrer le thread d'écoute des messages du serveur
            startListeningToServer();

            // Démarrer la mise à jour automatique de la liste des users
            startAutoRefreshUserList();

            System.out.println("Connecté au serveur " + HOST + ":" + PORT);

        } catch (IOException e) {
            Platform.runLater(() -> {
                ui.appendMessage("[ERREUR] Impossible de se connecter au serveur : " + e.getMessage());
            });
            e.printStackTrace();
        }
    }

    /**
     * Thread qui écoute en continu les messages du serveur
     */
    private void startListeningToServer() {
        listenerThread = new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = input.readLine()) != null) {
                    final String msg = serverMessage;

                    // Parser les commandes spéciales du serveur
                    if (msg.startsWith("[USERLIST]")) {
                        // Format: [USERLIST]Alice,Bob,Charlie
                        String usersStr = msg.substring(10); // Enlève "[USERLIST]"
                        List<String> users = Arrays.asList(usersStr.split(","));
                        Platform.runLater(() -> ui.updateUserList(users));
                    }
                    else if (msg.startsWith("[CHANNEL]")) {
                        // Format: [CHANNEL]#general
                        String channel = msg.substring(9); // Enlève "[CHANNEL]"
                        Platform.runLater(() -> ui.updateChannelName(channel));
                    }
                    else {
                        // Message normal : afficher dans le chat
                        Platform.runLater(() -> ui.appendMessage(msg));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    ui.appendMessage("[ERREUR] Connexion perdue avec le serveur.");
                });
                System.out.println("Connexion fermée.");
            }
        });
        listenerThread.setDaemon(true); // Thread daemon pour qu'il se ferme avec l'app
        listenerThread.start();
    }

    /**
     * Thread qui demande régulièrement la liste des users au serveur
     */
    private void startAutoRefreshUserList() {
        Thread refreshThread = new Thread(() -> {
            try {
                while (!Thread.interrupted() && socket != null && !socket.isClosed()) {
                    Thread.sleep(3000); // Toutes les 3 secondes
                    if (output != null) {
                        output.println("/users");
                    }
                }
            } catch (InterruptedException e) {
                // Thread interrompu, on arrête
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    /**
     * Envoyer un message au serveur
     */
    private void sendToServer(String message) {
        if (output != null) {
            // Gestion spéciale de "bye" pour déconnexion propre
            if (message.equalsIgnoreCase("bye")) {
                disconnect();
                Platform.runLater(() -> {
                    ui.appendMessage("[SYSTÈME] Déconnexion...");
                });
                Platform.exit();
            } else {
                output.println(message);
            }
        }
    }

    /**
     * Fermer proprement la connexion
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée principal : Lance l'UI JavaFX avec le controller
     */
    public static void main(String[] args) {
        // Lancer l'application JavaFX
        Application.launch(ChatClientUI.class, args);
    }
}
