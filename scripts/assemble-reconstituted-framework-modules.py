#!/usr/bin/env python3
"""Assemble recovered framework source into deterministic owned modules.

The supplier run JAR is not copied or placed on a runtime classpath. This tool
consumes the source-closure report produced by resolve-java-source-closure.py,
materializes tracked generated Java/resources, and emits per-module provenance,
public-dependency allowlists, POMs and open forbidden-content reports.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import re
import shutil
from typing import Iterable, Mapping, Sequence
from xml.sax.saxutils import escape


ROOT = Path(__file__).resolve().parents[1]

MODULE_SOURCE_JARS = {
    "owned-core-common": [
        "gikam-core-base-interface-14.1.5.jar",
        "gikam-core-common-14.1.5.jar",
    ],
    "owned-core-base-service": [
        "gikam-core-base-service-14.1.5.jar",
    ],
    "owned-core-web": [],
    "owned-core-ext": [
        "gikam-core-ext-interface-14.1.5.jar",
        "gikam-core-ext-service-14.1.5.jar",
        "gikam-core-file-ftp-14.1.5.jar",
    ],
    "owned-core-lcdp": [
        "gikam-core-lcdp-interface-14.1.5.jar",
        "gikam-core-lcdp-service-14.1.5.jar",
    ],
    "owned-core-report": [
        "gikam-core-fr-interface-14.1.5.jar",
        "gikam-core-fr-service-base-14.1.5.jar",
        "gikam-core-report-interface-14.1.5-pageoffice6.jar",
        "gikam-core-report-service-14.1.5-pageoffice6.jar",
        "lims2-report-template-14.1.5.jar",
    ],
    "owned-core-schedule": [
        "gikam-core-schedule-interface-14.1.5.jar",
        "gikam-core-schedule-service-14.1.5.jar",
    ],
}

MODULE_DEPENDENCIES = {
    "owned-core-common": [],
    "owned-core-base-service": ["owned-core-common"],
    "owned-core-web": ["owned-core-common"],
    "owned-core-ext": ["owned-core-common"],
    "owned-core-lcdp": ["owned-core-common"],
    "owned-core-report": ["owned-core-common"],
    "owned-core-schedule": ["owned-core-common"],
}

MODULE_REASONS = {
    "owned-core-common": (
        "Merges the common foundation and its immediately downstream base-interface "
        "JAR to avoid a two-module cycle-free core with no independent runtime boundary."
    ),
    "owned-core-base-service": "Generic persistence and service behavior.",
    "owned-core-web": "Original Thymeleaf/static/MyBatis web resources at their source paths.",
    "owned-core-ext": "Extension interfaces, extension services and FTP support.",
    "owned-core-lcdp": "LCDP interfaces and services.",
    "owned-core-report": "FR, report, PageOffice and LIMS report-template source/resources.",
    "owned-core-schedule": "Schedule interfaces and services.",
}

SOURCE_JAR_TO_MODULE = {
    source_jar: module
    for module, source_jars in MODULE_SOURCE_JARS.items()
    for source_jar in source_jars
}

NATIVE_DECLARATION_RE = re.compile(
    r"^\s*(?:public|protected|private|static|final\s+)*native\s+",
    re.MULTILINE,
)
LOADER_REFERENCE_RE = re.compile(
    r"SunwayLibraryExtractor|ThirdPartyType|System\.load(?:Library)?\(|"
    r"third-party/sunway-app|sunway-app\.(?:so|dll|o)",
    re.IGNORECASE,
)
THIRD_PARTY_LICENSE_RE = re.compile(
    r"(?:^|/)(?:LICENSE|LICENSE\.(?:md|txt)|[^/]+\.LICENSE\.txt)$",
    re.IGNORECASE,
)
FORBIDDEN_NATIVE_SUFFIXES = {".so", ".dll", ".o"}


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def write_json(path: Path, payload: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def tree_digest(records: Iterable[Mapping[str, object]]) -> str:
    digest = hashlib.sha256()
    for record in sorted(records, key=lambda item: str(item["path"])):
        digest.update(str(record["path"]).encode("utf-8"))
        digest.update(b"\0")
        digest.update(bytes.fromhex(str(record["sha256"])))
    return digest.hexdigest()


def safe_artifact_id(filename: str) -> str:
    value = re.sub(r"[^A-Za-z0-9_.-]+", "-", filename.removesuffix(".jar"))
    return value.strip("-.")


def render_module_pom(
    module: str,
    public_dependencies: Sequence[Mapping[str, object]],
) -> str:
    dependencies: list[str] = []
    for dependency in MODULE_DEPENDENCIES[module]:
        dependencies.append(
            """
    <dependency>
      <groupId>com.sunwayworld.owned.reconstituted</groupId>
      <artifactId>{artifact}</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>""".format(artifact=escape(dependency))
        )
    for item in public_dependencies:
        filename = str(item["filename"])
        dependencies.append(
            """
    <dependency>
      <groupId>external.pinned</groupId>
      <artifactId>{artifact}</artifactId>
      <version>1</version>
      <scope>system</scope>
      <systemPath>${{project.basedir}}/../../../build/reconstituted-public-deps/{filename}</systemPath>
    </dependency>""".format(
                artifact=escape(safe_artifact_id(filename)),
                filename=escape(filename),
            )
        )
    return """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.sunwayworld.owned.reconstituted</groupId>
    <artifactId>lims-reconstituted-framework-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
  </parent>
  <artifactId>{module}</artifactId>
  <name>{module}</name>
  <packaging>jar</packaging>
  <dependencies>{dependencies}
  </dependencies>
