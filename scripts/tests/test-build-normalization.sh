#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TOOL="$ROOT/tools/normalize_jar.py"
tmp="$(mktemp -d)"
trap 'mv "$tmp"/outside.jar "$tmp"/outside.jar.done 2>/dev/null || true; rm -rf "$tmp"' EXIT

python3 - "$tmp/a.jar" "$tmp/b.jar" <<'PY'
import sys, zipfile
for path, names in ((sys.argv[1], ("z.txt", "a.txt")), (sys.argv[2], ("a.txt", "z.txt"))):
    with zipfile.ZipFile(path, "w") as z:
        for name in names:
            z.writestr(zipfile.ZipInfo(name, date_time=(2026, 1, 1, 1, 2, 3)), name.encode())
PY
chmod 600 "$tmp/a.jar" "$tmp/b.jar"
python3 "$TOOL" "$tmp/a.jar"
python3 "$TOOL" "$tmp/b.jar"
cmp -s "$tmp/a.jar" "$tmp/b.jar" || {
  echo "build normalization test: normalized archives differ" >&2
  exit 1
}
if unzip -tq "$tmp/a.jar" >/dev/null 2>&1; then :; else
  echo "build normalization test: normalized archive is invalid" >&2
  exit 1
fi
echo "build normalization test PASS"
