#!/usr/bin/env python3
"""Render the two production-backed CRM mappers for a local logical clone.

The original mapper XML remains the source of truth.  This tool only emits an
explicit build-profile overlay for the three known production link domains;
it does not rewrite arbitrary SQL, create a database link, or connect to
Oracle.  The generated files are suitable for an isolated classpath overlay:
use ``direct-schema`` when the clone tables live in named schemas and
``local-view`` when same-name views/synonyms are installed in the application
schema.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import re
from typing import Mapping

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "api-fixtures/schema/external-schema-clone-manifest.json"
PROFILE = ROOT / "api-fixtures/schema/external-schema-runtime-profile.example.json"
TARGETS = (
    Path("project/src/main/java/com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml"),
    Path("project/src/main/java/com/sunwayworld/emc/module/mdm/contract/mapper/conf/emc-contract-oracle-mapper.xml"),
)
# This is deliberately narrower than a generic ``@identifier`` substitution.
LINK_RE = re.compile(r"@([A-Za-z][A-Za-z0-9_$#]*)")
TABLE_LINK_RE = re.compile(
    r"(?P<table>\bT_[A-Za-z0-9_$#]+)\s*@(?P<link>[A-Za-z][A-Za-z0-9_$#]*)",
    re.IGNORECASE,
)
IDENT_RE = re.compile(r"^[A-Z][A-Z0-9_$#]{0,29}$")
MODES = frozenset(("direct-schema", "local-view"))


class MapperProfileError(ValueError):
    pass


def _manifest_links(manifest: Mapping) -> set[str]:
    domains = manifest.get("domains")
    if not isinstance(domains, list):
        raise MapperProfileError("manifest domains must be a list")
    result: set[str] = set()
    for domain in domains:
        access = domain.get("clone_access", {})
        links = access.get("production_qualifiers", [])
        if not isinstance(links, list):
            raise MapperProfileError("production qualifier allowlist must be a list")
        result.update(str(x) for x in links)
    if result != {"LIMS_CZJKY_HT", "LIMS_CZJKY_HT2", "LIMS_CZJKY_SP", "LIMS_CZJKY_XFP"}:
        raise MapperProfileError("manifest qualifier allowlist is not the approved HT/SP/XFP set")
    return result


def _load_profile(path: Path, manifest: Mapping) -> tuple[str, dict[str, str]]:
    profile = json.loads(path.read_text(encoding="utf-8"))
    mode = profile.get("mode")
    if mode not in MODES:
        raise MapperProfileError("profile mode must be direct-schema or local-view")
    links = _manifest_links(manifest)
    mapping = profile.get("qualifier_schemas")
    if not isinstance(mapping, dict) or set(mapping) != links:
        raise MapperProfileError("profile must map every approved production qualifier exactly once")
    normalized = {}
    for link, schema in mapping.items():
        if not isinstance(link, str) or not isinstance(schema, str) or not IDENT_RE.fullmatch(schema):
            raise MapperProfileError("profile schema names must be Oracle identifiers")
        normalized[link] = schema
    return mode, normalized


def _assert_known_qualifiers(text: str, links: set[str]) -> None:
    found = {match.group(1).upper() for match in LINK_RE.finditer(text)}
    unknown = found - {link.upper() for link in links}
    if unknown:
        raise MapperProfileError("unapproved SQL qualifier(s): " + ", ".join(sorted(unknown)))


def render_mapper(text: str, mode: str, mapping: Mapping[str, str], links: set[str] | None = None) -> str:
    """Render only table@approved-link references, preserving all SQL semantics."""
    if mode not in MODES:
        raise MapperProfileError("unsupported mode")
    allowed = links or set(mapping)
    _assert_known_qualifiers(text, allowed)
    normalized = {key.upper(): value for key, value in mapping.items()}

    def replace(match: re.Match[str]) -> str:
        table, link = match.group("table"), match.group("link").upper()
        if link not in normalized:
            raise MapperProfileError("unapproved SQL qualifier: " + link)
        if mode == "local-view":
            return table
        return normalized[link] + "." + table

    # A qualifier must be attached to a table identifier.  Any remaining
    # approved @token is rejected instead of silently changing SQL semantics.
    rendered = TABLE_LINK_RE.sub(replace, text)
    remaining = {match.group(1).upper() for match in LINK_RE.finditer(rendered)}
    if remaining:
        raise MapperProfileError("qualifier is not attached to an approved table: " + ", ".join(sorted(remaining)))
    return rendered


def classpath_path(source: Path) -> Path:
    parts = source.parts
    for index in range(len(parts) - 1):
        if parts[index] == "com" and parts[index + 1] == "sunwayworld":
            return Path(*parts[index:])
    raise MapperProfileError("mapper source is outside com/sunwayworld classpath")


def render(root: Path, output: Path, profile_path: Path = PROFILE, manifest_path: Path = MANIFEST) -> list[str]:
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    mode, mapping = _load_profile(profile_path, manifest)
    links = _manifest_links(manifest)
    written: list[str] = []
    for relative in TARGETS:
        source = root / relative
        if not source.is_file():
            raise MapperProfileError("mapper source missing: " + relative.as_posix())
        text = source.read_text(encoding="utf-8")
        rendered = render_mapper(text, mode, mapping, links)
        target = output / classpath_path(relative)
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(rendered, encoding="utf-8")
        written.append(target.relative_to(output).as_posix())
    return written


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output-root", type=Path, required=True)
    parser.add_argument("--profile", type=Path, default=PROFILE)
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    args = parser.parse_args()
    try:
        written = render(ROOT, args.output_root, args.profile, args.manifest)
    except (OSError, json.JSONDecodeError, MapperProfileError) as exc:
        parser.error(str(exc))
    print(json.dumps({"status": "pass", "mode": json.loads(args.profile.read_text(encoding="utf-8"))["mode"], "written": written}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
