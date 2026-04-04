# JavaGroupChat

> Application de messagerie temps réel multi-clients avec système de channels, messages privés et interface graphique JavaFX.

## Stack technique

| Couche | Technologie |
|--------|-------------|
| Langage | Java 17+ |
| Réseau | TCP Sockets |
| Concurrence | Multithreading, CopyOnWriteArrayList |
| UI | JavaFX |
| Tests | JUnit (TestMessages.java) |

## Architecture

```
[Client TCP]
     ↓ connexion TCP (port 3000)
[ServerClientHandler — thread dédié par client]
     ↓
[ChatServer]
  ├── broadcast → BroadcastMessage
  ├── routing privé → PrivateMessage (@username)
  └── channels → ChatRoom (HashMap<String, ChatRoom>)
     ↓
[Hiérarchie Message]
  Message (abstraite)
  ├── BroadcastMessage
  ├── PrivateMessage
  └── SystemMessage
```

## Fonctionnalités

**Messagerie**
- Chat de groupe avec broadcast thread-safe
- Messages privés — syntaxe `@username message`
- Timestamps `[HH:mm:ss]` sur tous les messages
- Historique des 20 derniers messages envoyé aux nouveaux connectés

**Channels**
- Channels par défaut : `#general`, `#random`, `#java`
- `/join #channel` · `/leave` · `/channels`
- Messages isolés par channel — visibilité stricte

**Interface JavaFX** *(en cours)*
- Fenêtre de chat avec zone messages et saisie
- Différenciation visuelle messages publics / privés / système
- Connexion avec pseudonyme

## Lancement

```bash
git clone https://github.com/jxgedouard/distributed-chat-system.git
cd distributed-chat-system

# Démarrer le serveur
javac Server.java && java Server

# Démarrer un client (dans un autre terminal)
javac Client.java && java Client
```

## Commandes disponibles

| Commande | Description |
|----------|-------------|
| `@username message` | Message privé |
| `/join #channel` | Rejoindre un channel |
| `/leave` | Quitter le channel actuel |
| `/channels` | Lister les channels disponibles |

## Concepts techniques

| Concept | Implémentation |
|---------|----------------|
| Héritage & polymorphisme | Classe abstraite `Message`, méthode `format()` |
| Thread-safety | `CopyOnWriteArrayList` pour les clients connectés |
| Collections | `LinkedList` (historique FIFO) · `HashMap` (channels) |
| Streams I/O | `BufferedReader` / `PrintWriter` sur socket TCP |

## Roadmap

- [ ] Interface graphique JavaFX
- [ ] Commandes `/help` et `/list`
- [ ] Validation et unicité des pseudonymes
- [ ] Système de rôles (Admin / Moderator / User)
