#!/usr/bin/env python3
"""Collect a credential-backed, read-only sss2 frontend diagnostic.

The password is read from a private 0600 file and sent only over the existing
SSH connection to sss2.  The report keeps route status, sizes, hashes and
resource URLs; it never stores credentials, cookies or response bodies.  This
is diagnostic evidence, not a strict production comparison.
"""
import argparse
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
import shlex
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path

PAGE_ROUTES = (
    "/secure/emc/module/mdm/emp/emps/page/edit-list?readonly=1",
    "/secure/emc/module/mdm/equipt/equipts/page/edit-list?readonly=1",
    "/secure/emc/module/item/consume/consumes/page/edit-list?readonly=1",
    "/secure/emc/module/bp/reagents/page/edit-list",
    "/secure/emc/module/index/workspace/download-attachment",
)
API_ROUTES = ("/secure/emc/module/bp/reagents/queries/raw",)

REMOTE = r'''import base64
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile

user = os.environ["AUTH_USER"]
port = os.environ["AUTH_PORT"]
password = os.environ["AUTH_PASSWORD"]
base = "http://127.0.0.1:" + port
root = Path(tempfile.mkdtemp(prefix="frontend-auth-baseline-"))
root.chmod(0o700)
PAGE_ROUTES = (
    "/secure/emc/module/mdm/emp/emps/page/edit-list?readonly=1",
    "/secure/emc/module/mdm/equipt/equipts/page/edit-list?readonly=1",
    "/secure/emc/module/item/consume/consumes/page/edit-list?readonly=1",
    "/secure/emc/module/bp/reagents/page/edit-list",
    "/secure/emc/module/index/workspace/download-attachment",
)
API_ROUTES = ("/secure/emc/module/bp/reagents/queries/raw",)

def digest(data):
    return hashlib.sha256(data).hexdigest()

def tree_summary(data):
    try:
        parsed = json.loads(data.decode("utf-8"))
        roots = parsed if isinstance(parsed, list) else parsed.get("menus", [])
        if not isinstance(roots, list):
            return {"json": True, "node_count": 0, "root_count": 0, "max_depth": 0, "keys": [], "ids_sha256": None}
        nodes = []
        keys = set()
        ext_keys = set()
        ext_types = set()
        stack = [(item, 1) for item in roots]
        while stack:
            item, depth = stack.pop()
            if not isinstance(item, dict):
                continue
            nodes.append(item)
            keys.update(str(key) for key in item.keys())
            extension = item.get("ext$")
            if isinstance(extension, dict):
                ext_keys.update(str(key) for key in extension.keys())
            elif extension is not None:
                ext_types.add(type(extension).__name__)
            children = item.get("children")
            if isinstance(children, list):
                stack.extend((child, depth + 1) for child in children)
        ids = sorted(str(item.get("id")) for item in nodes if item.get("id") is not None)
        return {
            "json": True,
            "node_count": len(nodes),
            "root_count": len(roots),
            "max_depth": _tree_depth(roots),
            "keys": sorted(keys),
            "ext_keys": sorted(ext_keys),
            "ext_non_object_types": sorted(ext_types),
            "ids_sha256": digest("\n".join(ids).encode("utf-8")),
        }
    except Exception:
        return {"json": False, "node_count": 0, "root_count": 0, "max_depth": 0, "keys": [], "ext_keys": [], "ext_non_object_types": [], "ids_sha256": None}

def _tree_depth(nodes, depth=1):
    if not isinstance(nodes, list) or not nodes:
        return 0
    deepest = depth
    for item in nodes:
        if isinstance(item, dict) and isinstance(item.get("children"), list) and item.get("children"):
            deepest = max(deepest, _tree_depth(item["children"], depth + 1))
    return deepest

def curl(path, *, follow=False, method="GET", data=None, raw=None, extra_headers=()):
    body = root / ("body-" + hashlib.sha256(path.encode()).hexdigest()[:12])
    headers = root / ("headers-" + hashlib.sha256((path + method).encode()).hexdigest()[:12])
    command = ["curl", "-ksS", "--max-time", "20", "-D", str(headers), "-o", str(body),
               "-w", "%{http_code}", "-b", str(root / "cookies"), "-c", str(root / "cookies")]
    if follow:
        command.append("-L")
    if method != "GET":
        command.extend(["-X", method])
    for header in extra_headers:
        command.extend(["-H", header])
    if data:
        for key, value in data:
            command.extend(["--data-urlencode", key + "=" + value])
    if raw is not None:
        command.extend(["--data", raw])
    command.append(base + path)
    result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                            universal_newlines=True, check=False)
    try:
        status = int(result.stdout.strip() or "0")
    except ValueError:
        status = 0
    content = body.read_bytes() if body.exists() else b""
    header_text = headers.read_text(errors="replace") if headers.exists() else ""
    locations = [line.split(":", 1)[1].strip() for line in header_text.splitlines()
                 if line.lower().startswith("location:")]
    return {
        "status": status,
        "bytes": len(content),
        "sha256": digest(content),
        "locations": locations,
        "transport_error": result.stderr[-300:] if status == 0 and result.stderr else None,
        "body": content,
    }

try:
    key = curl("/open/security/public-key")["body"].decode("ascii", "ignore").strip()
    (root / "key.der").write_bytes(base64.b64decode(key))
    subprocess.run(["openssl", "rsa", "-pubin", "-inform", "DER", "-in", str(root / "key.der"),
                    "-out", str(root / "key.pem")], check=True,
                    stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    encrypted = subprocess.run(
        ["openssl", "pkeyutl", "-encrypt", "-pubin", "-inkey", str(root / "key.pem"),
         "-pkeyopt", "rsa_padding_mode:pkcs1"],
        input=password.encode(), stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True,
    ).stdout
    encoded = base64.b64encode(encrypted).decode()
    login = curl("/core/module/sys/login", method="POST", data=[("id", user), ("password", encoded)],
                 extra_headers=["SECURITY-RSA-KEY: " + key, "Content-Type: application/x-www-form-urlencoded"])
    root_page = curl("/", follow=True)
    resources = sorted(set(re.findall(r"(?:src|href)\s*=\s*[\"']([^\"']+)[\"']",
                                      root_page["body"].decode("utf-8", "replace"), flags=re.I)))
    resources = [item for item in resources if item.startswith("/static/")]
    resource_results = {}
    for resource in resources:
        observed = curl(resource)
        resource_results[resource] = {key: observed[key] for key in ("status", "bytes", "sha256")}
    routes = {"/module/index/workspaces": curl("/module/index/workspaces"),
              "/secure/core/module/sys/menus/tree/root": curl("/secure/core/module/sys/menus/tree/root", method="POST"),
              "/secure/core/module/sys/menus": curl("/secure/core/module/sys/menus"),
              "/secure/core/module/index/workspace": curl("/secure/core/module/index/workspace"),
              "/secure/emc/module/index/workspace": curl("/secure/emc/module/index/workspace")}
    page_routes = {}
    for path in PAGE_ROUTES:
        observed = curl(path)
        page_resources = sorted(set(re.findall(
            r"(?:src|href)\s*=\s*[\"']([^\"']+)[\"']",
            observed["body"].decode("utf-8", "replace"), flags=re.I)))
        page_resources = [item for item in page_resources if item.startswith("/static/")]
        page_routes[path] = {
            "status": observed["status"],
            "bytes": observed["bytes"],
            "sha256": observed["sha256"],
            "locations": observed["locations"],
            "resource_urls": page_resources,
        }
    api_routes = {}
    for path in API_ROUTES:
        observed = curl(path, method="POST", raw="{}", extra_headers=["Content-Type: application/json"])
        summary = {"json": False, "keys": [], "total": None, "row_count": None}
        try:
            parsed = json.loads(observed["body"].decode("utf-8"))
            if isinstance(parsed, dict):
                rows = parsed.get("rows")
                summary = {"json": True, "keys": sorted(parsed.keys()), "total": parsed.get("total"),
                           "row_count": len(rows) if isinstance(rows, list) else None,
                           "first": parsed.get("first"), "last": parsed.get("last"),
                           "numberOfElements": parsed.get("numberOfElements"),
                           "pageNumber": parsed.get("pageNumber"), "pageSize": parsed.get("pageSize"),
                           "totalPages": parsed.get("totalPages")}
        except Exception:
            pass
        api_routes[path] = {"status": observed["status"], "bytes": observed["bytes"],
                            "sha256": observed["sha256"], "locations": observed["locations"],
                            "transport_error": observed.get("transport_error"),
                            "summary": summary}
    login_identity = {"json": False, "authenticated": False, "identity_fields": []}
    try:
        parsed_login = json.loads(login["body"].decode("utf-8"))
        identity = parsed_login.get("identity") if isinstance(parsed_login, dict) else None
        login_identity = {
            "json": isinstance(parsed_login, dict),
            "authenticated": parsed_login.get("authenticated") is True if isinstance(parsed_login, dict) else False,
            "identity_fields": sorted(identity.keys()) if isinstance(identity, dict) else [],
        }
    except Exception:
        pass
    menu = routes["/secure/core/module/sys/menus"]["body"]
    menu_tree = routes["/secure/core/module/sys/menus/tree/root"]["body"]
    menu_summary = {"json": False}
    try:
        parsed = json.loads(menu.decode("utf-8"))
        menu_items = parsed.get("menus") if isinstance(parsed, dict) else None
        menu_summary = {
            "json": True,
            "keys": sorted(parsed.keys()) if isinstance(parsed, dict) else [],
            "menu_count": len(menu_items) if isinstance(menu_items, list) else None,
            "menu_ids": sorted(str(item.get("id")) for item in menu_items
                                if isinstance(item, dict) and item.get("id") is not None)
                         if isinstance(menu_items, list) else [],
        }
    except Exception:
        pass
    payload = {
        "port": int(port),
        "login": {key: login[key] for key in ("status", "bytes", "sha256", "locations")},
        "login_identity": login_identity,
        "root": {key: root_page[key] for key in ("status", "bytes", "sha256", "locations")},
        "initial_resource_urls": resources,
        "initial_resources": resource_results,
        "routes": {path: {key: observed[key] for key in ("status", "bytes", "sha256", "locations")}
                   for path, observed in routes.items()},
        "page_routes": page_routes,
        "api_routes": api_routes,
        "menu_summary": menu_summary,
        "menu_tree_summary": tree_summary(menu_tree),
    }
    print(json.dumps(payload, ensure_ascii=False, sort_keys=True))
finally:
    shutil.rmtree(root, ignore_errors=True)
'''


