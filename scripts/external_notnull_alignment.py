"""Finite source-bound migration for the thirteen duplicate external NOT NULL checks."""
from __future__ import annotations

import hashlib
import json
from pathlib import Path
import re

SOURCE_DIRECTORY = 'deployment/oracle/external-schema/'
SOURCES = ('structure.json', 'manifest.json', '20-tables.sql', '30-indexes.sql', '35-constraints.sql', '90-verify.sql')
SCHEMA = 'external-notnull-alignment-v1'


def fail(message):
    raise ValueError(message)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def source_items(structure):
    items = []
    for table in structure['tables']:
        for constraint in table['constraints']:
            match = re.fullmatch(r'"([A-Z][A-Z0-9_]*)" IS NOT NULL', constraint.get('condition') or '')
            if constraint['kind'] == 'C' and match:
                column = match.group(1)
                if next(c['nullable'] for c in table['columns'] if c['name'] == column) != 'N':
                    fail('formal NOT NULL is nullable')
                items.append({'table': table['name'], 'column': column, 'constraint': constraint['name'],
                              'temporary_constraint': 'SW_NN_DUP_' + str(len(items) + 1).zfill(2)})
    if len(items) != 13:
        fail('expected exactly thirteen formal NOT NULL constraints')
    return items


def render(structure):
    items = source_items(structure)
    sql = ["""-- Stage 2a: retain the real inline NOT NULL; remove only its redundant ordinary CHECK.
-- Source: deployment/oracle/external-schema/structure.json and formal installer SQL.
-- Application and schema writers must stay stopped; coordinator retains backup/content hashes.
-- Oracle DDL commits implicitly. Preserve errors; rerun accepts only the four declared states.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
ALTER SESSION SET DDL_LOCK_TIMEOUT=5;
DECLARE
  terminal_only BOOLEAN := FALSE;
  n NUMBER;

  PROCEDURE reject(p_message VARCHAR2) IS
  BEGIN RAISE_APPLICATION_ERROR(-20260,p_message); END;

  FUNCTION constraint_role(p_table VARCHAR2, p_column VARCHAR2, p_name VARCHAR2)
    RETURN VARCHAR2 IS
    ddl CLOB;
    compact VARCHAR2(32767);
    base VARCHAR2(1000);
    named_clause VARCHAR2(1000);
    total NUMBER;
  BEGIN
    SELECT COUNT(*) INTO total FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME=p_name;
    IF total=0 THEN RETURN 'ABSENT'; END IF;
    SELECT COUNT(*) INTO total FROM USER_CONSTRAINTS
      WHERE CONSTRAINT_NAME=p_name AND TABLE_NAME=p_table AND CONSTRAINT_TYPE='C'
        AND SEARCH_CONDITION_VC='"'||p_column||'" IS NOT NULL'
        AND STATUS='ENABLED' AND VALIDATED='VALIDATED'
        AND DEFERRABLE='NOT DEFERRABLE' AND DEFERRED='IMMEDIATE'
        AND INDEX_NAME IS NULL AND RELY IS NULL AND BAD IS NULL
        AND INVALID IS NULL AND VIEW_RELATED IS NULL;
    IF total<>1 THEN reject('Constraint state conflicts: '||p_name); END IF;
    SELECT COUNT(*) INTO total FROM USER_CONS_COLUMNS WHERE CONSTRAINT_NAME=p_name;
    IF total<>1 THEN reject('Constraint column closure conflicts: '||p_name); END IF;
    SELECT COUNT(*) INTO total FROM USER_CONS_COLUMNS
      WHERE CONSTRAINT_NAME=p_name AND TABLE_NAME=p_table AND COLUMN_NAME=p_column;
    IF total<>1 THEN reject('Constraint column conflicts: '||p_name); END IF;
    -- GET_DDL for this exact dictionary name distinguishes MODIFY NOT NULL from ADD CHECK.
    -- NULLABLE=N and a primary key alone do not establish which constraint is inline.
    ddl := DBMS_METADATA.GET_DDL('CONSTRAINT',p_name,'LIMS_RUNTIME_LOCAL');
    IF DBMS_LOB.GETLENGTH(ddl)>8000 THEN reject('Unexpected constraint DDL size: '||p_name); END IF;
    compact := RTRIM(REGEXP_REPLACE(DBMS_LOB.SUBSTR(ddl,8000,1),'[[:space:]]',''),';');
    base := 'ALTERTABLE"LIMS_RUNTIME_LOCAL"."'||p_table||'"';
    named_clause := 'CONSTRAINT"'||p_name||'"';
    -- Oracle may omit generated names from inline DDL. Both exact forms identify
    -- this dictionary constraint because GET_DDL was requested by its exact name.
    -- RENAME need not change GENERATED; that flag never decides the role.
    IF compact=base||'MODIFY("'||p_column||'"'||named_clause||'NOTNULLENABLE)'
        OR compact=base||'MODIFY("'||p_column||'"NOTNULLENABLE)' THEN
      RETURN 'INLINE';
    ELSIF compact=base||'ADD'||named_clause||'CHECK("'||p_column||'"ISNOTNULL)ENABLE' THEN
      RETURN 'CHECK';
    END IF;
    reject('Unsupported exact constraint DDL: '||p_name);
    RETURN 'INVALID';
  END;

  FUNCTION item_state(p_table VARCHAR2,p_column VARCHAR2,p_canonical VARCHAR2,p_temporary VARCHAR2,
                      p_inline OUT VARCHAR2) RETURN NUMBER IS
    canonical_role VARCHAR2(10);
    temporary_role VARCHAR2(10);
    role VARCHAR2(10);
    inline_count NUMBER := 0;
    check_count NUMBER := 0;
    nullable_state VARCHAR2(1);
    generated_state VARCHAR2(20);
    total NUMBER;
  BEGIN
    p_inline := NULL;
    SELECT NULLABLE INTO nullable_state FROM USER_TAB_COLUMNS WHERE TABLE_NAME=p_table AND COLUMN_NAME=p_column;
    IF nullable_state<>'N' THEN reject('NOT NULL column became nullable: '||p_table||'.'||p_column); END IF;
    canonical_role := constraint_role(p_table,p_column,p_canonical);
    temporary_role := constraint_role(p_table,p_column,p_temporary);
    FOR c IN (SELECT CONSTRAINT_NAME FROM USER_CONSTRAINTS WHERE TABLE_NAME=p_table
               AND CONSTRAINT_TYPE='C' AND SEARCH_CONDITION_VC='"'||p_column||'" IS NOT NULL') LOOP
      role := constraint_role(p_table,p_column,c.CONSTRAINT_NAME);
      IF role='INLINE' THEN inline_count:=inline_count+1; p_inline:=c.CONSTRAINT_NAME;
      ELSIF role='CHECK' THEN
        check_count:=check_count+1;
        IF c.CONSTRAINT_NAME NOT IN (p_canonical,p_temporary) THEN reject('Unknown duplicate CHECK: '||c.CONSTRAINT_NAME); END IF;
      END IF;
    END LOOP;
    IF inline_count<>1 THEN reject('Expected exactly one actual inline NOT NULL: '||p_table||'.'||p_column); END IF;
    -- 0 original, 1 canonical CHECK renamed, 2 actual inline renamed, 3 complete.
    IF canonical_role='CHECK' AND temporary_role='ABSENT' AND check_count=1 THEN
      RETURN 0;
    ELSIF canonical_role='ABSENT' AND temporary_role='CHECK' AND check_count=1 THEN
      RETURN 1;
    ELSIF canonical_role='INLINE' AND p_inline=p_canonical AND temporary_role IN ('CHECK','ABSENT')
        AND check_count=CASE WHEN temporary_role='CHECK' THEN 1 ELSE 0 END THEN
      SELECT GENERATED INTO generated_state FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME=p_canonical;
      DBMS_OUTPUT.PUT_LINE('Canonical GENERATED='||generated_state||' '||p_canonical);
      IF generated_state<>'USER NAME' THEN reject('Formal GENERATED differs: '||p_canonical); END IF;
      IF temporary_role='CHECK' THEN RETURN 2; END IF;
      RETURN 3;
    END IF;
    reject('Unsupported NOT NULL alignment state: '||p_table||'.'||p_column);
    RETURN -1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    reject('Missing NOT NULL target column: '||p_table||'.'||p_column);
    RETURN -1;
  END;

  PROCEDURE check_item(p_table VARCHAR2,p_column VARCHAR2,p_canonical VARCHAR2,p_temporary VARCHAR2) IS
    state NUMBER;
    inline_name VARCHAR2(128);
  BEGIN
    state := item_state(p_table,p_column,p_canonical,p_temporary,inline_name);
    IF terminal_only AND state<>3 THEN reject('NOT NULL alignment incomplete: '||p_canonical); END IF;
    DBMS_OUTPUT.PUT_LINE('NOT NULL state='||state||' '||p_table||'.'||p_column);
  END;

  PROCEDURE assert_preflight IS
    n NUMBER;
    actual_columns VARCHAR2(4000);
  BEGIN
    IF SYS_CONTEXT('USERENV','SESSION_USER')<>'LIMS_RUNTIME_LOCAL'
        OR SYS_CONTEXT('USERENV','CURRENT_SCHEMA')<>'LIMS_RUNTIME_LOCAL'
        OR SYS_CONTEXT('USERENV','CON_NAME')<>'FREEPDB1' THEN reject('Wrong local NOT NULL target'); END IF;
"""]
    for table in structure['tables']:
        name = table['name']
        sql.append(f"    SELECT COUNT(*) INTO n FROM USER_TABLES WHERE TABLE_NAME='{name}' AND TEMPORARY='N' AND SECONDARY='N' AND NESTED='NO';\n")
        sql.append(f"    IF n<>1 THEN reject('Table conflicts: {name}'); END IF;\n")
        sql.append(f"    SELECT COUNT(*) INTO n FROM USER_TRIGGERS WHERE TABLE_NAME='{name}';\n")
        sql.append(f"    IF n<>0 THEN reject('Unexpected trigger: {name}'); END IF;\n")
        checks = [i for i in items if i['table'] == name]
        normal = [c for c in table['constraints'] if c['kind'] != 'C']
        # Prevent unknown constraints without guessing generated names for true inline NOT NULL.
        allowed_conditions = ','.join("'\"" + i['column'] + '\" IS NOT NULL\'' for i in checks)
        normal_names = ','.join("'" + c['name'] + "'" for c in normal)
        sql.append(f"    SELECT COUNT(*) INTO n FROM USER_CONSTRAINTS WHERE TABLE_NAME='{name}' AND CONSTRAINT_NAME NOT IN ({normal_names}) AND (CONSTRAINT_TYPE<>'C' OR SEARCH_CONDITION_VC IS NULL OR SEARCH_CONDITION_VC NOT IN ({allowed_conditions}));\n")
        sql.append(f"    IF n<>0 THEN reject('Unexpected constraint closure: {name}'); END IF;\n")
        for c in normal:
            indexes = [c['index_name']]
            if c['name'] in ('T_CRM_CONTRACT_UN','PK_T_LIMS_CLEARANCEFORM_ID','PK_FOLDERS_ID','T_XFP_REPORT_UNIQUE'):
                indexes.append(c['name'])
            index_names = ','.join("'"+i+"'" for i in indexes)
            sql.append(f"    SELECT COUNT(*) INTO n FROM USER_CONSTRAINTS WHERE TABLE_NAME='{name}' AND CONSTRAINT_NAME='{c['name']}' AND CONSTRAINT_TYPE='{c['kind']}' AND STATUS='{c['status']}' AND VALIDATED='{c['validated']}' AND DEFERRABLE='{c['deferrable']}' AND DEFERRED='{c['deferred']}' AND INDEX_NAME IN ({index_names}) AND INDEX_OWNER='LIMS_RUNTIME_LOCAL' AND RELY IS NULL AND BAD IS NULL AND INVALID IS NULL AND VIEW_RELATED IS NULL;\n")
            sql.append(f"    IF n<>1 THEN reject('Unrelated key state conflicts: {c['name']}'); END IF;\n")
            expected = '|'.join(str(v[0])+':'+v[1] for v in c['columns'])
            sql.append(f"    SELECT LISTAGG(POSITION||':'||COLUMN_NAME,'|') WITHIN GROUP (ORDER BY POSITION) INTO actual_columns FROM USER_CONS_COLUMNS WHERE CONSTRAINT_NAME='{c['name']}';\n")
            sql.append(f"    IF actual_columns IS NULL OR actual_columns<>'{expected}' THEN reject('Unrelated key columns conflict: {c['name']}'); END IF;\n")
    for i in items:
        table = next(t for t in structure['tables'] if t['name'] == i['table'])
        c = next(c for c in table['columns'] if c['name'] == i['column'])
        pred = [f"TABLE_NAME='{i['table']}'", f"COLUMN_NAME='{i['column']}'", f"DATA_TYPE='{c['type']}'", "NULLABLE='N'", "HIDDEN_COLUMN='NO'", "VIRTUAL_COLUMN='NO'", "IDENTITY_COLUMN='NO'"]
        for key, value in [('DATA_PRECISION',c['precision']),('DATA_SCALE',c['scale'])]:
            pred.append(key+(' IS NULL' if value is None else '='+str(value)))
        if c['type'] in ('VARCHAR2','CHAR'):
            pred.extend([f"DATA_LENGTH={c['length']}",f"CHAR_USED='{c['char_used']}'",f"CHAR_LENGTH={c['char_length']}"])
        sql.append('    SELECT COUNT(*) INTO n FROM USER_TAB_COLS WHERE '+' AND '.join(pred)+';\n')
        sql.append(f"    IF n<>1 THEN reject('NOT NULL column shape conflicts: {i['table']}.{i['column']}'); END IF;\n")
        sql.append("    check_item(" + ','.join("'"+i[k]+"'" for k in ('table','column','constraint','temporary_constraint')) + ');\n')
    sql.append("""  END;

  PROCEDURE align_item(p_table VARCHAR2,p_column VARCHAR2,p_canonical VARCHAR2,p_temporary VARCHAR2) IS
    state NUMBER;
    inline_name VARCHAR2(128);
    PROCEDURE expect_state(p_expected NUMBER) IS
    BEGIN
      state := item_state(p_table,p_column,p_canonical,p_temporary,inline_name);
      IF state<>p_expected THEN reject('NOT NULL step postcheck failed: '||p_canonical); END IF;
    END;
  BEGIN
    state := item_state(p_table,p_column,p_canonical,p_temporary,inline_name);
    IF state=0 THEN
      EXECUTE IMMEDIATE 'ALTER TABLE "'||p_table||'" RENAME CONSTRAINT "'||p_canonical||'" TO "'||p_temporary||'"';
      expect_state(1);
    END IF;
    IF state=1 THEN
      -- inline_name is selected by exact MODIFY NOT NULL DDL, never by SYS name or PK.
      EXECUTE IMMEDIATE 'ALTER TABLE "'||p_table||'" RENAME CONSTRAINT "'||inline_name||'" TO "'||p_canonical||'"';
      expect_state(2);
    END IF;
    IF state=2 THEN
      -- State 2 proves exact canonical inline NOT NULL ENABLE, USER NAME and NULLABLE=N.
      -- The sole DROP target is the ordinary CHECK with the finite temporary name.
      EXECUTE IMMEDIATE 'ALTER TABLE "'||p_table||'" DROP CONSTRAINT "'||p_temporary||'"';
      expect_state(3);
    END IF;
    IF state<>3 THEN reject('NOT NULL alignment did not finish: '||p_canonical); END IF;
  END;
BEGIN
  -- Check ALL thirteen state paths, collisions and key closure before the first DDL.
  assert_preflight;
""")
    for i in items:
        sql.append("  align_item(" + ','.join("'"+i[k]+"'" for k in ('table','column','constraint','temporary_constraint')) + ');\n')
    sql.append("""  terminal_only := TRUE;
  assert_preflight;
  DBMS_OUTPUT.PUT_LINE('NOT NULL alignment complete: 13 canonical inline constraints; backing-index stage remains separate');
END;
/
""")
    return ''.join(sql)


