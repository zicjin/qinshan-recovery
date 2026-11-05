#!/usr/bin/env python3
"""Prepare an isolated Maven repository from the hash-pinned plugin seed."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import shutil
import tarfile


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
        default=ROOT / "evidence" / "t02-build-plugin-seed.sha256",
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
    print(
        json.dumps(
            {
                "status": "pass",
                "output": str(output),
                "plugin_seed_sha256": actual,
                "required_artifact_count": len(REQUIRED_ARTIFACTS),
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
