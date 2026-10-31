#!/usr/bin/env python3
"""Normalize a Spring Boot JAR for byte-for-byte reproducible packaging.

The Maven/Spring Boot versions used by this recovery preserve filesystem
iteration order and current ZIP timestamps.  Neither affects application
content, but both make two clean builds hash differently.  Rewriting entries
in sorted-name order with a fixed DOS timestamp makes the archive deterministic
while preserving entry bytes, compression type, permissions, and executable
layout.  The input is replaced atomically only after the normalized temporary
archive is complete.
"""

from __future__ import annotations

import os
import sys
import tempfile
import zipfile
from pathlib import Path


FIXED_DATE = (1980, 1, 1, 0, 0, 0)


def normalize(path: Path) -> None:
    if not path.is_file() or path.is_symlink():
        raise SystemExit(f"not a regular JAR: {path}")
    mode = path.stat().st_mode & 0o777
    fd, tmp_name = tempfile.mkstemp(prefix=f".{path.name}.", suffix=".tmp", dir=str(path.parent))
    os.close(fd)
    tmp = Path(tmp_name)
    try:
        with zipfile.ZipFile(path, "r") as source:
            infos = source.infolist()
            names = [info.filename for info in infos]
            if len(names) != len(set(names)):
                raise SystemExit("JAR contains duplicate entry names")
            by_name = {info.filename: info for info in infos}
            with zipfile.ZipFile(tmp, "w", allowZip64=True) as target:
                for name in sorted(names):
                    old = by_name[name]
                    info = zipfile.ZipInfo(name, date_time=FIXED_DATE)
                    for attr in (
                        "compress_type", "comment", "extra", "create_system",
                        "create_version", "extract_version", "flag_bits",
                        "internal_attr", "external_attr",
                    ):
                        setattr(info, attr, getattr(old, attr))
                    target.writestr(info, source.read(name), compress_type=old.compress_type)
        os.chmod(tmp, mode)
        os.replace(tmp, path)
    finally:
        try:
            tmp.unlink()
        except FileNotFoundError:
            pass


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit(f"usage: {sys.argv[0]} <jar>")
    normalize(Path(sys.argv[1]))
