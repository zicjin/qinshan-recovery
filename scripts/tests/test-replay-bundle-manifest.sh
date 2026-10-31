#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
python3 - "$ROOT" <<'PY'
from pathlib import Path
import hashlib, json, re, subprocess, sys

root = Path(sys.argv[1])
for name in ("replay-bundle-equipment-use-20261021.json", "replay-bundle-equipment-period-check-20261021.json"):
    path = root / "evidence" / name
    payload = json.loads(path.read_text(encoding="utf-8"))
    if payload.get("production_access") is not False:
        raise SystemExit("replay bundle must be production-read-only: %s" % name)
    if not re.fullmatch(r"[0-9a-f]{7,40}", payload.get("source_commit", "")):
        raise SystemExit("replay bundle source commit is not pinned: %s" % name)
    image = payload.get("oracle_image", "")
    if "@sha256:" not in image or not re.fullmatch(r"[a-z0-9./_-]+@sha256:[0-9a-f]{64}", image):
        raise SystemExit("replay bundle Oracle image is not digest-pinned: %s" % name)
    for command in payload.get("build_commands", []) + payload.get("verification_commands", []):
        if any(secret in command.lower() for secret in ("password=", "secret=", "token=", "private_key")):
            raise SystemExit("replay command contains a credential-shaped argument: %s" % name)
    for relative in payload.get("evidence", []) + [payload["matrix"]]:
        if not (root / relative).is_file():
            raise SystemExit("replay bundle path is missing: %s" % relative)
    if payload.get("expected", {}).get("oracle_error_codes") != []:
        raise SystemExit("replay bundle must require zero Oracle errors: %s" % name)

name = "replay-bundle-equipment-create-browser-20261022.json"
path = root / "evidence" / name
payload = json.loads(path.read_text(encoding="utf-8"))
if payload.get("production_access") is not False:
    raise SystemExit("browser replay bundle must be production-read-only")
source = payload.get("source_commit", "")
if not re.fullmatch(r"[0-9a-f]{7,40}", source):
    raise SystemExit("browser replay bundle source commit is not pinned")
if subprocess.run(["git", "cat-file", "-e", source + "^{commit}"], cwd=root).returncode:
    raise SystemExit("browser replay bundle source commit is absent")
image = payload.get("oracle_image", "")
if not re.fullmatch(r"[a-z0-9./_-]+@sha256:[0-9a-f]{64}", image):
    raise SystemExit("browser replay bundle Oracle image is not digest-pinned")
for command in payload.get("replay_commands", []):
    if any(secret in command.lower() for secret in ("password=", "secret=", "token=", "private_key")):
        raise SystemExit("browser replay command contains a credential-shaped argument")
fixture = root / payload["fixture"]
if not fixture.is_file() or hashlib.sha256(fixture.read_bytes()).hexdigest() != payload.get("fixture_sha256"):
    raise SystemExit("browser replay fixture hash does not match")
evidence_path = root / payload["evidence"]
evidence = json.loads(evidence_path.read_text(encoding="utf-8"))
expected = payload.get("expected", {})
if evidence.get("status") != expected.get("status") or evidence.get("source_commit") != source:
    raise SystemExit("browser replay evidence status/source does not match bundle")
if evidence.get("checks") and len(evidence["checks"]) != expected.get("checks"):
    raise SystemExit("browser replay check count does not match bundle")
if evidence.get("source_dirty") is not expected.get("source_dirty") or evidence.get("production_access") is not expected.get("production_access"):
    raise SystemExit("browser replay source/isolation flags do not match bundle")
if evidence.get("oracle_errors") != expected.get("oracle_errors"):
    raise SystemExit("browser replay Oracle error set does not match bundle")
if evidence.get("baseline", {}).get("projection_sha256") != expected.get("fixture_baseline", {}).get("projection_sha256"):
    raise SystemExit("browser replay baseline hash does not match bundle")
cleanup = evidence.get("cleanup", {})
if any(value is not True for key, value in cleanup.items() if key.endswith(("_removed", "_closed", "_stopped", "_restored"))):
    raise SystemExit("browser replay cleanup is incomplete")
print("browser equipment replay bundle: PASS (source, fixture, evidence, Oracle and cleanup hashes)")

print("replay bundle manifest: PASS (pinned source/image, commands, evidence and cleanup boundary)")
PY
