# 📊 STATUT DU PROJET - JavaGroupChat

**Dernière mise à jour :** 2025-11-28

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

---

## 🚧 FONCTIONNALITÉS EN COURS

Aucune feature en cours actuellement

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

**Session du 2025-11-28 - Statut : Architecture Message COMPLÈTE ✅**

**Ce qui a été fait cette session** :
1. ✅ Méthode `findClientByUsername()` implémentée
2. ✅ Envoi sélectif des messages privés (uniquement expéditeur + destinataire)
3. ✅ Notifications avec `SystemMessage` (connexion/déconnexion)
4. ✅ Tests complets (connexion, messages publics/privés, déconnexion)
5. ✅ Documentation mise à jour (Server_Explications.md - Partie 2)
6. ✅ README actualisé

**Prochaines features possibles** :
- [ ] Commande `/help` (afficher toutes les commandes)
- [ ] Commande `/list` (voir tous les utilisateurs connectés)
- [ ] Validation des pseudos (unicité, caractères interdits)
- [ ] Timestamps sur les messages
- [ ] Système de statuts (en ligne, absent, occupé)
- [ ] Historique des messages (20 derniers messages)

**Rappel méthodologie :**
- Mode pédagogique : guider avec questions, pas de solutions directes
- Faire réfléchir avant de coder
- Documenter au fur et à mesure

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
