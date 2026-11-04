#!/usr/bin/env python3
"""Extract hash-pinned public dependencies for reconstituted module compilation.

The stock supplier run JAR is a read-only controlled source for public Maven
libraries. No private framework JAR or supplier runtime class is copied.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import zipfile


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_ALLOWLIST = ROOT / "reconstituted" / "framework" / "public-dependency-allowlist.json"
PRIVATE_NAME_MARKERS = (
    "gikam-core-",
    "lims2-report-template-",
    "sunway-app.",
    "license.lic",
    "vendor.lic",
)


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("output", type=Path)
    parser.add_argument("--allowlist", type=Path, default=DEFAULT_ALLOWLIST)
    parser.add_argument(
        "--run-jar",
        type=Path,
        default=Path(
            os.environ.get(
                "SUPPLIER_RUN_JAR",
                ROOT / ".external-materials" / "gikam-third-czjky-14.1.5-run.jar",
            )
        ),
    )
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    allowlist = json.loads(args.allowlist.read_text(encoding="utf-8"))
    if allowlist.get("schema") != "pinned-public-dependency-allowlist-v1":
        raise SystemExit("unsupported public dependency allowlist schema")
    actual_run_sha256 = sha256_file(args.run_jar)
    if actual_run_sha256 != allowlist["run_jar_sha256"]:
        raise SystemExit("supplier run JAR identity does not match allowlist")

    output = args.output.resolve()
    if output.exists():
        if not args.overwrite:
            raise SystemExit(f"output already exists: {output}")
        shutil.rmtree(output)
    output.mkdir(parents=True)

    seen: set[str] = set()
    with zipfile.ZipFile(args.run_jar) as archive:
        for item in sorted(allowlist["entries"], key=lambda value: value["filename"]):
            filename = str(item["filename"])
            lower = filename.lower()
            if any(marker in lower for marker in PRIVATE_NAME_MARKERS):
                raise SystemExit(f"private/supplier dependency in allowlist: {filename}")
            if filename in seen:
                raise SystemExit(f"duplicate public dependency: {filename}")
            seen.add(filename)
            member = "BOOT-INF/lib/" + filename
            data = archive.read(member)
            if len(data) != int(item["size"]):
                raise SystemExit(f"public dependency size mismatch: {filename}")
            if sha256_bytes(data) != item["sha256"]:
                raise SystemExit(f"public dependency hash mismatch: {filename}")
            (output / filename).write_bytes(data)

    print(
        json.dumps(
            {
                "status": "pass",
                "run_jar_sha256": actual_run_sha256,
                "public_dependency_count": len(seen),
                "output": str(output),
            },
            indent=2,
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
