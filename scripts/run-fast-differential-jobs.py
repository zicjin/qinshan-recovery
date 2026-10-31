#!/usr/bin/env python3
"""Execute generated fast differential jobs as diagnostic batches.

The contract skeleton remains planning metadata, while this adapter turns each
generated job into a concrete invocation of ``fast_differential_api_runner``.
It never writes business data and never publishes canonical evidence.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import subprocess
import sys
from typing import Any, Dict, List, Mapping, Sequence

ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = ROOT.parent
RUNNER = ROOT / "scripts" / "fast_differential_api_runner.py"
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import DIAGNOSTIC_ROOT, guard_output_path  # noqa: E402


def _load(path: Path) -> Dict[str, Any]:
    value = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(value, dict):
        raise ValueError("JSON document must be an object: " + str(path))
    return value


def build_command(job: Mapping[str, Any], *, ledger: Path, plan: Path,
                  supplier: str, owned: str, auth_user: str, password_file: Path,
                  output: Path, timeout: float) -> List[str]:
    families = job.get("families") or []
    route_kinds = job.get("route_kind") or ["inherited-api", "api-resource"]
    profiles = job.get("profiles") or ["default", "page-2", "empty", "malformed"]
    if not families:
        raise ValueError("generated fast job has no families")
    return [
        sys.executable, str(RUNNER),
        "--ledger", str(ledger), "--plan", str(plan),
        "--supplier-base-url", supplier, "--owned-base-url", owned,
        "--auth-user", auth_user, "--password-file", str(password_file),
        "--family", ",".join(str(value) for value in families),
        "--route-kind", ",".join(str(value) for value in route_kinds),
        "--profiles", ",".join(str(value) for value in profiles),
        "--limit", str(max(1, len(job.get("probe_ids") or []))),
        "--timeout", str(timeout), "--output", str(output),
    ]


def run(args: argparse.Namespace) -> Dict[str, Any]:
    skeleton = _load(args.skeleton)
    ledger = _load(args.ledger)
    plan = _load(args.plan)
    for label, document in (("skeleton", skeleton), ("plan", plan)):
        if document.get("implementation_tree_digest") != ledger.get("implementation_tree_digest") or document.get("implementation_binding") != ledger.get("implementation_binding"):
            raise ValueError(label + " implementation binding does not match ledger")
        if document.get("implementation_dirty") is not False:
            raise ValueError(label + " implementation scope must be clean")
    jobs = skeleton.get("jobs")
    if not isinstance(jobs, list) or not jobs:
        raise ValueError("skeleton has no generated jobs")
    output_dir = args.output_dir or (DIAGNOSTIC_ROOT / "fast-jobs" / str(skeleton.get("implementation_tree_digest", skeleton["source_commit"]))[:12])
    output_dir.mkdir(parents=True, exist_ok=True)
    results: List[Dict[str, Any]] = []
    for job in jobs:
        wave = str(job.get("wave", "batch"))
        output = output_dir / ("api-fast-" + wave + ".json")
        guard_output_path(output)
        command = build_command(job, ledger=args.ledger, plan=args.plan,
                                supplier=args.supplier_base_url, owned=args.owned_base_url,
                                auth_user=args.auth_user, password_file=args.password_file,
                                output=output, timeout=args.timeout)
        completed = subprocess.run(command, cwd=REPO_ROOT, text=True,
                                   capture_output=True, check=False)
        results.append({"wave": wave, "output": str(output),
                        "returncode": completed.returncode,
                        "stdout": completed.stdout[-1000:],
                        "stderr": completed.stderr[-1000:]})
        if completed.returncode:
            break
    return {"schema": 1, "status": "diagnostic-pass" if all(item["returncode"] == 0 for item in results) and len(results) == len(jobs) else "diagnostic-failed",
            "execution": "fast-differential-jobs", "strict_evidence": False,
            "source_commit": skeleton.get("source_commit"), "source_dirty": skeleton.get("source_dirty"),
            "repository_head_at_capture": skeleton.get("repository_head_at_capture", skeleton.get("source_commit")),
            "implementation_binding": skeleton.get("implementation_binding"),
            "implementation_tree_digest": skeleton.get("implementation_tree_digest"),
            "implementation_dirty": skeleton.get("implementation_dirty"),
            "implementation_scope": skeleton.get("implementation_scope", []),
            "jobs": results}


def main(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--skeleton", type=Path, required=True)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--supplier-base-url", required=True)
    parser.add_argument("--owned-base-url", required=True)
    parser.add_argument("--auth-user", required=True)
    parser.add_argument("--password-file", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path)
    parser.add_argument("--timeout", type=float, default=10.0)
    args = parser.parse_args(argv)
    result = run(args)
    print("fast differential jobs:", json.dumps(result, ensure_ascii=False, sort_keys=True))
    return 0 if result["status"] == "diagnostic-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
