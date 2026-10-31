# Personnel qualification write slice (2026-10-21)

The original `POST /secure/emc/module/mdm/emp/emps/{id}/quals` and matching
DELETE route now have an explicit isolated mode. The filter permits them only
when `owned.personnel.write.mode=isolated` and the client is loopback; the
service rechecks the current personnel menu grant and organization scope,
writes through an Oracle transaction, allocates `SEQ_CORE`, and records an
audit event. The default configuration continues to reject writes.

Validation uses a digest-pinned disposable internal Oracle, synthetic rows,
and the original `GikamWpjcApplication`:

```text
python3 scripts/tests/test-personnel-query-oracle.py --write
personnel qualification write: PASS (12 checks; isolated Oracle loopback only)
personnel query service: PASS (15 checks; synthetic Oracle only)
personnel filter parity: PASS (19 checks; synthetic Oracle only)
```

The run verifies insert, sequence-backed ID, scoped fields, cross-organization
rejection, delete, audit output, credential omission, and cleanup. A separate
default-mode packaged HTTP run still passes 92 checks, including write denial.
This is one isolated qualification write path; personnel editing, tests,
training, and other workflows remain open.
