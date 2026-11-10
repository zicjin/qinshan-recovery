#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-external-logical-clone-runtime.py"
spec = importlib.util.spec_from_file_location("external_clone_runtime", SCRIPT)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class ExternalLogicalCloneRuntimeTest(unittest.TestCase):
    def test_parse_read_only_receipt(self):
        receipt = module.parse_receipt("tables=10\ncolumns=1303\nconstraints=38\nindexes=47\nrows=0\nlinks=0\n")
        self.assertEqual(receipt, {"tables": 10, "columns": 1303, "constraints": 38, "indexes": 47, "rows": 0, "links": 0})

    def test_sql_is_read_only(self):
        sql = module.sql_text().upper()
        self.assertNotRegex(sql, r"\b(CREATE|ALTER\s+USER|INSERT|UPDATE|DELETE|DROP|MERGE)\b")
        self.assertIn("ALL_TABLES", sql)
        self.assertIn("ALL_DB_LINKS", sql)


if __name__ == "__main__":
    unittest.main()
