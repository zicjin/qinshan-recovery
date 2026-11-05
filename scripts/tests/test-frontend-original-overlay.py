#!/usr/bin/env python3
"""Check that the imported supplier frontend is the owned default input."""
import csv
import hashlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
FRONTEND = ROOT / "owned-web/src/main/resources/frontend-original"
SIMPLIFIED = ROOT / "owned-web/src/main/resources/static"


def main():
    provenance = FRONTEND / "provenance.tsv"
    assert provenance.is_file(), "original frontend provenance is missing"
    rows = list(csv.DictReader(provenance.open(encoding="utf-8"), delimiter="\t"))
    assert len(rows) >= 14
    for row in rows:
        path = FRONTEND / row["path"]
        assert path.is_file(), row["path"]
        assert path.stat().st_size == int(row["size"]), row["path"]
        assert hashlib.sha256(path.read_bytes()).hexdigest() == row["sha256"], row["path"]
        expected_sources = {"sss2:18081"}
        if row["provenance"] == "optional-css-fallback":
            expected_sources.add("owned-web")
        assert row["source"] in expected_sources, row["path"]
    for name, minimum in {
        "chunk-common.js": 1_000_000,
        "chunk-vendors.js": 1_000_000,
        "home.js": 50_000,
        "workspace.js": 100_000,
    }.items():
        path = FRONTEND / "static/gikam/main/js" / name
        assert path.stat().st_size >= minimum, name
    workspace = FRONTEND / "workspace.html"
    assert workspace.is_file(), "original authenticated workspace HTML is missing"
    workspace_row = next(row for row in rows if row["path"] == "workspace.html")
    assert workspace.stat().st_size == int(workspace_row["size"])
    assert hashlib.sha256(workspace.read_bytes()).hexdigest() == workspace_row["sha256"]
    workspace_text = workspace.read_bytes().decode("utf-8", "ignore")
    assert "owned-web" not in workspace_text
    assert "owned-gikam" not in workspace_text
    assert "return v.initEln()" not in workspace.read_text(encoding="utf-8", errors="ignore")
    assert not (SIMPLIFIED / "gikam").exists(), "simplified Gikam shell still overrides original frontend"
    assert not (SIMPLIFIED / "module/lcdp").exists(), "simplified LCDP shell still overrides original frontend"
    forbidden = ("/core/module/sys/licenses", "/open/security/licenses", "sunway-app.so")
    for path in (FRONTEND / "static").rglob("*"):
        if path.is_file():
            body = path.read_bytes().decode("utf-8", "ignore")
            assert not any(token in body for token in forbidden), str(path)
    print("frontend original overlay: PASS")


if __name__ == "__main__":
    main()
