#!/usr/bin/env python3
"""Stage one report webroot from authorized offline inputs; never start it."""

import argparse
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
MATERIALS = ROOT / "deployment/report-engine"
SCHEMA_PATTERN = re.compile(r"[A-Z][A-Z0-9_]{0,127}\Z")
WEB_XML = b'''<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="4.0">
  <display-name>Owned commission summary report</display-name>
</web-app>
'''


class StageError(ValueError):
    pass


def sha256(path):
    result = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            result.update(block)
    return result.hexdigest()


def checked_file(path, expected, label):
    if not path.is_file():
        raise StageError("missing " + label)
    if path.stat().st_size != expected["bytes"] or sha256(path) != expected["sha256"]:
        raise StageError("input hash mismatch: " + label)
    return path


def unescape_property(value):
    result = []
    offset = 0
    escapes = {"t": "\t", "n": "\n", "r": "\r", "f": "\f"}
    while offset < len(value):
        char = value[offset]
        offset += 1
        if char != "\\":
            result.append(char)
            continue
        if offset == len(value):
            raise StageError("invalid application properties escape")
        char = value[offset]
        offset += 1
        if char == "u":
            digits = value[offset:offset + 4]
            if len(digits) != 4 or re.fullmatch(r"[0-9a-fA-F]{4}", digits) is None:
                raise StageError("invalid application properties unicode escape")
            result.append(chr(int(digits, 16)))
            offset += 4
        else:
            result.append(escapes.get(char, char))
    return "".join(result)


def application_properties(path):
    # Match java.util.Properties.load(InputStream), including continuations.
    try:
        physical = path.read_bytes().decode("iso-8859-1").splitlines()
    except OSError:
        raise StageError("application properties cannot be read") from None
    logical = []
    pending = ""
    continuing = False
    for line in physical:
        if not continuing and line.lstrip(" \t\f").startswith(("#", "!")):
            continue
        pending += line.lstrip(" \t\f") if continuing else line
        slash_count = len(pending) - len(pending.rstrip("\\"))
        if slash_count % 2:
            pending = pending[:-1]
            continuing = True
        else:
            logical.append(pending)
            pending = ""
            continuing = False
    if continuing:
        logical.append(pending)
    properties = {}
    for line in logical:
        line = line.lstrip(" \t\f")
        if not line:
            continue
        offset = 0
        while offset < len(line):
            if line[offset] == "\\":
                offset += 2
                continue
            if line[offset] in "=: \t\f":
                break
            offset += 1
        key = line[:offset]
        remainder = line[offset:].lstrip(" \t\f")
        if remainder.startswith(("=", ":")):
            remainder = remainder[1:].lstrip(" \t\f")
        properties[unescape_property(key)] = unescape_property(remainder)
    return properties


def local_schema(config):
    properties = application_properties(config)
    username = properties.get("spring.datasource.username", "")
    if not username:
        raise StageError("spring.datasource.username is required")
    schema = username.upper()
    if SCHEMA_PATTERN.fullmatch(schema) is None:
        raise StageError("datasource schema must match [A-Z][A-Z0-9_]{0,127}")
    override = properties.get("owned.report.engine.schema", "")
    if override and override != schema:
        raise StageError("schema override must match the application datasource schema")
    return schema


def validate_output(path):
    if not path.is_absolute() or path.is_symlink() or not path.is_dir():
        raise StageError("--output must be an absolute existing empty directory")
    if any(path.iterdir()):
        raise StageError("--output is not empty; existing files are never overwritten")


def public_inputs(directories, expected):
    found = []
    for entry in expected:
        candidates = [directory / entry["name"] for directory in directories
                      if (directory / entry["name"]).exists()]
        if not candidates:
            raise StageError("missing public library: " + entry["name"])
        for candidate in candidates:
            checked_file(candidate, entry, "public library " + entry["name"])
        found.append(candidates[0])
    return found


def compile_listener(javac, libraries, classes):
    sources = sorted((MATERIALS / "src").rglob("*.java"))
    if not sources:
        raise StageError("owned report session listener source is missing")
    try:
        version = subprocess.run([str(javac), "-version"], stdout=subprocess.PIPE,
                                 stderr=subprocess.PIPE, universal_newlines=True,
                                 check=True, timeout=30)
        match = re.search(r"javac\s+(?:1\.)?(\d+)", version.stdout + version.stderr)
        if match is None or int(match.group(1)) < 8:
            raise StageError("Java compiler version 8 or newer is required")
        flags = ["-source", "8", "-target", "8"] if int(match.group(1)) == 8 else ["--release", "8"]
        command = [str(javac), *flags, "-proc:none", "-encoding", "UTF-8",
                   "-classpath", os.pathsep.join(str(p) for p in libraries),
                   "-d", str(classes), *map(str, sources)]
        compiled = subprocess.run(command, stdout=subprocess.PIPE,
                                  stderr=subprocess.PIPE, timeout=120)
    except (OSError, subprocess.SubprocessError):
        raise StageError("owned report listener compilation could not run") from None
    if compiled.returncode:
        # Compiler diagnostics are deliberately not echoed: operator paths may be private.
        raise StageError("owned report listener compilation failed")
    if not list(classes.rglob("PortalReportSessionListener.class")):
        raise StageError("owned report listener compilation produced no listener")


