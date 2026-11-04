#!/usr/bin/env python3
"""Verify artifact and source-reconstitution pilot reports."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re
import sys
from typing import Mapping


PRIVATE_CLASSPATH_RE = re.compile(
    r"(?:BOOT-INF/lib/)?gikam-core-[^:\s]+\.jar", re.IGNORECASE
)
FORBIDDEN_DISTRIBUTION_RE = re.compile(
    r"(?:sunway-app\.so|license\.lic|vendor\.lic|native.?loader|machine.?fingerprint)",
    re.IGNORECASE,
)


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def load_json(path: Path) -> dict[str, object]:
    return json.loads(path.read_text(encoding="utf-8"))


def require(condition: bool, message: str, errors: list[str]) -> None:
    if not condition:
        errors.append(message)


def verify_manifest(manifest: Mapping[str, object], report: Mapping[str, object],
                    output_dir: Path, errors: list[str]) -> None:
    require(
        manifest.get("schema") == "supplier-artifact-manifest-v1",
        "unexpected artifact manifest schema",
        errors,
    )
    require(
        manifest.get("artifact_sha256") == report.get("run_jar_sha256"),
        "closure report and artifact manifest SHA-256 differ",
        errors,
    )
    members = manifest.get("members")
    require(isinstance(members, list), "artifact manifest members missing", errors)
    if not isinstance(members, list):
        return
    require(
        int(manifest.get("manifest_member_count", -1)) == len(members),
        "manifest member count mismatch",
        errors,
    )
    nested = manifest.get("nested_jars")
    require(isinstance(nested, list), "nested JAR inventory missing", errors)
    if isinstance(nested, list):
        require(
            int(manifest.get("nested_jar_count", -1)) == len(nested),
            "nested JAR count mismatch",
            errors,
        )
        require(
            all(item.get("sha256") for item in nested if isinstance(item, dict)),
            "nested JAR without SHA-256",
            errors,
        )
        require(
            sum(
                1
                for item in nested
                if isinstance(item, dict) and item.get("private_framework")
            )
            == int(manifest.get("private_framework_jar_count", -1)),
            "private framework JAR count mismatch",
            errors,
        )
    for index, item in enumerate(members):
        if not isinstance(item, dict):
            errors.append(f"manifest member {index} is not an object")
            continue
        require(bool(item.get("member_path")), f"member {index} has no path", errors)
        require(bool(item.get("member_sha256")), f"member {index} has no SHA-256", errors)
        require(bool(item.get("kind")), f"member {index} has no kind", errors)
        if item.get("source_recovery_required"):
            require(
                item.get("final_distribution_allowed") is False,
                f"recovery input is distribution-allowed: {item.get('member_path')}",
                errors,
            )
        if FORBIDDEN_DISTRIBUTION_RE.search(str(item.get("member_path", ""))):
            require(
                item.get("final_distribution_allowed") is False,
                f"forbidden member is distribution-allowed: {item.get('member_path')}",
                errors,
            )
    app_missing = int(manifest.get("application_top_level_source_missing", -1))
    require(app_missing >= 0, "application source mapping count missing", errors)


def verify_sources(report: Mapping[str, object], output_dir: Path,
                   errors: list[str]) -> None:
    provenance = report.get("provenance")
    require(isinstance(provenance, list), "source provenance missing", errors)
    if not isinstance(provenance, list):
        return
    require(
        int(report.get("source_file_count", -1)) == len(provenance),
        "source file count mismatch",
        errors,
    )
    tree = hashlib.sha256()
    for index, item in enumerate(provenance):
        if not isinstance(item, dict):
            errors.append(f"provenance row {index} is not an object")
            continue
        source_path = output_dir / str(item.get("generated_source_path", ""))
        require(source_path.is_file(), f"generated source missing: {source_path}", errors)
        if source_path.is_file():
            require(
                sha256_file(source_path) == item.get("source_sha256"),
                f"generated source hash mismatch: {source_path}",
                errors,
            )
            tree.update(str(item.get("generated_source_path", "")).encode("utf-8"))
            tree.update(b"\0")
            tree.update(bytes.fromhex(str(item.get("source_sha256", ""))))
        require(bool(item.get("source_jar")), f"provenance {index} missing source_jar", errors)
        require(
            bool(item.get("source_class_path")),
            f"provenance {index} missing source_class_path",
            errors,
        )
        require(
            bool(item.get("class_sha256")),
            f"provenance {index} missing class_sha256",
            errors,
        )
        require(
            bool(item.get("decompiler")),
            f"provenance {index} missing decompiler",
            errors,
        )
        require(
            item.get("decompiler_exit_code") == 0,
            f"provenance {index} records a failed decompiler",
            errors,
        )
        require(
            bool(item.get("source_sha256_before_transform")),
            f"provenance {index} missing pre-transform source hash",
            errors,
        )
        require(
            isinstance(item.get("transformations"), list),
            f"provenance {index} missing transformations list",
            errors,
        )
    require(
        tree.hexdigest() == report.get("source_tree_sha256"),
        "generated source tree hash mismatch",
        errors,
    )


def verify_web_resources(report: Mapping[str, object], output_dir: Path,
                         errors: list[str]) -> None:
    expected = int(report.get("web_resource_extracted_count", -1))
    manifest_path = output_dir / "web-resources.json"
    if expected == 0:
        return
    require(manifest_path.is_file(), "web resource provenance missing", errors)
    if not manifest_path.is_file():
        return
    manifest = load_json(manifest_path)
    resources = manifest.get("resources")
    require(isinstance(resources, list), "web resource rows missing", errors)
    if not isinstance(resources, list):
        return
    require(len(resources) == expected, "web resource count mismatch", errors)
    for index, item in enumerate(resources):
        if not isinstance(item, dict):
            errors.append(f"web resource row {index} is not an object")
            continue
        relative = Path(str(item.get("path", "")))
        require(not relative.is_absolute(), f"absolute web resource path {relative}", errors)
        source = output_dir / "resources" / relative
        require(source.is_file(), f"web resource missing: {source}", errors)
        if source.is_file():
            require(
                sha256_file(source) == item.get("sha256"),
                f"web resource hash mismatch: {source}",
                errors,
            )


def verify_compile(report: Mapping[str, object], output_dir: Path,
                   errors: list[str]) -> None:
    compile_report = report.get("compile")
    require(isinstance(compile_report, dict), "compile report missing", errors)
    if not isinstance(compile_report, dict):
        return
    require(compile_report.get("java_version") == "8", "compile did not target Java 8", errors)
    require(
        compile_report.get("source_only_framework_classpath") is True,
        "framework classpath is not source-only",
        errors,
    )
    require(
        compile_report.get("supplier_private_jars_on_classpath") is False,
        "supplier private JAR is on javac classpath",
        errors,
    )
    javac_log = output_dir / "javac.log"
    require(javac_log.is_file(), "javac log missing", errors)
    if javac_log.is_file():
        text = javac_log.read_text(encoding="utf-8", errors="replace")
        require(
            not PRIVATE_CLASSPATH_RE.search(text),
            "javac log references a private gikam-core JAR",
            errors,
        )
    gate = report.get("gate_status")
    require(
        gate in {
            "pilot-compile-pass",
            "pilot-compile-failed",
            "project-source-compile-pass",
            "project-source-compile-failed",
        },
        "unexpected source gate status",
        errors,
    )
    if gate in {"pilot-compile-pass", "project-source-compile-pass"}:
        require(compile_report.get("exit_code") == 0, "compile pass has nonzero exit", errors)
        require(compile_report.get("error_count") == 0, "compile pass has errors", errors)
        require(
            compile_report.get("missing_symbol_queue") == [],
            "compile pass has unresolved missing symbols",
            errors,
        )


def verify(run_jar: Path, output_dir: Path) -> dict[str, object]:
    errors: list[str] = []
    manifest_path = output_dir / "artifact-manifest.json"
    report_path = output_dir / "source-closure.json"
    require(run_jar.is_file(), f"run JAR missing: {run_jar}", errors)
    require(manifest_path.is_file(), f"artifact manifest missing: {manifest_path}", errors)
    require(report_path.is_file(), f"source closure report missing: {report_path}", errors)
    if errors:
        return {"status": "fail", "errors": errors}
    manifest = load_json(manifest_path)
    report = load_json(report_path)
    if run_jar.is_file():
        require(
            sha256_file(run_jar) == manifest.get("artifact_sha256"),
            "run JAR SHA-256 mismatch",
            errors,
        )
    verify_manifest(manifest, report, output_dir, errors)
    verify_sources(report, output_dir, errors)
    verify_web_resources(report, output_dir, errors)
    verify_compile(report, output_dir, errors)
    return {
        "status": "pass" if not errors else "fail",
        "artifact_sha256": manifest.get("artifact_sha256"),
        "gate_status": report.get("gate_status"),
        "closure_class_count": report.get("closure_class_count"),
        "source_file_count": report.get("source_file_count"),
        "compile": report.get("compile", {}),
        "missing_external_framework_class_count": len(
            report.get("missing_external_framework_classes", [])
        ),
        "errors": errors,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run-jar", required=True)
    parser.add_argument("--output-dir", required=True)
    parser.add_argument("--json-out")
    args = parser.parse_args()
    result = verify(Path(args.run_jar).resolve(), Path(args.output_dir).resolve())
    text = json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True) + "\n"
    if args.json_out:
        Path(args.json_out).write_text(text, encoding="utf-8")
    sys.stdout.write(text)
    return 0 if result["status"] == "pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
