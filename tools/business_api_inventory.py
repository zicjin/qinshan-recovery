#!/usr/bin/env python3
"""Inventory project Java HTTP routes against an owned distribution.

This is a source-to-runtime index, not behavior evidence.  Every route keeps
an explicit ``behavior_status=unknown`` until a real HTTP/browser contract
promotes it.  That distinction prevents a packaged class from masquerading as
an implemented business API. Owned source-only rows are served by the same
Spring application as project routes; they are not separate processes.
"""
from __future__ import annotations

import argparse
import ast
import hashlib
import json
import re
import subprocess
import zipfile
import sys
from pathlib import Path
from typing import Dict, Iterable, List, Mapping, Optional, Sequence, Set, Tuple

SCRIPT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPT_ROOT / "scripts"))
from evidence_output import guard_output_path  # noqa: E402
from source_binding import REPO_ROOT, binding_metadata, capture_binding, implementation_dirty  # noqa: E402


HTTP_ANNOTATIONS = {
    "GetMapping": "GET",
    "PostMapping": "POST",
    "PutMapping": "PUT",
    "DeleteMapping": "DELETE",
    "PatchMapping": "PATCH",
}
REQUEST_METHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"}
OWNED_ROUTE_ALIAS_EXPANSIONS = {
    # The controller intentionally uses one bounded {resource} mapping for
    # these concrete GenericResource status aliases. Keep the inventory
    # concrete so owned_sources can be associated with project routes.
    "OwnedEquipmentResourceStatusController": (
        "equipt-crves-detail", "equipt-crves", "equipt-dioxin-sets",
        "equipt-flow-sets", "equipt-sets", "equipt-standard-sets",
        "equipt-sw-sets", "equipt-temp-sets",
    ),
    # EmcEmpResourceImpl is registered by OwnedPersonnelConfiguration and
    # delegates the inherited main resource to this owned DAO. The DAO has no
    # Spring mapping of its own, so keep this wiring explicit in provenance.
    "OwnedPersonnelReadDao": (
        "__personnel_main_queries__",
    ),
}
GENERIC_INHERITED_PATHS = {"/", "/queries", "/queries/raw", "/{id}", "/instant"}
GENERIC_RESOURCE_INHERITED_ROUTES = (
    ("POST", "/queries"),
    ("POST", "/queries/raw"),
    ("GET", "/{id}"),
    ("GET", "/{id}/columns/{column}"),
    ("GET", "/{id}/info/status"),
    ("GET", "/info/status"),
    ("DELETE", ""),
    ("POST", ""),
    ("PUT", "/instant"),
    ("PUT", "/{id}"),
    ("POST", "/queries/searchable"),
    ("POST", "/queries/roles/{roleId}"),
    ("POST", "/{id}/action/validate"),
    ("POST", "/{id}/action/validate-unique"),
    ("POST", "/{id}/action/validate-unique/{columnName}/{columnValue}"),
    ("POST", "/action/validate-unique/{columnName}/{columnValue}"),
    ("POST", "/action/validate-unique"),
    ("PUT", "/action/swap/{column}/{firstId}/{secondId}"),
    ("PUT", "/action/swap/{firstId}/{secondId}"),
    ("GET", "/gikam-attrs/mybatis-mapper"),
    ("POST", "/gikam-attrs/reqentries/queries"),
    ("GET", "/{id}/action/print"),
)
GENERIC_AUDITABLE_INHERITED_ROUTES = (
    ("POST", "/queries/tasks"),
    ("POST", "/queries/withdrawable"),
    ("POST", "/queries/undoable"),
    ("PUT", "/action/start-process"),
    ("PUT", "/action/start-process-with-progress"),
    ("PUT", "/action/withdraw-process"),
    ("PUT", "/action/withdraw-process-with-progress"),
    ("PUT", "/action/complete-task"),
    ("PUT", "/action/complete-task-with-progress"),
    ("PUT", "/action/transfer-task"),
    ("PUT", "/action/transfer-task-with-progress"),
    ("PUT", "/action/end-task"),
    ("PUT", "/action/end-task-with-progress"),
    ("POST", "/bpmn-task-status"),
    ("PUT", "/action/reject-task"),
    ("PUT", "/action/reject-task-with-progress"),
    ("PUT", "/action/odd-reject-task"),
    ("PUT", "/action/odd-reject-task-with-progress"),
    ("PUT", "/action/undo"),
    ("PUT", "/action/undo-with-progress"),
    ("GET", "/{id}/previous-tasks"),
)
# Exact mappings from reconstructed framework/controller/Generic*PageController.
# Their @RequestMapping declarations do not restrict the HTTP method. Keep ANY
# rather than inferring GET from the page requests observed during screening.
GENERIC_PAGE_INHERITED_ROUTES = {
    "GenericAuditListPageController": (("ANY", "/audit-list"),),
    "GenericChooseMultPageController": (("ANY", "/choose-mult-list"),),
    "GenericChoosePageController": (("ANY", "/choose-list"), ("ANY", "/simple-choose-list")),
    "GenericDeactivatedListPageController": (("ANY", "/deactivated-list"),),
    "GenericDetailPageController": (("ANY", "/detail"),),
    "GenericEditListPageController": (("ANY", "/edit-list"),),
    "GenericIndexPageController": (("ANY", ""),),
    "GenericInnerSelectPageController": (("ANY", "/inner-select"),),
    "GenericLineDetailPageController": (("ANY", "/line-detail"),),
    "GenericLineSearchListPageController": (("ANY", "/line-search-list"),),
    "GenericMatrixPageController": (("ANY", "/matrix"),),
    "GenericPreInsertPageController": (("ANY", "/pre-insert"),),
    "GenericPrintPageController": (("ANY", "/print"),),
    "GenericQueryListPageController": (("ANY", "/query-list"),),
    "GenericRejectListPageController": (("ANY", "/reject-list"),),
    "GenericSearchListPageController": (("ANY", "/search-list"),),
    "GenericSuspendListPageController": (("ANY", "/suspend-list"),),
    "GenericUndoActivitedListPageController": (("ANY", "/undoactivite-list"),),
    "GenericUndoListPageController": (("ANY", "/undo-list"),),
}


