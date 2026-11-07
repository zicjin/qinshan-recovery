#!/usr/bin/env python3
"""Verify source-backed schema fixtures and render guarded SQLPlus; never connect."""
from __future__ import annotations
import argparse
import hashlib
import json
from pathlib import Path
import re
import sys

OWNER = 'LIMS_RUNTIME_LOCAL'
BASE = 'api-fixtures/schema/missing-runtime-tables'
SOURCE_PATHS = frozenset({
    'project/sql/oracle/oracle_sql_emc.txt',
    'project/sql/核心发版14.1.5/init-sql/oracle/0.common_sql_oracle-1.txt',
})
TABLES = frozenset({
    'T_EMC_INVOICE', 'T_EMC_RETURNED', 'T_EMC_QC_ITEM', 'T_EMC_SEAL_RULE',
    'T_EMC_SEAL_RULE_DETAILS', 'T_CORE_EMAIL_CONFIG', 'T_CORE_EMAIL_TEMPLATE',
})
IDENT = r'"?([A-Z][A-Z0-9_]*)"?'


class FixtureError(ValueError):
    pass


def sha(value):
    return hashlib.sha256(value).hexdigest()


def statements(text):
    """Only semicolon-delimited DDL; quoted comment literals may contain semicolons."""
    parts = re.findall(r"(?:'[^']*(?:''[^']*)*'|[^';])+;", text)
    if ''.join(parts).strip() != text.strip():
        raise FixtureError('unterminated or unsupported fixture text')
    return [part.strip() for part in parts]


def column(text):
    match = re.fullmatch(IDENT + r'\s+(NUMBER(?:\(\d+\))?|VARCHAR2\(\d+(?: BYTE)?\)|DATE|CLOB)(?:\s+(NOT NULL|NULL))?', text.strip())
    if not match:
        raise FixtureError('unsupported column definition: ' + text)
    name, datatype, nullable = match.groups()
    precision = re.fullmatch(r'NUMBER\((\d+)\)', datatype)
    varchar = re.fullmatch(r'VARCHAR2\((\d+)(?: BYTE)?\)', datatype)
    return {'name': name, 'type': datatype.split('(')[0],
            'length': int(varchar[1]) if varchar else None,
            'precision': int(precision[1]) if precision else None,
            'scale': 0 if precision else None,
            'nullable': 'N' if nullable == 'NOT NULL' else 'Y'}


def definitions(sql):
    result = {}
    for stmt in sql:
        match = re.fullmatch(r'CREATE TABLE\s+' + IDENT + r'\s*\((.*?)\)\s*;', stmt, re.S)
        if match:
            name, body = match.groups()
            if name not in TABLES or name in result:
                raise FixtureError('unapproved or duplicate table')
            result[name] = {'columns': [column(c) for c in body.split(',')],
                            'primary_key': None, 'indexes': [], 'comments': {}, 'statements': []}
        else:
            match = re.fullmatch(r'ALTER TABLE\s+' + IDENT + r' ADD CONSTRAINT\s+' + IDENT + r' PRIMARY KEY\s*\(\s*' + IDENT + r'\s*\);', stmt)
            if match:
                name, key, col = match.groups()
                if name not in result or result[name]['primary_key']:
                    raise FixtureError('unknown table or duplicate primary key')
                result[name]['primary_key'] = {'name': key, 'column': col}
            else:
                match = re.fullmatch(r'ALTER TABLE\s+' + IDENT + r' ADD\s+(.+);', stmt, re.S)
                if match:
                    name, body = match.groups()
                    if name not in result:
                        raise FixtureError('unknown ALTER target')
                    result[name]['columns'].append(column(body))
                else:
                    match = re.fullmatch(r'CREATE INDEX\s+' + IDENT + r' ON\s+' + IDENT + r'\s*\(\s*' + IDENT + r'(?: ASC)?\s*\);', stmt)
                    if match:
                        index, name, col = match.groups()
                        if name not in result:
                            raise FixtureError('unknown index target')
                        result[name]['indexes'].append({'name': index, 'column': col})
                    else:
                        match = re.fullmatch(r'COMMENT ON (TABLE|COLUMN)\s+' + IDENT + r'(?:\."?([A-Z][A-Z0-9_]*)"?)?\s+IS\s+\'((?:[^\']|\'\')*)\';', stmt, re.I | re.S)
                        if not match:
                            raise FixtureError('only approved CREATE/ALTER/INDEX/COMMENT allowed')
                        kind, name, col, value = match.groups()
                        if name not in result or ((kind.upper() == 'COLUMN') != bool(col)):
                            raise FixtureError('unknown comment target')
                        result[name]['comments'][col or ''] = value.replace("''", "'")
        result[name]['statements'].append(stmt)
    return result


