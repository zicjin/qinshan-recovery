#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
CATALOG="$ROOT/evidence/function-coverage-catalog.tsv"
VALIDATOR="$ROOT/tools/validate_function_catalog.py"
bash "$ROOT/scripts/tests/test-api-evidence-gate.sh" >/dev/null
python3 "$VALIDATOR" "$CATALOG" --source-inventory "$ROOT/evidence/t02-source-inventory.tsv" >/dev/null
fixture="$(mktemp -d)"; trap 'rm -rf "$fixture"' EXIT
cp "$CATALOG" "$fixture/catalog.tsv"
printf 'identity.login\tidentity\tduplicate\tx\tx\tx\tx\tx\tx\tx\tx\tx\tevidence/x.md\tunknown\tstatic-present\tlow\tx\n' >> "$fixture/catalog.tsv"
if python3 "$VALIDATOR" "$fixture/catalog.tsv" >/dev/null 2>&1; then echo 'catalog test: duplicate feature accepted' >&2; exit 1; fi
sed '0,/evidence\/[^;]*/s//& password=oops/' "$CATALOG" > "$fixture/secret.tsv"
if python3 "$VALIDATOR" "$fixture/secret.tsv" >/dev/null 2>&1; then echo 'catalog test: secret material accepted' >&2; exit 1; fi
python3 - "$CATALOG" "$fixture/candidate.tsv" <<'PY'
import csv,sys
with open(sys.argv[1],newline='',encoding='utf-8') as f: rows=list(csv.reader(f,delimiter='\t'))
rows[1][6]='FakeResource'
with open(sys.argv[2],'w',newline='',encoding='utf-8') as f: csv.writer(f,delimiter='\t',lineterminator='\n').writerows(rows)
PY
if python3 "$VALIDATOR" "$fixture/candidate.tsv" >/dev/null 2>&1; then echo 'catalog test: invented mapping accepted' >&2; exit 1; fi
# An evidence-backed runtime result must be representable; historical
# evidence alone must never promote the owned runtime.
python3 - "$CATALOG" "$fixture/runtime.tsv" "$fixture/historical-promotion.tsv" <<'PY'
import csv,sys
with open(sys.argv[1],newline='',encoding='utf-8') as f: reader=csv.DictReader(f,delimiter='\t'); fields=reader.fieldnames; rows=list(reader)
rows[0]['status']='read-flow-verified'; rows[0]['state_history']+=';read-flow-verified'
rows[0]['provenance']='historical/vendor evidence only'
with open(sys.argv[3],'w',newline='',encoding='utf-8') as f: w=csv.DictWriter(f,fieldnames=fields,delimiter='\t',lineterminator='\n');w.writeheader();w.writerows(rows)
rows[0]['provenance']+='; owned-runtime-evidence: evidence/personnel-identity-security.md'
with open(sys.argv[2],'w',newline='',encoding='utf-8') as f: w=csv.DictWriter(f,fieldnames=fields,delimiter='\t',lineterminator='\n');w.writeheader();w.writerows(rows)
PY
if python3 "$VALIDATOR" "$fixture/historical-promotion.tsv" >/dev/null 2>&1; then echo 'catalog test: historical evidence promoted owned status' >&2; exit 1; fi
python3 "$VALIDATOR" "$fixture/runtime.tsv" >/dev/null
python3 "$ROOT/scripts/tests/test-candidate-closure-list.py" >/dev/null
echo 'function catalog test PASS'
