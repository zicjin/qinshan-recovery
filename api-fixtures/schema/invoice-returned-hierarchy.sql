-- Restore only the three original invoice cascade metadata records.
-- Source statements: project/sql/oracle/oracle_sql_emc.txt:17127-17137.
-- Run through the approved local runtime SQLPlus session, CURRENT_SCHEMA already
-- LIMS_RUNTIME_LOCAL. This script does not select an instance or change schema.
-- No business rows, menus, workflow definitions or sequences are written.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
SET DEFINE OFF
SET SERVEROUTPUT ON

DECLARE
    hierarchy_count PLS_INTEGER;
    hierarchy_exact PLS_INTEGER;
    invoice_count PLS_INTEGER;
    invoice_exact PLS_INTEGER;
    returned_count PLS_INTEGER;
    returned_exact PLS_INTEGER;
    inserted_count PLS_INTEGER := 0;
BEGIN
    IF SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'
       OR SYS_CONTEXT('USERENV', 'SESSION_USER') NOT IN ('SYS', 'LIMS_RUNTIME_LOCAL') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Invoice metadata fixture requires the authorized local runtime schema/session');
    END IF;

    -- Inspect ID and child name together: another ID for the same child, another
    -- child at the pinned ID, duplicate or case-variant relationships all fail.
    SELECT COUNT(*), COUNT(CASE WHEN ID = '10180501'
             AND TABLENAME = 'T_EMC_RETURNED'
             AND MASTERTABLENAME = 'T_EMC_INVOICE'
             AND REFERCOLUMN = 'INVOICEID' THEN 1 END)
      INTO hierarchy_count, hierarchy_exact
      FROM T_CORE_TABLE_HIERARCHY
     WHERE ID = '10180501' OR UPPER(TABLENAME) = 'T_EMC_RETURNED';
    IF NOT (hierarchy_count = 0 OR (hierarchy_count = 1 AND hierarchy_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20002, 'Conflicting invoice return hierarchy ID or child relationship');
    END IF;

    -- CoreTableService.selectTable reads these records directly. The child row
    -- is required before cascade traversal can dereference its physical name.
    SELECT COUNT(*), COUNT(CASE WHEN ID = 'T_EMC_INVOICE'
             AND AUDITABLE = '1' AND ATTACHMENT = '0' THEN 1 END)
      INTO invoice_count, invoice_exact
      FROM T_CORE_TABLE
     WHERE UPPER(ID) = 'T_EMC_INVOICE';
    IF NOT (invoice_count = 0 OR (invoice_count = 1 AND invoice_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20003, 'Conflicting invoice table metadata');
    END IF;

    SELECT COUNT(*), COUNT(CASE WHEN ID = 'T_EMC_RETURNED'
             AND AUDITABLE = '0' AND ATTACHMENT = '0' THEN 1 END)
      INTO returned_count, returned_exact
      FROM T_CORE_TABLE
     WHERE UPPER(ID) = 'T_EMC_RETURNED';
    IF NOT (returned_count = 0 OR (returned_count = 1 AND returned_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20004, 'Conflicting invoice return table metadata');
    END IF;

    -- All three records have passed preflight before the first write.
    SAVEPOINT invoice_metadata_fixture;
    BEGIN
        IF invoice_count = 0 THEN
            INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)
            VALUES ('T_EMC_INVOICE', '1', '0');
            inserted_count := inserted_count + 1;
        END IF;
        IF returned_count = 0 THEN
            INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)
            VALUES ('T_EMC_RETURNED', '0', '0');
            inserted_count := inserted_count + 1;
        END IF;
        IF hierarchy_count = 0 THEN
            INSERT INTO T_CORE_TABLE_HIERARCHY (ID, TABLENAME, MASTERTABLENAME, REFERCOLUMN)
            VALUES ('10180501','T_EMC_RETURNED', 'T_EMC_INVOICE', 'INVOICEID');
            inserted_count := inserted_count + 1;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK TO invoice_metadata_fixture;
            RAISE;
    END;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Invoice cascade metadata ready; inserted=' || inserted_count);
END;
/