def _source_commit() -> str:
    return subprocess.check_output(["git", "-C", str(REPO_ROOT), "rev-parse", "HEAD"], text=True).strip()


def _source_dirty() -> bool:
    return implementation_dirty(REPO_ROOT)


def _source_reference(path: Path) -> str:
    try:
        return str(path.resolve().relative_to(REPO_ROOT))
    except ValueError:
        return str(path)


def _quoted_values(text: str, key: str = "", constants: Optional[Mapping[str, str]] = None) -> List[str]:
    if key:
        # Stop at the array's closing brace, allowing a quoted route itself
        # to contain a path-variable brace such as ``/{id}``.
        match = re.search(r"\b" + re.escape(key) + r"\s*=\s*\{(.*?)\}\s*(?:,|\))", text, flags=re.S)
        if not match:
            match = re.search(r"\b" + re.escape(key) + r"\s*=\s*(\"[^\"]*\")", text)
            if not match:
                return []
            value = match.group(1)
        else:
            value = match.group(1)
    else:
        match = re.search(r"(\{[^}]*\}|\"[^\"]*\")", text)
        if not match:
            return []
        value = match.group(1)
    constants = constants or {}
    for name, replacement in constants.items():
        value = re.sub(r"\b" + re.escape(name) + r"\b", json.dumps(replacement), value)
    # Evaluate the small Java expression form used by recovered mappings:
    # ``PREFIX + "/queries"``.  Splitting on commas at this stage is safe
    # because route literals do not contain nested annotation expressions.
    values: List[str] = []
    for part in value.strip().strip("{}").split(","):
        literals = re.findall(r'"([^"\\]*(?:\\.[^"\\]*)*)"', part)
        if literals:
            values.append("".join(bytes(item, "utf-8").decode("unicode_escape") for item in literals))
    return values


def _annotation(line: str, constants: Optional[Mapping[str, str]] = None) -> Optional[Dict[str, object]]:
    match = re.search(r"@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\s*(?:\((.*)\))?", line)
    if not match:
        return None
    name, args = match.group(1), match.group(2) or ""
    paths = (_quoted_values(args, "path", constants) or _quoted_values(args, "value", constants)
             or _quoted_values(args, constants=constants))
    if not paths:
        paths = [""]
    methods: List[str]
    if name in HTTP_ANNOTATIONS:
        methods = [HTTP_ANNOTATIONS[name]]
    else:
        found = re.findall(r"RequestMethod\.([A-Z]+)", args)
        methods = [item for item in found if item in REQUEST_METHODS] if found else ["ANY"]
    return {"name": name, "paths": paths, "methods": methods, "has_explicit_method": bool(re.search(r"RequestMethod\.", args))}


def _join(base: str, child: str) -> str:
    value = "/" + "/".join(part.strip("/") for part in (base, child) if part and part.strip("/"))
    return value if value != "//" else "/"


def _class_name(source: str, source_path: str) -> str:
    package = re.search(r"\bpackage\s+([\w.]+)\s*;", source)
    stem = Path(source_path).stem
    return (package.group(1) + "." if package else "") + stem


