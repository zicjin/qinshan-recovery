#!/usr/bin/env python3
"""Collect read-only frontend baseline metadata without credentials.

The production URL is supplied only through ``PRODUCTION_BASE_URL`` at run
time and is never copied into the report.  The supplier target is reached via
the existing ``sss2`` SSH alias.  This report is diagnostic baseline evidence,
not strict behavior evidence.
"""
import argparse
import datetime
import hashlib
import json
import os
import re
import subprocess
import urllib.error
import urllib.request
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))
from evidence_output import guard_output_path


ROOT = Path(__file__).resolve().parents[1]
RESOURCE_RE = re.compile(r"(?:src|href)\s*=\s*[\"']([^\"']+)[\"']", re.I)
ROUTES = (
    "/",
    "/login",
    "/open/security/public-key",
    "/core/module/sys/login",
    "/secure/core/module/sys/menus",
    "/secure/core/module/index/workspace",
    "/secure/emc/module/index/workspace",
)


def digest(body):
    return hashlib.sha256(body).hexdigest()


def local_fetch(base, route):
    request = urllib.request.Request(base.rstrip("/") + route, method="GET")
    try:
        with urllib.request.urlopen(request, timeout=10) as response:
            body = response.read()
            return response.status, body
    except urllib.error.HTTPError as error:
        return error.code, error.read()
    except Exception as error:  # diagnostic collection must retain route failures
        return None, str(type(error).__name__).encode()


def ssh_fetch(host, port, route):
    url = "http://127.0.0.1:%s%s" % (port, route)
    command = [
        "ssh", "-o", "BatchMode=yes", "-o", "ConnectTimeout=10", host,
        "curl", "-ksS", "--max-time", "10", "-o", "-", "-w", "\\\\n__STATUS__%{http_code}\\\\n", url,
    ]
    result = subprocess.run(command, capture_output=True, timeout=30, check=False)
    marker = b"\n__STATUS__"
    if marker not in result.stdout:
        return None, result.stdout or result.stderr
    body, status = result.stdout.rsplit(marker, 1)
    try:
        return int(status.strip()), body
    except ValueError:
        return None, body


def summarize(status, body):
    return {"status": status, "bytes": len(body), "sha256": digest(body)}


def target_report(label, fetch, base_route_resources=True):
    routes = {}
    for route in ROUTES:
        status, body = fetch(route)
        entry = summarize(status, body)
        if route == "/" and body:
            resources = sorted({item for item in RESOURCE_RE.findall(body.decode("utf-8", "replace"))
                                if item.startswith("/static/")})
            entry["initial_resource_urls"] = resources
            entry["initial_resources"] = {}
            for resource in resources:
                resource_status, resource_body = fetch(resource)
                entry["initial_resources"][resource] = summarize(resource_status, resource_body)
        routes[route] = entry
    return {"label": label, "routes": routes}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--supplier-host", default="sss2")
    parser.add_argument("--supplier-port", default="18081")
    parser.add_argument("--owned-port", default="18087")
    args = parser.parse_args()
    production_base = os.environ.get("PRODUCTION_BASE_URL")
    if not production_base:
        raise SystemExit("PRODUCTION_BASE_URL is required at runtime and is not written to the report")
    guard_output_path(args.output)

    supplier = target_report(
        "supplier-test-18081-read-only",
        lambda route: ssh_fetch(args.supplier_host, args.supplier_port, route),
    )
    owned = target_report(
        "owned-debug-%s-diagnostic" % args.owned_port,
        lambda route: ssh_fetch(args.supplier_host, args.owned_port, route),
    )
    production = target_report(
        "production-8081-read-only",
        lambda route: local_fetch(production_base, route),
    )
    supplier_routes = supplier["routes"]
    production_routes = production["routes"]
    root_same = supplier_routes["/"]["sha256"] == production_routes["/"]["sha256"]
    resources = sorted(set(supplier_routes["/"]["initial_resources"]) |
                       set(production_routes["/"]["initial_resources"]))
    resource_same = all(
        supplier_routes["/"]["initial_resources"].get(path) ==
        production_routes["/"]["initial_resources"].get(path)
        for path in resources
    )
    payload = {
        "schema": 1,
        "status": "diagnostic-pass" if root_same and resource_same else "diagnostic-diff",
        "mode": "read-only-baseline",
        "production_read_only": True,
        "credentials_used": False,
        "source_commit": subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip(),
        "source_dirty": bool(subprocess.check_output(
            ["git", "status", "--porcelain", "--", "project/src", "owned-framework/src", "owned-web/src", "scripts/frontend-baseline-readonly.py"],
            cwd=ROOT, text=True).strip()),
        "observed_at": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "targets": {"supplier": supplier, "production": production},
        "owned_debug_target": owned,
        "comparison": {
            "root_document_same": root_same,
            "initial_resource_urls_same": (
                supplier_routes["/"]["initial_resource_urls"] ==
                production_routes["/"]["initial_resource_urls"]
            ),
            "initial_resource_responses_same": resource_same,
            "owned_root_document_same_as_supplier": (
                owned["routes"]["/"]["sha256"] == supplier_routes["/"]["sha256"]
            ),
            "owned_initial_resource_urls_same_as_supplier": (
                owned["routes"]["/"]["initial_resource_urls"] ==
                supplier_routes["/"]["initial_resource_urls"]
            ),
        },
        "workspace_menu": {
            "authenticated_observation": False,
            "reason": "no credentials supplied; unauthenticated route responses are retained per target",
            "supplier_menu": supplier_routes["/secure/core/module/sys/menus"],
            "production_menu": production_routes["/secure/core/module/sys/menus"],
            "supplier_workspace": supplier_routes["/secure/core/module/index/workspace"],
            "production_workspace": production_routes["/secure/core/module/index/workspace"],
        },
        "limitations": [
            "read-only anonymous baseline only",
            "authenticated workspace/menu and post-login resource loading remain open",
            "this report is diagnostic and must not be registered as strict matrix evidence",
        ],
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n")
    print("frontend read-only baseline: %s (%s)" % (payload["status"], args.output))


if __name__ == "__main__":
    main()
