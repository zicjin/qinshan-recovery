#!/usr/bin/env python3
"""Test the milestone CLI against immutable files and a fake Docker boundary."""
import copy
import hashlib
import json
import os
from pathlib import Path
import struct
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'verify-local-runtime-milestone.py'
NETWORK = 'lims-local-runtime-isolated'
OWNERS = ('LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL')


def digest(value):
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(',', ':'), ensure_ascii=True).encode('ascii')).hexdigest()


def fingerprint(owner, content='1'*64):
    # Independent worked v1 framing follows the published Java snapshot format.
    column = {'name': 'ID', 'native_type': 'NUMBER', 'jdbc_type': 2,
              'precision': 10, 'scale': 0, 'nullable': 0}
    tables = [{'name': table, 'columns': [column.copy()], 'row_count': 7,
               'content_sha256': content if table == 'ALPHA' else '2'*64}
              for table in ('ALPHA', 'AUDIT')]
    result = {'format': 'oracle-content-sha256-v1', 'schema': owner,
              'transaction_read_only': True, 'tables': tables, 'type_coverage': [2]}
    seal_fingerprint(result)
    return result


def seal_fingerprint(result):
    def frame(tag, payload):
        tag = tag.encode('utf-8')
        return struct.pack('>i', len(tag)) + tag + struct.pack('>q', len(payload)) + payload
    data = frame('SCHEMA-v1', b'')
    for table in result['tables']:
        data += frame('TABLE', table['name'].encode('utf-8'))
        for column in table['columns']:
            for i, key in enumerate(('name', 'native_type', 'jdbc_type', 'precision', 'scale', 'nullable')):
                data += frame('COLUMN-' + str(i), str(column[key]).encode('utf-8'))
        data += frame('COUNT', str(table['row_count']).encode('ascii'))
        data += frame('CONTENT', bytes.fromhex(table['content_sha256']))
    result['schema_sha256'] = hashlib.sha256(data).hexdigest()


def observation(at):
    containers = {role: {'name': 'lims-local-runtime-' + role, 'id': identity*64,
                         'running': True, 'started_at': '2026-11-07T01:00:00Z',
                         'image_id': 'sha256:' + 'e'*64}
                  for role, identity in (('app', 'a'), ('oracle', 'b'), ('redis', 'c'))}
    columns = [[table, 'ID', '1', 'NUMBER', '22', '10', '0', 'N', '0', '-', '-']
               for table in ('ALPHA', 'AUDIT')]
    counts = [{'table': table, 'count': 7} for table in ('ALPHA', 'AUDIT')]
    schemas = {owner: {'account_status': 'LOCKED' if owner == OWNERS[0] else 'OPEN',
                       'column_metadata': {'rows': columns, 'record_count': 2, 'sha256': digest(columns)},
                       'row_counts': {'rows': counts, 'sha256': digest(counts),
                                      'coverage': {'expected_tables': 2, 'observed_tables': 2, 'complete': True}}}
               for owner in OWNERS}
    return {'schema_version': 1, 'kind': 'local-runtime-observation', 'status': 'diagnostic-observed',
            'strict_evidence': False, 'execution_location': 'local-wsl-docker', 'observed_at': at,
            'containers': containers, 'application': containers['app'], 'schemas': schemas,
            'network': {'name': NETWORK, 'id': 'd'*64, 'internal': True}}