def _class_mapping_paths(source: str) -> List[str]:
    paths: List[str] = []
    for match in re.finditer(r"@RequestMapping\b", source):
        open_paren = source.find("(", match.end())
        if open_paren < 0:
            continue
        depth = 0
        close_paren = -1
        for index in range(open_paren, len(source)):
            if source[index] == "(":
                depth += 1
            elif source[index] == ")":
                depth -= 1
                if depth == 0:
                    close_paren = index
                    break
        if close_paren < 0:
            continue
        arguments = source[open_paren + 1:close_paren]
        if re.search(r"RequestMethod\.", arguments):
            continue
        following = source[close_paren + 1:close_paren + 400]
        if not re.search(r"\b(class|interface|enum)\b", following):
            continue
        values = _quoted_values(arguments, "path") or _quoted_values(arguments)
        paths.extend(str(item) for item in (values or [""]))
    return sorted(set(paths))


def _declared_types(source: str) -> List[str]:
    declaration = re.search(r"\b(?:class|interface|enum)\s+\w+([^\{]*)\{", source)
    if not declaration:
        return []
    # Remove complete nested generic arguments before splitting parents. A
    # comma inside Map<String,List<T>> never separates two Java parent types.
    depth = 0
    plain = []
    for char in declaration.group(1):
        if char == "<":
            depth += 1
        elif char == ">":
            depth -= 1
        elif depth == 0:
            plain.append(char)
    clause = "".join(plain)
    parent_clause = re.search(r"\b(?:extends|implements)\s+(.+)", clause, re.S)
    if not parent_clause:
        return []
    return sorted(set(re.findall(
        r"[A-Za-z_$][\w.$]*", re.sub(r"\bimplements\b", ",", parent_clause.group(1))
    )))


def _test_source_path(path: Path) -> bool:
    parts = path.parts
    return any(parts[index:index + 2] in {
        ("src", "test"), ("src", "testFixtures"), ("src", "integrationTest"),
    } for index in range(len(parts) - 1))


def _source_infos(source_root: Path) -> Dict[str, Dict[str, object]]:
    source_root = source_root.resolve()
    infos: Dict[str, Dict[str, object]] = {}
    for source_path in sorted(source_root.rglob("*.java")):
        if _test_source_path(source_path):
            continue
        relative = str(source_path.relative_to(source_root)).replace("\\", "/")
        source = source_path.read_text(encoding="utf-8", errors="replace")
        constants = {
            name: value
            for name, value in re.findall(r"\b(?:private|public|protected)?\s*static\s+final\s+String\s+(\w+)\s*=\s*\"([^\"]*)\"", source)
        }
        qualified = _class_name(source, relative)
        info = {
            "source": source_path,
            "relative": relative,
            "qualified": qualified,
            "simple": qualified.rsplit(".", 1)[-1],
            "class_bases": _class_mapping_paths(source),
            "parents": _declared_types(source),
            "constants": constants,
            "text": source,
            "source_root": source_root,
            "imports": dict((name.rsplit(".", 1)[-1], name) for name in
                            re.findall(r"^\s*import\s+([\w.]+)\s*;", source, re.M)),
            "is_interface": bool(re.search(r"\binterface\s+" + re.escape(source_path.stem) + r"\b", source)),
            "is_handler": bool(re.search(r"@(RestController|Controller)\b", source))
                          and not bool(re.search(r"\babstract\s+class\b", source)),
        }
        infos[str(info["simple"])] = info
        infos[str(info["qualified"])] = info
    return infos


def _parent_info(info: Dict[str, object], parent: str,
                 infos: Dict[str, Dict[str, object]]) -> Optional[Dict[str, object]]:
    imported = info.get("imports", {}).get(parent)
    package = str(info["qualified"]).rsplit(".", 1)[0]
    for name in (imported, parent if "." in parent else package + "." + parent, parent):
        if name and name in infos:
            return infos[name]
    return None


def _source_index(roots: Sequence[Path]) -> Dict[str, Dict[str, object]]:
    """First FQN wins, matching framework > project > transitional owned."""
    qualified = {}
    for root in roots:
        for name, info in _source_infos(root).items():
            if name != info["qualified"]:
                continue
            if name in qualified:
                if info["source"] != qualified[name]["source"]:
                    qualified[name].setdefault("shadowed_sources", []).append(str(info["source"]))
            else:
                qualified[name] = info
    result = dict(qualified)
    simple = {}
    for info in qualified.values():
        simple.setdefault(str(info["simple"]), []).append(info)
    for name, candidates in simple.items():
        if len(candidates) == 1:
            result[name] = candidates[0]
    # Existing owned mapped classes can be explicitly created by @Bean with
    # no component stereotype. Conditions remain unverified source metadata.
    for config in qualified.values():
        for method in re.finditer(
            r"@(?:[\w.]+\.)?Bean\b(?P<prefix>[\s\S]*?)\bpublic\s+"
            r"(?P<type>[\w.$]+)\s+(?P<method>\w+)\s*\(", str(config["text"])
        ):
            if re.search(r"\breturn\b|;", method.group("prefix")):
                continue
            target = _parent_info(config, method.group("type"), result)
            if not target or not target["class_bases"] or target["is_interface"]:
                continue
            conditions = re.findall(r"@(?:[\w.]+\.)?(GikamBean|Profile|Conditional\w*)\b",
                                    str(config["text"]))
            target["is_handler"] = True
            target.setdefault("bean_factory_sources", []).append({
                "source": _source_reference(config["source"]), "class": config["qualified"],
                "method": method.group("method"), "conditions": sorted(set(conditions)),
            })
    return result


