#!/usr/bin/env python3
"""Inventory supplier artifacts and build a source-only Java compile closure.

The supplier run JAR is an external, read-only input. This tool hashes and
classifies its members, indexes nested Sunway framework JARs, computes a
class-reference closure for explicit project imports/seeds, decompiles the
selected classes, and attempts a Java 8 source-only compile.

No supplier or private `gikam-core-*` JAR is placed on the javac classpath.
"""
from __future__ import annotations

import argparse
from collections import Counter, defaultdict
from contextlib import nullcontext
from dataclasses import dataclass
from datetime import datetime, timezone
import hashlib
import io
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import struct
import subprocess
import sys
import tempfile
import zipfile
from typing import Iterable, Mapping, Sequence


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_RUN_JAR = ROOT / ".external-materials" / "gikam-third-czjky-14.1.5-run.jar"
DEFAULT_CFR = ROOT / "tools" / "cfr-0.152.jar"
DEFAULT_JDK = ROOT / "build" / "jdk1.8.0_391"
DEFAULT_TRANSFORM_MANIFEST = ROOT / "docs" / "reconstitution" / "decompiler-transforms.json"
PILOT_PRIVATE_JARS = (
    "gikam-core-base-interface-14.1.5.jar",
    "gikam-core-common-14.1.5.jar",
    "gikam-core-base-service-14.1.5.jar",
)
CORE_WEB_JAR = "gikam-core-web-14.1.5.jar"
PRIVATE_FRAMEWORK_RE = re.compile(r"^gikam-core-.*\.jar$")
FORBIDDEN_NAMES_RE = re.compile(
    r"(?:^|/)(?:sunway-app\.so|license\.lic|vendor\.lic|[^/]+\.so)$",
    re.IGNORECASE,
)
JAVA_IMPORT_RE = re.compile(
    r"^\s*import\s+(?:static\s+)?(com\.sunwayworld\.[A-Za-z0-9_.$]+)\s*;",
    re.MULTILINE,
)
JAVAC_ERROR_PATH_RE = re.compile(r"^(.+?\.java):\d+:\s+error:\s*(.+)$")
MISSING_PACKAGE_RE = re.compile(
    r"package\s+(com\.sunwayworld\.[A-Za-z0-9_.]+)\s+does not exist"
)
MISSING_IMPORT_RE = re.compile(
    r"^\s*import\s+(com\.sunwayworld\.[A-Za-z0-9_.$]+)\s*;",
    re.MULTILINE,
)
DESCRIPTOR_CLASS_RE = re.compile(rb"L(com/sunwayworld/[A-Za-z0-9_$/]+);")


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def source_tree_digest(records: Iterable[Mapping[str, object]]) -> str:
    digest = hashlib.sha256()
    for record in sorted(
        records,
        key=lambda item: (
            str(item["generated_source_path"]),
            str(item["source_class_path"]),
        ),
    ):
        source_sha256 = record.get("source_sha256")
        if not isinstance(source_sha256, str):
            continue
        digest.update(str(record["generated_source_path"]).encode("utf-8"))
        digest.update(b"\0")
        digest.update(bytes.fromhex(source_sha256))
    return digest.hexdigest()


