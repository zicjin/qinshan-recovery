#!/usr/bin/env python3
"""Execute one API wave on one shared strict runtime.

The route execution plan deliberately has small case groups for affected-runner
selection, while a wave is the lifecycle boundary. This orchestrator starts
one clone-derived runtime for a wave, runs one or more explicit HTTP contract
jobs against it, performs one final cleanup, and only then finalizes reports so
the closure tool can promote them. Project routes and owned Java source-only
routes use the same Spring application; a boundary route never creates a
boundary process, microservice, JNI daemon, or internal HTTP bridge. The
generated skeleton is planning-only; this command accepts only concrete
contract documents. Reports are written to the feature diagnostic pending
area; use ``manage-evidence.py promote`` after closure validation to publish a
canonical report.
"""
from __future__ import annotations

import argparse
import datetime as dt
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import re
import socket
import subprocess
import sys
from typing import Any, Dict, Iterable, List, Mapping, Sequence
from urllib.parse import urlsplit
sys.path.insert(0, str(Path(__file__).resolve().parent))
from evidence_output import CANONICAL_ROOT, DIAGNOSTIC_ROOT, cleanup_contract_complete, current_implementation_binding, pending_path
from source_binding import (
    REPO_ROOT as DISCOVERED_REPO_ROOT,
    feature_root,
    resolve_workspace_path,
)

ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = DISCOVERED_REPO_ROOT
FEATURE_ROOT = feature_root(REPO_ROOT)
WAVE_ORDER = ("foundation", "personnel", "equipment", "consumables-materials", "secondary")
REQUIRED_CLEANUP = (
    "application_stopped", "oracle_stopped", "redis_stopped",
    "network_removed", "volumes_removed", "temporary_files_removed",
)
LIFECYCLE_ID = re.compile(r"^[A-Za-z0-9][A-Za-z0-9._-]{0,95}$")
MILESTONE_ROOT = DIAGNOSTIC_ROOT / "pending" / "milestone-lifecycle"


def now() -> str:
    return dt.datetime.now(dt.timezone.utc).isoformat()


def _module(path: Path, name: str):
    spec = importlib.util.spec_from_file_location(name, path)
    if not spec or not spec.loader:
        raise RuntimeError("cannot load " + str(path))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def _load_json(path: Path) -> Dict[str, Any]:
    value = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(value, dict):
        raise ValueError("JSON document must be an object: " + str(path))
    return value


def _repo_path(value: str) -> Path:
    return resolve_workspace_path(value, REPO_ROOT)


def _digest_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def _begin_milestone(document: Mapping[str, Any], plan_path: Path) -> tuple[Path, Path, Dict[str, Any]]:
    lifecycle_id = str(document.get("lifecycle_id", ""))
    if not LIFECYCLE_ID.fullmatch(lifecycle_id):
        raise ValueError("milestone lifecycle_id must be a safe stable identifier")
    root = MILESTONE_ROOT
    root.mkdir(parents=True, exist_ok=True)
    lock = root / (lifecycle_id + ".lock")
    journal = root / (lifecycle_id + ".json")
    try:
        with lock.open("x", encoding="utf-8") as stream:
            stream.write(now() + "\n")
    except FileExistsError as error:
        raise ValueError("milestone lifecycle is already running or was interrupted: " + lifecycle_id) from error
    payload = {
        "schema": 1,
        "lifecycle_id": lifecycle_id,
        "status": "running",
        "started_at": now(),
        "manifest_path": str(document.get("manifest_path", "")),
        "manifest_sha256": hashlib.sha256(json.dumps(document, ensure_ascii=False, sort_keys=True).encode("utf-8")).hexdigest(),
        "plan_path": str(plan_path),
        "plan_sha256": _digest_file(plan_path),
        "source_commit": document.get("source_commit"),
        "repository_head_at_capture": document.get("repository_head_at_capture", document.get("source_commit")),
        "implementation_binding": document.get("implementation_binding"),
        "implementation_tree_digest": document.get("implementation_tree_digest"),
        "implementation_dirty": document.get("implementation_dirty"),
        "implementation_scope": document.get("implementation_scope", []),
        "waves": [str(item.get("wave")) for item in document.get("waves", []) if isinstance(item, Mapping)],
        "completed_waves": [],
        "reports": [],
    }
    journal.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    journal.chmod(0o600)
    return lock, journal, payload