def _resolved_class_bases(info: Dict[str, object], infos: Dict[str, Dict[str, object]], seen: Optional[Set[str]] = None) -> List[str]:
    seen = set() if seen is None else seen
    key = str(info["qualified"])
    if key in seen:
        return []
    seen.add(key)
    bases = list(info["class_bases"])
    for parent in info["parents"]:
        parent_info = _parent_info(info, str(parent), infos)
        if parent_info:
            bases.extend(_resolved_class_bases(parent_info, infos, seen))
    return sorted(set(str(item) for item in bases))


def _inherits(info: Dict[str, object], infos: Dict[str, Dict[str, object]], target: str,
              seen: Optional[Set[str]] = None) -> bool:
    seen = set() if seen is None else seen
    key = str(info["qualified"])
    if key in seen:
        return False
    seen.add(key)
    parents = [str(item) for item in info.get("parents", [])]
    if target in parents:
        return True
    for parent in parents:
        parent_info = _parent_info(info, parent, infos)
        if parent_info and _inherits(parent_info, infos, target, seen):
            return True
    return False


def _routes_for_source(info: Dict[str, object], infos: Dict[str, Dict[str, object]]) -> List[Dict[str, str]]:
    source_path = info["source"]
    source = info.get("text") or source_path.read_text(encoding="utf-8", errors="replace")
    routes = extract_routes(source, str(info["relative"]), info.get("constants", {}))
    bases = _resolved_class_bases(info, infos)
    resolved: List[Dict[str, str]] = []
    for route in routes:
        if route.get("class_base") or not bases:
            resolved.append(route)
            continue
        for base in bases:
            item = dict(route)
            item["class_base"] = base
            item["path"] = _join(base, route["path"])
            resolved.append(item)
    # Generic resource interfaces contribute a large inherited API surface that
    # is invisible to a source-only annotation scan.  Register those routes
    # explicitly so a route such as material-apply/action/complete-task cannot
    # disappear from the exhaustive inventory.  Implementation classes inherit
    # these mappings through the interface and are intentionally not duplicated.
    if re.search(r"\binterface\s+\w+", source):
        inherited_groups = []
        if _inherits(info, infos, "GenericResource"):
            inherited_groups.append(("inherited.GenericResource", GENERIC_RESOURCE_INHERITED_ROUTES))
        if _inherits(info, infos, "GenericAuditableResource"):
            inherited_groups.append(("inherited.GenericAuditableResource", GENERIC_AUDITABLE_INHERITED_ROUTES))
        for parent, page_routes in GENERIC_PAGE_INHERITED_ROUTES.items():
            if _inherits(info, infos, parent):
                inherited_groups.append(("inherited." + parent, page_routes))
        for annotation, inherited_routes in inherited_groups:
            for base in bases:
                for method, path in inherited_routes:
                    if annotation.removeprefix("inherited.") in GENERIC_PAGE_INHERITED_ROUTES:
                        # A rootless shared interface is not an application route;
                        # an explicit override already supplies its own provenance.
                        if base not in info["class_bases"] or not base.strip("/") or any(
                            row["method"] == method and row["path"] == _join(base, path)
                            for row in resolved
                        ):
                            continue
                    resolved.append({
                        "source": str(info["relative"]),
                        "class": str(info["qualified"]),
                        "class_base": base,
                        "method": method,
                        "path": _join(base, path),
                        "annotation": annotation,
                    })
    unique = {}
    for route in resolved:
        unique[(route["method"], route["path"], route["class"], route["annotation"])] = route
    # Walk the actual source parents, including selector/chooser/tree and
    # custom interfaces across module roots. Rootless Generic interfaces are
    # never materialized as independent handlers.
    if bases and (info["class_bases"] or not info.get("is_interface", False)):
        visited = set()
        def inherit(child):
            for name in child["parents"]:
                parent = _parent_info(child, str(name), infos)
                if not parent or parent["qualified"] in visited:
                    continue
                visited.add(parent["qualified"])
                parent_routes = extract_routes(parent["text"], str(parent["relative"]), parent["constants"])
                for route in parent_routes:
                    suffix = route["path"]
                    if route.get("class_base"):
                        suffix = suffix[len(route["class_base"]):]
                    for base in bases:
                        # An inherited class mapping is already in bases;
                        # never append the same base twice.
                        path = _join(base, suffix)
                        provenance = {"source": _source_reference(parent["source"]), "class": parent["qualified"],
                                      "annotation": route["annotation"]}
                        matches = [row for row in unique.values()
                                   if row["method"] == route["method"] and row["path"] == path]
                        if matches:
                            for row in matches:
                                if provenance not in row.setdefault("inherited_mapping_sources", []):
                                    row["inherited_mapping_sources"].append(provenance)
                            continue
                        item = {"source": str(info["relative"]), "class": str(info["qualified"]),
                                "class_base": base, "method": route["method"], "path": path,
                                "annotation": "inherited." + str(parent["simple"]),
                                "inherited_mapping_sources": [provenance]}
                        unique[(item["method"], path, item["class"], item["annotation"])] = item
                inherit(parent)
        inherit(info)
    return list(unique.values())


