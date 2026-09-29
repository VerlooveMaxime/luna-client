#!/usr/bin/env bash
# Compiles (if needed) and launches the Luna #377 client against localhost:43594.
set -euo pipefail
cd "$(dirname "$0")"
if [ ! -f out/client.class ] || [ -n "$(find src -name '*.java' -newer out/client.class | head -1)" ]; then
  echo "Compiling client..."
  mkdir -p out
  javac -nowarn -encoding UTF-8 -d out $(find src -name '*.java')
fi
exec java -cp out client "$@"
