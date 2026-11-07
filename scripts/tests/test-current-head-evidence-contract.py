#!/usr/bin/env python3
"""Exercise the evidence identity CLI against isolated, real Git histories."""
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
GATE = ROOT / "scripts/tests/test-current-head-evidence.py"


class CurrentHeadEvidenceContractTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="evidence-identity-")
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        for name, text in {
            "project/src/main/java/App.java": "class App {}\n",
            "owned-framework/src/Framework.java": "class Framework {}\n",
            "scripts/spring_api_contract_runner.py": "# harness v1\n",
            "scripts/manage-evidence.py": "# governance v1\n",
            "tools/api_route_ledger.py": "# planning v1\n",
            "docs/spec.md": "docs v1\n",
            ".gitignore": "__pycache__/\n*.pyc\n",
        }.items():
            path = self.root / name
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(text, encoding="utf-8")
        shutil.copyfile(ROOT / "scripts/source_binding.py", self.root / "scripts/source_binding.py")
        self.git("init", "-q")
        self.git("config", "user.email", "test@example.invalid")
        self.git("config", "user.name", "evidence-contract-test")
        self.commit("baseline")
        self.report = self.capture()
        self.report.update(schema=2, source_commit=self.report["repository_head_at_capture"])
        self.path = self.root / "report.json"

    def git(self, *arguments):
        return subprocess.check_output(["git", *arguments], cwd=self.root, text=True).strip()

    def commit(self, message):
        self.git("add", ".")
        self.git("commit", "-qm", message)

    def capture(self, runtime=None):
        code = ("import json,sys;sys.path.insert(0,'scripts');"
                "from source_binding import capture_binding;"
                "print(json.dumps(capture_binding(runtime_contract=json.loads(sys.argv[1]))))")
        return json.loads(subprocess.check_output(
            [sys.executable, "-c", code, json.dumps(runtime)], cwd=self.root, text=True,
            env={**os.environ, "PYTHONDONTWRITEBYTECODE": "1"},
        ))

    def evaluate(self):
        self.path.write_text(json.dumps(self.report), encoding="utf-8")
        return subprocess.run(
            [sys.executable, str(GATE), "--repo-root", str(self.root), str(self.path)],
            cwd=self.root, capture_output=True, text=True,
            env={**os.environ, "PYTHONDONTWRITEBYTECODE": "1"},
        )

    def assert_current(self):
        result = self.evaluate()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("PASS", result.stdout)

    def assert_stale(self, reason):
        result = self.evaluate()
        self.assertNotEqual(result.returncode, 0, result.stdout)
        self.assertIn(reason, result.stderr)

    def test_clean_schema_two_runner_metadata_remains_supported(self):
        # binding_metadata() on existing schema=2 reports omits these fields.
        for key in list(self.report):
            if key.endswith("_tree_digest") and key != "implementation_tree_digest":
                del self.report[key]
            elif key.endswith("_dirty") and key != "implementation_dirty":
                del self.report[key]
        self.report.pop("schema_version")
        self.report.pop("runtime_contract_digest")
        self.assert_current()

    def test_uncommitted_application_edit_is_rejected(self):
        (self.root / "project/src/main/java/App.java").write_text("class App { int changed; }\n")
        self.assert_stale("application binding is stale")

    def test_dirty_application_with_matching_bytes_is_rejected(self):
        path = self.root / "project/src/main/java/App.java"
        path.write_text("class App { int changed; }\n")
        self.report = self.capture()
        self.report.update(schema=2, source_commit=self.report["repository_head_at_capture"],
                           application_dirty=False, implementation_dirty=False)
        self.assert_stale("application scope is dirty")

    def test_committed_harness_change_cannot_use_semantics_flag(self):
        (self.root / "scripts/spring_api_contract_runner.py").write_text("# harness v2\n")
        self.commit("changed request construction")
        self.report["harness_semantics_unchanged"] = True
        self.assert_stale("harness binding is stale")

    def test_planning_change_requires_new_identity(self):
        (self.root / "tools/api_route_ledger.py").write_text("# planning v2\n")
        self.commit("changed alias map")
        self.assert_stale("planning binding is stale")

    def test_reported_dirty_application_is_rejected(self):
        self.report["application_dirty"] = True
        self.assert_stale("application report scope is dirty")

    def test_governance_worktree_edit_does_not_stale_clean_behavior_surfaces(self):
        (self.root / "scripts/manage-evidence.py").write_text("# governance dirty\n")
        self.assert_current()

    def test_scope_cannot_be_narrowed_or_extended(self):
        for surface in ("application", "harness", "planning", "runtime_contract"):
            for change in (lambda scope: scope[:-1], lambda scope: scope + ["extra/path"]):
                with self.subTest(surface=surface, change=change):
                    original = self.report[surface + "_scope"]
                    self.report[surface + "_scope"] = change(original)
                    self.assert_stale(surface + " scope is not canonical")
                    self.report[surface + "_scope"] = original

    def test_governance_and_aggregate_scope_metadata_do_not_force_replay(self):
        self.report["governance_scope"] = ["scripts/retired-governance.py"]
        self.report["implementation_scope"].append("scripts/retired-governance.py")
        self.assert_current()

    def test_runtime_schema_mutation_is_rejected(self):
        self.report["runtime_contract"]["runtime_schema"] = "OTHER_RUNTIME"
        self.assert_stale("runtime contract binding does not match report runtime")

    def test_runtime_profile_and_redis_identity_are_bound(self):
        for field, changed in (("runtime_profile", "local-wsl"), ("redis_container", "another-redis")):
            with self.subTest(field=field):
                original = self.report["runtime_contract"][field]
                self.report["runtime_contract"][field] = changed
                self.assert_stale("runtime contract binding does not match report runtime")
                self.report["runtime_contract"][field] = original

    def test_missing_runtime_cannot_fall_back_to_repository_default(self):
        del self.report["runtime_contract"]
        self.assert_stale("runtime contract or schema identity is missing")

    def test_missing_runtime_schema_is_rejected(self):
        del self.report["runtime_contract"]["runtime_schema"]
        self.assert_stale("runtime contract or schema identity is missing")

    def test_unknown_binding_schema_is_rejected(self):
        self.report["schema_version"] = 999
        self.assert_stale("unsupported binding schema")

    def test_unknown_provenance_commit_is_rejected(self):
        self.report["source_commit"] = "a" * 40
        self.assert_stale("not a Git object")

    def test_legacy_composite_is_not_automatically_current(self):
        for surface in ("application", "harness", "runtime_contract", "planning", "governance"):
            del self.report[surface + "_binding"]
        self.report["source_dirty"] = False
        self.assert_stale("legacy composite binding is stale")

    def test_docs_registry_and_archive_changes_preserve_evidence(self):
        for relative in ("docs/spec.md", "evidence/evidence-index.tsv", "archive/old.json"):
            path = self.root / relative
            path.parent.mkdir(exist_ok=True)
            path.write_text("bookkeeping only\n")
        self.commit("docs registry archive")
        self.assert_current()

    def test_governance_commit_preserves_local_runtime_evidence(self):
        local = {
            "runtime_profile": "local-wsl",
            "oracle_instance_id": "lims-local-runtime-oracle",
            "oracle_container": "lims-local-runtime-oracle",
            "redis_container": "lims-local-runtime-redis",
            "baseline_schema": "LIMS_BASELINE_LOCAL",
            "runtime_schema": "LIMS_RUNTIME_LOCAL",
        }
        self.report = self.capture(local)
        self.report.update(schema=2, source_commit=self.report["repository_head_at_capture"])
        (self.root / "scripts/manage-evidence.py").write_text("# governance v2\n")
        self.commit("governance only")
        self.assertNotEqual(self.report["implementation_binding"], self.capture()["implementation_binding"])
        self.assert_current()


if __name__ == "__main__":
    unittest.main()
