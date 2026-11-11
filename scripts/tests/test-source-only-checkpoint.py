#!/usr/bin/env python3
"""Regression checks for the offline source-only checkpoint gate."""
from __future__ import annotations

import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-source-only-checkpoint.py"


def load_module():
    spec = importlib.util.spec_from_file_location("source_only_checkpoint", SCRIPT)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


def main() -> int:
    build_inputs = (
        ROOT / "scripts/build-owned-distribution.sh",
        ROOT / "scripts/build-reconstituted-framework.sh",
        ROOT / "scripts/build-source-only-application.py",
        ROOT / "scripts/stage-source-only-public-deps.py",
        ROOT / "scripts/prepare-reconstituted-maven-repo.py",
    )
    forbidden = (
        "gikam-third-czjky-14.1.5-run.jar",
        "SUPPLIER_RUN_JAR",
        "cfr-0.152",
        "Vineflower",
        "prepare-reconstituted-public-deps.py",
        "build-prep.sh",
        "docker/Dockerfile.built",
    )
    for path in build_inputs:
        text = path.read_text(encoding="utf-8")
        assert not any(token in text for token in forbidden), path
    framework_build = (ROOT / "scripts/build-reconstituted-framework.sh").read_text(encoding="utf-8")
    assert "LIMS_EXTERNAL_ARTIFACT_ROOT" in framework_build
    assert "--seed \"$PLUGIN_SEED\"" in framework_build

    verifier = load_module()
    # The registry includes owned additions after the historical generated
    # source snapshot. Counts must follow those modules, not that old milestone.
    with tempfile.TemporaryDirectory(prefix="source-only-module-count-") as temporary:
        fixture = Path(temporary)
        docs = fixture / "docs/reconstitution"
        docs.mkdir(parents=True)
        registry = json.loads((ROOT / "docs/reconstitution/framework-module-registry.json").read_text())
        for name in ("all-framework-source-closure-current.json", "project-source-closure-current.json", "decompiler-transforms.json"):
            (docs / name).symlink_to(ROOT / "docs/reconstitution" / name)
        (fixture / "project").symlink_to(ROOT / "project", target_is_directory=True)
        (fixture / "scripts").symlink_to(ROOT / "scripts", target_is_directory=True)
        for item in registry["modules"]:
            java = fixture / "reconstituted/framework" / item["module"] / "src/main/java"
            java.mkdir(parents=True)
            (java / "Existing.java").write_text("class Existing {}\n")
            item["source_file_count"] = 1
        registry["source_file_count"] = 7
        registry_path = docs / "framework-module-registry.json"
        registry_path.write_text(json.dumps(registry))
        artifact = registry["artifact_sha256"]
        errors = []
        verifier.verify_snapshots(fixture, artifact, errors)
        assert not errors, errors

        extra = fixture / "reconstituted/framework/owned-core-common/src/main/java/NewOwned.java"
        extra.write_text("class NewOwned {}\n")
        errors = []
        verifier.verify_snapshots(fixture, artifact, errors)
        assert any("owned-core-common" in error and "source count" in error for error in errors), errors

        registry["modules"][0]["source_file_count"] = 2
        registry["source_file_count"] = 8
        registry_path.write_text(json.dumps(registry))
        errors = []
        verifier.verify_snapshots(fixture, artifact, errors)
        assert not errors, errors

        registry["source_file_count"] = 9
        registry_path.write_text(json.dumps(registry))
        errors = []
        verifier.verify_snapshots(fixture, artifact, errors)
        assert any("registry source count" in error for error in errors), errors

    result = subprocess.run(
        [sys.executable, str(SCRIPT)], cwd=ROOT, text=True,
        capture_output=True, check=False,
    )
    assert result.returncode == 0, result.stdout + result.stderr
    payload = json.loads(result.stdout)
    assert payload["status"] == "pass"
    assert payload["errors"] == []

    with tempfile.TemporaryDirectory(prefix="source-only-checkpoint-test-") as temporary:
        distribution = Path(temporary)
        (distribution / "lib").mkdir(parents=True)
        (distribution / "manifest.json").write_text(json.dumps({
            "distribution_mode": "source-only-reconstituted",
            "dependency_classpath": ["public-lib.jar"],
        }) + "\n", encoding="utf-8")
        (distribution / "lib/classpath.txt").write_text("public-lib.jar\n", encoding="utf-8")
        errors: list[str] = []
        verifier.verify_distribution(distribution, errors)
        assert errors == []

        (distribution / "lib/classpath.txt").write_text("gikam-core-private.jar\n", encoding="utf-8")
        errors = []
        verifier.verify_distribution(distribution, errors)
        assert any("private supplier dependency" in error for error in errors)

    # A tracked snapshot must name a generator that still exists. This catches
    # convergence commits that remove the updater while retaining stale JSON.
    with tempfile.TemporaryDirectory(prefix="source-only-checkpoint-generator-test-") as temporary:
        temporary = Path(temporary)
        snapshot = json.loads((ROOT / "docs/reconstitution/project-source-closure-current.json").read_text())
        snapshot["generated_from"] = "scripts/missing-source-snapshot-generator.py current compile"
        probe = temporary / "probe.json"
        probe.write_text(json.dumps(snapshot), encoding="utf-8")
        errors = []
        verifier.verify_snapshots(ROOT, "4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f", errors, probe)
        assert any("project snapshot generator is missing" in error for error in errors)

    print("source-only checkpoint verifier: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
