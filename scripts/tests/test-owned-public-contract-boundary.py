#!/usr/bin/env python3
"""Keep project-owned internals separate from strict supplier-facing contracts."""
import os
from pathlib import Path
import subprocess
import tempfile
import zipfile

root = Path(__file__).resolve().parents[2]
artifact = root / "owned-framework/target/lims-owned-framework-1.0.0.jar"
checker = root / "scripts/tests/test-owned-public-contracts.py"
jdk = Path(os.environ.get("JAVA_HOME", root / "build/jdk1.8.0_391"))
with tempfile.TemporaryDirectory(prefix="owned-contract-boundary-") as directory:
    temporary = Path(directory)
    for package, accepted in [("com.sunwayworld.owned.internal.converter", True),
                              ("com.sunwayworld.framework.converter", False),
                              ("com.sunwayworld.owned.internals.converter", False)]:
        source = temporary / "BoundaryProbe.java"
        source.write_text("package " + package + "; public final class BoundaryProbe {}\n")
        subprocess.run([str(jdk / "bin/javac"), "-source", "8", "-target", "8", "-d", str(temporary), str(source)], check=True)
        entry = package.replace(".", "/") + "/BoundaryProbe.class"
        probe = temporary / (package + ".jar")
        probe.write_bytes(artifact.read_bytes())
        with zipfile.ZipFile(probe, "a") as archive:
            archive.writestr(entry, (temporary / entry).read_bytes())
        result = subprocess.run(["python3", str(checker), str(probe)], text=True, capture_output=True)
        assert (result.returncode == 0) == accepted, (package, result.stdout, result.stderr)
        if not accepted:
            assert "owned class has no selected supplier contract: " + entry in result.stderr
print("owned declaration package boundary: PASS (internal accepted; compatibility and lookalike rejected)")
