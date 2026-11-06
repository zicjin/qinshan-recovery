#!/usr/bin/env python3
"""Generate route-complete API contract skeletons and continuous waves.

The output is planning material.  It deliberately leaves behaviour, fixture,
role and audit fields unknown until a real HTTP contract is written and
replayed.  Waves share one milestone runtime and persistent Oracle baseline/runtime
schemas, while every route keeps its case skeleton and group assignment. Source-only/boundary cases
remain in the same Spring application wave; they never imply a separate
compatibility process.
"""
from __future__ import annotations

import argparse
import collections
import hashlib
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Any, Dict, Iterable, List, Mapping

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))
sys.path.insert(0, str(ROOT / "scripts"))
from business_api_inventory import _annotation, _class_mapping_paths, _class_name
from source_binding import binding_metadata


WAVE_ORDER = (
    "foundation",
    "personnel",
    "equipment",
    "consumables-materials",
    "secondary",
)

GLOBAL_QUERY_SEAM_PATHS = (
    "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/sql/SqlHelper.java",
    "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/interceptor/MybatisInterceptor.java",
    "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mybatis/page/MybatisPageHelper.java",
    "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/exception/advice/GlobalExceptionHandler.java",
    "owned-framework/src/main/java/com/sunwayworld/owned/internal/web/QueryRequest.java",
)
FAMILY_FINGERPRINT_SCHEMA = 1
FAMILY_RESPONSE_CONTRACT_VERSION = "3"


def wave_for_family(family: str) -> str:
    if family == "core":
        return "foundation"
    if family in {"personnel", "equipment", "consumables-materials"}:
        return family
    return "secondary"


def _case(route: Mapping[str, Any], group_id: str | None, wave: str) -> Dict[str, Any]:
    wave_runtime_id = "api-wave-" + wave
    return {
        "id": str(route.get("id", "")),
        "route_id": str(route.get("id", "")),
        "method": route.get("method"),
        "path": route.get("path"),
        "source": route.get("source"),
        "class": route.get("class"),
        "family": route.get("family"),
        "route_kind": route.get("route_kind"),
        "native_relevance": route.get("native_relevance"),
        "route_origin": route.get("route_origin", "project-spring-source"),
        "boundary_semantics": route.get("boundary_semantics", "project Spring route"),
        "runtime_model": route.get("runtime_model", "same-spring-application"),
        "wave": wave,
        "wave_runtime_id": wave_runtime_id,
        "execution_group": group_id,
        "operation": "unknown",
        "body": {},
        "headers": {},
        "expected_status": [],
        "assert": {},
        "fixture": {
            "mode": "unknown",
            "setup_requests": [],
            "restore_requests": [],
            "business_write_via": "http",
        },
        "role": "unknown",
        "organization": "unknown",
        "mode": "strict",
        "database_objects": route.get("database_objects", []),
        "audit": {"required": "unknown", "status": "unknown"},
        "rollback": "unknown",
        "cleanup": "unknown",
        "disposition": "unknown",
        "evidence": None,
        "unknown_reason": "contract skeleton generated; no HTTP disposition registered",
    }



