#!/usr/bin/env bash
# Verify that owned Docker contexts contain only the packaged distribution.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
IGNORE="$ROOT/.dockerignore"
BASE="$ROOT/docker/Dockerfile"
BUILT="$ROOT/docker/Dockerfile.built"
BUILT_IGNORE="$ROOT/docker/Dockerfile.built.dockerignore"

require_line() {
  local file="$1" expected="$2"
  grep -Fxq "$expected" "$file" || { echo "docker input test failed: missing '$expected' in $file" >&2; exit 1; }
}
reject_pattern() {
  local file="$1" pattern="$2"
  if grep -nE "$pattern" "$file"; then
    echo "docker input test failed: forbidden pattern '$pattern' in $file" >&2
    exit 1
  fi
}

for file in "$IGNORE" "$BUILT_IGNORE"; do
  require_line "$file" '*'
  require_line "$file" '!owned-distribution/target/distribution/'
  require_line "$file" '!owned-distribution/target/distribution/**'
  reject_pattern "$file" '(^|/)(materials|private|project/target|sunway-app\.so)'
done
for file in "$BASE" "$BUILT"; do
  reject_pattern "$file" 'COPY[[:space:]].*(materials/|private/|project/target/|sunway-app\.so|gikam-third)'
  reject_pattern "$file" 'FROM[[:space:]]+debian:bullseye-slim'
  grep -Fq 'COPY owned-distribution/target/distribution/' "$file" || {
    echo "docker input test failed: owned distribution is not the image input" >&2; exit 1;
  }
done
grep -Fq 'ARG JAVA_BASE_IMAGE=' "$BUILT"
echo "docker build input test PASS"
