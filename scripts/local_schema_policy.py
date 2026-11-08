#!/usr/bin/env python3
"""Pinned local schema delta; SELECT predicates reuse the guarded fixture checks.

This module neither connects to a database nor renders or executes fixture DDL.
Proof validates supplied observations, not their acquisition authenticity.
"""
from __future__ import annotations
import hashlib
import importlib.util
import json
from pathlib import Path
import re
from schema_fixture import BASE, OWNER, TABLES, FixtureError, checks, load_fixture

EQUAL = 'equal-schema'
APPROVED = 'approved-missing-runtime-tables-v1'
APPROVED_EMAIL = 'approved-missing-runtime-and-email-tables-v2'
APPROVED_FILE = 'approved-runtime-core-file-v3'
APPROVED_LES = 'approved-runtime-les-data-source-v4'
POLICIES = (EQUAL, APPROVED, APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES)
ROOT = Path(__file__).resolve().parents[1]
FIXTURE_SHA256 = 'd1227563b5a27ef249376a0652ddd10f9edb6526f64a0df7b2e466ac9b5adfcb'
DEFINITIONS = {
    'T_CORE_EMAIL_CONFIG': '34e5bbcab1c0e1dd5e88c1d35e4c907d5e6102c152bd511d58cd19c501c60453',
    'T_CORE_EMAIL_TEMPLATE': '5839e72071cb5f5b4ffffe3201c4d74bbf922ecd23f22d3a46c3c50c18d6cfb3',
    'T_EMC_INVOICE': '3942365ed0431ec5fd54b2836b122becc921a27865786e41a9af786446359c11',
    'T_EMC_QC_ITEM': '65185588632775a90ffe3a8f7a444eee21b1026567a6392606e511e0536b3d7a',
    'T_EMC_RETURNED': '5de6bdfee9385bc3df967a03f54ad016e048e40ba4667a101f599233f6a0d3b4',
    'T_EMC_SEAL_RULE': '5c37eafd2f39a51f1312ab014cb884d4a7ec2f82dc4e509c5eb822aaace90646',
    'T_EMC_SEAL_RULE_DETAILS': 'd119290e28bcac59f88381eeb7601f7848957634185f08d2a4c232967628769a',
}
EMAIL_BASE = 'api-fixtures/schema/email-config-associations'
EMAIL_MANIFEST_SHA256 = 'd41487c4bcb21da1903952a2296c0ef49fab83eed94307a00298f017cea49eb1'
EMAIL_DEFINITIONS = {
    'T_CORE_EMAIL_CONFIG_RECEIPENT': '4636062c627dc070fc1bc727abaa4056a7492fce95c013774aceb483a05b049d',
    'T_CORE_EMAIL_CONFIG_REC_ROLE': '95f8e73cd203db23ff742c5c849496d93322bb861b708121664cc18ccd2377d9',
}
FILE_BASE = 'api-fixtures/schema/core-file-restoration'
FILE_MANIFEST_SHA256 = '60817ef5a5073e7077cafeb12847a2660fffb70195d8a12e56f5cf717688acb2'
FILE_TABLE = 'T_CORE_FILE'
LES_BASE = 'api-fixtures/schema/les-data-source-restoration'
LES_MANIFEST_SHA256 = 'a38f468b4db751e1fac4854ffd9482c7a56fea4ad2dfaeb7fe843fef6f691919'
LES_DEFINITIONS = {
    'T_CORE_LES_DS': 'fe554b8dae313c9f1a7a8db1c6a59583fbd5232fcc3dc7b43360e8ac19df049e',
    'T_CORE_LES_DS_FIELD': '319730d6ed00daae131146e97bc0ceca38353772841b5f65b7cd16f0b9ae80da',
}


class PolicyError(ValueError):
    pass


def digest(value):
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(',', ':'), ensure_ascii=True).encode('ascii')).hexdigest()


def require(condition, message):
    if not condition:
        raise PolicyError(message)


