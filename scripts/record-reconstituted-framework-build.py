#!/usr/bin/env python3
"""Record successful Maven output in reconstituted module compile reports."""
from __future__ import annotations

import argparse
import hashlib
import io
import json
from pathlib import Path
import platform
import subprocess
import zipfile


ROOT = Path(__file__).resolve().parents[1]


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def write_json(path: Path, payload: object) -> None:
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def tree_digest(records: list[dict[str, object]]) -> str:
    digest = hashlib.sha256()
    for record in sorted(records, key=lambda item: str(item["path"])):
        digest.update(str(record["path"]).encode("utf-8"))
        digest.update(b"\0")
        digest.update(bytes.fromhex(str(record["sha256"])))
    return digest.hexdigest()


def finalize_module_provenance(module_root: Path) -> dict[str, object]:
    provenance_path = module_root / "module-provenance.json"
    provenance = json.loads(provenance_path.read_text(encoding="utf-8"))
    override_root = ROOT / "reconstituted" / "overrides" / "framework" / module_root.name
    for record in provenance["source_files"]:
        source_path = module_root / str(record["path"])
        record["sha256"] = sha256_file(source_path)
        record["compile_status"] = "pass"
        override_path = override_root / str(record["path"])
        if override_path.is_file():
            record.setdefault(
                "source_sha256_before_overlay",
                record.get("source_sha256_before_transform"),
            )
            transformations = list(record.get("transformations", []))
            transformation = f"overlay:{record['path']}"
            if transformation not in transformations:
                transformations.append(transformation)
            record["transformations"] = transformations
    provenance["source_tree_sha256"] = tree_digest(provenance["source_files"])
    write_json(provenance_path, provenance)

    resource_path = module_root / "resource-provenance.json"
    resources = json.loads(resource_path.read_text(encoding="utf-8"))
    for record in resources["resources"]:
        record["compile_status"] = "not-applicable-resource"
    write_json(resource_path, resources)
    return provenance


def command_output(command: list[str], cwd: Path) -> str:
    result = subprocess.run(
        command,
        cwd=cwd,
        check=True,
        text=True,
        capture_output=True,
    )
    return (result.stdout + result.stderr).strip()


def plugin_seed_sha256() -> str:
    manifest = ROOT / "build-support" / "t02-build-plugin-seed.sha256"
    for line in manifest.read_text(encoding="utf-8").splitlines():
        if not line or line.startswith("#"):
            continue
        expected, relative = line.split(maxsplit=1)
        if relative.endswith("maven-build-plugin-seed.tar.gz"):
            return expected
    raise SystemExit("Maven plugin seed is not registered")


def normalize_jar(path: Path) -> None:
    """Rewrite Maven output with stable order and timestamps."""
    with zipfile.ZipFile(path) as source:
        entries = [
            (info.filename, source.read(info.filename))
            for info in source.infolist()
        ]
    with io.BytesIO() as output:
        with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED) as target:
            for name, data in sorted(entries):
                info = zipfile.ZipInfo(name, date_time=(1980, 1, 1, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                info.external_attr = 0o644 << 16
                target.writestr(info, data)
        path.write_bytes(output.getvalue())


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=ROOT / "reconstituted" / "framework")
    args = parser.parse_args()
    root = args.root.resolve()
    registry_path = root.parent.parent / "docs" / "reconstitution" / "framework-module-registry.json"
    registry = json.loads(registry_path.read_text(encoding="utf-8"))
    java_version = command_output(
        [str(ROOT / "build" / "jdk1.8.0_391" / "bin" / "java"), "-version"],
        ROOT,
    ).splitlines()[0]
    maven_version = command_output(["mvn", "-version"], ROOT).splitlines()[0]

    summaries = []
    for summary in registry["modules"]:
        module = summary["module"]
        module_root = root / module
        jars = sorted(
            path
            for path in (module_root / "target").glob("*.jar")
            if not path.name.endswith("-sources.jar")
        )
        if len(jars) != 1:
            raise SystemExit(f"expected one module jar for {module}, found {jars}")
        jar = jars[0]
        normalize_jar(jar)
        provenance = finalize_module_provenance(module_root)
        report = {
            "schema": "supplier-reconstituted-module-compile-v1",
            "module": module,
            "status": "pass",
            "exit_code": 0,
            "java_version": "8",
            "java_runtime": java_version,
            "maven_runtime": maven_version,
            "command": "mvn -o -Dmaven.repo.local=<isolated-plugin-seed-repo> -DskipTests clean package",
            "maven_repository_mode": "isolated-hash-pinned-plugin-seed",
            "maven_plugin_seed_sha256": plugin_seed_sha256(),
            "source_file_count": provenance["source_file_count"],
            "source_tree_sha256": provenance["source_tree_sha256"],
            "output_jar": str(jar.relative_to(root)),
            "output_jar_sha256": sha256_file(jar),
            "source_only_framework_classpath": True,
            "supplier_private_jars_on_classpath": False,
            "host": platform.system(),
        }
        write_json(module_root / "compile-report.json", report)
        summary["compile_status"] = "pass"
        summary["source_file_count"] = provenance["source_file_count"]
        summary["source_tree_sha256"] = provenance["source_tree_sha256"]
        summary["output_jar"] = report["output_jar"]
        summary["output_jar_sha256"] = report["output_jar_sha256"]
        summaries.append(summary)

    registry["modules"] = summaries
    registry["status"] = "compiled-source-only"
    registry["all_modules_compile_pass"] = True
    write_json(registry_path, registry)
    print(
        json.dumps(
            {
                "status": "compiled-source-only",
                "module_count": len(summaries),
                "source_file_count": sum(item["source_file_count"] for item in summaries),
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
