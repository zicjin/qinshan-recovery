#!/usr/bin/env python3
"""Prepare and (only with an explicit gate) run an owned distribution on an sss2 clone.

The default operation is read-only: ``plan`` inspects a built distribution and
writes a redacted deployment plan.  ``run`` is intentionally guarded by both
``SSS2_WRITABLE_CLONE=1`` and ``OWNED_SSS2_WRITE_MODE=isolated``.  It creates a
parallel container with a loopback-only host port and never replaces the
existing sss2 application container.
"""
from __future__ import annotations

import argparse
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile
import time
from typing import Any, Dict, Iterable, List
import urllib.error
import urllib.request


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_JDK = ROOT / "build" / "jdk1.8.0_391"
ARTIFACT_ROOT = Path(os.environ.get("LIMS_EXTERNAL_ARTIFACT_ROOT", ROOT / ".external-materials"))
JDK_ARCHIVE = Path(os.environ.get("JDK_ARCHIVE", ARTIFACT_ROOT / "jdk-8u391-linux-x64.tar.gz"))
FORBIDDEN = re.compile(
    r"(?i)(sunway-app\.so|sunway-app\.dll|vendor\.lic|license\.lic|"
    r"license-machine|mac_address|system_mac_address_list|gikam-core-)"
)
PRODUCTION = re.compile(r"(?i)(production|prod|10\.100\.|218\.93\.|192\.168\.)")
FORBIDDEN_CONTAINER_NAMES = {"lims-replay-demo-s1-app", "lims-app", "sss2-app"}
IMAGE = "debian:bullseye-slim"


def fail(message: str) -> None:
    raise SystemExit("sss2-owned-deploy: FAIL: " + message)


def run(command: Iterable[str], *, check: bool = True, timeout: int = 120,
        capture: bool = True) -> subprocess.CompletedProcess[str]:
    result = subprocess.run(
        [str(value) for value in command],
        cwd=ROOT,
        text=True,
        capture_output=capture,
        timeout=timeout,
    )
    if check and result.returncode:
        detail = ((result.stdout or "") + (result.stderr or ""))[-4000:]
        fail("command failed: %s\n%s" % (" ".join(map(str, command)), detail))
    return result


