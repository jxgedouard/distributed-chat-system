package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import channels.ChatRoom;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import messages.Message;
import messages.BroadcastMessage;
import messages.PrivateMessage;
import messages.SystemMessage;

public class Server {
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static volatile boolean running = true;

    private static final LinkedList<Message> messageHistory = new LinkedList<>();
    private static final HashMap<String, ChatRoom> channels = new HashMap<>();
    private static final int MAX_HISTORY_SIZE = 20; 

    public static void main(String[] args) {
        int port = 3000;
        System.out.println("Serveur lancé sur le port " + port);

        channels.put("#general", new ChatRoom("#general"));
        channels.put("#random", new ChatRoom("#random"));
        channels.put("#java", new ChatRoom("#java"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            // Lance le thread console une seule fois
            startConsoleThread(serverSocket);

            // Boucle principale d’attente des clients
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);

                    new Thread(handler).start();

                } catch (SocketException e) {
                    // Exception levée quand serverSocket.close() est appelé.
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Serveur arrêté proprement.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Envoi d'un message à tous les clients connectés
    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);
        }
    }

    // Thread console : permet au serveur d'envoyer des messages ou de s'arrêter
    public static void startConsoleThread(ServerSocket serverSocket) {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                String serverMsg;
                while (running && !(serverMsg = scanner.nextLine()).equalsIgnoreCase("stop")) {
                    broadcast("Serveur => " + serverMsg);
                    System.out.println("Serveur => " + serverMsg);
                }

                System.out.println("Arrêt du serveur demandé.");
                broadcast("⚠️ Le serveur s'arrête, déconnexion...");

                // Ferme toutes les connexions clients
                for (ClientHandler client : clients) {
                    try {
                        client.socket.close();
                    } catch (IOException ignored) {}
                }

                // Stoppe la boucle principale
                running = false;

                // Ferme le ServerSocket pour débloquer accept()
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static ClientHandler findClientByUsername(String username) {
        for (ClientHandler client : clients) {
            if (client.username.equals(username)) {
                return client;
            }
        }
        return null;
    }

    private static synchronized void addToHistory(Message message) {
        messageHistory.add(message); 
        if (messageHistory.size() > MAX_HISTORY_SIZE) {
            messageHistory.removeFirst();
        }
    }
    private static void sendHistoryTo(ClientHandler client) {
        for (Message msg : messageHistory) {
            client.output.println(msg.format());
        }
    }

    // Classe interne pour gérer chaque client
    public static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private String username;
        private ChatRoom currentChannel;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            output.println(message);
        }

        @Override
        public void run() {
            try {
                // Réception du pseudo
                this.username = input.readLine();

                // Envoyer l'historique des 20 derniers messages au nouveau client
                sendHistoryTo(this);

                // Acces channel
                this.currentChannel = channels.get("#general");
                currentChannel.addMember(this);

                SystemMessage connectionMsg = new SystemMessage("✅ " + username + " a rejoint le chat.");
                System.out.println(connectionMsg.format());
                broadcast(connectionMsg.format());
                addToHistory(connectionMsg);  

                // Boucle de messages
                String messageText;
                while ((messageText = input.readLine()) != null) {

                    Message msg;

                    if (messageText.startsWith("/")) {
                        if (messageText.startsWith("/join ")) {
                            String[] parts = messageText.split(" ", 2);
                            if (parts.length < 2) {
                                output.println("[ERREUR] Format : /join #channel");
                                continue;
                            }
                            String channelName = parts[1];
                            ChatRoom targetChannel = channels.get(channelName);

                            if (targetChannel == null) {
                                output.println("[ERREUR] Channel '" + channelName + "' inexistant");
                            }
                            else {
                                // Quitter l'ancier channel
                                if (currentChannel != null) {
                                    currentChannel.removeMember(this);
                                }
                                // Rejoindre le nouveau
                                currentChannel = targetChannel;
                                currentChannel.addMember(this);
                                output.println("✅ Vous avez rejoint " + channelName);                            }
                        }
                        else if (messageText.equals("/leave")) {
                            if (currentChannel != null) {
                                String oldChannelName = currentChannel.getName();
                                // Notifier les autres membres du channel avant de partir
                                SystemMessage leaveMsg = new SystemMessage("👋 " + username + " a quitté " + oldChannelName);
                                currentChannel.broadcast(leaveMsg.format());
                                // Retirer du channel
                                currentChannel.removeMember(this);
                                currentChannel = null;
                                // Notifier l'utilisateur
                                output.println("✅ Vous avez quitté " + oldChannelName);
                            } else {
                                output.println("[INFO] Vous n'êtes dans aucun channel");
                            }
                        }

                        else if (messageText.equals("/channels")) {
                            output.println("📋 Channels disponibles : #general, #random, #java");
                        }

                        else {
                            output.println("[ERREUR] Commande inconnue");
                        }
                        continue;
                    }

                    else if (messageText.startsWith("@")) {
                        // Message privé
                        String[] parts = messageText.split(" ", 2);

                        // Vérifier qu'il y a bien un destinataire ET un message
                        if (parts.length < 2) {
                            output.println("[ERREUR] Format : @username message");
                            continue;
                        }

                        String recipient = parts[0].substring(1);  // Enlève le @
                        String content = parts[1];

                        ClientHandler recipientClient = findClientByUsername(recipient);

                        if (recipientClient == null) {
                            output.println("[ERREUR] Utilisateur '" + recipient + "' introuvable.");
                        } else {
                            msg = new PrivateMessage(username, recipient, content);
                            System.out.println(msg.format());
                            this.output.println(msg.format());
                            recipientClient.output.println(msg.format());
                            addToHistory(msg);  // Sauvegarder dans l'historique
                        }

                    } else {
                        // Message public
                        if (currentChannel != null) {
                            msg = new BroadcastMessage(username, messageText);
                            System.out.println("[" + currentChannel.getName() + "] " + msg.format());
                            currentChannel.broadcast(msg.format());
                            addToHistory(msg);  // Sauvegarder dans l'historique
                        } else {
                            output.println("[INFO] Vous devez rejoindre un channel avec /join #channel");
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Connexion perdue avec " + username);
            } finally {
                try {
                    clients.remove(this);
                    socket.close();

                    SystemMessage disconnectionMsg = new SystemMessage("❌ " + username + " s'est déconnecté.");
                    System.out.println(disconnectionMsg.format());
                    broadcast(disconnectionMsg.format());
                    addToHistory(disconnectionMsg);  // Sauvegarder dans l'historique
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