class SourceManifest:
    """Conservative source references, not a Java call-graph or API verifier.

    Only direct concrete annotation matches can combine candidate aliases.
    Missing/ambiguous mappings retain one family per route. Dependency edges
    and Mapper XML are static reconstruction inputs; policy and wire contracts
    remain unknown until validated by real HTTP execution.
    """
    def __init__(self, root: Path):
        self.root = root.resolve()
        self.sources: Dict[Path, str] = {}
        self.handlers: Dict[Path, Dict[tuple[str, str], List[Dict[str, str]]]] = {}
        self.chains: Dict[Path, List[Dict[str, Any]]] = {}
        self.mappers: Dict[Path, List[Dict[str, Any]]] = {}
        self.java_roots = sorted(self.root.glob("owned-*/src/main/java")) + [self.root / "project/src/main/java"]

    def resolve(self, value: str) -> Path | None:
        path = Path(value)
        if path.is_absolute():
            try:
                path.resolve().relative_to(self.root)
            except ValueError:
                return None
            return path if path.is_file() else None
        if value.startswith("lims-recovery/"):
            value = value[len("lims-recovery/"):]
        for candidate in [self.root / value, self.root / "project/src/main/java" / value]:
            try:
                candidate.resolve().relative_to(self.root)
            except ValueError:
                continue
            if candidate.is_file():
                return candidate
        return None

    def text(self, path: Path) -> str:
        if path not in self.sources:
            self.sources[path] = path.read_text(encoding="utf-8", errors="replace")
        return self.sources[path]

    def reference(self, path: Path) -> Dict[str, str]:
        return {"path": path.relative_to(self.root).as_posix(),
                "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
                "evidence_kind": "static-source-reference"}

    @staticmethod
    def _code(text: str) -> str:
        # Remove comments without mistaking comment markers inside strings.
        return re.sub(r'"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*.*?\*/',
                      lambda m: m.group(0) if m.group(0).startswith(('"', "'")) else " ", text, flags=re.S)

    def mapped_handlers(self, path: Path) -> Dict[tuple[str, str], List[Dict[str, str]]]:
        if path in self.handlers:
            return self.handlers[path]
        text = self._code(self.text(path))
        constants = dict(re.findall(r'static\s+final\s+String\s+(\w+)\s*=\s*"([^"\\]*)"', text))
        bases = _class_mapping_paths(text) or [""]
        result: Dict[tuple[str, str], List[Dict[str, str]]] = collections.defaultdict(list)
        for match in re.finditer(r"@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\s*\(", text):
            opening = text.index("(", match.start())
            depth, quote, escaped, closing = 0, False, False, None
            for offset in range(opening, len(text)):
                char = text[offset]
                if quote:
                    if escaped: escaped = False
                    elif char == "\\": escaped = True
                    elif char == '"': quote = False
                elif char == '"': quote = True
                elif char == "(": depth += 1
                elif char == ")":
                    depth -= 1
                    if depth == 0:
                        closing = offset
                        break
            if closing is None:
                continue
            following = text[closing + 1:closing + 2000]
            declaration = re.search(r"\b(class|interface|enum)\b|\b(?:public|protected)\s+(?:static\s+)?(?:final\s+)?[\w.<>,?\s\[\]]+?\s+(\w+)\s*\(", following)
            if not declaration or declaration.group(1):
                continue
            annotation = _annotation(" ".join(text[match.start():closing + 1].split()), constants)
            if not annotation:
                continue
            for base in bases:
                for route_path in annotation["paths"]:
                    joined = "/" + "/".join(part.strip("/") for part in (base, route_path) if part.strip("/"))
                    for method in annotation["methods"]:
                        result[(str(method), joined)].append({"class": _class_name(text, path.name),
                                                             "method": declaration.group(2),
                                                             **self.reference(path)})
        self.handlers[path] = dict(result)
        return self.handlers[path]

    def handler(self, route: Mapping[str, Any]) -> tuple[Dict[str, str] | None, str | None]:
        found = {}
        for raw in route.get("owned_sources", []):
            path = self.resolve(str(raw))
            if not path or path.suffix != ".java":
                continue
            for handler in self.mapped_handlers(path).get((str(route.get("method")), str(route.get("path"))), []):
                found[(handler["path"], handler["method"])] = handler
        if len(found) == 1:
            return next(iter(found.values())), None
        return None, ("ambiguous source handler mapping" if found else "source handler not established from concrete owned mapping")

    def dependency_chain(self, handler: Mapping[str, Any]) -> List[Dict[str, Any]]:
        start = self.resolve(str(handler["path"]))
        if start is None:
            return []
        if start in self.chains:
            return self.chains[start]
        pending, seen, nodes = [(start, 0)], set(), []
        while pending:
            path, depth = pending.pop(0)
            if path in seen:
                continue
            seen.add(path)
            text = self._code(self.text(path))
            node = {"class": _class_name(text, path.name), **self.reference(path)}
            nodes.append(node)
            if depth >= 2:
                continue
            imports = {qualified.rsplit(".", 1)[-1]: qualified
                       for qualified in re.findall(r"\bimport\s+([\w.]+)\s*;", text)}
            package = _class_name(text, path.name).rsplit(".", 1)[0]
            referenced_types = set(re.findall(r"\b(?:private|protected|public)\s+(?:final\s+)?(\w+)\s+\w+\s*[;=]", text))
            referenced_types.update(re.findall(r"\bnew\s+(\w+)\s*\(", text))
            for name in sorted(referenced_types):
                if not name.endswith(("Service", "Dao", "DAO", "Mapper")):
                    continue
                qualified = imports.get(name, package + "." + name)
                for java_root in self.java_roots:
                    dependency = java_root / (qualified.replace(".", "/") + ".java")
                    if dependency.is_file():
                        pending.append((dependency, depth + 1))
                        break
        self.chains[start] = nodes
        return nodes

    def mapper_statements(self, route: Mapping[str, Any]) -> List[Dict[str, Any]]:
        project_source = self.resolve(str(route.get("source", "")))
        if not project_source or "project/src/main/java" not in project_source.as_posix():
            return []
        domain = project_source.parent.parent
        if domain in self.mappers:
            return self.mappers[domain]
        statements = []
        for path in sorted((domain / "mapper/conf").glob("*oracle*mapper.xml")):
            try:
                xml = ET.fromstring(self.text(path))
            except ET.ParseError:
                continue
            for statement in xml:
                if statement.tag not in {"select", "insert", "update", "delete"}:
                    continue
                sql = " ".join(" ".join(statement.itertext()).split())
                statements.append({**self.reference(path), "namespace": xml.get("namespace"),
                                   "statement_id": statement.get("id"), "operation": statement.tag,
                                   "sql_projection": sql,
                                   "tables": sorted(set(re.findall(r"\b(?:FROM|JOIN|UPDATE|INTO)\s+(T_[A-Z0-9_]+)", sql, flags=re.I))),
                                   "includes": sorted({node.get("refid") for node in statement.iter("include") if node.get("refid")}),
                                   "runtime_statement_verified": False})
        self.mappers[domain] = statements
        return statements