def digest(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def source_state() -> tuple[str, bool]:
    commit = run(["git", "rev-parse", "HEAD"]).stdout.strip()
    dirty = bool(run([
        "git", "status", "--porcelain", "--untracked-files=all", "--",
        "owned-framework/src", "owned-personnel/src", "owned-equipment/src",
        "owned-consumables/src", "owned-attachments/src", "owned-report/src",
        "owned-scheduler/src", "owned-web/src", "owned-distribution",
        "scripts/sss2-owned-deploy.py",
    ]).stdout.strip())
    return commit, dirty


def scan_distribution(distribution: Path) -> Dict[str, Any]:
    distribution = distribution.resolve()
    required = [
        distribution / "start.sh",
        distribution / "manifest.json",
        distribution / "app" / "lims-owned-application.jar",
    ]
    for path in required:
        if not path.is_file():
            fail("distribution is missing " + str(path))
    hits: List[str] = []
    files: List[Dict[str, str]] = []
    for path in sorted(p for p in distribution.rglob("*") if p.is_file()):
        relative = str(path.relative_to(distribution))
        files.append({"path": relative, "sha256": digest(path)})
        try:
            data = path.read_bytes()
        except OSError as error:
            fail("cannot read distribution file %s: %s" % (relative, error))
        text = data.decode("utf-8", errors="ignore")
        for match in FORBIDDEN.finditer(relative + "\n" + text):
            hits.append(relative + ":" + match.group(0))
    if hits:
        fail("forbidden supplier/runtime marker in distribution: " + ", ".join(hits[:8]))
    manifest = json.loads((distribution / "manifest.json").read_text(encoding="utf-8"))
    if manifest.get("vendor_runtime") is True:
        fail("manifest claims vendor_runtime=true")
    return {
        "path": str(distribution),
        "manifest_sha256": digest(distribution / "manifest.json"),
        "application_sha256": digest(distribution / "app" / "lims-owned-application.jar"),
        "file_count": len(files),
        "files": files,
        "supplier_runtime": False,
        "sunway_app_so": False,
        "license_input": False,
    }


def ensure_private_file(path: Path) -> None:
    if not path.is_file() or path.is_symlink():
        fail("configuration must be a regular private file: " + str(path))
    if path.stat().st_mode & 0o077:
        fail("configuration must have mode 0600: " + str(path))
    raw = path.read_text(encoding="utf-8", errors="strict")
    if FORBIDDEN.search(raw):
        fail("configuration contains a supplier/native/license marker")
    if PRODUCTION.search(raw):
        fail("configuration contains a production marker")
    if "owned.sss2.write.mode=isolated" not in raw:
        fail("configuration must contain owned.sss2.write.mode=isolated")


def plan(args: argparse.Namespace) -> int:
    distribution = Path(args.distribution)
    package = scan_distribution(distribution)
    commit, dirty = source_state()
    payload = {
        "schema": 1,
        "status": "prepared",
        "execution": "not-executed",
        "production_access": False,
        "scope": "parallel supplier-free owned distribution on an authorized disposable sss2 clone",
        "created_at": dt.datetime.now(dt.timezone.utc).isoformat(),
        "source_commit": commit,
        "source_dirty": dirty,
        "distribution": package,
        "network": args.network,
        "container_name": args.container_name,
        "host_bind": "127.0.0.1:ephemeral",
        "replacement": False,
        "required_gates": [
            "SSS2_WRITABLE_CLONE=1",
            "OWNED_SSS2_WRITE_MODE=isolated",
            "existing sss2 application is not stopped or replaced",
            "network is Docker internal=true",
            "configuration file is mode 0600 and has no forbidden markers",
        ],
        "cleanup": {
            "application_stopped": False,
            "container_removed": False,
            "image_removed": False,
            "network_removed": False,
            "temporary_files_removed": False,
        },
        "limitations": [
            "Plan creation does not connect to sss2 or mutate Docker state.",
            "A passing plan is not deployment or API replay evidence.",
        ],
    }
    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("sss2-owned-deploy: prepared plan " + str(output))
    return 0


def docker_network_is_internal(network: str) -> bool:
    result = run(["docker", "network", "inspect", network], timeout=30)
    try:
        data = json.loads(result.stdout)[0]
    except (ValueError, IndexError, TypeError) as error:
        fail("cannot parse Docker network inspect: " + str(error))
    if data.get("Internal") is not True:
        fail("target network is not internal=true")
    return True


def run_deployment(args: argparse.Namespace) -> int:
    if os.environ.get("SSS2_WRITABLE_CLONE") != "1":
        fail("run requires SSS2_WRITABLE_CLONE=1")
    if os.environ.get("OWNED_SSS2_WRITE_MODE") != "isolated":
        fail("run requires OWNED_SSS2_WRITE_MODE=isolated")
    name = args.container_name
    if name in FORBIDDEN_CONTAINER_NAMES or PRODUCTION.search(name):
        fail("refusing to use a production/current application container name")
    if not re.fullmatch(r"[a-z][a-z0-9_.-]{2,62}", name):
        fail("invalid container name")
    distribution = Path(args.distribution).resolve()
    package = scan_distribution(distribution)
    config = Path(args.config).resolve()
    ensure_private_file(config)
    docker_network_is_internal(args.network)
    if not JDK_ARCHIVE.is_file():
        fail("pinned Java 8 archive is missing: " + str(JDK_ARCHIVE))
    workspace = Path(tempfile.mkdtemp(prefix="sss2-owned-image-", dir="/tmp"))
    image = "lims-owned-sss2-" + name
    container_id = None
    try:
        image_dist = workspace / "distribution"
        shutil.copytree(distribution, image_dist)
        shutil.copy2(JDK_ARCHIVE, workspace / "jdk.tgz")
        dockerfile = workspace / "Dockerfile"
        dockerfile.write_text(
            "FROM %s\n"
            "COPY jdk.tgz /tmp/jdk.tgz\n"
            "RUN mkdir -p /opt && tar -xzf /tmp/jdk.tgz -C /opt && rm /tmp/jdk.tgz\n"
            "ENV JAVA_HOME=/opt/jdk1.8.0_391\n"
            "COPY distribution /opt/owned\n"
            "RUN chmod 0755 /opt/owned/start.sh\n"
            "ENTRYPOINT [\"/opt/owned/start.sh\", \"/run/config/application.properties\"]\n"
            % IMAGE,
            encoding="utf-8",
        )
        run(["docker", "build", "--pull=false", "--tag", image, str(workspace)], timeout=1800)
        create = run([
            "docker", "create", "--name", name, "--network", args.network,
            "--publish", "127.0.0.1::8080", "--env", "LIMS_BIND_ADDRESS=0.0.0.0",
            "--volume", str(config) + ":/run/config/application.properties:ro",
            image,
        ], timeout=60)
        container_id = create.stdout.strip()
        run(["docker", "start", name], timeout=60)
        inspect = json.loads(run(["docker", "inspect", name], timeout=30).stdout)[0]
        bindings = inspect.get("NetworkSettings", {}).get("Ports", {}).get("8080/tcp", [])
        if not bindings or bindings[0].get("HostIp") != "127.0.0.1":
            fail("owned container did not receive a loopback-only port")
        host_port = bindings[0]["HostPort"]
        payload = {
            "schema": 1,
            "status": "started",
            "execution": "executed-unverified",
            "production_access": False,
            "source_commit": source_state()[0],
            "distribution": package,
            "network": args.network,
            "container_name": name,
            "container_id": container_id,
            "loopback_url": "http://127.0.0.1:%s" % host_port,
            "image": image,
            "cleanup_required": True,
            "warning": "API replay and cleanup are still required before executed-pass.",
        }
        Path(args.output).write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print("sss2-owned-deploy: started parallel container " + name)
        return 0
    except BaseException:
        if container_id:
            run(["docker", "rm", "--force", name], check=False, timeout=60)
        run(["docker", "image", "rm", "--force", image], check=False, timeout=60)
        raise
    finally:
        shutil.rmtree(workspace, ignore_errors=True)


def http_status(url: str, timeout: float = 3.0) -> tuple[int, str]:
    request = urllib.request.Request(url, headers={"Cache-Control": "no-store"})
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            return response.status, response.read(4096).decode("utf-8", errors="replace")
    except urllib.error.HTTPError as error:
        body = error.read(4096).decode("utf-8", errors="replace") if error.fp else ""
        return error.code, body


def probe(args: argparse.Namespace) -> int:
    report_path = Path(args.report)
    if not report_path.is_file():
        fail("missing deployment report: " + str(report_path))
    report = json.loads(report_path.read_text(encoding="utf-8"))
    if report.get("status") != "started" or report.get("execution") != "executed-unverified":
        fail("deployment report is not an unverified started run")
    base = report.get("loopback_url")
    name = report.get("container_name")
    if not isinstance(base, str) or not base.startswith("http://127.0.0.1:"):
        fail("deployment report lacks a loopback URL")
    if not isinstance(name, str):
        fail("deployment report lacks a container name")
    cases = {
        "/": 200,
        "/open/security/public-key": 200,
        "/core/module/sys/licenses": 404,
        "/core/module/sys/licenses/number-of-online-users": 404,
        "/open/security/licenses": 404,
    }
    observations: List[Dict[str, Any]] = []
    deadline = time.monotonic() + args.timeout
    while True:
        observations = []
        all_ready = True
        for path, expected in cases.items():
            actual, body = http_status(base + path)
            observations.append({
                "path": path,
                "expected_status": expected,
                "status": actual,
                "body_bytes": len(body.encode("utf-8")),
            })
            if actual != expected:
                all_ready = False
        if all_ready or time.monotonic() >= deadline:
            break
        time.sleep(1)
    if not all_ready:
        report.update({"status": "executed-fail", "execution": "executed-fail",
                       "http_cases": observations,
                       "reason": "owned loopback smoke did not reach expected statuses"})
        report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        fail("loopback smoke failed; container retained for diagnostics")

    inspect = json.loads(run(["docker", "inspect", name], timeout=30).stdout)[0]
    mounts = inspect.get("Mounts", [])
    if any(mount.get("Destination") != "/run/config/application.properties" or
           mount.get("RW") is not False for mount in mounts):
        fail("container has an unexpected or writable mount")
    marker_probe = run([
        "docker", "exec", name, "sh", "-c",
        "set -eu; cat /proc/1/cmdline /proc/1/environ /proc/1/maps; "
        "find /opt/owned -type f -print",
    ], check=False, timeout=30)
    marker_text = (marker_probe.stdout or "") + (marker_probe.stderr or "")
    if FORBIDDEN.search(marker_text):
        fail("running owned container exposes a forbidden supplier/native marker")
    report.update({
        "status": "executed-pass",
        "execution": "executed-pass",
        "http_cases": observations,
        "runtime_boundary": {
            "supplier_runtime": False,
            "sunway_app_so": False,
            "license_input": False,
            "forbidden_marker_scan": True,
            "mounts_read_only": True,
        },
        "limitations": [
            "This is a loopback bootstrap/license-boundary smoke only.",
            "It does not prove authenticated business API parity or sss2 data writes.",
        ],
    })
    report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("sss2-owned-deploy: smoke replay PASS")
    return 0


def cleanup(args: argparse.Namespace) -> int:
    if os.environ.get("SSS2_WRITABLE_CLONE") != "1":
        fail("cleanup requires SSS2_WRITABLE_CLONE=1")
    if args.container_name in FORBIDDEN_CONTAINER_NAMES:
        fail("refusing to remove current application container")
    removed = run(["docker", "rm", "--force", args.container_name], check=False, timeout=60).returncode == 0
    if args.image:
        image_removed = run(["docker", "image", "rm", "--force", args.image], check=False, timeout=60).returncode == 0
    else:
        image_removed = True
    if args.report:
        report_path = Path(args.report)
        if report_path.is_file():
            report = json.loads(report_path.read_text(encoding="utf-8"))
            report["cleanup"] = {
                "application_stopped": removed,
                "container_removed": removed,
                "image_removed": image_removed,
                "network_removed": False,
                "temporary_files_removed": True,
            }
            report["cleanup_verified_at"] = dt.datetime.now(dt.timezone.utc).isoformat()
            report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("sss2-owned-deploy: cleanup requested for " + args.container_name)
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    p = sub.add_parser("plan")
    p.add_argument("--distribution", required=True)
    p.add_argument("--output", required=True)
    p.add_argument("--network", default="sss2-writable-clone-net")
    p.add_argument("--container-name", default="lims-owned-sss2-clone")
    p.set_defaults(func=plan)
    p = sub.add_parser("run")
    p.add_argument("--distribution", required=True)
    p.add_argument("--config", required=True)
    p.add_argument("--network", required=True)
    p.add_argument("--container-name", required=True)
    p.add_argument("--output", required=True)
    p.set_defaults(func=run_deployment)
    p = sub.add_parser("probe")
    p.add_argument("--report", required=True)
    p.add_argument("--timeout", type=int, default=90)
    p.set_defaults(func=probe)
    p = sub.add_parser("cleanup")
    p.add_argument("--container-name", required=True)
    p.add_argument("--image")
    p.add_argument("--report")
    p.set_defaults(func=cleanup)
    args = parser.parse_args()
    return args.func(args)


if __name__ == "__main__":
    raise SystemExit(main())
