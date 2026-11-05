#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/classes"
cat > "$TMP/Fixture.java" <<'EOF'
public class Fixture { public int value; protected native void call(String x); public String run(int x){ return String.valueOf(x); } }
EOF
javac -d "$TMP/classes" "$TMP/Fixture.java"
python3 - "$ROOT" "$TMP/classes/Fixture.class" <<'PY'
import json, pathlib, sys
sys.path.insert(0, str(pathlib.Path(sys.argv[1]) / 'tools'))
from framework_contract_inventory import class_info
x=class_info(pathlib.Path(sys.argv[2]).read_bytes())
assert x['name']=='Fixture' and x['native_methods'][0]['name']=='call'
assert any(m['name']=='run' for m in x['public_protected_methods'])
assert any(f['name']=='value' for f in x['fields'])
print('fixture contract parser: PASS')
PY
OUT="$TMP/inventory"
python3 - "$TMP" "$TMP/classes/Fixture.class" <<'PY'
import io
import pathlib
import sys
import zipfile

tmp = pathlib.Path(sys.argv[1])
fixture = pathlib.Path(sys.argv[2]).read_bytes()
with zipfile.ZipFile(tmp / "fixture-run.jar", "w") as outer:
    for index in range(18):
        payload = io.BytesIO()
        with zipfile.ZipFile(payload, "w") as nested:
            if index == 0:
                nested.writestr("Fixture.class", fixture)
                nested.writestr("static/fixture.txt", "fixture-resource\n")
        outer.writestr("BOOT-INF/lib/gikam-fixture-%02d.jar" % index, payload.getvalue())
(tmp / "sunway-app.so").write_bytes(b"fixture-native")
PY
python3 "$ROOT/tools/framework_contract_inventory.py" --run-jar "$TMP/fixture-run.jar" --native "$TMP/sunway-app.so" --output-dir "$OUT" > "$TMP/summary"
python3 - "$OUT/summary.json" <<'PY'
import json,sys
x=json.load(open(sys.argv[1])); assert x['jars'] == 18 and x['classes'] > 0 and x['resources'] > 0
assert x['native']['bytes'] > 0
print('run jar contract inventory: PASS', x['jars'], x['classes'], x['resources'])
PY