def _update_milestone(journal: Path, payload: Dict[str, Any], **updates: Any) -> None:
    payload.update(updates)
    journal.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    journal.chmod(0o600)


def _wave_for_family(family: str) -> str:
    if family == "core" or family.startswith("core-"):
        return "foundation"
    if family in {"personnel", "equipment", "consumables-materials"}:
        return family
    return "secondary"


def _validate_plan(plan: Mapping[str, Any]) -> None:
    binding = current_implementation_binding()
    if plan.get("implementation_tree_digest"):
        if plan.get("implementation_tree_digest") != binding["implementation_tree_digest"]:
            raise ValueError("execution plan implementation binding must match current implementation")
        if plan.get("implementation_binding") != binding["binding_id"]:
            raise ValueError("execution plan implementation_binding must match current implementation")
        if plan.get("implementation_dirty") is not False or binding["implementation_dirty"]:
            raise ValueError("execution plan implementation scope must be clean")
    else:
        head = subprocess.check_output(["git", "-C", str(REPO_ROOT), "rev-parse", "HEAD"], text=True).strip()
        if plan.get("source_commit") != head or plan.get("source_dirty") is not False:
            raise ValueError("legacy execution plan source binding is stale or dirty")
    if plan.get("wave_order") != list(WAVE_ORDER):
        raise ValueError("execution plan wave_order is invalid")
    if plan.get("runtime_count") != len(WAVE_ORDER):
        raise ValueError("execution plan must expose five wave runtimes")
    if sum(plan.get("wave_counts", {}).values()) != plan.get("route_count"):
        raise ValueError("execution plan wave counts do not cover every route")
    model = plan.get("route_execution_model")
    if not isinstance(model, Mapping):
        raise ValueError("execution plan lacks route_execution_model")
    if model.get("boundary_process") is not False or model.get("internal_http_bridge") is not False:
        raise ValueError("boundary routes must share the Spring application; no boundary process/bridge")


def _route_waves(plan: Mapping[str, Any]) -> Dict[str, str]:
    result: Dict[str, str] = {}
    for group in plan.get("groups", []):
        wave = group.get("wave")
        if wave not in WAVE_ORDER:
            raise ValueError("execution group has invalid wave")
        for route_id in group.get("case_ids", []):
            if route_id in result and result[route_id] != wave:
                raise ValueError("route assigned to multiple waves: " + str(route_id))
            result[str(route_id)] = str(wave)
    if len(result) != plan.get("route_count"):
        raise ValueError("execution plan route-to-wave map is incomplete")
    return result


