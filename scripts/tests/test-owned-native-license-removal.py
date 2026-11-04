#!/usr/bin/env python3
import json
from pathlib import Path
import subprocess
import tempfile
import zipfile


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "verify-owned-native-license-removal.py"


def run(root: Path, distribution: Path, output: Path):
    return subprocess.run(
        ["python3", str(SCRIPT), "--source-root", str(root),
         "--distribution", str(distribution), "--output", str(output)],
        text=True, capture_output=True,
    )


def main():
    with tempfile.TemporaryDirectory() as temp:
        root = Path(temp)
        source = root / "source"
        source.mkdir()
        distribution = root / "distribution"
        distribution.mkdir()
        output = root / "report.json"
        (source / "business.java").write_text("String license = \"business\";\n")
        (source / "NOTICE").write_text("Licensed under MIT.\n")
        result = run(source, distribution, output)
        assert result.returncode == 0, result.stderr
        assert json.loads(output.read_text())["status"] == "pass"

        (source / "loader.java").write_text("static { System.loadLibrary(\"sunway\"); }\n")
        result = run(source, distribution, output)
        assert result.returncode != 0
        assert json.loads(output.read_text())["status"] == "fail"
        (source / "loader.java").unlink()

        archive = distribution / "app.jar"
        with zipfile.ZipFile(archive, "w") as jar:
            jar.writestr("third-party/sunway-app/linux/sunway-app.so", b"native")
        result = run(source, distribution, output)
        assert result.returncode != 0
        assert any("forbidden file name" in item["reason"] for item in json.loads(output.read_text())["findings"])
    print("owned native/license removal: PASS")


if __name__ == "__main__":
    main()