def read_password(path):
    password_file = Path(path)
    if not password_file.is_file() or password_file.stat().st_mode & 0o077:
        raise SystemExit("password file must be a regular 0600 file")
    values = {}
    for line in password_file.read_text(encoding="utf-8").splitlines():
        if line.startswith("TEST_PASSWORD="):
            values["password"] = line.split("=", 1)[1]
    if not values.get("password"):
        raise SystemExit("password file has no TEST_PASSWORD entry")
    return values["password"]


def collect(host, port, username, password):
    remote = "export AUTH_USER=%s AUTH_PORT=%s AUTH_PASSWORD=%s\npython3 - <<'PY'\n%s\nPY\n" % (
        shlex.quote(username), shlex.quote(str(port)), shlex.quote(password), REMOTE)
    result = subprocess.run(
        ["ssh", "-o", "BatchMode=yes", "-o", "ConnectTimeout=10", host,
         "bash", "-s"], input=remote, text=True, capture_output=True, timeout=90,
        check=False,
    )
    if result.returncode:
        raise RuntimeError("ssh diagnostic failed: " + result.stderr[-1000:])
    lines = [line for line in result.stdout.splitlines() if line.strip()]
    if not lines:
        raise RuntimeError("authenticated diagnostic returned no report")
    return json.loads(lines[-1])


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--password-file", type=Path, required=True)
    parser.add_argument("--username", required=True)
    parser.add_argument("--supplier-host", default="sss2")
    parser.add_argument("--supplier-port", type=int, default=18081)
    parser.add_argument("--owned-port", type=int, default=18088)
    args = parser.parse_args()
    guard_output_path(args.output)
    password = read_password(args.password_file)
    reports = {
        "supplier": collect(args.supplier_host, args.supplier_port, args.username, password),
        "owned": collect(args.supplier_host, args.owned_port, args.username, password),
    }
    source_commit = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
    dirty = bool(subprocess.check_output(
        ["git", "status", "--porcelain", "--", "project/src", "owned-framework/src",
         "owned-web/src", "scripts/frontend-authenticated-baseline-sss2.py"],
        cwd=ROOT, text=True,
    ).strip())
    payload = {
        "schema": 1,
        "status": "diagnostic-pass",
        "mode": "sss2-authenticated-read-only",
        "production_access": False,
        "credentials_used": True,
        "credential_material_stored": False,
        "source_commit": source_commit,
        "source_dirty": dirty,
        "observed_at": dt.datetime.now(dt.timezone.utc).isoformat(),
        "account_label": "permitted-test-account",
        "targets": reports,
        "comparison": {
            "login_status_same": reports["supplier"]["login"]["status"] == reports["owned"]["login"]["status"],
            "root_status_same": reports["supplier"]["root"]["status"] == reports["owned"]["root"]["status"],
            "menu_status_same": reports["supplier"]["routes"]["/secure/core/module/sys/menus"]["status"] == reports["owned"]["routes"]["/secure/core/module/sys/menus"]["status"],
            "menu_tree_status_same": reports["supplier"]["routes"]["/secure/core/module/sys/menus/tree/root"]["status"] == reports["owned"]["routes"]["/secure/core/module/sys/menus/tree/root"]["status"],
            "workspace_status_same": reports["supplier"]["routes"]["/secure/core/module/index/workspace"]["status"] == reports["owned"]["routes"]["/secure/core/module/index/workspace"]["status"],
            "workspace_entry_status_same": reports["supplier"]["routes"]["/module/index/workspaces"]["status"] == reports["owned"]["routes"]["/module/index/workspaces"]["status"],
            "page_route_statuses_same": all(
                reports["supplier"]["page_routes"][path]["status"]
                == reports["owned"]["page_routes"][path]["status"]
                for path in PAGE_ROUTES
            ),
            "page_route_statuses": {
                path: {
                    "supplier": reports["supplier"]["page_routes"][path]["status"],
                    "owned": reports["owned"]["page_routes"][path]["status"],
                }
                for path in PAGE_ROUTES
            },
            "reagent_query_statuses_same": reports["supplier"]["api_routes"][API_ROUTES[0]]["status"]
            == reports["owned"]["api_routes"][API_ROUTES[0]]["status"],
        },
        "limitations": [
            "Both targets are sss2 test instances; production authentication was not attempted.",
            "This is diagnostic evidence and is not a strict matrix pass.",
            "The report stores hashes, statuses, resource URLs and menu IDs only; cookies and response bodies are discarded.",
        ],
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n")
    print("authenticated sss2 frontend baseline: diagnostic-pass (%s)" % args.output)


if __name__ == "__main__":
    main()
