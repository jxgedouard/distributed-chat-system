#!/bin/bash
# Script de démonstration - Lance serveur + 3 clients automatiquement

cd "$(dirname "$0")"

echo "🚀 Lancement de la démonstration JavaGroupChat..."
echo ""

# Compiler si nécessaire
echo "📦 Compilation..."
javac --module-path "$HOME/.m2/repository/org/openjfx/javafx-controls/21:$HOME/.m2/repository/org/openjfx/javafx-graphics/21:$HOME/.m2/repository/org/openjfx/javafx-base/21" --add-modules javafx.controls -d bin src/messages/*.java src/channels/*.java src/server/*.java src/client/*.java 2>/dev/null

echo "✅ Compilation terminée"
echo ""

# Lancer le serveur en arrière-plan
echo "🖥️  Lancement du serveur..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && ./run-server.sh"'
sleep 2

# Lancer 3 clients
echo "👤 Lancement des clients..."
for i in 1 2 3; do
    osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && ./run-client.sh"'
    sleep 0.5
done

echo ""
echo "✨ Démonstration lancée !"
echo "   - 1 serveur"
echo "   - 3 clients"
echo ""
echo "💡 Pseudos suggérés : Alice, Bob, Charlie"
