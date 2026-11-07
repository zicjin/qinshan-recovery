#!/usr/bin/env python3
"""Read-only local Docker/Oracle observation; JSON stdout is diagnostic only.

No lifecycle operations, credentials, SQL files or arbitrary SQL are accepted.
COUNT(*) observations detect row-count drift, not changes to row contents.
These sequential reads are not a transactional cross-schema snapshot.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import hashlib
import json
import math
import os
from pathlib import Path
import re
import subprocess
import sys

from source_binding import capture_binding

NETWORK = 'lims-local-runtime-isolated'
CONTAINERS = {role: 'lims-local-runtime-' + role for role in ('app', 'oracle', 'redis')}
OWNERS = ('LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_LOCAL')
NETWORK_FORMAT = '{"name":{{json .Name}},"id":{{json .Id}},"internal":{{json .Internal}}}'
CONTAINER_FORMAT = ('{"name":{{json .Name}},"id":{{json .Id}},"running":{{json .State.Running}},'
                    '"started_at":{{json .State.StartedAt}},"image_id":{{json .Image}},'
                    '"networks":{{json .NetworkSettings.Networks}}}')
FILTER = "owner in ('LIMS_BASELINE_LOCAL','LIMS_RUNTIME_LOCAL')"
COLUMN_FROM = ('from dba_tab_columns c join dba_tables t on t.owner=c.owner and t.table_name=c.table_name '
               "where c.owner in ('LIMS_BASELINE_LOCAL','LIMS_RUNTIME_LOCAL')")


class ObservationError(Exception):
    """Errors contain fixed safe descriptions, never external command output."""


def digest(value):
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(',', ':'),
                                     ensure_ascii=True).encode('ascii')).hexdigest()


def identifier(value):
    if not isinstance(value, str) or not re.fullmatch(r'[A-Z][A-Z0-9_$#]{0,127}', value):
        raise ObservationError('invalid database identifier')
    return value


def positive_timeout(value):
    try:
        number = float(value)
    except ValueError:
        raise argparse.ArgumentTypeError('timeout must be finite and positive') from None
    if not math.isfinite(number) or number <= 0:
        raise argparse.ArgumentTypeError('timeout must be finite and positive')
    return number


class Observer:
    def __init__(self, args):
        self.args = args
        self.queries = []

    def command(self, argv, stdin=None):
        # Force a local Unix socket even when a caller has remote Docker settings.
        env = {k: v for k, v in os.environ.items()
               if k not in ('DOCKER_HOST', 'DOCKER_CONTEXT', 'DOCKER_TLS_VERIFY', 'DOCKER_CERT_PATH')}
        try:
            result = subprocess.run([self.args.docker, '--host', 'unix:///var/run/docker.sock', *argv],
                                    input=stdin, capture_output=True, text=True,
                                    timeout=self.args.timeout, env=env)
        except (OSError, subprocess.TimeoutExpired, UnicodeError):
            raise ObservationError('local Docker command unavailable or timed out') from None
        if result.returncode or result.stderr.strip():
            raise ObservationError('local Docker command failed')
        return result.stdout

    def inspect(self, argv):
        try:
            item = json.loads(self.command(argv))
        except (ValueError, TypeError):
            raise ObservationError('invalid Docker observation') from None
        if not isinstance(item, dict):
            raise ObservationError('invalid Docker observation')
        return item

    def identities(self):
        network = self.inspect(['network', 'inspect', '--format', NETWORK_FORMAT, NETWORK])
        if (network.get('name') != NETWORK or network.get('internal') is not True
                or not re.fullmatch('[a-f0-9]{64}', str(network.get('id', '')))):
            raise ObservationError('local isolated network identity or Internal flag mismatch')
        containers = {}
        for role, name in CONTAINERS.items():
            item = self.inspect(['inspect', '--type', 'container', '--format', CONTAINER_FORMAT, name])
            networks = item.get('networks')
            attachment = networks.get(NETWORK) if isinstance(networks, dict) else None
            if (item.get('name') != '/' + name or item.get('running') is not True
                    or not re.fullmatch('[a-f0-9]{64}', str(item.get('id', '')))
                    or not re.fullmatch('sha256:[a-f0-9]{64}', str(item.get('image_id', '')))
                    or not isinstance(item.get('started_at'), str) or not item['started_at']
                    or not isinstance(attachment, dict) or set(networks) != {NETWORK}
                    or attachment.get('NetworkID') != network['id']):
                raise ObservationError('local container identity, running state or network mismatch')
            # Whitelist output. Docker environment, command and credentials are never emitted.
            containers[role] = {key: item[key] for key in ('id', 'running', 'started_at', 'image_id')}
            containers[role]['name'] = name
        if len({c['id'] for c in containers.values()}) != 3:
            raise ObservationError('duplicate local container identity')
        return network, containers

    def query(self, name, selects, prefix, fields):
        sql = ('whenever oserror exit failure\nwhenever sqlerror exit failure\n'
               'set echo off\nset feedback off\nset heading off\nset pagesize 0\n'
               'set linesize 32767\nset trimout on\nset tab off\nset verify off\nset define off\n'
               'alter session set container = FREEPDB1;\n'
               'prompt OBSERVE_BEGIN\n' + '\n'.join(selects) + '\n'
               'prompt OBSERVE_END\nexit\n')
        output = self.command(['exec', '-i', '-u', 'oracle', CONTAINERS['oracle'], 'sh', '-c',
                               'exec "$ORACLE_HOME/bin/sqlplus" -s / as sysdba'], sql)
        if re.search(r'\b(?:ORA|SP2|TNS|LRM)-\d+', output):
            raise ObservationError('Oracle observation failed')
        lines = [line.strip() for line in output.splitlines() if line.strip()]
        if (len(lines) < 4 or lines[0] != 'OBSERVE_BEGIN' or lines[-1] != 'OBSERVE_END'
                or not re.fullmatch(r'EXPECTED\|[0-9]+', lines[1])):
            raise ObservationError('empty or invalid SQL observation frame')
        expected = int(lines[1].split('|')[1])
        rows = [line.split('|') for line in lines[2:-1]]
        if not expected or len(rows) != expected or any(len(row) != fields + 1 or row[0] != prefix for row in rows):
            raise ObservationError('SQL record coverage incomplete or empty')
        records = [row[1:] for row in rows]
        if len({tuple(row) for row in records}) != expected:
            raise ObservationError('duplicate SQL observation records')
        self.queries.append({'name': name, 'sql_sha256': hashlib.sha256(sql.encode('ascii')).hexdigest(),
                             'expected_records': expected, 'parsed_records': len(records),
                             'parsed_rows_sha256': digest(records), 'coverage_complete': True})
        return records

    def schemas(self):
        users = self.query('account-status', [
            "select 'EXPECTED|' || to_char(count(*),'FM9999999990') from dba_users where username in ('LIMS_BASELINE_LOCAL','LIMS_RUNTIME_LOCAL');",
            "select 'USER|' || username || '|' || account_status from dba_users where username in ('LIMS_BASELINE_LOCAL','LIMS_RUNTIME_LOCAL') order by username;"], 'USER', 2)
        if dict(users) != {OWNERS[0]: 'LOCKED', OWNERS[1]: 'OPEN'} or len(users) != 2:
            raise ObservationError('baseline must be LOCKED and runtime must be OPEN')
        tables = self.query('table-inventory', [
            "select 'EXPECTED|' || to_char(count(*),'FM9999999990') from dba_tables where " + FILTER + ';',
            "select 'TABLE|' || owner || '|' || table_name from dba_tables where " + FILTER + ' order by owner,table_name;'], 'TABLE', 2)
        expected_tables = set()
        for owner, table in tables:
            if owner not in OWNERS:
                raise ObservationError('unexpected table owner')
            identifier(table)
            expected_tables.add((owner, table))
        table_sets = [{table for owner, table in expected_tables if owner == schema} for schema in OWNERS]
        if not table_sets[0] or table_sets[0] != table_sets[1]:
            raise ObservationError('baseline/runtime table inventories mismatch or empty')
        columns = self.query('column-metadata', [
            "select 'EXPECTED|' || to_char(count(*),'FM9999999990') " + COLUMN_FROM + ';',
            "select 'COLUMN|' || c.owner || '|' || c.table_name || '|' || c.column_name || '|' || "
            "to_char(c.column_id,'FM9999999990') || '|' || c.data_type || '|' || "
            "to_char(c.data_length,'FM9999999990') || '|' || nvl(to_char(c.data_precision,'FM9999999990'),'-') || '|' || "
            "nvl(to_char(c.data_scale,'FM9999999990'),'-') || '|' || c.nullable || '|' || "
            "to_char(c.char_length,'FM9999999990') || '|' || nvl(c.char_used,'-') || '|' || "
            "case when c.data_type_owner=c.owner then 'SELF' else nvl(c.data_type_owner,'-') end "
            + COLUMN_FROM + ' order by c.owner,c.table_name,c.column_id;'], 'COLUMN', 12)
        seen_columns = set()
        for row in columns:
            owner, table, column = row[:3]
            if (owner, table) not in expected_tables:
                raise ObservationError('column table coverage mismatch')
            identifier(column)
            key = (owner, table, column)
            if key in seen_columns:
                raise ObservationError('duplicate column identity')
            seen_columns.add(key)
            if (not re.fullmatch('[1-9][0-9]*', row[3]) or not row[4]
                    or not all(re.fullmatch(r'-?[0-9]+|-', row[i]) for i in (5, 6, 7, 9))
                    or row[8] not in ('Y', 'N') or row[10] not in ('B', 'C', '-')
                    or not row[11]):
                raise ObservationError('invalid column metadata')
        if {(row[0], row[1]) for row in columns} != expected_tables:
            raise ObservationError('column metadata missing tables')
        counts = self.query('actual-table-counts', [f"select 'EXPECTED|{len(expected_tables)}' from dual;", *[
            f"select 'COUNT|{owner}|{table}|' || to_char(count(*),'FM999999999999999999999999999999999999990') from \"{owner}\".\"{table}\";"
            for owner, table in sorted(expected_tables)]], 'COUNT', 3)
        observed_tables = set()
        for owner, table, count in counts:
            if (owner, table) not in expected_tables or not re.fullmatch(r'[0-9]+', count) or (owner, table) in observed_tables:
                raise ObservationError('invalid or duplicate actual table count')
            observed_tables.add((owner, table))
        if observed_tables != expected_tables:
            raise ObservationError('actual table count coverage incomplete')
        schemas = {}
        for owner in OWNERS:
            metadata = sorted([row[1:] for row in columns if row[0] == owner])
            actual = sorted([{'table': table, 'count': int(n)} for schema, table, n in counts if schema == owner], key=lambda row: row['table'])
            schemas[owner] = {'account_status': dict(users)[owner],
                              'column_metadata': {'record_count': len(metadata), 'sha256': digest(metadata), 'rows': metadata},
                              'row_counts': {'sha256': digest(actual), 'rows': actual,
                                             'coverage': {'expected_tables': len(table_sets[0]), 'observed_tables': len(actual), 'complete': True}}}
        return schemas

    def observe(self):
        network, containers = self.identities()
        schemas = self.schemas()
        after_network, after_containers = self.identities()
        if network != after_network or containers != after_containers:
            raise ObservationError('runtime identity changed during observation')
        binding = capture_binding(runtime_contract={
            'runtime_profile': 'local-wsl', 'oracle_instance_id': CONTAINERS['oracle'],
            'oracle_container': CONTAINERS['oracle'], 'redis_container': CONTAINERS['redis'],
            'baseline_schema': OWNERS[0], 'runtime_schema': OWNERS[1],
            'baseline_schema_hash': schemas[OWNERS[0]]['column_metadata']['sha256'],
            'runtime_schema_hash_before': schemas[OWNERS[1]]['column_metadata']['sha256'],
            'baseline_schema_immutable': False, 'runtime_schema_restored': False})
        return {'schema_version': 1, 'kind': 'local-runtime-observation',
                'observed_at': datetime.now(timezone.utc).isoformat(), 'status': 'diagnostic-observed',
                'execution_location': 'local-wsl-docker', 'strict_evidence': False,
                'runtime_schema_restored': False, 'restoration': 'not-verified',
                'content_verified': False, 'key_data_hashes': 'not-observed',
                'baseline_account_locked': True, 'baseline_immutability': 'not-verified',
                'consistency': 'sequential-reads-not-transactional',
                'fixture_restore': 'not-verified', 'audit': 'not-observed', 'rollback': 'not-executed',
                'cleanup': 'not-executed-read-only-observation', 'real_http': False,
                'network': network, 'application': containers['app'], 'containers': containers,
                'schemas': schemas, 'queries': self.queries, 'source_binding': binding,
                'runtime_contract': binding['runtime_contract'],
                'limitations': ['account LOCKED is observed, not proof of baseline content immutability',
                                'column metadata excludes defaults, indexes, constraints and stored code',
                                'row counts cannot detect equal-count content changes']}



def validate_previous(before):
    """Require complete, self-consistent observations before reporting no drift."""
    try:
        if (before['schema_version'] != 1 or before['kind'] != 'local-runtime-observation'
                or before['status'] != 'diagnostic-observed' or before['strict_evidence'] is not False
                or set(before['schemas']) != set(OWNERS)
                or set(before['containers']) != set(CONTAINERS)
                or before['network']['name'] != NETWORK or before['network']['internal'] is not True):
            raise ValueError
        for role, name in CONTAINERS.items():
            item = before['containers'][role]
            if item['name'] != name or item['running'] is not True or not item['id'] or not item['started_at']:
                raise ValueError
        for owner in OWNERS:
            schema = before['schemas'][owner]
            metadata, counts = schema['column_metadata'], schema['row_counts']
            rows = metadata['rows']
            if (not isinstance(rows, list) or not rows or metadata['record_count'] != len(rows)
                    or metadata['sha256'] != digest(rows) or counts['sha256'] != digest(counts['rows'])
                    or not all(isinstance(row, list) and len(row) == 11 for row in rows)):
                raise ValueError
            tables = {identifier(row[0]) for row in rows}
            actual = counts['rows']
            if (not isinstance(actual, list) or not actual or len(actual) != len(tables)
                    or {identifier(row['table']) for row in actual} != tables
                    or any(type(row['count']) is not int or row['count'] < 0 for row in actual)
                    or counts['coverage'] != {'expected_tables': len(tables), 'observed_tables': len(tables), 'complete': True}):
                raise ValueError
    except (KeyError, TypeError, ValueError, ObservationError):
        raise ObservationError('invalid previous observation') from None


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--docker', default='docker', help='Docker executable (default: docker)')
    parser.add_argument('--timeout', type=positive_timeout, default=120, help='per-command timeout in seconds')
    parser.add_argument('--before', type=Path, help='compare a previous diagnostic observation; never promotes evidence')
    args = parser.parse_args()
    try:
        snapshot = Observer(args).observe()
        if args.before:
            try:
                before = json.loads(args.before.read_text())
            except (OSError, ValueError):
                raise ObservationError('invalid previous observation') from None
            validate_previous(before)
            changes = []
            for field in ('network', 'containers', 'schemas'):
                if before.get(field) != snapshot[field]:
                    changes.append(field)
            snapshot['comparison'] = {'drift_detected': bool(changes), 'changed_sections': changes,
                                      'content_comparison': 'not-performed', 'strict_evidence': False}
        print(json.dumps(snapshot, sort_keys=True, indent=2))
        return 0
    except ObservationError as error:
        print(json.dumps({'schema_version': 1, 'kind': 'local-runtime-observation',
                          'status': 'diagnostic-failed', 'strict_evidence': False,
                          'runtime_schema_restored': False, 'error': str(error)}, sort_keys=True))
        return 1


if __name__ == '__main__':
    sys.exit(main())
