#!/usr/bin/env python3
"""Select execution shards affected by source changes.

This command only plans work.  It never starts a runner, database, browser or
application.  A behavior contract and its execution shard remain separate:
the output names each affected shard once, even when several strict contracts
share it.
"""
import argparse
import csv
import json
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "tools"))
from vertical_slices.matrix import load_runner_registry

GROUPS = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-execution-groups.tsv"
REGISTRY = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv"

FAST_GATE_PATHS = (
    "evidence/", "tools/vertical_slices/", "scripts/tests/",
    ".scratch/", "AGENTS.md",
)


def relative_path(value):
    path = Path(value).as_posix().lstrip("./")
    if path.startswith("lims-recovery/"):
        return path[len("lims-recovery/"):]
    return path


def changed_paths(base=None):
    commands = []
    if base:
        commands.append(["git", "diff", "--name-only", "--no-renames", base, "--"])
    else:
        commands.append(["git", "diff", "--name-only", "--no-renames", "--"])
    commands.append(["git", "diff", "--cached", "--name-only", "--no-renames", "--"])
    paths = set()
    for command in commands:
        result = subprocess.run(command, cwd=ROOT.parent, text=True,
                                capture_output=True, check=True)
        paths.update(relative_path(line) for line in result.stdout.splitlines() if line)
    if not base:
        status = subprocess.run(["git", "status", "--short", "--untracked-files=all"],
                                cwd=ROOT.parent, text=True, capture_output=True,
                                check=True)
        for line in status.stdout.splitlines():
            if len(line) >= 3 and (line[:2].strip() or line.startswith("??")):
                paths.add(relative_path(line[3:]))
    return sorted(paths)


def scope_matches(path, scope):
    scope = scope.rstrip("/")
    return path == scope or path.startswith(scope + "/")


def select(paths):
    registry = load_runner_registry(REGISTRY)
    groups = list(csv.DictReader(GROUPS.open(encoding="utf-8"), delimiter="\t"))
    by_case = {row["case_id"]: row for row in groups}
    selected_cases = set()
    for case_id, registration in registry.items():
        if any(scope_matches(path, scope) for path in paths for scope in registration.source_scopes):
            selected_cases.add(case_id)
    selected_shards = {}
    for case_id in sorted(selected_cases):
        row = by_case.get(case_id)
        if not row:
            raise ValueError("runner registry case is missing from execution groups: " + case_id)
        key = (row["execution_group"], row["runner"])
        selected_shards.setdefault(key, {
            "execution_group": row["execution_group"],
            "runner": row["runner"],
            "cases": [],
        })["cases"].append(case_id)
    fast_gates = sorted(path for path in paths
                        if any(scope_matches(path, prefix.rstrip("/")) for prefix in FAST_GATE_PATHS))
    return {
        "changed_paths": paths,
        "selected_cases": sorted(selected_cases),
        "execution_shards": [selected_shards[key] for key in sorted(selected_shards)],
        "fast_gate_only": not selected_shards,
        "fast_gate_paths": fast_gates,
        "full_milestone_gate_required": False,
    }


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("paths", nargs="*", help="LIMS-relative paths; defaults to staged/worktree changes")
    parser.add_argument("--base", help="compare the current checkout with a Git base commit")
    parser.add_argument("--format", choices=("tsv", "json"), default="tsv")
    args = parser.parse_args(argv)
    paths = [relative_path(path) for path in args.paths] if args.paths else changed_paths(args.base)
    payload = select(sorted(set(paths)))
    if args.format == "json":
        print(json.dumps(payload, ensure_ascii=False, indent=2))
    else:
        print("execution_group\trunner\tcase_count\tcases")
        for group in payload["execution_shards"]:
            print("%s\t%s\t%d\t%s" % (group["execution_group"], group["runner"],
                                        len(group["cases"]), ",".join(group["cases"])))
        if payload["fast_gate_only"]:
            print("FAST-GATES-ONLY\tmetadata\t0\t")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