def query_sql(selects):
    """Single SQLPlus envelope used for acquisition and offline query pin checks."""
    return ('whenever oserror exit failure\nwhenever sqlerror exit failure\n'
            'set echo off\nset feedback off\nset heading off\nset pagesize 0\n'
            'set linesize 32767\nset trimout on\nset tab off\nset verify off\nset define off\n'
            'alter session set container = FREEPDB1;\n'
            'prompt OBSERVE_BEGIN\n' + '\n'.join(selects) + '\n'
            'prompt OBSERVE_END\nexit\n')


def email_fixture(root):
    """Read the existing two-table fixture with its original pinned renderer."""
    root = Path(root)
    manifest_raw = (root / (EMAIL_BASE + '.json')).read_bytes()
    require(hashlib.sha256(manifest_raw).hexdigest() == EMAIL_MANIFEST_SHA256,
            'email schema manifest drift')
    manifest = json.loads(manifest_raw)
    require(manifest['owner'] == OWNER and set(manifest['tables']) == set(EMAIL_DEFINITIONS),
            'email schema fixture table coverage mismatch')
    source = (root / manifest['source']['path']).read_bytes()
    require(hashlib.sha256(source).hexdigest() == manifest['source']['sha256'], 'email schema source drift')
    lines = source.decode('utf-8').splitlines(keepends=True)
    statements = []
    for block in manifest['source_blocks']:
        start, end = block['line_start'], block['line_end']
        require(1 <= start <= end <= len(lines), 'email schema source range mismatch')
        raw = ''.join(lines[start - 1:end]).strip()
        require(hashlib.sha256(raw.encode()).hexdigest() == block['sha256'], 'email schema source block drift')
        statements.append(raw)
    renderer = root / 'scripts/schema_fixture.py'
    require(hashlib.sha256(renderer.read_bytes()).hexdigest() == manifest['generator_sha256'],
            'email schema renderer drift')
    # Isolated module state preserves schema_fixture.TABLES and the seven-table policy.
    spec = importlib.util.spec_from_file_location('email_schema_fixture', renderer)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    module.TABLES = frozenset(EMAIL_DEFINITIONS)
    loaded = module.definitions(module.statements('\n'.join(statements)))
    require(set(loaded) == set(EMAIL_DEFINITIONS), 'email schema definition coverage mismatch')
    logical = {t: {k: v for k, v in d.items() if k != 'statements'} for t, d in loaded.items()}
    require({t: digest(d) for t, d in logical.items()} == EMAIL_DEFINITIONS, 'email schema definition drift')
    fixture = (root / manifest['fixture']['path']).read_bytes()
    require(hashlib.sha256(fixture).hexdigest() == manifest['fixture']['sha256']
            and fixture == module.render(loaded, OWNER).encode('utf-8'), 'email schema fixture drift')
    return loaded, {'fixture_path': manifest['fixture']['path'], 'fixture_sha256': manifest['fixture']['sha256'],
                    'manifest_path': EMAIL_BASE + '.json', 'manifest_sha256': EMAIL_MANIFEST_SHA256,
                    'generator_sha256': manifest['generator_sha256'], 'source_blocks': manifest['source_blocks']}


def policy_tables(policy):
    require(policy in POLICIES, 'unknown local schema policy')
    tables = TABLES | frozenset(EMAIL_DEFINITIONS) if policy in (APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES) else TABLES
    return tables | frozenset(LES_DEFINITIONS) if policy == APPROVED_LES else tables


