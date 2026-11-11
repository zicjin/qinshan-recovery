#!/usr/bin/env python3
"""Finite opt-in external runtime schema policy; no live DB or DDL execution."""
import copy
import importlib.util
import json
from pathlib import Path
import shutil
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import local_schema_policy as policy

V6 = 'approved-runtime-external-schema-v6'
TABLES = {'T_CRM_CONTRACT', 'T_CRM_INVOICE_PROJ', 'T_CRM_INVOICE', 'T_CRM_PAY_PROJECT',
          'T_CRM_PAYMENT', 'T_LIMS_CLEARANCEFORM', 'T_LIMS_FOLDERS', 'T_XFP_ORDER_TASK',
          'T_XFP_PROJECT', 'T_XFP_REPORT', 'T_CRM_CONTRACT_PACKPRICE'}
spec = importlib.util.spec_from_file_location('v5_fixture_tests', Path(__file__).with_name('test-production-runtime-schema-policy.py'))
previous = importlib.util.module_from_spec(spec)
spec.loader.exec_module(previous)


def snapshot():
    result = previous.snapshot()
    runtime = result['schemas'][previous.RUNTIME]
    structure = json.loads((ROOT / 'deployment/oracle/external-schema/structure.json').read_text())
    for table in structure['tables']:
        runtime['column_metadata']['rows'].extend([[table['name'], c['name'], str(c['position']),
            c['type'], c['length'], c['precision'] or '-', c['scale'] or '-', c['nullable'],
            c['char_length'], c['char_used'] or '-', '-'] for c in table['columns']])
    pack = json.loads((ROOT / 'deployment/report-engine/dependencies/external-contract-packprice-20261111.json').read_text())
    item = pack['metadata']['columns']
    for row in item['rows']:
        c = dict(zip(item['columns'], row))
        runtime['column_metadata']['rows'].append([pack['object'], c['COLUMN_NAME'], c['COLUMN_ID'],
            c['DATA_TYPE'], c['DATA_LENGTH'], c['DATA_PRECISION'] or '-', c['DATA_SCALE'] or '-',
            c['NULLABLE'], c['CHAR_LENGTH'], c['CHAR_USED'] or '-', '-'])
    runtime['column_metadata']['rows'].sort()
    runtime['row_counts']['rows'].extend({'table': table, 'count': 7} for table in sorted(TABLES))
    runtime['row_counts']['rows'].sort(key=lambda r: r['table'])
    return previous.install_proof(result, V6)


