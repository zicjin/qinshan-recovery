#!/usr/bin/env python3
"""Compile the original project application against source-reconstituted modules."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import zipfile


ROOT = Path(__file__).resolve().parents[1]
MAIN_CLASS = "com.sunwayworld.emc.projectfunction.GikamWpjcApplication"
SUPPLIER_PRIVATE_MARKERS = (
    "gikam-core-",
    "lims2-report-template-",
    "gikam-third-czjky-",
)


def digest(path: Path) -> str:
    value = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            value.update(chunk)
    return value.hexdigest()


def write_json(path: Path, payload: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def source_fqn(path: Path, source_root: Path) -> str | None:
    text = path.read_text(encoding="utf-8", errors="replace")
    package = re.search(r"^\s*package\s+([A-Za-z_$][\w.$]*)\s*;", text, re.MULTILINE)
    type_name = re.search(
        r"\b(?:class|interface|enum|@interface)\s+([A-Za-z_$][\w$]*)",
        text,
    )
    if not type_name:
        return None
    relative = path.relative_to(source_root).with_suffix("")
    if package:
        return f"{package.group(1)}.{type_name.group(1)}"
    return ".".join(relative.parts)


def framework_source_fqns(framework_root: Path) -> set[str]:
    values: set[str] = set()
    for provenance_path in sorted(framework_root.glob("*/module-provenance.json")):
        payload = json.loads(provenance_path.read_text(encoding="utf-8"))
        for record in payload["source_files"]:
            class_path = str(record["source_class_path"])
            values.add(class_path.removesuffix(".class").replace("/", "."))
    return values


def framework_compile_jars(framework_root: Path) -> list[Path]:
    registry_path = ROOT / "docs" / "reconstitution" / "framework-module-registry.json"
    registry = json.loads(registry_path.read_text(encoding="utf-8"))
    if registry.get("status") != "compiled-source-only":
        raise SystemExit("framework module registry is not compiled-source-only")
    jars = []
    for module in registry["modules"]:
        module_report = json.loads(
            (framework_root / module["module"] / "compile-report.json").read_text(
                encoding="utf-8"
            )
        )
        if module_report.get("status") != "pass":
            raise SystemExit(f"framework module did not compile: {module['module']}")
        jar = framework_root / module["output_jar"]
        if not jar.is_file() or digest(jar) != module["output_jar_sha256"]:
            raise SystemExit(f"framework JAR hash mismatch: {jar}")
        jars.append(jar)
    return jars


def copy_resource_tree(source_root: Path, target_root: Path) -> list[dict[str, object]]:
    records: list[dict[str, object]] = []
    for source in sorted(path for path in source_root.rglob("*") if path.is_file()):
        relative = source.relative_to(source_root)
        if relative.as_posix() == "lib/private-sdk.jar":
            continue
        destination = target_root / relative
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, destination)
        records.append(
            {
                "path": relative.as_posix(),
                "sha256": digest(destination),
                "bytes": destination.stat().st_size,
            }
        )
    return records


def copy_java_tree_resources(source_root: Path, target_root: Path) -> list[dict[str, object]]:
    records: list[dict[str, object]] = []
    for source in sorted(path for path in source_root.rglob("*") if path.is_file()):
        if source.suffix == ".java":
            continue
        relative = source.relative_to(source_root)
        destination = target_root / relative
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, destination)
        records.append(
            {
                "path": relative.as_posix(),
                "sha256": digest(destination),
                "bytes": destination.stat().st_size,
            }
        )
    return records


def copy_license_clean_frontend_overlay(
    target_root: Path,
    overlay_root: Path = ROOT / "owned-web" / "src" / "main" / "resources" / "frontend-original",
) -> list[dict[str, object]]:
    records: list[dict[str, object]] = []
    for relative in (
        PurePosixPath("static/gikam/main/js/home.js"),
        PurePosixPath("static/gikam/main/js/workspace.js"),
    ):
        source = overlay_root / relative
        if not source.is_file():
            raise SystemExit(f"missing license-clean frontend overlay: {source}")
        data = source.read_bytes()
        if b"/core/module/sys/licenses" in data or b"/open/security/licenses" in data:
            raise SystemExit(f"frontend overlay retains removed license route: {source}")
        if relative.name == "workspace.js" and b"return v.initEln()" in data:
            raise SystemExit("frontend overlay enables missing ELN preload resource")
        destination = target_root / relative
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_bytes(data)
        try:
            source_path = source.relative_to(ROOT).as_posix()
        except ValueError:
            source_path = source.as_posix()
        records.append(
            {
                "path": relative.as_posix(),
                "sha256": digest(destination),
                "bytes": destination.stat().st_size,
                "source_path": source_path,
                "classification": "license-clean-original-frontend-overlay",
            }
        )
    return records


def build_application_jar(classes: Path, resources: Path, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    entries: dict[str, bytes] = {}
    for source in sorted(classes.rglob("*.class")):
        entries[source.relative_to(classes).as_posix()] = source.read_bytes()
    for source in sorted(path for path in resources.rglob("*") if path.is_file()):
        name = source.relative_to(resources).as_posix()
        if name == "META-INF/MANIFEST.MF":
            continue
        entries[name] = source.read_bytes()
    manifest = (
        "Manifest-Version: 1.0\r\n"
        f"Main-Class: {MAIN_CLASS}\r\n"
        "Created-By: lims-source-reconstitution\r\n\r\n"
    ).encode("utf-8")
    with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        info = zipfile.ZipInfo("META-INF/MANIFEST.MF", date_time=(1980, 1, 1, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        info.external_attr = 0o644 << 16
        archive.writestr(info, manifest)
        for name in sorted(entries):
            info = zipfile.ZipInfo(name, date_time=(1980, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            archive.writestr(info, entries[name])


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project-root", type=Path, default=ROOT / "project")
    parser.add_argument(
        "--framework-root",
        type=Path,
        default=ROOT / "reconstituted" / "framework",
    )
    parser.add_argument(
        "--public-deps",
        type=Path,
        default=ROOT / "build" / "reconstituted-public-deps",
    )
    parser.add_argument(
        "--output-root",
        type=Path,
        default=ROOT / "build" / "source-only-application",
    )
    parser.add_argument("--javac", type=Path, default=ROOT / "build/jdk1.8.0_391/bin/javac")
    args = parser.parse_args(argv)

    project_root = args.project_root.resolve()
    framework_root = args.framework_root.resolve()
    public_deps = args.public_deps.resolve()
    output_root = args.output_root.resolve()
    source_root = project_root / "src/main/java"
    resource_source_root = project_root / "src/main/resources"
    if not args.javac.is_file():
        raise SystemExit(f"missing Java 8 javac: {args.javac}")
    if output_root.exists():
        shutil.rmtree(output_root)
    classes = output_root / "classes"
    resources = output_root / "resources"
    classes.mkdir(parents=True)
    resources.mkdir(parents=True)
    third_party_deps = output_root / "third-party-deps"
    third_party_deps.mkdir(parents=True)

    framework_jars = framework_compile_jars(framework_root)
    public_jars = sorted(public_deps.glob("*.jar"))
    if not public_jars:
        raise SystemExit(f"missing pinned public dependencies: {public_deps}")
    private_sdk_source = resource_source_root / "lib/private-sdk.jar"
    private_sdk = third_party_deps / "qiyuesuo-sdk-vendored.jar"
    if private_sdk_source.is_file():
        shutil.copyfile(private_sdk_source, private_sdk)
    classpath_entries = [*framework_jars, *public_jars]
    if private_sdk.is_file():
        classpath_entries.append(private_sdk)
    classpath = ":".join(str(path) for path in classpath_entries)

    framework_fqns = framework_source_fqns(framework_root)
    project_sources = sorted(source_root.rglob("*.java"))
    compile_sources: list[Path] = []
    provided_by_framework: list[dict[str, str]] = []
    for source in project_sources:
        fqn = source_fqn(source, source_root)
        if fqn and fqn in framework_fqns:
            provided_by_framework.append(
                {"fqn": fqn, "project_source": source.relative_to(project_root).as_posix()}
            )
        else:
            compile_sources.append(source)

    source_list = output_root / "sources.txt"
    source_list.write_text(
        "\n".join(str(path) for path in compile_sources) + "\n",
        encoding="utf-8",
    )
    command = [
        str(args.javac),
        "-source",
        "8",
        "-target",
        "8",
        "-encoding",
        "UTF-8",
        "-Xmaxerrs",
        "10000",
        "-classpath",
        classpath,
        "-d",
        str(classes),
        f"@{source_list}",
    ]
    result = subprocess.run(
        command,
        cwd=ROOT,
        text=True,
        capture_output=True,
        check=False,
    )
    compile_log = result.stdout + result.stderr
    (output_root / "javac.log").write_text(compile_log, encoding="utf-8")
    if result.returncode:
        write_json(
            output_root / "compile-report.json",
            {
                "schema": "source-only-project-application-v1",
                "status": "failed",
                "exit_code": result.returncode,
                "canonical": False,
                "strict_evidence": False,
                "api_evidence": False,
            },
        )
        print(compile_log[-10000:])
        return result.returncode

    resource_records = copy_resource_tree(resource_source_root, resources)
    resource_records.extend(copy_license_clean_frontend_overlay(resources))
    java_tree_resources = copy_java_tree_resources(source_root, resources)
    resource_records.extend(java_tree_resources)
    app_jar = output_root / "lims-owned-application.jar"
    build_application_jar(classes, resources, app_jar)
    third_party_provenance = (
        [
            {
                "filename": private_sdk.name,
                "artifact": private_sdk.relative_to(ROOT).as_posix(),
                "artifact_sha256": digest(private_sdk),
                "source_path": private_sdk_source.relative_to(ROOT).as_posix(),
                "source_sha256": digest(private_sdk_source),
                "classification": "third-party business SDK",
                "reason": (
                    "Project FileSealUtils imports net.qiyuesuo; retained as a "
                    "renamed vendored business dependency, not Sunway runtime."
                ),
            }
        ]
        if private_sdk.is_file()
        else []
    )
    write_json(
        output_root / "third-party-dependencies.json",
        {
            "schema": "source-only-third-party-dependencies-v1",
            "entries": third_party_provenance,
        },
    )

    classpath_details = []
    for path in classpath_entries:
        classpath_details.append(
            {
                "path": path.relative_to(ROOT).as_posix() if path.is_relative_to(ROOT) else str(path),
                "sha256": digest(path),
                "kind": (
                    "reconstituted-framework"
                    if path in framework_jars
                    else "third-party-sdk"
                    if path == private_sdk
                    else "public-dependency"
                ),
            }
        )
    private_classpath = [
        item
        for item in classpath_details
        if item["kind"] != "third-party-sdk"
        and any(marker in PurePosixPath(item["path"]).name for marker in SUPPLIER_PRIVATE_MARKERS)
    ]
    report = {
        "schema": "source-only-project-application-v1",
        "status": "compiled-source-only",
        "canonical": False,
        "strict_evidence": False,
        "api_evidence": False,
        "java_version": "8",
        "main_class": MAIN_CLASS,
        "project_source_file_count": len(project_sources),
        "compile_target_source_file_count": len(compile_sources),
        "provided_by_reconstituted_modules": provided_by_framework,
        "classpath": classpath_details,
        "supplier_private_jars_on_classpath": bool(private_classpath),
        "framework_module_jar_count": len(framework_jars),
        "public_dependency_count": len(public_jars),
        "third_party_sdk_count": int(private_sdk.is_file()),
        "third_party_sdk_provenance": third_party_provenance,
        "compile_exit_code": result.returncode,
        "compile_error_count": compile_log.count("error:"),
        "java_source_resource_count": len(java_tree_resources),
        "resource_record_count": len(resource_records),
        "resource_tree_sha256": hashlib.sha256(
            json.dumps(resource_records, sort_keys=True, separators=(",", ":")).encode()
        ).hexdigest(),
        "application_jar": app_jar.relative_to(ROOT).as_posix(),
        "application_jar_sha256": digest(app_jar),
        "application_jar_bytes": app_jar.stat().st_size,
        "runtime_model": {
            "application": "one Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
    }
    write_json(output_root / "compile-report.json", report)
    print(json.dumps(report, ensure_ascii=False, indent=2, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
