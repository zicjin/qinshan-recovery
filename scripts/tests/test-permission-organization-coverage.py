#!/usr/bin/env python3
"""Validate the role/organization batch execution without duplicating a shard.

The permission runner executes four roles in two organizations and both write
modes in one Oracle/Redis/browser lifecycle.  The declarative behavior matrix
deliberately keeps contracts separate from that execution shard, so this gate
checks the batch report's complete coverage without adding one matrix row per
HTTP observation.
"""

import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import current_implementation_binding
DEFAULT_REPORT = ROOT.parent / ".scratch/sunway-supplier-exit-api-first/archive/historical/permission-role-organization-20261024-org-refresh.json"
DEFAULT_OUTPUT = ROOT.parent / ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/permission-organization-coverage-20261025.json"
ROLES = ("reader", "manager", "admin", "role-admin")
ORGANIZATIONS = ("org-a", "org-b")
MODES = ("default", "isolated")
BUSINESS_ROLES = {"reader", "manager", "admin"}
MATRIX_ROLES = {"admin", "role-admin"}
WRITE_ROLES = {"manager", "admin"}
BASE = "/secure/emc/module/mdm/equipt/equipts"
BUSINESS_READ = "POST " + BASE + "/queries/raw"
BUSINESS_WRITE = "POST " + BASE
MATRIX = "POST /secure/core/module/sys/menu-roles/queries/matrix-headers"
MENUS = "GET /secure/core/module/sys/menus"
REQUIRED_CLEANUP = (
    "browser_session_closed", "temporary_files_removed", "container_removed",
    "anonymous_volumes_removed", "network_removed",
)


def fail(message):
    raise SystemExit("permission organization coverage: FAIL: " + message)


def git_head():
    return subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT,
                                   text=True).strip()


def pick(cases, role, organization, mode, route, label_fragment=None, phase="baseline"):
    matches = [item for item in cases
               if item.get("role") == role
               and item.get("organization") == organization
               and item.get("mode") == mode
               and item.get("route") == route
               and (phase is None or item.get("phase") == phase)
               and (label_fragment is None or label_fragment in item.get("label", ""))]
    if len(matches) != 1:
        fail("expected one observation for %s/%s/%s %s %r, got %d" %
             (role, organization, mode, route, label_fragment, len(matches)))
    return matches[0]


