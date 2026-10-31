#!/usr/bin/env python3
"""Contract tests for the ticket-16 evidence gate."""
import json
import subprocess
import sys
import tempfile
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
GATE = ROOT / 'scripts' / 'ticket16-integration-gate.py'


def report(status='executed-pass', dirty=False, cleanup=None, extra=None):
    value = {
        'status': status,
        'scope': 'attachment report scheduler isolated replay',
        'production_access': False,
        'source_dirty': dirty,
        'role': 'manager',
        'organization': 'org-a',
        'mode': 'isolated',
        'fixture': 'ticket16-fixture',
        'source_commit': 'a' * 40,
        'source_sha256': {'runner.py': 'b' * 64},
        'artifact_sha256': {'owned.jar': 'c' * 64},
        'cleanup': cleanup if cleanup is not None else {
            'application_stopped': True,
            'fixture_root_removed': True,
            'database_changes': False,
            'sss2_changes': False,
        },
    }
    if extra:
        value.update(extra)
    return value


class Ticket16GateTest(unittest.TestCase):
    def run_gate(self, values):
        with tempfile.TemporaryDirectory(prefix='ticket16-gate-') as directory:
            paths = []
            for index, value in enumerate(values):
                path = Path(directory) / ('report-%d.json' % index)
                path.write_text(json.dumps(value) + '\n')
                paths.append(str(path))
            return subprocess.run(
                [sys.executable, str(GATE), '--attachment', paths[0], '--report', paths[1], '--scheduler', paths[2]],
                text=True, capture_output=True)

    def test_three_pass_reports_are_accepted(self):
        result = self.run_gate([report(), report(), report(extra={'historical_equivalence': 'unknown'})])
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn('ticket16 integration gate: PASS', result.stdout)

    def test_evidence_lifecycle_is_checked_before_release_reports(self):
        # The release gate delegates to the same registry verifier used by the
        # API/package gates; a clean repository therefore accepts valid temp
        # report inputs without promoting them.
        result = self.run_gate([report(), report(), report(extra={'historical_equivalence': 'unknown'})])
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_production_access_or_dirty_evidence_is_rejected(self):
        result = self.run_gate([report(extra={'production_access': True}), report(), report()])
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('production_access', result.stderr)
        result = self.run_gate([report(), report(dirty=True), report()])
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('source_dirty', result.stderr)

    def test_cleanup_must_be_complete(self):
        result = self.run_gate([report(cleanup={'application_stopped': False}), report(), report()])
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('cleanup', result.stderr)

    def test_context_and_hashes_are_required(self):
        value = report(extra={'role': None})
        result = self.run_gate([value, report(), report(extra={'historical_equivalence': 'unknown'})])
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('role', result.stderr)
        value = report(extra={'artifact_sha256': {'owned.jar': 'not-a-sha256'}})
        result = self.run_gate([value, report(), report(extra={'historical_equivalence': 'unknown'})])
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('artifact_sha256', result.stderr)


if __name__ == '__main__':
    unittest.main()
