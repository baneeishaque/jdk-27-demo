#!/usr/bin/env bash
# Run the JMH vector dot-product benchmark and write the raw report.
#
# Usage: scripts/bench-vector.sh
# Output: benchmarks/results/vector-jmh.txt (also printed to stdout)
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
mkdir -p benchmarks/results

java --enable-preview --add-modules jdk.incubator.vector \
  -cp "target/classes:$(cat target/classpath.txt)" \
  org.openjdk.jmh.Main 'VectorDotBenchmark' -rf text -rff benchmarks/results/vector-jmh.txt
