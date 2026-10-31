#!/usr/bin/env bash
# Verify the Docker build context and Dockerfiles never place restricted
# configuration, licenses, attachments, or the production-derived index in
# its static Dockerfile/context policy. This test intentionally does not
# require Docker; an actual context/layer inspection remains a runtime check.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
IGNORE="$ROOT/.dockerignore"
BASE="$ROOT/docker/Dockerfile"
BUILT="$ROOT/docker/Dockerfile.built"
BUILT_IGNORE="$ROOT/docker/Dockerfile.built.dockerignore"

require_line() {
  local file="$1" expected="$2"
  grep -Fxq "$expected" "$file" || {
    echo "docker input test failed: missing '$expected' in $file" >&2
    exit 1
  }
}

reject_pattern() {
  local file="$1" pattern="$2"
  if grep -nE "$pattern" "$file"; then
    echo "docker input test failed: forbidden pattern '$pattern' in $file" >&2
    exit 1
  fi
}

require_line "$IGNORE" '*'
require_line "$IGNORE" '!project/target/gikam-third-czjky-14.1.5-run.jar'
require_line "$IGNORE" 'project/*'
require_line "$IGNORE" 'project/target/*'
require_line "$IGNORE" 'project/.git'
require_line "$IGNORE" 'project/.git/**'
require_line "$IGNORE" 'project/application.properties'
require_line "$IGNORE" 'materials/*'

reject_pattern "$IGNORE" '^!private/'
reject_pattern "$IGNORE" '^!materials/index'
reject_pattern "$IGNORE" '^!project/(src|sql|\.git)'
reject_pattern "$BASE" '^[[:space:]]*COPY[[:space:]].*(private/|materials/index)'
reject_pattern "$BUILT" '^[[:space:]]*COPY[[:space:]].*(private/|materials/index)'
reject_pattern "$BUILT" '^FROM[[:space:]].*(lims-app|\$\{BASE_IMAGE\})'

require_line "$BUILT_IGNORE" '!project/target/gikam-third-czjky-14.1.5-run.jar'
require_line "$BUILT_IGNORE" 'project/*'
require_line "$BUILT_IGNORE" 'materials/*'
reject_pattern "$BUILT_IGNORE" '^!materials/gikam-third-czjky-14.1.5-run.jar'
reject_pattern "$BUILT_IGNORE" '^!materials/index'
reject_pattern "$BUILT_IGNORE" '^!project/(src|sql|\.git)'

grep -Fxq 'COPY project/target/gikam-third-czjky-14.1.5-run.jar .' "$BUILT" || {
  echo "docker input test failed: built image does not copy the current jar" >&2
  exit 1
}
grep -Fxq 'FROM debian:bullseye-slim' "$BUILT" || {
  echo "docker input test failed: built image is not self-contained" >&2
  exit 1
}

echo "docker build input test PASS"
