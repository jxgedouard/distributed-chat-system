# 📚 DOCUMENTATION COMPLÈTE : Server.java

**Auteur** : Edouard
**Date** : 2025-11-21
**Objectif** : Comprendre en profondeur chaque partie du serveur multi-clients TCP

---

## 🎯 VUE D'ENSEMBLE

Le serveur JavaGroupChat est un **serveur TCP multi-clients** qui permet à plusieurs utilisateurs de communiquer en temps réel via un chat.

**Architecture globale** :
```
┌─────────────────────────────────────────┐
│         Serveur (Thread principal)       │
│                                          │
│  - Écoute sur le port 3000               │
│  - Accepte les connexions clients        │
│  - Crée un thread par client             │
└─────────────────┬────────────────────────┘
                  │
       ┌──────────┴──────────┬──────────────────┐
       │                     │                  │
       ▼                     ▼                  ▼
  Thread Client 1      Thread Client 2    Thread Client 3
  (Alice)              (Bob)              (Charlie)
```

---

## 📦 IMPORTS ET DÉPENDANCES

```java
import java.io.*;                               // BufferedReader, PrintWriter, IOException
import java.net.*;                              // ServerSocket, Socket, SocketException
import java.util.Scanner;                       // Lecture input console serveur
import java.util.concurrent.CopyOnWriteArrayList; // Liste thread-safe
```

### Pourquoi ces imports ?

- **`java.io.*`** : Pour lire/écrire des données (messages) via les sockets
- **`java.net.*`** : Pour la communication réseau (TCP)
- **`Scanner`** : Pour que le serveur puisse taper des messages
- **`CopyOnWriteArrayList`** : Liste spéciale qui gère le multi-threading

---

## 🔧 ATTRIBUTS DE LA CLASSE

### 1️⃣ La liste des clients connectés

```java
private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
```

**Explication** :
- **`private`** : Accessible uniquement dans cette classe
- **`static`** : Partagée par TOUS les clients (pas une liste par client)
- **`final`** : La référence ne peut pas changer (mais le contenu oui !)
- **`CopyOnWriteArrayList`** : Liste thread-safe (voir section dédiée ci-dessous)

**À quoi ça sert ?**
- Stocker tous les `ClientHandler` (= tous les clients connectés)
- Permettre le `broadcast()` : envoyer un message à tout le monde

---

### 📌 FOCUS : CopyOnWriteArrayList vs ArrayList

**Pourquoi pas un simple `ArrayList` ?**

Imagine cette situation problématique :
```
Thread A (broadcast) : Parcourt la liste pour envoyer un message
Thread B (serveur)   : Supprime un client qui vient de se déconnecter

💥 CRASH : ConcurrentModificationException
```

**Solution : `CopyOnWriteArrayList`**

| Opération | Ce qui se passe |
|-----------|----------------|
| **Ajout/Suppression** | Crée une **copie** de la liste en arrière-plan |
| **Lecture/Parcours** | Utilise l'**ancienne version** (pas de conflit) |
| **Résultat** | ✅ Pas de crash, thread-safe ! |

**Trade-off** :
- ✅ Très efficace pour la **lecture** (broadcast fréquent)
- ⚠️ Moins efficace pour l'**écriture** (mais peu d'ajouts/suppressions)
- → Parfait pour notre cas d'usage !

---

### 2️⃣ Le flag d'arrêt du serveur

```java
private static volatile boolean running = true;
```

**Explication** :
- **`volatile`** : Garantit que TOUS les threads voient la vraie valeur (voir section dédiée)
- **`boolean running`** : Indique si le serveur doit continuer à tourner

**À quoi ça sert ?**
- Arrêter proprement le serveur quand on tape "stop"
- Tous les threads vérifient `running` pour savoir s'ils doivent s'arrêter

---

### 📌 FOCUS : Le mot-clé `volatile`

**Problème sans `volatile`** :

Chaque thread a sa **propre copie en cache** des variables pour aller plus vite.

```
Thread Principal              Thread Console
     │                              │
     │ running = false              │
     │                              │ Lit son cache : running = true ❌
     │                              │ Continue à tourner ! 💥
```

**Solution : `volatile`**
```java
private static volatile boolean running = true;
```

Avec `volatile` :
- Force TOUS les threads à lire/écrire dans la **mémoire principale** (pas le cache)
- Changements **immédiatement visibles** par tous les threads
- ✅ Synchronisation garantie !

