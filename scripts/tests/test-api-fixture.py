#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "api_fixture.py"
spec = importlib.util.spec_from_file_location("api_fixture", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class FixtureTest(unittest.TestCase):
    def test_fixture_must_stay_inside_root_and_reject_secret_markers(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            good = root / "good.sql"
            good.write_text("-- isolated fixture\n", encoding="utf-8")
            self.assertEqual(module.validate_sql_fixture(good, root), good.resolve())
            bad = root / "bad.sql"
            bad.write_text("connect user/password@10.100.2.196\n", encoding="utf-8")
            with self.assertRaises(module.FixtureError):
                module.validate_sql_fixture(bad, root)
            outside = Path(temp).parent / "outside.sql"
            outside.write_text("-- no secret\n", encoding="utf-8")
            try:
                with self.assertRaises(module.FixtureError):
                    module.validate_sql_fixture(outside, root)
            finally:
                outside.unlink(missing_ok=True)

    def test_executor_result_contains_hashes_without_output(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            fixture = root / "fixture.sql"
            fixture.write_text("-- isolated fixture\n", encoding="utf-8")
            executor = root / "executor.py"
            executor.write_text("#!/usr/bin/env python3\nimport sys\nassert '--fixture' in sys.argv and '--phase' in sys.argv\nprint('fixture-output-must-not-be-recorded')\n", encoding="utf-8")
            result = module.execute_fixture(executor, fixture, "setup", root=root)
            self.assertEqual(result["status"], "executed")
            self.assertTrue(result["sha256"])
            self.assertNotIn("fixture-output-must-not-be-recorded", result)


if __name__ == "__main__":
    unittest.main()