</project>
""".format(module=escape(module), dependencies="".join(dependencies))


def render_parent_pom(modules: Iterable[str]) -> str:
    module_lines = "\n".join(f"    <module>{escape(module)}</module>" for module in modules)
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.sunwayworld.owned.reconstituted</groupId>
  <artifactId>lims-reconstituted-framework-parent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>
  <modules>
{module_lines}
  </modules>
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.1</version>
          <configuration>
            <source>1.8</source>
            <target>1.8</target>
            <encoding>UTF-8</encoding>
          </configuration>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>
"""


def normalized_relative_generated_path(value: str) -> PurePosixPath:
    path = PurePosixPath(value)
    if path.parts[:1] != ("src",):
        raise ValueError(f"unexpected generated source path: {value}")
    return PurePosixPath("src/main/java", *path.parts[1:])


def scan_forbidden(
    module: str,
    source_files: list[Path],
    resource_files: list[Path],
) -> dict[str, object]:
    native_declarations = []
    loader_references = []
    for path in source_files:
        text = path.read_text(encoding="utf-8", errors="replace")
        if NATIVE_DECLARATION_RE.search(text):
            native_declarations.append(str(path))
        if LOADER_REFERENCE_RE.search(text):
            loader_references.append(str(path))
    third_party_license_documents = []
    forbidden_native_files = []
    for path in resource_files:
        relative = path.as_posix()
        if THIRD_PARTY_LICENSE_RE.search(relative):
            third_party_license_documents.append(relative)
        if path.suffix.lower() in FORBIDDEN_NATIVE_SUFFIXES:
            forbidden_native_files.append(relative)
    return {
        "schema": "supplier-reconstituted-module-forbidden-content-v1",
        "module": module,
        "status": "open-native-removal" if native_declarations or loader_references else "pass",
        "native_declaration_files": sorted(native_declarations),
        "loader_reference_files": sorted(loader_references),
        "third_party_license_documents": sorted(third_party_license_documents),
        "forbidden_native_files": sorted(forbidden_native_files),
        "sunway_license_payload_retained": False,
        "supplier_private_jar_retained": False,
        "policy": {
            "native_declaration_files": "must be replaced or removed before final distribution",
            "loader_reference_files": "must be removed before final distribution",
            "third_party_license_documents": "allowlisted documentation only; never Sunway runtime input",
            "forbidden_native_files": "must be absent before final distribution",
        },
    }


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source-report", type=Path, required=True)
    parser.add_argument("--source-root", type=Path, required=True)
    parser.add_argument("--resource-root", type=Path, required=True)
    parser.add_argument("--resource-provenance", type=Path, required=True)
    parser.add_argument(
        "--output-root", type=Path, default=ROOT / "reconstituted" / "framework"
    )
    parser.add_argument(
        "--registry-output",
        type=Path,
        default=ROOT / "docs" / "reconstitution" / "framework-module-registry.json",
    )
    parser.add_argument(
        "--allowlist-output",
        type=Path,
        default=ROOT / "reconstituted" / "framework" / "public-dependency-allowlist.json",
    )
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args(argv)

    report = json.loads(args.source_report.read_text(encoding="utf-8"))
    if report.get("gate_status") != "all-framework-source-compile-pass":
        raise SystemExit("source report is not an all-framework compile pass")
    if report["compile"]["exit_code"] != 0 or report["compile"]["error_count"] != 0:
        raise SystemExit("source report compile is not clean")
    if report["compile"]["missing_symbol_queue"]:
        raise SystemExit("source report has missing symbols")
    if report["compile"]["supplier_private_jars_on_classpath"] is not False:
        raise SystemExit("source report used supplier/private classpath")

    output_root = args.output_root.resolve()
    if output_root.exists():
        if not args.overwrite:
            raise SystemExit(f"output root already exists: {output_root}")
        shutil.rmtree(output_root)
    output_root.mkdir(parents=True)

    public_dependencies = sorted(
        report["public_dependencies"], key=lambda item: str(item["filename"])
    )
    allowlist = {
        "schema": "pinned-public-dependency-allowlist-v1",
        "run_jar_sha256": report["run_jar_sha256"],
        "source": "stock BOOT-INF/lib public-dependency classification",
        "runtime_dependency_on_supplier_run_jar": False,
        "entries": public_dependencies,
    }
    write_json(args.allowlist_output.resolve(), allowlist)
    write_json(output_root / "public-dependency-allowlist.json", allowlist)

    source_records_by_module: dict[str, list[dict[str, object]]] = {
        module: [] for module in MODULE_SOURCE_JARS
    }
    assigned_class_paths: set[str] = set()
    for record in report["provenance"]:
        source_jar = Path(str(record["source_jar"])).name
        module = SOURCE_JAR_TO_MODULE.get(source_jar)
        if not module:
            raise SystemExit(f"unassigned source JAR: {source_jar}")
        source_class_path = str(record["source_class_path"])
        if source_class_path in assigned_class_paths:
            raise SystemExit(f"duplicate class path: {source_class_path}")
        assigned_class_paths.add(source_class_path)
        source_relative = normalized_relative_generated_path(
            str(record["generated_source_path"])
        )
        source_path = args.source_root.resolve() / PurePosixPath(*source_relative.parts[3:])
        module_relative = PurePosixPath(*source_relative.parts)
        destination = output_root / module / module_relative
        if not source_path.is_file():
            raise SystemExit(f"missing generated source: {source_path}")
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source_path, destination)
        actual_sha256 = sha256_file(destination)
        if actual_sha256 != record["source_sha256"]:
            raise SystemExit(f"generated source hash mismatch: {source_path}")
        source_records_by_module[module].append(
            {
                "source_jar": record["source_jar"],
                "source_class_path": source_class_path,
                "class_sha256": record["class_sha256"],
                "path": str(module_relative),
                "sha256": actual_sha256,
                "source_sha256_before_transform": record["source_sha256_before_transform"],
                "decompiler": record["decompiler"],
                "decompiler_version": record["decompiler_version"],
                "transformations": record["transformations"],
                "compile_status": "pending-module-assembly",
            }
        )

    resource_provenance = json.loads(
        args.resource_provenance.read_text(encoding="utf-8")
    )
    resource_records = []
    resource_destination_root = output_root / "owned-core-web" / "src/main/resources"
    for record in resource_provenance["resources"]:
        relative = PurePosixPath(str(record["path"]))
        if relative.is_absolute() or ".." in relative.parts:
            raise SystemExit(f"unsafe resource path: {relative}")
        source_path = args.resource_root.resolve() / Path(*relative.parts)
        if not source_path.is_file():
            raise SystemExit(f"missing generated resource: {source_path}")
        actual_sha256 = sha256_file(source_path)
        if actual_sha256 != record["sha256"]:
            raise SystemExit(f"resource hash mismatch: {source_path}")
        if relative.as_posix() == "META-INF/MANIFEST.MF":
            continue
        destination = resource_destination_root / Path(*relative.parts)
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source_path, destination)
        resource_records.append(
            {
                "path": str(relative),
                "sha256": actual_sha256,
                "bytes": record["bytes"],
                "kind": record["kind"],
                "final_distribution_allowed": record["final_distribution_allowed"],
                "forbidden_reference": record["forbidden_reference"],
            }
        )
    write_json(
        output_root / "owned-core-web" / "resource-provenance.json",
        {
            "schema": "supplier-reconstituted-resource-provenance-v1",
            "source_jar": resource_provenance["source_jar"],
            "source_jar_sha256": resource_provenance["source_jar_sha256"],
            "excluded_paths": ["META-INF/MANIFEST.MF"],
            "resource_count": len(resource_records),
            "resource_tree_sha256": tree_digest(resource_records),
            "resources": resource_records,
        },
    )

    module_summaries = []
    for module in MODULE_SOURCE_JARS:
        module_root = output_root / module
        source_records = sorted(
            source_records_by_module[module],
            key=lambda item: str(item["path"]),
        )
        source_files = [
            module_root / str(item["path"])
            for item in source_records
        ]
        resource_files = (
            sorted((module_root / "src/main/resources").rglob("*"))
            if (module_root / "src/main/resources").is_dir()
            else []
        )
        resource_files = [path for path in resource_files if path.is_file()]
        source_tree = tree_digest(source_records)
        compile_report = {
            "schema": "supplier-reconstituted-module-compile-v1",
            "module": module,
            "status": "pending",
            "java_version": "8",
            "source_only_framework_classpath": True,
            "supplier_private_jars_on_classpath": False,
        }
        forbidden_report = scan_forbidden(module, source_files, resource_files)
        write_json(module_root / "compile-report.json", compile_report)
        write_json(module_root / "forbidden-content-report.json", forbidden_report)
        write_json(
            module_root / "public-dependency-allowlist.json",
            allowlist,
        )
        write_json(
            module_root / "module-provenance.json",
            {
                "schema": "supplier-reconstituted-module-v1",
                "module": module,
                "status": "assembled-source-only",
                "artifact": {
                    "run_jar_sha256": report["run_jar_sha256"],
                    "source_jars": MODULE_SOURCE_JARS[module],
                },
                "module_reason": MODULE_REASONS[module],
                "dependencies": MODULE_DEPENDENCIES[module],
                "source_file_count": len(source_records),
                "source_tree_sha256": source_tree,
                "source_decompilers": dict(
                    sorted(
                        {
                            name: sum(1 for item in source_records if item["decompiler"] == name)
                            for name in {
                                str(item["decompiler"]) for item in source_records
                            }
                        }.items()
                    )
                ),
                "transformed_source_count": sum(
                    any(
                        str(value).startswith("rule:")
                        for value in item["transformations"]
                    )
                    for item in source_records
                ),
                "transformation_manifest": "docs/reconstitution/decompiler-transforms.json",
                "transformation_manifest_sha256": report[
                    "transformation_manifest_sha256"
                ],
                "public_dependency_allowlist": "public-dependency-allowlist.json",
                "compile_report": "compile-report.json",
                "forbidden_content_report": "forbidden-content-report.json",
                "source_files": source_records,
            },
        )
        (module_root / "pom.xml").write_text(
            render_module_pom(module, public_dependencies),
            encoding="utf-8",
        )
        module_summaries.append(
            {
                "module": module,
                "source_jars": MODULE_SOURCE_JARS[module],
                "source_file_count": len(source_records),
                "resource_file_count": len(resource_files),
                "source_tree_sha256": source_tree,
                "dependencies": MODULE_DEPENDENCIES[module],
                "reason": MODULE_REASONS[module],
                "compile_status": "pending",
                "forbidden_content_status": forbidden_report["status"],
            }
        )

    (output_root / "pom.xml").write_text(
        render_parent_pom(MODULE_SOURCE_JARS),
        encoding="utf-8",
    )
    registry = {
        "schema": "reconstituted-framework-module-registry-v1",
        "generated_at": report["generated_at"],
        "status": "assembled-source-only",
        "canonical": False,
        "strict_evidence": False,
        "api_evidence": False,
        "artifact_sha256": report["run_jar_sha256"],
        "source_closure_mode": report["mode"],
        "source_closure_gate_status": report["gate_status"],
        "source_file_count": sum(item["source_file_count"] for item in module_summaries),
        "resource_file_count": sum(
            item["resource_file_count"] for item in module_summaries
        ),
        "module_count": len(module_summaries),
        "candidate_group_count": 8,
        "merged_candidate_groups": {
            "owned-core-interface": "owned-core-common",
        },
        "public_dependency_count": len(public_dependencies),
        "public_dependency_allowlist": "reconstituted/framework/public-dependency-allowlist.json",
        "transformation_manifest": "docs/reconstitution/decompiler-transforms.json",
        "transformation_manifest_sha256": report[
            "transformation_manifest_sha256"
        ],
        "modules": module_summaries,
        "runtime_model": {
            "application": "one Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "limitations": [
            "Source assembly is not a final distribution gate.",
            "Native/license removal and module compile reports must be completed before runtime packaging.",
            "Public dependency JARs remain external controlled build inputs and are never copied into source control.",
        ],
    }
    write_json(args.registry_output.resolve(), registry)
    print(
        json.dumps(
            {
                "status": "assembled-source-only",
                "module_count": len(module_summaries),
                "source_file_count": registry["source_file_count"],
                "resource_file_count": registry["resource_file_count"],
                "public_dependency_count": len(public_dependencies),
                "output_root": str(output_root),
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
