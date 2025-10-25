package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static volatile boolean running = true; 

    public static void main(String[] args) {
        int port = 3000;
        System.out.println("Serveur lancé sur le port " + port);

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

    // Classe interne pour gérer chaque client
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Réception du pseudo
                this.username = input.readLine();
                System.out.println("Utilisateur connecté : " + username);
                broadcast("✅ " + username + " a rejoint le chat.");

                // Boucle de messages
                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(username + " => " + message);
                    broadcast(username + " => " + message);
                }

            } catch (IOException e) {
                System.out.println("Connexion perdue avec " + username);
            } finally {
                try {
                    clients.remove(this);
                    socket.close();
                    System.out.println("❌ " + username + " s'est déconnecté.");
                    broadcast("❌ " + username + " s'est déconnecté.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