def load_fixture(root):
    root = Path(root)
    manifest = json.loads((root / (BASE + '.json')).read_text(encoding='utf-8'))
    if manifest.get('version') != 1 or manifest.get('owner') != OWNER:
        raise FixtureError('unsupported fixture manifest or owner')
    sources = {}
    for entry in manifest['sources']:
        path = entry['path']
        if path not in SOURCE_PATHS or path in sources:
            raise FixtureError('unapproved source path')
        data = (root / path).read_bytes()
        if sha(data) != entry['sha256']:
            raise FixtureError('source file hash mismatch: ' + path)
        sources[path] = data.decode('utf-8').splitlines(keepends=True)
    fixture = (root / (BASE + '.sql')).read_bytes()
    if sha(fixture) != manifest['fixture_sha256']:
        raise FixtureError('fixture hash mismatch')
    verified = []
    for entry in manifest['statements']:
        if entry['source'] not in sources:
            raise FixtureError('unbound statement source')
        start, end = entry['line_start'], entry['line_end']
        if not isinstance(start, int) or not isinstance(end, int) or not 1 <= start <= end <= len(sources[entry['source']]):
            raise FixtureError('invalid source range')
        raw = ''.join(sources[entry['source']][start-1:end]).strip()
        if sha(raw.encode()) != entry['source_sha256']:
            raise FixtureError('source statement hash mismatch')
        transformations = entry['transformations']
        if transformations == ['fullwidth-semicolon-terminator'] and raw.endswith('；'):
            raw = raw[:-1] + ';'
        elif transformations:
            raise FixtureError('unapproved source transformation')
        if sha(raw.encode()) != entry['sha256'] or len(statements(raw)) != 1:
            raise FixtureError('statement hash or boundary mismatch')
        verified.append(raw)
    if statements(fixture.decode('utf-8')) != verified:
        raise FixtureError('fixture differs from verified source statements')
    result = definitions(verified)
    if not result:
        raise FixtureError('empty fixture')
    return result


def plan(loaded, owner, metadata=None):
    if owner != OWNER:
        raise FixtureError('only LIMS_RUNTIME_LOCAL is allowed')
    create, skip = [], []
    for table, expected in loaded.items():
        if metadata is None:
            create.append(table)
            continue
        if table not in metadata:
            raise FixtureError('incomplete metadata: ' + table)
        actual = metadata[table]
        if actual is None:
            create.append(table)
        elif actual == {k: v for k, v in expected.items() if k != 'statements'}:
            skip.append(table)
        else:
            raise FixtureError('incompatible existing schema: ' + table)
    return {'owner': owner, 'create': create, 'skip': skip,
            'metadata_verified': metadata is not None, 'strict_evidence': False}


def literal(value):
    return "'" + str(value).replace("'", "''") + "'"


def predicate(field, value):
    return field + (' IS NULL' if value is None else '=' + (str(value) if isinstance(value, int) else literal(value)))