def extract_routes(source: str, source_path: str, constants: Optional[Mapping[str, str]] = None) -> List[Dict[str, str]]:
    lines = _logical_lines(source)
    class_name = _class_name(source, source_path)
    base = ""
    rows: List[Dict[str, str]] = []
    pending: Optional[Dict[str, object]] = None
    for index, line in enumerate(lines):
        parsed = _annotation(line, constants)
        if parsed is None:
            continue
        name = str(parsed["name"])
        # Annotation-only class mappings are identified by the following
        # declaration.  A method-level RequestMapping with an explicit HTTP
        # method is always a route.
        following = " ".join(item.strip() for item in lines[index + 1:index + 4] if item.strip())
        class_decl = bool(re.search(r"\b(class|interface|enum)\b", following))
        is_class_mapping = name == "RequestMapping" and not parsed["has_explicit_method"] and class_decl
        if is_class_mapping:
            base = str(parsed["paths"][0])
            pending = parsed
            continue
        for method in parsed["methods"]:
            for path in parsed["paths"]:
                rows.append({
                    "source": source_path,
                    "class": class_name,
                    "class_base": base,
                    "method": method,
                    "path": _join(base, path),
                    "annotation": name,
                })
        pending = None
    return rows


def _logical_lines(source: str) -> List[str]:
    """Join multiline mapping annotations before route extraction.

    The recovered project uses both one-line mappings and ``path`` arrays
    spread over several lines.  Treating each physical line independently
    silently loses those routes and makes the source-to-owned index look
    less complete than the project actually is.
    """
    physical = source.splitlines()
    result: List[str] = []
    index = 0
    while index < len(physical):
        line = physical[index]
        if "@" in line and "(" in line and line.count("(") > line.count(")"):
            parts = [line]
            balance = line.count("(") - line.count(")")
            index += 1
            while index < len(physical) and balance > 0:
                parts.append(physical[index])
                balance += physical[index].count("(") - physical[index].count(")")
                index += 1
            result.append(" ".join(parts))
            continue
        result.append(line)
        index += 1
    return result


def _owned_route_is_concrete(route: Dict[str, str]) -> bool:
    """Keep route matches tied to a concrete owned controller/resource.

    GenericResource contributes inherited ``/queries``, ``/{id}``, and root
    mappings without a class-level base path.  Matching those paths globally
    makes every unrelated project resource appear implemented by the same
    generic controller.  A concrete class base makes an otherwise generic
    child path specific again; full-path framework seams remain eligible.
    """
    path = route["path"]
    base = route.get("class_base", "")
    if route.get("class", "").rsplit(".", 1)[-1] in GENERIC_PAGE_INHERITED_ROUTES:
        return False
    return bool(base.strip("/")) or path not in GENERIC_INHERITED_PATHS


def _owned_alias_routes(info: Dict[str, object]) -> List[Dict[str, str]]:
    """Expand explicitly bounded owned controller resource aliases.

    This is provenance metadata for routes already served by the controller;
    it must remain an explicit allowlist rather than guessing from arbitrary
    path variables.
    """
    aliases = OWNED_ROUTE_ALIAS_EXPANSIONS.get(str(info.get("simple")), ())
    if not aliases:
        return []
    source = str(info["relative"])
    qualified = str(info["qualified"])
    if str(info.get("simple")) == "OwnedPersonnelReadDao":
        return [{
            "source": str(info["relative"]),
            "class": str(info["qualified"]),
            "class_base": "",
            "method": method,
            "path": path,
            "annotation": "owned-configuration-wiring",
        } for method, path in (
            ("POST", "/secure/emc/module/mdm/emp/emps/queries"),
            ("POST", "/secure/emc/module/mdm/emp/emps/queries/raw"),
        )]
    return [{
        "source": source,
        "class": qualified,
        "class_base": "",
        "method": "GET",
        "path": "/secure/emc/module/bp/%s/info/status" % resource,
        "annotation": "owned-alias-expansion",
    } for resource in aliases]


