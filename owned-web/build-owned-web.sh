#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/../project}"
OUT="${1:-$ROOT/target}"
ORIGINAL="$ROOT/src/main/resources/frontend-original"
rm -rf "$OUT"
mkdir -p "$OUT/static" "$OUT/templates"
if [ -d "$PROJECT_ROOT/src/main/resources/static" ]; then
  cp -R "$PROJECT_ROOT/src/main/resources/static/." "$OUT/static/"
fi
if [ -d "$PROJECT_ROOT/src/main/resources/templates" ]; then
  cp -R "$PROJECT_ROOT/src/main/resources/templates/." "$OUT/templates/"
fi
if [ ! -d "$ORIGINAL/static" ] || [ ! -f "$ORIGINAL/index.html" ] || [ ! -f "$ORIGINAL/workspace.html" ] || [ ! -f "$ORIGINAL/provenance.tsv" ]; then
  echo 'missing imported original frontend overlay' >&2
  exit 1
fi
# Original supplier frontend is the default shell.  Minified bundles are still
# frontend code and are copied verbatim from the read-only sss2 capture.
cp -R "$ORIGINAL/static/." "$OUT/static/"
# The source-owned `static/` tree contains historical compatibility shell
# material. It is intentionally excluded from the default distribution so it
# cannot overwrite the imported supplier Gikam/LCDP resources or root index.
if [ -d "$ROOT/src/main/resources/templates" ]; then
  cp -R "$ROOT/src/main/resources/templates/." "$OUT/templates/"
fi
cp "$ORIGINAL/index.html" "$OUT/index.html"
cp "$ORIGINAL/index.html" "$OUT/static/index.html"
cp "$ORIGINAL/workspace.html" "$OUT/static/workspace.html"
python3 - "$OUT" "$ORIGINAL/provenance.tsv" <<'PY'
import hashlib, json, pathlib, re, sys
out=pathlib.Path(sys.argv[1]); provenance=pathlib.Path(sys.argv[2])
files=sorted(p for p in out.rglob('*') if p.is_file() and p.name not in {'manifest.json','forbidden-content-findings.json'})
# Contextual scan: generic UI/business words such as "license" are allowed;
# runtime activation, MAC binding, native loader and license-file paths are not.
patterns=(
    r'sunway-app\\.so', r'license\\.lic', r'vendor\\.lic',
    r'(?:sunway|gikam).{0,48}(?:mac.?binding|machine.?fingerprint|native.?loader|jni)',
    r'System\\.loadLibrary', r'JNI_OnLoad',
    r'/open/security/licenses', r'/core/module/sys/licenses',
)
forbidden=re.compile('|'.join(patterns), re.I)
findings=[]
for path in files:
    if path.suffix.lower() in {'.so','.lic'}:
        findings.append({'path':str(path.relative_to(out)),'reason':'forbidden native/license file suffix'})
        continue
    text=path.read_bytes().decode('utf-8','ignore')
    matches=sorted(set(m.group(0) for m in forbidden.finditer(text)))
    if matches:
        findings.append({'path':str(path.relative_to(out)),'matches':matches,'reason':'runtime license/native loader reference'})
if findings:
    (out/'forbidden-content-findings.json').write_text(json.dumps({'schema':1,'findings':findings},indent=2)+'\n')
    raise SystemExit('owned-web runtime contains forbidden license/native inputs; see forbidden-content-findings.json')
manifest={str(p.relative_to(out)): {'sha256': hashlib.sha256(p.read_bytes()).hexdigest(), 'size': p.stat().st_size} for p in files}
lines=provenance.read_text().splitlines()
header=lines[0].split('\t') if lines else []
source_rows=[dict(zip(header,line.split('\t'))) for line in lines[1:] if line.strip()]
source_provenance={'schema':1,'source_host':'sss2','source_port':18081,'production_access':False,'resources':source_rows}
(out/'manifest.json').write_text(json.dumps({
    'schema':3,
    'runtime':'owned-web',
    'vendor_runtime':False,
    'frontend_default':'original-supplier-runtime-bundle-import',
    'original_frontend_imported':True,
    'original_project_static_included':True,
    'original_project_template_included':True,
    'license_native_cleaned':True,
    'api_dependency_closure':'pending',
    'boundary_process':False,
    'internal_http_bridge':False,
    'source_provenance':source_provenance,
    'files':manifest,
},indent=2)+'\n')
PY
JAR_BIN="${JAR_BIN:-${JAVA_HOME:+$JAVA_HOME/bin/}jar}"
"$JAR_BIN" cf "$OUT/lims-owned-web-1.0.0.jar" -C "$OUT" static -C "$OUT" templates
if find "$OUT/static" -type f ! -path '*/emc/*' ! -path '*/gikam/*' ! -path '*/module/*' ! -path '*/plugin/*' ! -path '*/pageoffice/*' ! -path '*/workspace-panels/*' ! -path '*/owned-web/*' ! -path '*/index.html' ! -path '*/workspace.html' -print -quit | grep -q .; then
  echo 'unexpected static runtime asset' >&2
  exit 1
fi
if find "$OUT/static/owned-web" -type f \( -name 'app.js' -o -name 'owned.css' -o -name 'owned-gikam.js' \) -print -quit 2>/dev/null | grep -q .; then
  echo 'simplified owned workspace shell entered the default distribution' >&2
  exit 1
fi
echo "owned-web build passed: $OUT"
