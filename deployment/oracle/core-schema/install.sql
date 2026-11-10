WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
BEGIN
  RAISE_APPLICATION_ERROR(-20100, 'Core schema package is partial; resolve manifest blockers before installation.');
END;
/
@@00-preflight.sql
@@10-types.sql
@@20-tables.sql
@@30-indexes.sql
@@35-constraints.sql
@@60-sequences.sql
@@40-routines.sql
@@50-comments.sql
@@90-verify.sql