class ExternalRuntimePolicyTest(unittest.TestCase):
    def test_v6_adds_only_eleven_formal_tables_to_unchanged_v5(self):
        descriptor, predicates = policy.definition(V6)
        old, old_predicates = policy.definition(previous.V5)
        self.assertEqual(policy.digest(old), '1922961ae904d83fb1a4f434fa242c7a07f3bf4842a2c581a335a232aad88bc2')
        self.assertEqual(descriptor['table_count'], 23)
        self.assertEqual(descriptor['column_count'], 1473)
        self.assertEqual(set(descriptor['external_schema']['tables']), TABLES)
        self.assertEqual(predicates[:len(old_predicates)], old_predicates)
        baseline = {'ORIGINAL', 'T_CORE_FILE'}
        runtime = baseline | set(old['definition_sha256']) | TABLES
        policy.check_inventories(baseline, runtime, V6)
        for selected in (policy.EQUAL, previous.V5):
            with self.subTest(policy=selected), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, runtime, selected)
        for delta in ({'UNAPPROVED'}, {'T_CRM_PAYMENT'}):
            with self.subTest(delta=delta), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, runtime ^ delta, V6)
        with self.assertRaises(policy.PolicyError):
            policy.check_inventories(baseline | {'T_CRM_PAYMENT'}, runtime, V6)

    def test_observe_checks_real_default_text_complete_constraints_and_indexes(self):
        descriptor, predicates = policy.definition(V6)
        additions = predicates[len(policy.definition(previous.V5)[1]):]
        self.assertTrue(additions)
        text = '\n'.join(policy.proof_selects(additions))
        self.assertIn('DBMS_XMLGEN.GETXMLTYPE', text)
        self.assertIn('TRIM(DEFAULT_TEXT)', text)
        self.assertIn('SEARCH_CONDITION_VC', text)
        self.assertIn("VALIDATED='NOT VALIDATED'", text)
        self.assertIn("INDEX_NAME='IDX_T_LIMS_FOLDERS_FOLDERNO'", text)
        self.assertIn("UNIQUENESS='NONUNIQUE'", text)
        self.assertIn("CHAR_USED='C'", text)
        for view in ('ALL_IND_EXPRESSIONS', 'ALL_TRIGGERS', 'NLS_DATABASE_PARAMETERS',
                     'NLS_SESSION_PARAMETERS'):
            self.assertTrue(any(p['view'] == view for p in additions), view)
        self.assertFalse(any(p['view'] == '"LIMS_RUNTIME_LOCAL"."T_CRM_PAYMENT"' for p in additions))
        # Simulate the public query boundary. A missing or equal-length changed
        # default, extra constraint/index/trigger or semantic drift changes a
        # single exact predicate count; rehashed observations must still reject.
        def query(name, selects, prefix, width):
            self.assertEqual((name, prefix, width), ('approved-schema-metadata', 'FIXTURE', 2))
            self.assertEqual(selects, policy.proof_selects(predicates))
            return [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
        observed = policy.observe(V6, query)
        self.assertEqual(observed['metadata_proof']['record_count'], len(predicates))
        selectors = {
            'missing-default': lambda p: 'DBMS_XMLGEN' in p['view'],
            'same-length-default': lambda p: 'DBMS_XMLGEN' in p['view'],
            'extra-constraint': lambda p: p['view'] == 'ALL_CONSTRAINTS' and p['expected_count'] == 3,
            'extra-index': lambda p: p['view'] == 'ALL_INDEXES' and p['expected_count'] == 3,
            'expression': lambda p: p['view'] == 'ALL_IND_EXPRESSIONS',
            'trigger': lambda p: p['view'] == 'ALL_TRIGGERS',
            'table-semantics': lambda p: p['view'] == 'ALL_TABLES',
            'hidden-virtual': lambda p: p['view'] == 'ALL_TAB_COLS' and 'HIDDEN_COLUMN' in p['predicate'],
            'nls': lambda p: p['view'] == 'NLS_DATABASE_PARAMETERS',
        }
        for label, select in selectors.items():
            target = next(p for p in additions if select(p))
            index = predicates.index(target)
            def bad_query(*args):
                rows = query(*args)
                rows[index][1] = str(0 if label in ('missing-default', 'same-length-default')
                                     else target['expected_count'] + 1)
                return rows
            with self.subTest(drift=label), self.assertRaises(policy.PolicyError):
                policy.observe(V6, bad_query)

    def test_validate_accepts_data_and_preserves_exact_baseline_and_v5_shared_contract(self):
        result = snapshot()
        self.assertEqual(policy.validate(result, V6)['id'], V6)
        for owner, table, column in [(previous.BASELINE, 'T_CORE_FILE', 'ID'),
                                     (previous.RUNTIME, 'ORIGINAL', 'ID'),
                                     (previous.RUNTIME, 'T_CORE_FILE', 'NAME'),
                                     (previous.RUNTIME, 'T_CRM_CONTRACT', 'ID'),
                                     (previous.RUNTIME, 'T_CRM_CONTRACT_PACKPRICE', 'ID')]:
            mutated = copy.deepcopy(result)
            row = previous.row_in(mutated, owner, table, column)
            row[4] = '999'
            previous.previous.existing.seal_snapshot(mutated)
            with self.subTest(table=table), self.assertRaises(policy.PolicyError):
                policy.validate(mutated, V6)

    def test_formal_pins_are_required_without_private_inputs(self):
        with tempfile.TemporaryDirectory() as temporary:
            copied = Path(temporary)
            previous.copy_pinned_inputs(copied)
            paths = set(policy.EXTERNAL_PINS)
            external = json.loads((ROOT / 'deployment/oracle/external-schema/manifest.json').read_text())
            report = json.loads((ROOT / 'deployment/report-engine/manifest.json').read_text())
            paths.update('deployment/oracle/external-schema/' + i['path'] for i in external['files'])
            paths.update('deployment/report-engine/' + i['path'] for i in report['dependencies'])
            for name in paths:
                target = copied / name
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / name, target)
            self.assertFalse((copied / 'private').exists())
            policy.definition(V6, copied)
            for name in sorted(paths):
                path = copied / name
                raw = path.read_bytes()
                path.write_bytes(raw + b'\n')
                with self.subTest(path=name), self.assertRaises(policy.PolicyError):
                    policy.definition(V6, copied)
                path.write_bytes(raw)

    def test_default_expressions_and_non_lob_index_closure_are_explicit(self):
        _, predicates = policy.definition(V6)
        additions = predicates[len(policy.definition(previous.V5)[1]):]
        defaults = [p for p in additions if 'DBMS_XMLGEN' in p['view']]
        self.assertEqual(len(defaults), 33)
        isred = next(p for p in defaults if p['table'] == 'T_CRM_INVOICE'
                     and "COLUMN_NAME=''ISRED''" in p['view'])
        self.assertEqual(isred['predicate'], "RAWTOHEX(UTL_RAW.CAST_TO_RAW(TRIM(DEFAULT_TEXT)))='273027'")
        self.assertEqual(isred['expected_count'], 1)
        # Exact expected totals independently taken from delivered inputs.
        for table, constraints, indexes in [('T_CRM_CONTRACT', 3, 3),
                ('T_LIMS_CLEARANCEFORM', 2, 13), ('T_LIMS_FOLDERS', 5, 8),
                ('T_CRM_CONTRACT_PACKPRICE', 2, 1)]:
            closure = [p for p in additions if p['table'] == table]
            self.assertTrue(any(p['view'] == 'ALL_CONSTRAINTS'
                and p['predicate'] == "OWNER='LIMS_RUNTIME_LOCAL' AND TABLE_NAME='" + table + "'"
                and p['expected_count'] == constraints for p in closure))
            self.assertTrue(any(p['view'] == 'ALL_INDEXES' and p['expected_count'] == indexes
                and p['predicate'].endswith("INDEX_TYPE<>'LOB'") for p in closure))
        generated = [p for p in additions if 'GENERATED NAME' in p['predicate']]
        self.assertTrue(generated)
        self.assertEqual({p['table'] for p in generated}, {'T_CRM_CONTRACT_PACKPRICE'})
        self.assertFalse(any('SYS_C0016140' in p['predicate'] for p in additions))

    def test_resealed_proof_and_query_tampering_remain_rejected(self):
        original = snapshot()
        for mutation in ('default-proof', 'constraint-proof', 'index-proof', 'query-sql',
                         'query-coverage', 'query-missing', 'query-duplicate', 'runtime-binding'):
            result = copy.deepcopy(original)
            if mutation.endswith('proof'):
                proof = result['schema_policy']['metadata_proof']
                rows = proof['rows']
                row = next(r for r in rows if r['table'] in TABLES and
                    (('DBMS_XMLGEN' in r['view']) if mutation == 'default-proof' else
                     r['view'] == ('ALL_CONSTRAINTS' if mutation == 'constraint-proof' else 'ALL_INDEXES')))
                row['observed_count'] += 1
                proof['sha256'] = policy.digest(rows)
            elif mutation == 'query-sql': result['queries'][0]['sql_sha256'] = '0' * 64
            elif mutation == 'query-coverage': result['queries'][0]['coverage_complete'] = False
            elif mutation == 'query-missing': result['queries'] = []
            elif mutation == 'query-duplicate': result['queries'].append(copy.deepcopy(result['queries'][0]))
            else: result['runtime_contract']['schema_policy_id'] = previous.V5
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, V6)


if __name__ == '__main__':
    unittest.main()
