#!/usr/bin/env python3
"""Run one explicitly registered vertical-slice replay and verify its report.

Only runners listed in .scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv can be selected.
The command is assembled as an argv list; shell evaluation is deliberately
not supported.
"""
import argparse
from dataclasses import replace
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[2]
LIMS = ROOT / "lims-recovery"
sys.path.insert(0, str(LIMS / "tools"))
from vertical_slices.matrix import load_matrix, load_runner_registry, validate_evidence


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("case_id")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    cases = {case.case_id: case for case in load_matrix(LIMS / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-matrix.tsv")}
    registry = load_runner_registry(LIMS / ".scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv")
    if args.case_id not in cases or args.case_id not in registry:
        raise SystemExit("case is not explicitly registered for replay: " + args.case_id)
    registration = registry[args.case_id]
    runner = (LIMS / registration.runner).resolve()
    try:
        runner.relative_to(LIMS.resolve())
    except ValueError:
        raise SystemExit("runner path escapes the LIMS checkout")
    if not runner.is_file() or runner.suffix != ".py":
        raise SystemExit("registered runner is not a Python file: " + registration.runner)
    output = args.output or (LIMS / cases[args.case_id].evidence)
    if not output.is_absolute():
        output = (ROOT / output).resolve()
    command = [sys.executable, str(runner), "--output", str(output)]
    if args.case_id == "personnel-qualification-browser-write":
        command.extend(["--personnel-qualification-write", "--distribution"])
    result = subprocess.run(command, cwd=str(LIMS), check=False)
    if result.returncode:
        return result.returncode
    validate_evidence([replace(cases[args.case_id], evidence=str(output))], LIMS, registry)
    print("vertical slice replay: PASS (" + args.case_id + ")")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
