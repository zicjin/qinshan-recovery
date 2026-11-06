#!/usr/bin/env python3
"""Verify tracked reconstituted module sources, provenance and optional jars."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import re
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
PRIVATE_NAME_MARKERS = (
    "gikam-core-",
    "lims2-report-template-",
    "sunway-app.",
    "license.lic",
    "vendor.lic",
)


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def tree_digest(records: list[dict[str, object]]) -> str:
    digest = hashlib.sha256()
    for record in sorted(records, key=lambda item: str(item["path"])):
        digest.update(str(record["path"]).encode("utf-8"))
        digest.update(b"\0")
        digest.update(bytes.fromhex(str(record["sha256"])))
    return digest.hexdigest()


def require(condition: bool, message: str, errors: list[str]) -> None:
    if not condition:
        errors.append(message)


def pom_module_names(path: Path) -> list[str]:
    root = ET.parse(path).getroot()
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    return [item.text for item in root.findall("m:modules/m:module", ns)]


def pom_dependencies(path: Path) -> list[str]:
    root = ET.parse(path).getroot()
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    return [
        item.text
        for item in root.findall("m:dependencies/m:dependency/m:artifactId", ns)
    ]


def registered_plugin_seed_sha256() -> str:
    manifest = ROOT / "build-support" / "t02-build-plugin-seed.sha256"
    for line in manifest.read_text(encoding="utf-8").splitlines():
        if not line or line.startswith("#"):
            continue
        expected, relative = line.split(maxsplit=1)
        if relative.endswith("maven-build-plugin-seed.tar.gz"):
            return expected
    raise ValueError("Maven plugin seed is not registered")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=ROOT / "reconstituted" / "framework")
    parser.add_argument("--compiled", action="store_true")
    args = parser.parse_args()
    root = args.root.resolve()
    errors: list[str] = []
    registry_path = (
        ROOT / "docs" / "reconstitution" / "framework-module-registry.json"
    )
    registry = json.loads(registry_path.read_text(encoding="utf-8"))
    require(registry["schema"] == "reconstituted-framework-module-registry-v1", "registry schema", errors)
    require(registry["module_count"] == 7, "module count is not seven", errors)
    require(registry["candidate_group_count"] == 8, "candidate count changed", errors)
    require(registry["source_file_count"] == 4576, "source total mismatch", errors)
    require(registry["resource_file_count"] == 9426, "resource total mismatch", errors)
    require(registry["runtime_model"]["boundary_process"] is False, "boundary process flag", errors)
    require(registry["runtime_model"]["internal_http_bridge"] is False, "HTTP bridge flag", errors)
    require(registry["native_removal"]["status"] == "pass", "native removal status", errors)
    require(registry["native_removal"]["forbidden_native_resources_excluded"] == 5, "native exclusion count", errors)
    require(registry["native_removal"]["native_declarations"] == 0, "native declarations remain", errors)
    require(registry["native_removal"]["native_loader_files"] == 0, "native loader references remain", errors)
    require(registry["native_removal"]["valid_license_mock_added"] is False, "license mock was added", errors)

    allowlist_path = root / "public-dependency-allowlist.json"
    allowlist = json.loads(allowlist_path.read_text(encoding="utf-8"))
    require(allowlist["schema"] == "pinned-public-dependency-allowlist-v1", "allowlist schema", errors)
    require(len(allowlist["entries"]) == 386, "public dependency count", errors)
    excluded_vendor = [
        item for item in allowlist["entries"] if item.get("prepare_excluded")
    ]
    require(
        [item.get("filename") for item in excluded_vendor]
        == ["private-sdk-RELEASE.jar"],
        "vendored SDK exclusion policy",
        errors,
    )
    require(
        all(item.get("final_distribution_allowed") is False for item in excluded_vendor),
        "vendored SDK final-distribution policy",
        errors,
    )
    for item in allowlist["entries"]:
        filename = str(item["filename"]).lower()
        require(
            not any(marker in filename for marker in PRIVATE_NAME_MARKERS),
            f"private dependency in public allowlist: {item['filename']}",
            errors,
        )

    modules = {item["module"]: item for item in registry["modules"]}
    require(set(modules) == set(pom_module_names(root / "pom.xml")), "parent POM modules", errors)
    source_total = 0
    resource_total = 0
    for module, summary in modules.items():
        module_root = root / module
        provenance = json.loads(
            (module_root / "module-provenance.json").read_text(encoding="utf-8")
        )
        require(provenance["module"] == module, f"{module}: provenance module", errors)
        require(provenance["status"] == "assembled-source-only", f"{module}: provenance status", errors)
        records = provenance["source_files"]
        source_total += len(records)
        require(len(records) == summary["source_file_count"], f"{module}: source count", errors)
        require(tree_digest(records) == provenance["source_tree_sha256"], f"{module}: source tree", errors)
        for record in records:
            path = module_root / str(record["path"])
            require(path.is_file(), f"{module}: missing {record['path']}", errors)
            if path.is_file():
                require(sha256_file(path) == record["sha256"], f"{module}: hash {record['path']}", errors)
            if args.compiled:
                require(
                    record.get("compile_status") == "pass",
                    f"{module}: compile status {record['path']}",
                    errors,
                )
            else:
                require(
                    record.get("compile_status") in {"pending-module-assembly", "pass"},
                    f"{module}: compile status {record['path']}",
                    errors,
                )
            require(record.get("source_jar"), f"{module}: source jar {record['path']}", errors)
            require(record.get("source_class_path"), f"{module}: source class {record['path']}", errors)
            require(
                isinstance(record.get("transformations"), list),
                f"{module}: transformations {record['path']}",
                errors,
            )
            if record.get("source_jar") == "tracked-override":
                require(record.get("decompiler") == "tracked-override", f"{module}: override origin {record['path']}", errors)
            else:
                require(record.get("class_sha256"), f"{module}: class hash {record['path']}", errors)
                require(record.get("source_sha256_before_transform"), f"{module}: pre-transform hash {record['path']}", errors)
                require(record.get("decompiler"), f"{module}: decompiler {record['path']}", errors)
                require(record.get("decompiler_version"), f"{module}: decompiler version {record['path']}", errors)
        module_allowlist = json.loads(
            (module_root / "public-dependency-allowlist.json").read_text(encoding="utf-8")
        )
        require(module_allowlist == allowlist, f"{module}: allowlist mismatch", errors)
        dependencies = pom_dependencies(module_root / "pom.xml")
        for dependency in summary["dependencies"]:
            require(dependency in dependencies, f"{module}: missing dependency {dependency}", errors)
        forbidden = json.loads(
            (module_root / "forbidden-content-report.json").read_text(encoding="utf-8")
        )
        require(
            forbidden["sunway_license_payload_retained"] is False,
            f"{module}: license payload retained",
            errors,
        )
        require(
            forbidden["supplier_private_jar_retained"] is False,
            f"{module}: supplier private jar retained",
            errors,
        )
        require(forbidden["status"] == "pass", f"{module}: forbidden content status", errors)
        require(not forbidden["native_declaration_files"], f"{module}: native declarations", errors)
        require(not forbidden["loader_reference_files"], f"{module}: loader references", errors)
        require(not forbidden["forbidden_native_files"], f"{module}: native resource files", errors)
        if args.compiled:
            compile_report = json.loads(
                (module_root / "compile-report.json").read_text(encoding="utf-8")
            )
            require(compile_report["status"] == "pass", f"{module}: compile status", errors)
            require(compile_report["supplier_private_jars_on_classpath"] is False, f"{module}: private classpath", errors)
            require(
                compile_report.get("maven_repository_mode") == "isolated-hash-pinned-plugin-seed",
                f"{module}: Maven repository mode",
                errors,
            )
            require(
                compile_report.get("maven_plugin_seed_sha256") == registered_plugin_seed_sha256(),
                f"{module}: Maven plugin seed identity",
                errors,
            )
            jar = root / compile_report["output_jar"]
            require(jar.is_file(), f"{module}: missing jar", errors)
            if jar.is_file():
                require(sha256_file(jar) == compile_report["output_jar_sha256"], f"{module}: jar hash", errors)

    for module, summary in modules.items():
        resource_manifest = json.loads(
            (root / module / "resource-provenance.json").read_text(encoding="utf-8")
        )
        resource_records = resource_manifest["resources"]
        resource_total += len(resource_records)
        require(
            tree_digest(resource_records) == resource_manifest["resource_tree_sha256"],
            f"{module}: resource tree",
            errors,
        )
        for record in resource_records:
            require(
                not str(record["path"]).startswith("third-party/sunway-app/"),
                f"{module}: forbidden native resource {record['path']}",
                errors,
            )
            require(record.get("source_jar"), f"{module}: resource source jar {record['path']}", errors)
            require(
                record.get("compile_status") == "not-applicable-resource",
                f"{module}: resource compile status {record['path']}",
                errors,
            )
            path = root / module / "src/main/resources" / str(record["path"])
            require(path.is_file(), f"{module}: missing resource {record['path']}", errors)
            if path.is_file():
                require(sha256_file(path) == record.get("destination_sha256", record["sha256"]), f"{module}: resource hash {record['path']}", errors)
    require(source_total == registry["source_file_count"], "registry source total", errors)
    require(resource_total == registry["resource_file_count"], "registry resource total", errors)

    forbidden_paths = []
    for path in root.rglob("*"):
        if path.is_file() and (
            path.name in {"sunway-app.so", "sunway-app.dll", "sunway-app.o", "license.lic", "vendor.lic"}
            or re.match(r"gikam-core-.*\.jar$", path.name)
        ):
            forbidden_paths.append(str(path.relative_to(root)))
    require(not forbidden_paths, f"forbidden source/build inputs: {forbidden_paths}", errors)

    if errors:
        print(json.dumps({"status": "fail", "errors": errors}, indent=2))
        return 1
    print(
        json.dumps(
            {
                "status": "pass",
                "mode": "compiled" if args.compiled else "source",
                "module_count": len(modules),
                "source_file_count": source_total,
                "resource_file_count": resource_total,
                "public_dependency_count": len(allowlist["entries"]),
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
