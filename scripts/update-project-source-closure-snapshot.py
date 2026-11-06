#!/usr/bin/env python3
"""Refresh the tracked diagnostic project-closure snapshot from a verified run."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--snapshot", type=Path, required=True)
    parser.add_argument("--commit", required=True)
    args = parser.parse_args()
    output = args.output_dir.resolve()
    snapshot = json.loads(args.snapshot.read_text(encoding="utf-8"))
    artifact = json.loads((output / "artifact-manifest.json").read_text(encoding="utf-8"))
    closure = json.loads((output / "source-closure.json").read_text(encoding="utf-8"))
    if closure.get("gate_status") != "project-source-compile-pass":
        raise SystemExit("source closure is not project-source-compile-pass")
    compile_report = closure.get("compile") or {}
    if compile_report.get("exit_code") != 0 or compile_report.get("error_count") != 0:
        raise SystemExit("source closure compile report is not clean")
    if compile_report.get("supplier_private_jars_on_classpath") is not False:
        raise SystemExit("source closure uses supplier/private classpath")
    snapshot["generated_from"] = "scripts/resolve-java-source-closure.py pilot --project-closure"
    snapshot["generated_at_utc"] = closure.get("generated_at")
    snapshot["source_commit"] = args.commit
    snapshot["stock_artifact"] = {
        "path": ".external-materials/gikam-third-czjky-14.1.5-run.jar",
        "bytes": artifact["artifact_bytes"],
        "sha256": artifact["artifact_sha256"],
    }
    snapshot["artifact_inventory"] = {
        key: artifact[key]
        for key in (
            "outer_member_count", "manifest_member_count", "nested_jar_count",
            "application_class_count", "application_top_level_class_count",
            "application_top_level_source_present", "application_top_level_source_missing",
            "private_framework_jar_count", "private_framework_class_entry_count",
            "private_framework_distinct_class_count", "private_framework_duplicate_class_count",
            "public_dependency_jar_count",
        )
    }
    snapshot["framework_closure"] = {
        "direct_project_import_count": len(closure.get("project_imports", {})),
        "framework_input_jars": closure.get("framework_input_jars", []),
        "closure_class_count": closure.get("closure_class_count"),
        "generated_source_file_count": closure.get("source_file_count"),
        "source_decompilers": closure.get("source_decompilers", {}),
        "fallback_compile_round_count": closure.get("fallback_compile_round_count", 0),
        "fallback_replacement_count": closure.get("fallback_replacement_count", 0),
        "transformed_source_count": closure.get("transformed_source_count", 0),
        "normalized_source_count": closure.get("normalized_source_count", 0),
        "source_tree_sha256": closure.get("source_tree_sha256"),
        "missing_external_framework_classes": closure.get("missing_external_framework_classes", []),
    }
    snapshot["project_compile"] = {
        "project_source_file_count": closure.get("project_source_file_count"),
        "provided_project_class_count": closure.get("provided_project_class_count"),
        "compile_target_source_file_count": closure.get("compile_target_source_file_count"),
        "java_version": compile_report.get("java_version"),
        "exit_code": compile_report.get("exit_code"),
        "error_count": compile_report.get("error_count"),
        "failed_source_count": compile_report.get("failed_source_count"),
        "missing_symbol_queue": compile_report.get("missing_symbol_queue", []),
        "source_only_framework_classpath": compile_report.get("source_only_framework_classpath"),
        "supplier_private_jars_on_classpath": compile_report.get("supplier_private_jars_on_classpath"),
        "status": compile_report.get("status"),
    }
    snapshot["diagnostic_output_sha256"] = {
        name: sha256(output / name)
        for name in ("artifact-manifest.json", "source-closure.json", "javac.log")
        if (output / name).is_file()
    }
    args.snapshot.write_text(json.dumps(snapshot, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print(json.dumps({
        "status": "pass",
        "snapshot": str(args.snapshot),
        "artifact_sha256": artifact["artifact_sha256"],
        "gate_status": closure["gate_status"],
        "closure_class_count": closure["closure_class_count"],
        "generated_source_file_count": closure["source_file_count"],
        "compile_error_count": compile_report["error_count"],
    }, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
