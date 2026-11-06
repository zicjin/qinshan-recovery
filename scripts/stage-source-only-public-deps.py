#!/usr/bin/env python3
"""Stage hash-pinned public dependencies for source-only compilation."""
from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil


ROOT = Path(__file__).resolve().parents[1]


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--input",
        type=Path,
        default=Path(
            os.environ.get(
                "LIMS_PUBLIC_DEPENDENCY_DIR",
                ROOT / "build/reconstituted-public-deps",
            )
        ),
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=ROOT / "build/reconstituted-public-deps",
    )
    parser.add_argument(
        "--allowlist",
        type=Path,
        default=ROOT / "reconstituted/framework/public-dependency-allowlist.json",
    )
    args = parser.parse_args()
    source = args.input.resolve()
    output = args.output.resolve()
    allowlist = json.loads(args.allowlist.read_text(encoding="utf-8"))
    expected = {
        str(item["filename"]): item
        for item in allowlist.get("entries", [])
        if not item.get("prepare_excluded")
    }
    actual = {path.name: path for path in source.glob("*.jar")}
    missing = sorted(set(expected) - set(actual))
    extra = sorted(set(actual) - set(expected))
    if missing or extra:
        raise SystemExit(
            f"public dependency input differs from allowlist: missing={missing[:8]} extra={extra[:8]}"
        )
    for filename, item in expected.items():
        path = actual[filename]
        if path.stat().st_size != int(item["size"]):
            raise SystemExit(f"public dependency size mismatch: {filename}")
        if sha256(path) != item["sha256"]:
            raise SystemExit(f"public dependency hash mismatch: {filename}")
    if output.exists():
        # The standalone build uses the default directory as both its
        # externally supplied input and its staged output.  Do not delete the
        # input before copying from it: that turns a valid dependency set into
        # an empty directory and makes the next build fail with a misleading
        # missing-JAR error.  Validation above already proves that this tree
        # is the exact pinned allowlist, so it is safe to reuse it in place.
        if source == output:
            print(
                json.dumps(
                    {
                        "status": "pass",
                        "input": str(source),
                        "output": str(output),
                        "public_dependency_count": len(expected),
                        "staged_in_place": True,
                    },
                    sort_keys=True,
                )
            )
            return 0
        shutil.rmtree(output)
    output.mkdir(parents=True)
    for filename in sorted(expected):
        shutil.copyfile(actual[filename], output / filename)
    print(
        json.dumps(
            {
                "status": "pass",
                "input": str(source),
                "output": str(output),
                "public_dependency_count": len(expected),
            },
            sort_keys=True,
        )
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