def verify_configuration(manifest):
    """Verify the finite report bootstrap, without opening private captures or a DB."""
    configuration = manifest.get("configuration")
    expected_counts = {"T_CORE_REPORT_CONFIG": 1, "T_CORE_REPORT_CONFIG_LINE": 27}
    if (not isinstance(configuration, dict)
            or configuration.get("schema") != "owned-home-report-configuration/v2"
            or configuration.get("row_counts") != expected_counts):
        raise StageError("report configuration bootstrap is missing or differs")
    entries = configuration.get("files")
    expected = {"install-config.sql": "configuration", "config-provenance.json": "provenance"}
    if (not isinstance(entries, list) or len(entries) != 2
            or {entry.get("path"): entry.get("role") for entry in entries} != expected):
        raise StageError("report configuration file scope differs")
    files = [(entry, checked_file(MATERIALS / entry["path"], entry,
                                 "report configuration " + entry["path"])) for entry in entries]
    provenance = json.loads((MATERIALS / "config-provenance.json").read_text(encoding="utf-8"))
    if (provenance.get("schema") != configuration["schema"]
            or provenance.get("source_class") != "current-production-at-capture"
            or provenance.get("business_rows") is not False
            or provenance.get("identities_or_grants") is not False
            or provenance.get("row_counts") != expected_counts):
        raise StageError("report configuration provenance scope differs")
    sql = MATERIALS / "install-config.sql"
    if provenance.get("installation_sql") != {"path": sql.name,
            "sha256": sha256(sql), "bytes": sql.stat().st_size}:
        raise StageError("report configuration SQL differs from provenance")
    rows = provenance.get("rows")
    if not isinstance(rows, list):
        raise StageError("report configuration rows are absent")
    expected_ids = {("T_CORE_REPORT_CONFIG", "homePageReport")} | {
        ("T_CORE_REPORT_CONFIG_LINE", line_id) for line_id in ('1389452644773344', '1389464558218720', '1389464703528416', '1389464916356579', '1389624399859168', '1389665817128416', '1391751419556224', '1469555387403840', '1511240290732896', '1516502645514560', '1517068322189920', '1518396586296736', '1533345555174176', '1546806278272480', '1558284399977536', '1579155852848576', '1595473245765344', '1609691922740064', '1682633607010880', '1683439152962112', '1692400913685120', '1694089578136256', '1699853647404768', '1706506964086784', '1706557505163264', '1735579720293280', '1865261756650944')}
    if (not isinstance(rows, list) or len(rows) != 28
            or {(row.get("table"), row.get("id")) for row in rows} != expected_ids):
        raise StageError("report configuration row scope differs")
    declared_lines = {asset.get("report_line_id") for asset in manifest.get("assets", [])}
    configuration_lines = {row["id"] for row in rows if row["table"] == "T_CORE_REPORT_CONFIG_LINE"}
    if not declared_lines or not declared_lines.issubset(configuration_lines):
        raise StageError("report asset configuration binding differs")
    for row in rows:
        values = row.get("values")
        if not isinstance(values, dict) or values.get("ID") != row["id"]:
            raise StageError("report configuration row identity differs")
        digest = hashlib.sha256(json.dumps(values, sort_keys=True, ensure_ascii=True,
                                           separators=(",", ":")).encode("ascii")).hexdigest()
        if digest != row.get("source_row_sha256"):
            raise StageError("report configuration source row hash differs")
    return files


def attachment_path(asset):
    # CoreFileUtils uses EXT/NAME for finereport; FilePathManager omits its date.
    install = PurePosixPath(asset["install_path"])
    if (len(install.parts) != 4 or install.parts[:2] != ("WEB-INF", "reportlets")
            or install.parts[2] != asset["report_code"]
            or re.fullmatch(r"[a-z0-9_]+", install.parts[2]) is None
            or not install.name.endswith(".cpt")
            or any(c in install.name for c in "\\:\x00")
            or any(ord(c) < 32 for c in install.name)):
        raise StageError("invalid report attachment path")
    return PurePosixPath("finereport", *install.parts[2:])


