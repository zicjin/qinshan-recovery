#!/usr/bin/env python3
"""Verify the tracked source-only reconstitution checkpoint.

This gate is intentionally offline.  It does not start an application, access
Oracle/Redis, or treat a compiled class/process as API evidence.  It binds the
tracked module registry and project-closure snapshots to the same stock-artifact
identity, then optionally verifies compiled module JARs, the source-only
application report, and a built distribution.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys
from typing import Any, Mapping
import zipfile


ROOT = Path(__file__).resolve().parents[1]
PRIVATE_MARKER = re.compile(r"(?:^|[/\\])(?:gikam-core-[^/\\]+\.jar|lims2-report-template-[^/\\]+\.jar|gikam-third-[^/\\]+\.jar)$", re.IGNORECASE)
FORBIDDEN_NAME = re.compile(r"(?:sunway-app\.(?:so|dll|o)|license\.lic|vendor\.lic|license-machine\.env)$", re.IGNORECASE)


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def load(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeDecodeError, json.JSONDecodeError) as error:
        raise ValueError(f"cannot read JSON {path}: {error}") from error
    if not isinstance(value, dict):
        raise ValueError(f"JSON root is not an object: {path}")
    return value


def require(condition: bool, message: str, errors: list[str]) -> None:
    if not condition:
        errors.append(message)


def artifact_identity(root: Path, run_jar: Path | None, errors: list[str]) -> str:
    inventory = load(root / "docs/reconstitution/artifact-inventory-current.json")
    artifact = inventory.get("artifact")
    require(isinstance(artifact, dict), "artifact inventory has no artifact object", errors)
    if not isinstance(artifact, dict):
        return ""
    expected = str(artifact.get("artifact_sha256", ""))
    require(bool(re.fullmatch(r"[0-9a-f]{64}", expected)), "artifact inventory SHA-256 is malformed", errors)
    require(inventory.get("schema") == "supplier-artifact-inventory-snapshot-v1", "artifact inventory schema", errors)
    require(inventory.get("status") == "diagnostic-generated-from-current-external-artifact", "artifact inventory status", errors)
    if run_jar is not None:
        require(run_jar.is_file(), f"stock run JAR is missing: {run_jar}", errors)
        if run_jar.is_file():
            require(sha256(run_jar) == expected, "stock run JAR SHA-256 differs from tracked inventory", errors)
    return expected


def verify_snapshots(root: Path, artifact_sha: str, errors: list[str], project_snapshot_path: Path | None = None) -> None:
    registry = load(root / "docs/reconstitution/framework-module-registry.json")
    require(registry.get("schema") == "reconstituted-framework-module-registry-v1", "framework registry schema", errors)
    require(registry.get("status") == "compiled-source-only", "framework registry is not compiled-source-only", errors)
    require(registry.get("all_modules_compile_pass") is True, "framework registry compile gate is not pass", errors)
    require(registry.get("artifact_sha256") == artifact_sha, "framework registry artifact identity differs", errors)
    require(registry.get("module_count") == 7, "framework registry module count", errors)
    module_source_count = 0
    for module in registry.get("modules", []):
        name = str(module.get("module", ""))
        require(bool(re.fullmatch(r"owned-core-[a-z-]+", name)), "invalid framework module name", errors)
        if not re.fullmatch(r"owned-core-[a-z-]+", name):
            continue
        source_root = root / "reconstituted/framework" / name / "src/main/java"
        actual = len(list(source_root.rglob("*.java")))
        require(module.get("source_file_count") == actual,
                "framework module source count differs from checkout: " + name, errors)
        module_source_count += actual
    require(registry.get("source_file_count") == module_source_count,
            "framework registry source count differs from module sources", errors)
    require(registry.get("resource_file_count") == 9427, "framework registry resource count", errors)
    require(registry.get("native_removal", {}).get("status") == "pass", "framework native removal status", errors)
    runtime_model = registry.get("runtime_model", {})
    require(runtime_model.get("boundary_process") is False, "framework boundary process enabled", errors)
    require(runtime_model.get("internal_http_bridge") is False, "framework HTTP bridge enabled", errors)
    require(runtime_model.get("jni_replacement_process") is False, "framework JNI replacement enabled", errors)

    all_snapshot = load(root / "docs/reconstitution/all-framework-source-closure-current.json")
    require(all_snapshot.get("schema") == "supplier-all-framework-source-closure-current-v1", "all-framework snapshot schema", errors)
    require(all_snapshot.get("status") == "diagnostic-all-framework-source-compile-pass", "all-framework snapshot status", errors)
    require(all_snapshot.get("canonical") is False and all_snapshot.get("strict_evidence") is False, "source snapshot is presented as evidence", errors)
    require(all_snapshot.get("artifact", {}).get("sha256") == artifact_sha, "all-framework artifact identity differs", errors)
    recovery = all_snapshot.get("source_recovery", {})
    require(recovery.get("closure_class_count") == 4876, "all-framework closure class count", errors)
    require(recovery.get("generated_top_level_source_count") == 4576, "all-framework generated source count", errors)
    require(recovery.get("source_tree_sha256"), "all-framework source tree digest missing", errors)
    project_compile = all_snapshot.get("project_compile", {})
    require(project_compile.get("exit_code") == 0, "all-framework project compile exit", errors)
    require(project_compile.get("error_count") == 0, "all-framework project compile errors", errors)
    require(project_compile.get("missing_symbol_queue") == [], "all-framework missing symbols", errors)
    require(project_compile.get("source_only_framework_classpath") is True, "all-framework classpath is not source-only", errors)
    require(project_compile.get("supplier_private_jars_on_classpath") is False, "supplier JAR on all-framework classpath", errors)
    transform = root / str(registry.get("transformation_manifest", ""))
    require(transform.is_file(), "tracked transformation manifest is missing", errors)
    if transform.is_file():
        require(sha256(transform) == registry.get("transformation_manifest_sha256"), "transformation manifest hash mismatch", errors)

    project_snapshot = load(project_snapshot_path or (root / "docs/reconstitution/project-source-closure-current.json"))
    require(project_snapshot.get("schema") == "supplier-project-source-closure-snapshot-v1", "project snapshot schema", errors)
    require(project_snapshot.get("status") == "diagnostic-project-source-compile-pass", "project snapshot status", errors)
    require(project_snapshot.get("canonical") is False and project_snapshot.get("strict_evidence") is False, "project snapshot is presented as evidence", errors)
    require(project_snapshot.get("stock_artifact", {}).get("sha256") == artifact_sha, "project snapshot artifact identity differs", errors)
    generated_from = str(project_snapshot.get("generated_from", ""))
    generator_match = re.match(r"^(scripts/[A-Za-z0-9._/-]+\.py)\b", generated_from)
    require(generator_match is not None, "project snapshot generated_from must name a tracked Python generator", errors)
    if generator_match:
        generator = root / generator_match.group(1)
        require(generator.is_file(), "project snapshot generator is missing: " + generator_match.group(1), errors)
    compile_data = project_snapshot.get("project_compile", {})
    require(compile_data.get("status") == "pass", "project snapshot compile status", errors)
    require(compile_data.get("exit_code") == 0 and compile_data.get("error_count") == 0, "project snapshot compile errors", errors)
    require(compile_data.get("missing_symbol_queue") == [], "project snapshot missing symbols", errors)
    require(compile_data.get("source_only_framework_classpath") is True, "project snapshot classpath is not source-only", errors)
    require(compile_data.get("supplier_private_jars_on_classpath") is False, "supplier JAR on project classpath", errors)
    actual_project_sources = len(list((root / "project/src/main/java").rglob("*.java")))
    require(compile_data.get("project_source_file_count") == actual_project_sources, "project source snapshot count differs from checkout", errors)


def verify_application_report(root: Path, report_path: Path, errors: list[str]) -> None:
    report = load(report_path)
    require(report.get("status") == "compiled-source-only", "source-only application report status", errors)
    require(report.get("java_version") == "8", "source-only application does not target Java 8", errors)
    require(report.get("supplier_private_jars_on_classpath") is False, "supplier JAR on application classpath", errors)
    require(report.get("runtime_model", {}).get("boundary_process") is False, "application boundary process enabled", errors)
    require(report.get("runtime_model", {}).get("internal_http_bridge") is False, "application HTTP bridge enabled", errors)
    for entry in report.get("classpath", []):
        if isinstance(entry, Mapping):
            name = str(entry.get("path", ""))
            require(not PRIVATE_MARKER.search(name), f"private supplier classpath entry: {name}", errors)
    app = root / str(report.get("application_jar", ""))
    require(app.is_file(), f"source-only application JAR is missing: {app}", errors)
    if app.is_file():
        require(sha256(app) == report.get("application_jar_sha256"), "source-only application JAR hash mismatch", errors)


def verify_distribution(distribution: Path, errors: list[str]) -> None:
    manifest_path = distribution / "manifest.json"
    classpath_path = distribution / "lib/classpath.txt"
    require(manifest_path.is_file(), f"distribution manifest is missing: {manifest_path}", errors)
    require(classpath_path.is_file(), f"distribution classpath is missing: {classpath_path}", errors)
    if not manifest_path.is_file():
        return
    manifest = load(manifest_path)
    require(manifest.get("distribution_mode") == "source-only-reconstituted", "distribution mode", errors)
    verify_distribution_framework(distribution, manifest, errors)
    for value in manifest.get("dependency_classpath", []):
        name = Path(str(value)).name
        require(not PRIVATE_MARKER.search(name), f"private supplier dependency in distribution: {name}", errors)
    if classpath_path.is_file():
        for raw in classpath_path.read_text(encoding="utf-8").splitlines():
            require(not PRIVATE_MARKER.search(Path(raw).name), f"private supplier dependency in classpath: {raw}", errors)
    for path in distribution.rglob("*"):
        if not path.is_file():
            continue
        require(not FORBIDDEN_NAME.search(path.name), f"forbidden native/license file in distribution: {path.relative_to(distribution)}", errors)


def verify_distribution_framework(distribution: Path, manifest: dict[str, Any], errors: list[str]) -> None:
    """Bind shipped archives to the tracked registry and their actual merged bytes."""
    registry_path = ROOT / "docs/reconstitution/framework-module-registry.json"
    shipped = distribution / "provenance/framework-module-registry.json"
    if not shipped.is_file():
        errors.append("distribution framework registry is missing")
        return
    require(shipped.read_bytes() == registry_path.read_bytes(), "distribution framework registry mismatch", errors)
    registry = load(registry_path)
    framework_names = [Path(item["output_jar"]).name for item in registry["modules"]]
    expected = ["modules/lims-owned-application.jar"] + ["modules/" + name for name in framework_names]
    require(len(framework_names) == 7 and len(set(framework_names)) == 7,
            "distribution framework registry module coverage", errors)
    actual = manifest.get("module_jars")
    if actual != expected:
        errors.append("distribution framework module coverage or order differs from registry")
        return
    source_hashes = manifest.get("source_jar_sha256", {})
    files = manifest.get("files", {})
    require(isinstance(source_hashes, dict) and set(source_hashes) == {Path(p).name for p in expected},
            "distribution framework source archive coverage", errors)
    if not isinstance(source_hashes, dict) or not isinstance(files, dict):
        errors.append("distribution framework file hashes are missing")
        return
    app_name = manifest.get("app_jar")
    require(app_name == "app/lims-owned-application.jar", "distribution merged application path", errors)
    selected: dict[str, tuple[str, bytes]] = {}
    factory_sources: list[str] = []
    overrides = manifest.get("resource_overrides", [])
    try:
        for relative in expected:
            path = distribution / relative
            if not path.is_file():
                errors.append("distribution framework module is missing: " + relative)
                return
            digest = sha256(path)
            require(files.get(relative) == digest and source_hashes.get(path.name) == digest,
                    "distribution framework module hash mismatch: " + relative, errors)
            with zipfile.ZipFile(path) as archive:
                names = archive.namelist()
                require(len(names) == len(set(names)), "distribution module duplicate ZIP entries: " + relative, errors)
                for name in names:
                    if name == "META-INF/spring.factories":
                        factory_sources.append(path.name)
                        continue
                    if name.endswith("/") or name in ("META-INF/MANIFEST.MF", "META-INF/spring.factories"):
                        continue
                    data = archive.read(name)
                    if name not in selected:
                        selected[name] = (path.name, data)
                    elif selected[name][1] != data:
                        prior_name, prior_data = selected[name]
                        record = {"entry": name, "selected_source": prior_name, "shadowed_source": path.name,
                                  "selected_sha256": hashlib.sha256(prior_data).hexdigest(),
                                  "shadowed_sha256": hashlib.sha256(data).hexdigest(),
                                  "policy": "first-source-wins for application resource overrides"}
                        require(not name.endswith(".class") and isinstance(overrides, list) and record in overrides,
                                "distribution framework resource override mismatch: " + name, errors)
        merged_path = distribution / "app/lims-owned-application.jar"
        if not merged_path.is_file():
            errors.append("distribution merged application is missing")
            return
        require(files.get("app/lims-owned-application.jar") == sha256(merged_path),
                "distribution merged application hash mismatch", errors)
        with zipfile.ZipFile(merged_path) as merged:
            names = merged.namelist()
            require(len(names) == len(set(names)), "distribution merged duplicate ZIP entries", errors)
            available = set(names)
            for name, (_, data) in selected.items():
                require(name in available and merged.read(name) == data,
                        "distribution merged framework entry missing or changed: " + name, errors)
            merged_resources = manifest.get("merged_resources", {})
            require(isinstance(merged_resources, dict)
                    and set(merged_resources) == ({"META-INF/spring.factories"} if factory_sources else set()),
                    "distribution merged resource coverage mismatch", errors)
            if not isinstance(merged_resources, dict):
                return
            if factory_sources:
                record = merged_resources.get("META-INF/spring.factories", {})
                require(isinstance(record, dict) and record.get("sources") == factory_sources,
                        "distribution merged spring.factories source coverage mismatch", errors)
            for name, record in merged_resources.items():
                require(name in available and isinstance(record, dict)
                        and hashlib.sha256(merged.read(name)).hexdigest() == record.get("sha256"),
                        "distribution merged resource hash mismatch: " + name, errors)
    except (OSError, zipfile.BadZipFile, KeyError, TypeError) as error:
        errors.append("distribution framework archive validation failed: " + str(error))


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run-jar", type=Path, help="optional externally injected stock run JAR")
    parser.add_argument("--application-report", type=Path, help="optional source-only application compile report")
    parser.add_argument("--distribution", type=Path, help="optional built distribution directory")
    parser.add_argument("--project-snapshot", type=Path, help="optional project closure snapshot override for regression tests")
    args = parser.parse_args(argv)
    root = ROOT.resolve()
    errors: list[str] = []
    try:
        artifact_sha = artifact_identity(root, args.run_jar.resolve() if args.run_jar else None, errors)
        verify_snapshots(root, artifact_sha, errors, args.project_snapshot.resolve() if args.project_snapshot else None)
        module_gate = subprocess.run(
            [sys.executable, str(root / "scripts/verify-reconstituted-framework-modules.py"),
             "--root", str(root / "reconstituted/framework"), "--compiled"],
            cwd=root, text=True, capture_output=True, check=False,
        )
        require(module_gate.returncode == 0, "reconstituted framework module verifier failed: " + (module_gate.stdout + module_gate.stderr)[-2000:], errors)
        application_report = args.application_report or root / "build/source-only-application/compile-report.json"
        if application_report.is_file():
            verify_application_report(root, application_report.resolve(), errors)
        if args.distribution:
            verify_distribution(args.distribution.resolve(), errors)
    except (OSError, ValueError, json.JSONDecodeError) as error:
        errors.append(str(error))
    result = {"status": "pass" if not errors else "fail", "artifact_sha256": artifact_sha, "errors": errors}
    print(json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True))
    return 0 if not errors else 1


if __name__ == "__main__":
    raise SystemExit(main())