def checks(table, definition):
    """Return exact logical metadata predicates, independent of storage/tablespace."""
    filt = 'OWNER=' + literal(OWNER) + ' AND TABLE_NAME=' + literal(table)
    queries = [('ALL_TABLES', filt + " AND TEMPORARY='N' AND NESTED='NO' AND SECONDARY='N' AND PARTITIONED='NO'", 1),
               # ALL_TAB_COLS exposes hidden/virtual flags; count every column
               # so an unexpected hidden column cannot escape equivalence checks.
               ('ALL_TAB_COLS', filt, len(definition['columns']))]
    for position, col in enumerate(definition['columns'], 1):
        terms = [filt, 'COLUMN_ID=' + str(position), 'DEFAULT_LENGTH IS NULL',
                 "HIDDEN_COLUMN='NO'", "VIRTUAL_COLUMN='NO'"]
        for field, key in [('COLUMN_NAME', 'name'), ('DATA_TYPE', 'type'), ('DATA_PRECISION', 'precision'), ('DATA_SCALE', 'scale'), ('NULLABLE', 'nullable')]:
            terms.append(predicate(field, col[key]))
        if col['type'] == 'VARCHAR2':
            terms += ['CHAR_LENGTH=' + str(col['length']), "CHAR_USED='B'", 'DATA_LENGTH=' + str(col['length'])]
        queries.append(('ALL_TAB_COLS', ' AND '.join(terms), 1))
    pk = definition['primary_key']
    notnulls = sum(c['nullable'] == 'N' for c in definition['columns'])
    queries.append(('ALL_CONSTRAINTS', filt, notnulls + bool(pk)))
    queries.append(('ALL_CONSTRAINTS', filt + " AND CONSTRAINT_TYPE='C' AND STATUS='ENABLED' AND VALIDATED='VALIDATED'", notnulls))
    for col in definition['columns']:
        if col['nullable'] == 'N':
            condition = '"' + col['name'] + '" IS NOT NULL'
            queries.append(('ALL_CONSTRAINTS', filt + " AND CONSTRAINT_TYPE='C' AND SEARCH_CONDITION_VC=" + literal(condition), 1))
    if pk:
        key = filt + ' AND CONSTRAINT_NAME=' + literal(pk['name'])
        queries += [('ALL_CONSTRAINTS', key + " AND CONSTRAINT_TYPE='P' AND STATUS='ENABLED' AND VALIDATED='VALIDATED' AND DEFERRABLE='NOT DEFERRABLE'", 1),
                    ('ALL_CONS_COLUMNS', key, 1),
                    ('ALL_CONS_COLUMNS', key + ' AND COLUMN_NAME=' + literal(pk['column']) + ' AND POSITION=1', 1)]
    indexes = [(i, 'NONUNIQUE') for i in definition['indexes']]
    if pk:
        indexes.append((pk, 'UNIQUE'))
    queries.append(('ALL_INDEXES', 'TABLE_OWNER=' + literal(OWNER) + ' AND TABLE_NAME=' + literal(table) + " AND INDEX_TYPE<>'LOB'", len(indexes)))
    for index, unique in indexes:
        key = 'OWNER=' + literal(OWNER) + ' AND INDEX_NAME=' + literal(index['name'])
        queries.append(('ALL_INDEXES', key + ' AND TABLE_NAME=' + literal(table) + " AND INDEX_TYPE='NORMAL' AND STATUS='VALID' AND UNIQUENESS=" + literal(unique), 1))
        key = 'INDEX_OWNER=' + literal(OWNER) + ' AND INDEX_NAME=' + literal(index['name'])
        queries += [('ALL_IND_COLUMNS', key, 1), ('ALL_IND_COLUMNS', key + ' AND COLUMN_NAME=' + literal(index['column']) + " AND COLUMN_POSITION=1 AND DESCEND='ASC'", 1)]
    for col in definition['columns']:
        value = definition['comments'].get(col['name'])
        queries.append(('ALL_COL_COMMENTS', filt + ' AND COLUMN_NAME=' + literal(col['name']) + ' AND ' + predicate('COMMENTS', value), 1))
    queries.append(('ALL_TAB_COMMENTS', filt + ' AND ' + predicate('COMMENTS', definition['comments'].get('')), 1))
    queries.append(('ALL_TRIGGERS', 'TABLE_OWNER=' + literal(OWNER) + ' AND TABLE_NAME=' + literal(table), 0))
    return queries