def inspect_local_path(path):
    if not path.is_absolute() or ".." in path.parts:
        raise StageError("attachment paths must be absolute without parent components")
    current = Path(path.anchor)
    for part in path.parts[1:]:
        current = current / part
        if current.is_symlink():
            raise StageError("symbolic link in attachment path")
        if current != path and current.exists() and not current.is_dir():
            raise StageError("invalid attachment directory")


def install_attachments(staged, files_root, application_config):
    """Install only this release's finite attachment set; never replace a conflict."""
    inspect_local_path(staged)
    inspect_local_path(files_root)
    if not staged.is_dir() or not files_root.is_dir():
        raise StageError("staged report and application files directories must exist")
    manifest_path = MATERIALS / "manifest.json"
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    receipt = json.loads((staged / "staging-manifest.json").read_text(encoding="utf-8"))
    if receipt.get("input_manifest_sha256") != sha256(manifest_path):
        raise StageError("report staging input manifest differs")
    expected = manifest["assets"]
    attachments, assets = receipt.get("attachment_files"), receipt.get("assets")
    if (not isinstance(attachments, list) or not isinstance(assets, list)
            or len(attachments) != len(expected) or len(assets) != len(expected)):
        raise StageError("report attachment manifest scope differs")
    schema = local_schema(application_config)
    prepared = []
    already_present = 0
    for asset, record, engine in zip(expected, attachments, assets):
        original = checked_file(MATERIALS / asset["path"], asset, "owned report asset").read_bytes()
        projected = original.replace(asset["datasource_placeholder"].encode(), b"OWNED_LOCAL_REPORT")
        projected = projected.replace(asset["schema_placeholder"].encode(), schema.encode("ascii"))
        digest = hashlib.sha256(projected).hexdigest()
        relative = attachment_path(asset)
        source_relative = PurePosixPath("attachment-files", *relative.parts)
        if (record.get("relative_path") != str(relative) or record.get("path") != str(source_relative)
                or engine.get("path") != asset["install_path"]
                or record.get("sha256") != engine.get("sha256") or record.get("sha256") != digest
                or record.get("bytes") != engine.get("bytes") or record.get("bytes") != len(projected)):
            raise StageError("report attachment manifest path or asset binding differs")
        source = staged.joinpath(*source_relative.parts)
        engine_source = staged.joinpath(*PurePosixPath(engine["path"]).parts)
        target = files_root.joinpath(*relative.parts)
        for path in (source, engine_source, target):
            inspect_local_path(path)
        checked_file(source, record, "staged report attachment")
        checked_file(engine_source, record, "staged report template")
        if target.exists():
            if not target.is_file() or target.stat().st_size != record["bytes"] or sha256(target) != record["sha256"]:
                raise StageError("existing attachment differs; restore or migrate it explicitly")
            already_present += 1
        else:
            prepared.append((source, target))
    # Preflight the whole set before publishing anything; an existing conflict
    # must not leave earlier templates installed. Exclusive links also prevent
    # overwriting a file created after preflight.
    created = []
    try:
        for source, target in prepared:
            target.parent.mkdir(parents=True, exist_ok=True)
            inspect_local_path(target)
            with tempfile.NamedTemporaryFile(prefix=".report-attachment-", dir=target.parent) as temporary:
                with source.open("rb") as stream:
                    shutil.copyfileobj(stream, temporary)
                temporary.flush()
                os.fsync(temporary.fileno())
                os.chmod(temporary.name, 0o644)
                os.link(temporary.name, target)
            created.append(target)
    except (OSError, StageError):
        for path in created:
            path.unlink()
        raise StageError("report attachment installation failed; existing files were not replaced") from None
    return {"status": "attachments-installed", "installed": len(created),
            "already_present": already_present, "database_connected": False}


