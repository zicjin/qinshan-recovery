# sss2 personnel data baseline

Date: 2026-10-20 (Asia/Singapore)

Read-only Oracle metadata/count queries ran inside the existing
`lims-replay-demo-s1-oracle` container in PDB `XEPDB1`. No row, schema, or
configuration was modified, and no production endpoint was accessed.

| Check | Result |
| --- | ---: |
| `T_EMC_EMP` rows | 482 |
| `T_CORE_USER` rows | 5,353 |
| `T_CORE_ORG` rows | 1,205 |
| personnel rows with a non-null username | 482 |
| personnel rows linked to an organisation through the user/org joins | 477 |

The `T_CORE_USER.PASSWORD` column is present as `VARCHAR2(120)`; only password
length metadata was inspected, and no password or user identifier was recorded.

The existing `sss2` clone therefore has enough personnel and organisation data
for a bounded read-only query slice. No production data import is needed for
the current personnel page work. Authentication, permission-scoped SQL, and
owned service execution still require implementation and separate evidence.
