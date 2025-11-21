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

- 🔧 **Intégration de l'architecture Message dans Server.java**
  - Adapter le serveur pour utiliser les nouvelles classes
  - Implémenter la détection des messages privés (@username)
  - Remplacer les String par des objets Message

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

**Au démarrage de la prochaine session:**
1. Lire ce fichier PROJECT_STATUS.md
2. Résumer ce qui a été fait
3. Proposer la prochaine feature à implémenter
4. Guider avec des questions (pas de solutions directes)
5. Enseigner Git au fur et à mesure

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