def les_fixture(root):
    """Verify the existing finite R02 fixture, without changing old admission."""
    root = Path(root)
    raw = (root / (LES_BASE + '.json')).read_bytes()
    require(hashlib.sha256(raw).hexdigest() == LES_MANIFEST_SHA256, 'LES schema manifest drift')
    manifest = json.loads(raw)
    require(manifest['owner'] == OWNER and set(manifest['tables']) == set(LES_DEFINITIONS),
            'LES schema fixture table coverage mismatch')
    sources = {}
    for source in manifest['sources']:
        data = (root / source['path']).read_bytes()
        require(hashlib.sha256(data).hexdigest() == source['sha256'], 'LES schema source drift')
        sources[source['path']] = data.decode('utf-8').splitlines(keepends=True)
    renderer = root / 'scripts/schema_fixture.py'
    require(hashlib.sha256(renderer.read_bytes()).hexdigest() == manifest['generator_sha256'],
            'LES schema renderer drift')
    spec = importlib.util.spec_from_file_location('les_schema_fixture', renderer)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    module.TABLES = frozenset(LES_DEFINITIONS)
    statements = []
    for block in manifest['source_blocks']:
        lines = sources[block['source']]
        start, end = block['line_start'], block['line_end']
        require(1 <= start <= end <= len(lines), 'LES schema source range mismatch')
        text = ''.join(lines[start - 1:end]).strip()
        require(hashlib.sha256(text.encode()).hexdigest() == block['source_sha256'], 'LES source block drift')
        original = module.statements(text)
        require([hashlib.sha256(s.encode()).hexdigest() for s in original] == block['statement_sha256'],
                'LES source statement drift')
        normalized = []
        for statement in original:
            parts = re.split(r"('(?:''|[^'])*')", statement)
            for position in range(0, len(parts), 2):
                parts[position] = re.sub(r'\s+', ' ', parts[position]).replace(' add ORDERNO', ' ADD ORDERNO')
            normalized.append(''.join(parts).strip())
        require([hashlib.sha256(s.encode()).hexdigest() for s in normalized] == block['normalized_sha256'],
                'LES normalized statement drift')
        statements.extend(normalized)
    loaded = module.definitions(statements)
    logical = {t: {k: v for k, v in d.items() if k != 'statements'} for t, d in loaded.items()}
    require({t: digest(d) for t, d in logical.items()} == LES_DEFINITIONS, 'LES schema definition drift')
    fixture = manifest['fixture']
    data = (root / fixture['path']).read_bytes()
    require(fixture['path'] == LES_BASE + '.sql' and hashlib.sha256(data).hexdigest() == fixture['sha256']
            and data == module.render(loaded, OWNER).encode('utf-8'), 'LES schema fixture drift')
    return loaded, {'manifest_path': LES_BASE + '.json', 'manifest_sha256': LES_MANIFEST_SHA256,
                    'fixture_path': fixture['path'], 'fixture_sha256': fixture['sha256'],
                    'source_blocks': manifest['source_blocks'], 'generator_sha256': manifest['generator_sha256']}


def core_file_fixture(root):
    """Pin the one existing-table transformation; the baseline remains old10."""
    root = Path(root)
    raw = (root / (FILE_BASE + '.json')).read_bytes()
    require(hashlib.sha256(raw).hexdigest() == FILE_MANIFEST_SHA256, 'CoreFile schema manifest drift')
    manifest = json.loads(raw)
    require(manifest['owner'] == OWNER and manifest['table'] == FILE_TABLE
            and manifest['container'] == 'FREEPDB1', 'CoreFile fixture target mismatch')
    for source in manifest['sources']:
        data = (root / source['path']).read_bytes()
        require(hashlib.sha256(data).hexdigest() == source['sha256'], 'CoreFile schema source drift')
        lines = data.decode('utf-8').splitlines()
        for block in source['blocks']:
            start, end = block['line_start'], block['line_end']
            require(1 <= start <= end <= len(lines), 'CoreFile source range mismatch')
            text = '\n'.join(lines[start - 1:end])
            require(hashlib.sha256(text.encode()).hexdigest() == block['sha256'], 'CoreFile source block drift')
    fixture = manifest['fixture']
    require(fixture['path'] == FILE_BASE + '.sql'
            and hashlib.sha256((root / fixture['path']).read_bytes()).hexdigest() == fixture['sha256']
            == manifest['fixture_sha256'], 'CoreFile schema fixture drift')
    historical = {'columns': manifest['historical_columns'],
                  'primary_key': {'name': manifest['retained_primary_key'], 'column': 'ID'},
                  'indexes': [], 'comments': {}}
    target = manifest['target_definition']
    require(len(historical['columns']) == 10 and len(target['columns']) == 21
            and historical['columns'] == manifest['old_columns']
            and target['columns'] == manifest['restored_columns']
            and [c['name'] for c in target['columns']] == manifest['physical_order'],
            'CoreFile physical column definition mismatch')
    provenance = {'manifest_path': FILE_BASE + '.json', 'manifest_sha256': FILE_MANIFEST_SHA256,
                  'fixture_path': fixture['path'], 'fixture_sha256': fixture['sha256'],
                  'sources': manifest['sources'], 'baseline_definition_sha256': digest(historical),
                  'runtime_definition_sha256': digest(target)}
    return historical, target, provenance