def write_json(path: Path, payload: Mapping[str, object]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def display_path(path: Path) -> str:
    resolved = path.resolve()
    return str(resolved.relative_to(ROOT)) if resolved.is_relative_to(ROOT) else str(resolved)


def java_package(name: str) -> str | None:
    if "." not in name:
        return None
    return name.rsplit(".", 1)[0]


def class_name_from_entry(entry: str) -> str | None:
    if not entry.endswith(".class") or entry.endswith("module-info.class"):
        return None
    return entry[:-6].replace("/", ".")


def is_forbidden_resource(name: str) -> bool:
    return bool(FORBIDDEN_NAMES_RE.search(name))


def maven_coordinates(archive: zipfile.ZipFile) -> list[str]:
    coordinates: set[str] = set()
    for name in archive.namelist():
        if not name.endswith("/pom.properties"):
            continue
        try:
            lines = archive.read(name).decode("utf-8", "replace").splitlines()
        except KeyError:
            continue
        values: dict[str, str] = {}
        for line in lines:
            if "=" in line and not line.lstrip().startswith("#"):
                key, value = line.split("=", 1)
                values[key.strip()] = value.strip()
        group = values.get("groupId")
        artifact = values.get("artifactId")
        version = values.get("version")
        if group and artifact and version:
            coordinates.add(f"{group}:{artifact}:{version}")
    return sorted(coordinates)


def read_class_references(data: bytes) -> set[str]:
    """Return class names from the constant-pool Class and descriptor entries."""
    if len(data) < 10 or data[:4] != b"\xca\xfe\xba\xbe":
        return set()
    constant_pool_count = struct.unpack_from(">H", data, 8)[0]
    offset = 10
    index = 1
    utf8: dict[int, str] = {}
    class_indices: list[int] = []
    while index < constant_pool_count:
        tag = data[offset]
        offset += 1
        if tag == 1:
            length = struct.unpack_from(">H", data, offset)[0]
            offset += 2
            utf8[index] = data[offset:offset + length].decode("utf-8", "replace")
            offset += length
        elif tag in (3, 4):
            offset += 4
        elif tag in (5, 6):
            offset += 8
            index += 1
        elif tag in (7, 8, 16, 19, 20):
            value = struct.unpack_from(">H", data, offset)[0]
            offset += 2
            if tag == 7:
                class_indices.append(value)
        elif tag in (9, 10, 11, 12, 17, 18):
            offset += 4
        elif tag == 15:
            offset += 3
        else:
            raise ValueError(f"unsupported class constant-pool tag {tag}")
        index += 1

    references = {
        utf8[value].replace("/", ".")
        for value in class_indices
        if value in utf8 and utf8[value].replace("/", ".").startswith("com.sunwayworld.")
    }
    for value in utf8.values():
        references.update(
            match.group(1).decode("utf-8", "replace").replace("/", ".")
            for match in DESCRIPTOR_CLASS_RE.finditer(value.encode("utf-8", "replace"))
        )
    return references


@dataclass(frozen=True)
class NestedJar:
    outer_member: str
    filename: str
    sha256: str
    size: int
    private_framework: bool
    coordinates: tuple[str, ...]


@dataclass(frozen=True)
class ClassEntry:
    fqn: str
    outer_member: str
    entry_name: str
    sha256: str
    size: int


def classify_resource(name: str) -> str:
    lowered = name.lower()
    if lowered.startswith("templates/"):
        return "template"
    if "mapper" in lowered and lowered.endswith(".xml"):
        return "mapper"
    if lowered.startswith("static/"):
        return "static"
    if lowered.endswith((".html", ".js", ".css", ".map", ".woff", ".woff2", ".ttf")):
        return "frontend_resource"
    if lowered.endswith((".xml", ".yml", ".yaml", ".properties", ".json")):
        return "config_resource"
    if is_forbidden_resource(name):
        return "forbidden_native_or_license"
    return "resource"


def source_recovery_required(member: str, entry: str) -> bool:
    return (
        entry.endswith(".class")
        and entry.replace("/", ".").startswith("com.sunwayworld.")
    )


def distribution_allowed(member: str, entry: str) -> bool:
    if is_forbidden_resource(entry):
        return False
    if entry.endswith(".class"):
        return False
    filename = PurePosixPath(member).name
    if PRIVATE_FRAMEWORK_RE.match(filename):
        return False
    return True


class RunJar:
    def __init__(self, path: Path):
        self.path = path.resolve()
        if not self.path.is_file():
            raise FileNotFoundError(self.path)
        self._archive = zipfile.ZipFile(self.path)
        self.outer_infos = self._archive.infolist()
        self.nested_jar_info: dict[str, NestedJar] = {}
        self._class_index: dict[str, list[ClassEntry]] = defaultdict(list)
        self._private_archives: dict[str, zipfile.ZipFile] = {}
        self._index_nested_jars()

    def close(self) -> None:
        for archive in self._private_archives.values():
            archive.close()
        self._archive.close()

    def __enter__(self) -> "RunJar":
        return self

    def __exit__(self, *_args: object) -> None:
        self.close()

    def _index_nested_jars(self) -> None:
        for info in self.outer_infos:
            if not (info.filename.startswith("BOOT-INF/lib/") and info.filename.endswith(".jar")):
                continue
            data = self._archive.read(info.filename)
            filename = PurePosixPath(info.filename).name
            archive = zipfile.ZipFile(io.BytesIO(data))
            coordinates = tuple(maven_coordinates(archive))
            has_sunway_classes = any(
                not member.is_dir()
                and member.filename.endswith(".class")
                and member.filename.startswith("com/sunwayworld/")
                for member in archive.infolist()
            )
            private = bool(PRIVATE_FRAMEWORK_RE.match(filename)) or has_sunway_classes
            if private:
                self._private_archives[filename] = archive
                for member in archive.infolist():
                    if member.is_dir() or not member.filename.endswith(".class"):
                        continue
                    fqn = class_name_from_entry(member.filename)
                    if not fqn or not fqn.startswith("com.sunwayworld."):
                        continue
                    content = archive.read(member.filename)
                    self._class_index[fqn].append(
                        ClassEntry(
                            fqn=fqn,
                            outer_member=info.filename,
                            entry_name=member.filename,
                            sha256=sha256_bytes(content),
                            size=len(content),
                        )
                    )
            else:
                archive.close()
            self.nested_jar_info[info.filename] = NestedJar(
                outer_member=info.filename,
                filename=filename,
                sha256=sha256_bytes(data),
                size=len(data),
                private_framework=private,
                coordinates=coordinates,
            )

    def manifest(self, project_source: Path) -> dict[str, object]:
        members: list[dict[str, object]] = []
        kind_counts: Counter[str] = Counter()
        for info in self.outer_infos:
            if info.is_dir():
                continue
            data = self._archive.read(info.filename)
            filename = PurePosixPath(info.filename).name
            if info.filename.startswith("BOOT-INF/lib/") and info.filename.endswith(".jar"):
                nested = self.nested_jar_info[info.filename]
                kind = "sunway_framework" if nested.private_framework else "public_dependency"
                coordinates = list(nested.coordinates)
            elif info.filename.startswith("BOOT-INF/classes/") and info.filename.endswith(".class"):
                kind = "application_class"
                coordinates = []
            elif info.filename.startswith("BOOT-INF/classes/"):
                kind = classify_resource(info.filename[len("BOOT-INF/classes/"):])
                coordinates = []
            elif info.filename.startswith("org/springframework/boot/loader/"):
                kind = "spring_boot_loader"
                coordinates = []
            else:
                kind = classify_resource(info.filename)
                coordinates = []
            kind_counts[kind] += 1
            members.append(
                {
                    "artifact_sha256": None,
                    "member_path": info.filename,
                    "member_sha256": sha256_bytes(data),
                    "size": len(data),
                    "kind": kind,
                    "java_package": None,
                    "maven_coordinates_or_unknown": coordinates or ["unknown"],
                    "source_recovery_required": False,
                    "final_distribution_allowed": (
                        not self.nested_jar_info[info.filename].private_framework
                        if info.filename in self.nested_jar_info
                        else distribution_allowed(info.filename, info.filename)
                    ),
                    "nested_jar": filename if info.filename.endswith(".jar") else None,
                }
            )

            if info.filename in self.nested_jar_info and self.nested_jar_info[info.filename].private_framework:
                archive = self._private_archives[filename]
                for nested_info in archive.infolist():
                    if nested_info.is_dir():
                        continue
                    nested_data = archive.read(nested_info.filename)
                    fqn = class_name_from_entry(nested_info.filename)
                    nested_kind = (
                        "sunway_framework_class"
                        if nested_info.filename.endswith(".class")
                        else classify_resource(nested_info.filename)
                    )
                    kind_counts[nested_kind] += 1
                    members.append(
                        {
                            "artifact_sha256": nested.sha256,
                            "member_path": f"{info.filename}!/{nested_info.filename}",
                            "member_sha256": sha256_bytes(nested_data),
                            "size": len(nested_data),
                            "kind": nested_kind,
                            "java_package": java_package(fqn) if fqn else None,
                            "maven_coordinates_or_unknown": coordinates or ["unknown"],
                            "source_recovery_required": source_recovery_required(
                                info.filename, nested_info.filename
                            ),
                            "final_distribution_allowed": False,
                            "nested_jar": filename,
                        }
                    )

        top_application_classes: list[dict[str, object]] = []
        for info in self.outer_infos:
            entry = info.filename
            if not (entry.startswith("BOOT-INF/classes/") and entry.endswith(".class")):
                continue
            relative = entry[len("BOOT-INF/classes/"):]
            if "$" in PurePosixPath(relative).name:
                continue
            source_relative = PurePosixPath(relative).with_suffix(".java")
            source_path = project_source / Path(*source_relative.parts)
            top_application_classes.append(
                {
                    "class_path": entry,
                    "source_path": display_path(source_path),
                    "source_present": source_path.is_file(),
                }
            )

        private_entries = [
            entry
            for _, entries in self._class_index.items()
            for entry in entries
            if self.nested_jar_info[entry.outer_member].private_framework
        ]
        artifact_sha = sha256_file(self.path)
        return {
            "schema": "supplier-artifact-manifest-v1",
            "generated_at": utc_now(),
            "artifact_path": display_path(self.path),
            "artifact_sha256": artifact_sha,
            "artifact_bytes": self.path.stat().st_size,
            "outer_member_count": sum(1 for info in self.outer_infos if not info.is_dir()),
            "manifest_member_count": len(members),
            "nested_jar_count": len(self.nested_jar_info),
            "private_framework_jar_count": sum(
                1 for item in self.nested_jar_info.values() if item.private_framework
            ),
            "public_dependency_jar_count": sum(
                1 for item in self.nested_jar_info.values() if not item.private_framework
            ),
            "application_class_count": sum(
                1 for info in self.outer_infos
                if info.filename.startswith("BOOT-INF/classes/") and info.filename.endswith(".class")
            ),
            "application_top_level_class_count": len(top_application_classes),
            "application_top_level_source_present": sum(
                1 for item in top_application_classes if item["source_present"]
            ),
            "application_top_level_source_missing": sum(
                1 for item in top_application_classes if not item["source_present"]
            ),
            "private_framework_class_entry_count": len(private_entries),
            "private_framework_distinct_class_count": len(
                {entry.fqn for entry in private_entries}
            ),
            "private_framework_duplicate_class_count": sum(
                1 for entries in self._class_index.values() if len(entries) > 1
            ),
            "kind_counts": dict(sorted(kind_counts.items())),
            "application_top_level_classes": top_application_classes,
            "nested_jars": [
                {
                    "outer_member": item.outer_member,
                    "filename": item.filename,
                    "sha256": item.sha256,
                    "size": item.size,
                    "private_framework": item.private_framework,
                    "maven_coordinates_or_unknown": list(item.coordinates) or ["unknown"],
                    "final_distribution_allowed": not item.private_framework,
                }
                for item in self.nested_jar_info.values()
            ],
            "members": members,
        }

    def private_class_entries(self, jar_names: Iterable[str]) -> dict[str, list[ClassEntry]]:
        selected = {f"BOOT-INF/lib/{name}" for name in jar_names}
        return {
            fqn: entries
            for fqn, entries in self._class_index.items()
            if any(entry.outer_member in selected for entry in entries)
        }

    def classes_in_jar(self, jar_name: str) -> list[ClassEntry]:
        member = f"BOOT-INF/lib/{jar_name}"
        return sorted(
            (
                entry
                for entries in self._class_index.values()
                for entry in entries
                if entry.outer_member == member
            ),
            key=lambda item: item.fqn,
        )

    def class_bytes(self, entry: ClassEntry) -> bytes:
        filename = PurePosixPath(entry.outer_member).name
        return self._private_archives[filename].read(entry.entry_name)

    def write_nested_jars(self, output: Path, private_jars: Iterable[str]) -> None:
        output.mkdir(parents=True, exist_ok=True)
        for jar_name in private_jars:
            member = f"BOOT-INF/lib/{jar_name}"
            (output / jar_name).write_bytes(self._archive.read(member))

    def extract_public_dependencies(self, output: Path) -> list[dict[str, object]]:
        output.mkdir(parents=True, exist_ok=True)
        extracted: list[dict[str, object]] = []
        for member, item in self.nested_jar_info.items():
            if item.private_framework:
                continue
            data = self._archive.read(member)
            destination = output / item.filename
            destination.write_bytes(data)
            extracted.append(
                {
                    "filename": item.filename,
                    "sha256": item.sha256,
                    "size": item.size,
                    "maven_coordinates_or_unknown": list(item.coordinates) or ["unknown"],
                }
            )
        return extracted

    def extract_web_resources(self, output: Path) -> list[dict[str, object]]:
        filename = CORE_WEB_JAR
        if f"BOOT-INF/lib/{filename}" not in self.nested_jar_info:
            return []
        archive = zipfile.ZipFile(
            io.BytesIO(self._archive.read(f"BOOT-INF/lib/{filename}"))
        )
        resources: list[dict[str, object]] = []
        for info in archive.infolist():
            if info.is_dir() or info.filename.endswith(".class"):
                continue
            destination = output / info.filename
            destination.parent.mkdir(parents=True, exist_ok=True)
            data = archive.read(info.filename)
            destination.write_bytes(data)
            resources.append(
                {
                    "path": info.filename,
                    "sha256": sha256_bytes(data),
                    "bytes": len(data),
                    "kind": classify_resource(info.filename),
                    "forbidden_reference": is_forbidden_resource(info.filename),
                    "final_distribution_allowed": not is_forbidden_resource(
                        info.filename
                    ),
                }
            )
        archive.close()
        return sorted(resources, key=lambda item: str(item["path"]))

    def count_web_resources(self) -> int:
        member = f"BOOT-INF/lib/{CORE_WEB_JAR}"
        if member not in self.nested_jar_info:
            return 0
        archive = zipfile.ZipFile(io.BytesIO(self._archive.read(member)))
        try:
            return sum(
                1
                for info in archive.infolist()
                if not info.is_dir() and not info.filename.endswith(".class")
            )
        finally:
            archive.close()


def project_imports(paths: Iterable[Path]) -> dict[str, list[str]]:
    imports: dict[str, list[str]] = defaultdict(list)
    for path in paths:
        text = path.read_text(encoding="utf-8", errors="replace")
        for match in JAVA_IMPORT_RE.finditer(text):
            symbol = match.group(1)
            if symbol.startswith("com.sunwayworld.emc."):
                continue
            imports[symbol].append(str(path))
    return dict(sorted(imports.items()))


def project_class_names(source_root: Path) -> set[str]:
    return {
        ".".join(path.relative_to(source_root).with_suffix("").parts)
        for path in source_root.rglob("*.java")
    }


def resolve_closure(
    run_jar: RunJar,
    entries: Mapping[str, list[ClassEntry]],
    seeds: Iterable[str],
    provided_classes: Iterable[str] = (),
) -> tuple[set[str], dict[str, list[str]], list[str]]:
    by_fqn = {fqn.replace("$", "."): fqn for fqn in entries}
    provided = set(provided_classes)
    queue = list(seeds)
    closure: set[str] = set()
    edges: dict[str, list[str]] = {}
    missing_external: list[str] = []
    while queue:
        requested = queue.pop()
        if requested in provided:
            continue
        actual = requested if requested in entries else by_fqn.get(requested)
        if actual is None:
            if requested.startswith("com.sunwayworld."):
                missing_external.append(requested)
            continue
        if actual in closure:
            continue
        closure.add(actual)
        dependencies: list[str] = []
        for candidate_entry in entries[actual]:
            # The closure records references, not duplicate jar selection.
            refs = read_class_references(run_jar.class_bytes(candidate_entry))
            for reference in sorted(refs):
                target = reference if reference in entries else by_fqn.get(reference)
                if target:
                    if target == actual:
                        continue
                    dependencies.append(target)
                elif reference in provided:
                    continue
                elif reference.startswith("com.sunwayworld."):
                    missing_external.append(reference)
        edges[actual] = sorted(set(dependencies))
        queue.extend(edges[actual])
    return closure, edges, sorted(set(missing_external))


def run_process(
    args: Sequence[str],
    *,
    cwd: Path,
    stdout_path: Path,
    stderr_path: Path,
) -> int:
    with stdout_path.open("wb") as stdout, stderr_path.open("wb") as stderr:
        result = subprocess.run(args, cwd=cwd, stdout=stdout, stderr=stderr, check=False)
    return result.returncode


def decompile_with_cfr(
    java: Path,
    cfr: Path,
    jar: Path,
    output: Path,
    extra_classpath: Sequence[Path],
    stdout: Path,
    stderr: Path,
) -> int:
    output.mkdir(parents=True, exist_ok=True)
    return run_process(
        [
            str(java),
            "-jar",
            str(cfr),
            str(jar),
            "--outputdir",
            str(output),
            "--silent",
            "true",
            "--comments",
            "false",
            "--decodelambdas",
            "true",
            "--hideutf",
            "true",
            "--extraclasspath",
            os.pathsep.join(str(path) for path in extra_classpath),
        ],
        cwd=ROOT,
        stdout_path=stdout,
        stderr_path=stderr,
    )


def decompile_with_vineflower(
    java: Path,
    vineflower: Path,
    jar: Path,
    output: Path,
    external_jars: Sequence[Path],
    stdout: Path,
    stderr: Path,
) -> int:
    output.mkdir(parents=True, exist_ok=True)
    command = [
        str(java),
        "-jar",
        str(vineflower),
        "--silent",
        "--no-decompiler-comments",
        "--folder",
        "--thread-count=1",
    ]
    command.extend(f"-e={path}" for path in external_jars)
    command.extend([str(jar), str(output)])
    return run_process(
        command,
        cwd=ROOT,
        stdout_path=stdout,
        stderr_path=stderr,
    )


def decompile_one_with_vineflower(
    java: Path,
    vineflower: Path,
    jar: Path,
    fqn: str,
    output_file: Path,
    external_jars: Sequence[Path],
) -> int:
    output_file.parent.mkdir(parents=True, exist_ok=True)
    top_level = top_level_fqn(fqn)
    class_path = top_level.replace(".", "/") + ".class"
    with tempfile.TemporaryDirectory(prefix="vineflower-class-") as temp_name:
        temp = Path(temp_name)
        single_jar = temp / "class-input.jar"
        with zipfile.ZipFile(jar) as source_archive, zipfile.ZipFile(
            single_jar, "w", compression=zipfile.ZIP_DEFLATED
        ) as target_archive:
            prefix = class_path[:-6]
            for info in source_archive.infolist():
                if info.filename == class_path or info.filename.startswith(prefix + "$"):
                    target_archive.writestr(info.filename, source_archive.read(info.filename))
        decompiled = temp / "decompiled"
        decompiled.mkdir()
        command = [
            str(java),
            "-jar",
            str(vineflower),
            "--silent",
            "--no-decompiler-comments",
            "--folder",
            "--thread-count=1",
        ]
        command.extend(f"-e={path}" for path in external_jars)
        command.extend([str(single_jar), str(decompiled)])
        result = subprocess.run(
            command,
            cwd=ROOT,
            text=True,
            capture_output=True,
            check=False,
        )
        generated = decompiled / source_path_for_class(top_level)
        if result.returncode == 0 and generated.is_file():
            shutil.copyfile(generated, output_file)
            return 0
        return result.returncode or 1


def source_path_for_class(fqn: str) -> Path:
    return Path(*fqn.split(".")).with_suffix(".java")


def top_level_fqn(fqn: str) -> str:
    return fqn.split("$", 1)[0]


def load_transform_manifest(path: Path) -> dict[str, object]:
    payload = json.loads(path.read_text(encoding="utf-8"))
    if payload.get("schema") != "supplier-decompiler-transformations-v1":
        raise ValueError(f"unsupported transformation manifest schema: {path}")
    rules = payload.get("rules")
    if not isinstance(rules, list):
        raise ValueError(f"transformation manifest rules must be a list: {path}")
    seen: set[str] = set()
    for rule in rules:
        if not isinstance(rule, dict):
            raise ValueError(f"transformation rule must be an object: {path}")
        rule_id = rule.get("id")
        relative_file = rule.get("file")
        operations = rule.get("operations")
        if not isinstance(rule_id, str) or not rule_id:
            raise ValueError(f"transformation rule without id: {path}")
        if rule_id in seen:
            raise ValueError(f"duplicate transformation rule id {rule_id!r}: {path}")
        seen.add(rule_id)
        if not isinstance(relative_file, str) or PurePosixPath(relative_file).is_absolute():
            raise ValueError(f"invalid transformation file for {rule_id}: {path}")
        if not isinstance(operations, list) or not operations:
            raise ValueError(f"transformation rule {rule_id} has no operations: {path}")
        for operation in operations:
            if not isinstance(operation, dict):
                raise ValueError(f"invalid operation in {rule_id}: {path}")
            if operation.get("op") not in {"replace", "regex_replace"}:
                raise ValueError(f"unsupported operation in {rule_id}: {path}")
            if not isinstance(operation.get("expected_count"), int):
                raise ValueError(f"operation without expected_count in {rule_id}: {path}")
    return payload


def apply_transform_rules(
    source: Path,
    source_root: Path,
    transform_manifest: Mapping[str, object],
    decompiler_name: str,
) -> tuple[str, list[str]]:
    relative_file = source.relative_to(source_root).as_posix()
    rules = (
        [
            rule
            for rule in transform_manifest["rules"]
            if rule["file"] == relative_file
        ]
        if transform_manifest.get("decompiler") == decompiler_name
        else []
    )
    text = source.read_text(encoding="utf-8")
    applied: list[str] = []
    for rule in rules:
        for operation in rule["operations"]:
            expected = operation["expected_count"]
            if operation["op"] == "replace":
                find = operation["find"]
                replacement = operation["replace"]
                actual = text.count(find)
                if actual != expected:
                    raise ValueError(
                        f"{rule['id']}: expected {expected} occurrences in "
                        f"{relative_file}, found {actual}"
                    )
                text = text.replace(find, replacement)
            else:
                pattern = re.compile(operation["pattern"], re.MULTILINE)
                text, actual = pattern.subn(operation["replace"], text)
                if actual != expected:
                    raise ValueError(
                        f"{rule['id']}: expected {expected} regex matches in "
                        f"{relative_file}, found {actual}"
                    )
        applied.append(rule["id"])
    normalized = "\n".join(line.rstrip() for line in text.splitlines()).rstrip() + "\n"
    if normalized != text:
        applied.append("normalization:generated-source")
        text = normalized
    if applied:
        source.write_text(text, encoding="utf-8")
    return text, applied


def decompile_pilot_sources(
    run_jar: RunJar,
    output_sources: Path,
    work: Path,
    *,
    cfr_java: Path,
    cfr_jar: Path,
    vineflower_java: Path | None,
    vineflower_jar: Path | None,
    analysis_classpath: Sequence[Path],
    primary_decompiler: str,
    closure: set[str],
    transform_manifest: Mapping[str, object],
    private_jar_names: Sequence[str],
) -> tuple[list[dict[str, object]], dict[str, str]]:
    selected_top_levels = {top_level_fqn(item) for item in closure}
    provenance: list[dict[str, object]] = []
    source_to_fqn: dict[str, str] = {}
    for jar_name in private_jar_names:
        jar_path = work / "private-jars" / jar_name
        extracted = work / "decompiled" / jar_name
        cfr_fallback_source: Path | None = None
        extracted.mkdir(parents=True, exist_ok=True)
        if primary_decompiler == "vineflower":
            if not vineflower_java or not vineflower_jar:
                raise ValueError("Vineflower primary mode requires --vineflower-jar")
            rc = decompile_with_vineflower(
                vineflower_java,
                vineflower_jar,
                jar_path,
                extracted,
                analysis_classpath,
                work / f"{jar_name}.vineflower.stdout",
                work / f"{jar_name}.vineflower.stderr",
            )
            decompiler_name = "Vineflower"
            decompiler_version = "1.11.1"
            cfr_fallback_source = work / "cfr-fallback" / jar_name
            decompile_with_cfr(
                cfr_java,
                cfr_jar,
                jar_path,
                cfr_fallback_source,
                analysis_classpath,
                work / f"{jar_name}.cfr-fallback.stdout",
                work / f"{jar_name}.cfr-fallback.stderr",
            )
        else:
            rc = decompile_with_cfr(
                cfr_java,
                cfr_jar,
                jar_path,
                extracted,
                analysis_classpath,
                work / f"{jar_name}.cfr.stdout",
                work / f"{jar_name}.cfr.stderr",
            )
            decompiler_name = "CFR"
            decompiler_version = "0.152"
        for entry in run_jar.classes_in_jar(jar_name):
            if entry.fqn not in selected_top_levels:
                continue
            source = extracted / source_path_for_class(entry.fqn)
            destination = output_sources / source_path_for_class(entry.fqn)
            record: dict[str, object] = {
                "source_jar": entry.outer_member,
                "source_class_path": entry.entry_name,
                "class_sha256": entry.sha256,
                "generated_source_path": str(destination.relative_to(output_sources.parent)),
                "source_sha256_before_transform": None,
                "source_sha256": None,
                "decompiler": decompiler_name,
                "decompiler_version": decompiler_version,
                "decompiler_exit_code": rc,
                "transformations": (
                    ["primary-vineflower-with-analysis-classpath"]
                    if primary_decompiler == "vineflower"
                    else []
                ),
                "compile_status": "pending",
                "first_compile_error": None,
            }
            if (
                not source.is_file()
                and primary_decompiler == "cfr"
                and vineflower_java
                and vineflower_jar
            ):
                destination.parent.mkdir(parents=True, exist_ok=True)
                vf_rc = decompile_one_with_vineflower(
                    vineflower_java,
                    vineflower_jar,
                    jar_path,
                    entry.fqn,
                    destination,
                    analysis_classpath,
                )
                record["decompiler"] = "Vineflower"
                record["decompiler_version"] = "1.11.1"
                record["decompiler_exit_code"] = vf_rc
            elif source.is_file():
                destination.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(source, destination)
            elif primary_decompiler == "vineflower" and cfr_fallback_source:
                cfr_source = cfr_fallback_source / source_path_for_class(entry.fqn)
                if cfr_source.is_file():
                    destination.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copyfile(cfr_source, destination)
                    record["decompiler"] = "CFR"
                    record["decompiler_version"] = "0.152"
                    record["transformations"] = [
                        "primary-vineflower-with-analysis-classpath",
                        "vineflower-to-cfr-fallback",
                    ]
            else:
                record["compile_status"] = "decompile-failed"
                provenance.append(record)
                continue
            if destination.is_file():
                source_sha256_before_transform = sha256_file(destination)
                _transformed, applied_transformations = apply_transform_rules(
                    destination,
                    output_sources,
                    transform_manifest,
                    str(record["decompiler"]),
                )
                record["source_sha256_before_transform"] = (
                    source_sha256_before_transform
                )
                if applied_transformations:
                    record["transformations"].extend(
                        (
                            item
                            if str(item).startswith("normalization:")
                            else f"rule:{item}"
                        )
                        for item in applied_transformations
                    )
                record["source_sha256"] = sha256_file(destination)
                source_to_fqn[str(destination.resolve())] = entry.fqn
            provenance.append(record)
    return provenance, source_to_fqn


def compile_java8(
    javac: Path,
    sources: Sequence[Path],
    target_sources: Sequence[Path],
    classpath_dir: Path,
    output_classes: Path,
    log_path: Path,
) -> tuple[int, str]:
    output_classes.mkdir(parents=True, exist_ok=True)
    classpath = os.pathsep.join(
        str(path) for path in sorted(classpath_dir.glob("*.jar"))
    )
    command = [
        str(javac),
        "-source",
        "8",
        "-target",
        "8",
        "-Xmaxerrs",
        "10000",
        "-encoding",
        "UTF-8",
        "-classpath",
        classpath,
        "-d",
        str(output_classes),
        *[str(path) for path in sources],
        *[str(path) for path in target_sources],
    ]
    result = subprocess.run(command, cwd=ROOT, text=True, capture_output=True, check=False)
    log_path.write_text(result.stdout + result.stderr, encoding="utf-8")
    return result.returncode, result.stdout + result.stderr


def compile_summary(log: str, source_to_fqn: Mapping[str, str]) -> dict[str, object]:
    failures: list[dict[str, str]] = []
    missing_queue: set[str] = set()
    categories: Counter[str] = Counter()
    for line in log.splitlines():
        match = JAVAC_ERROR_PATH_RE.match(line)
        if not match:
            continue
        path, message = match.groups()
        failures.append({"path": path, "message": message})
        categories[message.split(":", 1)[0]] += 1
        package_match = MISSING_PACKAGE_RE.search(message)
        if package_match:
            missing_queue.add(package_match.group(1))
    for match in MISSING_IMPORT_RE.finditer(log):
        missing_queue.add(match.group(1))
    failed_sources = sorted(
        {
            item["path"]
            for item in failures
            if str(Path(item["path"]).resolve()) in source_to_fqn
        }
    )
    return {
        "error_count": len(failures),
        "error_categories": dict(sorted(categories.items())),
        "failed_source_count": len(failed_sources),
        "failed_sources": failed_sources,
        "missing_symbol_queue": sorted(missing_queue),
        "failures": failures,
    }


def inventory(args: argparse.Namespace) -> int:
    output_dir = Path(args.output_dir).resolve()
    output_dir.mkdir(parents=True, exist_ok=True)
    with RunJar(Path(args.run_jar)) as run_jar:
        manifest = run_jar.manifest(Path(args.project_source).resolve())
    write_json(output_dir / "artifact-manifest.json", manifest)
    summary_keys = (
        "artifact_sha256",
        "artifact_bytes",
        "outer_member_count",
        "manifest_member_count",
        "nested_jar_count",
        "private_framework_jar_count",
        "public_dependency_jar_count",
        "application_class_count",
        "application_top_level_class_count",
        "application_top_level_source_present",
        "application_top_level_source_missing",
        "private_framework_class_entry_count",
        "private_framework_distinct_class_count",
        "private_framework_duplicate_class_count",
        "kind_counts",
    )
    summary = {key: manifest[key] for key in summary_keys}
    write_json(output_dir / "artifact-summary.json", summary)
    print(json.dumps(summary, ensure_ascii=False, indent=2, sort_keys=True))
    return 0


def pilot(args: argparse.Namespace) -> int:
    output_dir = Path(args.output_dir).resolve()
    output_dir.mkdir(parents=True, exist_ok=True)
    target_sources = [Path(item).resolve() for item in args.target_source]
    project_source = Path(args.project_source).resolve()
    project_sources = sorted(project_source.rglob("*.java"))
    full_project_closure = bool(args.project_closure)
    import_sources = (
        project_sources if full_project_closure else target_sources
    )
    compile_target_sources = (
        project_sources if full_project_closure else target_sources
    )
    provided_project_classes = project_class_names(project_source)
    transform_manifest_path = Path(args.transform_manifest).resolve()
    transform_manifest = load_transform_manifest(transform_manifest_path)
    for target in target_sources:
        if not target.is_file():
            raise FileNotFoundError(target)
    with RunJar(Path(args.run_jar)) as run_jar:
        manifest = run_jar.manifest(project_source)
        write_json(output_dir / "artifact-manifest.json", manifest)
        available_private_jars = tuple(
            sorted(
                Path(item.outer_member).name
                for item in run_jar.nested_jar_info.values()
                if item.private_framework
            )
        )
        private_jar_names = (
            available_private_jars if full_project_closure else PILOT_PRIVATE_JARS
        )
        private_entries = run_jar.private_class_entries(private_jar_names)
        imports = project_imports(import_sources)
        seeds = set(imports)
        seeds.update(args.seed_class or [])
        closure, edges, missing_external = resolve_closure(
            run_jar,
            private_entries,
            seeds,
            provided_classes=provided_project_classes,
        )

        if args.work_dir:
            work_path = Path(args.work_dir).resolve()
            work_path.mkdir(parents=True, exist_ok=True)
            work_context = nullcontext(str(work_path))
        else:
            work_context = tempfile.TemporaryDirectory(
                prefix="lims-source-closure-"
            )
        with work_context as temp_name:
            work = Path(temp_name)
            run_jar.write_nested_jars(work / "private-jars", private_jar_names)
            public_deps = run_jar.extract_public_dependencies(work / "public-deps")
            analysis_classpath = sorted(
                (work / "private-jars").glob("*.jar")
            ) + sorted((work / "public-deps").glob("*.jar"))
            web_count = run_jar.count_web_resources()
            web_resources = (
                run_jar.extract_web_resources(output_dir / "resources")
                if args.extract_web_resources
                else []
            )
            web_extracted_count = len(web_resources)
            if args.extract_web_resources:
                resource_tree = hashlib.sha256()
                for resource in web_resources:
                    resource_tree.update(str(resource["path"]).encode())
                    resource_tree.update(b"\0")
                    resource_tree.update(bytes.fromhex(str(resource["sha256"])))
                write_json(
                    output_dir / "web-resources.json",
                    {
                        "schema": "supplier-web-resource-provenance-v1",
                        "source_jar": f"BOOT-INF/lib/{CORE_WEB_JAR}",
                        "source_jar_sha256": run_jar.nested_jar_info[
                            f"BOOT-INF/lib/{CORE_WEB_JAR}"
                        ].sha256,
                        "resource_count": len(web_resources),
                        "resource_tree_sha256": resource_tree.hexdigest(),
                        "resources": web_resources,
                    },
                )
            source_dir = output_dir / "src"
            source_dir.mkdir(parents=True, exist_ok=True)
            provenance, source_to_fqn = decompile_pilot_sources(
                run_jar,
                source_dir,
                work,
                cfr_java=Path(args.java8),
                cfr_jar=Path(args.cfr_jar),
                vineflower_java=Path(args.vineflower_java) if args.vineflower_jar else None,
                vineflower_jar=Path(args.vineflower_jar) if args.vineflower_jar else None,
                analysis_classpath=analysis_classpath,
                primary_decompiler=args.primary_decompiler,
                closure=closure,
                transform_manifest=transform_manifest,
                private_jar_names=private_jar_names,
            )
            sources = sorted(source_dir.rglob("*.java"))
            fallback_compile_round_count = 0
            fallback_replacement_count = 0
            fallback_attempted: set[str] = set()
            if args.vineflower_jar and args.primary_decompiler == "cfr":
                compile_rc, compile_log = compile_java8(
                    Path(args.javac),
                    sources,
                    compile_target_sources,
                    work / "public-deps",
                    work / "classes",
                    work / "javac-first.log",
                )
                while compile_rc != 0:
                    fallback_compile_round_count += 1
                    failed = compile_summary(compile_log, source_to_fqn)["failed_sources"]
                    replacements = 0
                    for failed_source in failed:
                        source_path = Path(failed_source).resolve()
                        fqn = source_to_fqn.get(str(source_path))
                        if not fqn or fqn in fallback_attempted:
                            continue
                        jar_name = next(
                            (
                                entry.outer_member
                                for entry in private_entries.get(fqn, [])
                            ),
                            None,
                        )
                        if not jar_name:
                            continue
                        vf_rc = decompile_one_with_vineflower(
                            Path(args.vineflower_java),
                            Path(args.vineflower_jar),
                            work / "private-jars" / PurePosixPath(jar_name).name,
                            fqn,
                            source_path,
                            analysis_classpath,
                        )
                        fallback_attempted.add(fqn)
                        fallback_replacement_count += 1
                        if vf_rc != 0:
                            continue
                        sha_before_transform = sha256_file(source_path)
                        _transformed, applied_transformations = apply_transform_rules(
                            source_path,
                            source_dir,
                            transform_manifest,
                            "Vineflower",
                        )
                        for record in provenance:
                            if record["generated_source_path"] == str(
                                source_path.relative_to(output_dir)
                            ):
                                record["decompiler"] = "Vineflower"
                                record["decompiler_version"] = "1.11.1"
                                record["decompiler_exit_code"] = vf_rc
                                record["source_sha256_before_transform"] = (
                                    sha_before_transform
                                )
                                record["source_sha256"] = sha256_file(source_path)
                                record["transformations"] = [
                                    "cfr-to-vineflower-fallback",
                                    *(
                                        (
                                            item
                                            if str(item).startswith("normalization:")
                                            else f"rule:{item}"
                                        )
                                        for item in applied_transformations
                                    ),
                                ]
                                break
                        replacements += 1
                    if replacements == 0:
                        break
                    compile_rc, compile_log = compile_java8(
                        Path(args.javac),
                        sources,
                        compile_target_sources,
                        work / "public-deps",
                        work / f"classes-fallback-{fallback_compile_round_count}",
                        work / f"javac-fallback-{fallback_compile_round_count}.log",
                    )
            compile_rc, compile_log = compile_java8(
                Path(args.javac),
                sources,
                compile_target_sources,
                work / "public-deps",
                work / "classes-final",
                output_dir / "javac.log",
            )
            summary = compile_summary(compile_log, source_to_fqn)

    provenance.sort(
        key=lambda item: (
            str(item["generated_source_path"]),
            str(item["source_class_path"]),
        )
    )
    closure_payload = {
        "schema": "supplier-java-source-closure-v1",
        "generated_at": utc_now(),
        "run_jar_sha256": manifest["artifact_sha256"],
        "mode": "full-project-closure" if full_project_closure else "pilot",
        "project_source_file_count": len(project_sources),
        "compile_target_source_file_count": len(compile_target_sources),
        "provided_project_class_count": len(provided_project_classes),
        "project_imports": imports,
        "seed_classes": sorted(seeds),
        "framework_input_jars": list(private_jar_names),
        "closure_class_count": len(closure),
        "closure_classes": sorted(closure),
        "edges": {key: edges[key] for key in sorted(edges)},
        "missing_external_framework_classes": missing_external,
        "public_dependency_count": len(public_deps),
        "fallback_compile_round_count": fallback_compile_round_count,
        "fallback_replacement_count": fallback_replacement_count,
        "public_dependencies": public_deps,
        "transformation_manifest": display_path(transform_manifest_path),
        "transformation_manifest_sha256": sha256_file(transform_manifest_path),
        "transformed_source_count": sum(
            bool(
                [
                    item
                    for item in record["transformations"]
                    if str(item).startswith("rule:")
                ]
            )
            for record in provenance
        ),
        "normalized_source_count": sum(
            "normalization:generated-source" in record["transformations"]
            for record in provenance
        ),
        "web_resource_count": web_count,
        "web_resource_extracted_count": web_extracted_count,
        "source_file_count": len(provenance),
        "source_tree_sha256": source_tree_digest(provenance),
        "source_decompilers": dict(
            sorted(Counter(str(item["decompiler"]) for item in provenance).items())
        ),
        "provenance": provenance,
        "compile": {
            "exit_code": compile_rc,
            "status": "pass" if compile_rc == 0 else "fail",
            "java_version": "8",
            "source_only_framework_classpath": True,
            "supplier_private_jars_on_classpath": False,
            **summary,
        },
        "gate_status": (
            (
                "project-source-compile-pass"
                if full_project_closure
                else "pilot-compile-pass"
            )
            if compile_rc == 0
            else (
                "project-source-compile-failed"
                if full_project_closure
                else "pilot-compile-failed"
            )
        ),
    }
    write_json(output_dir / "source-closure.json", closure_payload)
    print(
        json.dumps(
            {
                "gate_status": closure_payload["gate_status"],
                "mode": closure_payload["mode"],
                "run_jar_sha256": manifest["artifact_sha256"],
                "seed_count": len(seeds),
                "closure_class_count": len(closure),
                "missing_external_framework_class_count": len(missing_external),
                "decompiled_source_count": len(provenance),
                "transformed_source_count": closure_payload["transformed_source_count"],
                "normalized_source_count": closure_payload[
                    "normalized_source_count"
                ],
                "transformation_manifest_sha256": closure_payload[
                    "transformation_manifest_sha256"
                ],
                "compile_exit_code": compile_rc,
                "compile_error_count": summary["error_count"],
                "compile_error_categories": summary["error_categories"],
                "public_dependency_count": len(public_deps),
                "web_resource_count": web_count,
                "web_resource_extracted_count": web_extracted_count,
            },
            ensure_ascii=False,
            indent=2,
            sort_keys=True,
        )
    )
    return compile_rc


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description=__doc__)
    subparsers = parser.add_subparsers(dest="command", required=True)

    inventory_parser = subparsers.add_parser("inventory")
    inventory_parser.add_argument("--run-jar", default=str(DEFAULT_RUN_JAR))
    inventory_parser.add_argument(
        "--project-source", default=str(ROOT / "project" / "src" / "main" / "java")
    )
    inventory_parser.add_argument("--output-dir", required=True)
    inventory_parser.set_defaults(func=inventory)

    pilot_parser = subparsers.add_parser("pilot")
    pilot_parser.add_argument("--run-jar", default=str(DEFAULT_RUN_JAR))
    pilot_parser.add_argument(
        "--project-source", default=str(ROOT / "project" / "src" / "main" / "java")
    )
    pilot_parser.add_argument("--target-source", action="append", required=True)
    pilot_parser.add_argument("--seed-class", action="append")
    pilot_parser.add_argument(
        "--project-closure",
        action="store_true",
        help=(
            "seed from every project Java import, use all private framework "
            "JARs and compile the complete project source tree"
        ),
    )
    pilot_parser.add_argument("--output-dir", required=True)
    pilot_parser.add_argument(
        "--work-dir",
        help="keep extraction/decompiler/compiler work under this isolated directory",
    )
    pilot_parser.add_argument(
        "--transform-manifest",
        default=str(DEFAULT_TRANSFORM_MANIFEST),
        help="tracked deterministic source transformation manifest",
    )
    pilot_parser.add_argument(
        "--extract-web-resources",
        action="store_true",
        help="materialize core-web resources under output-dir/resources",
    )
    pilot_parser.add_argument("--cfr-jar", default=str(DEFAULT_CFR))
    pilot_parser.add_argument("--java8", default=str(DEFAULT_JDK / "bin" / "java"))
    pilot_parser.add_argument("--javac", default=str(DEFAULT_JDK / "bin" / "javac"))
    pilot_parser.add_argument(
        "--vineflower-jar",
        default=os.environ.get("VINEFLOWER_JAR", ""),
    )
    pilot_parser.add_argument(
        "--vineflower-java",
        default=os.environ.get("VINEFLOWER_JAVA", "java"),
    )
    pilot_parser.add_argument(
        "--primary-decompiler",
        choices=("cfr", "vineflower"),
        default="cfr",
    )
    pilot_parser.set_defaults(func=pilot)
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    if args.command == "pilot" and not args.vineflower_jar:
        args.vineflower_jar = None
    return args.func(args)


if __name__ == "__main__":
    raise SystemExit(main())
