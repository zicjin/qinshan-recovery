#!/usr/bin/env python3
"""Verify that a declared permission shard actually reports shared runtime execution."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REPORT = ROOT.parent / '.scratch/sunway-supplier-exit-api-first/archive/historical/permission-role-organization-20261024-org-refresh.json'


def main():
    report = json.loads(REPORT.read_text(encoding='utf-8'))
    if report.get('status') != 'executed-pass':
        raise SystemExit('batch report is not executed-pass')
    if report.get('execution_group') != 'permission-role-organization':
        raise SystemExit('batch execution group is missing')
    shared = report.get('shared_runtime')
    for name in ('oracle', 'redis', 'application', 'browser'):
        if not isinstance(shared, dict) or name not in shared:
            raise SystemExit('shared runtime field missing: ' + name)
    if shared['oracle'].get('shared_across_cases') is not True or shared['redis'].get('shared_across_cases') is not True:
        raise SystemExit('Oracle/Redis were not proven shared across cases')
    cases = report.get('batch_case_results')
    if not isinstance(cases, list) or {item.get('case_id') for item in cases} != {'permission-matrix-default', 'permission-matrix-isolated'}:
        raise SystemExit('batch case results do not cover both modes')
    if any(item.get('fixture_restore') is not True or item.get('http_case_count', 0) <= 0 for item in cases):
        raise SystemExit('batch case fixture or HTTP result is incomplete')
    timings = report.get('phase_timings_ms')
    if not isinstance(timings, dict) or any(not isinstance(value, (int, float)) for value in timings.values()):
        raise SystemExit('phase timings are missing or malformed')
    cleanup = report.get('cleanup', {})
    required = ('browser_session_closed', 'temporary_files_removed', 'container_removed', 'anonymous_volumes_removed', 'network_removed')
    if any(cleanup.get(key) is not True for key in required):
        raise SystemExit('batch cleanup proof is incomplete')
    print('batch execution report: PASS (shared Oracle/Redis shard, 2 mode cases, fixture restore and timing fields)')


if __name__ == '__main__':
    main()