def core_file_predicates(historical, target):
    predicates = []
    for owner, definition in (('LIMS_BASELINE_LOCAL', historical), (OWNER, target)):
        for view, where, count in checks(FILE_TABLE, definition):
            predicates.append({'table': FILE_TABLE, 'view': view,
                               'predicate': where.replace("'" + OWNER + "'", "'" + owner + "'"),
                               'expected_count': count})
        predicates.extend([
            {'table': FILE_TABLE, 'view': 'ALL_CONSTRAINTS',
             'predicate': "CONSTRAINT_TYPE='R' AND R_OWNER='" + owner
                          + "' AND R_CONSTRAINT_NAME='" + definition['primary_key']['name'] + "'",
             'expected_count': 0},
            {'table': FILE_TABLE, 'view': 'ALL_TAB_IDENTITY_COLS',
             'predicate': "OWNER='" + owner + "' AND TABLE_NAME='T_CORE_FILE'", 'expected_count': 0},
        ])
    return predicates


def core_file_column_rows(columns):
    """Exact existing observer row shape, including fixed Oracle type lengths."""
    rows = []
    for ordinal, col in enumerate(columns, 1):
        varchar = col['type'] == 'VARCHAR2'
        length = col['length'] if varchar else {'NUMBER': 22, 'DATE': 7, 'TIMESTAMP(6)': 11}[col['type']]
        rows.append([FILE_TABLE, col['name'], str(ordinal), col['type'], str(length),
                     str(col['precision']) if col['precision'] is not None else '-',
                     str(col['scale']) if col['scale'] is not None else '-', col['nullable'],
                     str(col['length']) if varchar else '0', 'B' if varchar else '-', '-'])
    return sorted(rows)


