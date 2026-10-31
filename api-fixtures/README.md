# Spring API fixture boundary

API contract cases live under `lims-recovery/api-contracts/`. SQL fixtures belong
in this directory, one file per business family or execution group, and are
loaded only by the fixture controller used by a runner.

A fixture may create or restore the minimum schema state needed before or after
an HTTP case. Business rows that are the behavior under test must be created,
modified, deleted and queried through the Spring Web API. Direct SQL is limited
to fixture control, sequence/audit reads, rollback checks and final cleanup.

Fixture files must not contain credentials, production endpoints or private
license material. Reports record the fixture path and restore result, never raw
passwords or SQL output.
