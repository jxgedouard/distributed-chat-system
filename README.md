# 📊 STATUT DU PROJET - JavaGroupChat

**Dernière mise à jour :** 2025-11-21

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

## 🚧 FONCTIONNALITÉS EN COURS

- 🔧 **Intégration de l'architecture Message dans Server.java** (70% terminé)
  - ✅ Imports des classes Message ajoutés
  - ✅ Détection des messages privés (@username) vs publics
  - ✅ Création des objets Message appropriés (BroadcastMessage/PrivateMessage)
  - ✅ Utilisation de format() pour l'affichage
  - ⏳ TODO: Méthode findClientByUsername() pour chercher un client
  - ⏳ TODO: Envoi sélectif des messages privés (seulement expéditeur + destinataire)
  - ⏳ TODO: Remplacer notifications connexion/déconnexion par SystemMessage
  - ⏳ TODO: Tests complets serveur + clients

---

## 📋 FONCTIONNALITÉS À FAIRE

### 🟢 NIVEAU SIMPLE
- [x] 📐 **ARCHITECTURE:** Refactoring avec héritage ✅ **TERMINÉ**
  - Classe abstraite `Message`
  - Sous-classes: `BroadcastMessage`, `PrivateMessage`, `SystemMessage`

- [ ] **Commande /help**
  - Afficher toutes les commandes disponibles
  - Format clair et lisible

- [ ] **Commande /list**
  - Afficher tous les utilisateurs connectés
  - Afficher leur statut actuel

- [ ] **Validation des pseudos**
  - Vérifier unicité du pseudo
  - Interdire: @, #, / et espaces
  - Refuser pseudos vides

---

### 🟡 NIVEAU INTERMÉDIAIRE

- [ ] **Timestamps sur messages**
  - Format: `[HH:mm:ss]`
  - Ajout automatique côté serveur

- [ ] **Système de statuts**
  - 🟢 En ligne (défaut)
  - 🟠 Absent
  - 🔴 Occupé
  - Commande: `/status [en ligne|absent|occupé]`

- [ ] **Historique des messages**
  - Sauvegarder les 20 derniers messages
  - Envoyer l'historique aux nouveaux connectés
  - Structure de données adaptée (Queue?)

---

### 🔴 NIVEAU AVANCÉ

- [ ] **Système de channels/salons**
  - Channels par défaut: #general, #random, #java
  - Commandes: `/join #channel`, `/leave`, `/channels`
  - HashMap pour gérer les channels
  - Messages visibles uniquement dans le channel actuel

- [ ] **Système de rôles et permissions**
  - Interface `Role` avec méthodes abstraites
  - Classes: `Admin`, `Moderator`, `User`
  - Permissions différentes par rôle
  - Commandes réservées aux admins

---

### 🌟 FEATURES WOW

- [ ] **Réactions emoji en temps réel**
  - Commande: `+react <messageID> <emoji>`
  - Affichage: `Alice => Salut ! [👍 x3] [❤️ x1]`
  - Stockage des réactions par message

---

### 🎨 BONUS

- [ ] **Interface graphique**
  - Intégration branche `feature/interface`
  - Connexion Client.java avec UI
  - Affichage temps réel des messages

---

## 🎯 PRIORITÉS RECOMMANDÉES

1. **Refactoring architecture** (héritage Message) - Important pour le prof
2. **/help et /list** - Rapide et utile
3. **Validation pseudos** - Évite bugs
4. **Timestamps** - Facile et professionnel
5. **Système de channels** - Feature impressionnante
6. **Réactions emoji** - Le "wow effect"

---

## 📝 NOTES IMPORTANTES

### Objectifs pédagogiques pour le prof:
- ✅ Multi-threading (déjà fait)
- ✅ Streams I/O (déjà fait)
- ✅ Héritage (classe abstraite Message) ← **TERMINÉ !**
- ✅ Polymorphisme (méthode format() dans Message) ← **TERMINÉ !**
- ✅ Encapsulation (attributs privés + getters)
- 🎯 Interface (Role) - À faire
- 🎯 Collections avancées (HashMap, Queue) - À faire

### Branches Git:
- `main` - Branche principale
- `feature/private-messages` - Messages privés (ancienne version, à fusionner)
- `feature/message-hierarchy` - **BRANCHE ACTUELLE** (architecture Message OOP)
- `feature/interface` - UI en cours (brouillon)

---

## 🔄 PROCHAINE SESSION

**Session du 2025-11-21 - Statut : En pause sur intégration Message**

**Contexte** :
- Branche actuelle : `feature/message-hierarchy`
- Fichiers modifiés : `src/server/Server.java`
- Documentation créée : `documentation/Server_Explications.md`
- Dernier commit : "feat(wip): integrate Message architecture in Server (partial)"

**Ce qui reste à faire (30-40 min estimé)** :

1. **Ajouter méthode `findClientByUsername(String username)` dans Server.java** (~5 min)
   - Parcourt la liste `clients`
   - Retourne le ClientHandler correspondant ou null

2. **Implémenter l'envoi sélectif pour messages privés** (~10 min)
   - Ligne 144 de Server.java : remplacer le TODO
   - Utiliser findClientByUsername() pour trouver le destinataire
   - Envoyer à l'expéditeur ET au destinataire seulement
   - Gérer le cas "utilisateur introuvable"

3. **Remplacer notifications par SystemMessage** (~5 min)
   - Lignes 120, 131 : connexion/déconnexion
   - Utiliser `new SystemMessage(...)` au lieu de String brute

4. **Tester** (~15 min)
   - Compiler : `javac src/server/Server.java src/messages/*.java`
   - Lancer serveur + plusieurs clients
   - Tester messages publics, privés, connexions

5. **Mettre à jour documentation** (~10 min)
   - Ajouter section sur intégration Message dans Server_Explications.md
   - Expliquer findClientByUsername(), parsing messages privés

6. **Commit final** (~5 min)
   - `git add .`
   - `git commit -m "feat: complete Message architecture integration"`

**Au démarrage de la prochaine session:**
1. Lire ce fichier README.md (section "PROCHAINE SESSION")
2. Reprendre là où on s'est arrêté (Server.java ligne 144)
3. Suivre le plan ci-dessus étape par étape
4. Mode pédagogique : guider avec questions, pas de solutions directes

**Méthodologie d'apprentissage:**
- ❌ Ne PAS donner la solution complète
- ✅ Poser des questions précises pour guider
- ✅ Expliquer les commandes Git au moment opportun
- ✅ Faire réfléchir sur l'architecture avant de coder
- ✅ Encourager à chercher dans la doc Java

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
