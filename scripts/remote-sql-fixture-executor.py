#!/usr/bin/env python3
"""Execute a narrowly scoped SQL fixture in the current remote runtime schema.

The executor is only for schema/fixture control. Business rows under test must
still be created, changed and deleted through the Spring HTTP API. Credentials
are read inside the remote app container and never printed or written to the
fixture report.
"""
from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
FIXTURE_ROOT = ROOT / "api-fixtures"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--fixture", type=Path, required=True)
    parser.add_argument("--phase", choices=("setup", "restore"), required=True)
    args = parser.parse_args()
    fixture = args.fixture.resolve()
    fixture.relative_to(FIXTURE_ROOT.resolve())
    sql = fixture.read_text(encoding="utf-8")
    manifest_path = os.environ.get("OWNED_RUNTIME_MANIFEST")
    ssh_host = os.environ.get("OWNED_RUNTIME_SSH_HOST", "sss2")
    if not manifest_path:
        raise SystemExit("OWNED_RUNTIME_MANIFEST is required")
    import json
    manifest = json.loads(Path(manifest_path).read_text(encoding="utf-8"))
    oracle = str(manifest.get("oracle_container", ""))
    app = str((manifest.get("runtime_identity") or {}).get("application", ""))
    if (not oracle or not app or
            any(not re.fullmatch(r"[A-Za-z0-9_-]+", value) for value in (oracle, app))):
        raise SystemExit("runtime manifest lacks safe oracle/application identity")
    remote = r'''set -eu
ORACLE="$1"
APP="$2"
USER="$(docker exec "$APP" sh -c "grep '^spring.datasource.username=' /run/config/application.properties | tail -1 | cut -d= -f2- | tr -d '\\r'")"
PASS="$(docker exec "$APP" sh -c "grep '^spring.datasource.password=' /run/config/application.properties | tail -1 | cut -d= -f2- | tr -d '\\r'")"
test -n "$USER"; test -n "$PASS"
docker exec -i "$ORACLE" env RUNTIME_USER="$USER" RUNTIME_PASS="$PASS" sh -c 'export ORACLE_HOME=${ORACLE_HOME:-/opt/oracle/product/21c/dbhomeXE}; export PATH=$ORACLE_HOME/bin:$PATH; sqlplus -s "$RUNTIME_USER/$RUNTIME_PASS@//localhost/XEPDB1"'
'''
    result = subprocess.run(
        ["ssh", "-o", "BatchMode=yes", "-o", "ConnectTimeout=8", ssh_host,
         "bash", "-s", "--", oracle, app],
        input=(remote + "\n").encode("utf-8") + sql.encode("utf-8"),
        stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=False,
    )
    if result.returncode:
        raise SystemExit("remote fixture executor failed (phase=%s, exit=%d)" %
                         (args.phase, result.returncode))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
