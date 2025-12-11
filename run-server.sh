#!/bin/bash
# Lance le serveur

cd "$(dirname "$0")"
java -cp bin server.Server
