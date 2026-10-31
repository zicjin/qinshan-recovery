#!/usr/bin/env python3
"""Build an auditable, source-derived candidate capability catalog."""
from __future__ import annotations

import argparse
import hashlib
import importlib.util
import json
import re
from collections import defaultdict
from pathlib import Path

_spec = importlib.util.spec_from_file_location("catalog_extract", Path(__file__).with_name("extract.py"))
_extract = importlib.util.module_from_spec(_spec)
assert _spec.loader is not None
_spec.loader.exec_module(_extract)

FORMAT = "lims-full-capability-catalog-v1"
FAMILIES = ("read", "create", "update", "delete", "submit", "approve", "attachments", "reports", "import-export", "tasks", "integrations", "unknown")
KINDS = (("endpoints", "endpoint"), ("templates", "template"), ("mapper_xml", "mapper"), ("interfaces", "interface"))
INTEGRATION_MARKERS = {
    "erp": re.compile(r"\bErp[A-Z]\w*\b|\bERP\b|\.module\.erp\."),
    "oa": re.compile(r"\bEmcOa[A-Z]\w*\b|\bOA\b|\.module\.mdm\.oa\."),
    "reporting-service": re.compile(r"\bReportingService\w*\b|\breportingService\w*\b"),
    "fanruan": re.compile(r"\b(?:Fanruan|fanruan|FineReport)\w*\b"),
    "pageoffice": re.compile(r"\b(?:PageOffice|WordDocument|WebOffice)\w*\b|\bcom\.zhuozhengsoft\."),
    "http-client": re.compile(r"\b(?:OkHttpClientUtils|CloseableHttpClient|RestTemplate|HttpURLConnection)\b"),
    "email": re.compile(r"\b(?:sendEmail|sendMail|MailSender|JavaMailSender|MimeMessage|SMTP)\b"),
    "ftp": re.compile(r"\b(?:FTPClient|FtpClient|SftpClient|SFTP|FTP)\b"),
}


