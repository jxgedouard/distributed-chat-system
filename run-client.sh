#!/bin/bash
# Lance le client JavaFX

cd "$(dirname "$0")"

JAVAFX_PATH="/Users/edouard/.m2/repository/org/openjfx/javafx-controls/21:/Users/edouard/.m2/repository/org/openjfx/javafx-graphics/21:/Users/edouard/.m2/repository/org/openjfx/javafx-base/21"

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls -cp bin client.ChatClientUI
