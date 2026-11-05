#!/usr/bin/env python3
from __future__ import annotations

import importlib.util
from pathlib import Path
import shutil
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parents[2]


def load(name: str, path: Path):
    spec = importlib.util.spec_from_file_location(name, path)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def main() -> int:
    builder = load(
        "source_only_builder_parameters",
        ROOT / "scripts/build-source-only-application.py",
    )
    assembler = load(
        "reconstituted_framework_assembler_parameters",
        ROOT / "scripts/assemble-reconstituted-framework-modules.py",
    )

    assert "-parameters" in builder.PROJECT_JAVAC_FLAGS
    parent_pom = assembler.render_parent_pom(["owned-core-common"])
    assert "<parameters>true</parameters>" in parent_pom

    javac = shutil.which("javac")
    if javac is None:
        raise SystemExit("missing javac for parameter metadata test")
    with tempfile.TemporaryDirectory(prefix="java-parameter-metadata-") as name:
        root = Path(name)
        source = root / "ParameterFixture.java"
        classes = root / "classes"
        classes.mkdir()
        source.write_text(
            "public class ParameterFixture {"
            " public void lookup(Long orderItemGroupId) {}"
            "}\n",
            encoding="utf-8",
        )
        subprocess.run(
            [javac, *builder.PROJECT_JAVAC_FLAGS, "-d", str(classes), str(source)],
            check=True,
            capture_output=True,
            text=True,
        )
        classfile = (classes / "ParameterFixture.class").read_bytes()
        assert b"MethodParameters" in classfile
        assert b"orderItemGroupId" in classfile

    print("Java parameter metadata contract: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
