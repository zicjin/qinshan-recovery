WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
BEGIN
  RAISE_APPLICATION_ERROR(-20100, 'External package is partial; complete dependency and installation acceptance first.');
END;
/
@@00-preflight.sql
@@20-tables.sql
@@30-indexes.sql
@@35-constraints.sql
@@90-verify.sql