FAKE_DOCKER = r'''#!/usr/bin/env python3
import json, os, sys
args = sys.argv[1:]
assert args[:2] == ['--host', 'unix:///var/run/docker.sock']
assert not any(k in os.environ for k in ('DOCKER_HOST', 'DOCKER_CONTEXT', 'DOCKER_TLS_VERIFY', 'DOCKER_CERT_PATH'))
args = args[2:]
with open(os.environ['MILESTONE_LOG'], 'a') as log: log.write(json.dumps(args) + '\n')
scenario = os.environ.get('MILESTONE_SCENARIO', 'valid')
if scenario == 'error':
    print('private-secret', file=sys.stderr); sys.exit(1)
if scenario == 'mutate-input':
    path = __import__('pathlib').Path(os.environ['MILESTONE_LOG']).parent / 'runtime_after.json'
    path.write_text(path.read_text() + ' ')
if args[:2] == ['network', 'inspect']:
    assert args[-1] == 'lims-local-runtime-isolated'
    print(json.dumps({'name': args[-1], 'id': 'd'*64, 'internal': scenario != 'external'}))
elif args[:1] == ['inspect']:
    role = args[-1].rsplit('-', 1)[-1]
    assert args[-1] == 'lims-local-runtime-' + role and role in ('app', 'oracle', 'redis')
    item = {'name': '/' + args[-1], 'id': {'app': 'a', 'oracle': 'b', 'redis': 'c'}[role]*64,
            'running': role != 'app' or scenario == 'app-running',
            'started_at': '2026-11-07T01:00:00Z', 'image_id': 'sha256:' + 'e'*64,
            'networks': {'lims-local-runtime-isolated': {'NetworkID': 'd'*64}}}
    if scenario == 'restart-' + role: item['started_at'] = '2026-11-07T03:00:00Z'
    if scenario == 'replace-' + role: item['id'] = 'f'*64
    if scenario == 'extra-network': item['networks']['bridge'] = {}
    if scenario == 'stopped-' + role: item['running'] = False
    print(json.dumps(item))
else:
    raise AssertionError('Only read-only inspection is permitted')
'''


class LocalRuntimeMilestoneTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.docker = self.root / 'docker'
        self.docker.write_text(FAKE_DOCKER)
        self.docker.chmod(0o755)
        self.log = self.root / 'boundary.jsonl'
        self.inputs = {
            'observation_before': observation('2026-11-07T02:00:00+00:00'),
            'observation_after': observation('2026-11-07T04:00:00+00:00'),
            'baseline_before': fingerprint(OWNERS[0]), 'baseline_after': fingerprint(OWNERS[0]),
            # Runtime intentionally differs from baseline; its own before is the target.
            'runtime_before': fingerprint(OWNERS[1], '3'*64), 'runtime_after': fingerprint(OWNERS[1], '3'*64),
        }
        self.write_manifest()

    def write_manifest(self):
        files = {}
        for role, data in self.inputs.items():
            raw = (json.dumps(data) + '\n').encode()
            (self.root / (role + '.json')).write_bytes(raw)
            files[role] = {'path': role + '.json', 'sha256': hashlib.sha256(raw).hexdigest()}
        self.manifest = {'schema_version': 1, 'kind': 'local-runtime-milestone-inputs', 'files': files}
        self.save_manifest()

    def save_manifest(self):
        (self.root / 'manifest.json').write_text(json.dumps(self.manifest))

    def run_cli(self, scenario='valid'):
        env = dict(os.environ, MILESTONE_SCENARIO=scenario, MILESTONE_LOG=str(self.log),
                   DOCKER_HOST='tcp://forbidden.invalid', DOCKER_CONTEXT='remote', DOCKER_TLS_VERIFY='1')
        result = subprocess.run([sys.executable, str(SCRIPT), '--manifest', str(self.root / 'manifest.json'),
                                 '--docker', str(self.docker)], env=env, capture_output=True, text=True, timeout=30)
        return result, json.loads(result.stdout) if result.stdout else None

    def test_validates_content_restoration_and_actual_cleanup_without_strict_claim(self):
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        self.assertEqual(report['status'], 'diagnostic-lifecycle-verified')
        self.assertTrue(report['baseline_schema_immutable'])
        self.assertTrue(report['runtime_schema_restored'])
        self.assertTrue(report['application_stopped'])
        self.assertTrue(report['oracle_reused'])
        self.assertTrue(report['redis_reused'])
        self.assertFalse(report['runtime_dirty'])
        self.assertFalse(report['strict_evidence'])
        self.assertFalse(report['real_http'])
        self.assertEqual(report['fixture_restore'], 'not-verified')
        self.assertTrue(report['lifecycle_verified'])
        self.assertEqual(report['source_binding']['runtime_contract']['runtime_profile'], 'local-wsl')
        self.assertEqual(report['source_binding']['runtime_contract']['oracle_instance_id'], 'lims-local-runtime-oracle')
        self.assertEqual(report['runtime_identity']['container_ids']['oracle'], 'b'*64)
        self.assertEqual(set(report['inputs']), set(self.inputs))
        self.assertEqual(report['comparisons']['runtime']['changed_tables'], [])
        self.assertTrue(self.log.exists())

    def test_rejects_precision_metadata_disagreement_between_content_and_observer(self):
        for role in ('runtime_before', 'runtime_after'):
            self.inputs[role]['tables'][0]['columns'][0]['precision'] = 9
            seal_fingerprint(self.inputs[role])
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1, result.stdout)
        self.assertFalse(report['runtime_schema_restored'])

    def test_equal_row_counts_cannot_hide_runtime_content_changes_including_audit(self):
        for index in (0, 1):
            with self.subTest(table=index):
                self.inputs['runtime_after'] = fingerprint(OWNERS[1], '3'*64)
                self.inputs['runtime_after']['tables'][index]['content_sha256'] = '9'*64
                seal_fingerprint(self.inputs['runtime_after'])
                self.write_manifest()
                result, report = self.run_cli()
                self.assertEqual(result.returncode, 1, result.stderr)
                self.assertTrue(report['baseline_schema_immutable'])
                self.assertFalse(report['runtime_schema_restored'])
                self.assertTrue(report['runtime_dirty'])
                self.assertEqual(report['comparisons']['runtime']['changed_tables'][0]['changed_fields'], ['content_sha256'])
                self.assertEqual(report['comparisons']['runtime']['changed_tables'][0]['table'], ('ALPHA', 'AUDIT')[index])

    def test_baseline_content_change_is_independent_of_runtime_restoration(self):
        self.inputs['baseline_after']['tables'][0]['content_sha256'] = '8'*64
        seal_fingerprint(self.inputs['baseline_after'])
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1)
        self.assertTrue(report['runtime_schema_restored'])
        self.assertFalse(report['baseline_schema_immutable'])
        self.assertEqual(report['failed_checks'], ['baseline_schema_immutable'])

    def test_reports_changed_counts_and_content(self):
        content = self.inputs['runtime_after']
        content['tables'][0].update(row_count=8, content_sha256='8'*64)
        seal_fingerprint(content)
        schema = self.inputs['observation_after']['schemas'][OWNERS[1]]
        schema['row_counts'] = copy.deepcopy(schema['row_counts'])
        schema['row_counts']['rows'][0]['count'] = 8
        schema['row_counts']['sha256'] = digest(schema['row_counts']['rows'])
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['comparisons']['runtime']['changed_tables'][0]['changed_fields'], ['row_count', 'content_sha256'])

    def test_refuses_to_verify_cleanup_when_user_application_is_running(self):
        result, report = self.run_cli('app-running')
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['failed_checks'], ['application_stopped'])
        self.assertTrue(report['cleanup_observation']['containers']['app']['running'])
        self.assertFalse(report['application_stopped'])
        self.assertTrue(report['runtime_dirty'])

    def test_rejects_restarted_replaced_stopped_or_nonisolated_services(self):
        for scenario in ('restart-oracle', 'restart-redis', 'replace-oracle', 'replace-redis',
                         'replace-app', 'stopped-oracle', 'stopped-redis', 'external', 'extra-network', 'error'):
            with self.subTest(scenario=scenario):
                result, report = self.run_cli(scenario)
                self.assertEqual(result.returncode, 1, result.stdout)
                self.assertEqual(report['status'], 'diagnostic-failed')
                self.assertFalse(report['strict_evidence'])
                self.assertTrue(report['runtime_dirty'])
                self.assertNotIn('private-secret', result.stdout + result.stderr)

    def test_rejects_content_tampering_against_raw_pin(self):
        path = self.root / 'runtime_after.json'
        path.write_text(path.read_text() + ' ')
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['error'], 'snapshot raw SHA256 mismatch')
        self.assertFalse(self.log.exists())

    def test_rejects_semantic_hash_tampering_even_with_updated_file_pin(self):
        self.inputs['runtime_after']['tables'][0]['content_sha256'] = '8'*64
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['error'], 'content schema hash mismatch')
        self.assertFalse(self.log.exists())

    def test_rejects_missing_duplicate_and_wrong_schema_content(self):
        original = copy.deepcopy(self.inputs['runtime_after'])
        for case in ('missing-table', 'duplicate-table', 'wrong-owner', 'unsupported-type', 'not-readonly'):
            with self.subTest(case=case):
                content = copy.deepcopy(original)
                if case == 'missing-table': content['tables'].pop()
                if case == 'duplicate-table': content['tables'][1] = copy.deepcopy(content['tables'][0])
                if case == 'wrong-owner': content['schema'] = OWNERS[0]
                if case == 'unsupported-type': content['tables'][0]['columns'][0]['jdbc_type'] = 2002
                if case == 'not-readonly': content['transaction_read_only'] = False
                seal_fingerprint(content)
                self.inputs['runtime_after'] = content
                self.write_manifest()
                result, report = self.run_cli()
                self.assertEqual(result.returncode, 1, result.stdout)
                self.assertFalse(report['runtime_schema_restored'])
                self.assertFalse(self.log.exists())

    def test_rejects_unlocked_baseline_missing_tables_and_changed_instance_observations(self):
        original = copy.deepcopy(self.inputs['observation_after'])
        for case in ('unlocked', 'missing-table', 'restarted', 'wrong-schema', 'bad-column-hash', 'reversed-time'):
            with self.subTest(case=case):
                after = copy.deepcopy(original)
                if case == 'unlocked': after['schemas'][OWNERS[0]]['account_status'] = 'OPEN'
                if case == 'missing-table': after['schemas'][OWNERS[1]]['row_counts']['rows'].pop()
                if case == 'restarted': after['containers']['oracle']['started_at'] = '2026-11-07T03:00:00Z'
                if case == 'wrong-schema': after['schemas']['OTHER'] = after['schemas'].pop(OWNERS[1])
                if case == 'bad-column-hash': after['schemas'][OWNERS[1]]['column_metadata']['sha256'] = 'f'*64
                if case == 'reversed-time': after['observed_at'] = '2026-11-07T01:00:00Z'
                self.inputs['observation_after'] = after
                self.write_manifest()
                result, report = self.run_cli()
                self.assertEqual(result.returncode, 1, result.stdout)
                self.assertFalse(self.log.exists())

    def test_rejects_symlink_inputs_parent_symlinks_and_path_escape(self):
        original = copy.deepcopy(self.manifest)
        for mode in ('symlink', 'parent-symlink', 'escape', 'absolute'):
            with self.subTest(mode=mode):
                self.manifest = copy.deepcopy(original)
                entry = self.manifest['files']['runtime_after']
                if mode == 'symlink':
                    (self.root / 'link.json').symlink_to(self.root / 'runtime_after.json')
                    entry['path'] = 'link.json'
                elif mode == 'parent-symlink':
                    (self.root / 'linked').symlink_to(self.root, target_is_directory=True)
                    entry['path'] = 'linked/runtime_after.json'
                elif mode == 'escape': entry['path'] = '../runtime_after.json'
                else: entry['path'] = str(self.root / 'runtime_after.json')
                self.save_manifest()
                result, report = self.run_cli()
                self.assertEqual(result.returncode, 1, result.stdout)
                self.assertFalse(self.log.exists())

    def test_rejects_duplicate_json_keys_and_incomplete_manifest(self):
        for text in ('{"schema_version":1,"schema_version":1}', '{}'):
            (self.root / 'manifest.json').write_text(text)
            result, report = self.run_cli()
            self.assertEqual(result.returncode, 1)
            self.assertFalse(self.log.exists())

    def test_matches_oracle_timestamp_declaration_to_jdbc_timestamp_without_name_normalization(self):
        for snapshot in self.inputs.values():
            if snapshot.get('kind') == 'local-runtime-observation':
                for schema in snapshot['schemas'].values():
                    schema['column_metadata'] = copy.deepcopy(schema['column_metadata'])
                    row = schema['column_metadata']['rows'][0]
                    row[3], row[4], row[5], row[6] = 'TIMESTAMP(6)', '11', '-', '6'
                    schema['column_metadata']['sha256'] = digest(schema['column_metadata']['rows'])
            else:
                column = snapshot['tables'][0]['columns'][0]
                column.update(native_type='TIMESTAMP', jdbc_type=93, precision=26, scale=6)
                snapshot['type_coverage'] = [2, 93]
                seal_fingerprint(snapshot)
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stdout)

    def test_accepts_actual_oracle_jdbc_lob_number_date_and_timestamp_metadata(self):
        cases = [('CLOB', 2005, -1, 0, '-', '-'), ('BLOB', 2004, -1, 0, '-', '-'),
                 ('NUMBER', 2, 0, -127, '-', '-'), ('DATE', 93, 0, 0, '-', '-'),
                 ('TIMESTAMP', 93, 0, 6, '-', '6'), ('VARCHAR2', 12, 100, 0, '-', '-'),
                 ('RAW', -3, 0, 0, '-', '-'), ('CHAR', 1, 1, 0, '-', '-'),
                 ('NVARCHAR2', -9, 36, 0, '-', '-')]
        columns = []
        rows = []
        for index, (native, jdbc, precision, scale, dba_precision, dba_scale) in enumerate(cases):
            name = 'COL_' + str(index)
            columns.append({'name': name, 'native_type': native, 'jdbc_type': jdbc,
                            'precision': precision, 'scale': scale, 'nullable': 1})
            rows.append(['ALPHA', name, str(index+1), 'TIMESTAMP(6)' if native == 'TIMESTAMP' else native,
                         '100', dba_precision, dba_scale, 'Y', '0', '-', '-'])
        rows.append(['AUDIT', 'ID', '1', 'NUMBER', '22', '10', '0', 'N', '0', '-', '-'])
        for snapshot in self.inputs.values():
            if snapshot.get('kind') == 'local-runtime-observation':
                for schema in snapshot['schemas'].values():
                    schema['column_metadata'] = {'rows': rows, 'record_count': len(rows), 'sha256': digest(rows)}
            else:
                snapshot['tables'][0]['columns'] = columns
                snapshot['type_coverage'] = sorted({case[1] for case in cases})
                seal_fingerprint(snapshot)
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stdout)

    def test_detects_snapshot_changes_during_cleanup_inspection(self):
        result, report = self.run_cli('mutate-input')
        self.assertEqual(result.returncode, 1)
        self.assertEqual(report['error'], 'snapshot changed during verification')
        self.assertTrue(report['runtime_dirty'])

    def test_rejects_symbolic_manifest_file(self):
        path = self.root / 'manifest.json'
        path.rename(self.root / 'real-manifest.json')
        path.symlink_to(self.root / 'real-manifest.json')
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1)
        self.assertFalse(self.log.exists())

    def test_rejects_different_baseline_runtime_table_inventories_even_if_each_is_stable(self):
        for role in ('observation_before', 'observation_after'):
            schema = self.inputs[role]['schemas'][OWNERS[1]]
            schema['column_metadata'] = copy.deepcopy(schema['column_metadata'])
            schema['column_metadata']['rows'].pop()
            schema['column_metadata']['record_count'] = 1
            schema['column_metadata']['sha256'] = digest(schema['column_metadata']['rows'])
            counts = schema['row_counts'] = copy.deepcopy(schema['row_counts'])
            counts['rows'].pop()
            counts['sha256'] = digest(counts['rows'])
            counts['coverage'].update(expected_tables=1, observed_tables=1)
        for role in ('runtime_before', 'runtime_after'):
            self.inputs[role]['tables'].pop()
            seal_fingerprint(self.inputs[role])
        self.write_manifest()
        result, report = self.run_cli()
        self.assertEqual(result.returncode, 1, result.stdout)
        self.assertFalse(self.log.exists())


if __name__ == '__main__':
    unittest.main()
