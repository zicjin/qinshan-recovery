#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
REPO="$(cd "$ROOT/.." && pwd -P)"

# Publication safety is deliberately a read-only gate.  A runner that leaves
# an unregistered JSON, stale canonical row, incomplete strict contract or
# invalid output default fails here; classification/promote remains an
# explicit lifecycle action outside the gate.
python3 "$ROOT/scripts/manage-evidence.py" verify >/dev/null
python3 "$ROOT/scripts/tests/test-evidence-registry.py" >/dev/null
python3 "$ROOT/scripts/tests/test-evidence-output-policy.py" >/dev/null
python3 "$ROOT/scripts/tests/test-current-head-evidence.py" >/dev/null

python3 - "$ROOT" <<'PY'
import csv
import subprocess
from pathlib import Path
import sys
root = Path(sys.argv[1])
registry = root / "evidence/evidence-index.tsv"
rows = {row["path"]: row for row in csv.DictReader(registry.open(encoding="utf-8"), delimiter="\t")}
for path in root.joinpath("evidence").glob("*.json"):
    relative = path.relative_to(root.parent).as_posix()
    row = rows.get(relative)
    if not row or row.get("class") not in {"canonical-strict", "canonical-diagnostic"}:
        raise SystemExit("evidence lifecycle: unregistered/non-canonical root JSON: " + relative)
for raw in subprocess.check_output(
        ["git", "ls-files", "--others", "--exclude-standard", "--full-name"],
        cwd=root.parent, text=True).splitlines():
    relative = raw.strip()
    if not relative.endswith(".json"):
        continue
    if relative.startswith("lims-recovery/evidence/"):
        row = rows.get(relative)
        if not row or row.get("class") not in {"canonical-strict", "canonical-diagnostic"}:
            raise SystemExit("evidence lifecycle: unregistered root JSON in git status: " + relative)
    elif relative.startswith("lims-recovery/api-contracts/"):
        # Versioned contract inputs are source documents, not runner output;
        # they do not belong in the evidence registry.
        continue
    elif not (relative.startswith(".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/")
              or relative.startswith(".scratch/sunway-supplier-exit-api-first/archive/")):
        raise SystemExit("evidence lifecycle: runner JSON outside managed diagnostic/archive tree: " + relative)
    if relative not in rows:
        raise SystemExit("evidence lifecycle: unregistered JSON in git status: " + relative)
PY

echo 'evidence lifecycle gate PASS'
