#!/usr/bin/env python3
"""Offline validation of four source-bound local backing-index replacements.

This command never connects to Oracle. Execute the verified SQL separately in a
clean application-principal session with all schema writers stopped and a backup.
"""
from __future__ import annotations
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = 'api-fixtures/schema/external-index-alignment-20261112'
SOURCES = ('structure.json', '30-indexes.sql', '35-constraints.sql')
MAPPINGS = (
    ('T_CRM_CONTRACT', 'T_CRM_CONTRACT_UN', 'T_CRM_CONTRACT_CONTRACTNO_IDX', 'CONTRACTNO', 'U'),
    ('T_LIMS_CLEARANCEFORM', 'PK_T_LIMS_CLEARANCEFORM_ID', 'IDX_T_LIMS_CLEARANCEFORM_ID', 'ID', 'P'),
    ('T_LIMS_FOLDERS', 'PK_FOLDERS_ID', 'IDX_FOLDERS_ID', 'ID', 'P'),
    ('T_XFP_REPORT', 'T_XFP_REPORT_UNIQUE', 'T_XFP_REPORT_PROJID_IDX', 'PROJID', 'U'),
)
COLUMN_SHAPES = (
    '1|VARCHAR2|120|-|-|Y|B|120',
    '1|VARCHAR2|32|-|-|N|B|32',
    '1|VARCHAR2|32|-|-|N|B|32',
    '2|NUMBER|22|-|-|Y|-|0',
)

