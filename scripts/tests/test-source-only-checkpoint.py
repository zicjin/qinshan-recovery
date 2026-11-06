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

    result = subprocess.run(
        [sys.executable, str(SCRIPT)], cwd=ROOT, text=True,
        capture_output=True, check=False,
    )
    assert result.returncode == 0, result.stdout + result.stderr
    payload = json.loads(result.stdout)
    assert payload["status"] == "pass"
    assert payload["errors"] == []

    verifier = load_module()
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

    print("source-only checkpoint verifier: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
