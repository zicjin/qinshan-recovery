import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
RUNNER = ROOT / 'owned-distribution/tests/run-api-harness.py'
spec = importlib.util.spec_from_file_location('run_api_harness', RUNNER)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class ApiHarnessContractTest(unittest.TestCase):
    def test_forbidden_runtime_markers_are_detected(self):
        self.assertEqual(module.forbidden_runtime_markers('java -jar owned.jar'), [])
        hits = module.forbidden_runtime_markers(
            'sunway-app.so license-machine MAC_ADDRESS com/sunwayworld/module/sys/license')
        self.assertEqual(hits, [
            'sunway-app.so', 'license-machine', 'MAC_ADDRESS',
            'com/sunwayworld/module/sys/license'])

    def test_redaction_removes_secrets_from_report_text(self):
        self.assertEqual(module.redact_text(
            'password=secret-token token=secret-token', ['secret-token']),
            'password=<REDACTED> token=<REDACTED>')


if __name__ == '__main__':
    unittest.main()