def render(loaded, owner):
    plan(loaded, owner)
    lines = ['WHENEVER SQLERROR EXIT FAILURE ROLLBACK', 'WHENEVER OSERROR EXIT FAILURE ROLLBACK',
             'SET DEFINE OFF', 'SET SQLBLANKLINES ON', 'SET SERVEROUTPUT ON', 'DECLARE',
             '  n PLS_INTEGER;', '  v_semantics VARCHAR2(10);',
             '  TYPE flags IS TABLE OF BOOLEAN INDEX BY VARCHAR2(128);',
             '  missing flags;', '  PROCEDURE verify_table(t VARCHAR2) IS BEGIN']
    for table, definition in loaded.items():
        lines.append('    IF t=' + literal(table) + ' THEN')
        for view, where, count in checks(table, definition):
            lines += ['      SELECT COUNT(*) INTO n FROM ' + view + ' WHERE ' + where + ';',
                      '      IF n<>' + str(count) + " THEN RAISE_APPLICATION_ERROR(-20002, 'Incompatible " + table + ' ' + view + "'); END IF;"]
        lines.append('    END IF;')
    lines += ['  END;', 'BEGIN',
              "  IF SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')<>'LIMS_RUNTIME_LOCAL' THEN",
              "    RAISE_APPLICATION_ERROR(-20001, 'Only LIMS_RUNTIME_LOCAL is allowed'); END IF;",
              "  IF SYS_CONTEXT('USERENV', 'SESSION_USER') NOT IN ('SYS', 'LIMS_RUNTIME_LOCAL') THEN",
              "    RAISE_APPLICATION_ERROR(-20001, 'Session must be SYS or LIMS_RUNTIME_LOCAL'); END IF;",
              "  SELECT VALUE INTO v_semantics FROM NLS_SESSION_PARAMETERS WHERE PARAMETER='NLS_LENGTH_SEMANTICS';",
              "  IF v_semantics<>'BYTE' THEN RAISE_APPLICATION_ERROR(-20001, 'NLS_LENGTH_SEMANTICS must be BYTE'); END IF;"]
    for table in loaded:
        lines += ["  SELECT COUNT(*) INTO n FROM ALL_OBJECTS WHERE OWNER='LIMS_RUNTIME_LOCAL' AND OBJECT_NAME=" + literal(table) + ';',
                  '  missing(' + literal(table) + '):=n=0;',
                  '  IF n<>0 THEN verify_table(' + literal(table) + '); END IF;']
    for table, definition in loaded.items():
        lines.append('  IF missing(' + literal(table) + ') THEN')
        pk = definition['primary_key']
        if pk:
            lines += ["    SELECT COUNT(*) INTO n FROM ALL_CONSTRAINTS WHERE OWNER='LIMS_RUNTIME_LOCAL' AND CONSTRAINT_NAME=" + literal(pk['name']) + ';',
                      "    IF n<>0 THEN RAISE_APPLICATION_ERROR(-20004, 'Constraint name collision'); END IF;"]
        for item in definition['indexes'] + ([pk] if pk else []):
            lines += ["    SELECT COUNT(*) INTO n FROM ALL_OBJECTS WHERE OWNER='LIMS_RUNTIME_LOCAL' AND OBJECT_NAME=" + literal(item['name']) + ';',
                      "    IF n<>0 THEN RAISE_APPLICATION_ERROR(-20004, 'Index name collision'); END IF;"]
        lines.append('  END IF;')
    lines.append('  -- Every target has passed preflight before any DDL is executed.')
    for table, definition in loaded.items():
        lines += ['  IF missing(' + literal(table) + ') THEN',
                  "    SELECT COUNT(*) INTO n FROM ALL_OBJECTS WHERE OWNER='LIMS_RUNTIME_LOCAL' AND OBJECT_NAME=" + literal(table) + ';',
                  "    IF n<>0 THEN RAISE_APPLICATION_ERROR(-20003, 'Concurrent schema change'); END IF;"]
        for stmt in definition['statements']:
            lines.append('    EXECUTE IMMEDIATE ' + literal(stmt[:-1]) + ';')
        lines += ['    verify_table(' + literal(table) + ');',
                  "    DBMS_OUTPUT.PUT_LINE('created " + table + "');", '  ELSE',
                  "    DBMS_OUTPUT.PUT_LINE('skipped equivalent " + table + "');", '  END IF;']
    lines += ['END;', '/', 'EXIT SUCCESS', '']
    return '\n'.join(lines)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--owner', default=OWNER)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument('--plan', action='store_true')
    mode.add_argument('--render', action='store_true')
    parser.add_argument('--metadata', type=Path, help='optional complete table metadata JSON for offline plan only')
    args = parser.parse_args()
    try:
        if args.metadata and not args.plan:
            raise FixtureError('--metadata is only supported with --plan')
        loaded = load_fixture(Path(__file__).resolve().parents[1])
        if args.plan:
            metadata = json.loads(args.metadata.read_text()) if args.metadata else None
            print(json.dumps(plan(loaded, args.owner, metadata), indent=2))
        else:
            print(render(loaded, args.owner), end='')
    except (FixtureError, OSError, KeyError, TypeError, json.JSONDecodeError) as error:
        parser.exit(1, 'schema fixture rejected: ' + str(error) + '\n')


if __name__ == '__main__':
    main()
