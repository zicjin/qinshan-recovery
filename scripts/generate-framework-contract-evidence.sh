#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="${1:-$ROOT/evidence/framework-contract}"
ARTIFACT_ROOT="${LIMS_EXTERNAL_ARTIFACT_ROOT:-$ROOT/.external-materials}"
RUN_JAR="${SUPPLIER_RUN_JAR:-$ARTIFACT_ROOT/gikam-third-czjky-14.1.5-run.jar}"
NATIVE="${SUPPLIER_NATIVE:-$ARTIFACT_ROOT/sunway-app.so}"
mkdir -p "$OUT"
python3 "$ROOT/tools/framework_contract_inventory.py" \
  --run-jar "$RUN_JAR" \
  --native "$NATIVE" --output-dir "$OUT"
echo "framework contract evidence written to $OUT"
