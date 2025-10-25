package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 3000;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connecté au serveur.");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Demande du pseudo
            System.out.print("Choisissez un pseudo : ");
            String pseudo = scanner.nextLine();
            output.println(pseudo); // envoie le pseudo au serveur

            System.out.println("Bienvenue " + pseudo + " ! Vous pouvez maintenant discuter :");

            // Thread qui écoute les messages du serveur
            Thread readThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = input.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Connexion fermée.");
                }
            });
            readThread.start();

            // Envoi des messages
            String msg;
            while (!(msg = scanner.nextLine()).equalsIgnoreCase("bye")) {
                output.println(msg);
            }
            
            scanner.close();
            socket.close();
            System.out.println("Déconnexion du serveur.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