def _packaged_classes(distribution: Optional[Path]) -> Set[str]:
    if not distribution:
        return set()
    paths: List[Path] = []
    if distribution.is_dir():
        paths = list(distribution.rglob("*.class"))
        return {str(path.relative_to(distribution)).replace("\\", "/") for path in paths}
    with zipfile.ZipFile(distribution) as archive:
        return {name for name in archive.namelist() if name.endswith(".class")}


def _runtime_java_sources(source_root: Path) -> Dict[str, Dict[str, object]]:
    return _source_infos(source_root)


def _handler_candidates(info: Dict[str, object], infos: Dict[str, Dict[str, object]]) -> List[Dict[str, object]]:
    if "_handler_candidates" in info:
        return info["_handler_candidates"]
    candidates = []
    for name, handler in infos.items():
        if name != handler["qualified"] or not handler.get("is_handler"):
            continue
        if handler is not info and not _inherits(handler, infos, str(info["qualified"])):
            # _inherits also accepts short generic fallback names. Candidate
            # matching must resolve the actual FQN to avoid package collisions.
            seen = set()
            def implements(child):
                if child["qualified"] in seen:
                    return False
                seen.add(child["qualified"])
                for parent in child["parents"]:
                    found = _parent_info(child, str(parent), infos)
                    if found and (found is info or implements(found)):
                        return True
                return False
            if not implements(handler):
                continue
        conditions = re.findall(r"@(GikamBean|Profile|Conditional\w*)\b", str(handler["text"]))
        factories = handler.get("bean_factory_sources", [])
        conditions.extend(value for factory in factories for value in factory["conditions"])
        candidates.append({"class": handler["qualified"], "source": _source_reference(handler["source"]),
                           "conditions": sorted(set(conditions)),
                           "bean_factory_sources": factories,
                           "runtime_registration": "unverified"})
    info["_handler_candidates"] = sorted(candidates, key=lambda row: str(row["class"]))
    return info["_handler_candidates"]


def _compiled_source_roots(source_root: Path, explicit_owned: Sequence[Path]) -> Tuple[List[Path], List[Path]]:
    """Read existing build inputs; do not maintain a second module allowlist."""
    source_root = source_root.resolve()
    root = source_root.parents[3]  # project/src/main/java -> repository
    registry = root / "docs/reconstitution/framework-module-registry.json"
    framework = []
    if registry.is_file():
        for module in json.loads(registry.read_text())["modules"]:
            path = root / "reconstituted/framework" / module["module"] / "src/main/java"
            if path.is_dir():
                framework.append(path)
    owned = list(explicit_owned)
    compiler = root / "scripts/build-source-only-application.py"
    if compiler.is_file():
        # The source-only compiler is authoritative for which transitional
        # modules enter this application. Read its literal module tuple.
        for node in ast.walk(ast.parse(compiler.read_text())):
            if not isinstance(node, ast.Assign) or not any(
                isinstance(target, ast.Name) and target.id == "owned_source_roots"
                for target in node.targets
            ):
                continue
            if isinstance(node.value, ast.ListComp):
                for generator in node.value.generators:
                    try:
                        names = ast.literal_eval(generator.iter)
                    except (ValueError, TypeError):
                        continue
                    for name in names:
                        path = root / name / "src/main/java"
                        if path.is_dir():
                            owned.append(path)
    return framework, list(dict.fromkeys(path.resolve() for path in owned))


