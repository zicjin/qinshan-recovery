#!/usr/bin/env python3
"""Validate the independent attachment/report/scheduler evidence for ticket 16."""
import argparse
import json
import re
import subprocess
import sys
from pathlib import Path


SHA256 = re.compile(r'^[0-9a-f]{64}$')


def fail(message):
    print('ticket16 integration gate: FAIL: ' + message, file=sys.stderr)
    raise SystemExit(1)


def load(path, label):
    try:
        value = json.loads(Path(path).read_text())
    except (OSError, ValueError) as error:
        fail('%s report unreadable: %s' % (label, error))
    if not isinstance(value, dict):
        fail('%s report must be an object' % label)
    return value


def check_common(report, label):
    if report.get('status') != 'executed-pass':
        fail('%s status is %r' % (label, report.get('status')))
    if report.get('production_access') is not False:
        fail('%s production_access must be false' % label)
    if report.get('source_dirty') is not False:
        fail('%s source_dirty must be false' % label)
    for field in ('role', 'organization', 'mode', 'fixture'):
        value = report.get(field)
        if not isinstance(value, str) or not value.strip():
            fail('%s %s is required' % (label, field))
    source_commit = report.get('source_commit')
    if not isinstance(source_commit, str) or not source_commit.strip():
        fail('%s source_commit is required' % label)
    for field in ('source_sha256', 'artifact_sha256'):
        hashes = report.get(field)
        if not isinstance(hashes, dict) or not hashes:
            fail('%s %s must be a non-empty hash map' % (label, field))
        malformed = [key for key, value in hashes.items()
                     if not isinstance(key, str) or not key.strip()
                     or not isinstance(value, str) or not SHA256.fullmatch(value)]
        if malformed:
            fail('%s %s contains malformed entries: %s' % (label, field, ', '.join(map(str, malformed))))
    cleanup = report.get('cleanup')
    if not isinstance(cleanup, dict) or not cleanup:
        fail('%s cleanup is missing' % label)
    false_values = [key for key, value in cleanup.items()
                    if isinstance(value, bool) and value is not True
                    and not key.endswith('_changes') and not key.startswith('existing_')]
    if false_values:
        fail('%s cleanup has false fields: %s' % (label, ', '.join(false_values)))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--attachment', required=True, type=Path)
    parser.add_argument('--report', required=True, type=Path)
    parser.add_argument('--scheduler', required=True, type=Path)
    args = parser.parse_args()
    lifecycle = subprocess.run(
        [sys.executable, str(Path(__file__).resolve().parent / 'manage-evidence.py'), 'verify'],
        text=True, capture_output=True,
    )
    if lifecycle.returncode != 0:
        fail('evidence lifecycle registry verification failed: ' + lifecycle.stderr[-2000:] + lifecycle.stdout[-2000:])
    attachment = load(args.attachment, 'attachment')
    report = load(args.report, 'report')
    scheduler = load(args.scheduler, 'scheduler')
    check_common(attachment, 'attachment')
    check_common(report, 'report')
    check_common(scheduler, 'scheduler')
    if 'attachment' not in str(attachment.get('scope', '')).lower() and 'erp' not in str(attachment.get('scope', '')).lower():
        fail('attachment scope is not declared')
    if 'report' not in str(report.get('scope', '')).lower():
        fail('report scope is not declared')
    if scheduler.get('historical_equivalence') is None:
        fail('scheduler report must declare historical_equivalence')
    print('ticket16 integration gate: PASS (attachment, report, scheduler evidence)')


if __name__ == '__main__':
    main()