**Analogie** :
- Sans `volatile` : Chacun a son propre tableau blanc (peut être différent)
- Avec `volatile` : Tout le monde regarde LE MÊME tableau blanc (toujours à jour)

---

## 🚀 MÉTHODE PRINCIPALE : main()

```java
public static void main(String[] args) {
    int port = 3000;
    System.out.println("Serveur lancé sur le port " + port);

    try (ServerSocket serverSocket = new ServerSocket(port)) {
        // ... code
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### Ligne par ligne :

#### 1. Définir le port
```java
int port = 3000;
```
- **Port 3000** : "Adresse" sur laquelle le serveur écoute
- Les clients devront se connecter à `localhost:3000`

#### 2. Créer le ServerSocket
```java
try (ServerSocket serverSocket = new ServerSocket(port)) {
```

**Explication** :
- **`ServerSocket`** = La "porte d'entrée" du serveur 🚪
- **`new ServerSocket(port)`** = Ouvre la porte sur le port 3000
- **`try (...)`** = Try-with-resources : ferme automatiquement le socket à la fin

**Analogie** :
- Le serveur = Un restaurant 🍽️
- ServerSocket = La porte d'entrée du restaurant
- Port 3000 = L'adresse du restaurant (3000 rue Main Street)

---

### 📌 FOCUS : ServerSocket vs Socket

| Type | Rôle | Analogie |
|------|------|----------|
| **ServerSocket** | Écoute et attend les connexions | 🚪 Porte du restaurant |
| **Socket** | Connexion établie avec UN client | 🪑 Table attribuée à un client |

```java
ServerSocket serverSocket = new ServerSocket(3000);  // Ouvre le restaurant
Socket clientSocket = serverSocket.accept();         // Un client arrive, on lui donne une table
```

**Différence clé** :
- **1 seul ServerSocket** par serveur (la porte)
- **N Sockets** (un par client connecté) (les tables)

---

## 🔁 BOUCLE PRINCIPALE : Accepter les clients

```java
startConsoleThread(serverSocket);  // Lance le thread console

while (running) {
    try {
        Socket clientSocket = serverSocket.accept();  // ⏸️ ATTEND un client
        System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

        ClientHandler handler = new ClientHandler(clientSocket);
        clients.add(handler);

        new Thread(handler).start();  // Lance le thread pour ce client

    } catch (SocketException e) {
        if (running) {
            e.printStackTrace();
        }
    }
}
```

### Ligne par ligne :

#### 1. Lancer le thread console
```java
startConsoleThread(serverSocket);
```
- Permet au serveur d'envoyer des messages pendant qu'il accepte des clients
- Expliqué en détail plus bas ⬇️

#### 2. Attendre un client
```java
Socket clientSocket = serverSocket.accept();  // ⏸️ BLOQUANT
```

**IMPORTANT** : `accept()` est **BLOQUANT** :
- Le programme **s'arrête ici** et attend
- Dès qu'un client se connecte → retourne un `Socket`
- Puis continue avec le client suivant

**Analogie** :
```
Restaurant (serveur) :
  → Hôtesse à la porte (serverSocket.accept())
  → Elle ATTEND qu'un client arrive ⏸️
  → Client arrive → elle lui donne une table (Socket)
  → Elle retourne attendre le prochain client
```

#### 3. Créer un gestionnaire pour ce client
```java
ClientHandler handler = new ClientHandler(clientSocket);
clients.add(handler);
```

**Explication** :
- **`ClientHandler`** : Classe qui gère la communication avec UN client
- **`handler`** : Instance spécifique pour CE client
- **`clients.add(handler)`** : Ajoute à la liste globale (pour broadcast)

**Analogie** :
```java
ClientHandler handler = new ClientHandler(clientSocket);
// = Assigner un serveur dédié à cette table
```

#### 4. Lancer un thread pour ce client
```java
new Thread(handler).start();
```

**Explication** :
- **`new Thread(handler)`** : Crée un nouveau fil d'exécution
- **`.start()`** : Démarre le thread (appelle automatiquement `handler.run()`)

**Pourquoi un thread par client ?**
- Sans threads : Le serveur traite UN client à la fois → les autres attendent ❌
- Avec threads : Chaque client a son propre "fil" → tous communiquent en même temps ✅

**Schéma** :
```
Serveur (Thread principal)       Thread Alice       Thread Bob
      │                              │                  │
      │ accept() Alice ──────────────→ run()            │
      │                              │ readLine()       │
      │ accept() Bob ─────────────────────────────────→ run()
      │                              │                  │ readLine()
      │ accept() Charlie...          │                  │
      │                              │                  │
```

---

## 📢 MÉTHODE broadcast() : Envoyer à tous

```java
public static void broadcast(String message) {
    for (ClientHandler client : clients) {
        client.output.println(message);
    }
}
```

**Explication ligne par ligne** :

```java
for (ClientHandler client : clients) {
```
- Parcourt TOUS les clients connectés (dans la liste `clients`)

```java
client.output.println(message);
```
- **`client.output`** : Le `PrintWriter` de ce client (son "canal de sortie")
- **`.println(message)`** : Envoie le message à ce client spécifique

**Analogie** :
```
Serveur annonce au micro : "Alice a rejoint le chat"
→ Chaque client (Alice, Bob, Charlie) entend l'annonce
```

---

## 🎮 MÉTHODE startConsoleThread() : Console serveur

```java
public static void startConsoleThread(ServerSocket serverSocket) {
    new Thread(() -> {
        // ... code du thread
    }).start();
}
```

### 📌 FOCUS : La syntaxe `() -> { }`

**Ta question** : "Que veut dire `(() -> {` ?"

**Réponse** : C'est une **lambda expression** (fonction anonyme) en Java 8+.

#### Syntaxe classique (AVANT Java 8) :
```java
new Thread(new Runnable() {
    @Override
    public void run() {
        // Code du thread ici
    }
}).start();
```

#### Syntaxe moderne (DEPUIS Java 8) :
```java
new Thread(() -> {
    // Code du thread ici
}).start();
```

**Décomposition** :
```java
() -> { ... }
││   │   │
││   │   └─ Corps de la fonction (code à exécuter)
││   └───── Flèche qui signifie "devient" ou "fait"
│└────────── Pas de paramètres (parenthèses vides)
└─────────── Lambda expression
```

**Équivalence** :
```java
() -> { System.out.println("Hello"); }
// = Une fonction qui prend 0 paramètres et affiche "Hello"
```

**Autre exemple avec paramètres** :
```java
(x, y) -> { return x + y; }
// = Une fonction qui prend 2 paramètres et retourne leur somme
```

**Pourquoi c'est plus simple ?**
- Moins de code répétitif
- Plus lisible
- Java comprend automatiquement que c'est un `Runnable`

---

### Code complet de startConsoleThread()

```java
public static void startConsoleThread(ServerSocket serverSocket) {
    new Thread(() -> {                              // Lambda : nouveau thread
        try (Scanner scanner = new Scanner(System.in)) {  // Lire input console
            String serverMsg;
            while (running && !(serverMsg = scanner.nextLine()).equalsIgnoreCase("stop")) {
                broadcast("Serveur => " + serverMsg);
                System.out.println("Serveur => " + serverMsg);
            }

            // Si on sort de la boucle = "stop" a été tapé
            System.out.println("Arrêt du serveur demandé.");
            broadcast("⚠️ Le serveur s'arrête, déconnexion...");

            // Ferme toutes les connexions clients
            for (ClientHandler client : clients) {
                try {
                    client.socket.close();
                } catch (IOException ignored) {}
            }

            running = false;           // Stoppe la boucle principale
            serverSocket.close();      // Ferme le ServerSocket
        }
    }).start();
}
```

**Ligne par ligne** :

#### 1. Créer un Scanner pour lire la console
```java
try (Scanner scanner = new Scanner(System.in)) {
```
- **`Scanner`** : Permet de lire ce que le serveur tape dans la console
- **`System.in`** : L'entrée standard (clavier)

#### 2. Boucle de lecture
```java
while (running && !(serverMsg = scanner.nextLine()).equalsIgnoreCase("stop")) {
```

**Décomposition** :
```java
while (
    running                                    // Le serveur tourne encore
    &&                                         // ET
    !(serverMsg = scanner.nextLine())          // Lire une ligne (et la stocker)
    .equalsIgnoreCase("stop")                  // N'est PAS "stop"
)
```

**Ce qui se passe** :
1. Attend que le serveur tape quelque chose
2. Stocke dans `serverMsg`
3. Si c'est "stop" → sort de la boucle
4. Sinon → envoie le message à tous les clients

#### 3. Envoyer le message
```java
broadcast("Serveur => " + serverMsg);
System.out.println("Serveur => " + serverMsg);
```
- Envoie à tous les clients
- Affiche aussi côté serveur (pour confirmation)

#### 4. Arrêt propre du serveur
```java
running = false;           // Stoppe la boucle while(running)
serverSocket.close();      // Ferme la porte (plus de nouveaux clients)
```

**Pourquoi `serverSocket.close()` ?**
- Le thread principal est bloqué sur `serverSocket.accept()`
- Fermer le socket **débloque** `accept()` avec une `SocketException`
- Permet au serveur de se terminer proprement

---

## 👤 CLASSE ClientHandler : Gérer un client

```java
static class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    // ... méthodes
}
```

**Explication** :
- **`static class`** : Classe interne statique (peut exister sans instance de Server)
- **`implements Runnable`** : Peut tourner dans un thread (doit avoir `run()`)

### Attributs :

| Attribut | Type | Rôle |
|----------|------|------|
| `socket` | Socket | Connexion avec CE client spécifique |
| `input` | BufferedReader | Canal de **lecture** (recevoir messages du client) |
| `output` | PrintWriter | Canal d'**écriture** (envoyer messages au client) |
| `username` | String | Pseudo de ce client |

---

### Constructeur

```java
public ClientHandler(Socket socket) {
    this.socket = socket;
    try {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

**Ligne par ligne** :

#### 1. Créer le canal de lecture (input)
```java
input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
```

**Décomposition** (de l'intérieur vers l'extérieur) :
```java
socket.getInputStream()               // Flux brut de bytes du client
      ↓
new InputStreamReader(...)            // Convertit bytes → caractères
      ↓
new BufferedReader(...)               // Ajoute un buffer (plus efficace)
      ↓
input                                 // Variable finale : lecture ligne par ligne
```

**Analogie** :
- `getInputStream()` = Tuyau brut de données (bytes)
- `InputStreamReader` = Traducteur (bytes → texte lisible)
- `BufferedReader` = Stockage temporaire (lit par paquets, pas byte par byte)

#### 2. Créer le canal d'écriture (output)
```java
output = new PrintWriter(socket.getOutputStream(), true);
```

- **`socket.getOutputStream()`** : Flux de sortie vers le client
- **`PrintWriter`** : Permet d'écrire du texte facilement
- **`true`** : **Auto-flush** (envoie immédiatement, pas de buffer)

**Pourquoi auto-flush ?**
```java
output.println("Message");

// Avec auto-flush (true) :
→ Envoyé IMMÉDIATEMENT au client ✅

// Sans auto-flush (false) :
→ Stocké dans un buffer
→ Envoyé seulement quand le buffer est plein ❌
→ Messages retardés !
```

---

### Méthode run() : Le cœur du thread client

```java
@Override
public void run() {
    try {
        // 1. Réception du pseudo
        this.username = input.readLine();
        System.out.println("Utilisateur connecté : " + username);
        broadcast("✅ " + username + " a rejoint le chat.");

        // 2. Boucle de messages
        String message;
        while ((message = input.readLine()) != null) {
            System.out.println(username + " => " + message);
            broadcast(username + " => " + message);
        }

    } catch (IOException e) {
        System.out.println("Connexion perdue avec " + username);
    } finally {
        // 3. Déconnexion
        clients.remove(this);
        socket.close();
        broadcast("❌ " + username + " s'est déconnecté.");
    }
}
```

**Décomposition** :

#### Étape 1 : Recevoir le pseudo
```java
this.username = input.readLine();
```
- **Premier message** du client = son pseudo
- Stocké dans `this.username`
- Annonce à tout le monde qu'il a rejoint

#### Étape 2 : Boucle de réception des messages
```java
while ((message = input.readLine()) != null) {
```

**Décomposition** :
```java
while (
    (message = input.readLine())   // Lire une ligne ET stocker dans message
    != null                        // Tant que ce n'est pas null (= déconnexion)
)
```

**Quand `readLine()` retourne `null` ?**
- Le client a fermé sa connexion
- Le socket a été fermé
- → Sort de la boucle → Déconnexion

#### Étape 3 : Envoyer le message à tous
```java
System.out.println(username + " => " + message);  // Log serveur
broadcast(username + " => " + message);           // Envoyer à tous
```

#### Étape 4 : Nettoyage (finally)
```java
finally {
    clients.remove(this);              // Retirer de la liste
    socket.close();                    // Fermer la connexion
    broadcast("❌ " + username + " s'est déconnecté.");
}
```

**Pourquoi `finally` ?**
- S'exécute **TOUJOURS**, même en cas d'erreur
- Garantit que le client est bien retiré de la liste
- Évite les "clients fantômes"

---

## 🎯 RÉSUMÉ : FLUX D'EXÉCUTION COMPLET

### 1️⃣ Démarrage du serveur
```
1. main() démarre
2. Crée ServerSocket sur port 3000
3. Lance startConsoleThread()
4. Entre dans while(running)
```

### 2️⃣ Connexion d'un client (Alice)
```
1. Alice se connecte → accept() retourne un Socket
2. Crée ClientHandler(socket) pour Alice
3. Ajoute à la liste clients
4. Lance new Thread(handler).start()
5. Thread Alice exécute run() :
   - Lit son pseudo
   - Entre en boucle de lecture
```

### 3️⃣ Alice envoie un message
```
1. Thread Alice : readLine() reçoit "Bonjour"
2. Affiche côté serveur : "Alice => Bonjour"
3. broadcast("Alice => Bonjour")
   → Envoie à Alice, Bob, Charlie, etc.
```

### 4️⃣ Arrêt du serveur
```
1. Serveur tape "stop"
2. Thread console :
   - Ferme tous les sockets clients
   - Met running = false
   - Ferme serverSocket
3. Thread principal sort de while(running)
4. Serveur s'arrête proprement
```

---

## 📊 CONCEPTS CLÉS À RETENIR

### 1. Multi-threading
- **1 thread principal** : Accepte les connexions
- **1 thread par client** : Gère la communication
- **1 thread console** : Permet au serveur d'interagir

### 2. Synchronisation
- **`CopyOnWriteArrayList`** : Thread-safe pour la liste clients
- **`volatile`** : Visibilité des changements entre threads

### 3. Sockets
- **ServerSocket** : Écoute et accepte les connexions
- **Socket** : Connexion établie avec un client
- **Streams** : Input (lecture) et Output (écriture)

### 4. Gestion d'erreurs
- **try-with-resources** : Ferme automatiquement les ressources
- **finally** : Nettoyage garanti, même en cas d'erreur

---

## 🚀 PROCHAINES ÉTAPES

### Ce qui va changer avec l'architecture Message :
1. Au lieu d'envoyer des `String` brutes, on enverra des objets `Message`
2. Détection automatique du type (public, privé, système)
3. Formatage uniforme avec `message.format()`
4. Timestamps automatiques

---

---

## 🏗️ INTÉGRATION DE L'ARCHITECTURE MESSAGE (V3.0)

**Date d'implémentation** : 2025-11-28

### 📌 Vue d'ensemble

L'architecture Message introduit une hiérarchie de classes pour remplacer les `String` brutes par des objets typés :

```
        Message (abstract)
            │
    ┌───────┼────────┐
    │       │        │
Broadcast Private System
Message   Message  Message
```

### 🔧 Nouvelle méthode : findClientByUsername()

```java
private static ClientHandler findClientByUsername(String username) {
    for (ClientHandler client : clients) {
        if (client.username.equals(username)) {
            return client;
        }
    }
    return null;
}
```

**Rôle** :
- Cherche un `ClientHandler` par son pseudo
- Retourne le `ClientHandler` si trouvé, sinon `null`
- Utilisée pour l'envoi sélectif des messages privés

**Pourquoi `static` ?**
- Appelée depuis `ClientHandler` (qui est `static class`)
- Accède à la liste `clients` (qui est `static`)

---

### 📨 Gestion des messages privés (ligne 133-157)

**Ancien code** (brut) :
```java
if (messageText.startsWith("@")) {
    // Parse et broadcast à tout le monde ❌
    broadcast("[@" + username + " -> " + recipient + "] " + content);
}
```

**Nouveau code** (architecture Message) :
```java
if (messageText.startsWith("@")) {
    // 1. Parser le message
    String[] parts = messageText.split(" ", 2);

    if (parts.length < 2) {
        output.println("[ERREUR] Format : @username message");
        continue;
    }

    String recipient = parts[0].substring(1);  // Enlève le @
    String content = parts[1];

    // 2. Chercher le destinataire
    ClientHandler recipientClient = findClientByUsername(recipient);

    if (recipientClient == null) {
        // Destinataire introuvable
        output.println("[ERREUR] Utilisateur '" + recipient + "' introuvable.");
    } else {
        // 3. Créer le message typé
        msg = new PrivateMessage(username, recipient, content);
        System.out.println(msg.format());

        // 4. Envoi sélectif (SEULEMENT expéditeur + destinataire)
        this.output.println(msg.format());      // Expéditeur
        recipientClient.output.println(msg.format());  // Destinataire
    }
}
```

**Améliorations** :
- ✅ **Validation** : Vérifie que le destinataire existe
- ✅ **Envoi sélectif** : Seuls l'expéditeur et le destinataire reçoivent le message
- ✅ **Type-safety** : Utilise `PrivateMessage` au lieu de String
- ✅ **Formatage uniforme** : `msg.format()` gère l'affichage

---

### 📢 Utilisation de SystemMessage (connexion/déconnexion)

**Connexion d'un utilisateur (ligne 125-127)** :

**Avant** :
```java
this.username = input.readLine();
System.out.println("Utilisateur connecté : " + username);
broadcast("✅ " + username + " a rejoint le chat.");
```

**Après** :
```java
this.username = input.readLine();

SystemMessage connectionMsg = new SystemMessage("✅ " + username + " a rejoint le chat.");
System.out.println(connectionMsg.format());
broadcast(connectionMsg.format());
```

**Déconnexion d'un utilisateur (ligne 173-175)** :

**Avant** :
```java
System.out.println("❌ " + username + " s'est déconnecté.");
broadcast("❌ " + username + " s'est déconnecté.");
```

**Après** :
```java
SystemMessage disconnectionMsg = new SystemMessage("❌ " + username + " s'est déconnecté.");
System.out.println(disconnectionMsg.format());
broadcast(disconnectionMsg.format());
```

**Avantages** :
- ✅ **Cohérence** : Toutes les notifications système passent par `SystemMessage`
- ✅ **Évolutivité** : Facile d'ajouter un timestamp ou un style spécial
- ✅ **Polymorphisme** : Tout est un `Message` (principe OOP)

---

### 🎯 Flux d'exécution d'un message privé

```
1. Alice tape : "@Bob salut !"
         ↓
2. Thread Alice : readLine() reçoit "@Bob salut !"
         ↓
3. Détection : startsWith("@") → Message privé
         ↓
4. Parsing : split(" ", 2)
   → recipient = "Bob"
   → content = "salut !"
         ↓
5. Recherche : findClientByUsername("Bob")
   → Retourne le ClientHandler de Bob
         ↓
6. Création : new PrivateMessage("Alice", "Bob", "salut !")
         ↓
7. Envoi sélectif :
   → Alice reçoit : "[MP à Bob] salut !"
   → Bob reçoit   : "[MP de Alice] salut !"
   → Charlie NE reçoit RIEN ✅
```

---

### 🔍 Différence : broadcast() vs envoi sélectif

**broadcast()** - Messages publics :
```java
broadcast(msg.format());
// → Envoie à TOUS les clients de la liste
```

**Envoi sélectif** - Messages privés :
```java
this.output.println(msg.format());           // Expéditeur
recipientClient.output.println(msg.format()); // Destinataire
// → Envoie SEULEMENT à 2 personnes
```

**Schéma** :
```
Clients connectés : Alice, Bob, Charlie

Message public (Alice : "Bonjour à tous")
→ broadcast() → Alice ✅, Bob ✅, Charlie ✅

Message privé (Alice : "@Bob salut toi")
→ Envoi sélectif → Alice ✅, Bob ✅, Charlie ❌
```

---

### 🧪 Tests effectués

✅ **Connexion** : Notifications avec `SystemMessage`
✅ **Messages publics** : Tous les clients reçoivent
✅ **Messages privés** : Seuls expéditeur + destinataire reçoivent
✅ **Utilisateur introuvable** : Message d'erreur affiché
✅ **Déconnexion** : Notifications avec `SystemMessage`

---

### 📚 Concepts OOP appliqués

| Concept | Où ? | Exemple |
|---------|------|---------|
| **Héritage** | Message → BroadcastMessage, PrivateMessage, SystemMessage | `extends Message` |
| **Polymorphisme** | `msg.format()` | Comportement différent selon le type |
| **Encapsulation** | Attributs `private`, méthodes `public` | `private String sender` |
| **Abstraction** | Classe abstraite `Message` | `abstract String format()` |

---

**FIN DE LA DOCUMENTATION (PARTIE 2)**
**Architecture Message complètement intégrée et testée ✅**
