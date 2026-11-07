#!/usr/bin/env python3
"""Verify local milestone snapshot comparisons and read-only cleanup inspection.

--manifest is a private JSON object with schema_version=1,
kind=local-runtime-milestone-inputs and files containing exactly six roles:
observation_before, observation_after, baseline_before, baseline_after,
runtime_before, runtime_after. Each entry has a relative path and raw-file
sha256. Observation files come from observe-local-runtime.py; content files
come from OracleContentFingerprint. Keep the manifest and all snapshots outside
Git. Runtime-before is the restoration target, independent of baseline content.

The verifier only inspects the local Docker Unix socket. It never stops apps,
resets data, executes SQL or promotes strict evidence. It compares supplied
snapshots, not their authenticity, acquisition timing or fixture/audit/rollback.
Approved seven-table observations require the same explicit --schema-policy;
source provenance, definitions and SELECT predicate/result coverage are rechecked.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import re
import stat
import struct
import sys

from source_binding import capture_binding
import local_schema_policy as policy

_spec = importlib.util.spec_from_file_location('local_runtime_observer', Path(__file__).with_name('observe-local-runtime.py'))
observer = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(observer)
OWNERS = observer.OWNERS
ROLES = ('observation_before', 'observation_after', 'baseline_before', 'baseline_after', 'runtime_before', 'runtime_after')
HEX = re.compile('[a-f0-9]{64}')
COLUMN_KEYS = ('name', 'native_type', 'jdbc_type', 'precision', 'scale', 'nullable')
SUPPORTED = {1, 12, -1, -15, -9, -16, 2, 3, 4, 5, -6, -5, 6, 7, 8, 91, 93, -2, -3, -4, 2004, 2005, 2011}


class VerificationError(ValueError):
    """Fixed safe errors; external output and file contents never escape."""


def require(condition, message):
    if not condition:
        raise VerificationError(message)


def sha(raw):
    return hashlib.sha256(raw).hexdigest()


def unique_object(pairs):
    result = {}
    for key, value in pairs:
        require(key not in result, 'duplicate JSON key')
        result[key] = value
    return result


def decode(raw):
    try:
        return json.loads(raw, object_pairs_hook=unique_object,
                          parse_constant=lambda _: (_ for _ in ()).throw(ValueError()))
    except (ValueError, UnicodeError, RecursionError):
        raise VerificationError('invalid input JSON') from None


def read_regular(path):
    # Pin every directory and the final file with O_NOFOLLOW. This rejects both
    # final symlinks and parent-directory substitution during traversal.
    path = Path(os.path.abspath(path))
    fd = os.open('/', os.O_RDONLY | os.O_DIRECTORY)
    try:
        for part in path.parts[1:-1]:
            next_fd = os.open(part, os.O_RDONLY | os.O_DIRECTORY | os.O_NOFOLLOW, dir_fd=fd)
            os.close(fd)
            fd = next_fd
        file_fd = os.open(path.name, os.O_RDONLY | os.O_NOFOLLOW | os.O_NONBLOCK, dir_fd=fd)
        with os.fdopen(file_fd, 'rb') as stream:
            require(stat.S_ISREG(os.fstat(stream.fileno()).st_mode), 'input must be a regular file')
            return stream.read()
    finally:
        os.close(fd)


def read_inputs(path):
    raw_manifest = read_regular(path)
    manifest = decode(raw_manifest)
    require(isinstance(manifest, dict) and manifest.get('schema_version') == 1
            and manifest.get('kind') == 'local-runtime-milestone-inputs'
            and isinstance(manifest.get('files'), dict) and set(manifest['files']) == set(ROLES), 'invalid input manifest')
    snapshots, pins, paths = {}, {}, {}
    for role in ROLES:
        entry = manifest['files'][role]
        require(isinstance(entry, dict) and set(entry) == {'path', 'sha256'}
                and isinstance(entry['path'], str) and entry['path']
                and isinstance(entry['sha256'], str) and HEX.fullmatch(entry['sha256']), 'invalid snapshot pin')
        relative = Path(entry['path'])
        require(not relative.is_absolute() and all(p not in ('', '.', '..') for p in entry['path'].split('/')),
                'snapshot path must stay inside manifest directory')
        target = path.parent / relative
        require(target not in paths.values() and target != path, 'snapshot paths must be distinct')
        raw = read_regular(target)
        require(sha(raw) == entry['sha256'], 'snapshot raw SHA256 mismatch')
        snapshots[role], pins[role], paths[role] = decode(raw), {'sha256': sha(raw), 'bytes': len(raw)}, target
    return snapshots, pins, paths, raw_manifest


def java_order(value):
    return value.encode('utf-16-be')


def frame(tag, payload):
    tag = tag.encode('utf-8')
    return struct.pack('>i', len(tag)) + tag + struct.pack('>q', len(payload)) + payload


def content_snapshot(snapshot, owner, schema):
    require(isinstance(snapshot, dict) and snapshot.get('format') == 'oracle-content-sha256-v1'
            and snapshot.get('schema') == owner and snapshot.get('transaction_read_only') is True,
            'invalid content snapshot identity or format')
    tables = snapshot.get('tables')
    require(isinstance(tables, list) and tables, 'empty content table inventory')
    by_name, types = {}, set()
    whole = hashlib.sha256(frame('SCHEMA-v1', b''))
    counts = {row['table']: row['count'] for row in schema['row_counts']['rows']}
    observed_columns = {}
    for row in schema['column_metadata']['rows']:
        observed_columns.setdefault(row[0], []).append(row)
    for table in tables:
        require(isinstance(table, dict) and isinstance(table.get('name'), str) and table['name']
                and table['name'] not in by_name, 'invalid or duplicate content table')
        name = table['name']
        require(name in counts and type(table.get('row_count')) is int and table['row_count'] >= 0
                and table['row_count'] == counts[name], 'content table coverage or row count disagrees with observation')
        require(isinstance(table.get('content_sha256'), str) and HEX.fullmatch(table['content_sha256']), 'invalid table content hash')
        columns = table.get('columns')
        require(isinstance(columns, list) and columns, 'empty content column inventory')
        metadata = sorted(observed_columns[name], key=lambda row: int(row[2]))
        require(len(columns) == len(metadata), 'content columns disagree with observation')
        names = set()
        whole.update(frame('TABLE', name.encode('utf-8')))
        for column, row in zip(columns, metadata):
            require(isinstance(column, dict) and set(column) == set(COLUMN_KEYS), 'invalid content column metadata')
            require(isinstance(column['name'], str) and column['name'] not in names and column['name'] == row[1]
                    and (column['native_type'] == row[3] or
                         (column['native_type'] == 'TIMESTAMP' and column['jdbc_type'] == 93
                          and re.fullmatch(r'TIMESTAMP\([0-9]\)', row[3]))),
                    'content columns disagree with observation')
            require(all(type(column[k]) is int for k in COLUMN_KEYS[2:])
                    and column['jdbc_type'] in SUPPORTED and column['nullable'] in (0, 1, 2)
                    and (column['precision'] >= 0 or
                         (column['precision'] == -1 and column['jdbc_type'] in (2004, 2005, 2011))),
                    'invalid or unsupported content column type')
            names.add(column['name'])
            types.add(column['jdbc_type'])
            require(column['nullable'] == (0 if row[7] == 'N' else 1), 'content nullable metadata disagrees with observation')
            for key, index in (('precision', 5), ('scale', 6)):
                if row[index] != '-':
                    require(column[key] == int(row[index]), 'content numeric metadata disagrees with observation')
            for index, key in enumerate(COLUMN_KEYS):
                whole.update(frame('COLUMN-' + str(index), str(column[key]).encode('utf-8')))
        whole.update(frame('COUNT', str(table['row_count']).encode('ascii')))
        whole.update(frame('CONTENT', bytes.fromhex(table['content_sha256'])))
        by_name[name] = table
    require(set(by_name) == set(counts), 'content table coverage incomplete')
    require(list(by_name) == sorted(by_name, key=java_order), 'content table inventory not in canonical order')
    require(snapshot.get('type_coverage') == sorted(types), 'content type coverage mismatch')
    require(snapshot.get('schema_sha256') == whole.hexdigest(), 'content schema hash mismatch')
    return by_name


def validate_observation(snapshot, schema_policy=policy.EQUAL):
    observer.validate_previous(snapshot, schema_policy)
    require(snapshot.get('execution_location') == 'local-wsl-docker', 'observation execution location mismatch')
    require(snapshot.get('application') == snapshot['containers']['app'], 'application observation mismatch')
    network = snapshot['network']
    require(HEX.fullmatch(str(network.get('id', ''))) is not None, 'invalid observation network ID')
    ids = set()
    for role, item in snapshot['containers'].items():
        require(HEX.fullmatch(str(item.get('id', ''))) is not None
                and re.fullmatch('sha256:[a-f0-9]{64}', str(item.get('image_id', '')))
                and isinstance(item.get('started_at'), str) and item['started_at'], 'invalid observation container identity')
        ids.add(item['id'])
    require(len(ids) == 3, 'duplicate observation container IDs')
    for owner in OWNERS:
        require(snapshot['schemas'][owner]['account_status'] == ('LOCKED' if owner == OWNERS[0] else 'OPEN'), 'baseline must remain LOCKED and runtime OPEN')
        seen = set()
        ordinals = set()
        for row in snapshot['schemas'][owner]['column_metadata']['rows']:
            require((row[0], row[1]) not in seen and re.fullmatch('[1-9][0-9]*', row[2])
                    and (row[0], row[2]) not in ordinals, 'duplicate or invalid observation column')
            seen.add((row[0], row[1]))
            ordinals.add((row[0], row[2]))
    try:
        moment = datetime.fromisoformat(snapshot['observed_at'].replace('Z', '+00:00'))
        require(moment.tzinfo is not None, 'observation time must include timezone')
        return moment
    except (ValueError, AttributeError):
        raise VerificationError('invalid observation time') from None


def compare(before, after):
    changed = []
    for name in sorted(set(before) | set(after), key=java_order):
        fields = [key for key in ('row_count', 'content_sha256', 'columns')
                  if before.get(name, {}).get(key) != after.get(name, {}).get(key)]
        if name not in before or name not in after:
            fields.append('table_inventory')
        if fields:
            changed.append({'table': name, 'changed_fields': fields})
    return {'equal': not changed, 'table_count_before': len(before), 'table_count_after': len(after), 'changed_tables': changed}


def cleanup_inspection(args):
    reader = observer.Observer(args)
    network = reader.inspect(['network', 'inspect', '--format', observer.NETWORK_FORMAT, observer.NETWORK])
    require(network.get('name') == observer.NETWORK and network.get('internal') is True
            and HEX.fullmatch(str(network.get('id', ''))), 'cleanup network identity or Internal flag mismatch')
    containers = {}
    for role, name in observer.CONTAINERS.items():
        item = reader.inspect(['inspect', '--type', 'container', '--format', observer.CONTAINER_FORMAT, name])
        networks = item.get('networks')
        require(item.get('name') == '/' + name and isinstance(networks, dict) and set(networks) == {observer.NETWORK}
                and networks[observer.NETWORK].get('NetworkID') == network['id']
                and type(item.get('running')) is bool, 'cleanup container identity or network mismatch')
        containers[role] = {key: item[key] for key in ('id', 'running', 'started_at', 'image_id')}
        containers[role]['name'] = name
    return {'network': network, 'containers': containers}


def verify(args, report, *, recorded_cleanup=None):
    path = args.manifest.absolute()
    snapshots, pins, paths, raw_manifest = read_inputs(path)
    report.update(inputs=pins, manifest_sha256=sha(raw_manifest))
    before, after = snapshots['observation_before'], snapshots['observation_after']
    schema_policy_id = getattr(args, 'schema_policy', policy.EQUAL)
    require(validate_observation(before, schema_policy_id) <= validate_observation(after, schema_policy_id), 'observation time order invalid')
    schema_policy = policy.validate(before, schema_policy_id)
    require(schema_policy == policy.validate(after, schema_policy_id), 'schema policy changed between observations')
    report['schema_policy'] = schema_policy
    for field in ('network', 'containers'):
        require(before[field] == after[field], 'runtime identity changed between observations')
    tables = {}
    for role in ROLES[2:]:
        schema_role, moment = role.split('_')
        owner = OWNERS[0 if schema_role == 'baseline' else 1]
        observation = before if moment == 'before' else after
        tables[role] = content_snapshot(snapshots[role], owner, observation['schemas'][owner])
    comparisons = {role: compare(tables[role + '_before'], tables[role + '_after']) for role in ('baseline', 'runtime')}
    report['comparisons'] = comparisons
    for role, owner in zip(('baseline', 'runtime'), OWNERS):
        comparisons[role]['observer_columns_equal'] = before['schemas'][owner]['column_metadata'] == after['schemas'][owner]['column_metadata']
    report['baseline_schema_immutable'] = comparisons['baseline']['equal'] and comparisons['baseline']['observer_columns_equal']
    report['runtime_schema_restored'] = comparisons['runtime']['equal'] and comparisons['runtime']['observer_columns_equal']
    cleanup = cleanup_inspection(args) if recorded_cleanup is None else recorded_cleanup[0]
    # Historical replay rechecks both pinned inspections without stopping a recovered app.
    second = cleanup_inspection(args) if recorded_cleanup is None else recorded_cleanup[1]
    require(cleanup == second, 'cleanup identity changed during inspection')
    report['cleanup_inspections'] = [cleanup, second]
    report['cleanup_observation'] = cleanup
    report['runtime_identity'] = {'network_id': before['network']['id'],
                                  'container_ids': {role: item['id'] for role, item in before['containers'].items()}}
    require(cleanup['network'] == before['network'], 'cleanup network replaced')
    for role in ('oracle', 'redis', 'app'):
        actual, expected = cleanup['containers'][role], before['containers'][role]
        same = all(actual[key] == expected[key] for key in ('id', 'name', 'started_at', 'image_id'))
        if role == 'app':
            report['application_stopped'] = same and actual['running'] is False
        else:
            report[role + '_reused'] = same and actual['running'] is True
    for role, target in paths.items():
        require(sha(read_regular(target)) == pins[role]['sha256'], 'snapshot changed during verification')
    require(read_regular(path) == raw_manifest, 'manifest changed during verification')
    report['source_binding'] = capture_binding(runtime_contract={
        'schema_policy_id': schema_policy['id'], 'schema_policy_sha256': schema_policy['sha256'],
        'runtime_profile': 'local-wsl', 'oracle_instance_id': observer.CONTAINERS['oracle'],
        'oracle_container': observer.CONTAINERS['oracle'], 'redis_container': observer.CONTAINERS['redis'],
        'baseline_schema': OWNERS[0], 'runtime_schema': OWNERS[1],
        'baseline_schema_hash': before['schemas'][OWNERS[0]]['column_metadata']['sha256'],
        'runtime_schema_hash_before': before['schemas'][OWNERS[1]]['column_metadata']['sha256'],
        'runtime_schema_hash_after': after['schemas'][OWNERS[1]]['column_metadata']['sha256'],
        'baseline_key_data_hash': snapshots['baseline_before']['schema_sha256'],
        'runtime_key_data_hash_before': snapshots['runtime_before']['schema_sha256'],
        'runtime_key_data_hash_after': snapshots['runtime_after']['schema_sha256'],
        'baseline_schema_immutable': report['baseline_schema_immutable'],
        'runtime_schema_restored': report['runtime_schema_restored'],
        'oracle_reused': report['oracle_reused'], 'redis_reused': report['redis_reused'],
        'schema_copy_method': 'supplied-runtime-before-content-restore-target'})
    checks = ('baseline_schema_immutable', 'runtime_schema_restored', 'application_stopped', 'oracle_reused', 'redis_reused')
    report['failed_checks'] = [key for key in checks if not report[key]]
    require(not report['failed_checks'], 'milestone lifecycle checks failed')
    manifest = decode(raw_manifest)
    if 'context' in manifest or 'prepared_runtime' in manifest:
        from lifecycle_proof import validate_lifecycle_context
        validate_lifecycle_context(manifest, path, snapshots, report)
        report['context'] = manifest['context']
        report['prepared_runtime'] = manifest['prepared_runtime']
    report['manifest_ref'] = {'path': str(path), 'sha256': sha(raw_manifest)}
    report.update(status='diagnostic-lifecycle-verified', lifecycle_verified=True, runtime_dirty=False)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('--schema-policy', choices=policy.POLICIES, default=policy.EQUAL,
                        help='must explicitly match the approved observation policy')
    parser.add_argument('--manifest', type=Path, required=True)
    parser.add_argument('--docker', default='docker')
    parser.add_argument('--timeout', type=observer.positive_timeout, default=30)
    args = parser.parse_args()
    report = {'schema_version': 1, 'kind': 'local-runtime-milestone-verification',
              'status': 'diagnostic-failed', 'execution_location': 'local-wsl-docker',
              'observed_at': datetime.now(timezone.utc).isoformat(), 'strict_evidence': False,
              'real_http': False, 'lifecycle_verified': False, 'runtime_dirty': True, 'baseline_schema_immutable': False,
              'runtime_schema_restored': False, 'application_stopped': False,
              'oracle_reused': False, 'redis_reused': False, 'fixture_restore': 'not-verified',
              'audit': 'not-verified', 'rollback': 'not-verified',
              'limitations': ['compares supplied snapshots; acquisition authenticity and timing are not established',
                              'baseline immutability means sampled content and columns before/after only',
                              'general column metadata excludes defaults, indexes, constraints and stored code; approved fixture proof checks its exact DDL properties',
                              'sequential observations do not prove absence of intervening changes',
                              'no API, fixture restore, audit, rollback or strict evidence approval']}
    try:
        verify(args, report)
        code = 0
    except (VerificationError, observer.ObservationError) as error:
        report['error'], code = str(error), 1
    except (OSError, ValueError, TypeError, KeyError, AttributeError, OverflowError, UnicodeError, RecursionError):
        report['error'], code = 'invalid, inaccessible or incomplete milestone input', 1
    print(json.dumps(report, sort_keys=True, indent=2, ensure_ascii=True))
    return code


if __name__ == '__main__':
    sys.exit(main())
