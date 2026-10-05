#!/usr/bin/env bash
# Measure the JVM-level JDK 27 features and emit a markdown report on stdout.
#
# Usage: scripts/bench-jvm-features.sh > benchmarks/results/jvm-features.md
# These are single-run wall-clock observations, not statistically rigorous
# benchmarks: they demonstrate the feature deltas, while scripts/bench-vector.sh
# provides the JMH-grade numbers for the Vector API.
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
CP="target/classes:$(cat target/classpath.txt)"
FLAGS=(--enable-preview --add-modules jdk.incubator.vector)

java_version=$(java -version 2>&1 | head -1)
echo "# JVM feature measurements"
echo
echo "- JDK: ${java_version}"
echo "- Machine: $(uname -m) on $(uname -s) $(uname -r)"
echo "- Flags: ${FLAGS[*]}"
echo
echo "## Compact object headers (JEP 534)"
echo
compact=$(java "${FLAGS[@]}" -cp "$CP" io.github.baneeishaque.jdk27.Main headers \
  | grep -o 'allocation: [0-9]* bytes/object' | grep -o '[0-9]*')
legacy=$(java "${FLAGS[@]}" -XX:-UseCompactObjectHeaders -cp "$CP" \
  io.github.baneeishaque.jdk27.Main headers \
  | grep -o 'allocation: [0-9]* bytes/object' | grep -o '[0-9]*')
echo "| Scenario | bytes per Point(1,2) |"
echo "| --- | --- |"
echo "| compact object headers (default) | ${compact} |"
echo "| -XX:-UseCompactObjectHeaders | ${legacy} |"
echo
echo "## G1 under constrained CPU count (JEP 523)"
echo
g1_line=$(java -XX:ActiveProcessorCount=1 -XX:+PrintFlagsFinal -version 2>/dev/null \
  | grep -E 'UseG1GC.*=' | head -1 | tr -s ' ')
echo '```text'
echo "-XX:ActiveProcessorCount=1:${g1_line}"
echo '```'
echo
echo "## Structured concurrency order pipeline (JEP 533)"
echo
orders=$(java "${FLAGS[@]}" -cp "$CP" io.github.baneeishaque.jdk27.Main orders)
sequential=$(echo "$orders" | grep -o 'sequential: [0-9]*ms' | grep -o '[0-9]*')
structured=$(echo "$orders" | grep -o 'structured: [0-9]*ms' | grep -o '[0-9]*')
echo "| Mode | 4 orders wall clock |"
echo "| --- | --- |"
echo "| sequential service calls | ${sequential} ms |"
echo "| structured fan-out | ${structured} ms |"
echo
echo "## Vector API dot product, in-process timing (JEP 537)"
echo
vector=$(java "${FLAGS[@]}" -cp "$CP" io.github.baneeishaque.jdk27.Main vector | tail -n +3)
echo '```text'
echo "$vector"
echo '```'
echo
echo "> For JMH-grade vector numbers see \`vector-jmh.txt\`."