def digest(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def domain_for(path: str) -> str:
    text = path.removeprefix("project/src/main/").lower()
    text = text.removeprefix("java/com/sunwayworld/").removeprefix("resources/templates/")
    rules = (("integrations", r"/(?:erp|oa)(?:/|$)"), ("personnel", r"/(?:emp|emps)(?:/|$)"), ("equipment", r"/(?:equipt[^/]*|equipment[^/]*)(?:/|$)"), ("consumables", r"/(?:consume[^/]*|mtl[^/]*)(?:/|$)"), ("materials", r"/(?:reagent[^/]*|material[^/]*)(?:/|$)"), ("attachments", r"/(?:homeattachment|attachment[^/]*|annex[^/]*)(?:/|$)"), ("reports", r"/(?:report[^/]*|pageoffice)(?:/|$)"), ("samples", r"/(?:sample[^/]*)(?:/|$)"), ("results", r"/(?:result|run)(?:/|$)"), ("orders", r"/(?:order[^/]*|contract[^/]*|bp/bp)(?:/|$)"), ("projects", r"/(?:project[^/]*|projscheme[^/]*)(?:/|$)"), ("identity", r"/(?:security|sso|role[^/]*|permission[^/]*)(?:/|$)"), ("administration", r"/(?:dict[^/]*|menu[^/]*|org[^/]*)(?:/|$)"))
    return next((name for name, pattern in rules if re.search(pattern, "/" + text)), "other")


def resource_for(path: str) -> str:
    if path.startswith("project/src/main/java/"):
        relative = path.removeprefix("project/src/main/java/")
        parts = re.split(r"/(?:resource|controller|service|mapper)/", relative)
        return parts[0] if len(parts) > 1 else relative.rsplit("/", 1)[0]
    return path.removeprefix("project/src/main/resources/templates/").rsplit("/", 1)[0]


def operation_for(member: str) -> tuple[str, str]:
    words = re.sub(r"([a-z0-9])([A-Z])", r"\1 \2", member).lower()
    tokens = set(re.findall(r"[a-z]+", words))
    rules = (("attachments", {"attach", "attachment", "annex", "upload", "download", "file", "url", "document"}), ("import-export", {"import", "export", "excel", "csv"}), ("approve", {"approve", "approval", "audit", "review", "pass"}), ("submit", {"submit", "send", "apply"}), ("delete", {"delete", "remove", "clear", "discard", "cancel"}), ("update", {"update", "edit", "modify", "change", "replace", "set", "reset"}), ("create", {"create", "insert", "add", "save", "new", "copy"}), ("tasks", {"trigger", "execute", "schedule", "scheduled"}), ("integrations", {"sync", "synchronize", "erp"}), ("reports", {"report", "reports", "pdf", "preview", "print"}), ("read", {"list", "page", "query", "select", "find", "get", "detail", "view", "choose", "search", "load", "init", "pagination"}))
    for family, candidates in rules:
        if tokens & candidates:
            return family, "lexical-member-name-candidate"
    return "unknown", "no-safe-lexical-operation"


def source_ref(item: dict) -> dict:
    source = item["source"]
    return {"path": source["path"], "line": source["line"], "sha256": source["sha256"]}


def java_units(index: dict, source_root: Path | None) -> list[dict]:
    if source_root is None:
        return []
    units = []
    for manifest in index["source_manifest"]:
        if not manifest["path"].endswith(".java"):
            continue
        path = source_root / manifest["path"].removeprefix("project/")
        raw = path.read_bytes()
        if digest(raw) != manifest["sha256"]:
            raise ValueError("source hash differs from pinned index: " + manifest["path"])
        text = raw.decode("utf-8-sig")
        units.append({"source": dict(manifest), "text": text, "clean": _extract.strip_comments(text), "parsed": _extract.java_file(path, source_root)})
    return units


def scheduled_records(units: list[dict]) -> list[dict]:
    rows = []
    for unit in units:
        for match in re.finditer(r"@(?:org\.springframework\.scheduling\.annotation\.)?Scheduled\b", unit["clean"]):
            end = match.end()
            while end < len(unit["clean"]) and unit["clean"][end].isspace():
                end += 1
            end = _extract.close_paren(unit["clean"], end) if end < len(unit["clean"]) and unit["clean"][end] == "(" else end
            rows.append({"record_id": f"scheduled.{len(rows):06d}", "asset_type": "scheduled", "source": {**unit["source"], "line": unit["text"].count("\n", 0, match.start()) + 1}, "domain": "tasks", "resource": resource_for(unit["source"]["path"]), "capability": _extract.next_method(unit["clean"], end) or "unknown", "operation_family": "tasks", "operation_basis": "scheduled-annotation-declaration", "annotation": "Scheduled", "status": "unknown", "dynamic_status": "unknown", "static_status": "static-present"})
    return rows


def integration_candidates(units: list[dict]) -> list[dict]:
    found = {key: [] for key in INTEGRATION_MARKERS}
    for unit in units:
        for number, line in enumerate(unit["clean"].splitlines(), 1):
            for category, marker in INTEGRATION_MARKERS.items():
                if marker.search(line):
                    found[category].append({**unit["source"], "line": number})
    return [{"category": category, "basis": "public-source-lexical-marker-candidate", "status": "unknown", "source_refs": refs} for category, refs in found.items() if refs]


def page_links(index: dict, units: list[dict]) -> list[dict]:
    templates = {row["view_name"]: (i, row) for i, row in enumerate(index["templates"])}
    links = []
    for unit in units:
        parsed = unit["parsed"]
        if not parsed or parsed["role"] != "controller":
            continue
        for match in re.finditer(r'\breturn\s+("(?:\\.|[^"\\])*")\s*;', unit["clean"]):
            view = _extract.decode_literal(match[1])
            if view not in templates:
                continue
            before = unit["clean"][:match.start()]
            methods = list(re.finditer(r"\b(?:public|protected|private)\s+(?:[\w<>?,.\[\]]+\s+)+(\w+)\s*\([^;{}]*\)\s*(?:throws\s+[\w,.\s]+)?\{", before))
            member = methods[-1][1] if methods else "unknown"
            ti, template = templates[view]
            links.append({"controller_symbol": parsed["symbol"], "member": member, "template_index": ti, "template_source": template["source"], "return_source": {**unit["source"], "line": unit["text"].count("\n", 0, match.start()) + 1}, "basis": "literal-controller-return-matches-template-view-name", "status": "unknown"})
    return links


def build(index_path: Path, output: Path, source_root: Path | None = None) -> dict:
    index = json.loads(index_path.read_text(encoding="utf-8"))
    units = java_units(index, source_root)
    records = []
    for source_kind, kind in KINDS:
        for position, item in enumerate(index[source_kind]):
            source = source_ref(item)
            row = {"record_id": f"{kind}.{position:06d}", "asset_type": kind, "source_index": position, "source": source, "domain": domain_for(source["path"]), "domain_basis": "source-path-candidate", "resource": resource_for(source["path"]), "resource_basis": "source-package-or-directory", "status": "unknown", "dynamic_status": "unknown", "static_status": "static-present", "menu": "unknown", "permission": "unknown", "owned_runtime_evidence": [], "service_links": [], "mapper_links": [], "database_links": [], "operation_family": "unknown", "operation_basis": "source-asset-only"}
            if kind == "endpoint":
                member = item.get("member") or "unknown"
                row.update({"capability": member, "symbol": item["symbol"], "http_methods": item["http_methods"], "declared_paths": item["declared_paths"], "route_candidates": item["candidate_routes"], "unresolved_expression": item.get("unresolved_expression", False), "unresolved_inheritance": item.get("unresolved_inheritance", [])})
                row["operation_family"], row["operation_basis"] = operation_for(member)
            elif kind == "template":
                row.update({"capability": item["view_name"], "view_name": item["view_name"], "operation_family": "read", "operation_basis": "template-existence-only"})
            elif kind == "mapper":
                row.update({"capability": item.get("namespace") or "unknown", "namespace": item.get("namespace"), "statements": item.get("statements", []), "table_candidates": item.get("table_candidates", [])})
            else:
                row.update({"capability": item["symbol"], "symbol": item["symbol"], "role": item["role"], "parents": item["parents"]})
            records.append(row)
    scheduled = scheduled_records(units)
    records.extend(scheduled)
    links = page_links(index, units)
    by_symbol = {u["parsed"]["symbol"]: u["parsed"] for u in units if u["parsed"]}
    interface_ids = {r["symbol"]: r["record_id"] for r in records if r["asset_type"] == "interface"}
    for row in records:
        if row["asset_type"] == "endpoint":
            symbols = {row["symbol"]} | {symbol for symbol, parsed in by_symbol.items() if row["symbol"] in parsed["parents"]}
            row["page_candidates"] = [{**link, "basis": link["basis"] + ";declared-or-direct-implementing-controller"} for link in links if link["controller_symbol"] in symbols and link["member"] == row["capability"]]
        elif row["asset_type"] == "mapper" and row.get("namespace") in interface_ids:
            row["interface_candidate"] = {"record_id": interface_ids[row["namespace"]], "basis": "exact-mapper-namespace-and-interface-symbol"}
    grouped = defaultdict(list)
    for row in records:
        owner = row.get("symbol") if row["asset_type"] == "endpoint" else row["resource"]
        grouped[(row["domain"], str(owner), row["operation_family"])].append(row["record_id"])
    groups = [{"group_id": f"candidate.{i:06d}", "domain": key[0], "resource": key[1], "operation_family": key[2], "basis": "source-resource-and-lexical-operation-candidate", "record_ids": ids, "status": "unknown", "menu": "unknown", "permission": "unknown"} for i, (key, ids) in enumerate(sorted(grouped.items()))]
    used_templates = {link["template_index"] for link in links}
    counts = {kind: len(index[source_kind]) for source_kind, kind in KINDS}
    counts.update({"source_file": len(index["source_manifest"]), "scheduled": len(scheduled), "record": len(records), "candidate_group": len(groups), "controller_template_link": len(links)})
    result = {"format": FORMAT, "source_index": "evidence/function-catalog/recovered-source.json", "source_index_sha256": digest(index_path.read_bytes()), "source_commit": index.get("source_commit"), "source_tree_sha256": index["source_tree_sha256"], "source_worktree_dirty": index.get("source_worktree_dirty"), "source_scan_performed": source_root is not None, "counts": counts, "operation_families": list(FAMILIES), "records": records, "candidate_groups": groups, "controller_template_links": links, "integration_candidates": integration_candidates(units), "source_assets": [{**item, "status": "unknown", "static_status": "static-present", "dynamic_status": "unknown"} for item in index["source_manifest"]], "coverage": {"unassigned_endpoint_template_indices": [r["source_index"] for r in records if r["asset_type"] == "endpoint" and not r.get("page_candidates")], "unassigned_template_controller_indices": [i for i in range(len(index["templates"])) if i not in used_templates], "unassigned_endpoint_service_indices": list(range(len(index["endpoints"]))), "unassigned_endpoint_mapper_indices": list(range(len(index["endpoints"]))), "unassigned_mapper_runtime_indices": list(range(len(index["mapper_xml"]))), "unassigned_interface_runtime_indices": list(range(len(index["interfaces"]))), "unassigned_menu_and_permission": "all source records"}, "limitations": ["Candidate groups organize declarations; they do not establish capabilities or runtime reachability.", "Operation and domain labels are lexical candidates; inherited routes, dynamic views and dataflow remain unknown.", "Literal controller/template and mapper/interface links are lexical references, not executed behavior.", "Table tokens and SQL statements remain candidates; no service/table joins are inferred.", "Every public source asset is inventoried; no production configuration, database, private material or network is read.", "Owned-runtime evidence remains in the seed catalog and never promotes these static records."]}
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    return result


generate = build


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--source-index", type=Path, required=True)
    parser.add_argument("--source-root", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    try:
        print(json.dumps(build(args.source_index, args.output, args.source_root)["counts"], sort_keys=True))
    except (OSError, UnicodeError, ValueError, KeyError) as exc:
        parser.exit(1, f"full catalog build failed: {exc}\n")
