#!/bin/bash
# Lanza RuneLite desde el codigo fuente con este plugin ya cargado.
set -e
export JAVA_HOME="$HOME/.local/opt/jdk-17.0.20"
export PATH="$JAVA_HOME/bin:$PATH"
cd "$(dirname "$0")"
./gradlew run
