#!/usr/bin/env python3
"""Pinned local schema delta; SELECT predicates reuse the guarded fixture checks.

This module neither connects to a database nor renders or executes fixture DDL.
Proof validates supplied observations, not their acquisition authenticity.
"""
from __future__ import annotations
import copy
import hashlib
import importlib.util
import json
from pathlib import Path
import re
from schema_fixture import BASE, OWNER, TABLES, FixtureError, checks, load_fixture, literal

EQUAL = 'equal-schema'
APPROVED = 'approved-missing-runtime-tables-v1'
APPROVED_EMAIL = 'approved-missing-runtime-and-email-tables-v2'
APPROVED_FILE = 'approved-runtime-core-file-v3'
APPROVED_LES = 'approved-runtime-les-data-source-v4'
APPROVED_PRODUCTION = 'approved-runtime-production-alignment-v5'
APPROVED_EXTERNAL = 'approved-runtime-external-schema-v6'
POLICIES = (EQUAL, APPROVED, APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL)
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
PRODUCTION_BASE = 'api-fixtures/schema/approved-production-alignment-v5.json'
PRODUCTION_MANIFEST_SHA256 = 'e464f124d77911f5e3f7d190d5089d71e061bb929e16da4fc0dd634ecc8217cf'
ACQUISITION_TABLE = 'T_EMC_EQUIPT_ACQ_FILE'
EXTERNAL_BASE = 'deployment/oracle/external-schema/'
PACKPRICE_BASE = 'deployment/report-engine/dependencies/external-contract-packprice-20261111'
EXTERNAL_TABLES = frozenset({'T_CRM_CONTRACT', 'T_CRM_INVOICE_PROJ', 'T_CRM_INVOICE',
    'T_CRM_PAY_PROJECT', 'T_CRM_PAYMENT', 'T_LIMS_CLEARANCEFORM', 'T_LIMS_FOLDERS',
    'T_XFP_ORDER_TASK', 'T_XFP_PROJECT', 'T_XFP_REPORT', 'T_CRM_CONTRACT_PACKPRICE'})
