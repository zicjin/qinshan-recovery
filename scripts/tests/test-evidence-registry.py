#!/usr/bin/env python3
import csv
import contextlib
import io
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))
from source_binding import REPO_ROOT, is_standalone_root
EVIDENCE_PREFIX = "evidence/" if is_standalone_root(REPO_ROOT) else "lims-recovery/evidence/"
REGISTRY = ROOT / "evidence" / "evidence-index.tsv"
SPEC = __import__("importlib.util").util.spec_from_file_location("manage_evidence", ROOT / "scripts" / "manage-evidence.py")
MANAGE = __import__("importlib.util").util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MANAGE)


class EvidenceRegistryTest(unittest.TestCase):
    def test_registry_exposes_dependency_and_fingerprint_view_fields(self):
        required = {
            "application_binding", "harness_binding", "runtime_contract_binding",
            "planning_binding", "governance_binding", "family_fingerprint",
            "stimulus_fingerprint", "response_fingerprint", "validation_mode",
            "stale_reason", "batch_id",
        }
        self.assertTrue(required.issubset(set(MANAGE.REGISTRY_FIELDS)))

    def test_registry_path_guard_rejects_escape(self):
        self.assertFalse(MANAGE.inside(MANAGE.EVIDENCE.parent / "outside.json", MANAGE.EVIDENCE))

    def test_classify_does_not_materialize_external_scratch_registry(self):
        with tempfile.TemporaryDirectory(prefix="evidence-classify-standalone-") as temporary:
            base = Path(temporary)
            evidence = base / "evidence"
            feature = base / "parent" / ".scratch" / "sunway-supplier-exit-api-first"
            diagnostic = feature / "diagnostic-evidence"
            archive = feature / "archive"
            external = diagnostic / "pending" / "old.json"
            external.parent.mkdir(parents=True)
            external.write_text(json.dumps({"status": "diagnostic-pass"}) + "\n", encoding="utf-8")
            registry = evidence / "evidence-index.tsv"
            patched = mock.patch.multiple(
                MANAGE, ROOT=base, EVIDENCE=evidence, FEATURE=feature,
                DIAGNOSTIC=diagnostic, ARCHIVE=archive, REGISTRY=registry,
                head=mock.Mock(return_value="fixture-head"),
                scan_rows=mock.Mock(return_value=[{
                    "path": ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/old.json",
                    "class": "diagnostic-failed",
                }]),
            )
            with patched:
                output = io.StringIO()
                with contextlib.redirect_stdout(output):
                    self.assertEqual(MANAGE.cmd_classify(type("Args", (), {})()), 0)
            self.assertFalse(registry.exists())
            self.assertFalse(evidence.exists())
            self.assertIn('"ignored_external_rows": 1', output.getvalue())

    def test_promoted_report_uses_canonical_closure_reference(self):
        pending = MANAGE.FEATURE / "diagnostic-evidence/pending/closure.json"
        canonical = MANAGE.EVIDENCE / "closure.json"
        self.assertEqual(MANAGE.canonical_closure_reference(pending),
                         EVIDENCE_PREFIX + "closure.json")
        self.assertEqual(MANAGE.canonical_closure_reference(canonical),
                         EVIDENCE_PREFIX + "closure.json")

    def test_duplicate_primary_prefers_archived_audit_copy(self):
        paths = [
            ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/run/report.json",
            ".scratch/sunway-supplier-exit-api-first/archive/historical/report.json",
        ]
        self.assertEqual(MANAGE.duplicate_primary(paths), paths[1])

    def test_plan_proposes_root_migration_without_mutating_files(self):
        with tempfile.TemporaryDirectory(prefix="evidence-plan-") as temporary:
            base = Path(temporary)
            evidence = base / "evidence"
            feature = base / "feature"
            diagnostic = feature / "diagnostic-evidence"
            archive = feature / "archive"
            registry = evidence / "evidence-index.tsv"
            source = evidence / "api-contract-failed.json"
            evidence.mkdir(parents=True)
            source.write_text(json.dumps({
                "status": "executed-fail", "source_commit": "old",
                "source_dirty": False, "strict_evidence": True,
            }) + "\n", encoding="utf-8")
            row = {
                "path": str(source.relative_to(base)), "class": "diagnostic-failed",
                "status": "executed-fail", "source_commit": "old", "source_dirty": "false",
                "strict_evidence": "true", "ticket": "09", "closure": "",
                "sha256": MANAGE.sha256(source), "retention": "180d", "reason": "failed",
                "referenced_by": "", "generated_at": "2026-01-01T00:00:00+00:00", "superseded_by": "-",
            }
            patched = mock.patch.multiple(MANAGE, ROOT=base, EVIDENCE=evidence,
                                          FEATURE=feature, DIAGNOSTIC=diagnostic,
                                          ARCHIVE=archive, REGISTRY=registry)
            with patched:
                target, action = MANAGE.planned_destination(row, set())
            self.assertEqual(action, "move-to-diagnostic:failed or incomplete report must stay diagnostic")
            self.assertTrue(target.endswith("feature/diagnostic-evidence/failed/api-contract-failed.json"))
            self.assertTrue(source.exists())

    def test_registry_contract_rejects_diagnostic_claiming_strict(self):
        violations = MANAGE.registry_contract_violations({
            "lims-recovery/evidence/report.json": {
                "class": "canonical-diagnostic", "strict_evidence": "true",
            }
        }, "current")
        self.assertEqual(len(violations), 1)
        self.assertIn("strict_evidence", violations[0])

    def test_strict_closure_contract_rejects_diagnostic_input(self):
        with mock.patch.object(MANAGE, "current_source_clean", return_value=True):
            ok, reason = MANAGE.strict_closure_contract({
            "source_commit": "current", "source_dirty": False,
            "strict_pass_count": 1, "failures": [],
            "route_execution_model": {
                "boundary_process": False, "internal_http_bridge": False,
                "jni_replacement_process": False,
            },
            "reports": [{
                "path": "pending.json", "status": "diagnostic-pass",
                "strict_evidence": False, "source_commit": "current",
                "source_dirty": False, "strict_contract": "diagnostic only",
            }],
            }, "current")
        self.assertFalse(ok)
        self.assertIn("diagnostic", reason)

    def test_registry_report_closure_link_uses_behavior_surfaces(self):
        bindings = {"application_binding": "app", "harness_binding": "harness", "planning_binding": "plan"}
        report = {**bindings, "class": "canonical-strict", "strict_evidence": "true",
                  "closure": "evidence/api-route-closure.json", "implementation_tree_digest": "0" * 64}
        closure = {**bindings, "class": "canonical-strict", "strict_evidence": "true",
                   "implementation_tree_digest": "1" * 64, "implementation_dirty": "false"}
        registry = {"evidence/report.json": report, "evidence/api-route-closure.json": closure}
        self.assertFalse(MANAGE.registry_contract_violations(registry, "new-head"))
        closure["application_binding"] = "stale"
        self.assertTrue(MANAGE.registry_contract_violations(registry, "new-head"))
        closure["application_binding"] = "app"
        closure["implementation_dirty"] = "true"
        self.assertTrue(MANAGE.registry_contract_violations(registry, "new-head"))
        closure["implementation_dirty"] = "false"
        del report["planning_binding"]
        self.assertTrue(MANAGE.registry_contract_violations(registry, "new-head"))

    def test_strict_closure_rejects_local_storage_evidence(self):
        ok, reason = MANAGE.closure_report_registry_valid({
            "reports": [{
                "path": "evidence/report.json",
                "strict_evidence": True,
                "implementation_dirty": False,
            }],
        }, {
            "evidence/report.json": {
                "class": "canonical-strict",
                "storage": "local",
                "implementation_dirty": "false",
            },
        }, "current")
        self.assertFalse(ok)
        self.assertIn("storage=git", reason)

    def test_closure_preserves_captured_runtime_and_governance_provenance(self):
        surfaces = {"application_binding": "app-current", "harness_binding": "harness-current",
                    "planning_binding": "plan-current"}
        item = {**surfaces, "path": "evidence/report.json", "strict_evidence": True,
                "implementation_dirty": False, "implementation_binding": "impl-captured",
                "implementation_tree_digest": "0" * 64, "runtime_contract_binding": "rt-milestone",
                "governance_binding": "gov-captured"}
        closure = {**surfaces, "runtime_contract_binding": "rt-default",
                   "governance_binding": "gov-new", "implementation_binding": "impl-new",
                   "implementation_tree_digest": "1" * 64, "reports": [item]}
        row = {**{k: v for k, v in item.items() if k.endswith("_binding")},
               "implementation_tree_digest": "0" * 64, "class": "canonical-strict",
               "storage": "git", "implementation_dirty": "false"}
        self.assertTrue(MANAGE.closure_reports_current(closure, "new-head"))
        ok, reason = MANAGE.closure_report_registry_valid(closure, {"evidence/report.json": row}, "new-head")
        self.assertTrue(ok, reason)
        row["runtime_contract_binding"] = "rt-other"
        ok, reason = MANAGE.closure_report_registry_valid(closure, {"evidence/report.json": row}, "new-head")
        self.assertFalse(ok)
        self.assertIn("binding", reason)
        row["runtime_contract_binding"] = "rt-milestone"
        row["implementation_tree_digest"] = "2" * 64
        ok, reason = MANAGE.closure_report_registry_valid(closure, {"evidence/report.json": row}, "new-head")
        self.assertFalse(ok)
        row["implementation_tree_digest"] = "0" * 64
        for field in ("implementation_binding", "implementation_tree_digest"):
            saved_item, saved_row = item.pop(field), row.pop(field)
            self.assertFalse(MANAGE.closure_reports_current(closure, "new-head"))
            ok, reason = MANAGE.closure_report_registry_valid(closure, {"evidence/report.json": row}, "new-head")
            self.assertFalse(ok)
            item[field], row[field] = saved_item, saved_row
        for surface in surfaces:
            with self.subTest(surface=surface):
                item[surface] = "stale"
                self.assertFalse(MANAGE.closure_reports_current(closure, "new-head"))
                item[surface] = surfaces[surface]

    def test_strict_contract_rejects_ancestor_even_when_scope_is_unchanged(self):
        payload = {
            "status": "executed-pass",
            "strict_evidence": True,
            "production_access": False,
            "source_dirty": False,
            "source_scope": [],
            "cleanup": {key: True for key in MANAGE.REQUIRED_CLEANUP},
            "route_execution_model": {
                "boundary_process": False,
                "internal_http_bridge": False,
                "jni_replacement_process": False,
            },
            "cases": [{
                "status": 200,
                "route": {"method": "GET", "path": "/health"},
                "fixture": {},
                "audit": "not-required",
            }],
        }
        row = {"source_commit": "ancestor", "source_dirty": "false", "strict_evidence": "true"}
        with mock.patch.object(MANAGE, "source_scope_unchanged", return_value=True):
            ok, reason = MANAGE.strict_contract(row, payload, "current-head")
        self.assertFalse(ok)
        self.assertIn("current HEAD", reason)

    def test_current_source_clean_rejects_ancestor_even_when_scope_is_unchanged(self):
        payload = {"source_commit": "ancestor", "source_dirty": False}
        with mock.patch.object(MANAGE, "source_scope_unchanged", return_value=True):
            self.assertFalse(MANAGE.current_source_clean(payload, "current-head"))

    def test_authoritative_files_reject_historical_report_references(self):
        with tempfile.TemporaryDirectory() as temporary:
            evidence = Path(temporary)
            (evidence / "vertical-slice-matrix.tsv").write_text(
                "report=api-route-closure-old.json\n", encoding="utf-8"
            )
            with mock.patch.object(MANAGE, "EVIDENCE", evidence):
                violations = MANAGE.authoritative_reference_violations({
                    "lims-recovery/evidence/api-route-closure-old.json": {
                        "class": "historical-superseded"
                    }
                })
            self.assertEqual(len(violations), 1)
            self.assertIn("historical-superseded", violations[0])

    def test_reference_index_does_not_pin_every_duplicate_basename(self):
        with tempfile.TemporaryDirectory(prefix="evidence-refs-") as temporary:
            base = Path(temporary)
            evidence = base / "evidence"
            feature = base / "feature"
            first = feature / "pending" / "a" / "api-route-ledger-current-head.json"
            second = feature / "pending" / "b" / "api-route-ledger-current-head.json"
            source = base / "docs.md"
            for path in (first, second):
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text("{}\n", encoding="utf-8")
            source.write_text("use api-route-ledger-current-head.json\n", encoding="utf-8")
            with mock.patch.multiple(MANAGE, ROOT=base, EVIDENCE=evidence,
                                     FEATURE=feature, DIAGNOSTIC=feature / "diagnostic-evidence",
                                     ARCHIVE=feature / "archive"):
                refs = MANAGE.reference_index([first, second])
            self.assertEqual(refs[first.relative_to(base).as_posix()], [])
            self.assertEqual(refs[second.relative_to(base).as_posix()], [])

    def test_strict_closure_rejects_diagnostic_report_inputs(self):
        closure = {
            "reports": [{
                "status": "executed-fail",
                "strict_evidence": True,
                "strict_contract": "report status is not executed-pass",
            }]
        }
        self.assertFalse(MANAGE.closure_reports_strict(closure))

    def test_registry_is_current_and_complete(self):
        if not REGISTRY.is_file():
            # The standalone deploy tree may have no published evidence at all;
            # historical rows live in the parent archive registry.  In that
            # state the evidence directory itself is optional; a clean clone
            # must not be forced to materialize a parent-workspace path.
            self.assertFalse((ROOT / "evidence").exists())
            return
        result = subprocess.run([sys.executable, str(ROOT / "scripts" / "manage-evidence.py"), "verify"], cwd=ROOT.parent, text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        with REGISTRY.open(encoding="utf-8", newline="") as handle:
            rows = list(csv.DictReader(handle, delimiter="\t"))
        # A clean standalone checkout may have no promoted canonical report yet;
        # the registry header remains present and historical/artifact rows live
        # in the parent evidence archive.
        if not rows:
            with REGISTRY.open(encoding="utf-8") as registry_handle:
                self.assertEqual(set(next(csv.reader(registry_handle, delimiter="\t"))),
                                 set(MANAGE.REGISTRY_FIELDS))
            return
        self.assertTrue(all(row.get("path") and row.get("class") and row.get("sha256") for row in rows))
        self.assertTrue(all(row.get("class") in {
            "canonical-strict", "canonical-diagnostic", "diagnostic-failed",
            "historical-superseded", "runtime-lifecycle", "duplicate-or-unreferenced",
        } for row in rows))
        fields = set(rows[0])
        self.assertIn("referenced_by", fields)
        self.assertIn("generated_at", fields)
        for row in rows:
            references = [value for value in row.get("referenced_by", "").split(";") if value]
            self.assertEqual(references, sorted(set(references)), row["path"])
        root_rows = [row for row in rows if row["path"].startswith("lims-recovery/evidence/") and row["path"].endswith(".json")]
        self.assertTrue(all(row["class"] in {"canonical-strict", "canonical-diagnostic"} for row in root_rows))

    def test_prune_expires_only_unreferenced_noncanonical_material(self):
        with tempfile.TemporaryDirectory(prefix="evidence-prune-") as temporary:
            base = Path(temporary)
            evidence = base / "evidence"
            feature = base / "feature"
            diagnostic = feature / "diagnostic-evidence"
            archive = feature / "archive"
            registry = evidence / "evidence-index.tsv"
            expired = diagnostic / "failed.json"
            retained = diagnostic / "retained.json"
            canonical_diagnostic = diagnostic / "canonical-diagnostic.json"
            evidence.mkdir(parents=True)
            expired.parent.mkdir(parents=True)
            expired.write_text(json.dumps({"status": "failed"}) + "\n", encoding="utf-8")
            retained.write_text(json.dumps({"status": "failed"}) + "\n", encoding="utf-8")
            canonical_diagnostic.write_text(json.dumps({"status": "diagnostic-pass"}) + "\n", encoding="utf-8")
            old = expired.stat().st_mtime - 200 * 86400
            import os
            os.utime(expired, (old, old))
            fields = MANAGE.REGISTRY_FIELDS
            rows = [
                {"path": str(expired.relative_to(base)), "class": "diagnostic-failed", "status": "failed",
                 "source_commit": "unknown", "source_dirty": "unknown", "strict_evidence": "false",
                 "ticket": "08", "closure": "", "sha256": MANAGE.sha256(expired), "retention": "180d",
                 "reason": "failed", "referenced_by": "", "generated_at": "2026-01-01T00:00:00+00:00", "superseded_by": "-"},
                {"path": str(retained.relative_to(base)), "class": "diagnostic-failed", "status": "failed",
                 "source_commit": "unknown", "source_dirty": "unknown", "strict_evidence": "false",
                 "ticket": "08", "closure": "", "sha256": MANAGE.sha256(retained), "retention": "180d",
                 "reason": "failed", "referenced_by": "", "generated_at": "2026-10-28T00:00:00+00:00", "superseded_by": "-"},
                {"path": str(canonical_diagnostic.relative_to(base)), "class": "canonical-diagnostic", "status": "diagnostic-pass",
                 "source_commit": "current", "source_dirty": "false", "strict_evidence": "false",
                 "ticket": "09", "closure": "", "sha256": MANAGE.sha256(canonical_diagnostic), "retention": "1d",
                 "reason": "current diagnostic", "referenced_by": "", "generated_at": "2026-01-01T00:00:00+00:00", "superseded_by": "-"},
            ]
            MANAGE.write_tsv(registry, rows, fields)
            patched = mock.patch.multiple(MANAGE, ROOT=base, EVIDENCE=evidence,
                                          FEATURE=feature, DIAGNOSTIC=diagnostic,
                                          ARCHIVE=archive, REGISTRY=registry)
            with patched:
                dry_output = io.StringIO()
                with contextlib.redirect_stdout(dry_output):
                    self.assertEqual(MANAGE.cmd_prune(type("Args", (), {"apply": False, "dry_run": False})()), 0)
                self.assertTrue(expired.exists())
                self.assertIn("would-delete", dry_output.getvalue())
                with contextlib.redirect_stdout(io.StringIO()):
                    self.assertEqual(MANAGE.cmd_prune(type("Args", (), {"apply": True, "dry_run": False})()), 0)
            self.assertFalse(expired.exists())
            self.assertTrue(retained.exists())
            self.assertTrue(canonical_diagnostic.exists())
            self.assertTrue((archive / "evidence-prune-audit.tsv").is_file())
            remaining = {row["path"] for row in MANAGE.load_registry(registry)}
            self.assertNotIn(str(expired.relative_to(base)), remaining)
            self.assertIn(str(retained.relative_to(base)), remaining)

    def test_archive_is_dry_run_by_default_and_does_not_overwrite(self):
        with tempfile.TemporaryDirectory(prefix="evidence-archive-") as temporary:
            base = Path(temporary)
            evidence = base / "evidence"
            feature = base / "feature"
            diagnostic = feature / "diagnostic-evidence"
            archive = feature / "archive"
            registry = evidence / "evidence-index.tsv"
            source = evidence / "report.json"
            existing = archive / "historical" / "report.json"
            evidence.mkdir(parents=True)
            existing.parent.mkdir(parents=True)
            source.write_text(json.dumps({"status": "failed", "value": "new"}) + "\n", encoding="utf-8")
            existing.write_text(json.dumps({"status": "failed", "value": "old"}) + "\n", encoding="utf-8")
            row = {"path": str(source.relative_to(base)), "class": "historical-superseded", "status": "failed",
                   "source_commit": "old", "source_dirty": "false", "strict_evidence": "false",
                   "ticket": "09", "closure": "", "sha256": MANAGE.sha256(source), "retention": "permanent-archive",
                   "reason": "stale", "referenced_by": "", "generated_at": "2026-01-01T00:00:00+00:00", "superseded_by": "-"}
            evidence.mkdir(exist_ok=True)
            MANAGE.write_tsv(registry, [row], MANAGE.REGISTRY_FIELDS)
            patched = mock.patch.multiple(MANAGE, ROOT=base, EVIDENCE=evidence,
                                          FEATURE=feature, DIAGNOSTIC=diagnostic,
                                          ARCHIVE=archive, REGISTRY=registry)
            args = type("Args", (), {"path": [row["path"]], "apply": False, "dry_run": False,
                                     "demote_canonical": False, "references_updated": False})()
            with patched:
                output = io.StringIO()
                with contextlib.redirect_stdout(output):
                    self.assertEqual(MANAGE.cmd_archive(args), 0)
                self.assertTrue(source.exists())
                self.assertTrue(existing.exists())
                self.assertIn('"dry_run": true', output.getvalue())

                args.apply = True
                with contextlib.redirect_stdout(io.StringIO()):
                    self.assertEqual(MANAGE.cmd_archive(args), 0)
            self.assertFalse(source.exists())
            # The source hash is retained in the move audit even after the
            # source is moved; locate the deterministic suffix without relying
            # on the pre-existing destination contents.
            candidates = list((archive / "historical").glob("report--*.json"))
            self.assertEqual(len(candidates), 1)
            self.assertTrue((archive / "evidence-move-audit.tsv").is_file())


if __name__ == "__main__":
    unittest.main()