def build_inventory(source_root: Path, packaged_classes: Set[str],
                    owned_route_map: Optional[Dict[str, List[str]]] = None,
                    source_index: Optional[Dict[str, Dict[str, object]]] = None) -> List[Dict[str, object]]:
    owned_route_map = owned_route_map or {}
    source_root = source_root.resolve()
    rows: List[Dict[str, object]] = []
    inherited_pages: Dict[Tuple[str, str], Dict[str, object]] = {}
    infos = source_index if source_index is not None else _source_infos(source_root)
    for qualified in sorted({str(item["qualified"]) for item in infos.values()}):
        info = infos[qualified]
        source_path = info["source"]
        if not source_path.is_relative_to(source_root.resolve()):
            continue
        relative = str(info["relative"])
        routes = _routes_for_source(info, infos)
        # The source-root-relative path is authoritative even when a fixture
        # omits a package declaration; it also avoids confusing nested package
        # names with a relocated source root.
        class_entry = relative[:-len(".java")] + ".class" if routes else ""
        for route in routes:
            canonical = f"{route['method']} {route['path']} {route['class']}"
            route_key = "%s %s" % (route["method"], route["path"])
            owned_sources = sorted(set(owned_route_map.get(route_key, [])))
            row = {
                "id": hashlib.sha256(canonical.encode("utf-8")).hexdigest()[:16],
                **route,
                "runtime_status": "owned-class-present" if class_entry in packaged_classes or owned_sources else "source-only",
                "owned_sources": owned_sources,
                "handler_candidates": _handler_candidates(info, infos),
                "runtime_registration": "unverified",
                "behavior_status": "unknown",
                "evidence_status": "unknown",
            }
            if route["annotation"].removeprefix("inherited.") in GENERIC_PAGE_INHERITED_ROUTES:
                key = (route["method"], route["path"])
                provenance = {name: route[name] for name in ("source", "class", "annotation")}
                if key in inherited_pages:
                    inherited_pages[key]["inherited_page_sources"].append(provenance)
                    continue
                row["inherited_page_sources"] = [provenance]
                inherited_pages[key] = row
            rows.append(row)
    return sorted(rows, key=lambda row: (row["path"], row["method"], row["class"], row["source"]))