def verify(manifest_path, root):
    manifest = json.loads(manifest_path.read_text(encoding='utf-8'))
    if manifest.get('schema') != SCHEMA or manifest.get('stage') != 'not-null-only':
        fail('unsupported NOT NULL alignment schema or stage')
    if manifest.get('strict_evidence') is not False:
        fail('NOT NULL alignment is not strict evidence')
    if manifest.get('target_owner') != 'LIMS_RUNTIME_LOCAL' or manifest.get('target_container') != 'FREEPDB1':
        fail('NOT NULL alignment target must be LIMS_RUNTIME_LOCAL/FREEPDB1')
    if manifest.get('business_rows_written') is not False or manifest.get('production_writes') is not False:
        fail('NOT NULL alignment must not claim business or production writes')
    sources = manifest.get('sources', [])
    if [s.get('path') for s in sources] != [SOURCE_DIRECTORY + p for p in SOURCES]:
        fail('NOT NULL alignment formal source set differs')
    for source in sources:
        if digest(root / source['path']) != source.get('sha256'):
            fail('NOT NULL alignment source hash mismatch: ' + source['path'])
    structure = json.loads((root / (SOURCE_DIRECTORY + 'structure.json')).read_text(encoding='utf-8'))
    items = source_items(structure)
    if manifest.get('not_null_constraints') != items:
        fail('NOT NULL alignment differs from all thirteen formal source constraints')
    entry = manifest.get('sql', {})
    path = Path(str(entry.get('path', '')))
    if path.is_absolute() or '..' in path.parts or path.parts[:2] != ('api-fixtures', 'schema'):
        fail('NOT NULL SQL must be a portable schema fixture')
    raw = (root / path).read_bytes()
    if hashlib.sha256(raw).hexdigest() != entry.get('sha256'):
        fail('NOT NULL SQL hash mismatch')
    if raw.decode('utf-8') != render(structure):
        fail('NOT NULL SQL differs from guarded formal source rendering')
    print(json.dumps({'status': 'pass', 'stage': 'not-null-only', 'not_null_constraints': len(items), 'strict_evidence': False}))
    return 0
