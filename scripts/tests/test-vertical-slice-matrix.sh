#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PYTHONPATH="$ROOT/tools${PYTHONPATH:+:$PYTHONPATH}" python3 - "$ROOT" <<'PY'
from pathlib import Path
from dataclasses import replace
import json
import re
import subprocess
import sys
import tempfile
from vertical_slices.matrix import load_matrix, load_runner_registry, validate_evidence

root = Path(sys.argv[1])
cases = load_matrix(root.parent / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-matrix.tsv")
registry = load_runner_registry(root.parent / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv")
counts = validate_evidence(cases, root, registry, root.parent)
if counts["cases"] != 92 or counts["pass"] != counts["cases"]:
    raise SystemExit("unexpected matrix counts: %s" % counts)

# A refreshed evidence revision replaces a case report; it must not add a second
# behavior contract with the same route, actor, scope, mode, expected result,
# audit and cleanup semantics.
contracts = {}
for case in cases:
    contract = (case.route, case.role, case.organization, case.mode, case.seed,
                case.expected_result, case.audit, case.cleanup)
    if contract in contracts:
        raise ValueError("duplicate behavior contract: %s and %s" %
                         (contracts[contract], case.case_id))
    contracts[contract] = case.case_id

# Every evidence revision has an explicit status. Historical and diagnostic
# reports remain useful, but they cannot silently count as current clean
# source acceptance; strict registrations must be current-strict.
status_rows = (root.parent / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-evidence-status.tsv").read_text(encoding="utf-8").splitlines()
if not status_rows or status_rows[0].split("\t") != ["evidence", "classification", "rationale"]:
    raise ValueError("evidence status table has no declared interface")
evidence_status = {}
for line in status_rows[1:]:
    if not line.strip():
        continue
    values = line.split("\t")
    if len(values) != 3 or values[0] in evidence_status:
        raise ValueError("evidence status row is malformed or duplicated")
    if values[1] not in {"current-strict", "legacy-clean", "historical", "diagnostic"}:
        raise ValueError("unsupported evidence classification: " + values[1])
    evidence_status[values[0]] = values[1]
if set(evidence_status) != {case.evidence for case in cases}:
    raise ValueError("evidence status table does not exactly cover registered evidence")
for case in cases:
    evidence_path = root.parent / case.evidence if case.evidence.startswith(".scratch/") else root / case.evidence
    payload = json.loads(evidence_path.read_text(encoding="utf-8"))
    classification = evidence_status[case.evidence]
    contract = registry.get(case.case_id)
    if contract and contract.contract == "strict" and classification != "current-strict":
        raise ValueError("strict case is not current-strict: " + case.case_id)
    if payload.get("source_dirty") is True and classification not in {"historical", "diagnostic"}:
        raise ValueError("dirty evidence is classified as current: " + case.evidence)
    if payload.get("source_dirty") is False and classification in {"historical", "diagnostic"}:
        raise ValueError("clean evidence is classified as historical/diagnostic: " + case.evidence)

# Every registered report must identify the exact Git object that produced it.
# Historical reports may point at an older object, but they must never omit or
# falsify source identity; newer runners additionally expose observed HTTP cases.
for case in cases:
    evidence_path = root.parent / case.evidence if case.evidence.startswith(".scratch/") else root / case.evidence
    payload = json.loads(evidence_path.read_text(encoding="utf-8"))
    source_commit = payload.get("source_commit")
    if not isinstance(source_commit, str) or not re.fullmatch(r"[0-9a-f]{7,40}", source_commit):
        raise ValueError("evidence source_commit is invalid for %s" % case.case_id)
    # Historical matrix evidence may have been produced by the parent
    # planning checkout, while current standalone evidence is produced by the
    # lims-recovery repository itself.  Preserve the exact source object
    # requirement, but look in both repository boundaries instead of treating
    # a valid parent provenance commit as missing merely because it is not in
    # the standalone object database.
    commit_present = False
    for repository in (root, root.parent):
        if subprocess.run(["git", "cat-file", "-e", source_commit + "^{commit}"], cwd=repository,
                          stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0:
            commit_present = True
            break
    if not commit_present:
        raise ValueError("evidence source_commit is not present for %s" % case.case_id)
    if not isinstance(payload.get("source_dirty"), bool):
        raise ValueError("evidence source_dirty must be boolean for %s" % case.case_id)

def rejected(action, label):
    try:
        action()
    except ValueError:
        return
    raise AssertionError(label)

browser = next(case for case in cases if case.case_id == "equipment-create-browser")
for changed in (replace(browser, route=browser.route.replace("equipts", "equips")),
                replace(browser, role="low-permission"), replace(browser, mode="default")):
    rejected(lambda: validate_evidence([changed], root), "unobserved route/role/mode accepted")
duplicate = replace(browser, case_id="duplicate-equipment-browser")
try:
    duplicate_contract = (duplicate.route, duplicate.role, duplicate.organization, duplicate.mode,
                          duplicate.seed, duplicate.expected_result, duplicate.audit, duplicate.cleanup)
    if duplicate_contract in contracts:
        raise ValueError("duplicate behavior contract")
except ValueError:
    pass
else:
    raise AssertionError("duplicate behavior contract accepted")
with tempfile.TemporaryDirectory(prefix="vertical-matrix-negative-") as temporary:
    temporary = Path(temporary)
    for malformed in ("GET /ok;garbage", "GET /ok;POST relative"):
        fixture = temporary / "matrix.tsv"
        fixture.write_text((root.parent / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-matrix.tsv").read_text().replace(browser.route, malformed))
        rejected(lambda: load_matrix(fixture), "malformed secondary route accepted")
    browser_evidence_path = root.parent / browser.evidence if browser.evidence.startswith(".scratch/") else root / browser.evidence
    payload = json.loads(browser_evidence_path.read_text())
    payload["cleanup"]["container_removed"] = False
    (temporary / "failed.json").write_text(json.dumps(payload))
    rejected(lambda: validate_evidence([replace(browser, evidence="failed.json")], temporary),
             "failed cleanup accepted")

# Legacy reports are historical fallback only. A strict registration without
# implementation binding must be rejected even with source_commit_exception;
# a stale implementation digest must also be rejected before HTTP checks.
from vertical_slices.matrix import MatrixCase, RunnerRegistration, _validate_strict_evidence
strict_case = MatrixCase("binding-negative", "GET /ok", "reader", "org-a", "default", "fixture", "allowed", "audit", "cleanup", "x.json")
strict_registration = RunnerRegistration("binding-negative", "runner", ("owned-framework/src",), "strict")
head = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root, text=True).strip()
legacy_payload = {
    "runner": "runner", "source_commit": head, "source_dirty": False,
    "source_commit_exception": {"reason": "historical fallback"},
}
rejected(lambda: _validate_strict_evidence(strict_case, legacy_payload, strict_registration,
                                           root, root.parent),
         "legacy strict evidence without implementation binding accepted")
stale_payload = {
    "runner": "runner", "source_commit": head, "source_dirty": False,
    "implementation_tree_digest": "0" * 64, "implementation_binding": "impl-0000000000000000",
}
rejected(lambda: _validate_strict_evidence(strict_case, stale_payload, strict_registration,
                                           root, root.parent),
         "stale implementation binding accepted")
print("vertical slice matrix: PASS (%d cases; evidence, cleanup and source identity valid)" % counts["cases"])
print("vertical slice matrix negative controls: PASS (route, role, mode, syntax and cleanup)")
PY
