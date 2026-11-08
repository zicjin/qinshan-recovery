-- Original LES data-source table and child relationship metadata only.
-- Source: project/sql/核心发版14.1.5/init-sql/oracle/0.common_sql_oracle-1.txt:5335-5337.
-- No business rows. Use an idle, dedicated approved local runtime session.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
SET DEFINE OFF
SET SERVEROUTPUT ON

DECLARE
    ds_count PLS_INTEGER;
    ds_exact PLS_INTEGER;
    field_count PLS_INTEGER;
    field_exact PLS_INTEGER;
    hierarchy_count PLS_INTEGER;
    hierarchy_exact PLS_INTEGER;
    inserted_count PLS_INTEGER := 0;
BEGIN
    IF SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'
       OR SYS_CONTEXT('USERENV', 'SESSION_USER') NOT IN ('SYS', 'LIMS_RUNTIME_LOCAL') THEN
        RAISE_APPLICATION_ERROR(-20001, 'LES metadata requires the approved local runtime schema/session');
    END IF;
    SELECT COUNT(*), COUNT(CASE WHEN ID = 'T_CORE_LES_DS'
             AND AUDITABLE = '0' AND ATTACHMENT = '0' THEN 1 END)
      INTO ds_count, ds_exact
      FROM T_CORE_TABLE WHERE UPPER(ID) = 'T_CORE_LES_DS';
    IF NOT (ds_count = 0 OR (ds_count = 1 AND ds_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20002, 'Conflicting LES data-source table metadata');
    END IF;
    SELECT COUNT(*), COUNT(CASE WHEN ID = 'T_CORE_LES_DS_FIELD'
             AND AUDITABLE = '0' AND ATTACHMENT = '0' THEN 1 END)
      INTO field_count, field_exact
      FROM T_CORE_TABLE WHERE UPPER(ID) = 'T_CORE_LES_DS_FIELD';
    IF NOT (field_count = 0 OR (field_count = 1 AND field_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20003, 'Conflicting LES field table metadata');
    END IF;
    -- The original uses SEQ_CORE, so an equivalent relationship's actual ID
    -- is retained. A different parent, column, case or duplicate is rejected.
    SELECT COUNT(*), COUNT(CASE WHEN TABLENAME = 'T_CORE_LES_DS_FIELD'
             AND MASTERTABLENAME = 'T_CORE_LES_DS' AND REFERCOLUMN = 'LESDSID' THEN 1 END)
      INTO hierarchy_count, hierarchy_exact
      FROM T_CORE_TABLE_HIERARCHY WHERE UPPER(TABLENAME) = 'T_CORE_LES_DS_FIELD';
    IF NOT (hierarchy_count = 0 OR (hierarchy_count = 1 AND hierarchy_exact = 1)) THEN
        RAISE_APPLICATION_ERROR(-20004, 'Conflicting LES field hierarchy');
    END IF;

    SAVEPOINT les_metadata_fixture;
    BEGIN
        IF ds_count = 0 THEN
            INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)
            VALUES ('T_CORE_LES_DS', '0', '0');
            inserted_count := inserted_count + 1;
        END IF;
        IF field_count = 0 THEN
            INSERT INTO T_CORE_TABLE (ID, AUDITABLE, ATTACHMENT)
            VALUES ('T_CORE_LES_DS_FIELD', '0', '0');
            inserted_count := inserted_count + 1;
        END IF;
        IF hierarchy_count = 0 THEN
            INSERT INTO T_CORE_TABLE_HIERARCHY (ID, TABLENAME, MASTERTABLENAME, REFERCOLUMN)
            VALUES (SEQ_CORE.NEXTVAL, 'T_CORE_LES_DS_FIELD', 'T_CORE_LES_DS', 'LESDSID');
            inserted_count := inserted_count + 1;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK TO les_metadata_fixture;
            RAISE;
    END;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('LES metadata ready; inserted=' || inserted_count);
END;
/