def definition(policy=EQUAL, root=ROOT):
    require(policy in POLICIES, 'unknown local schema policy')
    if policy == EQUAL:
        return {'id': EQUAL}, []
    try:
        loaded = load_fixture(root)
        require(set(loaded) == TABLES == set(DEFINITIONS), 'approved fixture table coverage mismatch')
        logical = {t: {k: v for k, v in d.items() if k != 'statements'} for t, d in loaded.items()}
        require({t: digest(d) for t, d in logical.items()} == DEFINITIONS, 'approved fixture definition drift')
        require(sum(len(d['columns']) for d in logical.values()) == 105, 'approved fixture column coverage mismatch')
        sql = (Path(root) / (BASE + '.sql')).read_bytes()
        require(hashlib.sha256(sql).hexdigest() == FIXTURE_SHA256, 'approved fixture content drift')
        manifest_raw = (Path(root) / (BASE + '.json')).read_bytes()
        manifest = json.loads(manifest_raw)
        predicates = [{'table': table, 'view': view, 'predicate': where, 'expected_count': count}
                      for table, d in sorted(loaded.items()) for view, where, count in checks(table, d)]
        descriptor = {'id': APPROVED, 'owner': OWNER, 'fixture_path': BASE + '.sql',
                      'fixture_sha256': FIXTURE_SHA256, 'manifest_path': BASE + '.json',
                      'manifest_sha256': hashlib.sha256(manifest_raw).hexdigest(),
                      'sources': sorted(manifest['sources'], key=lambda x: x['path']),
                      'definition_sha256': DEFINITIONS, 'table_count': 7, 'column_count': 105,
                      'predicates_sha256': digest(predicates)}
        if policy in (APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES):
            email, provenance = email_fixture(root)
            loaded.update(email)
            predicates = [{'table': table, 'view': view, 'predicate': where, 'expected_count': count}
                          for table, d in sorted(loaded.items()) for view, where, count in checks(table, d)]
            descriptor.update(id=APPROVED_EMAIL, definition_sha256={**DEFINITIONS, **EMAIL_DEFINITIONS},
                              table_count=9, column_count=125, additional_fixtures=[provenance],
                              predicates_sha256=digest(predicates))
        if policy in (APPROVED_FILE, APPROVED_LES):
            historical, target, provenance = core_file_fixture(root)
            predicates.extend(core_file_predicates(historical, target))
            descriptor.update(id=APPROVED_FILE, altered_tables=[FILE_TABLE], core_file=provenance,
                              predicates_sha256=digest(predicates))
        if policy == APPROVED_LES:
            les, provenance = les_fixture(root)
            loaded.update(les)
            predicates.extend({'table': table, 'view': view, 'predicate': where, 'expected_count': count}
                              for table, d in sorted(les.items()) for view, where, count in checks(table, d))
            descriptor.update(id=APPROVED_LES, definition_sha256={**DEFINITIONS, **EMAIL_DEFINITIONS, **LES_DEFINITIONS},
                              table_count=11, column_count=152,
                              additional_fixtures=[*descriptor['additional_fixtures'], provenance],
                              predicates_sha256=digest(predicates))
        return descriptor, predicates
    except (FixtureError, OSError, KeyError, TypeError, ValueError) as error:
        # Do not echo private source contents or filesystem diagnostics.
        raise PolicyError('approved schema fixture provenance or definition rejected') from error


def check_inventories(baseline, runtime, policy=EQUAL):
    require(bool(baseline), 'baseline/runtime table inventories mismatch or empty')
    if policy == EQUAL:
        require(baseline == runtime, 'baseline/runtime table inventories mismatch or empty')
    else:
        tables = policy_tables(policy)
        require(policy in (APPROVED, APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES) and not baseline.intersection(tables)
                and runtime == baseline.union(tables), 'approved schema table delta mismatch')
        if policy in (APPROVED_FILE, APPROVED_LES):
            require(FILE_TABLE in baseline, 'CoreFile historical baseline table missing')


def proof_selects(predicates):
    return [f"select 'EXPECTED|{len(predicates)}' from dual;", *[
        f"select 'FIXTURE|{i}|' || to_char(count(*),'FM9999999990') from {p['view']} where {p['predicate']};"
        for i, p in enumerate(predicates)]]