def validate_jobs(job_document: Mapping[str, Any], *, wave: str,
                  route_waves: Mapping[str, str]) -> List[Dict[str, Any]]:
    if job_document.get("schema") != 1 or job_document.get("wave") != wave:
        raise ValueError("job manifest schema/wave does not match requested wave")
    jobs = job_document.get("jobs")
    if not isinstance(jobs, list) or not jobs:
        raise ValueError("wave job manifest requires a non-empty jobs list")
    validated: List[Dict[str, Any]] = []
    seen: set[str] = set()
    for job in jobs:
        if not isinstance(job, dict) or not isinstance(job.get("cases"), str):
            raise ValueError("each job requires a cases path")
        cases_path = _repo_path(job["cases"])
        document = _load_json(cases_path)
        cases = document.get("cases")
        if not isinstance(cases, list) or not cases:
            raise ValueError("contract document has no cases: " + str(cases_path))
        family = str(document.get("family", ""))
        if family and _wave_for_family(family) != wave:
            raise ValueError("contract family is outside requested wave: " + str(cases_path))
        for case in cases:
            if not isinstance(case, dict) or not isinstance(case.get("id"), str):
                raise ValueError("every contract case requires an id: " + str(cases_path))
            case_id = case["id"]
            if case_id in seen:
                raise ValueError("duplicate case id in wave jobs: " + case_id)
            seen.add(case_id)
            route_id = case.get("route_id")
            if route_id is not None and route_waves.get(str(route_id)) != wave:
                raise ValueError("contract route is outside requested wave: " + case_id)
            if case.get("route_origin") == "owned-java-source-only":
                if case.get("runtime_model", "same-spring-application") != "same-spring-application":
                    raise ValueError("owned Java source-only route cannot declare a separate runtime: " + case_id)
                if "boundary process" not in str(case.get("boundary_semantics", "boundary route != boundary process")):
                    raise ValueError("owned Java source-only route lacks boundary semantics: " + case_id)
        output = job.get("output")
        if not isinstance(output, str):
            raise ValueError("each job requires an output report path")
        validated.append({"job": job, "cases_path": cases_path, "document": document,
                          "output": _repo_path(output)})
    return validated


def _cleanup_result(text: str) -> Dict[str, Any]:
    for line in reversed(text.splitlines()):
        try:
            value = json.loads(line)
        except json.JSONDecodeError:
            continue
        if isinstance(value, dict) and (all(key in value for key in REQUIRED_CLEANUP) or value.get("cleanup_mode") == "persistent-oracle"):
            return value
    raise RuntimeError("cleanup command did not return a cleanup contract")


def finalize_report(path: Path, cleanup: Mapping[str, Any], *, wave: str,
                    runtime_id: str) -> Dict[str, Any]:
    report = _load_json(path)
    report["cleanup"] = dict(cleanup)
    report["cleanup_pending"] = not cleanup_contract_complete(cleanup)
    report["wave"] = wave
    report["wave_runtime_id"] = runtime_id
    report["wave_finalized_at"] = now()
    if report.get("status") == "executed-pass-pending-cleanup" and not report["cleanup_pending"]:
        report["status"] = "executed-pass"
    path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    path.chmod(0o600)
    return report


def _run(command: Sequence[str], *, env: Mapping[str, str] | None = None) -> subprocess.CompletedProcess[str]:
    return subprocess.run(command, cwd=REPO_ROOT, env=dict(env) if env else None,
                          text=True, capture_output=True, check=False)


def _start_ssh_tunnel(ssh_host: str, remote_url: str, local_port: int | None = None):
    parsed = urlsplit(remote_url)
    if parsed.hostname not in {"127.0.0.1", "localhost", "::1", "[::1]"} or parsed.port is None:
        raise ValueError("runtime loopback URL is required for SSH tunnel")
    local_port = local_port or parsed.port + 1
    command = [
        "ssh", "-N", "-o", "BatchMode=yes", "-o", "ExitOnForwardFailure=yes",
        "-o", "ConnectTimeout=5", "-o", "ServerAliveInterval=15",
        "-o", "ServerAliveCountMax=4", "-L",
        "%d:127.0.0.1:%d" % (local_port, parsed.port), ssh_host,
    ]
    process = subprocess.Popen(command, cwd=REPO_ROOT, stdout=subprocess.DEVNULL,
                               stderr=subprocess.PIPE, text=True)
    try:
        for _ in range(100):
            if process.poll() is not None:
                detail = (process.stderr.read() if process.stderr else "").strip()
                raise RuntimeError("SSH tunnel exited: " + detail[-500:])
            try:
                with socket.create_connection(("127.0.0.1", local_port), timeout=0.2):
                    return process, "http://127.0.0.1:%d" % local_port
            except OSError:
                import time
                time.sleep(0.1)
        raise RuntimeError("SSH tunnel readiness timeout")
    except BaseException:
        process.terminate()
        try:
            process.wait(timeout=3)
        except subprocess.TimeoutExpired:
            process.kill()
        raise


