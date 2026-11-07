-- Final fixture cleanup only. The config parent was created and deleted through
-- its real HTTP API. The original schema has no parent/line cascade metadata.
-- Caller supplies the journaled numeric line ID; all fields are pinned.
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
DECLARE
    affected NUMBER;
BEGIN
    DELETE FROM LIMS_RUNTIME_LOCAL.T_CORE_AT_CONFIG_LINE
     WHERE ID=&1 AND CONFIGID='T_EMC_SEAL_RULE' AND AUDITTRAIL='1'
       AND NOT EXISTS (SELECT 1 FROM LIMS_RUNTIME_LOCAL.T_CORE_AT_CONFIG
                        WHERE ID='T_EMC_SEAL_RULE');
    affected := SQL%ROWCOUNT;
    IF affected != 1 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Expected one journaled fixture line');
    END IF;
    COMMIT;
END;
/
