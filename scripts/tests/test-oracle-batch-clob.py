#!/usr/bin/env python3
"""Exercise the real OracleDialect task-history CLOB batch with optional config."""
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]

if __name__ == "__main__":
    jdk = ROOT / "build/jdk1.8.0_391/bin"
    classes = ROOT / "reconstituted/framework/owned-core-common/target/classes"
    public = ROOT / "build/reconstituted-public-deps"
    fixture = ROOT / "scripts/tests/fixtures/oracle-batch-clob/OracleBatchClobCheck.java"
    with tempfile.TemporaryDirectory(prefix="oracle-batch-clob-") as tmp:
        cp = ":".join((tmp, str(classes), str(public / "*")))
        # Compile the current source over the last built classes so the test goes
        # red/green without changing any shared framework target directory.
        source = ROOT / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/dialect/OracleDialect.java"
        subprocess.run([str(jdk / "javac"), "-encoding", "UTF-8", "-cp", cp,
                        "-d", tmp, str(source), str(fixture)], check=True)
        for setting in ("absent", "false", "true"):
            subprocess.run([str(jdk / "java"), "-cp", cp,
                            "com.sunwayworld.framework.database.dialect.OracleBatchClobCheck",
                            setting], check=True)