def _stop_ssh_tunnel(process) -> None:
    if process is None:
        return
    process.terminate()
    try:
        process.wait(timeout=5)
    except subprocess.TimeoutExpired:
        process.kill()
        process.wait(timeout=5)


def run_wave(*, wave: str, jobs_path: Path, plan_path: Path, runtime_report: Path | None,
             start_script: Path | None, cleanup_script: Path | None, base_url: str | None,
             artifacts: Iterable[Path], timeout: float, run_id: str | None = None,
             host_port: str | None = None, ssh_host: str | None = None,
             local_port: int | None = None, manage_cleanup: bool = True,
             reuse_oracle: bool = True, oracle_instance: str | None = None,
             baseline_schema: str | None = None, runtime_schema: str | None = None,
             refresh_baseline: bool = False, reset_runtime_schema: bool = False) -> Dict[str, Any]:
    if wave not in WAVE_ORDER:
        raise ValueError("invalid wave: " + wave)
    plan = _load_json(plan_path)
    _validate_plan(plan)
    route_waves = _route_waves(plan)
    jobs = validate_jobs(_load_json(jobs_path), wave=wave, route_waves=route_waves)
    if start_script is not None:
        runtime_report = runtime_report or (DIAGNOSTIC_ROOT / "runtime" / ("strict-api-runtime-" + wave + ".json"))
        if runtime_report.resolve().is_relative_to(CANONICAL_ROOT.resolve()):
            raise ValueError("runtime manifest output must be diagnostic; canonical evidence is publication-only")
        env = os.environ.copy()
        env["RUN_ID"] = run_id or ("api-wave-" + wave)
        env["HOST_PORT"] = host_port or env.get("HOST_PORT", "18089")
        env["OUTPUT"] = str(runtime_report)
        env["REUSE_ORACLE"] = "1" if reuse_oracle else "0"
        env["REFRESH_BASELINE"] = "1" if refresh_baseline else "0"
        env["RESET_RUNTIME_SCHEMA"] = "1" if reset_runtime_schema else "0"
        if oracle_instance: env["ORACLE_INSTANCE"] = oracle_instance
        if baseline_schema: env["BASELINE_SCHEMA"] = baseline_schema
        if runtime_schema: env["RUNTIME_SCHEMA"] = runtime_schema
        result = _run([str(start_script)], env=env)
        if result.returncode:
            raise RuntimeError("strict runtime start failed: " + result.stderr[-1000:])
    if runtime_report is None or not runtime_report.is_file():
        raise ValueError("a runtime report is required")
    runtime = _load_json(runtime_report)
    runner = _module(ROOT / "scripts" / "spring_api_contract_runner.py", "spring_api_contract_runner_wave")
    runtime_contract = _module(ROOT / "scripts" / "owned_runtime_contract.py", "owned_runtime_contract_wave")
    runtime_contract.validate_file(runtime_report, "strict")
    tunnel = None
    effective_base = base_url or runtime.get("loopback_url")
    if not effective_base:
        raise ValueError("runtime report lacks loopback_url and --base-url was not supplied")
    if base_url is None and ssh_host:
        tunnel, effective_base = _start_ssh_tunnel(
            ssh_host, str(runtime["loopback_url"]), local_port=local_port)
    artifact_paths = [Path(item) for item in artifacts]
    if not artifact_paths:
        raise ValueError("at least one --artifact is required for strict wave evidence")
    reports: List[Path] = []
    promotion_targets: Dict[str, str] = {}
    failures: List[str] = []
    try:
        for item in jobs:
            job = item["job"]
            output = item["output"]
            pending = pending_path(output, run_id or ("api-wave-" + wave))
            pending.parent.mkdir(parents=True, exist_ok=True)
            promotion_targets[str(pending)] = str(output)
            document = dict(item["document"])
            document["case_file"] = str(item["cases_path"])
            previous_manifest = os.environ.get("OWNED_RUNTIME_MANIFEST")
            previous_host = os.environ.get("OWNED_RUNTIME_SSH_HOST")
            os.environ["OWNED_RUNTIME_MANIFEST"] = str(runtime_report)
            if ssh_host:
                os.environ["OWNED_RUNTIME_SSH_HOST"] = str(ssh_host)
            try:
                report = runner.run_cases(
                    document, base_url=effective_base, mode="strict",
                    role=str(job.get("role", "anonymous")),
                    organization=str(job.get("organization", "none")),
                    clone_identity=None, artifacts=artifact_paths, output=pending, timeout=timeout,
                    runtime_report=runtime_report,
                    cleanup_contract={"cleanup_pending": True}, defer_cleanup=True,
                    auth_user=job.get("auth_user"),
                    password_file=_repo_path(job["password_file"]) if job.get("password_file") else None,
                    sql_fixture_executor=_repo_path(job["sql_fixture_executor"])
                    if job.get("sql_fixture_executor") else None,
                )
            finally:
                if previous_manifest is None:
                    os.environ.pop("OWNED_RUNTIME_MANIFEST", None)
                else:
                    os.environ["OWNED_RUNTIME_MANIFEST"] = previous_manifest
                if previous_host is None:
                    os.environ.pop("OWNED_RUNTIME_SSH_HOST", None)
                else:
                    os.environ["OWNED_RUNTIME_SSH_HOST"] = previous_host
            reports.append(pending)
            if report.get("status") not in {"executed-pass-pending-cleanup", "executed-pass"}:
                failures.append(str(pending))
    finally:
        cleanup: Dict[str, Any] | None = None
        if cleanup_script and manage_cleanup:
            env = os.environ.copy()
            env["MANIFEST"] = str(runtime_report)
            result = _run([str(cleanup_script)], env=env)
            if result.returncode == 0:
                cleanup = _cleanup_result(result.stdout)
            else:
                failures.append("cleanup: " + result.stderr[-1000:])
        if cleanup is not None:
            for report_path in reports:
                finalized = finalize_report(report_path, cleanup, wave=wave,
                                            runtime_id="api-wave-" + wave)
                if finalized.get("status") != "executed-pass":
                    failures.append(str(report_path))
        elif reports and manage_cleanup:
            failures.append("wave cleanup was not executed")
        if manage_cleanup:
            _stop_ssh_tunnel(tunnel)
    return {"wave": wave, "runtime_id": "api-wave-" + wave,
            "runtime_report": str(runtime_report), "reports": [str(path) for path in reports],
            "promotion_targets": promotion_targets,
            "failures": sorted(set(failures)), "status": "executed-pass" if not failures else "executed-fail"}


