WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
BEGIN
  RAISE_APPLICATION_ERROR(-20100, 'Combined installation is partial; resolve configuration, grants and full-system blockers first.');
END;
/
@@00-preflight.sql
@@external-schema/20-tables.sql
@@external-schema/30-indexes.sql
@@external-schema/35-constraints.sql
@@external-schema/90-verify.sql
@@core-schema/10-types.sql
@@core-schema/20-tables.sql
@@core-schema/30-indexes.sql
@@core-schema/35-constraints.sql
@@core-schema/60-sequences.sql
@@core-schema/40-routines.sql
@@core-schema/50-comments.sql
@@core-schema/70-bootstrap.sql
@@core-schema/80-bootstrap-verify.sql
@@core-schema/90-verify.sql
@@99-verify.sql
