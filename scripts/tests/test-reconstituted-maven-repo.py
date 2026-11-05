#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import importlib.util
import io
from pathlib import Path
import subprocess
import sys
import tarfile
import tempfile


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts" / "prepare-reconstituted-maven-repo.py"
REQUIRED = (
    "org/apache/maven/plugins/maven-clean-plugin/3.2.0/maven-clean-plugin-3.2.0.jar",
    "org/apache/maven/plugins/maven-compiler-plugin/3.8.1/maven-compiler-plugin-3.8.1.jar",
    "org/apache/maven/plugins/maven-jar-plugin/3.1.2/maven-jar-plugin-3.1.2.jar",
    "org/apache/maven/plugins/maven-resources-plugin/3.3.1/maven-resources-plugin-3.3.1.jar",
)


def load_module():
    spec = importlib.util.spec_from_file_location("prepare_reconstituted_maven_repo", SCRIPT)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


def write_seed(path: Path, unsafe: bool = False) -> None:
    with tarfile.open(path, "w:gz") as archive:
        for relative in REQUIRED:
            data = b"fixture"
            info = tarfile.TarInfo(relative)
            info.size = len(data)
            archive.addfile(info, io.BytesIO(data))
        if unsafe:
            data = b"unsafe"
            info = tarfile.TarInfo("../escape")
            info.size = len(data)
            archive.addfile(info, io.BytesIO(data))


def main() -> int:
    module = load_module()
    with tempfile.TemporaryDirectory(prefix="reconstituted-maven-repo-test-") as temp_name:
        root = Path(temp_name)
        seed = root / "maven-build-plugin-seed.tar.gz"
        write_seed(seed)
        expected = hashlib.sha256(seed.read_bytes()).hexdigest()
        manifest = root / "seed.sha256"
        manifest.write_text(f"{expected}  materials/{seed.name}\n", encoding="utf-8")
        output = root / "repo"
        result = subprocess.run(
            [
                sys.executable,
                str(SCRIPT),
                str(output),
                "--seed",
                str(seed),
                "--manifest",
                str(manifest),
            ],
            text=True,
            capture_output=True,
        )
        assert result.returncode == 0, result.stderr
        assert all((output / relative).is_file() for relative in REQUIRED)

        manifest.write_text(f"{'0' * 64}  materials/{seed.name}\n", encoding="utf-8")
        failed = subprocess.run(
            [
                sys.executable,
                str(SCRIPT),
                str(root / "bad-repo"),
                "--seed",
                str(seed),
                "--manifest",
                str(manifest),
            ],
            text=True,
            capture_output=True,
        )
        assert failed.returncode != 0
        assert "SHA-256 mismatch" in failed.stderr

        unsafe_seed = root / "unsafe.tar.gz"
        write_seed(unsafe_seed, unsafe=True)
        try:
            module.validate_archive(unsafe_seed)
        except SystemExit as error:
            assert "unsafe plugin seed path" in str(error)
        else:
            raise AssertionError("unsafe plugin seed path was accepted")

    print("reconstituted Maven repository preparation: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
