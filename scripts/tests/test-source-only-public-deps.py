#!/usr/bin/env python3
from pathlib import Path
import hashlib
import json
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/stage-source-only-public-deps.py"


def main() -> int:
    with tempfile.TemporaryDirectory(prefix="source-only-public-deps-") as temporary:
        root = Path(temporary)
        source = root / "source"
        output = root / "output"
        source.mkdir()
        dependency = source / "fixture.jar"
        dependency.write_bytes(b"public-dependency")
        allowlist = root / "allowlist.json"
        allowlist.write_text(json.dumps({
            "entries": [{
                "filename": dependency.name,
                "size": dependency.stat().st_size,
                "sha256": hashlib.sha256(dependency.read_bytes()).hexdigest(),
            }],
        }) + "\n", encoding="utf-8")
        result = subprocess.run(
            [
                sys.executable,
                str(SCRIPT),
                "--input",
                str(source),
                "--output",
                str(output),
                "--allowlist",
                str(allowlist),
            ],
            text=True,
            capture_output=True,
            check=False,
        )
        assert result.returncode == 0, result.stdout + result.stderr
        assert (output / dependency.name).read_bytes() == dependency.read_bytes()
        (output / "stale.jar").write_bytes(b"stale")
        dependency.write_bytes(b"X" * 17)
        failed = subprocess.run(
            [
                sys.executable,
                str(SCRIPT),
                "--input",
                str(source),
                "--output",
                str(output),
                "--allowlist",
                str(allowlist),
            ],
            text=True,
            capture_output=True,
            check=False,
        )
        assert failed.returncode != 0
        assert "hash mismatch" in failed.stderr
    print("source-only public dependency staging: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