def observe(policy, query):
    descriptor, predicates = definition(policy)
    result = {**descriptor, 'sha256': digest(descriptor)}
    if predicates:
        rows = query('approved-schema-metadata', proof_selects(predicates), 'FIXTURE', 2)
        expected = [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
        require(rows == expected, 'approved schema metadata predicate mismatch')
        records = [{**p, 'observed_count': int(row[1])} for p, row in zip(predicates, rows)]
        result['metadata_proof'] = {'record_count': len(records), 'sha256': digest(records), 'rows': records}
    return result


def validate(snapshot, policy=EQUAL):
    """Recompute provenance, logical pins, every predicate, and query/result coverage."""
    descriptor, predicates = definition(policy)
    supplied = snapshot.get('schema_policy', {'id': EQUAL})
    expected = {**descriptor, 'sha256': digest(descriptor)}
    if policy == EQUAL:
        require(supplied in ({'id': EQUAL}, expected), 'schema policy opt-in mismatch')
    else:
        records = [{**p, 'observed_count': p['expected_count']} for p in predicates]
        expected['metadata_proof'] = {'record_count': len(records), 'sha256': digest(records), 'rows': records}
        # Dict equality alone considers bool == int; exact canonical bytes reject that.
        require(digest(supplied) == digest(expected), 'approved schema metadata proof or policy pins mismatch')
        rows = [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
        query = {'name': 'approved-schema-metadata',
                 'sql_sha256': hashlib.sha256(query_sql(proof_selects(predicates)).encode('utf-8')).hexdigest(),
                 'expected_records': len(rows), 'parsed_records': len(rows),
                 'parsed_rows_sha256': digest(rows), 'coverage_complete': True}
        actual = [q for q in snapshot.get('queries', []) if q.get('name') == query['name']]
        require(digest(actual) == digest([query]), 'approved schema metadata query coverage mismatch')
        runtime = snapshot.get('runtime_contract', {})
        require(runtime.get('schema_policy_id') == policy and runtime.get('schema_policy_sha256') == expected['sha256'],
                'approved schema policy runtime binding mismatch')
        for owner, hash_key in (('LIMS_BASELINE_LOCAL', 'baseline_schema_hash'), (OWNER, 'runtime_schema_hash_before')):
            require(runtime.get(hash_key) == snapshot['schemas'][owner]['column_metadata']['sha256'],
                    'schema policy column hash runtime binding mismatch')
        columns = snapshot['schemas'][OWNER]['column_metadata']['rows']
        loaded = load_fixture(ROOT)
        if policy in (APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES):
            loaded.update(email_fixture(ROOT)[0])
        if policy == APPROVED_LES:
            loaded.update(les_fixture(ROOT)[0])
        for table, d in loaded.items():
            actual = sorted([r for r in columns if r[0] == table], key=lambda r: int(r[2]))
            require(len(actual) == len(d['columns']), 'approved fixture column metadata coverage mismatch')
            for i, (row, col) in enumerate(zip(actual, d['columns']), 1):
                require(row[1:4] == [col['name'], str(i), col['type']]
                        and row[5:8] == [str(col['precision']) if col['precision'] is not None else '-',
                                         str(col['scale']) if col['scale'] is not None else '-', col['nullable']]
                        and row[10] == '-', 'approved fixture column metadata mismatch')
                if col['type'] == 'VARCHAR2':
                    require(row[4] == row[8] == str(col['length']) and row[9] == 'B',
                            'approved fixture character metadata mismatch')
        shared = [r for r in columns if r[0] not in policy_tables(policy)]
        baseline = snapshot['schemas']['LIMS_BASELINE_LOCAL']['column_metadata']['rows']
        if policy in (APPROVED_FILE, APPROVED_LES):
            historical, target, _ = core_file_fixture(ROOT)
            require(sorted(r for r in baseline if r[0] == FILE_TABLE) == core_file_column_rows(historical['columns']),
                    'CoreFile baseline column metadata mismatch')
            require(sorted(r for r in shared if r[0] == FILE_TABLE) == core_file_column_rows(target['columns']),
                    'CoreFile runtime column metadata mismatch')
            shared = [r for r in shared if r[0] != FILE_TABLE]
            baseline = [r for r in baseline if r[0] != FILE_TABLE]
        require(sorted(shared) == sorted(baseline),
                'baseline/runtime shared column metadata mismatch')
        binding = snapshot.get('source_binding', {})
        from source_binding import runtime_contract_digest
        require(binding.get('runtime_contract') == runtime
                and binding.get('runtime_contract_digest') == runtime_contract_digest(runtime)
                and binding.get('runtime_contract_binding') == 'rt-' + runtime_contract_digest(runtime)[:16],
                'approved schema policy runtime digest mismatch')
    owners = ('LIMS_BASELINE_LOCAL', OWNER)
    inventories = [{row['table'] for row in snapshot['schemas'][owner]['row_counts']['rows']} for owner in owners]
    check_inventories(*inventories, policy)
    return expected
