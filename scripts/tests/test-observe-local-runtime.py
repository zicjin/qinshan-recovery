#!/usr/bin/env python3
"""Exercise the observer CLI at the Docker/database boundary, without Docker."""
import json
import os
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
    elif "'COLUMN|'" in sql:
        print('EXPECTED|' + ('5' if scenario == 'truncated-metadata' else '4'))
        for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']:
            for table in tables:
                name = 'BAD;DELETE' if scenario == 'invalid-identifier' else table
                column = '检验名称$#' if scenario == 'quoted-identifiers' else 'ID'
                if scenario.startswith('bad-column-'):
                    column = {'quote': 'bad"name', 'delimiter': 'bad|name', 'newline': 'bad\nname'}[scenario[11:]]
                print(f'COLUMN|{owner}|{name}|{column}|1|NUMBER|22|10|0|N|0|-|-')
    elif 'from dba_tables' in sql:
        print('EXPECTED|' + ('0' if scenario == 'empty-tables' else '4'))
        if scenario != 'empty-tables':
            for owner in ['LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL']:
                for table in tables:
                    if scenario == 'mismatched-tables' and owner == 'LIMS_RUNTIME_LOCAL' and table == 'BETA': table = 'GAMMA'
                    print(f'TABLE|{owner}|{table}')
    else:
        statements = re.findall(r"select 'COUNT\|([^']+)\|' \|\| to_char\(count\(\*\).*? from \"([^\"]+)\"\.\"([^\"]+)\";", sql, re.I)
        assert len(statements) == 4, sql
        print('EXPECTED|4')
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
        result = subprocess.run([sys.executable, str(SCRIPT), '--docker', str(self.docker), *extra],
                                env=env, text=True, capture_output=True, timeout=60)
        return result, json.loads(result.stdout) if result.stdout else None

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
