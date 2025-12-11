# 📊 STATUT DU PROJET - JavaGroupChat

**Dernière mise à jour :** 2025-12-11

---

## ✅ FONCTIONNALITÉS TERMINÉES

### V1.0 - Base du projet
- ✅ Serveur TCP multi-clients (port 3000)
- ✅ Client TCP avec connexion au serveur
- ✅ Chat de groupe (broadcast)
- ✅ Système de pseudonymes
- ✅ Gestion thread-safe (CopyOnWriteArrayList)
- ✅ Console serveur (envoi messages + commande `stop`)
- ✅ Notifications connexion/déconnexion

### V2.0 - Messages privés
- ✅ Messages privés avec syntaxe `@username message`
- ✅ Méthode `findClientByUsername()`
- ✅ Méthode `sendToUser()` pour routing privé
- ✅ Affichage `[MP de X]` et `[MP à Y]`
- ✅ Gestion erreur si destinataire inexistant

### V3.0 - Architecture Message (refactoring)
- ✅ Classe abstraite `Message` avec attributs communs (sender, content, timestamp)
- ✅ Classe `BroadcastMessage` pour messages publics
- ✅ Classe `PrivateMessage` pour messages privés (avec recipient)
- ✅ Classe `SystemMessage` pour notifications serveur
- ✅ Méthode abstraite `format()` implémentée par chaque type
- ✅ Tests unitaires avec `TestMessages.java`

---

## ✅ FONCTIONNALITÉS TERMINÉES (suite)

### V3.0 - Intégration de l'architecture Message (TERMINÉ ✅)
- ✅ Imports des classes Message ajoutés
- ✅ Détection des messages privés (@username) vs publics
- ✅ Création des objets Message appropriés (BroadcastMessage/PrivateMessage)
- ✅ Utilisation de format() pour l'affichage
- ✅ Méthode findClientByUsername() implémentée
- ✅ Envoi sélectif des messages privés (seulement expéditeur + destinataire)
- ✅ Notifications connexion/déconnexion avec SystemMessage
- ✅ Tests complets serveur + clients
- ✅ Documentation mise à jour (Server_Explications.md)

### V4.0 - Timestamps et Historique (TERMINÉ ✅)
- ✅ Timestamps format [HH:mm:ss] sur tous les messages
- ✅ DateTimeFormatter dans toutes les classes Message
- ✅ Historique des 20 derniers messages (LinkedList/Queue)
- ✅ Méthode addToHistory() avec rotation automatique
- ✅ Méthode sendHistoryTo() pour nouveaux clients
- ✅ Sauvegarde de tous types de messages (broadcast, privé, système)