EXTERNAL_PINS = {
    EXTERNAL_BASE + 'manifest.json': '722c98160ba0683254dcbda8be05a697cb9a7498645f15a7667be55953744e22',
    EXTERNAL_BASE + 'structure.json': '7b8541e25c3055a0f440e137a5f08b5fd10c77adcdbcc5dd58adc519a77fd878',
    PACKPRICE_BASE + '.json': '4fdc594b7e345134df5cfd00cce6ef756f5fbbfb97a672c3b40ab534cf0d19b0',
    'deployment/report-engine/manifest.json': 'f802b0dbebd4df8efcb7f78cad0a5d7bcff866c5a7754b5fef9f66502d7bde6f',
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
    if policy == APPROVED_EXTERNAL:
        return policy_tables(APPROVED_PRODUCTION) | EXTERNAL_TABLES
    if policy == APPROVED_PRODUCTION:
        return policy_tables(APPROVED_LES) | frozenset({ACQUISITION_TABLE})
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


def production_column(row):
    return {'name': row['COLUMN_NAME'], 'type': row['DATA_TYPE'],
            'length': int(row['DATA_LENGTH']) if row['DATA_TYPE'] == 'VARCHAR2' else None,
            'precision': int(row['DATA_PRECISION']) if row['DATA_PRECISION'] is not None else None,
            'scale': int(row['DATA_SCALE']) if row['DATA_SCALE'] is not None else None,
            'nullable': row['NULLABLE']}


def production_column_rows(table, columns):
    rows = []
    for ordinal, col in enumerate(columns, 1):
        varchar = col['type'] == 'VARCHAR2'
        length = col['length'] if varchar else {'NUMBER': 22, 'DATE': 7, 'CLOB': 4000}[col['type']]
        rows.append([table, col['name'], str(ordinal), col['type'], str(length),
                     str(col['precision']) if col['precision'] is not None else '-',
                     str(col['scale']) if col['scale'] is not None else '-', col['nullable'],
                     str(col['length']) if varchar else '0', 'B' if varchar else '-', '-'])
    return sorted(rows)


def production_index_predicates(item):
    name, table, columns = item['name'], item['table'], item['columns']
    key = "OWNER='LIMS_RUNTIME_LOCAL' AND INDEX_NAME=" + literal(name)
    result = [{'table': table, 'view': 'ALL_INDEXES', 'predicate': key
               + ' AND TABLE_NAME=' + literal(table)
               + " AND INDEX_TYPE='NORMAL' AND STATUS='VALID' AND UNIQUENESS='NONUNIQUE'",
               'expected_count': 1}]
    key = "INDEX_OWNER='LIMS_RUNTIME_LOCAL' AND INDEX_NAME=" + literal(name)
    result.append({'table': table, 'view': 'ALL_IND_COLUMNS', 'predicate': key,
                   'expected_count': len(columns)})
    result.extend({'table': table, 'view': 'ALL_IND_COLUMNS', 'predicate': key
                   + ' AND COLUMN_NAME=' + literal(column) + ' AND COLUMN_POSITION=' + str(position)
                   + " AND DESCEND='ASC'", 'expected_count': 1}
                  for position, column in enumerate(columns, 1))
    return result


def production_fixture(root):
    """Derive only the already-reviewed v5 changes from immutable source inputs.

    This is an offline finite contract, not a schema learner or DDL executor.
    """
    root = Path(root)
    try:
        raw = (root / PRODUCTION_BASE).read_bytes()
        require(hashlib.sha256(raw).hexdigest() == PRODUCTION_MANIFEST_SHA256,
                'production alignment policy manifest drift')
        manifest = json.loads(raw)
        require(manifest['policy_id'] == APPROVED_PRODUCTION and manifest['owner'] == OWNER
                and manifest['container'] == 'FREEPDB1', 'production alignment target drift')
        for pin in manifest['pins']:
            require(hashlib.sha256((root / pin['path']).read_bytes()).hexdigest() == pin['sha256'],
                    'production alignment source pin drift')
        predecessor, _ = definition(APPROVED_LES, root)
        require(digest(predecessor) == manifest['predecessor_descriptor_sha256'],
                'production alignment predecessor drift')
        source = json.loads((root / 'api-fixtures/schema/production-ddl-source-20261109.json').read_text())
        source_columns = {(r['TABLE_NAME'], r['COLUMN_NAME']): r for r in source['columns']}
        sql_texts = []
        for name in ('production-ddl-alignment-20261109', 'production-ddl-field-alignment-20261109'):
            migration = json.loads((root / ('api-fixtures/schema/' + name + '.json')).read_text())
            sql = (root / migration['sql']['path']).read_bytes()
            require(hashlib.sha256(sql).hexdigest() == migration['sql']['sha256']
                    and migration['reviewed_source']['sha256'] == hashlib.sha256(
                        (root / migration['reviewed_source']['path']).read_bytes()).hexdigest(),
                    'production migration provenance drift')
            sql_texts.append(sql.decode('utf-8'))
        migration_sql = '\n'.join(sql_texts)
        transforms = []
        for item in manifest['shared_width_transforms']:
            table, name = item['table'], item['column']
            after = production_column(source_columns[table, name])
            require(after['type'] == 'VARCHAR2' and after['length'] == item['after_bytes'],
                    'production shared target differs from reviewed source')
            require(f'ALTER TABLE "{OWNER}"."{table}" MODIFY ("{name}" VARCHAR2({after["length"]} BYTE));'
                    in migration_sql, 'production shared transformation missing from migration')
            before = {**after, 'length': item['before_bytes']}
            transforms.append({'table': table, 'column': name, 'before': before, 'after': after})
        historical, target, file_provenance = core_file_fixture(root)
        target = copy.deepcopy(target)
        for name, (before_width, after_width) in manifest['core_file_width_transforms'].items():
            col = next(c for c in target['columns'] if c['name'] == name)
            require(col['type'] == 'VARCHAR2' and col['length'] == before_width,
                    'production CoreFile predecessor width drift')
            after = production_column(source_columns[FILE_TABLE, name])
            require(after['length'] == after_width
                    and f'ALTER TABLE "{OWNER}"."{FILE_TABLE}" MODIFY ("{name}" VARCHAR2({after_width} BYTE));'
                    in migration_sql, 'production CoreFile width not source-bound')
            col.update(after)
        for name, (precision, scale) in manifest['core_file_number_transforms'].items():
            col = next(c for c in target['columns'] if c['name'] == name)
            require((col['type'], col['precision'], col['scale']) == ('NUMBER', precision, scale),
                    'production CoreFile predecessor number drift')
            after = production_column(source_columns[FILE_TABLE, name])
            require(after['precision'] is None and after['scale'] is None
                    and f'ALTER TABLE "{OWNER}"."{FILE_TABLE}" MODIFY ("{name}" NUMBER);'
                    in migration_sql, 'production CoreFile number not source-bound')
            col.update(after)
        remark = next(c for c in target['columns'] if c['name'] == 'REMARK')
        require(remark['type'] == 'VARCHAR2' and remark['length'] == 258
                and target['columns'].index(remark) == 16
                and f'ALTER TABLE "{OWNER}"."{FILE_TABLE}" DROP COLUMN "REMARK";' in migration_sql
                and f'ALTER TABLE "{OWNER}"."{FILE_TABLE}" ADD ("REMARK" CLOB);' in migration_sql,
                'production CoreFile REMARK migration drift')
        target['columns'].remove(remark)
        target['columns'].append(production_column(source_columns[FILE_TABLE, 'REMARK']))
        require(len(target['columns']) == 21 and target['columns'][-1]['type'] == 'CLOB',
                'production CoreFile physical order drift')
        acquisition_columns = sorted((r for r in source['columns'] if r['TABLE_NAME'] == ACQUISITION_TABLE),
                                     key=lambda r: int(r['COLUMN_ID']))
        require(len(acquisition_columns) == 9, 'production acquisition column coverage drift')
        added = {'columns': [production_column(r) for r in acquisition_columns],
                 'primary_key': {'name': 'APPROVED_ACQ_PK', 'column': 'ID'},
                 'indexes': [], 'comments': {}}
        acquisition_ddl = source['table_ddl'][0]['ddl']
        require(source['table_ddl'][0]['name'] == ACQUISITION_TABLE
                and '\n'.join(line.rstrip() for line in acquisition_ddl.replace(
                    '"SOURCE_SCHEMA".', '"' + OWNER + '".').splitlines()).strip() in migration_sql,
                'production acquisition DDL not source-bound')
        for column, text in re.findall(r'COMMENT ON COLUMN "LIMS_RUNTIME_LOCAL"\."T_EMC_EQUIPT_ACQ_FILE"\."([A-Z_]+)" IS \'((?:[^\']|\'\')*)\';', migration_sql):
            added['comments'][column] = text.replace("''", "'")
        require(len(added['comments']) == 8, 'production acquisition comments incomplete')
        category = json.loads((root / manifest['report_category_fixture']).read_text())
        for item in category['sources']:
            data = (root / item['path']).read_bytes()
            require(hashlib.sha256(data).hexdigest() == item['sha256'], 'report category source drift')
            lines = data.decode('utf-8').splitlines()
            for block in item['blocks']:
                text = '\n'.join(lines[block['line_start'] - 1:block['line_end']])
                require(hashlib.sha256(text.encode()).hexdigest() == block['sha256'],
                        'report category source block drift')
        require(hashlib.sha256((root / category['fixture']['path']).read_bytes()).hexdigest()
                == category['fixture']['sha256'], 'report category fixture drift')
        report_columns = [production_column({'COLUMN_NAME': c['name'], 'DATA_TYPE': c['type'],
                           'DATA_LENGTH': c['data_length'], 'DATA_PRECISION': c['precision'],
                           'DATA_SCALE': c['scale'], 'NULLABLE': c['nullable']})
                          for c in category['existing_columns']]
        report_column = {'name': 'CATEGORY', 'type': 'VARCHAR2', 'length': 36,
                         'precision': None, 'scale': None, 'nullable': 'Y'}
        require(category['added_column'] == {'name': 'CATEGORY', 'type': 'VARCHAR2', 'length': 36,
                'length_semantics': 'BYTE', 'nullable': 'Y', 'default': None, 'comment': '类别'},
                'report category finite definition drift')
        # Reuse the existing count/coverage proof envelope, including the function body.
        predicates = []
        for view, where, count in checks(ACQUISITION_TABLE, added):
            # Stock CREATE TABLE has an unnamed primary key. Pin its exact structure,
            # not the Oracle-generated identifier on this particular imported schema.
            pk_query = "(SELECT CONSTRAINT_NAME FROM ALL_CONSTRAINTS WHERE OWNER='" + OWNER + "' AND TABLE_NAME='" + ACQUISITION_TABLE + "' AND CONSTRAINT_TYPE='P')"
            index_query = "(SELECT INDEX_NAME FROM ALL_CONSTRAINTS WHERE OWNER='" + OWNER + "' AND TABLE_NAME='" + ACQUISITION_TABLE + "' AND CONSTRAINT_TYPE='P')"
            where = where.replace("CONSTRAINT_NAME='APPROVED_ACQ_PK'", 'CONSTRAINT_NAME=' + pk_query)
            where = where.replace("INDEX_NAME='APPROVED_ACQ_PK'", 'INDEX_NAME=' + index_query)
            predicates.append({'table': ACQUISITION_TABLE, 'view': view, 'predicate': where, 'expected_count': count})
        predicates.extend([
            {'table': ACQUISITION_TABLE, 'view': 'ALL_CONSTRAINTS',
             'predicate': "R_OWNER='" + OWNER + "' AND CONSTRAINT_TYPE='R' AND R_CONSTRAINT_NAME=" + pk_query,
             'expected_count': 0},
            {'table': ACQUISITION_TABLE, 'view': 'ALL_TAB_IDENTITY_COLS',
             'predicate': "OWNER='" + OWNER + "' AND TABLE_NAME='" + ACQUISITION_TABLE + "'",
             'expected_count': 0},
        ])
        for item in manifest['indexes']:
            ddl = next(d['ddl'] for d in source['index_ddl'] if d['name'] == item['name'])
            require('\n'.join(line.rstrip() for line in ddl.replace('"SOURCE_SCHEMA".',
                    '"' + OWNER + '".').splitlines()).strip() in migration_sql,
                    'production index DDL not source-bound')
            require(re.findall(r'"([A-Z_]+)"', ddl)[-len(item['columns']):] == item['columns'],
                    'production index column order drift')
            predicates.extend(production_index_predicates(item))
        function = source['function_ddl'][0]
        require(function['name'] == manifest['function'] == 'STRINGTOSCIENCE',
                'production function identity drift')
        function_text = '\n'.join(line.rstrip() for line in function['ddl'].replace(
            '"SOURCE_SCHEMA".', '"' + OWNER + '".').splitlines()).strip()
        require(function_text in migration_sql, 'production function body not migration-bound')
        function_text = re.sub(r'^CREATE OR REPLACE EDITIONABLE ', '', function_text)
        function_text = function_text.replace('"' + OWNER + '".', '', 1)
        function_lines = function_text.splitlines()
        require(len(function_lines) == 44, 'production function line coverage drift')
        function_filter = "OWNER='" + OWNER + "' AND NAME='STRINGTOSCIENCE' AND TYPE='FUNCTION'"
        predicates.append({'table': 'STRINGTOSCIENCE', 'view': 'ALL_OBJECTS',
                           'predicate': "OWNER='" + OWNER + "' AND OBJECT_NAME='STRINGTOSCIENCE' AND OBJECT_TYPE='FUNCTION' AND STATUS='VALID'",
                           'expected_count': 1})
        predicates.append({'table': 'STRINGTOSCIENCE', 'view': 'ALL_SOURCE',
                           'predicate': function_filter, 'expected_count': len(function_lines)})
        expression = "RAWTOHEX(UTL_RAW.CAST_TO_RAW(REGEXP_REPLACE(RTRIM(TEXT,CHR(10)||CHR(13)||' '),'^FUNCTION[[:space:]]+','FUNCTION ')))"
        for number, line in enumerate(function_lines, 1):
            hexadecimal = line.encode('utf-8').hex().upper()
            predicates.append({'table': 'STRINGTOSCIENCE', 'view': 'ALL_SOURCE',
                               'predicate': function_filter + ' AND LINE=' + str(number) + ' AND '
                               + expression + ('=' + literal(hexadecimal) if hexadecimal else ' IS NULL'),
                               'expected_count': 1})
        provenance = {'manifest_path': PRODUCTION_BASE, 'manifest_sha256': PRODUCTION_MANIFEST_SHA256,
                      'pins': manifest['pins'], 'predecessor_descriptor_sha256': digest(predecessor)}
        return {'added_table': added, 'core_file_target': target, 'historical_core_file': historical,
                'shared_column_transforms': transforms,
                'report_category': {'baseline_columns': report_columns, 'column': report_column,
                                    'comment': category['added_column']['comment']},
                'indexes': manifest['indexes'], 'function': {'name': 'STRINGTOSCIENCE',
                    'line_count': len(function_lines), 'source_sha256': digest(function_lines)},
                'provenance': provenance}, predicates
    except (OSError, KeyError, TypeError, ValueError, StopIteration) as error:
        raise PolicyError('production alignment provenance or finite definition rejected') from error


def production_shared_rows(baseline, production):
    """Apply declared before/after shapes; every other baseline row stays exact."""
    expected = copy.deepcopy(baseline)
    for item in production['shared_column_transforms']:
        matches = [r for r in expected if r[:2] == [item['table'], item['column']]]
        require(len(matches) == 1, 'production shared predecessor column missing or duplicate')
        row = matches[0]
        before = production_column_rows(item['table'], [item['before']])[0]
        require(row[3:] == before[3:], 'production shared predecessor column drift')
        after = production_column_rows(item['table'], [item['after']])[0]
        row[3:] = after[3:]
    report = production['report_category']
    require(sorted(r for r in expected if r[0] == 'T_CORE_REPORT_CONFIG')
            == production_column_rows('T_CORE_REPORT_CONFIG', report['baseline_columns']),
            'report category baseline metadata drift')
    expected = [r for r in expected if r[0] != 'T_CORE_REPORT_CONFIG']
    expected.extend(production_column_rows('T_CORE_REPORT_CONFIG',
                    report['baseline_columns'] + [report['column']]))
    return sorted(expected)


def production_definition(root):
    predecessor, predicates = definition(APPROVED_LES, root)
    production, additional = production_fixture(root)
    # Old policy paths and descriptors stay untouched. Only v5 replaces the
    # runtime CoreFile target that was subsequently changed by approved DDL.
    predicates = [p for p in predicates if p['table'] != FILE_TABLE]
    file_predicates = core_file_predicates(production['historical_core_file'], production['core_file_target'])
    for item in file_predicates:
        if item['view'] == 'ALL_INDEXES' and item['predicate'] == "TABLE_OWNER='" + OWNER + "' AND TABLE_NAME='T_CORE_FILE' AND INDEX_TYPE<>'LOB'":
            item['expected_count'] = 5
    predicates.extend(file_predicates)
    predicates.extend(additional)
    for item in production['shared_column_transforms']:
        col = item['after']
        predicates.append({'table': item['table'], 'view': 'ALL_TAB_COLS',
            'predicate': "OWNER='" + OWNER + "' AND TABLE_NAME=" + literal(item['table'])
                + ' AND COLUMN_NAME=' + literal(item['column'])
                + " AND DEFAULT_LENGTH IS NULL AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO'"
                + " AND DATA_TYPE='VARCHAR2' AND CHAR_USED='B' AND NULLABLE=" + literal(col['nullable'])
                + ' AND DATA_LENGTH=' + str(col['length']) + ' AND CHAR_LENGTH=' + str(col['length'])
                + ' AND DATA_PRECISION IS NULL AND DATA_SCALE IS NULL', 'expected_count': 1})
    predicates.extend([
        {'table': 'T_CORE_REPORT_CONFIG', 'view': 'ALL_TAB_COLS',
         'predicate': "OWNER='" + OWNER + "' AND TABLE_NAME='T_CORE_REPORT_CONFIG' AND COLUMN_NAME='CATEGORY' AND COLUMN_ID=8 AND DATA_TYPE='VARCHAR2' AND DATA_LENGTH=36 AND CHAR_LENGTH=36 AND CHAR_USED='B' AND NULLABLE='Y' AND DATA_PRECISION IS NULL AND DATA_SCALE IS NULL AND DEFAULT_LENGTH IS NULL AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO'", 'expected_count': 1},
        {'table': 'T_CORE_REPORT_CONFIG', 'view': 'ALL_COL_COMMENTS',
         'predicate': "OWNER='" + OWNER + "' AND TABLE_NAME='T_CORE_REPORT_CONFIG' AND COLUMN_NAME='CATEGORY' AND COMMENTS=" + literal(production['report_category']['comment']), 'expected_count': 1},
        {'table': 'T_CORE_REPORT_CONFIG', 'view': 'ALL_CONS_COLUMNS',
         'predicate': "OWNER='" + OWNER + "' AND TABLE_NAME='T_CORE_REPORT_CONFIG' AND COLUMN_NAME='CATEGORY'", 'expected_count': 0},
    ])
    descriptor = {**predecessor, 'id': APPROVED_PRODUCTION, 'table_count': 12,
                  'column_count': 161, 'altered_tables': sorted({FILE_TABLE, 'T_CORE_REPORT_CONFIG',
                     *(i['table'] for i in production['shared_column_transforms'])}),
                  'core_file': {**predecessor['core_file'],
                     'predecessor_runtime_definition_sha256': predecessor['core_file']['runtime_definition_sha256'],
                     'runtime_definition_sha256': digest(production['core_file_target'])},
                  'production_alignment': production['provenance'],
                  'production_definition_sha256': digest({k: v for k, v in production.items() if k != 'provenance'}),
                  'definition_sha256': {**predecessor['definition_sha256'],
                     ACQUISITION_TABLE: digest(production['added_table'])},
                  'predicates_sha256': digest(predicates)}
    return descriptor, predicates


def external_inputs(root):
    """Only the hash-pinned delivered inputs; private provenance is never opened."""
    root = Path(root)
    try:
        contents = {}
        for path, sha256 in EXTERNAL_PINS.items():
            raw = (root / path).read_bytes()
            require(hashlib.sha256(raw).hexdigest() == sha256, 'external schema source pin drift')
            contents[path] = json.loads(raw)
        manifest = contents[EXTERNAL_BASE + 'manifest.json']
        report = contents['deployment/report-engine/manifest.json']
        for base, files in ((EXTERNAL_BASE, manifest['files']),
                            ('deployment/report-engine/', report['dependencies'])):
            for item in files:
                require(hashlib.sha256((root / (base + item['path'])).read_bytes()).hexdigest()
                        == item['sha256'], 'external schema delivered file drift')
        structure = contents[EXTERNAL_BASE + 'structure.json']
        pack = contents[PACKPRICE_BASE + '.json']
        require({t['name'] for t in structure['tables']} | {pack['object']} == EXTERNAL_TABLES
                and len(structure['tables']) == 10, 'external schema finite table coverage drift')
        return structure, pack
    except (OSError, KeyError, TypeError, ValueError) as error:
        raise PolicyError('external schema source or finite definition rejected') from error


def external_match(fields):
    """Finite metadata values are SQL literals, never executable source text."""
    return ' AND '.join(key + (' IS NULL' if value is None else '=' + literal(str(value)))
                        for key, value in fields.items())


def external_columns(structure, pack):
    tables = copy.deepcopy(structure['tables'])
    metadata = pack['metadata']
    def rows(name):
        item = metadata[name]
        return [dict(zip(item['columns'], row)) for row in item['rows']]
    columns = [{'name': c['COLUMN_NAME'], 'position': int(c['COLUMN_ID']),
                'type': c['DATA_TYPE'], 'length': c['DATA_LENGTH'],
                'precision': c['DATA_PRECISION'], 'scale': c['DATA_SCALE'],
                'nullable': c['NULLABLE'], 'char_used': c['CHAR_USED'],
                'char_length': c['CHAR_LENGTH'], 'default': None} for c in rows('columns')]
    require(not rows('column_defaults') and not rows('index_expressions') and not rows('triggers')
            and all(c['DEFAULT_LENGTH'] is None for c in rows('columns')),
            'unsupported PACKPRICE default, expression or trigger')
    links = {c['CONSTRAINT_NAME']: c for c in rows('constraint_index_links')}
    constraints = []
    for c in rows('constraints'):
        generated = c['GENERATED'] == 'GENERATED NAME'
        require(not generated or (c['CONSTRAINT_TYPE'] == 'C'
                and c['SEARCH_CONDITION_VC'] == '"ID" IS NOT NULL'),
                'unsupported generated PACKPRICE constraint')
        constraints.append({'name': c['CONSTRAINT_NAME'], 'kind': c['CONSTRAINT_TYPE'],
            'status': c['STATUS'], 'validated': c['VALIDATED'], 'deferrable': c['DEFERRABLE'],
            'deferred': c['DEFERRED'], 'condition': c['SEARCH_CONDITION_VC'],
            'index_name': links.get(c['CONSTRAINT_NAME'], {}).get('INDEX_NAME'),
            'generated': c['GENERATED'], 'generated_target_name': generated,
            'columns': [[r['POSITION'], r['COLUMN_NAME']] for r in rows('constraint_columns')
                        if r['CONSTRAINT_NAME'] == c['CONSTRAINT_NAME']]})
    indexes = [{'name': i['INDEX_NAME'], 'uniqueness': i['UNIQUENESS'],
                'columns': [[c['COLUMN_POSITION'], c['COLUMN_NAME'], c['DESCEND']]
                            for c in rows('index_columns') if c['INDEX_NAME'] == i['INDEX_NAME']]}
               for i in rows('indexes')]
    attributes = {k: v for k, v in rows('table_semantics')[0].items() if k not in ('OWNER', 'TABLE_NAME')}
    # READ_ONLY is a formal target check, not a captured source field. Likewise
    # the pinned CREATE TABLE declares only ordinary nonvirtual, visible columns.
    attributes['READ_ONLY'] = 'NO'
    tables.append({'name': pack['object'], 'columns': columns, 'constraints': constraints,
                   'indexes': indexes, 'attributes': attributes})
    require(sum(len(t['columns']) for t in tables) == 1312
            and sum(len(t['constraints']) for t in tables) == 28
            and sum(len(t['indexes']) for t in tables) == 46,
            'external schema finite metadata coverage drift')
    return tables


def external_predicates(structure, pack):
    """Mirror the delivered verifier's selected-table contract, without its
    empty-installer restrictions. Unsupported future metadata stays rejected by
    input pins; no table learning, DDL execution or business-row predicates.
    """
    predicates = []
    semantics = {t['name']: t for t in structure['semantic_contract']['tables']}
    def add(table, view, fields, count=1, suffix=''):
        predicates.append({'table': table, 'view': view,
            'predicate': external_match(fields) + suffix, 'expected_count': count})
    domains = list(structure['semantic_contract']['domains'].values())
    for domain in domains:
        require(domain['database_nls'] == domains[0]['database_nls']
                and domain['link_session_nls'] == domains[0]['link_session_nls'],
                'unsupported divergent external NLS contract')
    for view, key in (('NLS_DATABASE_PARAMETERS', 'database_nls'),
                      ('NLS_SESSION_PARAMETERS', 'link_session_nls')):
        for parameter, value in sorted(domains[0][key].items()):
            add('EXTERNAL_NLS', view, {'PARAMETER': parameter, 'VALUE': value})
    for table in external_columns(structure, pack):
        name = table['name']
        key = {'OWNER': OWNER, 'TABLE_NAME': name}
        sem = semantics.get(name)
        attrs = sem['attributes'] if sem else table['attributes']
        add(name, 'ALL_TABLES', {**key, **attrs})
        add(name, 'ALL_TAB_COLS', key, len(table['columns']))
        col_sem = {c['name']: c for c in sem['columns']} if sem else {}
        for c in table['columns']:
            require(c['type'] in ('VARCHAR2', 'CHAR', 'NUMBER', 'DATE', 'CLOB', 'TIMESTAMP(6)'),
                    'unsupported external column type')
            fields = {**key, 'COLUMN_NAME': c['name'], 'COLUMN_ID': c['position'],
                'DATA_TYPE': c['type'], 'DATA_LENGTH': c['length'],
                'DATA_PRECISION': c['precision'], 'DATA_SCALE': c['scale'],
                'NULLABLE': c['nullable'], 'CHAR_USED': c['char_used'],
                'CHAR_LENGTH': c['char_length'], 'DATA_TYPE_OWNER': None,
                'HIDDEN_COLUMN': 'NO', 'VIRTUAL_COLUMN': 'NO'}
            if sem:
                cs = col_sem[c['name']]
                fields.update(COLLATION=cs['collation'], IDENTITY_COLUMN=cs['identity_column'],
                    DEFAULT_ON_NULL=cs['default_on_null'], USER_GENERATED=cs['user_generated'],
                    VIRTUAL_COLUMN=cs['virtual_column'])
            default = c['default']
            add(name, 'ALL_TAB_COLS', fields, suffix=' AND DEFAULT_LENGTH IS '
                + ('NULL' if default is None else 'NOT NULL'))
            if default is not None:
                # DBMS_XMLGEN is the read-only LONG-to-XML bridge. Oracle TRIM
                # matches 90-verify.sql; hex preserves quotes, Unicode and inner
                # whitespace and rejects equal-length replacement expressions.
                select = 'SELECT DATA_DEFAULT FROM ALL_TAB_COLUMNS WHERE ' + external_match(
                    {**key, 'COLUMN_NAME': c['name']})
                view = '(SELECT EXTRACTVALUE(DBMS_XMLGEN.GETXMLTYPE(' + literal(select) \
                       + "),'/ROWSET/ROW/DATA_DEFAULT') DEFAULT_TEXT FROM DUAL)"
                add(name, view, {'RAWTOHEX(UTL_RAW.CAST_TO_RAW(TRIM(DEFAULT_TEXT)))':
                                default.strip(' ').encode('utf-8').hex().upper()})
        add(name, 'ALL_CONSTRAINTS', key, len(table['constraints']))
        constraint_sem = {c['name']: c for c in sem['constraints']} if sem else {}
        for c in table['constraints']:
            require(c['kind'] in ('P', 'U', 'C'), 'unsupported external constraint kind')
            fields = {**key, 'CONSTRAINT_TYPE': c['kind'], 'STATUS': c['status'],
                'VALIDATED': c['validated'], 'DEFERRABLE': c['deferrable'], 'DEFERRED': c['deferred'],
                'SEARCH_CONDITION_VC': c['condition'], 'INDEX_NAME': c['index_name'],
                'INDEX_OWNER': OWNER if c['index_name'] is not None else None}
            if sem:
                cs = constraint_sem[c['name']]
                fields.update(GENERATED=cs['target_generated'], RELY=cs['rely'],
                              BAD=cs['bad'], INVALID=cs['invalid'], VIEW_RELATED=cs['view_related'])
            else:
                fields['GENERATED'] = c['generated']
            if c.get('generated_target_name'):
                # Only PACKPRICE's one explicitly remapped NOT NULL constraint
                # may use its actual generated name; count and full shape remain exact.
                name_filter = 'CONSTRAINT_NAME=(SELECT CONSTRAINT_NAME FROM ALL_CONSTRAINTS WHERE ' \
                    + external_match({**key, 'CONSTRAINT_TYPE': 'C', 'GENERATED': 'GENERATED NAME',
                                      'SEARCH_CONDITION_VC': c['condition']}) + ')'
            else:
                name_filter = 'CONSTRAINT_NAME=' + literal(c['name'])
            add(name, 'ALL_CONSTRAINTS', fields, suffix=' AND ' + name_filter)
            add(name, 'ALL_CONS_COLUMNS', key, len(c['columns']), suffix=' AND ' + name_filter)
            for position, column in c['columns']:
                add(name, 'ALL_CONS_COLUMNS', {**key, 'COLUMN_NAME': column, 'POSITION': position},
                    suffix=' AND ' + name_filter)
        add(name, 'ALL_INDEXES', {'TABLE_OWNER': OWNER, 'TABLE_NAME': name}, len(table['indexes']),
            suffix=" AND INDEX_TYPE<>'LOB'")
        for index in table['indexes']:
            add(name, 'ALL_INDEXES', {'OWNER': OWNER, 'TABLE_OWNER': OWNER, 'TABLE_NAME': name,
                'INDEX_NAME': index['name'], 'INDEX_TYPE': 'NORMAL', 'STATUS': 'VALID',
                'UNIQUENESS': index['uniqueness']})
            ikey = {'INDEX_OWNER': OWNER, 'INDEX_NAME': index['name'], 'TABLE_OWNER': OWNER, 'TABLE_NAME': name}
            add(name, 'ALL_IND_COLUMNS', ikey, len(index['columns']))
            for position, column, direction in index['columns']:
                add(name, 'ALL_IND_COLUMNS', {**ikey, 'COLUMN_POSITION': position,
                                              'COLUMN_NAME': column, 'DESCEND': direction})
        add(name, 'ALL_IND_EXPRESSIONS', {'TABLE_OWNER': OWNER, 'TABLE_NAME': name}, 0)
        add(name, 'ALL_TRIGGERS', {'TABLE_OWNER': OWNER, 'TABLE_NAME': name}, 0)
    return predicates


def external_definition(root):
    predecessor, predicates = definition(APPROVED_PRODUCTION, root)
    structure, pack = external_inputs(root)
    predicates = [*predicates, *external_predicates(structure, pack)]
    descriptor = {**predecessor, 'id': APPROVED_EXTERNAL, 'table_count': 23,
        'column_count': 1473, 'external_schema': {'tables': sorted(EXTERNAL_TABLES),
            'pins': EXTERNAL_PINS, 'predecessor_descriptor_sha256': digest(predecessor),
            'definition_sha256': digest({'structure': structure, 'packprice': pack})},
        'predicates_sha256': digest(predicates)}
    return descriptor, predicates


def definition(policy=EQUAL, root=ROOT):
    require(policy in POLICIES, 'unknown local schema policy')
    if policy == APPROVED_EXTERNAL:
        return external_definition(root)
    if policy == APPROVED_PRODUCTION:
        return production_definition(root)
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
        require(policy in (APPROVED, APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL) and not baseline.intersection(tables)
                and runtime == baseline.union(tables), 'approved schema table delta mismatch')
        if policy in (APPROVED_FILE, APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL):
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
        if policy in (APPROVED_EMAIL, APPROVED_FILE, APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL):
            loaded.update(email_fixture(ROOT)[0])
        if policy in (APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL):
            loaded.update(les_fixture(ROOT)[0])
        production = None
        if policy in (APPROVED_PRODUCTION, APPROVED_EXTERNAL):
            production, _ = production_fixture(ROOT)
            loaded[ACQUISITION_TABLE] = production['added_table']
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
        if policy == APPROVED_EXTERNAL:
            structure, pack = external_inputs(ROOT)
            for table in external_columns(structure, pack):
                exact = sorted([[table['name'], c['name'], str(c['position']), c['type'],
                    str(c['length']), str(c['precision']) if c['precision'] is not None else '-',
                    str(c['scale']) if c['scale'] is not None else '-', c['nullable'],
                    str(c['char_length']), c['char_used'] or '-', '-'] for c in table['columns']])
                require(sorted(r for r in columns if r[0] == table['name']) == exact,
                        'external exact column metadata mismatch')
        shared = [r for r in columns if r[0] not in policy_tables(policy)]
        baseline = snapshot['schemas']['LIMS_BASELINE_LOCAL']['column_metadata']['rows']
        if policy in (APPROVED_FILE, APPROVED_LES, APPROVED_PRODUCTION, APPROVED_EXTERNAL):
            historical, target, _ = core_file_fixture(ROOT)
            require(sorted(r for r in baseline if r[0] == FILE_TABLE) == core_file_column_rows(historical['columns']),
                    'CoreFile baseline column metadata mismatch')
            if production is not None:
                target = production['core_file_target']
            target_rows = (production_column_rows(FILE_TABLE, target['columns']) if production is not None
                           else core_file_column_rows(target['columns']))
            require(sorted(r for r in shared if r[0] == FILE_TABLE) == target_rows,
                    'CoreFile runtime column metadata mismatch')
            shared = [r for r in shared if r[0] != FILE_TABLE]
            baseline = [r for r in baseline if r[0] != FILE_TABLE]
        if production is not None:
            baseline = production_shared_rows(baseline, production)
            require(sorted(r for r in columns if r[0] == ACQUISITION_TABLE)
                    == production_column_rows(ACQUISITION_TABLE, production['added_table']['columns']),
                    'production acquisition exact column metadata mismatch')
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
