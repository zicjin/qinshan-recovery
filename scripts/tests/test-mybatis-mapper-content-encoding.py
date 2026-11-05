#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TARGET = ROOT / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/MybatisHelper.java"


def main() -> int:
    source = TARGET.read_text(encoding="utf-8")
    assert "StandardCharsets.UTF_8" in source, "MybatisHelper must read XML resource with explicit UTF-8"
    assert "new InputStreamReader(is, StandardCharsets.UTF_8)" in source
    print("Mybatis mapper content encoding contract: PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
