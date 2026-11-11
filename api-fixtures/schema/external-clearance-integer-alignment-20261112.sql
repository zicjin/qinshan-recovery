-- Stage 0: empty-table NUMBER(*,0) alignment, before defaults stage 1.
-- Source: deployment/oracle/external-schema/20-tables.sql lines 405-406.
-- Keep application/schema writers stopped. Oracle DDL implicitly commits.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
ALTER SESSION SET DDL_LOCK_TIMEOUT=5;
DECLARE
  n NUMBER;
  rows_now NUMBER;
  ccid_missing BOOLEAN;
  version_missing BOOLEAN;
  applied NUMBER := 0;

  FUNCTION needs_scale(p_column VARCHAR2, p_position NUMBER) RETURN BOOLEAN IS
    scale_now NUMBER;
    default_text LONG;
  BEGIN
    SELECT COUNT(*) INTO n FROM USER_TAB_COLS
      WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM' AND COLUMN_NAME=p_column
        AND COLUMN_ID=p_position AND DATA_TYPE='NUMBER' AND DATA_TYPE_OWNER IS NULL
        AND DATA_LENGTH=22 AND DATA_PRECISION IS NULL AND (DATA_SCALE IS NULL OR DATA_SCALE=0)
        AND NULLABLE='Y' AND CHAR_USED IS NULL AND CHAR_LENGTH=0
        AND DEFAULT_LENGTH IS NULL AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO'
        AND IDENTITY_COLUMN='NO' AND DEFAULT_ON_NULL='NO' AND USER_GENERATED='YES'
        AND COLLATION IS NULL;
    IF n <> 1 THEN RAISE_APPLICATION_ERROR(-20240,'Integer column conflicts: '||p_column); END IF;
    SELECT DATA_SCALE,DATA_DEFAULT INTO scale_now,default_text FROM USER_TAB_COLS
      WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM' AND COLUMN_NAME=p_column;
    IF default_text IS NOT NULL THEN RAISE_APPLICATION_ERROR(-20240,'Unexpected integer default: '||p_column); END IF;
    RETURN scale_now IS NULL;
  END;

  PROCEDURE assert_empty IS
  BEGIN
    SELECT COUNT(*) INTO rows_now FROM T_LIMS_CLEARANCEFORM;
    IF rows_now <> 0 THEN RAISE_APPLICATION_ERROR(-20241,'Clearance table must remain empty'); END IF;
  END;
BEGIN
  IF SYS_CONTEXT('USERENV','SESSION_USER') <> 'LIMS_RUNTIME_LOCAL'
      OR SYS_CONTEXT('USERENV','CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'
      OR SYS_CONTEXT('USERENV','CON_NAME') <> 'FREEPDB1' THEN
    RAISE_APPLICATION_ERROR(-20242,'Wrong local integer alignment target');
  END IF;
  SELECT COUNT(*) INTO n FROM USER_TABLES WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM'
    AND TEMPORARY='N' AND NESTED='NO' AND SECONDARY='N' AND IOT_TYPE IS NULL
    AND PARTITIONED='NO' AND CLUSTER_NAME IS NULL AND COMPRESSION='DISABLED'
    AND DEFAULT_COLLATION='USING_NLS_COMP' AND READ_ONLY='NO';
  IF n <> 1 THEN RAISE_APPLICATION_ERROR(-20243,'Clearance table conflicts'); END IF;
  SELECT COUNT(*) INTO n FROM USER_TAB_COLS WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM';
  IF n <> 477 THEN RAISE_APPLICATION_ERROR(-20243,'Clearance column closure conflicts'); END IF;
  SELECT COUNT(*) INTO n FROM USER_TRIGGERS WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM';
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20243,'Unexpected clearance trigger'); END IF;
  SELECT COUNT(*) INTO n FROM USER_CONS_COLUMNS WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM' AND COLUMN_NAME IN ('CCID','VERSION');
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20243,'Unexpected integer constraint dependency'); END IF;
  SELECT COUNT(*) INTO n FROM USER_IND_COLUMNS WHERE TABLE_NAME='T_LIMS_CLEARANCEFORM' AND COLUMN_NAME IN ('CCID','VERSION');
  IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20243,'Unexpected integer index dependency'); END IF;
  -- All known target/shape/dependency/emptiness conflicts precede any DDL.
  LOCK TABLE T_LIMS_CLEARANCEFORM IN EXCLUSIVE MODE NOWAIT;
  assert_empty;
  ccid_missing := needs_scale('CCID',104);
  version_missing := needs_scale('VERSION',105);
  IF ccid_missing THEN
    assert_empty;
    IF needs_scale('CCID',104) THEN
      EXECUTE IMMEDIATE 'ALTER TABLE "T_LIMS_CLEARANCEFORM" MODIFY ("CCID" NUMBER(*,0))';
      applied := applied+1;
    END IF;
  END IF;
  -- DDL releases the preflight lock; stopped writers are required throughout.
  IF version_missing THEN
    assert_empty;
    IF needs_scale('VERSION',105) THEN
      EXECUTE IMMEDIATE 'ALTER TABLE "T_LIMS_CLEARANCEFORM" MODIFY ("VERSION" NUMBER(*,0))';
      applied := applied+1;
    END IF;
  END IF;
  assert_empty;
  IF needs_scale('CCID',104) OR needs_scale('VERSION',105) THEN
    RAISE_APPLICATION_ERROR(-20244,'Integer scale postcheck failed');
  END IF;
  -- Release the lock on the equivalent/no-DDL path; use a dedicated clean session.
  COMMIT;
  DBMS_OUTPUT.PUT_LINE('Empty clearance integer columns aligned: applied='||applied);
END;
/