def run_milestone(*, manifest_path: Path, plan_path: Path, runtime_report: Path,
                  start_script: Path, cleanup_script: Path, artifacts: Iterable[Path],
                  timeout: float, host_port: str, ssh_host: str, local_port: int,
                  reuse_oracle: bool = True, oracle_instance: str | None = None,
                  baseline_schema: str | None = None, runtime_schema: str | None = None,
                  refresh_baseline: bool = False, reset_runtime_schema: bool = True) -> Dict[str, Any]:
    """Run ordered waves on one clone-derived runtime and clean once.

    A durable journal plus an exclusive lock makes duplicate or interrupted
    lifecycle IDs visible instead of silently starting a second Oracle clone.
    """
    document = _load_json(manifest_path)
    lifecycle_id = str(document.get("lifecycle_id", "api-milestone"))
    waves = document.get("waves")
    if not isinstance(waves, list) or not waves:
        raise ValueError("milestone manifest requires non-empty waves")
    wave_names = [str(item.get("wave")) for item in waves if isinstance(item, Mapping)]
    if len(wave_names) != len(waves) or len(set(wave_names)) != len(wave_names):
        raise ValueError("milestone waves must have unique wave names")
    if any(name not in WAVE_ORDER for name in wave_names):
        raise ValueError("milestone contains an unknown wave")
    if wave_names != sorted(wave_names, key=WAVE_ORDER.index):
        raise ValueError("milestone waves must follow foundation to secondary order")
    plan = _load_json(plan_path)
    _validate_plan(plan)
    lock, journal, journal_payload = _begin_milestone({**document, "manifest_path": str(manifest_path)}, plan_path)
    all_reports: List[Path] = []
    failures: List[str] = []
    tunnel = None
    cleanup: Dict[str, Any] | None = None
    runtime_started = False
    try:
        env = os.environ.copy()
        env.update({"RUN_ID": lifecycle_id, "HOST_PORT": str(host_port), "OUTPUT": str(runtime_report),
                    "REUSE_ORACLE": "1" if reuse_oracle else "0",
                    "REFRESH_BASELINE": "1" if refresh_baseline else "0",
                    "RESET_RUNTIME_SCHEMA": "1" if reset_runtime_schema else "0"})
        if oracle_instance: env["ORACLE_INSTANCE"] = oracle_instance
        if baseline_schema: env["BASELINE_SCHEMA"] = baseline_schema
        if runtime_schema: env["RUNTIME_SCHEMA"] = runtime_schema
        # Treat a start attempt as a cleanup obligation: the script may have
        # created a network/container before returning a non-zero status.
        runtime_started = True
        started = _run([str(start_script)], env=env)
        if started.returncode:
            raise RuntimeError("milestone runtime start failed: " + started.stderr[-1000:])
        runtime = _load_json(runtime_report)
        runtime_contract = _module(ROOT / "scripts" / "owned_runtime_contract.py", "owned_runtime_contract_milestone")
        runtime_contract.validate_file(runtime_report, "strict")
        _update_milestone(journal, journal_payload, runtime_report=str(runtime_report), loopback_url=runtime.get("loopback_url"))
        tunnel, effective_base = _start_ssh_tunnel(ssh_host, str(runtime["loopback_url"]), local_port=local_port)
        for item in waves:
            wave = str(item["wave"])
            result = run_wave(wave=wave, jobs_path=_repo_path(str(item["jobs"])),
                              plan_path=plan_path, runtime_report=runtime_report,
                              start_script=None, cleanup_script=None, base_url=effective_base,
                              artifacts=artifacts, timeout=timeout,
                              run_id=lifecycle_id + "-" + wave, manage_cleanup=False,
                              ssh_host=ssh_host,
                              reuse_oracle=reuse_oracle, oracle_instance=oracle_instance,
                              baseline_schema=baseline_schema, runtime_schema=runtime_schema,
                              refresh_baseline=False, reset_runtime_schema=False)
            all_reports.extend(Path(path) for path in result["reports"])
            failures.extend(result["failures"])
            _update_milestone(journal, journal_payload,
                              completed_waves=journal_payload.get("completed_waves", []) + [wave])
    except BaseException as error:
        failures.append("milestone: " + str(error))
        _update_milestone(journal, journal_payload, status="failed", error=str(error), reports=[str(path) for path in all_reports])
        raise
    finally:
        if runtime_started:
            result = _run([str(cleanup_script)], env={**os.environ, "MANIFEST": str(runtime_report)})
            cleanup = _cleanup_result(result.stdout) if result.returncode == 0 else None
            if result.returncode != 0:
                failures.append("cleanup: " + result.stderr[-1000:])
        if cleanup is not None:
            for report in all_reports:
                payload = _load_json(report)
                finalized = finalize_report(report, cleanup, wave=str(payload.get("wave", "milestone")), runtime_id=lifecycle_id)
                if finalized.get("status") != "executed-pass":
                    failures.append(str(report))
        _stop_ssh_tunnel(tunnel)
        final_status = "executed-pass" if not failures else "executed-fail"
        _update_milestone(journal, journal_payload, status=final_status,
                          finished_at=now(), reports=[str(path) for path in all_reports],
                          failures=sorted(set(failures)), cleanup=cleanup)
        lock.unlink(missing_ok=True)
    return {"lifecycle_id": lifecycle_id, "runtime_report": str(runtime_report),
            "reports": [str(path) for path in all_reports], "failures": sorted(set(failures)),
            "status": "executed-pass" if not failures else "executed-fail"}


