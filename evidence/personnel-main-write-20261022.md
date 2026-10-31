# Personnel master-table isolated write slice (2026-10-22)

The owned personnel adapter now exposes the unchanged generic personnel resource write contract:

- `POST /secure/emc/module/mdm/emp/emps`
- `PUT /secure/emc/module/mdm/emp/emps/{id}`
- `DELETE /secure/emc/module/mdm/emp/emps`

Writes require the explicit `owned.personnel.write.mode=isolated` profile and a loopback client. The adapter rechecks the personnel menu grant and concrete organization grants on every request. New rows receive the actor's organization and audit identity; update and delete require every requested row to be in the actor's organization scope. SQL runs through `OwnedWriteSupport.transaction`, and audit events are emitted only after the transaction commits. Invalid bodies and cross-organization batches roll back without a success audit.

Validation used a disposable digest-pinned Oracle container on an internal single-endpoint Docker network, synthetic rows, the recovered application entrypoint, and a real HTTP client. Production was not contacted.

```text
JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391 \
  python3 scripts/tests/test-personnel-query-oracle.py \
  --http --write --main-write \
  --output .scratch/sunway-supplier-exit-api-first/archive/historical/personnel-main-write-20261022.json

personnel main write: PASS (24 checks; isolated Oracle loopback only)
personnel qualification write: PASS (12 checks; isolated Oracle loopback only)
personnel data HTTP: PASS (92 checks; packaged original application)
personnel query service: PASS (15 checks; synthetic Oracle only)
personnel filter parity: PASS (19 checks; synthetic Oracle only)
```

The JSON evidence records source commit `2d7627d2baeecb9aff7de8a8fb74296abceca30a`, `source_dirty=false`, no Oracle error codes, and successful container, anonymous-volume, and network cleanup. The regression also covers the packaged original HTTP read routes after the master-table write fixture is applied; the projection keeps the historical organization extension stable when the master table includes its own `ORGNAME` column. The default profile rejected the generic POST before any row was written. The slice does not cover personnel training, visible-test writes, attachments, Excel import/export, reports, or browser acceptance; those remain open.
