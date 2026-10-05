#!/usr/bin/env bash
# Run the full JDK 27 showcase (all demos, or one demo id).
#
# Usage: scripts/run-demo.sh [demo-id]
# Requires: JDK 27 and Maven on PATH (or `mise x java@27 maven@3.10.0 --`).
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt

exec java --enable-preview --add-modules jdk.incubator.vector \
  -cp "target/classes:$(cat target/classpath.txt)" \
  io.github.baneeishaque.jdk27.Main "$@"