### V5.0 - Système de Channels (TERMINÉ ✅)
- ✅ Classe ChatRoom pour gérer les salons
- ✅ HashMap pour stocker les channels (#general, #random, #java)
- ✅ Commande /join #channel pour rejoindre un salon
- ✅ Commande /leave pour quitter un salon
- ✅ Commande /channels pour lister les salons disponibles
- ✅ Messages visibles uniquement dans le channel actuel
- ✅ Notifications quand un utilisateur rejoint/quitte un channel
- ✅ Attribution automatique au #general à la connexion

---

## 🚧 FONCTIONNALITÉS EN COURS

Aucune feature en cours actuellement

---

## 📋 FONCTIONNALITÉS À FAIRE

### ✅ SÉLECTION POUR PRÉSENTATION AMPHI (4 features)

- [x] 📐 **ARCHITECTURE:** Refactoring avec héritage ✅ **TERMINÉ**
  - Classe abstraite `Message`
  - Sous-classes: `BroadcastMessage`, `PrivateMessage`, `SystemMessage`

- [x] 🕐 **Timestamps sur messages** ✅ **TERMINÉ**
  - Format: `[HH:mm:ss]`
  - DateTimeFormatter implémenté dans toutes les classes Message

- [x] 📜 **Historique des messages** ✅ **TERMINÉ**
  - Sauvegarde des 20 derniers messages
  - Envoi automatique aux nouveaux connectés
  - Structure: LinkedList (Queue)
  - **Concept:** Queue (objectif pédagogique)

- [x] 🏠 **Système de channels/salons** ✅ **TERMINÉ**
  - Channels par défaut: #general, #random, #java
  - Commandes: `/join #channel`, `/leave`, `/channels`
  - HashMap pour gérer les channels
  - Messages visibles uniquement dans le channel actuel
  - Notification quand un utilisateur rejoint/quitte un channel
  - **Concept:** HashMap (objectif pédagogique)

- [ ] 📺 **Interface graphique JavaFX** ← DERNIÈRE ÉTAPE
  - Fenêtre de chat avec zone messages + zone saisie
  - Connexion avec pseudo
  - Affichage temps réel des messages
  - Différenciation visuelle messages publics/privés
  - Temps estimé: 2-3h (avec aide Gemini)

---

### 🟢 NIVEAU SIMPLE (NON RETENU - disponible pour plus tard)

- [ ] **(NON RETENU) Commande /help**
  - Afficher toutes les commandes disponibles
  - Format clair et lisible

- [ ] **(NON RETENU) Commande /list**
  - Afficher tous les utilisateurs connectés
  - Afficher leur statut actuel

- [ ] **(NON RETENU) Validation des pseudos**
  - Vérifier unicité du pseudo
  - Interdire: @, #, / et espaces
  - Refuser pseudos vides

---

### 🟡 NIVEAU INTERMÉDIAIRE (NON RETENU - disponible pour plus tard)

- [ ] **(NON RETENU) Système de statuts**
  - 🟢 En ligne (défaut)
  - 🟠 Absent
  - 🔴 Occupé
  - Commande: `/status [en ligne|absent|occupé]`

---

### 🔴 NIVEAU AVANCÉ (NON RETENU - disponible pour plus tard)

- [ ] **(NON RETENU) Système de rôles et permissions**
  - Interface `Role` avec méthodes abstraites
  - Classes: `Admin`, `Moderator`, `User`
  - Permissions différentes par rôle
  - Commandes réservées aux admins

---

### 🌟 FEATURES WOW (NON RETENU - disponible pour plus tard)

- [ ] **(NON RETENU) Réactions emoji en temps réel**
  - Commande: `+react <messageID> <emoji>`
  - Affichage: `Alice => Salut ! [👍 x3] [❤️ x1]`
  - Stockage des réactions par message

---

## 🎯 ORDRE D'IMPLÉMENTATION RECOMMANDÉ

1. ✅ **Refactoring architecture** (héritage Message) - TERMINÉ
2. ✅ **Timestamps** - TERMINÉ
3. ✅ **Historique messages** - TERMINÉ
4. ✅ **Système de channels** - TERMINÉ
5. ⏳ **Interface JavaFX** (2-3h) - En dernier pour tester toutes les features visuellement

**Progrès : 4/5 features terminées (80%)**

---

## 📝 NOTES IMPORTANTES

### Objectifs pédagogiques pour le prof:
- ✅ Multi-threading (déjà fait)
- ✅ Streams I/O (déjà fait)
- ✅ Héritage (classe abstraite Message) ← **TERMINÉ !**
- ✅ Polymorphisme (méthode format() dans Message) ← **TERMINÉ !**
- ✅ Encapsulation (attributs privés + getters)
- ✅ Collections avancées - **TERMINÉ**
  - ✅ Queue pour l'historique des messages (LinkedList)
  - ✅ HashMap pour les channels/salons
- ❌ Interface (Role) - Non retenu pour la présentation

### Branches Git:
- `main` - Branche principale (à jour avec toutes les features terminées)
- `feature/message-hierarchy` - Architecture Message + Channels (mergée dans main ✅)
- `feature/interface` - UI en cours (brouillon)
- `feature/private-messages` - OBSOLÈTE (à supprimer)

---

## 🔄 PROCHAINE SESSION

**Session du 2025-12-11 - Statut : Channels TERMINÉS ✅**

**Ce qui a été fait cette session** :
1. ✅ Système de channels avec HashMap (#general, #random, #java)
2. ✅ Commandes /join, /leave, /channels
3. ✅ Notifications quand utilisateur rejoint/quitte un channel
4. ✅ Merge dans main (architecture Git nettoyée)
5. ✅ README mis à jour

**Features restantes pour la présentation amphi** :
1. ⏳ 📺 Interface graphique JavaFX (2-3h) - **DERNIÈRE ÉTAPE**

**Progrès total : 4/5 features terminées (80%)**

**Prochaines étapes :**
- Implémenter l'interface JavaFX pour finaliser le projet
- Tester toutes les features visuellement

---

## 📚 COMMANDES GIT UTILISÉES

```bash
# Voir l'état actuel
git status

# Voir l'historique
git log --oneline

# Créer une nouvelle branche
git checkout -b feature/nom-feature

# Changer de branche
git checkout nom-branche

# Ajouter fichiers modifiés
git add .
git add chemin/vers/fichier.java

# Créer un commit
git commit -m "description du commit"

# Pousser vers GitHub
git push origin nom-branche

# Fusionner une branche (depuis main)
git merge feature/nom-feature
```

---

**FIN DU FICHIER DE STATUT**