def _resource_path(route: Mapping[str, Any]) -> str:
    # An explicit resource path is stronger than the shared controller class.
    # Only the three inherited query aliases have a conservative fallback.
    return str(route.get("class_base") or re.sub(r"/queries(?:/(?:raw|searchable))?$", "", str(route.get("path", ""))))


def _global_seam_references(source_root: Path) -> List[Dict[str, str]]:
    references = []
    for relative in GLOBAL_QUERY_SEAM_PATHS:
        path = source_root / relative
        if path.is_file():
            references.append({
                "path": relative,
                "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
                "evidence_kind": "framework-seam-source",
            })
    return sorted(references, key=lambda item: item["path"])


def _family_fingerprint(family: Mapping[str, Any],
                        global_seams: List[Dict[str, str]]) -> Dict[str, Any]:
    provenance = sorted(
        family.get("source_provenance", []),
        key=lambda item: (str(item.get("path", "")), str(item.get("sha256", ""))),
    )
    mapper_statements = sorted(
        family.get("mapper_statements", []),
        key=lambda item: (
            str(item.get("path", "")),
            str(item.get("namespace", "")),
            str(item.get("statement_id", "")),
            str(item.get("operation", "")),
        ),
    )
    payload = {
        "schema": FAMILY_FINGERPRINT_SCHEMA,
        "response_contract_version": FAMILY_RESPONSE_CONTRACT_VERSION,
        "source_provenance": provenance,
        "mapper_statements": mapper_statements,
        "framework_seams": global_seams,
        "permission_policy": family.get("permission_policy"),
        "organization_policy": family.get("organization_policy"),
        "response_envelope": family.get("response_envelope"),
        "error_contract": family.get("error_contract"),
        "pagination_policy": family.get("pagination_policy"),
    }
    return {
        "family_fingerprint": "fam-" + hashlib.sha256(
            json.dumps(payload, ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode("utf-8")
        ).hexdigest(),
        "family_fingerprint_schema": FAMILY_FINGERPRINT_SCHEMA,
        "family_fingerprint_inputs": payload,
    }


def behavior_manifest(routes: List[Mapping[str, Any]], assignment: Mapping[str, Any],
                      source_root: Path) -> tuple[List[Dict[str, Any]], List[Dict[str, Any]], Dict[str, str]]:
    source = SourceManifest(source_root)
    global_seams = _global_seam_references(source_root)
    grouped: Dict[str, Dict[str, Any]] = {}
    route_assignment = {}
    for route in routes:
        handler, unknown = source.handler(route)
        key = {"handler": handler, "resource_path": _resource_path(route), "method": route.get("method"),
               "wave": wave_for_family(str(route.get("family", ""))),
               "path_template": re.sub(r"/queries/(?:raw|searchable)$", "/queries", str(route.get("path", "")))}
        if handler is None:
            key["unresolved_route_id"] = str(route.get("id"))
        family_id = "behavior-" + hashlib.sha256(json.dumps(key, sort_keys=True).encode()).hexdigest()[:16]
        if family_id not in grouped:
            chain = source.dependency_chain(handler) if handler else []
            grouped[family_id] = {
                "id": family_id, "source_handler": handler, "resource_path": _resource_path(route),
                "http_method": route.get("method"), "path_template": key["path_template"],
                "wave": key["wave"], "alias_route_ids": [], "aliases": [],
                "source_provenance": [], "dependency_chain": chain, "mapper_statements": [],
                "permission_policy": "unknown", "organization_policy": "unknown",
                "response_envelope": "unknown", "error_contract": "unknown", "pagination_policy": "unknown",
                "alias_equivalence": "unverified", "disposition": "unknown", "strict_evidence": False,
                "unknown_reasons": ([unknown] if unknown else []) + [
                    "permission and organization behavior require HTTP verification",
                    "response, errors, pagination and alias equivalence require parameterized HTTP execution",
                    "static source references do not establish runtime Service/DAO/Mapper dispatch"],
            }
        family = grouped[family_id]
        route_id = str(route.get("id"))
        family["alias_route_ids"].append(route_id)
        family["aliases"].append({"route_id": route_id, "method": route.get("method"), "path": route.get("path"),
                                  "source": route.get("source"), "class": route.get("class"),
                                  "route_kind": route.get("route_kind"), "route_origin": route.get("route_origin", "project-spring-source"),
                                  "role": route.get("roles", "unknown"), "organization": route.get("organizations", "unknown"),
                                  "unknown_reason": route.get("unknown_reason", "HTTP contract not executed")})
        for reference in chain + ([source.reference(path)] if (path := source.resolve(str(route.get("source", "")))) else []):
            if reference not in family["source_provenance"]:
                family["source_provenance"].append(reference)
        for statement in source.mapper_statements(route):
            if statement not in family["mapper_statements"]:
                family["mapper_statements"].append(statement)
        route_assignment[route_id] = family_id
    families, jobs = [], []
    for family_id, family in sorted(grouped.items()):
        if not family["mapper_statements"]:
            family["unknown_reasons"].append("project Mapper statement not established")
        classes = [node["class"].rsplit(".", 1)[-1] for node in family["dependency_chain"]]
        if not any(name.endswith("Service") for name in classes) or not any(name.endswith(("Dao", "DAO", "Mapper")) for name in classes):
            family["unknown_reasons"].append("Service/DAO implementation chain not established")
        family["alias_route_ids"].sort()
        family["aliases"].sort(key=lambda item: item["route_id"])
        family["source_provenance"].sort(key=lambda item: item["path"])
        family.update(_family_fingerprint(family, global_seams))
        families.append(family)
        jobs.append({"id": "job-" + family_id, "behavior_family_id": family_id, "wave": family["wave"],
                     "job_kind": "parameterized-http-contract-planning", "strict_ready": False, "strict_evidence": False,
                     "execution_groups": sorted({str(assignment[route_id]) for route_id in family["alias_route_ids"]}),
                     "request_template": {"role": "unknown", "organization": "unknown", "fixture": "unknown",
                                          "expected_status": [], "assert": {}, "audit": "unknown", "cleanup": "unknown"},
                     "parameters": [{"route_id": alias["route_id"], "method": alias["method"], "path": alias["path"]}
                                    for alias in family["aliases"]],
                     "coverage_policy": "execute and validate every alias parameter; grouping alone grants no strict route pass"})
    return families, jobs, route_assignment

def build(ledger: Mapping[str, Any], plan: Mapping[str, Any], *, source_root: Path = ROOT) -> Dict[str, Any]:
    routes = list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", []))
    assignment = plan.get("assignment", {})
    if not isinstance(assignment, Mapping):
        raise ValueError("execution plan assignment must be an object")
    waves: Dict[str, List[Dict[str, Any]]] = collections.defaultdict(list)
    for route in routes:
        route_id = str(route.get("id", ""))
        group_id = assignment.get(route_id)
        if not group_id:
            raise ValueError("route missing execution group: " + route_id)
        wave = wave_for_family(str(route.get("family", "")))
        waves[wave].append(_case(route, str(group_id), wave))

    families, jobs, family_assignment = behavior_manifest(routes, assignment, source_root)
    family_by_id = {family["id"]: family for family in families}
    planned_fingerprints = plan.get("family_fingerprints", {})
    if isinstance(planned_fingerprints, Mapping):
        for family_id, fingerprint in family_by_id.items():
            planned = planned_fingerprints.get(family_id)
            if planned and planned != fingerprint["family_fingerprint"]:
                raise ValueError("behavior family fingerprint differs from execution plan: " + family_id)
    for cases in waves.values():
        for case in cases:
            case["behavior_family_id"] = family_assignment[case["route_id"]]
            family = family_by_id[case["behavior_family_id"]]
            case["family_fingerprint"] = family["family_fingerprint"]

    output_waves: List[Dict[str, Any]] = []
    for wave in WAVE_ORDER:
        cases = sorted(waves.get(wave, []), key=lambda item: (str(item.get("family")), str(item.get("id"))))
        group_counts = collections.Counter(str(item["execution_group"]) for item in cases)
        output_waves.append({
            "wave": wave,
            "runtime_id": "api-wave-" + wave,
            "families": sorted({str(item.get("family")) for item in cases}),
            "route_count": len(cases),
            "execution_group_count": len(group_counts),
            "runtime_policy": "one Spring application/Redis lifecycle per milestone, one persistent clone-derived Oracle baseline/runtime schema; waves/groups are case batches, no Oracle restart or Data Pump per wave",
            "boundary_process": False,
            "internal_http_bridge": False,
            "parallel_policy": "waves are sequential; parallel groups require isolated checkout, target, ports, containers, network and temporary root",
            "strict_ready": False,
            "cases": cases,
        })
    return {
        "schema": 1,
        "scope": "current-head-api-contract-skeleton-and-continuous-waves",
        "source_commit": ledger.get("source_commit"),
        "source_dirty": ledger.get("source_dirty"),
        **binding_metadata(ledger),
        "implementation_dirty": ledger.get("implementation_dirty", ledger.get("source_dirty")),
        "route_count": len(routes),
        "project_route_count": len(ledger.get("routes", [])),
        "boundary_route_count": len(ledger.get("boundary_routes", [])),
        "wave_order": list(WAVE_ORDER),
        "policy": "planning-only; skeleton fields do not promote route behaviour",
        "route_execution_model": {
            "application": "one owned Spring application per milestone",
            "oracle": "one persistent Oracle with immutable baseline and recoverable runtime schema",
            "redis": "one Redis lifecycle per milestone",
            "boundary_route": "owned Java source-only route in the same Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "behavior_family_count": len(families),
        "behavior_families": families,
        "behavior_family_jobs": jobs,
        "behavior_family_assignment": family_assignment,
        "waves": output_waves,
    }


def main(argv: Iterable[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args(argv)
    ledger = json.loads(args.ledger.read_text(encoding="utf-8"))
    plan = json.loads(args.plan.read_text(encoding="utf-8"))
    if ledger.get("implementation_tree_digest") and plan.get("implementation_tree_digest"):
        if ledger.get("implementation_tree_digest") != plan.get("implementation_tree_digest"):
            raise SystemExit("contract skeleton: ledger and execution plan implementation bindings differ")
        if ledger.get("implementation_binding") != plan.get("implementation_binding"):
            raise SystemExit("contract skeleton: ledger and execution plan implementation_binding differs")
        if ledger.get("implementation_dirty") is not False or plan.get("implementation_dirty") is not False:
            raise SystemExit("contract skeleton: implementation scope must be clean")
    elif ledger.get("source_commit") != plan.get("source_commit"):
        raise SystemExit("contract skeleton: legacy ledger and execution plan source commits differ")
    skeleton = build(ledger, plan)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(skeleton, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("API contract skeleton:", {
        "source_commit": skeleton["source_commit"],
        "route_count": skeleton["route_count"],
        "waves": {wave["wave"]: wave["route_count"] for wave in skeleton["waves"]},
    })
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
