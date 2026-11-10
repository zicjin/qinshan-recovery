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


DEFAULT_COLUMNS = ["OWNER", "TABLE_NAME", "COLUMN_NAME", "DEFAULT_LENGTH", "DATA_DEFAULT"]
CONSTRAINT_INDEX_COLUMNS = ["OWNER", "TABLE_NAME", "CONSTRAINT_NAME", "CONSTRAINT_TYPE", "STATUS", "VALIDATED", "DEFERRABLE", "DEFERRED", "INDEX_OWNER", "INDEX_NAME"]


def with_default(value, expression, length=None):
    metadata = value["domains"]["HT"]["metadata"]
    default_length = str(len(expression.encode("utf-8"))) if length is None else str(length)
    metadata["columns"]["rows"][0][11] = default_length
    metadata["column_defaults"] = section(DEFAULT_COLUMNS, [
        ["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "ID", default_length, expression]])
    return metadata


def with_index_links(value):
    for domain in value["domains"].values():
        metadata = domain["metadata"]
        metadata["constraint_index_links"] = section(CONSTRAINT_INDEX_COLUMNS, [
            row[:8] + [row[0], row[2]]
            for row in metadata["constraints"]["rows"] if row[3] in {"P", "U"}])
    return value


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

    def test_check_constraint_does_not_hide_independent_index(self):
        value = receipt()
        metadata = value["domains"]["HT"]["metadata"]
        owner, table = "LIMS_CZJKY_HT", "T_CRM_CONTRACT"
        metadata["columns"]["rows"].append(
            [owner, table, "2", "NAME", "VARCHAR2", "40", None, None, "N", "B", "40", None])
        metadata["constraints"]["rows"].append(
            [owner, table, "CK_NAME", "C", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, '"NAME" IS NOT NULL'])
        metadata["constraint_columns"]["rows"].append([owner, table, "CK_NAME", "NAME", None])
        metadata["indexes"]["rows"].append([owner, "IDX_NAME", "NORMAL", "NONUNIQUE", "VALID", table])
        metadata["index_columns"]["rows"].append([owner, "IDX_NAME", owner, table, "NAME", "1", "ASC"])
        result, sql, _ = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('CREATE INDEX "LIMS_EXT_HT"."IDX_NAME" ON "LIMS_EXT_HT"."T_CRM_CONTRACT" ("NAME");', sql)

    def test_enabled_novalidate_constraints_preserve_source_state(self):
        for kind, clause, condition in (("P", 'PRIMARY KEY ("ID")', None),
                                        ("U", 'UNIQUE ("ID")', None),
                                        ("C", 'CHECK ("ID" > 0)', '"ID" > 0')):
            with self.subTest(kind=kind):
                value = receipt()
                metadata = value["domains"]["HT"]["metadata"]
                metadata["constraints"]["rows"][0] = ["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "NOVALIDATE_TEST", kind, "ENABLED", "NOT VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, condition]
                metadata["constraint_columns"]["rows"][0][2] = "NOVALIDATE_TEST"
                result, sql, report = self.run_generator(value)
                self.assertEqual(result.returncode, 0, result.stderr)
                self.assertIn('CONSTRAINT "NOVALIDATE_TEST" ' + clause + ' ENABLE NOVALIDATE', sql)
                self.assertFalse(any(gap.get("name") == "NOVALIDATE_TEST" for gap in report["blocked_gaps"]))

    def test_disabled_or_unknown_constraint_state_remains_blocked(self):
        for status, validation in (("DISABLED", "VALIDATED"), ("ENABLED", "UNKNOWN")):
            with self.subTest(status=status, validation=validation):
                value = receipt()
                value["domains"]["HT"]["metadata"]["constraints"]["rows"][0][4:6] = [status, validation]
                result, sql, report = self.run_generator(value)
                self.assertEqual(result.returncode, 0, result.stderr)
                self.assertNotIn('CONSTRAINT "PK_0" PRIMARY KEY ("ID")', sql.split('CREATE TABLE "LIMS_EXT_HT"."T_CRM_INVOICE_PROJ"')[0])
                self.assertTrue(any(gap["kind"] == "constraint" and gap.get("name") == "PK_0" for gap in report["blocked_gaps"]))

    def test_captured_default_is_rendered_and_missing_expression_stays_blocked(self):
        value = receipt()
        metadata = with_default(value, "'x' ")
        metadata["columns"]["rows"][1][11] = "1"
        result, sql, report = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"ID" NUMBER(19,0) DEFAULT \'x\' NOT NULL', sql)
        self.assertEqual(report["source_receipt_schema"], "synthetic-external-metadata-v1")
        gaps = [gap for gap in report["blocked_gaps"] if gap["kind"] == "column-default"]
        self.assertEqual([(gap["table"], gap["column"]) for gap in gaps], [("T_CRM_INVOICE_PROJ", "ID")])

    def test_only_captured_safe_default_expressions_are_rendered(self):
        for raw, rendered in (("'text' ", "'text'"), ("N'字符'", "N'字符'"),
                              ("'O''Brien'", "'O''Brien'"), ("-1.25E+3\n", "-1.25E+3"),
                              ("NULL", "NULL"), (" SYSDATE ", "SYSDATE")):
            with self.subTest(expression=rendered):
                value = receipt()
                with_default(value, raw)
                result, sql, report = self.run_generator(value)
                self.assertEqual(result.returncode, 0, result.stderr)
                self.assertIn(" DEFAULT " + rendered + " NOT NULL", sql)
                self.assertFalse(any(gap["kind"] == "column-default" for gap in report["blocked_gaps"]))

    def test_default_literal_cannot_trigger_sqlplus_substitution(self):
        value = receipt()
        with_default(value, "'R&D'")
        result, sql, _ = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("DEFAULT 'R&D'", sql)
        self.assertIn("SET DEFINE OFF\n", sql[:sql.index("CREATE TABLE")])
        self.assertIn("WHENEVER SQLERROR EXIT FAILURE ROLLBACK\n", sql[:sql.index("CREATE TABLE")])

    def test_default_length_must_match_capture_and_column_before_trim(self):
        for mismatch in ("raw", "column"):
            with self.subTest(mismatch=mismatch):
                value = receipt()
                metadata = with_default(value, "'x' ")
                if mismatch == "raw":
                    metadata["column_defaults"]["rows"][0][4] = "'x'"
                else:
                    metadata["columns"]["rows"][0][11] = "5"
                result, sql, report = self.run_generator(value)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn("column default length mismatch", result.stderr)
                self.assertEqual(sql, "")
                self.assertIsNone(report)

    def test_unsafe_or_unsupported_default_fails_without_echoing_value(self):
        for expression in ("1; DROP TABLE secret_probe", "'x' || secret_probe()", "secret_probe.nextval", "'unterminated_secret_probe"):
            with self.subTest(expression=expression):
                value = receipt()
                with_default(value, expression)
                result, sql, report = self.run_generator(value)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn("unsafe or unsupported column default expression", result.stderr)
                self.assertNotIn("secret_probe", result.stderr)
                self.assertEqual(sql, "")
                self.assertIsNone(report)

    def test_duplicate_unmatched_or_malformed_default_capture_is_rejected(self):
        for invalid in ("duplicate", "unmatched", "columns", "blocked", "row"):
            with self.subTest(invalid=invalid):
                value = receipt()
                metadata = with_default(value, "1")
                captured = metadata["column_defaults"]
                if invalid == "duplicate":
                    captured["rows"].append(captured["rows"][0][:])
                elif invalid == "unmatched":
                    captured["rows"][0][2] = "UNKNOWN"
                elif invalid == "columns":
                    captured["columns"] = []
                elif invalid == "blocked":
                    captured["status"] = "blocked"
                else:
                    captured["rows"][0].pop()
                result, sql, _ = self.run_generator(value)
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(sql, "")

    def test_number_with_unconstrained_precision_preserves_declared_scale(self):
        for scale in ("0", "-2", "3"):
            with self.subTest(scale=scale):
                value = receipt()
                value["domains"]["HT"]["metadata"]["columns"]["rows"][0][6:8] = [None, scale]
                result, sql, _ = self.run_generator(value)
                self.assertEqual(result.returncode, 0, result.stderr)
                self.assertIn('"ID" NUMBER(*,' + scale + ') NOT NULL', sql)

    def test_named_not_null_is_emitted_once_with_its_validation_state(self):
        for validation, suffix in (("VALIDATED", ""), ("NOT VALIDATED", " ENABLE NOVALIDATE")):
            with self.subTest(validation=validation):
                value = receipt()
                metadata = value["domains"]["HT"]["metadata"]
                metadata["constraints"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "CK_ID_NOT_NULL", "C", "ENABLED", validation, "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, '"ID" IS NOT NULL'])
                metadata["constraint_columns"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "CK_ID_NOT_NULL", "ID", None])
                with_default(value, "1 ")
                result, sql, report = self.run_generator(value)
                self.assertEqual(result.returncode, 0, result.stderr)
                first_table = sql.split('CREATE TABLE "LIMS_EXT_HT"."T_CRM_INVOICE_PROJ"')[0]
                self.assertIn('"ID" NUMBER(19,0) DEFAULT 1 CONSTRAINT "CK_ID_NOT_NULL" NOT NULL' + suffix, first_table)
                self.assertEqual(first_table.count("NOT NULL"), 1)
                self.assertNotIn('CHECK ("ID" IS NOT NULL)', first_table)
                self.assertEqual(report["approved_tables"][0]["constraints"], 2)

    def test_check_on_nullable_column_does_not_become_not_null_declaration(self):
        value = receipt()
        metadata = value["domains"]["HT"]["metadata"]
        metadata["columns"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "2", "NAME", "VARCHAR2", "40", None, None, "Y", "B", "40", None])
        metadata["constraints"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "CK_NAME", "C", "ENABLED", "VALIDATED", "NOT DEFERRABLE", "IMMEDIATE", "USER NAME", None, None, '"NAME" IS NOT NULL'])
        metadata["constraint_columns"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", "CK_NAME", "NAME", None])
        result, sql, _ = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('CONSTRAINT "CK_NAME" CHECK ("NAME" IS NOT NULL)', sql)
        self.assertNotIn('"NAME" VARCHAR2(40 BYTE) CONSTRAINT "CK_NAME" NOT NULL', sql)

    def test_national_character_and_raw_lengths_use_oracle_syntax(self):
        value = receipt()
        metadata = value["domains"]["HT"]["metadata"]
        for position, name, kind, length, semantics, chars in (
                (2, "NATIONAL_TEXT", "NVARCHAR2", "72", "C", "36"),
                (3, "NATIONAL_CHAR", "NCHAR", "18", "C", "9"),
                (4, "BINARY_DATA", "RAW", "20", None, "0")):
            metadata["columns"]["rows"].append(["LIMS_CZJKY_HT", "T_CRM_CONTRACT", str(position), name, kind, length, None, None, "Y", semantics, chars, None])
        result, sql, _ = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"NATIONAL_TEXT" NVARCHAR2(36)', sql)
        self.assertIn('"NATIONAL_CHAR" NCHAR(9)', sql)
        self.assertIn('"BINARY_DATA" RAW(20)', sql)

    def test_linked_constraint_uses_exact_named_nonunique_source_index(self):
        value = with_index_links(receipt())
        metadata = value["domains"]["HT"]["metadata"]
        metadata["constraints"]["rows"][0][2:6] = ["SOURCE_UQ", "U", "ENABLED", "NOT VALIDATED"]
        metadata["constraint_columns"]["rows"][0][2] = "SOURCE_UQ"
        metadata["indexes"]["rows"][0][1] = "SOURCE_BACKING_IDX"
        metadata["indexes"]["rows"][0][3] = "NONUNIQUE"
        metadata["index_columns"]["rows"][0][1] = "SOURCE_BACKING_IDX"
        metadata["constraint_index_links"]["rows"][0] = metadata["constraints"]["rows"][0][:8] + ["LIMS_CZJKY_HT", "SOURCE_BACKING_IDX"]
        result, sql, report = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        index = 'CREATE INDEX "LIMS_EXT_HT"."SOURCE_BACKING_IDX" ON "LIMS_EXT_HT"."T_CRM_CONTRACT" ("ID");'
        constraint = 'ALTER TABLE "LIMS_EXT_HT"."T_CRM_CONTRACT" ADD CONSTRAINT "SOURCE_UQ" UNIQUE ("ID") USING INDEX "LIMS_EXT_HT"."SOURCE_BACKING_IDX" ENABLE NOVALIDATE;'
        self.assertIn(index, sql)
        self.assertIn(constraint, sql)
        self.assertLess(sql.index(index), sql.index(constraint))
        self.assertNotIn('CONSTRAINT "SOURCE_UQ"', sql[:sql.index(index)])
        self.assertEqual(report["approved_tables"][0]["indexes"], 1)
        self.assertEqual(report["approved_tables"][0]["constraints"], 1)

    def test_missing_or_inconsistent_backing_index_metadata_is_rejected(self):
        for invalid in ("link", "index", "columns", "owner", "state", "duplicate", "invalid-index", "constraint-columns", "uniqueness", "direction"):
            with self.subTest(invalid=invalid):
                value = with_index_links(receipt())
                metadata = value["domains"]["HT"]["metadata"]
                if invalid == "link":
                    metadata["constraint_index_links"]["rows"].pop(0)
                elif invalid == "index":
                    metadata["indexes"]["rows"].pop(0)
                elif invalid == "columns":
                    metadata["index_columns"]["rows"][0][4] = "OTHER"
                elif invalid == "owner":
                    metadata["constraint_index_links"]["rows"][0][8] = "OTHER_OWNER"
                elif invalid == "state":
                    metadata["constraint_index_links"]["rows"][0][5] = "NOT VALIDATED"
                elif invalid == "duplicate":
                    metadata["constraint_index_links"]["rows"].append(metadata["constraint_index_links"]["rows"][0][:])
                elif invalid == "invalid-index":
                    metadata["indexes"]["rows"][0][4] = "UNUSABLE"
                elif invalid == "constraint-columns":
                    metadata["constraint_columns"]["rows"].pop(0)
                elif invalid == "uniqueness":
                    metadata["indexes"]["rows"][0][3] = "UNKNOWN"
                else:
                    metadata["index_columns"]["rows"][0][6] = "UNKNOWN"
                result, sql, report = self.run_generator(value)
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(sql, "")
                self.assertIsNone(report)

    def test_exact_index_link_mode_keeps_all_source_indexes_and_validated_state(self):
        value = with_index_links(receipt())
        metadata = value["domains"]["HT"]["metadata"]
        metadata["indexes"]["rows"].append(["LIMS_CZJKY_HT", "EXTRA_SOURCE_IDX", "NORMAL", "NONUNIQUE", "VALID", "T_CRM_CONTRACT"])
        metadata["index_columns"]["rows"].append(["LIMS_CZJKY_HT", "EXTRA_SOURCE_IDX", "LIMS_CZJKY_HT", "T_CRM_CONTRACT", "ID", "1", "DESC"])
        result, sql, report = self.run_generator(value)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('CREATE UNIQUE INDEX "LIMS_EXT_HT"."PK_0" ON "LIMS_EXT_HT"."T_CRM_CONTRACT" ("ID");', sql)
        self.assertIn('CREATE INDEX "LIMS_EXT_HT"."EXTRA_SOURCE_IDX" ON "LIMS_EXT_HT"."T_CRM_CONTRACT" ("ID" DESC);', sql)
        self.assertIn('ADD CONSTRAINT "PK_0" PRIMARY KEY ("ID") USING INDEX "LIMS_EXT_HT"."PK_0" ENABLE VALIDATE;', sql)
        self.assertEqual(report["approved_tables"][0]["indexes"], 2)
        self.assertFalse(any(gap["kind"] == "index" for gap in report["blocked_gaps"]))


if __name__ == "__main__":
    unittest.main()
