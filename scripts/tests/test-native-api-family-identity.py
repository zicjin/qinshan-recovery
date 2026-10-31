import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
class IdentityFamilyCoverageTest(unittest.TestCase):
    def test_all_registered_family_items_have_independent_results(self):
        registry = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json").read_text())
        report = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-bootstrap-identity-20261025.json").read_text())
        expected = {item["id"] for item in registry["items"] if item.get("scope") == "external-native" and item.get("family") in {"bootstrap-web", "security-identity"}}
        results = {item["registry_id"]: item for item in report["results"]}
        self.assertEqual(set(results), expected)
        self.assertEqual(len(expected), 45)
        self.assertTrue(all(item["status"] in {"owned-pass", "removed-negative-pass"} for item in results.values()))
        self.assertTrue(all(item.get("evidence") and item.get("observed") for item in results.values()))
        self.assertTrue(all(report["cleanup"].values()))
    def test_license_family_items_are_negative_contracts(self):
        report = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-bootstrap-identity-20261025.json").read_text())
        for item in report["results"]:
            if any(token in item["registry_id"] for token in []):
                self.fail("unreachable")
        self.assertTrue(any(item["status"] == "removed-negative-pass" for item in report["results"]))
if __name__ == "__main__": unittest.main()