def stage(args):
    validate_output(args.output)
    manifest = json.loads((MATERIALS / "manifest.json").read_text(encoding="utf-8"))
    configuration_files = verify_configuration(manifest)
    expected = manifest["third_party_engine"]["libraries"]
    if len(expected) != 13 or len({entry["name"] for entry in expected}) != 13:
        raise StageError("manifest must declare thirteen distinct engine libraries")
    expected_names = {entry["name"] for entry in expected}
    supplied_names = {path.name for path in args.engine_lib.glob("fine-*.jar")}
    if supplied_names != expected_names:
        raise StageError("engine library inventory differs from the thirteen authorized inputs")
    engine = [checked_file(args.engine_lib / entry["name"], entry,
                           "engine library " + entry["name"]) for entry in expected]
    public = public_inputs(args.public_lib, manifest["public_libraries"])
    schema = local_schema(args.application_config)
    asset_entries = manifest.get("assets", [manifest["asset"]])
    if not isinstance(asset_entries, list) or not 1 <= len(asset_entries) <= 27:
        raise StageError("manifest must declare one to 27 reviewed report templates")
    if any(asset_entries[0].get(key) != value for key, value in manifest["asset"].items()):
        raise StageError("primary report asset differs from compatibility entry")
    staged_assets = []
    seen_install = set()
    for asset_entry in asset_entries:
        asset = checked_file(MATERIALS / asset_entry["path"], asset_entry, "owned report asset").read_bytes()
        replacements = [(asset_entry["datasource_placeholder"].encode(), b"OWNED_LOCAL_REPORT",
                         asset_entry["datasource_occurrences"]),
                        (asset_entry["schema_placeholder"].encode(), schema.encode("ascii"),
                         asset_entry["schema_occurrences"])]
        for token, value, count in replacements:
            if asset.count(token) != count:
                raise StageError("owned report asset placeholder count differs from manifest")
            asset = asset.replace(token, value)
        parsed = ET.fromstring(asset)
        if any((parameter.find("O").text or "").strip()
               for parameter in parsed.findall("./TableDataMap/TableData/Parameters/Parameter")):
            raise StageError("owned report asset contains a nonblank parameter default")
        if any(list(parsed.iter(tag)) for tag in
               ("TemplateIdAttMark", "ForkIdAttrMark", "TemplateCloudInfoAttrMark")):
            raise StageError("owned report asset contains origin metadata")
        install = PurePosixPath(asset_entry["install_path"])
        if (install.is_absolute() or ".." in install.parts or not install.parts
                or str(install) in seen_install):
            raise StageError("invalid or duplicate asset installation path")
        seen_install.add(str(install))
        attachment_path(asset_entry)
        metadata_path = asset_entry.get("metadata_source", "install-metadata.sql")
        metadata_relative = PurePosixPath(metadata_path)
        if metadata_path not in ("install-metadata.sql", "install-contract-metadata.sql") and not (
                len(metadata_relative.parts) == 2 and metadata_relative.parts[0] == "metadata"
                and re.fullmatch(r"[a-z0-9_]+\.sql", metadata_relative.name)) :
            raise StageError("unreviewed metadata source")
        metadata_source = checked_file(MATERIALS / metadata_path,
            {"bytes": asset_entry["metadata_bytes"], "sha256": asset_entry["metadata_sha256"]},
            "report attachment metadata").read_bytes()
        metadata_token = asset_entry.get("metadata_size_token", "${OWNED_REPORT_ASSET_BYTES}").encode("ascii")
        if metadata_source.count(metadata_token) != (4 if metadata_path.startswith("metadata/") else 3):
            raise StageError("metadata SQL must contain the declared asset-size placeholders")
        metadata = metadata_source.replace(metadata_token, str(len(asset)).encode("ascii"))
        if b"${" in metadata:
            raise StageError("metadata SQL contains an unresolved placeholder")
        staged_assets.append((install, asset, metadata_path, metadata_source, metadata))
    with tempfile.TemporaryDirectory(prefix=".report-engine-stage-", dir=args.output.parent) as temporary:
        webroot = Path(temporary)
        webinf = webroot / "WEB-INF"
        for name in ("classes", "lib", "assist", "config", "embed", "local",
                     "plugins", "reportlets", "resources"):
            (webinf / name).mkdir(parents=True)
        (webroot / "logs").mkdir()
        for source in engine + public:
            destination = webinf / "lib" / source.name
            shutil.copyfile(source, destination)
            if sha256(source) != sha256(destination):
                raise StageError("copied library hash mismatch")
        compile_listener(args.javac, sorted((webinf / "lib").glob("*.jar")), webinf / "classes")
        asset_records = []
        attachment_records = []
        metadata_records = []
        for entry, (install, asset, metadata_path, metadata_source, metadata) in zip(asset_entries, staged_assets):
            destination = webroot.joinpath(*install.parts)
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_bytes(asset)
            relative = attachment_path(entry)
            attachment = webroot.joinpath("attachment-files", *relative.parts)
            attachment.parent.mkdir(parents=True, exist_ok=True)
            attachment.write_bytes(asset)
            attachment_records.append({"path": "attachment-files/" + str(relative),
                                       "relative_path": str(relative), "bytes": len(asset),
                                       "sha256": hashlib.sha256(asset).hexdigest()})
            (webroot / metadata_path).parent.mkdir(parents=True, exist_ok=True)
            (webroot / metadata_path).write_bytes(metadata)
            asset_records.append({"path": str(install), "bytes": len(asset),
                                  "sha256": hashlib.sha256(asset).hexdigest()})
            metadata_records.append({"path": metadata_path,
                                     "source_sha256": hashlib.sha256(metadata_source).hexdigest(),
                                     "bytes": len(metadata), "sha256": hashlib.sha256(metadata).hexdigest(),
                                     "asset_size_replacements": metadata_source.count(metadata_token)})
        configuration_records = []
        for entry, source in configuration_files:
            shutil.copyfile(source, webroot / entry["path"])
            configuration_records.append(dict(entry))
        dependency_records = []
        for dependency in manifest.get("dependencies", []):
            relative = PurePosixPath(dependency["path"])
            if (relative.is_absolute() or ".." in relative.parts
                    or len(relative.parts) != 2 or relative.parts[0] != "dependencies"):
                raise StageError("invalid report dependency path")
            source = checked_file(MATERIALS.joinpath(*relative.parts), dependency,
                                  "report dependency")
            destination = webroot.joinpath(*relative.parts)
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, destination)
            dependency_records.append({"path": str(relative), "bytes": source.stat().st_size,
                                       "sha256": sha256(source), "role": dependency["role"]})
        (webinf / "web.xml").write_bytes(WEB_XML)
        staged_manifest = {
            "schema": "owned-report-engine-stage/v1",
            "input_manifest_sha256": sha256(MATERIALS / "manifest.json"),
            "asset": asset_records[0],
            "assets": asset_records,
            "attachment_files": attachment_records,
            "metadata_sql": metadata_records[0],
            "metadata_sql_files": metadata_records,
            "dependencies": dependency_records,
            "configuration_files": configuration_records,
            "libraries": manifest["third_party_engine"]["libraries"] + manifest["public_libraries"],
            "listener_sources": [
                {"path": str(source.relative_to(MATERIALS)), "sha256": sha256(source)}
                for source in sorted((MATERIALS / "src").rglob("*.java"))],
            "listener_classes": [
                {"path": str(source.relative_to(webroot)), "sha256": sha256(source)}
                for source in sorted((webinf / "classes").rglob("*.class"))],
            "configuration_values_recorded": False,
            "license_or_private_config_copied": False,
            "database_connected": False,
        }
        (webroot / "staging-manifest.json").write_text(
            json.dumps(staged_manifest, indent=2, ensure_ascii=True) + "\n", encoding="utf-8")
        validate_output(args.output)
        for source in sorted(webroot.iterdir()):
            source.rename(args.output / source.name)
    return {"status": "staged", "engine_libraries": len(engine),
            "public_libraries": len(public), "templates": len(staged_assets),
            "listener_compiled": True, "database_connected": False,
            "license_or_private_config_copied": False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--install-attachments-from", type=Path,
                        help="install the staged attachment set instead of staging an engine")
    parser.add_argument("--files-root", type=Path,
                        help="existing absolute application work files directory; conflicts are rejected")
    parser.add_argument("--engine-lib", type=Path,
                        help="operator-authorized FineReport 11 JAR directory (offline)")
    parser.add_argument("--public-lib", type=Path, action="append",
                        help="offline public JAR directory; repeat to combine directories")
    parser.add_argument("--output", type=Path,
                        help="absolute existing empty destination directory; never overwritten")
    parser.add_argument("--application-config", type=Path,
                        help="private Java application properties; schema is read, never copied")
    parser.add_argument("--javac", type=Path,
                        default=Path(shutil.which("javac") or "javac"),
                        help="Java compiler 8+; generated listener targets Java 8")
    args = parser.parse_args()
    if args.install_attachments_from is not None:
        if args.files_root is None or args.application_config is None or any((args.engine_lib, args.public_lib, args.output)):
            parser.error("attachment installation requires --files-root, --application-config and no engine/output options")
    elif args.files_root is not None or any(value is None for value in (args.engine_lib, args.output, args.application_config)):
        parser.error("staging requires --engine-lib, --output and --application-config")
    if args.public_lib is None:
        args.public_lib = [ROOT / "build/reconstituted-public-deps"]
    try:
        result = (install_attachments(args.install_attachments_from, args.files_root, args.application_config)
                  if args.install_attachments_from is not None else stage(args))
        print(json.dumps(result, sort_keys=True))
    except (StageError, OSError, ValueError, KeyError, ET.ParseError) as failure:
        message = str(failure) if isinstance(failure, StageError) else "invalid or unavailable staging input"
        print("report engine staging failed: " + message, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
