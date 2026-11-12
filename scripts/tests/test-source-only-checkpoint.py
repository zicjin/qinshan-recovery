#!/usr/bin/env python3
"""Regression checks for the offline source-only checkpoint gate."""
from __future__ import annotations

import importlib.util
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-source-only-checkpoint.py"


def load_module():
    spec = importlib.util.spec_from_file_location("source_only_checkpoint", SCRIPT)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


def distribution_fixture(directory: Path) -> dict:
    """Eight real archives and independent expected merged bytes; no build."""
    registry_raw = (ROOT / 'docs/reconstitution/framework-module-registry.json').read_bytes()
    registry = json.loads(registry_raw)
    provenance = directory / 'provenance'
    provenance.mkdir()
    (provenance / 'framework-module-registry.json').write_bytes(registry_raw)
    (directory / 'modules').mkdir()
    (directory / 'app').mkdir()
    (directory / 'lib').mkdir()
    (directory / 'lib/classpath.txt').write_text('public-lib.jar\n')
    names = ['lims-owned-application.jar'] + [Path(m['output_jar']).name for m in registry['modules']]
    merged = {}
    factory = b'example.Factory=\\\nexample.Implementation\n'
    for index, name in enumerate(names):
        entries = {f'example/Module{index}.class': bytes([index]),
                   f'templates/module{index}.html': f'module {index}'.encode()}
        merged.update(entries)
        if index == 0:
            entries['META-INF/spring.factories'] = factory
            merged['META-INF/spring.factories'] = factory
        with zipfile.ZipFile(directory / 'modules' / name, 'w') as jar:
            for path, data in entries.items():
                jar.writestr(path, data)
    with zipfile.ZipFile(directory / 'app/lims-owned-application.jar', 'w') as jar:
        for path, data in merged.items():
            jar.writestr(path, data)
    digest = lambda path: hashlib.sha256(path.read_bytes()).hexdigest()
    manifest = {'distribution_mode': 'source-only-reconstituted',
        'app_jar': 'app/lims-owned-application.jar',
        'module_jars': ['modules/' + name for name in names],
        'source_jar_sha256': {name: digest(directory / 'modules' / name) for name in names},
        'source_jar_hash_convention': 'normalized module archives; fixed ZIP timestamps, no generated manifest',
        'resource_overrides': [], 'merged_resources': {'META-INF/spring.factories': {
            'sources': [names[0]], 'sha256': hashlib.sha256(factory).hexdigest()}},
        'dependency_classpath': ['public-lib.jar'],
        'files': {str(p.relative_to(directory)): digest(p) for p in directory.rglob('*') if p.is_file()}}
    (directory / 'manifest.json').write_text(json.dumps(manifest))
    return manifest


def test_distribution_framework_coverage(verifier):
    with tempfile.TemporaryDirectory(prefix='checkpoint-framework-coverage-') as temporary:
        distribution = Path(temporary)
        manifest = distribution_fixture(distribution)
        errors = []
        verifier.verify_distribution(distribution, errors)
        assert not errors, errors
        # Real jq failure packaged only the application: all ordinary mode and
        # forbidden-content checks pass, but the framework was omitted.
        missing = manifest['module_jars'].pop()
        del manifest['files'][missing]
        del manifest['source_jar_sha256'][Path(missing).name]
        (distribution / missing).unlink()
        (distribution / 'manifest.json').write_text(json.dumps(manifest))
        errors = []
        verifier.verify_distribution(distribution, errors)
        assert any('framework module coverage' in error for error in errors), errors

    for mutation in ('registry', 'module-hash', 'merged-class', 'merged-resource', 'merged-factories'):
        with tempfile.TemporaryDirectory(prefix='checkpoint-framework-tamper-') as temporary:
            distribution = Path(temporary)
            manifest = distribution_fixture(distribution)
            if mutation == 'registry':
                path = distribution / 'provenance/framework-module-registry.json'
                data = json.loads(path.read_text())
                data['modules'].pop()
                path.write_text(json.dumps(data))
                expected = 'framework registry mismatch'
            elif mutation == 'module-hash':
                path = distribution / manifest['module_jars'][-1]
                with zipfile.ZipFile(path, 'a') as jar:
                    jar.writestr('example/Unexpected.class', b'unexpected')
                expected = 'framework module hash mismatch'
            else:
                path = distribution / manifest['app_jar']
                omitted = {'merged-class': 'example/Module7.class',
                           'merged-resource': 'templates/module7.html',
                           'merged-factories': 'META-INF/spring.factories'}[mutation]
                with zipfile.ZipFile(path) as jar:
                    entries = {name: jar.read(name) for name in jar.namelist() if name != omitted}
                with zipfile.ZipFile(path, 'w') as jar:
                    for name, raw in entries.items():
                        jar.writestr(name, raw)
                # Even a self-consistent rehashed outer manifest cannot conceal
                # missing compiled framework classes or original resources.
                manifest['files'][manifest['app_jar']] = hashlib.sha256(path.read_bytes()).hexdigest()
                if mutation == 'merged-factories':
                    manifest['merged_resources'] = {}
                (distribution / 'manifest.json').write_text(json.dumps(manifest))
                expected = ('merged resource coverage mismatch' if mutation == 'merged-factories'
                            else 'merged framework entry missing or changed: ' + omitted)
            errors = []
            verifier.verify_distribution(distribution, errors)
            assert any(expected in error for error in errors), (mutation, errors)


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
    test_distribution_framework_coverage(verifier)
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
        distribution_fixture(distribution)
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