def build_owned_boundary_inventory(owned_roots: Sequence[Path], project_routes: Sequence[Dict[str, object]],
                                   source_index: Optional[Dict[str, Dict[str, object]]] = None) -> List[Dict[str, object]]:
    """Index concrete owned Java routes with no project source counterpart.

    These are owned Java source-only/framework entry points (login, workspace,
    menus, owned controllers, and similar seams). They are kept separate from
    project route rows for source accounting, but run in the same Spring
    application and never imply a compatibility process or HTTP bridge.
    """
    project_keys = {(str(row["method"]), str(row["path"])) for row in project_routes}
    owned: Dict[Tuple[str, str], Dict[str, object]] = {}
    infos = source_index if source_index is not None else _source_index(owned_roots)
    for owned_root in owned_roots:
        owned_root = owned_root.resolve()
        for qualified in sorted({str(item["qualified"]) for item in infos.values()}):
            info = infos[qualified]
            if not info["source"].is_relative_to(owned_root.resolve()):
                continue
            if not info.get("is_handler") and not info["class_bases"]:
                continue
            candidates = _handler_candidates(info, infos)
            if not candidates:
                continue
            for route in _routes_for_source(info, infos):
                if not _owned_route_is_concrete(route):
                    continue
                key = (str(route["method"]), str(route["path"]))
                if key in project_keys:
                    continue
                try:
                    source = str(owned_root.relative_to(REPO_ROOT) / str(info["relative"]))
                except ValueError:
                    source = str(owned_root / str(info["relative"]))
                framework = "reconstituted/framework/" in str(info["source"])
                item = owned.setdefault(key, {
                    "id": hashlib.sha256(("owned-boundary " + key[0] + " " + key[1]).encode("utf-8")).hexdigest()[:16],
                    **route,
                    "source": source,
                    "source_relative_to_module": route["source"],
                    "boundary_scope": "owned-framework-source-only" if framework else "owned-source-only",
                    "route_origin": "owned-java-source-only",
                    "boundary_semantics": "owned Java source-only route; boundary route != boundary process",
                    "runtime_model": "same-spring-application",
                    "owned_sources": [],
                    "handler_candidates": [],
                    "runtime_registration": "unverified",
                    "registration_notes": ["source candidate; conditional/component-scan/override selection is not runtime proof"],
                    "behavior_status": "unknown",
                    "evidence_status": "unknown",
                })
                if source not in item["owned_sources"]:
                    item["owned_sources"].append(source)
                for candidate in candidates:
                    if candidate not in item["handler_candidates"]:
                        item["handler_candidates"].append(candidate)
                for provenance in route.get("inherited_mapping_sources", []):
                    if provenance not in item.setdefault("inherited_mapping_sources", []):
                        item["inherited_mapping_sources"].append(provenance)
                if info.get("shadowed_sources"):
                    item["shadowed_sources"] = [_source_reference(Path(path)) for path in info["shadowed_sources"]]
    return sorted(owned.values(), key=lambda row: (row["path"], row["method"], row["source"]))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-root", type=Path, required=True)
    parser.add_argument("--distribution", type=Path)
    parser.add_argument("--owned-source-root", type=Path, action="append", default=[])
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    framework_roots, owned_roots = _compiled_source_roots(args.source_root, args.owned_source_root)
    boundary_roots = framework_roots + owned_roots
    infos = _source_index(framework_roots + [args.source_root] + owned_roots)
    owned_route_map: Dict[str, List[str]] = {}
    for owned_root in boundary_roots:
        for qualified in sorted({str(item["qualified"]) for item in infos.values()}):
            info = infos[qualified]
            if not info["source"].is_relative_to(owned_root.resolve()):
                continue
            if not info["class_bases"] and not info.get("is_handler"):
                continue
            if not _handler_candidates(info, infos):
                continue
            routes = _routes_for_source(info, infos) + _owned_alias_routes(info)
            for route in routes:
                if not _owned_route_is_concrete(route):
                    continue
                key = "%s %s" % (route["method"], route["path"])
                owned_route_map.setdefault(key, []).append(_source_reference(info["source"]))
    rows = build_inventory(args.source_root, _packaged_classes(args.distribution), owned_route_map, infos)
    boundary_routes = build_owned_boundary_inventory(boundary_roots, rows, infos)
    # Retain former project provenance only when method/path still matches.
    # Newly resolved inherited bases must not alias an old wrong root path to
    # a different real HTTP request.
    old_rows = build_inventory(args.source_root, set())
    current_by_key = {}
    for route in rows + boundary_routes:
        current_by_key.setdefault((route["method"], route["path"]), []).append(route)
    active_ids = {route["id"] for route in rows + boundary_routes}
    removed_rows = []
    for old in old_rows:
        if old["id"] in active_ids:
            continue
        matches = current_by_key.get((old["method"], old["path"]), [])
        if matches:
            matches[0].setdefault("source_route_ids", []).append(old["id"])
        else:
            removed_rows.append({name: old[name] for name in ("id", "method", "path", "source", "class")})
            removed_rows[-1]["reason"] = "old isolated source-root mapping has no matching authoritative method/path; not an evidence alias"
    # A former owned-only route may now share its wire contract with a newly
    # discovered project declaration. Preserve only its same-method/path ID.
    for route in rows:
        key = str(route["method"]) + " " + str(route["path"])
        if key in owned_route_map:
            route.setdefault("source_route_ids", []).append(hashlib.sha256(
                ("owned-boundary " + key).encode("utf-8")).hexdigest()[:16])
    # Audit old rootless owned interface rows without retaining them as HTTP
    # candidates. These were previously scanned without a concrete bean/base.
    for owned_root in owned_roots:
        for name, info in _source_infos(owned_root).items():
            if name != info["qualified"] or not info["is_interface"] or info["class_bases"]:
                continue
            if _handler_candidates(info, infos):
                continue
            for route in extract_routes(info["text"], info["relative"], info["constants"]):
                if not _owned_route_is_concrete(route):
                    continue
                if (route["method"], route["path"]) in current_by_key:
                    continue
                key = str(route["method"]) + " " + str(route["path"])
                removed_rows.append({"id": hashlib.sha256(("owned-boundary " + key).encode()).hexdigest()[:16],
                                     **{field: route[field] for field in ("method", "path", "source", "class")},
                                     "reason": "rootless interface has no concrete mapped handler candidate; not an HTTP route"})
    # Keep identifiers from earlier source-accounting views addressable. This
    # is an alias formula, not a route list: discovery comes from Java source.
    for route in boundary_routes:
        key = str(route["method"]) + " " + str(route["path"])
        route["source_route_ids"] = sorted(set(route.get("source_route_ids", [])) | {str(route["id"]), hashlib.sha256(
            ("framework-boundary " + key).encode("utf-8")).hexdigest()[:16]})
    binding = capture_binding(REPO_ROOT)
    payload = {
        "schema": 1,
        "scope": "project-java-http-api-source-inventory",
        "source_commit": _source_commit(),
        "source_dirty": _source_dirty(),
        **binding_metadata(binding),
        "source_root": str(args.source_root),
        "source_index_roots": [_source_reference(path) for path in framework_roots + [args.source_root] + owned_roots],
        "removed_source_rows": removed_rows,
        "distribution": str(args.distribution) if args.distribution else None,
        "counts": {
            "route_count": len(rows),
            "owned_class_present": sum(row["runtime_status"] == "owned-class-present" for row in rows),
            "owned_route_source_match": sum(bool(row.get("owned_sources")) for row in rows),
            "source_only": sum(row["runtime_status"] == "source-only" for row in rows),
            "behavior_unknown": sum(row["behavior_status"] == "unknown" for row in rows),
            "owned_boundary_route_count": len(boundary_routes),
            "owned_boundary_behavior_unknown": sum(row["behavior_status"] == "unknown" for row in boundary_routes),
        },
        "policy": "class presence is not behavior evidence; behavior_status remains unknown until a real contract report is bound",
        "route_execution_model": {
            "application": "one owned Spring application",
            "oracle": "shared by project and owned Java source-only routes within a wave",
            "redis": "shared by project and owned Java source-only routes within a wave",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "routes": rows,
        "boundary_routes": boundary_routes,
    }
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("business API inventory: PASS", payload["counts"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
