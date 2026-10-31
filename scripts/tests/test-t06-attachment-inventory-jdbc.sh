#!/usr/bin/env bash
set -euo pipefail
umask 077

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DRIVER="$ROOT/tools/db-probe/lib/ojdbc8-19.3.0.0.jar"
HELPER="$ROOT/tools/t06-attachment-inventory/AttachmentInventory.java"
TEST="$ROOT/scripts/tests/AttachmentInventoryTest.java"
classes="$(mktemp -d)"
trap 'rm -rf "$classes"' EXIT

javac -source 8 -target 8 -cp "$DRIVER" -d "$classes" "$HELPER" "$TEST" >/dev/null 2>&1
java -cp "$classes:$DRIVER" AttachmentInventoryTest
echo "t06 attachment inventory JDBC test PASS"