SQL_HEADER = '''-- Finite local backing-index alignment; no business DML.
-- All writers must remain stopped; use a clean session and retain backup/logs.
-- Oracle DDL commits implicitly. Rerun only with these exact guarded states.
-- Any other partial failure requires metadata inspection or approved restore.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
ALTER SESSION SET DDL_LOCK_TIMEOUT=5;
DECLARE
  n NUMBER;
  old_count NUMBER;
  new_count NUMBER;
  old_visibility VARCHAR2(20);
  new_visibility VARCHAR2(20);
  constraint_status VARCHAR2(20);
  constraint_validation VARCHAR2(20);
  constraint_index VARCHAR2(128);
  constraint_index_owner VARCHAR2(128);

  PROCEDURE assert_column(p_table VARCHAR2,p_column VARCHAR2,p_shape VARCHAR2) IS
    actual_shape VARCHAR2(1000);
  BEGIN
    SELECT COLUMN_ID||'|'||DATA_TYPE||'|'||DATA_LENGTH||'|'||
      NVL(TO_CHAR(DATA_PRECISION),'-')||'|'||NVL(TO_CHAR(DATA_SCALE),'-')||'|'||
      NULLABLE||'|'||NVL(CHAR_USED,'-')||'|'||CHAR_LENGTH
      INTO actual_shape FROM USER_TAB_COLS
      WHERE TABLE_NAME=p_table AND COLUMN_NAME=p_column AND DATA_TYPE_OWNER IS NULL
        AND DEFAULT_LENGTH IS NULL AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO'
        AND USER_GENERATED='YES' AND IDENTITY_COLUMN='NO' AND DEFAULT_ON_NULL='NO';
    IF actual_shape<>p_shape THEN
      RAISE_APPLICATION_ERROR(-20270,'Column shape conflicts: '||p_table||'.'||p_column);
    END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RAISE_APPLICATION_ERROR(-20270,'Column shape missing: '||p_table||'.'||p_column);
  END;

  PROCEDURE reject(p_message VARCHAR2) IS
  BEGIN
    RAISE_APPLICATION_ERROR(-20270,p_message);
  END;

  PROCEDURE assert_index(p_table VARCHAR2,p_index VARCHAR2,p_column VARCHAR2,
                         p_uniqueness VARCHAR2) IS
  BEGIN
    SELECT COUNT(*) INTO n FROM USER_INDEXES
      WHERE INDEX_NAME=p_index AND TABLE_OWNER=USER AND TABLE_NAME=p_table
        AND INDEX_TYPE='NORMAL' AND UNIQUENESS=p_uniqueness AND STATUS='VALID'
        AND PARTITIONED='NO' AND TEMPORARY='N' AND GENERATED='N'
        AND SECONDARY='N' AND DROPPED='NO' AND COMPRESSION='DISABLED'
        AND PREFIX_LENGTH IS NULL AND FUNCIDX_STATUS IS NULL AND JOIN_INDEX='NO'
        AND VISIBILITY IN ('VISIBLE','INVISIBLE');
    IF n <> 1 THEN reject('Index definition conflicts: '||p_index); END IF;
    SELECT COUNT(*) INTO n FROM USER_IND_COLUMNS WHERE INDEX_NAME=p_index;
    IF n <> 1 THEN reject('Index column count conflicts: '||p_index); END IF;
    SELECT COUNT(*) INTO n FROM USER_IND_COLUMNS
      WHERE INDEX_NAME=p_index AND TABLE_NAME=p_table AND COLUMN_NAME=p_column
        AND COLUMN_POSITION=1 AND DESCEND='ASC';
    IF n <> 1 THEN reject('Index column conflicts: '||p_index); END IF;
    SELECT COUNT(*) INTO n FROM USER_IND_EXPRESSIONS WHERE INDEX_NAME=p_index;
    IF n <> 0 THEN reject('Index expression conflicts: '||p_index); END IF;
  END;

  PROCEDURE inspect(p_table VARCHAR2,p_constraint VARCHAR2,p_new VARCHAR2,
                    p_column VARCHAR2,p_kind VARCHAR2) IS
  BEGIN
    SELECT COUNT(*) INTO n FROM USER_TABLES
      WHERE TABLE_NAME=p_table AND TEMPORARY='N' AND NESTED='NO'
        AND SECONDARY='N' AND PARTITIONED='NO' AND IOT_TYPE IS NULL
        AND CLUSTER_NAME IS NULL AND READ_ONLY='NO';
    IF n <> 1 THEN reject('Table conflicts: '||p_table); END IF;
    SELECT COUNT(*) INTO n FROM USER_TAB_COLS
      WHERE TABLE_NAME=p_table AND COLUMN_NAME=p_column AND HIDDEN_COLUMN='NO'
        AND VIRTUAL_COLUMN='NO' AND USER_GENERATED='YES' AND IDENTITY_COLUMN='NO';
    IF n <> 1 THEN reject('Column conflicts: '||p_table||'.'||p_column); END IF;
    SELECT COUNT(*) INTO n FROM USER_CONSTRAINTS
      WHERE CONSTRAINT_NAME=p_constraint AND TABLE_NAME=p_table
        AND CONSTRAINT_TYPE=p_kind AND DEFERRABLE='NOT DEFERRABLE'
        AND DEFERRED='IMMEDIATE' AND GENERATED='USER NAME' AND RELY IS NULL
        AND BAD IS NULL AND INVALID IS NULL AND VIEW_RELATED IS NULL;
    IF n <> 1 THEN reject('Constraint definition conflicts: '||p_constraint); END IF;
    SELECT COUNT(*) INTO n FROM USER_CONS_COLUMNS WHERE CONSTRAINT_NAME=p_constraint;
    IF n <> 1 THEN reject('Constraint column count conflicts: '||p_constraint); END IF;
    SELECT COUNT(*) INTO n FROM USER_CONS_COLUMNS
      WHERE CONSTRAINT_NAME=p_constraint AND TABLE_NAME=p_table
        AND COLUMN_NAME=p_column AND POSITION=1;
    IF n <> 1 THEN reject('Constraint column conflicts: '||p_constraint); END IF;
    -- DBA view is necessary: USER/ALL views cannot prove absence across owners.
    -- Missing SELECT privilege fails here before the first migration DDL.
    SELECT COUNT(*) INTO n FROM DBA_CONSTRAINTS
      WHERE CONSTRAINT_TYPE='R' AND R_OWNER=USER AND R_CONSTRAINT_NAME=p_constraint;
    IF n <> 0 THEN reject('Incoming foreign key conflicts: '||p_constraint); END IF;
    SELECT STATUS,VALIDATED,INDEX_NAME,INDEX_OWNER
      INTO constraint_status,constraint_validation,constraint_index,constraint_index_owner
      FROM USER_CONSTRAINTS WHERE CONSTRAINT_NAME=p_constraint;
    SELECT COUNT(*) INTO old_count FROM USER_INDEXES WHERE INDEX_NAME=p_constraint;
    SELECT COUNT(*) INTO new_count FROM USER_INDEXES WHERE INDEX_NAME=p_new;
    old_visibility := NULL;
    new_visibility := NULL;
    IF old_count = 1 THEN
      assert_index(p_table,p_constraint,p_column,'UNIQUE');
      SELECT VISIBILITY INTO old_visibility FROM USER_INDEXES WHERE INDEX_NAME=p_constraint;
    END IF;
    IF new_count = 1 THEN
      assert_index(p_table,p_new,p_column,'NONUNIQUE');
      SELECT VISIBILITY INTO new_visibility FROM USER_INDEXES WHERE INDEX_NAME=p_new;
    END IF;
    -- Neither index may support a second constraint, including another owner.
    SELECT COUNT(*) INTO n FROM DBA_CONSTRAINTS
      WHERE INDEX_OWNER=USER AND INDEX_NAME IN (p_constraint,p_new)
        AND (OWNER<>USER OR CONSTRAINT_NAME<>p_constraint);
    IF n <> 0 THEN reject('Index has other constraint references: '||p_constraint); END IF;
    SELECT COUNT(*) INTO n FROM USER_INDEXES i
      WHERE i.TABLE_OWNER=USER AND i.TABLE_NAME=p_table
        AND i.INDEX_NAME NOT IN (p_constraint,p_new)
        AND (SELECT COUNT(*) FROM USER_IND_COLUMNS c WHERE c.INDEX_NAME=i.INDEX_NAME)=1
        AND EXISTS (SELECT 1 FROM USER_IND_COLUMNS c
                    WHERE c.INDEX_NAME=i.INDEX_NAME AND c.COLUMN_NAME=p_column);
    IF n <> 0 THEN reject('Unexpected equivalent index: '||p_table); END IF;
    -- Exact resumable states, each produced by one statement below.
    IF constraint_status='ENABLED' AND constraint_validation='VALIDATED'
        AND constraint_index_owner=USER AND constraint_index=p_constraint
        AND old_count=1 THEN
      IF new_count=0 AND old_visibility='VISIBLE' THEN RETURN; END IF;
      IF new_count=1 AND
          ((old_visibility='VISIBLE' AND new_visibility='INVISIBLE') OR
           (old_visibility='INVISIBLE' AND new_visibility IN ('INVISIBLE','VISIBLE'))) THEN RETURN; END IF;
    ELSIF constraint_status='DISABLED' AND constraint_validation='NOT VALIDATED'
        AND old_count=1 AND new_count=1
        AND old_visibility='INVISIBLE' AND new_visibility='VISIBLE'
        AND ((constraint_index IS NULL AND constraint_index_owner IS NULL) OR
             (constraint_index=p_constraint AND constraint_index_owner=USER)) THEN
      RETURN;
    ELSIF constraint_status='ENABLED' AND constraint_validation='VALIDATED'
        AND constraint_index_owner=USER AND constraint_index=p_new
        AND new_count=1 AND new_visibility='VISIBLE'
        AND (old_count=0 OR (old_count=1 AND old_visibility='INVISIBLE')) THEN
      RETURN;
    END IF;
    reject('Unsupported migration state: '||p_constraint);
  END;

  PROCEDURE align_one(p_table VARCHAR2,p_constraint VARCHAR2,p_new VARCHAR2,
                      p_column VARCHAR2,p_kind VARCHAR2) IS
  BEGIN
    inspect(p_table,p_constraint,p_new,p_column,p_kind);
    IF new_count=0 THEN
      EXECUTE IMMEDIATE 'CREATE INDEX "'||p_new||'" ON "'||p_table||'" ("'||p_column||'") INVISIBLE';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF old_count=1 AND old_visibility='VISIBLE' THEN
      EXECUTE IMMEDIATE 'ALTER INDEX "'||p_constraint||'" INVISIBLE';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF new_visibility='INVISIBLE' THEN
      EXECUTE IMMEDIATE 'ALTER INDEX "'||p_new||'" VISIBLE';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF constraint_status='ENABLED' AND constraint_index=p_constraint THEN
      EXECUTE IMMEDIATE 'ALTER TABLE "'||p_table||'" DISABLE NOVALIDATE CONSTRAINT "'||p_constraint||'" KEEP INDEX';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF constraint_status='DISABLED' THEN
      EXECUTE IMMEDIATE 'ALTER TABLE "'||p_table||'" ENABLE VALIDATE CONSTRAINT "'||p_constraint||'" USING INDEX "'||p_new||'"';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF old_count=1 THEN
      -- Verify validated target binding before considering the obsolete index.
      IF constraint_status<>'ENABLED' OR constraint_validation<>'VALIDATED'
          OR constraint_index<>p_new OR constraint_index_owner<>USER THEN
        reject('New constraint binding not verified: '||p_constraint);
      END IF;
      SELECT COUNT(*) INTO n FROM DBA_CONSTRAINTS
        WHERE INDEX_OWNER=USER AND INDEX_NAME=p_constraint;
      IF n <> 0 THEN reject('Old index still referenced: '||p_constraint); END IF;
      EXECUTE IMMEDIATE 'DROP INDEX "'||p_constraint||'"';
      inspect(p_table,p_constraint,p_new,p_column,p_kind);
    END IF;
    IF old_count<>0 OR new_count<>1 OR new_visibility<>'VISIBLE'
        OR constraint_status<>'ENABLED' OR constraint_validation<>'VALIDATED'
        OR constraint_index<>p_new OR constraint_index_owner<>USER THEN
      reject('Final state conflicts: '||p_constraint);
    END IF;
    DBMS_OUTPUT.PUT_LINE('Backing index aligned: '||p_constraint||' -> '||p_new);
  END;
BEGIN
  IF SYS_CONTEXT('USERENV','SESSION_USER') <> 'LIMS_RUNTIME_LOCAL'
      OR SYS_CONTEXT('USERENV','CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'
      OR SYS_CONTEXT('USERENV','CON_NAME') <> 'FREEPDB1' THEN
    reject('Wrong local alignment target');
  END IF;
  -- Finish ALL known metadata, reference and state checks before any index DDL.
'''