def main(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--wave", choices=WAVE_ORDER)
    parser.add_argument("--jobs", type=Path)
    parser.add_argument("--milestone", type=Path)
    parser.add_argument("--plan", type=Path, default=FEATURE_ROOT / "diagnostic-evidence/pending/current/api-route-execution-plan-current-head.json")
    parser.add_argument("--runtime-report", type=Path)
    parser.add_argument("--start-script", type=Path)
    parser.add_argument("--cleanup-script", type=Path)
    parser.add_argument("--base-url")
    parser.add_argument("--artifact", type=Path, action="append", default=[])
    parser.add_argument("--timeout", type=float, default=10.0)
    parser.add_argument("--run-id")
    parser.add_argument("--host-port")
    parser.add_argument("--ssh-host", default=os.environ.get("SSH_HOST", "sss2"))
    parser.add_argument("--local-port", type=int)
    parser.add_argument("--reuse-oracle", action=argparse.BooleanOptionalAction, default=True)
    parser.add_argument("--oracle-instance", default=os.environ.get("ORACLE_INSTANCE"))
    parser.add_argument("--baseline-schema", default=os.environ.get("BASELINE_SCHEMA"))
    parser.add_argument("--runtime-schema", default=os.environ.get("RUNTIME_SCHEMA"))
    parser.add_argument("--refresh-baseline", action="store_true")
    parser.add_argument("--reset-runtime-schema", action=argparse.BooleanOptionalAction, default=None)
    args = parser.parse_args(argv)
    if args.milestone:
        required = (args.plan, args.start_script, args.cleanup_script, args.runtime_report, args.host_port, args.local_port)
        if not all(required):
            raise SystemExit("milestone requires --plan, --start-script, --cleanup-script, --runtime-report, --host-port and --local-port")
        result = run_milestone(manifest_path=args.milestone, plan_path=args.plan,
                               runtime_report=args.runtime_report, start_script=args.start_script,
                               cleanup_script=args.cleanup_script, artifacts=args.artifact,
                               timeout=args.timeout, host_port=args.host_port,
                               ssh_host=args.ssh_host, local_port=args.local_port,
                               reuse_oracle=args.reuse_oracle, oracle_instance=args.oracle_instance,
                               baseline_schema=args.baseline_schema, runtime_schema=args.runtime_schema,
                               refresh_baseline=args.refresh_baseline,
                               reset_runtime_schema=(True if args.reset_runtime_schema is None else args.reset_runtime_schema))
        print("API milestone orchestrator:", json.dumps(result, ensure_ascii=False, sort_keys=True))
        return 0 if result["status"] == "executed-pass" else 1
    if not args.wave or not args.jobs:
        raise SystemExit("single-wave mode requires --wave and --jobs")
    if not args.cleanup_script:
        raise SystemExit("strict wave orchestration requires --cleanup-script")
    result = run_wave(wave=args.wave, jobs_path=args.jobs, plan_path=args.plan,
                      runtime_report=args.runtime_report, start_script=args.start_script,
                      cleanup_script=args.cleanup_script, base_url=args.base_url,
                      artifacts=args.artifact, timeout=args.timeout, run_id=args.run_id,
                      host_port=args.host_port, ssh_host=args.ssh_host,
                      local_port=args.local_port, reuse_oracle=args.reuse_oracle,
                      oracle_instance=args.oracle_instance, baseline_schema=args.baseline_schema,
                      runtime_schema=args.runtime_schema, refresh_baseline=args.refresh_baseline,
                      reset_runtime_schema=(False if args.reset_runtime_schema is None else args.reset_runtime_schema))
    print("API wave orchestrator:", json.dumps(result, ensure_ascii=False, sort_keys=True))
    return 0 if result["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
