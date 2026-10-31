#!/usr/bin/env python3
"""Reject PostgreSQL runtime inputs from the owned Oracle application mainline."""
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[2]
OWNED = tuple(ROOT.glob("owned-*/src"))
FORBIDDEN = (
    re.compile(r"org\.postgresql", re.I),
    re.compile(r"jdbc:postgresql", re.I),
    re.compile(r"postgresql-[0-9].*\.jar", re.I),
)


def source_hits():
    hits = []
    for source_root in OWNED:
        for path in source_root.rglob("*"):
            if not path.is_file() or "test" in path.parts:
                continue
            try:
                text = path.read_text(encoding="utf-8")
            except UnicodeDecodeError:
                continue
            for pattern in FORBIDDEN[:2]:
                if pattern.search(text):
                    hits.append(f"{path.relative_to(ROOT)}:{pattern.pattern}")
    return hits


def distribution_hits():
    distribution = ROOT / "owned-distribution/target/distribution"
    if not distribution.is_dir():
        return []
    hits = []
    for path in distribution.rglob("*"):
        if path.name.lower().startswith("postgresql-") and path.suffix == ".jar":
            hits.append(str(path.relative_to(ROOT)))
        if not path.is_file() or path.suffix not in {".jar", ".properties", ".xml"}:
            continue
        if path.suffix == ".jar":
            try:
                with zipfile.ZipFile(path) as archive:
                    names = archive.namelist()
                    if any(pattern.search(name) for pattern in FORBIDDEN[2:] for name in names):
                        hits.append(str(path.relative_to(ROOT)))
            except zipfile.BadZipFile:
                hits.append(str(path.relative_to(ROOT)) + ":invalid-archive")
        else:
            text = path.read_text(encoding="utf-8", errors="replace")
            if any(pattern.search(text) for pattern in FORBIDDEN[:2]):
                hits.append(str(path.relative_to(ROOT)))
    return hits


source = source_hits()
distribution = distribution_hits()
if source or distribution:
    raise SystemExit("Oracle mainline contains PostgreSQL runtime input: " + ", ".join(source + distribution))
print("Oracle mainline only: PASS (owned sources and available distribution contain no PostgreSQL runtime path)")
