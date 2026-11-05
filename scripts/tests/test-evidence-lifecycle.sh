#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
REPO="$(git -C "$ROOT" rev-parse --show-toplevel)"
EVIDENCE_PREFIX="evidence/"
if [ "$REPO" != "$ROOT" ]; then
  EVIDENCE_PREFIX="lims-recovery/evidence/"
fi

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
repo = Path(subprocess.check_output(["git", "-C", str(root), "rev-parse", "--show-toplevel"], text=True).strip())
prefix = "" if repo == root else "lims-recovery/"
registry = root / "evidence/evidence-index.tsv"
rows = {row["path"]: row for row in csv.DictReader(registry.open(encoding="utf-8"), delimiter="\t")}
for path in root.joinpath("evidence").glob("*.json"):
    relative = prefix + path.relative_to(root).as_posix()
    row = rows.get(relative)
    if not row or row.get("class") not in {"canonical-strict", "canonical-diagnostic"}:
        raise SystemExit("evidence lifecycle: unregistered/non-canonical root JSON: " + relative)
for raw in subprocess.check_output(
        ["git", "ls-files", "--others", "--exclude-standard", "--full-name"],
        cwd=Path(subprocess.check_output(["git", "-C", str(root), "rev-parse", "--show-toplevel"], text=True).strip()), text=True).splitlines():
    relative = raw.strip()
    if not relative.endswith(".json"):
        continue
    if relative.startswith("evidence/") or relative.startswith("lims-recovery/evidence/"):
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
    if relative.startswith("evidence/") or relative.startswith("lims-recovery/evidence/"):
        if relative not in rows:
            raise SystemExit("evidence lifecycle: unregistered root JSON in git status: " + relative)
    # Diagnostic and archive outputs are intentionally generated in bulk and
    # are registered/promoted only at a checkpoint boundary.
PY

echo 'evidence lifecycle gate PASS'
