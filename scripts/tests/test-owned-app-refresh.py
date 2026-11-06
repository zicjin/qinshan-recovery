#!/usr/bin/env python3
from pathlib import Path
import subprocess


ROOT = Path(__file__).resolve().parents[2]


def main() -> int:
    refresh = (ROOT / "scripts/refresh-owned-sss2-app.sh").read_text(encoding="utf-8")
    rollback = (ROOT / "scripts/rollback-owned-sss2-app.sh").read_text(encoding="utf-8")
    assert "docker rm --force \"$APP\"" in refresh
    assert "reused_oracle" in refresh and "reused_redis" in refresh
    assert "oracle_restarted\":false" in refresh
    assert "supplier_18081_preserved" in refresh and "debug_18088_preserved" in refresh
    assert "verify-source-only-checkpoint.py" in rollback
    assert "refresh-owned-sss2-app.sh" in rollback
    assert "docker rm" not in rollback
    assert "docker stop" not in rollback
    result = subprocess.run(
        ["bash", str(ROOT / "scripts/rollback-owned-sss2-app.sh")],
        text=True, capture_output=True, check=False,
    )
    assert result.returncode == 2 and "previous-owned-distribution" in result.stderr
    print("owned app refresh/rollback contract: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