def check_case(observation, expected_status, case_id, checks, *, suppressed=None):
    actual = observation.get("status")
    if actual != expected_status or observation.get("expected_status") != expected_status:
        fail("%s expected status %s, observed %r" % (case_id, expected_status, observation))
    if suppressed is not None:
        body = observation.get("response_body")
        if suppressed and body not in ("", "null"):
            fail("%s cross-organization body was not suppressed: %r" % (case_id, body))
    checks.append({
        "case_id": case_id,
        "route": observation["route"],
        "role": observation["role"],
        "organization": observation["organization"],
        "mode": observation["mode"],
        "label": observation.get("label", ""),
        "expected_status": expected_status,
        "status": actual,
        "response_suppressed": suppressed,
    })


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--report", type=Path, default=DEFAULT_REPORT)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    args = parser.parse_args()
    report_path = args.report if args.report.is_absolute() else ROOT / args.report
    if not report_path.is_file():
        fail("missing batch report: %s" % report_path)
    report = json.loads(report_path.read_text(encoding="utf-8"))
    if report.get("status") != "executed-pass":
        fail("batch report is not executed-pass")
    if report.get("production_access") is not False:
        fail("batch report does not prove production isolation")
    if report.get("source_dirty") is not False:
        fail("batch report is source-dirty")
    source_commit = report.get("source_commit")
    if not isinstance(source_commit, str) or not re.fullmatch(r"[0-9a-f]{7,40}", source_commit):
        fail("batch report source_commit is malformed")
    if subprocess.run(["git", "cat-file", "-e", source_commit + "^{commit}"], cwd=ROOT,
                      stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode:
        fail("batch report source_commit is not a Git object")
    implementation_digest = report.get("implementation_tree_digest")
    legacy_historical = not implementation_digest
    if implementation_digest:
        current = current_implementation_binding()
        if implementation_digest != current.get("implementation_tree_digest"):
            fail("batch report implementation binding is stale")
        binding = report.get("implementation_binding")
        if isinstance(binding, dict):
            binding = binding.get("binding_id")
        if binding != current.get("binding_id"):
            fail("batch report implementation_binding is stale")
        if report.get("implementation_dirty") is not False or current.get("implementation_dirty"):
            fail("batch report implementation scope is dirty")
    elif source_commit != git_head() and not isinstance(report.get("source_commit_exception"), dict):
        # Legacy reports have no implementation binding and retain the old
        # exact-HEAD rule until explicitly migrated.
        fail("legacy batch report is not current HEAD and has no migration binding")
    cleanup = report.get("cleanup")
    if not isinstance(cleanup, dict) or any(cleanup.get(name) is not True for name in REQUIRED_CLEANUP):
        fail("batch report cleanup proof is incomplete")
    observations = report.get("http_cases")
    if not isinstance(observations, list):
        fail("batch report has no structured http_cases")

    checks = []
    # All eight accounts are exercised in both modes.  This is the baseline
    # role/org matrix, independent of the number of behavior contracts.
    for mode in MODES:
        for role in ROLES:
            for organization in ORGANIZATIONS:
                prefix = "%s-%s-%s" % (role, organization, mode)
                check_case(pick(observations, role, organization, mode,
                                MENUS), 200,
                           prefix + "-menus", checks)
                business = role in BUSINESS_ROLES
                check_case(pick(observations, role, organization, mode,
                                BUSINESS_READ), 200 if business else 403,
                           prefix + "-business-read", checks)
                matrix = mode == "isolated" and role in MATRIX_ROLES
                check_case(pick(observations, role, organization, mode, MATRIX),
                           200 if matrix else 403, prefix + "-matrix-read", checks)
                can_write = mode == "isolated" and role in WRITE_ROLES
                check_case(pick(observations, role, organization, mode, BUSINESS_WRITE,
                                "same organization write gate"), 200 if can_write else 403,
                           prefix + "-same-org-write", checks)
                check_case(pick(observations, role, organization, mode, BUSINESS_WRITE,
                                "foreign category write denied"), 403,
                           prefix + "-foreign-write-denied", checks)
                detail_id = "2" if organization == "org-a" else "1"
                detail = pick(observations, role, organization, mode,
                              "GET " + BASE + "/" + detail_id, "cross organization detail")
                check_case(detail, 200 if business else 403, prefix + "-cross-org-detail",
                           checks, suppressed=business)

    # Every business role/org pair exercises organization revocation, while
    # role-admin is intentionally outside the equipment business scope.
    for role in sorted(BUSINESS_ROLES):
        for organization in ORGANIZATIONS:
            prefix = "%s-%s-isolated" % (role, organization)
            revoked = pick(observations, role, organization, "isolated",
                           BUSINESS_READ, "revoked organization read",
                           phase="organization-revoked")
            check_case(revoked, 200, prefix + "-organization-revoked-read", checks)
            denied = pick(observations, role, organization, "isolated", BUSINESS_WRITE,
                          "revoked organization write denied", phase="organization-revoked")
            check_case(denied, 403, prefix + "-organization-revoked-write", checks)

    # Existing sessions must re-check grant revocation, role suspension,
    # disabled accounts and logout/session invalidation for every role/org pair.
    for role in ROLES:
        for organization in ORGANIZATIONS:
            prefix = "%s-%s-isolated" % (role, organization)
            route = BUSINESS_READ if role in BUSINESS_ROLES else MATRIX
            for suffix, fragment, expected, phase in (
                ("grant-revoked", "revocation rechecked on existing session", 403, "grant-revoked"),
                ("role-suspended", "suspended role denied", 403, "role-suspended"),
                ("user-disabled", "disabled account denied", 403, "user-disabled"),
                ("session-cleared", "reactivation does not restore cleared session", 403, "session-invalidated"),
                ("relogin", "explicit relogin restores authorized read", 200, "session-invalidated"),
                ("logout", "explicit logout", 200, "session-invalidated"),
                ("old-session", "old session denied after logout", 403, "session-invalidated"),
            ):
                lifecycle_route = "POST /core/module/sys/logout" if suffix == "logout" else route
                check_case(pick(observations, role, organization, "isolated", lifecycle_route,
                                fragment, phase=phase), expected,
                           prefix + "-" + suffix, checks)

    audit_events = report.get("audit", {}).get("events", [])
    if not isinstance(audit_events, list) or not any(
            event.get("action") == "authorize" and event.get("outcome") == "DENIED"
            for event in audit_events if isinstance(event, dict)):
        fail("permission-denied audit outcome is missing")
    if not any(
            event.get("action") == "authorize" and event.get("outcome") == "ALLOWED"
            for event in audit_events if isinstance(event, dict)):
        fail("permission-allowed audit outcome is missing")

    output = {
        "schema": 1,
        "status": "coverage-pass",
        "strict_evidence": False,
        "classification": "legacy-historical" if legacy_historical else "implementation-bound-diagnostic",
        "legacy_historical": legacy_historical,
        "production_access": False,
        "scope": "derived role/organization coverage index; no additional runtime execution",
        "source_report": (report_path.relative_to(ROOT.parent).as_posix()
                          if report_path.is_relative_to(ROOT.parent)
                          else report_path.relative_to(ROOT).as_posix()),
        "source_report_sha256": hashlib.sha256(report_path.read_bytes()).hexdigest(),
        "source_commit": source_commit,
        "source_dirty": False,
        "implementation_binding": report.get("implementation_binding"),
        "implementation_tree_digest": report.get("implementation_tree_digest"),
        "implementation_dirty": report.get("implementation_dirty"),
        "source_commit_exception": report.get("source_commit_exception"),
        "runner": report.get("runner"),
        "execution_group": "permission-role-organization",
        "roles": list(ROLES),
        "organizations": list(ORGANIZATIONS),
        "modes": list(MODES),
        "checks": checks,
        "check_count": len(checks),
        "audit": {
            "allowed_authorize_observed": True,
            "denied_authorize_observed": True,
        },
        "cleanup": {name: True for name in REQUIRED_CLEANUP},
        "derived_from_cleanup": cleanup,
        "finished_at": report.get("finished_at"),
    }
    output_path = args.output if args.output.is_absolute() else ROOT / args.output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(output, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("permission organization coverage: PASS (%d observations; 4 roles x 2 organizations x 2 modes)" % len(checks))
    print("coverage report: %s" % output_path)


if __name__ == "__main__":
    main()
