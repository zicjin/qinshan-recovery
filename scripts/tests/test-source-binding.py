#!/usr/bin/env python3
"""Regression tests for orthogonal bindings and repository provenance."""
from __future__ import annotations

import importlib.util
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest import mock

MODULE = Path(__file__).resolve().parents[1] / "source_binding.py"
spec = importlib.util.spec_from_file_location("source_binding", MODULE)
assert spec and spec.loader
binding = importlib.util.module_from_spec(spec)
spec.loader.exec_module(binding)


class SourceBindingTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        files = {
            "project/src/main/java/Owned.java": "class Owned {}\n",
            "project/pom.xml": "<project/>\n",
            "owned-framework/src/Framework.java": "class Framework {}\n",
            "reconstituted/framework/Reconstituted.java": "class Reconstituted {}\n",
            "docs/reconstitution/decompiler-transforms.json": '{"rules":[]}\n',
            "scripts/source_binding.py": "binding-v1\n",
            "scripts/evidence_output.py": "output-v1\n",
            "scripts/spring_api_contract_runner.py": "runner-v1\n",
            "scripts/api_fixture.py": "fixture-v1\n",
            "scripts/build-owned-distribution.sh": "build-v1\n",
            "scripts/manage-evidence.py": "governance-v1\n",
            "tools/api_route_ledger.py": "ledger-v1\n",
            "tools/api_route_closure.py": "closure-v1\n",
            "docs/spec.md": "docs-v1\n",
            "evidence/index.tsv": "index-v1\n",
            "archive/audit.txt": "archive-v1\n",
        }
        for relative, value in files.items():
            path = self.root / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(value, encoding="utf-8")
        (self.root / "owned-framework").mkdir(exist_ok=True)
        (self.root / ".gitignore").write_text(
            "target/\n__pycache__/\n*.pyc\n*.pyo\n",
            encoding="utf-8",
        )
        self.git("init", "-q")
        self.git("config", "user.email", "test@example.invalid")
        self.git("config", "user.name", "source-binding-test")
        self.git("add", ".")
        self.git("commit", "-qm", "initial")

    def tearDown(self):
        self.temp.cleanup()

    def git(self, *args: str) -> None:
        subprocess.run(["git", *args], cwd=self.root, check=True)

    def capture(self, runtime_contract=None):
        return binding.capture_binding(self.root, runtime_contract=runtime_contract)

    def test_schema_policy_identity_changes_runtime_and_helper_changes_harness_only(self):
        first = self.capture()
        policy = self.capture({'schema_policy_id': 'approved-missing-runtime-tables-v1', 'schema_policy_sha256': 'a'*64})
        self.assertNotEqual(first['runtime_contract_digest'], policy['runtime_contract_digest'])
        changed_pin = self.capture({'schema_policy_id': 'approved-missing-runtime-tables-v1', 'schema_policy_sha256': 'b'*64})
        self.assertNotEqual(policy['runtime_contract_digest'], changed_pin['runtime_contract_digest'])
        self.assertIn('runtime.schema_policy_sha256', policy['runtime_contract_scope'])
        (self.root / 'scripts/local_schema_policy.py').write_text('policy-v1\n')
        second = self.capture()
        self.assertNotEqual(first['harness_binding'], second['harness_binding'])
        self.assertEqual(first['application_binding'], second['application_binding'])
        self.assertIn('scripts/http_write_proof.py', second['harness_scope'])

    def test_docs_and_registry_archive_only_commit_preserve_all_bindings(self):
        first = self.capture()
        (self.root / "docs" / "spec.md").write_text("docs-v2\n", encoding="utf-8")
        (self.root / "evidence" / "index.tsv").write_text("index-v2\n", encoding="utf-8")
        (self.root / "archive" / "audit.txt").write_text("archive-v2\n", encoding="utf-8")
        self.git("add", "docs/spec.md", "evidence/index.tsv", "archive/audit.txt")
        self.git("commit", "-qm", "docs registry archive")
        second = self.capture()

        self.assertNotEqual(
            first["repository_head_at_capture"],
            second["repository_head_at_capture"],
        )
        self.assertEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertEqual(binding.binding_delta(first, second), {
            "application": False,
            "harness": False,
            "runtime_contract": False,
            "planning": False,
            "governance": False,
        })
        self.assertEqual(binding.replay_surfaces(first, second), ())
        self.assertFalse(second["implementation_dirty"])

    def test_application_source_change_only_changes_application_surface(self):
        first = self.capture()
        (self.root / "project" / "src" / "main" / "java" / "Owned.java").write_text(
            "class Owned { int version = 2; }\n", encoding="utf-8",
        )
        second = self.capture()

        self.assertNotEqual(first["application_binding"], second["application_binding"])
        self.assertEqual(first["harness_binding"], second["harness_binding"])
        self.assertEqual(first["runtime_contract_binding"], second["runtime_contract_binding"])
        self.assertEqual(first["planning_binding"], second["planning_binding"])
        self.assertEqual(first["governance_binding"], second["governance_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ("application",))
        self.assertTrue(second["application_dirty"])
        self.assertTrue(second["implementation_dirty"])

    def test_mapper_renderer_changes_application_binding(self):
        first = self.capture()
        (self.root / "scripts/render-external-logical-mappers.py").write_text("rendered-sql-v2\n")
        second = self.capture()
        self.assertNotEqual(first["application_binding"], second["application_binding"])
        self.assertTrue(second["application_dirty"])
        self.assertIn("scripts/render-external-logical-mappers.py", second["application_scope"])

    def test_deployment_ddl_changes_application_surface(self):
        first = self.capture()
        ddl = self.root / 'deployment/oracle/core-schema/20-tables.sql'
        ddl.parent.mkdir(parents=True)
        ddl.write_text('CREATE TABLE "EXAMPLE" ("ID" NUMBER);\n')
        second = self.capture()
        self.assertNotEqual(first['application_binding'], second['application_binding'])
        self.assertIn('deployment/oracle/core-schema/20-tables.sql', second['application_scope'])
        self.assertTrue(second['application_dirty'])

    def test_deployment_readme_does_not_change_application_binding(self):
        first = self.capture()
        readme = self.root / 'deployment/oracle/core-schema/README.md'
        readme.parent.mkdir(parents=True)
        readme.write_text('Deployment instructions only.\n')
        second = self.capture()
        self.assertEqual(first['application_binding'], second['application_binding'])

    def test_external_deployment_sql_is_bound_but_instructions_are_replay_neutral(self):
        first = self.capture()
        directory = self.root / 'deployment/oracle/external-schema'
        directory.mkdir(parents=True)
        (directory / '20-tables.sql').write_text('CREATE TABLE EXAMPLE (ID NUMBER);\n')
        second = self.capture()
        self.assertNotEqual(first['application_binding'], second['application_binding'])
        self.assertTrue(second['application_dirty'])
        (directory / 'README.md').write_text('Partial package instructions.\n')
        third = self.capture()
        self.assertEqual(second['application_binding'], third['application_binding'])

    def test_combined_installation_inputs_change_application_but_readme_is_neutral(self):
        for relative in ('deployment/oracle/installation-manifest.json',
                         'deployment/oracle/install.sql', 'deployment/oracle/00-preflight.sql',
                         'deployment/oracle/99-verify.sql'):
            with self.subTest(relative=relative):
                first = self.capture()
                target = self.root / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text('combined-deployment-input\n')
                second = self.capture()
                self.assertNotEqual(first['application_binding'], second['application_binding'])
                self.assertEqual(first['harness_binding'], second['harness_binding'])
                self.assertTrue(second['application_dirty'])
        first = self.capture()
        (self.root / 'deployment/oracle/README.md').write_text('Instructions only.\n')
        second = self.capture()
        self.assertEqual(first['application_binding'], second['application_binding'])

    def test_shipped_combined_verifier_changes_harness_and_distribution(self):
        first = self.capture()
        (self.root / 'scripts/verify-combined-oracle-installation.py').write_text('verification-v1\n')
        second = self.capture()
        self.assertNotEqual(first['application_binding'], second['application_binding'])
        self.assertNotEqual(first['harness_binding'], second['harness_binding'])
        self.assertTrue(second['harness_dirty'])

    def test_shipped_installation_inputs_change_distribution_binding(self):
        for relative in ('deployment/oracle/installation-manifest.json',
                         'deployment/oracle/core-schema/manifest.json',
                         'scripts/verify-core-schema-package.py',
                         'scripts/verify-combined-oracle-installation.py',
                         'owned-distribution/installation-README.md',
                         'owned-distribution/initialize-workflows.sh',
                         'deployment/oracle/workflow/manifest.json',
                         'deployment/oracle/workflow/definitions.json',
                         'deployment/oracle/workflow/README.md',
                         'deployment/oracle/import-templates/manifest.json',
                         'deployment/oracle/import-templates/assets/reportSendInfo.xlsx',
                         'scripts/install-import-template-assets.py'):
            with self.subTest(relative=relative):
                first = self.capture()
                target = self.root / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text('installation input\n')
                second = self.capture()
                self.assertNotEqual(first['distribution_input_digest'], second['distribution_input_digest'])

    def test_harness_change_only_changes_harness_surface(self):
        first = self.capture()
        (self.root / "scripts" / "evidence_output.py").write_text("output-v2\n", encoding="utf-8")
        second = self.capture()

        self.assertEqual(first["application_binding"], second["application_binding"])
        self.assertNotEqual(first["harness_binding"], second["harness_binding"])
        self.assertEqual(first["planning_binding"], second["planning_binding"])
        self.assertEqual(first["governance_binding"], second["governance_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ("harness",))
        self.assertTrue(second["harness_dirty"])
        self.assertFalse(second["application_dirty"])

    def test_schema_fixture_and_renderer_change_harness_without_application_rebuild(self):
        for relative in ('api-fixtures/schema/missing-runtime-tables.sql',
                         'api-fixtures/schema/missing-runtime-tables.json',
                         'scripts/schema_fixture.py',
                         'project/sql/oracle/oracle_sql_emc.txt',
                         'project/sql/核心发版14.1.5/init-sql/oracle/0.common_sql_oracle-1.txt'):
            with self.subTest(relative=relative):
                first = self.capture()
                target = self.root / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text('schema-fixture-input\n', encoding='utf-8')
                second = self.capture()
                self.assertEqual(first['application_binding'], second['application_binding'])
                self.assertNotEqual(first['harness_binding'], second['harness_binding'])
                self.assertEqual(first['runtime_contract_binding'], second['runtime_contract_binding'])
                self.assertEqual(binding.replay_surfaces(first, second), ('harness',))
                self.assertTrue(second['harness_dirty'])
                self.assertFalse(second['application_dirty'])

    def test_harness_bookkeeping_change_requires_no_replay_when_semantics_are_unchanged(self):
        first = self.capture()
        (self.root / "scripts" / "evidence_output.py").write_text(
            "output-v2 logging-only\n", encoding="utf-8",
        )
        second = self.capture()

        self.assertNotEqual(first["harness_binding"], second["harness_binding"])
        self.assertEqual(
            binding.replay_surfaces(first, second, harness_semantics_unchanged=True),
            (),
        )
        self.assertEqual(
            binding.replay_surfaces(first, second, harness_semantics_unchanged=False),
            ("harness",),
        )

    def test_planning_change_only_changes_planning_surface(self):
        first = self.capture()
        (self.root / "tools" / "api_route_ledger.py").write_text("ledger-v2\n", encoding="utf-8")
        second = self.capture()

        self.assertEqual(first["application_binding"], second["application_binding"])
        self.assertEqual(first["harness_binding"], second["harness_binding"])
        self.assertNotEqual(first["planning_binding"], second["planning_binding"])
        self.assertEqual(first["governance_binding"], second["governance_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ("planning",))

    def test_governance_change_requires_no_http_replay(self):
        first = self.capture()
        (self.root / "scripts" / "manage-evidence.py").write_text(
            "governance-v2\n", encoding="utf-8",
        )
        second = self.capture()

        self.assertEqual(first["application_binding"], second["application_binding"])
        self.assertEqual(first["harness_binding"], second["harness_binding"])
        self.assertEqual(first["runtime_contract_binding"], second["runtime_contract_binding"])
        self.assertEqual(first["planning_binding"], second["planning_binding"])
        self.assertNotEqual(first["governance_binding"], second["governance_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ())
        self.assertTrue(second["governance_dirty"])

    def test_distribution_input_change_changes_application_and_distribution_digest(self):
        first = self.capture()
        (self.root / "project" / "pom.xml").write_text("<project>v2</project>\n", encoding="utf-8")
        second = self.capture()

        self.assertNotEqual(first["application_binding"], second["application_binding"])
        self.assertNotEqual(
            first["distribution_input_digest"],
            second["distribution_input_digest"],
        )
        self.assertEqual(binding.replay_surfaces(first, second), ("application",))

    def test_runtime_contract_hash_changes_only_runtime_surface(self):
        first = self.capture()
        second = self.capture(runtime_contract={
            "baseline_schema_hash": "a" * 64,
            "runtime_schema_hash_before": "b" * 64,
            "runtime_schema_hash_after": "b" * 64,
        })

        self.assertEqual(first["application_binding"], second["application_binding"])
        self.assertEqual(first["harness_binding"], second["harness_binding"])
        self.assertNotEqual(first["runtime_contract_binding"], second["runtime_contract_binding"])
        self.assertEqual(first["planning_binding"], second["planning_binding"])
        self.assertEqual(first["governance_binding"], second["governance_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ("runtime_contract",))
        self.assertFalse(second["runtime_contract_dirty"])
        self.assertFalse(second["implementation_dirty"])

    def test_local_runtime_profile_and_redis_identity_are_binding_inputs(self):
        first = self.capture()
        local_contract = {
            "runtime_profile": "local-wsl",
            "oracle_instance_id": "lims-local-runtime-oracle",
            "oracle_container": "lims-local-runtime-oracle",
            "redis_container": "lims-local-runtime-redis",
            "baseline_schema": "LIMS_BASELINE_LOCAL",
            "runtime_schema": "LIMS_RUNTIME_LOCAL",
        }
        local = self.capture(runtime_contract=local_contract)
        self.assertEqual(local["runtime_contract"]["runtime_profile"], "local-wsl")
        self.assertEqual(local["runtime_contract"]["redis_container"], "lims-local-runtime-redis")
        self.assertNotEqual(first["runtime_contract_binding"], local["runtime_contract_binding"])
        self.assertEqual(first["application_binding"], local["application_binding"])
        self.assertEqual(first["harness_binding"], local["harness_binding"])
        changed_profile = self.capture(runtime_contract={**local_contract, "runtime_profile": "sss2"})
        changed_redis = self.capture(runtime_contract={**local_contract, "redis_container": "another-redis"})
        self.assertNotEqual(local["runtime_contract_binding"], changed_profile["runtime_contract_binding"])
        self.assertNotEqual(local["runtime_contract_binding"], changed_redis["runtime_contract_binding"])

    def test_runner_change_preserves_legacy_compatibility_aggregate_change(self):
        first = self.capture()
        (self.root / "scripts" / "spring_api_contract_runner.py").write_text(
            "runner-v2\n", encoding="utf-8",
        )
        second = self.capture()

        self.assertNotEqual(first["implementation_binding"], second["implementation_binding"])
        self.assertNotEqual(
            first["implementation_tree_digest"],
            second["implementation_tree_digest"],
        )
        self.assertEqual(
            second["implementation_tree_digest"],
            binding.implementation_tree_digest(self.root),
        )

    def test_repository_head_can_change_without_replay(self):
        first = self.capture()
        (self.root / "docs" / "spec.md").write_text("docs-v2\n", encoding="utf-8")
        self.git("add", "docs/spec.md")
        self.git("commit", "-qm", "docs")
        second = self.capture()

        self.assertNotEqual(
            first["repository_head_at_capture"],
            second["repository_head_at_capture"],
        )
        self.assertEqual(first["implementation_binding"], second["implementation_binding"])
        self.assertEqual(binding.replay_surfaces(first, second), ())

    def test_scopes_are_disjoint_except_compatibility_union(self):
        payload = self.capture()
        application = set(payload["application_scope"])
        harness = set(payload["harness_scope"])
        planning = set(payload["planning_scope"])
        governance = set(payload["governance_scope"])

        self.assertFalse(application & harness)
        self.assertFalse(application & planning)
        self.assertFalse(application & governance)
        self.assertFalse(harness & planning)
        self.assertFalse(harness & governance)
        self.assertFalse(planning & governance)
        self.assertTrue(set(payload["runtime_contract_scope"]).isdisjoint(application))
        self.assertEqual(
            set(payload["implementation_scope"]),
            application | harness | set(payload["runtime_contract_scope"]) | planning | governance,
        )
        self.assertTrue(payload["implementation_binding"].startswith("impl-"))
        self.assertTrue(payload["application_binding"].startswith("app-"))
        self.assertTrue(payload["harness_binding"].startswith("harness-"))
        self.assertTrue(payload["runtime_contract_binding"].startswith("rt-"))
        self.assertTrue(payload["planning_binding"].startswith("plan-"))
        self.assertTrue(payload["governance_binding"].startswith("gov-"))

    def test_generated_target_and_bytecode_are_excluded(self):
        first = self.capture()
        target = self.root / "project" / "src" / "main" / "java" / "target"
        target.mkdir()
        (target / "Owned.class").write_bytes(b"build-output")
        cache = self.root / "scripts" / "__pycache__"
        cache.mkdir()
        (cache / "source_binding.pyc").write_bytes(b"bytecode")
        second = self.capture()

        self.assertEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertFalse(second["implementation_dirty"])

    def test_custom_implementation_scope_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "centralized"):
            binding.implementation_dirty(self.root, ("project/src/main/java",))

    def test_feature_root_prefers_parent_workspace_for_standalone_repo(self):
        parent = self.root.parent
        feature = parent / ".scratch" / binding.FEATURE_SLUG
        feature.mkdir(parents=True, exist_ok=True)
        self.assertEqual(binding.feature_root(self.root), feature.resolve())

    def test_feature_root_override_is_respected(self):
        override = self.root / "external-feature"
        with mock.patch.dict(os.environ, {"SUNWAY_FEATURE_ROOT": str(override)}):
            self.assertEqual(binding.feature_root(self.root), override.resolve())

    def test_workspace_path_resolves_legacy_prefixes_in_standalone_mode(self):
        parent = self.root.parent
        feature = parent / ".scratch" / binding.FEATURE_SLUG
        feature.mkdir(parents=True, exist_ok=True)
        self.assertEqual(
            binding.resolve_workspace_path("lims-recovery/api-contracts/core/read.json", self.root),
            self.root / "api-contracts" / "core" / "read.json",
        )
        self.assertEqual(
            binding.resolve_workspace_path(
                ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/jobs.json",
                self.root,
            ),
            parent / ".scratch" / binding.FEATURE_SLUG / "diagnostic-evidence" / "pending" / "jobs.json",
        )


if __name__ == "__main__":
    unittest.main()
