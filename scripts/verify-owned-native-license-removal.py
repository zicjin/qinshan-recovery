#!/usr/bin/env python3
"""Verify that the owned source and distribution contain no supplier loader seam.

This is a negative source/distribution gate.  It deliberately does not scan the
external supplier JAR or the native dossier: those are read-only provenance
inputs.  Business fields named ``license`` and third-party license notices are
allowed; executable Sunway native/license inputs are not.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys
import zipfile


FORBIDDEN_FILE_NAMES = {
    "sunway-app.so",
    "sunway-app.dll",
    "sunway-app.o",
    "license.lic",
    "vendor.lic",
    "license-machine.env",
}
FORBIDDEN_TEXT = (
    re.compile(rb"SunwayLibraryExtractor"),
    re.compile(rb"com\.sunwayworld\.boot\.thirdparty\.sunwaylibrary"),
    re.compile(rb"(?:^|[/\\])sunway-app\.(?:so|dll|o)(?:$|[^A-Za-z0-9])"),
    re.compile(rb"SYSTEM_MAC_ADDRESS_LIST"),
    re.compile(rb"license-machine"),
)
JAVA_LOADER_TEXT = (
    re.compile(rb"System\.(?:load|loadLibrary)\s*\("),
    re.compile(rb"\bnative\s+[A-Za-z_$][A-Za-z0-9_$]*\s*\("),
)
METADATA_NAMES = {
    "resource-provenance.json",
    "forbidden-content-report.json",
    "module-provenance.json",
    "framework-module-registry.json",
    "source-only-application.json",
    "public-dependency-allowlist.json",
}
ARCHIVE_SUFFIXES = {".jar", ".war", ".zip", ".ear"}


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def inspect_bytes(label: str, data: bytes, findings: list[dict[str, str]], *, java_source: bool = False) -> None:
    patterns = (*FORBIDDEN_TEXT, *JAVA_LOADER_TEXT) if java_source else FORBIDDEN_TEXT
    for pattern in patterns:
        if pattern.search(data):
            findings.append({"path": label, "reason": pattern.pattern.decode("ascii")})


def inspect_archive(label: str, data: bytes, findings: list[dict[str, str]]) -> None:
    try:
        archive = zipfile.ZipFile(__import__("io").BytesIO(data))
    except zipfile.BadZipFile:
        findings.append({"path": label, "reason": "invalid archive"})
        return
    for entry in archive.infolist():
        if entry.is_dir():
            continue
        name = entry.filename
        if Path(name).name.lower() in FORBIDDEN_FILE_NAMES:
            findings.append({"path": f"{label}:{name}", "reason": "forbidden file name"})
            continue
        member = archive.read(name)
        inspect_bytes(f"{label}:{name}", member, findings, java_source=name.endswith(".java"))
        if Path(name).suffix.lower() in ARCHIVE_SUFFIXES:
            inspect_archive(f"{label}:{name}", member, findings)


def inspect_path(path: Path, findings: list[dict[str, str]]) -> None:
    if path.is_dir():
        for child in sorted(path.rglob("*")):
            if not child.is_file() or child.name in METADATA_NAMES:
                continue
            if child.name.lower() in FORBIDDEN_FILE_NAMES:
                findings.append({"path": str(child), "reason": "forbidden file name"})
                continue
            data = child.read_bytes()
            inspect_bytes(str(child), data, findings, java_source=child.suffix.lower() == ".java")
            if child.suffix.lower() in ARCHIVE_SUFFIXES:
                inspect_archive(str(child), data, findings)
        return
    if path.name.lower() in FORBIDDEN_FILE_NAMES:
        findings.append({"path": str(path), "reason": "forbidden file name"})
        return
    data = path.read_bytes()
    inspect_bytes(str(path), data, findings, java_source=path.suffix.lower() == ".java")
    if path.suffix.lower() in ARCHIVE_SUFFIXES:
        inspect_archive(str(path), data, findings)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-root", action="append", type=Path, required=True)
    parser.add_argument("--distribution", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    findings: list[dict[str, str]] = []
    for root in [*args.source_root, args.distribution]:
        if not root.exists():
            findings.append({"path": str(root), "reason": "missing scan root"})
            continue
        inspect_path(root, findings)

    binding = "unknown"
    binding_script = Path(__file__).with_name("source_binding.py")
    try:
        result = subprocess.run(
            [sys.executable, str(binding_script)],
            text=True, capture_output=True, check=False,
        )
        if result.returncode == 0:
            binding = json.loads(result.stdout).get("binding_id", "unknown")
    except (OSError, json.JSONDecodeError):
        pass

    report = {
        "schema": "owned-native-license-removal-v1",
        "status": "pass" if not findings else "fail",
        "strict_evidence": False,
        "implementation_binding": binding,
        "source_roots": [str(path) for path in args.source_root],
        "distribution": str(args.distribution),
        "findings": findings,
        "policy": {
            "business_license_fields_allowed": True,
            "third_party_license_notices_allowed": True,
            "supplier_native_loader_forbidden": True,
            "supplier_license_input_forbidden": True,
            "jni_replacement_process_forbidden": True,
        },
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"status": report["status"], "finding_count": len(findings), "output": str(args.output)}))
    return 0 if not findings else 1


if __name__ == "__main__":
    raise SystemExit(main())