def render_sql():
    lines = [SQL_HEADER]
    lines.extend("  assert_column('"+row[0]+"','"+row[3]+"','"+shape+"');\n" for row,shape in zip(MAPPINGS,COLUMN_SHAPES))
    for proc in ('inspect', 'align_one'):
        lines.extend('  '+proc+'('+','.join("'"+v+"'" for v in row)+');\n' for row in MAPPINGS)
    lines.append('END;\n/\n')
    return ''.join(lines)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def verify(path):
    plan = json.loads(path.read_text())
    expected = [{'table':t,'constraint':c,'old_index':c,'target_index':i,'column':col,'kind':k} for t,c,i,col,k in MAPPINGS]
    if plan.get('schema') != 'external-index-alignment-v1' or plan.get('mappings') != expected:
        raise ValueError('finite mapping contract conflicts')
    if (plan.get('target_owner'),plan.get('target_container'),plan.get('business_rows_written'),plan.get('production_writes')) != ('LIMS_RUNTIME_LOCAL','FREEPDB1',False,False):
        raise ValueError('target or write boundary conflicts')
    expected_sources = ['deployment/oracle/external-schema/'+name for name in SOURCES]
    if [s['path'] for s in plan['sources']] != expected_sources:
        raise ValueError('formal source set conflicts')
    for source in plan['sources']:
        if digest(ROOT / source['path']) != source['sha256']:
            raise ValueError('formal source hash conflicts: '+source['path'])
    structure = json.loads((ROOT / expected_sources[0]).read_text())
    tables = {t['name']:t for t in structure['tables']}
    for table,con,idx,col,kind in MAPPINGS:
        t = tables[table]
        c = next(c for c in t['constraints'] if c['name']==con)
        i = next(i for i in t['indexes'] if i['name']==idx)
        if (c['kind'],c['status'],c['validated'],c['deferrable'],c['deferred'],c['index_name'],c['columns']) != (kind,'ENABLED','VALIDATED','NOT DEFERRABLE','IMMEDIATE',idx,[['1',col]]):
            raise ValueError('formal constraint conflicts: '+con)
        if i['uniqueness']!='NONUNIQUE' or i['columns']!=[['1',col,'ASC']]:
            raise ValueError('formal index conflicts: '+idx)
        statement = 'CREATE INDEX "'+idx+'" ON "'+table+'" ("'+col+'");'
        if statement not in (ROOT / expected_sources[1]).read_text():
            raise ValueError('formal index SQL conflicts: '+idx)
        statement = 'ALTER TABLE "'+table+'" ADD CONSTRAINT "'+con+'" '+('PRIMARY KEY' if kind=='P' else 'UNIQUE')+' ("'+col+'") USING INDEX "'+idx+'" ENABLE VALIDATE;'
        if statement not in (ROOT / expected_sources[2]).read_text():
            raise ValueError('formal constraint SQL conflicts: '+con)
    sql = plan['sql']
    # Exact generated content also rejects a rehashed mutation of a safety guard.
    if sql['path'] != BASE+'.sql' or digest(ROOT / sql['path']) != sql['sha256'] or (ROOT / sql['path']).read_text() != render_sql():
        raise ValueError('migration SQL contract conflicts')
    print(json.dumps({'schema':plan['schema'],'status':'PASS','indexes':len(MAPPINGS),'oracle_executed':False},sort_keys=True))


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--manifest',type=Path,default=ROOT/(BASE+'.json'))
    args=parser.parse_args()
    try:
        verify(args.manifest)
    except (OSError,ValueError,KeyError,TypeError,StopIteration) as exc:
        parser.exit(1,'external-index-alignment: FAIL: '+str(exc)+'\n')

if __name__=='__main__': main()
