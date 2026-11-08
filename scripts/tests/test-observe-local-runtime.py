#!/usr/bin/env python3
"""Exercise the observer CLI at the Docker/database boundary, without Docker."""
import json
import os
import shutil
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'observe-local-runtime.py'
FAKE_DOCKER = r'''#!/usr/bin/env python3
import json, os, re, sys
from pathlib import Path
args = sys.argv[1:]
scenario = os.environ.get('OBSERVER_SCENARIO', 'valid')
sql = sys.stdin.read() if 'exec' in args else ''
with open(os.environ['OBSERVER_LOG'], 'a') as log:
    log.write(json.dumps({'args': args, 'sql': sql}) + '\n')
assert args[:2] == ['--host', 'unix:///var/run/docker.sock'], args
args = args[2:]
network_id = 'd' * 64
fixture = json.loads(Path(os.environ['OBSERVER_FIXTURE']).read_text()) if os.environ.get('OBSERVER_FIXTURE') else {}
tables = ['objectdr', 'test'] if scenario == 'quoted-identifiers' else ['ALPHA', 'BETA']
if scenario == 'unicode-table': tables = ['检验表$#', 'test']
if scenario.startswith('bad-table-'):
    tables[0] = {'quote': 'bad"name', 'apostrophe': "bad'name", 'delimiter': 'bad|name',
                 'newline': 'bad\nname', 'semicolon': 'bad;delete', 'control': 'bad\x00name'}[scenario[10:]]
if scenario == 'docker-error':
    print('private-secret-must-not-escape', file=sys.stderr)
    sys.exit(1)
if scenario == 'invalid-json':
    print('invalid-private-json-material')
    sys.exit(0)
if args[:2] == ['network', 'inspect']:
    assert args[-1] == 'lims-local-runtime-isolated'
    print(json.dumps({'name': args[-1], 'id': network_id, 'internal': scenario != 'external-network'}))
elif args[:1] == ['inspect']:
    name = args[-1]
    assert name in ['lims-local-runtime-app', 'lims-local-runtime-oracle', 'lims-local-runtime-redis']
    suffix = name.rsplit('-', 1)[-1]
    data = {'name': '/' + name, 'id': {'app':'a','oracle':'b','redis':'c'}[suffix] * 64,
            'running': scenario != 'stopped-' + suffix, 'started_at': '2026-11-07T01:00:00Z',
            'image_id': 'sha256:' + 'e'*64, 'networks': {'lims-local-runtime-isolated': {'NetworkID': network_id}}}
    if scenario == 'wrong-name': data['name'] = '/other'
    if scenario == 'wrong-network-id': data['networks']['lims-local-runtime-isolated']['NetworkID'] = 'f' * 64
    if scenario == 'detached': data['networks'] = {}
    if scenario == 'external-attachment': data['networks']['bridge'] = {'NetworkID': 'f'*64}
    if scenario == 'identity-replaced':
        prior = [json.loads(line) for line in Path(os.environ['OBSERVER_LOG']).read_text().splitlines()]
        if any(c['sql'] for c in prior): data['id'] = 'f'*64
    print(json.dumps(data))
elif args[:1] == ['exec']:
    assert args == ['exec','-i','-e','NLS_LANG=.AL32UTF8','-u','oracle','lims-local-runtime-oracle','sh','-c',
                    'exec "$ORACLE_HOME/bin/sqlplus" -s / as sysdba'], args
    assert 'alter session set container = FREEPDB1;' in sql
    # Refuse lifecycle, business SQL writes and unexpected SQLPlus commands.
    for line in sql.splitlines():
        assert line.lower().startswith(('set ', 'whenever ', 'alter session set container = freepdb1;',
                                        'select ', 'prompt ', 'exit')), line
    if scenario == 'sql-error':
        print('ORA-00942: sensitive-material-must-not-escape')
        sys.exit(0)
    if scenario == 'empty-output': sys.exit(0)
    print('OBSERVE_BEGIN')
    if 'from dba_users' in sql:
        print('EXPECTED|2')
        print('USER|LIMS_BASELINE_LOCAL|' + ('OPEN' if scenario == 'baseline-open' else 'LOCKED'))
        print('USER|LIMS_RUNTIME_LOCAL|' + ('LOCKED' if scenario == 'runtime-locked' else 'OPEN'))
    elif "'FIXTURE|" in sql:
        records = fixture['checks']
        print('EXPECTED|' + str(len(records)))
        statements = [line for line in sql.splitlines() if line.startswith("select 'FIXTURE|")]
        tokens = {'defaults': 'DEFAULT_LENGTH IS NULL', 'pk': "CONSTRAINT_TYPE='P'",
                  'constraint': 'from ALL_CONSTRAINTS', 'index': 'from ALL_INDEXES',
                  'index-column': 'from ALL_IND_COLUMNS', 'comment': 'from ALL_COL_COMMENTS',
                  'trigger': 'from ALL_TRIGGERS'}
        for i, count in enumerate(records):
            mismatch = (scenario == 'fixture-mismatch' and i == 0)
            if scenario.startswith('metadata-mismatch-'):
                mismatch = tokens[scenario[len('metadata-mismatch-'):]] in statements[i]
            print(f'FIXTURE|{i}|' + str(count + (1 if mismatch else 0)))
    elif "'COLUMN|'" in sql:
        if fixture:
            rows = [[owner, *row] for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']
                    for row in fixture['columns'][owner]]
            print('EXPECTED|' + str(len(rows)))
            for row in rows: print('COLUMN|' + '|'.join(row))
            print('OBSERVE_END'); sys.exit(0)
        print('EXPECTED|' + ('5' if scenario == 'truncated-metadata' else '4'))
        for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']:
            for table in tables:
                name = 'BAD;DELETE' if scenario == 'invalid-identifier' else table
                column = '检验名称$#' if scenario == 'quoted-identifiers' else 'ID'
                if scenario.startswith('bad-column-'):
                    column = {'quote': 'bad"name', 'delimiter': 'bad|name', 'newline': 'bad\nname'}[scenario[11:]]
                print(f'COLUMN|{owner}|{name}|{column}|1|NUMBER|22|10|0|N|0|-|-')
    elif 'from dba_tables' in sql:
        if fixture:
            rows = [[owner, table] for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']
                    for table in sorted({row[0] for row in fixture['columns'][owner]})]
            if scenario == 'unknown-extra': rows.append(['LIMS_RUNTIME_LOCAL', 'UNKNOWN_EXTRA'])
            if scenario == 'missing-fixture': rows = [r for r in rows if r[1] != 'T_EMC_INVOICE']
            if scenario == 'base-removal': rows.remove(['LIMS_RUNTIME_LOCAL', 'BETA'])
            print('EXPECTED|' + str(len(rows)))
            for row in rows: print('TABLE|' + '|'.join(row))
            print('OBSERVE_END'); sys.exit(0)
        print('EXPECTED|' + ('0' if scenario == 'empty-tables' else '4'))
        if scenario != 'empty-tables':
            for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']:
                for table in tables:
                    if scenario == 'mismatched-tables' and owner == 'LIMS_RUNTIME_LOCAL' and table == 'BETA': table = 'GAMMA'
                    print(f'TABLE|{owner}|{table}')
    else:
        statements = re.findall(r"select 'COUNT\|([^']+)\|' \|\| to_char\(count\(\*\).*? from \"([^\"]+)\"\.\"([^\"]+)\";", sql, re.I)
        assert len(statements) == (sum(len({row[0] for row in rows}) for rows in fixture['columns'].values()) if fixture else 4), sql
        print('EXPECTED|' + str(len(statements)))
        for label, owner, table in statements:
            assert label == owner + '|' + table
            if scenario == 'missing-count' and owner == 'LIMS_RUNTIME_LOCAL' and table == 'BETA': continue
            n = 7 if table in ('ALPHA', 'objectdr') else 0
            if scenario == 'drift' and owner == 'LIMS_RUNTIME_LOCAL' and table == 'ALPHA': n = 8
            print(f'COUNT|{owner}|{table}|{n}')
    print('OBSERVE_END')
else:
    raise AssertionError(args)
'''


class ObserveLocalRuntimeTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.docker = self.root / 'docker'
        self.docker.write_text(FAKE_DOCKER)
        self.docker.chmod(0o755)
        self.log = self.root / 'boundary.jsonl'

    def run_cli(self, scenario='valid', *extra):
        env = dict(os.environ, OBSERVER_SCENARIO=scenario, OBSERVER_LOG=str(self.log))
        if hasattr(self, 'fixture_path'): env['OBSERVER_FIXTURE'] = str(self.fixture_path)
        result = subprocess.run([sys.executable, str(getattr(self, 'script', SCRIPT)), '--docker', str(self.docker), *extra],
                                env=env, text=True, capture_output=True, timeout=60)
        return result, json.loads(result.stdout) if result.stdout else None

    def enable_fixture(self, schema_policy='approved-missing-runtime-tables-v1'):
        sys.path.insert(0, str(SCRIPT.parent))
        import schema_fixture
        loaded = schema_fixture.load_fixture(SCRIPT.parents[1])
        if schema_policy == 'approved-missing-runtime-and-email-tables-v2':
            import local_schema_policy
            loaded.update(local_schema_policy.email_fixture(SCRIPT.parents[1])[0])
        columns = {owner: [[table, 'ID', '1', 'NUMBER', '22', '10', '0', 'N', '0', '-', '-']
                           for table in ('ALPHA', 'BETA')] for owner in ('LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL')}
        for table, definition in sorted(loaded.items()):
            for i, col in enumerate(definition['columns'], 1):
                length = col['length'] if col['type'] == 'VARCHAR2' else {'NUMBER': 22, 'DATE': 7, 'CLOB': 4000}[col['type']]
                columns['LIMS_RUNTIME_LOCAL'].append([table, col['name'], str(i), col['type'], str(length),
                    str(col['precision']) if col['precision'] is not None else '-',
                    str(col['scale']) if col['scale'] is not None else '-', col['nullable'],
                    str(col['length'] or 0), 'B' if col['type'] == 'VARCHAR2' else '-', '-'])
        self.fixture_path = self.root / 'oracle-metadata.json'
        self.fixture_path.write_text(json.dumps({'columns': columns,
            'checks': [count for table, definition in sorted(loaded.items()) for _, _, count in schema_fixture.checks(table, definition)]}))

    def test_explicit_email_policy_observes_nine_tables_and_validates_previous_observation(self):
        selected = 'approved-missing-runtime-and-email-tables-v2'
        self.enable_fixture(selected)
        result, report = self.run_cli('valid', '--schema-policy', selected)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(report['schemas']['LIMS_RUNTIME_LOCAL']['row_counts']['coverage']['expected_tables'], 11)
        self.assertEqual(report['schemas']['LIMS_RUNTIME_LOCAL']['column_metadata']['record_count'], 127)
        self.assertEqual(report['schema_policy']['metadata_proof']['record_count'], 389)
        self.assertEqual(report['runtime_contract']['schema_policy_id'], selected)
        path = self.root / 'before-email.json'
        path.write_text(json.dumps(report))
        result, observed = self.run_cli('valid', '--schema-policy', selected, '--before', str(path))
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertFalse(observed['comparison']['drift_detected'])
        for legacy in ((), ('--schema-policy', 'approved-missing-runtime-tables-v1')):
            result, rejected = self.run_cli('valid', *legacy)
            self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
            self.assertFalse(rejected['strict_evidence'])
        for scenario in ('unknown-extra', 'missing-fixture', 'base-removal', 'fixture-mismatch',
                         *['metadata-mismatch-' + kind for kind in ('defaults', 'pk', 'constraint', 'index', 'index-column', 'comment', 'trigger')]):
            with self.subTest(scenario=scenario):
                result, rejected = self.run_cli(scenario, '--schema-policy', selected)
                self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
                self.assertFalse(rejected['strict_evidence'])

    def test_explicit_source_policy_observes_exact_seven_tables_and_full_predicates(self):
        self.enable_fixture()
        result, report = self.run_cli('valid', '--schema-policy', 'approved-missing-runtime-tables-v1')
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual(report['schemas']['LIMS_BASELINE_LOCAL']['row_counts']['coverage']['expected_tables'], 2)
        self.assertEqual(report['schemas']['LIMS_RUNTIME_LOCAL']['row_counts']['coverage']['expected_tables'], 9)
        self.assertEqual(report['schemas']['LIMS_RUNTIME_LOCAL']['column_metadata']['record_count'], 107)
        self.assertEqual(report['schema_policy']['metadata_proof']['record_count'], 314)
        self.assertEqual(report['runtime_contract']['schema_policy_id'], 'approved-missing-runtime-tables-v1')
        sql = '\n'.join(json.loads(line)['sql'] for line in self.log.read_text().splitlines())
        for predicate in ('DEFAULT_LENGTH IS NULL', 'ALL_CONSTRAINTS', 'ALL_INDEXES', 'ALL_IND_COLUMNS',
                          'ALL_COL_COMMENTS', 'ALL_TRIGGERS', "HIDDEN_COLUMN='NO'"):
            self.assertIn(predicate, sql)
        self.assertNotIn('EXECUTE IMMEDIATE', sql)
        self.assertNotIn('CREATE TABLE', sql)

    def test_opt_in_rejects_unknown_extra_missing_fixture_and_removed_base_tables(self):
        self.enable_fixture()
        for scenario in ('unknown-extra', 'missing-fixture', 'base-removal', 'fixture-mismatch',
                         *['metadata-mismatch-' + kind for kind in ('defaults', 'pk', 'constraint', 'index', 'index-column', 'comment', 'trigger')]):
            with self.subTest(scenario=scenario):
                result, report = self.run_cli(scenario, '--schema-policy', 'approved-missing-runtime-tables-v1')
                self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
                self.assertEqual(report['status'], 'diagnostic-failed')
                self.assertFalse(report['strict_evidence'])
        result, report = self.run_cli('valid')
        self.assertEqual(result.returncode, 1)
        self.assertIn('inventories mismatch', report['error'])

    def test_previous_policy_observation_requires_fresh_pins_and_complete_query_proof(self):
        self.enable_fixture()
        result, before = self.run_cli('valid', '--schema-policy', 'approved-missing-runtime-tables-v1')
        self.assertEqual(result.returncode, 0, result.stderr)
        path = self.root / 'before.json'
        path.write_text(json.dumps(before))
        result, report = self.run_cli('valid', '--schema-policy', 'approved-missing-runtime-tables-v1', '--before', str(path))
        self.assertEqual(result.returncode, 0, result.stdout)
        self.assertFalse(report['comparison']['drift_detected'])
        before['schema_policy']['sources'][0]['sha256'] = '0'*64
        path.write_text(json.dumps(before))
        result, report = self.run_cli('valid', '--schema-policy', 'approved-missing-runtime-tables-v1', '--before', str(path))
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['error'], 'invalid previous observation')

    def test_changed_source_or_fixture_is_rejected_from_real_tool_files(self):
        self.enable_fixture()
        copied = self.root / 'tool-checkout'
        sys.path.insert(0, str(SCRIPT.parent))
        import schema_fixture
        paths = ['scripts/observe-local-runtime.py', 'scripts/local_schema_policy.py', 'scripts/schema_fixture.py',
                 'scripts/source_binding.py', schema_fixture.BASE + '.json', schema_fixture.BASE + '.sql',
                 *schema_fixture.SOURCE_PATHS]
        for relative in paths:
            target = copied / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(SCRIPT.parents[1] / relative, target)
        self.script = copied / 'scripts/observe-local-runtime.py'
        for relative in [schema_fixture.BASE + '.sql', *sorted(schema_fixture.SOURCE_PATHS)]:
            with self.subTest(path=relative):
                path = copied / relative
                original = path.read_bytes()
                path.write_bytes(original + b'\n-- drift\n')
                result, report = self.run_cli('valid', '--schema-policy', 'approved-missing-runtime-tables-v1')
                self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
                self.assertEqual(report['error'], 'approved schema fixture provenance or definition rejected')
                path.write_bytes(original)

    def test_observes_normalized_schema_and_actual_counts_without_claiming_restore(self):
        result, snapshot = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(snapshot['status'], 'diagnostic-observed')
        self.assertFalse(snapshot['strict_evidence'])
        self.assertFalse(snapshot['runtime_schema_restored'])
        self.assertFalse(snapshot['content_verified'])
        self.assertEqual(snapshot['application']['running'], True)
        baseline = snapshot['schemas']['LIMS_BASELINE_LOCAL']
        runtime = snapshot['schemas']['LIMS_RUNTIME_LOCAL']
        self.assertEqual(baseline['column_metadata']['record_count'], 2)
        self.assertEqual(baseline['column_metadata']['sha256'], runtime['column_metadata']['sha256'])
        self.assertEqual(runtime['row_counts']['rows'], [{'table': 'ALPHA', 'count': 7}, {'table': 'BETA', 'count': 0}])
        self.assertEqual(runtime['row_counts']['coverage'], {'expected_tables': 2, 'observed_tables': 2, 'complete': True})
        self.assertEqual(snapshot['runtime_contract']['runtime_profile'], 'local-wsl')
        self.assertEqual(snapshot['runtime_contract']['baseline_key_data_hash'], 'unbound')
        self.assertFalse(snapshot['runtime_contract']['runtime_schema_restored'])
        self.assertFalse(snapshot['runtime_contract']['baseline_schema_immutable'])
        self.assertTrue(snapshot['baseline_account_locked'])
        self.assertEqual(snapshot['baseline_immutability'], 'not-verified')
        self.assertTrue(all(len(q['sql_sha256']) == 64 for q in snapshot['queries']))
        calls = [json.loads(line) for line in self.log.read_text().splitlines()]
        count_sql = '\n'.join(c['sql'] for c in calls if "'COUNT|" in c['sql'])
        self.assertIn('count(*)', count_sql.lower())
        self.assertNotIn('num_rows', count_sql.lower())
        self.assertTrue(all(c['args'][2] in ['inspect', 'network', 'exec'] for c in calls))


    def test_preserves_quoted_lowercase_tables_and_unicode_column_metadata(self):
        result, snapshot = self.run_cli('quoted-identifiers')
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        columns = snapshot['schemas']['LIMS_RUNTIME_LOCAL']['column_metadata']['rows']
        self.assertEqual([row[:2] for row in columns], [['objectdr', '检验名称$#'], ['test', '检验名称$#']])
        self.assertEqual(snapshot['schemas']['LIMS_RUNTIME_LOCAL']['row_counts']['rows'],
                         [{'table': 'objectdr', 'count': 7}, {'table': 'test', 'count': 0}])
        calls = [json.loads(line) for line in self.log.read_text().splitlines()]
        generated_sql = '\n'.join(c['sql'] for c in calls if "'COUNT|" in c['sql'])
        self.assertIn('from "LIMS_RUNTIME_LOCAL"."objectdr";', generated_sql)
        self.assertIn('from "LIMS_BASELINE_LOCAL"."test";', generated_sql)

    def test_quotes_unicode_table_identifiers_and_hashes_actual_utf8_sql(self):
        result, snapshot = self.run_cli('unicode-table')
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertEqual({row['table'] for row in snapshot['schemas']['LIMS_RUNTIME_LOCAL']['row_counts']['rows']},
                         {'检验表$#', 'test'})
        calls = [json.loads(line) for line in self.log.read_text().splitlines()]
        self.assertTrue(any('from "LIMS_RUNTIME_LOCAL"."检验表$#";' in call['sql'] for call in calls))

    def test_rejects_identifier_injection_before_generating_count_sql(self):
        for scenario in ('bad-table-quote', 'bad-table-apostrophe', 'bad-table-delimiter',
                         'bad-table-newline', 'bad-table-semicolon', 'bad-table-control',
                         'bad-column-quote', 'bad-column-delimiter', 'bad-column-newline'):
            with self.subTest(scenario=scenario):
                self.log.write_text('')
                result, report = self.run_cli(scenario)
                self.assertEqual(result.returncode, 1)
                self.assertEqual(report['status'], 'diagnostic-failed')
                calls = [json.loads(line) for line in self.log.read_text().splitlines()]
                self.assertFalse(any("'COUNT|" in call['sql'] for call in calls))

    def test_fails_closed_for_missing_stopped_detached_or_nonisolated_runtime(self):
        for scenario in ('stopped-app', 'stopped-oracle', 'stopped-redis', 'external-network',
                         'wrong-name', 'wrong-network-id', 'detached', 'external-attachment',
                         'docker-error', 'invalid-json', 'identity-replaced'):
            with self.subTest(scenario=scenario):
                result, report = self.run_cli(scenario)
                self.assertEqual(result.returncode, 1)
                self.assertEqual(report['status'], 'diagnostic-failed')
                self.assertFalse(report['strict_evidence'])
                self.assertNotIn('schemas', report)
                self.assertNotIn('private-', result.stdout + result.stderr)

    def test_rejects_empty_truncated_error_unlocked_baseline_and_incomplete_counts(self):
        for scenario in ('sql-error', 'empty-output', 'baseline-open', 'runtime-locked',
                         'truncated-metadata', 'invalid-identifier', 'empty-tables',
                         'mismatched-tables', 'missing-count'):
            with self.subTest(scenario=scenario):
                result, report = self.run_cli(scenario)
                self.assertEqual(result.returncode, 1)
                self.assertEqual(report['status'], 'diagnostic-failed')
                self.assertNotIn('schemas', report)
                self.assertNotIn('sensitive-material', result.stdout + result.stderr)

    def test_detects_actual_count_drift_but_does_not_claim_content_comparison(self):
        result, before = self.run_cli()
        self.assertEqual(result.returncode, 0)
        path = self.root / 'before.json'
        path.write_text(json.dumps(before))
        result, unchanged = self.run_cli('valid', '--before', str(path))
        self.assertEqual(result.returncode, 0)
        self.assertFalse(unchanged['comparison']['drift_detected'])
        result, after = self.run_cli('drift', '--before', str(path))
        self.assertEqual(result.returncode, 0)
        self.assertEqual(after['comparison']['changed_sections'], ['schemas'])
        self.assertTrue(after['comparison']['drift_detected'])
        self.assertEqual(after['comparison']['content_comparison'], 'not-performed')
        self.assertFalse(after['strict_evidence'])

    def test_rejects_incomplete_previous_snapshot(self):
        path = self.root / 'incomplete.json'
        path.write_text(json.dumps({'kind': 'local-runtime-observation', 'status': 'diagnostic-observed',
                                    'schemas': {'LIMS_BASELINE_LOCAL': {}, 'LIMS_RUNTIME_LOCAL': {}}}))
        result, report = self.run_cli('valid', '--before', str(path))
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['status'], 'diagnostic-failed')


if __name__ == '__main__':
    unittest.main()
