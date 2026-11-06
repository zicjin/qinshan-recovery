#!/usr/bin/env python3
"""Prepare an isolated Maven repository from the hash-pinned plugin seed."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import shutil
import tarfile
import zipfile


ROOT = Path(__file__).resolve().parents[1]
REQUIRED_ARTIFACTS = (
    "org/apache/maven/plugins/maven-clean-plugin/3.2.0/maven-clean-plugin-3.2.0.jar",
    "org/apache/maven/plugins/maven-compiler-plugin/3.8.1/maven-compiler-plugin-3.8.1.jar",
    "org/apache/maven/plugins/maven-jar-plugin/3.1.2/maven-jar-plugin-3.1.2.jar",
    "org/apache/maven/plugins/maven-resources-plugin/3.3.1/maven-resources-plugin-3.3.1.jar",
)


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def expected_seed_hash(manifest: Path, seed: Path) -> str:
    for line in manifest.read_text(encoding="utf-8").splitlines():
        if not line or line.startswith("#"):
            continue
        expected, relative = line.split(maxsplit=1)
        if PurePosixPath(relative).name == seed.name:
            return expected
    raise SystemExit(f"plugin seed is not registered in {manifest}")


def validate_archive(path: Path) -> None:
    with tarfile.open(path, "r:*") as archive:
        for member in archive.getmembers():
            name = member.name
            if name in {"", ".", "./"}:
                continue
            normalized = PurePosixPath(name)
            if normalized.is_absolute() or ".." in normalized.parts or "\\" in name:
                raise SystemExit(f"unsafe plugin seed path: {name}")
            if member.isdir() or member.isreg():
                continue
            raise SystemExit(f"unsupported plugin seed member: {name}")


def jar_maven_coordinates(path: Path) -> list[tuple[str, str, str, bytes | None]]:
    records = []
    with zipfile.ZipFile(path) as archive:
        names = set(archive.namelist())
        for name in sorted(names):
            prefix = "META-INF/maven/"
            if not name.startswith(prefix) or not name.endswith("/pom.properties"):
                continue
            properties = {}
            for line in archive.read(name).decode("utf-8").splitlines():
                if line and not line.startswith("#") and "=" in line:
                    key, value = line.split("=", 1)
                    properties[key.strip()] = value.strip()
            group = properties["groupId"]
            artifact = properties["artifactId"]
            version = properties["version"]
            pom_name = name[:-len("pom.properties")] + "pom.xml"
            pom = archive.read(pom_name) if pom_name in names else None
            records.append((group, artifact, version, pom))
    return records


def minimal_pom(group: str, artifact: str, version: str) -> bytes:
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<project xmlns="http://maven.apache.org/POM/4.0.0">\n'
        "  <modelVersion>4.0.0</modelVersion>\n"
        f"  <groupId>{group}</groupId>\n"
        f"  <artifactId>{artifact}</artifactId>\n"
        f"  <version>{version}</version>\n"
        "</project>\n"
    ).encode("utf-8")


def populate_public_dependencies(
    output: Path,
    public_deps: Path,
    allowlist_path: Path,
) -> int:
    allowlist = json.loads(allowlist_path.read_text(encoding="utf-8"))
    entries = {item["filename"]: item for item in allowlist["entries"]}
    artifacts: dict[tuple[str, str, str], tuple[str, bytes | None]] = {}
    for jar in sorted(public_deps.glob("*.jar")):
        entry = entries.get(jar.name)
        if not entry:
            raise SystemExit(f"public dependency is not registered: {jar.name}")
        data = jar.read_bytes()
        if len(data) != int(entry["size"]) or hashlib.sha256(data).hexdigest() != entry["sha256"]:
            raise SystemExit(f"public dependency identity mismatch: {jar.name}")
        records = jar_maven_coordinates(jar)
        if not records:
            coordinates = entry.get("maven_coordinates_or_unknown", [])
            valid = [item for item in coordinates if isinstance(item, str) and item.count(":") == 2]
            if len(valid) == 1:
                records = [(*valid[0].split(":"), None)]
        for group, artifact, version, pom in records:
            key = (group, artifact, version)
            previous = artifacts.get(key)
            if previous and previous[0] != jar.name:
                raise SystemExit(f"conflicting Maven artifact {key}: {previous[0]} and {jar.name}")
            artifacts[key] = (jar.name, pom)

    for (group, artifact, version), (source_name, pom) in sorted(artifacts.items()):
        directory = output / group.replace(".", "/") / artifact / version
        directory.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(public_deps / source_name, directory / f"{artifact}-{version}.jar")
        (directory / f"{artifact}-{version}.pom").write_bytes(
            pom or minimal_pom(group, artifact, version)
        )
    return len(artifacts)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path)
    parser.add_argument(
        "--seed",
        type=Path,
        default=ROOT / ".external-materials" / "maven-build-plugin-seed.tar.gz",
    )
    parser.add_argument(
        "--manifest",
        type=Path,
        default=ROOT / "build-support" / "t02-build-plugin-seed.sha256",
    )
    parser.add_argument("--public-deps", type=Path)
    parser.add_argument(
        "--allowlist",
        type=Path,
        default=ROOT / "reconstituted" / "framework" / "public-dependency-allowlist.json",
    )
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    seed = args.seed.resolve()
    manifest = args.manifest.resolve()
    output = args.output.resolve()
    if not seed.is_file():
        raise SystemExit(f"missing Maven plugin seed: {seed}")
    if not manifest.is_file():
        raise SystemExit(f"missing Maven plugin seed manifest: {manifest}")

    expected = expected_seed_hash(manifest, seed)
    actual = sha256_file(seed)
    if actual != expected:
        raise SystemExit("Maven plugin seed SHA-256 mismatch")
    validate_archive(seed)

    if output.exists():
        if not args.overwrite:
            raise SystemExit(f"output already exists: {output}")
        shutil.rmtree(output)
    output.mkdir(parents=True)
    with tarfile.open(seed, "r:*") as archive:
        archive.extractall(output)

    missing = [item for item in REQUIRED_ARTIFACTS if not (output / item).is_file()]
    if missing:
        raise SystemExit("plugin seed is missing required artifacts: " + ", ".join(missing))
    public_artifact_count = 0
    if args.public_deps:
        public_artifact_count = populate_public_dependencies(
            output,
            args.public_deps.resolve(),
            args.allowlist.resolve(),
        )
    print(
        json.dumps(
            {
                "status": "pass",
                "output": str(output),
                "plugin_seed_sha256": actual,
                "required_artifact_count": len(REQUIRED_ARTIFACTS),
                "public_artifact_count": public_artifact_count,
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
