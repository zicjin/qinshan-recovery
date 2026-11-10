#!/usr/bin/env python3
import json
from pathlib import Path
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/generate-external-logical-clone-ddl.py"


def section(columns, rows):
    return {"status": "ok", "columns": columns, "row_count": len(rows), "rows": rows}


def receipt():
    domains = {}
    targets = {"HT": ["T_CRM_CONTRACT", "T_CRM_INVOICE_PROJ", "T_CRM_INVOICE", "T_CRM_PAY_PROJECT", "T_CRM_PAYMENT"], "SP": ["T_LIMS_CLEARANCEFORM", "T_LIMS_FOLDERS"], "XFP": ["T_XFP_ORDER_TASK", "T_XFP_PROJECT", "T_XFP_REPORT"]}
    owners = {"HT": "LIMS_CZJKY_HT", "SP": "LIMS_CZJKY_SP", "XFP": "LIMS_CZJKY_XF"}
    for domain, tables in targets.items():
        owner = owners[domain]
        table = tables[0]
        columns = [
            [owner, table, "1", "ID", "NUMBER", "22", "19", "0", "N", None, "0", None],
            [owner, table, "2", "NAME", "VARCHAR2", "40", None, None, "Y", "B", "40", None],
        ]
        constraints = [
            [owner, table, "PK_TEST", "P", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, None],
            [owner, table, "UQ_TEST", "U", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, None],
            [owner, table, "CK_TEST", "C", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, '"ID" IS NOT NULL'],
        ]
        constraint_columns = [
            [owner, table, "PK_TEST", "ID", "1"],
            [owner, table, "UQ_TEST", "NAME", "1"],
            [owner, table, "CK_TEST", "ID", None],
        ]
        indexes = [
            [owner, "PK_TEST", "NORMAL", "UNIQUE", "VALID", table],
            [owner, "IDX_TEST_NAME", "NORMAL", "NONUNIQUE", "VALID", table],
        ]
        index_columns = [
            [owner, "PK_TEST", owner, table, "ID", "1", "ASC"],
            [owner, "IDX_TEST_NAME", owner, table, "NAME", "1", "DESC"],
        ]
        # Include all approved table names in target_objects, while metadata
        # only contains a minimal fixture table.  The generator must reject
        # that incomplete receipt rather than inventing columns.
        domains[domain] = {
            "target_objects": tables,
            "metadata": {
                "columns": section([], columns),
                "constraints": section([], constraints),
                "constraint_columns": section([], constraint_columns),
                "indexes": section([], indexes),
                "index_columns": section([], index_columns),
                "types": {"status": "blocked"},
                "procedures": {"status": "blocked"},
                "arguments": {"status": "blocked"},
                "grants": {"status": "blocked"},
            },
        }
    # Make every approved table present by reusing the same shape; this tests
    # rendering rather than a fixture-specific schema catalogue.
    for domain, value in domains.items():
        owner = owners[domain]
        metadata = value["metadata"]
        rows = []
        constraints = []
        constraint_columns = []
        indexes = []
        index_columns = []
        for i, table in enumerate(targets[domain]):
            rows.extend([[owner, table, "1", "ID", "NUMBER", "22", "19", "0", "N", None, "0", None]])
            constraints.extend([[owner, table, f"PK_{i}", "P", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, None]])
            constraint_columns.extend([[owner, table, f"PK_{i}", "ID", "1"]])
            indexes.extend([[owner, f"PK_{i}", "NORMAL", "UNIQUE", "VALID", table]])
            index_columns.extend([[owner, f"PK_{i}", owner, table, "ID", "1", "ASC"]])
        metadata["columns"] = section([], rows)
        metadata["constraints"] = section([], constraints)
        metadata["constraint_columns"] = section([], constraint_columns)
        metadata["indexes"] = section([], indexes)
        metadata["index_columns"] = section([], index_columns)
    return {"schema": "synthetic-external-metadata-v1", "domains": domains}


class GenerateExternalLogicalCloneDdlTest(unittest.TestCase):
    def run_generator(self, value):
        with tempfile.TemporaryDirectory() as tmp:
            tmp = Path(tmp)
            receipt_path = tmp / "receipt.json"
            sql_path = tmp / "clone.sql"
            report_path = tmp / "clone.json"
            receipt_path.write_text(json.dumps(value), encoding="utf-8")
            result = subprocess.run([
                "python3", str(SCRIPT), "--receipt", str(receipt_path),
                "--output", str(sql_path), "--manifest-output", str(report_path),
            ], text=True, capture_output=True)
            sql = sql_path.read_text(encoding="utf-8") if sql_path.exists() else ""
            report = json.loads(report_path.read_text(encoding="utf-8")) if report_path.exists() else None
            return result, sql, report

    def test_renders_only_approved_tables_and_records_blocked_gaps(self):
        result, sql, report = self.run_generator(receipt())
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(len(report["approved_tables"]), 10)
        self.assertIn('CREATE TABLE "LIMS_EXT_HT"."T_CRM_CONTRACT"', sql)
        self.assertIn('CONSTRAINT "PK_0" PRIMARY KEY ("ID")', sql)
        self.assertNotIn("@LIMS_", sql)
        self.assertFalse(report["database_connection"])
        self.assertFalse(report["business_rows_read"])
        kinds = {gap["kind"] for gap in report["blocked_gaps"]}
        self.assertIn("transport", kinds)
        self.assertIn("business-rows", kinds)
        self.assertIn("procedural-metadata", kinds)

    def test_rejects_missing_columns_in_approved_table(self):
        value = receipt()
        value["domains"]["HT"]["metadata"]["columns"]["rows"] = [
            row for row in value["domains"]["HT"]["metadata"]["columns"]["rows"]
            if row[1] != "T_CRM_PAYMENT"
        ]
        result, _, _ = self.run_generator(value)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("approved table has no columns", result.stderr)

    def test_rejects_non_approved_target_closure(self):
        value = receipt()
        value["domains"]["SP"]["target_objects"].append("T_NOT_APPROVED")
        result, _, _ = self.run_generator(value)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("target object closure differs", result.stderr)


if __name__ == "__main__":
    unittest.main()
