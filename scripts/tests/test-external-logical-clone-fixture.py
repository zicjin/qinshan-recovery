#!/usr/bin/env python3
import subprocess
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-external-logical-clone-fixture.py"


class ExternalLogicalCloneFixtureTest(unittest.TestCase):
    def test_fixture_is_source_bound_and_structure_only(self):
        result = subprocess.run(["python3", str(SCRIPT)], text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"tables": 10', result.stdout)


if __name__ == "__main__":
    unittest.main()
